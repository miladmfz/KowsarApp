package com.kits.kowsarapp.fragment.ocr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.model.ocr.Ocr_StackEnumeration;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.text.DecimalFormat;
import java.util.ArrayList;



public class Ocr_StackEnumeration_Janamie_Fragment extends Fragment  {
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



    ArrayList<Ocr_Good> ocr_goods=new ArrayList<>();


    ArrayList<Ocr_StackEnumeration> stackEnumerations=new ArrayList<>();


    ScrollView scrollView_main ;
    LinearLayoutCompat ll_main;
    LinearLayoutCompat ll_title;
    LinearLayoutCompat ll_good_body_detail;
    LinearLayoutCompat ll_good_body;
    LinearLayoutCompat ll_send_confirm;

    androidx.viewpager.widget.ViewPager ViewPager;

    Button btn_confirm;


    TextView tv_Fragment_title;
    TextView tv_StackEnumeration_Code;
    TextView tv_StackEnumeration_Date;
    TextView tv_StackEnumeration_Title;
    TextView tv_StackEnumeration_Explain;

    String StackEnumerationCode;
    String StackEnumerationState_Str;

    Integer width=1;
    Integer firsttry = 0;
    Integer lastCunter = 0;
    Integer row_counter;
    Integer conter_confirm = 0;

    Integer Sum_Confirm_Amount=0;




    public ArrayList<Ocr_StackEnumeration> getStackEnumerations() {
        return stackEnumerations;
    }

    public void setStackEnumerations(ArrayList<Ocr_StackEnumeration> stackEnumerations) {
        this.stackEnumerations = stackEnumerations;
    }


    public String getStackEnumerationCode() {
        return StackEnumerationCode;
    }

