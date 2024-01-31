package com.kits.kowsarapp.activity.broker;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.broker.Broker_ProductAdapter;
import com.kits.kowsarapp.application.App;
import com.kits.kowsarapp.application.CallMethod;
import com.kits.kowsarapp.model.Category;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.GoodGroup;
import com.kits.kowsarapp.model.Product;
import com.kits.kowsarapp.webService.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;

import java.util.ArrayList;
import java.util.List;


public class Broker_AllGrpActivity extends AppCompatActivity {


    Broker_APIInterface broker_apiInterface;
    CallMethod callMethod;
    Broker_DBH dbh;
    Toolbar toolbar;
    ArrayList<Category> categories = new ArrayList<>();
    RecyclerView rc;
    Category category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_view);


        Config();
        try {
            Handler handler = new Handler();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                handler.postDelayed(this::init, 100);
            }
        } catch (Exception ignored) {

        }


    }


    public void Config() {
        callMethod = new CallMethod(this);
        String databaseName = callMethod.ReadString("DatabaseName");
        dbh = new Broker_DBH(this, databaseName);
        dbh.ClearSearchColumn();

        toolbar = findViewById(R.id.allview_toolbar);
        setSupportActionBar(toolbar);

        rc = findViewById(R.id.allview_rc);
        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);


    }


    public void init() {
        List<Category> categories = new ArrayList<>();

        List<GoodGroup> groupHeaders = dbh.getAllGroups(dbh.ReadConfig("GroupCodeDefult"));
        for (GoodGroup groupHeader : groupHeaders) {
            List<Product> products = new ArrayList<>();
            List<GoodGroup> groupRows = dbh.getAllGroups(groupHeader.getGoodGroupFieldValue("groupcode"));
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
        RecyclerView rc = findViewById(R.id.allview_rc);
        rc.setAdapter(adapter);
        rc.setLayoutManager(new LinearLayoutManager(this));
    }


}