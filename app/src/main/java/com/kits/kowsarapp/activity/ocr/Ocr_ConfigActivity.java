package com.kits.kowsarapp.activity.ocr;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.kowsarapp.adapter.base.Base_ThemeSpinnerAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.application.base.NetworkUtils;
import com.kits.kowsarapp.model.base.AppPrinter;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.model.ocr.Ocr_SpinnerItem;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.base.Job;
import com.kits.kowsarapp.model.base.JobPerson;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_ConfigActivity extends AppCompatActivity  {

    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;
    CallMethod callMethod;
    ImageInfo imageInfo;

    private static final String THEME_KEY = "selectedTheme";
    private int selectedTheme;

    ArrayList<String> jobsstr=new ArrayList<>();
    ArrayList<String> jobpersonsstr=new ArrayList<>();
    ArrayList<Integer> jobpersonsref_int=new ArrayList<>();
    ArrayList<String> stacks=new ArrayList<>();
    ArrayList<String> printers_name=new ArrayList<>();
    ArrayList<String> ActiveDatabase_array=new ArrayList<>();
    List<Ocr_SpinnerItem> works = new ArrayList<>();


    Spinner spinnerPath,spinnercategory,spinnerjob,spinnerjobperson,spinnerActiveDatabase,spinnerprintername;

    Button btn_config;
    EditText ed_titlesize,ed_rowcall,ed_bodysize;
    TextView tv_Deliverer,tv_lastprinter,tv_barcodedelay,tv_delay,tv_accesscount,tv_laststack;

    SwitchMaterial sm_showdetailamount,sm_showtotalamount,sm_autosend,sm_sendtimetype,sm_printbarcode,sm_justscanner,sm_sumamounthint,sm_arabictext,sm_listorsingle,sm_shortagelist;
    SwitchMaterial sm_checklistfromgooddetail,        sm_confirmcheckamount,    sm_sendcheckamount;
    LinearLayoutCompat ll_spinner_Stack,ll_tv_Stack;

    String stackcategory="همه";
    String workcategory="0";
    String selected_PrinterName="بدون پرینتر";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        setContentView(R.layout.ocr_activity_config);

        Config();
        init();


    }


    public void Config() {

        callMethod = new CallMethod(this);
        imageInfo = new ImageInfo(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);


        ActiveDatabase_array.add("هر دو دیتابیس");
        ActiveDatabase_array.add("دیتابیس اول");
        ActiveDatabase_array.add("دیتابیس دوم");

        works.add(new Ocr_SpinnerItem(0,"برای انتخاب کلیک کنید"));
        works.add(new Ocr_SpinnerItem(1,"اسکن بارکد"));
        works.add(new Ocr_SpinnerItem(2,"جمع کننده انبار"));
        works.add(new Ocr_SpinnerItem(3,"بررسی مجدد انبار"));
        works.add(new Ocr_SpinnerItem(4,"ارسال"));
        works.add(new Ocr_SpinnerItem(5,"مدیریت"));
        works.add(new Ocr_SpinnerItem(6,"جانمایی انبار"));
        works.add(new Ocr_SpinnerItem(7,"انبارگردانی"));

        btn_config =findViewById(R.id.ocr_config_a_btn);



        spinnerPath=findViewById(R.id.ocr_config_a_spinnerstacks);
        spinnercategory =findViewById(R.id.ocr_config_a_spinnercategory);
        spinnerprintername=findViewById(R.id.ocr_config_a_spinnerprinter);
        spinnerActiveDatabase =findViewById(R.id.ocr_config_a_spinneractivedatabase);
        spinnerjob =findViewById(R.id.ocr_config_a_spinnerjob);
        spinnerjobperson =findViewById(R.id.ocr_config_a_spinnerjobperson);


        ed_titlesize = findViewById(R.id.ocr_config_a_titlesize);
        ed_bodysize = findViewById(R.id.ocr_config_a_bodysize);
        ed_rowcall = findViewById(R.id.ocr_config_a_rowcall);



        tv_Deliverer =findViewById(R.id.ocr_config_a_deliverer);
        tv_lastprinter =findViewById(R.id.ocr_config_a_lastprinter);
        tv_delay =findViewById(R.id.ocr_config_a_delay);
        tv_barcodedelay =findViewById(R.id.ocr_config_a_barcodedelay);
        tv_accesscount =findViewById(R.id.ocr_config_a_accesscount);
        tv_laststack =findViewById(R.id.ocr_config_a_laststack);





        sm_arabictext = findViewById(R.id.ocr_config_a_arabictext);
        sm_showdetailamount = findViewById(R.id.ocr_config_a_showamount);
        sm_showtotalamount = findViewById(R.id.ocr_config_a_showtotalamount);
        sm_autosend = findViewById(R.id.ocr_config_a_autosend);
        sm_sendtimetype = findViewById(R.id.ocr_config_a_sendtimetype);
        sm_printbarcode = findViewById(R.id.ocr_config_a_printbarcode);
        sm_justscanner = findViewById(R.id.ocr_config_a_justscanner);
        sm_sumamounthint = findViewById(R.id.ocr_config_a_showsumamounthint);
        sm_listorsingle = findViewById(R.id.ocr_config_a_listorsingle);
        sm_shortagelist = findViewById(R.id.ocr_config_a_shortagelist);

        sm_checklistfromgooddetail = findViewById(R.id.ocr_config_a_checklistfromgooddetail);
        sm_confirmcheckamount = findViewById(R.id.ocr_config_a_confirmcheckamount);
        sm_sendcheckamount = findViewById(R.id.ocr_config_a_sendcheckamount);


        ll_spinner_Stack=findViewById(R.id.ocr_config_a_line_stack_spinner);
        ll_tv_Stack=findViewById(R.id.ocr_config_a_line_stack_tv);


        ImageView img_logo = findViewById(R.id.ocr_config_a_logo);

        Glide.with(img_logo)
                .asBitmap()
                .load(callMethod.ReadString("ServerURLUse")+"SlideImage/logo.jpg")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        imageInfo.SaveLogo(resource);
                        return false;
                    }
                })
                .into(img_logo);

    }

    public void init() {
        GetDataIsPersian();
        themeconfig();
        tv_Deliverer.setText(callMethod.ReadString("Deliverer"));
        tv_laststack.setText(callMethod.ReadString("StackCategory"));
        tv_lastprinter.setText(callMethod.ReadString("PrinterName"));

        ed_titlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        ed_bodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));

        ed_rowcall.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("RowCall")));
        tv_delay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        tv_barcodedelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BarcodeDelay")));
        tv_accesscount.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("AccessCount")));

        sm_arabictext.setChecked(callMethod.ReadBoolan("ArabicText"));

        sm_showdetailamount.setChecked(callMethod.ReadBoolan("ShowDetailAmount"));
        sm_showtotalamount.setChecked(callMethod.ReadBoolan("ShowTotalAmount"));
        sm_autosend.setChecked(callMethod.ReadBoolan("AutoSend"));
        sm_printbarcode.setChecked(callMethod.ReadBoolan("PrintBarcode"));
        sm_justscanner.setChecked(callMethod.ReadBoolan("JustScanner"));
        sm_sumamounthint.setChecked(callMethod.ReadBoolan("ShowSumAmountHint"));
        sm_listorsingle.setChecked(callMethod.ReadBoolan("ListOrSingle"));
        sm_shortagelist.setChecked(callMethod.ReadBoolan("ShortageList"));


        sm_checklistfromgooddetail.setChecked(callMethod.ReadBoolan("CheckListFromGoodDialog"));
        sm_confirmcheckamount.setChecked(callMethod.ReadBoolan("ConfirmCheckAmount"));
        sm_sendcheckamount.setChecked(callMethod.ReadBoolan("SendCheckAmount"));


        btn_config.setOnClickListener(v -> {
            callMethod.EditString("Deliverer",tv_Deliverer.getText().toString());
            callMethod.EditString("Delay",tv_delay.getText().toString());
            callMethod.EditString("BarcodeDelay",tv_barcodedelay.getText().toString());
            callMethod.EditString("AccessCount",tv_accesscount.getText().toString());

            callMethod.EditString("Category",workcategory);
            callMethod.EditString("StackCategory",stackcategory);
            callMethod.EditString("PrinterName",selected_PrinterName);

            callMethod.EditString("TitleSize",NumberFunctions.EnglishNumber(ed_titlesize.getText().toString()));
            callMethod.EditString("BodySize",NumberFunctions.EnglishNumber(ed_bodysize.getText().toString()));
            callMethod.EditString("RowCall",NumberFunctions.EnglishNumber(ed_rowcall.getText().toString()));

            finish();
        });

        sm_checklistfromgooddetail.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("CheckListFromGoodDialog")) {
                callMethod.EditBoolan("CheckListFromGoodDialog", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("CheckListFromGoodDialog", true);
                callMethod.showToast("بله");



            }
        });


        sm_confirmcheckamount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ConfirmCheckAmount")) {
                callMethod.EditBoolan("ConfirmCheckAmount", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ConfirmCheckAmount", true);
                callMethod.showToast("بله");
            }
        });


        sm_sendcheckamount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("SendCheckAmount")) {
                callMethod.EditBoolan("SendCheckAmount", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("SendCheckAmount", true);
                callMethod.showToast("بله");
            }
        });


        sm_showdetailamount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowDetailAmount")) {
                callMethod.EditBoolan("ShowDetailAmount", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowDetailAmount", true);
                callMethod.showToast("بله");
            }
        });
        sm_showtotalamount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowTotalAmount")) {
                callMethod.EditBoolan("ShowTotalAmount", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowTotalAmount", true);
                callMethod.showToast("بله");
            }
        });
        sm_printbarcode.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("PrintBarcode")) {
                callMethod.EditBoolan("PrintBarcode", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("PrintBarcode", true);
                callMethod.showToast("بله");
            }
        });

        sm_autosend.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("AutoSend")) {
                callMethod.EditBoolan("AutoSend", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("AutoSend", true);
                callMethod.showToast("بله");
            }
        });

        sm_justscanner.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("JustScanner")) {
                callMethod.EditBoolan("JustScanner", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("JustScanner", true);
                callMethod.showToast("بله");
            }
        });
        sm_sumamounthint.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowSumAmountHint")) {
                callMethod.EditBoolan("ShowSumAmountHint", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowSumAmountHint", true);
                callMethod.showToast("بله");
            }
        });

        sm_listorsingle.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ListOrSingle")) {
                callMethod.EditBoolan("ListOrSingle", false);
                callMethod.showToast("ارسال تکی کالا فعال شد ");
            } else {
                callMethod.EditBoolan("ListOrSingle", true);
                callMethod.showToast("انتخاب لیستی فعال شد");
            }
        });

        sm_shortagelist.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShortageList")) {
                callMethod.EditBoolan("ShortageList", false);
                callMethod.showToast("عدم نمایش لیست کسری");
            } else {
                callMethod.EditBoolan("ShortageList", true);
                callMethod.showToast(" نمایش لیست کسری");
            }
        });



        ArrayAdapter<Ocr_SpinnerItem> spinnerAdapter = new ArrayAdapter<>(Ocr_ConfigActivity.this,
                android.R.layout.simple_spinner_item, works);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercategory.setAdapter(spinnerAdapter);


        int selectedValue = Integer.parseInt(callMethod.ReadString("Category"));
        for (int i = 0; i < works.size(); i++) {
            if (works.get(i).getValue() == selectedValue) {
                spinnercategory.setSelection(i);
                break;
            }
        }

        ArrayAdapter<String> ActiveDatabase_adapter = new ArrayAdapter<>(Ocr_ConfigActivity.this,
                android.R.layout.simple_spinner_item, ActiveDatabase_array);
        ActiveDatabase_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActiveDatabase.setAdapter(ActiveDatabase_adapter);
        spinnerActiveDatabase.setSelection(Integer.parseInt(callMethod.ReadString("ActiveDatabase")));



