package com.kits.kowsarapp.activity.broker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.broker.Broker_CustomerAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.broker.Broker_Replication;
import com.kits.kowsarapp.databinding.BrokerActivityCustomerBinding;
import com.kits.kowsarapp.model.Customer;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.NumberFunctions;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;

import java.util.ArrayList;


public class Broker_CustomerActivity extends AppCompatActivity {
    private Broker_APIInterface broker_apiInterface;
    private String factor_target = "0";
    private String edit = "0";
    private Broker_DBH dbh;
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Customer> citys = new ArrayList<>();
    private Broker_CustomerAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private Broker_Replication replication;
    private String srch = "";
    private String id = "0";
    private Intent intent;
    private ArrayList<String> city_array = new ArrayList<>();
    private String kodemelli, citycode = "", name, family, address, phone, mobile, email, postcode, zipcode;
    private boolean activecustomer = true;
    private CallMethod callMethod;
    private BrokerActivityCustomerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BrokerActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        intent();
        Config();
        try {
            init();
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }


    }

//*****************************************************************************************


    private void Config() {
        callMethod = new CallMethod(this);
        replication = new Broker_Replication(this);
        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);
        dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));
        setSupportActionBar(binding.bCustomerAToolbar);
    }




    public void init() {

        switch (id) {
            case "0":
                Customer_search();
                break;
            case "1":
                Customer_new();
                break;
        }

    }

    private void intent() {
        Bundle data = getIntent().getExtras();
        edit = data.getString("edit");
        factor_target = data.getString("factor_code");
        id = data.getString("id");
        assert data != null;
    }
    private void Customer_search() {
        binding.bCustomerASearchLine.setVisibility(View.VISIBLE);
        binding.bCustomerAEdtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                srch = NumberFunctions.EnglishNumber(editable.toString());
                allCustomer();
            }
        });


        binding.bCustomerANewRegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Broker_CustomerActivity.this, Broker_CustomerActivity.class);
            intent.putExtra("edit", "0");
            intent.putExtra("factor_code", "0");
            intent.putExtra("id", "1");
            startActivity(intent);
        });

        SwitchMaterial mySwitchActivestack = findViewById(R.id.b_customer_a_switch);
        mySwitchActivestack.setOnCheckedChangeListener((compoundButton, b) -> {
            activecustomer = b;
            mySwitchActivestack.setText(b ? "فعال" : "فعال -غیرفعال");
            allCustomer();
        });
        allCustomer();

    }

    public void Customer_new() {
        binding.bCustomerANewLine.setVisibility(View.VISIBLE);
        // replication.replicate_customer();


        citys = dbh.city();
        for (Customer citycustomer : citys) {
            city_array.add(citycustomer.getCustomerFieldValue("CityName"));
        }

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, city_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.bCustomerACitySpinner.setAdapter(spinner_adapter);
        binding.bCustomerACitySpinner.setSelection(0);


        binding.bCustomerACitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citycode = citys.get(position).getCustomerFieldValue("CityCode");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.bCustomerANewKodemelliCheck.setOnClickListener(v -> {

            if (dbh.Customer_check(binding.bCustomerANewKodemelli.getText().toString()) > 0) {

                binding.bCustomerANewKodemelliStatus.setText("کد ملی ثبت شده است");
                binding.bCustomerANewKodemelliStatus.setTextColor(getResources().getColor(R.color.red_300));
            } else {

                binding.bCustomerANewKodemelliStatus.setText("کد ملی ثبت نشده است");
                binding.bCustomerANewKodemelliStatus.setTextColor(getResources().getColor(R.color.green_900));

            }
        });

        binding.bCustomerANewRegisterBtn.setOnClickListener(v -> {

            if (dbh.Customer_check(binding.bCustomerANewKodemelli.getText().toString()) > 0) {
                binding.bCustomerANewKodemelliStatus.setText("کد ملی ثبت شده است");
                binding.bCustomerANewKodemelliStatus.setTextColor(getResources().getColor(R.color.red_300));
            } else {

                if (Integer.parseInt(dbh.ReadConfig("BrokerCode")) > 0) {
                    kodemelli = NumberFunctions.EnglishNumber(binding.bCustomerANewKodemelli.getText().toString());
                    name = NumberFunctions.EnglishNumber(binding.bCustomerANewName.getText().toString());
                    family = NumberFunctions.EnglishNumber(binding.bCustomerANewFamily.getText().toString());
                    address = NumberFunctions.EnglishNumber(binding.bCustomerANewAddress.getText().toString());
                    phone = NumberFunctions.EnglishNumber(binding.bCustomerANewPhone.getText().toString());
                    mobile = NumberFunctions.EnglishNumber(binding.bCustomerANewMobile.getText().toString());
                    email = NumberFunctions.EnglishNumber(binding.bCustomerANewEmail.getText().toString());
                    postcode = NumberFunctions.EnglishNumber(binding.bCustomerANewPostcode.getText().toString());
                    zipcode = NumberFunctions.EnglishNumber(binding.bCustomerANewZipcode.getText().toString());
//
//                    Call<RetrofitResponse> call = apiInterface.customer_insert( auser.getBrokerCode(), citycode, kodemelli, name, family, address, phone, mobile, email, postcode, zipcode);
//                    call.enqueue(new Callback<RetrofitResponse>() {
//                        @Override
//                        public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
//                            if (response.isSuccessful()) {
//                                assert response.body() != null;
//                                ArrayList<Customer> Customes = response.body().getCustomers();
//                                callMethod.showToast(Customes.get(0).getCustomerFieldValue("ErrDesc"));
//                                intent = new Intent(App.getContext(), CustomerActivity.class);
//                                intent.putExtra("edit", "0");
//                                intent.putExtra("factor_code", "0");
//                                intent.putExtra("id", "0");
//                                startActivity(intent);
//                            }
//
//
//                        }
//
//                        @Override
//                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
//                            callMethod.Log(t.getMessage());
//                        }
//                    });

                } else {
                    intent = new Intent(this, Broker_ConfigActivity.class);
                    callMethod.showToast("کد بازاریاب را وارد کنید");
                    startActivity(intent);

                }
            }

        });


    }


    public void allCustomer() {
        customers = dbh.AllCustomer(srch, activecustomer);
        adapter = new Broker_CustomerAdapter(customers, this, edit, factor_target);
        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.bCustomerAR1.setLayoutManager(gridLayoutManager);
        binding.bCustomerAR1.setAdapter(adapter);
        binding.bCustomerAR1.setItemAnimator(new DefaultItemAnimator());
    }


}