    public void setStackEnumerationCode(String stackEnumerationCode) {
        StackEnumerationCode = stackEnumerationCode;
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
            callMethod.Log("55");

            ocr_dbh = new Ocr_DBH(requireActivity(), callMethod.ReadString("DatabaseName"));
            ocr_action = new Ocr_Action(requireActivity());
            apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
            secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
            handler=new Handler();
            callMethod.Log("66");

            DisplayMetrics metrics = new DisplayMetrics();
            view.getDisplay().getMetrics(metrics);
            width =metrics.widthPixels;
            dialogProg = new Dialog(requireActivity());
            dialogProg.setContentView(R.layout.ocr_spinner_box);
            dialogProg.findViewById(R.id.ocr_spinner_text).setVisibility(View.GONE);
            callMethod.Log("77");
            CreateView_Control();
        }catch (Exception e){
            callMethod.Log(e.getMessage());

        }
    }



    @SuppressLint("RtlHardcoded")
    public void CreateView_Control(){
        callMethod.Log("88");
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

        callMethod.Log("0");



        tv_Fragment_title.setText(NumberFunctions.PerisanNumber("انبارگردانی"));

        switch (stackEnumerations.get(0).getEnumerationState()) {
            case "1":
                StackEnumerationState_Str = "شمارش اول";
                break;
            case "2":
                StackEnumerationState_Str = "شمارش دوم";
                break;
            case "3":
                StackEnumerationState_Str = "شمارش سوم";
                break;
            case "0":
                StackEnumerationState_Str = "در مرحله انتخاب کالا";
                break;
        }

        callMethod.Log("1");
        tv_StackEnumeration_Title.setText(NumberFunctions.PerisanNumber(" عنوان :   " + stackEnumerations.get(0).getStackEnumerationTitle()));
        tv_StackEnumeration_Code.setText(NumberFunctions.PerisanNumber(" کد انبارگردانی :   " + stackEnumerations.get(0).getStackEnumerationCode()+ "  "+StackEnumerationState_Str));
        tv_StackEnumeration_Date.setText(NumberFunctions.PerisanNumber(" تارخ انبارگردانی :   " + stackEnumerations.get(0).getStackEnumerationDate()));
        tv_StackEnumeration_Explain.setText(NumberFunctions.PerisanNumber(" توضیحات :   " + stackEnumerations.get(0).getExplain()));

        callMethod.Log("2");
        btn_confirm.setText("Test");

        callMethod.Log("3");

        row_counter= 0;

        for (Ocr_StackEnumeration Single_stackEnumeration : stackEnumerations) {
            StackEnumerationRow_show(Single_stackEnumeration);
        }




        ll_title.addView(tv_Fragment_title);
        ll_title.addView(tv_StackEnumeration_Title);
        ll_title.addView(tv_StackEnumeration_Code);
        ll_title.addView(tv_StackEnumeration_Date);
        ll_title.addView(tv_StackEnumeration_Explain);


        ll_title.addView(ViewPager);

        ll_send_confirm.addView(btn_confirm);

        ll_good_body.addView(ll_good_body_detail);


        ll_main.addView(ll_title);
        ll_main.addView(ll_good_body);


        ll_main.addView(ll_send_confirm);

        btn_confirm.setOnClickListener(v -> {

            callMethod.Log("test");

        });


    }


    public void NewView(){

        ll_title = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body_detail = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_send_confirm = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ViewPager = new ViewPager(requireActivity().getApplicationContext());
        tv_Fragment_title = new TextView(requireActivity().getApplicationContext());
        tv_StackEnumeration_Title = new TextView(requireActivity().getApplicationContext());
        tv_StackEnumeration_Code = new TextView(requireActivity().getApplicationContext());
        tv_StackEnumeration_Date = new TextView(requireActivity().getApplicationContext());
        tv_StackEnumeration_Explain = new TextView(requireActivity().getApplicationContext());
        btn_confirm = new Button(requireActivity().getApplicationContext());
    }

    public void setLayoutParams(){

        ll_title.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_send_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

        tv_Fragment_title.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_StackEnumeration_Title.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_StackEnumeration_Code.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_StackEnumeration_Date.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_StackEnumeration_Explain.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        btn_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));

         ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 3));


    }
    public void setOrientation(){
        ll_title.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_good_body.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_good_body_detail.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_send_confirm.setOrientation(LinearLayoutCompat.HORIZONTAL);
    }
    public void setLayoutDirection(){
        ll_title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_send_confirm.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
    @SuppressLint("RtlHardcoded")
    public void setGravity(){
        tv_Fragment_title.setGravity(Gravity.CENTER);
        tv_StackEnumeration_Title.setGravity(Gravity.RIGHT);
        tv_StackEnumeration_Code.setGravity(Gravity.RIGHT);
        tv_StackEnumeration_Date.setGravity(Gravity.RIGHT);
        tv_StackEnumeration_Explain.setGravity(Gravity.RIGHT);
        btn_confirm.setGravity(Gravity.CENTER);
    }
    public void setTextSize(){
        tv_Fragment_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_StackEnumeration_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_StackEnumeration_Code.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_StackEnumeration_Date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_StackEnumeration_Explain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        btn_confirm.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));

    }
    public void setBackgroundResource(){

        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        btn_confirm.setBackgroundResource(R.color.green_800);

    }

    public void setTextColor(){
        tv_Fragment_title.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_StackEnumeration_Title.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_StackEnumeration_Code.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_StackEnumeration_Date.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_StackEnumeration_Explain.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
    }

    public void setPadding(){
        tv_Fragment_title.setPadding(0, 0, 30, 20);
        tv_StackEnumeration_Title.setPadding(0, 0, 30, 20);
        tv_StackEnumeration_Code.setPadding(0, 0, 30, 20);
        tv_StackEnumeration_Date.setPadding(0, 0, 30, 20);
        tv_StackEnumeration_Explain.setPadding(0, 0, 30, 20);
        btn_confirm.setPadding(0, 0, 30, 20);
    }


    @SuppressLint("RtlHardcoded")
    public void StackEnumerationRow_show(Ocr_StackEnumeration stackEnumerationrow){
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
        TextView tv_radif = new TextView(requireActivity().getApplicationContext());
        TextView tv_good_part1 = new TextView(requireActivity().getApplicationContext());
        TextView tv_good_part2 = new TextView(requireActivity().getApplicationContext());
        TextView tv_good_part3 = new TextView(requireActivity().getApplicationContext());


        ll_factor_row.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_details.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_radif_check.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 7.7));
        ll_name_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 1.3));
        vp_rows.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
        vp_radif_name.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_name_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_amount_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(20, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_radif.setLayoutParams(new LinearLayoutCompat.LayoutParams(20, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_good_part1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)1.5));
        tv_good_part2.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)4));
        tv_good_part3.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)3.5));


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
        tv_gap.setGravity(Gravity.CENTER);
        tv_radif.setGravity(Gravity.CENTER);
        tv_good_part1.setGravity(Gravity.RIGHT);
        tv_good_part2.setGravity(Gravity.CENTER);
        tv_good_part3.setGravity(Gravity.CENTER);


        tv_good_part1.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        tv_good_part2.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize"))+3);
        tv_good_part2.setTypeface(null, Typeface.BOLD);

        tv_good_part3.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));




        try {


            tv_radif.setText(NumberFunctions.PerisanNumber(row_counter.toString()));
            tv_good_part1.setText(NumberFunctions.PerisanNumber(stackEnumerationrow.getGoodName()));

            if (stackEnumerationrow.getEnumerationState().equals("1")){
                tv_good_part2.setText(NumberFunctions.PerisanNumber(stackEnumerationrow.getFirstNumeration()));

            }else  if (stackEnumerationrow.getEnumerationState().equals("2")){
                tv_good_part2.setText(NumberFunctions.PerisanNumber(stackEnumerationrow.getSecondNumeration()));

            }else  if (stackEnumerationrow.getEnumerationState().equals("3")){
                tv_good_part2.setText(NumberFunctions.PerisanNumber(stackEnumerationrow.getThirdNumeration()));

            }else  if (stackEnumerationrow.getEnumerationState().equals("0")) {

                tv_good_part2.setText("غیر فعال");
            }


            if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                    callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
                tv_good_part3.setText(NumberFunctions.PerisanNumber(stackEnumerationrow.getGoodExplain2()));


            } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
                tv_good_part3.setText(NumberFunctions.PerisanNumber(stackEnumerationrow.getFormNo()));
            } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrMahris")){
                tv_good_part3.setText(NumberFunctions.PerisanNumber(stackEnumerationrow.getGoodExplain3()));
            }else{
                tv_good_part3.setText(NumberFunctions.PerisanNumber(stackEnumerationrow.getMaxSellPrice()));
            }

            tv_gap.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_radif.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_good_part1.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_good_part2.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_good_part3.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));

            tv_good_part3.setPadding(0, 10, 0, 10);
            tv_good_part1.setPadding(0, 10, 5, 10);




        }catch (Exception e){
            callMethod.Log("kowsar "+ e.getMessage());
        }

        ll_radif_check.addView(tv_radif);
        ll_radif_check.addView(tv_gap);


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




        ll_details.setOnClickListener(v -> {
            good_detail_StackEnumeration_view(stackEnumerations.get(correct_row));
//
//                //if(ocr_goods_visible.get(fa).getAppRowIsControled().equals("True")){
//                if(stackEnumerations.get(correct_row).getStackLockFlag().equals("1")){
//                    callMethod.showToast("شمارش این آیتم بسته شده است");
//
//                }else{
//
//                }

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




    public void good_detail_StackEnumeration_view(Ocr_StackEnumeration single_StackEnumeration) {

        //if(ocr_goods_visible.get(fa).getAppRowIsControled().equals("True")){
        if(single_StackEnumeration.getStackLockFlag().equals("1")){
            callMethod.showToast("شمارش این آیتم بسته شده است");
        }else{
            ocr_action.good_detail_StackEnumeration_janamie(single_StackEnumeration);
        }

    }

}