package com.kits.kowsarapp.application.find;


import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.NetworkUtils;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
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
            callMethod.Log("kowsar = "+call1.request() );
            callMethod.Log("kowsar = "+call1.request().toString() );
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
                                try {
                                    // 🟢 بررسی وضعیت اتصال
                                    if (!NetworkUtils.isNetworkAvailable(mContext)) {
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
