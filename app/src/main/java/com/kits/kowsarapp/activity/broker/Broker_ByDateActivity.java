package com.kits.kowsarapp.activity.broker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.broker.Broker_GoodAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.BrokerActivityBydateBinding;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class Broker_ByDateActivity extends AppCompatActivity {


    DecimalFormat decimalFormat = new DecimalFormat("0,000");


    CallMethod callMethod;
    Broker_DBH broker_dbh;
    Broker_GoodAdapter broker_goodAdapter;
    GridLayoutManager gridLayoutManager;
    PersianCalendar persianCalendar;
    Intent intent;
    Menu item_multi;


    ArrayList<String[]> Multi_buy = new ArrayList<>();
    ArrayList<Good> Multi_Good = new ArrayList<>();
    ArrayList<Good> Moregoods = new ArrayList<>();
    ArrayList<Good> goods = new ArrayList<>();


    Integer pastVisiblesItems = 0, visibleItemCount, totalItemCount;
    Integer conter = 0;
    Integer grid;

    String year,mount,day;
    String PageMoreData = "0";
    String lastDate;
    String date;

    Boolean defultenablesellprice;
    Boolean loading = true;

    BrokerActivityBydateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        binding = BrokerActivityBydateBinding.inflate(getLayoutInflater());
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

    //***************************************************


    public void Config() {
        callMethod = new CallMethod(this);
        broker_dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));
        persianCalendar = new PersianCalendar();

        setSupportActionBar(binding.bBydateAToolbar);

    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        date = data.getString("date");
    }

    @SuppressLint("SetTextI18n")
    public void init() {


        persianCalendar.setPersianDate(
                persianCalendar.getPersianYear(),
                persianCalendar.getPersianMonth(),
                persianCalendar.getPersianDay() - Integer.parseInt(date)
        );

        year = "";
        mount = "0";
        day = "0";

        year = year + persianCalendar.getPersianYear();

        if (String.valueOf(persianCalendar.getPersianMonth()).equals("11")) {
            mount = "12";
        } else if (String.valueOf(persianCalendar.getPersianMonth()).equals("00")) {
            mount = "01";
        } else {
            mount = mount + (persianCalendar.getPersianMonth() + 1);
        }
        day = day + (persianCalendar.getPersianDay());
        lastDate = year + "/" + mount.substring(mount.length() - 2) + "/" + day.substring(day.length() - 2);


        grid = Integer.parseInt(callMethod.ReadString("Grid"));
        binding.bBydateADate.setText(date);
        GetDataFromDataBase();


        binding.bBydateABtn.setOnClickListener(view -> {
            goods.clear();

            persianCalendar = new PersianCalendar();
            if (!binding.bBydateADate.getText().toString().equals("")) {
                date = binding.bBydateADate.getText().toString();
            } else {
                date = "7";
            }

            persianCalendar.setPersianDate(
                    persianCalendar.getPersianYear(),
                    persianCalendar.getPersianMonth(),
                    persianCalendar.getPersianDay() - Integer.parseInt(date)
            );

            year = "";
            mount = "0";
            day = "0";

            year = year + persianCalendar.getPersianYear();

            if (String.valueOf(persianCalendar.getPersianMonth()).equals("11")) {
                mount = "12";
            } else if (String.valueOf(persianCalendar.getPersianMonth()).equals("00")) {
                mount = "01";
            } else {
                mount = mount + (persianCalendar.getPersianMonth() + 1);
            }
            day = day + (persianCalendar.getPersianDay());
            lastDate = year + "/" + mount.substring(mount.length() - 2) + "/" + day.substring(day.length() - 2);
            GetDataFromDataBase();

        });


        if (callMethod.ReadBoolan("GoodAmount")) {
            binding.bBydateASwitchAmount.setChecked(true);
            binding.bBydateASwitchAmount.setText("موجود");
        } else {
            binding.bBydateASwitchAmount.setChecked(false);
            binding.bBydateASwitchAmount.setText("هردو");
        }

        binding.bBydateASwitchAmount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.bBydateASwitchAmount.setText("موجود");
                callMethod.EditBoolan("GoodAmount", true);
            } else {
                binding.bBydateASwitchAmount.setText("هردو");
                callMethod.EditBoolan("GoodAmount", false);
            }
            if (conter == 0) {
                goods.clear();
                PageMoreData = "0";
                GetDataFromDataBase();
            }
        });

        binding.bBydateAFab.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.broker_buymulti_box);
            Button boxbuy = dialog.findViewById(R.id.b_buymulti_btn);
            final EditText amount_mlti = dialog.findViewById(R.id.b_buymulti_amount);
            final EditText unitratio_mlti = dialog.findViewById(R.id.b_buymulti_unitratio);
            final TextView tv = dialog.findViewById(R.id.b_buymulti_factor);
            String tempvalue = "";
            defultenablesellprice = false;

            for (Good good : Multi_Good) {
                Good goodtempdata = broker_dbh.getGooddata(good.getGoodFieldValue("GoodCode"));


                if (Multi_Good.get(0).equals(good)) {
                    if (goodtempdata.getGoodFieldValue("SellPrice" + broker_dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode"))).equals("")) {
                        tempvalue = "100.0";
                    } else {
                        tempvalue = goodtempdata.getGoodFieldValue("Sellprice" + broker_dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode")));
                    }
                }

                if (!tempvalue.equals(goodtempdata.getGoodFieldValue("Sellprice" + broker_dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode"))))) {
                    defultenablesellprice = true;
                }

            }

            if (defultenablesellprice) {
                unitratio_mlti.setHint(NumberFunctions.PerisanNumber("بر اساس نرخ فروش"));
            } else {
                unitratio_mlti.setText(NumberFunctions.PerisanNumber(String.valueOf(100 - Integer.parseInt(tempvalue.substring(0, tempvalue.length() - 2)))));
            }

            tv.setText(broker_dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
            dialog.show();
            amount_mlti.requestFocus();
            amount_mlti.postDelayed(() -> {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(amount_mlti, InputMethodManager.SHOW_IMPLICIT);
            }, 500);

            boxbuy.setOnClickListener(view -> {
                if (unitratio_mlti.getText().toString().equals("بر اساس نرخ فروش")) {
                    unitratio_mlti.setText("100.0");
                }
                String AmountMulti = amount_mlti.getText().toString();
                if (!AmountMulti.equals("")) {

                    if (Integer.parseInt(AmountMulti) != 0) {

                        for (Good good : Multi_Good) {
                            Good gooddata = broker_dbh.getGooddata(good.getGoodFieldValue("GoodCode"));
                            String temppercent;
                            if (gooddata.getGoodFieldValue("SellPrice" + broker_dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode"))).equals("")) {
                                temppercent = "100.0";
                            } else {
                                temppercent = gooddata.getGoodFieldValue("Sellprice" + broker_dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode")));
                            }

                            if (unitratio_mlti.getText().toString().equals("")) {
                                temppercent = String.valueOf(100 - Integer.parseInt(temppercent.substring(0, temppercent.length() - 2)));
                            } else {
                                temppercent = NumberFunctions.EnglishNumber(unitratio_mlti.getText().toString());
                            }
                            if (Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) > 0) {
                                long Pricetemp = (long) Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) - ((long) Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) * Integer.parseInt(temppercent) / 100);
                                broker_dbh.InsertPreFactorwithPercent(callMethod.ReadString("PreFactorCode"),
                                        good.getGoodFieldValue("GoodCode"),
                                        AmountMulti,
                                        String.valueOf(Pricetemp),
                                        "0");
                            } else {
                                broker_dbh.InsertPreFactor(callMethod.ReadString("PreFactorCode"),
                                        good.getGoodFieldValue("GoodCode"),
                                        AmountMulti,
                                        "0",
                                        "0");
                            }
                        }
                        callMethod.showToast("به سبد خرید اضافه شد");

                        dialog.dismiss();
                        item_multi.findItem(R.id.b_menu_multi).setVisible(false);
                        for (Good good : goods) {
                            good.setCheck(false);
                        }
                        Multi_Good.clear();
                        broker_goodAdapter = new Broker_GoodAdapter(goods, this);
                        broker_goodAdapter.multi_select = false;
                        gridLayoutManager = new GridLayoutManager(this, grid);
                        gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
                        binding.bBydateARecycler.setLayoutManager(gridLayoutManager);
                        binding.bBydateARecycler.setAdapter(broker_goodAdapter);
                        binding.bBydateARecycler.setItemAnimator(new DefaultItemAnimator());
                        binding.bBydateAFab.setVisibility(View.GONE);

                    } else {
                        callMethod.showToast("تعداد مورد نظر صحیح نمی باشد.");
                    }
                } else {
                    callMethod.showToast("تعداد مورد نظر صحیح نمی باشد.");
                }
            });


        });


        binding.bBydateARecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 1) {
                            loading = false;
                            PageMoreData = String.valueOf(Integer.parseInt(PageMoreData) + 1);
                            GetMoreDataFromDataBase();
                        }
                    }
                }
            }
        });
        GetDataFromDataBase();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item_multi = menu;
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
                startActivity(intent);

            } else {
                callMethod.showToast("فاکتوری انتخاب نشده است");
            }
            return true;
        }
        if (item.getItemId() == R.id.b_menu_multi) {
            item_multi.findItem(R.id.b_menu_multi).setVisible(false);
            for (Good good : goods) {
                good.setCheck(false);
            }
            Multi_buy.clear();
            broker_goodAdapter = new Broker_GoodAdapter(goods, this);
            broker_goodAdapter.multi_select = false;

            gridLayoutManager = new GridLayoutManager(this, grid);
            gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
            binding.bBydateARecycler.setLayoutManager(gridLayoutManager);
            binding.bBydateARecycler.setAdapter(broker_goodAdapter);
            binding.bBydateARecycler.setItemAnimator(new DefaultItemAnimator());
            binding.bBydateAFab.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void CallRecyclerView() {
        broker_goodAdapter = new Broker_GoodAdapter(goods, this);
        if (broker_goodAdapter.getItemCount() == 0) {
            binding.bBydateATvstatus.setText("کالایی یافت نشد");
            binding.bBydateATvstatus.setVisibility(View.VISIBLE);
            binding.bBydateALottie.setVisibility(View.VISIBLE);
        } else {
            binding.bBydateALottie.setVisibility(View.GONE);
            binding.bBydateATvstatus.setVisibility(View.GONE);
        }
        gridLayoutManager = new GridLayoutManager(this, grid);
        binding.bBydateARecycler.setLayoutManager(gridLayoutManager);
        binding.bBydateARecycler.setAdapter(broker_goodAdapter);
        binding.bBydateARecycler.setItemAnimator(new DefaultItemAnimator());
    }

    public void GetDataFromDataBase() {
        Moregoods.clear();
        Moregoods = broker_dbh.getAllGood_ByDate(lastDate, PageMoreData);
        if (goods.isEmpty()) {
            goods.addAll(Moregoods);
        }
        CallRecyclerView();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void GetMoreDataFromDataBase() {
        loading = true;
        Moregoods.clear();
        Moregoods = broker_dbh.getAllGood_ByDate(lastDate, PageMoreData);

        if (Moregoods.size() > 0) {
            if (goods.isEmpty()) {
                goods.addAll(Moregoods);
            }
            if (goods.size() > (Integer.parseInt(callMethod.ReadString("Grid")) * 10)) {
                goods.addAll(Moregoods);
            }
            broker_goodAdapter.notifyDataSetChanged();
        } else {
            callMethod.showToast("کالایی بیشتری یافت نشد");
            PageMoreData = String.valueOf(Integer.parseInt(PageMoreData) - 1);
        }

    }


    public void good_select_function(Good good) {

        if (!Multi_Good.contains(good)) {
            Multi_Good.add(good);

            binding.bBydateAFab.setVisibility(View.VISIBLE);
            item_multi.findItem(R.id.b_menu_multi).setVisible(true);
        } else {
            Multi_Good.remove(good);

            if (Multi_Good.size() < 1) {
                binding.bBydateAFab.setVisibility(View.GONE);
                broker_goodAdapter.multi_select = false;
                item_multi.findItem(R.id.b_menu_multi).setVisible(false);
            }
        }
    }

    public void factorState() {
        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            binding.bBydateACustomer.setText("فاکتوری انتخاب نشده");
            binding.bBydateALlSumFactor.setVisibility(View.GONE);
        } else {
            binding.bBydateALlSumFactor.setVisibility(View.VISIBLE);
            binding.bBydateACustomer.setText(NumberFunctions.PerisanNumber(broker_dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode"))));
            binding.bBydateASumFactor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(broker_dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        factorState();
        super.onWindowFocusChanged(hasFocus);
    }


}
