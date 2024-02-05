package com.kits.kowsarapp.activity.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Status;
import com.google.android.material.button.MaterialButton;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.DefaultActivityDbBinding;
import com.kits.kowsarapp.model.Activation;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.NumberFunctions;
import com.kits.kowsarapp.model.RetrofitResponse;
import com.kits.kowsarapp.webService.base.APIClient_kowsar;
import com.kits.kowsarapp.webService.base.APIInterface_kowsar;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Base_ChoiceDBActivity extends AppCompatActivity {

    APIInterface_kowsar apiInterface = APIClient_kowsar.getCleint_log().create(APIInterface_kowsar.class);
    CallMethod callMethod;
    Activation activation;
    Broker_DBH dbh;
    Broker_DBH dbhbase;
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

        dbhbase = new Broker_DBH(App.getContext(), "/data/data/com.kits.kowsarapp/databases/KowsarDb.sqlite");
        dbhbase.CreateActivationDb();

        dialog.setContentView(R.layout.broker_spinner_box);
        tv_rep = dialog.findViewById(R.id.b_spinner_text);
        tv_step = dialog.findViewById(R.id.b_spinner_step);
        btn_prog = dialog.findViewById(R.id.b_spinner_btn);

    }

    @SuppressLint("SdCardPath")
    public void init() {

        activations = dbhbase.getActivation();

        binding.activitionVersion.setText(NumberFunctions.PerisanNumber("نسخه نرم افزار : " + BuildConfig.VERSION_NAME));
        for (Activation singleactive : activations) {
            try {
                CreateView(singleactive);
            }catch (Exception e){
                callMethod.Log(e.getMessage());
            }

        }


        binding.activitionBtn.setOnClickListener(v -> {
            Call<RetrofitResponse> call1 = apiInterface.Activation(binding.activitionEdittext.getText().toString());
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        activation = response.body().getActivations().get(0);
                        dbhbase.InsertActivation(activation);
                        finish();

                        startActivity(getIntent());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    callMethod.Log(t.getMessage());
                }
            });

        });


    }

    public void DownloadRequest(Activation activation) {
        btn_prog.setOnClickListener(view -> DownloadRequest(activation));


        String downloadurl="http://178.131.31.161:60005/api/kits/GetDb?Code="+activation.getActivationCode();

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
                        dbh = new Broker_DBH(App.getContext(), callMethod.ReadString("DatabaseName"));
                        dbh.DatabaseCreate();
                        File tempdb = new File(activation.getDatabaseFolderPath() + "/tempDb");

                        if (tempdb.exists()) {
                            dbh.GetLastDataFromOldDataBase(activation.getDatabaseFolderPath() + "/tempDb");
                            dbh.InitialConfigInsert();
                            tempdb.delete();
                        } else {
                            dbh.InitialConfigInsert();
                        }

                        callMethod.EditString("PersianCompanyNameUse", activation.getPersianCompanyName());
                        callMethod.EditString("EnglishCompanyNameUse", activation.getEnglishCompanyName());
                        callMethod.EditString("ServerURLUse", activation.getServerURL());
                        callMethod.EditString("ActivationCode", activation.getActivationCode());
                        callMethod.EditString("AppType", activation.getAppType());

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
        String serverip = singleactive.getServerURL().substring(singleactive.getServerURL().indexOf("//") + 2);


        LinearLayoutCompat ll_main = new LinearLayoutCompat(this);
        LinearLayoutCompat ll_tv = new LinearLayoutCompat(this);
        LinearLayoutCompat ll_btn = new LinearLayoutCompat(this);
        TextView tv_PersianCompanyName = new TextView(this);
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
        tv_EnglishCompanyName.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_ServerURL.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        btn_login.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.3));
        btn_update.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.3));
        btn_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.4));

        tv_PersianCompanyName.setTextColor(getResources().getColor(R.color.grey_800));
        tv_EnglishCompanyName.setTextColor(getResources().getColor(R.color.grey_800));
        tv_ServerURL.setTextColor(getResources().getColor(R.color.grey_800));

        ll_main.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_tv.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_btn.setOrientation(LinearLayoutCompat.HORIZONTAL);


        margin_10.setMargins(10, 10, 10, 10);
        margin_5.setMargins(5, 5, 5, 5);


        ll_main.setBackgroundResource(R.color.grey_20);
        tv_PersianCompanyName.setBackgroundResource(R.color.grey_20);
        tv_EnglishCompanyName.setBackgroundResource(R.color.grey_20);
        tv_ServerURL.setBackgroundResource(R.color.grey_20);
        btn_login.setBackgroundResource(R.color.white);
        btn_update.setBackgroundResource(R.color.white);
        btn_gap.setBackgroundResource(R.color.white);

        tv_PersianCompanyName.setTextSize(26);
        tv_EnglishCompanyName.setTextSize(16);
        tv_ServerURL.setTextSize(16);
        btn_login.setTextSize(18);
        btn_update.setTextSize(18);

        ll_main.setPadding(20, 20, 20, 20);
        ll_btn.setPadding(0, 20, 0, 0);

        ll_main.setWeightSum(1);
        ll_btn.setWeightSum(1);

        btn_gap.setVisibility(View.INVISIBLE);

        tv_PersianCompanyName.setText(NumberFunctions.PerisanNumber(singleactive.getPersianCompanyName()));
        tv_EnglishCompanyName.setText("نام پوشه عکس : " + singleactive.getEnglishCompanyName());
        tv_ServerURL.setText("آدرس سرور : " + serverip);
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

                         singleactive.getActivationCode()
            );
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        activation = response.body().getActivations().get(0);
                        dbhbase.InsertActivation(activation);
                        finish();
                        startActivity(getIntent());

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {


                    callMethod.Log(t.getMessage());
                }
            });
        });

        ll_btn.addView(btn_login);
        ll_btn.addView(btn_gap);
        ll_btn.addView(btn_update);

        ll_tv.addView(tv_PersianCompanyName);
        ll_tv.addView(tv_EnglishCompanyName);
        ll_tv.addView(tv_ServerURL);

        ll_tv.addView(ll_btn, margin_5);

        ll_main.addView(ll_tv);

        binding.activitionLine.addView(ll_main, margin_10);
    }



    @Override
    protected void onDestroy() {
        cancelDownloadIfRunning(downloadId);
        super.onDestroy();
    }
    private void cancelDownloadIfRunning(int downloadId) {
        if (PRDownloader.getStatus(downloadId) == Status.RUNNING) {
            PRDownloader.cancel(downloadId);
        }
    }
}