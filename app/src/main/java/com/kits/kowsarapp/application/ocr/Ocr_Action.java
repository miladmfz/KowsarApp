package com.kits.kowsarapp.application.ocr;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kits.kowsarapp.activity.ocr.Ocr_Check_Confirm_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_Collect_Confirm_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_ConfigActivity;
import com.kits.kowsarapp.activity.ocr.Ocr_FactorListLocalActivity;
import com.kits.kowsarapp.activity.ocr.Ocr_StackEnumeration_Factor_Check_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_StackEnumeration_Janamaie_Check_Activity;
import com.kits.kowsarapp.adapter.ocr.Ocr_GoodScan_Adapter;
import com.kits.kowsarapp.application.base.CallMethod;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.NetworkUtils;
import com.kits.kowsarapp.fragment.ocr.OnGoodConfirmListener;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.Job;
import com.kits.kowsarapp.model.base.JobPerson;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.model.ocr.Ocr_StackEnumeration;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;


public class Ocr_Action extends Activity implements DatePickerDialog.OnDateSetListener {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");

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
    String Conter= "";
    String CountStep= "";
    String inventory_isFinished= "";
    ArrayList<Job> jobs;
    String date = "";
    String LocationStackCode = "0";
    TextView ed_pack_h_date;
    Dialog dialog, dialogProg;
    TextView tv_rep;
    Ocr_Print print;
    Handler handler = new Handler();
    ArrayList<Ocr_Good> Empty_goods = new ArrayList<>();

    public Ocr_Action(Context mcontxt) {
        this.mContext = mcontxt;
        callMethod = new CallMethod(mContext);
        ocr_dbh = new Ocr_DBH(mContext, callMethod.ReadString("DatabaseName"));

        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);

        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

        dialog = new Dialog(mContext);
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


callMethod.Log("=="+factor.getFactorPrivateCode());

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
        TextView tv_appfactorexplain = dialog.findViewById(R.id.ocr_factordialog_d_appfactorexplain);
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


        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
            tv_Ersall.setText(NumberFunctions.PerisanNumber(factor.getErsall()));
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
            tv_Ersall.setText(NumberFunctions.PerisanNumber(factor.getMandehBedehkar()));
        }else{
            tv_Ersall.setText(NumberFunctions.PerisanNumber(factor.getErsall()));
        }


        if (factor.getBrokerName().length() > 20)
            tv_BrokerName.setText(NumberFunctions.PerisanNumber(factor.getBrokerName().substring(0, 20) + "..."));
        else
            tv_BrokerName.setText(NumberFunctions.PerisanNumber(factor.getBrokerName()));

        tv_AppFactorState.setText(NumberFunctions.PerisanNumber(factor.getAppFactorState()));

        tv_appfactorexplain.setText(NumberFunctions.PerisanNumber(factor.getAppOCRFactorExplain()));


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
                    }

                }
            });


        });

        btn_2.setOnClickListener(v -> {
            Pack_detail(factor,"1");
            dialog.dismiss();
        });

        dialog.show();


    }


    public void Pack_detail(Factor factor,String detail_flag) {

        if (detail_flag.equals("0")){

            dialogProg();

            Call<RetrofitResponse> call2;
            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call2=apiInterface.SetPackDetail(
                        "SetPackDetail",
                        factor.getAppOCRFactorCode(),
                        "",
                        callMethod.ReadString("Deliverer"),
                        "",
                        "",
                        "0",
                        factor.getAppOCRFactorExplain()


                );
            }else{
                call2=secendApiInterface.SetPackDetail(
                        "SetPackDetail",
                        factor.getAppOCRFactorCode(),
                        "",
                        callMethod.ReadString("Deliverer"),
                        "",
                        "",
                        "0",
                        factor.getAppOCRFactorExplain()

                );
            }

            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    dialogProg.dismiss();

                    if (!callMethod.ReadString("Category").equals("5")) {
                        print.Printing(factor,Empty_goods,"1","0");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
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
                    }
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
            EditText ed_pack_h_ocrexplain = dialog.findViewById(R.id.ocr_packdetail_b_ocrexplain);

            ed_pack_h_date = dialog.findViewById(R.id.ocr_packdetail_b_senddate);

            PersianCalendar persianCalendar = new PersianCalendar();
            String tmonthOfYear, tdayOfMonth;
            tmonthOfYear = "0" + (persianCalendar.getPersianMonth() + 1);
            tdayOfMonth = "0" + persianCalendar.getPersianDay();
            date = persianCalendar.getPersianYear() + "/"
                    + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                    + tdayOfMonth.substring(tdayOfMonth.length() - 2);

            ed_pack_h_date.setText(NumberFunctions.PerisanNumber(date));
            ed_pack_h_ocrexplain.setText(factor.getAppOCRFactorExplain());

            LinearLayoutCompat ll_pack_h_main = dialog.findViewById(R.id.ocr_packdetail_b_linejob);
            sendtime=NumberFunctions.PerisanNumber(date);
            Call<RetrofitResponse> call;
            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call=apiInterface.GetJob("GetJob", "Ocr3");
            }else{
                call=secendApiInterface.GetJob("GetJob", "Ocr3");
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
                                call1=apiInterface.GetJobPerson("GetJobPerson", job.getTitle());
                            }else{
                                call1=secendApiInterface.GetJobPerson("GetJobPerson", job.getTitle());
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
                                    }
                                }
                            });
                            ll_pack_h_main.addView(ll_new);
                        }


                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
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
                    }

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



                    if (!job.getText().equals("برای انتخاب کلیک کنید")) {


                        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
                            if (job.getJobCode().equals("1")) {
                                coltrol_s = job.getText();
                            }
                            if (job.getJobCode().equals("2")) {
                                reader_s = job.getText();
                            }
                            if (job.getJobCode().equals("3")) {
                                pack_s = job.getText();
                            }
                        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
                            if (job.getJobCode().equals("3")) {
                                coltrol_s = job.getText();
                            }
                            if (job.getJobCode().equals("4")) {
                                reader_s = job.getText();
                            }
                            if (job.getJobCode().equals("5")) {
                                pack_s = job.getText();
                            }
                        }else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrCheshme")){
                            if (job.getJobCode().equals("8")) {
                                coltrol_s = job.getText();
                            }
                            if (job.getJobCode().equals("7")) {
                                reader_s = job.getText();
                            }
                            if (job.getJobCode().equals("5")) {
                                pack_s = job.getText();
                            }
                        }else{
                            if (job.getJobCode().equals("1")) {
                                coltrol_s = job.getText();
                            }
                            if (job.getJobCode().equals("2")) {
                                reader_s = job.getText();
                            }
                            if (job.getJobCode().equals("3")) {
                                pack_s = job.getText();
                            }
                        }





                    } else {
                        falt = true;
                        falt_message = job.getTitle();
                        break;
                    }
                }
                //






                if(callMethod.ReadString("Category").equals("5")) {



                    Call<RetrofitResponse> call2;
                    if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                        call2 = apiInterface.SetPackDetail(
                                "SetPackDetail",
                                factor.getAppOCRFactorCode(),
                                reader_s,
                                coltrol_s,
                                pack_s,
                                NumberFunctions.EnglishNumber(date),
                                packCount,
                                NumberFunctions.EnglishNumber(ed_pack_h_ocrexplain.getText().toString())
                        );
                    } else {
                        call2 = secendApiInterface.SetPackDetail(
                                "SetPackDetail",
                                factor.getAppOCRFactorCode(),
                                reader_s,
                                coltrol_s,
                                pack_s,
                                NumberFunctions.EnglishNumber(date),
                                packCount,
                                NumberFunctions.EnglishNumber(ed_pack_h_ocrexplain.getText().toString())
                        );
                    }
                    call2.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                            dialog.dismiss();
                            callMethod.Log("جزئیات فاکتور ثبت گردید");
                            if (!callMethod.ReadString("Category").equals("5")) {
                                print.Printing(factor, Empty_goods, packCount, "0");

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
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
                            }
                            callMethod.Log("جزئیات فاکتور ثبت نگردید");

                        }
                    });




                }else{



                if (!falt) {
                    //dialogProg();

                    Call<RetrofitResponse> call3;

                    if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                        call3 = apiInterface.OcrControlled(
                                "OcrControlled",
                                factor.getAppOCRFactorCode(),
                                "3",
                                callMethod.ReadString("JobPersonRef")
                        );
                    } else {
                        call3 = secendApiInterface.OcrControlled(
                                "OcrControlled",
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
                            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                                call2 = apiInterface.SetPackDetail(
                                        "SetPackDetail",
                                        factor.getAppOCRFactorCode(),
                                        reader_s,
                                        coltrol_s,
                                        pack_s,
                                        NumberFunctions.EnglishNumber(date),
                                        packCount,
                                        NumberFunctions.EnglishNumber(ed_pack_h_ocrexplain.getText().toString())
                                );
                            } else {
                                call2 = secendApiInterface.SetPackDetail(
                                        "SetPackDetail",
                                        factor.getAppOCRFactorCode(),
                                        reader_s,
                                        coltrol_s,
                                        pack_s,
                                        NumberFunctions.EnglishNumber(date),
                                        packCount,
                                        NumberFunctions.EnglishNumber(ed_pack_h_ocrexplain.getText().toString())
                                );
                            }
                            dialogProg();
                            call2.enqueue(new Callback<RetrofitResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                    dialog.dismiss();
//                                if (!callMethod.ReadString("Category").equals("5")) {
//                                    OcrPrintPacker(factor);
//                                }
                                    if (!callMethod.ReadString("Category").equals("5")) {
                                        print.Printing(factor, Empty_goods, packCount, "0");
                                        callMethod.Log("جزئیات فاکتور ثبت گردید");
                                        dialogProg_dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
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
                                    }
                                    dialogProg_dismiss();

                                }
                            });
                        }

                        @Override
                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
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
                            }
                            dialogProg_dismiss();
                        }
                    });


                } else {
                    dialogProg_dismiss();

                    callMethod.showToast(falt_message + " را تکمیل کنید");
                }
            }

            });


            dialog.show();




        }


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


    @SuppressLint("ClickableViewAccessibility")
    public void StackEnum_good(Ocr_Good singleGood, String StackEnumerationCode, String LocationCode) {

        LocationStackCode="0";
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.ocr_stackenum_good_box);



        ImageView iv_good = dialog.findViewById(R.id.ocr_stackenum_good_b_img);

        TextView tv_good_1 = dialog.findViewById(R.id.ocr_stackenum_good_b_tv1);
        TextView tv_good_2 = dialog.findViewById(R.id.ocr_stackenum_good_b_tv2);
        TextView lb_good_1 = dialog.findViewById(R.id.ocr_stackenum_good_b_lb1);

        LinearLayoutCompat ll_good_1= dialog.findViewById(R.id.ocr_stackenum_good_b_ll_lb1);



        tv_good_1.setText(singleGood.getGoodName());
        tv_good_2.setText(singleGood.getMaxSellPrice());



        TextView tv_Firstenum = dialog.findViewById(R.id.ocr_stackenum_good_b_firstnum);
        EditText ed_auxn11 = dialog.findViewById(R.id.ocr_stackenum_good_b_auxn11);
        EditText ed_auxn12 = dialog.findViewById(R.id.ocr_stackenum_good_b_auxn12);
        EditText ed_auxn13 = dialog.findViewById(R.id.ocr_stackenum_good_b_auxn13);

        ed_auxn11.setOnClickListener(v -> ed_auxn11.selectAll());
        ed_auxn12.setOnClickListener(v -> ed_auxn12.selectAll());
        ed_auxn13.setOnClickListener(v -> ed_auxn13.selectAll());



        MaterialButton btn_confirm = dialog.findViewById(R.id.ocr_stackenum_good_b_btn_confirm);
        MaterialButton btn_cansel = dialog.findViewById(R.id.ocr_stackenum_good_b_btn_cancel);

        byte[] BaseImageByte;
        BaseImageByte = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
        iv_good.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length), BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getWidth() * 2, BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getHeight() * 2, false));
        //iv_good.setOnTouchListener(new ZoomHelper());

        Call<RetrofitResponse> call2;
        call2=apiInterface.GetImage("getImage", singleGood.getGoodCode(), 0, 300);

