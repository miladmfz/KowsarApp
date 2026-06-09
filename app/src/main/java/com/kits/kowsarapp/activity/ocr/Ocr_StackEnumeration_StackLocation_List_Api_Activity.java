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
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.kits.kowsarapp.adapter.ocr.Ocr_StackEnumeration_StackLocation_ListApi_Adapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_StackEnumeration_StackLocation_List_Api_Activity extends AppCompatActivity {


    Intent intent;
    ProgressBar prog;
    GridLayoutManager gridLayoutManager;
    Dialog dialog1;

    Call<RetrofitResponse> Requset_List_call;
    Call<RetrofitResponse> Requset_ListCount_call;

    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;
    Ocr_StackEnumeration_StackLocation_ListApi_Adapter ocr_stackEnumeration_stackLocation_listApi_adapter;

    ArrayList<Ocr_Good> ocr_goods=new ArrayList<>();


    Handler handler;

    CallMethod callMethod;

    Ocr_DBH ocr_dbh;

    Button btn_refresh_list;
    Button btn_show_locationstack;

    RecyclerView recycler;

    TextView textView_Count,textView_status;
    LottieAnimationView animationView;


    Toolbar toolbar;
    AppCompatEditText edtsearch;

    String StackEnumerationCode="0";
    String LocationCode="0";
    String LocationTitle="0";

    String Row="10",state="0",srch="";



    private boolean loading = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));

        setContentView(R.layout.activity_ocr_stacklocation_list_api);

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
        StackEnumerationCode=bundle.getString("StackEnumerationCode");
        LocationCode=bundle.getString("LocationCode");
        LocationTitle=bundle.getString("LocationTitle");

    }

    public void Config() {
        callMethod = new CallMethod(this);
        ocr_dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
        handler=new Handler();
        prog = findViewById(R.id.ocr_stacklocation_a_prog);

        toolbar = findViewById(R.id.ocr_stacklocation_a_toolbar);
        toolbar.setTitle("انتخاب کالا");

        setSupportActionBar(toolbar);

    }


    public void init(){


        recycler=findViewById(R.id.ocr_stacklocation_a_recyclerView);
        textView_status=findViewById(R.id.ocr_stacklocation_a_tvstatus);
        animationView=findViewById(R.id.ocr_stacklocation_a_lottie);
        btn_refresh_list=findViewById(R.id.ocr_stacklocation_a_refresh);
        edtsearch = findViewById(R.id.ocr_stacklocation_a_edtsearch);


        btn_refresh_list.setOnClickListener(v -> {
            RetrofitRequset_List();

        });

        srch=callMethod.ReadString("Last_search_LocationStack");

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
                            callMethod.EditString("Last_search_LocationStack", srch);
                            RetrofitRequset_List();
                        }, 1000);

                    }
                });

        RetrofitRequset_List();

    }

    public void CallRecycle() {


        ocr_stackEnumeration_stackLocation_listApi_adapter = new Ocr_StackEnumeration_StackLocation_ListApi_Adapter(ocr_goods,StackEnumerationCode,LocationCode, this);
        if (ocr_stackEnumeration_stackLocation_listApi_adapter.getItemCount()==0){
            prog.setVisibility(View.GONE);


            textView_status.setText("شمارشی یافت نشد");
            textView_status.setVisibility(View.VISIBLE);
            animationView.setVisibility(View.VISIBLE);
        } else {

            textView_status.setVisibility(View.GONE);
            animationView.setVisibility(View.GONE);
        }
        gridLayoutManager = new GridLayoutManager(this, 1);//grid
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setAdapter(ocr_stackEnumeration_stackLocation_listApi_adapter);
        recycler.setItemAnimator(new DefaultItemAnimator());

        dialog1.dismiss();
    }


    public void RetrofitRequset_List() {

        if (Requset_List_call != null && !Requset_List_call.isExecuted() && !Requset_List_call.isCanceled()) {
            Requset_List_call.cancel();
        }

/*
        PageNo=0;
        pastVisiblesItems=0;
        RetrofitRequset_ListCount();



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
        dialog1.show();

        Requset_List_call=apiInterface.GetEnum_Rows(
                "GetEnum_Rows",
                StackEnumerationCode,
                LocationCode,
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
                    ocr_goods=response.body().getOcr_Goods();

                    CallRecycle();

                }
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {


                dialog1.dismiss();
                try {
                    ocr_goods.clear();
                    dialog1.dismiss();
                    prog.setVisibility(View.GONE);
                    animationView.setVisibility(View.VISIBLE);
                    textView_status.setVisibility(View.VISIBLE);
                    textView_status.setText("شمارشی یافت نشد");
                    ocr_stackEnumeration_stackLocation_listApi_adapter.notifyDataSetChanged();

                }catch (Exception ignored){}

            }
        });

    }



    @Override
    protected void onRestart() {
        super.onRestart();
        intent = new Intent(this, Ocr_StackEnumeration_StackLocation_List_Api_Activity.class);
        intent.putExtra("StackEnumerationCode", StackEnumerationCode);
        intent.putExtra("LocationCode", LocationCode);
        intent.putExtra("LocationTitle", LocationTitle);

        startActivity(intent);
        finish();

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }



}