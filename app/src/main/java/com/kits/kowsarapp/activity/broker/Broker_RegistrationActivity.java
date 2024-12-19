package com.kits.kowsarapp.activity.broker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;
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

    Broker_DBH dbh;
    CallMethod callMethod;
    Broker_Action broker_action;
    Base_Action base_action;

    Broker_Replication replication;
    Intent intent;
    BrokerActivityRegistrBinding binding;
    Broker_APIInterface broker_apiInterface;
    ArrayList<String> SellBroker_Names = new ArrayList<>();
    ArrayList<SellBroker> SellBrokers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));
        replication = new Broker_Replication(this);
        broker_action = new Broker_Action(this);
        base_action = new Base_Action(this);

        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);

        Call<RetrofitResponse> call1 = broker_apiInterface.GetSellBroker("GetSellBroker");

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

                callMethod.Log(""+t.getMessage());
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
            if (sellBroker.getBrokerCode().equals(dbh.ReadConfig("BrokerCode"))){
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


        binding.bRegisterABroker.setText(NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));
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
                dbh.deleteColumn();
                replication.BrokerStack();
                dbh.DatabaseCreate();
                base_action.app_info();
                replication.DoingReplicate();


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

            if(!dbh.ReadConfig("BrokerCode").equals(NumberFunctions.EnglishNumber(binding.bRegisterABroker.getText().toString()))){
                Registration();
            }else {
                finish();
            }

        });


    }


    public void Registration() {



            UserInfo UserInfoNew = new UserInfo();
            UserInfoNew.setBrokerCode(NumberFunctions.EnglishNumber(binding.bRegisterABroker.getText().toString()));
            dbh.SaveConfig("BrokerCode",NumberFunctions.EnglishNumber(binding.bRegisterABroker.getText().toString()));
            dbh.SavePersonalInfo(UserInfoNew);
            dbh.DatabaseCreate();

            replication.BrokerStack();
            base_action.app_info();
            replication.DoingReplicate();

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
}
