package com.kits.kowsarapp.activity.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;


import com.downloader.Status;
import com.google.android.material.button.MaterialButton;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.DefaultActivityDbBinding;
import com.kits.kowsarapp.model.base.Activation;
import com.kits.kowsarapp.model.base.Base_DBH;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.order.Order_DBH;
import com.kits.kowsarapp.model.search.Search_DBH;
import com.kits.kowsarapp.webService.base.APIClient_kowsar;
import com.kits.kowsarapp.webService.base.Kowsar_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.io.File;
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
    TextView tv_rep;
    TextView tv_step;
    Dialog dialog;
    ArrayList<Activation> activations;
    Button btn_prog;
    Intent intent;
    int downloadId;
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
        dialog = new Dialog(this);
        activation = new Activation();
        apiInterface = APIClient_kowsar.getCleint_log().create(Kowsar_APIInterface.class);
        base_dbh = new Base_DBH(App.getContext(), "/data/data/com.kits.kowsarapp/databases/KowsarDb.sqlite");
        base_dbh.CreateActivationDb();

        dialog.setContentView(R.layout.broker_spinner_box);
        tv_rep = dialog.findViewById(R.id.b_spinner_text);
        tv_step = dialog.findViewById(R.id.b_spinner_step);
        btn_prog = dialog.findViewById(R.id.b_spinner_btn);

    }

    @SuppressLint("SdCardPath")
    public void init() {

        activations = base_dbh.getActivation();
        Log.e("kowsar","activations="+activations.size()+"");

        binding.baseAppVersion.setText(NumberFunctions.PerisanNumber("نسخه نرم افزار : " + BuildConfig.VERSION_NAME));
        for (Activation singleactive : activations) {

            try {
                CreateView(singleactive);
            }catch (Exception e){
                callMethod.Log(e.getMessage());
            }

        }


        binding.baseAppRegistercode.setOnClickListener(v -> {
            Log.e("kowsar","0");


            int exist=0;
            Log.e("kowsar","1");

            for (Activation singleactive : activations) {

                if (binding.baseAppTvGetcode.getText().toString().equals(singleactive.getActivationCode())){

                    exist=exist+1;
                }


            }
            Log.e("kowsar","3");

            if (exist<1){
                Log.e("kowsar","4");

                Call<RetrofitResponse> call1 = apiInterface.Activation(
                        Objects.requireNonNull(binding.baseAppTvGetcode.getText()).toString(),"1");
                call1.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            Log.e("kowsar","5");

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
                        Log.e("kowsar",t.getMessage());

                    }
                });




            }else{
                callMethod.showToast("این کد وارد شده است");
            }



        });


    }

    public void DownloadRequest(Activation activation) {
        btn_prog.setOnClickListener(view -> DownloadRequest(activation));


        String downloadurl="http://5.160.152.173:60005/api/kits/GetDb?Code="+activation.getActivationCode();

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();

        PRDownloader.initialize(getApplicationContext(), config);

        // Setting timeout globally for the download network requests:
        PRDownloaderConfig config1 = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config1);


        downloadId = PRDownloader.download(
                        downloadurl,
                        activation.getDatabaseFolderPath(),
                        "KowsarDbTemp.sqlite"
                )

                .build()
                .setOnStartOrResumeListener(() -> {
                    dialog.show();
                    dialog.setCancelable(false);
                })
                .setOnPauseListener(() -> {

                })
                .setOnCancelListener(() -> {
                    File DownloadTemp = new File(activation.getDatabaseFolderPath() + "/KowsarDbTemp.sqlite");
                    DownloadTemp.delete();
                })

                .setOnProgressListener(progress -> {
                    tv_rep.setText("در حال بارگیری...");
                    tv_step.setVisibility(View.VISIBLE);
                    tv_step.setText(NumberFunctions.PerisanNumber((((progress.currentBytes) * 100) / progress.totalBytes) + "/100"));
                })

                .start(new OnDownloadListener() {
                    @SuppressLint("SdCardPath")
                    @Override

                    public void onDownloadComplete() {

                        File DownloadTemp = new File(activation.getDatabaseFolderPath() + "/KowsarDbTemp.sqlite");
                        File CompletefILE = new File(activation.getDatabaseFolderPath() + "/KowsarDb.sqlite");
                        DownloadTemp.renameTo(CompletefILE);
                        callMethod.EditString("DatabaseName", activation.getDatabaseFilePath());

                        if (activation.getAppType().equals("1")){ // broker
                            Broker_DBH broker_dbh = new Broker_DBH(App.getContext(), callMethod.ReadString("DatabaseName"));
                            broker_dbh.DatabaseCreate();
                            broker_dbh.InitialConfigInsert();
                        }else if (activation.getAppType().equals("2")){ // ocr
                            Ocr_DBH ocr_dbh = new Ocr_DBH(App.getContext(), callMethod.ReadString("DatabaseName"));
                            ocr_dbh.DatabaseCreate();
                        }else if (activation.getAppType().equals("3")){ // order
                            Order_DBH order_dbh = new Order_DBH(App.getContext(), callMethod.ReadString("DatabaseName"));
                            order_dbh.DatabaseCreate();
                        }else if (activation.getAppType().equals("4")){ // search
                            Search_DBH search_dbh = new Search_DBH(App.getContext(), callMethod.ReadString("DatabaseName"));
                            search_dbh.DatabaseCreate();
                        }


                        callMethod.EditString("PersianCompanyNameUse", activation.getPersianCompanyName());
                        callMethod.EditString("EnglishCompanyNameUse", activation.getEnglishCompanyName());
                        callMethod.EditString("ServerURLUse", activation.getServerURL());
                        callMethod.EditString("IpConfig", "");
                        callMethod.EditString("AppType", activation.getAppType());

                        callMethod.EditString("ActivationCode", activation.getActivationCode());
                        intent = new Intent(App.getContext(), Base_SplashActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }


                    @Override
                    public void onError(Error error) {
                        btn_prog.setVisibility(View.VISIBLE);
                        File DownloadTemp = new File(activation.getDatabaseFolderPath() + "/KowsarDbTemp.sqlite");
                        DownloadTemp.delete();
                        tv_step.setText("مشکل ارتباطی لطفا دوباره امتحان کنید");

                    }
                });

    }

    @SuppressLint({"SetTextI18n", "SdCardPath"})
    public void CreateView(Activation singleactive) {


        LinearLayoutCompat ll_main = new LinearLayoutCompat(this);
        LinearLayoutCompat ll_tv = new LinearLayoutCompat(this);
        LinearLayoutCompat ll_btn = new LinearLayoutCompat(this);
        TextView tv_PersianCompanyName = new TextView(this);
        TextView tv_apptype = new TextView(this);
        TextView tv_EnglishCompanyName = new TextView(this);
        TextView tv_ServerURL = new TextView(this);
        MaterialButton btn_login = new MaterialButton(this);
        MaterialButton btn_update = new MaterialButton(this);
        MaterialButton btn_gap = new MaterialButton(this);
        LinearLayoutCompat.LayoutParams margin_10 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        LinearLayoutCompat.LayoutParams margin_5 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        ll_main.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.3));
        ll_btn.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.7));
        tv_PersianCompanyName.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_apptype.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_EnglishCompanyName.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_ServerURL.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        btn_login.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.3));
        btn_update.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.3));
        btn_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.4));

        tv_PersianCompanyName.setTextColor(getResources().getColor(R.color.grey_800));
        tv_apptype.setTextColor(getResources().getColor(R.color.grey_800));
        tv_EnglishCompanyName.setTextColor(getResources().getColor(R.color.grey_800));
        tv_ServerURL.setTextColor(getResources().getColor(R.color.grey_800));

        ll_main.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_tv.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_btn.setOrientation(LinearLayoutCompat.HORIZONTAL);


        margin_10.setMargins(10, 10, 10, 10);
        margin_5.setMargins(5, 5, 5, 5);


        ll_main.setBackgroundResource(R.color.grey_20);
        tv_PersianCompanyName.setBackgroundResource(R.color.grey_20);
        tv_apptype.setBackgroundResource(R.color.grey_20);
        tv_EnglishCompanyName.setBackgroundResource(R.color.grey_20);
        tv_ServerURL.setBackgroundResource(R.color.grey_20);

        btn_gap.setBackgroundResource(R.color.white);

        tv_PersianCompanyName.setTextSize(26);
        tv_apptype.setTextSize(18);
        tv_EnglishCompanyName.setTextSize(16);
        tv_ServerURL.setTextSize(20);
        btn_login.setTextSize(18);
        btn_update.setTextSize(18);

        ll_main.setPadding(20, 20, 20, 20);
        ll_btn.setPadding(0, 20, 0, 0);

        ll_main.setWeightSum(1);
        ll_btn.setWeightSum(1);

        btn_gap.setVisibility(View.INVISIBLE);

        tv_PersianCompanyName.setText(NumberFunctions.PerisanNumber(singleactive.getPersianCompanyName()));

        if (singleactive.getAppType().equals("1")){ // broker
            tv_apptype.setText("نوع نرم افزار : بازاریابی");
        }else if (singleactive.getAppType().equals("2")){ // ocr
            tv_apptype.setText("نوع نرم افزار : انباردازی و توضیع");
        }else if (singleactive.getAppType().equals("3")){ // order
            tv_apptype.setText("نوع نرم افزار : سفارشگیری");
        }else if (singleactive.getAppType().equals("4")){ // search
            tv_apptype.setText("نوع نرم افزار : جستجوی کالا");
        }


        tv_EnglishCompanyName.setText("نام پوشه عکس : " + singleactive.getEnglishCompanyName());

        String fullUrl = singleactive.getServerURL(); // مقدار کامل URL
        String ipAddress = "";

        if (fullUrl != null && fullUrl.startsWith("http")) {
            // جدا کردن پروتکل و مسیر اضافی
            String temp = fullUrl.replace("http://", "").replace("https://", "");
            // جدا کردن فقط IP
            int colonIndex = temp.indexOf(':');
            if (colonIndex != -1) {
                ipAddress = temp.substring(0, colonIndex); // بخش IP قبل از ':'
            } else {
                int slashIndex = temp.indexOf('/');
                ipAddress = (slashIndex != -1) ? temp.substring(0, slashIndex) : temp;
            }
        }



        tv_ServerURL.setText("آدرس سرور : " + ipAddress);
        btn_login.setText("ورود");
        btn_update.setText("اصلاح");


        btn_login.setOnClickListener(v -> {


            if (!new File(singleactive.getDatabaseFilePath()).exists()) {
                DownloadRequest(singleactive);
            } else {
                callMethod.EditString("PersianCompanyNameUse", singleactive.getPersianCompanyName());
                callMethod.EditString("EnglishCompanyNameUse", singleactive.getEnglishCompanyName());
                callMethod.EditString("ServerURLUse", singleactive.getServerURL());
                callMethod.EditString("DatabaseName", singleactive.getDatabaseFilePath());
                callMethod.EditString("ActivationCode", singleactive.getActivationCode());
                callMethod.EditString("AppType", singleactive.getAppType());

                intent = new Intent(this, Base_SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btn_update.setOnClickListener(v -> {

            Call<RetrofitResponse> call1 = apiInterface.Activation(

                    singleactive.getActivationCode(),"0"
            );
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        activation = response.body().getActivations().get(0);
                        base_dbh.InsertActivation(activation);
                        finish();
                        startActivity(getIntent());

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

//                    callMethod.ErrorLog(t.getMessage());
                }
            });
        });

        ll_btn.addView(btn_login);
        ll_btn.addView(btn_gap);
        ll_btn.addView(btn_update);

        ll_tv.addView(tv_PersianCompanyName);
        ll_tv.addView(tv_apptype);
        ll_tv.addView(tv_EnglishCompanyName);
        ll_tv.addView(tv_ServerURL);

        ll_tv.addView(ll_btn, margin_5);

        ll_main.addView(ll_tv);

        binding.baseAppLine.addView(ll_main, margin_10);
    }

    @SuppressLint("HardwareIds")
    public void FirstActivation(Activation activation) {


        Log.e("Debug Build.VERSION.SDK_INT =", Build.VERSION.SDK_INT+"");


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

        Log.e("e=",""+Body_str);
        Call<RetrofitResponse> call = apiInterface.LogReport(callMethod.RetrofitBody(Body_str));
        Log.e("ec=",""+call.request().url());
        Log.e("ec=",""+call.request().body());


        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                Log.e("res=",""+response.body().toString());

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


    @Override
    protected void onDestroy() {
        if (PRDownloader.getStatus(downloadId) == Status.RUNNING) {
            PRDownloader.cancel(downloadId);
        }
        super.onDestroy();

    }
}