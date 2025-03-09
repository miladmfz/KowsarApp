package com.kits.kowsarapp.activity.ocr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_FactorDetailActivity extends AppCompatActivity {

    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;
    Ocr_DBH ocr_dbh ;
    LinearLayoutCompat main_layout;
    LinearLayoutCompat title_layout;
    LinearLayoutCompat boby_good_layout;
    LinearLayoutCompat good_layout;
    LinearLayoutCompat total_layout;
    androidx.viewpager.widget.ViewPager ViewPager, ViewPager_chap, ViewPager_rast;
    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    ArrayList<Ocr_Good> ocr_goods;
    Factor factor;
    String BarcodeScan;
    String bitmap_factor_base64="";
    Intent intent;
    Bitmap bitmap_factor;
    int width=1;
    CallMethod callMethod;
    Ocr_Action ocr_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        setContentView(R.layout.ocr_activity_factordetail);
        Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.ocr_spinner_box);
        TextView repw = dialog1.findViewById(R.id.ocr_spinner_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        }catch (Exception e){
            callMethod.Log(e.getMessage());
        }


    }
    ///**********************************************************

    public  void intent(){
        Bundle bundle =getIntent().getExtras();
        assert bundle != null;
        BarcodeScan=bundle.getString("ScanResponse");
        bitmap_factor_base64=bundle.getString("FactorImage");
    }

    public void Config() {

        callMethod = new CallMethod(this);
        ocr_dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));
        ocr_action= new Ocr_Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
        main_layout = findViewById(R.id.ocr_factordetail_a_layout);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;

    }


    public void init(){



        if(bitmap_factor_base64.equals("")){

//
//
//            Call<RetrofitResponse> call;
//
//
//            String Body_str  = "";
//
//            Body_str =callMethod.CreateJson("barcode", BarcodeScan, Body_str);
//            Body_str =callMethod.CreateJson("Step", "0", Body_str);
//            Body_str =callMethod.CreateJson("orderby", "GoodName", Body_str);
//
//
//
//            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//                call = apiInterface.GetOcrFactor(callMethod.RetrofitBody(Body_str));
//            }else{
//                call = secendApiInterface.GetOcrFactor(callMethod.RetrofitBody(Body_str));
//            }

            Call<RetrofitResponse> call;
            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call =apiInterface.GetFactor("Getocrfactor",BarcodeScan,"GoodName");
            }else {
                call =secendApiInterface.GetFactor("Getocrfactor",BarcodeScan,"GoodName");
            }


            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if(response.isSuccessful()){

                        assert response.body() !=null;
//                        factor=response.body().getFactors().get(0);
                        factor=response.body().getFactor();

                        if(factor.getFactorCode().equals("0"))
                        {

                            callMethod.showToast("لطفا مجددا اسکن کنید");
                            finish();
                        }else {

                            ocr_goods=response.body().getOcr_Goods();
                            ocr_dbh.InsertScan(factor.getAppOCRFactorCode(),BarcodeScan,factor.getFactorPrivateCode(),factor.getFactorDate(),factor.getCustName(),factor.getCustomerRef());
                            CreateView();
                        }


                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    callMethod.showToast("Connection fail ...!!!");
                }
            });

        }else {
            bitmap_factor_base64=ocr_dbh.getimagefromfactor(BarcodeScan,"FactorImage");

            ImageView imageView=new ImageView(getApplicationContext());
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

            byte[] imageByteArray1;
            imageByteArray1 = Base64.decode(bitmap_factor_base64, Base64.DEFAULT);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth(), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight(), false));
            main_layout.addView(imageView);

            Button button=  new Button(getApplicationContext());
            button.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            button.setBackgroundResource(R.color.green_700);
            button.setText("تایید و امضای رسید");
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            button.setTextColor(getColor(R.color.white));
            button.setOnClickListener(v -> {

                intent = new Intent(Ocr_FactorDetailActivity.this, Ocr_PaintActivity.class);
                intent.putExtra("ScanResponse", BarcodeScan);
                intent.putExtra("FactorImage", "hasimage");
                intent.putExtra("Width", String.valueOf(width));
                startActivity(intent);
                finish();
            });
            main_layout.addView(button);
        }

    }



    @SuppressLint("RtlHardcoded")
    public void CreateView(){

        title_layout = new LinearLayoutCompat(getApplicationContext());
        boby_good_layout = new LinearLayoutCompat(getApplicationContext());
        good_layout = new LinearLayoutCompat(getApplicationContext());
        total_layout = new LinearLayoutCompat(getApplicationContext());
        ViewPager = new ViewPager(getApplicationContext());
        ViewPager_rast = new ViewPager(getApplicationContext());
        ViewPager_chap = new ViewPager(getApplicationContext());

        title_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        title_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        title_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        TextView company_tv = new TextView(getApplicationContext());
        company_tv.setText(NumberFunctions.PerisanNumber("فاکتور فروش"));
        company_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        company_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        company_tv.setTextColor(getColor(R.color.colorPrimaryDark));
        company_tv.setGravity(Gravity.CENTER);
        company_tv.setPadding(0, 0, 0, 20);




        boby_good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        total_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));


        good_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);
        boby_good_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        total_layout.setOrientation(LinearLayoutCompat.VERTICAL);

        good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        boby_good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        total_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 3));
        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_rast.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_rast.setBackgroundResource(R.color.red_800);
        ViewPager_chap.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_chap.setBackgroundResource(R.color.green_800);




            TextView customername_tv = new TextView(getApplicationContext());
            customername_tv.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
            customername_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            customername_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            customername_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            customername_tv.setGravity(Gravity.RIGHT);
            customername_tv.setPadding(0, 0, 0, 15);

            TextView factorcode_tv = new TextView(getApplicationContext());
            factorcode_tv.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));
            factorcode_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            factorcode_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            factorcode_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            factorcode_tv.setGravity(Gravity.RIGHT);
            factorcode_tv.setPadding(0, 0, 0, 15);

            TextView factordate_tv = new TextView(getApplicationContext());
            factordate_tv.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));
            factordate_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            factordate_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            factordate_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            factordate_tv.setGravity(Gravity.RIGHT);
            factordate_tv.setPadding(0, 0, 0, 35);
            TextView address_tv = new TextView(getApplicationContext());
            address_tv.setText(NumberFunctions.PerisanNumber(" آدرس : " + factor.getAddress()));
            address_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            address_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            address_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            address_tv.setGravity(Gravity.RIGHT);
            TextView phone_tv = new TextView(getApplicationContext());
            phone_tv.setText(NumberFunctions.PerisanNumber(" تلفن تماس : " + factor.getPhone()));
            phone_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            phone_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            phone_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            phone_tv.setGravity(Gravity.RIGHT);

            title_layout.addView(company_tv);
            title_layout.addView(customername_tv);
            title_layout.addView(factorcode_tv);
            title_layout.addView(factordate_tv);
            title_layout.addView(address_tv);
            title_layout.addView(phone_tv);
            title_layout.addView(ViewPager);

            TextView total_amount_tv = new TextView(getApplicationContext());
            total_amount_tv.setText(NumberFunctions.PerisanNumber(" تعداد کل:   " + factor.getSumAmount()));
            total_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            total_amount_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            total_amount_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            total_amount_tv.setGravity(Gravity.RIGHT);
            total_amount_tv.setPadding(0, 20, 0, 10);


            TextView total_price_tv = new TextView(getApplicationContext());
            total_price_tv.setText(NumberFunctions.PerisanNumber(" قیمت کل : " + decimalFormat.format(Integer.valueOf(factor.getSumPrice())) + " ریال"));
            total_price_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            total_price_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            total_price_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            total_price_tv.setGravity(Gravity.RIGHT);


            total_layout.addView(total_amount_tv);
            total_layout.addView(total_price_tv);

        if(!factor.getNewSumPrice().equals(factor.getSumPrice())){
            TextView total_newprice_tv = new TextView(getApplicationContext());
            total_newprice_tv.setText(NumberFunctions.PerisanNumber(" قیمت کل(جدید) : " + decimalFormat.format(Integer.valueOf(factor.getNewSumPrice())) + " ریال"));
            total_newprice_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            total_newprice_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            total_newprice_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            total_newprice_tv.setGravity(Gravity.RIGHT);

            total_layout.addView(total_newprice_tv);

        }




        int CounterGood = 0;
        for (Ocr_Good g : ocr_goods) {
            CounterGood++;
            LinearLayoutCompat first_layout = new LinearLayoutCompat(getApplicationContext());
            first_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            first_layout.setOrientation(LinearLayoutCompat.VERTICAL);

            LinearLayoutCompat name_detail = new LinearLayoutCompat(getApplicationContext());
            name_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            name_detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
            name_detail.setWeightSum(6);
            name_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            TextView radif = new TextView(getApplicationContext());
            radif.setText(NumberFunctions.PerisanNumber(String.valueOf(CounterGood)));
            radif.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 5));
            radif.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            radif.setGravity(Gravity.CENTER);
            radif.setTextColor(getColor(R.color.colorPrimaryDark));
            radif.setPadding(0, 10, 0, Integer.parseInt(callMethod.ReadString("TitleSize")));

            androidx.viewpager.widget.ViewPager ViewPager_goodname = new ViewPager(getApplicationContext());
            ViewPager_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_goodname.setBackgroundResource(R.color.colorPrimaryDark);

            TextView good_name_tv = new TextView(getApplicationContext());
            good_name_tv.setText(NumberFunctions.PerisanNumber(g.getGoodName()));
            good_name_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1));
            good_name_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            good_name_tv.setGravity(Gravity.RIGHT);
            good_name_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            good_name_tv.setPadding(0, 10, 5, 0);

            name_detail.addView(radif);
            name_detail.addView(ViewPager_goodname);
            name_detail.addView(good_name_tv);

            LinearLayoutCompat detail = new LinearLayoutCompat(getApplicationContext());
            detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
            detail.setWeightSum(9);
            detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            TextView good_price_tv = new TextView(getApplicationContext());
            good_price_tv.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.valueOf(g.getPrice()))));
            good_price_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
            good_price_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            good_price_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            good_price_tv.setGravity(Gravity.CENTER);

            TextView good_amount_tv = new TextView(getApplicationContext());
            good_amount_tv.setText(NumberFunctions.PerisanNumber(String.valueOf(g.getFacAmount())));
            good_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
            good_amount_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            good_amount_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            good_amount_tv.setGravity(Gravity.CENTER);

            TextView good_totalprice_tv = new TextView(getApplicationContext());
            good_totalprice_tv.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.valueOf(g.getSumPrice()))));
            good_totalprice_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
            good_totalprice_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            good_totalprice_tv.setTextColor(getColor(R.color.colorPrimaryDark));
            good_totalprice_tv.setPadding(0, 0, 0, 10);
            good_totalprice_tv.setGravity(Gravity.CENTER);

            androidx.viewpager.widget.ViewPager ViewPager_sell1 = new ViewPager(getApplicationContext());
            ViewPager_sell1.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_sell1.setBackgroundResource(R.color.colorPrimaryDark);
            androidx.viewpager.widget.ViewPager ViewPager_sell2 = new ViewPager(getApplicationContext());
            ViewPager_sell2.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_sell2.setBackgroundResource(R.color.colorPrimaryDark);

            detail.addView(good_price_tv);
            detail.addView(ViewPager_sell1);
            detail.addView(good_amount_tv);
            detail.addView(ViewPager_sell2);
            detail.addView(good_totalprice_tv);

            androidx.viewpager.widget.ViewPager extra_ViewPager = new ViewPager(getApplicationContext());
            extra_ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
            extra_ViewPager.setBackgroundResource(R.color.colorPrimaryDark);

            androidx.viewpager.widget.ViewPager extra_ViewPager1 = new ViewPager(getApplicationContext());
            extra_ViewPager1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
            extra_ViewPager1.setBackgroundResource(R.color.colorPrimaryDark);


            first_layout.addView(name_detail);
            first_layout.addView(extra_ViewPager);
            first_layout.addView(detail);
            first_layout.addView(extra_ViewPager1);

            boby_good_layout.addView(first_layout);


        }
        good_layout.addView(ViewPager_rast);
        good_layout.addView(boby_good_layout);
        good_layout.addView(ViewPager_chap);




        main_layout.addView(title_layout);
        main_layout.addView(good_layout);
        main_layout.addView(total_layout);
        bitmap_factor=loadBitmapFromView(main_layout);




        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap_factor.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();


        bitmap_factor_base64= Base64.encodeToString(byteArray, Base64.DEFAULT);
        ocr_dbh.Insert_factorImage(BarcodeScan,bitmap_factor_base64);






        Button button=  new Button(getApplicationContext());
        button.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        button.setBackgroundResource(R.color.green_700);
        button.setText("تایید و امضای رسید");
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        button.setTextColor(getColor(R.color.white));
        button.setOnClickListener(v -> {

            intent = new Intent(Ocr_FactorDetailActivity.this, Ocr_PaintActivity.class);
            intent.putExtra("ScanResponse", BarcodeScan);
            intent.putExtra("FactorImage", "hasimage");
            intent.putExtra("Width", String.valueOf(width));
            startActivity(intent);
            finish();
        });
        main_layout.addView(button);




    }

    public Bitmap loadBitmapFromView(View v) {
        v.measure(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        Bitmap b = Bitmap.createBitmap(width, v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.layout(0, 0, width, v.getMeasuredHeight());
        v.draw(c);
        return b;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        intent = new Intent(Ocr_FactorDetailActivity.this, Ocr_FactorDetailActivity.class);
        intent.putExtra("ScanResponse", BarcodeScan);
        intent.putExtra("FactorImage", bitmap_factor_base64);
        startActivity(intent);
        finish();
    }


}