package com.kits.kowsarapp.activity.ocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.fragment.ocr.OnGoodConfirmListener;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_MergingActivity extends AppCompatActivity implements OnGoodConfirmListener {

    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;

    ArrayList<Ocr_Good> ocr_goods= new ArrayList<>();

    Integer row_counter;

    LinearLayoutCompat ll_main;
    CallMethod callMethod;

    String OrderBy;
    int width=1;
    Ocr_Action ocr_action;
    Handler handler;

    Integer state_category;
    public String searchtarget = "";

    /*
     * Warehouse process state can be supplied through the Intent extra
     * "AppOCRFactorExplain". If it is absent, the saved value is used.
     */
    private String appOCRFactorExplain = "";

    public String BarcodeScan = "";

    private static final int PAGE_SIZE = 20;

    private final ArrayList<Ocr_Good> displayGoods = new ArrayList<>();
    private final LinkedHashMap<String, Integer> groupCounts = new LinkedHashMap<>();

    private int currentPage = 0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(
                getSharedPreferences("ThemePrefs", MODE_PRIVATE)
                        .getInt("selectedTheme", R.style.RoyalGoldTheme)
        );

        setContentView(R.layout.ocr_activity_merging);

        Config();

        final Dialog loadingDialog = new Dialog(this);

        try {
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            Objects.requireNonNull(loadingDialog.getWindow())
                    .setBackgroundDrawableResource(
                            android.R.color.transparent
                    );

            loadingDialog.setContentView(R.layout.ocr_spinner_box);

            TextView loadingText =
                    loadingDialog.findViewById(
                            R.id.ocr_spinner_text
                    );

            loadingText.setText("در حال خواندن اطلاعات");
            loadingDialog.show();

        } catch (Exception exception) {
            callMethod.Log(
                    "Loading dialog error: "
                            + exception.getMessage()
            );
        }


        handler.postDelayed(this::init, 100);

        handler.postDelayed(() -> {
            try {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            } catch (Exception exception) {
                callMethod.Log(
                        "Loading dialog dismiss error: "
                                + exception.getMessage()
                );
            }
        }, 1000);
    }


    public void Config() {

        callMethod = new CallMethod(this);

        appOCRFactorExplain = safeValue(
                getIntent().getStringExtra("AppOCRFactorExplain")
        );

        if (appOCRFactorExplain.isEmpty()) {
            appOCRFactorExplain = safeValue(
                    callMethod.ReadString("AppOCRFactorExplain")
            );
        }

        BarcodeScan = safeValue(
                getIntent().getStringExtra("BarcodeScan")
        );

        ocr_action = new Ocr_Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

        handler=new Handler();
        ll_main = findViewById(R.id.ocr_merg_f_layout);
        callMethod.EditString("FactorDbName",callMethod.ReadString("DbName"));



        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;


    }



    public void init(){

        try {
            state_category=Integer.parseInt(callMethod.ReadString("Category"));
        }catch (Exception e){
            state_category=0;
        }


        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {

            OrderBy = "Order By GoodExplain1";
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")) {
            OrderBy = "Order By FormNo";
        }else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrCheshme")) {
            OrderBy = "Order By LocationTitle";
        } else {
            OrderBy = "Order By GoodExplain1";
        }

        Call<RetrofitResponse> call=apiInterface.OcrShortageList("OcrShortageList",OrderBy,"0");

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getOcr_Goods() != null) {

                    ocr_goods = response.body().getOcr_Goods();
                    row_counter = 0;

                    showGroupedGoods(ocr_goods);

                } else {

                    ocr_goods.clear();
                    showGroupedGoods(ocr_goods);

                    callMethod.showToast(
                            "اطلاعات کسری دریافت نشد"
                    );
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.Log(t.getMessage());
                callMethod.showToast("مشکلی در برقراری ارتباط");
            }
        });




    }
    private void showGroupedGoods(ArrayList<Ocr_Good> goods) {

        displayGoods.clear();
        groupCounts.clear();

        LinkedHashMap<String, ArrayList<Ocr_Good>> groupedGoods =
                new LinkedHashMap<>();


        if (goods != null) {

            for (Ocr_Good item : goods) {

                String groupKey = getGroupKey(item);

                if (!groupedGoods.containsKey(groupKey)) {
                    groupedGoods.put(
                            groupKey,
                            new ArrayList<>()
                    );
                }

                ArrayList<Ocr_Good> groupItems =
                        groupedGoods.get(groupKey);

                if (groupItems != null) {
                    groupItems.add(item);
                }
            }
        }


        for (Map.Entry<String, ArrayList<Ocr_Good>> entry
                : groupedGoods.entrySet()) {

            ArrayList<Ocr_Good> groupItems =
                    entry.getValue();

            if (groupItems == null || groupItems.isEmpty()) {
                continue;
            }

            groupCounts.put(
                    entry.getKey(),
                    groupItems.size()
            );

            displayGoods.addAll(groupItems);
        }


        currentPage = 0;
        renderCurrentPage();
    }


    private String getGroupKey(Ocr_Good item) {

        if (item == null) {
            return "|";
        }

        return safeValue(item.getPreFactorCode())
                + "|"
                + safeValue(item.getCustName());
    }


    private void renderCurrentPage() {

        ll_main.removeAllViews();

        if (displayGoods.isEmpty()) {
            addEmptyState();
            return;
        }


        int totalPages =
                Math.max(
                        1,
                        (int) Math.ceil(
                                displayGoods.size()
                                        / (double) PAGE_SIZE
                        )
                );


        if (currentPage < 0) {
            currentPage = 0;
        }

        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }


        int startIndex =
                currentPage * PAGE_SIZE;

        int endIndex =
                Math.min(
                        startIndex + PAGE_SIZE,
                        displayGoods.size()
                );


        row_counter = startIndex;

        String lastGroupKey = null;


        for (int index = startIndex;
             index < endIndex;
             index++) {

            Ocr_Good item =
                    displayGoods.get(index);

            String groupKey =
                    getGroupKey(item);


            if (!groupKey.equals(lastGroupKey)) {

                if (lastGroupKey != null) {
                    addGroupSpace();
                }

                addPreFactorHeader(
                        safeValue(item.getPreFactorCode()),
                        safeValue(item.getCustName()),
                        groupCounts.containsKey(groupKey)
                                ? groupCounts.get(groupKey)
                                : 0
                );

                lastGroupKey = groupKey;
            }


            goodshow(item);
        }


        if (totalPages > 1) {
            addPaginationControls(totalPages);
        }


        scrollListToTop();
    }


    private void addEmptyState() {

        TextView emptyView =
                new TextView(this);

        LinearLayoutCompat.LayoutParams params =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                dpToPx(16),
                dpToPx(30),
                dpToPx(16),
                dpToPx(16)
        );

        emptyView.setLayoutParams(params);

        emptyView.setText(
                "موردی برای نمایش وجود ندارد"
        );

        emptyView.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryDark
                )
        );

        emptyView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                15
        );

        emptyView.setGravity(Gravity.CENTER);

        emptyView.setPadding(
                dpToPx(12),
                dpToPx(18),
                dpToPx(12),
                dpToPx(18)
        );

        emptyView.setBackground(
                createRoundedBackground(
                        Color.parseColor("#F7F9FC"),
                        Color.parseColor("#DCE3EA"),
                        1,
                        10
                )
        );

        ll_main.addView(emptyView);
    }


    private void addPaginationControls(
            int totalPages
    ) {

        LinearLayoutCompat pager =
                new LinearLayoutCompat(this);

        TextView previousButton =
                createPageButton(
                        "صفحه قبل",
                        currentPage > 0
                );

        TextView pageInfo =
                new TextView(this);

        TextView nextButton =
                createPageButton(
                        "صفحه بعد",
                        currentPage < totalPages - 1
                );


        LinearLayoutCompat.LayoutParams pagerParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );

        pagerParams.setMargins(
                dpToPx(8),
                dpToPx(10),
                dpToPx(8),
                dpToPx(16)
        );

        pager.setLayoutParams(pagerParams);
        pager.setOrientation(LinearLayoutCompat.HORIZONTAL);
        pager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        pager.setGravity(Gravity.CENTER_VERTICAL);

        pager.setPadding(
                dpToPx(6),
                dpToPx(6),
                dpToPx(6),
                dpToPx(6)
        );


        LinearLayoutCompat.LayoutParams buttonParams =
                new LinearLayoutCompat.LayoutParams(
                        0,
                        dpToPx(44),
                        1
                );

        previousButton.setLayoutParams(buttonParams);


        LinearLayoutCompat.LayoutParams infoParams =
                new LinearLayoutCompat.LayoutParams(
                        0,
                        dpToPx(44),
                        1.25f
                );

        infoParams.setMargins(
                dpToPx(6),
                0,
                dpToPx(6),
                0
        );

        pageInfo.setLayoutParams(infoParams);

        nextButton.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        dpToPx(44),
                        1
                )
        );


        String pageText =
                "صفحه "
                        + (currentPage + 1)
                        + " از "
                        + totalPages
                        + ""
                + displayGoods.size()
                + " کالا";

        pageInfo.setText(
                NumberFunctions.PerisanNumber(
                        pageText
                )
        );

        pageInfo.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryDark
                )
        );

        pageInfo.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                12
        );

        pageInfo.setTypeface(
                null,
                Typeface.BOLD
        );

        pageInfo.setGravity(Gravity.CENTER);

        pageInfo.setBackground(
                createRoundedBackground(
                        Color.parseColor("#F2F5F8"),
                        Color.parseColor("#DCE3EA"),
                        1,
                        9
                )
        );


        previousButton.setOnClickListener(v -> {

            if (currentPage > 0) {
                currentPage--;
                renderCurrentPage();
            }
        });


        nextButton.setOnClickListener(v -> {

            if (currentPage < totalPages - 1) {
                currentPage++;
                renderCurrentPage();
            }
        });


        pager.addView(previousButton);
        pager.addView(pageInfo);
        pager.addView(nextButton);

        ll_main.addView(pager);
    }


    private TextView createPageButton(
            String text,
            boolean enabled
    ) {

        TextView button =
                new TextView(this);

        button.setText(
                NumberFunctions.PerisanNumber(text)
        );

        button.setTextColor(
                enabled
                        ? Color.WHITE
                        : Color.parseColor("#9AA6B2")
        );

        button.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                13
        );

        button.setTypeface(
                null,
                Typeface.BOLD
        );

        button.setGravity(Gravity.CENTER);
        button.setEnabled(enabled);
        button.setClickable(enabled);
        button.setFocusable(enabled);

        button.setBackground(
                createRoundedBackground(
                        enabled
                                ? ContextCompat.getColor(
                                this,
                                R.color.colorPrimaryDark
                        )
                                : Color.parseColor("#E8EDF2"),
                        enabled
                                ? ContextCompat.getColor(
                                this,
                                R.color.colorPrimaryDark
                        )
                                : Color.parseColor("#D6DDE4"),
                        1,
                        9
                )
        );

        return button;
    }


    private void scrollListToTop() {

        ll_main.post(() -> {

            ViewParent parent =
                    ll_main.getParent();

            while (parent != null) {

                if (parent instanceof ScrollView) {
                    ((ScrollView) parent).scrollTo(0, 0);
                    break;
                }

                parent = parent.getParent();
            }
        });
    }


    @SuppressLint("RtlHardcoded")
    private void addPreFactorHeader(
            String preFactorCode,
            String custName,
            int goodsCount
    ) {

        LinearLayoutCompat headerCard =
                new LinearLayoutCompat(this);

        LinearLayoutCompat titleRow =
                new LinearLayoutCompat(this);

        TextView tvCustomer =
                new TextView(this);

        TextView tvCountBadge =
                new TextView(this);

        TextView tvFactorInfo =
                new TextView(this);


        LinearLayoutCompat.LayoutParams cardParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                dpToPx(8),
                dpToPx(8),
                dpToPx(8),
                dpToPx(4)
        );


        headerCard.setLayoutParams(cardParams);
        headerCard.setOrientation(LinearLayoutCompat.VERTICAL);
        headerCard.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        headerCard.setPadding(
                dpToPx(10),
                dpToPx(8),
                dpToPx(10),
                dpToPx(8)
        );

        headerCard.setElevation(dpToPx(3));

        headerCard.setBackground(
                createRoundedBackground(
                        ContextCompat.getColor(
                                this,
                                R.color.colorPrimaryDark
                        ),
                        ContextCompat.getColor(
                                this,
                                R.color.colorPrimaryDark
                        ),
                        0,
                        12
                )
        );


        titleRow.setOrientation(LinearLayoutCompat.HORIZONTAL);
        titleRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);


        LinearLayoutCompat.LayoutParams customerParams =
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        1
                );

        tvCustomer.setLayoutParams(customerParams);

        tvCustomer.setText(
                NumberFunctions.PerisanNumber(
                        custName.isEmpty()
                                ? "بدون نام مشتری"
                                : custName
                )
        );

        tvCustomer.setTextColor(Color.WHITE);
        tvCustomer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvCustomer.setTypeface(null, Typeface.BOLD);
        tvCustomer.setGravity(Gravity.RIGHT);


        tvCountBadge.setText(
                NumberFunctions.PerisanNumber(
                        goodsCount + " کالا"
                )
        );

        tvCountBadge.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryDark
                )
        );

        tvCountBadge.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                12
        );

        tvCountBadge.setTypeface(null, Typeface.BOLD);
        tvCountBadge.setGravity(Gravity.CENTER);

        tvCountBadge.setPadding(
                dpToPx(10),
                dpToPx(5),
                dpToPx(10),
                dpToPx(5)
        );

        tvCountBadge.setBackground(
                createRoundedBackground(
                        Color.WHITE,
                        Color.WHITE,
                        0,
                        20
                )
        );


        String factorText =
                "پیش‌فاکتور "
                        + (
                        preFactorCode.isEmpty()
                                ? "-"
                                : preFactorCode
                );

        tvFactorInfo.setText(
                NumberFunctions.PerisanNumber(factorText)
        );

        tvFactorInfo.setTextColor(
                Color.parseColor("#DDE7F3")
        );

        tvFactorInfo.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                13
        );

        tvFactorInfo.setGravity(Gravity.RIGHT);

        tvFactorInfo.setPadding(
                0,
                dpToPx(7),
                0,
                0
        );


        titleRow.addView(tvCustomer);
        titleRow.addView(tvCountBadge);

        headerCard.addView(titleRow);
        headerCard.addView(tvFactorInfo);

        ll_main.addView(headerCard);
    }


    private void addGroupSpace() {

        View space = new View(this);

        space.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dpToPx(12)
                )
        );

        ll_main.addView(space);
    }

    private int dpToPx(int dp) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(dp * density);
    }

    private String safeValue(Object value) {

        if (value == null) {
            return "";
        }

        return String.valueOf(value).trim();
    }
    @SuppressLint("RtlHardcoded")
    public void goodshow(Ocr_Good good_detial) {

        row_counter++;


        LinearLayoutCompat card =
                new LinearLayoutCompat(this);

        LinearLayoutCompat topRow =
                new LinearLayoutCompat(this);

        LinearLayoutCompat infoRow =
                new LinearLayoutCompat(this);

        TextView tvRowNumber =
                new TextView(this);

        TextView tv_good_part1 =
                new TextView(this);

        TextView tv_good_part2 =
                new TextView(this);

        TextView tvFacAmount =
                new TextView(this);

        TextView tvThirdValue =
                new TextView(this);


        String goodName =
                safeValue(good_detial.getGoodName());

        String facAmount =
                normalizeDecimal(
                        good_detial.getFacAmount()
                );

        String shortageAmount =
                normalizeDecimal(
                        good_detial.getShortageAmount()
                );

        String thirdTitle;
        String thirdValue;


        String companyName =
                safeValue(
                        callMethod.ReadString(
                                "EnglishCompanyNameUse"
                        )
                );


        if (companyName.equals("OcrCheshme")) {

            thirdTitle = "موقعیت";
            thirdValue = safeValue(
                    good_detial.getLocationTitle()
            );

        } else if (companyName.equals("OcrGostaresh")) {

            thirdTitle = "فرم";
            thirdValue = safeValue(
                    good_detial.getFormNo()
            );

        } else {

            thirdTitle = "قیمت";
            thirdValue = normalizeDecimal(
                    good_detial.getGoodMaxSellPrice()
            );
        }


        boolean hasShortage =
                isPositiveNumber(
                        good_detial.getShortageAmount()
                );


        LinearLayoutCompat.LayoutParams cardParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                dpToPx(10),
                dpToPx(4),
                dpToPx(10),
                dpToPx(6)
        );


        card.setLayoutParams(cardParams);
        card.setOrientation(LinearLayoutCompat.VERTICAL);
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        card.setPadding(
                dpToPx(10),
                dpToPx(8),
                dpToPx(10),
                dpToPx(8)
        );

        card.setElevation(dpToPx(2));

        int cardColor =
                row_counter % 2 == 0
                        ? Color.parseColor("#F7F9FC")
                        : Color.WHITE;

        int borderColor =
                hasShortage
                        ? ContextCompat.getColor(
                        this,
                        R.color.red_800
                )
                        : Color.parseColor("#DCE3EA");

        card.setBackground(
                createRoundedBackground(
                        cardColor,
                        borderColor,
                        hasShortage ? 2 : 1,
                        12
                )
        );


        topRow.setOrientation(
                LinearLayoutCompat.HORIZONTAL
        );

        topRow.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        topRow.setGravity(
                Gravity.CENTER_VERTICAL
        );


        tvRowNumber.setText(
                NumberFunctions.PerisanNumber(
                        String.valueOf(row_counter)
                )
        );

        tvRowNumber.setTextColor(Color.WHITE);
        tvRowNumber.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                12
        );

        tvRowNumber.setTypeface(
                null,
                Typeface.BOLD
        );

        tvRowNumber.setGravity(Gravity.CENTER);

        tvRowNumber.setMinWidth(dpToPx(34));
        tvRowNumber.setMinHeight(dpToPx(34));

        tvRowNumber.setPadding(
                dpToPx(6),
                dpToPx(6),
                dpToPx(6),
                dpToPx(6)
        );

        tvRowNumber.setBackground(
                createRoundedBackground(
                        ContextCompat.getColor(
                                this,
                                R.color.colorPrimaryDark
                        ),
                        ContextCompat.getColor(
                                this,
                                R.color.colorPrimaryDark
                        ),
                        0,
                        20
                )
        );


        LinearLayoutCompat.LayoutParams nameParams =
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        1
                );

        nameParams.setMargins(
                0,
                0,
                dpToPx(10),
                0
        );

        tv_good_part1.setLayoutParams(nameParams);

        tv_good_part1.setText(
                NumberFunctions.PerisanNumber(
                        goodName
                )
        );

        tv_good_part1.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryDark
                )
        );

        tv_good_part1.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                getTitleSize()
        );

        tv_good_part1.setTypeface(
                null,
                Typeface.BOLD
        );

        tv_good_part1.setGravity(Gravity.RIGHT);
        tv_good_part1.setMaxLines(2);
        tv_good_part1.setEllipsize(TextUtils.TruncateAt.END);

        tv_good_part1.setPadding(
                dpToPx(6),
                dpToPx(4),
                dpToPx(6),
                dpToPx(4)
        );

        tv_good_part1.setClickable(true);
        tv_good_part1.setFocusable(true);


        topRow.addView(tvRowNumber);
        topRow.addView(tv_good_part1);


        infoRow.setOrientation(
                LinearLayoutCompat.HORIZONTAL
        );

        infoRow.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        infoRow.setWeightSum(3);

        infoRow.setPadding(
                0,
                dpToPx(7),
                0,
                0
        );


        setupInfoText(
                tv_good_part2,
                "کسری",
                shortageAmount,
                hasShortage
                        ? ContextCompat.getColor(
                        this,
                        R.color.red_800
                )
                        : ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryDark
                ),
                hasShortage
                        ? Color.parseColor("#FFF1F1")
                        : Color.parseColor("#F2F5F8")
        );


        setupInfoText(
                tvFacAmount,
                "تعداد فاکتور",
                facAmount,
                ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryDark
                ),
                Color.parseColor("#F2F5F8")
        );


        setupInfoText(
                tvThirdValue,
                thirdTitle,
                thirdValue.isEmpty()
                        ? "-"
                        : thirdValue,
                ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryDark
                ),
                Color.parseColor("#F2F5F8")
        );

        tvFacAmount.setClickable(false);
        tvFacAmount.setFocusable(false);

        tvThirdValue.setClickable(false);
        tvThirdValue.setFocusable(false);


        LinearLayoutCompat.LayoutParams infoParams1 =
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        1
                );

        infoParams1.setMargins(
                0,
                0,
                0,
                0
        );

        tv_good_part2.setLayoutParams(infoParams1);


        LinearLayoutCompat.LayoutParams infoParams2 =
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        1
                );

        infoParams2.setMargins(
                dpToPx(5),
                0,
                dpToPx(5),
                0
        );

        tvFacAmount.setLayoutParams(infoParams2);


        LinearLayoutCompat.LayoutParams infoParams3 =
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        1
                );

        tvThirdValue.setLayoutParams(infoParams3);


        infoRow.addView(tv_good_part2);
        infoRow.addView(tvFacAmount);
        infoRow.addView(tvThirdValue);


        card.addView(topRow);
        card.addView(infoRow);

        ll_main.addView(card);


        tv_good_part1.setOnClickListener(v -> {


                good_detail_view(good_detial);


        });


        tv_good_part2.setOnClickListener(v -> {


                good_amount_view(
                        safeValue(
                                good_detial.getFacAmount()
                        ),
                        safeValue(
                                good_detial.getShortageAmount()
                        )
                );

        });