//        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//            call2=apiInterface.GetImage("getImage", singleGood.getGoodRef(), 0, 300);
//        }else{
//            call2=secendApiInterface.GetImage("getImage", singleGood.getGoodCode(), 0, 300);
//        }

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

                try {

                } catch (Exception e) {
                    callMethod.Log("Network check error: " + e.getMessage());
                    callMethod.showToast("خطا در بررسی وضعیت شبکه");
                }
            }
        });


        Call<RetrofitResponse> call1;
        call1=apiInterface.GetEnum_Rows(
                "GetEnum_Rows",
                StackEnumerationCode,
                LocationCode,
                singleGood.getGoodCode()

        );


        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                if(response.isSuccessful()) {

                    assert response.body() != null;

                    if (response.body().getOcr_Goods().size() > 0){
                        LocationStackCode=response.body().getOcr_Goods().get(0).getLocationStackCode();
                        tv_Firstenum.setText(NumberFunctions.PerisanNumber(response.body().getOcr_Goods().get(0).getNum1()));
                        ed_auxn11.setText(NumberFunctions.PerisanNumber(response.body().getOcr_Goods().get(0).getAuxn11()));
                        ed_auxn12.setText(NumberFunctions.PerisanNumber(response.body().getOcr_Goods().get(0).getAuxn12()));
                        ed_auxn13.setText(NumberFunctions.PerisanNumber(response.body().getOcr_Goods().get(0).getAuxn13()));


                        if(Integer.parseInt(response.body().getOcr_Goods().get(0).getAuxn11())>0){
                            if(Integer.parseInt(response.body().getOcr_Goods().get(0).getAuxn12())>0){
                                if(Integer.parseInt(response.body().getOcr_Goods().get(0).getAuxn13())>0) {
                                    ed_auxn13.requestFocus();

                                }else {


                                    ed_auxn13.post(() -> {
                                        ed_auxn13.setFocusable(true);
                                        ed_auxn13.requestFocus();
                                        ed_auxn13.selectAll();


                                        InputMethodManager imm = (InputMethodManager)
                                                mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if (imm != null) {
                                            imm.showSoftInput(ed_auxn13, InputMethodManager.SHOW_IMPLICIT);
                                        }
                                    });


                                }
                            }else {



                                ed_auxn12.post(() -> {
                                    ed_auxn12.setFocusable(true);
                                    ed_auxn12.requestFocus();
                                    ed_auxn12.selectAll();


                                    InputMethodManager imm = (InputMethodManager)
                                            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (imm != null) {
                                        imm.showSoftInput(ed_auxn12, InputMethodManager.SHOW_IMPLICIT);
                                    }
                                });
                            }
                        }else {


                            ed_auxn11.post(() -> {
                                ed_auxn11.setFocusable(true);
                                ed_auxn11.requestFocus();
                                ed_auxn11.selectAll();


                                InputMethodManager imm = (InputMethodManager)
                                        mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.showSoftInput(ed_auxn11, InputMethodManager.SHOW_IMPLICIT);
                                }
                            });

                        }



                    }else{
                        LocationStackCode="0";
                        tv_Firstenum.setText(NumberFunctions.PerisanNumber("0"));
                        ed_auxn11.setText(NumberFunctions.PerisanNumber("0"));
                        ed_auxn12.setText(NumberFunctions.PerisanNumber("0"));
                        ed_auxn13.setText(NumberFunctions.PerisanNumber("0"));



                        ed_auxn11.post(() -> {
                            ed_auxn11.setFocusable(true);
                            ed_auxn11.requestFocus();
                            ed_auxn11.selectAll();


                            InputMethodManager imm = (InputMethodManager)
                                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.showSoftInput(ed_auxn11, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });

                    }


                }
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                finish();
                callMethod.showToast("جانمایی موجود نمی باشد");
            }
        });





//        tv_good_1.setText(response.body().getOcr_Goods().get(0).getGoodName());
//        tv_Firstenum.setText(singleGood.getFirstNumeration());
//
//        ed_auxn11.setText(singleGood.getAuxn11());
//        ed_auxn12.setText(singleGood.getAuxn12());
//        ed_auxn13.setText(singleGood.getAuxn13());



        ed_auxn11.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }
                    @Override
                    public void afterTextChanged( Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {
                            try {
                             if (Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn11.getText().toString())) < 0) {
                                 ed_auxn11.setText(NumberFunctions.PerisanNumber("0"));
                             }else{

                                 int int_auxn11;
                                 int int_auxn12;
                                 int int_auxn13;

                                 try{
                                     int_auxn11=Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn11.getText().toString()));
                                 }catch (Exception e){
                                     int_auxn11=0;
                                 }
                                 try{
                                     int_auxn12=Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn12.getText().toString()));
                                 }catch (Exception e){
                                     int_auxn12=0;
                                 }
                                 try{
                                     int_auxn13=Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn13.getText().toString()));
                                 }catch (Exception e){
                                     int_auxn13=0;
                                 }

                                 tv_Firstenum.setText(NumberFunctions.PerisanNumber((int_auxn11+int_auxn12+int_auxn13)+""));
                             }


                        } catch (Exception e) {
                                ed_auxn11.setText(NumberFunctions.PerisanNumber("0"));
                        }
                        }, 1000);

                    }
                });

        ed_auxn12.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }
                    @Override
                    public void afterTextChanged( Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {

                                try {
                                    if (Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn12.getText().toString())) < 0) {
                                        ed_auxn12.setText(NumberFunctions.PerisanNumber("0"));
                                    }else{
                                        int int_auxn11;
                                        int int_auxn12;
                                        int int_auxn13;

                                        try{
                                            int_auxn11=Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn11.getText().toString()));
                                        }catch (Exception e){
                                            int_auxn11=0;
                                        }
                                        try{
                                            int_auxn12=Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn12.getText().toString()));
                                        }catch (Exception e){
                                            int_auxn12=0;
                                        }
                                        try{
                                            int_auxn13=Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn13.getText().toString()));
                                        }catch (Exception e){
                                            int_auxn13=0;
                                        }

                                        tv_Firstenum.setText(NumberFunctions.PerisanNumber((int_auxn11+int_auxn12+int_auxn13)+""));
                                    }

                                } catch (Exception e) {
                                    ed_auxn12.setText(NumberFunctions.PerisanNumber("0"));
                                }
                        }, 1000);

                    }
                });

        ed_auxn13.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }
                    @Override
                    public void afterTextChanged( Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {

                                try {
                                    if (Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn13.getText().toString())) < 0) {
                                        ed_auxn13.setText(NumberFunctions.PerisanNumber("0"));
                                    }else{
                                        int int_auxn11;
                                        int int_auxn12;
                                        int int_auxn13;

                                        try{
                                            int_auxn11=Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn11.getText().toString()));
                                        }catch (Exception e){
                                            int_auxn11=0;
                                        }
                                        try{
                                            int_auxn12=Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn12.getText().toString()));
                                        }catch (Exception e){
                                            int_auxn12=0;
                                        }
                                        try{
                                            int_auxn13=Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn13.getText().toString()));
                                        }catch (Exception e){
                                            int_auxn13=0;
                                        }

                                        tv_Firstenum.setText(NumberFunctions.PerisanNumber((int_auxn11+int_auxn12+int_auxn13)+""));
                                    }

                                } catch (Exception e) {
                                    ed_auxn13.setText(NumberFunctions.PerisanNumber("0"));
                                }
                        }, 1000);

                    }
                });


        btn_confirm.setOnClickListener(v -> {


            Call<RetrofitResponse> call3;
            call3=apiInterface.GetEnum_SetRow(
                    "GetEnum_SetRow",
                    StackEnumerationCode,
                    LocationCode,
                    LocationStackCode,
                    singleGood.getGoodCode(),
                    callMethod.ReadString("JobPersonRef"),
                    NumberFunctions.EnglishNumber(ed_auxn11.getText().toString()),
                    NumberFunctions.EnglishNumber(ed_auxn12.getText().toString()),
                    NumberFunctions.EnglishNumber(ed_auxn13.getText().toString())


            );


            call3.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    if(response.isSuccessful()) {

                        assert response.body() != null;
                        dialog.dismiss();

                    }
                }
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {


                    callMethod.showToast("انجام نشد عدم برقراری ارتباط");
                }
            });




        });


        btn_cansel.setOnClickListener(v -> {

            dialog.dismiss(); // بستن پنجره
        });

          dialog.show();

          // TODO sssssssssss





//        Call<RetrofitResponse> call;
//        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//            call=apiInterface.GetOcrGoodDetail("GetOcrGoodDetail", GoodCode);
//        }else{
//            call=secendApiInterface.GetOcrGoodDetail("GetOcrGoodDetail", GoodCode);
//        }

