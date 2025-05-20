package com.kits.kowsarapp.application.ocr;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kits.kowsarapp.activity.ocr.Ocr_ConfigActivity;
import com.kits.kowsarapp.activity.ocr.Ocr_ConfirmActivity;
import com.kits.kowsarapp.activity.ocr.Ocr_FactorListLocalActivity;
import com.kits.kowsarapp.adapter.ocr.Ocr_GoodScan_Adapter;
import com.kits.kowsarapp.application.base.CallMethod;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.Job;
import com.kits.kowsarapp.model.base.JobPerson;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Ocr_Action extends Activity implements DatePickerDialog.OnDateSetListener {

    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;
    Ocr_DBH ocr_dbh;
    private final Context mContext;
    CallMethod callMethod;
    String coltrol_s = "";
    String reader_s = "";
    String pack_s = "";
    String sendtime = "";
    String packCount = "";
    ArrayList<Job> jobs;
    String date = "";
    TextView ed_pack_h_date;
    Dialog dialog, dialogProg;
    TextView tv_rep;
    Ocr_Print print;
    Handler handler;
    ArrayList<Ocr_Good> Empty_goods = new ArrayList<>();

    public Ocr_Action(Context mcontxt) {
        this.mContext = mcontxt;
        callMethod = new CallMethod(mContext);
        ocr_dbh = new Ocr_DBH(mContext, callMethod.ReadString("DatabaseName"));

        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);

        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

        dialog = new Dialog(mcontxt);
        dialogProg = new Dialog(mContext);
        print = new Ocr_Print(mContext);


    }
    public void dialogProg() {
        dialogProg.setContentView(R.layout.ocr_spinner_box);
        tv_rep = dialogProg.findViewById(R.id.ocr_spinner_text);
        tv_rep.setVisibility(View.GONE);
        dialogProg.show();
    }
    public void dialogProg_dismiss() {
        dialogProg.dismiss();
    }
    public void factor_detail(Factor factor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.ocr_dialog_factordetail);


        TextView tv_AppOCRFactorCode = dialog.findViewById(R.id.ocr_factordialog_d_appocrfactorcode);
        TextView tv_AppTcPrintRef = dialog.findViewById(R.id.ocr_factordialog_d_apptcprintref);
        TextView tv_AppControlDate = dialog.findViewById(R.id.ocr_factordialog_d_appcontroldate);
        TextView tv_AppPackDate = dialog.findViewById(R.id.ocr_factordialog_d_apppackdate);
        TextView tv_AppReader = dialog.findViewById(R.id.ocr_factordialog_d_appreader);
        TextView tv_AppControler = dialog.findViewById(R.id.ocr_factordialog_d_appcontroler);
        TextView tv_AppPacker = dialog.findViewById(R.id.ocr_factordialog_d_apppacker);
        TextView tv_AppPackDeliverDate = dialog.findViewById(R.id.ocr_factordialog_d_apppackdeliverdate);
        TextView tv_AppPackCount = dialog.findViewById(R.id.ocr_factordialog_d_apppackcount);
        TextView tv_AppDeliverer = dialog.findViewById(R.id.ocr_factordialog_d_appdeliverer);
        TextView tv_IsEdited = dialog.findViewById(R.id.ocr_factordialog_d_isedited);
        TextView tv_HasSignature = dialog.findViewById(R.id.ocr_factordialog_d_hassignature);


        TextView tv_FactorPrivateCode = dialog.findViewById(R.id.ocr_factordialog_d_factorprivatecode);
        TextView tv_FactorDate = dialog.findViewById(R.id.ocr_factordialog_d_factordate);
        TextView tv_CustName = dialog.findViewById(R.id.ocr_factordialog_d_custName);
        TextView tv_customercode = dialog.findViewById(R.id.ocr_factordialog_d_customercode);
        TextView tv_Ersall = dialog.findViewById(R.id.ocr_factordialog_d_ersall);
        TextView tv_BrokerName = dialog.findViewById(R.id.ocr_factordialog_d_brokername);
        TextView tv_AppFactorState = dialog.findViewById(R.id.ocr_factordialog_d_appfactorstate);
        Button btn_1 = dialog.findViewById(R.id.ocr_factordialog_d_btn1);
        Button btn_2 = dialog.findViewById(R.id.ocr_factordialog_d_btn2);

        tv_AppOCRFactorCode.setText(NumberFunctions.PerisanNumber(factor.getAppOCRFactorCode()));
        tv_AppTcPrintRef.setText(NumberFunctions.PerisanNumber(factor.getAppTcPrintRef()));
        tv_AppControlDate.setText(NumberFunctions.PerisanNumber(factor.getAppControlDate()));
        tv_AppPacker.setText(NumberFunctions.PerisanNumber(factor.getAppPacker()));
        tv_AppPackDeliverDate.setText(NumberFunctions.PerisanNumber(factor.getAppPackDeliverDate()));
        tv_AppPackCount.setText(NumberFunctions.PerisanNumber(factor.getAppPackCount()));
        tv_AppDeliverer.setText(NumberFunctions.PerisanNumber(factor.getAppDeliverer()));

        tv_FactorPrivateCode.setText(NumberFunctions.PerisanNumber(factor.getFactorPrivateCode()));
        tv_FactorDate.setText(NumberFunctions.PerisanNumber(factor.getFactorDate()));
        tv_CustName.setText(NumberFunctions.PerisanNumber(factor.getCustName()));
        tv_customercode.setText(NumberFunctions.PerisanNumber(factor.getCustomercode()));
        //TODO change Ersal be customer path
        tv_Ersall.setText(NumberFunctions.PerisanNumber(factor.getErsall()));

        if (factor.getBrokerName().length() > 20)
            tv_BrokerName.setText(NumberFunctions.PerisanNumber(factor.getBrokerName().substring(0, 20) + "..."));
        else
            tv_BrokerName.setText(NumberFunctions.PerisanNumber(factor.getBrokerName()));

        tv_AppFactorState.setText(NumberFunctions.PerisanNumber(factor.getAppFactorState()));


        tv_AppPackDate.setText(NumberFunctions.PerisanNumber(factor.getAppPackDate()));
        tv_AppReader.setText(NumberFunctions.PerisanNumber(factor.getAppReader()));
        tv_AppControler.setText(NumberFunctions.PerisanNumber(factor.getAppControler()));


