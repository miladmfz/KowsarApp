package com.kits.kowsarapp.activity.search;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.databinding.SearchActivityConfigBinding;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.search.Search_DBH;


import java.text.DecimalFormat;


public class Search_ConfigActivity extends AppCompatActivity {

    private Search_DBH dbh;
    private CallMethod callMethod;
    private SearchActivityConfigBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SearchActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize variables and configurations
        Config();

        // Populate views with data
        populateViews();

        // Set listeners for buttons
        setButtonListeners();
    }

     void Config() {
        callMethod = new CallMethod(this);
        dbh = new Search_DBH(this, callMethod.ReadString("DatabaseName"));

    }

     void populateViews() {
        DecimalFormat decimalFormat = new DecimalFormat("0,000");
        //binding.configSumFactor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(dbh.getsum_sumfactor())));
        binding.seaConfigABroker.setText(NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));
        binding.seaConfigAGrid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        binding.seaConfigADelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        binding.seaConfigATitlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        binding.seaConfigABodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));
        //binding.configServerurl.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("ServerURLUse")));

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

         binding.seaConfigAServerurl.setText(ipAddress); // نمایش IP



         binding.seaConfigAKeyboardrunnable.setChecked(callMethod.ReadBoolan("keyboardRunnable"));

    }

     void setButtonListeners() {
        binding.seaConfigABtnToReg.setOnClickListener(view -> {


            if (callMethod.ReadString("ActivationCode").equals("111111")) {
                Intent intent = new Intent(this, Search_RegistrationActivity.class);
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

                                Intent intent = new Intent(Search_ConfigActivity.this, Search_RegistrationActivity.class);
                                startActivity(intent);
                            } else {
                                callMethod.showToast("رمز عبور صیحیح نیست");
                            }

                        }
                    }
                });











        btn_login.setOnClickListener(v -> {

            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {

                Intent intent = new Intent(this, Search_RegistrationActivity.class);
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

