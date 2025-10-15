package com.kits.kowsarapp.fragment.ocr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.kits.kowsarapp.activity.ocr.Ocr_Check_Confirm_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_Collect_Confirm_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_NavActivity;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Print;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Ocr_CollectFragment extends Fragment {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");

    CallMethod callMethod;
    Handler handler;
    Intent intent;
    View view;
    Dialog dialogProg;

    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;
    Ocr_DBH ocr_dbh ;
    Ocr_Action ocr_action;
    Ocr_Print ocr_print;
    Factor factor;

    ArrayList<Ocr_Good> ocr_goods=new ArrayList<>();
    ArrayList<Ocr_Good> ocr_goods_visible=new ArrayList<>();
    ArrayList<Ocr_Good> ocr_goods_scan=new ArrayList<>();

    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    ArrayList<String> GoodCodeCheck=new ArrayList<>();
    ArrayList<String> Array_GoodCodesCheck = new ArrayList<>();

    ScrollView scrollView_main ;
    LinearLayoutCompat ll_main;
    LinearLayoutCompat ll_title;
    LinearLayoutCompat ll_good_body_detail;
    LinearLayoutCompat ll_good_body;
    LinearLayoutCompat ll_factor_summary;
    LinearLayoutCompat ll_send_confirm;
    LinearLayoutCompat ll_shortage_print;

    ViewPager ViewPager;

    Button btn_send;
    Button btn_confirm;
    Button btn_shortage;
    Button btn_set_stack;
    Button btn_print;


    TextView tv_company;
    TextView tv_customername;
    TextView tv_factorcode;
    TextView tv_factordate;
    TextView tv_address;
    TextView tv_phone;
    TextView tv_total_amount;
    TextView tv_total_price;
    TextView tv_appocrfactorexplain;
    TextView tv_factorexplain;

    String state;
    String TcPrintRef;
    String BarcodeScan;

    Integer width=1;
    Integer firsttry = 0;
    Integer lastCunter = 0;
    Integer row_counter;
    Integer conter_confirm = 0;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTcPrintRef() {
        return TcPrintRef;
    }

    public void setTcPrintRef(String tcPrintRef) {
        TcPrintRef = tcPrintRef;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.ocr_fragment_collect, container, false);
        ll_main = view.findViewById(R.id.ocr_collect_f_layout);
        scrollView_main= view.findViewById(R.id.ocr_collect_scrollView_main);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
        callMethod = new CallMethod(requireActivity());
        ocr_dbh = new Ocr_DBH(requireActivity(), callMethod.ReadString("DatabaseName"));
        ocr_action = new Ocr_Action(requireActivity());
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
        handler=new Handler();
        ocr_print = new Ocr_Print(requireActivity());
        for (final String[] ignored : arraygood_shortage) {
            arraygood_shortage.add(new String[]{"goodcode", "amount "});
        }
        DisplayMetrics metrics = new DisplayMetrics();
        view.getDisplay().getMetrics(metrics);
        width =metrics.widthPixels;
        dialogProg = new Dialog(requireActivity());
        dialogProg.setContentView(R.layout.ocr_spinner_box);
        dialogProg.findViewById(R.id.ocr_spinner_text).setVisibility(View.GONE);
        CreateView_Control();
    }catch (Exception e){
            callMethod.Log(e.getMessage());

   }
    }



    @SuppressLint("RtlHardcoded")
    public void CreateView_Control(){

        NewView();
        setLayoutParams();
        setOrientation();
        setLayoutParams();
        setLayoutDirection();
        setGravity();
        setTextSize();
        setBackgroundResource();
        setTextColor();
        setPadding();


        ll_send_confirm.setWeightSum(2);
        ll_shortage_print.setWeightSum(2);


        tv_company.setText(NumberFunctions.PerisanNumber("بخش انبار"));
        tv_appocrfactorexplain.setText(NumberFunctions.PerisanNumber(" انبار :   " + factor.getAppOCRFactorExplain()));
        tv_customername.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
        tv_factorcode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));

        tv_factorcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(callMethod.ReadBoolan("ShowSumAmountHint")){
                    ocr_action.checkSumAmounthint(factor);
                }
                return false;

            }
        });



        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));
        tv_factorexplain.setText(NumberFunctions.PerisanNumber(" توضیحات :   " + factor.getExplain()));


        tv_address.setText(NumberFunctions.PerisanNumber(" آدرس : " + factor.getAddress()));
        tv_phone.setText(NumberFunctions.PerisanNumber(" تلفن تماس : " + factor.getPhone()));
        tv_total_amount.setText(NumberFunctions.PerisanNumber(" تعداد کل:   " + factor.getSumAmount()));
        tv_total_price.setText(NumberFunctions.PerisanNumber(" قیمت کل : " + decimalFormat.format(Integer.valueOf(factor.getSumPrice())) + " ریال"));


        btn_confirm.setText("تاییده بخش");
        btn_send.setText("ارسال تاییده");
        btn_set_stack.setText("آغاز فرآیند انبار");
        btn_shortage.setText("اعلام کسر موجودی");
        btn_print.setText("پرینت فاکتور");




        if(!factor.getNewSumPrice().equals(factor.getSumPrice())){

            TextView tv_total_newprice = new TextView(requireActivity().getApplicationContext());
            tv_total_newprice.setText(NumberFunctions.PerisanNumber(" قیمت کل(جدید) : " + decimalFormat.format(Integer.valueOf(factor.getNewSumPrice())) + " ریال"));
            tv_total_newprice.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            tv_total_newprice.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            tv_total_newprice.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_total_newprice.setGravity(Gravity.RIGHT);

            ll_factor_summary.addView(tv_total_newprice);
        }

        row_counter= 0;
        for (Ocr_Good g : ocr_goods) {


            if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                    callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

                if(callMethod.ReadString("StackCategory").equals("همه")) {
                    ocr_goods_visible.add(g);
                    goodshow(g);
                }else if(g.getGoodExplain4().equals(callMethod.ReadString("StackCategory"))){
                    ocr_goods_visible.add(g);
                    goodshow(g);
                }


            } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
                if(callMethod.ReadString("StackCategory").equals("همه")) {

                    ocr_goods_visible.add(g);
                    goodshow(g);

                } else if (g.getFormNo() != null) {

                    int FormNo = Integer.parseInt(g.getFormNo());  // Ensure this value is of type double

                    if (callMethod.ReadString("StackCategory").equals("انبار1-1") && FormNo >= 106000 && FormNo <= 114999) {
                        ocr_goods_visible.add(g);
                        goodshow(g);
                    }else if (callMethod.ReadString("StackCategory").equals("انبار1-2") && FormNo >= 115000 && FormNo <= 126999) {
                        ocr_goods_visible.add(g);
                        goodshow(g);
                    } else if (callMethod.ReadString("StackCategory").equals("انبار2-1") && FormNo > 205000 && FormNo <= 214999) {
                        ocr_goods_visible.add(g);
                        goodshow(g);
                    } else if (callMethod.ReadString("StackCategory").equals("انبار2-2") && FormNo > 215000 && FormNo <= 226999) {
                        ocr_goods_visible.add(g);
                        goodshow(g);
                    } else if (callMethod.ReadString("StackCategory").equals("انبار3-1") && FormNo > 301000 && FormNo <= 317999) {
                        ocr_goods_visible.add(g);
                        goodshow(g);
                    } else if (callMethod.ReadString("StackCategory").equals("انبار3-2") && FormNo > 318000 && FormNo <= 399999) {
                        ocr_goods_visible.add(g);
                        goodshow(g);
                    }
                }


            }else{
                if(callMethod.ReadString("StackCategory").equals("همه")) {
                    ocr_goods_visible.add(g);
                    goodshow(g);
                }else if(g.getGoodExplain4().equals(callMethod.ReadString("StackCategory"))){
                    ocr_goods_visible.add(g);
                    goodshow(g);
                }

            }

        }

        try{
            factor.getAppOCRFactorExplain();
        }catch (Exception e){
            callMethod.Log(e.getMessage());
            factor.setAppOCRFactorExplain("");

        }


        ll_title.addView(tv_company);

        if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
        } else {
            ll_title.addView(btn_set_stack);
        }


        ll_title.addView(tv_appocrfactorexplain);



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

        if (callMethod.ReadBoolan("ShowTotalAmount")){
            ll_factor_summary.addView(tv_total_amount);
        }
        ll_factor_summary.addView(tv_total_price);

        ll_main.addView(ll_title);
        ll_main.addView(ll_good_body);
        if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
            if (callMethod.ReadString("Category").equals("2")) {
                ll_shortage_print.addView(btn_shortage);
                ll_shortage_print.addView(btn_print);
                ll_main.addView(ll_shortage_print);
            }
            ll_main.addView(ll_factor_summary);
            ll_main.addView(ll_send_confirm);
        }
