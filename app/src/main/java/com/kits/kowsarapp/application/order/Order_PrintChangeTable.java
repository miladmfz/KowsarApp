package com.kits.kowsarapp.application.order;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.AppPrinter;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.order.Order_BasketInfo;
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

public class Order_PrintChangeTable {

    private static final int MAX_PRINTER_RETRY = 1;

    Order_Print order_print;
    private final Context mContext;
    public Order_APIInterface apiInterface;
    public Call<RetrofitResponse> call;
    CallMethod callMethod;
    Order_DBH order_dbh;
    Integer il;
    PersianCalendar persianCalendar;
    Dialog dialog, dialogProg;
    Dialog dialogprint;
    Calendar cldr;
    int printerconter;
    ArrayList<Factor> Factor_header = new ArrayList<>();
    ArrayList<Factor> Factor_row = new ArrayList<>();
    ArrayList<AppPrinter> AppPrinters;
    int width = 500;
    LinearLayoutCompat main_layout;
    Bitmap bitmap_factor;
    Order_BasketInfo basketInfo_New;
    String bitmap_factor_base64 = "";
    String Filter_print = "";
    TextView tv_rep;

    private String activeBasketInfoCode = "";
    private boolean isPrinting = false;
    private int currentPrinterRetry = 0;
    private final ArrayList<String> failedPrinters = new ArrayList<>();
    private final Order_PrintBatchStore printBatchStore;
    private String activeTransferBatchId = "";

    // Snapshot all source-order fields before async transfer/print starts.
    private String sourceTableName = "";
    private String sourceMizType = "";
    private String sourceMizCode = "";
    private String sourcePersonName = "";
    private String sourceMobileNo = "";
    private String sourceInfoExplain = "";
    private String sourceReserveStart = "";
    private String sourceReserveEnd = "";
    private String sourceToday = "";
    private String sourceInfoState = "";

    public Order_PrintChangeTable(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.order_dbh = new Order_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.order_print = new Order_Print(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);
        this.dialogProg = new Dialog(mContext);
        this.AppPrinters = new ArrayList<>();
        this.printBatchStore = new Order_PrintBatchStore(mContext);
        printerconter = 0;

    }

    public void dialogProg() {
        dialogProg.setContentView(R.layout.broker_spinner_box);
        tv_rep = dialogProg.findViewById(R.id.b_spinner_text);
        LottieAnimationView animationView = dialogProg.findViewById(R.id.b_spinner_lottie);
        animationView.setAnimation(R.raw.receipt);
        dialogProg.show();

    }


