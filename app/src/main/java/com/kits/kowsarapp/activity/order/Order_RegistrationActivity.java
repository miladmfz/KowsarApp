package com.kits.kowsarapp.activity.order;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.order.Order_ThemeSpinnerAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.order.Order_Action;
import com.kits.kowsarapp.databinding.OrderActivityRegistrationBinding;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.PosDriver;
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
    private static final String PREFS_NAME = "ThemePrefs";
    private static final String THEME_KEY = "selectedTheme";
    private int selectedTheme;

    Order_DBH order_dbh;
    CallMethod callMethod;
    Order_Action order_action;
    OrderActivityRegistrationBinding binding;
    Order_APIInterface apiInterface;
    ArrayList<String> lang_array = new ArrayList<>();
    ArrayList<String> SellBroker_Names = new ArrayList<>();
    ArrayList<SellBroker> SellBrokers = new ArrayList<>();
    ArrayList<PosDriver> posDrivers = new ArrayList<>();
    ArrayList<String> pos_list=new ArrayList<>();

    Integer lang_position = 0;

    private static final String[] themeNames = {
            "پیش‌فرض",
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
            {Color.parseColor("#FFFFFF"), Color.parseColor("#E0E0E0"), Color.parseColor("#F5F5F5")}, // Default
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
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.DefaultTheme));

        binding = OrderActivityRegistrationBinding.inflate(getLayoutInflater());
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
        order_dbh = new Order_DBH(this, callMethod.ReadString("DatabaseName"));
        order_action = new Order_Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        SellBroker_Names.clear();
        if (callMethod.ReadString("LANG").equals("fa")) {
            binding.orderRegistrActivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            binding.orderRegistrActivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            binding.orderRegistrActivity.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
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
        binding.ordRegistrASpinnerbroker.setAdapter(spinner_adapter);
        int possellbroker=0;
        for (SellBroker sellBroker:SellBrokers){
            if (sellBroker.getBrokerCode().equals(order_dbh.ReadConfig("BrokerCode"))){
                possellbroker=SellBrokers.indexOf(sellBroker);
            }
        }

        binding.ordRegistrASpinnerbroker.setSelection(possellbroker);
        binding.ordRegistrASpinnerbroker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                order_dbh.SaveConfig("BrokerCode",SellBrokers.get(position).getBrokerCode());
                binding.ordRegistrABroker.setText(callMethod.NumberRegion(order_dbh.ReadConfig("BrokerCode")));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void init() {


        binding.ordRegistrABroker.setText(callMethod.NumberRegion(order_dbh.ReadConfig("BrokerCode")));
        binding.ordRegistrAGroupcode.setText(callMethod.NumberRegion(order_dbh.ReadConfig("GroupCodeDefult")));
        binding.ordRegistrADelay.setText(callMethod.NumberRegion(callMethod.ReadString("Delay")));
        binding.ordRegistrADbname.setText(callMethod.NumberRegion(callMethod.ReadString("PersianCompanyNameUse")));
        binding.ordRegistrATitlesize.setText(callMethod.NumberRegion(callMethod.ReadString("TitleSize")));
        binding.ordRegistrAActivereserv.setChecked(callMethod.ReadBoolan("ReserveActive"));
        binding.ordRegistrACanfreetable.setChecked(callMethod.ReadBoolan("CanFreeTable"));

        binding.ordRegistrAPozcode.setText(callMethod.NumberRegion(callMethod.ReadString("PosCode")));
        binding.ordRegistrAPozname.setText(callMethod.NumberRegion(callMethod.ReadString("PosName")));
        binding.ordRegistrAPospayment.setChecked(callMethod.ReadBoolan("PosPayment"));
        binding.ordRegistrAPaymentwithdevice.setChecked(callMethod.ReadBoolan("PaymentWithDevice"));


        binding.ordRegistrAPaymentwithdevice.setOnCheckedChangeListener((compoundButton, b) -> {

            if (callMethod.ReadBoolan("PaymentWithDevice")) {

                callMethod.EditBoolan("PaymentWithDevice", false);
                callMethod.showToast("خیر");

            } else {
                callMethod.EditBoolan("PaymentWithDevice", true);
                callMethod.showToast("بله");
            }
        });


        binding.ordRegistrAPospayment.setOnCheckedChangeListener((compoundButton, b) -> {

            if ((callMethod.ReadString("PosCode").equals("0"))||(callMethod.ReadString("PosName").equals(""))){
                binding.ordRegistrAPospayment.setChecked(false);
                callMethod.showToast("دستگاه پوز امتخاب نشده");
            }else {
                if (callMethod.ReadBoolan("PosPayment")) {

                    callMethod.EditBoolan("PosPayment", false);
                    callMethod.showToast("خیر");

                } else {

                    callMethod.EditBoolan("PosPayment", true);
                    callMethod.showToast("بله");
                }
            }
        });
        binding.ordRegistrACanfreetable.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("CanFreeTable")) {
                callMethod.EditBoolan("CanFreeTable", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("CanFreeTable", true);
                callMethod.showToast("بله");
            }
        });

        binding.ordRegistrAActivereserv.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ReserveActive")) {
                callMethod.EditBoolan("ReserveActive", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ReserveActive", true);
                callMethod.showToast("بله");
            }
        });

        binding.ordRegistrASpinnerpoz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    callMethod.EditString("PosName", "");
                    callMethod.EditString("PosCode", "0");
                }else{
                    for ( PosDriver posDriver: posDrivers) {
                        if(posDriver.getPosName().equals(pos_list.get(position))){
                            callMethod.EditString("PosName",posDriver.getPosName());
                            callMethod.EditString("PosCode",posDriver.getPosDriverCode());
                        }
                    }
                }
                binding.ordRegistrAPozcode.setText(callMethod.ReadString("PosName"));
                binding.ordRegistrAPozname.setText(callMethod.ReadString("PosCode"));

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Call<RetrofitResponse> call;

        call=apiInterface.OrderGetPosDriver("OrderGetPosDriver");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                pos_list.add("بدون پوز");

                if(response.isSuccessful()) {
                    assert response.body() != null;
                    posDrivers=response.body().getPosDrivers();
                    for ( PosDriver posDriver: posDrivers) {
                        pos_list.add(posDriver.getPosName());
                    }
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(Order_RegistrationActivity.this,
                            android.R.layout.simple_spinner_item, pos_list);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.ordRegistrASpinnerpoz.setAdapter(spinner_adapter);
                    int targetIndex = 0;
                    for (int i = 0; i < pos_list.size(); i++) {
                        if (pos_list.get(i).equals(callMethod.ReadString("PosName"))) {
                            targetIndex = i;
                            break;
                        }
                    }
                    binding.ordRegistrASpinnerpoz.setSelection(targetIndex); // Set selection baraye item ke matnash "همه" ast
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Log.e("kowsar_onFailure",t.getMessage());
            }
        });

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lang_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.ordRegistrASpinnerlang.setAdapter(spinner_adapter);

        binding.ordRegistrASpinnerlang.setSelection(lang_position);
        binding.ordRegistrASpinnerlang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        binding.ordRegistrADefultsetting.setOnClickListener(v -> {
            Dialog dialogProg = new Dialog(this);
            dialogProg.setContentView(R.layout.order_spinner_box);
            TextView tv_rep = dialogProg.findViewById(R.id.ord_spinner_text);
            tv_rep.setText(R.string.textvalue_receiveinformation);
            dialogProg.show();
            //Call<RetrofitResponse> call1 = apiInterface.DbSetupvalue("DbSetupvalue", "AppOrder_DefaultGroupCode");
            Call<RetrofitResponse> call1 = apiInterface.kowsar_info("kowsar_info", "AppOrder_DefaultGroupCode");

            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;

                        if (!response.body().getText().equals(order_dbh.ReadConfig("GroupCodeDefult"))) {
                            order_dbh.SaveConfig("GroupCodeDefult", response.body().getText());
                            binding.ordRegistrAGroupcode.setText(callMethod.NumberRegion(order_dbh.ReadConfig("GroupCodeDefult")));

                            Call<RetrofitResponse> call2 = apiInterface.kowsar_info("kowsar_info", "AppOrder_CanFree_FromTablet");
                            call2.enqueue(new Callback<RetrofitResponse>() {
                                @Override
                                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;

                                        Log.e("kowsar",response.body().getText());
                                        callMethod.EditBoolan("CanFreeTable", !response.body().getText().equals("0"));
                                        binding.ordRegistrACanfreetable.setChecked(callMethod.ReadBoolan("CanFreeTable"));

                                        Call<RetrofitResponse> call2 = apiInterface.kowsar_info("kowsar_info", "MaxShopCashDiscount");
                                        call2.enqueue(new Callback<RetrofitResponse>() {
                                            @Override
                                            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                                                if (response.isSuccessful()) {
                                                    assert response.body() != null;
                                                    Log.e("kowsar",response.body().getText());
                                                    callMethod.EditString("MaxSellOff", response.body().getText());
                                                    binding.ordRegistrAMaxselloff.setText(callMethod.ReadString("MaxSellOff"));
                                                    callMethod.showToast(getString(R.string.textvalue_resived));
                                                    dialogProg.dismiss();
                                                }
                                            }
                                            @Override
                                            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                                }
                            });                        }
                        dialogProg.dismiss();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                }
            });
        });


        binding.ordRegistrABtn.setOnClickListener(view -> {

            callMethod.EditString("TitleSize", NumberFunctions.EnglishNumber(binding.ordRegistrATitlesize.getText().toString()));
            callMethod.EditString("BodySize", NumberFunctions.EnglishNumber(binding.ordRegistrABodysize.getText().toString()));
            callMethod.EditString("Delay", NumberFunctions.EnglishNumber(binding.ordRegistrADelay.getText().toString()));

            if (!order_dbh.ReadConfig("BrokerCode").equals(NumberFunctions.EnglishNumber(binding.ordRegistrABroker.getText().toString()))) {
                order_dbh.SaveConfig("BrokerCode", NumberFunctions.EnglishNumber(binding.ordRegistrABroker.getText().toString()));
            }
            if (!order_dbh.ReadConfig("GroupCodeDefult").equals(NumberFunctions.EnglishNumber(binding.ordRegistrAGroupcode.getText().toString()))) {
                order_dbh.SaveConfig("GroupCodeDefult", NumberFunctions.EnglishNumber(binding.ordRegistrAGroupcode.getText().toString()));
            }
            finish();

        });



        Spinner themeSpinner = findViewById(R.id.themeSpinner);

        Button applyButton = findViewById(R.id.applyButton);

        // Set custom adapter
        Order_ThemeSpinnerAdapter adapter = new Order_ThemeSpinnerAdapter(this, themeNames, themeColors);
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