//        EditText edBarcode = requireActivity().findViewById(R.id.ocr_collect_confirm_a_barcode);


//        scrollView_main.setOnTouchListener((v, event) -> {
//            edBarcode.requestFocus();
//            edBarcode.selectAll();
//            return false; // یعنی اجازه بده اسکرول ادامه پیدا کنه
//        });

        ConfirmCount_Control();


        //// vase scroll kardan be avvalin tik nakhorde

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            for (int i = 0; i < ll_good_body_detail.getChildCount(); i++) {
                View child = ll_good_body_detail.getChildAt(i);

                if (child instanceof LinearLayoutCompat) {
                    LinearLayoutCompat ll_row = (LinearLayoutCompat) child;

                    if (ll_row.getChildCount() > 0) {
                        View firstChild = ll_row.getChildAt(0);

                        if (firstChild instanceof LinearLayoutCompat) {
                            LinearLayoutCompat ll_details = (LinearLayoutCompat) firstChild;

                            if (ll_details.getChildCount() > 0) {
                                View secondChild = ll_details.getChildAt(0);

                                if (secondChild instanceof LinearLayoutCompat) {
                                    LinearLayoutCompat ll_radif_check = (LinearLayoutCompat) secondChild;

                                    for (int j = 0; j < ll_radif_check.getChildCount(); j++) {
                                        View checkView = ll_radif_check.getChildAt(j);

                                        if (checkView instanceof MaterialCheckBox) {
                                            MaterialCheckBox cb = (MaterialCheckBox) checkView;

                                            if (!cb.isChecked()) {


                                                cb.requestFocus();

                                                scrollView_main.post(() -> {
                                                    cb.getParent().requestChildFocus(cb, cb);

//                                                    // برگردوندن فوکوس به EditText بعد از اسکرول
//                                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                                                        EditText edBarcode1 = requireActivity().findViewById(R.id.ocr_collect_confirm_a_barcode);
//                                                        //edBarcode1.requestFocus();
//                                                        edBarcode1.selectAll();
//                                                    }, 300);  // یه تاخیر کوتاه برای برگشت فوکوس
                                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                        EditText edBarcode1 = requireActivity().findViewById(R.id.ocr_collect_confirm_a_barcode);



                                                        // برگردوندن فوکوس و انتخاب همه متن
                                                        //edBarcode1.requestFocus();
                                                        edBarcode1.selectAll();
                                                        // بستن کیبورد اگر باز باشه
                                                        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(requireActivity().INPUT_METHOD_SERVICE);
                                                        if (imm != null) {
                                                            imm.hideSoftInputFromWindow(edBarcode1.getWindowToken(), 0);
                                                        }


                                                    }, 300);

                                                });

                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }, 1000);


        btn_shortage.setOnClickListener(v -> CreateView_shortage());
        btn_print.setOnClickListener(v -> ocr_print.Printing(factor,ocr_goods_visible,"0","1"));
        btn_set_stack.setOnClickListener(v -> {

            Call<RetrofitResponse> call;
            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call=apiInterface.SetOcrFactorExplain("SetOcrFactorExplain",factor.getAppOCRFactorCode(),callMethod.ReadString("StackCategory"));

            }else{
                call=secendApiInterface.SetOcrFactorExplain("SetOcrFactorExplain",factor.getAppOCRFactorCode(),callMethod.ReadString("StackCategory"));
            }
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if(response.isSuccessful()) {
                        dialogProg.dismiss();
                        intent = new Intent(requireActivity(), Ocr_Collect_Confirm_Activity.class);
                        intent.putExtra("ScanResponse", BarcodeScan);
                        intent.putExtra("State", "0");
                        intent.putExtra("FactorImage", "");
                        dialogProg.dismiss();
                        startActivity(intent);
                        requireActivity().finish();


                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            callMethod.Log(t.getMessage()); }
            });
        });


        btn_send.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.ocr_checkamount);
            EditText edamount = dialog.findViewById(R.id.ocr_checkamount_c_edamount);
            MaterialButton btncheckamount = dialog.findViewById(R.id.ocr_checkamount_c_btncheckamount);


            btncheckamount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callMethod.Log("factor.getSumAmount() = "+factor.getSumAmount());

                    if (NumberFunctions.EnglishNumber(edamount.getText().toString()).equals(factor.getSumAmount())) {


                        dialogProg.show();

                        Call<RetrofitResponse> call;
                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                            call=apiInterface.CheckState("OcrControlled_new",factor.getAppOCRFactorCode(),"1","");

                        }else{
                            call=secendApiInterface.CheckState("OcrControlled_new",factor.getAppOCRFactorCode(),"1","");
                        }
                        call.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                if(response.isSuccessful()) {
                                    dialogProg.dismiss();


                                    Call<RetrofitResponse> call1;
                                    if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                                        call1=apiInterface.OcrDoubleCheck("OcrDoubleCheck",factor.getAppOCRFactorCode());

                                    }else{
                                        call1=secendApiInterface.OcrDoubleCheck("OcrDoubleCheck",factor.getAppOCRFactorCode());
                                    }

                                    call1.enqueue(new Callback<RetrofitResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<RetrofitResponse> call1, @NonNull Response<RetrofitResponse> response) {
                                            if(response.isSuccessful()) {
                                                ocr_action.checkSumAmount(factor);
                                                if (response.body().getText().equals("HasNotDoubleCheck")){
                                                    ocr_action.Pack_detail(factor,"0");

                                                }else if (response.body().getText().equals("HasDoubleCheck")){
                                                    ocr_print.Printing(factor,ocr_goods_visible,"0","0");
                                                }

                                            }
                                        }
                                        @Override
                                        public void onFailure(@NonNull Call<RetrofitResponse> call1, @NonNull Throwable t) {
                                                    callMethod.Log(t.getMessage()); }
                                    });



                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                                        callMethod.Log(t.getMessage()); }
                        });


                    }else {
                        callMethod.showToast("تعداد وارد شده صحیح نیست");
                    }
                }
            });


            dialog.show();



        });

        btn_confirm.setOnClickListener(v -> {


            int Array_GoodCodesCheck_count=Array_GoodCodesCheck.size();
            conter_confirm = 0;
            dialogProg.show();
            try {

                for (String single_GoodCode_check : Array_GoodCodesCheck) {


                    Call<RetrofitResponse> call;
                    if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                        call=apiInterface.OcrControlled(
                                "OcrControlled_new",
                                single_GoodCode_check,
                                "0",
                                callMethod.ReadString("JobPersonRef")
                        );
                    }else{
                        call=secendApiInterface.OcrControlled(
                                "OcrControlled_new",
                                single_GoodCode_check,
                                "0",
                                callMethod.ReadString("JobPersonRef")
                        );
                    }


                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                            if(response.isSuccessful()) {
                                conter_confirm = conter_confirm +1;
                                if(conter_confirm==Array_GoodCodesCheck_count){
                                    assert response.body() != null;
                                    intent = new Intent(requireActivity(), Ocr_Collect_Confirm_Activity.class);
                                    intent.putExtra("ScanResponse", BarcodeScan);
                                    intent.putExtra("State", "0");
                                    intent.putExtra("FactorImage", "");
                                    dialogProg.dismiss();
                                    startActivity(intent);
                                    requireActivity().finish();

                                }
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            dialogProg.dismiss();
                            callMethod.Log(t.getMessage());

                        }
                    });


                }

            }catch (Exception e){
                 callMethod.Log(e.getMessage());
            }


        });


        if(callMethod.ReadString("Category").equals("1")) {
            btn_send.setVisibility(View.GONE);
            btn_confirm.setText("بازگشت به صفحه اصلی");
            btn_confirm.setOnClickListener(v -> {
                intent = new Intent(requireActivity(), Ocr_NavActivity.class);
                startActivity(intent);
                requireActivity().finish();
            });
        }





    }


    public void NewView(){

        ll_title = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body_detail = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_factor_summary = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_send_confirm = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_shortage_print = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ViewPager = new ViewPager(requireActivity().getApplicationContext());
        tv_company = new TextView(requireActivity().getApplicationContext());
        tv_customername = new TextView(requireActivity().getApplicationContext());
        tv_appocrfactorexplain = new TextView(requireActivity().getApplicationContext());
        tv_factorcode = new TextView(requireActivity().getApplicationContext());
        tv_factordate = new TextView(requireActivity().getApplicationContext());
        tv_factorexplain = new TextView(requireActivity().getApplicationContext());
        tv_address = new TextView(requireActivity().getApplicationContext());
        tv_phone = new TextView(requireActivity().getApplicationContext());
        tv_total_amount = new TextView(requireActivity().getApplicationContext());
        tv_total_price = new TextView(requireActivity().getApplicationContext());
        btn_confirm = new Button(requireActivity().getApplicationContext());
        btn_send = new Button(requireActivity().getApplicationContext());
        btn_set_stack = new Button(requireActivity().getApplicationContext());
        btn_shortage = new Button(requireActivity().getApplicationContext());
        btn_print = new Button(requireActivity().getApplicationContext());
    }

    public void setLayoutParams(){

        ll_title.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_factor_summary.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_send_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_shortage_print.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

        tv_company.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_appocrfactorexplain.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_customername.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factorcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factordate.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factorexplain.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_address.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_phone.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        btn_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_send.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_set_stack.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_shortage.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT,1));
        btn_print.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT,1));

        tv_total_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_total_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 3));


    }
    public void setOrientation(){
        ll_title.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_good_body.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_good_body_detail.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_factor_summary.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_send_confirm.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_shortage_print.setOrientation(LinearLayoutCompat.HORIZONTAL);
    }
    public void setLayoutDirection(){
        ll_title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_factor_summary.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_send_confirm.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_shortage_print.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
    @SuppressLint("RtlHardcoded")
    public void setGravity(){
        tv_company.setGravity(Gravity.CENTER);
        tv_appocrfactorexplain.setGravity(Gravity.RIGHT);
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
        btn_set_stack.setGravity(Gravity.CENTER);
        btn_shortage.setGravity(Gravity.CENTER);
        btn_print.setGravity(Gravity.CENTER);
    }
    public void setTextSize(){
        tv_company.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_appocrfactorexplain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_customername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factorcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factordate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factorexplain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_phone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_total_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_total_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        btn_confirm.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_send.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_set_stack.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_shortage.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_print.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_print.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));

    }
    public void setBackgroundResource(){

        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        btn_confirm.setBackgroundResource(R.color.green_800);
        btn_send.setBackgroundResource(R.color.red_700);
        btn_set_stack.setBackgroundResource(R.color.blue_500);
        btn_shortage.setBackgroundResource(R.color.orange_500);
        btn_print.setBackgroundResource(R.color.blue_500);
    }

    public void setTextColor(){
        tv_company.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_appocrfactorexplain.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
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
        btn_set_stack.setTextColor(requireActivity().getColor(R.color.white));
        btn_shortage.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        btn_print.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
    }

    public void setPadding(){
        tv_company.setPadding(0, 0, 30, 20);
        tv_appocrfactorexplain.setPadding(0, 0, 30, 20);
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
        btn_set_stack.setPadding(0, 0, 30, 20);
        btn_shortage.setPadding(0, 0, 30, 20);
        btn_print.setPadding(0, 0, 30, 20);
    }


    @SuppressLint("RtlHardcoded")
    public void goodshow(Ocr_Good good_detial){
        row_counter++;

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

        MaterialCheckBox checkBox = new MaterialCheckBox(requireActivity());

        ll_factor_row.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_details.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_radif_check.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 7.7));
        ll_name_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 1.3));
        vp_rows.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
        vp_radif_name.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_name_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_amount_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(20, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)1.5));
        tv_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)4));
        tv_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)3.5));

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


        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize"))-10);
        tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize"))+3);
        tv_amount.setTypeface(null, Typeface.BOLD);

        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));



        checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(row_counter)));
