package com.kits.kowsarapp.activity.order;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;
import com.kits.kowsarapp.adapter.order.Order_GoodBasketAdapter;
import com.kits.kowsarapp.adapter.order.Order_InternetConnection;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.order.Order_Action;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_BasketActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Order_APIInterface apiInterface;
    CallMethod callMethod;
    TextView Buy_row, Buy_amount;
    Intent intent;
    Order_GoodBasketAdapter adapter;
    Order_Action order_action;
    ArrayList<Good> goods = new ArrayList<>();
    Button total_delete;
    Button final_buy_test;
    LottieAnimationView prog;
    LottieAnimationView img_lottiestatus;
    TextView tv_lottiestatus;
    String State = "0";

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

//***********************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_basket);


        Order_InternetConnection ic = new Order_InternetConnection(this);
        if (ic.has()) {
            try {
                init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            intent = new Intent(this, Base_SplashActivity.class);
            startActivity(intent);
            finish();
        }


    }

    public void init() {


        callMethod = new CallMethod(Order_BasketActivity.this);
        order_action = new Order_Action(Order_BasketActivity.this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);

        CoordinatorLayout ll_activity = findViewById(R.id.order_basket_activity);
        if (callMethod.ReadString("LANG").equals("fa")) {
            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }


        Buy_row = findViewById(R.id.ord_basket_a_total_row_buy);
        Buy_amount = findViewById(R.id.ord_basket_a_total_amount_buy);
        total_delete = findViewById(R.id.ord_basket_a_total_delete);
        final_buy_test = findViewById(R.id.ord_basket_a_test);
        recyclerView = findViewById(R.id.ord_basket_a_R1);

        prog = findViewById(R.id.ord_basket_a_prog);
        img_lottiestatus = findViewById(R.id.ord_basket_a_lottie);
        tv_lottiestatus = findViewById(R.id.ord_basket_a_tvstatus);

        Toolbar toolbar = findViewById(R.id.ord_basket_a_toolbar);

        toolbar.setTitle(callMethod.NumberRegion(getString(R.string.textvalue_order) + callMethod.ReadString("RstMizName")));

        setSupportActionBar(toolbar);


        goods.clear();
        prog.setVisibility(View.VISIBLE);
        img_lottiestatus.setVisibility(View.GONE);
        tv_lottiestatus.setVisibility(View.GONE);


        GetOrder();

        final_buy_test.setOnClickListener(view -> {

            if (State.equals("4")) {

               // print.GetHeader_Data("");

            } else {


                order_action.BasketInfoExplainBeforOrder();



            }
        });




        total_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Order_BasketActivity.this, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage(R.string.textvalue_freetablemessage);

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {

                Call<RetrofitResponse> call1 = apiInterface.OrderDeleteAll("OrderDeleteAll", callMethod.ReadString("AppBasketInfoCode")

                );
                call1.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call1, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (response.body().getText().equals("Done")) {
                                callMethod.showToast(getString(R.string.textvalue_deleteorderbasket));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call1, @NotNull Throwable t) {
                    }
                });
            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                // code to handle negative button click
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });
    }

    private void GetOrder() {
        Call<RetrofitResponse> call = apiInterface.OrderGet("OrderGet", callMethod.ReadString("AppBasketInfoCode"), "3");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    goods = response.body().getGoods();
                    callrecycler();
                    prog.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                prog.setVisibility(View.GONE);
                goods.clear();
                callrecycler();
            }
        });
    }


    private void callrecycler() {

        adapter = new Order_GoodBasketAdapter(goods, this);

        if (adapter.getItemCount() == 0) {
            tv_lottiestatus.setText(R.string.textvalue_notfound);
            img_lottiestatus.setVisibility(View.VISIBLE);
            tv_lottiestatus.setVisibility(View.VISIBLE);
        } else {
            for (Good good : goods) {
                if (good.getFactorCode().equals("0")) {
                    final_buy_test.setVisibility(View.VISIBLE);
                    total_delete.setVisibility(View.VISIBLE);
                }
            }
            img_lottiestatus.setVisibility(View.GONE);
            tv_lottiestatus.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void RefreshState() {
        GetOrder();
        Call<RetrofitResponse> call2 = apiInterface.GetOrderSum("GetOrderSum", callMethod.ReadString("AppBasketInfoCode"));
        call2.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    State = response.body().getGoods().get(0).getInfoState();

                    Buy_row.setText(callMethod.NumberRegion(response.body().getGoods().get(0).getCountGood()));
                    Buy_amount.setText(callMethod.NumberRegion(response.body().getGoods().get(0).getSumFacAmount()));
                    if (State.equals("4")) {
                        final_buy_test.setText(R.string.textvalue_setreserveorder);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                goods.clear();
                callrecycler();
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        RefreshState();
        super.onWindowFocusChanged(hasFocus);
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
}