//        if (factor.getIsEdited().equals("True")) {
        if (factor.getIsEdited().equals("1")) {
            tv_IsEdited.setText("دارد");
        } else {
            tv_IsEdited.setText("ندارد");
        }
//        if (factor.getIsEdited().equals("True")) {
        if (factor.getIsEdited().equals("1")) {
            tv_HasSignature.setText("دارد");
        } else {
            tv_HasSignature.setText("ندارد");
        }

//        if (factor.getAppIsDelivered().equals("False")) {
        if (factor.getAppIsDelivered().equals("0")) {
            btn_1.setVisibility(View.GONE);
        } else {
            btn_1.setVisibility(View.VISIBLE);

        }

        btn_1.setOnClickListener(v -> {

            dialogProg();

            Call<RetrofitResponse> call1;
            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call1=apiInterface.ExitDelivery("ExitDelivery", factor.getAppOCRFactorCode());
            }else{
                call1=secendApiInterface.ExitDelivery("ExitDelivery", factor.getAppOCRFactorCode());
            }







            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    dialog.dismiss();
                    dialogProg.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                }
            });


        });

        btn_2.setOnClickListener(v -> {
            Pack_detail(factor,"0");
            dialog.dismiss();
        });


        dialog.show();


    }


    public void Pack_detail(Factor factor,String detail_flag) {

        if (detail_flag.equals("0")){

            dialogProg();

            Call<RetrofitResponse> call3;

            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call3=apiInterface.OcrControlled(
                        "OcrControlled_new",
                        factor.getAppOCRFactorCode(),
                        "3",
                        callMethod.ReadString("JobPersonRef")
                );
            }else{
                call3=secendApiInterface.OcrControlled(
                        "OcrControlled_new",
                        factor.getAppOCRFactorCode(),
                        "3",
                        callMethod.ReadString("JobPersonRef")
                );
            }


            call3.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {


                    Call<RetrofitResponse> call2;
                    if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                        call2=apiInterface.SetPackDetail(
                                "SetPackDetail_new",
                                factor.getAppOCRFactorCode(),
                                "",
                                callMethod.ReadString("Deliverer"),
                                "",
                                "",
                                "0"

                        );
                    }else{
                        call2=secendApiInterface.SetPackDetail(
                                "SetPackDetail_new",
                                factor.getAppOCRFactorCode(),
                                "",
                                callMethod.ReadString("Deliverer"),
                                "",
                                "",
                                "0"

                        );
                    }

                    call2.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                            dialog.dismiss();
//                                if (!callMethod.ReadString("Category").equals("5")) {
//                                    OcrPrintPacker(factor);
//                                }
                            if (!callMethod.ReadString("Category").equals("5")) {
                                print.Printing(factor,Empty_goods,packCount,"0");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    callMethod.Log(t.getMessage());
                }
            });






        }else{


            dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setContentView(R.layout.ocr_packdetail_box);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

            MaterialButton btn_pack_h_send = dialog.findViewById(R.id.ocr_packdetail_b_send);
            MaterialButton btn_pack_h_5 = dialog.findViewById(R.id.ocr_packdetail_b_btn5);
            EditText ed_pack_h_amount = dialog.findViewById(R.id.ocr_packdetail_b_packamount);

            ed_pack_h_date = dialog.findViewById(R.id.ocr_packdetail_b_senddate);

            PersianCalendar persianCalendar = new PersianCalendar();
            String tmonthOfYear, tdayOfMonth;
            tmonthOfYear = "0" + (persianCalendar.getPersianMonth() + 1);
            tdayOfMonth = "0" + persianCalendar.getPersianDay();
            date = persianCalendar.getPersianYear() + "/"
                    + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                    + tdayOfMonth.substring(tdayOfMonth.length() - 2);

            ed_pack_h_date.setText(NumberFunctions.PerisanNumber(date));

            LinearLayoutCompat ll_pack_h_main = dialog.findViewById(R.id.ocr_packdetail_b_linejob);
            sendtime=NumberFunctions.PerisanNumber(date);
            Call<RetrofitResponse> call;
            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call=apiInterface.GetJob("TestJob", "Ocr3");
            }else{
                call=secendApiInterface.GetJob("TestJob", "Ocr3");
            }

            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        jobs = response.body().getJobs();

                        for (Job job : jobs) {

                            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                                    70
                            );
                            params.setMargins(30, 30, 30, 30);
                            LinearLayoutCompat ll_new = new LinearLayoutCompat(mContext.getApplicationContext());
                            ll_new.setLayoutParams(params);
                            ll_new.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                            ll_new.setOrientation(LinearLayoutCompat.HORIZONTAL);
                            ll_new.setWeightSum(2);


                            TextView Tv_new = new TextView(mContext.getApplicationContext());
                            Tv_new.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
                            Tv_new.setText(job.getTitle());
                            Tv_new.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

                            ll_new.addView(Tv_new);


                            Call<RetrofitResponse> call1;
                            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                                call1=apiInterface.GetJobPerson("TestJobPerson", job.getTitle());
                            }else{
                                call1=secendApiInterface.GetJobPerson("TestJobPerson", job.getTitle());
                            }

                            call1.enqueue(new Callback<RetrofitResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
                                        ArrayList<JobPerson> jobPersons = response.body().getJobPersons();
                                        ArrayList<String> jobpersonsstr_new = new ArrayList<>();

                                        jobpersonsstr_new.add("برای انتخاب کلیک کنید");

                                        for (JobPerson jobPerson : jobPersons) {
                                            jobpersonsstr_new.add(jobPerson.getName());
                                        }

                                        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(mContext,
                                                android.R.layout.simple_spinner_item, jobpersonsstr_new);
                                        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        Spinner spinner_new = new Spinner(mContext.getApplicationContext());
                                        spinner_new.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
                                        spinner_new.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                        spinner_new.setAdapter(spinner_adapter);

                                        try {
                                            spinner_new.setSelection(Integer.parseInt(callMethod.ReadString(job.getTitle())));
                                        } catch (Exception e) {
                                            spinner_new.setSelection(0);

                                        }

                                        spinner_new.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                callMethod.EditString(job.getTitle(), String.valueOf(position));
                                                job.setText(jobpersonsstr_new.get(position));
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {
                                            }
                                        });
                                        ll_new.addView(spinner_new);

                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                                }
                            });
                            ll_pack_h_main.addView(ll_new);
                        }


                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                }
            });

            btn_pack_h_5.setOnClickListener(v -> {

                PersianCalendar persianCalendar1 = new PersianCalendar();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        this,
                        persianCalendar1.getPersianYear(),
                        persianCalendar1.getPersianMonth(),
                        persianCalendar1.getPersianDay()
                );


                datePickerDialog.show(((Activity) mContext).getFragmentManager(), "Datepickerdialog");
            });

            callMethod.Log(sendtime);
            btn_pack_h_send.setOnClickListener(v -> {
                coltrol_s = "";
                reader_s = "";
                pack_s = "";

                if (ed_pack_h_amount.getText().toString().equals("")) {
                    packCount = "1";
                } else
                    packCount = NumberFunctions.EnglishNumber(ed_pack_h_amount.getText().toString());

                boolean falt = false;
                String falt_message = "";

                for (Job job : jobs) {


                    // TODO qoqnos shod 1-2-3
                    // TODO gostaresh shod 3-4-5

                    if (!job.getText().equals("برای انتخاب کلیک کنید")) {
                        if (job.getJobCode().equals("1")) {
                            coltrol_s = job.getText();
                        }
                        if (job.getJobCode().equals("2")) {
                            reader_s = job.getText();
                        }
                        if (job.getJobCode().equals("3")) {
                            pack_s = job.getText();
                        }
                    } else {
                        falt = true;
                        falt_message = job.getTitle();
                        break;
                    }
                }


                if (!falt) {
                    dialogProg();

                    Call<RetrofitResponse> call3;

                    if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                        call3=apiInterface.OcrControlled(
                                "OcrControlled_new",
                                factor.getAppOCRFactorCode(),
                                "3",
                                callMethod.ReadString("JobPersonRef")
                        );
                    }else{
                        call3=secendApiInterface.OcrControlled(
                                "OcrControlled_new",
                                factor.getAppOCRFactorCode(),
                                "3",
                                callMethod.ReadString("JobPersonRef")
                        );
                    }


                    call3.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

//                        Call<RetrofitResponse> call2;
//
//
//                        String Body_str  = "";
//
//                        Body_str =callMethod.CreateJson("OcrFactorCode", factor.getAppOCRFactorCode(), Body_str);
//                        Body_str =callMethod.CreateJson("Reader", reader_s, Body_str);
//                        Body_str =callMethod.CreateJson("Controler", coltrol_s, Body_str);
//                        Body_str =callMethod.CreateJson("Packer", pack_s, Body_str);
//                        Body_str =callMethod.CreateJson("PackDeliverDate", NumberFunctions.EnglishNumber(date), Body_str);
//                        Body_str =callMethod.CreateJson("PackCount", packCount, Body_str);
//                        Body_str =callMethod.CreateJson("AppDeliverDate", sendtime, Body_str);
//
//
//                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//                            call2 = apiInterface.SetPackDetail(
//                                    callMethod.RetrofitBody(Body_str)
//                            );
//
//                        }else{
//                            call2 = secendApiInterface.SetPackDetail(
//                                    callMethod.RetrofitBody(Body_str)
//                            );
//
//                        }
                            Call<RetrofitResponse> call2;
                            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                                call2=apiInterface.SetPackDetail(
                                        "SetPackDetail_new",
                                        factor.getAppOCRFactorCode(),
                                        reader_s,
                                        coltrol_s,
                                        pack_s,
                                        NumberFunctions.EnglishNumber(date),
                                        packCount

                                );
                            }else{
                                call2=secendApiInterface.SetPackDetail(
                                        "SetPackDetail_new",
                                        factor.getAppOCRFactorCode(),
                                        reader_s,
                                        coltrol_s,
                                        pack_s,
                                        NumberFunctions.EnglishNumber(date),
                                        packCount

                                );
                            }

                            call2.enqueue(new Callback<RetrofitResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                    dialog.dismiss();
//                                if (!callMethod.ReadString("Category").equals("5")) {
//                                    OcrPrintPacker(factor);
//                                }
                                    if (!callMethod.ReadString("Category").equals("5")) {
                                        print.Printing(factor,Empty_goods,packCount,"0");
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                                }
                            });
                        }

                        @Override
                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            callMethod.Log(t.getMessage());
                        }
                    });


                } else {
                    callMethod.showToast(falt_message + " را تکمیل کنید");
                }


            });


            dialog.show();




        }


    }

    public void goodamount_detail(String amount,String shortage) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.ocr_amount_zoom);
        TextView tv_good_1 = dialog.findViewById(R.id.ocr_amountzoome_tv1);
        TextView tv_good_2 = dialog.findViewById(R.id.ocr_amountzoome_tv2);
        TextView tv_good_3 = dialog.findViewById(R.id.ocr_amountzoome_tv3);

        tv_good_1.setText(NumberFunctions.PerisanNumber(amount));
        int finalShortage = (shortage != null||shortage != "null") ? Integer.parseInt(shortage) : 0;
        tv_good_2.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(amount) - finalShortage)));

        tv_good_3.setText(NumberFunctions.PerisanNumber(shortage));

        dialog.show();
    }
    public void checkSumAmounthint(Factor factor) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.ocr_checkamount);
        EditText edamount = dialog.findViewById(R.id.ocr_checkamount_c_edamount);
        MaterialButton btncheckamount = dialog.findViewById(R.id.ocr_checkamount_c_btncheckamount);

        edamount.setText(factor.getSumAmount());
        edamount.setEnabled(false);
        btncheckamount.setVisibility(View.GONE);

        dialog.show();


    }

    public void checkSumAmount(Factor factor) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.ocr_checkamount);
        EditText edamount = dialog.findViewById(R.id.ocr_checkamount_c_edamount);
        MaterialButton btncheckamount = dialog.findViewById(R.id.ocr_checkamount_c_btncheckamount);

        callMethod.Log("factor.getSumAmount()"+ factor.getSumAmount());


        btncheckamount.setOnClickListener(v -> {
            callMethod.Log("factor.getSumAmount()"+ factor.getSumAmount());

            if (NumberFunctions.EnglishNumber(edamount.getText().toString()).equals(factor.getSumAmount())) {
                Pack_detail(factor,"1");
            }else {
                callMethod.showToast("تعداد وارد شده صحیح نیست");
            }
        });
        dialog.show();


    }


    public void good_detail(Ocr_Good singleGood,String BarcodeScan) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.ocr_gooddetail_box);
        ImageView iv_good = dialog.findViewById(R.id.ocr_gooddetail_b_img);
        TextView tv_good_1 = dialog.findViewById(R.id.ocr_gooddetail_b_tv1);
        TextView tv_good_2 = dialog.findViewById(R.id.ocr_gooddetail_b_tv2);
        TextView tv_good_3 = dialog.findViewById(R.id.ocr_gooddetail_b_tv3);
        TextView tv_good_4 = dialog.findViewById(R.id.ocr_gooddetail_b_tv4);
        TextView tv_good_5 = dialog.findViewById(R.id.ocr_gooddetail_b_tv5);
        TextView tv_good_6 = dialog.findViewById(R.id.ocr_gooddetail_b_tv6);


        TextView lb_good_1 = dialog.findViewById(R.id.ocr_gooddetail_b_lb1);
        TextView lb_good_2 = dialog.findViewById(R.id.ocr_gooddetail_b_lb2);
        TextView lb_good_3 = dialog.findViewById(R.id.ocr_gooddetail_b_lb3);
        TextView lb_good_4 = dialog.findViewById(R.id.ocr_gooddetail_b_lb4);
        TextView lb_good_5 = dialog.findViewById(R.id.ocr_gooddetail_b_lb5);
        TextView lb_good_6 = dialog.findViewById(R.id.ocr_gooddetail_b_lb6);

        LinearLayoutCompat ll_good_6= dialog.findViewById(R.id.ocr_gooddetail_ll_lb6);

        LinearLayoutCompat ll_amonut = dialog.findViewById(R.id.ocr_gooddetail_ll1_tv1);

        MaterialButton btn_confirm = dialog.findViewById(R.id.ocr_gooddetail_b_btn);


        Call<RetrofitResponse> call;
