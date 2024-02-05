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

import com.kits.ocrkowsar.Fragment.CollectFragment;
import com.kits.ocrkowsar.Fragment.PackFragment;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.adapter.Action;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Factor;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmActivity extends AppCompatActivity {
    APIInterface apiInterface;
    APIInterface secendApiInterface;
    EditText ed_barcode;
    DatabaseHelper dbh ;
    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    LinearLayoutCompat ll_main;
    CallMethod callMethod;
    FragmentManager fragmentManager ;
    FragmentTransaction fragmentTransaction;
    CollectFragment collectFragment;
    PackFragment packFragment;
    ArrayList<Good> goods;
    ArrayList<Good> goods_scan=new ArrayList<>();
    Factor factor;
    String BarcodeScan;
    String OrderBy;
    String State;
    int width=1;
    Action action;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();

        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        }catch (Exception e){
            callMethod.ErrorLog(e.getMessage());
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
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        action = new Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        secendApiInterface = APIClient.getCleint(callMethod.ReadString("SecendServerURL")).create(APIInterface.class);

        handler=new Handler();
        for (final String[] ignored : arraygood_shortage) {
            arraygood_shortage.add(new String[]{"goodcode","amount "});
        }

        ll_main = findViewById(R.id.confirm_layout);
        ed_barcode = findViewById(R.id.confirmActivity_barcode);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        collectFragment = new CollectFragment();
        packFragment = new PackFragment();
        collectFragment.setBarcodeScan(BarcodeScan);
        packFragment.setBarcodeScan(BarcodeScan);
        goods_scan.clear();
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
                        if (goods.size() > 0) {

                            goods_scan.clear();
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(() -> {
                                String barcode = NumberFunctions.EnglishNumber(editable.toString().substring(2,editable.toString().length()-2).replace("\n", ""));

                                ed_barcode.selectAll();

                                for (Good singlegood : goods) {
                                    if (singlegood.getCachedBarCode().indexOf(barcode) > 0) {
                                        goods_scan.add(singlegood);
                                    }

                                }

                                action.GoodScanDetail(goods_scan,State,BarcodeScan);
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
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetFactor("Getocrfactor",BarcodeScan,OrderBy);
        }else{
            call=secendApiInterface.GetFactor("Getocrfactor",BarcodeScan,OrderBy);
        }

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    factor = response.body().getFactor();
                    if (factor.getFactorCode().equals("0")) {
                        callMethod.showToast("لطفا مجددا اسکن کنید");
                        finish();
                    } else {
                        goods = response.body().getGoods();
                        if (factor.getAppIsControled().equals("0")) {
                            collectFragment.setFactor(factor);
                            collectFragment.setGoods(goods);
                            fragmentTransaction.replace(R.id.confirm_framelayout, collectFragment);
                            fragmentTransaction.commit();
                        } else if (factor.getAppIsPacked().equals("0")) {
                            packFragment.setFactor(factor);
                            packFragment.setGoods(goods);
                            fragmentTransaction.replace(R.id.confirm_framelayout, packFragment);
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


}