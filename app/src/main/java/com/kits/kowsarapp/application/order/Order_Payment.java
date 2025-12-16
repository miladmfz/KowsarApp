package com.kits.kowsarapp.application.order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.gson.Gson;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.NetworkUtils;
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
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_Payment extends Activity {

    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    ThirdPartyRequest BehPardakht_pos_request = new ThirdPartyRequest();
    ThirdPartyResult BehPardakht_pos_result = new ThirdPartyResult();
    Order_BasketInfo BehPardakht_basketInfo=new Order_BasketInfo();
    Context mContext;
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
    String takhfif="0";
    String payment_mablagh_tosend="0";
    String payment_mablagh_incrise="0";
    String payment_mablagh_decrise="0";



    public Order_Payment(Context mContext) {
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
        dialogProg.setContentView(R.layout.order_spinner_box);
        tv_rep = dialogProg.findViewById(R.id.ord_spinner_text);
        dialogProg.show();
    }


    public void BasketInfopayment(Order_BasketInfo basketInfo) {

        BehPardakht_basketInfo=basketInfo;
        payment_type="cash";
        payment_mablagh_tosend="0";
        payment_mablagh_incrise="0";
        payment_mablagh_decrise="0";
        totalprice="0";

        dialog_payment = new Dialog(mContext);

        dialog_payment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog_payment.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog_payment.setContentView(R.layout.order_payment_box);

        Button btn_payment = dialog_payment.findViewById(R.id.ord_payment_b_btn_final);
        Button btn_tocash = dialog_payment.findViewById(R.id.ord_payment_b_btn_cash);
        Button btn_topos = dialog_payment.findViewById(R.id.ord_payment_b_btn_pos);

        LinearLayoutCompat ll_edpay = dialog_payment.findViewById(R.id.ord_payment_b_ll_edpay);
        LinearLayoutCompat ll_edlable = dialog_payment.findViewById(R.id.ord_payment_b_ll_edlable);
        LinearLayoutCompat ll_notreceived = dialog_payment.findViewById(R.id.ord_payment_b_ll_notreceived);
        LinearLayoutCompat ll_received = dialog_payment.findViewById(R.id.ord_payment_b_ll_received);
        LinearLayoutCompat ll_cashtopay = dialog_payment.findViewById(R.id.ord_payment_b_ll_cashtopay);
        LinearLayoutCompat ll_postopay = dialog_payment.findViewById(R.id.ord_payment_b_ll_postopay);
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
        EditText ed_payment_postopay = dialog_payment.findViewById(R.id.ord_payment_b_postopay);

        dialog_payment.show();


        if (callMethod.ReadBoolan("PosPayment")){
            payment_type="pos";
            ll_cashtopay.setVisibility(View.GONE);
            ll_postopay.setVisibility(View.VISIBLE);
            btn_topos.setBackgroundColor(mContext.getResources().getColor(R.color.blue_500));
            btn_tocash.setBackgroundColor(mContext.getResources().getColor(R.color.gray_secondary));
        }else{
            ll_cashtopay.setVisibility(View.VISIBLE);
            ll_postopay.setVisibility(View.GONE);
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
            ll_postopay.setVisibility(View.GONE);
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

            ll_cashtopay.setVisibility(View.VISIBLE);
            ll_postopay.setVisibility(View.GONE);

        });

        btn_topos.setOnClickListener(v -> {

            ll_cashtopay.setVisibility(View.GONE);
            ll_postopay.setVisibility(View.VISIBLE);

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
        ed_payment_postopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));

        ed_payment_selloff.setText(NumberFunctions.PerisanNumber("0"));

        ed_payment_mablagh.setOnClickListener(v -> ed_payment_mablagh.selectAll());
        ed_payment_selloff.setOnClickListener(v -> ed_payment_selloff.selectAll());
        ed_payment_cashtopay.setOnClickListener(v -> ed_payment_cashtopay.selectAll());
        ed_payment_postopay.setOnClickListener(v -> ed_payment_postopay.selectAll());

        if (Integer.parseInt(basketInfo.getReceived())>0){
            ed_payment_cashtopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(basketInfo.getNotReceived())));
            ed_payment_postopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(basketInfo.getNotReceived())));
        }

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
                        ed_payment_postopay.setText(NewPrice);

                    } catch (Exception e) {
                        ed_payment_mablagh.setText(totalprice);
                        ed_payment_cashtopay.setText(totalprice);
                        ed_payment_postopay.setText(totalprice);
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
                        }else if (selloff<0){
                            selloff = 0;
                            ed_payment_selloff.setText(NumberFunctions.PerisanNumber(String.valueOf(selloff)));
                            ed_payment_selloff.setError("حداقل تخفیف");
                        }

                        long sellpricenew;
                        try {
                            sellpricenew = (long) (Integer.parseInt(totalprice) - ((Integer.parseInt(totalprice) * selloff / 100)));

                        }catch (Exception e){
                            sellpricenew = (long) Integer.parseInt(totalprice);
                        }

                        ed_payment_mablagh.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sellpricenew)));
                        ed_payment_cashtopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sellpricenew)));
                        ed_payment_postopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sellpricenew)));
                    } catch (Exception e) {
                        ed_payment_mablagh.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));
                        ed_payment_cashtopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));
                        ed_payment_postopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));

                    }

                }

            }
        });


        btn_payment.setOnClickListener(v -> {

            callMethod.Log("0");

            Call<RetrofitResponse> call_payment = null;

            if (Integer.parseInt(basketInfo.getReceived())>0){ // Received >0

                if (payment_type.equals("cash")){

                    String DecrementValue_str= basketInfo.getDecrementValue().replace("-","");

                    call_payment = order_apiInterface.Factor_Payment_Cash(
                            "Factor_Payment_Cash"
                            ,basketInfo.getFactorCode()
                            ,basketInfo.getNotReceived()
                            ,"0"
                            ,DecrementValue_str
                    );
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

                        }
                    });

                }else{

                    if (Integer.parseInt(basketInfo.getReceived())>0){
                        startPosPayment(basketInfo.getNotReceived());

                    }else{
                        startPosPayment(NumberFunctions.EnglishNumber(ed_payment_postopay.getText().toString().replace(",", "")));

                    }


                }

            }
            else
            { // Received == 0



                takhfif = String.valueOf((((long) Integer.parseInt(totalprice) * (Integer.parseInt(NumberFunctions.EnglishNumber(ed_payment_selloff.getText().toString()))) / 100)));

                if (payment_type.equals("cash")){

                    call_payment = order_apiInterface.Factor_Payment_Cash(
                            "Factor_Payment_Cash"
                            ,basketInfo.getFactorCode()
                            ,NumberFunctions.EnglishNumber(ed_payment_cashtopay.getText().toString().replace(",", ""))
                            ,"0"
                            , String.valueOf(takhfif)
                    );

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

                        }
                    });
                }else{

                    if (Integer.parseInt(basketInfo.getReceived())>0){
                        startPosPayment(basketInfo.getNotReceived());

                    }else{
                        startPosPayment(NumberFunctions.EnglishNumber(ed_payment_postopay.getText().toString().replace(",", "")));

                    }

                }



            }

        });

    }






    public void BasketInfopayment1(Order_BasketInfo basketInfo) {

        BehPardakht_basketInfo=basketInfo;
        payment_type="cash";
        payment_mablagh_tosend="0";
        payment_mablagh_incrise="0";
        payment_mablagh_decrise="0";
        totalprice="0";

        dialog_payment = new Dialog(mContext);

        dialog_payment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog_payment.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog_payment.setContentView(R.layout.order_payment_box);

        Button btn_payment = dialog_payment.findViewById(R.id.ord_payment_b_btn_final);
        Button btn_tocash = dialog_payment.findViewById(R.id.ord_payment_b_btn_cash);
        Button btn_topos = dialog_payment.findViewById(R.id.ord_payment_b_btn_pos);

        LinearLayoutCompat ll_edpay = dialog_payment.findViewById(R.id.ord_payment_b_ll_edpay);
        LinearLayoutCompat ll_edlable = dialog_payment.findViewById(R.id.ord_payment_b_ll_edlable);
        LinearLayoutCompat ll_notreceived = dialog_payment.findViewById(R.id.ord_payment_b_ll_notreceived);
        LinearLayoutCompat ll_received = dialog_payment.findViewById(R.id.ord_payment_b_ll_received);
        LinearLayoutCompat ll_cashtopay = dialog_payment.findViewById(R.id.ord_payment_b_ll_cashtopay);
        LinearLayoutCompat ll_postopay = dialog_payment.findViewById(R.id.ord_payment_b_ll_postopay);
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
        EditText ed_payment_postopay = dialog_payment.findViewById(R.id.ord_payment_b_postopay);

        dialog_payment.show();


        if (callMethod.ReadBoolan("PosPayment")){
            payment_type="pos";
            ll_cashtopay.setVisibility(View.GONE);
            ll_postopay.setVisibility(View.VISIBLE);
            btn_topos.setBackgroundColor(mContext.getResources().getColor(R.color.blue_500));
            btn_tocash.setBackgroundColor(mContext.getResources().getColor(R.color.gray_secondary));
        }else{
            ll_cashtopay.setVisibility(View.VISIBLE);
            ll_postopay.setVisibility(View.GONE);
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
            ll_postopay.setVisibility(View.GONE);
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
                ll_postopay.setVisibility(View.GONE);
            }else{
                ll_cashtopay.setVisibility(View.GONE);
                ll_postopay.setVisibility(View.VISIBLE);
            }
        });

        btn_topos.setOnClickListener(v -> {

            if (Integer.parseInt(basketInfo.getReceived())>0){
                ll_cashtopay.setVisibility(View.GONE);
                ll_postopay.setVisibility(View.GONE);
            }else{
                ll_cashtopay.setVisibility(View.GONE);
                ll_postopay.setVisibility(View.VISIBLE);
            }

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
        ed_payment_postopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));

        ed_payment_selloff.setText(NumberFunctions.PerisanNumber("0"));

        ed_payment_mablagh.setOnClickListener(v -> ed_payment_mablagh.selectAll());
        ed_payment_selloff.setOnClickListener(v -> ed_payment_selloff.selectAll());
        ed_payment_cashtopay.setOnClickListener(v -> ed_payment_cashtopay.selectAll());
        ed_payment_postopay.setOnClickListener(v -> ed_payment_postopay.selectAll());



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
                        ed_payment_postopay.setText(NewPrice);

                    } catch (Exception e) {
                        ed_payment_mablagh.setText(totalprice);
                        ed_payment_cashtopay.setText(totalprice);
                        ed_payment_postopay.setText(totalprice);
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
                        }else if (selloff<0){
                            selloff = 0;
                            ed_payment_selloff.setText(NumberFunctions.PerisanNumber(String.valueOf(selloff)));
                            ed_payment_selloff.setError("حداقل تخفیف");
                        }

                        long sellpricenew;
                        try {
                            sellpricenew = (long) (Integer.parseInt(totalprice) - ((Integer.parseInt(totalprice) * selloff / 100)));

                        }catch (Exception e){
                            sellpricenew = (long) Integer.parseInt(totalprice);
                        }

                        ed_payment_mablagh.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sellpricenew)));
                        ed_payment_cashtopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sellpricenew)));
                        ed_payment_postopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sellpricenew)));
                    } catch (Exception e) {
                        ed_payment_mablagh.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));
                        ed_payment_cashtopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));
                        ed_payment_postopay.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(totalprice))));

                    }

                }

            }
        });


        btn_payment.setOnClickListener(v -> {

            callMethod.Log("0");

            Call<RetrofitResponse> call_payment = null;

            if (Integer.parseInt(basketInfo.getReceived())>0){ // Received >0

                if (payment_type.equals("cash")){

                    String DecrementValue_str= basketInfo.getDecrementValue().replace("-","");

                    call_payment = order_apiInterface.Factor_Payment_Cash(
                            "Factor_Payment_Cash"
                            ,basketInfo.getFactorCode()
                            ,String.valueOf(Integer.parseInt(basketInfo.getNotReceived())+Integer.parseInt(basketInfo.getShopNaghdReceive()))
                            ,"0"
                            ,DecrementValue_str
                    );
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

                        }
                    });

                }else{

                    if (Integer.parseInt(basketInfo.getReceived())>0){
                        startPosPayment(basketInfo.getNotReceived());

                    }else{
                        startPosPayment(NumberFunctions.EnglishNumber(ed_payment_postopay.getText().toString().replace(",", "")));

                    }


                }

            }
            else
            { // Received == 0



                long  mablagh = (Integer.parseInt(totalprice) - (((long) Integer.parseInt(totalprice) * (Integer.parseInt(NumberFunctions.EnglishNumber(ed_payment_selloff.getText().toString()))) / 100)));
                long  takhfif = (((long) Integer.parseInt(totalprice) * (Integer.parseInt(NumberFunctions.EnglishNumber(ed_payment_selloff.getText().toString()))) / 100));

//                if (mablagh>Integer.parseInt(totalprice)){
//                    payment_mablagh_incrise=String.valueOf(mablagh-Integer.parseInt(totalprice));
//                }else{
//                    payment_mablagh_decrise=String.valueOf(Integer.parseInt(totalprice)-mablagh);
//                }
                callMethod.Log("3");
                callMethod.Log("3= "+ mablagh);
                callMethod.Log("3= " + takhfif);




                if ((!payment_mablagh_incrise.equals("0"))||(!payment_mablagh_decrise.equals("0"))){
                    callMethod.Log("4");

//                    call_payment_inc_dec = order_apiInterface.Factor_Payment_Cash(
//                            "Factor_Payment_Cash"
//                            ,basketInfo.getFactorCode()
//                            ,NumberFunctions.EnglishNumber(ed_payment_cashtopay.getText().toString().replace(",", ""))
//                            ,payment_mablagh_incrise
//                            ,payment_mablagh_decrise
//                    );


                }else{
                    callMethod.Log("5");

                    if (payment_type.equals("cash")){
                        callMethod.Log("6");

                        call_payment = order_apiInterface.Factor_Payment_Cash(
                                "Factor_Payment_Cash"
                                ,basketInfo.getFactorCode()
                                ,NumberFunctions.EnglishNumber(ed_payment_cashtopay.getText().toString().replace(",", ""))
                                ,"0"
                                ,"0"
                        );
                        if ((!payment_mablagh_incrise.equals("0"))||(!payment_mablagh_decrise.equals("0"))){
                            dialogProg();
                            Call<RetrofitResponse> finalCall_payment = call_payment;
//                            call_payment_inc_dec.enqueue(new Callback<RetrofitResponse>() {
//                                @Override
//                                public void onResponse(@NotNull Call<RetrofitResponse> call1, @NotNull Response<RetrofitResponse> response) {
//                                    if (response.isSuccessful()) {
//                                        dialog_payment.dismiss();
//                                        dialogProg.dismiss();
//                                    }
//                                }
//                                @Override
//                                public void onFailure(@NotNull Call<RetrofitResponse> call1, @NotNull Throwable t) {
//                                    try {
//                                        // 🟢 بررسی وضعیت اتصال
//                                        if (!NetworkUtils.isNetworkAvailable(mContext)) {
//                                            callMethod.showToast("اتصال اینترنت قطع است!");
//                                        } else if (NetworkUtils.isVPNActive()) {
//                                            callMethod.showToast("VPN فعال است، ممکن است ارتباط با سرور مختل شود!");
//                                        } else {
//                                            String serverUrl = callMethod.ReadString("ServerURLUse");
//                                            if (serverUrl != null && !serverUrl.isEmpty() && !NetworkUtils.canReachServer(serverUrl)) {
//                                                callMethod.showToast("سرور در دسترس نیست یا فیلتر شده است!");
//                                            } else {
//                                                callMethod.showToast("مشکل در برقراری ارتباط با سرور برای بارگیری عکس");
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                        callMethod.Log("Network check error: " + e.getMessage());
//                                        callMethod.showToast("خطا در بررسی وضعیت شبکه");
//                                    }                        }
//                            });

                        }
                        else{
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
                                    try {
                                        // 🟢 بررسی وضعیت اتصال
                                        if (!NetworkUtils.isNetworkAvailable(mContext)) {
                                            callMethod.showToast("اتصال اینترنت قطع است!");
                                        } else if (NetworkUtils.isVPNActive()) {
                                            callMethod.showToast("VPN فعال است، ممکن است ارتباط با سرور مختل شود!");
                                        } else {
                                            String serverUrl = callMethod.ReadString("ServerURLUse");
                                            if (serverUrl != null && !serverUrl.isEmpty() && !NetworkUtils.canReachServer(serverUrl)) {
                                                callMethod.showToast("سرور در دسترس نیست یا فیلتر شده است!");
                                            } else {
                                                callMethod.showToast("مشکل در برقراری ارتباط با سرور برای بارگیری عکس");
                                            }
                                        }
                                    } catch (Exception e) {
                                        callMethod.Log("Network check error: " + e.getMessage());
                                        callMethod.showToast("خطا در بررسی وضعیت شبکه");
                                    }                        }
                            });
                        }
                    }else{
                        callMethod.Log("7");

                        if (Integer.parseInt(basketInfo.getReceived())>0){
                            startPosPayment(basketInfo.getNotReceived());

                        }else{
                            startPosPayment(NumberFunctions.EnglishNumber(ed_payment_postopay.getText().toString().replace(",", "")));

                        }

                    }

                }



            }

        });

    }

    public void  dissmiss_all (){
        dialog_payment.dismiss();
        dialogProg.dismiss();
    }

    public void  BasketInfopayment_request (ThirdPartyResult res,String resultJson){

        Call<RetrofitResponse> call_payment = null;


        call_payment = order_apiInterface.Factor_Payment_Pos_new(
                "Factor_Payment_Pos"
                ,BehPardakht_basketInfo.getFactorCode()
                ,callMethod.ReadString("PosCode")
                ,res.transactionAmount
                ,takhfif
                ,res.sessionId
                ,res.resultCode
                ,res.resultDescription
                ,res.transactionAmount
                ,res.referenceID
                ,res.retrievalReferencedNumber
                ,res.maskedCardNumber
                ,res.terminalID
                ,res.dateOfTransaction
                ,res.timeOfTransaction
                ,res.echoData
                ,resultJson
                ,""
                ,BehPardakht_basketInfo.getAppBasketInfoCode()
        );


        dialogProg();
        if (Integer.parseInt(BehPardakht_basketInfo.getReceived())>0) { // Received >0

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
                    callMethod.Log("Network check error: " + t.getMessage());
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

                    }
                });

        }


    }

    private void startPosPayment(String amount) {

        if (mContext == null) {
            callMethod.Log("startPosPayment: mContext is null");
            return;
        }

        // اگر Activity بود، برای startActivityForResult استفاده می‌کنیم
        final Activity activity = (mContext instanceof Activity) ? (Activity) mContext : null;

        if (activity != null && (activity.isFinishing() || activity.isDestroyed())) {
            callMethod.Log("startPosPayment: activity not in valid state");
            return;
        }

        BehPardakht_pos_request.versionName = "2.0.0";
        BehPardakht_pos_request.sessionId = "Kits_" + System.currentTimeMillis();
        BehPardakht_pos_request.applicationId = 10135;
        BehPardakht_pos_request.totalAmount = amount;
        BehPardakht_pos_request.transactionType = "PURCHASE";
        BehPardakht_pos_request.echoData = "TestEcho";

        String json = gson.toJson(BehPardakht_pos_request);

        Intent posIntent = new Intent("com.behpardakht.thirdparty.payment");
        posIntent.setPackage("com.behpardakht.app");
        posIntent.putExtra("paymentData", json);

        // چک وجود اپ مقصد
        if (posIntent.resolveActivity(mContext.getPackageManager()) == null) {
            callMethod.showToast( "اپ به‌پرداخت نصب نیست یا این Intent را پشتیبانی نمی‌کند");
            return;
        }

        Runnable run = () -> {
            try {
                if (activity != null) {
                    activity.startActivityForResult(posIntent, REQUEST_POS);
                } else {
                    // اگر mContext Activity نبود، فقط میشه startActivity زد (بدون result)
                    posIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(posIntent);
                }
                callMethod.showToast( "در حال ارسال درخواست به پوز...");
            } catch (Exception e) {
                callMethod.showToast( "خطا در اجرای اپ پرداخت: " + e.getMessage());
                callMethod.Log("error = " + e.getMessage());
            }
        };

        // اجرای امن روی UI thread اگر Activity داریم
        if (activity != null) activity.runOnUiThread(run);
        else run.run();
    }








}
