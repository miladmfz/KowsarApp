package com.kits.kowsarapp.activity.broker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.kits.kowsarapp.adapter.broker.Broker_GroupLableAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.LatestRequestGate;
import com.kits.kowsarapp.application.base.SafeValueParser;
import com.kits.kowsarapp.application.broker.Broker_ProSearch;
import com.kits.kowsarapp.databinding.BrokerActivitySearchBinding;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.GoodGroup;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class Broker_SearchActivity extends AppCompatActivity {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");

    Menu item_multi;
    Dialog dialog1;
    Intent intent;
    final Handler handler = new Handler(Looper.getMainLooper());
    final Handler lifecycleHandler = new Handler(Looper.getMainLooper());
    final Handler keyboardHandler = new Handler(Looper.getMainLooper());
    final LatestRequestGate dataRequestGate = new LatestRequestGate();
    CallMethod callMethod;
    GridLayoutManager gridLayoutManager;


    Broker_GroupLableAdapter broker_groupLableAdapter;
    Broker_DBH broker_dbh;
    Broker_GoodAdapter broker_goodAdapter;


    public final ArrayList<Good> goods = new ArrayList<>();
    ArrayList<Good> Multi_Good = new ArrayList<>();
    ArrayList<Good> Moregoods = new ArrayList<>();
    ArrayList<GoodGroup> goodGroups= new ArrayList<>();


    public String id = "0";
    public String title = "";
    public String proSearchCondition = "";
    public String AutoSearch = "";
    public String PageMoreData = "0";

    Integer pastVisiblesItems = 0, visibleItemCount, totalItemCount;
    Integer grid;

    Boolean defultenablesellprice;
    Boolean loading = true;


    BrokerActivitySearchBinding binding;

    //*************************************************
    private final Runnable keyboardRunnable = () -> {
        if (!isUiActive()) return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.bSearchAEdtsearch.getWindowToken(),
                0
        );
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        binding = BrokerActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


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
            lifecycleHandler.postDelayed(() -> {
                if (!isUiActive()) return;
                if (broker_dbh.GetColumnscount().equals("0")) {
                    callMethod.showToast("تنظیم جدول از سمت دیتابیس مشکل دارد");
                    finish();
                    dialog1.dismiss();
                } else {
                    init();
                }
            }, 100);
            lifecycleHandler.postDelayed(this::dismissLoadingDialog, 1000);
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }


    }

    public void Config() {

        callMethod = new CallMethod(this);
        broker_dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));
        grid = Math.max(1, SafeValueParser.intOrDefault(callMethod.ReadString("Grid"), 1));

    }

    public void intent() {
        Bundle data = getIntent().getExtras();
        AutoSearch = data == null ? "" : safeString(data.getString("scan"), "");
        id = data == null ? "0" : safeString(data.getString("id"), "0");
        title = data == null ? "" : safeString(data.getString("title"), "");
    }

    //
    @SuppressLint("SetTextI18n")
    public void init() {
        if (id.equals("0")) {
            id = broker_dbh.ReadConfig("GroupCodeDefult");
        }

        binding.bSearchAToolbar.setTitle(title);


        goodGroups = broker_dbh.getAllGroups(id);
        broker_groupLableAdapter = new Broker_GroupLableAdapter(goodGroups, this);
        binding.bSearchAGrpRecy.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        binding.bSearchAGrpRecy.setAdapter(broker_groupLableAdapter);
        broker_goodAdapter = new Broker_GoodAdapter(goods, this);
        if (goodGroups.size() == 0) {
            binding.bSearchAGrpRecy.getLayoutParams().height = 0;
            binding.bSearchAGrp.setVisibility(View.GONE);
        }

        setSupportActionBar(binding.bSearchAToolbar);

        binding.bSearchAEdtsearch.setOnLongClickListener(v -> {
            binding.bSearchAEdtsearch.selectAll();
            return false;
        });

        if(callMethod.ReadBoolan("ShowSearchBand")){
            binding.bSearchALlCustomer.setVisibility(View.VISIBLE);
            binding.bSearchALlBtngrp.setVisibility(View.VISIBLE);
        }else{
            binding.bSearchALlCustomer.setVisibility(View.GONE);
            binding.bSearchALlBtngrp.setVisibility(View.GONE);
        }



        binding.bSearchAEdtsearch.setFocusable(true);
        binding.bSearchAEdtsearch.requestFocus();

        binding.bSearchAEdtsearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(final Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        String query = editable == null ? "" : editable.toString();
                        handler.postDelayed(() -> {
                            if (!isUiActive()) return;
                            AutoSearch = query;
                            proSearchCondition = "";
                            GetDataFromDataBase();
                            binding.bSearchAEdtsearch.setFocusable(true);
                            binding.bSearchAEdtsearch.requestFocus();
                            binding.bSearchAEdtsearch.selectAll();

                            if (callMethod.ReadBoolan("keyboardRunnable")) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(binding.bSearchAEdtsearch, InputMethodManager.SHOW_IMPLICIT);


                                keyboardHandler.removeCallbacks(keyboardRunnable);
                                keyboardHandler.postDelayed(keyboardRunnable,
                                        safeSearchDelay() * 2L
                                );

                            }

                        }, safeSearchDelay());


                    }
                });

        binding.bSearchAScan.setOnClickListener(view -> {
            intent = new Intent(this, Broker_ScanCodeActivity.class);
            startActivity(intent);
            finish();
        });

        binding.bSearchAGrp.setOnClickListener(view -> {
            if (binding.bSearchAGrpRecy.getVisibility() == View.GONE) {
                binding.bSearchAGrpRecy.setVisibility(View.VISIBLE);
            } else {
                binding.bSearchAGrpRecy.setVisibility(View.GONE);
            }
        });


        binding.bSearchAProSearch.setOnClickListener(view -> {
            Broker_ProSearch search_box = new Broker_ProSearch(this);
            search_box.search_pro();
        });

        binding.bSearchASwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.bSearchASwitch.setText("فعال");
                callMethod.EditBoolan("ActiveStack", true);
            } else {

                binding.bSearchASwitch.setText("فعال -غیرفعال");
                callMethod.EditBoolan("ActiveStack", false);
            }

            binding.bSearchAEdtsearch.setText(AutoSearch);
        });
        binding.bSearchASwitchAmount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.bSearchASwitchAmount.setText("موجود");
                callMethod.EditBoolan("GoodAmount", true);
            } else {
                binding.bSearchASwitchAmount.setText("هردو");
                callMethod.EditBoolan("GoodAmount", false);
            }

            binding.bSearchAEdtsearch.setText(AutoSearch);
        });


        binding.bSearchAFab.setOnClickListener(v -> {
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
            Good goodtempdata;

            for (Good good : Multi_Good) {

                goodtempdata = broker_dbh.getGooddata(good.getGoodFieldValue("GoodCode"));

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

                    if (Double.parseDouble(AmountMulti) != 0) {

                        for (Good good : Multi_Good) {
                            Good gooddata = broker_dbh.getGooddata(good.getGoodFieldValue("GoodCode"));
                            String temppercent;
                            if (gooddata.getGoodFieldValue("SellPrice" + broker_dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode"))).equals("")) {
                                temppercent = "100.0";
                            } else {
                                temppercent = gooddata.getGoodFieldValue("Sellprice" + broker_dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode")));
                            }

                            if (unitratio_mlti.getText().toString().equals("")) {
                                temppercent = String.valueOf(100 - Double.parseDouble(temppercent.substring(0, temppercent.length() - 2)));
                            } else {
                                temppercent = NumberFunctions.EnglishNumber(unitratio_mlti.getText().toString());
                            }
                            double maxSellPrice = Double.parseDouble(good.getGoodFieldValue("MaxSellPrice"));
                            double percent = Double.parseDouble(temppercent);

                            if (maxSellPrice > 0) {

                                long priceTemp = (long) (
                                        maxSellPrice - (maxSellPrice * percent / 100.0)
                                );

                                broker_dbh.InsertPreFactorwithPercent(
                                        callMethod.ReadString("PreFactorCode"),
                                        good.getGoodFieldValue("GoodCode"),
                                        AmountMulti,
                                        String.valueOf(priceTemp),
                                        "0"
                                );

                            } else {

                                broker_dbh.InsertPreFactor(
                                        callMethod.ReadString("PreFactorCode"),
                                        good.getGoodFieldValue("GoodCode"),
                                        AmountMulti,
                                        "0",
                                        "0"
                                );
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
                        binding.bSearchAAllgood.setLayoutManager(gridLayoutManager);
                        binding.bSearchAAllgood.setAdapter(broker_goodAdapter);
                        binding.bSearchAAllgood.setItemAnimator(new DefaultItemAnimator());
                        binding.bSearchAFab.setVisibility(View.GONE);

                    } else {
                        callMethod.showToast("تعداد مورد نظر صحیح نمی باشد.");
                    }
                } else {
                    callMethod.showToast("تعداد مورد نظر صحیح نمی باشد.");
                }
            });

            if (unitratio_mlti.hasFocusable()) {
                unitratio_mlti.selectAll();
            }
        });

        binding.bSearchAAllgood.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 1) {
                            loading = false;
                            PageMoreData = String.valueOf(
                                    SafeValueParser.intOrDefault(PageMoreData, 0) + 1);
                            binding.bSearchAProg.setVisibility(View.VISIBLE);
                            GetMoreDataFromDataBase();
                        }
                    }
                }
            }
        });
        binding.bSearchAEdtsearch.setText(AutoSearch);

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
            if (SafeValueParser.intOrDefault(callMethod.ReadString("PreFactorCode"), 0) != 0) {
                intent = new Intent(this, Broker_BasketActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
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
            Multi_Good.clear();
            broker_goodAdapter.multi_select = false;

            broker_goodAdapter = new Broker_GoodAdapter(goods, this);
            gridLayoutManager = new GridLayoutManager(this, grid);
            gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
            binding.bSearchAAllgood.setLayoutManager(gridLayoutManager);
            binding.bSearchAAllgood.setAdapter(broker_goodAdapter);
            binding.bSearchAAllgood.setItemAnimator(new DefaultItemAnimator());
            binding.bSearchAFab.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void GetDataFromDataBase() {
        if (!isUiActive()) return;
        final int requestToken = dataRequestGate.begin();

        goods.clear();
        Multi_Good.clear();
        PageMoreData = "0";

        binding.bSearchAFab.setVisibility(View.GONE);

        if (item_multi != null) {
            item_multi.findItem(R.id.b_menu_multi).setVisible(false);
        }

        loading = true;
        Moregoods.clear();

        Broker_DBH.DbCallback<ArrayList<Good>> callback =
                new Broker_DBH.DbCallback<ArrayList<Good>>() {

                    @Override
                    public void onResult(ArrayList<Good> result) {
                        if (!dataRequestGate.isCurrent(requestToken) || !isUiActive()) return;

                        Moregoods = result == null ? new ArrayList<>() : result;

                        if (goods.isEmpty()) {
                            goods.addAll(Moregoods);
                        }

                        CallRecyclerView();
                    }

                    @Override
                    public void onError(Exception e) {
                        if (!dataRequestGate.isCurrent(requestToken) || !isUiActive()) return;

                        callMethod.Log(e == null ? "Broker search failed" : e.getClass().getSimpleName());

                        binding.bSearchAProg.setVisibility(View.GONE);

                        callMethod.showToast("خطا در دریافت کالاها");
                    }
                };

        if (proSearchCondition.equals("")) {

            broker_dbh.getAllGoodAsync(
                    NumberFunctions.EnglishNumber(AutoSearch),
                    id,
                    PageMoreData,
                    callback
            );

        } else {

            broker_dbh.getAllGood_ExtendedAsync(
                    NumberFunctions.EnglishNumber(proSearchCondition),
                    id,
                    PageMoreData,
                    callback
            );
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void GetMoreDataFromDataBase() {
        if (!isUiActive()) return;
        final int requestToken = dataRequestGate.begin();

        Moregoods.clear();

        Broker_DBH.DbCallback<ArrayList<Good>> callback =
                new Broker_DBH.DbCallback<ArrayList<Good>>() {

                    @Override
                    public void onResult(ArrayList<Good> result) {
                        if (!dataRequestGate.isCurrent(requestToken) || !isUiActive()) return;

                        Moregoods = result == null ? new ArrayList<>() : result;

                        if (Moregoods.size() > 0) {

                            if (goods.isEmpty()) {
                                goods.addAll(Moregoods);
                            }

                            if (goods.size() > (grid * 10)) {
                                goods.addAll(Moregoods);
                            }

                            broker_goodAdapter.notifyDataSetChanged();

                            binding.bSearchAProg.setVisibility(View.GONE);

                            loading = true;

                        } else {

                            loading = false;

                            binding.bSearchAProg.setVisibility(View.GONE);

                            callMethod.showToast("کالای بیشتری یافت نشد");

                            PageMoreData =
                                    String.valueOf(
                                            Math.max(0, SafeValueParser.intOrDefault(PageMoreData, 1) - 1)
                                    );
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (!dataRequestGate.isCurrent(requestToken) || !isUiActive()) return;

                        callMethod.Log(e == null ? "Broker paging failed" : e.getClass().getSimpleName());

                        binding.bSearchAProg.setVisibility(View.GONE);

                        loading = true;

                        callMethod.showToast("خطا در دریافت ادامه کالاها");
                    }
                };

        if (proSearchCondition.equals("")) {

            broker_dbh.getAllGoodAsync(
                    NumberFunctions.EnglishNumber(AutoSearch),
                    id,
                    PageMoreData,
                    callback
            );

        } else {

            broker_dbh.getAllGood_ExtendedAsync(
                    NumberFunctions.EnglishNumber(proSearchCondition),
                    id,
                    PageMoreData,
                    callback
            );
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void CallRecyclerView() {
        broker_goodAdapter.notifyDataSetChanged();


        if (broker_goodAdapter.getItemCount() == 0) {
            binding.bSearchATvstatus.setText("کالایی یافت نشد");
            binding.bSearchATvstatus.setVisibility(View.VISIBLE);
            binding.bSearchALottie.setVisibility(View.VISIBLE);
        } else {
            binding.bSearchALottie.setVisibility(View.GONE);
            binding.bSearchATvstatus.setVisibility(View.GONE);
        }
        gridLayoutManager = new GridLayoutManager(this, grid);
        binding.bSearchAAllgood.setLayoutManager(gridLayoutManager);
        binding.bSearchAAllgood.setAdapter(broker_goodAdapter);
        binding.bSearchAAllgood.setItemAnimator(new DefaultItemAnimator());
        binding.bSearchAProg.setVisibility(View.GONE);
    }


    public void good_select_function(Good good) {

        if (!Multi_Good.contains(good)) {
            Multi_Good.add(good);
            binding.bSearchAFab.setVisibility(View.VISIBLE);
            item_multi.findItem(R.id.b_menu_multi).setVisible(true);
        } else {
            Multi_Good.remove(good);
            if (Multi_Good.size() < 1) {
                binding.bSearchAFab.setVisibility(View.GONE);
                broker_goodAdapter.multi_select = false;
                item_multi.findItem(R.id.b_menu_multi).setVisible(false);
            }
        }
    }

    public void RefreshState() {
        if (!isUiActive()) return;

        binding.bSearchAEdtsearch.selectAll();

        if (SafeValueParser.intOrDefault(callMethod.ReadString("PreFactorCode"), 0) == 0) {
            binding.bSearchACustomer.setText("فاکتوری انتخاب نشده");
            binding.bSearchALlSumFactor.setVisibility(View.GONE);
        } else {
            binding.bSearchALlSumFactor.setVisibility(View.VISIBLE);
            binding.bSearchACustomer.setText(NumberFunctions.PerisanNumber(broker_dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode"))));
            double factorSum = SafeValueParser.doubleOrDefault(
                    broker_dbh.getFactorSum(callMethod.ReadString("PreFactorCode")), 0.0);
            binding.bSearchASumFactor.setText(
                    NumberFunctions.PerisanNumber(decimalFormat.format(factorSum)));
        }

        if (callMethod.ReadBoolan("ActiveStack")) {
            binding.bSearchASwitch.setChecked(true);
            binding.bSearchASwitch.setText("فعال");
        } else {
            binding.bSearchASwitch.setChecked(false);
            binding.bSearchASwitch.setText("فعال -غیرفعال");
        }

        if (callMethod.ReadBoolan("GoodAmount")) {
            binding.bSearchASwitchAmount.setChecked(true);
            binding.bSearchASwitchAmount.setText("موجود");
        } else {
            binding.bSearchASwitchAmount.setChecked(false);
            binding.bSearchASwitchAmount.setText("هردو");
        }



        if (callMethod.ReadBoolan("keyboardRunnable")) {
            binding.bSearchAEdtsearch.setFocusable(true);
            binding.bSearchAEdtsearch.requestFocus();
            binding.bSearchAEdtsearch.selectAll();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.bSearchAEdtsearch, InputMethodManager.SHOW_IMPLICIT);
            keyboardHandler.removeCallbacks(keyboardRunnable);
            keyboardHandler.postDelayed(keyboardRunnable,
                    0
            );
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) RefreshState();


        super.onWindowFocusChanged(hasFocus);
    }

    private int safeSearchDelay() {
        return Math.max(0, SafeValueParser.intOrDefault(callMethod.ReadString("Delay"), 300));
    }

    private static String safeString(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private boolean isUiActive() {
        return binding != null && !isFinishing() && !isDestroyed();
    }

    private void dismissLoadingDialog() {
        if (dialog1 != null && dialog1.isShowing() && !isFinishing()) {
            dialog1.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dataRequestGate.invalidate();
        handler.removeCallbacksAndMessages(null);
        lifecycleHandler.removeCallbacksAndMessages(null);
        keyboardHandler.removeCallbacksAndMessages(null);
        if (dialog1 != null && dialog1.isShowing()) {
            dialog1.dismiss();
        }
        if (broker_dbh != null) {
            broker_dbh.closedb();
        }
        binding = null;
        super.onDestroy();
    }
}




