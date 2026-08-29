package com.kits.kowsarapp.fragment.ocr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.kowsarapp.activity.ocr.Ocr_Check_Confirm_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_Collect_Confirm_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_NavActivity;
import com.kits.kowsarapp.application.base.NetworkUtils;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Ocr_CollectFragment extends Fragment implements OnGoodConfirmListener {

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
    LinearLayoutCompat ll_factorCode_OrderBy;

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

    SwitchMaterial sm_orderby_Ace_desc;

    String Orderby_ASC_Str=" ASC";

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


    String ShowGoodDetail;

    String TcPrintRef;
    String BarcodeScan;

    Integer width=1;
    Integer firsttry = 0;
    Integer lastCunter = 0;
    Integer row_counter;
    Integer conter_confirm = 0;

    Integer Sum_Confirm_Amount=0;

    public String getShowGoodDetail() {
        return ShowGoodDetail;
    }

    public void setShowGoodDetail(String showGoodDetail) {
        ShowGoodDetail = showGoodDetail;
    }

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


        sm_orderby_Ace_desc.setChecked(callMethod.ReadBoolan("Orderby_ASC"));

        if (callMethod.ReadBoolan("Orderby_ASC")){
            sm_orderby_Ace_desc.setText("صعودی");
        }else{
            sm_orderby_Ace_desc.setText("نزولی");
        }

        sm_orderby_Ace_desc.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                callMethod.EditBoolan("Orderby_ASC",true);
                sm_orderby_Ace_desc.setText("صعودی");
                callMethod.showToast("صعودی");
            } else {
                callMethod.EditBoolan("Orderby_ASC",false);

                sm_orderby_Ace_desc.setText("نزولی");
                callMethod.showToast("نزولی");
            }
            intent = new Intent(requireActivity(), Ocr_Collect_Confirm_Activity.class);
            intent.putExtra("ScanResponse", BarcodeScan);
            intent.putExtra("State", "0");
            intent.putExtra("FactorImage", "");
            intent.putExtra("ShowGoodDetail", "0");
            startActivity(intent);
            requireActivity().finish();
        });



        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));
        tv_factorexplain.setText(NumberFunctions.PerisanNumber(" توضیحات :   " + factor.getExplain()));


        tv_address.setText(NumberFunctions.PerisanNumber(" آدرس : " + factor.getAddress()));
        tv_phone.setText(NumberFunctions.PerisanNumber(" تلفن تماس : " + factor.getPhone()));
        tv_total_amount.setText(NumberFunctions.PerisanNumber(" تعداد کل:   " + factor.getSumAmount()));
        tv_total_price.setText(NumberFunctions.PerisanNumber(" قیمت کل : " + decimalFormat.format(Double.valueOf(factor.getSumPrice())) + " ریال"));


        btn_confirm.setText("تاییده بخش");
        btn_send.setText("ارسال تاییده");
        btn_set_stack.setText("آغاز فرآیند انبار");
        btn_shortage.setText("اعلام کسر موجودی");
        btn_print.setText("پرینت فاکتور");




        if(!factor.getNewSumPrice().equals(factor.getSumPrice())){

            TextView tv_total_newprice = new TextView(requireActivity().getApplicationContext());
            tv_total_newprice.setText(NumberFunctions.PerisanNumber(" قیمت کل(جدید) : " + decimalFormat.format(Double.valueOf(factor.getNewSumPrice())) + " ریال"));
            tv_total_newprice.setLayoutParams(
                    new LinearLayoutCompat.LayoutParams(
                            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                    )
            );
            tv_total_newprice.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            tv_total_newprice.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_total_newprice.setGravity(Gravity.RIGHT);
            tv_total_newprice.setTypeface(null, Typeface.BOLD);
            tv_total_newprice.setPadding(dp(12), dp(5), dp(12), dp(8));

            ll_factor_summary.addView(tv_total_newprice);
        }

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


            }else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrCheshme")){
                if(callMethod.ReadString("StackCategory").equals("همه")) {

                    ocr_goods_visible.add(ocr_good_single);
                    goodshow(ocr_good_single);

                } else if (ocr_good_single.getLocationTitle() != null) {

                    int LocationTitle = Integer.parseInt(ocr_good_single.getLocationTitle());

                    if (callMethod.ReadString("StackCategory").equals("انبار1") && LocationTitle >= 100000 && LocationTitle <= 199999) {
                        ocr_goods_visible.add(ocr_good_single);
                        goodshow(ocr_good_single);
                    } else if (callMethod.ReadString("StackCategory").equals("انبار2") && LocationTitle > 200000 && LocationTitle <= 299999) {
                        ocr_goods_visible.add(ocr_good_single);
                        goodshow(ocr_good_single);

                    } else if (callMethod.ReadString("StackCategory").equals("انبار3") && LocationTitle > 300000 && LocationTitle <= 399999) {
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


        ll_factorCode_OrderBy.addView(tv_factorcode);
        ll_factorCode_OrderBy.addView(sm_orderby_Ace_desc);


        ll_title.addView(ll_factorCode_OrderBy);


        ll_title.addView(tv_customername);
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

                                                // گرفتن مدل کالا از tag
                                                Object tag = cb.getTag();
                                                Ocr_Good good = null;
                                                if (tag instanceof Ocr_Good) {
                                                    good = (Ocr_Good) tag;
                                                }

                                                // --- چک کردن shortage و رد کردن اگر کمبود داشت ---
                                                String shortageAmount = null;
                                                if (good != null) {
                                                    // اگر اسم فیلدت good_detial هست، همینو با good_detial عوض کن
                                                    shortageAmount = good.getShortageAmount();
                                                }

                                                if (shortageAmount == null) {
                                                    callMethod.Log("ShortageAmount is null");
                                                    // اینجا تصمیم با توئه:
                                                    // اگر null یعنی "کمبود نامشخص" و میخوای ردش کنی، این خط رو فعال کن:
                                                    // continue;
                                                } else {
                                                    try {
                                                        if (Integer.parseInt(shortageAmount) > 0) {
                                                            // کمبود دارد -> رد کن و برو checkbox بعدی
                                                            continue;
                                                        }
                                                    } catch (Exception e) {
                                                        callMethod.Log("ShortageAmount is not a number: " + shortageAmount);
                                                        // اگر مقدار خراب بود و میخوای ردش کنی:
                                                        // continue;
                                                    }
                                                }
                                                // --- پایان چک shortage ---

                                                // از اینجا به بعد یعنی "کمبود ندارد" و همون رفتار قبلی اجرا میشه
                                                if ("1".equals(ShowGoodDetail)) {
                                                    if (good != null) {
                                                        callMethod.Log("Good Name: " + good.getGoodName());
                                                        good_detail_view(good);
                                                    } else {
                                                        callMethod.Log("Checkbox tag is null or not Ocr_Good");
                                                    }
                                                }

                                                cb.requestFocus();

                                                scrollView_main.post(() -> {
                                                    cb.getParent().requestChildFocus(cb, cb);

                                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                        EditText edBarcode1 = requireActivity().findViewById(R.id.ocr_collect_confirm_a_barcode);
                                                        edBarcode1.selectAll();

                                                        InputMethodManager imm = (InputMethodManager) requireActivity()
                                                                .getSystemService(requireActivity().INPUT_METHOD_SERVICE);
                                                        if (imm != null) {
                                                            imm.hideSoftInputFromWindow(edBarcode1.getWindowToken(), 0);
                                                        }
                                                    }, 300);

                                                });

                                                return; // این return فقط وقتی اجرا میشه که checkbox انتخاب‌شده "کمبود نداشته باشه"
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
                        intent.putExtra("ShowGoodDetail", "0");
                        dialogProg.dismiss();
                        startActivity(intent);
                        requireActivity().finish();


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
                            call=apiInterface.CheckState("OcrControlled",factor.getAppOCRFactorCode(),"1","");

                        }else{
                            call=secendApiInterface.CheckState("OcrControlled",factor.getAppOCRFactorCode(),"1","");
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
                    call=apiInterface.CheckState("OcrControlled",factor.getAppOCRFactorCode(),"1","");

                }else{
                    call=secendApiInterface.CheckState("OcrControlled",factor.getAppOCRFactorCode(),"1","");
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
                                            "OcrControlled",
                                            single_GoodCode_check,
                                            "0",
                                            callMethod.ReadString("JobPersonRef")
                                    );
                                }else{
                                    call=secendApiInterface.OcrControlled(
                                            "OcrControlled",
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
                                                intent.putExtra("ShowGoodDetail", "0");
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
                                    "OcrControlled",
                                    single_GoodCode_check,
                                    "0",
                                    callMethod.ReadString("JobPersonRef")
                            );
                        }else{
                            call=secendApiInterface.OcrControlled(
                                    "OcrControlled",
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
                                        intent.putExtra("ShowGoodDetail", "0");
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

        ll_title = new LinearLayoutCompat(requireContext());
        ll_factorCode_OrderBy = new LinearLayoutCompat(requireContext());
        ll_good_body = new LinearLayoutCompat(requireContext());
        ll_good_body_detail = new LinearLayoutCompat(requireContext());
        ll_factor_summary = new LinearLayoutCompat(requireContext());
        ll_send_confirm = new LinearLayoutCompat(requireContext());
        ll_shortage_print = new LinearLayoutCompat(requireContext());

        ViewPager = new ViewPager(requireContext());

        tv_company = new TextView(requireContext());
        tv_customername = new TextView(requireContext());
        tv_appocrfactorexplain = new TextView(requireContext());
        tv_factorcode = new TextView(requireContext());
        tv_factordate = new TextView(requireContext());
        tv_factorexplain = new TextView(requireContext());
        tv_address = new TextView(requireContext());
        tv_phone = new TextView(requireContext());
        tv_total_amount = new TextView(requireContext());
        tv_total_price = new TextView(requireContext());

        sm_orderby_Ace_desc = new SwitchMaterial(requireContext());

        btn_confirm = new Button(requireContext());
        btn_send = new Button(requireContext());
        btn_set_stack = new Button(requireContext());
        btn_shortage = new Button(requireContext());
        btn_print = new Button(requireContext());
    }


    public void setLayoutParams(){

        LinearLayoutCompat.LayoutParams titleParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
        titleParams.setMargins(dp(8), dp(8), dp(8), dp(6));
        ll_title.setLayoutParams(titleParams);


        LinearLayoutCompat.LayoutParams factorOrderParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
        factorOrderParams.setMargins(0, dp(4), 0, dp(2));
        ll_factorCode_OrderBy.setLayoutParams(factorOrderParams);


        ll_good_body_detail.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
        );

        LinearLayoutCompat.LayoutParams goodBodyParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
        goodBodyParams.setMargins(dp(4), 0, dp(4), dp(4));
        ll_good_body.setLayoutParams(goodBodyParams);


        LinearLayoutCompat.LayoutParams summaryParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
        summaryParams.setMargins(dp(8), dp(6), dp(8), dp(6));
        ll_factor_summary.setLayoutParams(summaryParams);


        LinearLayoutCompat.LayoutParams actionRowParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
        actionRowParams.setMargins(dp(8), dp(4), dp(8), dp(8));
        ll_send_confirm.setLayoutParams(actionRowParams);


        LinearLayoutCompat.LayoutParams shortageActionParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
        shortageActionParams.setMargins(dp(8), dp(4), dp(8), dp(4));
        ll_shortage_print.setLayoutParams(shortageActionParams);


        tv_company.setLayoutParams(matchWrapParams());
        tv_appocrfactorexplain.setLayoutParams(matchWrapParams());
        tv_customername.setLayoutParams(matchWrapParams());

        LinearLayoutCompat.LayoutParams factorCodeParams =
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        1f
                );
        factorCodeParams.setMargins(0, 0, dp(6), 0);
        tv_factorcode.setLayoutParams(factorCodeParams);


        LinearLayoutCompat.LayoutParams switchParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
        sm_orderby_Ace_desc.setLayoutParams(switchParams);


        tv_factordate.setLayoutParams(matchWrapParams());
        tv_factorexplain.setLayoutParams(matchWrapParams());
        tv_address.setLayoutParams(matchWrapParams());
        tv_phone.setLayoutParams(matchWrapParams());
        tv_total_amount.setLayoutParams(matchWrapParams());
        tv_total_price.setLayoutParams(matchWrapParams());


        setActionLayoutParams(btn_confirm, true);
        setActionLayoutParams(btn_send, false);
        LinearLayoutCompat.LayoutParams stackButtonParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
        stackButtonParams.setMargins(0, dp(4), 0, dp(4));
        btn_set_stack.setLayoutParams(stackButtonParams);
        btn_set_stack.setMinHeight(dp(48));

        setActionLayoutParams(btn_shortage, true);
        setActionLayoutParams(btn_print, false);


        ViewPager.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(1)
                )
        );
    }

    public void setOrientation(){
        ll_title.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_factorCode_OrderBy.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_good_body.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_good_body_detail.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_factor_summary.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_send_confirm.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_shortage_print.setOrientation(LinearLayoutCompat.HORIZONTAL);
    }

    public void setLayoutDirection(){
        ll_title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_factorCode_OrderBy.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        sm_orderby_Ace_desc.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        ll_good_body.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_factor_summary.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_send_confirm.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_shortage_print.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    @SuppressLint("RtlHardcoded")
    public void setGravity(){
        tv_company.setGravity(Gravity.CENTER);
        tv_appocrfactorexplain.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv_customername.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv_factorcode.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        sm_orderby_Ace_desc.setGravity(Gravity.CENTER);

        tv_factordate.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv_factorexplain.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv_address.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv_phone.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv_total_amount.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv_total_price.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        btn_confirm.setGravity(Gravity.CENTER);
        btn_send.setGravity(Gravity.CENTER);
        btn_set_stack.setGravity(Gravity.CENTER);
        btn_shortage.setGravity(Gravity.CENTER);
        btn_print.setGravity(Gravity.CENTER);
    }

    public void setTextSize(){

        int configuredTitleSize = configuredTextSize("TitleSize", 16);
        int informationSize = Math.max(13, configuredTitleSize - 2);
        int buttonSize = Math.max(13, configuredTitleSize - 1);

        tv_company.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.min(20, configuredTitleSize + 2)
        );

        tv_appocrfactorexplain.setTextSize(TypedValue.COMPLEX_UNIT_SP, informationSize);
        tv_customername.setTextSize(TypedValue.COMPLEX_UNIT_SP, informationSize);
        tv_factorcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, informationSize);
        sm_orderby_Ace_desc.setTextSize(TypedValue.COMPLEX_UNIT_SP, informationSize);
        tv_factordate.setTextSize(TypedValue.COMPLEX_UNIT_SP, informationSize);
        tv_factorexplain.setTextSize(TypedValue.COMPLEX_UNIT_SP, informationSize);
        tv_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, informationSize);
        tv_phone.setTextSize(TypedValue.COMPLEX_UNIT_SP, informationSize);

        tv_total_amount.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.min(18, configuredTitleSize)
        );

        tv_total_price.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.min(18, configuredTitleSize)
        );

        btn_confirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonSize);
        btn_send.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonSize);
        btn_set_stack.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonSize);
        btn_shortage.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonSize);
        btn_print.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonSize);

        tv_company.setTypeface(null, Typeface.BOLD);
        tv_factorcode.setTypeface(null, Typeface.BOLD);
        tv_total_amount.setTypeface(null, Typeface.BOLD);
        tv_total_price.setTypeface(null, Typeface.BOLD);
    }

    public void setBackgroundResource(){

        ll_main.setPadding(dp(2), dp(2), dp(2), dp(12));
        ll_main.setBackgroundColor(Color.parseColor("#F1F4F8"));

        ll_title.setBackground(
                roundedBackground(
                        Color.WHITE,
                        Color.parseColor("#DCE3EA"),
                        1,
                        14
                )
        );
        ll_title.setElevation(dp(2));


        tv_company.setBackground(
                roundedBackground(
                        ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark),
                        ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark),
                        0,
                        12
                )
        );

        tv_appocrfactorexplain.setBackground(
                roundedBackground(
                        Color.parseColor("#EAF3FF"),
                        Color.parseColor("#C7DCF7"),
                        1,
                        9
                )
        );

        applyInformationBackground(tv_customername);
        applyInformationBackground(tv_factorcode);
        applyInformationBackground(tv_factordate);
        applyInformationBackground(tv_factorexplain);
        applyInformationBackground(tv_address);
        applyInformationBackground(tv_phone);

        sm_orderby_Ace_desc.setBackground(
                roundedBackground(
                        Color.parseColor("#F2F5F8"),
                        Color.parseColor("#D5DCE5"),
                        1,
                        10
                )
        );


        ll_factor_summary.setBackground(
                roundedBackground(
                        Color.parseColor("#F7FBF8"),
                        Color.parseColor("#CDE6D3"),
                        1,
                        12
                )
        );

        ViewPager.setBackgroundColor(Color.parseColor("#D5DDE6"));


        applyActionButton(
                btn_confirm,
                ContextCompat.getColor(requireContext(), R.color.green_800),
                Color.WHITE
        );

        applyActionButton(
                btn_send,
                ContextCompat.getColor(requireContext(), R.color.red_700),
                Color.WHITE
        );

        applyActionButton(
                btn_set_stack,
                ContextCompat.getColor(requireContext(), R.color.blue_500),
                Color.WHITE
        );

        applyActionButton(
                btn_shortage,
                ContextCompat.getColor(requireContext(), R.color.orange_500),
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
        );

        applyActionButton(
                btn_print,
                ContextCompat.getColor(requireContext(), R.color.blue_500),
                Color.WHITE
        );
    }


    public void setTextColor(){

        int primaryText =
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark);

        tv_company.setTextColor(Color.WHITE);
        tv_appocrfactorexplain.setTextColor(primaryText);
        tv_customername.setTextColor(primaryText);
        tv_factorcode.setTextColor(primaryText);
        sm_orderby_Ace_desc.setTextColor(primaryText);
        tv_factordate.setTextColor(primaryText);
        tv_factorexplain.setTextColor(primaryText);
        tv_address.setTextColor(primaryText);
        tv_phone.setTextColor(primaryText);

        tv_total_amount.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.green_800)
        );

        tv_total_price.setTextColor(primaryText);
    }


    public void setPadding(){

        tv_company.setPadding(dp(12), dp(10), dp(12), dp(10));

        setCompactInfoPadding(tv_appocrfactorexplain);
        setCompactInfoPadding(tv_customername);
        setCompactInfoPadding(tv_factorcode);
        setCompactInfoPadding(tv_factordate);
        setCompactInfoPadding(tv_factorexplain);
        setCompactInfoPadding(tv_address);
        setCompactInfoPadding(tv_phone);

        sm_orderby_Ace_desc.setPadding(dp(10), dp(7), dp(10), dp(7));

        tv_total_amount.setPadding(dp(12), dp(8), dp(12), dp(4));
        tv_total_price.setPadding(dp(12), dp(4), dp(12), dp(8));
    }



    private int dp(int value) {
        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }


    private int configuredTextSize(String key, int defaultValue) {
        try {
            return Integer.parseInt(callMethod.ReadString(key));
        } catch (Exception ignored) {
            return defaultValue;
        }
    }


    private LinearLayoutCompat.LayoutParams matchWrapParams() {
        return new LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        );
    }


    private void setActionLayoutParams(Button button, boolean addEndMargin) {

        LinearLayoutCompat.LayoutParams params =
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        1f
                );

        if (addEndMargin) {
            params.setMargins(0, 0, dp(5), 0);
        } else {
            params.setMargins(dp(5), 0, 0, 0);
        }

        button.setLayoutParams(params);
        button.setMinHeight(dp(48));
    }


    private GradientDrawable roundedBackground(
            int fillColor,
            int strokeColor,
            int strokeWidthDp,
            int radiusDp
    ) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setCornerRadius(dp(radiusDp));

        if (strokeWidthDp > 0) {
            drawable.setStroke(dp(strokeWidthDp), strokeColor);
        }

        return drawable;
    }


    private void applyInformationBackground(TextView textView) {
        textView.setBackground(
                roundedBackground(
                        Color.parseColor("#F7F9FC"),
                        Color.parseColor("#E0E6ED"),
                        1,
                        9
                )
        );
    }


    private void applyActionButton(
            Button button,
            int backgroundColor,
            int textColor
    ) {

        button.setAllCaps(false);
        button.setTextColor(textColor);
        button.setTypeface(null, Typeface.BOLD);
        button.setPadding(dp(8), dp(10), dp(8), dp(10));
        button.setElevation(dp(1));

        button.setBackground(
                roundedBackground(
                        backgroundColor,
                        backgroundColor,
                        0,
                        10
                )
        );
    }


    private void setCompactInfoPadding(TextView textView) {
        textView.setPadding(dp(10), dp(7), dp(10), dp(7));

        LinearLayoutCompat.LayoutParams params =
                (LinearLayoutCompat.LayoutParams) textView.getLayoutParams();

        params.setMargins(0, dp(2), 0, dp(2));
        textView.setLayoutParams(params);
    }


    private String safeText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }


    private String normalizeDecimal(Object value) {

        String rawValue = safeText(value);

        if (rawValue.isEmpty() || rawValue.equalsIgnoreCase("null")) {
            return "0";
        }

        try {
            return new BigDecimal(rawValue)
                    .stripTrailingZeros()
                    .toPlainString();
        } catch (Exception ignored) {
            return rawValue;
        }
    }


    private boolean isPositiveDecimal(Object value) {

        String rawValue = safeText(value);

        if (rawValue.isEmpty() || rawValue.equalsIgnoreCase("null")) {
            return false;
        }

        try {
            return new BigDecimal(rawValue)
                    .compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception ignored) {
            return false;
        }
    }


    private String getThirdColumnTitle() {

        String company =
                safeText(callMethod.ReadString("EnglishCompanyNameUse"));

        if (company.equals("OcrGostaresh")) {
            return "فرم";
        }

        if (company.equals("OcrCheshme")) {
            return "موقعیت";
        }

        if (company.equals("OcrMahris")) {
            return "قفسه";
        }

        return "قیمت";
    }


    private String getThirdColumnValue(Ocr_Good good) {

        String company =
                safeText(callMethod.ReadString("EnglishCompanyNameUse"));

        if (company.equals("OcrGostaresh")) {
            return safeText(good.getFormNo());
        }

        if (company.equals("OcrCheshme")) {
            return safeText(good.getLocationTitle());
        }

        if (company.equals("OcrMahris")) {
            return safeText(good.getLocationTitle());
        }

        return normalizeDecimal(good.getGoodMaxSellPrice());
    }


    private void styleValueBox(
            TextView textView,
            String title,
            String value,
            int textColor,
            int backgroundColor,
            int borderColor
    ) {

        textView.setText(
                NumberFunctions.PerisanNumber(
                        title + "\n" + (value.isEmpty() ? "-" : value)
                )
        );

        textView.setTextColor(textColor);
        textView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.max(12, configuredTextSize("TitleSize", 16) - 2)
        );

        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        textView.setMinHeight(dp(56));
        textView.setMaxLines(2);
        textView.setPadding(dp(5), dp(6), dp(5), dp(6));
        textView.setClickable(true);
        textView.setFocusable(true);

        textView.setBackground(
                roundedBackground(
                        backgroundColor,
                        borderColor,
                        1,
                        9
                )
        );
    }



    @SuppressLint("RtlHardcoded")
    public void goodshow(Ocr_Good good_detial){

        row_counter++;


        LinearLayoutCompat ll_factor_row =
                new LinearLayoutCompat(requireContext());

        LinearLayoutCompat ll_details =
                new LinearLayoutCompat(requireContext());

        LinearLayoutCompat ll_radif_check =
                new LinearLayoutCompat(requireContext());

        LinearLayoutCompat ll_name_price =
                new LinearLayoutCompat(requireContext());


        TextView tv_gap = new TextView(requireContext());
        TextView tv_good_part1 = new TextView(requireContext());
        TextView tv_good_part2 = new TextView(requireContext());
        TextView tv_good_part3 = new TextView(requireContext());

        View vp_radif_name = new View(requireContext());
        View vp_name_amount = new View(requireContext());
        View vp_amount_price = new View(requireContext());
        View bottomSpace = new View(requireContext());

        MaterialCheckBox checkBox =
                new MaterialCheckBox(requireContext());

        checkBox.setTag(good_detial);


        LinearLayoutCompat.LayoutParams rowParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );

        rowParams.setMargins(dp(7), dp(3), dp(7), dp(3));
        ll_factor_row.setLayoutParams(rowParams);


        ll_details.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
        );

        ll_details.setMinimumHeight(dp(72));
        ll_details.setPadding(dp(7), dp(7), dp(7), dp(7));
        ll_details.setElevation(dp(1));


        ll_radif_check.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        1.25f
                )
        );

        ll_name_price.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        7.75f
                )
        );


        vp_radif_name.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        dp(1),
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
        );

        vp_name_amount.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        dp(1),
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
        );

        vp_amount_price.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        dp(1),
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
        );

        bottomSpace.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(2)
                )
        );


        tv_gap.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        dp(2),
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
        );


        tv_good_part1.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        5.1f
                )
        );

        tv_good_part2.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        1.7f
                )
        );

        tv_good_part3.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        2.2f
                )
        );


        checkBox.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
        );


        ll_details.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_radif_check.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_name_price.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ll_factor_row.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_details.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_radif_check.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_name_price.setOrientation(LinearLayoutCompat.HORIZONTAL);

        ll_details.setGravity(Gravity.CENTER_VERTICAL);
        ll_radif_check.setGravity(Gravity.CENTER);
        ll_name_price.setGravity(Gravity.CENTER_VERTICAL);

        checkBox.setGravity(Gravity.CENTER);
        tv_good_part1.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv_good_part2.setGravity(Gravity.CENTER);
        tv_good_part3.setGravity(Gravity.CENTER);


        int titleSize = configuredTextSize("TitleSize", 16);

        checkBox.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.max(11, titleSize - 5)
        );

        tv_good_part1.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.max(13, titleSize)
        );

        tv_good_part1.setTypeface(null, Typeface.BOLD);
        tv_good_part1.setMaxLines(2);
        tv_good_part1.setEllipsize(TextUtils.TruncateAt.END);
        tv_good_part1.setPadding(dp(8), dp(5), dp(8), dp(5));
        tv_good_part1.setClickable(true);
        tv_good_part1.setFocusable(true);


        int checkBoxId = View.generateViewId();

        checkBox.setText(
                NumberFunctions.PerisanNumber(
                        String.valueOf(row_counter)
                )
        );

        checkBox.setId(checkBoxId);
        ocr_goods_visible.get(row_counter - 1).setCheckBoxId(checkBoxId);


        boolean hasShortage =
                isPositiveDecimal(good_detial.getShortageAmount());

        String facAmount =
                normalizeDecimal(good_detial.getFacAmount());

        String shortageAmount =
                normalizeDecimal(good_detial.getShortageAmount());

        String amountTitle =
                hasShortage ? "کسری" : "تعداد";

        String amountValue =
                hasShortage ? shortageAmount : facAmount;

        String thirdTitle = getThirdColumnTitle();
        String thirdValue = getThirdColumnValue(good_detial);


        tv_good_part1.setText(
                NumberFunctions.PerisanNumber(
                        safeText(good_detial.getGoodName())
                )
        );

        tv_good_part1.setTextColor(
                ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryDark
                )
        );


        styleValueBox(
                tv_good_part2,
                amountTitle,
                amountValue,
                hasShortage
                        ? ContextCompat.getColor(requireContext(), R.color.red_800)
                        : ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark),
                hasShortage
                        ? Color.parseColor("#FFF0F0")
                        : Color.parseColor("#F4F7FA"),
                hasShortage
                        ? Color.parseColor("#F1B5B5")
                        : Color.parseColor("#DDE4EB")
        );


        styleValueBox(
                tv_good_part3,
                thirdTitle,
                thirdValue,
                ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryDark
                ),
                Color.parseColor("#F4F7FA"),
                Color.parseColor("#DDE4EB")
        );


        checkBox.setTextColor(
                ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryDark
                )
        );


        vp_radif_name.setBackgroundColor(Color.parseColor("#E2E7ED"));
        vp_name_amount.setBackgroundColor(Color.TRANSPARENT);
        vp_amount_price.setBackgroundColor(Color.TRANSPARENT);


        int rowColor =
                row_counter % 2 == 0
                        ? Color.parseColor("#F8FAFC")
                        : Color.WHITE;

        int borderColor =
                hasShortage
                        ? Color.parseColor("#E2A5A5")
                        : Color.parseColor("#DCE3EA");


        if (
                (
                        safeText(callMethod.ReadString("EnglishCompanyNameUse"))
                                .equals("OcrQoqnoos")
                                ||
                                safeText(callMethod.ReadString("EnglishCompanyNameUse"))
                                        .equals("OcrQoqnoosOnline")
                )
                        &&
                        (
                                safeText(good_detial.getMinAmount()).equals("1")
                                        ||
                                        safeText(good_detial.getMinAmount()).equals("1.000")
                        )
        ) {

            rowColor = Color.parseColor("#FFF2F2");
            borderColor = Color.parseColor("#EDBABA");
        }


        ll_details.setBackground(
                roundedBackground(
                        rowColor,
                        borderColor,
                        1,
                        11
                )
        );


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

        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(bottomSpace);

        ll_good_body_detail.addView(ll_factor_row);


        int correct_row = row_counter - 1;


        if (
                "1".equals(
                        safeText(
                                ocr_goods_visible
                                        .get(correct_row)
                                        .getAppRowIsControled()
                        )
                )
        ) {

            checkBox.setChecked(true);
            checkBox.setEnabled(false);

        } else {

            if (callMethod.ReadBoolan("JustScanner")) {

                try {
                    checkBox.setEnabled(
                            !"دارد".equals(
                                    safeText(
                                            good_detial.getBarCodePrintState()
                                    )
                            )
                    );
                } catch (Exception e) {
                    callMethod.Log(e.getMessage());
                    checkBox.setEnabled(false);
                }

            } else {
                checkBox.setEnabled(true);
            }
        }


        if (callMethod.ReadString("Category").equals("1")) {
            checkBox.setVisibility(View.GONE);
            ll_radif_check.setVisibility(View.GONE);
            vp_radif_name.setVisibility(View.GONE);
        }


        checkBox.setOnClickListener(v -> {

            if (callMethod.ReadBoolan("CheckListFromGoodDialog")) {

                good_detail_view(
                        ocr_goods_visible.get(correct_row)
                );

                checkBox.toggle();

            } else {

                if (callMethod.ReadBoolan("JustScanner")) {

                    if (
                            "ندارد".equals(
                                    safeText(
                                            good_detial.getBarCodePrintState()
                                    )
                            )
                    ) {

                        callMethod.Log(
                                "BarCodePrint = "
                                        + good_detial.getBarCodePrintState()
                        );

                        ocr_goods_scan.clear();
                        ocr_goods_scan.add(good_detial);

                        if (
                                factor.getAppOCRFactorExplain()
                                        .contains(
                                                callMethod.ReadString(
                                                        "StackCategory"
                                                )
                                        )
                        ) {

                            checkBox.setChecked(false);

                            ocr_action.GoodScanDetail(
                                    ocr_goods_scan,
                                    state,
                                    getBarcodeScan()
                            );

                        } else {

                            callMethod.showToast(
                                    "لطفا ابتدا آغاز فرایند انبار را شروع کنید"
                            );
                        }

                    } else {

                        callMethod.Log(
                                "BarCodePrint = "
                                        + good_detial.getBarCodePrintState()
                        );
                    }
                }
            }
        });


        checkBox.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        handleGoodCheck(
                                checkBox,
                                isChecked,
                                correct_row
                        )
        );


        tv_good_part1.setOnClickListener(v -> {

            if (
                    factor.getAppOCRFactorExplain()
                            .contains(
                                    callMethod.ReadString(
                                            "StackCategory"
                                    )
                            )
            ) {

                good_detail_view(
                        ocr_goods_visible.get(correct_row)
                );

            } else {

                callMethod.showToast(
                        "لطفا ابتدا آغاز فرایند انبار را شروع کنید"
                );
            }
        });


        tv_good_part2.setOnClickListener(v -> {

            if (
                    factor.getAppOCRFactorExplain()
                            .contains(
                                    callMethod.ReadString(
                                            "StackCategory"
                                    )
                            )
            ) {

                good_amount_view(
                        ocr_goods_visible
                                .get(correct_row)
                                .getFacAmount(),

                        safeText(
                                ocr_goods_visible
                                        .get(correct_row)
                                        .getShortageAmount()
                        )
                );

            } else {

                callMethod.showToast(
                        "لطفا ابتدا آغاز فرایند انبار را شروع کنید"
                );
            }
        });
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
                        good_detail_view(ocr_goods_visible.get(correct_row));
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
        NewView();
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
                    call = apiInterface.CheckState("OcrControlled", factor.getAppOCRFactorCode(), "1", callMethod.ReadString("Deliverer"));
                } else {
                    call = secendApiInterface.CheckState("OcrControlled", factor.getAppOCRFactorCode(), "1", callMethod.ReadString("Deliverer"));
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
            applyActionButton(
                    btn_confirm,
                    ContextCompat.getColor(requireContext(), R.color.grey_60),
                    ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
            );
            btn_confirm.setEnabled(false);
            callMethod.showToast("اماده ارسال می باشد");
        }else{
            if(callMethod.ReadBoolan("AutoSend")){
                if(ocr_goods_visible.size() == ConfirmCounter_stack){
                    ocr_print.Printing(factor,ocr_goods_visible,"0","0");
                }
            }
            applyActionButton(
                    btn_send,
                    ContextCompat.getColor(requireContext(), R.color.grey_60),
                    ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
            );
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

        ll_factorCode_OrderBy.addView(tv_factorcode);
        ll_factorCode_OrderBy.addView(sm_orderby_Ace_desc);

        ll_title.addView(ll_factorCode_OrderBy);

        ll_title.addView(tv_customername);
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

        applyActionButton(
                btn_confirm,
                ContextCompat.getColor(requireContext(), R.color.red_500),
                Color.WHITE
        );
        btn_confirm.setEnabled(true);

        applyActionButton(
                btn_send,
                ContextCompat.getColor(requireContext(), R.color.green_500),
                Color.WHITE
        );
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
                                intent.putExtra("ShowGoodDetail", "0");
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
    public View CreateGoodViewForshortage(
            @NonNull Ocr_Good good,
            int countergood
    ) {

        arraygood_shortage.add(
                new String[]{
                        good.getAppOCRFactorRowCode(),
                        good.getFacAmount()
                }
        );


        LinearLayoutCompat ll_factor_row =
                new LinearLayoutCompat(requireContext());

        LinearLayoutCompat ll_details =
                new LinearLayoutCompat(requireContext());

        LinearLayoutCompat ll_radif_check =
                new LinearLayoutCompat(requireContext());

        LinearLayoutCompat ll_name_price =
                new LinearLayoutCompat(requireContext());
        ll_radif_check.setVisibility(View.GONE);

        TextView tv_gap = new TextView(requireContext());
        TextView tv_goodname = new TextView(requireContext());
        TextView tv_amount = new TextView(requireContext());

        EditText et_amountshortage =
                new EditText(requireContext());

        View vp_radif_name = new View(requireContext());
        View vp_name_amount = new View(requireContext());
        View vp_amount_price = new View(requireContext());
        View bottomSpace = new View(requireContext());

        CheckBox checkBox =
                new MaterialCheckBox(requireContext());


        LinearLayoutCompat.LayoutParams rowParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );

        rowParams.setMargins(dp(7), dp(3), dp(7), dp(3));
        ll_factor_row.setLayoutParams(rowParams);


        ll_details.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
        );

        ll_details.setMinimumHeight(dp(72));
        ll_details.setPadding(dp(7), dp(7), dp(7), dp(7));
        ll_details.setElevation(dp(1));


        ll_radif_check.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        1.25f
                )
        );

        ll_name_price.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        7.75f
                )
        );


        vp_radif_name.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        dp(1),
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
        );

        vp_name_amount.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        dp(1),
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
        );

        vp_amount_price.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        dp(1),
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
        );

        bottomSpace.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(2)
                )
        );


        tv_gap.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        dp(2),
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
        );

        tv_goodname.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        5.1f
                )
        );

        tv_amount.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        1.7f
                )
        );

        et_amountshortage.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        2.2f
                )
        );


        checkBox.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
        );


        ll_details.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_radif_check.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_name_price.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ll_factor_row.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_details.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_radif_check.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_name_price.setOrientation(LinearLayoutCompat.HORIZONTAL);

        ll_details.setGravity(Gravity.CENTER_VERTICAL);
        ll_radif_check.setGravity(Gravity.CENTER);
        ll_name_price.setGravity(Gravity.CENTER_VERTICAL);

        checkBox.setGravity(Gravity.CENTER);
        tv_goodname.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv_amount.setGravity(Gravity.CENTER);
        et_amountshortage.setGravity(Gravity.CENTER);


        int bodySize = configuredTextSize("BodySize", 15);

        checkBox.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.max(11, bodySize - 3)
        );

        tv_goodname.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.max(13, bodySize)
        );

        tv_goodname.setTypeface(null, Typeface.BOLD);
        tv_goodname.setMaxLines(2);
        tv_goodname.setEllipsize(TextUtils.TruncateAt.END);

        tv_amount.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.max(12, bodySize - 1)
        );

        et_amountshortage.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.max(12, bodySize - 1)
        );


        checkBox.setText(
                NumberFunctions.PerisanNumber(
                        String.valueOf(countergood)
                )
        );
        checkBox.setVisibility(View.GONE);

        tv_goodname.setText(
                NumberFunctions.PerisanNumber(
                        safeText(good.getGoodName())
                )
        );

        tv_amount.setText(
                NumberFunctions.PerisanNumber(
 normalizeDecimal(
                                good.getFacAmount()
                        )
                )
        );

        et_amountshortage.setHint(
                NumberFunctions.PerisanNumber(
                        "کسری تا "
                                + normalizeDecimal(
                                good.getFacAmount()
                        )
                )
        );

        et_amountshortage.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );


        int primaryText =
                ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryDark
                );

        checkBox.setTextColor(primaryText);
        tv_goodname.setTextColor(primaryText);
        tv_amount.setTextColor(primaryText);
        et_amountshortage.setTextColor(primaryText);
        et_amountshortage.setHintTextColor(
                Color.parseColor("#7A8795")
        );


        tv_goodname.setPadding(dp(8), dp(5), dp(8), dp(5));

        tv_amount.setPadding(dp(5), dp(6), dp(5), dp(6));
        tv_amount.setTypeface(null, Typeface.BOLD);
        tv_amount.setBackground(
                roundedBackground(
                        Color.parseColor("#F4F7FA"),
                        Color.parseColor("#DDE4EB"),
                        1,
                        9
                )
        );

        et_amountshortage.setPadding(dp(6), dp(5), dp(6), dp(5));
        et_amountshortage.setSingleLine(true);
        et_amountshortage.setSelectAllOnFocus(true);
        et_amountshortage.setBackground(
                roundedBackground(
                        Color.WHITE,
                        Color.parseColor("#F0A8A8"),
                        1,
                        9
                )
        );


        vp_radif_name.setBackgroundColor(Color.parseColor("#E2E7ED"));
        vp_name_amount.setBackgroundColor(Color.TRANSPARENT);
        vp_amount_price.setBackgroundColor(Color.TRANSPARENT);


        int rowColor =
                countergood % 2 == 0
                        ? Color.parseColor("#F8FAFC")
                        : Color.WHITE;

        int borderColor = Color.parseColor("#DCE3EA");


        if (
                safeText(good.getMinAmount()).equals("1")
                        ||
                        safeText(good.getMinAmount()).equals("1.000")
        ) {
            rowColor = Color.parseColor("#FFF2F2");
            borderColor = Color.parseColor("#EDBABA");
        }


        ll_details.setBackground(
                roundedBackground(
                        rowColor,
                        borderColor,
                        1,
                        11
                )
        );


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

        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(bottomSpace);


        int correct_row = countergood - 1;


        if (
                "1".equals(
                        safeText(
                                ocr_goods_visible
                                        .get(correct_row)
                                        .getAppRowIsPacked()
                        )
                )
        ) {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        } else {
            checkBox.setEnabled(true);
        }


        if (callMethod.ReadString("Category").equals("1")) {
            checkBox.setVisibility(View.GONE);
        }


        checkBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    String goodCode =
                            ocr_goods_visible
                                    .get(correct_row)
                                    .getAppOCRFactorRowCode();

                    if (isChecked) {

                        ocr_goods_visible
                                .get(correct_row)
                                .setAppRowIsControled("1");

                        if (!Array_GoodCodesCheck.contains(goodCode)) {
                            Array_GoodCodesCheck.add(goodCode);
                        }

                    } else {

                        ocr_goods_visible
                                .get(correct_row)
                                .setAppRowIsControled("0");

                        Array_GoodCodesCheck.remove(goodCode);
                    }
                }
        );


        tv_goodname.setOnClickListener(
                v -> good_detail_view(
                        ocr_goods_visible.get(correct_row)
                )
        );


        et_amountshortage.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {
                    }


                    @Override
                    public void afterTextChanged(Editable text) {

                        try {

                            if (firsttry == 0) {
                                arraygood_shortage.clear();
                                firsttry = 1;
                            }

                            String newAmount =
                                    text.toString();

                            String goodCode =
                                    good.getAppOCRFactorRowCode();


                            if (!newAmount.isEmpty()) {

                                BigDecimal amount =
                                        new BigDecimal(newAmount);

                                BigDecimal factorAmount =
                                        new BigDecimal(
                                                safeText(
                                                        good.getFacAmount()
                                                )
                                        );


                                if (
                                        amount.compareTo(
                                                factorAmount
                                        ) > 0
                                ) {

                                    et_amountshortage.setText("");

                                    callMethod.showToast(
                                            "از مقدار فاکتور بیشتر می باشد"
                                    );

                                } else {

                                    boolean found = false;

                                    for (
                                            int i = 0;
                                            i < arraygood_shortage.size();
                                            i++
                                    ) {

                                        if (
                                                arraygood_shortage
                                                        .get(i)[0]
                                                        .equals(goodCode)
                                        ) {

                                            arraygood_shortage
                                                    .get(i)[1] =
                                                    newAmount;

                                            found = true;
                                            break;
                                        }
                                    }


                                    if (!found) {

                                        arraygood_shortage.add(
                                                new String[]{
                                                        goodCode,
                                                        newAmount
                                                }
                                        );
                                    }
                                }
                            }

                        } catch (Exception ignored) {
                        }
                    }
                }
        );


        return ll_factor_row;
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





    public void good_detail_view(Ocr_Good singleGood) {
//        ocr_action.good_detail(singleGood,BarcodeScan);
        ocr_action.good_detail(singleGood, BarcodeScan, this);

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