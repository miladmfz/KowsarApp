package com.kits.kowsarapp.activity.search;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import com.kits.kowsarapp.adapter.search.Search_GoodAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.SearchActivitySearchBinding;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.search.Search_DBH;
import com.kits.kowsarapp.model.search.Search_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.search.Search_APIInterface;


import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search_SearchActivity extends AppCompatActivity {


    Search_GoodAdapter adapter;

    public ArrayList<Search_Good> search_goods = new ArrayList<>();
    public String id = "0";
    public String title = "";
    public String AutoSearch = "";
    Dialog dialog1;
    Handler handler;
    private int backPressCount = 0;
    GridLayoutManager gridLayoutManager;

    Search_DBH dbh;
    Search_APIInterface search_apiInterface;

    CallMethod callMethod;
    SearchActivitySearchBinding binding;
    private Integer grid;
    private Handler keyboardHandler = new Handler();

    private boolean loading = true;


    private Runnable keyboardRunnable = () -> {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.seaSearchAEdtsearch.getWindowToken(),
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersiveMode(); // فعالسازی حالت Immersive


        binding = SearchActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.broker_spinner_box);
        TextView repw = dialog1.findViewById(R.id.b_spinner_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        Config();


        try {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (dbh.GetColumnscount().equals("0")) {
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



        binding.seaSearchAEdtsearch.setOnLongClickListener(v -> {
            binding.seaSearchAEdtsearch.selectAll();
            return false;
        });


        binding.seaSearchAEdtsearch.setFocusable(true);
        binding.seaSearchAEdtsearch.requestFocus();

        binding.seaSearchAEdtsearch.addTextChangedListener(
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
                            GetDataFromDataBase();
                            binding.seaSearchAEdtsearch.setFocusable(true);
                            binding.seaSearchAEdtsearch.requestFocus();
                            binding.seaSearchAEdtsearch.selectAll();

                            if (callMethod.ReadBoolan("keyboardRunnable")) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(binding.seaSearchAEdtsearch, InputMethodManager.SHOW_IMPLICIT);


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
        dbh = new Search_DBH(this, callMethod.ReadString("DatabaseName"));
        search_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Search_APIInterface.class);


        handler = new Handler();
        grid = Integer.parseInt(callMethod.ReadString("Grid"));

    }

    public void GetDataFromDataBase() {
        search_goods.clear();

        loading = true;
        allgood();



    }



    public void allgood() {

        String Body_str  = "";
        Body_str =callMethod.CreateJson("SearchTarget", AutoSearch, Body_str);
        Call<RetrofitResponse> call = search_apiInterface.GetGoodList(callMethod.RetrofitBody(Body_str));

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    loading = false;
                    search_goods = response.body().getSearch_Goods();
                    adapter = new Search_GoodAdapter(search_goods, Search_SearchActivity.this);

                    CallRecyclerView();


                }
            }
            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                loading = false;

                Toast toast = Toast.makeText(Search_SearchActivity.this, "کالایی یافت نشد", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 10, 10);
                toast.show();
            }
        });



    }


    @SuppressLint("NotifyDataSetChanged")
    public void CallRecyclerView() {
        //adapter.notifyDataSetChanged();


        if (adapter.getItemCount() == 0) {
            binding.seaSearchATvstatus.setText("کالایی یافت نشد");
            binding.seaSearchATvstatus.setVisibility(View.VISIBLE);
            binding.seaSearchALottie.setVisibility(View.VISIBLE);
        } else {
            binding.seaSearchALottie.setVisibility(View.GONE);
            binding.seaSearchATvstatus.setVisibility(View.GONE);
        }
        gridLayoutManager = new GridLayoutManager(this, grid);
        binding.seaSearchAAllgood.setLayoutManager(gridLayoutManager);
        binding.seaSearchAAllgood.setAdapter(adapter);
        binding.seaSearchAAllgood.setItemAnimator(new DefaultItemAnimator());
        binding.seaSearchAProg.setVisibility(View.GONE);
    }



    public void showPasswordDialog() {

        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        backPressCount++;
        if (backPressCount >= 3) {
            // نمایش دیالوگ رمز
            showPasswordDialog();
            backPressCount = 0; // بازنشانی شمارنده
        } else {

        }
        new Handler().postDelayed(() -> backPressCount = 0, 2000);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (backPressCount < 3) {
//            Intent intent = new Intent(this, SearchActivity.class);
//            finish();
//            startActivity(intent);
//        }
//
//    }




}