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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;
import com.kits.kowsarapp.adapter.order.Order_GoodBasketAdapter;
import com.kits.kowsarapp.adapter.order.Order_InternetConnection;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ThirdPartyResult;
import com.kits.kowsarapp.application.order.Order_Action;
import com.kits.kowsarapp.application.order.Order_NetworkFailure;
import com.kits.kowsarapp.application.order.Order_Payment;
import com.kits.kowsarapp.application.order.Order_Print;
import com.kits.kowsarapp.application.order.Order_ValueParser;
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
    Order_APIInterface order_apiInterface;
    Order_BasketInfo order_basketInfo;

    CallMethod callMethod;
    TextView Buy_row, Buy_amount,tv_totalprice,tv_notresive,tv_resive;

    Intent intent;
    Order_GoodBasketAdapter order_goodBasketAdapter;
    Order_Action order_action;
    Order_Payment order_payment;
    ArrayList<Good> goods = new ArrayList<>();
    Button total_delete;
    Button btn_ordertofactor,btn_peyment;

    LottieAnimationView prog;
    LottieAnimationView img_lottiestatus;
    TextView tv_lottiestatus;
    String State = "0";
    Order_Print order_print;
    private Call<RetrofitResponse> orderCall;
    private Call<RetrofitResponse> summaryCall;
    private Call<RetrofitResponse> deleteCall;
    private boolean orderLoading;
    private boolean summaryLoading;
    private boolean deleteInProgress;


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

        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));


        setContentView(R.layout.order_activity_basket);


        Order_InternetConnection ic = new Order_InternetConnection(this);
        if (ic.has()) {
            try {
                init();
            } catch (Exception e) {
                callMethod.Log("Order basket initialization failed: "
                        + e.getClass().getSimpleName());
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
        order_payment = new Order_Payment(Order_BasketActivity.this);
        order_print = new Order_Print(Order_BasketActivity.this);

        order_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);

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
        btn_peyment.setEnabled(false);






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
            if (order_basketInfo != null) {
                order_payment.BasketInfopayment(order_basketInfo);
            }
        });


        total_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Order_BasketActivity.this, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage(R.string.textvalue_freetablemessage);

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {

                if (deleteInProgress) return;
                deleteInProgress = true;
                total_delete.setEnabled(false);

                try {
                    deleteCall = order_apiInterface.OrderDeleteAll("OrderDeleteAll", callMethod.ReadString("AppBasketInfoCode")

                );
                    deleteCall.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call1, @NotNull Response<RetrofitResponse> response) {
                        deleteInProgress = false;
                        deleteCall = null;
                        if (!canUpdateUi()) return;
                        total_delete.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null
                                && "Done".equals(response.body().getText())) {
                            callMethod.showToast(getString(R.string.textvalue_deleteorderbasket));
                            finish();
                        } else {
                            callMethod.showToast("حذف سفارش توسط سرور تأیید نشد.");
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call1, @NotNull Throwable t) {
                        deleteInProgress = false;
                        deleteCall = null;
                        if (!canUpdateUi() || call1.isCanceled()) return;
                        total_delete.setEnabled(true);
                        Order_NetworkFailure.show(Order_BasketActivity.this, callMethod,
                                "OrderDeleteAll", call1, t);
                    }
                    });
                } catch (RuntimeException exception) {
                    deleteInProgress = false;
                    deleteCall = null;
                    total_delete.setEnabled(true);
                    callMethod.Log("OrderDeleteAll failed before enqueue: "
                            + (exception.getMessage() == null ? "" : exception.getMessage()));
                    callMethod.showToast("شروع حذف سفارش ناموفق بود.");
                }
            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                // code to handle negative button click
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });
    }

    private void GetOrder() {
        if (orderLoading) return;
        orderLoading = true;
        try {
            orderCall = order_apiInterface.OrderGet(
                    "OrderGet", callMethod.ReadString("AppBasketInfoCode"), "3");
            orderCall.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                orderLoading = false;
                orderCall = null;
                if (!canUpdateUi()) return;
                prog.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null
                        || response.body().getGoods() == null) {
                    callMethod.showToast("پاسخ اقلام سفارش نامعتبر است.");
                    return;
                }
                goods = new ArrayList<>(response.body().getGoods());
                callrecycler();
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                orderLoading = false;
                orderCall = null;
                if (!call.isCanceled() && canUpdateUi()) {
                    prog.setVisibility(View.GONE);
                    callMethod.Log("OrderGet failed: "
                            + (t.getMessage() == null ? "" : t.getMessage()));
                }
            }
            });
        } catch (RuntimeException exception) {
            orderLoading = false;
            orderCall = null;
            if (canUpdateUi()) prog.setVisibility(View.GONE);
            callMethod.Log("OrderGet failed before enqueue: "
                    + (exception.getMessage() == null ? "" : exception.getMessage()));
        }
    }
    public void setupbasketview(){
        if (order_basketInfo == null) return;
        State = order_basketInfo.getInfoState() == null
                ? "0"
                : order_basketInfo.getInfoState();
        Buy_row.setText(callMethod.NumberRegion(order_basketInfo.getCountGood()));
        Buy_amount.setText(callMethod.NumberRegion(order_basketInfo.getSumFacAmount()));

        long totalPrice = Order_ValueParser.addOrDefault(
                Order_ValueParser.longOrDefault(order_basketInfo.getSumPrice(), 0),
                Order_ValueParser.longOrDefault(order_basketInfo.getSumTaxAndMayor(), 0),
                0);
        tv_totalprice.setText(callMethod.NumberRegion(String.valueOf(totalPrice)));
        tv_notresive.setText(callMethod.NumberRegion(order_basketInfo.getNotReceived()));
        tv_resive.setText(callMethod.NumberRegion(order_basketInfo.getReceived()));

        if (Order_ValueParser.longOrDefault(order_basketInfo.getFactorCode(), 0) > 0){
            if (Order_ValueParser.longOrDefault(order_basketInfo.getNotReceived(), 0) > 0){
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
        btn_peyment.setEnabled(true);
    }


    private void callrecycler() {

        if (goods == null) {
            goods = new ArrayList<>();
        }

        order_goodBasketAdapter = new Order_GoodBasketAdapter(goods, this);

        if (order_goodBasketAdapter.getItemCount() == 0) {
            btn_ordertofactor.setVisibility(View.GONE);
            total_delete.setVisibility(View.GONE);
            tv_lottiestatus.setText(R.string.textvalue_notfound);
            img_lottiestatus.setVisibility(View.VISIBLE);
            tv_lottiestatus.setVisibility(View.VISIBLE);
        } else {
            boolean hasPendingRow = false;
            boolean hasPrintedRow = false;
            for (Good good : goods) {
                if ("0".equals(good.getFactorCode())) {
                    hasPendingRow = true;
                } else {
                    hasPrintedRow = true;
                }
            }

            btn_ordertofactor.setVisibility(hasPendingRow ? View.VISIBLE : View.GONE);

            // Never use the legacy OrderDeleteAll when the basket contains rows
            // that have already been printed/factored. Printed history must only
            // be changed through an audited adjustment.
            total_delete.setVisibility(hasPendingRow && !hasPrintedRow
                    ? View.VISIBLE
                    : View.GONE);

            img_lottiestatus.setVisibility(View.GONE);
            tv_lottiestatus.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(order_goodBasketAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void RefreshState() {
        GetOrder();
        if (summaryLoading) return;
        summaryLoading = true;
        //Call<RetrofitResponse> call2 = apiInterface.GetOrderSum("GetOrderSum", callMethod.ReadString("AppBasketInfoCode"));
        try {
            summaryCall = order_apiInterface.OrderGetSummmary(
                    "OrderGetSummmary", callMethod.ReadString("AppBasketInfoCode"));

            summaryCall.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                summaryLoading = false;
                summaryCall = null;
                if (!canUpdateUi()) return;
                if (!response.isSuccessful() || response.body() == null ||
                        response.body().getBasketInfos() == null ||
                        response.body().getBasketInfos().isEmpty()) {
                    callMethod.showToast("خلاصه سفارش دریافت نشد.");
                    return;
                }
                order_basketInfo = response.body().getBasketInfos().get(0);
                setupbasketview();
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                summaryLoading = false;
                summaryCall = null;
                if (!call.isCanceled() && canUpdateUi()) {
                    callMethod.Log("OrderGetSummmary failed: "
                            + (t.getMessage() == null ? "" : t.getMessage()));
                }
            }
            });
        } catch (RuntimeException exception) {
            summaryLoading = false;
            summaryCall = null;
            callMethod.Log("OrderGetSummmary failed before enqueue: "
                    + (exception.getMessage() == null ? "" : exception.getMessage()));
        }

    }
    ThirdPartyResult BehPardakht_pos_result = new ThirdPartyResult();

    private static final int REQUEST_POS = 9001;
    private final Gson gson = new Gson();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_POS) return;

        String resultJson = (data == null) ? null : data.getStringExtra("paymentResult");
        BehPardakht_pos_result = null;
        try {
            if (resultJson != null && !resultJson.trim().isEmpty()) {
                BehPardakht_pos_result = gson.fromJson(resultJson, ThirdPartyResult.class);
            }
        } catch (Exception exception) {
            // Never log the raw POS payload or extras; they may contain payment data.
            callMethod.Log("POS result parse failed: "
                    + exception.getClass().getSimpleName());
        }


        if (BehPardakht_pos_result != null &&
                "000".equals(BehPardakht_pos_result.resultCode)) {
            order_payment.BasketInfopayment_request(BehPardakht_pos_result,resultJson);

        }else{
            order_payment.rejectPosResult(BehPardakht_pos_result == null);
        }




    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && order_apiInterface != null) {
            RefreshState();
        }
    }

    private boolean canUpdateUi() {
        return !isFinishing()
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1
                || !isDestroyed());
    }

    @Override
    protected void onDestroy() {
        if (orderCall != null) orderCall.cancel();
        if (summaryCall != null) summaryCall.cancel();
        if (deleteCall != null) deleteCall.cancel();
        if (order_action != null) order_action.cancelPending();
        if (order_payment != null) order_payment.cancelPending();
        super.onDestroy();
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
