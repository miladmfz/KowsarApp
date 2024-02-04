package com.kits.kowsarapp.activity.broker;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.broker.Broker_GoodBasketAdapter;

import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.databinding.BrokerActivityBasketBinding;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.Good;
import com.kits.kowsarapp.model.NumberFunctions;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Broker_BasketActivity extends AppCompatActivity {

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    ArrayList<Good> goods;
    Broker_GoodBasketAdapter adapter;
    GridLayoutManager gridLayoutManager;
    CallMethod callMethod;
    BrokerActivityBasketBinding binding;
    private Broker_Action action;
    private String PreFac = "0";
    private Broker_DBH dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = BrokerActivityBasketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        intent();
        Config();

        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }


    }

    //*****************************************************************

    public void Config() {
        action = new Broker_Action(this);
        callMethod = new CallMethod(this);
        dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));
        setSupportActionBar(binding.bBasketAToolbar);

    }

    public void init() {


        goods = dbh.getAllPreFactorRows("", PreFac);
        adapter = new Broker_GoodBasketAdapter(goods, this);
        if (adapter.getItemCount() == 0) {
            callMethod.showToast("سبد خرید خالی می باشد");
        }
        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.bBasketAR1.setLayoutManager(gridLayoutManager);
        binding.bBasketAR1.setAdapter(adapter);
        binding.bBasketAR1.setItemAnimator(new DefaultItemAnimator());
        binding.bBasketAR1.setVisibility(View.VISIBLE);

        try {
            binding.bBasketAR1.scrollToPosition(Integer.parseInt(callMethod.ReadString("BasketItemView")) - 1);
        } catch (Exception e) {
            binding.bBasketAR1.scrollToPosition(0);
            callMethod.EditString("BasketItemView", "0");

        }


        binding.bBasketAR1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    callMethod.EditString("BasketItemView", String.valueOf(gridLayoutManager.findFirstVisibleItemPosition()));
                }
            }
        });


        binding.bBasketATotalPriceBuy.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(PreFac)))));
        binding.bBasketATotalAmountBuy.setText(NumberFunctions.PerisanNumber(dbh.getFactorSumAmount(PreFac)));
        binding.bBasketATotalCustomerBuy.setText(NumberFunctions.PerisanNumber(dbh.getFactorCustomer(PreFac)));
        binding.bBasketATotalRowBuy.setText(NumberFunctions.PerisanNumber(String.valueOf(goods.size())));


        binding.bBasketATotalDelete.setOnClickListener(view ->


                {


                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
                    builder.setTitle(R.string.textvalue_allert);
                    builder.setMessage("آیا مایل به خالی کردن سبد خرید می باشید؟");

                    builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                        dbh.DeletePreFactorRow(PreFac, "0");
                        finish();
                        callMethod.showToast("سبد خرید با موفقیت حذف گردید!");

                    });

                    builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                        // code to handle negative button click
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


        );


        binding.bBasketATest.setOnClickListener(view ->


                {


                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
                    builder.setTitle(R.string.textvalue_allert);
                    builder.setMessage("آیا فاکتور ارسال گردد؟");

                    builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                        action.sendfactor(PreFac);
                    });

                    builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                        // code to handle negative button click
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

        );


    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        PreFac = data.getString("PreFac");
    }


}

