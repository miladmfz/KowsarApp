package com.kits.kowsarapp.activity.ocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_MergingActivity extends AppCompatActivity {

    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;

    ArrayList<Ocr_Good> ocr_goods= new ArrayList<>();

    Integer row_counter;

    LinearLayoutCompat ll_main;
    CallMethod callMethod;

    String OrderBy;
    int width=1;
    Ocr_Action ocr_action;
    Handler handler;

    Integer state_category;
    public String searchtarget = "";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_activity_merging);




        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        setContentView(R.layout.ocr_activity_merging);

        Dialog dialog1 = new Dialog(this);

        try {

            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setContentView(R.layout.ocr_spinner_box);
            TextView repw = dialog1.findViewById(R.id.ocr_spinner_text);
            repw.setText("در حال خواندن اطلاعات");
            dialog1.show();
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }


        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }


    }


    public void Config() {

        callMethod = new CallMethod(this);

        ocr_action = new Ocr_Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

        handler=new Handler();
        ll_main = findViewById(R.id.ocr_merg_f_layout);
        callMethod.EditString("FactorDbName",callMethod.ReadString("DbName"));



        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;


    }



    public void init(){

        try {
            state_category=Integer.parseInt(callMethod.ReadString("Category"));
        }catch (Exception e){
            state_category=0;
        }


        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

            OrderBy = "Order By GoodExplain1";
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")) {
            OrderBy = "Order By FormNo";
        } else {
            OrderBy = "Order By GoodExplain1";
        }

        Call<RetrofitResponse> call=apiInterface.OcrShortageList("OcrShortageList",OrderBy,"0");

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    ocr_goods = response.body().getOcr_Goods();
                    row_counter= 0;
                    for (Ocr_Good g : ocr_goods) {
                        goodshow(g);

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.Log(t.getMessage());
                callMethod.showToast("Connection fail ...!!!");
            }
        });




    }

    @SuppressLint("RtlHardcoded")
    public void goodshow(Ocr_Good good_detial){
        row_counter++;

        LinearLayoutCompat ll_factor_row = new LinearLayoutCompat(getApplicationContext());
        LinearLayoutCompat ll_details = new LinearLayoutCompat(getApplicationContext());
        LinearLayoutCompat ll_name_price = new LinearLayoutCompat(getApplicationContext());
        ViewPager vp_radif_name = new ViewPager(getApplicationContext());
        ViewPager vp_rows = new ViewPager(getApplicationContext());
        ViewPager vp_name_amount = new ViewPager(getApplicationContext());
        ViewPager vp_amount_price = new ViewPager(getApplicationContext());
        TextView tv_goodname = new TextView(getApplicationContext());
        TextView tv_amount = new TextView(getApplicationContext());
        TextView tv_price = new TextView(getApplicationContext());


        ll_factor_row.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_details.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_name_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 1.3));
        vp_rows.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
        vp_radif_name.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_name_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_amount_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float)1.5));
        tv_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float)4));
        tv_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float)3.5));


        ll_details.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_name_price.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ll_factor_row.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_details.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_name_price.setOrientation(LinearLayoutCompat.HORIZONTAL);

        ll_details.setWeightSum(9);
        ll_name_price.setWeightSum(9);

        vp_name_amount.setBackgroundResource(R.color.colorPrimaryDark);
        vp_amount_price.setBackgroundResource(R.color.colorPrimaryDark);
        vp_rows.setBackgroundResource(R.color.colorPrimaryDark);
        vp_radif_name.setBackgroundResource(R.color.colorPrimaryDark);

        tv_goodname.setGravity(Gravity.RIGHT);
        tv_amount.setGravity(Gravity.CENTER);
        tv_price.setGravity(Gravity.CENTER);


        tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize"))+3);
        tv_amount.setTypeface(null, Typeface.BOLD);

        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));



        tv_goodname.setText(NumberFunctions.PerisanNumber(good_detial.getGoodName()));

        tv_amount.setText(NumberFunctions.PerisanNumber(good_detial.getFacAmount()));

        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {


            tv_price.setText(NumberFunctions.PerisanNumber(good_detial.getGoodMaxSellPrice()));


        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){


            tv_price.setText(NumberFunctions.PerisanNumber(good_detial.getFormNo()));
        }else{

            tv_price.setText(NumberFunctions.PerisanNumber(good_detial.getGoodMaxSellPrice()));

        }

        tv_goodname.setTextColor(getColor(R.color.colorPrimaryDark));
        tv_amount.setTextColor(getColor(R.color.colorPrimaryDark));
        tv_price.setTextColor(getColor(R.color.colorPrimaryDark));

        tv_price.setPadding(0, 10, 0, 10);
        tv_goodname.setPadding(0, 10, 5, 10);


        if(good_detial.getShortageAmount()==null){
            callMethod.Log("ShortageAmount is null");
        }else {
            if(Integer.parseInt(good_detial.getShortageAmount())>0) {
                tv_amount.setText(NumberFunctions.PerisanNumber(good_detial.getShortageAmount() + ""));
                tv_amount.setTextColor(getColor(R.color.red_800));
            }

        }



        ll_name_price.addView(tv_goodname);
        ll_name_price.addView(vp_name_amount);
        ll_name_price.addView(tv_amount);
        ll_name_price.addView(vp_amount_price);
        ll_name_price.addView(tv_price);

        ll_details.addView(vp_radif_name);
        ll_details.addView(ll_name_price);


        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
            if (row_counter%2==0){
                ll_details.setBackgroundColor(getColor(R.color.grey_200));
            }
            try {
                if (good_detial.getMinAmount().equals("1.000")){
                    ll_details.setBackgroundColor(getColor(R.color.red_100));
                }
            }catch (Exception e){
                callMethod.Log(e.getMessage());

            }


        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
            if (row_counter%2==0){
                ll_details.setBackgroundColor(getColor(R.color.grey_200));
            }
            callMethod.Log("Gostaresh");

        }else{
            if (row_counter%2==0){
                ll_details.setBackgroundColor(getColor(R.color.grey_200));
            }
            callMethod.Log("defult");
        }


        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(vp_rows);


        ll_main.addView(ll_factor_row);

        tv_goodname.setOnClickListener(v -> image_zome_view(good_detial));



    }

    public void image_zome_view(Ocr_Good singleGood) {
        ocr_action.good_detail(singleGood,"");
    }

}