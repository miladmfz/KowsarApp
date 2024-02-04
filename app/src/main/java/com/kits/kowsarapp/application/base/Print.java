package com.kits.kowsarapp.application.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.AppPrinter;
import com.kits.kowsarapp.model.broker.Broker_DBH;

import com.kits.kowsarapp.model.RetrofitResponse;
import com.kits.kowsarapp.webService.base.APIClient;

import com.kits.kowsarapp.webService.broker.Broker_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;


import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;

public class Print {




    private final Context mContext;
    public Broker_APIInterface broker_apiInterface;
    public Call<RetrofitResponse> call;
    CallMethod callMethod;
    Broker_DBH dbh;
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

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    public Print(Context mContext,String PreFactorCode) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Broker_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);
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
        dialogProg.setContentView(R.layout.broker_spinner_box);
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

        //call = apiInterface.GetAppPrinter("OrderGetAppPrinter");
    }


    @SuppressLint("RtlHardcoded")
    public void printDialogView() {



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
