package com.kits.kowsarapp.activity.search;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;
import com.kits.kowsarapp.application.base.Base_Action;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.search.Search_Replication;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.search.Search_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.search.Search_APIInterface;
import com.kits.kowsarapp.R;

import java.util.Locale;


public class Search_NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Search_APIInterface apiInterface;

    Base_Action action;
    CallMethod callMethod;
    Toolbar toolbar;
    Search_DBH dbh;
    NavigationView navigationView;
    TextView tv_versionname;
    TextView tv_dbname;
    TextView tv_brokercode;
    Button btn_changedb;
    TextView btn0;
    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;

    private Search_Replication search_replication;

    //************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_nav);

        init();


    }

    @SuppressLint("WrongViewCast")
    public void Config() {


        action = new Base_Action(this);
        callMethod = new CallMethod(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Search_APIInterface.class);

        dbh = new Search_DBH(this, callMethod.ReadString("DatabaseName"));
        search_replication = new Search_Replication(this);

        LinearLayoutCompat ll_activity_main = findViewById(R.id.sea_main_activity);

        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);


        toolbar = findViewById(R.id.MainActivity_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        navigationView = findViewById(R.id.search_activity_nav);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        tv_versionname = hView.findViewById(R.id.header_versionname);
        tv_dbname = hView.findViewById(R.id.header_dbname);
        tv_brokercode = hView.findViewById(R.id.header_brokercode);
        btn_changedb = hView.findViewById(R.id.header_changedb);
        btn0=findViewById(R.id.sea_main_btn0);



    }

    public void init() {
        Config();


        //tv_versionname.setText(NumberFunctions.PerisanNumber(BuildConfig));

        tv_dbname.setText(callMethod.ReadString("PersianCompanyNameUse"));
        toolbar.setTitle(callMethod.ReadString("PersianCompanyNameUse"));

        btn_changedb.setOnClickListener(v -> {
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("DatabaseName", "");
            intent = new Intent(this, Base_SplashActivity.class);
            finish();
            startActivity(intent);
        });

        if (Integer.parseInt(dbh.ReadConfig("BrokerCode")) != 0) {

            tv_brokercode.setText(" کد بازاریاب : " + NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));
            if (dbh.ReadConfig("BrokerStack").equals("0")) {


                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
                builder.setTitle("انباری تعریف نشده");
                builder.setMessage("آیا مایل به تغییر کد بازاریاب می باشید ؟");

                builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                    callMethod.showToast("کد بازاریاب را وارد کنید");
                    intent = new Intent(this, Search_ConfigActivity.class);
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
                intent = new Intent(this, Search_ConfigActivity.class);
                startActivity(intent);
            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                callMethod.showToast("برای ادامه کار به کد بازاریاب نیازمندیم");
            });

            AlertDialog dialog = builder.create();
            dialog.show();





        }


        btn0.setText("جستجو");





        btn0.setOnClickListener(v -> {

            if(dbh.ReadConfig("BrokerStack").equals("0")){

                intent = new Intent(Search_NavActivity.this, Search_ConfigActivity.class);
                startActivity(intent);
            }else{
                intent = new Intent(Search_NavActivity.this, Search_SearchActivity.class);
                startActivity(intent);
            }
        });











    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        final int id = item.getItemId();

        if (id == R.id.aboutus) {
//            intent = new Intent(this, AboutUsActivity.class);
//            startActivity(intent);

        } else if (id == R.id.nav_cfg) {
            intent = new Intent(this, Search_ConfigActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;


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


