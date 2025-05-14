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

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.model.base.AppPrinter;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_Print {

    ArrayList<Ocr_Good> goods = new ArrayList<>();

    private final Context mContext;
    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;
    CallMethod callMethod;
    Ocr_DBH ocr_dbh;
    Integer il;
    Integer packCounter;
    String packs = "0";
    PersianCalendar persianCalendar;
    Dialog dialog, dialogProg;
    Dialog dialogprint;
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

    String hideamount = "0";


    public Ocr_Print(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.ocr_dbh = new Ocr_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        this.secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);
        this.dialogProg = new Dialog(mContext);
        this.AppPrinters = new ArrayList<>();
        this.imageInfo = new ImageInfo(mContext);
        printerconter = 0;

    }
    public void Printing(Factor factor , ArrayList<Ocr_Good> mgoods, String packCount, String hide) {
        factorData=factor;
        goods=mgoods;
        packs=packCount;
        hideamount=hide;
        GetAppPrinterList();
    }
    public void dialogProg() {
        dialogProg.setContentView(R.layout.broker_spinner_box);
        tv_rep = dialogProg.findViewById(R.id.b_spinner_text);
        LottieAnimationView animationView = dialogProg.findViewById(R.id.b_spinner_lottie);
        animationView.setAnimation(R.raw.receipt);
        dialogProg.show();

    }

    public void GetAppPrinterList() {

        dialogProg();

        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.OrderGetAppPrinter("OrderGetAppPrinter");
        }else{
            call=secendApiInterface.OrderGetAppPrinter("OrderGetAppPrinter");
        }



        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    printerconter = 0;
                    AppPrinters = response.body().getAppPrinters();

                    if (callMethod.ReadString("Category").equals("2")) {
                        for (AppPrinter appPrinter : AppPrinters) {
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
                callMethod.Log(t.getMessage());
                dialogProg.dismiss();
                ((Activity) mContext).finish();

            }
        });
    }



    @SuppressLint("RtlHardcoded")
    public void printDialogView() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);


        dialogprint = new Dialog(mContext);
        Objects.requireNonNull(dialogprint.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialogprint.setContentView(R.layout.ocr_print_layout_view);
        main_layout = dialogprint.findViewById(R.id.ocr_print_layout_view_ll);
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

        LinearLayoutCompat good_layout = new LinearLayoutCompat(mContext);
        good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width - 8, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        good_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);
        good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        good_layout.setGravity(Gravity.CENTER);

        LinearLayoutCompat boby_good_layout = new LinearLayoutCompat(mContext);
        boby_good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width - 8, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        boby_good_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        boby_good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        boby_good_layout.setGravity(Gravity.CENTER);




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

        TextView FactorDate = new TextView(mContext);
        FactorDate.setText(NumberFunctions.PerisanNumber(" تاریخ :   " +factorData.getFactorDate()));
        FactorDate.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        FactorDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) );
        FactorDate.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        FactorDate.setGravity(Gravity.RIGHT);
        FactorDate.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        FactorDate.setPadding(0, 0, 0, 15);

        TextView FactorDate1 = new TextView(mContext);
        FactorDate1.setText(NumberFunctions.PerisanNumber(" کد مسیر :   "+factorData.getErsall()));
        FactorDate1.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        FactorDate1.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) );
        FactorDate1.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        FactorDate1.setGravity(Gravity.RIGHT);
        FactorDate1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        FactorDate1.setPadding(0, 0, 0, 15);




        TextView TotalRow = new TextView(mContext);
        TotalRow.setText(NumberFunctions.PerisanNumber(" تعداد جلد :   " +factorData.getSumAmount()));
        TotalRow.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        TotalRow.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) );
        TotalRow.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        TotalRow.setGravity(Gravity.RIGHT);
        TotalRow.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        TotalRow.setPadding(0, 0, 0, 15);


        TextView Stack = new TextView(mContext);
        Stack.setText(NumberFunctions.PerisanNumber("انبار: " +callMethod.ReadString("StackCategory")+" - "+callMethod.ReadString("Deliverer")));
        Stack.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        Stack.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        Stack.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        Stack.setGravity(Gravity.RIGHT);
        Stack.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        Stack.setPadding(0, 0, 0, 30);



        LinearLayoutCompat Header_GoodList = new LinearLayoutCompat(App.getContext());
        Header_GoodList.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        Header_GoodList.setOrientation(LinearLayoutCompat.HORIZONTAL);
        Header_GoodList.setWeightSum(9);
        Header_GoodList.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView Header_ghafase = new TextView(App.getContext());
        Header_ghafase.setText(NumberFunctions.PerisanNumber("قفسه"));
        Header_ghafase.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
        Header_ghafase.setTextSize(14);
        Header_ghafase.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        Header_ghafase.setGravity(Gravity.CENTER);

        TextView Header_good_amount_tv = new TextView(App.getContext());
        Header_good_amount_tv.setText(NumberFunctions.PerisanNumber("تعداد کل"));
        Header_good_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
        Header_good_amount_tv.setTextSize(14);
        Header_good_amount_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        Header_good_amount_tv.setGravity(Gravity.CENTER);


        TextView Header_ShortageAmount = new TextView(App.getContext());
        Header_ShortageAmount.setText(NumberFunctions.PerisanNumber("کسری"));
        Header_ShortageAmount.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
        Header_ShortageAmount.setTextSize(14);
        Header_ShortageAmount.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        Header_ShortageAmount.setPadding(0, 0, 0, 10);
        Header_ShortageAmount.setGravity(Gravity.CENTER);








        int GoodList_Counter = 0;
        for (Ocr_Good gooddetail : goods) {

            if(Integer.parseInt(gooddetail.getShortageAmount())>0 && gooddetail.getAppRowIsControled().equals("0")){

                GoodList_Counter++;

                LinearLayoutCompat first_layout = new LinearLayoutCompat(App.getContext());
                first_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT-100, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                first_layout.setOrientation(LinearLayoutCompat.VERTICAL);
                LinearLayoutCompat name_detail = new LinearLayoutCompat(App.getContext());
                name_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(400, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                name_detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
                name_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


//                TextView radif = new TextView(App.getContext());
//                radif.setText(NumberFunctions.PerisanNumber(String.valueOf(j)));
//                radif.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 5));
//                radif.setTextSize(10);
//                radif.setGravity(Gravity.CENTER);
//                radif.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
//                radif.setPadding(0, 10, 0, 10);
//
//                ViewPager ViewPager_goodname = new ViewPager(App.getContext());
//                ViewPager_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
//                ViewPager_goodname.setBackgroundResource(R.color.colorPrimary);

                TextView good_name_tv = new TextView(App.getContext());
                good_name_tv.setText(NumberFunctions.PerisanNumber(gooddetail.getGoodName()));
                good_name_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(380, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
                good_name_tv.setTextSize(10);
                good_name_tv.setGravity(Gravity.RIGHT);
                good_name_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
                good_name_tv.setPadding(0, 10, 0, 0);


                name_detail.addView(good_name_tv);

                LinearLayoutCompat detail = new LinearLayoutCompat(App.getContext());
                detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
                detail.setWeightSum(9);
                detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

//                TextView ghafase = new TextView(App.getContext());
//                ghafase.setText(NumberFunctions.PerisanNumber(gooddetail.getFormNo()));
//                ghafase.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
//                ghafase.setTextSize(14);
//                ghafase.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
//                ghafase.setGravity(Gravity.CENTER);

                TextView good_amount_tv = new TextView(App.getContext());
                good_amount_tv.setText(NumberFunctions.PerisanNumber(gooddetail.getFacAmount()));
                good_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
                good_amount_tv.setTextSize(14);
                good_amount_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
                good_amount_tv.setGravity(Gravity.CENTER);


                TextView ShortageAmount = new TextView(App.getContext());
                ShortageAmount.setText(NumberFunctions.PerisanNumber(gooddetail.getShortageAmount()+""));
                ShortageAmount.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
                ShortageAmount.setTextSize(14);
                ShortageAmount.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
                ShortageAmount.setPadding(0, 0, 0, 10);
                ShortageAmount.setGravity(Gravity.CENTER);

//                ViewPager ViewPager_sell1 = new ViewPager(App.getContext());
//                ViewPager_sell1.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
//                ViewPager_sell1.setBackgroundResource(R.color.colorPrimaryDark);
//                ViewPager ViewPager_sell2 = new ViewPager(App.getContext());
//                ViewPager_sell2.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
//                ViewPager_sell2.setBackgroundResource(R.color.colorPrimaryDark);

                detail.removeAllViews();
//                detail.addView(ghafase);
                detail.addView(good_amount_tv);
                detail.addView(ShortageAmount);

                ViewPager extra_ViewPager = new ViewPager(App.getContext());
                extra_ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
                extra_ViewPager.setBackgroundResource(R.color.colorPrimaryDark);

                ViewPager extra_ViewPager1 = new ViewPager(App.getContext());
                extra_ViewPager1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
                extra_ViewPager1.setBackgroundResource(R.color.colorPrimaryDark);


                first_layout.addView(name_detail);
                first_layout.addView(extra_ViewPager);
                first_layout.addView(detail);
                first_layout.addView(extra_ViewPager1);

                boby_good_layout.addView(first_layout);
            }
        }




        Tag_layout.addView(CustName);
        Tag_layout.addView(FactorPrivateCode);
        Tag_layout.addView(FactorDate1);
        Tag_layout.addView(FactorDate);
        if(hideamount.equals("0")){
            Tag_layout.addView(TotalRow);
        }

        Tag_layout.addView(Stack);


        if (GoodList_Counter>0){
            Header_GoodList.addView(Header_ghafase);
            Header_GoodList.addView(Header_good_amount_tv);
            Header_GoodList.addView(Header_ShortageAmount);

            Tag_layout.addView(Header_GoodList);

            good_layout.addView(ViewPager_rast);
            good_layout.addView(boby_good_layout);
            good_layout.addView(ViewPager_chap);

            Tag_layout.addView(good_layout);
        }




        Body_Tag_layout.addView(Tag_layout);


        main_layout.addView(img_explain);


        //        if (callMethod.ReadBoolan("PrintBarcode")){
//            // Barcode view ro tolid mikonim
//            ImageView barcodeImageView = new ImageView(App.getContext());
//            barcodeImageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(
//                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
//                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
//            barcodeImageView.setPadding(0, 10, 0, 30);
//
//            try {
//                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//                Bitmap barcodeBitmap = barcodeEncoder.encodeBitmap("12547516876", BarcodeFormat.CODE_128, width, 100); // 12547516876 ro be barcode tabdil mikone
//                barcodeImageView.setImageBitmap(barcodeBitmap);
//            } catch (WriterException e) {
//                e.printStackTrace();
//            }
//
//
//            main_layout.addView(barcodeImageView);  // Barcode ImageView
//        }



        main_layout.addView(Body_Tag_layout);

        bitmap_factor = loadBitmapFromView(main_layout);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap_factor.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        bitmap_factor_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.OcrSendImage("OrderSendImage",
                    bitmap_factor_base64,
                    factorData.getFactorPrivateCode(),
                    targetprinter.getPrinterName(),
                    targetprinter.getPrintCount()

            );
        }else{
            call=secendApiInterface.OcrSendImage("OrderSendImage",
                    bitmap_factor_base64,
                    factorData.getFactorPrivateCode(),
                    targetprinter.getPrinterName(),
                    targetprinter.getPrintCount()

            );
        }






        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                assert response.body() != null;

                if (response.body().getText().equals("Done")) {
                    dialogProg.dismiss();
                    //((Activity) mContext).finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.Log(t.getMessage());

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
        FactorPrivateCode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " +factorData.getFactorPrivateCode()));
        FactorPrivateCode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        FactorPrivateCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        FactorPrivateCode.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        FactorPrivateCode.setGravity(Gravity.CENTER);
        FactorPrivateCode.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        FactorPrivateCode.setPadding(0, 0, 0, 15);


        TextView FactorDate = new TextView(mContext);

        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
            FactorDate.setText(NumberFunctions.PerisanNumber(" تاریخ :   " +factorData.getFactorDate() +"       "+factorData.getErsall()));
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
            FactorDate.setText(NumberFunctions.PerisanNumber(" تاریخ :   " +factorData.getFactorDate() +"       "+factorData.getMandehBedehkar()));
        }else{
            FactorDate.setText(NumberFunctions.PerisanNumber(" تاریخ :   " +factorData.getFactorDate()));
        }


        FactorDate.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        FactorDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) );
        FactorDate.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        FactorDate.setGravity(Gravity.RIGHT);
        FactorDate.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        FactorDate.setPadding(0, 0, 0, 15);

        TextView TotalRow = new TextView(mContext);
        TotalRow.setText(NumberFunctions.PerisanNumber(" تعداد اقلام :   " +factorData.getSumAmount()));
        TotalRow.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        TotalRow.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) );
        TotalRow.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        TotalRow.setGravity(Gravity.RIGHT);
        TotalRow.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        TotalRow.setPadding(0, 0, 0, 15);

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
        Tag_layout.addView(FactorDate);
        Tag_layout.addView(TotalRow);
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
            call=apiInterface.OcrSendImage("OrderSendImage",
                    bitmap_factor_base64,
                    factorData.getFactorPrivateCode(),
                    targetprinter.getPrinterName(),
                    targetprinter.getPrintCount()

            );
        }else{
            call=secendApiInterface.OcrSendImage("OrderSendImage",
                    bitmap_factor_base64,
                    factorData.getFactorPrivateCode(),
                    targetprinter.getPrinterName(),
                    targetprinter.getPrintCount()

            );
        }
        call.enqueue(new Callback<RetrofitResponse>() {
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
