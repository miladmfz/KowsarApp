package com.kits.kowsarapp.activity.broker;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.broker.Broker_BasketItemHistoryAdapter;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.BrokerActivityBaskethistoryBinding;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.Good;
import com.kits.kowsarapp.model.NumberFunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class Broker_BasketHistoryActivity extends AppCompatActivity {

    private String itemPosition = "0";
    private String searchQuery = "";

    private CallMethod callMethod;
    private ArrayList<Good> goods = new ArrayList<>();
    private Broker_DBH dbh;
    private Handler handler;
    private Broker_BasketItemHistoryAdapter adapter;
    
    private BrokerActivityBaskethistoryBinding binding;

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BrokerActivityBaskethistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // create a dialog to show progress while loading data
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setContentView(R.layout.broker_spinner_box);
        TextView repw = progressDialog.findViewById(R.id.b_spinner_text);
        repw.setText("در حال خواندن اطلاعات");
        progressDialog.show();

        handler = new Handler();
        handler.postDelayed(this::init, 100);
    }



    // initialize the activity
    public void init() {
        DecimalFormat decimalFormat = new DecimalFormat("0,000");
        callMethod = new CallMethod(this);
        dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));

        binding.bBaskethistoryAEdtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> {
                    searchQuery = NumberFunctions.EnglishNumber(editable.toString());
                    goods = dbh.getAllPreFactorRows(searchQuery, callMethod.ReadString("PreFactorGood"));

                    int backgroundResourceId = itemPosition.equals("1") ?
                            R.drawable.bg_round_green_history_line : R.drawable.bg_round_green_history;

                    binding.bBaskethistoryARow.setBackground(ContextCompat.getDrawable(
                            App.getContext(), backgroundResourceId));
                    // update the list view with the new data
                    adapter.updateList(goods, itemPosition);
                }, Integer.parseInt(callMethod.ReadString("Delay")));
            }
        });
        // set listener for the list item
        binding.bBaskethistoryARow.setOnClickListener(view -> {
            int backgroundResourceId;
            if (itemPosition.equals("1")) {
                itemPosition = "0";
                backgroundResourceId = R.drawable.bg_round_green_history_line;
            } else {
                itemPosition = "1";
                backgroundResourceId = R.drawable.bg_round_green_history;
            }
            binding.bBaskethistoryARow.setBackground(ContextCompat.getDrawable(
                    this, backgroundResourceId));

            adapter.updateList(goods, itemPosition);
        });

        goods = dbh.getAllPreFactorRows(searchQuery, callMethod.ReadString("PreFactorGood"));

        adapter = new Broker_BasketItemHistoryAdapter(goods, itemPosition, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        binding.bBaskethistoryAR1.setLayoutManager(gridLayoutManager);
        binding.bBaskethistoryAR1.setAdapter(adapter);
        binding.bBaskethistoryAR1.setItemAnimator(new DefaultItemAnimator());

        binding.bBaskethistoryATotalPriceBuy.setText(NumberFunctions.PerisanNumber(
                decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorGood"))))));
        binding.bBaskethistoryATotalAmountBuy.setText(NumberFunctions.PerisanNumber(
                dbh.getFactorSumAmount(callMethod.ReadString("PreFactorGood"))));
        binding.bBaskethistoryATotalRowBuy.setText(NumberFunctions.PerisanNumber(
                String.valueOf(goods.size())));

        progressDialog.dismiss();
    }
}