//
//        callMethod.Log(singleGood.getGoodCode());
//        call.enqueue(new Callback<RetrofitResponse>() {
//
//            @Override
//            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
//                if (response.isSuccessful()) {
//                    assert response.body() != null;
//                    ArrayList<Ocr_Good> ocr_goods = response.body().getOcr_Goods();
//
//
//                    if (!callMethod.ReadBoolan("ShowDetailAmount")){
//                        ll_amonut.setVisibility(View.GONE);
//                    }
//
//                    if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
//                            callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
//
//                        if (callMethod.ReadString("FactorDbName").equals("PakhshQOQNOOS")){
//
//                            lb_good_1.setText("موجودی کل");
//                            lb_good_2.setText("قطع");
//                            lb_good_3.setText("نوع جلد");
//                            lb_good_4.setText("پشت جلد");
//                            lb_good_5.setText("شماره قفسه");
//
//
//                        }else if (callMethod.ReadString("FactorDbName").equals("Afarinegan")){
//
//                            lb_good_1.setText("موجودی کل");
//                            lb_good_2.setText("قطع");
//                            lb_good_3.setText("نوع جلد");
//                            lb_good_4.setText("پشت جلد");
//                            lb_good_5.setText("شماره قفسه");
//
//
//                        }
//
//
//
//                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
//                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
//                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getCoverType()));
//                        tv_good_4.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getPageNo()));
//                        tv_good_5.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getGoodExplain2()));
//                        ll_good_6.setVisibility(View.GONE);
//                        ll_good_7.setVisibility(View.GONE);
//
//
//                    } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
//
//                        lb_good_1.setText("نام");
//                        lb_good_2.setText("شماره قفسه");
//                        lb_good_3.setText("تعداد فاکتور");
//                        lb_good_4.setText("قیمت");
//                        lb_good_5.setText("موجودی کل");
//                        lb_good_6.setText("قطع-جلد");
//                        lb_good_7.setText("کد کالا سیستم");
//
//
//                        lb_good_1.setVisibility(View.GONE);
//
//                        tv_good_1.setText(NumberFunctions.PerisanNumber(singleGood.getGoodName()));
//                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFormNo()));
//                        tv_good_3.setText(NumberFunctions.PerisanNumber(singleGood.getFacAmount()));
//                        tv_good_4.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.valueOf(singleGood.getGoodMaxSellPrice()))));
//
//                        tv_good_5.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
//                        tv_good_6.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getCoverType() +" - "+ocr_goods.get(0).getSize()));
//                        tv_good_7.setText(NumberFunctions.PerisanNumber(singleGood.getGoodCode()));
//
//
//                        tv_good_3.setTextColor(ContextCompat.getColor(mContext, R.color.red_900));
//                        float currentSize = tv_good_3.getTextSize() / mContext.getResources().getDisplayMetrics().scaledDensity;
//
//                        tv_good_3.setTextSize(currentSize + 5);
//
//                        tv_good_3.setTypeface(tv_good_3.getTypeface(), Typeface.BOLD);
//
//                        ll_good_3.setBackgroundResource(R.drawable.bg_round_green);
//
//
//
//                    }else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrMahris")){
//                        lb_good_1.setText("موجودی کل");
//                        lb_good_2.setText("قطع");
//                        lb_good_3.setText("نوع جلد");
//                        lb_good_4.setText("پشت جلد");
//                        lb_good_5.setText("شماره قفسه");
//                        lb_good_6.setText("نیاز فاکتور");
//                        lb_good_7.setText("کد کالا ");
//
//
//                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
//                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
//                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getCoverType()));
//                        tv_good_4.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getGoodMaxSellPrice()));
//                        tv_good_5.setText(NumberFunctions.PerisanNumber(singleGood.getGoodExplain3()));
//                        tv_good_6.setText(NumberFunctions.PerisanNumber(singleGood.getFacAmount()));
//
//                        tv_good_7.setText(NumberFunctions.PerisanNumber(singleGood.getGoodCode()));
//
//                    }else{
//                        ll_good_6.setVisibility(View.GONE);
//
//                        lb_good_1.setText("موجودی کل");
//                        lb_good_2.setText("قطع");
//                        lb_good_3.setText("نوع جلد");
//                        lb_good_4.setText("پشت جلد");
//                        lb_good_5.setText("شماره قفسه");
//
//                        tv_good_1.setText(ocr_goods.get(0).getTotalAvailable());
//                        tv_good_2.setText(ocr_goods.get(0).getSize());
//                        tv_good_3.setText(ocr_goods.get(0).getCoverType());
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
//                try {
//                    // 🟢 بررسی وضعیت اتصال
//                    if (!NetworkUtils.isNetworkAvailable(mContext)) {
//                        callMethod.showToast("اتصال اینترنت قطع است!");
//                    } else if (NetworkUtils.isVPNActive()) {
//                        callMethod.showToast("VPN فعال است، ممکن است ارتباط با سرور مختل شود!");
//                    } else {
//                        String serverUrl = callMethod.ReadString("ServerURLUse");
//                        if (serverUrl != null && !serverUrl.isEmpty() && !NetworkUtils.canReachServer(serverUrl)) {
//                            callMethod.showToast("سرور در دسترس نیست یا فیلتر شده است!");
//                        } else {
//                            callMethod.showToast("مشکل در برقراری ارتباط با سرور برای بارگیری عکس");
//                        }
//                    }
//                } catch (Exception e) {
//                    callMethod.Log("Network check error: " + e.getMessage());
//                    callMethod.showToast("خطا در بررسی وضعیت شبکه");
//                }
//            }
//        });





//        if (callMethod.ReadBoolan("ListOrSingle") || BarcodeScan.equals("")) {
//            btn_confirm.setVisibility(View.VISIBLE);
//            btn_cansel.setVisibility(View.VISIBLE);
////            btn_confirm.setVisibility(View.GONE);
////            btn_cansel.setVisibility(View.GONE);
//
//            if (callMethod.ReadBoolan("CheckListFromGoodDialog") ) {
//                btn_confirm.setVisibility(View.VISIBLE);
//                btn_cansel.setVisibility(View.VISIBLE);
//            }else{
//                btn_confirm.setVisibility(View.GONE);
//                btn_cansel.setVisibility(View.GONE);
//            }
//        }else{
//            btn_confirm.setVisibility(View.VISIBLE);
//        }




    }




    @SuppressLint("ClickableViewAccessibility")
    public void StackEnum_good_hint_moghayerat(Ocr_Good singleGood, String StackEnumerationCode, String LocationCode) {

        LocationStackCode="0";

            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.ocr_stackenum_good_box);



        ImageView iv_good = dialog.findViewById(R.id.ocr_stackenum_good_b_img);

        TextView tv_good_1 = dialog.findViewById(R.id.ocr_stackenum_good_b_tv1);
        TextView tv_good_2 = dialog.findViewById(R.id.ocr_stackenum_good_b_tv2);



        tv_good_1.setText(singleGood.getGoodName());
        tv_good_2.setText(singleGood.getGoodCode());



        TextView tv_Firstenum = dialog.findViewById(R.id.ocr_stackenum_good_b_firstnum);
        EditText ed_auxn11 = dialog.findViewById(R.id.ocr_stackenum_good_b_auxn11);
        EditText ed_auxn12 = dialog.findViewById(R.id.ocr_stackenum_good_b_auxn12);
        EditText ed_auxn13= dialog.findViewById(R.id.ocr_stackenum_good_b_auxn13);

        ed_auxn11.setOnClickListener(v -> ed_auxn11.selectAll());
        ed_auxn12.setVisibility(View.GONE);
        ed_auxn13.setVisibility(View.GONE);
        tv_Firstenum.setVisibility(View.GONE);


        MaterialButton btn_confirm = dialog.findViewById(R.id.ocr_stackenum_good_b_btn_confirm);
        MaterialButton btn_cansel = dialog.findViewById(R.id.ocr_stackenum_good_b_btn_cancel);

        byte[] BaseImageByte;
        BaseImageByte = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
        iv_good.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length), BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getWidth() * 2, BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getHeight() * 2, false));
        //iv_good.setOnTouchListener(new ZoomHelper());

        callMethod.Log("");
        Call<RetrofitResponse> call2;
        call2=apiInterface.GetImage("getImage", singleGood.getGoodCode(), 0, 300);
