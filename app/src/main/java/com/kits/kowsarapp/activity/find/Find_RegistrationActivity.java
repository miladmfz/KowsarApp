package com.kits.kowsarapp.activity.find;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;

import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.find.Find_Replication;

import com.kits.kowsarapp.databinding.FindActivityRegistrationBinding;
import com.kits.kowsarapp.model.base.Base_DBH;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.base.SellBroker;
import com.kits.kowsarapp.model.base.UserInfo;
import com.kits.kowsarapp.model.find.Find_DBH;

import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.find.Find_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Find_RegistrationActivity extends AppCompatActivity {

    Find_DBH dbh;
    Base_DBH dbhbase;
    CallMethod callMethod;
    Find_Replication replication;
    Intent intent;
    FindActivityRegistrationBinding binding;

    Find_APIInterface apiInterface;
    ArrayList<String> SellBroker_Names = new ArrayList<>();
    ArrayList<SellBroker> SellBrokers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FindActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Config();
        init();

    }
//*******************************************************

    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new Find_DBH(this, callMethod.ReadString("DatabaseName"));
        replication = new Find_Replication(this);

        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Find_APIInterface.class);
        dbhbase = new Base_DBH(App.getContext(), "/data/data/com.kits.kowsarapp/databases/KowsarDb.sqlite");

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
                sellBroker.setBrokerNameWithoutType("بازاریاب تعریف نشده");
                SellBrokers.add(sellBroker);
                brokerViewConfig();

            }
        });

    }

    public void brokerViewConfig() {
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SellBroker_Names);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.findRegisterASpinnerbroker.setAdapter(spinner_adapter);
        int possellbroker=0;
        for (SellBroker sellBroker:SellBrokers){
            if (sellBroker.getBrokerCode().equals(dbh.ReadConfig("BrokerCode"))){
                possellbroker=SellBrokers.indexOf(sellBroker);
            }
        }

        binding.findRegisterASpinnerbroker.setSelection(possellbroker);
        binding.findRegisterASpinnerbroker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                dbh.SaveConfig("BrokerCode",SellBrokers.get(position).getBrokerCode());
                binding.findRegisterABroker.setText(NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    public void init() {


        binding.findRegisterABroker.setText(NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));
        binding.findRegisterAGrid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        binding.findRegisterADelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        binding.findRegisterATitlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        binding.findRegisterABodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));
        binding.findRegisterADbname.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PersianCompanyNameUse")));

        String fullUrl = callMethod.ReadString("ServerURLUse"); // مقدار کامل URL
        String ipAddress = "";

        if (fullUrl != null && fullUrl.startsWith("http")) {
            // جدا کردن پروتکل و مسیر اضافی
            String temp = fullUrl.replace("http://", "").replace("https://", "");
            // جدا کردن فقط IP
            int colonIndex = temp.indexOf(':');
            if (colonIndex != -1) {
                ipAddress = temp.substring(0, colonIndex); // بخش IP قبل از ':'
            } else {
                int slashIndex = temp.indexOf('/');
                ipAddress = (slashIndex != -1) ? temp.substring(0, slashIndex) : temp;
            }
        }


        binding.findRegisterAServerurl.setText(ipAddress);


        binding.findRegisterAReplicationcolumn.setOnClickListener(v -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage("آیا تنظیمات پیش فرض مجددا گرفته شود ؟");

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                dbh.deleteColumn();
                dbh.DatabaseCreate();
                replication.DoingReplicate();
            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                // code to handle negative button click
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });



        binding.findRegisterAKeyboardrunnable.setChecked(callMethod.ReadBoolan("keyboardRunnable"));

        binding.findRegisterAKeyboardrunnable.setChecked(callMethod.ReadBoolan("keyboardRunnable"));
        binding.findRegisterAEditselected.setChecked(callMethod.ReadBoolan("disableSelectedFeild"));
        binding.findRegisterAShowallpage.setChecked(callMethod.ReadBoolan("ShowInFullPage"));
        binding.findRegisterASearchlock.setChecked(callMethod.ReadBoolan("LockSearchPage"));
        binding.findRegisterASelectallaftersearch.setChecked(callMethod.ReadBoolan("SelectAllAfterSearch"));



        binding.findRegisterAKeyboardrunnable.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("keyboardRunnable")) {
                callMethod.EditBoolan("keyboardRunnable", false);
                callMethod.showToast("خیر");

            } else {
                callMethod.EditBoolan("keyboardRunnable", true);
                callMethod.showToast("بله");
            }
        });

        binding.findRegisterASelectallaftersearch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("SelectAllAfterSearch")) {
                callMethod.EditBoolan("SelectAllAfterSearch", false);
                callMethod.showToast("خیر");

            } else {
                callMethod.EditBoolan("SelectAllAfterSearch", true);
                callMethod.showToast("بله");
            }
        });
        binding.findRegisterAShowallpage.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowInFullPage")) {
                callMethod.EditBoolan("ShowInFullPage", false);
                callMethod.showToast("خیر");

            } else {
                callMethod.EditBoolan("ShowInFullPage", true);
                callMethod.showToast("بله");
            }
        });
        binding.findRegisterASearchlock.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("LockSearchPage")) {
                callMethod.EditBoolan("LockSearchPage", false);
                callMethod.showToast("خیر");

            } else {
                callMethod.EditBoolan("LockSearchPage", true);
                callMethod.showToast("بله");
            }
        });



        binding.findRegisterAEditselected.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("disableSelectedFeild")) {
                callMethod.EditBoolan("disableSelectedFeild", false);
                callMethod.showToast("خیر");

            } else {
                callMethod.EditBoolan("disableSelectedFeild", true);
                callMethod.showToast("بله");
            }
        });


        binding.findRegisterABtn.setOnClickListener(view -> {
            callMethod.EditString("Grid", NumberFunctions.EnglishNumber(binding.findRegisterAGrid.getText().toString()));
            callMethod.EditString("Delay", NumberFunctions.EnglishNumber(binding.findRegisterADelay.getText().toString()));
            callMethod.EditString("TitleSize", NumberFunctions.EnglishNumber(binding.findRegisterATitlesize.getText().toString()));
            callMethod.EditString("BodySize", NumberFunctions.EnglishNumber(binding.findRegisterABodysize.getText().toString()));
            callMethod.EditString("PhoneNumber", NumberFunctions.EnglishNumber(binding.findRegisterAPhonenumber.getText().toString()));
            String newIp = binding.findRegisterAServerurl.getText().toString(); // مقدار جدید وارد شده توسط کاربر
            if (isValidIp(newIp)) {
                // جایگزین کردن فقط IP جدید در آدرس اصلی
                String newUrl = replaceIpInUrl(fullUrl, newIp);
                callMethod.EditString("ServerURLUse", newUrl); // ذخیره آدرس جدید
                dbhbase.UpdateUrl(callMethod.ReadString("ActivationCode"),newUrl);
                callMethod.showToast("آدرس ذخیره شد");
                if(!dbh.ReadConfig("BrokerCode").equals(NumberFunctions.EnglishNumber(binding.findRegisterABroker.getText().toString()))){
                    Registration();
                }else {
                    finish();
                }
            } else {
                callMethod.showToast("لطفاً یک IP معتبر وارد کنید");

            }


        });



    }
    private String replaceIpInUrl(String fullUrl, String newIp) {
        if (fullUrl == null || newIp == null) return fullUrl;

        // حذف پروتکل برای جایگزینی آسان
        String temp = fullUrl.replace("http://", "").replace("https://", "");
        int colonIndex = temp.indexOf(':'); // پیدا کردن پورت
        if (colonIndex != -1) {
            // جایگزین کردن IP قبل از پورت
            return fullUrl.startsWith("https") ?
                    "https://" + newIp + temp.substring(colonIndex) :
                    "http://" + newIp + temp.substring(colonIndex);
        }

        int slashIndex = temp.indexOf('/'); // پیدا کردن مسیر
        if (slashIndex != -1) {
            // جایگزین کردن IP قبل از مسیر
            return fullUrl.startsWith("https") ?
                    "https://" + newIp + temp.substring(slashIndex) :
                    "http://" + newIp + temp.substring(slashIndex);
        }

        // اگر هیچ پورت یا مسیری نبود، فقط IP را جایگزین می‌کنیم
        return fullUrl.startsWith("https") ? "https://" + newIp : "http://" + newIp;
    }

    private boolean isValidIp(String ip) {
        String ipPattern =
                "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$";
        return ip.matches(ipPattern);
    }

    public void Registration() {
        UserInfo UserInfoNew = new UserInfo();
        UserInfoNew.setBrokerCode(NumberFunctions.EnglishNumber(binding.findRegisterABroker.getText().toString()));
        callMethod.EditString("BrokerCode", NumberFunctions.EnglishNumber(binding.findRegisterABroker.getText().toString()));
        dbh.SavePersonalInfo(UserInfoNew);
        dbh.DatabaseCreate();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