try {



        tv_goodname.setText(NumberFunctions.PerisanNumber(good_detial.getGoodName()));

        tv_amount.setText(NumberFunctions.PerisanNumber(good_detial.getFacAmount()));

        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
            tv_price.setText(NumberFunctions.PerisanNumber(good_detial.getGoodMaxSellPrice()));


        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
            tv_price.setText(NumberFunctions.PerisanNumber(good_detial.getFormNo()));
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrMahris")){
            tv_price.setText(NumberFunctions.PerisanNumber(good_detial.getGoodExplain3()));
        }else{
            tv_price.setText(NumberFunctions.PerisanNumber(good_detial.getGoodMaxSellPrice()));
        }

        tv_gap.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        checkBox.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_goodname.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_amount.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_price.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));

        tv_price.setPadding(0, 10, 0, 10);
        tv_goodname.setPadding(0, 10, 5, 10);


        if(good_detial.getShortageAmount()==null){
            callMethod.Log("ShortageAmount is null");
        }else {
            if(Integer.parseInt(good_detial.getShortageAmount())>0) {
                tv_amount.setText(NumberFunctions.PerisanNumber(good_detial.getShortageAmount() + ""));
                tv_amount.setTextColor(requireActivity().getColor(R.color.red_800));
            }

        }

}catch (Exception e){
    callMethod.Log("kowsar "+ e.getMessage());
}
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
            if (row_counter%2==0){
                ll_details.setBackgroundColor(requireActivity().getColor(R.color.grey_200));
            }
            try {
                if (good_detial.getMinAmount().equals("1")){
                    ll_details.setBackgroundColor(requireActivity().getColor(R.color.red_100));
                }
            }catch (Exception e){
                callMethod.Log(e.getMessage());

            }


        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
            if (row_counter%2==0){
                ll_details.setBackgroundColor(requireActivity().getColor(R.color.grey_200));
            }
            callMethod.Log("Gostaresh");

        }else{
            if (row_counter%2==0){
                ll_details.setBackgroundColor(requireActivity().getColor(R.color.grey_200));
            }
            callMethod.Log("defult");
        }


        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(vp_rows);

        ll_good_body_detail.addView(ll_factor_row);


        int correct_row=row_counter-1;
        //if(ocr_goods_visible.get(fa).getAppRowIsControled().equals("True")){
        if(ocr_goods_visible.get(correct_row).getAppRowIsControled().equals("1")){
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        }else {

            if (callMethod.ReadBoolan("JustScanner")){
                try {
                    checkBox.setEnabled(!good_detial.getBarCodePrintState().equals("دارد"));
                }catch (Exception e){
                    callMethod.Log( e.getMessage());
                    checkBox.setEnabled(false);
                }
            }else{
                checkBox.setEnabled(true);
            }
            
        }
        if(callMethod.ReadString("Category").equals("1")) {
            checkBox.setVisibility(View.GONE);
        }
        checkBox.setOnClickListener(v -> {

            if (callMethod.ReadBoolan("JustScanner")){

                if (good_detial.getBarCodePrintState().equals("ندارد")) {
                    callMethod.Log("BarCodePrint = "+good_detial.getBarCodePrintState());
                    ocr_goods_scan.clear();
                    ocr_goods_scan.add(good_detial);

                    if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
                        checkBox.setChecked(false);

                        ocr_action.GoodScanDetail(ocr_goods_scan, state, getBarcodeScan());
                    } else {
                        callMethod.showToast("لطفا ابتدا آغاز فرایند انبار را شروع کنید");
                    }
                }else{
                    callMethod.Log("BarCodePrint = "+good_detial.getBarCodePrintState());
                }
            }
        });
        
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
                if (callMethod.ReadBoolan("ListOrSingle")){  // list true
                    if(isChecked){
                        ocr_goods_visible.get(correct_row).setAppRowIsControled("1");
                        if (!Array_GoodCodesCheck.contains(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode())) {
                            Array_GoodCodesCheck.add(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode());
                        }
                    }else {
                        ocr_goods_visible.get(correct_row).setAppRowIsControled("0");
                        Array_GoodCodesCheck.remove(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode());
                    }
                }else{

                    if (Array_GoodCodesCheck.size()>0){
                        if(isChecked){
                            checkBox.setChecked(false);
                            callMethod.showToast("چند انتخابی غیر فعال است");
                        }else {
                            ocr_goods_visible.get(correct_row).setAppRowIsControled("0");
                            Array_GoodCodesCheck.remove(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode());
                        }
                    }else{
                        if(isChecked){

                            ocr_goods_visible.get(correct_row).setAppRowIsControled("1");
                            image_zome_view(ocr_goods_visible.get(correct_row));
                            if (!Array_GoodCodesCheck.contains(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode())) {
                                Array_GoodCodesCheck.add(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode());
                            }
                        }else {
                            ocr_goods_visible.get(correct_row).setAppRowIsControled("0");
                            Array_GoodCodesCheck.remove(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode());

                        }
                    }
                }
            } else {
                callMethod.showToast("لطفا ابتدا آغاز فرایند انبار را شروع کنید");
            }


        });



        tv_goodname.setOnClickListener(v -> {
            if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
                image_zome_view(ocr_goods_visible.get(correct_row));
            } else {
                callMethod.showToast("لطفا ابتدا آغاز فرایند انبار را شروع کنید");
            }

        });
        tv_amount.setOnClickListener(v -> {
            if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
                amount_zome_view(ocr_goods_visible.get(correct_row).getFacAmount(),ocr_goods_visible.get(correct_row).getShortageAmount()+"");
            } else {
                callMethod.showToast("لطفا ابتدا آغاز فرایند انبار را شروع کنید");
            }

        });



    }

    public void Newview() {
        ll_title = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body_detail = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_factor_summary = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_send_confirm = new LinearLayoutCompat(requireActivity().getApplicationContext());
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
        btn_set_stack = new Button(requireActivity().getApplicationContext());
        btn_shortage = new Button(requireActivity().getApplicationContext());
        btn_print = new Button(requireActivity().getApplicationContext());


    }
    public void ConfirmCount_Control(){

        int ConfirmCounter_stack = 0;
        int ConfirmCounter = 0;

        for (Ocr_Good g : ocr_goods) {
            if(g.getAppRowIsControled().equals("1")){
                ConfirmCounter++;
            }
        }
        for (Ocr_Good g : ocr_goods_visible) {
            if(g.getAppRowIsControled().equals("1")){
                ConfirmCounter_stack++;
            }
        }
        if(ocr_goods.size() == ConfirmCounter){
            if(callMethod.ReadBoolan("AutoSend")) {
                dialogProg.show();

                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                    call = apiInterface.CheckState("OcrControlled_new", factor.getAppOCRFactorCode(), "1", callMethod.ReadString("Deliverer"));
                } else {
                    call = secendApiInterface.CheckState("OcrControlled_new", factor.getAppOCRFactorCode(), "1", callMethod.ReadString("Deliverer"));
                }


                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            callMethod.showToast("تاییده ارسال شد.");
                            dialogProg.dismiss();


                            Call<RetrofitResponse> call1;
                            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                                call1 = apiInterface.OcrDoubleCheck("OcrDoubleCheck", factor.getAppOCRFactorCode());

                            } else {
                                call1 = secendApiInterface.OcrDoubleCheck("OcrDoubleCheck", factor.getAppOCRFactorCode());
                            }

                            call1.enqueue(new Callback<RetrofitResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<RetrofitResponse> call1, @NonNull Response<RetrofitResponse> response) {
                                    if (response.isSuccessful()) {
                                        dialogProg.dismiss();

                                        assert response.body() != null;
                                        if (response.body().getText().equals("HasNotDoubleCheck")) {
                                            ocr_action.checkSumAmount(factor);


                                        } else if (response.body().getText().equals("HasDoubleCheck")) {
                                            ocr_print.Printing(factor, ocr_goods_visible, "0", "0");
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<RetrofitResponse> call1, @NonNull Throwable t) {
                                    callMethod.Log(t.getMessage());                                }
                            });

                            // print.Printing(factor,goods_visible,"0");

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        dialogProg.dismiss();
                        callMethod.Log(t.getMessage());
                    }
                });

            }
            btn_confirm.setBackgroundResource(R.color.grey_60);
            btn_confirm.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            btn_confirm.setEnabled(false);
            callMethod.showToast("اماده ارسال می باشد");
        }else{
            if(callMethod.ReadBoolan("AutoSend")){
                if(ocr_goods_visible.size() == ConfirmCounter_stack){
                    ocr_print.Printing(factor,ocr_goods_visible,"0","0");
                }
            }
            btn_send.setBackgroundResource(R.color.grey_60);
            btn_send.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            btn_send.setEnabled(false);
        }

    }



    @SuppressLint("RtlHardcoded")
    public void CreateView_shortage() {
        ll_main.removeAllViews();
        lastCunter=0;
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


        tv_company.setText(NumberFunctions.PerisanNumber("کسری صورت جمع کن"));
        tv_appocrfactorexplain.setText(NumberFunctions.PerisanNumber(" اتبار  :   " + factor.getAppOCRFactorExplain()));
        tv_customername.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
        tv_factorcode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));
        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));
        tv_factorexplain.setText(NumberFunctions.PerisanNumber(" توضیحات :   " + factor.getFactorDate()));

        btn_confirm.setText("ارسال کسری");
        btn_send.setText("بازگشت");
        btn_shortage.setText("اعلام کسر موجودی");
        btn_shortage.setTextSize(20);


        int countergood = 0;
        for (Ocr_Good singlegood : ocr_goods_visible) {
            countergood++;

            if (singlegood.getAppRowIsControled().equals("0")) {
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



        btn_shortage.setOnClickListener(v -> CreateView_shortage());

        btn_send.setOnClickListener(v -> requireActivity().finish());

        btn_confirm.setBackgroundResource(R.color.red_500);
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_confirm.setEnabled(true);

        btn_send.setBackgroundResource(R.color.green_500);
        btn_send.setTextColor(requireActivity().getColor(R.color.white));
        btn_send.setEnabled(true);

        btn_confirm.setOnClickListener(v -> {

            for (String[] goodchecks : arraygood_shortage) {

                Call<RetrofitResponse> call;
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

                                if (state.equals("0")){
                                    intent = new Intent(requireActivity(), Ocr_Collect_Confirm_Activity.class);

                                }else if (state.equals("1")){
                                    intent = new Intent(requireActivity(), Ocr_Check_Confirm_Activity.class);

                                }

                                intent.putExtra("ScanResponse", TcPrintRef );
                                intent.putExtra("State", state);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                                requireActivity().finish();

                                requireActivity().startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                        callMethod.Log(t.getMessage());
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
    public View CreateGoodViewForshortage(@NonNull Ocr_Good good, int countergood) {

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

        checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(countergood)));
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


        try {
            if (good.getMinAmount().equals("1.000")){
                ll_details.setBackgroundColor(requireActivity().getColor(R.color.red_100));
            }
        }catch (Exception e){

            callMethod.Log(e.getMessage());
        }



        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(vp_rows);


        int correct_row = countergood - 1;
        if (ocr_goods_visible.get(correct_row).getAppRowIsPacked().equals("1")) {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        } else {
            checkBox.setEnabled(true);
        }
        if (callMethod.ReadString("Category").equals("1")) {
            checkBox.setVisibility(View.GONE);
        }




        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String goodCode = ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode();
            if (isChecked) {
                ocr_goods_visible.get(correct_row).setAppRowIsControled("1");
                if (!Array_GoodCodesCheck.contains(goodCode)) {
                    Array_GoodCodesCheck.add(goodCode);
                }
            } else {
                ocr_goods_visible.get(correct_row).setAppRowIsControled("0");
                Array_GoodCodesCheck.remove(goodCode);
            }

        });


        tv_goodname.setOnClickListener(v -> image_zome_view(ocr_goods_visible.get(correct_row)));


        et_amountshortage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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





    public void image_zome_view(Ocr_Good singleGood) {
        ocr_action.good_detail(singleGood,BarcodeScan);
    }
    public void amount_zome_view(String Facamount,String shortage) {
        ocr_action.goodamount_detail(Facamount,shortage);
    }
    public String getBarcodeScan() {
        return BarcodeScan;
    }

    public void setBarcodeScan(String barcodeScan) {
        BarcodeScan = barcodeScan;
    }
}