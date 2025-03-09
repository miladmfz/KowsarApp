package com.kits.kowsarapp.activity.broker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.BrokerActivityConfigBinding;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.NumberFunctions;


public class Broker_ConfigActivity extends AppCompatActivity {

    private Broker_DBH dbh;
    private CallMethod callMethod;
    private BrokerActivityConfigBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        binding = BrokerActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Config();
        populateViews();
        setButtonListeners();
    }

     void Config() {
        callMethod = new CallMethod(this);
        dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));

    }

     void populateViews() {
        //binding.bConfigASumFactor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(dbh.getsum_sumfactor())));
        binding.bConfigABorker.setText(NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));
        binding.bConfigAGrid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        binding.bConfigADelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        binding.bConfigATitlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        binding.bConfigABodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));
        binding.bConfigAPhonenumber.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PhoneNumber")));
        binding.bConfigAPhonenumber.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PhoneNumber")));

        binding.bConfigASelloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);
        binding.bConfigAAutorep.setChecked(callMethod.ReadBoolan("AutoReplication"));
        binding.bConfigAKeyboardrunnable.setChecked(callMethod.ReadBoolan("keyboardRunnable"));
        binding.bConfigADetailshow.setChecked(callMethod.ReadBoolan("ShowDetail"));
        binding.bConfigALineview.setChecked(callMethod.ReadBoolan("LineView"));
    }

     void setButtonListeners() {
        binding.bConfigAToReg.setOnClickListener(view -> {


            if (callMethod.ReadString("ActivationCode").equals("111111") ||callMethod.ReadString("ActivationCode").equals("555555")) {
                Intent intent = new Intent(this, Broker_RegistrationActivity.class);
                startActivity(intent);
            }else {
                LoginSetting();
            }

        });
    }
    public void LoginSetting() {



        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.default_loginconfig);
        EditText ed_password = dialog.findViewById(R.id.d_loginconfig_ed);
        MaterialButton btn_login = dialog.findViewById(R.id.d_loginconfig_btn);



        ed_password.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(final Editable editable) {

                        if(NumberFunctions.EnglishNumber(ed_password.getText().toString()).length()>5) {
                            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {

                                Intent intent = new Intent(Broker_ConfigActivity.this, Broker_RegistrationActivity.class);
                                startActivity(intent);
                            } else {
                                callMethod.showToast("رمز عبور صیحیح نیست");
                            }

                        }
                    }
                });











        btn_login.setOnClickListener(v -> {

            callMethod.Log(callMethod.ReadString("ActivationCode"));
            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {

                Intent intent = new Intent(this, Broker_RegistrationActivity.class);
                startActivity(intent);
            }else {
                callMethod.showToast("رمز عبور صیحیح نیست");
            }


        });
        dialog.show();
    }

    @Override
    public void onRestart() {
        finish();
        startActivity(getIntent());
        super.onRestart();
    }

}

