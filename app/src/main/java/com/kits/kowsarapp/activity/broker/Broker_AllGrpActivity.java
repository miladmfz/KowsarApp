package com.kits.kowsarapp.activity.broker;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.broker.Broker_ProductAdapter;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Category;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.GoodGroup;
import com.kits.kowsarapp.model.base.Product;

import java.util.ArrayList;
import java.util.List;


public class Broker_AllGrpActivity extends AppCompatActivity {

    CallMethod callMethod;
    Broker_DBH broker_dbh;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        setContentView(R.layout.broker_activity_allgrp);

        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
        } catch (Exception ignored) {

        }


    }


    public void Config() {
        callMethod = new CallMethod(this);
        String databaseName = callMethod.ReadString("DatabaseName");
        broker_dbh = new Broker_DBH(this, databaseName);
        broker_dbh.ClearSearchColumn();

        toolbar = findViewById(R.id.b_allgrp_a_toolbar);
        setSupportActionBar(toolbar);


    }


    public void init() {
        List<Category> categories = new ArrayList<>();

        List<GoodGroup> groupHeaders = broker_dbh.getAllGroups(broker_dbh.ReadConfig("GroupCodeDefult"));
        for (GoodGroup groupHeader : groupHeaders) {
            List<Product> products = new ArrayList<>();
            List<GoodGroup> groupRows = broker_dbh.getAllGroups(groupHeader.getGoodGroupFieldValue("groupcode"));
            for (GoodGroup groupRow : groupRows) {
                Product product = new Product(
                        groupRow.getGoodGroupFieldValue("Name"),
                        Integer.parseInt(groupRow.getGoodGroupFieldValue("groupcode")),
                        Integer.parseInt(groupRow.getGoodGroupFieldValue("ChildNo")));
                products.add(product);
            }
            Category category = new Category(
                    groupHeader.getGoodGroupFieldValue("Name"),
                    products,
                    Integer.parseInt(groupHeader.getGoodGroupFieldValue("groupcode")),
                    Integer.parseInt(groupHeader.getGoodGroupFieldValue("ChildNo")));
            categories.add(category);
        }

        Broker_ProductAdapter adapter = new Broker_ProductAdapter(categories, App.getContext());
        RecyclerView rc = findViewById(R.id.b_allgrp_a_rc);
        rc.setAdapter(adapter);
        rc.setLayoutManager(new LinearLayoutManager(this));
    }


}