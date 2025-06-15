package com.kits.kowsarapp.activity.broker;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.broker.Broker_BasketItemHistoryAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.BrokerActivityBaskethistoryBinding;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class Broker_BasketHistoryActivity extends AppCompatActivity {

    private String itemPosition = "0";
    private String searchQuery = "";
    GridLayoutManager gridLayoutManager;
    private CallMethod callMethod;
    private ArrayList<Good> goods = new ArrayList<>();
    private Broker_DBH broker_dbh;
    private Handler handler;
    private Broker_BasketItemHistoryAdapter broker_basketItemHistoryAdapter;
    
    private BrokerActivityBaskethistoryBinding binding;

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        binding = BrokerActivityBaskethistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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




    public void init() {
        DecimalFormat decimalFormat = new DecimalFormat("0,000");
        callMethod = new CallMethod(this);
        broker_dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));

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
                    goods = broker_dbh.getAllPreFactorRows(searchQuery, callMethod.ReadString("PreFactorGood"));

//                    int backgroundResourceId = itemPosition.equals("1") ?
//                            R.drawable.bg_round_green_history_line : R.drawable.bg_round_green_history;
//
//                    binding.bBaskethistoryARow.setBackground(ContextCompat.getDrawable(
//                            App.getContext(), backgroundResourceId));
//                    // update the list view with the new data
                    broker_basketItemHistoryAdapter.updateList(goods, itemPosition);
                }, Integer.parseInt(callMethod.ReadString("Delay")));
            }
        });
        // set listener for the list item
//        binding.bBaskethistoryARow.setOnClickListener(view -> {
//            int backgroundResourceId;
//            if (itemPosition.equals("1")) {
//                itemPosition = "0";
//                backgroundResourceId = R.drawable.bg_round_green_history_line;
//            } else {
//                itemPosition = "1";
//                backgroundResourceId = R.drawable.bg_round_green_history;
//            }
//            binding.bBaskethistoryARow.setBackground(ContextCompat.getDrawable(
//                    this, backgroundResourceId));
//
//            broker_basketItemHistoryAdapter.updateList(goods, itemPosition);
//        });

        goods = broker_dbh.getAllPreFactorRows(searchQuery, callMethod.ReadString("PreFactorGood"));

        broker_basketItemHistoryAdapter = new Broker_BasketItemHistoryAdapter(goods, itemPosition, this);
        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.bBaskethistoryAR1.setLayoutManager(gridLayoutManager);
        binding.bBaskethistoryAR1.setAdapter(broker_basketItemHistoryAdapter);
        binding.bBaskethistoryAR1.setItemAnimator(new DefaultItemAnimator());

        binding.bBaskethistoryATotalPriceBuy.setText(NumberFunctions.PerisanNumber(
                decimalFormat.format(Integer.parseInt(broker_dbh.getFactorSum(callMethod.ReadString("PreFactorGood"))))));
        binding.bBaskethistoryATotalAmountBuy.setText(NumberFunctions.PerisanNumber(
                broker_dbh.getFactorSumAmount(callMethod.ReadString("PreFactorGood"))));
        binding.bBaskethistoryATotalRowBuy.setText(NumberFunctions.PerisanNumber(
                String.valueOf(goods.size())));

        progressDialog.dismiss();
    }
}