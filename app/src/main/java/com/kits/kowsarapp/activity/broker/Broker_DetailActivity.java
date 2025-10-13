package com.kits.kowsarapp.activity.broker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.broker.Broker_SliderAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.databinding.BrokerActivityDetailBinding;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class Broker_DetailActivity extends AppCompatActivity {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");

    CallMethod callMethod;
    Broker_DBH broker_dbh;
    Intent intent;

    Broker_Action broker_action;
    Broker_APIInterface broker_apiInterface;
    Good gooddetail;

    ArrayList<Column> Columns= new ArrayList<>();
    ArrayList<Good> imagelists= new ArrayList<>();


    String id;


    BrokerActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));

        binding = BrokerActivityDetailBinding.inflate(getLayoutInflater());
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

    //*********************************************************************


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        id = data.getString("id");

    }

    public void Config() {
        callMethod = new CallMethod(this);
        broker_dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));
        broker_action = new Broker_Action(this);
        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);
        Columns = broker_dbh.GetColumns(id, "", "0");
        gooddetail = broker_dbh.getGoodByCode(id);
        setSupportActionBar(binding.bDetailAToolbar);
    }


    @SuppressLint("UseCompatLoadingForColorStateLists")
    public void init() {
        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            binding.bDetailACustomer.setText("فاکتوری انتخاب نشده");
            binding.bDetailALlSumFactor.setVisibility(View.GONE);
        } else {
            binding.bDetailALlSumFactor.setVisibility(View.VISIBLE);
            binding.bDetailACustomer.setText(NumberFunctions.PerisanNumber(broker_dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode"))));
            binding.bDetailASumFactor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(broker_dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
        }

        for (Column Column : Columns) {
            if (Integer.parseInt(Column.getColumnFieldValue("SortOrder")) > 0) {
                CreateView(
                        Column.getColumnFieldValue("ColumnDesc"),
                        gooddetail.getGoodFieldValue(Column.getColumnFieldValue("columnname"))
                );
            }
        }

        imagelists = broker_dbh.GetksrImageCodes(gooddetail.getGoodFieldValue("GoodCode"));
        SliderView();

        if (gooddetail.getGoodFieldValue("ActiveStack").equals("1")){
            binding.bDetailABtnbuy.setBackgroundTintList(getResources().getColorStateList(R.color.green_600));
        }else{
            binding.bDetailABtnbuy.setBackgroundTintList(getResources().getColorStateList(R.color.grey_700));
        }


        if (callMethod.ReadBoolan("ShowGoodBuyBtn")) {
            binding.bDetailABtnbuy.setVisibility(View.VISIBLE);
            if (callMethod.ReadBoolan("CanUseInactive")){
                binding.bDetailABtnbuy.setText("غیر فعال ");
            }
        }else{
            binding.bDetailABtnbuy.setVisibility(View.GONE);

        }





        binding.bDetailABtnbuy.setOnClickListener(view -> {


            if (callMethod.ReadBoolan("CanUseInactive")){
                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    broker_action.buydialog(gooddetail.getGoodFieldValue("GoodCode"), "0");
                } else {
                    intent = new Intent(this, Broker_PFOpenActivity.class);
                    intent.putExtra("fac", "0");
                    startActivity(intent);
                }
            }else{
                if (gooddetail.getGoodFieldValue("ActiveStack").equals("1")) {

                    if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                        broker_action.buydialog(gooddetail.getGoodFieldValue("GoodCode"), "0");
                    } else {
                        intent = new Intent(this, Broker_PFOpenActivity.class);
                        intent.putExtra("fac", "0");
                        startActivity(intent);
                    }
                }else{
                    callMethod.showToast("این کالا غیر فعال می باشد");
                }
            }

        });
    }


    public void CreateView(String title, String body) {

        LinearLayoutCompat ll_1 = new LinearLayoutCompat(this);
        ll_1.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_1.setWeightSum(1);

        TextView extra_TextView1 = new TextView(this);
        extra_TextView1.setText(NumberFunctions.PerisanNumber(title));
        extra_TextView1.setBackgroundResource(R.color.grey_20);
        extra_TextView1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 0.7));
        extra_TextView1.setTextSize(Integer.parseInt(callMethod.ReadString("TitleSize")));
        extra_TextView1.setPadding(2, 5, 2, 5);
        extra_TextView1.setGravity(Gravity.CENTER);
        extra_TextView1.setTextColor(getResources().getColor(R.color.grey_800));

        ll_1.addView(extra_TextView1);

        TextView extra_TextView2 = new TextView(this);
        try {
            if (Integer.parseInt(body) > 999) {
                extra_TextView2.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(body))));
            } else {
                extra_TextView2.setText(NumberFunctions.PerisanNumber(body));
            }
        } catch (Exception e) {
            extra_TextView2.setText(NumberFunctions.PerisanNumber(body));
        }

        extra_TextView2.setBackgroundResource(R.color.white);
        extra_TextView2.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 0.3));
        extra_TextView2.setTextSize(Integer.parseInt(callMethod.ReadString("BodySize")));
        extra_TextView2.setPadding(2, 2, 2, 2);
        extra_TextView2.setGravity(Gravity.CENTER);
        extra_TextView2.setTextColor(getResources().getColor(R.color.grey_1000));
        ll_1.addView(extra_TextView2);

        ViewPager extra_ViewPager = new ViewPager(this);
        extra_ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 3));
        extra_ViewPager.setBackgroundResource(R.color.grey_40);

        binding.bDetailALineProperty.addView(ll_1);
        binding.bDetailALineProperty.addView(extra_ViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.broker_options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.b_bag_shop) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                intent = new Intent(this, Broker_BasketActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
            } else {
                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    intent = new Intent(this, Broker_SearchActivity.class);
                    intent.putExtra("scan", "");
                    intent.putExtra("id", "0");
                    intent.putExtra("title", "جستجوی کالا");

                } else {
                    callMethod.showToast("سبد خرید خالی می باشد");
                    intent = new Intent(this, Broker_PFOpenActivity.class);
                }
            }
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void SliderView() {



        Broker_SliderAdapter adapter = new Broker_SliderAdapter(imagelists, true, this);
        binding.bDetailAImageSlider.setSliderAdapter(adapter);
        binding.bDetailAImageSlider.setIndicatorAnimation(IndicatorAnimations.SCALE);
        binding.bDetailAImageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.bDetailAImageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        binding.bDetailAImageSlider.setIndicatorSelectedColor(Color.WHITE);
        binding.bDetailAImageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.bDetailAImageSlider.setScrollTimeInSec(3);
        binding.bDetailAImageSlider.startAutoCycle();

    }

    @Override
    public void onRestart() {
        finish();
        startActivity(getIntent());
        super.onRestart();
    }

}











