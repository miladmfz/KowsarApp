package com.kits.kowsarapp.activity.find;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.find.Find_GoodAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.FindActivitySearchBinding;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.find.Find_DBH;
import com.kits.kowsarapp.model.find.Find_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.find.Find_APIInterface;


import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Find_SearchActivity extends AppCompatActivity {


    Find_GoodAdapter find_goodAdapter;
    CallMethod callMethod;
    private final Handler keyboardHandler = new Handler();
    Dialog dialog1;
    Handler handler;
    GridLayoutManager gridLayoutManager;

    Find_DBH find_dbh;
    Find_APIInterface find_apiInterface;


    public ArrayList<Find_Good> find_goods = new ArrayList<>();
    private int backPressCount = 0;

    public String id = "0";
    public String title = "";
    public String AutoSearch = "";
    private Integer grid;

    FindActivitySearchBinding binding;

    private final Runnable keyboardRunnable = () -> {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.findSearchAEdtsearch.getWindowToken(),
                0
        );
    };

    private void enableImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        AutoSearch = data.getString("scan");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        intent();
        Config();

        if (callMethod.ReadBoolan("ShowInFullPage")){
            enableImmersiveMode(); // فعالسازی حالت Immersive
        }


        binding = FindActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.broker_spinner_box);
        TextView repw = dialog1.findViewById(R.id.b_spinner_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();



        try {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (find_dbh.GetColumnscount().equals("0")) {
                    callMethod.showToast("تنظیم جدول از سمت دیتابیس مشکل دارد");
                    finish();
                    dialog1.dismiss();
                } else {
                    init();
                }
            }, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        } catch (Exception e) {
        }



    }

    public void init() {



        binding.findSearchAEdtsearch.setOnLongClickListener(v -> {
            binding.findSearchAEdtsearch.selectAll();
            return false;
        });

        binding.findSearchABtnscan.setOnClickListener(view -> {
            Intent intent = new Intent(this, Find_ScanCodeActivity.class);
            startActivity(intent);
            finish();
        });
        binding.findSearchAEdtsearch.setFocusable(true);
        binding.findSearchAEdtsearch.requestFocus();

        binding.findSearchAEdtsearch.addTextChangedListener(
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

                            AutoSearch = NumberFunctions.EnglishNumber(editable.toString());
                            AutoSearch.replace(" ","%");
                            allgood();

                            if(callMethod.ReadBoolan("SelectAllAfterSearch")){
                                binding.findSearchAEdtsearch.setFocusable(true);
                                binding.findSearchAEdtsearch.requestFocus();
                                binding.findSearchAEdtsearch.selectAll();
                            }

                            if (callMethod.ReadBoolan("keyboardRunnable")) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(binding.findSearchAEdtsearch, InputMethodManager.SHOW_IMPLICIT);
                                keyboardHandler.removeCallbacks(keyboardRunnable);
                                keyboardHandler.postDelayed(keyboardRunnable,
                                        200
                                );
                            }
                        }, Integer.parseInt(callMethod.ReadString("Delay")));
                    }
                });
        allgood();
    }

    public void Config() {

        callMethod = new CallMethod(this);
        find_dbh = new Find_DBH(this, callMethod.ReadString("DatabaseName"));
        find_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Find_APIInterface.class);


        handler = new Handler();
        grid = Integer.parseInt(callMethod.ReadString("Grid"));

    }

    public void allgood() {

//        String Body_str  = "";
//        Body_str =callMethod.CreateJson("SearchTarget", AutoSearch, Body_str);
//        Call<RetrofitResponse> call = find_apiInterface.GetGoodList(callMethod.RetrofitBody(Body_str));
//

        find_goods.clear();

        binding.findSearchAProg.setVisibility(View.VISIBLE);
        Call<RetrofitResponse> call = find_apiInterface.GetGoodList ("GetFindGoodList", AutoSearch);
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    find_goods = response.body().getFind_Goods();
                    find_goodAdapter = new Find_GoodAdapter(find_goods, Find_SearchActivity.this);

                    CallRecyclerView();


                }
            }
            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {

                find_goods.clear();
                find_goodAdapter = new Find_GoodAdapter(find_goods, Find_SearchActivity.this);
                CallRecyclerView();
            }
        });



    }

    @SuppressLint("NotifyDataSetChanged")
    public void refresh() {
        find_goodAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void CallRecyclerView() {
        //adapter.notifyDataSetChanged();


        if (find_goodAdapter.getItemCount() == 0) {
            binding.findSearchATvstatus.setText("کالایی یافت نشد");
            binding.findSearchATvstatus.setVisibility(View.VISIBLE);
            binding.findSearchALottie.setVisibility(View.VISIBLE);
        } else {
            binding.findSearchALottie.setVisibility(View.GONE);
            binding.findSearchATvstatus.setVisibility(View.GONE);
        }
        gridLayoutManager = new GridLayoutManager(this, grid);
        binding.findSearchAAllgood.setLayoutManager(gridLayoutManager);
        binding.findSearchAAllgood.setAdapter(find_goodAdapter);
        binding.findSearchAAllgood.setItemAnimator(new DefaultItemAnimator());
        binding.findSearchAProg.setVisibility(View.GONE);
    }



    public void showPasswordDialog() {

        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.default_loginconfig);
        EditText ed_password = dialog.findViewById(R.id.d_loginconfig_ed);
        MaterialButton btn_login = dialog.findViewById(R.id.d_loginconfig_btn);



        ed_password.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(final Editable editable) {

                        if(NumberFunctions.EnglishNumber(ed_password.getText().toString()).length()>5) {
                            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {

                                finish();
                            } else {
                                callMethod.showToast("رمز عبور صیحیح نیست");
                            }

                        }
                    }
                });

        btn_login.setOnClickListener(v -> {

            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {

                finish();
            }else {
                callMethod.showToast("رمز عبور صیحیح نیست");
            }


        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (callMethod.ReadBoolan("LockSearchPage")){
        backPressCount++;
        if (backPressCount >= 3) {
            // نمایش دیالوگ رمز
            showPasswordDialog();
            backPressCount = 0; // بازنشانی شمارنده
        } else {

        }
        new Handler().postDelayed(() -> backPressCount = 0, 2000);
        }else{
            finish();
        }
    }




}