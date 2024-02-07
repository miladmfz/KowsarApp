package com.kits.kowsarapp.activity.order;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.order.Order_Action;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.base.SellBroker;
import com.kits.kowsarapp.model.order.Order_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_RegistrationActivity extends AppCompatActivity {

    Order_DBH dbh;
    CallMethod callMethod;
   Order_Action action;
    ActivityRegistrationBinding binding;
    Order_APIInterface apiInterface;
    ArrayList<String> lang_array = new ArrayList<>();
    ArrayList<String> SellBroker_Names = new ArrayList<>();
    ArrayList<SellBroker> SellBrokers = new ArrayList<>();

    Integer lang_position = 0;

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
//*******************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Config();
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new Order_DBH(this, callMethod.ReadString("DatabaseName"));
        action = new Order_Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        SellBroker_Names.clear();
        if (callMethod.ReadString("LANG").equals("fa")) {
            binding.registractivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            binding.registractivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            binding.registractivity.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        Call<RetrofitResponse> call1 = apiInterface.GetSellBroker("GetSellBroker");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    SellBrokers.clear();
                    SellBrokers = response.body().getSellBrokers();
                    SellBroker sellBroker= new SellBroker();
                    sellBroker.setBrokerCode("0");
                    sellBroker.setBrokerNameWithoutType("بازاریاب تعریف نشده");
                    SellBrokers.add(sellBroker);
                    for (SellBroker sb : SellBrokers) {
                        SellBroker_Names.add(sb.getBrokerNameWithoutType());
                    }
                    brokerViewConfig();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                SellBroker sellBroker= new SellBroker();
                sellBroker.setBrokerCode("0");
                sellBroker.setBrokerNameWithoutType("ویتری تعریف نشده");
                SellBrokers.add(sellBroker);
                brokerViewConfig();

            }
        });


        lang_array.add(getString(R.string.textvalue_langdefult));
        lang_array.add(getString(R.string.textvalue_langenglish));
        lang_array.add(getString(R.string.textvalue_langpersian));
        lang_array.add(getString(R.string.textvalue_langarabic));

        switch (callMethod.ReadString("LANG")) {
            case "":
                lang_position = 0;
                break;
            case "en":
                lang_position = 1;
                break;
            case "fa":
                lang_position = 2;
                break;
            case "ar":
                lang_position = 3;
                break;
        }

    }

    public void brokerViewConfig() {
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SellBroker_Names);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.registrSpinnerbroker.setAdapter(spinner_adapter);
        int possellbroker=0;
        for (SellBroker sellBroker:SellBrokers){
            if (sellBroker.getBrokerCode().equals(dbh.ReadConfig("BrokerCode"))){
                possellbroker=SellBrokers.indexOf(sellBroker);
            }
        }

        binding.registrSpinnerbroker.setSelection(possellbroker);
        binding.registrSpinnerbroker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                dbh.SaveConfig("BrokerCode",SellBrokers.get(position).getBrokerCode());
                binding.registrBroker.setText(callMethod.NumberRegion(dbh.ReadConfig("BrokerCode")));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void init() {


        binding.registrBroker.setText(callMethod.NumberRegion(dbh.ReadConfig("BrokerCode")));
        binding.registrGroupcode.setText(callMethod.NumberRegion(dbh.ReadConfig("GroupCodeDefult")));
        binding.registrDelay.setText(callMethod.NumberRegion(callMethod.ReadString("Delay")));
        binding.registrDbname.setText(callMethod.NumberRegion(callMethod.ReadString("PersianCompanyNameUse")));
        binding.registrTitlesize.setText(callMethod.NumberRegion(callMethod.ReadString("TitleSize")));


        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lang_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.registrSpinnerlang.setAdapter(spinner_adapter);

        binding.registrSpinnerlang.setSelection(lang_position);
        binding.registrSpinnerlang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        callMethod.EditString("LANG", "");
                        break;
                    case 1:
                        callMethod.EditString("LANG", "en");
                        break;
                    case 2:
                        callMethod.EditString("LANG", "fa");
                        break;
                    case 3:
                        callMethod.EditString("LANG", "ar");
                        break;
                }

                if (!(lang_position == position)) {
                    startActivity(getIntent());
                    finish();
                }

            }









            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.registrGroupcodeRefresh.setOnClickListener(v -> {
            Dialog dialogProg = new Dialog(this);
            dialogProg.setContentView(R.layout.rep_prog);
            TextView tv_rep = dialogProg.findViewById(R.id.rep_prog_text);
            tv_rep.setText(R.string.textvalue_receiveinformation);
            dialogProg.show();
            Call<RetrofitResponse> call1 = apiInterface.kowsar_info("kowsar_info", "AppOrder_DefaultGroupCode");
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;

                        if (!response.body().getText().equals(dbh.ReadConfig("GroupCodeDefult"))) {
                            dbh.SaveConfig("GroupCodeDefult", response.body().getText());
                            binding.registrGroupcode.setText(callMethod.NumberRegion(dbh.ReadConfig("GroupCodeDefult")));
                            callMethod.showToast(getString(R.string.textvalue_resived));
                        }
                        dialogProg.dismiss();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                }
            });
        });


        binding.registrBtn.setOnClickListener(view -> {

            callMethod.EditString("TitleSize", NumberFunctions.EnglishNumber(binding.registrTitlesize.getText().toString()));
            callMethod.EditString("BodySize", NumberFunctions.EnglishNumber(binding.registrBodysize.getText().toString()));
            callMethod.EditString("Delay", NumberFunctions.EnglishNumber(binding.registrDelay.getText().toString()));

            if (!dbh.ReadConfig("BrokerCode").equals(NumberFunctions.EnglishNumber(binding.registrBroker.getText().toString()))) {
                dbh.SaveConfig("BrokerCode", NumberFunctions.EnglishNumber(binding.registrBroker.getText().toString()));
            }
            if (!dbh.ReadConfig("GroupCodeDefult").equals(NumberFunctions.EnglishNumber(binding.registrGroupcode.getText().toString()))) {
                dbh.SaveConfig("GroupCodeDefult", NumberFunctions.EnglishNumber(binding.registrGroupcode.getText().toString()));
            }
            finish();

        });


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
