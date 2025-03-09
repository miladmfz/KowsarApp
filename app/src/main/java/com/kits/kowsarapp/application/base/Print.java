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
import com.kits.kowsarapp.model.base.AppPrinter;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.broker.Broker_DBH;

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
    Integer il;
    Integer sizetext;
    PersianCalendar persianCalendar;
    Dialog dialog, dialogProg;
    int printerconter;
    ArrayList<AppPrinter> AppPrinters;

    String PreFac = "";
    TextView tv_rep;


    public Print(Context mContext,String PreFactorCode) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
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
        tv_rep = dialogProg.findViewById(R.id.b_spinner_text);
        LottieAnimationView animationView = dialogProg.findViewById(R.id.b_spinner_lottie);
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



}