//        Call<RetrofitResponse> call =apiInterface.GetStackCategory("GetStackCategory");
        Call<RetrofitResponse> call =apiInterface.GetCustomerPath("GetStackCategory_new");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                stacks.add("همه");
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    for (Ocr_Good good : response.body().getOcr_Goods()) {
                        stacks.add(good.getGoodExplain4());
                    }
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(Ocr_ConfigActivity.this,
                            android.R.layout.simple_spinner_item, stacks);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPath.setAdapter(spinner_adapter);
                    int targetIndex = 0;
                    for (int i = 0; i < stacks.size(); i++) {
                        if (stacks.get(i).equals(callMethod.ReadString("StackCategory"))) {
                            targetIndex = i;
                            break;
                        }
                    }
                    spinnerPath.setSelection(targetIndex); // Set selection baraye item ke matnash "همه" ast
                    tv_laststack.setText(callMethod.ReadString("StackCategory"));

                }

            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                try {
                    // 🟢 بررسی وضعیت اتصال
                    if (!NetworkUtils.isNetworkAvailable(Ocr_ConfigActivity.this)) {
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

        spinnerPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stackcategory=stacks.get(position);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        Call<RetrofitResponse> call1;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call1=apiInterface.OrderGetAppPrinter("OrderGetAppPrinter");
        }else{
            call1=secendApiInterface.OrderGetAppPrinter("OrderGetAppPrinter");
        }


        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                printers_name.add("بدون پرینتر");

                if(response.isSuccessful()) {
                    assert response.body() != null;
                    for ( AppPrinter appPrinter: response.body().getAppPrinters()) {
                        printers_name.add(appPrinter.getPrinterExplain());
                    }
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(Ocr_ConfigActivity.this,
                            android.R.layout.simple_spinner_item, printers_name);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerprintername.setAdapter(spinner_adapter);
                    int targetIndex = 0;
                    for (int i = 0; i < printers_name.size(); i++) {
                        if (printers_name.get(i).equals(callMethod.ReadString("PrinterName"))) {
                            targetIndex = i;
                            break;
                        }
                    }
                    spinnerprintername.setSelection(targetIndex); // Set selection baraye item ke matnash "همه" ast
                    tv_lastprinter.setText(callMethod.ReadString("PrinterName"));

                }

            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.Log(t.getMessage());
                try {
                    // 🟢 بررسی وضعیت اتصال
                    if (!NetworkUtils.isNetworkAvailable(Ocr_ConfigActivity.this)) {
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

        spinnerprintername.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_PrinterName=printers_name.get(position);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        spinnercategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workcategory=String.valueOf(position);
                if(position==2 ||position==7){
                    ll_spinner_Stack.setVisibility(View.VISIBLE);
                    ll_tv_Stack.setVisibility(View.VISIBLE);
                }else {
                    ll_spinner_Stack.setVisibility(View.GONE);
                    ll_tv_Stack.setVisibility(View.GONE);

                }
                if (position==7){
                    position=2;
                }

                GetJob("Ocr"+position);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerActiveDatabase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                callMethod.EditString("ActiveDatabase",String.valueOf(position));

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerjob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0) {
                    GetJobPerson(jobsstr.get(position));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerjobperson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0) {

                    callMethod.EditString("JobPersonRef",String.valueOf(jobpersonsref_int.get(position)));
                    tv_Deliverer.setText(jobpersonsstr.get(position));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    public void GetJob(String where) {
        jobsstr.clear();
        jobpersonsstr.clear();
        jobpersonsref_int.clear();
        spinnerjob.setAdapter(null);
        spinnerjobperson.setAdapter(null);

//        Call<RetrofitResponse> call =apiInterface.GetJob("GetJob",where);
        Call<RetrofitResponse> call =apiInterface.GetJob("TestJob",where);

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Job> jobs=response.body().getJobs();
                    jobsstr.add("برای انتخاب کلیک کنید");

                    for(Job job:jobs){
                        jobsstr.add(job.getTitle());
                    }
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(Ocr_ConfigActivity.this,
                            android.R.layout.simple_spinner_item,jobsstr );
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerjob.setAdapter(spinner_adapter);
                    spinnerjob.setSelection(0);

                }

            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                try {
                    // 🟢 بررسی وضعیت اتصال
                    if (!NetworkUtils.isNetworkAvailable(Ocr_ConfigActivity.this)) {
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

    public void GetDataIsPersian() {

        //Call<RetrofitResponse> call =apiInterface.DbSetupvalue("DbSetupvalue","DataIsPersian");
        Call<RetrofitResponse> call =apiInterface.GetDataDbsetup("kowsar_info","DataIsPersian");

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {

                    assert response.body() != null;
                    callMethod.EditBoolan("ArabicText", !response.body().getText().equals("1"));


                }

            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                try {
                    // 🟢 بررسی وضعیت اتصال
                    if (!NetworkUtils.isNetworkAvailable(Ocr_ConfigActivity.this)) {
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


    public void GetJobPerson(String where) {
        Call<RetrofitResponse> call =apiInterface.GetJobPerson("TestJobPerson",where);
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<JobPerson> jobPersons=response.body().getJobPersons();

                    jobpersonsstr.add("برای انتخاب کلیک کنید");
                    jobpersonsref_int.add(0);
                    for(JobPerson jobPerson:jobPersons){
                        jobpersonsstr.add(jobPerson.getName());
                        jobpersonsref_int.add(Integer.parseInt(jobPerson.getJobPersonCode()));
                    }

                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(Ocr_ConfigActivity.this,
                            android.R.layout.simple_spinner_item,jobpersonsstr);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerjobperson.setAdapter(spinner_adapter);
                    spinnerjobperson.setSelection(0);

                }

            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                try {
                    // 🟢 بررسی وضعیت اتصال
                    if (!NetworkUtils.isNetworkAvailable(Ocr_ConfigActivity.this)) {
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    private static final String[] themeNames = {
            "پیش‌فرض",
            "کوثر زرین",
            "آبی هماهنگ",
            "سبز نعناعی",
            "درخشش غروب",
            "بنفش سایبری",
            "طلای شیک",
            "نسیم دریا",
            "آینده نئون",
            "جذابیت چوب رز",
            "حالت تاریک برتر",
            "لذت لیمویی",
            "اسطوخودوسی نرم",
            "سبز نئون"
    };
    private static final int[][] themeColors = {
            // DefaultTheme

            {Color.parseColor("#FFFFFF"), Color.parseColor("#E0E0E0"), Color.parseColor("#F5F5F5")}, // Defaultare
            {Color.parseColor("#FFD700"), Color.parseColor("#B8860B"), Color.parseColor("#1E3A8A")}, // Golden & Royal Blue Theme
            {Color.parseColor("#1E3A8A"), Color.parseColor("#3B82F6"), Color.parseColor("#E0F2FE")}, // BlueHarmonyTheme
            {Color.parseColor("#2DD4BF"), Color.parseColor("#99F6E4"), Color.parseColor("#F0FDFA")}, // MintGreenTheme
            {Color.parseColor("#F97316"), Color.parseColor("#FB923C"), Color.parseColor("#FFF7ED")}, // SunsetGlowTheme
            {Color.parseColor("#8B5CF6"), Color.parseColor("#C084FC"), Color.parseColor("#FAF5FF")}, // CyberPurpleTheme
            {Color.parseColor("#B8860B"), Color.parseColor("#FFD700"), Color.parseColor("#1C1C1C")}, // ElegantGoldTheme
            {Color.parseColor("#0E7490"), Color.parseColor("#38BDF8"), Color.parseColor("#ECFEFF")}, // OceanBreezeTheme
            {Color.parseColor("#1E40AF"), Color.parseColor("#E11D48"), Color.parseColor("#111827")}, // NeonFutureTheme
            {Color.parseColor("#881337"), Color.parseColor("#FBCFE8"), Color.parseColor("#FDF2F8")}, // RosewoodEleganceTheme
            {Color.parseColor("#0F172A"), Color.parseColor("#64748B"), Color.parseColor("#020617")}, // DarkModeEliteTheme
            {Color.parseColor("#FACC15"), Color.parseColor("#16A34A"), Color.parseColor("#FEFCE8")}, // LemonFreshTheme
            {Color.parseColor("#CE93D8"), Color.parseColor("#E1BEE7"), Color.parseColor("#F3E5F5")}, // SoftLavenderTheme
            {Color.parseColor("#00C853"), Color.parseColor("#69F0AE"), Color.parseColor("#E8F5E9")}, // NeonGreenTheme
    };
    private static final int[] themeArray = {
            R.style.DefaultTheme,
            R.style.RoyalGoldTheme,
            R.style.BlueHarmonyTheme,
            R.style.MintGreenTheme,
            R.style.SunsetGlowTheme,
            R.style.CyberPurpleTheme,
            R.style.ElegantGoldTheme,
            R.style.OceanBreezeTheme,
            R.style.NeonFutureTheme,
            R.style.RosewoodEleganceTheme,
            R.style.DarkModeEliteTheme,
            R.style.LemonFreshTheme,
            R.style.SoftLavenderTheme,
            R.style.NeonGreenTheme
    };



    public void themeconfig() {


        Spinner themeSpinner = findViewById(R.id.ocr_themeSpinner);

        Button applyButton = findViewById(R.id.ocr_applyButton);

        // Set custom adapter
        Base_ThemeSpinnerAdapter adapter = new Base_ThemeSpinnerAdapter(this, themeNames, themeColors);
        themeSpinner.setAdapter(adapter);


        // Set Spinner selection based on the saved theme
        int themePosition = getThemePosition(selectedTheme);
        themeSpinner.setSelection(themePosition);

        // Handle theme selection
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTheme = getThemeFromPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Save the selected theme and restart the activity
        applyButton.setOnClickListener(v -> {
            getSharedPreferences("ThemePrefs", MODE_PRIVATE).edit().putInt(THEME_KEY, selectedTheme).apply();
            recreate();
        });
    }


    private int getThemeFromPosition(int position) {
        if (position < 0 || position >= themeArray.length) {
            return R.style.DefaultTheme;
        }
        return themeArray[position];
    }

    private int getThemePosition(int theme) {
        for (int i = 0; i < themeArray.length; i++) {
            if (themeArray[i] == theme) {
                return i;
            }
        }
        return 0; // Default position
    }




}