package com.kits.kowsarapp.application.order;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.order.Order_BasketActivity;
import com.kits.kowsarapp.activity.order.Order_RegistrationActivity;
import com.kits.kowsarapp.activity.order.Order_SearchActivity;
import com.kits.kowsarapp.activity.order.Order_TableActivity;
import com.kits.kowsarapp.adapter.order.Order_GoodBoxItemAdapter;
import com.kits.kowsarapp.adapter.order.Order_ReserveAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ThirdPartyRequest;
import com.kits.kowsarapp.application.base.ThirdPartyResult;
import com.kits.kowsarapp.model.base.DistinctValue;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.ObjectType;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.order.Order_BasketInfo;
import com.kits.kowsarapp.model.order.Order_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_Action extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    ThirdPartyRequest BehPardakht_pos_request = new ThirdPartyRequest();
    ThirdPartyResult BehPardakht_pos_result = new ThirdPartyResult();
    Order_BasketInfo BehPardakht_basketInfo=new Order_BasketInfo();
    private final Context mContext;
    CallMethod callMethod;
    Intent intent;
    Dialog dialog, dialogProg,dialog_payment;
    PersianCalendar persianCalendar;
    Calendar cldr;
    TimePickerDialog picker;


    private static final int REQUEST_POS = 9001;
    private final Gson gson = new Gson();


    Order_Print order_print;
    Order_DBH order_dbh;
    Order_APIInterface order_apiInterface;

    ArrayList<DistinctValue> values = new ArrayList<>();
    ArrayList<String> values_array = new ArrayList<>();
    ArrayList<Good> Goods= new ArrayList<>();
    ArrayList<Good> good_box_items = new ArrayList<>();
    ArrayList<ObjectType> objectTypes = new ArrayList<>();

    public Call<RetrofitResponse> call;

    TextView tv_reservestart;
    TextView tv_reserveend;
    TextView tv_date;
    TextView tv_rep;

    Integer ehour = 0;
    Integer eminutes = 0;
    Integer printerconter ;
    Integer il;

    String date;

    String payment_type="";
    String totalprice="0";
    String payment_mablagh_tosend="0";
    String payment_mablagh_incrise="0";
    String payment_mablagh_decrise="0";

    // Prevent duplicate OrderToFactor calls caused by fast/double taps.
    private boolean orderToFactorInProgress = false;
    private boolean adjustmentInProgress = false;

    public Order_Action(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);

        this.order_dbh = new Order_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.order_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);
        this.dialogProg = new Dialog(mContext);
        this.order_print = new Order_Print(mContext);

        printerconter = 0;

    }

    public void dialogProg() {
        if (!canUpdateUi()) {
            callMethod.Log("Order progress ignored: inactive Activity");
            return;
        }
        dialogProg.setContentView(R.layout.order_spinner_box);
        tv_rep = dialogProg.findViewById(R.id.ord_spinner_text);
        dialogProg.show();
    }

    public void DeleteReserveDialog(Order_BasketInfo basketInfo) {
        dialogProg();
        call = order_apiInterface.OrderInfoReserveDelete(
                "OrderInfoReserveDelete",
                basketInfo.getAppBasketInfoCode()
        );
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (!response.isSuccessful() || response.body() == null || !canUpdateUi()) {
                    safeDismiss(dialogProg, "reserve delete progress");
                    callMethod.Log("OrderInfoReserveDelete returned invalid response");
                    return;
                }

                intent = new Intent(mContext, Order_TableActivity.class);
                intent.putExtra("State", "0");
                intent.putExtra("EditTable", "0");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );

                mContext.startActivity(intent);
                Activity activity = activityOrNull();
                if (activity != null) activity.finish();


            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Order_NetworkFailure.show(mContext, callMethod,
                        "OrderInfoReserveDelete", call, t);
            }
        });


    }
    public void LoginSetting() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.default_loginconfig);
        EditText ed_password = dialog.findViewById(R.id.d_loginconfig_ed);
        MaterialButton btn_login = dialog.findViewById(R.id.d_loginconfig_btn);

        btn_login.setOnClickListener(v -> {
            if (callMethod.ReadString("ActivationCode").equals("444444")){
                Intent intent = new Intent(mContext, Order_RegistrationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                mContext.startActivity(intent);
            }else{
                if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {
                    Intent intent = new Intent(mContext, Order_RegistrationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                    mContext.startActivity(intent);
                }else {
                    callMethod.showToast("رمز عبور صیحیح نیست");
                }
            }
        });
        dialog.show();
    }
    public void ReserveBoxDialog(Order_BasketInfo basketInfo) {

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.order_reserve_box);
        LinearLayout ll_reservebox = dialog.findViewById(R.id.order_reserve_box);
        if (callMethod.ReadString("LANG").equals("fa")) {
            ll_reservebox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            ll_reservebox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            ll_reservebox.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        EditText ed_personname = dialog.findViewById(R.id.ord_reserve_b_personname);
        EditText ed_mobileno = dialog.findViewById(R.id.ord_reserve_b_mobileno);
        EditText ed_explain = dialog.findViewById(R.id.ord_reserve_b_explain);
        tv_reservestart = dialog.findViewById(R.id.ord_reserve_b_reservestart);
        tv_reserveend = dialog.findViewById(R.id.ord_reserve_b_reserveend);
        tv_date = dialog.findViewById(R.id.ord_reserve_b_date);

        TextView tv_rstmizname = dialog.findViewById(R.id.ord_reserve_b_rstmiz);
        TextView tv_showrecycler = dialog.findViewById(R.id.ord_reserve_b_statetv);
        RecyclerView recycler = dialog.findViewById(R.id.ord_reserve_b_recycler);

        Button btn_reserve = dialog.findViewById(R.id.ord_reserve_b_btnsend);


        tv_showrecycler.setText(callMethod.NumberRegion(mContext.getString(R.string.textvalue_tvlistoftable) + basketInfo.getRstMizName()));
        tv_rstmizname.setText(callMethod.NumberRegion(basketInfo.getRstMizName()));


        call = order_apiInterface.OrderReserveList("OrderReserveList", basketInfo.getRstmizCode());
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                if (!response.isSuccessful() || response.body() == null || !canUpdateUi()) {
                    callMethod.Log("OrderReserveList returned invalid response");
                    return;
                }
                ArrayList<Order_BasketInfo> reserveItems = response.body().getBasketInfos();
                if (reserveItems == null) reserveItems = new ArrayList<>();
                Order_ReserveAdapter adapter = new Order_ReserveAdapter(reserveItems, mContext);
                recycler.setLayoutManager(new GridLayoutManager(mContext, 1));
                recycler.setAdapter(adapter);
                recycler.setItemAnimator(new DefaultItemAnimator());
                recycler.setAdapter(adapter);

            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Order_NetworkFailure.show(mContext, callMethod,
                        "OrderReserveList", call, t);

            }
        });


        call = order_apiInterface.GetTodeyFromServer("GetTodeyFromServer");

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (!response.isSuccessful() || response.body() == null
                        || response.body().getText() == null || !canUpdateUi()) {
                    callMethod.Log("GetTodeyFromServer returned invalid response");
                    return;
                }
                date = response.body().getText();
                tv_date.setText(callMethod.NumberRegion(date));
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Order_NetworkFailure.show(mContext, callMethod,
                        "GetTodeyFromServer", call, t);
            }
        });

        tv_reservestart.setOnClickListener(v -> {

            cldr = Calendar.getInstance();
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int minutes = cldr.get(Calendar.MINUTE);
            new TimePickerDialog();
            picker = TimePickerDialog.newInstance((view, hourOfDay, minute) -> {
                String thourOfDay, tminute, Time;
                thourOfDay = "0" + hourOfDay;
                tminute = "0" + minute;
                Time = thourOfDay.substring(thourOfDay.length() - 2) + ":"
                        + tminute.substring(tminute.length() - 2);

                tv_reservestart.setText(callMethod.NumberRegion(Time));
//                call = order_apiInterface.DbSetupvalue(
//                        "DbSetupvalue",
//                        "AppOrder_ValidReserveTime"
//                );
                call = order_apiInterface.kowsar_info(
                        "kowsar_info",
                        "AppOrder_ValidReserveTime"
                );
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        String ehourOfDay = "0", eminute, eTime;
                        if (!response.isSuccessful() || response.body() == null || !canUpdateUi()) {
                            callMethod.Log("Reserve duration returned invalid response");
                            return;
                        }
                        int reserveMinutes = Order_ValueParser.intInRangeOrDefault(
                                response.body().getText(), 0, 60, -1);
                        if (reserveMinutes < 0) {
                            callMethod.Log("Reserve duration is malformed");
                            callMethod.showToast("مدت رزرو معتبر نیست");
                            return;
                        }
                        if (minute + reserveMinutes > 60) {
                            eminute = String.valueOf(minute + reserveMinutes - 60);
                            if ((hourOfDay + 1) > 23) {
                                ehourOfDay = String.valueOf(hourOfDay);
                                eminute = "59";
                            } else {
                                ehourOfDay = String.valueOf(hourOfDay + 1);
                            }
                        } else {
                            eminute = String.valueOf(minute + reserveMinutes);
                        }

                        ehour = Integer.parseInt(ehourOfDay);
                        eminutes = Integer.parseInt(eminute);

                        ehourOfDay = "0" + ehourOfDay;
                        eminute = "0" + eminute;
                        eTime = ehourOfDay.substring(ehourOfDay.length() - 2) + ":"
                                + eminute.substring(eminute.length() - 2);

                        tv_reserveend.setText(callMethod.NumberRegion(eTime));


                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        Order_NetworkFailure.show(mContext, callMethod,
                                "AppOrder_ValidReserveTime", call, t);

                    }
                });


            }, hour, minutes, true);
            Activity activity = activityOrNull();
            if (activity != null) picker.show(activity.getFragmentManager(), "Timepickerdialog");


        });

        tv_reserveend.setOnClickListener(v -> {


            new TimePickerDialog();
            picker = TimePickerDialog.newInstance((view, ehour, eminutes) -> {
                String thourOfDay, tminute, Time;
                thourOfDay = "0" + ehour;
                tminute = "0" + eminutes;
                Time = thourOfDay.substring(thourOfDay.length() - 2) + ":"
                        + tminute.substring(tminute.length() - 2);
                tv_reserveend.setText(callMethod.NumberRegion(Time));


            }, ehour, eminutes, true);
            Activity activity = activityOrNull();
            if (activity != null) picker.show(activity.getFragmentManager(), "Timepickerdialog");


        });

        tv_date.setOnClickListener(v -> {

            PersianCalendar persianCalendar1 = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    persianCalendar1.getPersianYear(),
                    persianCalendar1.getPersianMonth(),
                    persianCalendar1.getPersianDay()
            );
            Activity activity = activityOrNull();
            if (activity != null) datePickerDialog.show(activity.getFragmentManager(), "Datepickerdialog");

        });


        tv_showrecycler.setOnClickListener(v -> {

            if (recycler.getVisibility() == View.GONE) {
                recycler.setVisibility(View.VISIBLE);
            } else {
                recycler.setVisibility(View.GONE);
            }
        });


        btn_reserve.setOnClickListener(v -> {
            dialogProg();
            tv_rep.setText(R.string.textvalue_sendinformation);

//
//            String Body_str  = "";
//
//            Body_str =callMethod.CreateJson("Broker", dbh.ReadConfig("BrokerCode"), Body_str);
//            Body_str =callMethod.CreateJson("Miz", basketInfo.getRstmizCode(), Body_str);
//            Body_str =callMethod.CreateJson("PersonName", NumberFunctions.EnglishNumber(ed_personname.getText().toString()), Body_str);
//            Body_str =callMethod.CreateJson("Mobile", NumberFunctions.EnglishNumber(ed_mobileno.getText().toString()), Body_str);
//            Body_str =callMethod.CreateJson("InfoExplain", NumberFunctions.EnglishNumber(ed_explain.getText().toString()) + mContext.getString(R.string.textvalue_tagreserve), Body_str);
//            Body_str =callMethod.CreateJson("Prepayed", "0", Body_str);
//            Body_str =callMethod.CreateJson("ReserveStartTime", NumberFunctions.EnglishNumber(tv_reservestart.getText().toString()), Body_str);
//            Body_str =callMethod.CreateJson("ReserveEndTime", NumberFunctions.EnglishNumber(tv_reserveend.getText().toString()), Body_str);
//            Body_str =callMethod.CreateJson("Date", NumberFunctions.EnglishNumber(tv_date.getText().toString()), Body_str);
//            Body_str =callMethod.CreateJson("State", "4", Body_str);
//            Body_str =callMethod.CreateJson("InfoCode", "0", Body_str);
//
//
//            Call<RetrofitResponse> call = order_apiInterface.OrderInfoInsert(callMethod.RetrofitBody(Body_str));


            call = order_apiInterface.OrderInfoInsert(
                    "OrderInfoInsert",
                    order_dbh.ReadConfig("BrokerCode"),
                    basketInfo.getRstmizCode(),
                    NumberFunctions.EnglishNumber(ed_personname.getText().toString()),
                    NumberFunctions.EnglishNumber(ed_mobileno.getText().toString()),
                    NumberFunctions.EnglishNumber(ed_explain.getText().toString()) + mContext.getString(R.string.textvalue_tagreserve),
                    "0",
                    NumberFunctions.EnglishNumber(tv_reservestart.getText().toString()),
                    NumberFunctions.EnglishNumber(tv_reserveend.getText().toString()),
                    NumberFunctions.EnglishNumber(tv_date.getText().toString()),
                    "4",
                    "0"
            );


            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    Order_BasketInfo result = firstBasket(response);
                    if (result == null || !canUpdateUi()) {
                        safeDismiss(dialogProg, "reserve insert progress");
                        callMethod.showToast("پاسخ ثبت رزرو معتبر نیست");
                        return;
                    }
                    if (Order_ValueParser.longOrDefault(result.getErrCode(), Long.MAX_VALUE) > 0) {
                        callMethod.showToast(safeString(result.getErrDesc()));
                        dialogProg.dismiss();
                    } else {
                        dialog.dismiss();
                        dialogProg.dismiss();
                        if (mContext instanceof Order_TableActivity) {
                            ((Order_TableActivity) mContext).CallSpinner();
                        }
                        lottieok();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Order_NetworkFailure.show(mContext, callMethod,
                            "OrderInfoInsert reserve", call, t);
                }
            });

        });

        dialog.show();

    }


    public void GoodBoxDialog(Good good, String Flag) {
        // Keep the original server row state before mutating the Good object.
        // Editing a printed row 1 -> 2 must create only +1 as a new pending row.
        final String originalAmount = good.getAmount();
        final String originalFactorCode = good.getFactorCode();
        final String originalRowCode = good.getRowCode();
        final String originalExplain = good.getExplain();
        final String activeBasketCode = callMethod.ReadString("AppBasketInfoCode");

        if (activeBasketCode == null || activeBasketCode.trim().isEmpty()) {
            callMethod.showToast("کد سفارش مشخص نیست.");
            return;
        }

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.order_goodorder_box);
        LinearLayoutCompat ll_orderboxgood = dialog.findViewById(R.id.order_goodorder_box);
        if (callMethod.ReadString("LANG").equals("fa")) {
            ll_orderboxgood.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            ll_orderboxgood.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            ll_orderboxgood.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        TextView ed_orderbox_goodname = dialog.findViewById(R.id.ord_goodorder_b_goodname);
        EditText ed_orderbox_amount = dialog.findViewById(R.id.ord_goodorder_b_amount);
        EditText ed_orderbox_explain = dialog.findViewById(R.id.ord_goodorder_b_explain);
        Spinner spinner_orderbox = dialog.findViewById(R.id.ord_goodorder_b_spinnerxplain);
        RecyclerView rc_orderbox = dialog.findViewById(R.id.ord_goodorder_b_rc);
        LinearLayoutCompat ll_inbasket = dialog.findViewById(R.id.ord_goodorder_b_inbasket);
        Button btn_orderbox = dialog.findViewById(R.id.ord_goodorder_b_btn);

        if (Flag.equals("1")) {
            ed_orderbox_amount.setText(callMethod.NumberRegion(good.getAmount()));
            ed_orderbox_explain.setText(callMethod.NumberRegion(good.getExplain()));
            btn_orderbox.setText(R.string.textvalue_editorder);
        } else {
            good.setRowCode("0");
            btn_orderbox.setText(R.string.textvalue_addtoorder);
        }

        ed_orderbox_amount.selectAll();
        ed_orderbox_amount.requestFocus();
        ed_orderbox_amount.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(ed_orderbox_amount, InputMethodManager.SHOW_IMPLICIT);
        }, 500);
        ed_orderbox_amount.setOnClickListener(v -> ed_orderbox_amount.selectAll());

        ed_orderbox_goodname.setText(good.getGoodName());

        call = order_apiInterface.GetDistinctValues("GetDistinctValues", "AppBasket", "Explain", "Where GoodRef=" + good.getGoodCode());
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                values_array.clear();
                values_array.add(0, "");
                if (!response.isSuccessful() || response.body() == null || !canUpdateUi()) {
                    callMethod.Log("GetDistinctValues returned invalid response");
                    return;
                }
                values = response.body().getValues();
                if (values == null) values = new ArrayList<>();
                for (DistinctValue value : values) {
                    values_array.add(callMethod.NumberRegion(value.getValue()));
                }

                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(mContext,
                        android.R.layout.simple_spinner_item, values_array);
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_orderbox.setAdapter(spinner_adapter);


                spinner_orderbox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        ed_orderbox_explain.setText(values_array.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                if (good.getRowCode().length() > 0) {
                    for (String strexplain : values_array) {
                        if (strexplain.equals(good.getExplain())) {
                            spinner_orderbox.setSelection(values_array.indexOf(strexplain));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Order_NetworkFailure.show(mContext, callMethod,
                        "GetDistinctValues", call, t);
            }
        });

        call = order_apiInterface.OrderGet(
                "OrderGet",
                activeBasketCode,
                "3"
        );
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful() && response.body() != null && canUpdateUi()) {
                    good_box_items.clear();
                    ArrayList<Good> responseGoods = response.body().getGoods();
                    if (responseGoods == null) responseGoods = new ArrayList<>();
                    for (Good g : responseGoods) {
                        if (g.getGoodCode().equals(good.getGoodCode())) {
                            good_box_items.add(g);
                        }
                    }
                    if (good_box_items.size()>0){
                        ll_inbasket.setVisibility(View.VISIBLE);
                        Order_GoodBoxItemAdapter adapter = new Order_GoodBoxItemAdapter(good_box_items, mContext);
                        rc_orderbox.setLayoutManager(new GridLayoutManager(mContext, 1));
                        rc_orderbox.setAdapter(adapter);
                        rc_orderbox.setItemAnimator(new DefaultItemAnimator());
                    }else{
                        ll_inbasket.setVisibility(View.GONE);

                    }

                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                Order_NetworkFailure.show(mContext, callMethod,
                        "OrderGet", call, t);
            }
        });


        btn_orderbox.setOnClickListener(v -> {

            // Guard against duplicate taps while the request is in-flight.
            if (!btn_orderbox.isEnabled()) {
                return;
            }

            String amo = NumberFunctions.EnglishNumber(ed_orderbox_amount.getText().toString()).trim();
            String explain = NumberFunctions.EnglishNumber(ed_orderbox_explain.getText().toString()).trim();

            if (amo.isEmpty()) {
                callMethod.showToast(mContext.getString(R.string.textvalue_insertnumber));
                return;
            }

            BigDecimal requestedAmount;
            try {
                requestedAmount = new BigDecimal(amo);
            } catch (Exception e) {
                callMethod.showToast(mContext.getString(R.string.textvalue_inserttruenumber));
                return;
            }

            if (requestedAmount.compareTo(BigDecimal.ZERO) < 0) {
                callMethod.showToast(mContext.getString(R.string.textvalue_inserttruenumber));
                return;
            }

            // Zero is meaningful only when cancelling a previously printed row.
            // New/pending rows still use the existing delete button instead.
            if (requestedAmount.compareTo(BigDecimal.ZERO) == 0
                    && !("1".equals(Flag) && !"0".equals(originalFactorCode))) {
                callMethod.showToast(mContext.getString(R.string.textvalue_inserttruenumber));
                return;
            }

            String rowCodeToSend = "0";
            BigDecimal amountToSend = requestedAmount;

            if ("1".equals(Flag)) {
                // Editing an existing basket row.
                if ("0".equals(originalFactorCode)) {
                    // Not printed yet: update the exact pending row with the absolute amount.
                    rowCodeToSend = originalRowCode;
                } else {
                    // Already printed/factored: preserve history and insert only the positive delta.
                    BigDecimal oldAmount;
                    try {
                        oldAmount = new BigDecimal(NumberFunctions.EnglishNumber(originalAmount));
                    } catch (Exception e) {
                        oldAmount = BigDecimal.ZERO;
                    }

                    BigDecimal delta = requestedAmount.subtract(oldAmount);

                    if (delta.compareTo(BigDecimal.ZERO) > 0) {
                        rowCodeToSend = "0";
                        amountToSend = delta;
                        callMethod.Log("Printed row edit => delta insert. old=" + oldAmount
                                + ", new=" + requestedAmount + ", delta=" + delta);
                    } else if (delta.compareTo(BigDecimal.ZERO) == 0) {
                        if (!Objects.equals(originalExplain, explain)) {
                            submitPrintedRowAdjustment(
                                    good, activeBasketCode, originalRowCode,
                                    delta, requestedAmount, explain, "EXPLAIN",
                                    btn_orderbox, Flag
                            );
                        } else {
                            callMethod.showToast("مقدار تغییری نکرده است.");
                        }
                        return;
                    } else {
                        // Never overwrite/delete a row that the kitchen has already
                        // seen. Ask the new audited server endpoint to append a
                        // negative adjustment while preserving the original row.
                        submitPrintedRowAdjustment(
                                good, activeBasketCode, originalRowCode,
                                delta, requestedAmount, explain,
                                requestedAmount.compareTo(BigDecimal.ZERO) == 0 ? "REMOVE" : "ADJUST",
                                btn_orderbox, Flag
                        );
                        return;
                    }
                }
            } else {
                // New add: merge only into an UNPRINTED row with same explanation.
                // Printed rows stay immutable; a new row represents an extra order.
                for (Good goodLikeOrder : good_box_items) {
                    if (Objects.equals(goodLikeOrder.getExplain(), explain)
                            && "0".equals(goodLikeOrder.getFactorCode())) {

                        rowCodeToSend = goodLikeOrder.getRowCode();
                        try {
                            BigDecimal currentPendingAmount =
                                    new BigDecimal(NumberFunctions.EnglishNumber(goodLikeOrder.getAmount()));
                            amountToSend = currentPendingAmount.add(requestedAmount);
                        } catch (Exception ignored) {
                            amountToSend = requestedAmount;
                        }
                        break;
                    }
                }
            }

            good.setAmount(formatOrderAmount(amountToSend));
            good.setExplain(explain);
            good.setRowCode(rowCodeToSend);

            callMethod.Log("OrderRowInsert => rowCode=" + good.getRowCode()
                    + ", amount=" + good.getAmount()
                    + ", factorCode(original)=" + originalFactorCode);

            btn_orderbox.setEnabled(false);
            dialogProg();
            tv_rep.setText(R.string.textvalue_sendinformation);

            Call<RetrofitResponse> insertCall = order_apiInterface.OrderRowInsert(
                    "OrderRowInsert",
                    good.getGoodCode() + "",
                    good.getAmount(),
                    good.getMaxSellPrice(),
                    good.getGoodUnitRef() + "",
                    good.getDefaultUnitValue() + "",
                    good.getExplain(),
                    activeBasketCode,
                    good.getRowCode()
            );

            insertCall.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                    if (!canUpdateUi()) {
                        restoreGoodAfterFailedEdit(good, Flag, originalAmount, originalExplain, originalRowCode);
                        return;
                    }
                    if (!response.isSuccessful() || response.body() == null
                            || response.body().getGoods() == null
                            || response.body().getGoods().isEmpty()) {
                        restoreGoodAfterFailedEdit(good, Flag, originalAmount, originalExplain, originalRowCode);
                        btn_orderbox.setEnabled(true);
                        if (dialogProg.isShowing()) {
                            dialogProg.dismiss();
                        }
                        callMethod.showToast("پاسخ نامعتبر از سرور");
                        return;
                    }

                    Goods = response.body().getGoods();
                    if (Order_ValueParser.longOrDefault(
                            Goods.get(0).getErrCode(), Long.MAX_VALUE) > 0) {
                        restoreGoodAfterFailedEdit(good, Flag, originalAmount, originalExplain, originalRowCode);
                        btn_orderbox.setEnabled(true);
                        callMethod.showToast(Goods.get(0).getErrDesc());
                        if (dialogProg.isShowing()) {
                            dialogProg.dismiss();
                        }
                    } else {
                        if ("0".equals(Flag)) {
                            callMethod.showToast(mContext.getString(R.string.textvalue_recorded));
                            dialog.dismiss();
                            if (dialogProg.isShowing()) {
                                dialogProg.dismiss();
                            }
                            if (mContext instanceof Order_SearchActivity) {
                                ((Order_SearchActivity) mContext).RefreshState();
                            } else {
                                btn_orderbox.setEnabled(true);
                            }
                        } else {
                            intent = new Intent(mContext, Order_BasketActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            if (mContext instanceof Activity) {
                                ((Activity) mContext).finish();
                                ((Activity) mContext).overridePendingTransition(0, 0);
                                mContext.startActivity(intent);
                            } else {
                                btn_orderbox.setEnabled(true);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                    restoreGoodAfterFailedEdit(good, Flag, originalAmount, originalExplain, originalRowCode);
                    if (!canUpdateUi()) return;
                    btn_orderbox.setEnabled(true);
                    if (dialogProg.isShowing()) {
                        dialogProg.dismiss();
                    }

                    Order_NetworkFailure.show(mContext, callMethod,
                            "OrderRowInsert", call, t);
                }
            });
        });

        dialog.show();
    }


    public void CancelPrintedGood(Good good) {
        if (good == null) return;
        if ("0".equals(good.getFactorCode())) {
            callMethod.showToast("این ردیف هنوز چاپ نشده است؛ از حذف معمولی استفاده کنید.");
            return;
        }

        final String basketCode = callMethod.ReadString("AppBasketInfoCode");
        if (basketCode == null || basketCode.trim().isEmpty()) {
            callMethod.showToast("کد سفارش مشخص نیست.");
            return;
        }

        BigDecimal oldAmount;
        try {
            oldAmount = new BigDecimal(NumberFunctions.EnglishNumber(good.getAmount()));
        } catch (Exception e) {
            callMethod.showToast("مقدار ردیف معتبر نیست.");
            return;
        }

        submitPrintedRowAdjustment(
                good, basketCode, good.getRowCode(),
                oldAmount.negate(), BigDecimal.ZERO, good.getExplain(),
                "REMOVE", null, "1"
        );
    }


    private void submitPrintedRowAdjustment(
            Good good,
            String basketCode,
            String originalRowCode,
            BigDecimal qtyDelta,
            BigDecimal newAmount,
            String newExplain,
            String actionType,
            Button submitButton,
            String flag
    ) {
        if (adjustmentInProgress) {
            callMethod.showToast("اصلاح سفارش در حال انجام است...");
            return;
        }
        adjustmentInProgress = true;
        if (submitButton != null) submitButton.setEnabled(false);
        dialogProg();
        tv_rep.setText(R.string.textvalue_sendinformation);

        try {
            Call<RetrofitResponse> adjustmentCall = order_apiInterface.OrderAdjustmentInsert(
                "OrderAdjustmentInsert",
                basketCode,
                originalRowCode,
                String.valueOf(good.getGoodCode()),
                formatOrderAmount(qtyDelta),
                formatOrderAmount(newAmount),
                good.getMaxSellPrice(),
                String.valueOf(good.getGoodUnitRef()),
                String.valueOf(good.getDefaultUnitValue()),
                newExplain,
                actionType
        );

            adjustmentCall.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (!canUpdateUi()) {
                    adjustmentInProgress = false;
                    return;
                }
                boolean success = false;
                String serverError = "";

                if (response.isSuccessful() && response.body() != null) {
                    RetrofitResponse body = response.body();
                    if ("Done".equals(body.getText())) {
                        success = true;
                    } else if (body.getGoods() != null && !body.getGoods().isEmpty()) {
                        long err = Order_ValueParser.longOrDefault(
                                body.getGoods().get(0).getErrCode(), Long.MAX_VALUE);
                        success = err <= 0;
                        if (!success) serverError = body.getGoods().get(0).getErrDesc();
                    } else if (body.getBasketInfos() != null && !body.getBasketInfos().isEmpty()) {
                        long err = Order_ValueParser.longOrDefault(
                                body.getBasketInfos().get(0).getErrCode(), Long.MAX_VALUE);
                        success = err <= 0;
                        if (!success) serverError = body.getBasketInfos().get(0).getErrDesc();
                    }
                }

                if (dialogProg.isShowing()) dialogProg.dismiss();

                if (!success) {
                    adjustmentInProgress = false;
                    if (submitButton != null) submitButton.setEnabled(true);
                    if (serverError == null || serverError.trim().isEmpty()) {
                        callMethod.showToast("سرور هنوز OrderAdjustmentInsert را پشتیبانی نمی‌کند؛ ردیف چاپ‌شده دست‌نخورده ماند.");
                    } else {
                        callMethod.showToast(serverError);
                    }
                    return;
                }

                callMethod.showToast("اصلاح سفارش ثبت شد");
                if (dialog != null && dialog.isShowing()) dialog.dismiss();

                // A decrease/cancel/explanation edit of an already printed row is
                // not a silent database edit. Ask the server to expose the appended
                // adjustment as the next printable delta, then print it with an
                // explicit ORDER_ADJUSTMENT title. If the legacy backend has not
                // implemented that printable adjustment yet, the audit row remains
                // saved and we safely refresh the UI without touching the original.
                startAdjustmentKitchenPrint(basketCode, flag);
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                adjustmentInProgress = false;
                if (!canUpdateUi()) return;
                if (submitButton != null) submitButton.setEnabled(true);
                if (dialogProg.isShowing()) dialogProg.dismiss();
                callMethod.Log("OrderAdjustmentInsert failed: "
                        + (t.getMessage() == null ? "" : t.getMessage()));
                callMethod.showToast("ثبت اصلاح سفارش انجام نشد؛ ردیف اصلی تغییری نکرد.");
            }
            });
        } catch (RuntimeException exception) {
            adjustmentInProgress = false;
            if (submitButton != null) submitButton.setEnabled(true);
            if (dialogProg.isShowing()) dialogProg.dismiss();
            callMethod.Log("OrderAdjustmentInsert failed before enqueue: "
                    + (exception.getMessage() == null ? "" : exception.getMessage()));
            callMethod.showToast("شروع ثبت اصلاح سفارش ناموفق بود؛ ردیف اصلی تغییر نکرد.");
        }
    }


    private void restoreGoodAfterFailedEdit(
            Good good,
            String flag,
            String originalAmount,
            String originalExplain,
            String originalRowCode
    ) {
        if (good == null || !"1".equals(flag)) {
            return;
        }
        good.setAmount(originalAmount);
        good.setExplain(originalExplain);
        good.setRowCode(originalRowCode);
    }

    private void startAdjustmentKitchenPrint(String basketCode, String flag) {
        try {
            Call<RetrofitResponse> canPrintCall = order_apiInterface.Order_CanPrint(
                "Order_CanPrint", basketCode, "1"
            );
            canPrintCall.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call,
                                   @NotNull Response<RetrofitResponse> response) {
                if (!canUpdateUi()) {
                    adjustmentInProgress = false;
                    return;
                }
                if (response.isSuccessful() && response.body() != null
                        && "Done".equals(response.body().getText())) {
                    try {
                        boolean started = order_print.GetHeader_DataForBasket(
                                basketCode,
                                "",
                                Order_Print.PrintReason.ORDER_ADJUSTMENT
                        );
                        if (!started) {
                            adjustmentInProgress = false;
                            callMethod.showToast("اصلاح ثبت شد، اما چاپ اصلاحیه در حال انجام نبود.");
                            refreshAfterAdjustment(flag);
                        }
                    } catch (RuntimeException exception) {
                        adjustmentInProgress = false;
                        callMethod.Log("Adjustment print start failed: "
                                + (exception.getMessage() == null ? "" : exception.getMessage()));
                        refreshAfterAdjustment(flag);
                    }
                    return;
                }

                callMethod.showToast("اصلاح ثبت شد، ولی چاپ اصلاحیه شروع نشد.");
                adjustmentInProgress = false;
                refreshAfterAdjustment(flag);
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                adjustmentInProgress = false;
                if (!canUpdateUi()) return;
                callMethod.Log("Order_CanPrint adjustment failed: "
                        + (t.getMessage() == null ? "" : t.getMessage()));
                callMethod.showToast("اصلاح ثبت شد، ولی چاپ اصلاحیه شروع نشد.");
                refreshAfterAdjustment(flag);
            }
            });
        } catch (RuntimeException exception) {
            adjustmentInProgress = false;
            callMethod.Log("Order_CanPrint adjustment failed before enqueue: "
                    + (exception.getMessage() == null ? "" : exception.getMessage()));
            refreshAfterAdjustment(flag);
        }
    }

    private void refreshAfterAdjustment(String flag) {
        if ("0".equals(flag) && mContext instanceof Order_SearchActivity) {
            ((Order_SearchActivity) mContext).RefreshState();
        } else if (mContext instanceof Activity) {
            Intent refreshIntent = new Intent(mContext, Order_BasketActivity.class);
            refreshIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(refreshIntent);
        }
    }

    private String formatOrderAmount(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return amount.stripTrailingZeros().toPlainString();
    }


    public void OrderToFactor() {
        OrderToFactor(callMethod.ReadString("AppBasketInfoCode"));
    }

    public void OrderToFactor(String basketCodeInput) {
        if (orderToFactorInProgress) {
            callMethod.showToast("ثبت سفارش در حال انجام است...");
            return;
        }
        orderToFactorInProgress = true;

        final String basketCode = basketCodeInput == null ? "" : basketCodeInput.trim();
        if (basketCode.isEmpty()) {
            orderToFactorInProgress = false;
            callMethod.showToast("کد سفارش مشخص نیست.");
            return;
        }

        dialogProg();
        tv_rep.setText(R.string.textvalue_sendinformation);
        try {
            Call<RetrofitResponse> call = order_apiInterface.OrderToFactor(
                "OrderToFactor",
                basketCode
            );

            call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (!canUpdateUi()) {
                    orderToFactorInProgress = false;
                    return;
                }
                if (!response.isSuccessful() || response.body() == null
                        || response.body().getBasketInfos() == null
                        || response.body().getBasketInfos().isEmpty()) {
                    orderToFactorInProgress = false;
                    if (dialogProg.isShowing()) {
                        dialogProg.dismiss();
                    }
                    callMethod.showToast("پاسخ ثبت سفارش از سرور نامعتبر است.");
                    return;
                }

                if (response.isSuccessful()) {
                    if (Order_ValueParser.longOrDefault(
                            response.body().getBasketInfos().get(0).getErrCode(),
                            Long.MAX_VALUE) > 0) {
                        orderToFactorInProgress = false;
                        callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                        if (dialogProg.isShowing()) dialogProg.dismiss();
                    } else {
                        // OrderToFactor itself is complete. Release the submit guard before
                        // starting the independent async print flow so a print failure does
                        // not permanently lock this Order_Action instance.
                        orderToFactorInProgress = false;
                        if (dialogProg.isShowing()) dialogProg.dismiss();

                        //todo dotnet
                        //OrderPrintFactor();
                        order_print.GetHeader_DataForBasket(basketCode, "", Order_Print.PrintReason.AUTO);

                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                orderToFactorInProgress = false;
                if (!canUpdateUi()) return;
                if (dialogProg.isShowing()) dialogProg.dismiss();
                Order_NetworkFailure.show(mContext, callMethod,
                        "OrderToFactor", call, t);
            }
            });
        } catch (RuntimeException exception) {
            orderToFactorInProgress = false;
            if (dialogProg.isShowing()) dialogProg.dismiss();
            callMethod.Log("OrderToFactor failed before enqueue: "
                    + (exception.getMessage() == null ? "" : exception.getMessage()));
            callMethod.showToast("شروع ثبت سفارش ناموفق بود.");
        }

    }
