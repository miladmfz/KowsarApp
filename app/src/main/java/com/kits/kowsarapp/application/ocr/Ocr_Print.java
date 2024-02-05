package com.kits.kowsarapp.application.ocr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.model.AppPrinter;
import com.kits.kowsarapp.model.Factor;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.NumberFunctions;
import com.kits.kowsarapp.model.RetrofitResponse;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_Print {


    private final Context mContext;
    Ocr_APIInterface ocr_apiInterface;
    Ocr_APIInterface ocr_secend_apiInterface;
    CallMethod callMethod;
    Ocr_DBH dbh;
    Integer il;
    Integer packCounter;
    String packs = "0";
    PersianCalendar persianCalendar;
    Dialog dialog, dialogProg;
    Dialog dialogprint;
    Calendar cldr;
    int printerconter ;
    Factor factorData;
    ArrayList<AppPrinter> AppPrinters ;
    AppPrinter targetprinter;
    int width = 500;
    LinearLayoutCompat main_layout;
    Bitmap bitmap_factor;
    String bitmap_factor_base64 = "";
    TextView tv_rep;
    ImageInfo imageInfo;

    public Ocr_Print(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Ocr_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.ocr_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        this.ocr_secend_apiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);
        this.dialogProg = new Dialog(mContext);
        this.AppPrinters = new ArrayList<>();
        this.imageInfo = new ImageInfo(mContext);
        printerconter = 0;

    }

    public void dialogProg() {

        dialogProg.setContentView(R.layout.ocr_spinner_box);
        tv_rep = dialogProg.findViewById(R.id.o_spinner_text);
        dialogProg.show();

    }

    public void Printing(Factor factor ,String packCount) {
        factorData=factor;
        packs=packCount;
        GetAppPrinterList();
    }

    public void GetAppPrinterList() {
        Log.e("test","0");
        dialogProg();

        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=ocr_apiInterface.OrderGetAppPrinter("OrderGetAppPrinter");
        }else{
            call=ocr_secend_apiInterface.OrderGetAppPrinter("OrderGetAppPrinter");
        }



        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    printerconter = 0;
                    AppPrinters = response.body().getAppPrinters();

                    if (callMethod.ReadString("Category").equals("2")) {
                        for (AppPrinter appPrinter : AppPrinters) {
                            Log.e("test_name", appPrinter.getPrinterName());
                            if (appPrinter.getWhereClause().equals(callMethod.ReadString("StackCategory"))) {
                                printerconter++;
                                targetprinter = appPrinter;
                                printDialogView();
                            }

                        }
                    } else if (callMethod.ReadString("Category").equals("3")) {
                        for (AppPrinter appPrinter : AppPrinters) {
                            if (appPrinter.getWhereClause().equals("")) {
                                printerconter++;
                                targetprinter = appPrinter;
                                printDialogView();
                            }

                        }
                    }

                    if (printerconter == 0) {
                        dialogProg.dismiss();
                        ((Activity) mContext).finish();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                Log.e("test", "3");
                dialogProg.dismiss();
                ((Activity) mContext).finish();

            }
        });
        Log.e("test","4");
    }

    @SuppressLint("RtlHardcoded")
    public void printDialogView() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);


        dialogprint = new Dialog(mContext);
        dialogprint.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogprint.setContentView(R.layout.print_layout_view);
        main_layout = dialogprint.findViewById(R.id.print_layout_view_ll);
        main_layout.setGravity(Gravity.CENTER);

        if (callMethod.ReadString("Category").equals("2")){

            CreateViewConfirm();
        }else if (callMethod.ReadString("Category").equals("3")){
            packCounter=1;
            CreateViewPack();
        }
    }


    public void CreateViewConfirm() {


        LinearLayoutCompat Tag_layout = new LinearLayoutCompat(mContext);
        Tag_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width - 8, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        Tag_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        Tag_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        Tag_layout.setGravity(Gravity.CENTER);

        LinearLayoutCompat Body_Tag_layout = new LinearLayoutCompat(mContext);
        Body_Tag_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width - 8, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        Body_Tag_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);
        Body_Tag_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        ViewPager ViewPagertop = new ViewPager(mContext);
        ViewPager ViewPagerbot = new ViewPager(mContext);
        ViewPager ViewPager_rast = new ViewPager(mContext);
        ViewPager ViewPager_chap = new ViewPager(mContext);



        ViewPagertop.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 4));
        ViewPagertop.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPagerbot.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 4));
        ViewPagerbot.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_rast.setLayoutParams(new LinearLayoutCompat.LayoutParams(4, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_rast.setBackgroundResource(R.color.colorPrimaryDark);

        ViewPager_chap.setLayoutParams(new LinearLayoutCompat.LayoutParams(4, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_chap.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_rast.setPadding(50, 0, 0, 0);



        ImageView img_explain = new ImageView(mContext);
        img_explain.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));
        img_explain.setPadding(0, 0, 0, 100);
        img_explain.setImageBitmap(imageInfo.LoadLogo());




        TextView CustName = new TextView(mContext);
        CustName.setText(NumberFunctions.PerisanNumber(factorData.getCustName()));
        CustName.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        CustName.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) );
        CustName.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        CustName.setGravity(Gravity.CENTER);
        CustName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        CustName.setPadding(0, 0, 0, 15);

        TextView FactorPrivateCode = new TextView(mContext);
        FactorPrivateCode.setText(NumberFunctions.PerisanNumber(factorData.getFactorPrivateCode()));
        FactorPrivateCode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        FactorPrivateCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        FactorPrivateCode.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        FactorPrivateCode.setGravity(Gravity.CENTER);
        FactorPrivateCode.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        FactorPrivateCode.setPadding(0, 0, 0, 15);

        TextView Deliverer = new TextView(mContext);
        Deliverer.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Deliverer")));
        Deliverer.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        Deliverer.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) );
        Deliverer.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        Deliverer.setGravity(Gravity.CENTER);
        Deliverer.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        Deliverer.setPadding(0, 0, 0, 15);

        TextView Stack = new TextView(mContext);
        Stack.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("StackCategory")));
        Stack.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        Stack.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        Stack.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        Stack.setGravity(Gravity.CENTER);
        Stack.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        Stack.setPadding(0, 0, 0, 30);


        Tag_layout.addView(CustName);
        Tag_layout.addView(FactorPrivateCode);
        Tag_layout.addView(Deliverer);
        Tag_layout.addView(Stack);

        Body_Tag_layout.addView(Tag_layout);


        main_layout.addView(img_explain);
        main_layout.addView(Body_Tag_layout);

        bitmap_factor = loadBitmapFromView(main_layout);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap_factor.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        bitmap_factor_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=ocr_apiInterface.OcrSendImage("OrderSendImage",
                    bitmap_factor_base64,
                    factorData.getFactorPrivateCode(),
                    targetprinter.getPrinterName(),
                    targetprinter.getPrintCount()

            );
        }else{
            call=ocr_secend_apiInterface.OcrSendImage("OrderSendImage",
                    bitmap_factor_base64,
                    factorData.getFactorPrivateCode(),
                    targetprinter.getPrinterName(),
                    targetprinter.getPrintCount()

            );
        }






        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                assert response.body() != null;

                if (response.body().getText().equals("Done")) {
                    dialogProg.dismiss();
                    ((Activity) mContext).finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Log.e("test_Confirm", t.getMessage());
                dialogProg.dismiss();
                ((Activity) mContext).finish();
            }
        });


    }

    public void CreateViewPack() {

        LinearLayoutCompat Gap_layout = new LinearLayoutCompat(mContext);
        Gap_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 400));
        Gap_layout.setOrientation(LinearLayoutCompat.VERTICAL);



        LinearLayoutCompat Tag_layout = new LinearLayoutCompat(mContext);
        Tag_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width - 8, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        Tag_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        Tag_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        Tag_layout.setGravity(Gravity.CENTER);

        LinearLayoutCompat Body_Tag_layout = new LinearLayoutCompat(mContext);
        Body_Tag_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width - 8, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        Body_Tag_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);
        Body_Tag_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);




        ViewPager ViewPagertop = new ViewPager(mContext);
        ViewPager ViewPagerbot = new ViewPager(mContext);
        ViewPager ViewPager_rast = new ViewPager(mContext);
        ViewPager ViewPager_chap = new ViewPager(mContext);



        ViewPagertop.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 4));
        ViewPagertop.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPagerbot.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 4));
        ViewPagerbot.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_rast.setLayoutParams(new LinearLayoutCompat.LayoutParams(4, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_rast.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_chap.setLayoutParams(new LinearLayoutCompat.LayoutParams(4, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_chap.setBackgroundResource(R.color.colorPrimaryDark);



        ImageView img_explain = new ImageView(mContext);
        img_explain.setLayoutParams(new LinearLayoutCompat.LayoutParams(300, 300));
        img_explain.setPadding(0, 0, 0, 20);
        img_explain.setImageBitmap(imageInfo.LoadLogo());

        TextView CustName = new TextView(mContext);
        CustName.setText(NumberFunctions.PerisanNumber(factorData.getCustName()));
        CustName.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        CustName.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        CustName.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        CustName.setGravity(Gravity.CENTER);
        CustName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        CustName.setPadding(0, 0, 0, 15);

        TextView FactorPrivateCode = new TextView(mContext);
        FactorPrivateCode.setText(NumberFunctions.PerisanNumber(factorData.getFactorPrivateCode()));
        FactorPrivateCode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        FactorPrivateCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        FactorPrivateCode.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        FactorPrivateCode.setGravity(Gravity.CENTER);
        FactorPrivateCode.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        FactorPrivateCode.setPadding(0, 0, 0, 15);

        TextView tv_Count = new TextView(mContext);
        tv_Count.setText(NumberFunctions.PerisanNumber("تعداد "+packCounter+" از "+packs));
        tv_Count.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_Count.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        tv_Count.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        tv_Count.setGravity(Gravity.CENTER);
        tv_Count.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tv_Count.setPadding(0, 0, 0, 30);




        Tag_layout.addView(img_explain);
        Tag_layout.addView(CustName);
        Tag_layout.addView(FactorPrivateCode);
        Tag_layout.addView(tv_Count);


        Body_Tag_layout.addView(Tag_layout);


        main_layout.addView(Body_Tag_layout);

        bitmap_factor = loadBitmapFromView(main_layout);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap_factor.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        bitmap_factor_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);


        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=ocr_apiInterface.OcrSendImage("OrderSendImage",
                    bitmap_factor_base64,
                    factorData.getFactorPrivateCode(),
                    targetprinter.getPrinterName(),
                    targetprinter.getPrintCount()

            );
        }else{
            call=ocr_secend_apiInterface.OcrSendImage("OrderSendImage",
                    bitmap_factor_base64,
                    factorData.getFactorPrivateCode(),
                    targetprinter.getPrinterName(),
                    targetprinter.getPrintCount()

            );
        }
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                assert response.body() != null;
                if (response.body().getText().equals("Done")) {
                    packCounter++;
                    if (Integer.parseInt(packs) < packCounter) {
                        ((Activity) mContext).finish();
                    } else {
                        main_layout.removeAllViews();
                        CreateViewPack();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                dialogProg.dismiss();
                ((Activity) mContext).finish();
            }
        });


    }

    public Bitmap loadBitmapFromView(View v) {
        v.measure(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        Bitmap b = Bitmap.createBitmap(width, v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.layout(0, 0, width, v.getMeasuredHeight());
        v.draw(c);
        return b;
    }


}
