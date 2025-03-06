package com.kits.kowsarapp.fragment.ocr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.ocr.Ocr_ConfirmActivity;
import com.kits.kowsarapp.activity.ocr.Ocr_NavActivity;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Print;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_PackFragment extends Fragment{


    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;
    Ocr_DBH dbh;
    Ocr_Action action;
    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    LinearLayoutCompat ll_main;
    LinearLayoutCompat ll_title;
    LinearLayoutCompat ll_good_body_detail;
    LinearLayoutCompat ll_good_body;
    LinearLayoutCompat ll_factor_summary;
    LinearLayoutCompat ll_send_confirm;
    CallMethod callMethod;
    ViewPager ViewPager;
    Button btn_send;
    Button btn_confirm;
    Button btn_shortage;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    ArrayList<Ocr_Good> ocr_goods;
    Factor factor;
    String BarcodeScan;
    Integer lastCunter = 0;
    Intent intent;
    int width = 1;
    Handler handler;
    TextView tv_company;
    TextView tv_customername;
    TextView tv_factorcode;
    TextView tv_factordate;
    TextView tv_address;
    TextView tv_phone;
    TextView tv_total_amount;
    TextView tv_total_price;
    View view;

    LinearLayoutCompat ll_shortage_print;

    Button  btn_print;


    TextView tv_factorexplain;
    ArrayList<Ocr_Good> empty_goods_visible = new ArrayList<>();

    ArrayList<String> Array_GoodCodesCheck = new ArrayList<>();

    int firsttry = 0;

    Ocr_Print print;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ocr_fragment_pack, container, false);
        ll_main = view.findViewById(R.id.ocr_pack_f_layout);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

        callMethod = new CallMethod(requireActivity());
        dbh = new Ocr_DBH(requireActivity(), callMethod.ReadString("DatabaseName"));
        action=new Ocr_Action(requireActivity());
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
        handler = new Handler();
            for (final String[] ignored : arraygood_shortage) {
                arraygood_shortage.add(new String[]{"goodcode", "amount "});
            }
        DisplayMetrics metrics = new DisplayMetrics();
        view.getDisplay().getMetrics(metrics);
        width = metrics.widthPixels;

        CreateView_Pack();
        }catch (Exception e){
            Log.e("kowsarwwww ",e.getMessage());
        }
    }


    @SuppressLint("RtlHardcoded")
    public void CreateView_Pack() {
        ll_main.removeAllViews();
        Newview();
        setLayoutParams();

        setOrientation();
        setLayoutDirection();
        setGravity();
        setTextSize();
        setBackgroundResource();
        setTextColor();
        setPadding();



        ll_send_confirm.setWeightSum(2);
        ll_shortage_print.setWeightSum(2);




        tv_company.setText(NumberFunctions.PerisanNumber("بخش بسته بندی"));
        tv_customername.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
        tv_factorcode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));
        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));

        tv_factorexplain.setText(NumberFunctions.PerisanNumber(" توضیحات :   " + factor.getExplain()));



        tv_address.setText(NumberFunctions.PerisanNumber(" آدرس : " + factor.getAddress()));
        tv_phone.setText(NumberFunctions.PerisanNumber(" تلفن تماس : " + factor.getPhone()));
        tv_total_amount.setText(NumberFunctions.PerisanNumber(" تعداد کل:   " + factor.getSumAmount()));
        tv_total_price.setText(NumberFunctions.PerisanNumber(" قیمت کل : " + decimalFormat.format(Integer.valueOf(factor.getSumPrice())) + " ریال"));

        btn_confirm.setText("تاییده بخش");
        btn_send.setText("ارسال تاییده");
        btn_shortage.setText("اعلام کسر موجودی");
        btn_print.setText("پرینت فاکتور");





        if (!factor.getNewSumPrice().equals(factor.getSumPrice())) {
            TextView tv_total_newprice = new TextView(requireActivity().getApplicationContext());
            tv_total_newprice.setText(NumberFunctions.PerisanNumber(" قیمت کل(جدید) : " + decimalFormat.format(Integer.valueOf(factor.getNewSumPrice())) + " ریال"));
            tv_total_newprice.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            tv_total_newprice.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            tv_total_newprice.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_total_newprice.setGravity(Gravity.RIGHT);

            ll_factor_summary.addView(tv_total_newprice);
        }

        int countergood = 0;
        for (Ocr_Good Singlegood : ocr_goods) {
            countergood++;
            ll_good_body_detail.addView(CreateGoodViewForPack(Singlegood, countergood));

        }

        ll_title.addView(tv_company);
        ll_title.addView(tv_customername);
        ll_title.addView(tv_factorcode);
        ll_title.addView(tv_factordate);
        ll_title.addView(tv_factorexplain);
        ll_title.addView(tv_address);
        ll_title.addView(tv_phone);
        ll_title.addView(ViewPager);

        ll_send_confirm.addView(btn_confirm);
        ll_send_confirm.addView(btn_send);


        ll_good_body.addView(ll_good_body_detail);

        ll_factor_summary.addView(tv_total_amount);
        ll_factor_summary.addView(tv_total_price);

        ll_main.addView(ll_title);
        ll_main.addView(ll_good_body);

        if (callMethod.ReadString("Category").equals("3")) {
            ll_shortage_print.addView(btn_shortage);
            ll_shortage_print.addView(btn_print);
            ll_main.addView(ll_shortage_print);
        }

        ll_main.addView(ll_factor_summary);
        ll_main.addView(ll_send_confirm);
        ConfirmCount_Pack();

        btn_shortage.setOnClickListener(v -> CreateView_shortage());

        btn_send.setOnClickListener(v -> action.Pack_detail(factor));
        btn_print.setOnClickListener(v -> print.Printing(factor,empty_goods_visible,"0","0"));


        btn_confirm.setOnClickListener(v -> {

            int b = Array_GoodCodesCheck.size();
            final int[] conter = {0};

            for (String goodchecks : Array_GoodCodesCheck) {

                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call=apiInterface.OcrControlled(
                            "OcrControlled",
                            goodchecks,
                            "2",
                            callMethod.ReadString("JobPersonRef")
                    );
                }else{
                    call=secendApiInterface.OcrControlled(
                            "OcrControlled",
                            goodchecks,
                            "2",
                            callMethod.ReadString("JobPersonRef")
                    );
                }

                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            conter[0] = conter[0] + 1;
                            if (conter[0] == b) {
                                intent = new Intent(requireActivity(), Ocr_ConfirmActivity.class);

                                intent.putExtra("ScanResponse", BarcodeScan);
                                intent.putExtra("State", "1");
                                intent.putExtra("FactorImage", "");
                                startActivity(intent);
                                requireActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        Log.e("", t.getMessage());
                    }
                });


            }

        });

        if (callMethod.ReadString("Category").equals("1")) {
            btn_send.setVisibility(View.GONE);
            btn_confirm.setText("بازگشت به صفحه اصلی");
            btn_confirm.setOnClickListener(v -> {
                intent = new Intent(requireActivity(), Ocr_NavActivity.class);
                startActivity(intent);
                requireActivity().finish();
            });
        }


    }

    @SuppressLint("RtlHardcoded")
    public View CreateGoodViewForPack(Ocr_Good good, int countergood) {
        LinearLayoutCompat ll_factor_row = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_details = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_radif_check = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_name_price = new LinearLayoutCompat(requireActivity().getApplicationContext());

        ViewPager vp_radif_name = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_rows = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_name_amount = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_amount_price = new ViewPager(requireActivity().getApplicationContext());

        TextView tv_gap = new TextView(requireActivity().getApplicationContext());
        TextView tv_goodname = new TextView(requireActivity().getApplicationContext());
        TextView tv_amount = new TextView(requireActivity().getApplicationContext());
        TextView tv_price = new TextView(requireActivity().getApplicationContext());

        CheckBox checkBox = new CheckBox(requireActivity());

        ll_factor_row.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_details.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_radif_check.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 7.7));
        ll_name_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 1.3));
        vp_rows.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
        vp_radif_name.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_name_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_amount_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(20, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 1.5));
        tv_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 4));
        tv_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 3.5));

        checkBox.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 4));

        ll_details.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_radif_check.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_name_price.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ll_factor_row.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_details.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_radif_check.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_name_price.setOrientation(LinearLayoutCompat.HORIZONTAL);

        ll_details.setWeightSum(9);
        ll_radif_check.setWeightSum(5);
        ll_name_price.setWeightSum(9);

        vp_name_amount.setBackgroundResource(R.color.colorPrimaryDark);
        vp_amount_price.setBackgroundResource(R.color.colorPrimaryDark);
        vp_rows.setBackgroundResource(R.color.colorPrimaryDark);
        vp_radif_name.setBackgroundResource(R.color.colorPrimaryDark);

        ll_radif_check.setGravity(Gravity.CENTER);
        checkBox.setGravity(Gravity.CENTER_VERTICAL);
        tv_gap.setGravity(Gravity.CENTER);
        tv_goodname.setGravity(Gravity.RIGHT);
        tv_amount.setGravity(Gravity.CENTER);
        tv_price.setGravity(Gravity.CENTER);
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize"))-10);
        checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize"))+3);
        tv_amount.setTypeface(null, Typeface.BOLD);

        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));

        checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(countergood)));
        tv_goodname.setText(NumberFunctions.PerisanNumber(good.getGoodName()));
        tv_amount.setText(NumberFunctions.PerisanNumber(good.getFacAmount()));
        tv_price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.valueOf(good.getGoodMaxSellPrice()))));


        tv_gap.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        checkBox.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_goodname.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_amount.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_price.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));

        tv_price.setPadding(0, 10, 0, 10);
        tv_goodname.setPadding(0, 10, 5, 10);

        ll_radif_check.addView(tv_gap);
        ll_radif_check.addView(checkBox);

        ll_name_price.addView(tv_goodname);
        ll_name_price.addView(vp_name_amount);
        ll_name_price.addView(tv_amount);
        ll_name_price.addView(vp_amount_price);
        ll_name_price.addView(tv_price);

        ll_details.addView(ll_radif_check);
        ll_details.addView(vp_radif_name);
        ll_details.addView(ll_name_price);

        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

            try {
                if (good.getMinAmount().equals("1.000")){
                    ll_details.setBackgroundColor(requireActivity().getColor(R.color.red_100));
                }
            }catch (Exception e){

                callMethod.Log(e.getMessage());
            }


        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("Ocr Gostaresh")){

            callMethod.Log("Gostaresh");


        }else{
            callMethod.Log("defult");


        }



        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(vp_rows);


        int correct_row = countergood - 1;

        //if (ocr_goods.get(fa).getAppRowIsPacked().equals("True")) {
        if (ocr_goods.get(correct_row).getAppRowIsPacked().equals("1")) {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        } else {
            checkBox.setEnabled(!callMethod.ReadBoolan("JustScanner"));

        }
       // if (callMethod.ReadString("Category").equals("True")) {
        if (callMethod.ReadString("Category").equals("1")) {
            checkBox.setVisibility(View.GONE);
        }
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ocr_goods.get(correct_row).setAppRowIsPacked("1");
                if (!Array_GoodCodesCheck.contains(ocr_goods.get(correct_row).getAppOCRFactorRowCode())) {
                    Array_GoodCodesCheck.add(ocr_goods.get(correct_row).getAppOCRFactorRowCode());
                }
            } else {
                ocr_goods.get(correct_row).setAppRowIsPacked("0");
                Array_GoodCodesCheck.remove(ocr_goods.get(correct_row).getAppOCRFactorRowCode());
            }


        });


        tv_goodname.setOnClickListener(v -> image_zome_view((ocr_goods.get(correct_row).getGoodCode())));


        return ll_factor_row;
    }


    @SuppressLint("RtlHardcoded")
    public void CreateView_shortage() {
        ll_main.removeAllViews();

        Newview();
        setLayoutParams();
        setOrientation();
        setLayoutDirection();
        setGravity();
        setTextSize();
        setBackgroundResource();
        setTextColor();
        setPadding();

        ll_send_confirm.setWeightSum(2);


        tv_company.setText(NumberFunctions.PerisanNumber("بخش بسته بندی"));
        tv_customername.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
        tv_factorcode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));
        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));
        tv_factorexplain.setText(NumberFunctions.PerisanNumber(" توضیحات :   " + factor.getFactorDate()));


        btn_confirm.setText("ارسال کسری");
        btn_send.setText("بازگشت");
        btn_shortage.setText("اعلام کسر موجودی");
        btn_shortage.setTextSize(20);


        int countergood = 0;
        for (Ocr_Good singlegood : ocr_goods) {
            countergood++;

            //if (singlegood.getAppRowIsPacked().equals("False")) {
            if (singlegood.getAppRowIsPacked().equals("0")) {
                ll_good_body_detail.addView(CreateGoodViewForshortage(singlegood, countergood));

            }
        }

        ll_title.addView(tv_company);
        ll_title.addView(tv_customername);
        ll_title.addView(tv_factorcode);
        ll_title.addView(tv_factordate);
        ll_title.addView(tv_factorexplain);

        ll_title.addView(ViewPager);
        ll_send_confirm.addView(btn_confirm);
        ll_send_confirm.addView(btn_send);


        ll_good_body.addView(ll_good_body_detail);

        ll_main.addView(ll_title);
        ll_main.addView(ll_good_body);