//        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//
//        }else{
//            call2=secendApiInterface.GetImage("getImage", singleGood.getGoodCode(), 0, 300);
//        }

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

                try {

                } catch (Exception e) {
                    callMethod.Log("Network check error: " + e.getMessage());
                    callMethod.showToast("خطا در بررسی وضعیت شبکه");
                }
            }
        });


        Call<RetrofitResponse> call1;
        call1=apiInterface.GetEnum_Rows(
                "GetEnum_Rows",
                StackEnumerationCode,
                LocationCode,
                singleGood.getGoodCode()

        );


        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                if(response.isSuccessful()) {

                    assert response.body() != null;

                    if (response.body().getOcr_Goods().size() > 0){
                        LocationStackCode=response.body().getOcr_Goods().get(0).getLocationStackCode();
                        tv_Firstenum.setText(NumberFunctions.PerisanNumber(response.body().getOcr_Goods().get(0).getNum1()));
                        ed_auxn11.setText(NumberFunctions.PerisanNumber(response.body().getOcr_Goods().get(0).getAuxn11()));

                        ed_auxn11.post(() -> {
                            ed_auxn11.setFocusable(true);
                            ed_auxn11.requestFocus();
                            ed_auxn11.selectAll();


                            InputMethodManager imm = (InputMethodManager)
                                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.showSoftInput(ed_auxn11, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });


                    }else{
                        LocationStackCode="0";
                        tv_Firstenum.setText(NumberFunctions.PerisanNumber("0"));
                        ed_auxn11.setText(NumberFunctions.PerisanNumber("0"));



                        ed_auxn11.post(() -> {
                            ed_auxn11.setFocusable(true);
                            ed_auxn11.requestFocus();
                            ed_auxn11.selectAll();


                            InputMethodManager imm = (InputMethodManager)
                                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.showSoftInput(ed_auxn11, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });

                    }


                }
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                finish();
                callMethod.showToast("جانمایی موجود نمی باشد");
            }
        });





        ed_auxn11.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }
                    @Override
                    public void afterTextChanged( Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {
                            try {
                             if (Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn11.getText().toString())) < 0) {
                                 ed_auxn11.setText(NumberFunctions.PerisanNumber("0"));
                             }else{

                                 int int_auxn11;

                                 try{
                                     int_auxn11=Integer.parseInt(NumberFunctions.EnglishNumber(ed_auxn11.getText().toString()));
                                 }catch (Exception e){
                                     int_auxn11=0;
                                 }


                                 tv_Firstenum.setText(NumberFunctions.PerisanNumber((int_auxn11)+""));
                             }


                        } catch (Exception e) {
                                ed_auxn11.setText(NumberFunctions.PerisanNumber("0"));
                        }
                        }, 1000);

                    }
                });



        btn_confirm.setOnClickListener(v -> {

            double edtAmount =
                    Double.parseDouble(
                            NumberFunctions.EnglishNumber(
                                    ed_auxn11.getText().toString()
                            )
                    );

            double goodAmount =
                    Double.parseDouble(
                            singleGood.getAmount()
                    );

            if (edtAmount == goodAmount) {


                Call<RetrofitResponse> call3;
                call3=apiInterface.GetEnum_SetRow(
                        "GetEnum_SetRow",
                        StackEnumerationCode,
                        LocationCode,
                        LocationStackCode,
                        singleGood.getGoodCode(),
                        callMethod.ReadString("JobPersonRef"),
                        NumberFunctions.EnglishNumber(ed_auxn11.getText().toString()),
                        "0",
                        "0"


                );


                call3.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                        if(response.isSuccessful()) {

                            assert response.body() != null;
                            dialog.dismiss();

                        }
                    }
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {


                        callMethod.showToast("انجام نشد عدم برقراری ارتباط");
                    }
                });


            }else
            {

                final Dialog dialog1 = new Dialog(mContext);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog1.setContentView(R.layout.ocr_enum_moghayerat);

                EditText moghayerat = dialog1.findViewById(R.id.ocr_enum_moghayerat_b_et);

                Button btnmoghayerat = dialog1.findViewById(R.id.ocr_enum_moghayerat_b_btn);
                dialog1.show();

                btnmoghayerat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (moghayerat.getText().toString().length()>0){


                                Call<RetrofitResponse> call3;
                                call3=apiInterface.GetEnum_SetRow(
                                        "GetEnum_SetRow",
                                        StackEnumerationCode,
                                        LocationCode,
                                        LocationStackCode,
                                        singleGood.getGoodCode(),
                                        callMethod.ReadString("JobPersonRef"),
                                        NumberFunctions.EnglishNumber(ed_auxn11.getText().toString()),
                                        "0",
                                        "0"


                                );


                                call3.enqueue(new Callback<RetrofitResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                                        if(response.isSuccessful()) {

                                            assert response.body() != null;
                                            dialog1.dismiss();
                                            dialog.dismiss();
                                        }
                                    }
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {


                                        callMethod.showToast("انجام نشد عدم برقراری ارتباط");
                                    }
                                });


                        }else{
                            callMethod.showToast("شمارش را وارد کنید");
                        }

                    }
                });


            }



        });


        btn_cansel.setOnClickListener(v -> {

            dialog.dismiss(); // بستن پنجره
        });

          dialog.show();

          // TODO sssssssssss





//        Call<RetrofitResponse> call;
//        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//            call=apiInterface.GetOcrGoodDetail("GetOcrGoodDetail", GoodCode);
//        }else{
//            call=secendApiInterface.GetOcrGoodDetail("GetOcrGoodDetail", GoodCode);
//        }

//
//        callMethod.Log(singleGood.getGoodCode());
//        call.enqueue(new Callback<RetrofitResponse>() {
//
//            @Override
//            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
//                if (response.isSuccessful()) {
//                    assert response.body() != null;
//                    ArrayList<Ocr_Good> ocr_goods = response.body().getOcr_Goods();
//
//
//                    if (!callMethod.ReadBoolan("ShowDetailAmount")){
//                        ll_amonut.setVisibility(View.GONE);
//                    }
//
//                    if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
//                            callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
//
//                        if (callMethod.ReadString("FactorDbName").equals("PakhshQOQNOOS")){
//
//                            lb_good_1.setText("موجودی کل");
//                            lb_good_2.setText("قطع");
//                            lb_good_3.setText("نوع جلد");
//                            lb_good_4.setText("پشت جلد");
//                            lb_good_5.setText("شماره قفسه");
//
//
//                        }else if (callMethod.ReadString("FactorDbName").equals("Afarinegan")){
//
//                            lb_good_1.setText("موجودی کل");
//                            lb_good_2.setText("قطع");
//                            lb_good_3.setText("نوع جلد");
//                            lb_good_4.setText("پشت جلد");
//                            lb_good_5.setText("شماره قفسه");
//
//
//                        }
//
//
//
//                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
//                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
//                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getCoverType()));
//                        tv_good_4.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getPageNo()));
//                        tv_good_5.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getGoodExplain2()));
//                        ll_good_6.setVisibility(View.GONE);
//                        ll_good_7.setVisibility(View.GONE);
//
//
//                    } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
//
//                        lb_good_1.setText("نام");
//                        lb_good_2.setText("شماره قفسه");
//                        lb_good_3.setText("تعداد فاکتور");
//                        lb_good_4.setText("قیمت");
//                        lb_good_5.setText("موجودی کل");
//                        lb_good_6.setText("قطع-جلد");
//                        lb_good_7.setText("کد کالا سیستم");
//
//
//                        lb_good_1.setVisibility(View.GONE);
//
//                        tv_good_1.setText(NumberFunctions.PerisanNumber(singleGood.getGoodName()));
//                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFormNo()));
//                        tv_good_3.setText(NumberFunctions.PerisanNumber(singleGood.getFacAmount()));
//                        tv_good_4.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.valueOf(singleGood.getGoodMaxSellPrice()))));
//
//                        tv_good_5.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
//                        tv_good_6.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getCoverType() +" - "+ocr_goods.get(0).getSize()));
//                        tv_good_7.setText(NumberFunctions.PerisanNumber(singleGood.getGoodCode()));
//
//
//                        tv_good_3.setTextColor(ContextCompat.getColor(mContext, R.color.red_900));
//                        float currentSize = tv_good_3.getTextSize() / mContext.getResources().getDisplayMetrics().scaledDensity;
//
//                        tv_good_3.setTextSize(currentSize + 5);
//
//                        tv_good_3.setTypeface(tv_good_3.getTypeface(), Typeface.BOLD);
//
//                        ll_good_3.setBackgroundResource(R.drawable.bg_round_green);
//
//
//
//                    }else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrMahris")){
//                        lb_good_1.setText("موجودی کل");
//                        lb_good_2.setText("قطع");
//                        lb_good_3.setText("نوع جلد");
//                        lb_good_4.setText("پشت جلد");
//                        lb_good_5.setText("شماره قفسه");
//                        lb_good_6.setText("نیاز فاکتور");
//                        lb_good_7.setText("کد کالا ");
//
//
//                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
//                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
//                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getCoverType()));
//                        tv_good_4.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getGoodMaxSellPrice()));
//                        tv_good_5.setText(NumberFunctions.PerisanNumber(singleGood.getGoodExplain3()));
//                        tv_good_6.setText(NumberFunctions.PerisanNumber(singleGood.getFacAmount()));
//
//                        tv_good_7.setText(NumberFunctions.PerisanNumber(singleGood.getGoodCode()));
//
//                    }else{
//                        ll_good_6.setVisibility(View.GONE);
//
//                        lb_good_1.setText("موجودی کل");
//                        lb_good_2.setText("قطع");
//                        lb_good_3.setText("نوع جلد");
//                        lb_good_4.setText("پشت جلد");
//                        lb_good_5.setText("شماره قفسه");
//
//                        tv_good_1.setText(ocr_goods.get(0).getTotalAvailable());
//                        tv_good_2.setText(ocr_goods.get(0).getSize());
//                        tv_good_3.setText(ocr_goods.get(0).getCoverType());
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
//                try {
//                    // 🟢 بررسی وضعیت اتصال
//                    if (!NetworkUtils.isNetworkAvailable(mContext)) {
//                        callMethod.showToast("اتصال اینترنت قطع است!");
//                    } else if (NetworkUtils.isVPNActive()) {
//                        callMethod.showToast("VPN فعال است، ممکن است ارتباط با سرور مختل شود!");
//                    } else {
//                        String serverUrl = callMethod.ReadString("ServerURLUse");
//                        if (serverUrl != null && !serverUrl.isEmpty() && !NetworkUtils.canReachServer(serverUrl)) {
//                            callMethod.showToast("سرور در دسترس نیست یا فیلتر شده است!");
//                        } else {
//                            callMethod.showToast("مشکل در برقراری ارتباط با سرور برای بارگیری عکس");
//                        }
//                    }
//                } catch (Exception e) {
//                    callMethod.Log("Network check error: " + e.getMessage());
//                    callMethod.showToast("خطا در بررسی وضعیت شبکه");
//                }
//            }
//        });





