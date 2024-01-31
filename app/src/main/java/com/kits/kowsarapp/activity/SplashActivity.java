package com.kits.kowsarapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.WorkManager;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.App;
import com.kits.kowsarapp.application.CallMethod;
import com.kits.kowsarapp.model.broker.Broker_DBH;

import java.io.File;
import java.util.Locale;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


    final int PERMISSION_CODE = 1;
    Intent intent;
    CallMethod callMethod;
    Handler handler;
    Broker_DBH dbh, dbhbase;
    WorkManager workManager;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_activity_splash);
        Config();
        try {
            init();

        } catch (Exception e) {
            callMethod.Log(e.getMessage());

        }

    }


    //***************************************************************************************



    public void Config() {
//        LinearLayoutCompat ll_activity = findViewById(R.id.splashactivity);
//
//        if (callMethod.ReadString("LANG").equals("fa")) {
//            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//        } else if (callMethod.ReadString("LANG").equals("ar")) {
//            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//        } else {
//            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//        }

    }


    @SuppressLint("SdCardPath")
    public void init() {
        callMethod = new CallMethod(this);
        dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));

        if (callMethod.ReadString("ServerURLUse").equals("")) {
            callMethod.EditString("DatabaseName", "");
        }
        if (callMethod.firstStart()) {


            //region $ broker
            callMethod.EditString("SellOff", "1");
            callMethod.EditString("Grid", "3");
            callMethod.EditString("Delay", "1000");
            callMethod.EditString("TitleSize", "18");
            callMethod.EditString("BodySize", "18");
            callMethod.EditString("PhoneNumber", "");
            callMethod.EditString("Theme", "Green");
            callMethod.EditBoolan("RealAmount", false);
            callMethod.EditBoolan("ActiveStack", false);
            callMethod.EditBoolan("GoodAmount", false);
            callMethod.EditBoolan("AutoReplication", false);
            callMethod.EditBoolan("SellPriceTypeDeactivate", true);
            callMethod.EditBoolan("ShowDetail", true);
            callMethod.EditBoolan("LineView", false);
            callMethod.EditBoolan("keyboardRunnable", false);
            callMethod.EditBoolan("kowsarService", false);
            callMethod.EditBoolan("ShowCustomerCredit", true);


            //endregion

            //region $ ocr
            callMethod.EditString("Deliverer", "پیش فرض");
            callMethod.EditString("Category", "0");
            callMethod.EditString("StackCategory", "همه");
            callMethod.EditString("ConditionPosition", "0");
            callMethod.EditString("TitleSize", "22");
            callMethod.EditString("LastTcPrint", "0");
            callMethod.EditBoolan("ArabicText", true);
            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("SQLiteURLUse", "");
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("DatabaseName", "");
            callMethod.EditString("ActivationCode", "");
            callMethod.EditString("SecendServerURL", "");
            callMethod.EditString("DbName", "");
            callMethod.EditString("AppType", "");
            callMethod.EditString("FactorDbName", "");

            //endregion

            //region $ order

            callMethod.EditString("Delay", "1000");
            callMethod.EditString("TitleSize", "20");
            callMethod.EditString("BodySize", "20");
            callMethod.EditString("Theme", "Green");
            callMethod.EditString("LANG", "");
            callMethod.EditString("AppBasketInfoCode", "0");
            callMethod.EditBoolan("RealAmount", false);
            callMethod.EditBoolan("ActiveStack", false);
            callMethod.EditBoolan("GoodAmount", false);
            callMethod.EditString("ObjectType", "");


            //endregion

            //region $ AllApp
            callMethod.EditBoolan("FirstStart", false);
            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("SQLiteURLUse", "");
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("ActivationCode", "");
            callMethod.EditString("AppType", "");

            dbhbase = new Broker_DBH(App.getContext(), "/data/data/com.kits.brokerkowsar/databases/KowsarDb.sqlite");
            dbhbase.CreateActivationDb();
            //endregion

        }





        //region $ BrokerApp
        if (callMethod.ReadBoolan("AutoReplication")) {
            try {
                workManager.cancelAllWork();
            } catch (Exception ignored) {
            }
        }
        callMethod.EditString("Filter", "0");
        callMethod.EditString("PreFactorCode", "0");
        callMethod.EditString("PreFactorGood", "0");
        callMethod.EditString("BasketItemView", "0");

        //endregion

        //region $ ocr
        callMethod.EditString("Last_search", "");
        callMethod.EditString("LastTcPrint", "0");
        callMethod.EditString("ConditionPosition", "0");
        callMethod.EditString("FactorDbName", "");
        //endregion

        //region $ order
        callMethod.EditString("ObjectType", "");
        callMethod.EditString("AppBasketInfoCode", "0");
        callMethod.EditString("MizType", "");
        callMethod.EditString("InfoState", "");
        callMethod.EditString("RstMizName", "");
        //endregion




        requestPermission();


    }

    private void Startapplication() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            File databasedir = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse"));
            File temp = new File(databasedir, "/tempDb");
            if (!temp.exists()) {
                if (callMethod.ReadString("DatabaseName").equals("")) {
                    handler = new Handler();
                    handler.postDelayed(() -> {
                        intent = new Intent(this, ChoiceDatabaseActivity.class);
                        startActivity(intent);
                        finish();
                    }, 2000);
                } else {
                    handler = new Handler();
                    handler.postDelayed(() -> {


//                        intent = new Intent(this, NavActivity.class);
//                        startActivity(intent);
//                        finish();

                        if (callMethod.ReadString("AppType").equals("0")){
                            callMethod.Log("company");
                        }else if (callMethod.ReadString("AppType").equals("1")){
                            callMethod.Log("broker");
                        }else if (callMethod.ReadString("AppType").equals("2")){
                            callMethod.Log("ocr");
                        }else if (callMethod.ReadString("AppType").equals("3")){
                            callMethod.Log("order");
                        }



                    }, 2000);
                }
            } else {

                callMethod.EditString("ServerURLUse", "");
                callMethod.EditString("SQLiteURLUse", "");
                callMethod.EditString("PersianCompanyNameUse", "");
                callMethod.EditString("EnglishCompanyNameUse", "");
                callMethod.EditString("DatabaseName", "");
                callMethod.EditString("ActivationCode", "");
                callMethod.EditString("AppType", "");

                startActivity(getIntent());
                finish();
            }

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        }


    }



    //region $ permissions

    private void runtimePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        } else {
            Startapplication();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_CODE);
            } else {
                Startapplication();
            }
        } else {
            runtimePermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                requestPermission();
                callMethod.showToast("مجوز صادر شد");
            } else {
                handler = new Handler();
                handler.postDelayed(() -> {
                    intent = new Intent(this, SplashActivity.class);
                    finish();
                    startActivity(intent);
                }, 2000);
                callMethod.showToast("مجوز مربوطه را فعال نمایید");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callMethod.showToast("دارای مجوز");
            } else {
                callMethod.showToast("بدون مجوز");
            }
            requestPermission();
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }


    //endregion


    //region $ changeLanguage




    @SuppressLint("ObsoleteSdkInt")
    public static ContextWrapper changeLanguage(Context context, String lang) {

        Locale currentLocal;
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocal = conf.getLocales().get(0);
        } else {
            currentLocal = conf.locale;
        }

        if (!lang.equals("") && !currentLocal.getLanguage().equals(lang)) {
            Locale newLocal = new Locale(lang);
            Locale.setDefault(newLocal);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                conf.setLocale(newLocal);
            } else {
                conf.locale = newLocal;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context = context.createConfigurationContext(conf);
            } else {
                res.updateConfiguration(conf, context.getResources().getDisplayMetrics());
            }


        }

        return new ContextWrapper(context);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("profile", Context.MODE_PRIVATE);
        String currentLang = preferences.getString("LANG", "");
        if (currentLang.equals("")) {
            currentLang = getAppLanguage();
        }
        Context context = changeLanguage(newBase, currentLang);
        super.attachBaseContext(context);
    }

    public String getAppLanguage() {
        return Locale.getDefault().getLanguage();
    }

    //endregion



}
