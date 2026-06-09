package com.kits.kowsarapp.activity.ocr;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.ocr.Ocr_StackEnumeration_Factor_ListApi_Adapter;
import com.kits.kowsarapp.adapter.ocr.Ocr_StackEnumeration_Janamie_ListApi_Adapter;
import com.kits.kowsarapp.adapter.ocr.Ocr_StackEnumeration_Location_ListApi_Adapter;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Location;
import com.kits.kowsarapp.model.ocr.Ocr_StackEnumeration;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_StackEnumeration_Location_List_Api_Activity extends AppCompatActivity {


    Intent intent;
    ProgressBar prog;
    GridLayoutManager gridLayoutManager;
    Dialog dialog1;

    Call<RetrofitResponse> Requset_List_call;
    Call<RetrofitResponse> Requset_ListCount_call;

    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;
    Ocr_StackEnumeration_Location_ListApi_Adapter ocr_stackEnumeration_location_listApi_adapter;

    ArrayList<Ocr_Location> locations=new ArrayList<>();


    Handler handler;

    CallMethod callMethod;

    Ocr_DBH ocr_dbh;

    Button btn_refresh_list;

    RecyclerView recycler;

    TextView textView_Count,textView_status;


    LottieAnimationView animationView;
    Toolbar toolbar;
    AppCompatEditText edtsearch;

    String StackEnumerationCode="0";

    String Row="30",srch="";



    private boolean loading = true;
    int pastVisiblesItems=0, visibleItemCount, totalItemCount;
    int recallcount=0, PageNo=1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));

        setContentView(R.layout.activity_ocr_location_list_api);

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
        StackEnumerationCode = bundle.getString("StackEnumerationCode");


    }

    public void Config() {
        callMethod = new CallMethod(this);
        ocr_dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
        handler=new Handler();
        prog = findViewById(R.id.ocr_locationlist_a_prog);

        toolbar = findViewById(R.id.ocr_locationlist_a_toolbar);
        toolbar.setTitle("انتخاب مکان");
        setSupportActionBar(toolbar);



    }


    public void init(){


        recycler=findViewById(R.id.ocr_locationlist_a_recyclerView);
        textView_status=findViewById(R.id.ocr_locationlist_a_tvstatus);
        animationView=findViewById(R.id.ocr_locationlist_a_lottie);
        btn_refresh_list=findViewById(R.id.ocr_locationlist_a_refresh);
        edtsearch = findViewById(R.id.ocr_locationlist_a_edtsearch);



        btn_refresh_list.setOnClickListener(v -> {
            RetrofitRequset_List();

        });

        srch=callMethod.ReadString("Last_search_Location");


        edtsearch.setText(srch);
        edtsearch.requestFocus();
        edtsearch.selectAll();
        edtsearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtsearch.selectAll();
                return false;
            }
        });


        edtsearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }
                    @Override
                    public void afterTextChanged( Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {
                            srch = NumberFunctions.EnglishNumber(ocr_dbh.GetRegionText(editable.toString()));
                            srch=srch.replace(" ","%");
                            callMethod.EditString("Last_search_Location", srch);
                            RetrofitRequset_List();
                        }, 1000);

                    }
                });

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            MoreList();
                        }
                    }
                }
            }
        });


        RetrofitRequset_List();

    }

    public void CallRecycle() {

        ocr_stackEnumeration_location_listApi_adapter = new Ocr_StackEnumeration_Location_ListApi_Adapter(locations,StackEnumerationCode, App.getContext());


        if (ocr_stackEnumeration_location_listApi_adapter.getItemCount()==0){
            prog.setVisibility(View.GONE);

            textView_status.setText("مکانی یافت نشد");
            textView_status.setVisibility(View.VISIBLE);
            animationView.setVisibility(View.VISIBLE);
        } else {
            textView_status.setVisibility(View.GONE);
            animationView.setVisibility(View.GONE);
        }


        gridLayoutManager = new GridLayoutManager(this, 1);//grid
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setAdapter(ocr_stackEnumeration_location_listApi_adapter);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.scrollToPosition(pastVisiblesItems);

        dialog1.dismiss();
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
        textView_status.setVisibility(View.GONE);
        PageNo=1;
        pastVisiblesItems=0;
        dialog1.show();
        Requset_List_call=apiInterface.GetLocationList(
                "GetLocationList",
                StackEnumerationCode,
                srch,
                String.valueOf(PageNo),
                Row,
                "0"
        );


        Requset_List_call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                if(response.isSuccessful()) {
                    dialog1.dismiss();
                    prog.setVisibility(View.GONE);
                    loading = true;
                    assert response.body() != null;
                    locations=response.body().getLocations();


                    CallRecycle();

                }
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {


                loading = true;

                try {
                    locations.clear();
                    dialog1.dismiss();
                    prog.setVisibility(View.GONE);
                    animationView.setVisibility(View.VISIBLE);
                    textView_status.setVisibility(View.VISIBLE);
                    textView_status.setText("مکانی یافت نشد");
                    ocr_stackEnumeration_location_listApi_adapter.notifyDataSetChanged();

                }catch (Exception ignored){}

            }
        });

    }

    public void MoreList() {

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



        prog.setVisibility(View.VISIBLE);

        Call<RetrofitResponse> call;


        callMethod.ReadString("ActiveDatabase");

        call=apiInterface.GetLocationList(
                "GetLocationList",
                StackEnumerationCode,
                srch,
                String.valueOf(PageNo),
                Row,
                "0"
        );

        call.enqueue(new Callback<RetrofitResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                if(response.isSuccessful()) {
                    prog.setVisibility(View.GONE);

                    assert response.body() != null;
                    ArrayList<Ocr_Location> ocr_locations = response.body().getLocations();
                    locations.addAll(ocr_locations);

                    ocr_stackEnumeration_location_listApi_adapter.notifyDataSetChanged();

                    CallRecycle();

                    loading=true;

                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {


                PageNo--;
                callMethod.showToast("مکان بیشتری موجود نیست");
                prog.setVisibility(View.GONE);
                loading = true;
            }
        });
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        intent = new Intent(this, Ocr_StackEnumeration_Location_List_Api_Activity.class);
        intent.putExtra("StackEnumerationCode", StackEnumerationCode);


        startActivity(intent);
        finish();

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }



}