package com.kits.kowsarapp.activity.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;
import com.kits.kowsarapp.adapter.order.Order_InternetConnection;
import com.kits.kowsarapp.adapter.order.Order_ObjectTypeAdapter;
import com.kits.kowsarapp.adapter.order.Order_RstMizAdapter;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.ObjectType;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.order.Order_BasketInfo;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_TableActivity extends AppCompatActivity {

    public String State = "0";
    public String EditTable = "0";
    public String mizType = "";
    CallMethod callMethod;
    Order_APIInterface apiInterface;
    Intent intent;
    RecyclerView recyclerView_object, recyclerView_Table;
    ArrayList<Order_BasketInfo> basketInfos = new ArrayList<>();
    ArrayList<ObjectType> objectTypes = new ArrayList<>();
    Order_RstMizAdapter adapter;
    ArrayList<String> InfoState_array = new ArrayList<>();
    Spinner spinner;

    LinearLayout init_ll;
    LottieAnimationView progressBar;
    LottieAnimationView img_lottiestatus;
    TextView tv_lottiestatus;

    @SuppressLint("ObsoleteSdkInt")
    public static ContextWrapper changeLanguage(Context context, String lang) {

        Locale currentLocal;
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocal = conf.getLocales().get(0);
        } else {
            currentLocal = conf.locale;
        }

        if (!lang.equals("") && !currentLocal.getLanguage().equals(lang)) {
            Locale newLocal = new Locale(lang);
            Locale.setDefault(newLocal);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                conf.setLocale(newLocal);
            } else {
                conf.locale = newLocal;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context = context.createConfigurationContext(conf);
            } else {
                res.updateConfiguration(conf, context.getResources().getDisplayMetrics());
            }


        }

        return new ContextWrapper(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.DefaultTheme));

        setContentView(R.layout.order_activity_table);
        intent();
        Config();

        Order_InternetConnection ic = new Order_InternetConnection(this);
        if (!ic.has()) {
            intent = new Intent(this, Base_SplashActivity.class);
            startActivity(intent);
            finish();
        } else {
            init();

        }
    }

    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        State = data.getString("State");
        EditTable = data.getString("EditTable");
    }

    public void Config() {

        callMethod = new CallMethod(App.getContext());
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);

        CoordinatorLayout ll_activity = findViewById(R.id.order_table_activity);
        if (callMethod.ReadString("LANG").equals("fa")) {
            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }


        spinner = findViewById(R.id.ord_table_a_spinner);

        init_ll = findViewById(R.id.ord_table_a_miztype_ll);

        progressBar = findViewById(R.id.ord_table_a_prog);
        img_lottiestatus = findViewById(R.id.ord_table_a_lottie);
        tv_lottiestatus = findViewById(R.id.ord_table_a_tvstatus);

        recyclerView_Table = findViewById(R.id.ord_table_a_mizlist_recy);
        recyclerView_object = findViewById(R.id.ord_table_a_miztype_recy);

        InfoState_array.add(getString(R.string.infostatearray_0));
        InfoState_array.add(getString(R.string.infostatearray_1));
        InfoState_array.add(getString(R.string.infostatearray_2));
        InfoState_array.add(getString(R.string.infostatearray_3));
        InfoState_array.add(getString(R.string.infostatearray_4));

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, InfoState_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setSelection(Integer.parseInt(State));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                State = String.valueOf(position);
                CallTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void CallSpinner() {
        if (EditTable.equals("1")) {
            State = "3";
            spinner.setVisibility(View.INVISIBLE);
        }
        if (String.valueOf(spinner.getSelectedItemPosition()).equals(State)) {
            CallTable();
        } else {
            spinner.setSelection(Integer.parseInt(State));
        }

    }

    public void CallTable() {

        progressBar.setVisibility(View.VISIBLE);
        img_lottiestatus.setVisibility(View.GONE);
        tv_lottiestatus.setVisibility(View.GONE);



        String Body_str  = "";

        Body_str =callMethod.CreateJson("MizType", callMethod.ReadString("ObjectType"), Body_str);
        Body_str =callMethod.CreateJson("InfoState", State, Body_str);


       // Call<RetrofitResponse> call1 = apiInterface.OrderMizList(callMethod.RetrofitBody(Body_str));
        Call<RetrofitResponse> call1 = apiInterface.OrderMizList("OrderMizList", State,callMethod.ReadString("ObjectType"));


        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().getBasketInfos().equals(basketInfos)) {
                        basketInfos.clear();
                        basketInfos = response.body().getBasketInfos();
                        callrecycler();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                basketInfos.clear();
                callrecycler();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        CallTable();
        super.onWindowFocusChanged(hasFocus);
    }

    public void init() {

        Call<RetrofitResponse> call1 = apiInterface.GetObjectTypeFromDbSetup("GetObjectTypeFromDbSetup", "RstMiz_MizType");
        callMethod.Log(call1.request().url().toString());
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                callMethod.Log("size = "+response.body().getObjectTypes().size());
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    objectTypes.clear();
                    ObjectType ob = new ObjectType();
                    ob.setTid("0");
                    ob.setaType("");
                    ob.setIsDefault("0");
                    objectTypes = response.body().getObjectTypes();
                    objectTypes.add(0, ob);

                    CallSpinner();


                    Order_ObjectTypeAdapter objectadapter = new Order_ObjectTypeAdapter(objectTypes, Order_TableActivity.this);
                    recyclerView_object.setLayoutManager(new GridLayoutManager(Order_TableActivity.this, 1));
                    recyclerView_object.setAdapter(objectadapter);
                    recyclerView_object.setItemAnimator(new DefaultItemAnimator());

                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                callMethod.Log("notsize = "+t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void callrecycler() {
        progressBar.setVisibility(View.GONE);
        adapter = new Order_RstMizAdapter(basketInfos, EditTable, Order_TableActivity.this);

        if (adapter.getItemCount() == 0) {
            tv_lottiestatus.setText(R.string.textvalue_notfound);
            img_lottiestatus.setVisibility(View.VISIBLE);
            tv_lottiestatus.setVisibility(View.VISIBLE);
        } else {
            img_lottiestatus.setVisibility(View.GONE);
            tv_lottiestatus.setVisibility(View.GONE);
        }
        recyclerView_Table.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView_Table.setAdapter(adapter);
        recyclerView_Table.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("profile", Context.MODE_PRIVATE);
        String currentLang = preferences.getString("LANG", "");
        if (currentLang.equals("")) {
            currentLang = getAppLanguage();
        }
        Context context = changeLanguage(newBase, currentLang);
        super.attachBaseContext(context);
    }

    public String getAppLanguage() {
        return Locale.getDefault().getLanguage();
    }

}