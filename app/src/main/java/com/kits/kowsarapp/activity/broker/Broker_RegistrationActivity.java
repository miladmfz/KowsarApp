package com.kits.kowsarapp.activity.broker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;
import com.kits.kowsarapp.adapter.base.Base_ThemeSpinnerAdapter;
import com.kits.kowsarapp.application.base.Base_Action;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.application.broker.Broker_Replication;
import com.kits.kowsarapp.databinding.BrokerActivityRegistrBinding;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.SellBroker;
import com.kits.kowsarapp.model.base.UserInfo;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Broker_RegistrationActivity extends AppCompatActivity {

    private static final String THEME_KEY = "selectedTheme";
    private int selectedTheme;

    Broker_DBH broker_dbh;
    CallMethod callMethod;
    Broker_Action broker_action;
    Base_Action base_action;

    Broker_Replication broker_replication;
    Intent intent;
    BrokerActivityRegistrBinding binding;
    Broker_APIInterface broker_apiInterface;
    ArrayList<String> SellBroker_Names = new ArrayList<>();
    ArrayList<SellBroker> SellBrokers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        binding = BrokerActivityRegistrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Config();
        try {
            init();
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }


    }
//*******************************************************

    public void Config() {

        callMethod = new CallMethod(this);
        broker_dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));
        broker_replication = new Broker_Replication(this);
        broker_action = new Broker_Action(this);
        base_action = new Base_Action(this);

        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);

        Call<RetrofitResponse> call1 = broker_apiInterface.GetSellBroker("GetSellBroker");
        callMethod.Log("kowsarapp ="+call1.request().toString());

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
                SellBrokers.clear();

                callMethod.Log("kowsarapp ="+t.getMessage());
                SellBroker sellBroker= new SellBroker();
                sellBroker.setBrokerCode("0");
                sellBroker.setBrokerNameWithoutType("بازاریاب تعریف نشده");
                SellBrokers.add(sellBroker);
                brokerViewConfig();

            }
        });

    }

    public void brokerViewConfig() {
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SellBroker_Names);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.bRegisterASpinnerbroker.setAdapter(spinner_adapter);

        int possellbroker=0;
        for (SellBroker sellBroker:SellBrokers){
            if (sellBroker.getBrokerCode().equals(broker_dbh.ReadConfig("BrokerCode"))){
                possellbroker=SellBrokers.indexOf(sellBroker);
            }
        }


        binding.bRegisterASpinnerbroker.setSelection(possellbroker);
        binding.bRegisterASpinnerbroker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                binding.bRegisterABroker.setText(NumberFunctions.PerisanNumber(SellBrokers.get(position).getBrokerCode()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    public void init() {

        themeconfig();

        binding.bRegisterABroker.setText(NumberFunctions.PerisanNumber(broker_dbh.ReadConfig("BrokerCode")));
        binding.bRegisterAGrid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        binding.bRegisterADelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        binding.bRegisterATitlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        binding.bRegisterABodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));
        binding.bRegisterAPhonenumber.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PhoneNumber")));
        binding.bRegisterADbname.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PersianCompanyNameUse")));

        binding.bRegisterABroker.setOnClickListener(v -> {
            binding.bRegisterABroker.selectAll();
        });

        binding.bRegisterATotaldelete.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage("آیا اطلاعات نرم افزار به صورت کلی حذف شود؟");

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                File databasedir = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse"));
                deleteRecursive(databasedir);

            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                // code to handle negative button click
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });

        binding.bRegisterABasedelete.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage("آیا نیازمند بارگیری مجدد اطلاعات هستید؟");

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {

                File currentFile = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse") + "/KowsarDb.sqlite");
                File newFile = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse") + "/tempDb");

                if (rename(currentFile, newFile)) {
                    callMethod.EditString("PersianCompanyNameUse", "");
                    callMethod.EditString("EnglishCompanyNameUse", "");
                    callMethod.EditString("ServerURLUse", "");
                    callMethod.EditString("DatabaseName", "");
                    intent = new Intent(this, Base_SplashActivity.class);
                    finish();
                    startActivity(intent);
                }
            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                // code to handle negative button click
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });

        binding.bRegisterAReplicationcolumn.setOnClickListener(v -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage("آیا تنظیمات پیش فرض مجددا گرفته شود ؟");

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                broker_dbh.deleteColumn();
                broker_replication.BrokerStack();
                broker_dbh.DatabaseCreate();
                base_action.app_info();
                broker_replication.DoingReplicate();


            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                // code to handle negative button click
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });


        binding.bRegisterASelloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);
        binding.bRegisterAAutorep.setChecked(callMethod.ReadBoolan("AutoReplication"));
        binding.bRegisterACustomercredit.setChecked(callMethod.ReadBoolan("ShowCustomerCredit"));
        binding.bRegisterAKeyboardrunnable.setChecked(callMethod.ReadBoolan("keyboardRunnable"));
        binding.bRegisterAKowsarservice.setChecked(callMethod.ReadBoolan("kowsarService"));
        binding.bRegisterAShowdetail.setChecked(callMethod.ReadBoolan("ShowDetail"));
        binding.bRegisterALineview.setChecked(callMethod.ReadBoolan("LineView"));
        binding.bRegisterACanuseinactive.setChecked(callMethod.ReadBoolan("CanUseInactive"));

        binding.bRegisterAShowsearchbandactive.setChecked(callMethod.ReadBoolan("ShowSearchBand"));
        binding.bRegisterAShowgoodimage.setChecked(callMethod.ReadBoolan("ShowGoodImage"));
        binding.bRegisterAShowgoodbuybtn.setChecked(callMethod.ReadBoolan("ShowGoodBuyBtn"));


        binding.bRegisterAShowgoodimage.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowGoodImage")) {
                callMethod.EditBoolan("ShowGoodImage", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowGoodImage", true);
                callMethod.showToast("بله");
            }
        });

        binding.bRegisterAShowgoodbuybtn.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowGoodBuyBtn")) {
                callMethod.EditBoolan("ShowGoodBuyBtn", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowGoodBuyBtn", true);
                callMethod.showToast("بله");
            }
        });

        binding.bRegisterAShowsearchbandactive.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowSearchBand")) {
                callMethod.EditBoolan("ShowSearchBand", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowSearchBand", true);
                callMethod.showToast("بله");
            }
        });


        binding.bRegisterACanuseinactive.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("CanUseInactive")) {
                callMethod.EditBoolan("CanUseInactive", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("CanUseInactive", true);
                callMethod.showToast("بله");
            }
        });


        binding.bRegisterAShowdetail.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowDetail")) {
                callMethod.EditBoolan("ShowDetail", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowDetail", true);
                callMethod.showToast("بله");
            }
        });



        binding.bRegisterALineview.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("LineView")) {
                callMethod.EditBoolan("LineView", false);
                callMethod.showToast("خیر");
                binding.bRegisterAGrid.setText(NumberFunctions.PerisanNumber("3"));
            } else {
                callMethod.EditBoolan("LineView", true);
                callMethod.showToast("بله");
                binding.bRegisterAGrid.setText(NumberFunctions.PerisanNumber("1"));
            }
        });


        binding.bRegisterACustomercredit.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowCustomerCredit")) {
                callMethod.EditBoolan("ShowCustomerCredit", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowCustomerCredit", true);
                callMethod.showToast("بله");
            }
        });


        binding.bRegisterASelloff.setOnCheckedChangeListener((compoundButton, b) -> {
            if (Integer.parseInt(callMethod.ReadString("SellOff")) == 0) {
                callMethod.EditString("SellOff", "1");
                callMethod.showToast("بله");
            } else {
                callMethod.EditString("SellOff", "0");
                callMethod.showToast("خیر");
            }
        });


        binding.bRegisterAAutorep.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("AutoReplication")) {
                callMethod.EditBoolan("AutoReplication", false);
                callMethod.showToast("خیر");

            } else {
                callMethod.EditBoolan("AutoReplication", true);
                callMethod.showToast("بله");
            }
        });



        binding.bRegisterAKeyboardrunnable.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("keyboardRunnable")) {
                callMethod.EditBoolan("keyboardRunnable", false);
                callMethod.showToast("خیر");

            } else {
                callMethod.EditBoolan("keyboardRunnable", true);
                callMethod.showToast("بله");
            }
        });

        binding.bRegisterAKowsarservice.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("kowsarService")) {
                callMethod.EditBoolan("kowsarService", false);
                callMethod.showToast("خیر");

            } else {
                callMethod.EditBoolan("kowsarService", true);
                callMethod.showToast("بله");
            }
        });


        binding.bRegisterABtn.setOnClickListener(view -> {
            callMethod.EditString("Grid", NumberFunctions.EnglishNumber(binding.bRegisterAGrid.getText().toString()));
            callMethod.EditString("Delay", NumberFunctions.EnglishNumber(binding.bRegisterADelay.getText().toString()));
            callMethod.EditString("TitleSize", NumberFunctions.EnglishNumber(binding.bRegisterATitlesize.getText().toString()));
            callMethod.EditString("BodySize", NumberFunctions.EnglishNumber(binding.bRegisterABodysize.getText().toString()));
            callMethod.EditString("PhoneNumber", NumberFunctions.EnglishNumber(binding.bRegisterAPhonenumber.getText().toString()));

            if(!broker_dbh.ReadConfig("BrokerCode").equals(NumberFunctions.EnglishNumber(binding.bRegisterABroker.getText().toString()))){
                Registration();
            }else {
                finish();
            }

        });


    }


    public void Registration() {



        UserInfo UserInfoNew = new UserInfo();
        UserInfoNew.setBrokerCode(NumberFunctions.EnglishNumber(binding.bRegisterABroker.getText().toString()));
        broker_dbh.SaveConfig("BrokerCode",NumberFunctions.EnglishNumber(binding.bRegisterABroker.getText().toString()));
        broker_dbh.SavePersonalInfo(UserInfoNew);
        broker_dbh.DatabaseCreate();

        broker_replication.BrokerStack();
        base_action.app_info();
        broker_replication.DoingReplicate();

    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
        callMethod.EditString("PersianCompanyNameUse", "");
        callMethod.EditString("EnglishCompanyNameUse", "");
        callMethod.EditString("ServerURLUse", "");
        callMethod.EditString("DatabaseName", "");
        callMethod.EditString("ActivationCode", "");
        callMethod.EditString("AppType", "");

        intent = new Intent(this, Base_SplashActivity.class);
        finish();
        startActivity(intent);

    }

    private boolean rename(File from, File to) {
        return Objects.requireNonNull(from.getParentFile()).exists() && from.exists() && from.renameTo(to);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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


        Spinner themeSpinner = findViewById(R.id.broker_themeSpinner);

        Button applyButton = findViewById(R.id.broker_applyButton);

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