//        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//            call=apiInterface.GetOcrGoodDetail("GetOcrGoodDetail", GoodCode);
//        }else{
//            call=secendApiInterface.GetOcrGoodDetail("GetOcrGoodDetail", GoodCode);
//        }
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetGoodDetail("GetOcrGoodDetail_new", singleGood.getGoodCode());
        }else{
            call=secendApiInterface.GetGoodDetail("GetOcrGoodDetail_new", singleGood.getGoodCode());
        }
        callMethod.Log(singleGood.getGoodCode());
        call.enqueue(new Callback<RetrofitResponse>() {

            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Ocr_Good> ocr_goods = response.body().getOcr_Goods();


                    if (!callMethod.ReadBoolan("ShowAmount")){
                        ll_amonut.setVisibility(View.GONE);
                    }

                    if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                            callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

                        if (callMethod.ReadString("FactorDbName").equals("PakhshQOQNOOS")){

                            lb_good_1.setText("موجودی کل");
                            lb_good_2.setText("قطع");
                            lb_good_3.setText("نوع جلد");
                            lb_good_4.setText("پشت جلد");
                            lb_good_5.setText("شماره قفسه");


                        }else if (callMethod.ReadString("FactorDbName").equals("Afarinegan")){

                            lb_good_1.setText("موجودی کل");
                            lb_good_2.setText("قطع");
                            lb_good_3.setText("نوع جلد");
                            lb_good_4.setText("پشت جلد");
                            lb_good_5.setText("شماره قفسه");


                        }



                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getCoverType()));
                        tv_good_4.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getPageNo()));
                        tv_good_5.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getGoodExplain2()));
                        ll_good_6.setVisibility(View.GONE);


                    } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
                        lb_good_1.setText("موجودی کل");
                        lb_good_2.setText("قطع");
                        lb_good_3.setText("نوع جلد");
                        lb_good_4.setText("پشت جلد");
                        lb_good_5.setText("شماره قفسه");
                        lb_good_6.setText("نیاز فاکتور");

                        tv_good_6.setText(NumberFunctions.PerisanNumber(singleGood.getFacAmount()));

                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getCoverType()));
                        tv_good_4.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getMaxSellPrice()));
                        tv_good_5.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFormNo()));

                    }else{
                        ll_good_6.setVisibility(View.GONE);

                        lb_good_1.setText("موجودی کل");
                        lb_good_2.setText("قطع");
                        lb_good_3.setText("نوع جلد");
                        lb_good_4.setText("پشت جلد");
                        lb_good_5.setText("شماره قفسه");

                        tv_good_1.setText(ocr_goods.get(0).getTotalAvailable());
                        tv_good_2.setText(ocr_goods.get(0).getSize());
                        tv_good_3.setText(ocr_goods.get(0).getCoverType());
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                callMethod.Log(t.getMessage());
            }
        });
        byte[] BaseImageByte;
        BaseImageByte = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
        iv_good.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length), BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getWidth() * 2, BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getHeight() * 2, false));

        Call<RetrofitResponse> call2;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call2=apiInterface.GetImage("getImage", singleGood.getGoodCode(), 0, 400);
        }else{
            call2=secendApiInterface.GetImage("getImage", singleGood.getGoodCode(), 0, 400);
        }

        call2.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        byte[] imageByteArray1;
                        imageByteArray1 = Base64.decode(response.body().getText(), Base64.DEFAULT);
                        iv_good.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));

                    } catch (Exception ignored) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {

                callMethod.Log(t.getMessage());
            }
        });


        if (callMethod.ReadBoolan("ListOrSingle") || BarcodeScan.equals("")) {

            btn_confirm.setVisibility(View.GONE);
        }else{
            btn_confirm.setVisibility(View.VISIBLE);
            btn_confirm.setOnClickListener(v -> {


                Call<RetrofitResponse> call1;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                    call1 = apiInterface.OcrControlled(
                            "OcrControlled_new",
                            singleGood.getAppOCRFactorRowCode(),
                            "0",
                            callMethod.ReadString("JobPersonRef")
                    );
                } else {
                    call1 = secendApiInterface.OcrControlled(
                            "OcrControlled_new",
                            singleGood.getAppOCRFactorRowCode(),
                            "0",
                            callMethod.ReadString("JobPersonRef")
                    );
                }


                callMethod.Log("call=" + call1.request().url());
                callMethod.Log("call=" + call1.request().toString());


                call1.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call1, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            callMethod.Log("step 2");

                            assert response.body() != null;
                            Intent intent = new Intent(mContext, Ocr_ConfirmActivity.class);
                            intent.putExtra("ScanResponse", BarcodeScan);
                            intent.putExtra("State", "0");
                            intent.putExtra("FactorImage", "");
                            mContext.startActivity(intent);
                            ((Activity) mContext).finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call1, @NonNull Throwable t) {

                        callMethod.Log(t.getMessage());

                    }
                });


            });
        }


        dialog.show();
    }














    public void GoodScanDetail(ArrayList<Ocr_Good> goodspass, String state, String barcodescan) {

        ArrayList<Ocr_Good> Currctgoods = new ArrayList<>();
        ArrayList<Ocr_Good> CurrctgoodsForBarcode = new ArrayList<>();

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.ocr_goodscan_box);
        RecyclerView goodscan_recycler = dialog.findViewById(R.id.ocr_goodscan_b_recyclerView);
        Button goodscan_btn = dialog.findViewById(R.id.ocr_goodscan_b_btn);
        TextView goodscan_tvstatus = dialog.findViewById(R.id.ocr_goodscan_b_status);
        EditText ed_goodscan = dialog.findViewById(R.id.ocr_goodscan_b_ed);


        if (goodspass.size() > 0) {
            for (Ocr_Good good : goodspass) {
                if (state.equals("0")){
                    //if (good.getAppRowIsControled().equals("False")) {
                    if (good.getAppRowIsControled().equals("0")) {
                        Currctgoods.add(good);
                    }
                }
                if (state.equals("1")) {
                    //if (good.getAppRowIsPacked().equals("False")) {
                    if (good.getAppRowIsPacked().equals("0")) {
                        Currctgoods.add(good);
                    }
                }
            }
            if (Currctgoods.size() > 0) {
                Ocr_GoodScan_Adapter goodscanadapter = new Ocr_GoodScan_Adapter(Currctgoods, mContext, state, barcodescan);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 1);//grid
                goodscan_recycler.setLayoutManager(gridLayoutManager);
                goodscan_recycler.setAdapter(goodscanadapter);
                goodscan_recycler.setItemAnimator(new DefaultItemAnimator());
            } else {
                goodscan_tvstatus.setText("اسکن شده");
            }

        } else {
            goodscan_tvstatus.setText("در این فکتور وجود ندارد");
        }

        goodscan_btn.setOnClickListener(view -> dialog.dismiss());
        ed_goodscan.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }



                    @Override
                    public void afterTextChanged( Editable editable) {

                        dialogProg();
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {


                            CurrctgoodsForBarcode.clear();
                            if (goodspass.size() > 0) {
                                for (Ocr_Good good : goodspass) {
                                    if (state.equals("0"))
                                        if (good.getAppRowIsControled().equals("0")) {
                                            CurrctgoodsForBarcode.add(good);
                                        }
                                    if (state.equals("1"))
                                        if (good.getAppRowIsPacked().equals("0")) {
                                            CurrctgoodsForBarcode.add(good);
                                        }
                                }
                                if (CurrctgoodsForBarcode.size() == 1) {

                                    if (state.equals("0")){

                                        Call<RetrofitResponse> call;
                                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                                            call=apiInterface.CheckState("OcrControlled_new", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "0", "");
                                        }else{
                                            call=secendApiInterface.CheckState("OcrControlled_new", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "0", "");
                                        }

                                        call.enqueue(new Callback<RetrofitResponse>() {
                                            @Override
                                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                                if (response.isSuccessful()) {

                                                    Intent intent = new Intent(mContext, Ocr_ConfirmActivity.class);
                                                    intent.putExtra("ScanResponse", barcodescan);
                                                    intent.putExtra("State", "0");
                                                    ((Activity) mContext).finish();
                                                    mContext.startActivity(intent);
                                                }
                                            }
                                            @Override
                                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                                                callMethod.Log(t.getMessage());
                                            }
                                        });

                                    }else if (state.equals("1"))
                                    {

                                        Call<RetrofitResponse> call;
                                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                                            call=apiInterface.CheckState("OcrControlled_new", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "2", "");
                                        }else{
                                            call=secendApiInterface.CheckState("OcrControlled_new", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "2", "");
                                        }
                                        call.enqueue(new Callback<RetrofitResponse>() {
                                            @Override
                                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                                if (response.isSuccessful()) {

                                                    Intent intent = new Intent(mContext, Ocr_ConfirmActivity.class);
                                                    intent.putExtra("ScanResponse", barcodescan);
                                                    intent.putExtra("State", "1");
                                                    ((Activity) mContext).finish();
                                                    mContext.startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                                                callMethod.Log(t.getMessage());                                            }
                                        });

                                    }
                                }
                            }

                        },  Integer.parseInt(callMethod.ReadString("Delay")));


                    }

                }
        );

        dialog.show();
    }


    public void LoginSetting() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.default_loginconfig);
        EditText ed_password = dialog.findViewById(R.id.d_loginconfig_ed);
        MaterialButton btn_login = dialog.findViewById(R.id.d_loginconfig_btn);
        ed_password.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(final Editable editable) {

                        if(NumberFunctions.EnglishNumber(ed_password.getText().toString()).length()>5) {
                            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {

                                Intent intent = new Intent(mContext, Ocr_ConfigActivity.class);
                                mContext.startActivity(intent);
                            } else {
                                callMethod.showToast("رمز عبور صیحیح نیست");
                            }

                        }
                    }
                });

        btn_login.setOnClickListener(v -> {
            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {
                Intent intent = new Intent(mContext, Ocr_ConfigActivity.class);
                mContext.startActivity(intent);
            }else {
                callMethod.showToast("رمز عبور صیحیح نیست");
            }
        });
        dialog.show();
    }

    public void GetOcrFactorDetail(Factor factor) {

        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetOcrFactorDetail(
                    "GetOcrFactorDetail",
                    factor.getAppOCRFactorCode()
            );
        }else{
            call=secendApiInterface.GetOcrFactorDetail(
                    "GetOcrFactorDetail",
                    factor.getAppOCRFactorCode()
            );
        }

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    Factor Factor=response.body().getFactors().get(0);
                    factor_detail(Factor);
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {}
        });


    }


    public void sendfactor(final String factor_code, String signatureimage) {

        app_info();
        dialogProg();

        Call<String> call;


        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call =apiInterface.getImageData("SaveOcrImage", signatureimage, factor_code);
        }else {
            call =secendApiInterface.getImageData("SaveOcrImage", signatureimage, factor_code);
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                callMethod.showToast("فاکتور ارسال گردید");

                ocr_dbh.Insert_IsSent(factor_code);

                Intent bag = new Intent(mContext, Ocr_FactorListLocalActivity.class);
                bag.putExtra("IsSent", "0");
                bag.putExtra("signature", "0");
                dialogProg.dismiss();
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(bag);
                ((Activity) mContext).overridePendingTransition(0, 0);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });


