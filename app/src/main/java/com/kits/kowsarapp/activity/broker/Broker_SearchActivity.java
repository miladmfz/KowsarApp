package com.kits.kowsarapp.activity.broker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.kits.kowsarapp.adapters.GoodAdapter;
import com.kits.kowsarapp.adapters.GroupLableAdapter;
import com.kits.kowsarapp.application.CallMethod;
import com.kits.kowsarapp.application.Search_box;
import com.kits.kowsarapp.databinding.ActivitySearchBinding;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.Good;
import com.kits.kowsarapp.model.GoodGroup;
import com.kits.kowsarapp.model.NumberFunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class Broker_SearchActivity extends AppCompatActivity {


    public ArrayList<Good> goods = new ArrayList<>();
    public String id = "0";
    public String title = "";
    public String proSearchCondition = "";
    public String AutoSearch = "";
    public String PageMoreData = "0";
    ArrayList<GoodGroup> goodGroups;
    Dialog dialog1;
    Broker_GroupLableAdapter grp_adapter;
    Intent intent;
    Broker_DBH dbh;
    Handler handler;
    ArrayList<Good> Multi_Good = new ArrayList<>();
    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    Broker_GoodAdapter adapter;
    GridLayoutManager gridLayoutManager;
    int pastVisiblesItems = 0, visibleItemCount, totalItemCount;
    Menu item_multi;
    CallMethod callMethod;
    boolean defultenablesellprice;
    ActivitySearchBinding binding;
    private ArrayList<Good> Moregoods = new ArrayList<>();
    private Integer grid;
    private boolean loading = true;
    private Handler keyboardHandler = new Handler();

    //*************************************************
    private Runnable keyboardRunnable = () -> {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.SearchActivityEdtsearch.getWindowToken(),
                0
        );
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        intent();
        Config();

        try {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (dbh.GetColumnscount().equals("0")) {
                    callMethod.showToast("تنظیم جدول از سمت دیتابیس مشکل دارد");
                    finish();
                    dialog1.dismiss();
                } else {
                    init();
                }
            }, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }


    }

    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new Broker_DBH(this, callMethod.ReadString("DatabaseName"));
        handler = new Handler();
        grid = Integer.parseInt(callMethod.ReadString("Grid"));
    }

    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        AutoSearch = data.getString("scan");
        id = data.getString("id");
        title = data.getString("title");
    }

    //
    @SuppressLint("SetTextI18n")
    public void init() {
        if (id.equals("0")) {
            id = dbh.ReadConfig("GroupCodeDefult");
        }

        binding.SearchActivityToolbar.setTitle(title);


        goodGroups = dbh.getAllGroups(id);
        grp_adapter = new Broker_GroupLableAdapter(goodGroups, this);
        binding.SearchActivityGrpRecy.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        binding.SearchActivityGrpRecy.setAdapter(grp_adapter);
        adapter = new GoodAdapter(goods, this);
        if (goodGroups.size() == 0) {
            binding.SearchActivityGrpRecy.getLayoutParams().height = 0;
            binding.SearchActivityGrp.setVisibility(View.GONE);
        }

        setSupportActionBar(binding.SearchActivityToolbar);

        binding.SearchActivityEdtsearch.setOnLongClickListener(v -> {
            binding.SearchActivityEdtsearch.selectAll();
            return false;
        });


        binding.SearchActivityEdtsearch.setFocusable(true);
        binding.SearchActivityEdtsearch.requestFocus();

        binding.SearchActivityEdtsearch.addTextChangedListener(
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
                        handler.postDelayed(() -> {
                            AutoSearch = editable.toString();
                            proSearchCondition = "";
                            GetDataFromDataBase();
                            binding.SearchActivityEdtsearch.setFocusable(true);
                            binding.SearchActivityEdtsearch.requestFocus();
                            binding.SearchActivityEdtsearch.selectAll();

                            if (callMethod.ReadBoolan("keyboardRunnable")) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(binding.SearchActivityEdtsearch, InputMethodManager.SHOW_IMPLICIT);


                                keyboardHandler.removeCallbacks(keyboardRunnable);
                                keyboardHandler.postDelayed(keyboardRunnable,
                                        Integer.parseInt(callMethod.ReadString("Delay")) + Integer.parseInt(callMethod.ReadString("Delay"))
                                );

                            }

                        }, Integer.parseInt(callMethod.ReadString("Delay")));


                    }
                });

        binding.SearchActivityScan.setOnClickListener(view -> {
            intent = new Intent(this, Broker_ScanCodeActivity.class);
            startActivity(intent);
            finish();
        });

        binding.SearchActivityGrp.setOnClickListener(view -> {
            if (binding.SearchActivityGrpRecy.getVisibility() == View.GONE) {
                binding.SearchActivityGrpRecy.setVisibility(View.VISIBLE);
            } else {
                binding.SearchActivityGrpRecy.setVisibility(View.GONE);
            }
        });


        binding.SearchActivityProSearch.setOnClickListener(view -> {
            Search_box search_box = new Search_box(this);
            search_box.search_pro();
        });

        binding.SearchActivityswitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.SearchActivityswitch.setText("فعال");
                callMethod.EditBoolan("ActiveStack", true);
            } else {

                binding.SearchActivityswitch.setText("فعال -غیرفعال");
                callMethod.EditBoolan("ActiveStack", false);
            }

            binding.SearchActivityEdtsearch.setText(AutoSearch);
        });
        binding.SearchActivityswitchAmount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.SearchActivityswitchAmount.setText("موجود");
                callMethod.EditBoolan("GoodAmount", true);
            } else {
                binding.SearchActivityswitchAmount.setText("هردو");
                callMethod.EditBoolan("GoodAmount", false);
            }

            binding.SearchActivityEdtsearch.setText(AutoSearch);
        });


        binding.SearchActivityFab.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.box_multi_buy);
            Button boxbuy = dialog.findViewById(R.id.box_multi_buy_btn);
            final EditText amount_mlti = dialog.findViewById(R.id.box_multi_buy_amount);
            final EditText unitratio_mlti = dialog.findViewById(R.id.box_multi_unitratio);
            final TextView tv = dialog.findViewById(R.id.box_multi_buy_factor);
            String tempvalue = "";
            defultenablesellprice = false;
            Good goodtempdata;

            for (Good good : Multi_Good) {

                goodtempdata = dbh.getGooddata(good.getGoodFieldValue("GoodCode"));

                if (Multi_Good.get(0).equals(good)) {
                    if (goodtempdata.getGoodFieldValue("SellPrice" + dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode"))).equals("")) {
                        tempvalue = "100.0";
                    } else {
                        tempvalue = goodtempdata.getGoodFieldValue("Sellprice" + dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode")));
                    }
                }

                if (!tempvalue.equals(goodtempdata.getGoodFieldValue("Sellprice" + dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode"))))) {
                    defultenablesellprice = true;
                }

            }

            if (defultenablesellprice) {
                unitratio_mlti.setHint(NumberFunctions.PerisanNumber("بر اساس نرخ فروش"));
            } else {
                unitratio_mlti.setText(NumberFunctions.PerisanNumber(String.valueOf(100 - Integer.parseInt(tempvalue.substring(0, tempvalue.length() - 2)))));
            }

            tv.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
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
                            Good gooddata = dbh.getGooddata(good.getGoodFieldValue("GoodCode"));
                            String temppercent;
                            if (gooddata.getGoodFieldValue("SellPrice" + dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode"))).equals("")) {
                                temppercent = "100.0";
                            } else {
                                temppercent = gooddata.getGoodFieldValue("Sellprice" + dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode")));
                            }

                            if (unitratio_mlti.getText().toString().equals("")) {
                                temppercent = String.valueOf(100 - Integer.parseInt(temppercent.substring(0, temppercent.length() - 2)));
                            } else {
                                temppercent = NumberFunctions.EnglishNumber(unitratio_mlti.getText().toString());
                            }
                            if (Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) > 0) {
                                long Pricetemp = (long) Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) - ((long) Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) * Integer.parseInt(temppercent) / 100);
                                dbh.InsertPreFactorwithPercent(callMethod.ReadString("PreFactorCode"),
                                        good.getGoodFieldValue("GoodCode"),
                                        AmountMulti,
                                        String.valueOf(Pricetemp),
                                        "0");
                            } else {
                                dbh.InsertPreFactor(callMethod.ReadString("PreFactorCode"),
                                        good.getGoodFieldValue("GoodCode"),
                                        AmountMulti,
                                        "0",
                                        "0");
                            }
                        }
                        callMethod.showToast("به سبد خرید اضافه شد");

                        dialog.dismiss();
                        item_multi.findItem(R.id.menu_multi).setVisible(false);
                        for (Good good : goods) {
                            good.setCheck(false);
                        }
                        Multi_Good.clear();
                        adapter = new GoodAdapter(goods, this);
                        adapter.multi_select = false;
                        gridLayoutManager = new GridLayoutManager(this, grid);
                        gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
                        binding.SearchActivityAllgood.setLayoutManager(gridLayoutManager);
                        binding.SearchActivityAllgood.setAdapter(adapter);
                        binding.SearchActivityAllgood.setItemAnimator(new DefaultItemAnimator());
                        binding.SearchActivityFab.setVisibility(View.GONE);

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

        binding.SearchActivityAllgood.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            binding.SearchActivityProg.setVisibility(View.VISIBLE);
                            GetMoreDataFromDataBase();
                        }
                    }
                }
            }
        });
        binding.SearchActivityEdtsearch.setText(AutoSearch);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item_multi = menu;
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                intent = new Intent(this, BasketActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                startActivity(intent);
            } else {
                callMethod.showToast("فاکتوری انتخاب نشده است");
            }
            return true;
        }
        if (item.getItemId() == R.id.menu_multi) {
            item_multi.findItem(R.id.menu_multi).setVisible(false);
            for (Good good : goods) {
                good.setCheck(false);
            }
            Multi_Good.clear();
            adapter.multi_select = false;

            adapter = new GoodAdapter(goods, this);
            gridLayoutManager = new GridLayoutManager(this, grid);
            gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
            binding.SearchActivityAllgood.setLayoutManager(gridLayoutManager);
            binding.SearchActivityAllgood.setAdapter(adapter);
            binding.SearchActivityAllgood.setItemAnimator(new DefaultItemAnimator());
            binding.SearchActivityFab.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void GetDataFromDataBase() {
        goods.clear();
        Multi_Good.clear();
        PageMoreData = "0";

        binding.SearchActivityFab.setVisibility(View.GONE);
        item_multi.findItem(R.id.menu_multi).setVisible(false);

        loading = true;
        Moregoods.clear();

        if (proSearchCondition.equals("")) {
            Moregoods = dbh.getAllGood(NumberFunctions.EnglishNumber(AutoSearch), id, PageMoreData);
        } else {
            Moregoods = dbh.getAllGood_Extended(NumberFunctions.EnglishNumber(proSearchCondition), id, PageMoreData);
        }
        if (goods.isEmpty()) {
            goods.addAll(Moregoods);
        }
        CallRecyclerView();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void GetMoreDataFromDataBase() {
        Moregoods.clear();
        if (proSearchCondition.equals("")) {
            Moregoods = dbh.getAllGood(NumberFunctions.EnglishNumber(AutoSearch), id, PageMoreData);
        } else {
            Moregoods = dbh.getAllGood_Extended(NumberFunctions.EnglishNumber(proSearchCondition), id, PageMoreData);
        }
        if (Moregoods.size() > 0) {
            if (goods.isEmpty()) {
                goods.addAll(Moregoods);
            }
            if (goods.size() > (Integer.parseInt(callMethod.ReadString("Grid")) * 10)) {
                goods.addAll(Moregoods);
            }
            adapter.notifyDataSetChanged();
            binding.SearchActivityProg.setVisibility(View.GONE);
            loading = true;
        } else {
            loading = false;
            binding.SearchActivityProg.setVisibility(View.GONE);
            callMethod.showToast("کالای بیشتری یافت نشد");
            PageMoreData = String.valueOf(Integer.parseInt(PageMoreData) - 1);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void CallRecyclerView() {
        adapter.notifyDataSetChanged();


        if (adapter.getItemCount() == 0) {
            binding.SearchActivityTvstatus.setText("کالایی یافت نشد");
            binding.SearchActivityTvstatus.setVisibility(View.VISIBLE);
            binding.SearchActivityLottie.setVisibility(View.VISIBLE);
        } else {
            binding.SearchActivityLottie.setVisibility(View.GONE);
            binding.SearchActivityTvstatus.setVisibility(View.GONE);
        }
        gridLayoutManager = new GridLayoutManager(this, grid);
        binding.SearchActivityAllgood.setLayoutManager(gridLayoutManager);
        binding.SearchActivityAllgood.setAdapter(adapter);
        binding.SearchActivityAllgood.setItemAnimator(new DefaultItemAnimator());
        binding.SearchActivityProg.setVisibility(View.GONE);
    }


    public void good_select_function(Good good) {

        if (!Multi_Good.contains(good)) {
            Multi_Good.add(good);
            binding.SearchActivityFab.setVisibility(View.VISIBLE);
            item_multi.findItem(R.id.menu_multi).setVisible(true);
        } else {
            Multi_Good.remove(good);
            if (Multi_Good.size() < 1) {
                binding.SearchActivityFab.setVisibility(View.GONE);
                adapter.multi_select = false;
                item_multi.findItem(R.id.menu_multi).setVisible(false);
            }
        }
    }

    public void RefreshState() {

        binding.SearchActivityEdtsearch.selectAll();

        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            binding.SearchActivityCustomer.setText("فاکتوری انتخاب نشده");
            binding.SearchActivityLlSumFactor.setVisibility(View.GONE);
        } else {
            binding.SearchActivityLlSumFactor.setVisibility(View.VISIBLE);
            binding.SearchActivityCustomer.setText(NumberFunctions.PerisanNumber(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode"))));
            binding.SearchActivitySumFactor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
        }

        if (callMethod.ReadBoolan("ActiveStack")) {
            binding.SearchActivityswitch.setChecked(true);
            binding.SearchActivityswitch.setText("فعال");
        } else {
            binding.SearchActivityswitch.setChecked(false);
            binding.SearchActivityswitch.setText("فعال -غیرفعال");
        }

        if (callMethod.ReadBoolan("GoodAmount")) {
            binding.SearchActivityswitchAmount.setChecked(true);
            binding.SearchActivityswitchAmount.setText("موجود");
        } else {
            binding.SearchActivityswitchAmount.setChecked(false);
            binding.SearchActivityswitchAmount.setText("هردو");
        }



        if (callMethod.ReadBoolan("keyboardRunnable")) {
            binding.SearchActivityEdtsearch.setFocusable(true);
            binding.SearchActivityEdtsearch.requestFocus();
            binding.SearchActivityEdtsearch.selectAll();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.SearchActivityEdtsearch, InputMethodManager.SHOW_IMPLICIT);
            keyboardHandler.removeCallbacks(keyboardRunnable);
            keyboardHandler.postDelayed(keyboardRunnable,
                    0
            );
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        RefreshState();


        super.onWindowFocusChanged(hasFocus);
    }
}




