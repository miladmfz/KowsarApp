package com.kits.kowsarapp.application.order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.order.Order_TableActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.AppPrinter;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.order.Order_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_Print {

    public enum PrintReason {
        AUTO,
        NEW_ORDER,
        ADD_ORDER,
        REPRINT,
        TABLE_TRANSFER,
        ORDER_ADJUSTMENT,
        RETRY_FAILED
    }

    private static final int MAX_PRINTER_RETRY = 1;

    private final Context mContext;
    public Order_APIInterface apiInterface;
    public Call<RetrofitResponse> call;
    CallMethod callMethod;
    Order_DBH order_dbh;
    Intent intent;
    Integer il;
    PersianCalendar persianCalendar;
    Dialog dialog, dialogProg;
    Dialog dialogprint;
    Calendar cldr;
    int printerconter ;
    ArrayList<Factor> Factor_header = new ArrayList<>();
    ArrayList<Factor> Factor_row = new ArrayList<>();
    ArrayList<AppPrinter> AppPrinters ;
    int width = 500;
    LinearLayoutCompat main_layout;
    Bitmap bitmap_factor;
    String bitmap_factor_base64 = "";
    String Filter_print = "";
    TextView tv_rep;

    // Snapshot the basket id at the beginning of a print job. Never re-read it
    // from SharedPreferences inside async callbacks, otherwise switching tables
    // can redirect an in-flight print to another basket.
    private String activeBasketInfoCode = "";
    private PrintReason printReason = PrintReason.AUTO;
    private boolean isPrinting = false;
    private int currentPrinterRetry = 0;
    private final ArrayList<String> failedPrinters = new ArrayList<>();
    private final Order_PrintBatchStore printBatchStore;
    private final Order_OperationJournal operationJournal;
    private String activePrintBatchId = "";
    private String activePrintOperationId = "";
    // True only when the current job has a valid server factor header.
    // Historical reprint can run from local snapshots without a header, but in
    // that mode we must never fall through to CreateView(), which dereferences
    // Factor_header.get(0).
    private boolean headerAvailable = false;

    public Order_Print(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.order_dbh = new Order_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);
        this.dialogProg = new Dialog(mContext);
        this.AppPrinters = new ArrayList<>();
        this.printBatchStore = new Order_PrintBatchStore(mContext);
        this.operationJournal = new Order_OperationJournal(mContext);
        printerconter = 0;

    }


    public void dialogProg() {
        dialogProg.setContentView(R.layout.broker_spinner_box);
        tv_rep = dialogProg.findViewById(R.id.b_spinner_text);
        LottieAnimationView animationView = dialogProg.findViewById(R.id.b_spinner_lottie);
        animationView.setAnimation(R.raw.receipt);
        dialogProg.show();

    }

    public void GetHeader_Data(String Filter) {
        GetHeader_Data(Filter, PrintReason.AUTO);
    }

    public boolean hasFailedPrintersForBasket(String basketInfoCode) {
        return printBatchStore.hasFailedPrinters(basketInfoCode);
    }

    public boolean RetryFailedPrintersForBasket(String basketInfoCode) {
        return GetHeader_DataForBasket(basketInfoCode, "", PrintReason.RETRY_FAILED);
    }

    public void GetHeader_Data(String Filter, PrintReason reason) {
        GetHeader_DataForBasket(
                callMethod.ReadString("AppBasketInfoCode"),
                Filter,
                reason
        );
    }

    public boolean GetHeader_DataForBasket(String basketInfoCode, String Filter, PrintReason reason) {
        if (isPrinting) {
            callMethod.showToast("عملیات چاپ در حال انجام است...");
            return false;
        }

        activeBasketInfoCode = basketInfoCode == null ? "" : basketInfoCode.trim();
        if (activeBasketInfoCode.isEmpty()) {
            callMethod.showToast("کد سفارش برای چاپ مشخص نیست.");
            return false;
        }

        if (!Order_PrintExecutionGuard.tryAcquire(activeBasketInfoCode)) {
            callMethod.showToast("چاپ این سفارش هم‌اکنون در حال انجام است.");
            return false;
        }

        isPrinting = true;
        Filter_print = Filter == null ? "" : Filter;
        printReason = reason == null ? PrintReason.AUTO : reason;
        printerconter = 0;
        currentPrinterRetry = 0;
        failedPrinters.clear();
        Factor_header.clear();
        Factor_row.clear();
        AppPrinters.clear();
        activePrintBatchId = "";
        headerAvailable = false;
        beginPrintOperation();
        markPrintOperation(Order_OperationStateMachine.State.RUNNING, false);

        try {
            dialogProg();
            tv_rep.setText(R.string.textvalue_printing);
        } catch (RuntimeException exception) {
            callMethod.Log("Print initialization failed: " + safeMessage(exception));
            failPrintJob("آماده‌سازی چاپ ناموفق بود.");
            return false;
        }

        // Failed-printer retry uses the exact locally stored image and does not
        // need mutable factor data from the server.
        if (printReason == PrintReason.RETRY_FAILED) {
            ensurePrintBatchStarted();
            GetAppPrinterList();
            return true;
        }

        try {
            call = apiInterface.OrderGetFactor(
                    "OrderGetFactor",
                    activeBasketInfoCode
            );

            call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (!response.isSuccessful() || response.body() == null
                        || response.body().getFactors() == null
                        || response.body().getFactors().isEmpty()) {
                    if (printReason == PrintReason.REPRINT
                            && printBatchStore.hasSuccessfulOrderSnapshot(activeBasketInfoCode)) {
                        ensurePrintBatchStarted();
                        GetAppPrinterList();
                    } else {
                        failPrintJob("اطلاعات فاکتور برای چاپ دریافت نشد.");
                    }
                    return;
                }

                Factor_header = response.body().getFactors();
                headerAvailable = !Factor_header.isEmpty();

                if (printReason == PrintReason.AUTO) {
                    printReason = resolveAutomaticPrintReason();
                }

                ensurePrintBatchStarted();
                GetAppPrinterList();
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                callMethod.Log("OrderGetFactor failed: " + safeMessage(t));
                if (printReason == PrintReason.REPRINT
                        && printBatchStore.hasSuccessfulOrderSnapshot(activeBasketInfoCode)) {
                    // Historical reprint can still continue from the exact local snapshot.
                    ensurePrintBatchStarted();
                    GetAppPrinterList();
                } else {
                    failPrintJob("خطا در دریافت اطلاعات چاپ. دوباره تلاش کنید.");
                }
            }
            });
        } catch (RuntimeException exception) {
            callMethod.Log("OrderGetFactor enqueue failed: " + safeMessage(exception));
            failPrintJob("خطا در شروع درخواست چاپ.");
            return false;
        }
        return true;
    }


    public void GetAppPrinterList() {
        try {
            call = apiInterface.OrderGetAppPrinter("OrderGetAppPrinter");
            call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (!response.isSuccessful() || response.body() == null
                        || response.body().getAppPrinters() == null) {
                    failPrintJob("لیست پرینترها دریافت نشد.");
                    return;
                }

                printerconter = 0;
                currentPrinterRetry = 0;
                AppPrinters.clear();

                for (AppPrinter appPrinter : response.body().getAppPrinters()) {
                    if (!"1".equals(appPrinter.getPrinterActive())) {
                        continue;
                    }

                    if (printReason == PrintReason.RETRY_FAILED
                            && printBatchStore.getLatestFailedSnapshot(
                            activeBasketInfoCode, appPrinter.getPrinterName()) == null) {
                        // This printer already succeeded in the original batch; never
                        // duplicate its kitchen ticket during a failure retry.
                        continue;
                    }
                    AppPrinters.add(appPrinter);
                }

                if (!AppPrinters.isEmpty()) {
                    GetRow_Data();
                } else if (printReason == PrintReason.RETRY_FAILED) {
                    failPrintJob("پرینتر ناموفق فعالی برای تلاش مجدد پیدا نشد؛ وضعیت چاپ باز ماند.");
                } else {
                    // There is intentionally no active printer. Complete the order
                    // without print, but clear CanPrint so it does not stay pending forever.
                    callMethod.showToast("ثبت بدون پرینت");
                    Order_CanPrint_0();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                callMethod.Log("OrderGetAppPrinter failed: " + safeMessage(t));
                failPrintJob("خطا در دریافت لیست پرینترها.");
            }
            });
        } catch (RuntimeException exception) {
            callMethod.Log("OrderGetAppPrinter enqueue failed: " + safeMessage(exception));
            failPrintJob("خطا در شروع درخواست لیست پرینترها.");
        }
    }


    public void GetRow_Data() {
        if (!isPrinting) {
            return;
        }

        if (printerconter >= AppPrinters.size()) {
            finishActiveBatch();
            if (failedPrinters.isEmpty()) {
                Order_CanPrint_0();
            } else {
                finishPrintJobWithFailures();
            }
            return;
        }

        final AppPrinter currentPrinter = AppPrinters.get(printerconter);

        if (printReason == PrintReason.RETRY_FAILED) {
            Order_PrintBatchStore.SnapshotRecord failedSnapshot =
                    printBatchStore.getLatestFailedSnapshot(activeBasketInfoCode, currentPrinter.getPrinterName());
            Bitmap failedBitmap = printBatchStore.loadBitmap(failedSnapshot);
            if (failedSnapshot == null || failedBitmap == null) {
                printerconter++;
                currentPrinterRetry = 0;
                GetRow_Data();
                return;
            }
            sendBitmapToCurrentPrinter(failedBitmap, failedSnapshot);
            return;
        }

        if (printReason == PrintReason.REPRINT) {
            // If the latest real order batch failed for this printer, that failed
            // snapshot is the newest intended ticket and must win over an older
            // successful snapshot. This prevents a full reprint from accidentally
            // sending yesterday/previous-addition content to the failed printer.
            Order_PrintBatchStore.SnapshotRecord sourceSnapshot =
                    printBatchStore.getLatestFailedSnapshot(
                            activeBasketInfoCode, currentPrinter.getPrinterName());
            if (sourceSnapshot == null) {
                sourceSnapshot = printBatchStore.getLatestSuccessfulOrderSnapshot(
                        activeBasketInfoCode, currentPrinter.getPrinterName());
            }
            Bitmap sourceBitmap = printBatchStore.loadBitmap(sourceSnapshot);
            if (sourceSnapshot != null && sourceBitmap != null) {
                boolean repairingFailedSource = Order_PrintBatchStore.STATUS_FAILED.equals(sourceSnapshot.status);
                sendBitmapToCurrentPrinter(
                        buildReprintBitmap(sourceBitmap),
                        repairingFailedSource ? sourceSnapshot : null
                );
                return;
            }
            // Compatibility fallback: if this device has no historical snapshot
            // (for example the order was printed by another terminal), continue
            // with the legacy server query only when a valid server header exists.
            // Otherwise CreateView() would dereference an empty Factor_header.
            if (!headerAvailable) {
                handleCurrentPrinterFailure(
                        "اسنپ‌شات تاریخی این پرینتر و هدر فاکتور سرور در دسترس نیست."
                );
                return;
            }
            callMethod.Log("No local snapshot for reprint; legacy fallback printer="
                    + currentPrinter.getPrinterName());
        }

        if (Filter_print.length() > 0) {
            String whereClause = currentPrinter.getWhereClause() == null ? "" : currentPrinter.getWhereClause();
            if (!whereClause.contains(Filter_print)) {
                printerconter++;
                currentPrinterRetry = 0;
                GetRow_Data();
                return;
            }
        }

        try {
            call = apiInterface.OrderGetFactorRow(
                    "OrderGetFactorRow",
                    activeBasketInfoCode,
                    currentPrinter.getGoodGroups(),
                    currentPrinter.getWhereClause()
            );

            call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (!response.isSuccessful() || response.body() == null
                        || response.body().getFactors() == null) {
                    handleCurrentPrinterFailure("دریافت اقلام پرینتر ناموفق بود.");
                    return;
                }

                Factor_row = response.body().getFactors();

                if (!Factor_row.isEmpty()) {
                    try {
                        printDialogView();
                    } catch (RuntimeException exception) {
                        callMethod.Log("Print view creation failed: " + safeMessage(exception));
                        handleCurrentPrinterFailure("ساخت تصویر چاپ ناموفق بود.");
                    }
                } else {
                    currentPrinterRetry = 0;
                    printerconter++;
                    GetRow_Data();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                callMethod.Log("OrderGetFactorRow failed: " + safeMessage(t));
                handleCurrentPrinterFailure("ارتباط با پرینتر/سرور برقرار نشد.");
            }
            });
        } catch (RuntimeException exception) {
            callMethod.Log("OrderGetFactorRow enqueue failed: " + safeMessage(exception));
            handleCurrentPrinterFailure("دریافت اقلام چاپ شروع نشد.");
        }
    }


    public void Order_CanPrint_0() {
        try {
            call = apiInterface.Order_CanPrint(
                    "Order_CanPrint",
                    activeBasketInfoCode,
                    "0"
            );

            call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && "Done".equals(response.body().getText())) {
                    callMethod.showToast(mContext.getString(R.string.textvalue_recorded));
                    finishToTable();
                } else {
                    // Do not recurse into GetRow_Data here. The printers have already
                    // been processed; recursive retry caused infinite loops before.
                    failPrintJob("چاپ انجام شد ولی وضعیت چاپ روی سرور به‌روزرسانی نشد.");
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                callMethod.Log("Order_CanPrint(0) failed: " + t.getClass().getSimpleName());
                markPrintOperation(Order_OperationStateMachine.State.UNKNOWN, true);
                failPrintJob("چاپ انجام شد ولی ثبت وضعیت چاپ روی سرور ناموفق بود.");
            }
            });
        } catch (RuntimeException exception) {
            callMethod.Log("Order_CanPrint(0) enqueue failed: " + safeMessage(exception));
            failPrintJob("چاپ انجام شد ولی ثبت وضعیت چاپ شروع نشد.");
        }
    }


    @SuppressLint("RtlHardcoded")
    public void printDialogView() {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);


        dialogprint = new Dialog(mContext);
        dialogprint.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogprint.setContentView(R.layout.order_print_layout_view);
        main_layout = dialogprint.findViewById(R.id.ord_print_layout_view_ll);
        CreateView();

    }


    @SuppressLint("RtlHardcoded")
    public void CreateView() {

        main_layout.removeAllViews();

        LinearLayoutCompat title_layout = new LinearLayoutCompat(mContext);
        LinearLayoutCompat boby_good_layout = new LinearLayoutCompat(mContext);
        LinearLayoutCompat good_layout = new LinearLayoutCompat(mContext);
        LinearLayoutCompat total_layout = new LinearLayoutCompat(mContext);
        ViewPager ViewPager = new ViewPager(mContext);
        ViewPager ViewPager_rast = new ViewPager(mContext);
        ViewPager ViewPager_chap = new ViewPager(mContext);


        title_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        title_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        if (callMethod.ReadString("LANG").equals("fa")) {
            title_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            title_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            title_layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        TextView tv_printCount = new TextView(mContext);
        tv_printCount.setText(getPrintReasonTitle());
        tv_printCount.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_printCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 6);
        tv_printCount.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        tv_printCount.setGravity(Gravity.CENTER);
        tv_printCount.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tv_printCount.setPadding(0, 0, 0, 15);

        TextView tv_transferWarning = new TextView(mContext);
        tv_transferWarning.setText("*** فقط جهت انتقال میز - سفارش جدید آماده نشود ***");
        tv_transferWarning.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_transferWarning.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 3);
        tv_transferWarning.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        tv_transferWarning.setGravity(Gravity.CENTER);
        tv_transferWarning.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tv_transferWarning.setPadding(0, 0, 0, 15);


        TextView company_tv = new TextView(mContext);
        company_tv.setText(callMethod.NumberRegion(AppPrinters.get(printerconter).getPrinterExplain()));
        company_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        company_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 6);
        company_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        company_tv.setGravity(Gravity.CENTER);
        company_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        company_tv.setPadding(0, 0, 0, 15);


        boby_good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width - 8, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        total_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));


        good_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);
        boby_good_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        total_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);


        if (callMethod.ReadString("LANG").equals("fa")) {
            good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            boby_good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            total_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            boby_good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            total_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            boby_good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            total_layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 4));
        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_rast.setLayoutParams(new LinearLayoutCompat.LayoutParams(4, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_rast.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_chap.setLayoutParams(new LinearLayoutCompat.LayoutParams(4, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_chap.setBackgroundResource(R.color.colorPrimaryDark);


        TextView Rstmiz_tv = new TextView(mContext);
        Rstmiz_tv.setText(callMethod.NumberRegion(mContext.getString(R.string.textvalue_tabletag)+ Factor_header.get(0).getMizType()+"  " + Factor_header.get(0).getRstMizName()));
        Rstmiz_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        Rstmiz_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 5);
        Rstmiz_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        Rstmiz_tv.setGravity(Gravity.RIGHT);
        Rstmiz_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        Rstmiz_tv.setPadding(0, 0, 0, 15);


        cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        String thourOfDay, tminute, Time;
        thourOfDay = "0" + hour;
        tminute = "0" + minutes;
        Time = thourOfDay.substring(thourOfDay.length() - 2) + ":"
                + tminute.substring(tminute.length() - 2);


        TextView factorcode_tv = new TextView(mContext);
        factorcode_tv.setText(callMethod.NumberRegion(mContext.getString(R.string.textvalue_factorcodetag) + Factor_header.get(0).getDailyCode() + "     " + Time));
        factorcode_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        factorcode_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 5);
        factorcode_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        factorcode_tv.setGravity(Gravity.RIGHT);
        factorcode_tv.setPadding(0, 0, 0, 15);
        factorcode_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


        TextView factordate_tv = new TextView(mContext);
        if ("2".equals(Factor_header.get(0).getInfoState())) {
            factordate_tv.setText(callMethod.NumberRegion(mContext.getString(R.string.textvalue_factortimetag) + Factor_header.get(0).getTimeStart() + "_" + Factor_header.get(0).getAppBasketInfoDate()));
        } else if ("4".equals(Factor_header.get(0).getInfoState())) {
            factordate_tv.setText(callMethod.NumberRegion(mContext.getString(R.string.textvalue_factortimetag) + Factor_header.get(0).getReserveStart() + "_" + Factor_header.get(0).getAppBasketInfoDate()));
        }

        factordate_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        factordate_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 5);
        factordate_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        factordate_tv.setGravity(Gravity.RIGHT);
        factordate_tv.setPadding(0, 0, 0, 35);
        factordate_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


        TextView explain_tv = new TextView(mContext);
        explain_tv.setText(callMethod.NumberRegion(Factor_header.get(0).getInfoExplain()));
        explain_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        explain_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 8);
        explain_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        explain_tv.setGravity(Gravity.RIGHT);
        explain_tv.setPadding(0, 0, 0, 35);
        explain_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        // Print reason is explicit. InfoPrintCount alone is not equal to "reprint":
        // it can also mean an additional order or a table transfer.
        title_layout.addView(tv_printCount);
        if (printReason == PrintReason.TABLE_TRANSFER) {
            title_layout.addView(tv_transferWarning);
        }
        title_layout.addView(company_tv);
        title_layout.addView(factordate_tv);
        title_layout.addView(factorcode_tv);
        if (!safeString(Factor_header.get(0).getInfoExplain()).isEmpty()) {
            title_layout.addView(explain_tv);
        }
        title_layout.addView(Rstmiz_tv);
        title_layout.addView(ViewPager);


        for (Factor FactorRow_detail : Factor_row) {

            LinearLayoutCompat first_layout = new LinearLayoutCompat(mContext);
            first_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            first_layout.setOrientation(LinearLayoutCompat.VERTICAL);

            LinearLayoutCompat name_detail = new LinearLayoutCompat(mContext);
            name_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            name_detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
            name_detail.setWeightSum(6);

            if (callMethod.ReadString("LANG").equals("fa")) {
                name_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else if (callMethod.ReadString("LANG").equals("ar")) {
                name_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                name_detail.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

            TextView good_amount_tv = new TextView(mContext);
            good_amount_tv.setText(FactorRow_detail.getFacAmount());
            good_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 5));
            good_amount_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 4);
            good_amount_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            good_amount_tv.setGravity(Gravity.CENTER);
            good_amount_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


            androidx.viewpager.widget.ViewPager ViewPager_goodname = new ViewPager(mContext);
            ViewPager_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(1, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_goodname.setBackgroundResource(R.color.colorPrimaryDark);

            TextView good_name_tv = new TextView(mContext);
            String goodname;
            if (FactorRow_detail.getIsExtra().equals("1")) {
                goodname = FactorRow_detail.getGoodName() + mContext.getString(R.string.textvalue_orderagaintag);
            } else {
                goodname = FactorRow_detail.getGoodName();
            }

            good_name_tv.setText(callMethod.NumberRegion(goodname));
            good_name_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1));
            good_name_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 6);
            good_name_tv.setGravity(Gravity.RIGHT);
            good_name_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            good_name_tv.setPadding(0, 10, 5, 0);
            good_name_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            LinearLayoutCompat detail = new LinearLayoutCompat(mContext);
            detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
            detail.setWeightSum(9);

            if (callMethod.ReadString("LANG").equals("fa")) {
                detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else if (callMethod.ReadString("LANG").equals("ar")) {
                detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                detail.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            TextView good_RowExplain_tv = new TextView(mContext);
            good_RowExplain_tv.setText(callMethod.NumberRegion(FactorRow_detail.getRowExplain()));
            good_RowExplain_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            good_RowExplain_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize());
            good_RowExplain_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            good_RowExplain_tv.setPadding(0, 0, 0, 10);
            good_RowExplain_tv.setGravity(Gravity.CENTER);
            good_RowExplain_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            if (!safeString(FactorRow_detail.getRowExplain()).isEmpty()) {
                good_name_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 6);
                good_RowExplain_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 3);
            }

            androidx.viewpager.widget.ViewPager ViewPager_sell2 = new ViewPager(mContext);
            ViewPager_sell2.setLayoutParams(new LinearLayoutCompat.LayoutParams(1, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_sell2.setBackgroundResource(R.color.colorPrimaryDark);

            androidx.viewpager.widget.ViewPager extra_ViewPager = new ViewPager(mContext);
            extra_ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
            extra_ViewPager.setBackgroundResource(R.color.colorPrimaryDark);

            androidx.viewpager.widget.ViewPager extra_ViewPager1 = new ViewPager(mContext);
            extra_ViewPager1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 4));
            extra_ViewPager1.setBackgroundResource(R.color.colorPrimaryDark);


            name_detail.addView(good_name_tv);
            name_detail.addView(ViewPager_goodname);
            name_detail.addView(good_amount_tv);


            detail.addView(good_RowExplain_tv);


            first_layout.addView(name_detail);
            first_layout.addView(extra_ViewPager);
            first_layout.addView(detail);
            first_layout.addView(extra_ViewPager1);

            boby_good_layout.addView(first_layout);


        }

        good_layout.addView(boby_good_layout);

        total_layout.addView(ViewPager_rast);
        total_layout.addView(good_layout);
        total_layout.addView(ViewPager_chap);


        main_layout.addView(title_layout);
        main_layout.addView(total_layout);
        bitmap_factor = loadBitmapFromView(main_layout);


        sendBitmapToCurrentPrinter(bitmap_factor, null);


    }

    private PrintReason resolveAutomaticPrintReason() {
        try {
            if (!Factor_header.isEmpty()
                    && Order_ValueParser.longOrDefault(
                    Factor_header.get(0).getInfoPrintCount(), 0) > 0) {
                return PrintReason.ADD_ORDER;
            }
        } catch (RuntimeException exception) {
            callMethod.Log("Print reason resolution failed: "
                    + exception.getClass().getSimpleName());
        }
        return PrintReason.NEW_ORDER;
    }

    private String getPrintReasonTitle() {
        switch (printReason) {
            case REPRINT:
                return "(چاپ مجدد)";
            case ADD_ORDER:
                return "(سفارش اضافه)";
            case TABLE_TRANSFER:
                return "(انتقال میز)";
            case ORDER_ADJUSTMENT:
                return "(اصلاح سفارش)";
            case RETRY_FAILED:
                return "(تلاش مجدد چاپ ناموفق)";
            case NEW_ORDER:
                return "(سفارش جدید)";
            case AUTO:
            default:
                return "(سفارش)";
        }
    }

    private void handleCurrentPrinterFailure(String message) {
        if (!isPrinting || printerconter >= AppPrinters.size()) {
            return;
        }

        if (currentPrinterRetry < MAX_PRINTER_RETRY) {
            currentPrinterRetry++;
            callMethod.Log("Retry printer " + AppPrinters.get(printerconter).getPrinterName()
                    + " attempt=" + currentPrinterRetry);
            GetRow_Data();
            return;
        }

        String printerName = AppPrinters.get(printerconter).getPrinterExplain();
        if (printerName == null || printerName.trim().isEmpty()) {
            printerName = AppPrinters.get(printerconter).getPrinterName();
        }
        failedPrinters.add(printerName);
        callMethod.Log(message + " printer=" + printerName);
        ensurePrintBatchStarted();
        printBatchStore.markPrinterStatus(
                activeBasketInfoCode, activePrintBatchId,
                AppPrinters.get(printerconter).getPrinterName(),
                Order_PrintBatchStore.STATUS_FAILED
        );

        currentPrinterRetry = 0;
        printerconter++;
        GetRow_Data();
    }

    private void finishPrintJobWithFailures() {
        StringBuilder names = new StringBuilder();
        for (String name : failedPrinters) {
            if (names.length() > 0) {
                names.append("، ");
            }
            names.append(name);
        }

        finishActiveBatch();
        markPrintOperation(Order_OperationStateMachine.State.FAILED, true);
        safeDismissProgress();
        isPrinting = false;

        // Keep CanPrint=1 intentionally. At least one destination did not receive
        // the ticket, so the order must remain visible as needing print/reprint.
        callMethod.showToast("چاپ کامل نشد: " + names + ". وضعیت سفارش برای چاپ مجدد باز ماند.");
        finishToTable();
    }

    private void failPrintJob(String message) {
        callMethod.Log(message);
        finishActiveBatch();
        markPrintOperation(Order_OperationStateMachine.State.FAILED, true);
        safeDismissProgress();
        isPrinting = false;
        releasePrintGuard();
        callMethod.showToast(message);
    }

    private void safeDismissProgress() {
        try {
            if (dialogProg != null && dialogProg.isShowing()) {
                dialogProg.dismiss();
            }
        } catch (RuntimeException exception) {
            callMethod.Log("Print progress dismiss failed: "
                    + exception.getClass().getSimpleName());
        }
    }

    private void finishToTable() {
        markPrintOperation(Order_OperationStateMachine.State.SUCCEEDED, true);
        safeDismissProgress();
        isPrinting = false;
        releasePrintGuard();

        intent = new Intent(mContext, Order_TableActivity.class);
        intent.putExtra("State", "0");
        intent.putExtra("EditTable", "0");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);

        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

    private void releasePrintGuard() {
        Order_PrintExecutionGuard.release(activeBasketInfoCode);
    }

    private int titleSize() {
        return Order_ValueParser.intInRangeOrDefault(
                callMethod.ReadString("TitleSize"), 8, 72, 16);
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private String safeMessage(Throwable throwable) {
        return throwable == null ? "Unknown" : throwable.getClass().getSimpleName();
    }


    private void beginPrintOperation() {
        markPrintOperation(Order_OperationStateMachine.State.UNKNOWN, true);
        try {
            activePrintOperationId = operationJournal.begin(
                    Order_OperationJournal.Type.PRINT,
                    activeBasketInfoCode
            );
        } catch (RuntimeException exception) {
            activePrintOperationId = "";
            callMethod.Log("Print audit begin unavailable: "
                    + exception.getClass().getSimpleName());
        }
    }

    private void markPrintOperation(
            Order_OperationStateMachine.State state,
            boolean clearAfterTransition
    ) {
        if (activePrintOperationId == null || activePrintOperationId.isEmpty()) return;
        try {
            Order_OperationStateMachine.TransitionResult transition =
                    operationJournal.transition(activePrintOperationId, state);
            if (transition == Order_OperationStateMachine.TransitionResult.INVALID) {
                callMethod.Log("Print audit transition rejected state=" + state.name());
            }
        } catch (RuntimeException exception) {
            callMethod.Log("Print audit transition unavailable: "
                    + exception.getClass().getSimpleName());
        } finally {
            if (clearAfterTransition) activePrintOperationId = "";
        }
    }

    private void ensurePrintBatchStarted() {
        if (activePrintBatchId != null && !activePrintBatchId.isEmpty()) {
            printBatchStore.setBatchReason(activeBasketInfoCode, activePrintBatchId, printReason.name());
            return;
        }
        activePrintBatchId = printBatchStore.beginBatch(activeBasketInfoCode, printReason.name());
        callMethod.Log("PrintBatch started reason=" + printReason.name());
    }

    private void finishActiveBatch() {
        if (activePrintBatchId == null || activePrintBatchId.isEmpty()) return;
        printBatchStore.finishBatch(activeBasketInfoCode, activePrintBatchId);
    }

    private Bitmap buildReprintBitmap(Bitmap source) {
        if (source == null) return null;
        try {
            LinearLayoutCompat wrapper = new LinearLayoutCompat(mContext);
            wrapper.setOrientation(LinearLayoutCompat.VERTICAL);
            wrapper.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

            TextView title = new TextView(mContext);
            title.setText("*** چاپ مجدد همان برگه قبلی ***");
            title.setGravity(Gravity.CENTER);
            title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            title.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    titleSize() + 5);
            title.setPadding(0, 10, 0, 12);

            android.widget.ImageView image = new android.widget.ImageView(mContext);
            image.setAdjustViewBounds(true);
            image.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
            image.setImageBitmap(source);
            image.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

            wrapper.addView(title);
            wrapper.addView(image);
            return loadBitmapFromView(wrapper);
        } catch (Exception e) {
            callMethod.Log("buildReprintBitmap fallback: " + safeMessage(e));
            return source;
        }
    }

    private void sendBitmapToCurrentPrinter(
            Bitmap bitmap,
            final Order_PrintBatchStore.SnapshotRecord retrySource
    ) {
        if (!isPrinting || bitmap == null || printerconter >= AppPrinters.size()) {
            handleCurrentPrinterFailure("تصویر چاپ قابل تولید نبود.");
            return;
        }

        ensurePrintBatchStarted();
        final AppPrinter currentPrinter = AppPrinters.get(printerconter);

        Order_PrintBatchStore.SnapshotRecord currentSnapshot = printBatchStore.saveSnapshot(
                activeBasketInfoCode,
                activePrintBatchId,
                printReason.name(),
                currentPrinter.getPrinterName(),
                currentPrinter.getPrinterExplain(),
                bitmap
        );
        printBatchStore.incrementAttempt(activeBasketInfoCode, activePrintBatchId, currentPrinter.getPrinterName());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)) {
            handleCurrentPrinterFailure("تبدیل تصویر چاپ ناموفق بود.");
            return;
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        bitmap_factor_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        try {
            Call<RetrofitResponse> sendCall = apiInterface.OrderSendImage(
                    "OrderSendImage",
                    bitmap_factor_base64,
                    activeBasketInfoCode,
                    currentPrinter.getPrinterName(),
                    currentPrinter.getPrintCount()
            );

            sendCall.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && "Done".equals(response.body().getText())) {
                    printBatchStore.markPrinterStatus(
                            activeBasketInfoCode,
                            activePrintBatchId,
                            currentPrinter.getPrinterName(),
                            Order_PrintBatchStore.STATUS_PRINTED
                    );
                    if (retrySource != null) {
                        // The old failed job is now actually delivered. Mark it so a
                        // future retry does not send the same kitchen ticket again.
                        printBatchStore.markSourceSnapshotPrinted(retrySource);
                    }
                    currentPrinterRetry = 0;
                    printerconter++;
                    GetRow_Data();
                } else {
                    handleCurrentPrinterFailure("ارسال چاپ به پرینتر ناموفق بود.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.Log("OrderSendImage failed: " + t.getClass().getSimpleName());
                markPrintOperation(Order_OperationStateMachine.State.UNKNOWN, false);
                handleCurrentPrinterFailure("ارسال چاپ به پرینتر ناموفق بود.");
            }
            });
        } catch (RuntimeException exception) {
            callMethod.Log("OrderSendImage enqueue failed: " + safeMessage(exception));
            handleCurrentPrinterFailure("ارسال تصویر چاپ شروع نشد.");
        }
    }

    public Bitmap loadBitmapFromView(View v) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(widthSpec, heightSpec);
        int measuredHeight = Math.max(1, v.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(width, measuredHeight, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.layout(0, 0, width, measuredHeight);
        v.draw(c);
        return b;
    }


}
