package com.kits.kowsarapp.viewholder.broker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_PFOpenActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.application.base.NetworkUtils;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Broker_GoodItemViewHolder extends RecyclerView.ViewHolder {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");

    private final LinearLayoutCompat mainline;
//    private final ImageView img;
    public final ShapeableImageView img;
    public MaterialCardView rltv;
    public final Button btnadd;

    boolean multi_select1;

    public TextView tv_line_name;
    public TextView tv_line_maxsellprice;
    public TextView tv_line_amount;
    public ProgressBar progressBar;

    private final Context mContext;
    CallMethod callMethod;

    Broker_DBH broker_dbh;

    Broker_APIInterface broker_apiInterface;
    private final ImageInfo image_info;
    public Call<RetrofitResponse> call;
    Broker_Action broker_action;
    ArrayList<Column> Columns;


    public Broker_GoodItemViewHolder(View itemView, Context context) {
        super(itemView);

        this.mContext = context;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.broker_dbh = new Broker_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.broker_action = new Broker_Action(mContext);
        this.Columns = broker_dbh.GetColumns("id", "", "1");
        this.broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);

        mainline = itemView.findViewById(R.id.b_good_c_mainline);
        img = itemView.findViewById(R.id.b_good_c_img);
        progressBar = itemView.findViewById(R.id.b_good_c_progress);
        rltv = itemView.findViewById(R.id.broker_good_card);
        btnadd = itemView.findViewById(R.id.b_good_c_btn);

        if (callMethod.ReadBoolan("LineView")) {
            tv_line_name = itemView.findViewById(R.id.b_good_c_name);
            tv_line_maxsellprice = itemView.findViewById(R.id.b_good_c_maxsellprice);
            tv_line_amount = itemView.findViewById(R.id.b_good_c_amount);

        }
    }

    public void bind(
            ArrayList<Column> Columns,
            Good good,
            Context mContext,
            CallMethod callMethod,
            boolean showalarm
    ) {

        mainline.removeAllViews();


        for (Column column : Columns) {

            int sortOrder = safeInt(column.getSortOrder(), 0);

            if (sortOrder <= 1) {
                continue;
            }

            String columnName =
                    column.getColumnFieldValue("columnname");

            String rawValue =
                    good.getGoodFieldValue(columnName);

            TextView valueView = createGoodValueView(
                    column,
                    columnName,
                    rawValue,
                    sortOrder,
                    showalarm
            );

            // تست خیلی واضح
            valueView.setTextColor(Color.BLACK);
            valueView.setTextSize(16);

            mainline.addView(valueView);

        }

        mainline.post(() -> {

            callMethod.Log(
                    "MAINLINE SIZE => width=" +
                            mainline.getWidth() +
                            " height=" +
                            mainline.getHeight() +
                            " childCount=" +
                            mainline.getChildCount()
            );

        });
    }
    public void bind1(ArrayList<Column> Columns, Good good, Context mContext, CallMethod callMethod,boolean showalarm) {



        mainline.removeAllViews();
        for (Column column : Columns) {

            int sortOrder = safeInt(column.getSortOrder(), 0);

            if (sortOrder <= 1) {
                continue;
            }

            String columnName = column.getColumnFieldValue("columnname");
            String rawValue = good.getGoodFieldValue(columnName);

            TextView valueView = createGoodValueView(
                    column,
                    columnName,
                    rawValue,
                    sortOrder,
                    showalarm
            );

            mainline.addView(valueView);
        }
    }

    private String formatGoodValue(
            String columnName,
            String rawValue
    ) {
        String value = rawValue == null
                ? ""
                : rawValue.trim();

        if (value.isEmpty()) {
            return "-";
        }

        if (columnName != null &&
                columnName.contains("FirstBarCode")) {
            return value;
        }

        try {
            double number = Double.parseDouble(
                    value.replace(",", "")
            );

            if (Math.abs(number) > 999) {
                return decimalFormat.format(number);
            }

        } catch (Exception ignored) {
        }

        return value;
    }
    private int dp(int value) {
        return Math.round(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        value,
                        mContext.getResources()
                                .getDisplayMetrics()
                )
        );
    }

    private int safeInt(
            String value,
            int fallback
    ) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return fallback;
        }
    }
    private GradientDrawable createRowBackground(
            int backgroundColor,
            int primaryColor
    ) {
        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(backgroundColor);
        drawable.setCornerRadius(dp(6));

        drawable.setStroke(
                dp(1),
                ColorUtils.setAlphaComponent(
                        primaryColor,
                        35
                )
        );

        return drawable;
    }


    private TextView createGoodValueView(
            Column column,
            String columnName,
            String rawValue,
            int sortOrder,
            boolean showAlarm
    ) {
        TextView textView = new TextView(mContext);

        int bodySize = safeInt(
                callMethod.ReadString("BodySize"),
                13
        );

        if (!callMethod.ReadBoolan("LineView")) {
            bodySize = Math.min(bodySize, 13);
        }

        int primaryColor = MaterialColors.getColor(
                mContext,
                com.google.android.material.R.attr.colorPrimary,
                Color.DKGRAY
        );

        int surfaceColor = MaterialColors.getColor(
                mContext,
                com.google.android.material.R.attr.colorSurface,
                Color.WHITE
        );

        int onSurfaceColor = MaterialColors.getColor(
                mContext,
                com.google.android.material.R.attr.colorOnSurface,
                Color.BLACK
        );

        int backgroundColor = ColorUtils.blendARGB(
                surfaceColor,
                primaryColor,
                0.07f
        );

        int height = sortOrder == 2 ? dp(54) : dp(36);

        LinearLayoutCompat.LayoutParams params =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        height
                );

        params.setMargins(0, 0, 0, dp(3));

        textView.setLayoutParams(params);
        textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        textView.setPaddingRelative(dp(7), dp(2), dp(7), dp(2));
        textView.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
        textView.setTextColor(onSurfaceColor);
        textView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                bodySize
        );

        textView.setBackground(
                createRowBackground(
                        backgroundColor,
                        primaryColor
                )
        );

        String displayValue = formatGoodValue(
                columnName,
                rawValue
        );

        if (sortOrder == 2) {
            textView.setMaxLines(3);
            textView.setEllipsize(TextUtils.TruncateAt.END);

            if (displayValue.length() > 50) {
                displayValue = displayValue.substring(0, 50) + "...";
            }
        } else {
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }

        if (columnName != null && columnName.contains("FirstBarCode")) {
            textView.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    Math.max(10, bodySize - 3)
            );

            textView.setTextDirection(View.TEXT_DIRECTION_LTR);
            textView.setGravity(Gravity.CENTER);
        }

        if ("MaxSellPrice".equals(column.getColumnName())) {
//            textView.setTextColor(
//                    getcolorresource("3", mContext)
//            );

            textView.setTypeface(
                    textView.getTypeface(),
                    Typeface.BOLD
            );

            if (showAlarm) {
                displayValue = "نیاز به بروز رسانی قیمت";
            }
        }

        textView.setText(
                NumberFunctions.PerisanNumber(displayValue)
        );

        return textView;
    }

    public void bindLine(ArrayList<Column> Columns, Good good, Context mContext, CallMethod callMethod,boolean showalarm) {


        tv_line_name.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue("GoodName")));
        tv_line_maxsellprice.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Double.parseDouble(good.getGoodFieldValue("MaxSellPrice")))));
        tv_line_amount.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue("StackAmount")));
        if (showalarm){
            tv_line_maxsellprice.setText("نیاز به بروز رسانی قیمت");
        }


    }


    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForColorStateLists"})
    public void Actionbtn(Good good, boolean multi_select) {

        this.multi_select1 = multi_select;


        if (good.getGoodFieldValue("ActiveStack").equals("1")){
            btnadd.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.green_600));
        }else{
            btnadd.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.grey_700));
        }

        btnadd.setOnClickListener(view -> {
            if (good.getGoodFieldValue("ActiveStack").equals("1")) {
                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    broker_action.buydialog(good.getGoodFieldValue("GoodCode"), "0");
                } else {
                    Intent intent = new Intent(mContext, Broker_PFOpenActivity.class);
                    intent.putExtra("fac", "0");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                    mContext.startActivity(intent);
                }
            }else{
                callMethod.showToast("این کالا غیر فعال می باشد");
            }
        });


    }

    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForColorStateLists"})
    public void Actionrltv(Good good, boolean multi_select) {

        this.multi_select1 = multi_select;

            if (good.getGoodFieldValue("ActiveStack").equals("1")) {
                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    broker_action.buydialog(good.getGoodFieldValue("GoodCode"), "0");
                } else {
                    Intent intent = new Intent(mContext, Broker_PFOpenActivity.class);
                    intent.putExtra("fac", "0");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                }
            }else{
                callMethod.showToast("این کالا غیر فعال می باشد");
            }



    }

    public void callimage(Good good) {
        String imagecode = broker_dbh.GetLastksrImageCode(good.getGoodFieldValue("GoodCode"));

        progressBar.setVisibility(View.VISIBLE);

        if (image_info.Image_exist(imagecode)) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    imagecode + ".jpg"
            );
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            img.setImageBitmap(myBitmap);
            progressBar.setVisibility(View.GONE);
            return;
        }

        //  نمایش عکس پیش‌فرض
        byte[] imageByteArray1 = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
        Bitmap defaultBitmap = BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length);
        img.setImageBitmap(defaultBitmap);

        // 🔹 فراخوانی API برای گرفتن تصویر
        call = broker_apiInterface.GetImageFromKsr("GetImageFromKsr", good.getGoodFieldValue("KsrImageCode"));
        callMethod.Log(call.request().toString());

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    String imgText = response.body().getText();
                    if (!"no_photo".equals(imgText)) {
                        try {
                            byte[] decodedBytes = Base64.decode(imgText, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            image_info.SaveImage(bitmap, imagecode);
                            img.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            callMethod.Log("Decode Error: " + e.getMessage());
                            showErrorState(img);
                        }
                    } else {
                        showErrorState(img);
                    }
                } else {
                    showErrorState(img);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                callMethod.Log("Request Failed: " + t.getMessage());

                if (!NetworkUtils.isNetworkAvailable(mContext)) {
                    callMethod.showToast("اتصال اینترنت قطع است!");
                } else if (NetworkUtils.isVPNActive()) {
                    callMethod.showToast("VPN فعال است، ممکن است اتصال با سرور مختل شود!");
                } else if (!NetworkUtils.canReachServer(callMethod.ReadString("ServerURLUse"))) {
                    callMethod.showToast("سرور در دسترس نیست یا فیلتر شده است!");
                } else {
                    callMethod.showToast("مشکل در برقراری ارتباط با سرور برای بارگیری عکس");
                }

                showErrorState(img);
            }

        });
    }


    private void showErrorState(ImageView img) {
        img.setImageResource(R.drawable.error_img);
    }

    public void callimage1(Good good){
        String imagecode = broker_dbh.GetLastksrImageCode(good.getGoodFieldValue("GoodCode"));

        if (image_info.Image_exist(imagecode)) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    imagecode + ".jpg"
            );
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            img.setImageBitmap(myBitmap);

        } else {

            byte[] imageByteArray1;
            imageByteArray1 = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
            img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));

        }



        call = broker_apiInterface.GetImageFromKsr("GetImageFromKsr",good.getGoodFieldValue("KsrImageCode"));
        callMethod.Log(call.request().toString());
        if (!image_info.Image_exist(imagecode)) {


            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            image_info.SaveImage(
                                    BitmapFactory.decodeByteArray(
                                            Base64.decode(response.body().getText(), Base64.DEFAULT),
                                            0,
                                            Base64.decode(response.body().getText(), Base64.DEFAULT).length
                                    ),
                                    imagecode
                            );
                            callimage(good);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    callMethod.Log(t.getMessage());

                }
            });
        }


    }

    public int getcolorresource1(String colortarget, Context mContext) {
        int intcolor;
        switch (colortarget) {
            case ("2"):
                intcolor = mContext.getColor(R.color.colorAccent);
                break;
            case ("3"):
                intcolor = mContext.getColor(R.color.color_red);
                break;
            case ("4"):
                intcolor = mContext.getColor(R.color.color_sky);
                break;
            case ("5"):
                intcolor = mContext.getColor(R.color.color_green);
                break;
            case ("6"):
                intcolor = mContext.getColor(R.color.color_yellow);
                break;
            case ("7"):
                intcolor = mContext.getColor(R.color.color_pink);
                break;
            case ("8"):
                intcolor = mContext.getColor(R.color.color_indigo);
                break;
            case ("9"):
                intcolor = mContext.getColor(R.color.color_brown);
                break;
            case ("10"):
                intcolor = mContext.getColor(R.color.color_purple);
                break;
            case ("11"):
                intcolor = mContext.getColor(R.color.color_blue);
                break;
            case ("12"):
                intcolor = mContext.getColor(R.color.color_orange);
                break;

            default:
                intcolor = mContext.getColor(R.color.color_black);

                break;
        }


        return intcolor;
    }

}