//    public void ChangeTable(Order_BasketInfo basketInfo) {
//
//
//
//        String Body_str  = "";
//
//        Body_str =callMethod.CreateJson("Broker", dbh.ReadConfig("BrokerCode"), Body_str);
//        Body_str =callMethod.CreateJson("Miz",basketInfo.getRstmizCode(), Body_str);
//        Body_str =callMethod.CreateJson("PersonName",callMethod.ReadString("PersonName"), Body_str);
//        Body_str =callMethod.CreateJson("Mobile",callMethod.ReadString("MobileNo"), Body_str);
//        Body_str =callMethod.CreateJson("InfoExplain", callMethod.ReadString("InfoExplain"), Body_str);
//        Body_str =callMethod.CreateJson("Prepayed",  "0", Body_str);
//        Body_str =callMethod.CreateJson("ReserveStartTime", callMethod.ReadString("ReserveStart"), Body_str);
//        Body_str =callMethod.CreateJson("ReserveEndTime", callMethod.ReadString("ReserveEnd"), Body_str);
//        Body_str =callMethod.CreateJson("Date", callMethod.ReadString("Today"), Body_str);
//        Body_str =callMethod.CreateJson("State", callMethod.ReadString("InfoState"), Body_str);
//        Body_str =callMethod.CreateJson("InfoCode", callMethod.ReadString("AppBasketInfoCode"), Body_str);
//
//
//
//       // call = order_apiInterface.OrderInfoInsert(callMethod.RetrofitBody(Body_str));
//
//
//        call.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
//                assert response.body() != null;
//                if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
//                    callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
//                } else {
//                    OrderChangeTable();
//                }
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
//            }
//        });
//
//    }