//
//
//        Call<RetrofitResponse> call2;
//
//
//        String Body_str  = "";
//        Body_str =callMethod.CreateJson("barcode", factor_code, Body_str);
//
//        Body_str =callMethod.CreateJson("ImageStr", signatureimage, Body_str);
//
//
//        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//            call2 = apiInterface.SaveOcrImage(
//                    callMethod.RetrofitBody(Body_str)
//            );
//
//        }else{
//            call2 = secendApiInterface.SaveOcrImage(
//                    callMethod.RetrofitBody(Body_str)
//            );
//
//        }


//        call2.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
//                callMethod.showToast("فاکتور ارسال گردید");
//
//                dbh.Insert_IsSent(factor_code);
//
//                Intent bag = new Intent(mContext, Ocr_FactorListLocalActivity.class);
//                bag.putExtra("IsSent", "0");
//                bag.putExtra("signature", "0");
//                dialogProg.dismiss();
//                ((Activity) mContext).finish();
//                ((Activity) mContext).overridePendingTransition(0, 0);
//                mContext.startActivity(bag);
//                ((Activity) mContext).overridePendingTransition(0, 0);
//            }
//
//            @Override
//            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
//        callMethod.Log(t.getMessage());
//            }
//        });


    }

//
//    public void OcrPrintPacker(Factor factor) {
//
//
//
//        String Body_str  = "";
//
//        Body_str =callMethod.CreateJson("FactorCode", factor.getFactorCode(), Body_str);
//        Body_str =callMethod.CreateJson("StackCategory", callMethod.ReadString("StackCategory"), Body_str);
//        Body_str =callMethod.CreateJson("Sender", callMethod.ReadString("Deliverer"), Body_str);
//
//        Call<RetrofitResponse> call = apiInterface.OcrPrintPacker(callMethod.RetrofitBody(Body_str));
//
//        call.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
//                if (response.isSuccessful()) {
//                    ((Activity) mContext).finish();
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
//                ((Activity) mContext).finish();
//            }
//        });
//
//    }
//    public void OcrPrintControler(Factor factor) {
//
//
//
//        String Body_str  = "";
//
//        Body_str =callMethod.CreateJson("FactorCode", factor.getFactorCode(), Body_str);
//        Body_str =callMethod.CreateJson("StackCategory", callMethod.ReadString("StackCategory"), Body_str);
//        Body_str =callMethod.CreateJson("Sender", callMethod.ReadString("Deliverer"), Body_str);
//
//        Call<RetrofitResponse> call = apiInterface.OcrPrintControler(callMethod.RetrofitBody(Body_str));
//
//        call.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
//                if (response.isSuccessful()) {
//                    ((Activity) mContext).finish();
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
//                ((Activity) mContext).finish();
//            }
//        });
//
//    }

    public void GoodStackLocation(Ocr_Good ocr_good) {


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.ocr_stacklocation);


        Button explain_btn = dialog.findViewById(R.id.ocr_stacklocation_explain_btn);
        final TextView goodname_tv = dialog.findViewById(R.id.ocr_stacklocation_goodname_tv);
        final EditText stacklocation_et = dialog.findViewById(R.id.ocr_stacklocation_explain_et);


        goodname_tv.setText(ocr_good.getGoodName());
        stacklocation_et.setText(ocr_good.getStackLocation());
        stacklocation_et.selectAll();



        dialog.show();
        stacklocation_et.requestFocus();
        stacklocation_et.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(stacklocation_et, InputMethodManager.SHOW_IMPLICIT);
        }, 500);




        explain_btn.setOnClickListener(view -> {
            String safeInput = stacklocation_et.getText().toString().replaceAll("[;'\"--#/*]", "");

            dialogProg();
            tv_rep.setText("در حال ارسال اطلاعات");
            Call<RetrofitResponse> call = apiInterface.SetStackLocation(
                    "SetStackLocation_new",
                    ocr_good.getGoodCode(),
                    NumberFunctions.EnglishNumber(safeInput)
            );

            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        dialog.dismiss();
                        dialogProg.dismiss();
                        callMethod.showToast("ثبت گردید");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                    dialog.dismiss();
                    dialogProg.dismiss();
                    callMethod.showToast("ثبت نگردید");

                }
            });
        });

    }


    public void app_info() {

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String tmonthOfYear, tdayOfMonth;
        tmonthOfYear = "0" + (monthOfYear + 1);
        tdayOfMonth = "0" + dayOfMonth;

        date = year + "/"
                + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                + tdayOfMonth.substring(tdayOfMonth.length() - 2);

        ed_pack_h_date.setText(NumberFunctions.PerisanNumber(date));
    }
}
