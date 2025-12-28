package com.kits.kowsarapp.fragment.ocr;

import android.annotation.SuppressLint;
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
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.ocr.Ocr_Check_Confirm_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_Collect_Confirm_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_Inventory_Check_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_NavActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.NetworkUtils;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.application.ocr.Ocr_Print;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_InventoryFragment extends Fragment implements OnGoodConfirmListener {
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

    androidx.viewpager.widget.ViewPager ViewPager;

    Button btn_send;
    Button btn_confirm;
    Button btn_set_stack;
    Button btn_print;


    TextView tv_company;
    TextView tv_factorcode;
    TextView tv_factordate;


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

    Integer Sum_Confirm_Amount=0;

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

        view= inflater.inflate(R.layout.ocr_fragment_inventory, container, false);
        ll_main = view.findViewById(R.id.ocr_inventory_f_layout);
        scrollView_main= view.findViewById(R.id.ocr_inventory_scrollView_main);
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


        tv_company.setText(NumberFunctions.PerisanNumber("انبارگردانی"));
        tv_appocrfactorexplain.setText(NumberFunctions.PerisanNumber(" انبار :   " + factor.getAppOCRFactorExplain()));
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


        btn_confirm.setText("تاییده بخش");
        btn_send.setText("ارسال تاییده");
        btn_set_stack.setText("آغاز فرآیند انبار");
        btn_print.setText("پرینت فاکتور");



        row_counter= 0;

        for (Ocr_Good ocr_good_single : ocr_goods) {


            if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                    callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

                if(callMethod.ReadString("StackCategory").equals("همه")) {
                    ocr_goods_visible.add(ocr_good_single);
                    goodshow(ocr_good_single);
                }else if(ocr_good_single.getGoodExplain4().equals(callMethod.ReadString("StackCategory"))){
                    ocr_goods_visible.add(ocr_good_single);
                    goodshow(ocr_good_single);
                }


            } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
                if(callMethod.ReadString("StackCategory").equals("همه")) {

                    ocr_goods_visible.add(ocr_good_single);
                    goodshow(ocr_good_single);

                } else if (ocr_good_single.getFormNo() != null) {

                    int FormNo = Integer.parseInt(ocr_good_single.getFormNo());  // Ensure this value is of type double

//                    if (callMethod.ReadString("StackCategory").equals("انبار1ب1") && FormNo >= 106000 && FormNo <= 114999) {
//                        ocr_goods_visible.add(ocr_good_single);
//                        goodshow(ocr_good_single);
//                    }else if (callMethod.ReadString("StackCategory").equals("انبار1ب2") && FormNo >= 115000 && FormNo <= 126999) {
//                        ocr_goods_visible.add(ocr_good_single);
//                        goodshow(ocr_good_single);
//                    } else if (callMethod.ReadString("StackCategory").equals("انبار2ب1") && FormNo > 205000 && FormNo <= 214999) {
//                        ocr_goods_visible.add(ocr_good_single);
//                        goodshow(ocr_good_single);
//                    } else if (callMethod.ReadString("StackCategory").equals("انبار2ب2") && FormNo > 215000 && FormNo <= 226999) {
//                        ocr_goods_visible.add(ocr_good_single);
//                        goodshow(ocr_good_single);
//                    } else if (callMethod.ReadString("StackCategory").equals("انبار3ب1") && FormNo > 301000 && FormNo <= 317999) {
//                        ocr_goods_visible.add(ocr_good_single);
//                        goodshow(ocr_good_single);
//                    } else if (callMethod.ReadString("StackCategory").equals("انبار3ب2") && FormNo > 318000 && FormNo <= 399999) {
//                        ocr_goods_visible.add(ocr_good_single);
//                        goodshow(ocr_good_single);
//                    }


                    if (callMethod.ReadString("StackCategory").equals("انبار1") && FormNo >= 101000 && FormNo <= 126999) {
                        ocr_goods_visible.add(ocr_good_single);
                        goodshow(ocr_good_single);
                    } else if (callMethod.ReadString("StackCategory").equals("انبار2") && FormNo > 201000 && FormNo <= 226999) {
                        ocr_goods_visible.add(ocr_good_single);
                        goodshow(ocr_good_single);

                    } else if (callMethod.ReadString("StackCategory").equals("انبار3ب1") && FormNo > 301000 && FormNo <= 317999) {
                        ocr_goods_visible.add(ocr_good_single);
                        goodshow(ocr_good_single);
                    } else if (callMethod.ReadString("StackCategory").equals("انبار3ب2") && FormNo > 318000 && FormNo <= 322999) {
                        ocr_goods_visible.add(ocr_good_single);
                        goodshow(ocr_good_single);
                    }


                }


            }else{
                if(callMethod.ReadString("StackCategory").equals("همه")) {
                    ocr_goods_visible.add(ocr_good_single);
                    goodshow(ocr_good_single);
                }else if(ocr_good_single.getGoodExplain4().equals(callMethod.ReadString("StackCategory"))){
                    ocr_goods_visible.add(ocr_good_single);
                    goodshow(ocr_good_single);
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



        ll_title.addView(tv_factorcode);
        ll_title.addView(tv_factordate);
        ll_title.addView(tv_factorexplain);


        ll_title.addView(ViewPager);

        ll_send_confirm.addView(btn_confirm);
        ll_send_confirm.addView(btn_send);

        ll_good_body.addView(ll_good_body_detail);


        ll_main.addView(ll_title);
        ll_main.addView(ll_good_body);
        if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
            if (callMethod.ReadString("Category").equals("2")) {
                ll_shortage_print.addView(btn_print);
                ll_main.addView(ll_shortage_print);
            }
            ll_main.addView(ll_factor_summary);
            ll_main.addView(ll_send_confirm);
        }


        ConfirmCount_Control();



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
//                                                        EditText edBarcode1 = requireActivity().findViewById(R.id.ocr_inventory_confirm_a_barcode);
//                                                        //edBarcode1.requestFocus();
//                                                        edBarcode1.selectAll();
//                                                    }, 300);  // یه تاخیر کوتاه برای برگشت فوکوس
                                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                        EditText edBarcode1 = requireActivity().findViewById(R.id.ocr_inventorycheck_a_barcode);



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
                        intent = new Intent(requireActivity(), Ocr_Inventory_Check_Activity.class);
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
                    try {

                    } catch (Exception e) {
                        callMethod.Log("Network check error: " + e.getMessage());
                        callMethod.showToast("خطا در بررسی وضعیت شبکه");
                    }
                }
            });
        });


        btn_send.setOnClickListener(v -> {



            if (callMethod.ReadBoolan("SendCheckAmount")){

                final Dialog dialog = new Dialog(requireActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setContentView(R.layout.ocr_checkamount);
                EditText edamount = dialog.findViewById(R.id.ocr_checkamount_c_edamount);
                MaterialButton btncheckamount = dialog.findViewById(R.id.ocr_checkamount_c_btncheckamount);


                btncheckamount.setOnClickListener(v12 -> {
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
                                            try {
                                                // 🟢 بررسی وضعیت اتصال
                                                if (!NetworkUtils.isNetworkAvailable(requireActivity())) {
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
                                            }                                        }
                                    });



                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                                try {
                                    // 🟢 بررسی وضعیت اتصال
                                    if (!NetworkUtils.isNetworkAvailable(requireActivity())) {
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
                                }
                            }
                        });


                    }else {
                        callMethod.showToast("تعداد وارد شده صحیح نیست");
                    }
                });


                dialog.show();

            }else{
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
                                    try {
                                        // 🟢 بررسی وضعیت اتصال
                                        if (!NetworkUtils.isNetworkAvailable(requireActivity())) {
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
                                    }
                                }
                            });



                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        try {
                            // 🟢 بررسی وضعیت اتصال
                            if (!NetworkUtils.isNetworkAvailable(requireActivity())) {
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
                        }
                    }
                });
            }





        });

        btn_confirm.setOnClickListener(v -> {

            if (callMethod.ReadBoolan("ConfirmCheckAmount")){

                final Dialog dialog = new Dialog(requireActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setContentView(R.layout.ocr_checkamount);
                EditText edamount = dialog.findViewById(R.id.ocr_checkamount_c_edamount);
                MaterialButton btncheckamount = dialog.findViewById(R.id.ocr_checkamount_c_btncheckamount);


                btncheckamount.setOnClickListener(v1 -> {



                    if (NumberFunctions.EnglishNumber(edamount.getText().toString()).equals(Sum_Confirm_Amount.toString())) {

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
                                                intent = new Intent(requireActivity(), Ocr_Inventory_Check_Activity.class);
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
                                        try {
                                            // 🟢 بررسی وضعیت اتصال
                                            if (!NetworkUtils.isNetworkAvailable(requireActivity())) {
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
                                        }
                                        dialogProg.dismiss();
                                        callMethod.Log(t.getMessage());

                                    }
                                });


                            }

                        }catch (Exception e){
                            callMethod.Log(e.getMessage());
                        }
                    }else {
                        callMethod.showToast("تعداد وارد شده صحیح نیست");
                    }
                });


                dialog.show();


            }else{

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
                                        intent = new Intent(requireActivity(), Ocr_Inventory_Check_Activity.class);
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
                                try {
                                    // 🟢 بررسی وضعیت اتصال
                                    if (!NetworkUtils.isNetworkAvailable(requireActivity())) {
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
                                }
                                dialogProg.dismiss();
                                callMethod.Log(t.getMessage());

                            }
                        });


                    }

                }catch (Exception e){
                    callMethod.Log(e.getMessage());
                }

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
        tv_appocrfactorexplain = new TextView(requireActivity().getApplicationContext());
        tv_factorcode = new TextView(requireActivity().getApplicationContext());
        tv_factordate = new TextView(requireActivity().getApplicationContext());
        tv_factorexplain = new TextView(requireActivity().getApplicationContext());
        btn_confirm = new Button(requireActivity().getApplicationContext());
        btn_send = new Button(requireActivity().getApplicationContext());
        btn_set_stack = new Button(requireActivity().getApplicationContext());
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
        tv_factorcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factordate.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factorexplain.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        btn_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_send.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_set_stack.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_print.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT,1));

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
        tv_factorcode.setGravity(Gravity.RIGHT);
        tv_factordate.setGravity(Gravity.RIGHT);
        tv_factorexplain.setGravity(Gravity.RIGHT);
        btn_confirm.setGravity(Gravity.CENTER);
        btn_send.setGravity(Gravity.CENTER);
        btn_set_stack.setGravity(Gravity.CENTER);
        btn_print.setGravity(Gravity.CENTER);
    }
    public void setTextSize(){
        tv_company.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_appocrfactorexplain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factorcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factordate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factorexplain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        btn_confirm.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_send.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_set_stack.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_print.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_print.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));

    }
    public void setBackgroundResource(){

        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        btn_confirm.setBackgroundResource(R.color.green_800);
        btn_send.setBackgroundResource(R.color.red_700);
        btn_set_stack.setBackgroundResource(R.color.blue_500);
        btn_print.setBackgroundResource(R.color.blue_500);
    }

    public void setTextColor(){
        tv_company.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_appocrfactorexplain.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factorcode.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factordate.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factorexplain.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
       btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_send.setTextColor(requireActivity().getColor(R.color.white));
        btn_set_stack.setTextColor(requireActivity().getColor(R.color.white));
        btn_print.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
    }

    public void setPadding(){
        tv_company.setPadding(0, 0, 30, 20);
        tv_appocrfactorexplain.setPadding(0, 0, 30, 20);
        tv_factorcode.setPadding(0, 0, 30, 20);
        tv_factordate.setPadding(0, 0, 30, 20);
        tv_factorexplain.setPadding(0, 0, 30, 20);
        btn_confirm.setPadding(0, 0, 30, 20);
        btn_send.setPadding(0, 0, 30, 20);
        btn_set_stack.setPadding(0, 0, 30, 20);
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
        TextView tv_good_part1 = new TextView(requireActivity().getApplicationContext());
        TextView tv_good_part2 = new TextView(requireActivity().getApplicationContext());
        TextView tv_good_part3 = new TextView(requireActivity().getApplicationContext());

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
        tv_good_part1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)1.5));
        tv_good_part2.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)4));
        tv_good_part3.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)3.5));

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
        tv_good_part1.setGravity(Gravity.RIGHT);
        tv_good_part2.setGravity(Gravity.CENTER);
        tv_good_part3.setGravity(Gravity.CENTER);


        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize"))-10);
        tv_good_part1.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        tv_good_part2.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize"))+3);
        tv_good_part2.setTypeface(null, Typeface.BOLD);

        tv_good_part3.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));


        int checkBoxId = View.generateViewId();

        checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(row_counter)));
        checkBox.setId(checkBoxId);
        ocr_goods_visible.get(row_counter - 1).setCheckBoxId(checkBoxId);

        try {



            tv_good_part1.setText(NumberFunctions.PerisanNumber(good_detial.getGoodName()));
            String Conter="0";
            if (good_detial.getCountedAmount1() == null) {
                Conter="0";
            } else if (good_detial.getCountedAmount2() == null) {
                Conter="1";
            } else if (good_detial.getCountedAmount3() == null) {
                Conter="2";
            }else{
                Conter="3";
            }

            tv_good_part2.setText(NumberFunctions.PerisanNumber(Conter));

            if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                    callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
                tv_good_part3.setText(NumberFunctions.PerisanNumber(good_detial.getGoodMaxSellPrice()));


            } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
                tv_good_part3.setText(NumberFunctions.PerisanNumber(good_detial.getFormNo()));
            } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrMahris")){
                tv_good_part3.setText(NumberFunctions.PerisanNumber(good_detial.getGoodExplain3()));
            }else{
                tv_good_part3.setText(NumberFunctions.PerisanNumber(good_detial.getGoodMaxSellPrice()));
            }

            tv_gap.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            checkBox.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_good_part1.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_good_part2.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_good_part3.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));

            tv_good_part3.setPadding(0, 10, 0, 10);
            tv_good_part1.setPadding(0, 10, 5, 10);


            if(good_detial.getShortageAmount()==null){
                callMethod.Log("ShortageAmount is null");
            }else {
                if(Integer.parseInt(good_detial.getShortageAmount())>0) {
                    tv_good_part2.setText(NumberFunctions.PerisanNumber(good_detial.getShortageAmount() + ""));
                    tv_good_part2.setTextColor(requireActivity().getColor(R.color.red_800));
                }

            }

        }catch (Exception e){
            callMethod.Log("kowsar "+ e.getMessage());
        }
        ll_radif_check.addView(tv_gap);


        ll_radif_check.addView(checkBox);

        ll_name_price.addView(tv_good_part1);
        ll_name_price.addView(vp_name_amount);
        ll_name_price.addView(tv_good_part2);
        ll_name_price.addView(vp_amount_price);
        ll_name_price.addView(tv_good_part3);

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


            if (callMethod.ReadBoolan("CheckListFromGoodDialog")){
                good_detail_inventory_view(ocr_goods_visible.get(correct_row));
                checkBox.toggle();
            }else{
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
            }


        });


        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleGoodCheck(checkBox, isChecked, correct_row);

        });



        tv_good_part1.setOnClickListener(v -> {
            if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {

                //if(ocr_goods_visible.get(fa).getAppRowIsControled().equals("True")){
                if(ocr_goods_visible.get(correct_row).getAppRowIsControled().equals("1")){
                    callMethod.showToast("شمارش این آیتم تمام شده است");

                }else{
                    good_detail_inventory_view(ocr_goods_visible.get(correct_row));

                }


            } else {
                callMethod.showToast("لطفا ابتدا آغاز فرایند انبار را شروع کنید");
            }

        });


