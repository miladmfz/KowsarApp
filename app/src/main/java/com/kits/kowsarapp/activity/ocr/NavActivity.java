package com.kits.kowsarapp.activity.ocr;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.kits.ocrkowsar.BuildConfig;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.adapter.Action;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;


public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    Integer state_category;
    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;
    Button btn1,btn2,btn3;
    Handler handler;
    CallMethod callMethod;
    DatabaseHelper dbh;

    Toolbar toolbar;
    Action action;
    NavigationView navigationView;
    TextView tv_versionname;
    TextView tv_dbname;
    Button btn_changedb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        Config();
        init();

    }

//***********************************************
public void Config() {

    callMethod = new CallMethod(this);
    action = new Action(this);
    dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));

    toolbar = findViewById(R.id.NavActivity_toolbar);
    setSupportActionBar(toolbar);
    DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
    navigationView = findViewById(R.id.NavActivity_nav);
    navigationView.setNavigationItemSelectedListener(this);
    View hView = navigationView.getHeaderView(0);
    tv_versionname = hView.findViewById(R.id.header_versionname);
    tv_dbname = hView.findViewById(R.id.header_dbname);
    btn_changedb = hView.findViewById(R.id.header_changedb);




    btn1 = findViewById(R.id.mainactivity_btn1);
    btn2 = findViewById(R.id.mainactivity_btn2);
    btn3 = findViewById(R.id.mainactivity_btn3);

}

    public void init() {


        tv_versionname.setText(BuildConfig.VERSION_NAME);
        tv_dbname.setText(callMethod.ReadString("PersianCompanyNameUse"));
        toolbar.setTitle(callMethod.ReadString("PersianCompanyNameUse"));
        btn_changedb.setOnClickListener(v -> {
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("DatabaseName", "");
            callMethod.EditString("ActivationCode", "");
            callMethod.EditString("SecendServerURL", "");
            callMethod.EditString("DbName", "");
            callMethod.EditString("FactorDbName", "");
            intent = new Intent(this, SplashActivity.class);
            finish();
            startActivity(intent);
        });




        try {
            state_category=Integer.parseInt(callMethod.ReadString("Category"));
        }catch (Exception e){
            state_category=0;
        }

        if(state_category==0){
            FirstLogin();

        }else if(state_category==1){
            Scan();
        }else if(state_category==2){ //state 0
            Collect();
        }else if(state_category==3){ //state 1
            Pack();
        }else if(state_category==4){ //state 2
            Delivery();
        }else if(state_category==5){ //state 2
            Manage();
        }

    }





    public void FirstLogin() {


        btn1.setText("تنظیمات");
        btn2.setVisibility(View.GONE);
        btn3.setVisibility(View.GONE);
        btn1.setOnClickListener(view -> action.LoginSetting());

    }
    public void Scan() {

        btn1.setText("اسکن دوربین");
        btn2.setText("اسکن بارکد خوان");
        btn3.setText("فاکتور های ارسال نشده");

        btn1.setOnClickListener(view -> {
            intent = new Intent(this, ScanCodeActivity.class);
            startActivity(intent);
        });
        btn2.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.scanner);

            final EditText tv_scanner = dialog.findViewById(R.id.scanner_tv);
            dialog.show();
            tv_scanner.requestFocus();
            tv_scanner.postDelayed(() -> {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(tv_scanner, InputMethodManager.SHOW_IMPLICIT);
            }, 500);


            tv_scanner.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(() -> {

                        if(s.length()>8) {
                            intent = new Intent(NavActivity.this, FactorActivity.class);
                            intent.putExtra("ScanResponse", s.toString());
                            startActivity(intent);
                        }
                    }, 1000);
                }
            });

        });
        btn3.setOnClickListener(view -> {
            intent = new Intent(NavActivity.this, OcrFactorListActivity.class);
            intent.putExtra("State", "4");
            startActivity(intent);

        });


    }

    public void Collect(){



        btn1.setText("فاکتور های جدید انبار");
        btn2.setText("فاکتور های ارسال نشده");

        btn3.setVisibility(View.GONE);

        btn1.setOnClickListener(view -> {
            intent = new Intent(NavActivity.this, OcrFactorListActivity.class);
            intent.putExtra("State", "0");
            startActivity(intent);

        });
        btn2.setOnClickListener(view -> {
            intent = new Intent(NavActivity.this, OcrFactorListActivity.class);
            intent.putExtra("State", "4");
            startActivity(intent);

        });



    }

    public void Pack(){

        btn1.setText("فاکتور های بسته بندی");
        btn2.setText("فاکتور های ارسال نشده");
        btn3.setText("آماده ارسال ");

        btn3.setVisibility(View.GONE);

        btn1.setOnClickListener(view -> {
            intent = new Intent(NavActivity.this, OcrFactorListActivity.class);
            intent.putExtra("State", "1");
            startActivity(intent);

        });
        btn2.setOnClickListener(view -> {
            intent = new Intent(NavActivity.this, OcrFactorListActivity.class);
            intent.putExtra("State", "4");

            startActivity(intent);

        });
        btn3.setOnClickListener(view -> {
            callMethod.EditString("Last_search", "");
            intent = new Intent(NavActivity.this, OcrFactorListActivity.class);
            intent.putExtra("State", "2");
            startActivity(intent);

        });


    }


    public void Delivery(){

        btn1.setText("فاکتور های آماده");
        btn2.setText("فاکتور های من");
        btn3.setText("کل فاکتورها");


        btn1.setOnClickListener(view -> {
            callMethod.EditString("Last_search", "");
            intent = new Intent(NavActivity.this, OcrFactorListActivity.class);
            intent.putExtra("State", "2");
            startActivity(intent);
        });


        btn2.setOnClickListener(view -> {
            callMethod.EditString("Last_search", "");
            intent = new Intent(NavActivity.this, LocalFactorListActivity.class);
            intent.putExtra("IsSent", "0");
            intent.putExtra("signature", "1");
            startActivity(intent);
        });

        btn3.setOnClickListener(view -> {
            callMethod.EditString("Last_search", "");
            intent = new Intent(NavActivity.this, LocalFactorListActivity.class);
            intent.putExtra("IsSent", "1");
            intent.putExtra("signature", "1");
            startActivity(intent);
        });
    }





    public void Manage(){

        btn1.setText("وضعیت فاکتورها");
        btn2.setVisibility(View.GONE);
        btn3.setVisibility(View.GONE);


        btn1.setOnClickListener(view -> {
            callMethod.EditString("Last_search", "");
            intent = new Intent(NavActivity.this, OcrFactorListActivity.class);
            intent.putExtra("State", "4");
            startActivity(intent);
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
        callMethod.showToast("برای خروج مجددا کلیک کنید");

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        final int id = item.getItemId();

        if (id == R.id.nav_cfg) {

            action.LoginSetting();
        }
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }







    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = ""+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        callMethod.showToast(date);

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        callMethod.EditString("Last_search", "");

        startActivity(getIntent());
        finish();

    }
}

