package com.kits.kowsarapp.activity.find;

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
import com.kits.kowsarapp.databinding.FindActivityConfigBinding;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.find.Find_DBH;


import java.text.DecimalFormat;


public class Find_ConfigActivity extends AppCompatActivity {

    private Find_DBH find_dbh;
    private CallMethod callMethod;
    private FindActivityConfigBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        binding = FindActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Config();
        populateViews();
        setButtonListeners();
    }

     void Config() {
        callMethod = new CallMethod(this);
         find_dbh = new Find_DBH(this, callMethod.ReadString("DatabaseName"));

    }

     void populateViews() {

        binding.findConfigABroker.setText(NumberFunctions.PerisanNumber(find_dbh.ReadConfig("BrokerCode")));
        binding.findConfigAGrid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        binding.findConfigADelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        binding.findConfigATitlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        binding.findConfigABodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));



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


         binding.findConfigAServerurl.setText(ipAddress); // نمایش IP
         binding.findConfigAEditselected.setChecked(callMethod.ReadBoolan("disableSelectedFeild"));
         binding.findConfigAKeyboardrunnable.setChecked(callMethod.ReadBoolan("keyboardRunnable"));
         binding.findConfigAShowallpage.setChecked(callMethod.ReadBoolan("ShowInFullPage"));
         binding.findConfigASearchlock.setChecked(callMethod.ReadBoolan("LockSearchPage"));
         binding.findConfigASelectallaftersearch.setChecked(callMethod.ReadBoolan("SelectAllAfterSearch"));


     }

     void setButtonListeners() {
        binding.findConfigABtnToReg.setOnClickListener(view -> {
            if (callMethod.ReadString("ActivationCode").equals("888888")) {
                Intent intent = new Intent(this, Find_RegistrationActivity.class);
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

                                Intent intent = new Intent(Find_ConfigActivity.this, Find_RegistrationActivity.class);
                                startActivity(intent);
                            } else {
                                callMethod.showToast("رمز عبور صیحیح نیست");
                            }

                        }
                    }
                });


        btn_login.setOnClickListener(v -> {

            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {

                Intent intent = new Intent(this, Find_RegistrationActivity.class);
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