//        tvThirdValue.setOnClickListener(v -> {
//
//            if (canUseWarehouseActions()) {
//
//                good_detail_view(good_detial);
//
//            } else {
//
//                callMethod.showToast(
//                        "لطفا ابتدا آغاز فرایند انبار را شروع کنید"
//                );
//            }
//        });
    }


    private void setupInfoText(
            TextView textView,
            String title,
            String value,
            int textColor,
            int backgroundColor
    ) {

        String text =
                title
                        + "\n"
                        + NumberFunctions.PerisanNumber(
                        value
                );

        textView.setText(text);
        textView.setTextColor(textColor);

        textView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                12
        );

        textView.setTypeface(
                null,
                Typeface.BOLD
        );

        textView.setGravity(Gravity.CENTER);

        textView.setMinHeight(
                dpToPx(50)
        );

        textView.setPadding(
                dpToPx(4),
                dpToPx(6),
                dpToPx(4),
                dpToPx(6)
        );

        textView.setClickable(true);
        textView.setFocusable(true);

        textView.setBackground(
                createRoundedBackground(
                        backgroundColor,
                        Color.parseColor("#DCE3EA"),
                        1,
                        9
                )
        );
    }


    private GradientDrawable createRoundedBackground(
            int fillColor,
            int strokeColor,
            int strokeWidthDp,
            int radiusDp
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(fillColor);

        drawable.setCornerRadius(
                dpToPx(radiusDp)
        );

        if (strokeWidthDp > 0) {
            drawable.setStroke(
                    dpToPx(strokeWidthDp),
                    strokeColor
            );
        }

        return drawable;
    }


    private int getTitleSize() {

        try {

            return Integer.parseInt(
                    callMethod.ReadString(
                            "TitleSize"
                    )
            );

        } catch (Exception e) {

            return 15;
        }
    }


    private String normalizeDecimal(Object value) {

        String rawValue =
                safeValue(value);

        if (rawValue.isEmpty()) {
            return "0";
        }

        try {

            return new BigDecimal(rawValue)
                    .stripTrailingZeros()
                    .toPlainString();

        } catch (NumberFormatException e) {

            return rawValue;
        }
    }


    private boolean isPositiveNumber(Object value) {

        String rawValue =
                safeValue(value);

        if (rawValue.isEmpty()) {
            return false;
        }

        try {

            return new BigDecimal(rawValue)
                    .compareTo(BigDecimal.ZERO) > 0;

        } catch (NumberFormatException e) {

            callMethod.Log(
                    "Invalid numeric value: "
                            + rawValue
            );

            return false;
        }
    }


    private boolean canUseWarehouseActions() {

        String stackCategory =
                safeValue(
                        callMethod.ReadString(
                                "StackCategory"
                        )
                );

        if (stackCategory.isEmpty()) {
            return false;
        }

        String explain =
                safeValue(
                        appOCRFactorExplain
                );

        if (explain.isEmpty()) {

            explain =
                    safeValue(
                            callMethod.ReadString(
                                    "AppOCRFactorExplain"
                            )
                    );
        }

        return explain.contains(
                stackCategory
        );
    }


    public void good_detail_view(
            Ocr_Good singleGood
    ) {

        ocr_action.good_detail(
                singleGood,
                BarcodeScan,
                this
        );
    }


    public void good_amount_view(
            String facAmount,
            String shortage
    ) {

        ocr_action.goodamount_detail(
                facAmount,
                shortage
        );
    }


    @Override
    public void onGoodConfirmed(
            Ocr_Good good
    ) {

        callMethod.Log(
                "Good confirmed: "
                        + safeValue(
                        good != null
                                ? good.getGoodCode()
                                : ""
                )
        );
    }


    @Override
    public void onGoodCanceled(
            Ocr_Good good
    ) {

        callMethod.Log(
                "Good canceled: "
                        + safeValue(
                        good != null
                                ? good.getGoodCode()
                                : ""
                )
        );
    }


    public void image_zome_view(Ocr_Good singleGood) {
//        ocr_action.good_detail(singleGood,"");
    }




    ///////////////////////////////////
































































}