package com.kits.kowsarapp.activity.ocr;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.adapter.base.Base_ThemeSpinnerAdapter;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.fragment.ocr.Ocr_CollectFragment;
import com.kits.kowsarapp.fragment.ocr.Ocr_PackFragment;
import com.kits.kowsarapp.fragment.ocr.Ocr_StackFragment;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class Ocr_ConfirmActivity extends AppCompatActivity {
    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;

    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    ArrayList<Ocr_Good> ocr_goods= new ArrayList<>();
    ArrayList<Ocr_Good> ocr_goods_scan=new ArrayList<>();


    LinearLayoutCompat ll_main;
    CallMethod callMethod;
    FragmentManager fragmentManager ;
    FragmentTransaction fragmentTransaction;
    Ocr_CollectFragment collectFragment;
    Ocr_PackFragment packFragment;
    Ocr_StackFragment stackFragment;

    EditText ed_barcode;

    Factor factor;
    String BarcodeScan;
    String OrderBy;
    String State;
    int width=1;
    Ocr_Action action;
    Handler handler;

    Integer state_category;
    public String searchtarget = "";


    LottieAnimationView progressBar;
    LottieAnimationView img_lottiestatus;
    Call<RetrofitResponse> call;
    TextView tv_lottiestatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        setContentView(R.layout.ocr_activity_confirm);

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
    ////////////////////////////////////////////////////


    public  void intent(){
        Bundle bundle =getIntent().getExtras();
        assert bundle != null;
        BarcodeScan=bundle.getString("ScanResponse");
        State=bundle.getString("State");

    }


    public void Config() {

        callMethod = new CallMethod(this);

        action = new Ocr_Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

        handler=new Handler();
        for (final String[] ignored : arraygood_shortage) {
            arraygood_shortage.add(new String[]{"goodcode","amount "});
        }

        ll_main = findViewById(R.id.ocr_confirm_a_layout);
        ed_barcode = findViewById(R.id.ocr_confirm_a_barcode);
        progressBar = findViewById(R.id.ocr_confirm_a_good_prog);
        img_lottiestatus = findViewById(R.id.ocr_confirm_a_good_lottie);
        tv_lottiestatus = findViewById(R.id.ocr_confirm_a_good_tvstatus);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        collectFragment = new Ocr_CollectFragment();
        packFragment = new Ocr_PackFragment();
        stackFragment = new Ocr_StackFragment();

        collectFragment.setBarcodeScan(BarcodeScan);
        packFragment.setBarcodeScan(BarcodeScan);
        stackFragment.setBarcodeScan(BarcodeScan);

        ocr_goods_scan.clear();
    }



    public void init(){

        try {
            state_category=Integer.parseInt(callMethod.ReadString("Category"));
        }catch (Exception e){
            state_category=0;
        }

        if(state_category==2){
            Collect_Pack();
        }else if(state_category==3){
            Collect_Pack();
        }else if(state_category==6){
            StackLocation();
        }

    }

    public void Collect_Pack(){


        ed_barcode.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }



                    @Override
                    public void afterTextChanged( Editable editable) {
                        //String barcode1 = editable.toString().substring(2).replace("\n", "");
                        if (ocr_goods.size() > 0) {

                            if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
                                ocr_goods_scan.clear();
                                handler.removeCallbacksAndMessages(null);
                                handler.postDelayed(() -> {
                                    String barcode = NumberFunctions.EnglishNumber(editable.toString().substring(2, editable.toString().length() - 2).replace("\n", ""));

                                    ed_barcode.selectAll();

                                    for (Ocr_Good singlegood : ocr_goods) {
                                        if (singlegood.getCachedBarCode().indexOf(barcode) > 0) {
                                            ocr_goods_scan.add(singlegood);
                                        }

                                    }

                                    action.GoodScanDetail(ocr_goods_scan, State, BarcodeScan);
                                }, Integer.parseInt(callMethod.ReadString("Delay")));
                            }
                        }else{
                            callMethod.showToast("لطفا ابتدا آغاز فرایند انبار را شروع کنید");
                        }
                    }

                }
        );





        if(State.equals("0")){

            if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                    callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

                OrderBy="GoodExplain1";

            } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("Ocr Gostaresh")){

                OrderBy="FormNo";

            }else{

                OrderBy="GoodExplain1";
            }


        }else{
            if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                    callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

                OrderBy="GoodName";

            } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("Ocr Gostaresh")){

                OrderBy="FormNo Desc";

            }else{

                OrderBy="GoodName";
            }

        }


        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetFactor("Getocrfactor",BarcodeScan,OrderBy);
        }else{
            call=secendApiInterface.GetFactor("Getocrfactor",BarcodeScan,OrderBy);
        }

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    factor = response.body().getFactor();
                    if (factor.getFactorCode().equals("0")) {
                        callMethod.showToast("لطفا مجددا اسکن کنید");
                        finish();
                    } else {
                        ocr_goods = response.body().getOcr_Goods();
                        if (factor.getAppIsControled().equals("0")) {
                            collectFragment.setFactor(factor);
                            collectFragment.setocr_Goods(ocr_goods);
                            collectFragment.setState(State);
                            collectFragment.setTcPrintRef(BarcodeScan);
                            fragmentTransaction.replace(R.id.ocr_confirm_a_framelayout, collectFragment);
                            fragmentTransaction.commit();
                        } else if (factor.getAppIsPacked().equals("0")) {
                            packFragment.setFactor(factor);
                            packFragment.setocr_Goods(ocr_goods);
                            fragmentTransaction.replace(R.id.ocr_confirm_a_framelayout, packFragment);
                            fragmentTransaction.commit();
                        } else {
                            finish();
                        }

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.showToast("Connection fail ...!!!");
            }
        });

        ed_barcode.setFocusable(true);
        ed_barcode.requestFocus();
    }


    public void StackLocation(){

        tv_lottiestatus.setText("اسکن کنید");
        tv_lottiestatus.setVisibility(View.VISIBLE);
        if (BarcodeScan.length()>0){

            progressBar.setVisibility(View.VISIBLE);
            tv_lottiestatus.setText("در حال جستجو");
            tv_lottiestatus.setVisibility(View.VISIBLE);
            ed_barcode.setText(BarcodeScan);
            ed_barcode.selectAll();
            Search_call();
        }
        ed_barcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 100));
        ed_barcode.setPadding(5, 5, 5, 5);



        img_lottiestatus.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        ed_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_barcode.setFocusable(true);
                ed_barcode.requestFocus();
                ed_barcode.selectAll();
            }
        });


        tv_lottiestatus.setOnClickListener(view -> {
            Intent intent = new Intent(this, Ocr_ScanCodeActivity.class);
            startActivity(intent);
            finish();
        });
        ed_barcode.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }



                    @Override
                    public void afterTextChanged( Editable editable) {


                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {


                            if (ed_barcode.getText().toString().length()>0){

                                Search_call();

                            }else {
                                if (!ocr_goods.isEmpty()) {
                                    ocr_goods.clear();
                                }

                                img_lottiestatus.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                tv_lottiestatus.setText("اسکن کنید");
                                tv_lottiestatus.setVisibility(View.VISIBLE);
                            }



                        },  Integer.parseInt(callMethod.ReadString("Delay")));




                    }
                }
        );



        ed_barcode.setFocusable(true);
        ed_barcode.requestFocus();
    }


    public void Search_call(){
        searchtarget = NumberFunctions.EnglishNumber(ed_barcode.getText().toString());
        searchtarget = searchtarget.replaceAll(" ", "%");


        call=apiInterface.GetOcrGoodList("GetOcrGoodList",searchtarget);
        action.dialogProg();


        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    action.dialogProg_dismiss();

                    ocr_goods.clear();
                    ocr_goods = response.body().getOcr_Goods();

                    if (ocr_goods.size()> 0) {
                        try {
                            img_lottiestatus.setVisibility(View.GONE);
                            tv_lottiestatus.setText("اسکن کنید");
                            tv_lottiestatus.setVisibility(View.VISIBLE);


                            FragmentManager fragmentManager = getSupportFragmentManager();
                            Ocr_StackFragment stackFragment = (Ocr_StackFragment) fragmentManager.findFragmentByTag("STACK_FRAGMENT");

                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            if (stackFragment != null) {
                                // Update the existing fragment's data
                                stackFragment.setOcr_goods(ocr_goods);
                                stackFragment.setBarcodeScan(BarcodeScan);
                                stackFragment.callrecycler() ;
                            } else {
                                // Create a new instance of StackFragment if not already added
                                stackFragment = new Ocr_StackFragment();
                                stackFragment.setOcr_goods(ocr_goods);
                                stackFragment.setBarcodeScan(BarcodeScan);
                                fragmentTransaction.replace(R.id.ocr_confirm_a_framelayout, stackFragment, "STACK_FRAGMENT");
                            }

                            fragmentTransaction.commitAllowingStateLoss();

                            progressBar.setVisibility(View.GONE);

                        }catch (Exception e){
                            callMethod.Log(e.getMessage());

                        }
                    } else {
                        tv_lottiestatus.setText("موردی یافت نشد");
                        img_lottiestatus.setVisibility(View.VISIBLE);
                        tv_lottiestatus.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.showToast("Connection fail ...!!!");
                tv_lottiestatus.setText("موردی یافت نشد");
                img_lottiestatus.setVisibility(View.VISIBLE);
                tv_lottiestatus.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ed_barcode.setFocusable(true);
        ed_barcode.requestFocus();
        ed_barcode.selectAll();


        super.onWindowFocusChanged(hasFocus);
    }




}