package com.kits.kowsarapp.application.find;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.kits.kowsarapp.application.base.CallMethod;

import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.find.Find_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.find.Find_APIInterface;
import com.kits.kowsarapp.R;


import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;


public class Find_Action extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private final Context mContext;
    public Find_APIInterface find_apiInterface;
    public Call<RetrofitResponse> call;
    CallMethod callMethod;
    Find_DBH find_dbh;
    Intent intent;
    Integer il;
    PersianCalendar persianCalendar;
    String date;
    Dialog dialog, dialogProg;
    Calendar cldr;
    TimePickerDialog picker;
    TextView tv_reservestart;
    TextView tv_reserveend;
    TextView tv_date;
    int ehour = 0;
    int eminutes = 0;
    int printerconter ;

    TextView tv_rep;

    public Find_Action(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.find_dbh = new Find_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.find_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Find_APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);
        this.dialogProg = new Dialog(mContext);
        printerconter = 0;

    }





    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String tmonthOfYear, tdayOfMonth;
        tmonthOfYear = "0" + (monthOfYear + 1);
        tdayOfMonth = "0" + dayOfMonth;

        date = year + "/"
                + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                + tdayOfMonth.substring(tdayOfMonth.length() - 2);

        tv_date.setText(callMethod.NumberRegion(date));
    }





    public void showdialogProg() {
        dialogProg.setContentView(R.layout.broker_spinner_box);
        dialogProg.show();
    }
    public void dialogdissmiss() {

        dialogProg.dismiss();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }


}