//        if (callMethod.ReadBoolan("ListOrSingle") || BarcodeScan.equals("")) {
//            btn_confirm.setVisibility(View.VISIBLE);
//            btn_cansel.setVisibility(View.VISIBLE);
////            btn_confirm.setVisibility(View.GONE);
////            btn_cansel.setVisibility(View.GONE);
//
//            if (callMethod.ReadBoolan("CheckListFromGoodDialog") ) {
//                btn_confirm.setVisibility(View.VISIBLE);
//                btn_cansel.setVisibility(View.VISIBLE);
//            }else{
//                btn_confirm.setVisibility(View.GONE);
//                btn_cansel.setVisibility(View.GONE);
//            }
//        }else{
//            btn_confirm.setVisibility(View.VISIBLE);
//        }




    }



    @SuppressLint("ClickableViewAccessibility")
    public void good_detail_StackEnumeration_Factor(Ocr_Good singleGood, String BarcodeScan, OnGoodConfirmListener listener) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.ocr_goodinventory_box);
        ImageView iv_good = dialog.findViewById(R.id.ocr_goodinventory_b_img);
        TextView tv_good_1 = dialog.findViewById(R.id.ocr_goodinventory_b_tv1);
        TextView tv_good_2 = dialog.findViewById(R.id.ocr_goodinventory_b_tv2);
        TextView tv_good_3 = dialog.findViewById(R.id.ocr_goodinventory_b_tv3);
        TextView tv_good_4 = dialog.findViewById(R.id.ocr_goodinventory_b_tv4);

        TextView tvcount = dialog.findViewById(R.id.ocr_goodinventory_b_tvcount);

        EditText ed_good_1 = dialog.findViewById(R.id.ocr_goodinventory_b_ed1);


        TextView lb_good_1 = dialog.findViewById(R.id.ocr_goodinventory_b_lb1);
        TextView lb_good_2 = dialog.findViewById(R.id.ocr_goodinventory_b_lb2);
        TextView lb_good_3 = dialog.findViewById(R.id.ocr_goodinventory_b_lb3);
        TextView lb_good_4 = dialog.findViewById(R.id.ocr_goodinventory_b_lb4);

        LinearLayoutCompat ll_good_1= dialog.findViewById(R.id.ocr_goodinventory_ll_lb1);
        LinearLayoutCompat ll_good_2= dialog.findViewById(R.id.ocr_goodinventory_ll_lb2);
        LinearLayoutCompat ll_good_3= dialog.findViewById(R.id.ocr_goodinventory_ll_lb3);
        LinearLayoutCompat ll_good_4= dialog.findViewById(R.id.ocr_goodinventory_ll_lb4);



        LinearLayoutCompat ll_amonut = dialog.findViewById(R.id.ocr_goodinventory_ll_lb1);

        MaterialButton btn_confirm = dialog.findViewById(R.id.ocr_goodinventory_b_btn_confirm);
        MaterialButton btn_cansel = dialog.findViewById(R.id.ocr_goodinventory_b_btn_cancel);



        Call<RetrofitResponse> call;

        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetGoodDetail("GetOcrGoodDetail", singleGood.getGoodCode());
        }else{
            call=secendApiInterface.GetGoodDetail("GetOcrGoodDetail", singleGood.getGoodCode());
        }

        callMethod.Log(singleGood.getGoodCode());
        call.enqueue(new Callback<RetrofitResponse>() {

            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Ocr_Good> ocr_goods = response.body().getOcr_Goods();

                    if (!callMethod.ReadBoolan("HintAmountInCount")){
                        ll_good_3.setVisibility(View.GONE);
                    }else{
                        ll_good_3.setVisibility(View.VISIBLE);

                    }

                    if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                            callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

                        if (callMethod.ReadString("FactorDbName").equals("PakhshQOQNOOS")){

                            lb_good_1.setText("موجودی کل");
                            lb_good_2.setText("قطع");
                            lb_good_3.setText("موجودی");
                            lb_good_4.setText("پشت جلد");


                        }else if (callMethod.ReadString("FactorDbName").equals("Afarinegan")){

                            lb_good_1.setText("موجودی کل");
                            lb_good_2.setText("قطع");
                            lb_good_3.setText("موجودی");
                            lb_good_4.setText("پشت جلد");


                        }



                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFacAmount()));
                        tv_good_4.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getPageNo()));



                    } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){

                        lb_good_1.setText("نام");
                        lb_good_2.setText("شماره قفسه");
                        lb_good_3.setText("موجودی");
                        lb_good_4.setText("قیمت");


                        lb_good_1.setVisibility(View.GONE);

                        tv_good_1.setText(NumberFunctions.PerisanNumber(singleGood.getGoodName()));
                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFormNo()));
                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFacAmount()));

                        tv_good_4.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.valueOf(singleGood.getGoodMaxSellPrice()))));




                    }else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrMahris")){
                        lb_good_1.setText("موجودی کل");
                        lb_good_2.setText("قطع");
                        lb_good_3.setText("موجودی");

                        lb_good_4.setText("پشت جلد");

                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFacAmount()));

                        tv_good_4.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getGoodMaxSellPrice()));

                    }else{

                        lb_good_1.setText("موجودی کل");
                        lb_good_2.setText("قطع");
                        lb_good_3.setText("موجودی");

                        lb_good_4.setText("پشت جلد");
                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFacAmount()));

                        tv_good_1.setText(ocr_goods.get(0).getTotalAvailable());
                        tv_good_2.setText(ocr_goods.get(0).getSize());
                    }

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
        byte[] BaseImageByte;
        BaseImageByte = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
        iv_good.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length), BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getWidth() * 2, BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getHeight() * 2, false));
        //iv_good.setOnTouchListener(new ZoomHelper());

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

                try {


                } catch (Exception e) {
                    callMethod.Log("Network check error: " + e.getMessage());
                    callMethod.showToast("خطا در بررسی وضعیت شبکه");
                }
            }
        });

        CountStep=callMethod.ReadString("CountStep");


        Conter="";



        // مرحله فعلی = اولین CountedAmount که null است
        if (singleGood.getCountedAmount1() == null) {
            Conter="1";
            tvcount.setText("شمارش 1");
        } else if (singleGood.getCountedAmount2() == null) {
            Conter="2";
            tvcount.setText("شمارش 2");

        } else if (singleGood.getCountedAmount3() == null) {
            Conter="3";
            tvcount.setText("شمارش 3");
        } else {
            Conter="4";
            tvcount.setText("اتمام شمارش آیتم");
            ed_good_1.setVisibility(View.GONE);
        }



        if (Integer.parseInt(Conter) == Integer.parseInt(CountStep)) {
            inventory_isFinished = "1"; // اتمام شمارش
        } else {
            inventory_isFinished = "0"; // هنوز ادامه دارد
        }

        btn_confirm.setOnClickListener(v -> {
            callMethod.Log("Conter= "+ Conter);
            if (Conter.equals("4")){
                callMethod.showToast("شمارش این آیتم تمام شده است");

            }else{
                String Counted = NumberFunctions.EnglishNumber(ed_good_1.getText().toString());


                if (NumberFunctions.EnglishNumber(ed_good_1.getText().toString()).equals(singleGood.getFacAmount())){

                    Call<RetrofitResponse> call1;
                    if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                        call1 = apiInterface.OcrCountInventory(
                                "OcrCountInventory",
                                singleGood.getAppOCRFactorRowCode(),
                                "0",
                                callMethod.ReadString("JobPersonRef"),
                                Counted,
                                Conter,
                                inventory_isFinished
                        );
                    } else {
                        call1 = apiInterface.OcrCountInventory(
                                "OcrCountInventory",
                                singleGood.getAppOCRFactorRowCode(),
                                "0",
                                callMethod.ReadString("JobPersonRef"),
                                Counted,
                                Conter,
                                inventory_isFinished
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
                                Intent intent = new Intent(mContext, Ocr_StackEnumeration_Factor_Check_Activity.class);
                                intent.putExtra("ScanResponse", BarcodeScan);
                                intent.putExtra("State", "0");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                                mContext.startActivity(intent);
                                ((Activity) mContext).finish();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<RetrofitResponse> call1, @NonNull Throwable t) {

                            try {

                            } catch (Exception e) {
                                callMethod.Log("Network check error: " + e.getMessage());
                                callMethod.showToast("خطا در بررسی وضعیت شبکه");
                            }
                        }
                    });


                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
                    builder.setTitle("مغایرت");
                    builder.setMessage("با تعداد در فاکتور مغایرت دارد مطمئن هستید ؟");

                    builder.setPositiveButton(R.string.textvalue_yes, (dialog1, which) -> {

                        Call<RetrofitResponse> call1;
                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                            call1 = apiInterface.OcrCountInventory(
                                    "OcrCountInventory",
                                    singleGood.getAppOCRFactorRowCode(),
                                    "0",
                                    callMethod.ReadString("JobPersonRef"),
                                    Counted,
                                    Conter,
                                    inventory_isFinished
                            );
                        } else {
                            call1 = apiInterface.OcrCountInventory(
                                    "OcrCountInventory",
                                    singleGood.getAppOCRFactorRowCode(),
                                    "0",
                                    callMethod.ReadString("JobPersonRef"),
                                    Counted,
                                    Conter,
                                    inventory_isFinished
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
                                    Intent intent = new Intent(mContext, Ocr_StackEnumeration_Factor_Check_Activity.class);
                                    intent.putExtra("ScanResponse", BarcodeScan);

                                    intent.putExtra("State", "0");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                                    mContext.startActivity(intent);
                                    ((Activity) mContext).finish();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call1, @NonNull Throwable t) {

                                try {

                                } catch (Exception e) {
                                    callMethod.Log("Network check error: " + e.getMessage());
                                    callMethod.showToast("خطا در بررسی وضعیت شبکه");
                                }
                            }
                        });

                    });

                    builder.setNegativeButton(R.string.textvalue_no, (dialog1, which) -> {
                        // code to handle negative button click
                    });

                    AlertDialog dialog1 = builder.create();
                    dialog1.show();



                }
            }




        });


        btn_cansel.setOnClickListener(v -> {

            if (listener != null) {
                listener.onGoodCanceled(singleGood);
            }
            dialog.dismiss(); // بستن پنجره
        });


        dialog.show();
    }


    @SuppressLint("ClickableViewAccessibility")
    public void good_detail_StackEnumeration_janamie(Ocr_StackEnumeration StackEnumeration) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.ocr_goodinventory_box);
        ImageView iv_good = dialog.findViewById(R.id.ocr_goodinventory_b_img);
        TextView tv_good_1 = dialog.findViewById(R.id.ocr_goodinventory_b_tv1);
        TextView tv_good_2 = dialog.findViewById(R.id.ocr_goodinventory_b_tv2);
        TextView tv_good_3 = dialog.findViewById(R.id.ocr_goodinventory_b_tv3);
        TextView tv_good_4 = dialog.findViewById(R.id.ocr_goodinventory_b_tv4);

        TextView tvcount = dialog.findViewById(R.id.ocr_goodinventory_b_tvcount);

        EditText ed_good_1 = dialog.findViewById(R.id.ocr_goodinventory_b_ed1);


        TextView lb_good_1 = dialog.findViewById(R.id.ocr_goodinventory_b_lb1);
        TextView lb_good_2 = dialog.findViewById(R.id.ocr_goodinventory_b_lb2);
        TextView lb_good_3 = dialog.findViewById(R.id.ocr_goodinventory_b_lb3);
        TextView lb_good_4 = dialog.findViewById(R.id.ocr_goodinventory_b_lb4);

        LinearLayoutCompat ll_good_1= dialog.findViewById(R.id.ocr_goodinventory_ll_lb1);
        LinearLayoutCompat ll_good_2= dialog.findViewById(R.id.ocr_goodinventory_ll_lb2);
        LinearLayoutCompat ll_good_3= dialog.findViewById(R.id.ocr_goodinventory_ll_lb3);
        LinearLayoutCompat ll_good_4= dialog.findViewById(R.id.ocr_goodinventory_ll_lb4);



        LinearLayoutCompat ll_amonut = dialog.findViewById(R.id.ocr_goodinventory_ll_lb1);

        MaterialButton btn_confirm = dialog.findViewById(R.id.ocr_goodinventory_b_btn_confirm);
        MaterialButton btn_cansel = dialog.findViewById(R.id.ocr_goodinventory_b_btn_cancel);



        Call<RetrofitResponse> call;
