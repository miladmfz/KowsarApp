package com.kits.kowsarapp.activity.find;



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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.activity.base.Base_AboutUsActivity;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.find.Find_Replication;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.find.Find_DBH;
import com.kits.kowsarapp.R;

import java.util.Locale;


public class Find_NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CallMethod callMethod;
    Toolbar toolbar;
    Find_DBH find_dbh;
    NavigationView navigationView;
    TextView tv_versionname;
    TextView tv_dbname;
    TextView tv_brokercode;
    Button btn_changedb;
    Button btn0;
    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;

    Find_Replication find_replication;

    //************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        setContentView(R.layout.find_activity_nav);

        init();


    }

    @SuppressLint("WrongViewCast")
    public void Config() {


        callMethod = new CallMethod(this);

        find_dbh = new Find_DBH(this, callMethod.ReadString("DatabaseName"));
        find_replication = new Find_Replication(this);
        //find_dbh.DatabaseCreate();

        if (callMethod.ReadString("ActivationCode").equals("888888")){
            find_replication.DoingReplicate();
        }


        LinearLayoutCompat ll_activity_main = findViewById(R.id.find_main_activity);
        DrawerLayout drawer = findViewById(R.id.find_nav_drawer_layout);


        toolbar = findViewById(R.id.find_main_a_toolbar);
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
        btn0=findViewById(R.id.find_main_a_btn0);



    }

    public void init() {
        Config();


        tv_versionname.setText(NumberFunctions.PerisanNumber(BuildConfig.VERSION_NAME));

        tv_dbname.setText(callMethod.ReadString("PersianCompanyNameUse"));
        toolbar.setTitle(callMethod.ReadString("PersianCompanyNameUse"));

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

        btn0.setText("جستجو");

        btn0.setOnClickListener(v -> {
            intent = new Intent(Find_NavActivity.this, Find_SearchActivity.class);
            intent.putExtra("scan", "");
            startActivity(intent);
        });











    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.find_nav_drawer_layout);
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
            intent = new Intent(this, Base_AboutUsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_cfg) {
            intent = new Intent(this, Find_ConfigActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.find_nav_drawer_layout);
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


