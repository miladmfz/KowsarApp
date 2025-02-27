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
import com.kits.kowsarapp.application.order.Order_Print;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.order.Order_BasketInfo;
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
    Order_BasketInfo basketInfo;

    CallMethod callMethod;
    TextView Buy_row, Buy_amount,tv_totalprice,tv_notresive,tv_resive;

    Intent intent;
    Order_GoodBasketAdapter adapter;
    Order_Action order_action;
    ArrayList<Good> goods = new ArrayList<>();
    Button total_delete;
    Button btn_ordertofactor,btn_peyment;

    LottieAnimationView prog;
    LottieAnimationView img_lottiestatus;
    TextView tv_lottiestatus;
    String State = "0";
    Order_Print order_print;


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
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.DefaultTheme));


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
        order_print = new Order_Print(Order_BasketActivity.this);

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
        btn_ordertofactor = findViewById(R.id.ord_basket_a_ordertofactor);
        recyclerView = findViewById(R.id.ord_basket_a_R1);

        prog = findViewById(R.id.ord_basket_a_prog);
        img_lottiestatus = findViewById(R.id.ord_basket_a_lottie);
        tv_lottiestatus = findViewById(R.id.ord_basket_a_tvstatus);



        tv_totalprice = findViewById(R.id.ord_basket_a_total_price);
        tv_notresive = findViewById(R.id.ord_basket_a_total_notresive);
        tv_resive = findViewById(R.id.ord_basket_a_total_resive);
        btn_peyment  = findViewById(R.id.ord_basket_a_payment);






        Toolbar toolbar = findViewById(R.id.ord_basket_a_toolbar);

        toolbar.setTitle(callMethod.NumberRegion(getString(R.string.textvalue_order) + callMethod.ReadString("RstMizName")));

        setSupportActionBar(toolbar);


        goods.clear();
        prog.setVisibility(View.VISIBLE);
        img_lottiestatus.setVisibility(View.GONE);
        tv_lottiestatus.setVisibility(View.GONE);


        GetOrder();

        btn_ordertofactor.setOnClickListener(view -> {
            if (State.equals("4")) {
               order_print.GetHeader_Data("");
            } else {
                order_action.BasketInfoExplainBeforOrder();
            }
        });


        btn_peyment.setOnClickListener(view -> {

            order_action.BasketInfopayment(basketInfo);
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
    public void setupbasketview(){
        State = basketInfo.getInfoState();
        Buy_row.setText(callMethod.NumberRegion(basketInfo.getCountGood()));
        Buy_amount.setText(callMethod.NumberRegion(basketInfo.getSumFacAmount()));

        tv_totalprice.setText(callMethod.NumberRegion(String.valueOf(Integer.parseInt(basketInfo.getSumPrice())+Integer.parseInt(basketInfo.getSumTaxAndMayor()))));
        tv_notresive.setText(callMethod.NumberRegion(basketInfo.getNotReceived()));
        tv_resive.setText(callMethod.NumberRegion(basketInfo.getReceived()));

        if (Integer.parseInt(basketInfo.getFactorCode())>0){
            if (Integer.parseInt(basketInfo.getNotReceived())>0){
                if (callMethod.ReadBoolan("PaymentWithDevice")) {
                    btn_peyment.setVisibility(View.VISIBLE);
                }else{
                    btn_peyment.setVisibility(View.GONE);
                }
            }else{
                btn_peyment.setVisibility(View.GONE);
            }
        }else{
            btn_peyment.setVisibility(View.GONE);
        }


        if (State.equals("4")) {
            btn_ordertofactor.setText(R.string.textvalue_setreserveorder);
        }
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
                    btn_ordertofactor.setVisibility(View.VISIBLE);
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
        //Call<RetrofitResponse> call2 = apiInterface.GetOrderSum("GetOrderSum", callMethod.ReadString("AppBasketInfoCode"));
        Call<RetrofitResponse> call2 = apiInterface.OrderGetSummmary("OrderGetSummmary", callMethod.ReadString("AppBasketInfoCode"));

        call2.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    basketInfo = response.body().getBasketInfos().get(0);
                    setupbasketview();
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
