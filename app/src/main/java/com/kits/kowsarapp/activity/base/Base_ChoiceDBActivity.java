package com.kits.kowsarapp.activity.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.base.Base_AllAppAdapter;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.DefaultActivityDbBinding;
import com.kits.kowsarapp.model.base.Activation;
import com.kits.kowsarapp.model.base.Base_DBH;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.webService.base.APIClient_kowsar;
import com.kits.kowsarapp.webService.base.Kowsar_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Base_ChoiceDBActivity extends AppCompatActivity {

    Kowsar_APIInterface apiInterface ;
    CallMethod callMethod;
    Activation activation;
    Base_DBH base_dbh;

    Dialog dialog;
    Base_AllAppAdapter base_allAppAdapter;
    GridLayoutManager gridLayoutManager;

    ArrayList<Activation> activations;
    TextView tv_rep;
    TextView tv_step;
    Button btn_prog;


    DefaultActivityDbBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DefaultActivityDbBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Config();

        try {
            init();
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }
    }

    //*****************************************************************************************
    @SuppressLint("SdCardPath")
    public void Config() {

        callMethod = new CallMethod(this);
        activation = new Activation();
        apiInterface = APIClient_kowsar.getCleint_log().create(Kowsar_APIInterface.class);
        base_dbh = new Base_DBH(App.getContext(), "/data/data/com.kits.kowsarapp/databases/KowsarDb.sqlite");
        base_dbh.CreateActivationDb();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.broker_spinner_box);
        tv_rep = dialog.findViewById(R.id.b_spinner_text);
        tv_step = dialog.findViewById(R.id.b_spinner_step);
        btn_prog = dialog.findViewById(R.id.b_spinner_btn);

    }

    @SuppressLint("SdCardPath")
    public void init() {

        activations = base_dbh.getActivation();

        binding.baseAppVersion.setText(NumberFunctions.PerisanNumber("نسخه نرم افزار : " + BuildConfig.VERSION_NAME));
        binding.baseAppRegistercode.setOnClickListener(v -> {
            int exist=0;
            for (Activation singleactive : activations) {
                if (binding.baseAppTvGetcode.getText().toString().equals(singleactive.getActivationCode())){
                    exist=exist+1;
                }
            }
            if (exist<1){
                Call<RetrofitResponse> call1 = apiInterface.Activation(
                        Objects.requireNonNull(binding.baseAppTvGetcode.getText()).toString(),"1");
                call1.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            activation = response.body().getActivations().get(0);
                            if (Integer.parseInt(activation.getErrCode())>0){
                                callMethod.showToast(activation.getErrDesc());
                            }else{
                                FirstActivation(activation);
                                base_dbh.InsertActivation(activation);
                                finish();
                                startActivity(getIntent());
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        callMethod.Log(t.getMessage());
                    }
                });
            }else{
                callMethod.showToast("این کد وارد شده است");
            }
        });

        callMethod.Log("activations = " +activations.size()+"");

        base_allAppAdapter = new Base_AllAppAdapter(activations, this);

        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.baseAppAllapp.setLayoutManager(gridLayoutManager);
        binding.baseAppAllapp.setAdapter(base_allAppAdapter);
        binding.baseAppAllapp.setItemAnimator(new DefaultItemAnimator());


    }


    @SuppressLint("HardwareIds")
    public void FirstActivation(Activation activation) {




        @SuppressLint("HardwareIds") String android_id = BuildConfig.BUILD_TYPE.equals("release") ?
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) :
                "debug";
        PersianCalendar calendar1 = new PersianCalendar();
        calendar1.setTimeZone(TimeZone.getDefault());
        String version = BuildConfig.VERSION_NAME;



        Kowsar_APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(Kowsar_APIInterface.class);
//        Call<RetrofitResponse> call = apiInterface.Kowsar_log("Kowsar_log", android_id
//                , url
//                , callMethod.ReadString("PersianCompanyNameUse")
//                , callMethod.ReadString("PreFactorCode")
//                , calendar1.getPersianShortDateTime()
//                , dbh.ReadConfig("BrokerCode")
//                , version);
//
//

        String Body_str  = "";
        Body_str =callMethod.CreateJson("Device_Id", android_id, Body_str);
        Body_str =callMethod.CreateJson("Address_Ip", activation.getServerURL(), Body_str);
        Body_str =callMethod.CreateJson("Server_Name", activation.getPersianCompanyName(), Body_str);
        Body_str =callMethod.CreateJson("Factor_Code", "0", Body_str);
        Body_str =callMethod.CreateJson("StrDate", calendar1.getPersianShortDateTime(), Body_str);
        Body_str =callMethod.CreateJson("Broker",  "0", Body_str);
        Body_str =callMethod.CreateJson("Explain", version, Body_str);
        Body_str =callMethod.CreateJson("DeviceAgant", Build.BRAND+" / "+Build.MODEL+" / "+Build.HARDWARE, Body_str);
        Body_str =callMethod.CreateJson("SdkVersion", Build.VERSION.SDK_INT+"", Body_str);
        Body_str =callMethod.CreateJson("DeviceIp", "---- / -----", Body_str);

        callMethod.Log("Body_str = "+Body_str);

        Call<RetrofitResponse> call = apiInterface.LogReport(callMethod.RetrofitBody(Body_str));
        callMethod.Log(""+call.request().url());
        callMethod.Log(""+call.request().body());


        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {

                if (response.isSuccessful()) {
                    // Handle successful response
                } else {
                    // Handle unsuccessful response
                }
            }


            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                // Handle failure
            }
        });






    }

}