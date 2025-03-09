package com.kits.kowsarapp.activity.broker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.broker.Broker_PFAdapter;
import com.kits.kowsarapp.adapter.broker.Broker_PFOpenAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.BrokerActivityPfopenBinding;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.PreFactor;

import java.util.ArrayList;
import java.util.Objects;


public class Broker_PFOpenActivity extends AppCompatActivity {


    Broker_DBH broker_dbh;
    private String fac;
    private Intent intent;
    GridLayoutManager gridLayoutManager;
    CallMethod callMethod;
    ArrayList<PreFactor> preFactors;


    BrokerActivityPfopenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        binding = BrokerActivityPfopenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Dialog dialog1;
        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.broker_spinner_box);
        TextView repw = dialog1.findViewById(R.id.b_spinner_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();

        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }


    }

    //*********************************************


    public void Config() {
        callMethod = new CallMethod(this);
        broker_dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));


    }

    public void init() {


        preFactors = broker_dbh.getAllPrefactorHeaderopen();
        binding.bPfopenAAmount.setText((NumberFunctions.PerisanNumber("" + preFactors.size())));


        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.bPfopenARecyclerView.setLayoutManager(gridLayoutManager);
        if (Integer.parseInt(fac) != 0) {
            Broker_PFAdapter adapter = new Broker_PFAdapter(preFactors, this);
            binding.bPfopenARecyclerView.setAdapter(adapter);
        } else {
            Broker_PFOpenAdapter adapter = new Broker_PFOpenAdapter(preFactors, this);
            binding.bPfopenARecyclerView.setAdapter(adapter);
        }
        binding.bPfopenARecyclerView.setItemAnimator(new DefaultItemAnimator());


        binding.bPfopenARefresh.setOnClickListener(view -> {
            finish();
            startActivity(getIntent());
        });

        binding.bPfopenADeleteempty.setOnClickListener(view -> {
            broker_dbh.DeleteEmptyPreFactor();
            finish();
            startActivity(getIntent());
        });


        binding.bPfopenABtn.setOnClickListener(view -> {
            intent = new Intent(this, Broker_CustomerActivity.class);
            intent.putExtra("edit", "0");
            intent.putExtra("factor_code", "0");
            intent.putExtra("id", "0");
            startActivity(intent);
        });


    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        fac = data.getString("fac");
    }


}