//        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//            call=apiInterface.GetOcrGoodDetail("GetOcrGoodDetail", GoodCode);
//        }else{
//            call=secendApiInterface.GetOcrGoodDetail("GetOcrGoodDetail", GoodCode);
//        }

        call=apiInterface.GetGoodDetail("GetOcrGoodDetail", StackEnumeration.getGoodCode());

        callMethod.Log(StackEnumeration.getGoodCode());
        call.enqueue(new Callback<RetrofitResponse>() {

            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Ocr_Good> ocr_goods = response.body().getOcr_Goods();




                    if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                            callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

//                        if (callMethod.ReadString("FactorDbName").equals("PakhshQOQNOOS")){
//
//                            lb_good_1.setText("نام");
//                            lb_good_2.setText("قطع");
//                            lb_good_3.setText("پشت جلد");
//
//
//                        }else if (callMethod.ReadString("FactorDbName").equals("Afarinegan")){
//
//                            lb_good_1.setText("نام");
//                            lb_good_2.setText("قطع");
//                            lb_good_3.setText("پشت جلد");
//
//
//                        }

                        lb_good_2.setText("نام");
                        lb_good_1.setText("قطع");
                        lb_good_3.setText("پشت جلد");



                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getGoodName()));
                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getMaxSellPrice()));

                        ll_good_4.setVisibility(View.GONE);
                        lb_good_2.setVisibility(View.GONE);

                    } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){

//                        lb_good_1.setText("نام");
//                        lb_good_2.setText("شماره قفسه");
//                        lb_good_3.setText("موجودی");
//                        lb_good_4.setText("قیمت");
//
//
//                        lb_good_1.setVisibility(View.GONE);
//
//                        tv_good_1.setText(NumberFunctions.PerisanNumber(singleGood.getGoodName()));
//                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFormNo()));
//                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFacAmount()));
//
//                        tv_good_4.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.valueOf(singleGood.getGoodMaxSellPrice()))));
//



                    }else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrMahris")){
//                        lb_good_1.setText("موجودی کل");
//                        lb_good_2.setText("قطع");
//                        lb_good_3.setText("موجودی");
//
//                        lb_good_4.setText("پشت جلد");
//
//                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getTotalAvailable()));
//                        tv_good_2.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
//                        tv_good_3.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getFacAmount()));
//
//                        tv_good_4.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getGoodMaxSellPrice()));

                    }else{


                        lb_good_2.setText("نام");
                        lb_good_1.setText("قطع");
                        lb_good_3.setText("پشت جلد");

                        tv_good_2.setText(NumberFunctions.PerisanNumber(StackEnumeration.getGoodName()));
                        tv_good_1.setText(NumberFunctions.PerisanNumber(ocr_goods.get(0).getSize()));
                        tv_good_3.setText(NumberFunctions.PerisanNumber(StackEnumeration.getMaxSellPrice()));

                        ll_good_4.setVisibility(View.GONE);
                    }

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

        byte[] BaseImageByte;
        BaseImageByte = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
        iv_good.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length), BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getWidth() * 2, BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getHeight() * 2, false));
        //iv_good.setOnTouchListener(new ZoomHelper());

        Call<RetrofitResponse> call2;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call2=apiInterface.GetImage("getImage", StackEnumeration.getGoodCode(), 0, 400);
        }else{
            call2=secendApiInterface.GetImage("getImage", StackEnumeration.getGoodCode(), 0, 400);
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

                try {


                } catch (Exception e) {
                    callMethod.Log("Network check error: " + e.getMessage());
                    callMethod.showToast("خطا در بررسی وضعیت شبکه");
                }
            }
        });

        CountStep=callMethod.ReadString("CountStep");


        Conter="";


        // مرحله فعلی = اولین CountedAmount که null است
        if (StackEnumeration.getEnumerationState().equals("1")) {
            tvcount.setText("شمارش اول");
        } else if (StackEnumeration.getEnumerationState().equals("2")) {
            tvcount.setText("شمارش دوم");

        } else if (StackEnumeration.getEnumerationState().equals("3")) {
            tvcount.setText("شمارش سوم");
        } else {
            tvcount.setText("اتمام شمارش آیتم");
            ed_good_1.setVisibility(View.GONE);
        }



        if (StackEnumeration.getStackLockFlag().equals("1")) {
            inventory_isFinished = "1"; // اتمام شمارش
        } else {
            inventory_isFinished = "0"; // هنوز ادامه دارد
        }

        btn_confirm.setOnClickListener(v -> {

            if (StackEnumeration.getStackLockFlag().equals("1")) {
                callMethod.showToast("شمارش این آیتم تمام شده است");

            }else{
                String Counted = NumberFunctions.EnglishNumber(ed_good_1.getText().toString());

                Call<RetrofitResponse> call1;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                    call1 = apiInterface.SetNumeration(
                            "SetNumeration",
                            StackEnumeration.getStackEnumerationRowCode(),
                            StackEnumeration.getEnumerationState(),
                            Counted
                    );
                } else {
                    call1 = apiInterface.SetNumeration(
                            "SetNumeration",
                            StackEnumeration.getStackEnumerationRowCode(),
                            StackEnumeration.getEnumerationState(),
                            Counted
                    );
                }


                call1.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call1, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            callMethod.Log("onResponse 2");

                            assert response.body() != null;
                            Intent intent = new Intent(mContext, Ocr_StackEnumeration_Janamaie_Check_Activity.class);
                            intent.putExtra("StackEnumerationCode", StackEnumeration.getStackEnumerationRef());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                            mContext.startActivity(intent);
                            ((Activity) mContext).finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call1, @NonNull Throwable t) {
                        callMethod.Log("onFailure 2");

                        try {
                            callMethod.Log("Network check error: " +t.getMessage());

                        } catch (Exception e) {
                            callMethod.Log("Network check error: " + e.getMessage());
                            callMethod.showToast("خطا در بررسی وضعیت شبکه");
                        }
                    }
                });













