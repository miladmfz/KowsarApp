package com.kits.kowsarapp.activity.ocr;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.kowsarapp.adapter.ocr.Ocr_FactorListLocal_Adapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.OcrFactorSelectionPolicy;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.util.ArrayList;
import java.util.Objects;


public class Ocr_FactorListLocalActivity extends AppCompatActivity {

    private Ocr_DBH ocr_dbh;
    Ocr_FactorListLocal_Adapter ocr_factorListLocal_adapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView factor_header_recycler;
    private EditText edtsearch;
    final Handler handler = new Handler(Looper.getMainLooper());
    final Handler lifecycleHandler = new Handler(Looper.getMainLooper());
    Dialog dialog1;
    ArrayList<Factor> factors = new ArrayList<>();
    public ArrayList<String[]> Multi_sign = new ArrayList<>();
    public ArrayList<String> Multi_barcode = new ArrayList<>();


    String IsSent, signature = "1", SearchTarget = "";
    TextView textView_Count;
    int width = 1;


    FloatingActionButton fab;

    Menu item_multi;
    Intent intent;
    CallMethod callMethod;
    Toolbar toolbar;
    SwitchMaterial mySwitch_activestack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        setContentView(R.layout.ocr_activity_factorlist_local);

        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.ocr_spinner_box);
        TextView repw = dialog1.findViewById(R.id.ocr_spinner_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        intent();
        Config();
        try {
            lifecycleHandler.postDelayed(() -> {
                if (!isFinishing()) init();
            }, 100);
            lifecycleHandler.postDelayed(this::dismissLoadingDialog, 1000);
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }

    }
    ////////////////////////////////////////////////////////////////////////////

    public void intent() {
        Bundle bundle = getIntent().getExtras();
        IsSent = bundle == null ? "" : safeString(bundle.getString("IsSent"), "");
        signature = bundle == null ? "1" : safeString(bundle.getString("signature"), "1");

    }

    public void Config() {

        callMethod = new CallMethod(this);
        ocr_dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));

        factor_header_recycler = findViewById(R.id.ocr_localfactor_a_recyclerView);
        fab = findViewById(R.id.ocr_localfactor_a_fab);
        //fab.setVisibility(View.VISIBLE);

        textView_Count = findViewById(R.id.ocr_localfactor_a_count);
        toolbar = findViewById(R.id.ocr_localfactor_a_toolbar);
        edtsearch = findViewById(R.id.ocr_localfactor_a_edtsearch);
        mySwitch_activestack = findViewById(R.id.ocr_localfactor_a_switch);

        setSupportActionBar(toolbar);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;

    }

    public void init(){

        SearchTarget = callMethod.ReadString("Last_search");

        factors = ocr_dbh.factorscan(IsSent, SearchTarget, signature);

        edtsearch.setText(callMethod.ReadString("Last_search"));

        fab.setOnClickListener(v -> {
            Multi_barcode.clear();
            for (String[] s : Multi_sign) {
                if (s != null && s.length > 0) Multi_barcode.add(s[0]);
            }
            if (Multi_barcode.isEmpty()) return;
            intent = new Intent(this, Ocr_PaintActivity.class);
            intent.putExtra("ScanResponse", "Multi_sign");
            intent.putExtra("FactorImage", "hasimage");
            intent.putExtra("Width", String.valueOf(width));
            intent.putStringArrayListExtra("list", Multi_barcode);

            startActivity(intent);
            finish();
        });


        mySwitch_activestack.setOnCheckedChangeListener((compoundButton, b) -> {
            //grid
            if (b) {
                signature = "1";
                mySwitch_activestack.setText("بدون امضا");

            } else {
                signature = "0";
                mySwitch_activestack.setText("همه");

            }
            factors = ocr_dbh.factorscan(IsSent, SearchTarget, signature);
            ocr_factorListLocal_adapter = new Ocr_FactorListLocal_Adapter(factors, this, width);
            if (ocr_factorListLocal_adapter.getItemCount() == 0) {
                callMethod.showToast("فاکتوری یافت نشد");
            }
            textView_Count.setText(NumberFunctions.PerisanNumber(String.valueOf(ocr_factorListLocal_adapter.getItemCount())));
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);//grid
            factor_header_recycler.setLayoutManager(gridLayoutManager);
            factor_header_recycler.setAdapter(ocr_factorListLocal_adapter);
            factor_header_recycler.setItemAnimator(new DefaultItemAnimator());
        });


        edtsearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(final Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {

                            SearchTarget = NumberFunctions.EnglishNumber(ocr_dbh.GetRegionText(editable.toString()));
                            SearchTarget=SearchTarget.replace(" ","%");
                            callMethod.EditString("Last_search", SearchTarget);
                            factors = ocr_dbh.factorscan(IsSent, SearchTarget, signature);

                            ocr_factorListLocal_adapter = new Ocr_FactorListLocal_Adapter(factors, Ocr_FactorListLocalActivity.this, width);
                            if (ocr_factorListLocal_adapter.getItemCount() == 0) {
                                callMethod.showToast("فاکتوری یافت نشد");
                            }
                            textView_Count.setText(NumberFunctions.PerisanNumber(String.valueOf(ocr_factorListLocal_adapter.getItemCount())));
                            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);//grid
                            factor_header_recycler.setLayoutManager(gridLayoutManager);
                            factor_header_recycler.setAdapter(ocr_factorListLocal_adapter);
                            factor_header_recycler.setItemAnimator(new DefaultItemAnimator());


                        }, 1000);

                        handler.postDelayed(() -> edtsearch.selectAll(), 5000);
                    }
                });


        ocr_factorListLocal_adapter = new Ocr_FactorListLocal_Adapter(factors, this, width);
        if (ocr_factorListLocal_adapter.getItemCount() == 0) {
            callMethod.showToast("فاکتوری یافت نشد");
        }
        textView_Count.setText(NumberFunctions.PerisanNumber(String.valueOf(ocr_factorListLocal_adapter.getItemCount())));
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);//grid
        factor_header_recycler.setLayoutManager(gridLayoutManager);
        factor_header_recycler.setAdapter(ocr_factorListLocal_adapter);
        factor_header_recycler.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item_multi = menu;
        getMenuInflater().inflate(R.menu.ocr_options_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.ocr_menu_multi) {
            item_multi.findItem(R.id.ocr_menu_multi).setVisible(false);
            for (Factor factor : factors) {
                factor.setCheck(false);
            }
            Multi_sign.clear();
            ocr_factorListLocal_adapter.multi_select = false;

            ocr_factorListLocal_adapter = new Ocr_FactorListLocal_Adapter(factors, this, width);
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);//grid
            factor_header_recycler.setLayoutManager(gridLayoutManager);
            factor_header_recycler.setAdapter(ocr_factorListLocal_adapter);
            factor_header_recycler.setItemAnimator(new DefaultItemAnimator());
            fab.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void factor_select_function(String Factor_barcode, String Customer_code, int flag) {
        if (flag == 1) {
            OcrFactorSelectionPolicy.addIfAbsent(Multi_sign, Factor_barcode, Customer_code);

        } else {
            OcrFactorSelectionPolicy.removeByBarcode(Multi_sign, Factor_barcode);
        }

        boolean hasSelection = !Multi_sign.isEmpty();
        fab.setVisibility(hasSelection ? View.VISIBLE : View.GONE);
        if (!hasSelection && ocr_factorListLocal_adapter != null) {
            ocr_factorListLocal_adapter.multi_select = false;
        }
        if (item_multi != null && item_multi.findItem(R.id.ocr_menu_multi) != null) {
            item_multi.findItem(R.id.ocr_menu_multi).setVisible(hasSelection);
        }
    }

    private static String safeString(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private void dismissLoadingDialog() {
        if (dialog1 != null && dialog1.isShowing() && !isFinishing()) {
            dialog1.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        lifecycleHandler.removeCallbacksAndMessages(null);
        if (dialog1 != null && dialog1.isShowing()) {
            dialog1.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        intent = new Intent(this, Ocr_FactorListLocalActivity.class);
        intent.putExtra("IsSent", IsSent);
        intent.putExtra("signature", signature);
        startActivity(intent);
        finish();

    }
}

