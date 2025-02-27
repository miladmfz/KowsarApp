package com.kits.kowsarapp.application.order;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.order.Order_BasketActivity;
import com.kits.kowsarapp.activity.order.Order_RegistrationActivity;
import com.kits.kowsarapp.activity.order.Order_SearchActivity;
import com.kits.kowsarapp.activity.order.Order_TableActivity;
import com.kits.kowsarapp.adapter.order.Order_GoodBoxItemAdapter;
import com.kits.kowsarapp.adapter.order.Order_ReserveAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_Action extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private final Context mContext;
    public Order_APIInterface apiInterface;
    public Call<RetrofitResponse> call;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    CallMethod callMethod;
    Order_DBH dbh;
    Intent intent;
    Integer il;
    PersianCalendar persianCalendar;
    String date;
    Dialog dialog, dialogProg;
    Calendar cldr;
    TimePickerDialog picker;
    TextView tv_reservestart;
    TextView tv_reserveend;
    TextView tv_date;
    int ehour = 0;
    int eminutes = 0;
    int printerconter ;
    ArrayList<DistinctValue> values = new ArrayList<>();
    ArrayList<String> values_array = new ArrayList<>();
    ArrayList<Good> Goods;
    ArrayList<Good> good_box_items = new ArrayList<>();
    ArrayList<ObjectType> objectTypes = new ArrayList<>();

    TextView tv_rep;
    Order_Print order_print;
    String payment_type="";
    String totalprice="0";
    String payment_mablagh_tosend="0";
    String payment_mablagh_incrise="0";
    String payment_mablagh_decrise="0";

    public Order_Action(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);

        this.dbh = new Order_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);
        this.dialogProg = new Dialog(mContext);
        this.order_print = new Order_Print(mContext);

        printerconter = 0;

    }
    public void BasketInfopayment(Order_BasketInfo basketInfo) {

        payment_type="cash";
        payment_mablagh_tosend="0";
        payment_mablagh_incrise="0";
        payment_mablagh_decrise="0";
        totalprice="0";

        final Dialog dialog_payment = new Dialog(mContext);

        dialog_payment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_payment.setContentView(R.layout.order_payment_box);

        Button btn_payment = dialog_payment.findViewById(R.id.ord_payment_b_btn_final);
        Button btn_tocash = dialog_payment.findViewById(R.id.ord_payment_b_btn_cash);
        Button btn_topos = dialog_payment.findViewById(R.id.ord_payment_b_btn_pos);

        LinearLayoutCompat ll_edpay = dialog_payment.findViewById(R.id.ord_payment_b_ll_edpay);
        LinearLayoutCompat ll_edlable = dialog_payment.findViewById(R.id.ord_payment_b_ll_edlable);
        LinearLayoutCompat ll_notreceived = dialog_payment.findViewById(R.id.ord_payment_b_ll_notreceived);
        LinearLayoutCompat ll_received = dialog_payment.findViewById(R.id.ord_payment_b_ll_received);
        LinearLayoutCompat ll_cashtopay = dialog_payment.findViewById(R.id.ord_payment_b_ll_cashtopay);
        LinearLayoutCompat ll_decrement = dialog_payment.findViewById(R.id.ord_payment_b_ll_decrement);

        TextView tv_payment_sumprice = dialog_payment.findViewById(R.id.ord_payment_b_sumprice);
        TextView tv_payment_sumtaxmayor = dialog_payment.findViewById(R.id.ord_payment_b_sumtaxmayor);
        TextView tv_payment_totalprice = dialog_payment.findViewById(R.id.ord_payment_b_totalprice);
        TextView tv_payment_received = dialog_payment.findViewById(R.id.ord_payment_b_received);
        TextView tv_payment_notreceived = dialog_payment.findViewById(R.id.ord_payment_b_notreceived);
        TextView tv_payment_decrement = dialog_payment.findViewById(R.id.ord_payment_b_decrement);

        EditText ed_payment_mablagh = dialog_payment.findViewById(R.id.ord_payment_b_mablagh);
        EditText ed_payment_selloff = dialog_payment.findViewById(R.id.ord_payment_b_selloff);
        EditText ed_payment_cashtopay = dialog_payment.findViewById(R.id.ord_payment_b_cashtopay);

        dialog_payment.show();


        if (callMethod.ReadBoolan("PosPayment")){
            payment_type="pos";
            btn_topos.setBackgroundColor(mContext.getResources().getColor(R.color.blue_500));
            btn_tocash.setBackgroundColor(mContext.getResources().getColor(R.color.gray_secondary));
        }

        if (Integer.parseInt(basketInfo.getDecrementValue().replace("-",""))>0){
            ll_decrement.setVisibility(View.VISIBLE);
        }else{
            ll_decrement.setVisibility(View.GONE);
        }


        if (Integer.parseInt(basketInfo.getReceived())>0){
            ll_edpay.setVisibility(View.GONE);
            ll_edlable.setVisibility(View.GONE);
            ll_cashtopay.setVisibility(View.GONE);
            ll_notreceived.setVisibility(View.VISIBLE);
            ll_received.setVisibility(View.VISIBLE);
        }else{
            ll_edpay.setVisibility(View.VISIBLE);
            ll_edlable.setVisibility(View.VISIBLE);
            ll_notreceived.setVisibility(View.GONE);
            ll_received.setVisibility(View.GONE);
        }


        btn_tocash.setOnClickListener(v -> {
            payment_type="cash";
            btn_tocash.setBackgroundColor(mContext.getResources().getColor(R.color.blue_500));
            btn_topos.setBackgroundColor(mContext.getResources().getColor(R.color.gray_secondary));

            if (Integer.parseInt(basketInfo.getReceived())==0){
                ll_cashtopay.setVisibility(View.VISIBLE);
            }else{
                ll_cashtopay.setVisibility(View.GONE);
            }
        });

        btn_topos.setOnClickListener(v -> {
            ll_cashtopay.setVisibility(View.GONE);
            if (callMethod.ReadString("PosName").length()>1){
                payment_type="pos";
                btn_topos.setBackgroundColor(mContext.getResources().getColor(R.color.blue_500));
                btn_tocash.setBackgroundColor(mContext.getResources().getColor(R.color.gray_secondary));
            }else{
                btn_tocash.callOnClick();
                callMethod.showToast("پوزی انتخاب نشده است");
            }

        });



        totalprice =String.valueOf(Integer.parseInt(basketInfo.getSumPrice())+Integer.parseInt(basketInfo.getSumTaxAndMayor()));



        tv_payment_sumprice.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(basketInfo.getSumPrice()))));
        tv_payment_sumtaxmayor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(basketInfo.getSumTaxAndMayor()))));

        tv_payment_totalprice.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));

        tv_payment_received.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(basketInfo.getReceived()))));
        tv_payment_notreceived.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(basketInfo.getNotReceived()))));
        tv_payment_decrement.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(basketInfo.getDecrementValue()))));

        ed_payment_mablagh.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));
        ed_payment_cashtopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));

        ed_payment_selloff.setText(NumberFunctions.PerisanNumber("0"));

        ed_payment_mablagh.setOnClickListener(v -> ed_payment_mablagh.selectAll());
        ed_payment_selloff.setOnClickListener(v -> ed_payment_selloff.selectAll());
        ed_payment_cashtopay.setOnClickListener(v -> ed_payment_cashtopay.selectAll());



        ed_payment_mablagh.addTextChangedListener(new TextWatcher() {
            @Override  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                if (ed_payment_mablagh.hasFocus()) {
                    try {
                        String NewPrice = NumberFunctions.EnglishNumber(ed_payment_mablagh.getText().toString());
                        ed_payment_selloff.setText(NumberFunctions.PerisanNumber("" + (100 - (100 * Long.parseLong(NewPrice) / Integer.parseInt(totalprice)))));
                        ed_payment_cashtopay.setText(NewPrice);

                    } catch (Exception e) {
                        ed_payment_mablagh.setText(totalprice);
                        ed_payment_cashtopay.setText(totalprice);
                    }
                }
            }
        });



        ed_payment_selloff.addTextChangedListener(new TextWatcher() {
            @Override  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                if (ed_payment_selloff.hasFocus()) {
                    long selloff;
                    try {

                        selloff = Integer.parseInt(NumberFunctions.EnglishNumber(ed_payment_selloff.getText().toString()));



                        if (selloff > Integer.parseInt(callMethod.ReadString("MaxSellOff"))) {
                            selloff = Integer.parseInt(callMethod.ReadString("MaxSellOff"));
                            ed_payment_selloff.setText(NumberFunctions.PerisanNumber(String.valueOf(selloff)));
                            ed_payment_selloff.setError("حداکثر تخفیف");
                        }

                        long sellpricenew = (long) (Integer.parseInt(totalprice) - ((Integer.parseInt(totalprice) * selloff / 100)));

                        ed_payment_mablagh.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sellpricenew)));
                        ed_payment_cashtopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sellpricenew)));
                    } catch (Exception e) {
                        ed_payment_mablagh.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));
                        ed_payment_cashtopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));

                    }

                }

            }
        });


        btn_payment.setOnClickListener(v -> {


            Call<RetrofitResponse> call_payment = null;
            Call<RetrofitResponse> call_payment_inc_dec= null;

            if (Integer.parseInt(basketInfo.getReceived())>0){ // Received >0

                if (payment_type.equals("cash")){

                    String DecrementValue_str= basketInfo.getDecrementValue().replace("-","");

                    call_payment = apiInterface.Factor_Payment_Cash(
                            "Factor_Payment_Cash"
                            ,basketInfo.getFactorCode()
                            ,String.valueOf(Integer.parseInt(basketInfo.getNotReceived())+Integer.parseInt(basketInfo.getShopNaghdReceive()))
                            ,"0"
                            ,DecrementValue_str
                    );
                }else{
                    call_payment = apiInterface.Factor_Payment_Pos(
                            "Factor_Payment_Pos"
                            ,basketInfo.getFactorCode()
                            ,callMethod.ReadString("PosCode")
                            ,basketInfo.getNotReceived()
                    );
                }

                dialogProg();
                call_payment.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call1, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            dialog_payment.dismiss();
                            dialogProg.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call1, @NotNull Throwable t) {
                        callMethod.Log("kowsar_______"+t.getMessage());
                    }
                });
            }else{ // Received == 0


                int mablagh = (Integer.parseInt(totalprice) - ((Integer.parseInt(totalprice) * (Integer.parseInt(NumberFunctions.EnglishNumber(ed_payment_selloff.getText().toString()))) / 100)));

                if (mablagh>Integer.parseInt(totalprice)){
                    payment_mablagh_incrise=String.valueOf(mablagh-Integer.parseInt(totalprice));
                }else{
                    payment_mablagh_decrise=String.valueOf(Integer.parseInt(totalprice)-mablagh);
                }

                if ((!payment_mablagh_incrise.equals("0"))||(!payment_mablagh_decrise.equals("0"))){

                    call_payment_inc_dec = apiInterface.Factor_Payment_Cash(
                            "Factor_Payment_Cash"
                            ,basketInfo.getFactorCode()
                            ,NumberFunctions.EnglishNumber(ed_payment_cashtopay.getText().toString().replace(",", ""))
                            ,payment_mablagh_incrise
                            ,payment_mablagh_decrise
                    );


                }else{

                    if (payment_type.equals("cash")){
                        call_payment = apiInterface.Factor_Payment_Cash(
                                "Factor_Payment_Cash"
                                ,basketInfo.getFactorCode()
                                ,NumberFunctions.EnglishNumber(ed_payment_cashtopay.getText().toString().replace(",", ""))
                                ,"0"
                                ,"0"
                        );
                    }else{
                        call_payment = apiInterface.Factor_Payment_Pos(
                                "Factor_Payment_Pos"
                                ,basketInfo.getFactorCode()
                                ,callMethod.ReadString("PosCode")
                                ,basketInfo.getNotReceived()
                        );
                    }

                }


                if ((!payment_mablagh_incrise.equals("0"))||(!payment_mablagh_decrise.equals("0"))){
                    dialogProg();
                    Call<RetrofitResponse> finalCall_payment = call_payment;
                    call_payment_inc_dec.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<RetrofitResponse> call1, @NotNull Response<RetrofitResponse> response) {
                            if (response.isSuccessful()) {
                                dialog_payment.dismiss();
                                dialogProg.dismiss();
                            }
                        }
                        @Override
                        public void onFailure(@NotNull Call<RetrofitResponse> call1, @NotNull Throwable t) {
                            callMethod.Log("kowsar_______"+t.getMessage());
                        }
                    });

                }else{
                    dialogProg();
                    call_payment.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<RetrofitResponse> call1, @NotNull Response<RetrofitResponse> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                dialog_payment.dismiss();
                                dialogProg.dismiss();
                            }
                        }
                        @Override
                        public void onFailure(@NotNull Call<RetrofitResponse> call1, @NotNull Throwable t) {
                            callMethod.Log("kowsar_______"+t.getMessage());
                        }
                    });
                }
            }

        });

    }

    public void dialogProg() {
        dialogProg.setContentView(R.layout.order_spinner_box);
        tv_rep = dialogProg.findViewById(R.id.ord_spinner_text);
        dialogProg.show();
    }

    public void DeleteReserveDialog(Order_BasketInfo basketInfo) {
        dialogProg();
        call = apiInterface.OrderInfoReserveDelete(
                "OrderInfoReserveDelete",
                basketInfo.getAppBasketInfoCode()
        );
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                assert response.body() != null;

                intent = new Intent(mContext, Order_TableActivity.class);
                intent.putExtra("State", "0");
                intent.putExtra("EditTable", "0");
                mContext.startActivity(intent);
                ((Activity) mContext).finish();


            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });


    }
    public void LoginSetting() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.default_loginconfig);
        EditText ed_password = dialog.findViewById(R.id.d_loginconfig_ed);
        MaterialButton btn_login = dialog.findViewById(R.id.d_loginconfig_btn);
        btn_login.setOnClickListener(v -> {
            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {
                Intent intent = new Intent(mContext, Order_RegistrationActivity.class);
                mContext.startActivity(intent);
            }else {
                callMethod.showToast("رمز عبور صیحیح نیست");
            }
        });
        dialog.show();
    }
    public void ReserveBoxDialog(Order_BasketInfo basketInfo) {

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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


        call = apiInterface.OrderReserveList("OrderReserveList", basketInfo.getRstmizCode());
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                assert response.body() != null;
                Order_ReserveAdapter adapter = new Order_ReserveAdapter(response.body().getBasketInfos(), mContext);
                recycler.setLayoutManager(new GridLayoutManager(mContext, 1));
                recycler.setAdapter(adapter);
                recycler.setItemAnimator(new DefaultItemAnimator());
                recycler.setAdapter(adapter);

            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });


        call = apiInterface.GetTodeyFromServer("GetTodeyFromServer");

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                assert response.body() != null;
                date = response.body().getText();
                tv_date.setText(callMethod.NumberRegion(date));
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
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
//                call = apiInterface.DbSetupvalue(
//                        "DbSetupvalue",
//                        "AppOrder_ValidReserveTime"
//                );
                call = apiInterface.kowsar_info(
                        "kowsar_info",
                        "AppOrder_ValidReserveTime"
                );
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        String ehourOfDay = "", eminute, eTime;
                        assert response.body() != null;
                        if (minute + Integer.parseInt(response.body().getText()) > 60) {
                            eminute = String.valueOf(minute + Integer.parseInt(response.body().getText()) - 60);
                            if ((hourOfDay + 1) > 23) {
                                ehourOfDay = String.valueOf(hourOfDay);
                                eminute = "59";
                            } else {
                                ehourOfDay = String.valueOf(hourOfDay + 1);
                            }
                        } else {
                            eminute = String.valueOf(minute + Integer.parseInt(response.body().getText()));
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

                    }
                });


            }, hour, minutes, true);
            picker.show(((Activity) mContext).getFragmentManager(), "Timepickerdialog");


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
            picker.show(((Activity) mContext).getFragmentManager(), "Timepickerdialog");


        });

        tv_date.setOnClickListener(v -> {

            PersianCalendar persianCalendar1 = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    persianCalendar1.getPersianYear(),
                    persianCalendar1.getPersianMonth(),
                    persianCalendar1.getPersianDay()
            );
            datePickerDialog.show(((Activity) mContext).getFragmentManager(), "Datepickerdialog");

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
//            Call<RetrofitResponse> call = apiInterface.OrderInfoInsert(callMethod.RetrofitBody(Body_str));


            call = apiInterface.OrderInfoInsert(
                    "OrderInfoInsert",
                    dbh.ReadConfig("BrokerCode"),
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
                    assert response.body() != null;
                    if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                        callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                        dialogProg.dismiss();
                    } else {
                        dialog.dismiss();
                        dialogProg.dismiss();
                        Order_TableActivity activity = (Order_TableActivity) mContext;
                        activity.CallSpinner();
                        lottieok();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                }
            });

        });

        dialog.show();

    }


    public void GoodBoxDialog(Good good, String Flag) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        call = apiInterface.GetDistinctValues("GetDistinctValues", "AppBasket", "Explain", "Where GoodRef=" + good.getGoodCode());
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                assert response.body() != null;
                values_array.clear();
                values_array.add(0, "");
                values = response.body().getValues();
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
            }
        });

        call = apiInterface.OrderGet(
                "OrderGet",
                callMethod.ReadString("AppBasketInfoCode"),
                "3"
        );
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    good_box_items.clear();
                    for (Good g : response.body().getGoods()) {
                        if (g.getGoodCode().equals(good.getGoodCode())) {
                            good_box_items.add(g);
                        }
                    }
                    Order_GoodBoxItemAdapter adapter = new Order_GoodBoxItemAdapter(good_box_items, mContext);
                    rc_orderbox.setLayoutManager(new GridLayoutManager(mContext, 1));
                    rc_orderbox.setAdapter(adapter);
                    rc_orderbox.setItemAnimator(new DefaultItemAnimator());
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
            }
        });


        btn_orderbox.setOnClickListener(v -> {

            String amo = NumberFunctions.EnglishNumber(ed_orderbox_amount.getText().toString());
            String explain = NumberFunctions.EnglishNumber(ed_orderbox_explain.getText().toString());
            if (!amo.equals("")) {
                if (Float.parseFloat(amo) > 0) {

                    good.setAmount(amo);
                    good.setExplain(explain);
                    callMethod.Log("start3= "+good.getRowCode());

                    for (Good goodlikeorder : good_box_items) {
                        callMethod.Log("-----------------------");

                        callMethod.Log("start00= "+goodlikeorder.getRowCode());
                        callMethod.Log("start00= "+explain);
                        callMethod.Log("start00= "+goodlikeorder.getExplain());

                        if (goodlikeorder.getExplain().equals(explain)) {

                            if (goodlikeorder.getFactorCode() == "0") {
                                good.setRowCode(goodlikeorder.getRowCode());
                                if (Flag.equals("0")) {
                                    good.setAmount(String.valueOf(Integer.parseInt(goodlikeorder.getAmount()) + Integer.parseInt(amo)));

                                }

                            } else {
                                good.setRowCode("0");
                            }
                        }

                    }


                    dialogProg();
                    tv_rep.setText(R.string.textvalue_sendinformation);
                    callMethod.Log("start4= "+good.getRowCode());



//                    String Body_str  = "";
//
//
//                    Body_str =callMethod.CreateJson("GoodRef", String.valueOf(good.getGoodCode()), Body_str);
//                    Body_str =callMethod.CreateJson("FacAmount", good.getAmount(), Body_str);
//                    Body_str =callMethod.CreateJson("Price", good.getMaxSellPrice(), Body_str);
//                    Body_str =callMethod.CreateJson("bUnitRef", String.valueOf(good.getGoodUnitRef()), Body_str);
//                    Body_str =callMethod.CreateJson("bRatio", String.valueOf(good.getDefaultUnitValue()), Body_str);
//                    Body_str =callMethod.CreateJson("Explain", good.getExplain(), Body_str);
//                    Body_str =callMethod.CreateJson("UserId", "-2000", Body_str);
//                    Body_str =callMethod.CreateJson("InfoRef", callMethod.ReadString("AppBasketInfoCode"), Body_str);
//                    Body_str =callMethod.CreateJson("RowCode", good.getRowCode(), Body_str);
//
//
//                    call = apiInterface.OrderRowInsert(callMethod.RetrofitBody(Body_str));
                    tv_rep.setText(R.string.textvalue_sendinformation);
                    Call<RetrofitResponse> call = apiInterface.OrderRowInsert("OrderRowInsert",
                            good.getGoodCode()+"",
                            good.getAmount(),
                            good.getMaxSellPrice(),
                            good.getGoodUnitRef()+"",
                            good.getDefaultUnitValue()+"",
                            good.getExplain(),
                            callMethod.ReadString("AppBasketInfoCode"),
                            good.getRowCode()
                    );

                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                            assert response.body() != null;
                            Goods = response.body().getGoods();
                            if (Integer.parseInt(Goods.get(0).getErrCode()) > 0) {
                                callMethod.showToast(Goods.get(0).getErrDesc());
                                dialogProg.dismiss();
                            } else {
                                if (Flag.equals("0")) {
                                    callMethod.showToast(mContext.getString(R.string.textvalue_recorded));
                                    dialog.dismiss();
                                    dialogProg.dismiss();
                                    Order_SearchActivity activity = (Order_SearchActivity) mContext;
                                    activity.RefreshState();
                                } else {
                                    intent = new Intent(mContext, Order_BasketActivity.class);
                                    ((Activity) mContext).finish();
                                    ((Activity) mContext).overridePendingTransition(0, 0);
                                    mContext.startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                            if (Flag.equals("0")) {
                                callMethod.showToast(getString(R.string.textvalue_notrecorded));
                                dialog.dismiss();
                                dialogProg.dismiss();
                                Order_SearchActivity activity = (Order_SearchActivity) mContext;
                                activity.RefreshState();
                            } else {
                                intent = new Intent(mContext, Order_BasketActivity.class);
                                ((Activity) mContext).finish();
                                ((Activity) mContext).overridePendingTransition(0, 0);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                } else {
                    callMethod.showToast(mContext.getString(R.string.textvalue_inserttruenumber));
                }
            } else {
                callMethod.showToast(mContext.getString(R.string.textvalue_insertnumber));
            }
        });
        dialog.show();
    }


    public void OrderToFactor() {
        dialogProg();
        tv_rep.setText(R.string.textvalue_sendinformation);
        Call<RetrofitResponse> call = apiInterface.OrderToFactor(
                "OrderToFactor",
                callMethod.ReadString("AppBasketInfoCode")
        );

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                        callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                        dialogProg.dismiss();
                    } else {
                        //todo dotnet
                        //OrderPrintFactor();
                        order_print.GetHeader_Data("");

                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                intent = new Intent(mContext, Order_BasketActivity.class);
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(intent);
            }
        });

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
//       // call = apiInterface.OrderInfoInsert(callMethod.RetrofitBody(Body_str));
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
//        Call<RetrofitResponse> call = apiInterface.OrderPrintFactor(
//                "OrderPrintFactor",
//                callMethod.ReadString("AppBasketInfoCode")
//        );
//
//        call.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
//                dialogProg.dismiss();
//
//                    call = apiInterface.Order_CanPrint("Order_CanPrint", callMethod.ReadString("AppBasketInfoCode"), "0");
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
//        call = apiInterface.Order_CanPrint("Order_CanPrint", callMethod.ReadString("AppBasketInfoCode"), "1");
//        call.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
//                if (response.isSuccessful()) {
//                    assert response.body() != null;
//                    if (response.body().getText().equals("Done")) {
//
//                        Call<RetrofitResponse> call_Change = apiInterface.OrderChangeTable(
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

        Call<RetrofitResponse> call1 = apiInterface.GetObjectTypeFromDbSetup("GetObjectTypeFromDbSetup", "AppOrder_InfoExplainList");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    objectTypes.clear();
                    values_array.clear();
                    values_array.add(0, "");
                    objectTypes = response.body().getObjectTypes();

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
//             call = apiInterface.OrderInfoInsert(callMethod.RetrofitBody(Body_str));
            call = apiInterface.OrderInfoInsert(
                    "OrderInfoInsert",
                    dbh.ReadConfig("BrokerCode"),
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
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
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
                        dialog.dismiss();
                        dialogProg.dismiss();
                    }
                });
            } else {
                dialog.dismiss();
            }
        });

    }


    public void BasketInfoExplainBeforOrder() {


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        Call<RetrofitResponse> call1 = apiInterface.GetObjectTypeFromDbSetup("GetObjectTypeFromDbSetup", "AppOrder_InfoExplainList");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    objectTypes.clear();
                    values_array.clear();
                    values_array.add(0, "");
                    objectTypes = response.body().getObjectTypes();

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

            }
        });





        explain_btn.setOnClickListener(view -> {

            if(explain_tv.getText().toString().length()>0) {
                dialogProg();
                tv_rep.setText(R.string.textvalue_sendinformation);

//
//
//
//                String Body_str  = "";
//
//                Body_str =callMethod.CreateJson("AppBasketInfoCode", callMethod.ReadString("AppBasketInfoCode"), Body_str);
//                Body_str =callMethod.CreateJson("Explain", NumberFunctions.EnglishNumber(explain_tv.getText().toString()), Body_str);
//
//
//                call = apiInterface.OrderEditInfoExplain(callMethod.RetrofitBody(Body_str));
                call = apiInterface.OrderEditInfoExplain(
                        "OrderEditInfoExplain",
                        callMethod.ReadString("AppBasketInfoCode"),
                        NumberFunctions.EnglishNumber(explain_tv.getText().toString())
                );

                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {

                                dialogProg.dismiss();
                            } else {
                                OrderToFactor();
                                dialog.dismiss();
                                dialogProg.dismiss();
                                callMethod.showToast(mContext.getString(R.string.textvalue_recorded));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                        dialog.dismiss();
                        dialogProg.dismiss();
                    }
                });
            }else{
                OrderToFactor();
            }
        });

    }


    public void lottieok() {

        Dialog dialog1 = new Dialog(mContext);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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
