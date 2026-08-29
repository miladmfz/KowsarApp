package com.kits.kowsarapp.activity.broker;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.google.android.material.color.MaterialColors;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.adapter.broker.Broker_SliderAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.databinding.BrokerActivityDetailBinding;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Broker_DetailActivity extends AppCompatActivity {

    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0");

    private CallMethod callMethod;
    private Broker_DBH broker_dbh;
    private Broker_Action broker_action;
    private Broker_APIInterface broker_apiInterface;

    private Good gooddetail;

    private ArrayList<Column> Columns = new ArrayList<>();
    private ArrayList<Good> imagelists = new ArrayList<>();

    private String id = "";
    private Intent intent;

    private BrokerActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(
                getSharedPreferences("ThemePrefs", MODE_PRIVATE)
                        .getInt("selectedTheme", R.style.RoyalGoldTheme)
        );

        binding = BrokerActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        readIntent();
        Config();

        new Handler(Looper.getMainLooper()).postDelayed(this::init, 100);
    }

    private void readIntent() {
        Bundle data = getIntent().getExtras();

        if (data != null) {
            String receivedId = data.getString("id");

            if (receivedId != null) {
                id = receivedId;
            }
        }
    }

    public void Config() {
        callMethod = new CallMethod(this);

        broker_dbh = new Broker_DBH(
                this,
                callMethod.ReadString("DatabaseName")
        );

        broker_action = new Broker_Action(this);

        broker_apiInterface = APIClient
                .getCleint(callMethod.ReadString("ServerURLUse"))
                .create(Broker_APIInterface.class);

        Columns = new ArrayList<>();

        setSupportActionBar(binding.bDetailAToolbar);
    }

    public void init() {
        if (id == null || id.trim().isEmpty()) {
            callMethod.showToast("شناسه کالا مشخص نشده است");
            return;
        }

        broker_dbh.getGoodByCodeAsync(
                id,
                new Broker_DBH.DbCallback<Good>() {

                    @Override
                    public void onResult(Good result) {
                        if (result == null) {
                            callMethod.showToast("اطلاعات کالا پیدا نشد");
                            return;
                        }

                        gooddetail = result;
                        Columns = broker_dbh.GetColumns(id, "", "0");

                        loadFactorInfo();
                        loadGoodProperties();
                        loadImages();
                        setupBuyButton();
                    }

                    @Override
                    public void onError(Exception e) {
                        String message = e.getMessage() == null
                                ? "Unknown error"
                                : e.getMessage();

                        callMethod.Log(message);
                        callMethod.showToast("خطا در دریافت اطلاعات کالا");
                    }
                }
        );
    }

    private void loadFactorInfo() {
        String preFactorCode = callMethod.ReadString("PreFactorCode");

        if (safeInt(preFactorCode, 0) == 0) {
            binding.bDetailACustomer.setText("فاکتوری انتخاب نشده");
            binding.bDetailALlSumFactor.setVisibility(View.GONE);
            return;
        }

        binding.bDetailALlSumFactor.setVisibility(View.VISIBLE);

        String customerName = broker_dbh.getFactorCustomer(preFactorCode);
        binding.bDetailACustomer.setText(
                NumberFunctions.PerisanNumber(customerName)
        );

        String factorSum = broker_dbh.getFactorSum(preFactorCode);
        binding.bDetailASumFactor.setText(formatNumber(factorSum));
    }

    private void loadGoodProperties() {
        /*
         * اولین View داخل این Container، عنوان بخش است.
         * Viewهای ساخته‌شده قبلی حذف می‌شوند تا اطلاعات تکراری نشوند.
         */
        while (binding.bDetailALineProperty.getChildCount() > 1) {
            binding.bDetailALineProperty.removeViewAt(1);
        }

        if (Columns == null || Columns.isEmpty()) {
            return;
        }

        for (Column column : Columns) {
            int sortOrder = safeInt(
                    column.getColumnFieldValue("SortOrder"),
                    0
            );

            if (sortOrder <= 0) {
                continue;
            }

            String title = column.getColumnFieldValue("ColumnDesc");
            String columnName = column.getColumnFieldValue("columnname");
            String body = gooddetail.getGoodFieldValue(columnName);

            CreateView(title, body);
        }
    }

    private void loadImages() {
        imagelists = broker_dbh.GetksrImageCodes(
                gooddetail.getGoodFieldValue("GoodCode")
        );

        SliderView();
    }

    private void setupBuyButton() {
        boolean isActive = "1".equals(
                gooddetail.getGoodFieldValue("ActiveStack")
        );

        int activeColor = MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorPrimary,
                ContextCompat.getColor(this, R.color.green_600)
        );

        int inactiveColor = ContextCompat.getColor(
                this,
                R.color.grey_700
        );

        binding.bDetailABtnbuy.setBackgroundTintList(
                ColorStateList.valueOf(
                        isActive ? activeColor : inactiveColor
                )
        );

        if (!callMethod.ReadBoolan("ShowGoodBuyBtn")) {
            binding.bDetailABtnbuy.setVisibility(View.GONE);
            return;
        }

        binding.bDetailABtnbuy.setVisibility(View.VISIBLE);

        if (callMethod.ReadBoolan("CanUseInactive")) {
            binding.bDetailABtnbuy.setText("افزودن به سبد خرید");
        } else if (isActive) {
            binding.bDetailABtnbuy.setText("افزودن به سبد خرید");
        } else {
            binding.bDetailABtnbuy.setText("کالای غیرفعال");
        }

        binding.bDetailABtnbuy.setOnClickListener(view -> {
            boolean canUseInactive =
                    callMethod.ReadBoolan("CanUseInactive");

            if (!canUseInactive && !isActive) {
                callMethod.showToast("این کالا غیرفعال می‌باشد");
                return;
            }

            openBuyDialogOrFactor();
        });
    }

    private void openBuyDialogOrFactor() {
        int preFactorCode = safeInt(
                callMethod.ReadString("PreFactorCode"),
                0
        );

        if (preFactorCode != 0) {
            broker_action.buydialog(
                    gooddetail.getGoodFieldValue("GoodCode"),
                    "0"
            );

            return;
        }

        intent = new Intent(
                Broker_DetailActivity.this,
                Broker_PFOpenActivity.class
        );

        intent.putExtra("fac", "0");
        startActivity(intent);
    }

    public void CreateView(String title, String body) {
        int primaryColor = MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorPrimary,
                Color.DKGRAY
        );

        int surfaceColor = MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorSurface,
                Color.WHITE
        );

        int onSurfaceColor = MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorOnSurface,
                Color.BLACK
        );

        int softPrimaryColor = ColorUtils.blendARGB(
                surfaceColor,
                primaryColor,
                0.10f
        );

        int dividerColor = ColorUtils.setAlphaComponent(
                primaryColor,
                45
        );

        int titleSize = clamp(
                safeInt(callMethod.ReadString("TitleSize"), 12),
                10,
                15
        );

        int bodySize = clamp(
                safeInt(callMethod.ReadString("BodySize"), 13),
                11,
                16
        );

        LinearLayoutCompat row = new LinearLayoutCompat(this);

        row.setOrientation(LinearLayoutCompat.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        row.setPadding(dp(3), dp(2), dp(3), dp(2));

        row.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(44)
                )
        );

        TextView titleView = new TextView(this);

        titleView.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        0.40f
                )
        );

        titleView.setText(
                NumberFunctions.PerisanNumber(
                        safeText(title)
                )
        );

        titleView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        titleView.setPaddingRelative(dp(8), 0, dp(6), 0);
        titleView.setMaxLines(1);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
        titleView.setTextColor(primaryColor);
        titleView.setTypeface(null, Typeface.BOLD);

        titleView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                titleSize
        );

        titleView.setBackground(
                createBackground(
                        softPrimaryColor,
                        dp(6),
                        Color.TRANSPARENT,
                        0
                )
        );

        row.addView(titleView);

        View verticalDivider = new View(this);

        LinearLayoutCompat.LayoutParams verticalDividerParams =
                new LinearLayoutCompat.LayoutParams(
                        dp(1),
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                );

        verticalDividerParams.setMargins(
                dp(3),
                dp(5),
                dp(3),
                dp(5)
        );

        verticalDivider.setLayoutParams(verticalDividerParams);
        verticalDivider.setBackgroundColor(dividerColor);

        row.addView(verticalDivider);

        TextView bodyView = new TextView(this);

        bodyView.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        0.60f
                )
        );

        bodyView.setText(formatValue(body));
        bodyView.setGravity(Gravity.CENTER);
        bodyView.setPaddingRelative(dp(6), 0, dp(6), 0);
        bodyView.setMaxLines(2);
        bodyView.setEllipsize(TextUtils.TruncateAt.END);
        bodyView.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
        bodyView.setTextColor(onSurfaceColor);
        bodyView.setTextIsSelectable(true);

        bodyView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                bodySize
        );

        bodyView.setBackground(
                createBackground(
                        surfaceColor,
                        dp(6),
                        Color.TRANSPARENT,
                        0
                )
        );

        row.addView(bodyView);

        View horizontalDivider = new View(this);

        horizontalDivider.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(1)
                )
        );

        horizontalDivider.setBackgroundColor(dividerColor);

        binding.bDetailALineProperty.addView(row);
        binding.bDetailALineProperty.addView(horizontalDivider);
    }

    private String formatValue(String value) {
        String safeValue = safeText(value);

        if (safeValue.isEmpty()) {
            return "-";
        }

        try {
            double number = Double.parseDouble(
                    safeValue.replace(",", "")
            );

            if (Math.abs(number) >= 1000) {
                return NumberFunctions.PerisanNumber(
                        decimalFormat.format(number)
                );
            }
        } catch (Exception ignored) {
        }

        return NumberFunctions.PerisanNumber(safeValue);
    }

    private String formatNumber(String value) {
        try {
            double number = Double.parseDouble(
                    safeText(value).replace(",", "")
            );

            return NumberFunctions.PerisanNumber(
                    decimalFormat.format(number)
            );

        } catch (Exception ignored) {
            return NumberFunctions.PerisanNumber(
                    safeText(value)
            );
        }
    }

    private GradientDrawable createBackground(
            int backgroundColor,
            float radius,
            int strokeColor,
            int strokeWidth
    ) {
        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(backgroundColor);
        drawable.setCornerRadius(radius);

        if (strokeWidth > 0) {
            drawable.setStroke(strokeWidth, strokeColor);
        }

        return drawable;
    }

    private int dp(int value) {
        return Math.round(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        value,
                        getResources().getDisplayMetrics()
                )
        );
    }

    private int safeInt(String value, int fallback) {
        try {
            return Integer.parseInt(safeText(value));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.broker_options_menu,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.b_bag_shop) {
            int preFactorCode = safeInt(
                    callMethod.ReadString("PreFactorCode"),
                    0
            );

            if (preFactorCode != 0) {
                intent = new Intent(
                        this,
                        Broker_BasketActivity.class
                );

                intent.putExtra(
                        "PreFac",
                        callMethod.ReadString("PreFactorCode")
                );

                intent.putExtra("showflag", "2");

            } else {
                callMethod.showToast("سبد خرید خالی می‌باشد");

                intent = new Intent(
                        this,
                        Broker_PFOpenActivity.class
                );
            }

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void SliderView() {
        if (imagelists == null || imagelists.isEmpty()) {
            binding.bDetailALnImageSlider.setVisibility(View.GONE);
            return;
        }

        binding.bDetailALnImageSlider.setVisibility(View.VISIBLE);

        Broker_SliderAdapter adapter =
                new Broker_SliderAdapter(
                        imagelists,
                        true,
                        this
                );

        binding.bDetailAImageSlider.setSliderAdapter(adapter);

        binding.bDetailAImageSlider.setIndicatorAnimation(
                IndicatorAnimations.SCALE
        );

        binding.bDetailAImageSlider.setSliderTransformAnimation(
                SliderAnimations.SIMPLETRANSFORMATION
        );

        binding.bDetailAImageSlider.setAutoCycleDirection(
                SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        );

        binding.bDetailAImageSlider.setIndicatorSelectedColor(
                Color.WHITE
        );

        binding.bDetailAImageSlider.setIndicatorUnselectedColor(
                Color.GRAY
        );

        binding.bDetailAImageSlider.setScrollTimeInSec(3);
        binding.bDetailAImageSlider.startAutoCycle();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        finish();
        startActivity(getIntent());
    }
}