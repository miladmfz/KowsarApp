package com.kits.kowsarapp.application.find;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kits.kowsarapp.activity.find.Find_NavActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.base.UserInfo;
import com.kits.kowsarapp.model.find.Find_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.find.Find_APIInterface;
import com.kits.kowsarapp.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Find_Replication {
    private final Context mContext;
    private final SQLiteDatabase database;
    private final Integer RepRowCount = 100;
    private final Find_DBH dbh;

    CallMethod callMethod;
    Find_APIInterface find_apiInterface;
    Intent intent;


    public Dialog dialog;

    String url;

    SQLiteDatabase sqLiteDatabase;
    TextView tv_rep;
    TextView tv_step;

    public Find_Replication(Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Find_DBH(mContext, callMethod.ReadString("DatabaseName"));

        url = callMethod.ReadString("ServerURLUse");
        database = mContext.openOrCreateDatabase(callMethod.ReadString("DatabaseName"), Context.MODE_PRIVATE, null);
        sqLiteDatabase = mContext.openOrCreateDatabase(callMethod.ReadString("DatabaseName"), Context.MODE_PRIVATE, null);
        find_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Find_APIInterface.class);

    }


    public void DoingReplicate() {

        dialog = new Dialog(mContext);
        dialog();


        if (dbh.GetColumnscount().equals("0")) {
            tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی تنظیم جدول"));
            GoodTypeReplication();
        } else {
            tv_step.setVisibility(View.GONE);
            dialog.dismiss();
            intent = new Intent(mContext, Find_NavActivity.class);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
            callMethod.showToast("بروز رسانی انجام شد");
        }


    }


    public void dialog() {
        dialog.setContentView(R.layout.broker_spinner_box);
        tv_rep = dialog.findViewById(R.id.b_spinner_text);
        tv_step = dialog.findViewById(R.id.b_spinner_step);
        dialog.show();


    }



    public void Closedialog() {

        dialog.dismiss();


    }



    public void BrokerStack() {

        UserInfo userInfo = dbh.LoadPersonalInfo();

        Call<RetrofitResponse> call1 = find_apiInterface.BrokerStack( "BrokerStack",userInfo.getBrokerCode());



        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().getText().equals(dbh.ReadConfig("BrokerStack"))) {
                        dbh.SaveConfig("BrokerStack", response.body().getText());
                        DoingReplicate();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });

    }


    public void GoodTypeReplication() {

        Call<RetrofitResponse> call1 = find_apiInterface.GetGoodType("GetGoodType");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Column> columns = response.body().getColumns();
                    for (Column column : columns) {
                        dbh.ReplicateGoodtype(column);
                    }
                    columnReplication();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });


    }

    public void columnReplication() {


        Call<RetrofitResponse> call2 = find_apiInterface.GetColumnList( "GetColumnList","1", "4", "1");
        call2.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Column> columns = response.body().getColumns();
                    for (Column column : columns) {
                        dbh.ReplicateColumn(column, 1);

                    }
                    DoingReplicate();

                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });


    }








}