//    public void OrderPrintFactor() {
//        dialogProg();
//        tv_rep.setText(R.string.textvalue_sendinformation);
//        Call<RetrofitResponse> call = order_apiInterface.OrderPrintFactor(
//                "OrderPrintFactor",
//                callMethod.ReadString("AppBasketInfoCode")
//        );
//
//        call.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
//                dialogProg.dismiss();
//
//                    call = order_apiInterface.Order_CanPrint("Order_CanPrint", callMethod.ReadString("AppBasketInfoCode"), "0");
//                    call.enqueue(new Callback<RetrofitResponse>() {
//                        @Override
//                        public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
//                            if (response.isSuccessful()) {
//                                assert response.body() != null;
//                                if (response.body().getText().equals("Done")) {
//                                    callMethod.showToast(mContext.getString(R.string.textvalue_recorded));
//                                    dialogProg.dismiss();
//                                    intent = new Intent(mContext, Order_TableActivity.class);
//                                    intent.putExtra("State", "0");
//                                    intent.putExtra("EditTable", "0");
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    mContext.startActivity(intent);
//                                    ((Activity) mContext).finish();
//                                }
//
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
//
//                        }
//                    });
//
//
//
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
//                intent = new Intent(mContext, Order_BasketActivity.class);
//                ((Activity) mContext).finish();
//                ((Activity) mContext).overridePendingTransition(0, 0);
//                mContext.startActivity(intent);
//            }
//        });
//
//    }
//
//    public void OrderChangeTable() {
//        dialogProg();
//        tv_rep.setText(R.string.textvalue_sendinformation);
//
//
//        call = order_apiInterface.Order_CanPrint("Order_CanPrint", callMethod.ReadString("AppBasketInfoCode"), "1");
//        call.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
//                if (response.isSuccessful()) {
//                    assert response.body() != null;
//                    if (response.body().getText().equals("Done")) {
//
//                        Call<RetrofitResponse> call_Change = order_apiInterface.OrderChangeTable(
//                                "OrderChangeTable",
//                                callMethod.ReadString("AppBasketInfoCode")
//                        );
//
//                        call_Change.enqueue(new Callback<RetrofitResponse>() {
//                            @Override
//                            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
//                                dialogProg.dismiss();
//                                if (response.body().getText().equals("Done")) {
//                                    callMethod.showToast(mContext.getString(R.string.textvalue_recorded));
//                                    dialogProg.dismiss();
//                                    intent = new Intent(mContext, Order_TableActivity.class);
//                                    intent.putExtra("State", "0");
//                                    intent.putExtra("EditTable", "0");
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    mContext.startActivity(intent);
//                                    ((Activity) mContext).finish();
//
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
//                                intent = new Intent(mContext, Order_BasketActivity.class);
//                                ((Activity) mContext).finish();
//                                ((Activity) mContext).overridePendingTransition(0, 0);
//                                mContext.startActivity(intent);
//                            }
//                        });
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
//
//            }
//        });
//
//
//
//
//    }
//


    public void EditBasketInfoExplain(Order_BasketInfo basketInfo) {


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.order_basketinfo_explain);
        Button explain_btn = dialog.findViewById(R.id.ord_basketexplain_b_explain_btn);
        explain_btn.setText(R.string.textvalue_setexplain);
        final EditText explain_tv = dialog.findViewById(R.id.ord_basketexplain_b_explain_tv);
        Spinner spinner_orderbox = dialog.findViewById(R.id.ord_basketexplain_b_spinnerexplain);
        String explainvalue="";

        if (basketInfo.getInfoExplain().contains("*")) {
            int startsub = basketInfo.getInfoExplain().indexOf("*");
            String temp = basketInfo.getInfoExplain().substring(startsub);
            int endsub = temp.indexOf("*");
            explainvalue = temp.substring(0, endsub);
        }
        explain_tv.setText(callMethod.NumberRegion(explainvalue));



        dialog.show();
        explain_tv.requestFocus();
        explain_tv.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(explain_tv, InputMethodManager.SHOW_IMPLICIT);
        }, 500);

        explain_tv.setOnLongClickListener(v -> {
            explain_tv.selectAll();
            return  false;
        });

        Call<RetrofitResponse> call1 = order_apiInterface.GetObjectTypeFromDbSetup("GetObjectTypeFromDbSetup", "AppOrder_InfoExplainList");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful() && response.body() != null && canUpdateUi()) {
                    objectTypes.clear();
                    values_array.clear();
                    values_array.add(0, "");
                    objectTypes = response.body().getObjectTypes();
                    if (objectTypes == null) objectTypes = new ArrayList<>();

                    for (ObjectType ob : objectTypes) {
                        values_array.add(callMethod.NumberRegion(ob.getaType()));
                    }

                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item, values_array);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_orderbox.setAdapter(spinner_adapter);


                    spinner_orderbox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            explain_tv.setText(explain_tv.getText().toString()+" "+values_array.get(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                Order_NetworkFailure.show(mContext, callMethod,
                        "GetObjectTypeFromDbSetup", call, t);

            }
        });



        explain_btn.setOnClickListener(view -> {

            dialogProg();
            tv_rep.setText(R.string.textvalue_sendinformation);


//            String Body_str  = "";
//
//            Body_str =callMethod.CreateJson("Broker", dbh.ReadConfig("BrokerCode"), Body_str);
//            Body_str =callMethod.CreateJson("Miz", basketInfo.getRstmizCode(), Body_str);
//            Body_str =callMethod.CreateJson("PersonName", basketInfo.getPersonName(), Body_str);
//            Body_str =callMethod.CreateJson("Mobile", basketInfo.getMobileNo(), Body_str);
//            Body_str =callMethod.CreateJson("InfoExplain", NumberFunctions.EnglishNumber( " * "+explain_tv.getText().toString())+" * ", Body_str);
//            Body_str =callMethod.CreateJson("Prepayed", basketInfo.getPrepayed(), Body_str);
//            Body_str =callMethod.CreateJson("ReserveStartTime", basketInfo.getReserveStart(), Body_str);
//            Body_str =callMethod.CreateJson("ReserveEndTime", basketInfo.getReserveEnd(), Body_str);
//            Body_str =callMethod.CreateJson("Date", basketInfo.getToday(), Body_str);
//            Body_str =callMethod.CreateJson("State", basketInfo.getInfoState(), Body_str);
//            Body_str =callMethod.CreateJson("InfoCode", basketInfo.getAppBasketInfoCode(), Body_str);
//
//
//
//             call = order_apiInterface.OrderInfoInsert(callMethod.RetrofitBody(Body_str));
            call = order_apiInterface.OrderInfoInsert(
                    "OrderInfoInsert",
                    order_dbh.ReadConfig("BrokerCode"),
                    basketInfo.getRstmizCode(),
                    basketInfo.getPersonName(),
                    basketInfo.getMobileNo(),
                    NumberFunctions.EnglishNumber( " * "+explain_tv.getText().toString())+" * ",
                    basketInfo.getPrepayed(),
                    basketInfo.getReserveStart(),
                    basketInfo.getReserveEnd(),
                    basketInfo.getToday(),
                    basketInfo.getInfoState(),
                    basketInfo.getAppBasketInfoCode()
            );

            if (!basketInfo.getInfoExplain().equals(NumberFunctions.EnglishNumber(explain_tv.getText().toString()))) {
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (!canUpdateUi()) return;
                        if (response.isSuccessful() && canUpdateUi()) {
                            Order_BasketInfo result = firstBasket(response);
                            if (result == null) {
                                safeDismiss(dialogProg, "edit info progress");
                                callMethod.showToast("پاسخ ثبت اطلاعات معتبر نیست");
                                return;
                            }
                            if (Order_ValueParser.longOrDefault(
                                    result.getErrCode(), Long.MAX_VALUE) > 0) {
                                callMethod.showToast(safeString(result.getErrDesc()));
                                dialogProg.dismiss();
                            } else {
                                dialog.dismiss();
                                dialogProg.dismiss();
                                callMethod.showToast(mContext.getString(R.string.textvalue_recorded));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                        Order_NetworkFailure.show(mContext, callMethod,
                                "OrderInfoInsert edit", call, t);
                        safeDismiss(dialog, "edit info dialog");
                        safeDismiss(dialogProg, "edit info progress");
                    }
                });
            } else {
                dialog.dismiss();
            }
        });

    }


    public void BasketInfoExplainBeforOrder() {

        final String basketCode = callMethod.ReadString("AppBasketInfoCode");
        if (basketCode == null || basketCode.trim().isEmpty()) {
            callMethod.showToast("کد سفارش مشخص نیست.");
            return;
        }

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.order_basketinfo_explain);
        Button explain_btn = dialog.findViewById(R.id.ord_basketexplain_b_explain_btn);
        explain_btn.setText(R.string.textvalue_setexplain);
        final EditText explain_tv = dialog.findViewById(R.id.ord_basketexplain_b_explain_tv);
        Spinner spinner_orderbox = dialog.findViewById(R.id.ord_basketexplain_b_spinnerexplain);

        dialog.show();
        explain_tv.requestFocus();
        explain_tv.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(explain_tv, InputMethodManager.SHOW_IMPLICIT);
        }, 500);


        explain_tv.setOnLongClickListener(v -> {
            explain_tv.selectAll();
            return  false;
        });

        Call<RetrofitResponse> call1 = order_apiInterface.GetObjectTypeFromDbSetup("GetObjectTypeFromDbSetup", "AppOrder_InfoExplainList");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful() && response.body() != null && canUpdateUi()) {
                    objectTypes.clear();
                    values_array.clear();
                    values_array.add(0, "");
                    objectTypes = response.body().getObjectTypes();
                    if (objectTypes == null) objectTypes = new ArrayList<>();

                    for (ObjectType ob : objectTypes) {
                        values_array.add(callMethod.NumberRegion(ob.getaType()));
                    }

                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item, values_array);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_orderbox.setAdapter(spinner_adapter);


                    spinner_orderbox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            explain_tv.setText(explain_tv.getText().toString()+" "+values_array.get(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                Order_NetworkFailure.show(mContext, callMethod,
                        "GetObjectTypeFromDbSetup", call, t);
            }
        });





        explain_btn.setOnClickListener(view -> {

            if(explain_tv.getText().toString().length()>0) {
                explain_btn.setEnabled(false);
                dialogProg();
                tv_rep.setText(R.string.textvalue_sendinformation);

//
//
//
//                String Body_str  = "";
//
//                Body_str =callMethod.CreateJson("AppBasketInfoCode", basketCode, Body_str);
//                Body_str =callMethod.CreateJson("Explain", NumberFunctions.EnglishNumber(explain_tv.getText().toString()), Body_str);
//
//
//                call = order_apiInterface.OrderEditInfoExplain(callMethod.RetrofitBody(Body_str));
                call = order_apiInterface.OrderEditInfoExplain(
                        "OrderEditInfoExplain",
                        basketCode,
                        NumberFunctions.EnglishNumber(explain_tv.getText().toString())
                );

                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (!response.isSuccessful() || response.body() == null
                                || response.body().getBasketInfos() == null
                                || response.body().getBasketInfos().isEmpty()) {
                            explain_btn.setEnabled(true);
                            if (dialogProg.isShowing()) dialogProg.dismiss();
                            callMethod.showToast("پاسخ ثبت توضیحات سفارش نامعتبر است.");
                            return;
                        }

                        if (Order_ValueParser.longOrDefault(
                                response.body().getBasketInfos().get(0).getErrCode(),
                                Long.MAX_VALUE) > 0) {
                            explain_btn.setEnabled(true);
                            if (dialogProg.isShowing()) dialogProg.dismiss();
                            callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                        } else {
                            OrderToFactor(basketCode);
                            dialog.dismiss();
                            if (dialogProg.isShowing()) dialogProg.dismiss();
                            callMethod.showToast(mContext.getString(R.string.textvalue_recorded));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                        if (!canUpdateUi()) return;
                        explain_btn.setEnabled(true);
                        Order_NetworkFailure.show(mContext, callMethod,
                                "OrderEditInfoExplain", call, t);
                        safeDismiss(dialog, "explain dialog");
                        safeDismiss(dialogProg, "explain progress");
                    }
                });
            }else{
                OrderToFactor(basketCode);
            }
        });

    }


    public void lottieok() {
        if (!canUpdateUi()) return;
        Dialog dialog1 = new Dialog(mContext);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog1.setContentView(R.layout.order_lottie);
        LottieAnimationView animationView = dialog1.findViewById(R.id.ord_lottie_name);
        animationView.setAnimation(R.raw.oklottie);
        dialog1.show();
        animationView.setRepeatCount(0);

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dialog1.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });


    }

    private Activity activityOrNull() {
        if (!(mContext instanceof Activity)) return null;
        Activity activity = (Activity) mContext;
        if (activity.isFinishing() || activity.isDestroyed()) return null;
        return activity;
    }

    private boolean canUpdateUi() {
        return activityOrNull() != null;
    }

    private Order_BasketInfo firstBasket(Response<RetrofitResponse> response) {
        if (response == null || !response.isSuccessful() || response.body() == null
                || response.body().getBasketInfos() == null
                || response.body().getBasketInfos().isEmpty()) {
            return null;
        }
        return response.body().getBasketInfos().get(0);
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private void safeDismiss(Dialog target, String operation) {
        if (target == null) return;
        try {
            if (target.isShowing()) target.dismiss();
        } catch (RuntimeException exception) {
            callMethod.Log(operation + " dismiss failed: "
                    + exception.getClass().getSimpleName());
        }
    }

    public void cancelPending() {
        if (call != null) call.cancel();
        call = null;
        orderToFactorInProgress = false;
        adjustmentInProgress = false;
        safeDismiss(dialogProg, "order progress");
        safeDismiss(dialog, "order dialog");
        safeDismiss(dialog_payment, "order payment dialog");
    }






    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String tmonthOfYear, tdayOfMonth;
        tmonthOfYear = "0" + (monthOfYear + 1);
        tdayOfMonth = "0" + dayOfMonth;

        date = year + "/"
                + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                + tdayOfMonth.substring(tdayOfMonth.length() - 2);

        tv_date.setText(callMethod.NumberRegion(date));
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }


}
