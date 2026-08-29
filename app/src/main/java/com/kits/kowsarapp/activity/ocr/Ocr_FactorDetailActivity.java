package com.kits.kowsarapp.activity.ocr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.google.android.material.color.MaterialColors;

import java.io.ByteArrayOutputStream;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.NetworkUtils;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_FactorDetailActivity extends AppCompatActivity {

    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;
    Ocr_DBH ocr_dbh ;
    LinearLayoutCompat main_layout;
    LinearLayoutCompat title_layout;
    LinearLayoutCompat boby_good_layout;
    LinearLayoutCompat good_layout;
    LinearLayoutCompat total_layout;
    androidx.viewpager.widget.ViewPager ViewPager, ViewPager_chap, ViewPager_rast;
    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    ArrayList<Ocr_Good> ocr_goods;
    Factor factor;
    String BarcodeScan;
    String bitmap_factor_base64="";
    Intent intent;
    Bitmap bitmap_factor;
    int width=1;
    CallMethod callMethod;
    Ocr_Action ocr_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        setContentView(R.layout.ocr_activity_factordetail);
        Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.ocr_spinner_box);
        TextView repw = dialog1.findViewById(R.id.ocr_spinner_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        }catch (Exception e){
            callMethod.Log(e.getMessage());
        }


    }
    ///**********************************************************

    public  void intent(){
        Bundle bundle =getIntent().getExtras();
        assert bundle != null;
        BarcodeScan=bundle.getString("ScanResponse");
        bitmap_factor_base64=bundle.getString("FactorImage");
    }

    public void Config() {

        callMethod = new CallMethod(this);
        ocr_dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));
        ocr_action= new Ocr_Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
        main_layout = findViewById(R.id.ocr_factordetail_a_layout);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;

    }


    public void init(){



        if(bitmap_factor_base64.equals("")){

//
//
//            Call<RetrofitResponse> call;
//
//
//            String Body_str  = "";
//
//            Body_str =callMethod.CreateJson("barcode", BarcodeScan, Body_str);
//            Body_str =callMethod.CreateJson("Step", "0", Body_str);
//            Body_str =callMethod.CreateJson("orderby", "GoodName", Body_str);
//
//
//
//            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//                call = apiInterface.GetOcrFactor(callMethod.RetrofitBody(Body_str));
//            }else{
//                call = secendApiInterface.GetOcrFactor(callMethod.RetrofitBody(Body_str));
//            }

            Call<RetrofitResponse> call;
            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call =apiInterface.GetFactor("GetOcrFactor",BarcodeScan,"GoodName");
            }else {
                call =secendApiInterface.GetFactor("GetOcrFactor",BarcodeScan,"GoodName");
            }


            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if(response.isSuccessful()){

                        assert response.body() !=null;
//                        factor=response.body().getFactors().get(0);
                        factor=response.body().getFactor();

                        if(factor.getFactorCode().equals("0"))
                        {

                            callMethod.showToast("لطفا مجددا اسکن کنید");
                            finish();
                        }else {

                            ocr_goods=response.body().getOcr_Goods();
                            ocr_dbh.InsertScan(factor.getAppOCRFactorCode(),BarcodeScan,factor.getFactorPrivateCode(),factor.getFactorDate(),factor.getCustName(),factor.getCustomerRef());
                            CreateView();
                        }


                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    try {
                        // 🟢 بررسی وضعیت اتصال
                        if (!NetworkUtils.isNetworkAvailable(Ocr_FactorDetailActivity.this)) {
                            callMethod.showToast("اتصال اینترنت قطع است!");
                        } else if (NetworkUtils.isVPNActive()) {
                            callMethod.showToast("VPN فعال است، ممکن است ارتباط با سرور مختل شود!");
                        } else {
                            String serverUrl = callMethod.ReadString("ServerURLUse");
                            if (serverUrl != null && !serverUrl.isEmpty() && !NetworkUtils.canReachServer(serverUrl)) {
                                callMethod.showToast("سرور در دسترس نیست یا فیلتر شده است!");
                            } else {
                                callMethod.showToast("مشکل در برقراری ارتباط با سرور برای بارگیری عکس");
                            }
                        }
                    } catch (Exception e) {
                        callMethod.Log("Network check error: " + e.getMessage());
                        callMethod.showToast("خطا در بررسی وضعیت شبکه");
                    }
                }
            });

        }else {
            bitmap_factor_base64=ocr_dbh.getimagefromfactor(BarcodeScan,"FactorImage");

            ImageView imageView=new ImageView(getApplicationContext());
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

            byte[] imageByteArray1;
            imageByteArray1 = Base64.decode(bitmap_factor_base64, Base64.DEFAULT);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth(), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight(), false));
            main_layout.addView(imageView);

            Button button=  new Button(getApplicationContext());
            button.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            button.setBackgroundResource(R.color.green_700);
            button.setText("تایید و امضای رسید");
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            button.setTextColor(getColor(R.color.white));
            button.setOnClickListener(v -> {

                intent = new Intent(Ocr_FactorDetailActivity.this, Ocr_PaintActivity.class);
                intent.putExtra("ScanResponse", BarcodeScan);
                intent.putExtra("FactorImage", "hasimage");
                intent.putExtra("Width", String.valueOf(width));
                startActivity(intent);
                finish();
            });
            main_layout.addView(button);
        }

    }

    private LinearLayoutCompat createInfoRow(
            String label,
            String value,
            int bodySize,
            int textColor,
            int mutedTextColor,
            int maxLines
    ) {

        LinearLayoutCompat row = new LinearLayoutCompat(this);

        row.setOrientation(LinearLayoutCompat.HORIZONTAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(2), 0, dp(2));
        row.setMinimumHeight(dp(36));

        TextView labelView = createReceiptText(
                label,
                9,
                mutedTextColor,
                Gravity.RIGHT | Gravity.CENTER_VERTICAL,
                false
        );

        labelView.setPadding(dp(4), 0, dp(4), 0);

        labelView.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        dp(88),
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
        );

        TextView valueView = createReceiptText(
                NumberFunctions.PerisanNumber(value),
                bodySize,
                textColor,
                Gravity.RIGHT | Gravity.CENTER_VERTICAL,
                true
        );

        valueView.setMaxLines(maxLines);
        valueView.setPadding(dp(5), dp(3), dp(5), dp(3));

        valueView.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        row.addView(labelView);
        row.addView(valueView);

        return row;
    }

    private LinearLayoutCompat createMetricCell(
            String label,
            String value,
            int bodySize,
            int valueColor,
            int labelColor
    ) {

        LinearLayoutCompat cell = new LinearLayoutCompat(this);

        cell.setOrientation(LinearLayoutCompat.VERTICAL);
        cell.setGravity(Gravity.CENTER);
        cell.setPadding(dp(2), dp(2), dp(2), dp(2));

        cell.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        dp(48),
                        1f
                )
        );

        TextView labelView = createReceiptText(
                label,
                8,
                labelColor,
                Gravity.CENTER,
                false
        );

        labelView.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(18)
                )
        );

        TextView valueView = createReceiptText(
                NumberFunctions.PerisanNumber(value),
                bodySize,
                valueColor,
                Gravity.CENTER,
                true
        );

        valueView.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(26)
                )
        );

        cell.addView(labelView);
        cell.addView(valueView);

        return cell;
    }

    private LinearLayoutCompat createTotalRow(
            String label,
            String value,
            int textSize,
            int valueColor,
            boolean bold
    ) {

        LinearLayoutCompat row = new LinearLayoutCompat(this);

        row.setOrientation(LinearLayoutCompat.HORIZONTAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(dp(34));

        TextView labelView = createReceiptText(
                label,
                Math.max(textSize - 1, 9),
                Color.rgb(70, 70, 70),
                Gravity.RIGHT | Gravity.CENTER_VERTICAL,
                bold
        );

        labelView.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        1f
                )
        );

        TextView valueView = createReceiptText(
                NumberFunctions.PerisanNumber(value),
                textSize,
                valueColor,
                Gravity.LEFT | Gravity.CENTER_VERTICAL,
                true
        );

        valueView.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        1.4f
                )
        );

        row.addView(labelView);
        row.addView(valueView);

        return row;
    }
    private View createReceiptDivider(int color) {

        View divider = new View(this);

        LinearLayoutCompat.LayoutParams params =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        params.setMargins(0, dp(2), 0, dp(2));

        divider.setLayoutParams(params);
        divider.setBackgroundColor(color);

        return divider;
    }

    private View createVerticalReceiptDivider(int color) {

        View divider = new View(this);

        LinearLayoutCompat.LayoutParams params =
                new LinearLayoutCompat.LayoutParams(
                        dp(1),
                        dp(34)
                );

        params.gravity = Gravity.CENTER_VERTICAL;

        divider.setLayoutParams(params);
        divider.setBackgroundColor(color);

        return divider;
    }

    private GradientDrawable createReceiptBackground(
            int backgroundColor,
            int strokeColor,
            int strokeWidth,
            int radius
    ) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(backgroundColor);
        drawable.setCornerRadius(dp(radius));

        if (strokeWidth > 0) {
            drawable.setStroke(dp(strokeWidth), strokeColor);
        }

        return drawable;
    }

    private String formatReceiptNumber(double value) {

        if (value == Math.rint(value)) {
            return decimalFormat.format((long) value);
        }

        return decimalFormat.format(value);
    }
    private String safeReceiptText(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        return value.trim();
    }
    private int dp(int value) {

        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }

    private double safeReceiptNumber(String value) {

        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        try {

            String normalized = value
                    .trim()
                    .replace("٬", "")
                    .replace(" ", "");

            if (normalized.contains(",") && !normalized.contains(".")) {
                normalized = normalized.replace(",", ".");
            } else {
                normalized = normalized.replace(",", "");
            }

            return Double.parseDouble(normalized);

        } catch (Exception ignored) {
            return 0;
        }
    }

    private int readReceiptTextSize(String key, int fallback) {

        try {

            String value = callMethod.ReadString(key);

            if (value == null || value.trim().isEmpty()) {
                return fallback;
            }

            return Integer.parseInt(value.trim());

        } catch (Exception ignored) {
            return fallback;
        }
    }

    private TextView createReceiptText(
            String text,
            int textSize,
            int textColor,
            int gravity,
            boolean bold
    ) {

        TextView textView = new TextView(this);

        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(textColor);
        textView.setGravity(gravity);
        textView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        if (bold) {
            textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return textView;
    }
    @SuppressLint("RtlHardcoded")
    public void CreateView() {

        final int titleSize = readReceiptTextSize("TitleSize", 14);
        final int bodySize = readReceiptTextSize("BodySize", 12);

        final int primaryColor = MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorPrimary,
                ContextCompat.getColor(this, R.color.colorPrimaryDark)
        );

        final int textColor = Color.rgb(35, 35, 35);
        final int mutedTextColor = Color.rgb(90, 90, 90);
        final int dividerColor = ColorUtils.setAlphaComponent(primaryColor, 65);
        final int paperColor = Color.WHITE;
        final int softBackground = Color.rgb(248, 248, 248);

        main_layout.removeAllViews();
        main_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        main_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        main_layout.setPadding(dp(6), dp(6), dp(6), dp(6));
        main_layout.setBackgroundColor(paperColor);

        /*
         * Header
         */
        title_layout = new LinearLayoutCompat(this);
        title_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        title_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        title_layout.setPadding(dp(8), dp(7), dp(8), dp(7));
        title_layout.setBackground(
                createReceiptBackground(paperColor, primaryColor, 1, 8)
        );

        LinearLayoutCompat.LayoutParams sectionParams =
                new LinearLayoutCompat.LayoutParams(
                        width,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );

        sectionParams.setMargins(0, 0, 0, dp(5));
        title_layout.setLayoutParams(sectionParams);

        TextView companyText = createReceiptText(
                NumberFunctions.PerisanNumber("فاکتور فروش"),
                titleSize + 2,
                primaryColor,
                Gravity.CENTER,
                true
        );

        LinearLayoutCompat.LayoutParams companyParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(38)
                );

        companyText.setLayoutParams(companyParams);
        title_layout.addView(companyText);
        title_layout.addView(createReceiptDivider(dividerColor));

        title_layout.addView(
                createInfoRow(
                        "نام مشتری",
                        safeReceiptText(factor.getCustName()),
                        bodySize,
                        textColor,
                        mutedTextColor,
                        2
                )
        );

        title_layout.addView(
                createInfoRow(
                        "کد فاکتور",
                        safeReceiptText(factor.getFactorPrivateCode()),
                        bodySize,
                        textColor,
                        mutedTextColor,
                        1
                )
        );

        title_layout.addView(
                createInfoRow(
                        "تاریخ فاکتور",
                        safeReceiptText(factor.getFactorDate()),
                        bodySize,
                        textColor,
                        mutedTextColor,
                        1
                )
        );

        title_layout.addView(
                createInfoRow(
                        "آدرس",
                        safeReceiptText(factor.getAddress()),
                        bodySize,
                        textColor,
                        mutedTextColor,
                        3
                )
        );

        title_layout.addView(
                createInfoRow(
                        "تلفن تماس",
                        safeReceiptText(factor.getPhone()),
                        bodySize,
                        textColor,
                        mutedTextColor,
                        1
                )
        );

        /*
         * Goods
         */
        good_layout = new LinearLayoutCompat(this);
        good_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        good_layout.setPadding(dp(5), dp(5), dp(5), dp(5));
        good_layout.setBackground(
                createReceiptBackground(paperColor, dividerColor, 1, 8)
        );

        LinearLayoutCompat.LayoutParams goodsParams =
                new LinearLayoutCompat.LayoutParams(
                        width,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );

        goodsParams.setMargins(0, 0, 0, dp(5));
        good_layout.setLayoutParams(goodsParams);

        int goodsCount = ocr_goods == null ? 0 : ocr_goods.size();

        TextView goodsTitle = createReceiptText(
                NumberFunctions.PerisanNumber(
                        "اقلام فاکتور  (" + goodsCount + ")"
                ),
                titleSize,
                primaryColor,
                Gravity.RIGHT | Gravity.CENTER_VERTICAL,
                true
        );

        goodsTitle.setPadding(dp(5), 0, dp(5), 0);
        goodsTitle.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(34)
                )
        );

        good_layout.addView(goodsTitle);
        good_layout.addView(createReceiptDivider(dividerColor));

        boby_good_layout = new LinearLayoutCompat(this);
        boby_good_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        boby_good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        boby_good_layout.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        width,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
        );

        int counterGood = 0;

        if (ocr_goods != null) {

            for (Ocr_Good good : ocr_goods) {

                counterGood++;

                LinearLayoutCompat itemLayout = new LinearLayoutCompat(this);
                itemLayout.setOrientation(LinearLayoutCompat.VERTICAL);
                itemLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                itemLayout.setPadding(dp(5), dp(4), dp(5), dp(4));
                itemLayout.setBackground(
                        createReceiptBackground(
                                softBackground,
                                ColorUtils.setAlphaComponent(primaryColor, 45),
                                1,
                                6
                        )
                );

                LinearLayoutCompat.LayoutParams itemParams =
                        new LinearLayoutCompat.LayoutParams(
                                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                        );

                itemParams.setMargins(0, dp(3), 0, dp(3));
                itemLayout.setLayoutParams(itemParams);

                /*
                 * Product name row
                 */
                LinearLayoutCompat nameRow = new LinearLayoutCompat(this);
                nameRow.setOrientation(LinearLayoutCompat.HORIZONTAL);
                nameRow.setGravity(Gravity.CENTER_VERTICAL);
                nameRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                nameRow.setMinimumHeight(dp(38));

                TextView rowNumber = createReceiptText(
                        NumberFunctions.PerisanNumber(String.valueOf(counterGood)),
                        bodySize,
                        Color.WHITE,
                        Gravity.CENTER,
                        true
                );

                rowNumber.setBackground(
                        createReceiptBackground(primaryColor, primaryColor, 0, 6)
                );

                rowNumber.setLayoutParams(
                        new LinearLayoutCompat.LayoutParams(
                                dp(32),
                                dp(32)
                        )
                );

                TextView goodName = createReceiptText(
                        NumberFunctions.PerisanNumber(
                                safeReceiptText(good.getGoodName())
                        ),
                        titleSize,
                        textColor,
                        Gravity.RIGHT | Gravity.CENTER_VERTICAL,
                        true
                );

                goodName.setMaxLines(3);
                goodName.setPadding(dp(7), dp(2), dp(7), dp(2));

                goodName.setLayoutParams(
                        new LinearLayoutCompat.LayoutParams(
                                0,
                                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                                1f
                        )
                );

                nameRow.addView(rowNumber);
                nameRow.addView(goodName);

                itemLayout.addView(nameRow);
                itemLayout.addView(createReceiptDivider(dividerColor));

                /*
                 * Amount and prices
                 */
                double price = safeReceiptNumber(good.getPrice());
                double amount = safeReceiptNumber(good.getFacAmount());
                double totalPrice = price * amount;

                LinearLayoutCompat detailRow = new LinearLayoutCompat(this);
                detailRow.setOrientation(LinearLayoutCompat.HORIZONTAL);
                detailRow.setGravity(Gravity.CENTER);
                detailRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                detailRow.setPadding(0, dp(3), 0, 0);

                detailRow.addView(
                        createMetricCell(
                                "تعداد",
                                formatReceiptNumber(amount),
                                bodySize,
                                textColor,
                                mutedTextColor
                        )
                );

                detailRow.addView(
                        createVerticalReceiptDivider(dividerColor)
                );

                detailRow.addView(
                        createMetricCell(
                                "فی",
                                formatReceiptNumber(price),
                                bodySize,
                                textColor,
                                mutedTextColor
                        )
                );

                detailRow.addView(
                        createVerticalReceiptDivider(dividerColor)
                );

                detailRow.addView(
                        createMetricCell(
                                "مبلغ",
                                formatReceiptNumber(totalPrice),
                                bodySize,
                                primaryColor,
                                mutedTextColor
                        )
                );

                itemLayout.addView(detailRow);
                boby_good_layout.addView(itemLayout);
            }
        }

        good_layout.addView(boby_good_layout);

        /*
         * Totals
         */
        total_layout = new LinearLayoutCompat(this);
        total_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        total_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        total_layout.setPadding(dp(7), dp(6), dp(7), dp(6));
        total_layout.setBackground(
                createReceiptBackground(
                        softBackground,
                        primaryColor,
                        1,
                        8
                )
        );

        total_layout.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        width,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
        );

        double sumAmount = safeReceiptNumber(factor.getSumAmount());
        double sumPrice = safeReceiptNumber(factor.getSumPrice());
        double sumTax = safeReceiptNumber(factor.getSumTax());
        double finalPrice = sumPrice + sumTax;

        total_layout.addView(
                createTotalRow(
                        "تعداد کل",
                        formatReceiptNumber(sumAmount),
                        bodySize,
                        textColor,
                        false
                )
        );

        total_layout.addView(
                createTotalRow(
                        "جمع مبلغ",
                        formatReceiptNumber(sumPrice) + " ریال",
                        bodySize,
                        textColor,
                        false
                )
        );

        if (sumTax > 0) {

            total_layout.addView(
                    createTotalRow(
                            "مالیات",
                            formatReceiptNumber(sumTax) + " ریال",
                            bodySize,
                            textColor,
                            false
                    )
            );

            total_layout.addView(createReceiptDivider(dividerColor));

            total_layout.addView(
                    createTotalRow(
                            "قیمت نهایی",
                            formatReceiptNumber(finalPrice) + " ریال",
                            titleSize,
                            primaryColor,
                            true
                    )
            );

        } else {

            total_layout.addView(createReceiptDivider(dividerColor));

            total_layout.addView(
                    createTotalRow(
                            "قیمت نهایی",
                            formatReceiptNumber(sumPrice) + " ریال",
                            titleSize,
                            primaryColor,
                            true
                    )
            );
        }

        /*
         * Add printable content
         */
        main_layout.addView(title_layout);
        main_layout.addView(good_layout);
        main_layout.addView(total_layout);

        /*
         * Create image before adding the action button
         */
        bitmap_factor = loadBitmapFromView(main_layout);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        bitmap_factor.compress(
                Bitmap.CompressFormat.JPEG,
                80,
                outputStream
        );

        byte[] imageBytes = outputStream.toByteArray();

        bitmap_factor_base64 = Base64.encodeToString(
                imageBytes,
                Base64.NO_WRAP
        );

        ocr_dbh.Insert_factorImage(
                BarcodeScan,
                bitmap_factor_base64
        );

        /*
         * Action button
         */
        Button confirmButton = new Button(this);

        LinearLayoutCompat.LayoutParams buttonParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(46)
                );

        buttonParams.setMargins(0, dp(7), 0, 0);
        confirmButton.setLayoutParams(buttonParams);

        confirmButton.setText("تأیید و امضای رسید");
        confirmButton.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                Math.max(bodySize, 12)
        );

        confirmButton.setTextColor(Color.WHITE);
        confirmButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        confirmButton.setGravity(Gravity.CENTER);
        confirmButton.setAllCaps(false);
        confirmButton.setMinWidth(0);
        confirmButton.setMinHeight(0);

        confirmButton.setBackgroundTintList(
                ColorStateList.valueOf(
                        ContextCompat.getColor(
                                this,
                                R.color.green_700
                        )
                )
        );

        confirmButton.setOnClickListener(view -> {

            intent = new Intent(
                    Ocr_FactorDetailActivity.this,
                    Ocr_PaintActivity.class
            );

            intent.putExtra("ScanResponse", BarcodeScan);
            intent.putExtra("FactorImage", "hasimage");
            intent.putExtra("Width", String.valueOf(width));

            startActivity(intent);
            finish();
        });

        main_layout.addView(confirmButton);
    }

    public Bitmap loadBitmapFromView(View v) {
        v.measure(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        Bitmap b = Bitmap.createBitmap(width, v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.layout(0, 0, width, v.getMeasuredHeight());
        v.draw(c);
        return b;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        intent = new Intent(Ocr_FactorDetailActivity.this, Ocr_FactorDetailActivity.class);
        intent.putExtra("ScanResponse", BarcodeScan);
        intent.putExtra("FactorImage", bitmap_factor_base64);
        startActivity(intent);
        finish();
    }


}