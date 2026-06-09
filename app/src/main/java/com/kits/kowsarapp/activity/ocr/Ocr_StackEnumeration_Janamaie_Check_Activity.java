package com.kits.kowsarapp.activity.ocr;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.fragment.ocr.Ocr_StackEnumeration_Janamie_Fragment;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_StackEnumeration;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_StackEnumeration_Janamaie_Check_Activity extends AppCompatActivity {

    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;


    LinearLayoutCompat ll_main;
    CallMethod callMethod;

    FragmentManager fragmentManager ;
    FragmentTransaction fragmentTransaction;
    Ocr_StackEnumeration_Janamie_Fragment stackEnumeration_janamie_fragment;
    EditText ed_barcode;

    ArrayList<Ocr_StackEnumeration> stackEnumerations= new ArrayList<>();

    String StackEnumerationCode;
    String LocationCode;
    String LocationTitle;
    Toolbar toolbar;
    String OrderBy;

    int width=1;
    Ocr_Action action;
    Handler handler;

    public String searchtarget = "";

    LottieAnimationView progressBar;
    LottieAnimationView img_lottiestatus;
    Call<RetrofitResponse> call;
    TextView tv_lottiestatus;

    Dialog dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));

        setContentView(R.layout.activity_ocr_inventory_check);

        intent();
        Config();

        try {
            dialog1 = new Dialog(this);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setContentView(R.layout.ocr_spinner_box);
            TextView repw = dialog1.findViewById(R.id.ocr_spinner_text);
            repw.setText("در حال خواندن اطلاعات");
            dialog1.show();

            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);

            handler.postDelayed(() -> {
                if (!isFinishing() && dialog1 != null && dialog1.isShowing()) {
                    dialog1.dismiss();
                }
            }, 1000);

        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }

    }

    ////////////////////////////////////////////////////

    public  void intent(){
        Bundle bundle =getIntent().getExtras();
        assert bundle != null;
        StackEnumerationCode=bundle.getString("StackEnumerationCode");
        LocationCode=bundle.getString("LocationCode");
        LocationTitle=bundle.getString("LocationTitle");
    }


    public void Config() {

        callMethod = new CallMethod(this);
        action = new Ocr_Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

        handler=new Handler();


        ll_main = findViewById(R.id.ocr_inventorycheck_a_layout);
        ed_barcode = findViewById(R.id.ocr_inventorycheck_a_barcode);
        ed_barcode.setFocusable(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;

        toolbar = findViewById(R.id.ocr_inventorycheck_a_toolbar);
        toolbar.setTitle(callMethod.NumberRegion("مکان :"+LocationTitle));
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        stackEnumeration_janamie_fragment = new Ocr_StackEnumeration_Janamie_Fragment();

        stackEnumeration_janamie_fragment.setStackEnumerationCode(StackEnumerationCode);
        stackEnumerations.clear();
    }

    public void init(){
        callMethod.Log("00");
        GetData();
    }

    public void GetData(){

        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
            OrderBy="GoodExplain2";
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
            OrderBy="FormNo";
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrMahris")){
            OrderBy="GoodExplain3";
        }else{
            OrderBy="GoodName";
        }
        if (callMethod.ReadBoolan("Orderby_ASC")){
            OrderBy=OrderBy + " ASC";
        }else{
            OrderBy=OrderBy + " DESC";
        }

//        Call<RetrofitResponse> call;
//        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//            call=apiInterface.StackEnumerationRow("StackEnumerationRow",StackEnumerationCode,searchtarget,OrderBy);
//        }else{
//            call=secendApiInterface.StackEnumerationRow("StackEnumerationRow",StackEnumerationCode,searchtarget,OrderBy);
//        }

        Call<RetrofitResponse> call;
        call=apiInterface.StackEnumerationRow("StackEnumerationRow",StackEnumerationCode,searchtarget,OrderBy);
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    callMethod.Log("11");
                    assert response.body() != null;
                    stackEnumerations=response.body().getStackEnumerations();
                    callMethod.Log(stackEnumerations.get(0).getGoodName());

                    stackEnumeration_janamie_fragment.setStackEnumerations(stackEnumerations);
                    fragmentTransaction.replace(R.id.ocr_inventorycheck_a_framelayout, stackEnumeration_janamie_fragment);
                    fragmentTransaction.commit();
                    callMethod.Log("22");
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.showToast("مشکلی در برقراری ارتباط");
                callMethod.Log("33");
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ed_barcode.requestFocus();
        ed_barcode.selectAll();
        super.onWindowFocusChanged(hasFocus);
    }

}