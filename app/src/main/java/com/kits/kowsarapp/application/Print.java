package com.kits.kowsarapp.application;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.AppPrinter;
import com.kits.kowsarapp.model.DatabaseHelper;
import com.kits.kowsarapp.model.Good;
import com.kits.kowsarapp.model.NumberFunctions;
import com.kits.kowsarapp.model.RetrofitResponse;
import com.kits.kowsarapp.webService.APIClient;
import com.kits.kowsarapp.webService.APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Print {




    private final Context mContext;
    public APIInterface apiInterface;
    public Call<RetrofitResponse> call;
    CallMethod callMethod;
    DatabaseHelper dbh;
    Integer il;
    Integer sizetext;
    PersianCalendar persianCalendar;
    Dialog dialog, dialogProg;
    Dialog dialogprint;
    int printerconter;
    ArrayList<AppPrinter> AppPrinters;
    int width = 500;
    LinearLayoutCompat main_layout;
    Bitmap bitmap_factor;
    String bitmap_factor_base64 = "";
    String PreFac = "";
    TextView tv_rep;

    ArrayList<Good> goods = new ArrayList<>();
    AppPrinter selectedPrinter;
    LinearLayoutCompat title_layout;
    LinearLayoutCompat boby_good_layout;
    LinearLayoutCompat good_layout;
    LinearLayoutCompat total_layout;
    ViewPager ViewPager;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    public Print(Context mContext,String PreFactorCode) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);
        this.dialogProg = new Dialog(mContext);
        this.AppPrinters = new ArrayList<>();
        this.PreFac=PreFactorCode;
        printerconter = 0;
        sizetext=Integer.parseInt(callMethod.ReadString("TitleSize"));
        //sizetext=40;
    }

    public void dialogProg() {
        dialogProg.setContentView(R.layout.rep_prog);
        tv_rep = dialogProg.findViewById(R.id.rep_prog_text);
        LottieAnimationView animationView = dialogProg.findViewById(R.id.lottie_raw);
        animationView.setAnimation(R.raw.receipt);
        dialogProg.show();

    }

    public void Start() {
        dialogProg();
        AppPrinters.clear();
        GetAppPrinterList();
    }


    public void GetAppPrinterList() {

        call = apiInterface.GetAppPrinter("OrderGetAppPrinter");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    AppPrinters = response.body().getAppPrinters();

                    Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.custom_printer_dialog); // You'll need to create this layout XML file
                    ListView listView = dialog.findViewById(R.id.listView);
                    ArrayList<String> printerNames = new ArrayList<>();
                    for (AppPrinter printer : AppPrinters) {
                        printerNames.add(printer.getPrinterExplain());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, printerNames);
                    listView.setAdapter(adapter);



                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        selectedPrinter = AppPrinters.get(position);
                        printDialogView();

                        dialog.dismiss();
                    });

                    dialog.show();


                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                GetAppPrinterList();

            }
        });
    }


    @SuppressLint("RtlHardcoded")
    public void printDialogView() {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);


        dialogprint = new Dialog(mContext);
        dialogprint.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogprint.setContentView(R.layout.print_layout_view);
        main_layout = dialogprint.findViewById(R.id.print_layout_view_ll);
        CreateView();

    }


    @SuppressLint("RtlHardcoded")
    public void CreateView() {
        tv_rep.setText("در حال برقراری ارتباط");
        main_layout.removeAllViews();
        ViewPager   ViewPager_rast;
        ViewPager  ViewPager_chap;
        goods.clear();
        goods = dbh.getAllPreFactorRows("", PreFac);
        main_layout = new LinearLayoutCompat(mContext);
        title_layout = new LinearLayoutCompat(mContext);
        boby_good_layout = new LinearLayoutCompat(mContext);
        good_layout = new LinearLayoutCompat(mContext);
        total_layout = new LinearLayoutCompat(mContext);
        ViewPager = new ViewPager(mContext);
        ViewPager_rast = new ViewPager(mContext);
        ViewPager_chap = new ViewPager(mContext);


        main_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        title_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        boby_good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width-6, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        total_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));


        main_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        main_layout.setBackgroundResource(R.color.white);
        title_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        good_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);
        boby_good_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        total_layout.setOrientation(LinearLayoutCompat.VERTICAL);

        main_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        title_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        boby_good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        total_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 3));
        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);




        ViewPager_rast.setLayoutParams(new LinearLayoutCompat.LayoutParams(3, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_rast.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_chap.setLayoutParams(new LinearLayoutCompat.LayoutParams(3, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_chap.setBackgroundResource(R.color.colorPrimaryDark);


        TextView company_tv = new TextView(App.getContext());
        company_tv.setText(NumberFunctions.PerisanNumber("فاکتور فروش"));
        company_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        company_tv.setTextSize(sizetext);
        company_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        company_tv.setGravity(Gravity.CENTER);
        company_tv.setPadding(0, 0, 0, 20);


        TextView customername_tv = new TextView(App.getContext());
        customername_tv.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + dbh.getFactorCustomer(PreFac)));
        customername_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        customername_tv.setTextSize(sizetext);
        customername_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        customername_tv.setGravity(Gravity.RIGHT);
        customername_tv.setPadding(0, 0, 0, 15);

        TextView factorcode_tv = new TextView(App.getContext());
        factorcode_tv.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + PreFac));
        factorcode_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        factorcode_tv.setTextSize(sizetext);
        factorcode_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        factorcode_tv.setGravity(Gravity.RIGHT);
        factorcode_tv.setPadding(0, 0, 0, 15);

        TextView factordate_tv = new TextView(App.getContext());
        factordate_tv.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + dbh.getFactordate(PreFac)));
        factordate_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        factordate_tv.setTextSize(sizetext);
        factordate_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        factordate_tv.setGravity(Gravity.RIGHT);
        factordate_tv.setPadding(0, 0, 0, 35);

        title_layout.addView(company_tv);
        title_layout.addView(customername_tv);
        title_layout.addView(factorcode_tv);
        title_layout.addView(factordate_tv);
        title_layout.addView(ViewPager);

        TextView total_amount_tv = new TextView(App.getContext());
        total_amount_tv.setText(NumberFunctions.PerisanNumber(" تعداد کل:   " + dbh.getFactorSumAmount(PreFac)));
        total_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        total_amount_tv.setTextSize(sizetext);
        total_amount_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        total_amount_tv.setGravity(Gravity.RIGHT);
        total_amount_tv.setPadding(0, 20, 0, 10);

        TextView total_price_tv = new TextView(App.getContext());
        total_price_tv.setText(NumberFunctions.PerisanNumber(" قیمت کل : " + decimalFormat.format(Integer.parseInt(dbh.getFactorSum(PreFac))) + " ریال"));
        total_price_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        total_price_tv.setTextSize(sizetext);
        total_price_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        total_price_tv.setGravity(Gravity.RIGHT);

        total_layout.addView(total_amount_tv);
        total_layout.addView(total_price_tv);


        int j = 0;
        for (Good gooddetail : goods) {
            j++;
            LinearLayoutCompat first_layout = new LinearLayoutCompat(App.getContext());
            first_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            first_layout.setOrientation(LinearLayoutCompat.VERTICAL);

            LinearLayoutCompat name_detail = new LinearLayoutCompat(App.getContext());
            name_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            name_detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
            name_detail.setWeightSum(6);
            name_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            TextView radif = new TextView(App.getContext());
            radif.setText(NumberFunctions.PerisanNumber(String.valueOf(j)));
            radif.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 5));
            radif.setTextSize(sizetext);
            radif.setGravity(Gravity.CENTER);
            radif.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            radif.setPadding(0, 10, 0, 10);

            ViewPager ViewPager_goodname = new ViewPager(App.getContext());
            ViewPager_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_goodname.setBackgroundResource(R.color.colorPrimary);
            TextView good_name_tv = new TextView(App.getContext());
            good_name_tv.setText(NumberFunctions.PerisanNumber(gooddetail.getGoodFieldValue("GoodName")));
            good_name_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1));
            good_name_tv.setTextSize(sizetext);
            good_name_tv.setGravity(Gravity.RIGHT);
            good_name_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            good_name_tv.setPadding(0, 10, 5, 0);

            name_detail.addView(radif);
            name_detail.addView(ViewPager_goodname);
            name_detail.addView(good_name_tv);

            LinearLayoutCompat detail = new LinearLayoutCompat(App.getContext());
            detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
            detail.setWeightSum(9);
            detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


            TextView good_price_tv = new TextView(App.getContext());
            good_price_tv.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(gooddetail.getGoodFieldValue("Price")))));
            good_price_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
            good_price_tv.setTextSize(sizetext);
            good_price_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            good_price_tv.setGravity(Gravity.CENTER);
            TextView good_amount_tv = new TextView(App.getContext());
            good_amount_tv.setText(NumberFunctions.PerisanNumber(gooddetail.getGoodFieldValue("FactorAmount")));
            good_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
            good_amount_tv.setTextSize(sizetext);
            good_amount_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            good_amount_tv.setGravity(Gravity.CENTER);

            long tprice = Integer.parseInt(gooddetail.getGoodFieldValue("FactorAmount")) * Integer.parseInt(gooddetail.getGoodFieldValue("Price"));


            TextView good_totalprice_tv = new TextView(App.getContext());
            good_totalprice_tv.setText(NumberFunctions.PerisanNumber(decimalFormat.format(tprice)));
            good_totalprice_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
            good_totalprice_tv.setTextSize(sizetext);
            good_totalprice_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            good_totalprice_tv.setPadding(0, 0, 0, 10);
            good_totalprice_tv.setGravity(Gravity.CENTER);

            ViewPager ViewPager_sell1 = new ViewPager(App.getContext());
            ViewPager_sell1.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_sell1.setBackgroundResource(R.color.colorPrimaryDark);
            ViewPager ViewPager_sell2 = new ViewPager(App.getContext());
            ViewPager_sell2.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_sell2.setBackgroundResource(R.color.colorPrimaryDark);

            detail.addView(good_price_tv);
            detail.addView(ViewPager_sell1);
            detail.addView(good_amount_tv);
            detail.addView(ViewPager_sell2);
            detail.addView(good_totalprice_tv);

            ViewPager extra_ViewPager = new ViewPager(App.getContext());
            extra_ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 2));
            extra_ViewPager.setBackgroundResource(R.color.colorPrimaryDark);

            ViewPager extra_ViewPager1 = new ViewPager(App.getContext());
            extra_ViewPager1.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 2));
            extra_ViewPager1.setBackgroundResource(R.color.colorPrimaryDark);


            first_layout.addView(name_detail);
            first_layout.addView(extra_ViewPager);
            first_layout.addView(detail);
            first_layout.addView(extra_ViewPager1);

            boby_good_layout.addView(first_layout);

        }


        good_layout.addView(ViewPager_rast);
        good_layout.addView(boby_good_layout);
        good_layout.addView(ViewPager_chap);


        main_layout.addView(title_layout);
        main_layout.addView(good_layout);
        main_layout.addView(total_layout);



        ImageInfo image_info = new ImageInfo(App.getContext());
        image_info.SaveImage_factor(loadBitmapFromView(main_layout), PreFac);
        bitmap_factor = loadBitmapFromView(main_layout);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap_factor.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();


        bitmap_factor_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Call<RetrofitResponse> call = apiInterface.AppBrokerPrint("AppBrokerPrint",
                bitmap_factor_base64,
                PreFac,
                selectedPrinter.getPrinterName(),
                selectedPrinter.getPrintCount()

        );

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                assert response.body() != null;
                if (response.body().getText().equals("Done")) {

                    callMethod.showToast("پرینت انجام شد");
                    dialogProg.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });


    }

    public Bitmap loadBitmapFromView(View v) {
        v.measure(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        Bitmap b = Bitmap.createBitmap(width, v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.layout(0, 0, width, v.getMeasuredHeight());
        v.draw(c);
        return b;
    }


}
