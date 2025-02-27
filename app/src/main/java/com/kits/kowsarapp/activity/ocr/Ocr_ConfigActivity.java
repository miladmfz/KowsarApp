package com.kits.kowsarapp.activity.ocr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.downloader.database.DatabaseOpenHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.model.base.AppPrinter;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_SpinnerItem;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.base.Good;
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
    Ocr_DBH dbh;
    Spinner spinnerPath,spinnercategory,spinnerjob,spinnerjobperson;
    String stackcategory="همه";
    String workcategory="0";
    ArrayList<String> jobsstr=new ArrayList<>();
    ArrayList<String> jobpersonsstr=new ArrayList<>();
    ArrayList<Integer> jobpersonsref_int=new ArrayList<>();
    ArrayList<String> stacks=new ArrayList<>();
    TextView ed_Deliverer;
    TextView tv_laststack;
    LinearLayoutCompat ll_Stack;
    MaterialButton btn_config;
    EditText ed_titlesize;
    SwitchMaterial sm_arabictext;
    ImageInfo imageInfo;



    Spinner spinnerActiveDatabase,spinnerprintername;
    String selected_PrinterName="بدون پرینتر";



    ArrayList<String> printers_name=new ArrayList<>();

    ArrayList<String> ActiveDatabase_array=new ArrayList<>();
    TextView tv_Deliverer,tv_lastprinter,tv_delay,tv_accesscount;
    EditText ed_rowcall,ed_bodysize;

    List<Ocr_SpinnerItem> works = new ArrayList<>();

    SwitchMaterial sm_showamount,sm_autosend,sm_sendtimetype,sm_printbarcode,sm_justscanner;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_activity_config);

        Config();
        init();


    }


    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));
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
        works.add(new Ocr_SpinnerItem(7,"توضیحات بسته بندی"));
        works.add(new Ocr_SpinnerItem(4,"ارسال"));
        works.add(new Ocr_SpinnerItem(5,"مدیریت"));
        works.add(new Ocr_SpinnerItem(6,"جانمایی انبار"));

        btn_config =findViewById(R.id.ocr_config_a_btn);

        ll_Stack=findViewById(R.id.ocr_config_a_line_stack);


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
        tv_accesscount =findViewById(R.id.ocr_config_a_accesscount);
        tv_laststack =findViewById(R.id.ocr_config_a_laststack);





        sm_arabictext = findViewById(R.id.ocr_config_a_arabictext);
        sm_showamount = findViewById(R.id.ocr_config_a_showamount);
        sm_autosend = findViewById(R.id.ocr_config_a_autosend);
        sm_sendtimetype = findViewById(R.id.ocr_config_a_sendtimetype);
        sm_printbarcode = findViewById(R.id.ocr_config_a_printbarcode);
        sm_justscanner = findViewById(R.id.ocr_config_a_justscanner);


        ImageView img_logo = findViewById(R.id.ocr_config_a_logo);

        Glide.with(img_logo)
                .asBitmap()
                .load(callMethod.ReadString("ServerURLUse")+"SlideImage/logo.jpg")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e("test","Failed");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.e("test","Ready");
                        imageInfo.SaveLogo(resource);
                        return false;
                    }
                })
                .into(img_logo);

    }

    public void init() {
        GetDataIsPersian();
        ed_Deliverer.setText(callMethod.ReadString("Deliverer"));
        tv_laststack.setText(callMethod.ReadString("StackCategory"));
        tv_lastprinter.setText(callMethod.ReadString("PrinterName"));

        ed_titlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        ed_bodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));

        ed_rowcall.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("RowCall")));
        tv_delay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        tv_accesscount.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("AccessCount")));

        sm_arabictext.setChecked(callMethod.ReadBoolan("ArabicText"));

        sm_showamount.setChecked(callMethod.ReadBoolan("ShowAmount"));
        sm_autosend.setChecked(callMethod.ReadBoolan("AutoSend"));
        sm_printbarcode.setChecked(callMethod.ReadBoolan("PrintBarcode"));
        sm_sendtimetype.setChecked(callMethod.ReadBoolan("SendTimeType"));
        sm_justscanner.setChecked(callMethod.ReadBoolan("JustScanner"));


        btn_config.setOnClickListener(v -> {
            callMethod.EditString("Deliverer",tv_Deliverer.getText().toString());
            callMethod.EditString("Delay",tv_delay.getText().toString());
            callMethod.EditString("AccessCount",tv_accesscount.getText().toString());

            callMethod.EditString("Category",workcategory);
            callMethod.EditString("StackCategory",stackcategory);
            callMethod.EditString("PrinterName",selected_PrinterName);

            callMethod.EditString("TitleSize",NumberFunctions.EnglishNumber(ed_titlesize.getText().toString()));
            callMethod.EditString("BodySize",NumberFunctions.EnglishNumber(ed_bodysize.getText().toString()));
            callMethod.EditString("RowCall",NumberFunctions.EnglishNumber(ed_rowcall.getText().toString()));

            finish();
        });


        sm_showamount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowAmount")) {
                callMethod.EditBoolan("ShowAmount", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowAmount", true);
                callMethod.showToast("بله");
            }
        });
        sm_printbarcode.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("PrintBarcode")) {
                callMethod.EditBoolan("PrintBarcode", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowAmount", true);
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
        sm_sendtimetype.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("SendTimeType")) {
                callMethod.EditBoolan("SendTimeType", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("SendTimeType", true);
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



        Call<RetrofitResponse> call =apiInterface.GetStackCategory("GetStackCategory");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                stacks.add("همه");
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    for ( Good good : response.body().getGoods()) {
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
                Log.e("kowsar_onFailure",t.getMessage());
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
                if(position==2){
                    ll_Stack.setVisibility(View.VISIBLE);
                }else {
                    ll_Stack.setVisibility(View.GONE);
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
                    ed_Deliverer.setText(jobpersonsstr.get(position));
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

        Call<RetrofitResponse> call =apiInterface.GetJob("GetJob",where);
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

            }
        });

    }

    public void GetDataIsPersian() {

        Call<RetrofitResponse> call =apiInterface.DbSetupvalue("DbSetupvalue","DataIsPersian");
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

            }
        });


    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}