package com.kits.kowsarapp.activity.order;


import static com.kits.kowsarapp.R.string.textvalue_exitmessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.base.Base_AboutUsActivity;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;
import com.kits.kowsarapp.activity.broker.Broker_RegistrationActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.find.Find_Replication;
import com.kits.kowsarapp.application.order.Order_Action;
import com.kits.kowsarapp.model.find.Find_DBH;
import com.kits.kowsarapp.model.order.Order_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import java.util.Locale;


public class Order_NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Order_APIInterface order_apiInterface;
    Order_Action order_action;
    Order_DBH order_dbh;

    CallMethod callMethod;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView tv_versionname;
    TextView tv_dbname;
    TextView tv_brokercode;
    Button btn_changedb;

    TextView Getmizlist_btn0;
    TextView Getmizlist_btn1;
    TextView Getmizlist_btn2;
    TextView Getmizlist_btn3;
    TextView Getmizlist_btn4;

    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;

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

    //************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));

        setContentView(R.layout.order_activity_nav);

        init();


    }

    @SuppressLint("WrongViewCast")
    public void Config() {


        order_action = new Order_Action(this);
        callMethod = new CallMethod(this);
        order_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        order_dbh = new Order_DBH(this, callMethod.ReadString("DatabaseName"));

        order_dbh.DatabaseCreate();

        LinearLayoutCompat ll_activity_main = findViewById(R.id.ord_main_a_layout);

        DrawerLayout drawer = findViewById(R.id.order_nav_drawer_layout);

        if (callMethod.ReadString("LANG").equals("fa")) {
            ll_activity_main.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            ll_activity_main.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            ll_activity_main.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }


        toolbar = findViewById(R.id.ord_main_a_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        navigationView = findViewById(R.id.order_activity_navigation);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        tv_versionname = hView.findViewById(R.id.header_versionname);
        tv_dbname = hView.findViewById(R.id.header_dbname);
        tv_brokercode = hView.findViewById(R.id.header_brokercode);
        btn_changedb = hView.findViewById(R.id.header_changedb);


        Getmizlist_btn0 = findViewById(R.id.ord_main_a_btn0);
        Getmizlist_btn1 = findViewById(R.id.ord_main_a_btn1);
        Getmizlist_btn2 = findViewById(R.id.ord_main_a_btn2);
        Getmizlist_btn3 = findViewById(R.id.ord_main_a_btn3);
        Getmizlist_btn4 = findViewById(R.id.ord_main_a_btn4);

    }

    public void init() {
        Config();


        tv_versionname.setText(callMethod.NumberRegion(BuildConfig.VERSION_NAME));
        tv_dbname.setText(callMethod.ReadString("PersianCompanyNameUse"));
        toolbar.setTitle(callMethod.ReadString("PersianCompanyNameUse"));

        Getmizlist_btn0.setOnClickListener(v -> {


            intent = new Intent(Order_NavActivity.this, Order_TableActivity.class);
            intent.putExtra("State", "0");
            intent.putExtra("EditTable", "0");
            startActivity(intent);
        });


        Getmizlist_btn1.setOnClickListener(v -> {

            intent = new Intent(Order_NavActivity.this, Order_TableActivity.class);
            intent.putExtra("State", "1");
            intent.putExtra("EditTable", "0");
            startActivity(intent);
        });


        Getmizlist_btn2.setOnClickListener(v -> {

            intent = new Intent(Order_NavActivity.this, Order_TableActivity.class);
            intent.putExtra("State", "2");
            intent.putExtra("EditTable", "0");
            startActivity(intent);
        });


        Getmizlist_btn3.setOnClickListener(v -> {

            intent = new Intent(Order_NavActivity.this, Order_TableActivity.class);
            intent.putExtra("State", "3");
            intent.putExtra("EditTable", "0");
            startActivity(intent);
        });


        Getmizlist_btn4.setOnClickListener(v -> {

            intent = new Intent(Order_NavActivity.this, Order_TableActivity.class);
            intent.putExtra("State", "4");
            intent.putExtra("EditTable", "0");
            startActivity(intent);
        });


        btn_changedb.setOnClickListener(v -> {
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("DatabaseName", "");
            callMethod.EditString("IpConfig", "");
            callMethod.EditString("AppType", "");
            callMethod.EditString("DbName", "");
            callMethod.EditString("ActivationCode", "");
            callMethod.EditString("SecendServerURL", "");
            callMethod.EditString("FactorDbName", "");
            intent = new Intent(this, Base_SplashActivity.class);
            finish();
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.order_nav_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, textvalue_exitmessage, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        final int id = item.getItemId();

        if (id == R.id.order_aboutus) {
            intent = new Intent(this, Base_AboutUsActivity.class);
            startActivity(intent);

        } else if (id == R.id.order_nav_cfg) {

            if (callMethod.ReadString("ActivationCode").equals("444444")) {
                Intent intent = new Intent(this, Order_RegistrationActivity.class);
                startActivity(intent);
            }else {
                order_action.LoginSetting();
            }

        }
        DrawerLayout drawer = findViewById(R.id.order_nav_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;


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

    @Override
    protected void onRestart() {
        super.onRestart();
        startActivity(getIntent());
        finish();

    }
}


