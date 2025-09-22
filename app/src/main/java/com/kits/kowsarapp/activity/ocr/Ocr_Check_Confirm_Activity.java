package com.kits.kowsarapp.activity.ocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.fragment.ocr.Ocr_CollectFragment;
import com.kits.kowsarapp.fragment.ocr.Ocr_PackFragment;
import com.kits.kowsarapp.model.base.Factor;
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

public class Ocr_Check_Confirm_Activity extends AppCompatActivity {



    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;

    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    ArrayList<Ocr_Good> ocr_goods= new ArrayList<>();
    ArrayList<Ocr_Good> ocr_goods_scan=new ArrayList<>();


    LinearLayoutCompat ll_main;
    CallMethod callMethod;
    FragmentManager fragmentManager ;
    FragmentTransaction fragmentTransaction;
    Ocr_PackFragment packFragment;


    EditText ed_barcode;

    Factor factor;
    String BarcodeScan;
    String OrderBy;
    String State;
    int width=1;
    Ocr_Action action;
    Handler handler;

    public String searchtarget = "";


    LottieAnimationView progressBar;
    LottieAnimationView img_lottiestatus;

    TextView tv_lottiestatus;

    Dialog dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        setContentView(R.layout.ocr_activity_check_confirm);

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

        ll_main = findViewById(R.id.ocr_check_confirm_a_layout);
        ed_barcode = findViewById(R.id.ocr_check_confirm_a_barcode);
        progressBar = findViewById(R.id.ocr_check_confirm_a_good_prog);
        img_lottiestatus = findViewById(R.id.ocr_check_confirm_a_good_lottie);
        tv_lottiestatus = findViewById(R.id.ocr_check_confirm_a_good_tvstatus);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        //collectFragment = new Ocr_CollectFragment();
        packFragment = new Ocr_PackFragment();


        //collectFragment.setBarcodeScan(BarcodeScan);
        packFragment.setBarcodeScan(BarcodeScan);

        ocr_goods_scan.clear();
    }



    public void init(){


        Double_Check();
    }

    public void Double_Check(){


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
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {

                        if (ocr_goods.size() > 0) {

                            if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
                                ocr_goods_scan.clear();
                                handler.removeCallbacksAndMessages(null);
                                handler.postDelayed(() -> {
                                    String barcode="" ;

                                    try {
                                        barcode = NumberFunctions.EnglishNumber(editable.toString().substring(2, editable.toString().length() - 2).replace("\n", ""));

                                    }catch (Exception e){
                                        barcode ="";
                                    }

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
                        }, Integer.parseInt(callMethod.ReadString("BarcodeDelay")));

                    }

                }
        );


        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

            OrderBy="GoodExplain1";
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
            OrderBy="FormNo";
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrMahris")){
            OrderBy="GoodName";
        }else{
            OrderBy="GoodExplain1";
        }


        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetFactor("GetOcrFactor_new",BarcodeScan,OrderBy);
        }else{
            call=secendApiInterface.GetFactor("GetOcrFactor_new",BarcodeScan,OrderBy);
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
                        if (factor.getAppIsPacked().equals("0")) {

                            packFragment.setFactor(factor);
                            packFragment.setocr_Goods(ocr_goods);
                            fragmentTransaction.replace(R.id.ocr_check_confirm_a_framelayout, packFragment);
                            fragmentTransaction.commit();


                        } else{
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


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        ed_barcode.setFocusable(true);
        ed_barcode.requestFocus();
        ed_barcode.selectAll();

        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onDestroy() {
        if (dialog1 != null && dialog1.isShowing()) {
            dialog1.dismiss();
        }
        super.onDestroy();
    }




}