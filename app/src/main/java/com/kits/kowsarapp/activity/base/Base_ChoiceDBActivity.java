package com.kits.kowsarapp.activity.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;


import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.base.Base_AllAppAdapter;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.Base_NetworkFailure;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.SafeListAccess;
import com.kits.kowsarapp.application.base.SafeValueParser;
import com.kits.kowsarapp.databinding.DefaultActivityDbBinding;
import com.kits.kowsarapp.model.base.Activation;
import com.kits.kowsarapp.model.base.Base_DBH;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.webService.base.APIClient_kowsar;
import com.kits.kowsarapp.webService.base.Kowsar_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;
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

    boolean doubleBackToExitPressedOnce = false;

    DefaultActivityDbBinding binding;
    private Call<RetrofitResponse> activationCall;
    private Call<RetrofitResponse> logReportCall;
    private boolean activationInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DefaultActivityDbBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        callMethod = new CallMethod(this);
        try {
            Config();
            init();
        } catch (RuntimeException e) {
            callMethod.Log("Base activation screen initialization failed: "
                    + e.getClass().getSimpleName());
            callMethod.showToast("آماده‌سازی صفحه فعال‌سازی انجام نشد");
        }
    }

    //*****************************************************************************************
    @SuppressLint("SdCardPath")
    public void Config() {

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
        if (activations == null) {
            callMethod.Log("Activation list was null; using an empty list");
            activations = new ArrayList<>();
        }

        binding.baseAppVersion.setText(NumberFunctions.PerisanNumber("نسخه نرم افزار : " + BuildConfig.VERSION_NAME));
        binding.baseAppRegistercode.setOnClickListener(v -> {
            if (activationInProgress) return;
            CharSequence enteredText = binding.baseAppTvGetcode.getText();
            String activationCode = enteredText == null ? "" : enteredText.toString();
            if (activationCode.trim().isEmpty()) {
                callMethod.showToast("کد فعال‌سازی را وارد کنید");
                return;
            }

            int exist=0;
            for (Activation singleactive : activations) {
                if (singleactive != null
                        && activationCode.equals(singleactive.getActivationCode())){
                    exist=exist+1;
                }
            }
            if (exist<1){
                setActivationInProgress(true);
                activationCall = apiInterface.Activation(activationCode,"1");
                activationCall.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (!canUpdateUi()) return;
                        activationCall = null;
                        RetrofitResponse body = response.body();
                        Activation responseActivation = body == null
                                ? null
                                : SafeListAccess.firstOrNull(body.getActivations());
                        if (!response.isSuccessful() || responseActivation == null) {
                            setActivationInProgress(false);
                            callMethod.Log("Activation response is empty or invalid");
                            callMethod.showToast("پاسخ فعال‌سازی معتبر نیست؛ دوباره تلاش کنید");
                            return;
                        }

                        activation = responseActivation;
                        Integer errorCode = SafeValueParser.intOrNull(activation.getErrCode());
                        if (errorCode == null) {
                            setActivationInProgress(false);
                            callMethod.Log("Activation ErrCode is invalid");
                            callMethod.showToast("پاسخ فعال‌سازی معتبر نیست؛ دوباره تلاش کنید");
                        } else if (errorCode > 0){
                            setActivationInProgress(false);
                            callMethod.showToast(activation.getErrDesc());
                        }else{
                            try {
                                base_dbh.InsertActivation(activation);
                            } catch (RuntimeException exception) {
                                setActivationInProgress(false);
                                callMethod.Log("Activation persistence failed: "
                                        + exception.getClass().getSimpleName());
                                callMethod.showToast("ذخیره‌سازی فعال‌سازی انجام نشد؛ دوباره تلاش کنید");
                                return;
                            }
                            FirstActivation(activation);
                            finish();
                            startActivity(getIntent());
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        if (!canUpdateUi()) return;
                        activationCall = null;
                        setActivationInProgress(false);
                        Base_NetworkFailure.show(
                                Base_ChoiceDBActivity.this,
                                callMethod,
                                "Activation",
                                call,
                                t
                        );
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
        if (activation == null) return;
        try {
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

        logReportCall = apiInterface.LogReport(callMethod.RetrofitBody(Body_str));

        logReportCall.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call,@NonNull  Response<RetrofitResponse> response) {
                if (logReportCall == call) logReportCall = null;
            }


            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                if (logReportCall == call) logReportCall = null;
                if (call.isCanceled()) return;
                Base_NetworkFailure.logOnly(callMethod, "FirstActivation LogReport", call, t);
            }
        });
        } catch (RuntimeException exception) {
            logReportCall = null;
            callMethod.Log("FirstActivation LogReport setup failed: "
                    + exception.getClass().getSimpleName());
        }
    }

    private void setActivationInProgress(boolean inProgress) {
        activationInProgress = inProgress;
        if (binding != null) binding.baseAppRegistercode.setEnabled(!inProgress);
    }

    private boolean canUpdateUi() {
        return binding != null && !isFinishing() && !isDestroyed();
    }

    @Override
    protected void onDestroy() {
        if (activationCall != null) activationCall.cancel();
        if (logReportCall != null) logReportCall.cancel();
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (IllegalArgumentException exception) {
                callMethod.Log("Activation dialog dismissal failed: "
                        + exception.getClass().getSimpleName());
            }
        }
        if (base_dbh != null) base_dbh.close();
        binding = null;
        super.onDestroy();
    }


    // TODO onBackPressed


//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.b_nav_a_drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "برای خروج مجددا کلیک کنید", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
//    }



}
