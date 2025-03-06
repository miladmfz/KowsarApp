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
    private final Find_DBH find_dbh;

    CallMethod callMethod;
    Find_APIInterface find_apiInterface;
    public Dialog dialog;
    TextView tv_rep;
    TextView tv_step;

    public Find_Replication(Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(mContext);
        this.find_dbh = new Find_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.dialog = new Dialog(mContext);
        find_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Find_APIInterface.class);

    }

    public void DoingReplicate() {

        if (find_dbh.GetColumnscount().equals("0")) {
            dialog();
            tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی تنظیم جدول"));
            Call<RetrofitResponse> call1 = find_apiInterface.GetGoodType("GetGoodType");
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        ArrayList<Column> columns = response.body().getColumns();
                        for (Column column : columns) {
                            find_dbh.ReplicateGoodtype(column);
                        }
                        Call<RetrofitResponse> call2 = find_apiInterface.GetColumnList( "GetColumnList","1", "4", "1");
                        call2.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                if (response.isSuccessful()) {
                                    assert response.body() != null;
                                    ArrayList<Column> columns = response.body().getColumns();
                                    for (Column column : columns) {
                                        find_dbh.ReplicateColumn(column, 1);
                                    }
                                    Closedialog();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            }
                        });
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                }
            });
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






}