//
//
//        tv_good_part2.setOnClickListener(v -> {
//            if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
//                good_amount_view(ocr_goods_visible.get(correct_row).getFacAmount(),ocr_goods_visible.get(correct_row).getShortageAmount()+"");
//            } else {
//                callMethod.showToast("لطفا ابتدا آغاز فرایند انبار را شروع کنید");
//            }
//
//        });



    }


    private void handleGoodCheck(MaterialCheckBox checkBox, boolean isChecked, int correct_row) {
        if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
            int amount = 0;
            try {
                amount = Integer.parseInt(ocr_goods_visible.get(correct_row).getFacAmount());
            } catch (Exception e) {
                amount = 0;
            }

            if (callMethod.ReadBoolan("ListOrSingle")) { // حالت لیستی
                if (isChecked) {
                    ocr_goods_visible.get(correct_row).setAppRowIsControled("1");
                    if (!Array_GoodCodesCheck.contains(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode())) {
                        Array_GoodCodesCheck.add(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode());
                    }
                    // ✅ اضافه کردن به جمع
                    Sum_Confirm_Amount += amount;
                } else {
                    ocr_goods_visible.get(correct_row).setAppRowIsControled("0");
                    Array_GoodCodesCheck.remove(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode());
                    // ✅ کم کردن از جمع
                    Sum_Confirm_Amount -= amount;
                }

            } else { // حالت تکی
                if (Array_GoodCodesCheck.size() > 0) {
                    if (isChecked) {
                        checkBox.setChecked(false);
                        callMethod.showToast("چند انتخابی غیر فعال است");
                    } else {
                        ocr_goods_visible.get(correct_row).setAppRowIsControled("0");
                        Array_GoodCodesCheck.remove(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode());
                        Sum_Confirm_Amount -= amount; // در حالت تکی هم کم شود اگر تیک برداشته شد
                    }
                } else {
                    if (isChecked) {
                        ocr_goods_visible.get(correct_row).setAppRowIsControled("1");
                        good_detail_inventory_view(ocr_goods_visible.get(correct_row));
                        if (!Array_GoodCodesCheck.contains(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode())) {
                            Array_GoodCodesCheck.add(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode());
                        }
                        Sum_Confirm_Amount += amount;
                    } else {
                        ocr_goods_visible.get(correct_row).setAppRowIsControled("0");
                        Array_GoodCodesCheck.remove(ocr_goods_visible.get(correct_row).getAppOCRFactorRowCode());
                        Sum_Confirm_Amount -= amount;
                    }
                }
            }

            // ✅ نمایش جمع جدید (در TextView یا Log)
            Log.e("SUM_DEBUG", "Sum_Confirm_Amount: " + Sum_Confirm_Amount);
            // یا اگر TextView داری:
            // txtSumConfirmAmount.setText(String.valueOf(Sum_Confirm_Amount));

        } else {
            callMethod.showToast("لطفا ابتدا آغاز فرایند انبار را شروع کنید");
        }
    }



    public void Newview() {
        ll_title = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body_detail = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_factor_summary = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_send_confirm = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ViewPager = new ViewPager(requireActivity().getApplicationContext());
        tv_company = new TextView(requireActivity().getApplicationContext());
        tv_factorcode = new TextView(requireActivity().getApplicationContext());
        tv_factordate = new TextView(requireActivity().getApplicationContext());
        tv_factorexplain = new TextView(requireActivity().getApplicationContext());
        btn_confirm = new Button(requireActivity().getApplicationContext());
        btn_send = new Button(requireActivity().getApplicationContext());
        btn_set_stack = new Button(requireActivity().getApplicationContext());
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
                                    try {
                                        // 🟢 بررسی وضعیت اتصال
                                        if (!NetworkUtils.isNetworkAvailable(requireActivity())) {
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
                                    }
                                }
                            });

                            // print.Printing(factor,goods_visible,"0");

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        try {
                            // 🟢 بررسی وضعیت اتصال
                            if (!NetworkUtils.isNetworkAvailable(requireActivity())) {
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
                        }
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


    @Override
    public void onGoodConfirmed(Ocr_Good singleGood) {
        try {
            // پیدا کردن ردیف
            int correct_row = -1;
            for (int i = 0; i < ocr_goods_visible.size(); i++) {
                if (ocr_goods_visible.get(i).getGoodCode().equals(singleGood.getGoodCode())) {
                    correct_row = i;
                    break;
                }
            }

            if (correct_row == -1) {
                callMethod.Log("onGoodConfirmed → Good not found: " + singleGood.getGoodCode());
                return;
            }

            // پیدا کردن چک‌باکس
            MaterialCheckBox checkBox = requireView().findViewById(singleGood.getCheckBoxId());
            if (checkBox == null) {
                callMethod.Log("onGoodConfirmed → Checkbox not found for ID: " + singleGood.getCheckBoxId());
                return;
            }

            // فقط اگر هنوز تیک نخورده
            if (!checkBox.isChecked()) {

                // 🔹 حذف موقت listener
                checkBox.setOnCheckedChangeListener(null);

                // 🔹 تیک زدن
                checkBox.setChecked(true);

                // 🔹 اجرای منطق اصلی فقط یک بار
                handleGoodCheck(checkBox, true, correct_row);

                // 🔹 دوباره listener اصلی رو ست کن
                int finalCorrect_row = correct_row;
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    handleGoodCheck(checkBox, isChecked, finalCorrect_row);
                });

                callMethod.Log("onGoodConfirmed → Checked good: " + singleGood.getGoodCode());
            }

        } catch (Exception e) {
            callMethod.Log("onGoodConfirmed Error → " + e.getMessage());
        }
    }


    @Override
    public void onGoodCanceled(Ocr_Good singleGood) {
        try {
            // پیدا کردن ردیف
            int correct_row = -1;
            for (int i = 0; i < ocr_goods_visible.size(); i++) {
                if (ocr_goods_visible.get(i).getGoodCode().equals(singleGood.getGoodCode())) {
                    correct_row = i;
                    break;
                }
            }

            if (correct_row == -1) {
                callMethod.Log("onGoodCanceled → Good not found: " + singleGood.getGoodCode());
                return;
            }

            // پیدا کردن چک‌باکس
            MaterialCheckBox checkBox = requireView().findViewById(singleGood.getCheckBoxId());
            if (checkBox == null) {
                callMethod.Log("onGoodCanceled → Checkbox not found for ID: " + singleGood.getCheckBoxId());
                return;
            }

            // ✅ فقط اگر تیک خورده بود
            if (checkBox.isChecked()) {

                // 🔹 حذف موقت listener تا حلقه ایجاد نشه
                checkBox.setOnCheckedChangeListener(null);

                // 🔹 برداشتن تیک
                checkBox.setChecked(false);

                // 🔹 اجرای منطق لغو
                handleGoodCheck(checkBox, false, correct_row);

                // 🔹 دوباره listener اصلی رو برگردون
                int finalCorrect_row = correct_row;
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    handleGoodCheck(checkBox, isChecked, finalCorrect_row);
                });

                callMethod.Log("onGoodCanceled → Unchecked good: " + singleGood.getGoodCode());
            }

        } catch (Exception e) {
            callMethod.Log("onGoodCanceled Error → " + e.getMessage());
        }
    }





    public void good_detail_inventory_view(Ocr_Good singleGood) {

        //if(ocr_goods_visible.get(fa).getAppRowIsControled().equals("True")){
        if(singleGood.getAppRowIsControled().equals("1")){
            callMethod.showToast("شمارش این آیتم تمام شده است");
        }else{
            ocr_action.good_detail_inventory(singleGood, TcPrintRef, this);
        }



    }
    public void good_amount_view(String Facamount,String shortage) {
        ocr_action.goodamount_detail(Facamount,shortage);
    }

    public String getBarcodeScan() {
        return BarcodeScan;
    }

    public void setBarcodeScan(String barcodeScan) {
        BarcodeScan = barcodeScan;
    }
}