    public void DoPrint() {
        printBatchStore.finishBatch(activeBasketInfoCode, activeTransferBatchId);
        if (basketInfo_New == null) {
            safeDismissProgress();
            isPrinting = false;
            releasePrintGuard();
            callMethod.showToast("میز مقصد مشخص نیست.");
            return;
        }

        try {
            call = apiInterface.OrderInfoInsert(
                    "OrderInfoInsert",
                    order_dbh.ReadConfig("BrokerCode"),
                    basketInfo_New.getRstmizCode(),
                    sourcePersonName,
                    sourceMobileNo,
                    sourceInfoExplain,
                    "0",
                    sourceReserveStart,
                    sourceReserveEnd,
                    sourceToday,
                    sourceInfoState,
                    activeBasketInfoCode
            );

            call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (!response.isSuccessful() || response.body() == null
                        || response.body().getBasketInfos() == null
                        || response.body().getBasketInfos().isEmpty()) {
                    safeDismissProgress();
                    isPrinting = false;
                    releasePrintGuard();
                    callMethod.showToast("پاسخ انتقال میز از سرور نامعتبر است.");
                    return;
                }

                int errorCode = Order_ValueParser.intInRangeOrDefault(
                        response.body().getBasketInfos().get(0).getErrCode(),
                        Integer.MIN_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                );
                if (errorCode != 0) {
                    safeDismissProgress();
                    isPrinting = false;
                    releasePrintGuard();
                    callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                    return;
                }

                try {
                    call = apiInterface.Order_CanPrint(
                            "Order_CanPrint",
                            activeBasketInfoCode,
                            "1"
                    );

                    call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && "Done".equals(response.body().getText())) {
                            // Destination-side ticket. Explicitly mark it as a TABLE_TRANSFER,
                            // not a "reprint" merely because InfoPrintCount > 0.
                            releasePrintGuard();
                            order_print.GetHeader_DataForBasket(
                                    activeBasketInfoCode,
                                    "MizType",
                                    Order_Print.PrintReason.TABLE_TRANSFER
                            );
                            isPrinting = false;
                        } else {
                            safeDismissProgress();
                            isPrinting = false;
                            releasePrintGuard();
                            callMethod.showToast("انتقال میز ثبت شد، اما آماده‌سازی چاپ مقصد ناموفق بود.");
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                        safeDismissProgress();
                        isPrinting = false;
                        releasePrintGuard();
                        callMethod.Log("ChangeTable Order_CanPrint(1) failed: "
                                + (t.getMessage() == null ? "" : t.getMessage()));
                        callMethod.showToast("انتقال میز ثبت شد، اما شروع چاپ مقصد ناموفق بود.");
                    }
                    });
                } catch (RuntimeException exception) {
                    safeDismissProgress();
                    isPrinting = false;
                    releasePrintGuard();
                    callMethod.Log("ChangeTable Order_CanPrint(1) enqueue failed: "
                            + safeMessage(exception));
                    callMethod.showToast("انتقال میز ثبت شد، اما شروع چاپ مقصد ناموفق بود.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                safeDismissProgress();
                isPrinting = false;
                releasePrintGuard();
                callMethod.Log("ChangeTable OrderInfoInsert failed: "
                        + (t.getMessage() == null ? "" : t.getMessage()));
                callMethod.showToast("خطا در ثبت انتقال میز.");
            }
            });
        } catch (RuntimeException exception) {
            safeDismissProgress();
            isPrinting = false;
            releasePrintGuard();
            callMethod.Log("ChangeTable OrderInfoInsert enqueue failed: " + safeMessage(exception));
            callMethod.showToast("خطا در شروع ثبت انتقال میز.");
        }
    }


    public boolean GetHeader_Data(String Filter, Order_BasketInfo basketInfo) {
        if (isPrinting) {
            callMethod.showToast("انتقال میز/چاپ در حال انجام است...");
            return false;
        }

        if (basketInfo == null) {
            callMethod.showToast("میز مقصد مشخص نیست.");
            return false;
        }

        activeBasketInfoCode = callMethod.ReadString("AppBasketInfoCode");
        if (activeBasketInfoCode == null || activeBasketInfoCode.trim().isEmpty()) {
            callMethod.showToast("کد سفارش برای انتقال میز مشخص نیست.");
            return false;
        }
        activeBasketInfoCode = activeBasketInfoCode.trim();
        if (!Order_PrintExecutionGuard.tryAcquire(activeBasketInfoCode)) {
            callMethod.showToast("عملیات چاپ یا انتقال این سفارش در حال انجام است.");
            return false;
        }

        // Freeze every value used later by asynchronous callbacks. Switching
        // table/screens must not mutate an in-flight transfer.
        sourceTableName = safeString(callMethod.ReadString("RstMizName"));
        sourceMizType = safeString(callMethod.ReadString("MizType"));
        sourceMizCode = safeString(callMethod.ReadString("RstmizCode"));
        sourcePersonName = safeString(callMethod.ReadString("PersonName"));
        sourceMobileNo = safeString(callMethod.ReadString("MobileNo"));
        sourceInfoExplain = safeString(callMethod.ReadString("InfoExplain"));
        sourceReserveStart = safeString(callMethod.ReadString("ReserveStart"));
        sourceReserveEnd = safeString(callMethod.ReadString("ReserveEnd"));
        sourceToday = safeString(callMethod.ReadString("Today"));
        sourceInfoState = safeString(callMethod.ReadString("InfoState"));

        isPrinting = true;
        Filter_print = Filter == null ? "" : Filter;
        basketInfo_New = basketInfo;
        printerconter = 0;
        currentPrinterRetry = 0;
        failedPrinters.clear();
        Factor_header.clear();
        Factor_row.clear();
        AppPrinters.clear();
        try {
            activeTransferBatchId = printBatchStore.beginBatch(activeBasketInfoCode, "TABLE_TRANSFER_SOURCE");
            dialogProg();
            tv_rep.setText(R.string.textvalue_printing);
        } catch (RuntimeException exception) {
            callMethod.Log("ChangeTable initialization failed: " + safeMessage(exception));
            safeDismissProgress();
            DoPrint();
            return true;
        }

        // If source and destination are in the same printer group/type, there is
        // no need to print a "leaving old section" ticket. Commit the transfer
        // and then print the destination ticket.
        if (Objects.equals(basketInfo.getMizType(), sourceMizType)) {
            safeDismissProgress();
            DoPrint();
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
                    callMethod.showToast("اطلاعات چاپ انتقال دریافت نشد؛ انتقال میز بدون چاپ بخش قبلی ادامه پیدا می‌کند.");
                    safeDismissProgress();
                    DoPrint();
                    return;
                }

                Factor_header = response.body().getFactors();
                GetAppPrinterList();
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                callMethod.Log("ChangeTable OrderGetFactor failed: "
                        + (t.getMessage() == null ? "" : t.getMessage()));
                callMethod.showToast("چاپ بخش قبلی انجام نشد؛ انتقال میز ادامه پیدا می‌کند.");
                safeDismissProgress();
                DoPrint();
            }
            });
        } catch (RuntimeException exception) {
            callMethod.Log("ChangeTable OrderGetFactor enqueue failed: " + safeMessage(exception));
            callMethod.showToast("چاپ بخش قبلی شروع نشد؛ انتقال میز ادامه پیدا می‌کند.");
            safeDismissProgress();
            DoPrint();
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
                    callMethod.showToast("لیست پرینترهای انتقال دریافت نشد؛ انتقال میز بدون چاپ ادامه پیدا می‌کند.");
                    safeDismissProgress();
                    DoPrint();
                    return;
                }

                printerconter = 0;
                currentPrinterRetry = 0;
                AppPrinters.clear();

                for (AppPrinter appPrinter : response.body().getAppPrinters()) {
                    if ("1".equals(appPrinter.getChangePrint())
                            && "1".equals(appPrinter.getPrinterActive())) {
                        AppPrinters.add(appPrinter);
                    }
                }

                if (!AppPrinters.isEmpty()) {
                    GetRow_Data();
                } else {
                    callMethod.showToast("انتقال میز بدون پرینت بخش قبلی");
                    safeDismissProgress();
                    DoPrint();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                // Previous implementation called GetAppPrinterList() recursively forever.
                callMethod.Log("ChangeTable printer list failed: "
                        + (t.getMessage() == null ? "" : t.getMessage()));
                callMethod.showToast("دریافت پرینترها ناموفق بود؛ انتقال میز ادامه پیدا می‌کند.");
                safeDismissProgress();
                DoPrint();
            }
            });
        } catch (RuntimeException exception) {
            callMethod.Log("ChangeTable printer list enqueue failed: " + safeMessage(exception));
            callMethod.showToast("دریافت پرینترها شروع نشد؛ انتقال میز ادامه پیدا می‌کند.");
            safeDismissProgress();
            DoPrint();
        }
    }


    public void GetRow_Data() {
        if (!isPrinting) {
            return;
        }

        if (printerconter >= AppPrinters.size()) {
            finishOldSectionTransferPrint();
            return;
        }

        final AppPrinter currentPrinter = AppPrinters.get(printerconter);

        if (Filter_print.length() > 0) {
            String whereClause = currentPrinter.getWhereClause() == null
                    ? ""
                    : currentPrinter.getWhereClause();

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
                    handleCurrentPrinterFailure("دریافت اقلام چاپ انتقال ناموفق بود.");
                    return;
                }

                Factor_row = response.body().getFactors();

                if (!Factor_row.isEmpty()) {
                    try {
                        printDialogView();
                    } catch (RuntimeException exception) {
                        callMethod.Log("ChangeTable print view creation failed: " + safeMessage(exception));
                        handleCurrentPrinterFailure("ساخت تصویر چاپ انتقال ناموفق بود.");
                    }
                } else {
                    currentPrinterRetry = 0;
                    printerconter++;
                    GetRow_Data();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                callMethod.Log("ChangeTable OrderGetFactorRow failed: "
                        + (t.getMessage() == null ? "" : t.getMessage()));
                handleCurrentPrinterFailure("دریافت اقلام چاپ انتقال ناموفق بود.");
            }
            });
        } catch (RuntimeException exception) {
            callMethod.Log("ChangeTable OrderGetFactorRow enqueue failed: " + safeMessage(exception));
            handleCurrentPrinterFailure("دریافت اقلام چاپ انتقال شروع نشد.");
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
        tv_printCount.setText("(انتقال میز)");
        tv_printCount.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_printCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 8);
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
        company_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 8);
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
        Rstmiz_tv.setText(callMethod.NumberRegion(mContext.getString(R.string.textvalue_tabletag) + Factor_header.get(0).getMizType() + "  " + Factor_header.get(0).getRstMizName()));
        Rstmiz_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        Rstmiz_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 5);
        Rstmiz_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        Rstmiz_tv.setGravity(Gravity.RIGHT);
        Rstmiz_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        Rstmiz_tv.setPadding(0, 0, 0, 15);

        TextView transferRoute_tv = new TextView(mContext);
        String destinationTable = basketInfo_New == null
                ? ""
                : basketInfo_New.getMizType() + "  " + basketInfo_New.getRstMizName();
        transferRoute_tv.setText(callMethod.NumberRegion(
                "از: " + Factor_header.get(0).getMizType() + "  " + Factor_header.get(0).getRstMizName()
                        + "\nبه: " + destinationTable));
        transferRoute_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        transferRoute_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 4);
        transferRoute_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        transferRoute_tv.setGravity(Gravity.RIGHT);
        transferRoute_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        transferRoute_tv.setPadding(0, 0, 0, 20);


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

        // This is a transfer notice, not a reprint/new food order.
        title_layout.addView(tv_printCount);
        title_layout.addView(tv_transferWarning);
        title_layout.addView(company_tv);
        title_layout.addView(transferRoute_tv);
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
            good_amount_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize() + 5);
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


        sendBitmapToCurrentTransferPrinter(bitmap_factor);


    }

    private void handleCurrentPrinterFailure(String message) {
        if (!isPrinting || printerconter >= AppPrinters.size()) {
            return;
        }

        if (currentPrinterRetry < MAX_PRINTER_RETRY) {
            currentPrinterRetry++;
            callMethod.Log("Retry transfer printer "
                    + AppPrinters.get(printerconter).getPrinterName()
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
        printBatchStore.markPrinterStatus(
                activeBasketInfoCode, activeTransferBatchId,
                AppPrinters.get(printerconter).getPrinterName(),
                Order_PrintBatchStore.STATUS_FAILED
        );

        currentPrinterRetry = 0;
        printerconter++;
        GetRow_Data();
    }

    private void finishOldSectionTransferPrint() {
        printBatchStore.finishBatch(activeBasketInfoCode, activeTransferBatchId);
        if (!failedPrinters.isEmpty()) {
            StringBuilder names = new StringBuilder();
            for (String name : failedPrinters) {
                if (names.length() > 0) {
                    names.append("، ");
                }
                names.append(name);
            }
            callMethod.showToast("هشدار: چاپ انتقال برای " + names
                    + " کامل نشد؛ انتقال میز ادامه پیدا می‌کند.");
        }

        try {
            call = apiInterface.Order_CanPrint(
                    "Order_CanPrint",
                    activeBasketInfoCode,
                    "0"
            );

            call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                safeDismissProgress();
                DoPrint();
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                // Never recurse here. The old code could loop indefinitely.
                callMethod.Log("ChangeTable Order_CanPrint(0) failed: "
                        + (t.getMessage() == null ? "" : t.getMessage()));
                safeDismissProgress();
                DoPrint();
            }
            });
        } catch (RuntimeException exception) {
            callMethod.Log("ChangeTable Order_CanPrint(0) enqueue failed: " + safeMessage(exception));
            safeDismissProgress();
            DoPrint();
        }
    }

    private void sendBitmapToCurrentTransferPrinter(Bitmap bitmap) {
        if (!isPrinting || bitmap == null || printerconter >= AppPrinters.size()) {
            handleCurrentPrinterFailure("تصویر انتقال میز قابل تولید نبود.");
            return;
        }

        final AppPrinter currentPrinter = AppPrinters.get(printerconter);
        printBatchStore.saveSnapshot(
                activeBasketInfoCode,
                activeTransferBatchId,
                "TABLE_TRANSFER_SOURCE",
                currentPrinter.getPrinterName(),
                currentPrinter.getPrinterExplain(),
                bitmap
        );
        printBatchStore.incrementAttempt(
                activeBasketInfoCode, activeTransferBatchId, currentPrinter.getPrinterName());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)) {
            handleCurrentPrinterFailure("تبدیل تصویر چاپ انتقال ناموفق بود.");
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
                            activeTransferBatchId,
                            currentPrinter.getPrinterName(),
                            Order_PrintBatchStore.STATUS_PRINTED
                    );
                    currentPrinterRetry = 0;
                    printerconter++;
                    GetRow_Data();
                } else {
                    handleCurrentPrinterFailure("ارسال چاپ انتقال به پرینتر ناموفق بود.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.Log("ChangeTable OrderSendImage failed: "
                        + (t.getMessage() == null ? "" : t.getMessage()));
                handleCurrentPrinterFailure("ارسال چاپ انتقال به پرینتر ناموفق بود.");
            }
            });
        } catch (RuntimeException exception) {
            callMethod.Log("ChangeTable OrderSendImage enqueue failed: " + safeMessage(exception));
            handleCurrentPrinterFailure("ارسال چاپ انتقال به پرینتر شروع نشد.");
        }
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private void releasePrintGuard() {
        Order_PrintExecutionGuard.release(activeBasketInfoCode);
    }

    private int titleSize() {
        return Order_ValueParser.intInRangeOrDefault(
                callMethod.ReadString("TitleSize"), 8, 72, 16);
    }

    private String safeMessage(Throwable throwable) {
        return throwable == null || throwable.getMessage() == null
                ? throwable == null ? "" : throwable.getClass().getSimpleName()
                : throwable.getMessage();
    }

    private void safeDismissProgress() {
        try {
            if (dialogProg != null && dialogProg.isShowing()) {
                dialogProg.dismiss();
            }
        } catch (RuntimeException exception) {
            callMethod.Log("Table-transfer print progress dismiss failed: "
                    + exception.getClass().getSimpleName());
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
