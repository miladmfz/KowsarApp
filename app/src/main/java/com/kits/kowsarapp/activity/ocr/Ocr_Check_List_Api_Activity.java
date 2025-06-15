package com.kits.kowsarapp.activity.ocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.ocr.Ocr_Check_ListApi_Adapter;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_Check_List_Api_Activity extends AppCompatActivity {


        Intent intent;
        ProgressBar prog;
        GridLayoutManager gridLayoutManager;
        Dialog dialog1;
        Call<RetrofitResponse> Requset_List_call;
        Call<RetrofitResponse> Requset_ListCount_call;

        Ocr_APIInterface apiInterface;
        Ocr_APIInterface secendApiInterface;
        Ocr_Check_ListApi_Adapter ocr_check_listApi_adapter;

        Handler handler;
        Handler counthandler=new Handler();
        CallMethod callMethod;
        Ocr_DBH ocr_dbh;

        ArrayList<Factor> factors=new ArrayList<>();
        ArrayList<Factor> visible_factors=new ArrayList<>();
        ArrayList<String> customerpath=new ArrayList<>();
        ArrayList<Factor> all_factors=new ArrayList<>();


        Button btn_refresh_list;


        LinearLayout factorlist_ll_counter;

        RecyclerView factor_list_recycler;

        TextView textView_Count,textView_status;

        AppCompatEditText edtsearch;

        Spinner spinnerPath;

        String Row="10",state="0",StateEdited="0",StateShortage="0",TotallistCount="0",srch="",path="همه";



        private boolean loading = true;
        int pastVisiblesItems=0, visibleItemCount, totalItemCount;
        int recallcount=0, PageNo=0;


        private int clickCount = 0;
        private long lastClickTime = 0;
        private static final long DOUBLE_CLICK_TIME_DELTA = 500;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
            setContentView(R.layout.ocr_activity_check_list_api);

            dialog1 = new Dialog(this);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setContentView(R.layout.ocr_spinner_box);
            TextView repw = dialog1.findViewById(R.id.ocr_spinner_text);
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


        }

        public void Config() {
            callMethod = new CallMethod(this);
            ocr_dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));
            apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
            secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
            handler=new Handler();
            prog = findViewById(R.id.ocr_checklist_a_prog);

            Toolbar toolbar = findViewById(R.id.ocr_checklist_a_toolbar);
            setSupportActionBar(toolbar);


            factor_list_recycler=findViewById(R.id.ocr_checklist_a_recyclerView);
            factorlist_ll_counter=findViewById(R.id.ocr_checklist_a_ll_counter);



            textView_Count=findViewById(R.id.ocr_checklist_a_count);
            textView_status=findViewById(R.id.ocr_checklist_a_Tvstatus);
            edtsearch = findViewById(R.id.ocr_checklist_a_edtsearch);
            spinnerPath= findViewById(R.id.ocr_checklist_a_path);

            btn_refresh_list=findViewById(R.id.ocr_checklist_a_refresh);



            factorlist_ll_counter.setOnClickListener(v -> {
                long currentClickTime = System.currentTimeMillis();

                if (lastClickTime != 0 && (currentClickTime - lastClickTime) > DOUBLE_CLICK_TIME_DELTA) {
                    clickCount = 0;
                }

                clickCount++;

                if (clickCount == 2) {

                    visibleItemCount =  0;
                    totalItemCount =   0;
                    pastVisiblesItems =   0;
                    prog.setVisibility(View.VISIBLE);
                    loading = false;
                    RetrofitRequset_List();
                }

                lastClickTime = currentClickTime;
            });

            btn_refresh_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long currentClickTime = System.currentTimeMillis(); // Zaman click ro ghabl az in sabt mikonim

                    // Agar 5 saniye gap bod, click ro reset mikonim
                    if (lastClickTime != 0 && (currentClickTime - lastClickTime) > DOUBLE_CLICK_TIME_DELTA) {
                        clickCount = 0; // Reset click ha agar be mehr zaman (5 saniye) bemoone
                    }

                    clickCount++;

                    if (clickCount == 2) {

                        visibleItemCount =  0;
                        totalItemCount =   0;
                        pastVisiblesItems =   0;
                        prog.setVisibility(View.VISIBLE);
                        loading = false;
                        RetrofitRequset_List();
                    }

                    lastClickTime = currentClickTime; // Update zamani ke click anjam shode
                }
            });

        }


        public void init(){


            customerpath.add("همه");

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
                                srch = NumberFunctions.EnglishNumber(ocr_dbh.GetRegionText(editable.toString()));
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





            RetrofitRequset_Path();

        }

        private void MoreFactor() {

//        prog.setVisibility(View.VISIBLE);
//
//        String Body_str  = "";
//
//        Body_str =callMethod.CreateJson("State", state, Body_str);
//        Body_str =callMethod.CreateJson("SearchTarget", srch, Body_str);
//        Body_str =callMethod.CreateJson("Stack",  callMethod.ReadString("StackCategory"), Body_str);
//        Body_str =callMethod.CreateJson("path", path, Body_str);
//        Body_str =callMethod.CreateJson("HasShortage", StateShortage, Body_str);
//        Body_str =callMethod.CreateJson("IsEdited", StateEdited, Body_str);
//        Body_str =callMethod.CreateJson("Row", Row, Body_str);
//        Body_str =callMethod.CreateJson("PageNo", String.valueOf(PageNo), Body_str);
//        Body_str =callMethod.CreateJson("CountFlag", "0", Body_str);
//        Body_str =callMethod.CreateJson("DbName", "", Body_str);
//
//        Call<RetrofitResponse> call = apiInterface.GetOcrFactorList(callMethod.RetrofitBody(Body_str));
//        call.enqueue(new Callback<RetrofitResponse>() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
//
//                if(response.isSuccessful()) {
//                    assert response.body() != null;
//                    ArrayList<Factor> factor_page = response.body().getFactors();
//                    factors.addAll(factor_page);
//                    visible_factors=factors;
//                    adapter.notifyDataSetChanged();
//                    String textView_st="تعداد "+adapter.getItemCount()+" از "+TotallistCount+"";
//                    textView_Count.setText(NumberFunctions.PerisanNumber(textView_st));
//                    CallRecycle();
//                    prog.setVisibility(View.GONE);
//                    loading=true;
//                }
//            }
//            @Override
//            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
//
//
//                PageNo--;
//                callMethod.showToast("فاکتور بیشتری موجود نیست");
//                prog.setVisibility(View.GONE);
//                loading = true;
//            }
//        });






            prog.setVisibility(View.VISIBLE);

            Call<RetrofitResponse> call;


            callMethod.ReadString("ActiveDatabase");

            call=apiInterface.GetOcrFactorList(
                    "GetFactorList",
                    state,
                    srch,
                    callMethod.ReadString("StackCategory"),
                    path,
                    StateShortage,
                    StateEdited,
                    Row,
                    String.valueOf(PageNo),
                    callMethod.ReadString("ActiveDatabase")
            );
            call.enqueue(new Callback<RetrofitResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    if(response.isSuccessful()) {
                        prog.setVisibility(View.GONE);

                        assert response.body() != null;
                        ArrayList<Factor> factor_page = response.body().getFactors();
                        factors.addAll(factor_page);
                        visible_factors=factors;

                        ocr_check_listApi_adapter.notifyDataSetChanged();

                        CallRecycle();
                        String textView_st="تعداد "+ocr_check_listApi_adapter.getItemCount()+" از "+TotallistCount+"";
                        textView_Count.setText(NumberFunctions.PerisanNumber(textView_st));
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

            ocr_check_listApi_adapter = new Ocr_Check_ListApi_Adapter(visible_factors,state, App.getContext());
            if (ocr_check_listApi_adapter.getItemCount()==0){
                prog.setVisibility(View.GONE);

                callMethod.showToast("فاکتوری یافت نشد");
            }

            counthandler.postDelayed(() -> {
                String textView_st="تعداد "+ocr_check_listApi_adapter.getItemCount()+" از "+TotallistCount+"";
                textView_Count.setText(NumberFunctions.PerisanNumber(textView_st));
            }, 500);
            gridLayoutManager = new GridLayoutManager(this, 1);//grid
            factor_list_recycler.setLayoutManager(gridLayoutManager);
            factor_list_recycler.setAdapter(ocr_check_listApi_adapter);
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


            Call<RetrofitResponse> call=apiInterface.GetCustomerPath("GetCustomerPath");
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {

                        recallcount=0;
                        assert response.body() != null;
                        for (Factor factor : response.body().getFactors()) {
                            customerpath.add(factor.getCustomerPath());
                        }

                        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(App.getContext(),
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
                        callMethod.Log(t.getMessage());
                    }

                }
            });
        }




        public void RetrofitRequset_List() {

            if (Requset_List_call != null && !Requset_List_call.isExecuted() && !Requset_List_call.isCanceled()) {
                Requset_List_call.cancel();
            }

/*
        PageNo=0;
        RetrofitRequset_ListCount();
        pastVisiblesItems=0;


        String Body_str  = "";

        Body_str =callMethod.CreateJson("State", state, Body_str);
        Body_str =callMethod.CreateJson("SearchTarget", srch, Body_str);
        Body_str =callMethod.CreateJson("Stack",  callMethod.ReadString("StackCategory"), Body_str);
        Body_str =callMethod.CreateJson("path", path, Body_str);
        Body_str =callMethod.CreateJson("HasShortage", StateShortage, Body_str);
        Body_str =callMethod.CreateJson("IsEdited", StateEdited, Body_str);
        Body_str =callMethod.CreateJson("Row", Row, Body_str);
        Body_str =callMethod.CreateJson("PageNo", "0", Body_str);
        Body_str =callMethod.CreateJson("CountFlag", "0", Body_str);
        Body_str =callMethod.CreateJson("DbName", "", Body_str);


        Call<RetrofitResponse> call = apiInterface.GetOcrFactorList(callMethod.RetrofitBody(Body_str));
*/
            PageNo=0;
            textView_status.setVisibility(View.GONE);
            RetrofitRequset_ListCount();
            pastVisiblesItems=0;

            Requset_List_call=apiInterface.GetOcrFactorList(
                    "GetFactorList",
                    state,
                    srch,
                    callMethod.ReadString("StackCategory"),
                    path,
                    StateShortage,
                    StateEdited,
                    Row,
                    "0",
                    callMethod.ReadString("ActiveDatabase")
            );
            callMethod.Log(Requset_List_call.request().url()+"");
            callMethod.Log(""+Requset_List_call.request().body());
            Requset_List_call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    if(response.isSuccessful()) {

                        prog.setVisibility(View.GONE);
                        loading = true;
                        recallcount=0;
                        assert response.body() != null;
                        factors.clear();
                        visible_factors.clear();
                        factors= response.body().getFactors();
                        visible_factors=factors;
                        all_factors=factors;

                        callMethod.showToast("بارگیری شد");

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
                    loading = true;

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
                            prog.setVisibility(View.GONE);
                            textView_status.setVisibility(View.VISIBLE);
                            textView_status.setText("فاکتوری یافت نشد");
                            textView_Count.setText(NumberFunctions.PerisanNumber("تعداد 0"));
                            ocr_check_listApi_adapter.notifyDataSetChanged();

                        }catch (Exception ignored){}


                    }
                }
            });

        }

        public void RetrofitRequset_ListCount() {

            if (Requset_ListCount_call != null && !Requset_ListCount_call.isExecuted() && !Requset_ListCount_call.isCanceled()) {
                Requset_ListCount_call.cancel();
            }


//        String Body_str  = "";
//
//        Body_str =callMethod.CreateJson("State", state, Body_str);
//        Body_str =callMethod.CreateJson("SearchTarget", srch, Body_str);
//        Body_str =callMethod.CreateJson("Stack",  callMethod.ReadString("StackCategory"), Body_str);
//        Body_str =callMethod.CreateJson("path", path, Body_str);
//        Body_str =callMethod.CreateJson("HasShortage", StateShortage, Body_str);
//        Body_str =callMethod.CreateJson("IsEdited", StateEdited, Body_str);
//        Body_str =callMethod.CreateJson("Row", Row, Body_str);
//        Body_str =callMethod.CreateJson("PageNo", "0", Body_str);
//        Body_str =callMethod.CreateJson("CountFlag", "1", Body_str);
//        Body_str =callMethod.CreateJson("DbName", "", Body_str);
//
//        Call<RetrofitResponse> call = apiInterface.GetOcrFactorList(callMethod.RetrofitBody(Body_str));




            Requset_ListCount_call=apiInterface.GetOcrFactorList(
                    "GetFactorListCount",
                    state,
                    srch,
                    callMethod.ReadString("StackCategory"),
                    path,
                    StateShortage,
                    StateEdited,
                    Row,
                    "0",
                    callMethod.ReadString("ActiveDatabase")
            );
            Requset_ListCount_call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if(response.isSuccessful()) {
                        assert response.body() != null;
                        TotallistCount=String.valueOf(response.body().getFactors().get(0).getTotalRow());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    callMethod.Log(t.getMessage());
                }
            });
        }

        @Override
        protected void onRestart() {
            super.onRestart();
            intent = new Intent(this, Ocr_Check_List_Api_Activity.class);
            intent.putExtra("State", state);
            startActivity(intent);
            finish();

        }
        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
        }



    }