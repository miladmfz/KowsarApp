package com.kits.kowsarapp.activity.base;

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
import com.kits.kowsarapp.activity.broker.Broker_NavActivity;
import com.kits.kowsarapp.activity.ocr.Ocr_NavActivity;
import com.kits.kowsarapp.activity.order.Order_NavActivity;
import com.kits.kowsarapp.activity.find.Find_NavActivity;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Base_DBH;
import com.kits.kowsarapp.model.broker.Broker_DBH;

import java.io.File;
import java.util.Locale;

@SuppressLint("CustomSplashScreen")
public class Base_SplashActivity extends AppCompatActivity {


    final int PERMISSION_CODE = 1;
    Intent intent;
    CallMethod callMethod;
    Handler handler;
    Base_DBH dbhbase;

    WorkManager workManager;

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

        if (callMethod.ReadString("ServerURLUse").equals("")) {
            callMethod.EditString("DatabaseName", "");
        }
        if (callMethod.firstStart()) {


            //region $ broker
            callMethod.EditString("SellOff", "1");
            callMethod.EditString("Grid", "3");
            callMethod.EditString("PhoneNumber", "");
            callMethod.EditBoolan("AutoReplication", false);
            callMethod.EditBoolan("SellPriceTypeDeactivate", true);
            callMethod.EditBoolan("ShowDetail", true);
            callMethod.EditBoolan("LineView", false);
            callMethod.EditBoolan("kowsarService", false);
            callMethod.EditBoolan("ShowCustomerCredit", true);
            callMethod.EditBoolan("CanUseInactive ", true);
            //endregion


            //region $ ocr
            callMethod.EditString("Deliverer", "پیش فرض");
            callMethod.EditString("PrinterName", "");
            callMethod.EditString("StackCategory", "همه");
            callMethod.EditString("FactorDbName", "");

            callMethod.EditString("Category", "0");
            callMethod.EditString("ConditionPosition", "0");
            callMethod.EditString("LastTcPrint", "0");
            callMethod.EditString("JobPersonRef", "0");
            callMethod.EditString("ActiveDatabase","0");
            callMethod.EditString("RowCall", "200");
            callMethod.EditString("AccessCount", "5");

            callMethod.EditBoolan("ShowSumAmountHint", true);
            callMethod.EditBoolan("ArabicText", true);
            callMethod.EditBoolan("ShowAmount", true);
            callMethod.EditBoolan("AutoSend", true);
            callMethod.EditBoolan("PrintBarcode", true);
            callMethod.EditBoolan("JustScanner", true);
            callMethod.EditBoolan("ListOrSingle", true);

            callMethod.EditBoolan("ShortageList", false);



            //endregion

            //region $ order


            callMethod.EditString("ObjectType", "");
            callMethod.EditString("PosName", "");

            callMethod.EditString("AppBasketInfoCode", "0");
            callMethod.EditString("PosCode", "0");

            callMethod.EditBoolan("PosPayment", false);
            callMethod.EditBoolan("PaymentWithDevice", false);
            callMethod.EditBoolan("ReserveActive", false);
            callMethod.EditBoolan("CanFreeTable", false);
            //endregion

            //region $ find
            callMethod.EditBoolan("ShowInFullPage", false);
            callMethod.EditBoolan("LockSearchPage", false);
            callMethod.EditBoolan("SelectAllAfterSearch", false);
            callMethod.EditBoolan("disableSelectedFeild", false);
            //endregion



            //region $ AllApp
            callMethod.EditString("LANG", "");
            callMethod.EditString("Delay", "1000");
            callMethod.EditString("TitleSize", "20");
            callMethod.EditString("BodySize", "20");
            callMethod.EditString("Theme", "Green");
            callMethod.EditBoolan("RealAmount", false);
            callMethod.EditBoolan("ActiveStack", false);
            callMethod.EditBoolan("GoodAmount", false);

            callMethod.EditBoolan("keyboardRunnable", false);



            callMethod.EditBoolan("FirstStart", false);
            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("SQLiteURLUse", "");
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("ActivationCode", "");
            callMethod.EditString("DatabaseName", "");
            callMethod.EditString("SecendServerURL", "");
            callMethod.EditString("DbName", "");
            callMethod.EditString("AppType", "");

            dbhbase = new Base_DBH(App.getContext(), "/data/data/com.kits.kowsarapp/databases/KowsarDb.sqlite");
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



        callMethod.Log("0");
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
                        intent = new Intent(this, Base_ChoiceDBActivity.class);
                        startActivity(intent);
                        finish();
                    }, 2000);
                } else {
                    handler = new Handler();
                    handler.postDelayed(() -> {


                        if (callMethod.ReadString("AppType").equals("1")){
                            callMethod.Log("broker");
                            intent = new Intent(this, Broker_NavActivity.class);
                            startActivity(intent);
                            finish();
                        }else if (callMethod.ReadString("AppType").equals("2")){
                            callMethod.Log("ocr");
                            intent = new Intent(this, Ocr_NavActivity.class);
                            startActivity(intent);
                            finish();
                        }else if (callMethod.ReadString("AppType").equals("3")){
                            callMethod.Log("order");
                            intent = new Intent(this, Order_NavActivity.class);
                            startActivity(intent);
                            finish();
                        }else if (callMethod.ReadString("AppType").equals("4")){
                            callMethod.Log("order");
                            intent = new Intent(this, Find_NavActivity.class);
                            startActivity(intent);
                            finish();
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


    private void requestPermission() {
        callMethod.Log("1");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            callMethod.Log("2");
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
            callMethod.Log("3");
            runtimePermission();
        }
    }

    private void runtimePermission() {
        callMethod.Log("4");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        } else {
            Startapplication();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callMethod.Log("5");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                requestPermission();
                callMethod.showToast("مجوز صادر شد");
            } else {
                handler = new Handler();
                handler.postDelayed(() -> {
                    intent = new Intent(this, Base_SplashActivity.class);
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
        callMethod.Log("6");

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
