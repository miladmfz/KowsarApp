package com.kits.kowsarapp.activity.ocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kits.kowsarapp.adapter.ocr.Ocr_Collect_ListApi_Adapter;

import android.content.Context;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.ocr.Ocr_StacksAdapter;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.LatestRequestGate;
import com.kits.kowsarapp.application.base.SafeListAccess;
import com.kits.kowsarapp.application.base.SafeValueParser;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_Collect_List_Api_Activity extends AppCompatActivity {


        Intent intent;
        NotificationManager notificationManager;
        ProgressBar prog;
        GridLayoutManager gridLayoutManager;
        Dialog dialog1;
        Call<RetrofitResponse> Requset_List_call;
        Call<RetrofitResponse> Requset_ListCount_call;
        Call<RetrofitResponse> moreFactorCall;
        Call<RetrofitResponse> pathCall;
        Call<RetrofitResponse> stackCall;
        Call<RetrofitResponse> editedCountCall;
        Call<RetrofitResponse> shortageCountCall;

        Ocr_APIInterface apiInterface;
        Ocr_APIInterface secendApiInterface;
        Ocr_Collect_ListApi_Adapter ocr_collect_listApi_adapter;
        Ocr_StacksAdapter ocr_stacksAdapter;

        Handler handler;
        Handler counthandler = new Handler(Looper.getMainLooper());
        Handler lifecycleHandler = new Handler(Looper.getMainLooper());
        private final LatestRequestGate listRequestGate = new LatestRequestGate();
        CallMethod callMethod;
        Ocr_DBH ocr_dbh;

        ArrayList<Factor> factors=new ArrayList<>();
        ArrayList<Factor> visible_factors=new ArrayList<>();
        ArrayList<Factor> visible_factors_temp=new ArrayList<>();
        ArrayList<String> customerpath=new ArrayList<>();
        ArrayList<String> stacks=new ArrayList<>();
        ArrayList<Factor> factors_filter_notstart=new ArrayList<>();
        ArrayList<Factor> all_factors=new ArrayList<>();


    AppCompatImageButton btn_refresh_list;


    LinearLayoutCompat factorlist_ll_counter,factorlistActivity_combocheck;

        RecyclerView factor_list_recycler,stacks_list_recycler;

        TextView textView_Count,textView_status;

        AppCompatEditText edtsearch;

        SwitchMaterial RadioEdited,RadioShortage,Radio_start_filter;
        Spinner spinnerPath;

        String channel_id = "Kowsarmobile",channel_name = "home";
        String Row="10",state="0",StateEdited="0",StateShortage="0",TotallistCount="0",srch="",path="همه";



        private boolean loading = true;
        int pastVisiblesItems=0, visibleItemCount, totalItemCount;
        int recallcount=0,ShortageCount=0,EditedCount=0 , PageNo=0;


        private int clickCount = 0;
        private long lastClickTime = 0;
        private static final long DOUBLE_CLICK_TIME_DELTA = 500;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
            setContentView(R.layout.ocr_activity_collect_list_api);

            dialog1 = new Dialog(this);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (dialog1.getWindow() != null) {
                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
            dialog1.setContentView(R.layout.ocr_spinner_box);
            TextView repw = dialog1.findViewById(R.id.ocr_spinner_text);
            repw.setText("در حال خواندن اطلاعات");


            intent();
            Config();
            lifecycleHandler.postDelayed(() -> {
                if (!isUiActive()) return;
                try {
                    init();
                } catch (RuntimeException exception) {
                    callMethod.Log("OCR collect startup failed: "
                            + exception.getClass().getSimpleName());
                    dismissLoadingDialog();
                }
            }, 100);



        }
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        public  void intent(){
            Bundle bundle =getIntent().getExtras();
            state = bundle == null ? "0" : bundle.getString("State", "0");
            StateEdited ="0";
            StateShortage ="0";
            if("5".equals(state))
            {
                state = "0";
                StateEdited = bundle.getString("StateEdited", "0");
                StateShortage = bundle.getString("StateShortage", "0");
            }



        }

        public void Config() {
            callMethod = new CallMethod(this);
            ocr_dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));
            apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
            secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
            handler = new Handler(Looper.getMainLooper());
            prog = findViewById(R.id.ocr_collectlist_a_prog);

            Toolbar toolbar = findViewById(R.id.ocr_collectlist_a_toolbar);
            setSupportActionBar(toolbar);


            factor_list_recycler=findViewById(R.id.ocr_collectlist_a_recyclerView);
            factorlist_ll_counter=findViewById(R.id.ocr_collectlist_a_ll_counter);



            stacks_list_recycler=findViewById(R.id.ocr_collectlist_a_stacks_recyclerView);
            factorlistActivity_combocheck=findViewById(R.id.ocr_collectlist_a_combocheck);


            textView_Count=findViewById(R.id.ocr_collectlist_a_count);
            textView_status=findViewById(R.id.ocr_collectlist_a_Tvstatus);
            edtsearch = findViewById(R.id.ocr_collectlist_a_edtsearch);
            RadioEdited= findViewById(R.id.ocr_collectlist_a_edited);
            RadioShortage= findViewById(R.id.ocr_collectlist_a_shortage);
            spinnerPath= findViewById(R.id.ocr_collectlist_a_path);

            btn_refresh_list=findViewById(R.id.ocr_collectlist_a_refresh);
            Radio_start_filter= findViewById(R.id.ocr_collectlist_a_start_filter);


            if (callMethod.ReadString("StackCategory").equals("همه") && callMethod.ReadString("Category").equals("2")) {
                Row=callMethod.ReadString("RowCall");
                factorlistActivity_combocheck.setVisibility(View.VISIBLE);

            }else{
                factorlistActivity_combocheck.setVisibility(View.GONE);
            }


            factorlist_ll_counter.setOnClickListener(v -> {
                long currentClickTime = System.currentTimeMillis();

                if (lastClickTime != 0 && (currentClickTime - lastClickTime) > DOUBLE_CLICK_TIME_DELTA) {
                    clickCount = 0;
                }

                clickCount++;

                if (clickCount == 2) {
                    clearStackSelection();
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
                        clearStackSelection();
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

        public void NotificationConfig(){

            RetrofitRequset_EditeCount();
            RetrofitRequset_shortageCount();


            lifecycleHandler.postDelayed(() -> {
                if (!isUiActive()) return;
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

    @SuppressLint("NotifyDataSetChanged")
    public void CheckStackList() {

        final String TAG = "CheckStackList";
        if (ocr_stacksAdapter == null) {
            callMethod.Log("OCR stack filter ignored before categories loaded");
            return;
        }
        visible_factors_temp.clear();
        List<String> selectedItems = ocr_stacksAdapter.getSelectedItems();
        if (selectedItems.size() > 0) {

            Set<String> selectedSet = new HashSet<>(selectedItems);
            for (Factor factor : factors) {
                if (factor.getStackClass() == null ||
                        factor.getStackClass().trim().isEmpty()) {
                    continue;
                }

                List<String> factorStacks =
                        Arrays.asList(factor.getStackClass().split(","));
                Set<String> factorStackSet = new HashSet<>();

                for (String stack : factorStacks) {
                    factorStackSet.add(stack.trim());
                }

                boolean isMatch = factorStackSet.containsAll(selectedSet);
                if (isMatch) {
                    visible_factors_temp.add(factor);
                }
            }

            if (visible_factors_temp.size() > 0) {

                visible_factors = new ArrayList<>(visible_factors_temp);
                ocr_collect_listApi_adapter.notifyDataSetChanged();
                CallRecycle();
            }

        } else {
            visible_factors = new ArrayList<>(factors);
            ocr_collect_listApi_adapter.notifyDataSetChanged();
            CallRecycle();

        }

    }


        public void init(){

            stackCall = apiInterface.GetCustomerPath("GetStackCategory");
            stackCall.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (!isUiActive() || !response.isSuccessful() || response.body() == null) return;

                    stacks.clear();
                    if (response.body().getOcr_Goods() != null) {
                        for (Ocr_Good good : response.body().getOcr_Goods()) {
                            if (good != null && good.getGoodExplain4() != null) {
                                stacks.add(good.getGoodExplain4());
                            }
                        }
                    } else if (response.body().getGoods() != null) {
                        for (Good good : response.body().getGoods()) {
                            if (good != null && good.getGoodExplain4() != null) {
                                stacks.add(good.getGoodExplain4());
                            }
                        }
                    }

                    ocr_stacksAdapter = new Ocr_StacksAdapter(
                            Ocr_Collect_List_Api_Activity.this,
                            stacks
                    );
                    stacks_list_recycler.setLayoutManager(new GridLayoutManager(
                            Ocr_Collect_List_Api_Activity.this,
                            1,
                            GridLayoutManager.HORIZONTAL,
                            false
                    ));
                    stacks_list_recycler.setAdapter(ocr_stacksAdapter);

                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (!call.isCanceled()) {
                        callMethod.Log("OCR stack categories failed: "
                                + t.getClass().getSimpleName());
                    }
                }
            });








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
                                if (!isUiActive()) return;
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
                                try {
                                    ocr_stacksAdapter.Clear_selectedItems();

                                }catch (Exception e){
                                    callMethod.Log(e.getMessage());
                                }
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



            Radio_start_filter.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (all_factors.size()>0){
                    factors_filter_notstart.clear();
                    if (isChecked) {
                        for (Factor factordetail : visible_factors) {
                            if (factordetail.getAppOCRFactorExplain() != null&&factordetail.getAppOCRFactorExplain().length()>0){
                            }else{
                                factors_filter_notstart.add(factordetail);
                            }
                        }
                        visible_factors=factors_filter_notstart;

                    } else {
                        visible_factors=all_factors;

                    }
                    CallRecycle();
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

            callMethod.ReadString("ActiveDatabase");

            if (moreFactorCall != null) moreFactorCall.cancel();
            moreFactorCall = apiInterface.GetOcrFactorList(
                    "GetFactorList",
                    state,
                    srch,
                    callMethod.ReadString("StackCategory"),
                    path,
                    StateShortage,
                    StateEdited,
                    Row,
                    String.valueOf(PageNo),
                    "0",
                    callMethod.ReadString("ActiveDatabase")
            );
            moreFactorCall.enqueue(new Callback<RetrofitResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    if (!isUiActive()) return;
                    if(response.isSuccessful() && response.body() != null) {
                        prog.setVisibility(View.GONE);

                        ArrayList<Factor> factor_page = SafeListAccess.mutableNonNullCopyOrEmpty(
                                response.body().getFactors()
                        );
                        factors.addAll(factor_page);
                        visible_factors=factors;

                        if (ocr_collect_listApi_adapter != null) {
                            ocr_collect_listApi_adapter.notifyDataSetChanged();
                        }

                        CallRecycle();
                        String textView_st="تعداد "+ocr_collect_listApi_adapter.getItemCount()+" از "+TotallistCount+"";
                        textView_Count.setText(NumberFunctions.PerisanNumber(textView_st));
                        loading=true;

                    } else {
                        PageNo = Math.max(0, PageNo - 1);
                        prog.setVisibility(View.GONE);
                        loading = true;
                        callMethod.Log("OCR collect next page response was empty or HTTP failed");
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (call.isCanceled() || !isUiActive()) return;
                    PageNo = Math.max(0, PageNo - 1);
                    callMethod.showToast("فاکتور بیشتری موجود نیست");
                    prog.setVisibility(View.GONE);
                    loading = true;
                }
            });


        }

        public void CallRecycle() {

            if (!isUiActive()) return;

            ocr_collect_listApi_adapter = new Ocr_Collect_ListApi_Adapter(visible_factors,state, App.getContext());
            if (ocr_collect_listApi_adapter.getItemCount()==0){
                prog.setVisibility(View.GONE);

                callMethod.showToast("فاکتوری یافت نشد");
            }

            counthandler.postDelayed(() -> {
                if (!isUiActive() || ocr_collect_listApi_adapter == null) return;
                String textView_st="تعداد "+ocr_collect_listApi_adapter.getItemCount()+" از "+TotallistCount+"";
                textView_Count.setText(NumberFunctions.PerisanNumber(textView_st));
            }, 500);
            gridLayoutManager = new GridLayoutManager(this, 1);//grid
            factor_list_recycler.setLayoutManager(gridLayoutManager);
            factor_list_recycler.setAdapter(ocr_collect_listApi_adapter);
            factor_list_recycler.setItemAnimator(new DefaultItemAnimator());
            factor_list_recycler.scrollToPosition(pastVisiblesItems);

            if (SafeValueParser.intOrDefault(callMethod.ReadString("LastTcPrint"), 0) > 0){
                for (Factor singlefactor :factors) {
                    if(singlefactor != null
                            && callMethod.ReadString("LastTcPrint").equals(singlefactor.getAppTcPrintRef()))
                        factor_list_recycler.scrollToPosition(factors.indexOf(singlefactor));
                }

            }

            dismissLoadingDialog();


        }

        public void RetrofitRequset_Path() {


            if (pathCall != null) pathCall.cancel();
            pathCall = apiInterface.GetCustomerPath("GetCustomerPath");
            pathCall.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (!isUiActive()) return;
                    if (response.isSuccessful() && response.body() != null) {

                        recallcount=0;
                        for (Factor factor : SafeListAccess.mutableNonNullCopyOrEmpty(
                                response.body().getFactors())) {
                            if (factor != null && factor.getCustomerPath() != null) {
                                customerpath.add(factor.getCustomerPath());
                            }
                        }

                        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(App.getContext(),
                                android.R.layout.simple_spinner_item, customerpath);
                        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPath.setAdapter(spinner_adapter);

                        int selectedPosition = SafeValueParser.intOrDefault(
                                callMethod.ReadString("ConditionPosition"),
                                0
                        );
                        if (selectedPosition < 0 || selectedPosition >= customerpath.size()) {
                            selectedPosition = 0;
                            callMethod.EditString("ConditionPosition", "0");
                        }
                        spinnerPath.setSelection(selectedPosition);


                    }

                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (call.isCanceled() || !isUiActive()) return;
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

            if (Requset_List_call != null && !Requset_List_call.isCanceled()) {
                Requset_List_call.cancel();
            }
            cancelCall(moreFactorCall);
            int requestToken = listRequestGate.begin();

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
                    "0",
                    callMethod.ReadString("ActiveDatabase")
            );
            Requset_List_call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    if (!canHandleListRequest(requestToken)) return;
                    if(response.isSuccessful() && response.body() != null) {

                        prog.setVisibility(View.GONE);
                        loading = true;
                        recallcount=0;
                        factors.clear();
                        visible_factors.clear();
                        factors = SafeListAccess.mutableNonNullCopyOrEmpty(
                                response.body().getFactors()
                        );
                        visible_factors=factors;
                        all_factors = new ArrayList<>(factors);

                        callMethod.showToast("بارگیری شد");

                        if(factors.size()>0){
                            CallRecycle();

                        }else {
                            finish();
                            callMethod.showToast("فاکتوری موجود نمی باشد");
                        }

                    } else {
                        showListFailure("OCR collect list response was empty or HTTP failed");
                    }
                }
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (call.isCanceled() || !canHandleListRequest(requestToken)) return;
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
                            dismissLoadingDialog();
                            prog.setVisibility(View.GONE);
                            textView_status.setVisibility(View.VISIBLE);
                            textView_status.setText("فاکتوری یافت نشد");
                            textView_Count.setText(NumberFunctions.PerisanNumber("تعداد 0"));
                            if (ocr_collect_listApi_adapter != null) {
                                ocr_collect_listApi_adapter.notifyDataSetChanged();
                            }

                        }catch (Exception ignored){}


                    }
                }
            });

        }

        public void RetrofitRequset_ListCount() {

            if (Requset_ListCount_call != null && !Requset_ListCount_call.isCanceled()) {
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
                    "GetFactorList",
                    state,
                    srch,
                    callMethod.ReadString("StackCategory"),
                    path,
                    StateShortage,
                    StateEdited,
                    Row,
                    "0",
                    "1",
                    callMethod.ReadString("ActiveDatabase")
            );
            Requset_ListCount_call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (!isUiActive() || call != Requset_ListCount_call
                            || !response.isSuccessful() || response.body() == null) return;
                    Factor first = SafeListAccess.firstOrNull(response.body().getFactors());
                    TotallistCount = first == null || first.getTotalRow() == null
                            ? "0"
                            : first.getTotalRow();
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (!call.isCanceled()) {
                        callMethod.Log("OCR collect count failed: "
                                + t.getClass().getSimpleName());
                    }
                }
            });
        }

        public void RetrofitRequset_EditeCount() {

//        String Body_str  = "";
//
//        Body_str =callMethod.CreateJson("State", state, Body_str);
//        Body_str =callMethod.CreateJson("SearchTarget", "0", Body_str);
//        Body_str =callMethod.CreateJson("Stack",  "همه", Body_str);
//        Body_str =callMethod.CreateJson("path", "همه", Body_str);
//        Body_str =callMethod.CreateJson("HasShortage", "0", Body_str);
//        Body_str =callMethod.CreateJson("IsEdited", "1", Body_str);
//        Body_str =callMethod.CreateJson("Row", "10000", Body_str);
//        Body_str =callMethod.CreateJson("PageNo", "0", Body_str);
//        Body_str =callMethod.CreateJson("CountFlag", "1", Body_str);
//        Body_str =callMethod.CreateJson("DbName", "", Body_str);
//
//        Call<RetrofitResponse> call = apiInterface.GetOcrFactorList(callMethod.RetrofitBody(Body_str));

            if (editedCountCall != null) editedCountCall.cancel();
            editedCountCall = apiInterface.GetOcrFactorList(
                    "GetFactorList",
                    state,
                    "0",
                    "همه",
                    "همه",
                    "0",
                    "1",
                    "10000",
                    "0",
                    "1",
                    callMethod.ReadString("ActiveDatabase")
            );
            editedCountCall.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (!isUiActive() || call != editedCountCall
                            || !response.isSuccessful() || response.body() == null) return;
                    Factor first = SafeListAccess.firstOrNull(response.body().getFactors());
                    EditedCount = first == null
                            ? 0
                            : SafeValueParser.intOrDefault(first.getTotalRow(), 0);
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (!call.isCanceled()) {
                        callMethod.Log("OCR edited count failed: "
                                + t.getClass().getSimpleName());
                    }
                }
            });
        }

        public void RetrofitRequset_shortageCount() {
//
//
//        Call<RetrofitResponse> call ;
//
//        String Body_str  = "";
//
//        Body_str =callMethod.CreateJson("State", state, Body_str);
//        Body_str =callMethod.CreateJson("SearchTarget", "", Body_str);
//        Body_str =callMethod.CreateJson("Stack",  "همه", Body_str);
//        Body_str =callMethod.CreateJson("path", "همه", Body_str);
//        Body_str =callMethod.CreateJson("HasShortage", "1", Body_str);
//        Body_str =callMethod.CreateJson("IsEdited", "0", Body_str);
//        Body_str =callMethod.CreateJson("Row", "10000", Body_str);
//        Body_str =callMethod.CreateJson("PageNo", "0", Body_str);
//        Body_str =callMethod.CreateJson("CountFlag", "1", Body_str);
//        Body_str =callMethod.CreateJson("DbName", "", Body_str);
//
//        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//            call = apiInterface.GetOcrFactorList(callMethod.RetrofitBody(Body_str));
//
//        }else{
//            call = secendApiInterface.GetOcrFactorList(callMethod.RetrofitBody(Body_str));
//        }
            if (shortageCountCall != null) shortageCountCall.cancel();
            if (java.util.Objects.equals(
                    callMethod.ReadString("FactorDbName"),
                    callMethod.ReadString("DbName"))){
                shortageCountCall = apiInterface.GetOcrFactorList(
                        "GetFactorList",
                        state,
                        "",
                        "همه",
                        "همه",
                        "1",
                        "0",
                        "10000",
                        "0",
                        "1",
                        callMethod.ReadString("ActiveDatabase")
                );

            }else{
                shortageCountCall = secendApiInterface.GetOcrFactorList(
                        "GetFactorList",
                        state,
                        "",
                        "همه",
                        "همه",
                        "1",
                        "0",
                        "10000",
                        "0",
                        "1",
                        callMethod.ReadString("ActiveDatabase")

                );
            }


            shortageCountCall.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (!isUiActive() || call != shortageCountCall
                            || !response.isSuccessful() || response.body() == null) return;
                    Factor first = SafeListAccess.firstOrNull(response.body().getFactors());
                    ShortageCount = first == null
                            ? 0
                            : SafeValueParser.intOrDefault(first.getTotalRow(), 0);
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (!call.isCanceled()) {
                        callMethod.Log("OCR shortage count failed: "
                                + t.getClass().getSimpleName());
                    }
                }
            });
        }

        public void noti_Messaging(String title, String message,String flag) {

            notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel Channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(Channel);
            Intent notificationIntent = new Intent(this, Ocr_Collect_List_Api_Activity.class);
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
                    .setSmallIcon(R.drawable.img_logo_kits_jpg)
                    .setContentIntent(contentIntent);

            notificationManager.notify(1, notcompat.build());
        }

        @Override
        protected void onRestart() {
            super.onRestart();
            intent = new Intent(this, Ocr_Collect_List_Api_Activity.class);
            intent.putExtra("State", state);
            startActivity(intent);
            finish();

        }
        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
        }

        private boolean canHandleListRequest(int requestToken) {
            return isUiActive() && listRequestGate.isCurrent(requestToken);
        }

        private boolean isUiActive() {
            return !isFinishing() && !isDestroyed();
        }

        private void showListFailure(String diagnostic) {
            if (!isUiActive()) return;
            callMethod.Log(diagnostic);
            loading = true;
            dismissLoadingDialog();
            if (prog != null) prog.setVisibility(View.GONE);
            if (textView_status != null) {
                textView_status.setVisibility(View.VISIBLE);
                textView_status.setText("پاسخ فهرست فاکتورها نامعتبر است");
            }
        }

        private void dismissLoadingDialog() {
            if (dialog1 != null && dialog1.isShowing()) dialog1.dismiss();
        }

        private void cancelCall(Call<?> call) {
            if (call != null && !call.isCanceled()) call.cancel();
        }

        private void clearStackSelection() {
            if (ocr_stacksAdapter != null) {
                ocr_stacksAdapter.Clear_selectedItems();
            }
        }

        @Override
        protected void onDestroy() {
            listRequestGate.invalidate();
            cancelCall(Requset_List_call);
            cancelCall(Requset_ListCount_call);
            cancelCall(moreFactorCall);
            cancelCall(pathCall);
            cancelCall(stackCall);
            cancelCall(editedCountCall);
            cancelCall(shortageCountCall);
            if (handler != null) handler.removeCallbacksAndMessages(null);
            counthandler.removeCallbacksAndMessages(null);
            lifecycleHandler.removeCallbacksAndMessages(null);
            dismissLoadingDialog();
            super.onDestroy();
        }



    }
