package com.kits.kowsarapp.activity.broker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.base.Base_AboutUsActivity;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;
import com.kits.kowsarapp.application.base.Base_Action;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.application.broker.Broker_Replication;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.application.base.AlarmReceiver;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.WManager;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.GoodGroup;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.webService.base.APIClient;





public class Broker_NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    Broker_APIInterface broker_apiInterface;
    CallMethod callMethod;
    Broker_DBH dbh;
    ArrayList<GoodGroup> menugrp;
    LinearLayoutCompat llsumfactor;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView tv_versionname;
    TextView tv_dbname;
    TextView tv_brokercode;
    Button btn_changedb;
    TextView customer;
    TextView sumfac;
    Button create_factor;
    Button good_search;
    Button open_factor;
    Button all_factor;
    PersianCalendar calendar1 = new PersianCalendar();
    Button btn_test;
    TextView tv_test, tv_test2;
    WorkManager workManager;
    private Broker_Action broker_action;
    private Base_Action base_action;

    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;
    private Broker_Replication broker_replication;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.P)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broker_activity_nav);

        Config();
        try {
            Handler handler = new Handler();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                handler.postDelayed(this::init, 100);
            }
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }

    }

    //************************************************************


    public void test_fun(View v) {

        //dbh.SaveConfig("LastGpsLocationCode","0");
        Broker_Action btest =new Broker_Action(this);
        btest.sendfactor11("1");

    }


    public void GpslocationCall() {


        if (callMethod.ReadBoolan("kowsarService")) {
            AlarmReceiver alarm = new AlarmReceiver();
            alarm.setAlarm(App.getContext());
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.P)
    public void Config() {


        broker_action = new Broker_Action(this);
        base_action= new Base_Action(this);
        callMethod = new CallMethod(this);
        workManager = WorkManager.getInstance(Broker_NavActivity.this);
        dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));
        broker_replication = new Broker_Replication(this);

        dbh.ClearSearchColumn();
        dbh.DatabaseCreate();


        toolbar = findViewById(R.id.b_main_a_toolbar);
        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);
        calendar1.setTimeZone(TimeZone.getDefault());
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.b_nav_a_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.b_nav_a_nav);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        tv_versionname = hView.findViewById(R.id.header_versionname);
        tv_dbname = hView.findViewById(R.id.header_dbname);
        tv_brokercode = hView.findViewById(R.id.header_brokercode);
        btn_changedb = hView.findViewById(R.id.header_changedb);
        btn_changedb = hView.findViewById(R.id.header_changedb);

        customer = findViewById(R.id.b_main_a_customer);
        sumfac = findViewById(R.id.b_main_a_sum_factor);
        create_factor = findViewById(R.id.b_main_a_create_factor);
        good_search = findViewById(R.id.b_main_a_good_search);
        open_factor = findViewById(R.id.b_main_a_open_factor);
        all_factor = findViewById(R.id.b_main_a_all_factor);
        btn_test = findViewById(R.id.b_main_a_test_btn);
        tv_test = findViewById(R.id.b_main_a_test_tv);
        tv_test2 = findViewById(R.id.b_main_a_test_tv2);

        llsumfactor = findViewById(R.id.b_main_a_ll_sum_factor);

        GpslocationCall();


    }


    @SuppressLint("SetTextI18n")
    public void CheckConfig() {



        if (Integer.parseInt(dbh.ReadConfig("BrokerCode")) != 0) {

            tv_brokercode.setText(" کد بازاریاب : " + NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));
            if (dbh.ReadConfig("BrokerStack").equals("0")) {


                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
                builder.setTitle("انباری تعریف نشده");
                builder.setMessage("آیا مایل به تغییر کد بازاریاب می باشید ؟");

                builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                    callMethod.showToast("کد بازاریاب را وارد کنید");
                    intent = new Intent(this, Broker_ConfigActivity.class);
                    startActivity(intent);
                });

                builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                    // code to handle negative button click
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else {



            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setTitle("عدم وجود کد بازاریاب");
            builder.setMessage("آیا مایل به تعریف کد بازاریاب می باشید ؟");

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                callMethod.showToast("کد بازاریاب را وارد کنید");
                intent = new Intent(this, Broker_ConfigActivity.class);
                startActivity(intent);
            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                callMethod.showToast("برای ادامه کار به کد بازاریاب نیازمندیم");
            });

            AlertDialog dialog = builder.create();
            dialog.show();





        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "MissingPermission"})
    public void init() {
        noti();
        CheckConfig();

        Constraints conster = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(WManager.class, 1, TimeUnit.MINUTES)
                .setConstraints(conster)
                .build();

        workManager.enqueue(req);

        if (callMethod.ReadBoolan("AutoReplication")) {
            workManager.cancelAllWork();
        }



        tv_versionname.setText(NumberFunctions.PerisanNumber(BuildConfig.VERSION_NAME));
        tv_dbname.setText(callMethod.ReadString("PersianCompanyNameUse"));
        toolbar.setTitle(callMethod.ReadString("PersianCompanyNameUse"));
        menugrp = dbh.getmenuGroups();


        navigationView.getMenu().clear();

        for (GoodGroup goodGroup : menugrp) {
            navigationView.getMenu().add(NumberFunctions.PerisanNumber(goodGroup.getGoodGroupFieldValue("Name"))).setIcon(R.drawable.grpmenu).setOnMenuItemClickListener(item -> {
                intent = new Intent(this, Broker_SearchActivity.class);
                intent.putExtra("scan", "");
                intent.putExtra("id", goodGroup.getGoodGroupFieldValue("GroupCode"));
                intent.putExtra("title", goodGroup.getGoodGroupFieldValue("Name"));
                startActivity(intent);
                return false;
            });
        }

        navigationView.inflateMenu(R.menu.broker_navigation_drawer_menu);



        if (callMethod.ReadString("EnglishCompanyNameUse").equals("asli")) {
            btn_test.setVisibility(View.VISIBLE);
            tv_test.setVisibility(View.VISIBLE);
            //dbh.SaveConfig("BrokerStack","1");
        }


        create_factor.setOnClickListener(view -> {
            intent = new Intent(this, Broker_CustomerActivity.class);
            intent.putExtra("edit", "0");
            intent.putExtra("id", "0");
            intent.putExtra("factor_code", "0");
            startActivity(intent);
        });


        good_search.setOnClickListener(view -> {
            intent = new Intent(this, Broker_SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", "0");
            intent.putExtra("title", "جستجوی کالا");
            startActivity(intent);
        });

        open_factor.setOnClickListener(view -> {
            intent = new Intent(this, Broker_PFOpenActivity.class);
            intent.putExtra("fac", "1");
            startActivity(intent);
        });

        all_factor.setOnClickListener(view -> {
            intent = new Intent(this, Broker_PFActivity.class);
            startActivity(intent);
        });
        btn_changedb.setOnClickListener(v -> {
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("DatabaseName", "");
            callMethod.EditString("AppType", "");

            intent = new Intent(this, Base_SplashActivity.class);
            finish();
            startActivity(intent);
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.b_nav_a_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            workManager.cancelAllWork();
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج مجددا کلیک کنید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        final int id = item.getItemId();


        if (id == R.id.b_nav_search) {
            intent = new Intent(this, Broker_SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", "0");
            intent.putExtra("title", "جستجوی کالا");
            startActivity(intent);
        } else if (id == R.id.b_aboutus) {
            intent = new Intent(this, Base_AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.b_nav_allview) {
            intent = new Intent(this, Broker_AllGrpActivity.class);
            startActivity(intent);
        } else if (id == R.id.b_nav_buy_history) {
            intent = new Intent(this, Broker_PFActivity.class);
            startActivity(intent);
        } else if (id == R.id.b_nav_open_fac) {
            intent = new Intent(this, Broker_PFOpenActivity.class);
            intent.putExtra("fac", "1");
            startActivity(intent);
        } else if (id == R.id.b_nav_rep) {

            broker_replication.BrokerStack();
            base_action.app_info();
            try {
                workManager.cancelAllWork();
                broker_replication.DoingReplicate();
            } catch (Exception e) {
                broker_replication.DoingReplicate();

            }

        } else if (id == R.id.b_nav_buy) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) > 0) {
                intent = new Intent(this, Broker_BasketActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
                startActivity(intent);
            } else {
                Toast.makeText(this, "سبد خرید خالی است.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.b_nav_search_date) {
            intent = new Intent(this, Broker_ByDateActivity.class);
            intent.putExtra("date", "7");
            startActivity(intent);
        } else if (id == R.id.b_nav_cfg) {
            intent = new Intent(this, Broker_ConfigActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.b_nav_a_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.broker_options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.b_bag_shop) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                intent = new Intent(this, Broker_BasketActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
                startActivity(intent);

            } else {
                Toast.makeText(this, "فاکتوری انتخاب نشده است", Toast.LENGTH_SHORT).show();

            }


            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void noti() {



    }

    public void factorState() {
        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            customer.setText("فاکتوری انتخاب نشده");
            llsumfactor.setVisibility(View.GONE);
        } else {
            llsumfactor.setVisibility(View.VISIBLE);
            customer.setText(NumberFunctions.PerisanNumber(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode"))));
            sumfac.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        factorState();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onStop() {
        if (callMethod.ReadBoolan("AutoReplication")) {
            workManager.cancelAllWork();
        }
        super.onStop();
    }


}
