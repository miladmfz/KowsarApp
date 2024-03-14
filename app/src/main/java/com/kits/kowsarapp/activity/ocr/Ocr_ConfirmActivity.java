package com.kits.kowsarapp.activity.ocr;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kits.kowsarapp.adapter.ocr.Ocr_Action;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.fragment.ocr.Ocr_CollectFragment;
import com.kits.kowsarapp.fragment.ocr.Ocr_PackFragment;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;















public class Ocr_ConfirmActivity extends AppCompatActivity {
    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;
    EditText ed_barcode;
    Ocr_DBH dbh ;
    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    LinearLayoutCompat ll_main;
    CallMethod callMethod;
    FragmentManager fragmentManager ;
    FragmentTransaction fragmentTransaction;
    Ocr_CollectFragment collectFragment;
    Ocr_PackFragment packFragment;
    ArrayList<Ocr_Good> ocr_goods;
    ArrayList<Ocr_Good> ocr_goods_scan=new ArrayList<>();
    Factor factor;
    String BarcodeScan;
    String OrderBy;
    String State;
    int width=1;
    Ocr_Action action;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));
        action = new Ocr_Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

        handler=new Handler();
        for (final String[] ignored : arraygood_shortage) {
            arraygood_shortage.add(new String[]{"goodcode","amount "});
        }

        ll_main = findViewById(R.id.ocr_confirm_a_layout);
        ed_barcode = findViewById(R.id.ocr_confirm_a_barcode);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        collectFragment = new Ocr_CollectFragment();
        packFragment = new Ocr_PackFragment();
        collectFragment.setBarcodeScan(BarcodeScan);
        packFragment.setBarcodeScan(BarcodeScan);
        ocr_goods_scan.clear();
    }




    public void init(){



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

                            ocr_goods_scan.clear();
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(() -> {
                                String barcode = NumberFunctions.EnglishNumber(editable.toString().substring(2,editable.toString().length()-2).replace("\n", ""));

                                ed_barcode.selectAll();

                                for (Ocr_Good singlegood : ocr_goods) {
                                    if (singlegood.getCachedBarCode().indexOf(barcode) > 0) {
                                        ocr_goods_scan.add(singlegood);
                                    }

                                }

                                action.GoodScanDetail(ocr_goods_scan,State,BarcodeScan);
                            }, 200);
                        }
                    }

                });





        if(State.equals("0")){
            OrderBy="GoodExplain1";
        }else{
            OrderBy="GoodName";
        }


        Call<RetrofitResponse> call;

        String Body_str  = "";

        Body_str =callMethod.CreateJson("barcode", BarcodeScan, Body_str);
        Body_str =callMethod.CreateJson("Step", "0", Body_str);
        Body_str =callMethod.CreateJson("orderby", OrderBy, Body_str);

        callMethod.Log("FactorDbName="+callMethod.ReadString("FactorDbName"));
        callMethod.Log("DbName="+callMethod.ReadString("DbName"));


        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call = apiInterface.GetOcrFactor(callMethod.RetrofitBody(Body_str));
        }else{
            call = secendApiInterface.GetOcrFactor(callMethod.RetrofitBody(Body_str));
        }
        callMethod.Log("call="+call.request().url().toString());

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    factor = response.body().getFactors().get(0);
                    if (factor.getFactorCode().equals("0")) {
                        callMethod.showToast("لطفا مجددا اسکن کنید");
                        finish();
                    } else {
                        ocr_goods=response.body().getOcr_Goods();
                        if (factor.getAppIsControled().equals("False")) {
                            collectFragment.setFactor(factor);
                            collectFragment.setocr_Goods(ocr_goods);
                            fragmentTransaction.replace(R.id.ocr_confirm_a_framelayout, collectFragment);
                            fragmentTransaction.commit();
                        } else if (factor.getAppIsPacked().equals("False")) {
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
                callMethod.Log(t.getMessage());
            }
        });

        ed_barcode.setFocusable(true);
        ed_barcode.requestFocus();
    }


}