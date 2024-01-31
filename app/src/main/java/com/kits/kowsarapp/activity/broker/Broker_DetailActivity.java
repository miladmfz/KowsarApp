package com.kits.kowsarapp.activity.broker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.kits.kowsarapp.adapters.SliderAdapter;
import com.kits.kowsarapp.application.Action;
import com.kits.kowsarapp.application.CallMethod;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.databinding.ActivityDetailTBinding;
import com.kits.kowsarapp.model.Column;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.Good;
import com.kits.kowsarapp.model.NumberFunctions;
import com.kits.kowsarapp.webService.APIClient;
import com.kits.kowsarapp.webService.APIInterface;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class Broker_DetailActivity extends AppCompatActivity {

    private String id;
    Good gooddetail;
    Broker_APIInterface broker_apiInterface;
    private Intent intent;
    CallMethod callMethod;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    Broker_DBH dbh;
    ArrayList<Column> Columns;
    ArrayList<Good> imagelists;
    Broker_Action action;

    SliderView sliderView;

    ActivityDetailTBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_t);


        binding = ActivityDetailTBinding.inflate(getLayoutInflater());
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
        dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));
        action = new Broker_Action(this);
        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);
        Columns = dbh.GetColumns(id, "", "0");
        gooddetail = dbh.getGoodByCode(id);
        setSupportActionBar(binding.DetailActivityToolbar);
    }


    @SuppressLint("UseCompatLoadingForColorStateLists")
    public void init() {
        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            binding.DetailActivityCustomer.setText("فاکتوری انتخاب نشده");
            binding.DetailActivityLlSumFactor.setVisibility(View.GONE);
        } else {
            binding.DetailActivityLlSumFactor.setVisibility(View.VISIBLE);
            binding.DetailActivityCustomer.setText(NumberFunctions.PerisanNumber(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode"))));
            binding.DetailActivitySumFactor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
        }

        for (Column Column : Columns) {
            if (Integer.parseInt(Column.getColumnFieldValue("SortOrder")) > 0) {
                CreateView(
                        Column.getColumnFieldValue("ColumnDesc"),
                        gooddetail.getGoodFieldValue(Column.getColumnFieldValue("columnname"))
                );
            }
        }

        Log.e("test",gooddetail.getGoodFieldValue("GoodCode"));
        imagelists = dbh.GetksrImageCodes(gooddetail.getGoodFieldValue("GoodCode"));
        SliderView();

        if (gooddetail.getGoodFieldValue("ActiveStack").equals("1")){
            binding.DetailActivityBtnbuy.setBackgroundTintList(getResources().getColorStateList(R.color.green_600));
        }else{
            binding.DetailActivityBtnbuy.setBackgroundTintList(getResources().getColorStateList(R.color.grey_700));
        }

        binding.DetailActivityBtnbuy.setOnClickListener(view -> {
            if (gooddetail.getGoodFieldValue("ActiveStack").equals("1")) {

                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    action.buydialog(gooddetail.getGoodFieldValue("GoodCode"), "0");
                } else {
                    intent = new Intent(this, Broker_PFOpenActivity.class);
                    intent.putExtra("fac", "0");
                    startActivity(intent);
                }
            }else{
                callMethod.showToast("این کالا غیر فعال می باشد");
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

        binding.DetailActivityLineProperty.addView(ll_1);
        binding.DetailActivityLineProperty.addView(extra_ViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.bag_shop) {
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
        binding.DetailActivityImageSlider.setSliderAdapter(adapter);
        binding.DetailActivityImageSlider.setIndicatorAnimation(IndicatorAnimations.SCALE);
        binding.DetailActivityImageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.DetailActivityImageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        binding.DetailActivityImageSlider.setIndicatorSelectedColor(Color.WHITE);
        binding.DetailActivityImageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.DetailActivityImageSlider.setScrollTimeInSec(3);
        binding.DetailActivityImageSlider.startAutoCycle();

    }

    @Override
    public void onRestart() {
        finish();
        startActivity(getIntent());
        super.onRestart();
    }

}











