package com.kits.kowsarapp.activity.ocr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.ocr.Ocr_FactorListOnline_Adapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_FactorListOnlineActivity extends AppCompatActivity {

    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;
    Ocr_FactorListOnline_Adapter adapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView factor_list_recycler;
    AppCompatEditText edtsearch;
    Handler handler;
    Handler counthandler=new Handler();
    ArrayList<Factor> factors=new ArrayList<>();
    String srch="";
    String TotallistCount="0";
    TextView textView_Count;
    TextView textView_status;
    String state="0",StateEdited="0",StateShortage="0";
    ProgressBar prog;

    Dialog dialog1;
    SwitchMaterial RadioEdited;
    SwitchMaterial RadioShortage;
    Spinner spinnerPath;
    String path="همه";
    ArrayList<String> customerpath=new ArrayList<>();
    Intent intent;
    NotificationManager notificationManager;
    String channel_id = "Kowsarmobile";
    String channel_name = "home";
    CallMethod callMethod;
    Ocr_DBH dbh;
    int recallcount=0;
    int ShortageCount=0;
    int EditedCount=0;


    private boolean loading = true;
    int pastVisiblesItems=0, visibleItemCount, totalItemCount;
    public int PageNo=0;
    String Row="10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_activity_factorlist_online);

        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.ocr_spinner_box);
        TextView repw = dialog1.findViewById(R.id.o_spinner_text);
        repw.setText("در حال خواندن اطلاعات");


        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
        }catch (Exception e){
            callMethod.Log(e.getMessage());
        }



    }
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public  void intent(){
        Bundle bundle =getIntent().getExtras();
        assert bundle != null;
        state = bundle.getString("State");
        StateEdited ="0";
        StateShortage ="0";
        if(state.equals("5"))
        {
            state = "0";
            StateEdited = bundle.getString("StateEdited");
            StateShortage = bundle.getString("StateShortage");
        }
    }

    public void Config() {
        callMethod = new CallMethod(this);
        dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
        handler=new Handler();
        prog = findViewById(R.id.o_onlinefactor_a_prog);

        Toolbar toolbar = findViewById(R.id.o_onlinefactor_a_toolbar);
        setSupportActionBar(toolbar);

        factor_list_recycler=findViewById(R.id.o_onlinefactor_a_recyclerView);
        textView_Count=findViewById(R.id.o_onlinefactor_a_count);
        textView_status=findViewById(R.id.o_onlinefactor_a_Tvstatus);
        edtsearch = findViewById(R.id.o_onlinefactor_a_edtsearch);
        RadioEdited= findViewById(R.id.o_onlinefactor_a_edited);
        RadioShortage= findViewById(R.id.o_onlinefactor_a_shortage);
        spinnerPath= findViewById(R.id.o_onlinefactor_a_path);

    }

    public void NotificationConfig(){

        RetrofitRequset_EditeCount();
        RetrofitRequset_shortageCount();


        Handler handler = new Handler();
        handler.postDelayed(() -> {
            String Titlequery="";
            String Bodyquery="";
            if (ShortageCount>0){
                Titlequery=Titlequery+"  کسری  ";
                Bodyquery=Bodyquery+"(دارای "+NumberFunctions.PerisanNumber(String.valueOf(ShortageCount))+" فکتور کسری)";
            }
            if (EditedCount>0){
                Titlequery=Titlequery+"  اصلاحی  ";
                Bodyquery=Bodyquery+"(دارای "+NumberFunctions.PerisanNumber(String.valueOf(EditedCount))+" فکتور اصلاحی)";
            }
            if(!Titlequery.equals(""))
                noti_Messaging(Titlequery, Bodyquery,"0");
        }, 500);

    }



    public void init(){


        customerpath.add("همه");

        if(!state.equals("0")){
            RadioEdited.setVisibility(View.GONE);
            RadioShortage.setVisibility(View.GONE);
        }else{
            NotificationConfig();
        }

        RadioEdited.setChecked(StateEdited.equals("1"));
        RadioShortage.setChecked(StateShortage.equals("1"));

        srch=callMethod.ReadString("Last_search");

        edtsearch.setText(srch);
        edtsearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged( Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {
                            srch = NumberFunctions.EnglishNumber(dbh.GetRegionText(editable.toString()));
                            srch=srch.replace(" ","%");
                            callMethod.EditString("Last_search", srch);
                            RetrofitRequset_List();
                        }, 1000);

                    }
                });



        factor_list_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount =   gridLayoutManager.getChildCount();
                    totalItemCount =   gridLayoutManager.getItemCount();
                    pastVisiblesItems =   gridLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount-1) {
                                loading = false;
                                PageNo++;
                                MoreFactor();
                        }
                    }
                }
            }
        });


        spinnerPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                path=customerpath.get(position);
                callMethod.EditString("ConditionPosition",String.valueOf(position));

                RetrofitRequset_List();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });






        RadioShortage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) StateShortage="1"; else  StateShortage="0";
            spinnerPath.setSelection(0);
            RetrofitRequset_List();
        });
        RadioEdited.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) StateEdited="1"; else  StateEdited="0";
            spinnerPath.setSelection(0);
            RetrofitRequset_List();

        });


        RetrofitRequset_Path();

    }

    private void MoreFactor() {

        prog.setVisibility(View.VISIBLE);




        Call<RetrofitResponse> call;


        call=apiInterface.GetOcrFactorList(
                "GetFactorList",
                state,
                srch,
                callMethod.ReadString("StackCategory"),
                path,
                StateShortage,
                StateEdited,
                Row,
                String.valueOf(PageNo));
        call.enqueue(new Callback<RetrofitResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                if(response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Factor> factor_page = response.body().getFactors();
                    factors.addAll(factor_page);
                    adapter.notifyDataSetChanged();
                    String textView_st="تعداد "+adapter.getItemCount()+" از "+TotallistCount+"";
                    textView_Count.setText(NumberFunctions.PerisanNumber(textView_st));
                    CallRecycle();
                    prog.setVisibility(View.GONE);
                    loading=true;
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {


                PageNo--;
                callMethod.showToast("فاکتور بیشتری موجود نیست");
                prog.setVisibility(View.GONE);
                loading = true;
            }
        });

    }

    public void CallRecycle() {

        adapter = new Ocr_FactorListOnline_Adapter(factors,state, Ocr_FactorListOnlineActivity.this);
        if (adapter.getItemCount()==0){
            callMethod.showToast("فاکتوری یافت نشد");
        }

        counthandler.postDelayed(() -> {
            String textView_st="تعداد "+adapter.getItemCount()+" از "+TotallistCount+"";
            textView_Count.setText(NumberFunctions.PerisanNumber(textView_st));
        }, 500);
        gridLayoutManager = new GridLayoutManager(this, 1);//grid
        factor_list_recycler.setLayoutManager(gridLayoutManager);
        factor_list_recycler.setAdapter(adapter);
        factor_list_recycler.setItemAnimator(new DefaultItemAnimator());
        factor_list_recycler.scrollToPosition(pastVisiblesItems);

        if (Integer.parseInt(callMethod.ReadString("LastTcPrint"))>0){
            for (Factor singlefactor :factors) {
                if(singlefactor.getAppTcPrintRef().equals(callMethod.ReadString("LastTcPrint")))
                    factor_list_recycler.scrollToPosition(factors.indexOf(singlefactor));
            }

        }

        dialog1.dismiss();


    }

    public void RetrofitRequset_Path() {


        Log.e("kowsar",callMethod.ReadString("FactorDbName"));
        Log.e("kowsar",callMethod.ReadString("DbName"));

        Call<RetrofitResponse> call;

        call=apiInterface.GetCustomerPath("GetCustomerPath");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {

                    recallcount=0;
                    assert response.body() != null;
                    for (Factor factor : response.body().getFactors()) {
                        customerpath.add(factor.getCustomerPath());
                    }

                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(Ocr_FactorListOnlineActivity.this,
                            android.R.layout.simple_spinner_item, customerpath);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPath.setAdapter(spinner_adapter);

                    try {
                        if (customerpath.size() < Integer.parseInt(callMethod.ReadString("ConditionPosition"))) {
                            callMethod.EditString("ConditionPosition", "0");
                        }
                        spinnerPath.setSelection(Integer.parseInt(callMethod.ReadString("ConditionPosition")));
                    } catch (Exception e) {
                        spinnerPath.setSelection(0);
                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                recallcount++;
                if(recallcount<2){
                    RetrofitRequset_Path();
                }else{
                    finish();
                    callMethod.showToast("مشکلی در گروه بندی ارسال");
                    Log.e("",t.getMessage());
                }

            }
        });
    }

    public void RetrofitRequset_List() {

        PageNo=0;
        RetrofitRequset_ListCount();
        pastVisiblesItems=0;

        Call<RetrofitResponse> call;

        call=apiInterface.GetOcrFactorList(
                "GetFactorList",
                state,
                srch,
                callMethod.ReadString("StackCategory"),
                path,
                StateShortage,
                StateEdited,
                Row,
                "0");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                if(response.isSuccessful()) {
                    recallcount=0;
                    assert response.body() != null;
                    factors.clear();
                    factors= response.body().getFactors();
                    if(factors.size()>0){
                        CallRecycle();

                    }else {
                        finish();
                        callMethod.showToast("فاکتوری موجود نمی باشد");
                    }

                }
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                recallcount++;
                if(recallcount<2){
                    RetrofitRequset_List();
                }else if (recallcount==2){

                    callMethod.EditString("Last_search", "");
                    srch=callMethod.ReadString("Last_search");
                    edtsearch.setText(srch);
                    RetrofitRequset_List();
                }else {
                    try {
                        factors.clear();
                        dialog1.dismiss();
                        textView_status.setText("فاکتوری یافت نشد");
                        textView_Count.setText(NumberFunctions.PerisanNumber("تعداد 0"));
                        adapter.notifyDataSetChanged();

                    }catch (Exception ignored){}


                }
            }
        });

    }

    public void RetrofitRequset_ListCount() {


        Call<RetrofitResponse> call;


        call=apiInterface.GetOcrFactorList(
                "GetFactorListCount",
                state,
                srch,
                callMethod.ReadString("StackCategory"),
                path,
                StateShortage,
                StateEdited,
                Row,
                "0");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    TotallistCount=String.valueOf(response.body().getFactors().get(0).getTotalRow());
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Log.e("test_+",t.getMessage());
            }
        });
    }

    public void RetrofitRequset_EditeCount() {

        Call<RetrofitResponse> call;

        call=apiInterface.GetOcrFactorList(
                "GetFactorListCount",
                state,
                "0",
                "همه",
                "همه",
                "0",
                "1",
                "10000",
                "0");


        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    EditedCount=Integer.parseInt(response.body().getFactors().get(0).getTotalRow());
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {}
        });
    }

    public void RetrofitRequset_shortageCount() {

        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetOcrFactorList(
                    "GetFactorListCount",
                    state,
                    "",
                    "همه",
                    "همه",
                    "1",
                    "0",
                    "10000",
                    "0");
        }else{
            call=secendApiInterface.GetOcrFactorList(
                    "GetFactorListCount",
                    state,
                    "",
                    "همه",
                    "همه",
                    "1",
                    "0",
                    "10000",
                    "0");
        }


        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    ShortageCount=Integer.parseInt(response.body().getFactors().get(0).getTotalRow());
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {}
        });
    }

    public void noti_Messaging(String title, String message,String flag) {

        notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel Channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(Channel);
        }
        Intent notificationIntent = new Intent(this, Ocr_FactorListOnlineActivity.class);
        notificationIntent.putExtra("State", "5");
        if(flag.equals("0")){
            notificationIntent.putExtra("StateEdited", "0");
            notificationIntent.putExtra("StateShortage", "1");
        }else {
            notificationIntent.putExtra("StateEdited", "1");
            notificationIntent.putExtra("StateShortage", "0");

        }


        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notcompat = new NotificationCompat.Builder(this, channel_id)
                .setContentTitle(title)
                .setContentText(message)
                .setOnlyAlertOnce(false)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(contentIntent);

        notificationManager.notify(1, notcompat.build());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        intent = new Intent(this, Ocr_FactorListOnlineActivity.class);
        intent.putExtra("State", state);
        startActivity(intent);
        finish();

    }


}