//                if (NumberFunctions.EnglishNumber(ed_good_1.getText().toString()).equals(singleGood.getFacAmount())){
//
//
//
//
//                }
//                else{
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
//                    builder.setTitle("مغایرت");
//                    builder.setMessage("با تعداد در فاکتور مغایرت دارد مطمئن هستید ؟");
//
//                    builder.setPositiveButton(R.string.textvalue_yes, (dialog1, which) -> {
//
//                        Call<RetrofitResponse> call1;
//                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
//                            call1 = apiInterface.OcrCountInventory(
//                                    "OcrCountInventory",
//                                    singleGood.getAppOCRFactorRowCode(),
//                                    "0",
//                                    callMethod.ReadString("JobPersonRef"),
//                                    Counted,
//                                    Conter,
//                                    inventory_isFinished
//                            );
//                        } else {
//                            call1 = apiInterface.OcrCountInventory(
//                                    "OcrCountInventory",
//                                    singleGood.getAppOCRFactorRowCode(),
//                                    "0",
//                                    callMethod.ReadString("JobPersonRef"),
//                                    Counted,
//                                    Conter,
//                                    inventory_isFinished
//                            );
//                        }
//
//
//                        callMethod.Log("call=" + call1.request().url());
//                        callMethod.Log("call=" + call1.request().toString());
//
//
//                        call1.enqueue(new Callback<RetrofitResponse>() {
//                            @Override
//                            public void onResponse(@NonNull Call<RetrofitResponse> call1, @NonNull Response<RetrofitResponse> response) {
//                                if (response.isSuccessful()) {
//                                    callMethod.Log("step 2");
//
//                                    assert response.body() != null;
//                                    Intent intent = new Intent(mContext, Ocr_StackEnumeration_Factor_Check_Activity.class);
//                                    intent.putExtra("ScanResponse", BarcodeScan);
//
//                                    intent.putExtra("State", "0");
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
//                                    mContext.startActivity(intent);
//                                    ((Activity) mContext).finish();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(@NonNull Call<RetrofitResponse> call1, @NonNull Throwable t) {
//
//                                try {
//
//                                } catch (Exception e) {
//                                    callMethod.Log("Network check error: " + e.getMessage());
//                                    callMethod.showToast("خطا در بررسی وضعیت شبکه");
//                                }
//                            }
//                        });
//
//                    });
//
//                    builder.setNegativeButton(R.string.textvalue_no, (dialog1, which) -> {
//                        // code to handle negative button click
//                    });
//
//                    AlertDialog dialog1 = builder.create();
//                    dialog1.show();
//
//
//
//                }
            }




        });


        btn_cansel.setOnClickListener(v -> {


            dialog.dismiss(); // بستن پنجره
        });


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
            goodscan_tvstatus.setText("در این فاکتور وجود ندارد");
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
                                            call=apiInterface.CheckState("OcrControlled", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "0", "");
                                        }else{
                                            call=secendApiInterface.CheckState("OcrControlled", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "0", "");
                                        }

                                        call.enqueue(new Callback<RetrofitResponse>() {
                                            @Override
                                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                                if (response.isSuccessful()) {

                                                    Intent intent = new Intent(mContext, Ocr_Collect_Confirm_Activity.class);
                                                    intent.putExtra("ScanResponse", barcodescan);
                                                    intent.putExtra("State", "0");
                                                    intent.putExtra("ShowGoodDetail", "0");
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                                                    ((Activity) mContext).finish();
                                                    mContext.startActivity(intent);
                                                }
                                            }
                                            @Override
                                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
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
                                                }                                            }
                                        });

                                    }else if (state.equals("1"))
                                    {

                                        Call<RetrofitResponse> call;
                                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                                            call=apiInterface.CheckState("OcrControlled", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "2", "");
                                        }else{
                                            call=secendApiInterface.CheckState("OcrControlled", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "2", "");
                                        }
                                        call.enqueue(new Callback<RetrofitResponse>() {
                                            @Override
                                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                                if (response.isSuccessful()) {

                                                    Intent intent = new Intent(mContext, Ocr_Check_Confirm_Activity.class);
                                                    intent.putExtra("ScanResponse", barcodescan);
                                                    intent.putExtra("State", "1");
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                                                    ((Activity) mContext).finish();
                                                    mContext.startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
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
                                                }                                            }
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
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
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

                    Factor Factor_detail=response.body().getFactors().get(0);

                    factor_detail(Factor_detail);
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
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
                }
            }
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
                }
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
//            public void onResponse(@NonNull Call<RetrofitResponse> call,@NonNull  Response<RetrofitResponse> response) {
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
//            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
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
        final TextView goodcode_tv = dialog.findViewById(R.id.ocr_stacklocation_goodcode_tv);
        final EditText stacklocation_et = dialog.findViewById(R.id.ocr_stacklocation_explain_et);


        goodname_tv.setText(ocr_good.getGoodName());
        goodcode_tv.setText(ocr_good.getGoodCode());
        stacklocation_et.setText(ocr_good.getStackLocation());
        stacklocation_et.selectAll();

        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
            stacklocation_et.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){
            stacklocation_et.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else{
            stacklocation_et.setInputType(InputType.TYPE_CLASS_TEXT);
        }

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
                    "SetStackLocation",
                    ocr_good.getGoodCode(),
                    NumberFunctions.EnglishNumber(safeInput)
            );

            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        if (response.body().getText().equals("Done")){
                            dialog.dismiss();
                            dialogProg.dismiss();
                            callMethod.showToast("ثبت گردید");
                        }


                    }
                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
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
                    }
                    dialog.dismiss();
                    dialogProg.dismiss();
                    callMethod.showToast("اطلاعات ثبت نگردید");

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






    @SuppressLint("ClickableViewAccessibility")
    public void good_detail(
            Ocr_Good singleGood,
            String BarcodeScan,
            OnGoodConfirmListener listener
    ) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rawContent = LayoutInflater
                .from(mContext)
                .inflate(R.layout.ocr_gooddetail_box, null, false);

        ScrollView scrollView = new ScrollView(mContext);
        scrollView.setFillViewport(true);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.addView(
                rawContent,
                new ScrollView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        dialog.setContentView(scrollView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        ImageView iv_good = rawContent.findViewById(
                R.id.ocr_gooddetail_b_img
        );

        TextView tv_good_1 = rawContent.findViewById(R.id.ocr_gooddetail_b_tv1);
        TextView tv_good_2 = rawContent.findViewById(R.id.ocr_gooddetail_b_tv2);
        TextView tv_good_3 = rawContent.findViewById(R.id.ocr_gooddetail_b_tv3);
        TextView tv_good_4 = rawContent.findViewById(R.id.ocr_gooddetail_b_tv4);
        TextView tv_good_5 = rawContent.findViewById(R.id.ocr_gooddetail_b_tv5);
        TextView tv_good_6 = rawContent.findViewById(R.id.ocr_gooddetail_b_tv6);
        TextView tv_good_7 = rawContent.findViewById(R.id.ocr_gooddetail_b_tv7);

        TextView lb_good_1 = rawContent.findViewById(R.id.ocr_gooddetail_b_lb1);
        TextView lb_good_2 = rawContent.findViewById(R.id.ocr_gooddetail_b_lb2);
        TextView lb_good_3 = rawContent.findViewById(R.id.ocr_gooddetail_b_lb3);
        TextView lb_good_4 = rawContent.findViewById(R.id.ocr_gooddetail_b_lb4);
        TextView lb_good_5 = rawContent.findViewById(R.id.ocr_gooddetail_b_lb5);
        TextView lb_good_6 = rawContent.findViewById(R.id.ocr_gooddetail_b_lb6);
        TextView lb_good_7 = rawContent.findViewById(R.id.ocr_gooddetail_b_lb7);

        LinearLayoutCompat ll_good_1 = rawContent.findViewById(R.id.ocr_gooddetail_ll_lb1);
        LinearLayoutCompat ll_good_2 = rawContent.findViewById(R.id.ocr_gooddetail_ll_lb2);
        LinearLayoutCompat ll_good_3 = rawContent.findViewById(R.id.ocr_gooddetail_ll_lb3);
        LinearLayoutCompat ll_good_4 = rawContent.findViewById(R.id.ocr_gooddetail_ll_lb4);
        LinearLayoutCompat ll_good_5 = rawContent.findViewById(R.id.ocr_gooddetail_ll_lb5);
        LinearLayoutCompat ll_good_6 = rawContent.findViewById(R.id.ocr_gooddetail_ll_lb6);
        LinearLayoutCompat ll_good_7 = rawContent.findViewById(R.id.ocr_gooddetail_ll_lb7);

        LinearLayoutCompat ll_amount = rawContent.findViewById(
                R.id.ocr_gooddetail_ll_lb1
        );

        MaterialButton btn_confirm = rawContent.findViewById(
                R.id.ocr_gooddetail_b_btn_confirm
        );

        MaterialButton btn_cancel = rawContent.findViewById(
                R.id.ocr_gooddetail_b_btn_cancel
        );

        TextView[] labels = {
                lb_good_1,
                lb_good_2,
                lb_good_3,
                lb_good_4,
                lb_good_5,
                lb_good_6,
                lb_good_7
        };

        TextView[] values = {
                tv_good_1,
                tv_good_2,
                tv_good_3,
                tv_good_4,
                tv_good_5,
                tv_good_6,
                tv_good_7
        };

        LinearLayoutCompat[] rows = {
                ll_good_1,
                ll_good_2,
                ll_good_3,
                ll_good_4,
                ll_good_5,
                ll_good_6,
                ll_good_7
        };

        configureCompactDetailViews(
                iv_good,
                labels,
                values,
                rows,
                btn_confirm,
                btn_cancel
        );

        setDefaultGoodImage(iv_good);

        Call<RetrofitResponse> call;

        if (callMethod.ReadString("FactorDbName")
                .equals(callMethod.ReadString("DbName"))) {

            call = apiInterface.GetGoodDetail(
                    "GetOcrGoodDetail",
                    singleGood.getGoodCode()
            );

        } else {

            call = secendApiInterface.GetGoodDetail(
                    "GetOcrGoodDetail",
                    singleGood.getGoodCode()
            );
        }

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<RetrofitResponse> call,
                    @NonNull Response<RetrofitResponse> response
            ) {

                if (!response.isSuccessful()
                        || response.body() == null
                        || response.body().getOcr_Goods() == null
                        || response.body().getOcr_Goods().isEmpty()) {

                    callMethod.showToast(
                            "جزئیات کالا دریافت نشد"
                    );
                    return;
                }

                Ocr_Good detail = response.body()
                        .getOcr_Goods()
                        .get(0);

                if (!callMethod.ReadBoolan("ShowDetailAmount")) {
                    ll_amount.setVisibility(View.GONE);
                }

                bindGoodDetailValues(
                        singleGood,
                        detail,
                        labels,
                        values,
                        rows
                );
            }

            @Override
            public void onFailure(
                    @NonNull Call<RetrofitResponse> call,
                    @NonNull Throwable throwable
            ) {
                showNetworkFailure(
                        "مشکل در دریافت جزئیات کالا",
                        throwable
                );
            }
        });

        Call<RetrofitResponse> imageCall;

        if (callMethod.ReadString("FactorDbName")
                .equals(callMethod.ReadString("DbName"))) {

            imageCall = apiInterface.GetImage(
                    "getImage",
                    singleGood.getGoodCode(),
                    0,
                    400
            );

        } else {

            imageCall = secendApiInterface.GetImage(
                    "getImage",
                    singleGood.getGoodCode(),
                    0,
                    400
            );
        }

        imageCall.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<RetrofitResponse> call,
                    @NonNull Response<RetrofitResponse> response
            ) {

                if (!response.isSuccessful()
                        || response.body() == null
                        || response.body().getText() == null) {
                    return;
                }

                try {
                    byte[] imageBytes = Base64.decode(
                            response.body().getText(),
                            Base64.DEFAULT
                    );

                    Bitmap bitmap = BitmapFactory.decodeByteArray(
                            imageBytes,
                            0,
                            imageBytes.length
                    );

                    if (bitmap != null) {
                        iv_good.setImageBitmap(bitmap);
                    }

                } catch (Exception exception) {
                    callMethod.Log(
                            "Image decode error: "
                                    + exception.getMessage()
                    );
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<RetrofitResponse> call,
                    @NonNull Throwable throwable
            ) {
                callMethod.Log(
                        "Image request error: "
                                + throwable.getMessage()
                );
            }
        });

        btn_confirm.setOnClickListener(v -> {

            if (callMethod.ReadBoolan("ListOrSingle")) {

                if (callMethod.ReadBoolan("CheckListFromGoodDialog")) {

                    if (listener != null) {
                        listener.onGoodConfirmed(singleGood);
                    }

                    dialog.dismiss();
                }

                return;
            }

            if (callMethod.ReadBoolan("CheckListFromGoodDialog")) {

                if (listener != null) {
                    listener.onGoodConfirmed(singleGood);
                }

                dialog.dismiss();
                return;
            }

            Call<RetrofitResponse> controlCall;

            if (callMethod.ReadString("FactorDbName")
                    .equals(callMethod.ReadString("DbName"))) {

                controlCall = apiInterface.OcrControlled(
                        "OcrControlled",
                        singleGood.getAppOCRFactorRowCode(),
                        "0",
                        callMethod.ReadString("JobPersonRef")
                );

            } else {

                controlCall = secendApiInterface.OcrControlled(
                        "OcrControlled",
                        singleGood.getAppOCRFactorRowCode(),
                        "0",
                        callMethod.ReadString("JobPersonRef")
                );
            }

            btn_confirm.setEnabled(false);

            controlCall.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(
                        @NonNull Call<RetrofitResponse> call,
                        @NonNull Response<RetrofitResponse> response
                ) {

                    btn_confirm.setEnabled(true);

                    if (!response.isSuccessful()) {
                        callMethod.showToast(
                                "ثبت کنترل کالا انجام نشد"
                        );
                        return;
                    }

                    dialog.dismiss();

                    Intent intent = new Intent(
                            mContext,
                            Ocr_Collect_Confirm_Activity.class
                    );

                    intent.putExtra("ScanResponse", BarcodeScan);
                    intent.putExtra("State", "0");
                    intent.putExtra("FactorImage", "");
                    intent.putExtra(
                            "ShowGoodDetail",
                            callMethod.ReadBoolan(
                                    "ShowNextGoodDetailAfterControl"
                            ) ? "1" : "0"
                    );

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);

                    if (mContext instanceof Activity) {
                        ((Activity) mContext).finish();
                    }
                }

                @Override
                public void onFailure(
                        @NonNull Call<RetrofitResponse> call,
                        @NonNull Throwable throwable
                ) {
                    btn_confirm.setEnabled(true);
                    showNetworkFailure(
                            "خطا در ثبت کنترل کالا",
                            throwable
                    );
                }
            });
        });

        btn_cancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGoodCanceled(singleGood);
            }
            dialog.dismiss();
        });

        dialog.setOnShowListener(ignored ->
                applyManagedDialogSize(
                        dialog,
                        0.94f,
                        0.90f,
                        680
                )
        );

        dialog.show();
    }


    public void goodamount_detail(
            String amount,
            String shortage
    ) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rawContent = LayoutInflater
                .from(mContext)
                .inflate(R.layout.ocr_amount_zoom, null, false);

        dialog.setContentView(rawContent);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        TextView tv_good_1 = rawContent.findViewById(
                R.id.ocr_amountzoome_tv1
        );

        TextView tv_good_2 = rawContent.findViewById(
                R.id.ocr_amountzoome_tv2
        );

        TextView tv_good_3 = rawContent.findViewById(
                R.id.ocr_amountzoome_tv3
        );

        BigDecimal facAmount = parseDecimal(amount);
        BigDecimal shortageAmount = parseDecimal(shortage);
        BigDecimal remaining = facAmount.subtract(shortageAmount);

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        tv_good_1.setText(
                NumberFunctions.PerisanNumber(
                        formatDecimal(facAmount)
                )
        );

        tv_good_2.setText(
                NumberFunctions.PerisanNumber(
                        formatDecimal(remaining)
                )
        );

        tv_good_3.setText(
                NumberFunctions.PerisanNumber(
                        formatDecimal(shortageAmount)
                )
        );

        styleAmountValue(
                tv_good_1,
                ContextCompat.getColor(
                        mContext,
                        R.color.colorPrimaryDark
                )
        );

        styleAmountValue(
                tv_good_2,
                ContextCompat.getColor(
                        mContext,
                        R.color.colorPrimaryDark
                )
        );

        styleAmountValue(
                tv_good_3,
                ContextCompat.getColor(
                        mContext,
                        R.color.red_800
                )
        );

        dialog.setOnShowListener(ignored ->
                applyManagedDialogSize(
                        dialog,
                        0.88f,
                        0.60f,
                        520
                )
        );

        dialog.show();
    }


    private void configureCompactDetailViews(
            ImageView imageView,
            TextView[] labels,
            TextView[] values,
            LinearLayoutCompat[] rows,
            MaterialButton confirmButton,
            MaterialButton cancelButton
    ) {

        DisplayMetrics metrics = mContext
                .getResources()
                .getDisplayMetrics();

        int imageHeight = Math.min(
                dpToPx(190),
                Math.round(metrics.heightPixels * 0.25f)
        );

        ViewGroup.LayoutParams imageParams = imageView.getLayoutParams();
        imageParams.height = imageHeight;
        imageView.setLayoutParams(imageParams);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        for (TextView label : labels) {
            if (label == null) {
                continue;
            }

            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            label.setMaxLines(1);
            label.setEllipsize(TextUtils.TruncateAt.END);
        }

        for (TextView value : values) {
            if (value == null) {
                continue;
            }

            value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            value.setMaxLines(2);
            value.setEllipsize(TextUtils.TruncateAt.END);
            value.setPadding(
                    dpToPx(4),
                    dpToPx(3),
                    dpToPx(4),
                    dpToPx(3)
            );
        }

        for (LinearLayoutCompat row : rows) {
            if (row == null) {
                continue;
            }

            row.setMinimumHeight(dpToPx(38));
            row.setPadding(
                    dpToPx(6),
                    dpToPx(2),
                    dpToPx(6),
                    dpToPx(2)
            );
        }

        confirmButton.setMinHeight(dpToPx(42));
        cancelButton.setMinHeight(dpToPx(42));
        confirmButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
    }


    private void bindGoodDetailValues(
            Ocr_Good source,
            Ocr_Good detail,
            TextView[] labels,
            TextView[] values,
            LinearLayoutCompat[] rows
    ) {

        String company = callMethod.ReadString(
                "EnglishCompanyNameUse"
        );

        if (company.equals("OcrQoqnoos")
                || company.equals("OcrQoqnoosOnline")) {

            setDetailRow(labels, values, rows, 0, "موجودی کل", detail.getTotalAvailable());
            setDetailRow(labels, values, rows, 1, "قطع", detail.getSize());
            setDetailRow(labels, values, rows, 2, "نوع جلد", detail.getCoverType());
            setDetailRow(labels, values, rows, 3, "پشت جلد", detail.getPageNo());
            setDetailRow(labels, values, rows, 4, "شماره قفسه", detail.getGoodExplain2());
            hideDetailRow(rows, 5);
            hideDetailRow(rows, 6);

        } else if (company.equals("OcrGostaresh")) {

            setDetailRow(labels, values, rows, 0, "نام", source.getGoodName());
            setDetailRow(labels, values, rows, 1, "شماره قفسه", detail.getFormNo());
            setDetailRow(labels, values, rows, 2, "تعداد فاکتور", source.getFacAmount());
            setDetailRow(labels, values, rows, 3, "قیمت", source.getGoodMaxSellPrice());
            setDetailRow(labels, values, rows, 4, "موجودی کل", detail.getTotalAvailable());
            setDetailRow(
                    labels,
                    values,
                    rows,
                    5,
                    "قطع - جلد",
                    safeText(detail.getCoverType())
                            + " - "
                            + safeText(detail.getSize())
            );
            setDetailRow(labels, values, rows, 6, "کد کالا", source.getGoodCode());

            values[2].setTextColor(
                    ContextCompat.getColor(
                            mContext,
                            R.color.red_800
                    )
            );
            values[2].setTypeface(null, Typeface.BOLD);

        } else if (company.equals("OcrMahris")) {

            setDetailRow(labels, values, rows, 0, "موجودی کل", detail.getTotalAvailable());
            setDetailRow(labels, values, rows, 1, "قطع", detail.getSize());
            setDetailRow(labels, values, rows, 2, "نوع جلد", detail.getCoverType());
            setDetailRow(labels, values, rows, 3, "پشت جلد", detail.getGoodMaxSellPrice());
            setDetailRow(labels, values, rows, 4, "شماره قفسه", source.getGoodExplain3());
            setDetailRow(labels, values, rows, 5, "نیاز فاکتور", source.getFacAmount());
            setDetailRow(labels, values, rows, 6, "کد کالا", source.getGoodCode());

        } else if (company.equals("OcrCheshme")) {

            setDetailRow(labels, values, rows, 0, "موجودی کل", detail.getTotalAvailable());
            setDetailRow(labels, values, rows, 1, "قطع", detail.getSize());
            setDetailRow(labels, values, rows, 2, "نوع جلد", detail.getCoverType());
            setDetailRow(labels, values, rows, 3, "قیمت", detail.getMaxSellPrice());
            setDetailRow(labels, values, rows, 4, "ناشر", detail.getGoodExplain2());
            setDetailRow(labels, values, rows, 5, "نیاز فاکتور", source.getFacAmount());
            setDetailRow(labels, values, rows, 6, "موقعیت", source.getLocationTitle());

        } else {

            setDetailRow(labels, values, rows, 0, "موجودی کل", detail.getTotalAvailable());
            setDetailRow(labels, values, rows, 1, "قطع", detail.getSize());
            setDetailRow(labels, values, rows, 2, "نوع جلد", detail.getCoverType());
            hideDetailRow(rows, 3);
            hideDetailRow(rows, 4);
            hideDetailRow(rows, 5);
            hideDetailRow(rows, 6);
        }
    }


    private void setDetailRow(
            TextView[] labels,
            TextView[] values,
            LinearLayoutCompat[] rows,
            int index,
            String label,
            Object value
    ) {

        rows[index].setVisibility(View.VISIBLE);
        labels[index].setVisibility(View.VISIBLE);
        values[index].setVisibility(View.VISIBLE);

        labels[index].setText(label);
        values[index].setText(
                NumberFunctions.PerisanNumber(
                        normalizeNumber(value)
                )
        );
    }


    private void hideDetailRow(
            LinearLayoutCompat[] rows,
            int index
    ) {
        rows[index].setVisibility(View.GONE);
    }


    private void setDefaultGoodImage(ImageView imageView) {

        try {
            byte[] imageBytes = Base64.decode(
                    mContext.getString(R.string.no_photo),
                    Base64.DEFAULT
            );

            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    imageBytes,
                    0,
                    imageBytes.length
            );

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }

        } catch (Exception exception) {
            callMethod.Log(
                    "Default image error: "
                            + exception.getMessage()
            );
        }
    }


    private void applyManagedDialogSize(
            Dialog dialog,
            float widthRatio,
            float heightRatio,
            int maxWidthDp
    ) {

        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }

        DisplayMetrics metrics = mContext
                .getResources()
                .getDisplayMetrics();

        int width = Math.min(
                Math.round(metrics.widthPixels * widthRatio),
                dpToPx(maxWidthDp)
        );

        int height = Math.round(
                metrics.heightPixels * heightRatio
        );

        window.setLayout(width, height);
        window.setGravity(Gravity.CENTER);
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );
    }


    private void styleAmountValue(
            TextView textView,
            int color
    ) {

        textView.setTextColor(color);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        textView.setMaxLines(1);
        textView.setPadding(
                dpToPx(6),
                dpToPx(5),
                dpToPx(6),
                dpToPx(5)
        );
    }


    private BigDecimal parseDecimal(Object value) {

        String text = safeText(value);

        if (text.isEmpty()
                || text.equalsIgnoreCase("null")) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(text);
        } catch (NumberFormatException exception) {
            callMethod.Log(
                    "Invalid decimal value: " + text
            );
            return BigDecimal.ZERO;
        }
    }


    private String formatDecimal(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }


    private String normalizeNumber(Object value) {

        String text = safeText(value);

        if (text.isEmpty()) {
            return "-";
        }

        try {
            return new BigDecimal(text)
                    .stripTrailingZeros()
                    .toPlainString();
        } catch (NumberFormatException exception) {
            return text;
        }
    }


    private String safeText(Object value) {
        return value == null
                ? ""
                : String.valueOf(value).trim();
    }


    private int dpToPx(int dp) {
        return Math.round(
                dp * mContext
                        .getResources()
                        .getDisplayMetrics()
                        .density
        );
    }


    private void showNetworkFailure(
            String fallbackMessage,
            Throwable throwable
    ) {

        callMethod.Log(
                fallbackMessage
                        + ": "
                        + (throwable == null
                        ? ""
                        : throwable.getMessage())
        );

        try {
            if (!NetworkUtils.isNetworkAvailable(mContext)) {
                callMethod.showToast("اتصال اینترنت قطع است!");
            } else if (NetworkUtils.isVPNActive()) {
                callMethod.showToast(
                        "VPN فعال است، ممکن است ارتباط مختل شود"
                );
            } else {
                callMethod.showToast(fallbackMessage);
            }
        } catch (Exception exception) {
            callMethod.showToast(fallbackMessage);
        }
    }

}