//        ll_main.addView(ll_factor_summary);
        ll_main.addView(ll_send_confirm);

        btn_shortage.setOnClickListener(v -> {

        });

        btn_send.setOnClickListener(v -> requireActivity().finish());

        btn_confirm.setBackgroundResource(R.color.red_500);
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_confirm.setEnabled(true);

        btn_send.setBackgroundResource(R.color.green_500);
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_confirm.setEnabled(true);

        btn_confirm.setOnClickListener(v -> {


            for (String[] goodchecks : arraygood_shortage) {

                Call<RetrofitResponse> call;
//                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//                    call=apiInterface.SetGoodShortage("ocrShortage", goodchecks[0], goodchecks[1]);
//                }else{
//                    call=secendApiInterface.SetGoodShortage("ocrShortage", goodchecks[0], goodchecks[1]);
//                }
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call=apiInterface.GoodShortage("ocrShortage", goodchecks[0], goodchecks[1]);
                }else{
                    call=secendApiInterface.GoodShortage("ocrShortage", goodchecks[0], goodchecks[1]);
                }

                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            lastCunter++;

                            if (lastCunter == arraygood_shortage.size()) {
                                requireActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                        Log.e("123", t.getMessage());
                    }
                });
            }


        });

        if (callMethod.ReadString("Category").equals("1")) {
            btn_send.setVisibility(View.GONE);
            btn_confirm.setText("بازگشت به صفحه اصلی");
            btn_confirm.setOnClickListener(v -> {
                intent = new Intent(requireActivity(), Ocr_NavActivity.class);
                startActivity(intent);
                requireActivity().finish();
            });
        }


    }

    @SuppressLint("RtlHardcoded")
    public View CreateGoodViewForshortage(@NonNull Ocr_Good good, int contergood) {
        arraygood_shortage.clear();
        arraygood_shortage.add(new String[]{good.getAppOCRFactorRowCode(), good.getFacAmount()});

        LinearLayoutCompat ll_factor_row = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_details = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_radif_check = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_name_price = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ViewPager vp_radif_name = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_rows = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_name_amount = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_amount_price = new ViewPager(requireActivity().getApplicationContext());
        TextView tv_gap = new TextView(requireActivity().getApplicationContext());
        TextView tv_goodname = new TextView(requireActivity().getApplicationContext());
        TextView tv_amount = new TextView(requireActivity().getApplicationContext());
        EditText et_amountshortage = new EditText(requireActivity().getApplicationContext());

        CheckBox checkBox = new MaterialCheckBox(requireActivity());

        ll_factor_row.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_details.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_radif_check.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 7.7));
        ll_name_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 1.3));
        vp_rows.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
        vp_radif_name.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_name_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_amount_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(20, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 1.5));
        tv_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 4));
        et_amountshortage.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 3.5));

        checkBox.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 4));

        ll_details.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_radif_check.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_name_price.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ll_factor_row.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_details.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_radif_check.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_name_price.setOrientation(LinearLayoutCompat.HORIZONTAL);

        ll_details.setWeightSum(9);
        ll_radif_check.setWeightSum(5);
        ll_name_price.setWeightSum(9);

        vp_name_amount.setBackgroundResource(R.color.colorPrimaryDark);
        vp_amount_price.setBackgroundResource(R.color.colorPrimaryDark);
        vp_rows.setBackgroundResource(R.color.colorPrimaryDark);
        vp_radif_name.setBackgroundResource(R.color.colorPrimaryDark);

        ll_radif_check.setGravity(Gravity.CENTER);
        checkBox.setGravity(Gravity.CENTER_VERTICAL);
        tv_gap.setGravity(Gravity.CENTER);
        tv_goodname.setGravity(Gravity.RIGHT);
        tv_amount.setGravity(Gravity.CENTER);
        et_amountshortage.setGravity(Gravity.CENTER);

        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        et_amountshortage.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));


        checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(contergood)));
        tv_goodname.setText(NumberFunctions.PerisanNumber(good.getGoodName()));
        tv_amount.setText(NumberFunctions.PerisanNumber(good.getFacAmount()));
        et_amountshortage.setHint(good.getFacAmount());
        et_amountshortage.setInputType(InputType.TYPE_CLASS_NUMBER);


        tv_gap.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        checkBox.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_goodname.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_amount.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        et_amountshortage.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));

        et_amountshortage.setPadding(0, 10, 0, 10);
        tv_goodname.setPadding(0, 10, 5, 10);

        ll_radif_check.addView(tv_gap);
        ll_radif_check.addView(checkBox);

        ll_name_price.addView(tv_goodname);
        ll_name_price.addView(vp_name_amount);
        ll_name_price.addView(tv_amount);
        ll_name_price.addView(vp_amount_price);
        ll_name_price.addView(et_amountshortage);

        ll_radif_check.setVisibility(View.INVISIBLE);
        ll_details.addView(ll_radif_check);
        ll_details.addView(vp_radif_name);
        ll_details.addView(ll_name_price);

        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
            if (contergood%2==0){
                ll_details.setBackgroundColor(requireActivity().getColor(R.color.grey_200));
            }
            try {
                if (good.getMinAmount().equals("1.000")){
                    ll_details.setBackgroundColor(requireActivity().getColor(R.color.red_100));
                }
            }catch (Exception e){

                callMethod.Log(e.getMessage());
            }



        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("Ocr Gostaresh")){
            if (contergood%2==0){
                ll_details.setBackgroundColor(requireActivity().getColor(R.color.grey_200));
            }
            callMethod.Log("Gostaresh");

        }else{
            if (contergood%2==0){
                ll_details.setBackgroundColor(requireActivity().getColor(R.color.grey_200));
            }
            callMethod.Log("def");
        }




        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(vp_rows);

        int correct_row = contergood - 1;


        //if (ocr_goods.get(fa).getAppRowIsPacked().equals("True")) {
        if (ocr_goods.get(correct_row).getAppRowIsPacked().equals("1")) {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        } else {

            checkBox.setEnabled(!callMethod.ReadBoolan("JustScanner"));

        }
        //if (callMethod.ReadString("Category").equals("True")) {
        if (callMethod.ReadString("Category").equals("1")) {

            checkBox.setVisibility(View.GONE);
        }
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ocr_goods.get(correct_row).setAppRowIsPacked("1");
                if (!Array_GoodCodesCheck.contains(ocr_goods.get(correct_row).getAppOCRFactorRowCode())) {
                    Array_GoodCodesCheck.add(ocr_goods.get(correct_row).getAppOCRFactorRowCode());
                }
                Array_GoodCodesCheck.add(ocr_goods.get(correct_row).getAppOCRFactorRowCode());
            } else {
                ocr_goods.get(correct_row).setAppRowIsPacked("0");
                Array_GoodCodesCheck.remove(ocr_goods.get(correct_row).getAppOCRFactorRowCode());
            }
        });


        tv_goodname.setOnClickListener(v -> image_zome_view((ocr_goods.get(correct_row).getGoodCode())));

        arraygood_shortage.clear();
        et_amountshortage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                try {
                    if (firsttry == 0) {
                        arraygood_shortage.clear();
                        firsttry = 1;
                    }

                    String newAmount = text.toString();
                    String goodCode = good.getAppOCRFactorRowCode();

                    if (!newAmount.isEmpty()) {
                        int amount = Integer.parseInt(newAmount);

                        if (amount > Integer.parseInt(good.getFacAmount())) {
                            et_amountshortage.setText("");  // مقدار رو پاک کن
                            callMethod.showToast("از مقدار فاکتور بیشتر می باشد");
                        } else {
                            // بررسی کن که آیا این `goodCode` قبلاً در لیست هست؟
                            boolean found = false;
                            for (int i = 0; i < arraygood_shortage.size(); i++) {
                                if (arraygood_shortage.get(i)[0].equals(goodCode)) {
                                    arraygood_shortage.get(i)[1] = newAmount;  // مقدار رو آپدیت کن
                                    found = true;
                                    break;
                                }
                            }

                            // اگر مقدار جدید بود، اضافه کن
                            if (!found) {
                                arraygood_shortage.add(new String[]{goodCode, newAmount});
                            }
                        }

                    }
                } catch (Exception ignored) {}


            }
        });
        return ll_factor_row;
    }

    @SuppressLint("RtlHardcoded")
    public void setGravity() {
        tv_company.setGravity(Gravity.CENTER);
        tv_customername.setGravity(Gravity.RIGHT);
        tv_factorcode.setGravity(Gravity.RIGHT);
        tv_factordate.setGravity(Gravity.RIGHT);
        tv_factorexplain.setGravity(Gravity.RIGHT);
        tv_address.setGravity(Gravity.RIGHT);
        tv_phone.setGravity(Gravity.RIGHT);
        tv_total_amount.setGravity(Gravity.RIGHT);
        tv_total_price.setGravity(Gravity.RIGHT);
        btn_confirm.setGravity(Gravity.CENTER);
        btn_send.setGravity(Gravity.CENTER);
        btn_shortage.setGravity(Gravity.CENTER);
        btn_print.setGravity(Gravity.CENTER);
    }


    public void setTextSize() {

        tv_company.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_customername.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_factorcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_factordate.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_factorexplain.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_phone.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_total_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_total_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        btn_confirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_send.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
    }

    public void setBackgroundResource() {
        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        btn_confirm.setBackgroundResource(R.color.green_800);
        btn_send.setBackgroundResource(R.color.red_700);
        btn_shortage.setBackgroundResource(R.color.orange_500);
        btn_print.setBackgroundResource(R.color.blue_500);

    }
    public void setTextColor() {
        tv_company.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_customername.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factorcode.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factordate.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factorexplain.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_address.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_phone.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_total_amount.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_total_price.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_send.setTextColor(requireActivity().getColor(R.color.white));
        btn_shortage.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        btn_print.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));

    }


    public void setPadding() {
        tv_company.setPadding(0, 0, 30, 20);
        tv_customername.setPadding(0, 0, 30, 20);
        tv_factorcode.setPadding(0, 0, 30, 20);
        tv_factordate.setPadding(0, 0, 30, 20);
        tv_factorexplain.setPadding(0, 0, 30, 20);
        tv_address.setPadding(0, 0, 30, 20);
        tv_phone.setPadding(0, 0, 30, 20);
        tv_total_amount.setPadding(0, 0, 30, 20);
        tv_total_price.setPadding(0, 0, 30, 20);
        btn_confirm.setPadding(0, 0, 30, 20);
        btn_send.setPadding(0, 0, 30, 20);
        btn_shortage.setPadding(0, 0, 30, 20);
        btn_print.setPadding(0, 0, 30, 20);
    }

    public void setLayoutParams() {
        ll_title.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_factor_summary.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_send_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_shortage_print.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

        tv_company.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_customername.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factorcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factordate.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factorexplain.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_address.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_phone.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        btn_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_send.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_shortage.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_print.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));

        tv_total_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_total_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 3));


    }

    public void Newview() {
        ll_title = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body_detail = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_factor_summary = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_send_confirm = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_shortage_print = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ViewPager = new ViewPager(requireActivity().getApplicationContext());
        tv_company = new TextView(requireActivity().getApplicationContext());
        tv_customername = new TextView(requireActivity().getApplicationContext());
        tv_factorcode = new TextView(requireActivity().getApplicationContext());
        tv_factordate = new TextView(requireActivity().getApplicationContext());
        tv_factorexplain = new TextView(requireActivity().getApplicationContext());
        tv_address = new TextView(requireActivity().getApplicationContext());
        tv_phone = new TextView(requireActivity().getApplicationContext());
        tv_total_amount = new TextView(requireActivity().getApplicationContext());
        tv_total_price = new TextView(requireActivity().getApplicationContext());
        btn_confirm = new Button(requireActivity().getApplicationContext());
        btn_send = new Button(requireActivity().getApplicationContext());
        btn_shortage = new Button(requireActivity().getApplicationContext());
        btn_print = new Button(requireActivity().getApplicationContext());


    }

    public void setOrientation() {

        ll_title.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_good_body.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_good_body_detail.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_factor_summary.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_send_confirm.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_shortage_print.setOrientation(LinearLayoutCompat.HORIZONTAL);
    }

    public void setLayoutDirection() {
        ll_title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_factor_summary.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_send_confirm.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_shortage_print.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    public void ConfirmCount_Pack() {
        int ConfirmCounter = 0;
        for (Ocr_Good g : ocr_goods) {
            //if (g.getAppRowIsPacked().equals("True")) {
            if (g.getAppRowIsPacked().equals("1")) {
                ConfirmCounter++;
            }
        }
        if (ocr_goods.size() == ConfirmCounter) {
            btn_confirm.setBackgroundResource(R.color.grey_60);
            btn_confirm.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            btn_confirm.setEnabled(false);
            callMethod.showToast("اماده ارسال می باشد");
        } else {
            btn_send.setBackgroundResource(R.color.grey_60);
            btn_send.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            btn_send.setEnabled(false);
        }
    }


    public void image_zome_view(String GoodCode) {

        action.good_detail(GoodCode);

    }

    public Factor getFactor() {
        return factor;
    }

    public void setFactor(Factor factor) {
        this.factor = factor;
    }

    public ArrayList<Ocr_Good> getGoods() {
        return ocr_goods;
    }

    public void setocr_Goods(ArrayList<Ocr_Good> ocr_goods) {
        this.ocr_goods = ocr_goods;
    }

    public String getBarcodeScan() {
        return BarcodeScan;
    }

    public void setBarcodeScan(String barcodeScan) {
        BarcodeScan = barcodeScan;
    }
}