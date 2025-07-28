package com.kits.kowsarapp.application.broker;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_NavActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.KowsarLocation;
import com.kits.kowsarapp.model.base.KowsarLocationNew;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.ReplicationModel;
import com.kits.kowsarapp.model.base.TableDetail;
import com.kits.kowsarapp.model.base.UserInfo;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Broker_Replication {
    private final Context mContext;
    private final SQLiteDatabase database;
    private final Integer RepRowCount = 100;
    private final Broker_DBH broker_dbh;
    ArrayList<KowsarLocation> locations = new ArrayList<>();
    ArrayList<KowsarLocationNew> locationsNew = new ArrayList<>();

    KowsarLocation location;
    KowsarLocationNew locationnew;

    CallMethod callMethod;
    Broker_APIInterface broker_apiInterface;
    Intent intent;
    ImageInfo image_info;
    String GpsLocationLastCode;

    private Integer FinalStep = 0;
    String LastRepCode = "0";
    public Dialog dialog;
    ArrayList<TableDetail> tableDetails = new ArrayList<>();
    ArrayList<ReplicationModel> replicationModels = new ArrayList<>();

    String url;
    Integer replicatelevel;
    Cursor cursor;
    SQLiteDatabase sqLiteDatabase;
    TextView tv_rep;
    TextView tv_step;

    public Broker_Replication(Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(mContext);
        this.broker_dbh = new Broker_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.image_info = new ImageInfo(mContext);
        url = callMethod.ReadString("ServerURLUse");
        database = mContext.openOrCreateDatabase(callMethod.ReadString("DatabaseName"), Context.MODE_PRIVATE, null);
        sqLiteDatabase = mContext.openOrCreateDatabase(callMethod.ReadString("DatabaseName"), Context.MODE_PRIVATE, null);
        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);

    }


    public void DoingReplicate() {

        dialog = new Dialog(mContext);
        dialog();
        SendGpsLocation();
        SendGpsLocationnew();


        if (broker_dbh.GetColumnscount().equals("0")) {
            tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی تنظیم جدول"));
            GoodTypeReplication();
        } else {
//            Call<RetrofitResponse> call1 = broker_apiInterface.GetMaxRepLog("MaxRepLogCode");
            Call<RetrofitResponse> call1 = broker_apiInterface.MaxRepLogCode("MaxRepLogCode");
            callMethod.Log(call1.request().toString()+"");
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    assert response.body() != null;
                    callMethod.Log(response.body().getText()+"");
                    broker_dbh.SaveConfig("MaxRepLogCode", Objects.requireNonNull(response.body()).getText());
                    RetrofitReplicate(0);
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                }
            });
        }


    }

//    public void DoingReplicateAuto() {
//
//        Call<RetrofitResponse> call1 = broker_apiInterface.GetMaxRepLog("MaxRepLogCode");
//        call1.enqueue(new Callback<RetrofitResponse>() {
//            @Override
//            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
//                broker_dbh.SaveConfig("MaxRepLogCode", response.body().getText());
//                RetrofitReplicateAuto(0);
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
//
//            }
//        });
//
//    }

    public void dialog() {
        dialog.setContentView(R.layout.broker_spinner_box);
        tv_rep = dialog.findViewById(R.id.b_spinner_text);
        tv_step = dialog.findViewById(R.id.b_spinner_step);
        dialog.show();


    }

    public void Closedialog() {

        dialog.dismiss();


    }

    public void RetrofitReplicate(Integer replevel) {
        broker_dbh.closedb();
        replicatelevel = replevel;
        replicationModels = broker_dbh.GetReplicationTable();
        if (replicatelevel < replicationModels.size()) {
            ReplicationModel replicatedetail = replicationModels.get(replicatelevel);



            String tableName;
            int currentStep = replicatedetail.getReplicationCode();
            int totalSteps = 15;


            switch (currentStep) {
                case 1:
                    tableName = "کالا";
                    break;
                case 2:
                    tableName = "موجودی انبار";
                    break;
                case 3:
                    tableName = "سرگروه";
                    break;
                case 4:
                    tableName = "گروه کالا";
                    break;
                case 5:
                    tableName = "اجزای پایه";
                    break;
                case 6:
                    tableName = "شهر";
                    break;
                case 7:
                    tableName = "ادرس";
                    break;
                case 8:
                    tableName = "مشتری";
                    break;
                case 9:
                    tableName = "خصوصیات اضافه";
                    break;
                case 10:
                    tableName = "گروهیندی ها";
                    break;
                case 11:
                    tableName = "سمت";
                    break;
                case 12:
                    tableName = "سمت شخص";
                    break;
                case 13:
                    tableName = "سمت شخص کالا";
                    break;
                case 14:
                    tableName = "مشتریان بازاریاب";
                    break;
                case 15:
                    tableName = "واحد سنجش";
                    break;
                default:
                    tableName = "نامشخص";
                    break;
            }

            String message = "مرحله " + currentStep + " از " + totalSteps + " در حال بروز رسانی " + tableName;
            tv_rep.setText(NumberFunctions.PerisanNumber(message));




            tableDetails = broker_dbh.GetTableDetail(replicatedetail.getClientTable());
            FinalStep = 0;
            LastRepCode = String.valueOf(replicatedetail.getLastRepLogCode());
            UserInfo userInfo = broker_dbh.LoadPersonalInfo();

//            Call<RetrofitResponse> call1 = broker_apiInterface.RetrofitReplicate("RetrofitReplicate",
            Call<RetrofitResponse> call1 = broker_apiInterface.RetrofitReplicate("repinfo",
                    String.valueOf(replicatedetail.getLastRepLogCode()),
                    replicatedetail.getServerTable(),
                    "",
                    "1",
                    String.valueOf(RepRowCount)
            );
            callMethod.Log(call1.request().toString());


            callMethod.Log("lastreplog= "+String.valueOf(replicatedetail.getLastRepLogCode()));
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        try {
                            callMethod.Log("8");
                            JSONArray arrayobject = null;
                            if (response.body() != null) {
                                arrayobject = new JSONArray(response.body().getText());
                            }
                            int ObjectSize = arrayobject.length();
                            JSONObject singleobject = arrayobject.getJSONObject(0);
                            String state = singleobject.getString("RLOpType");
                            FinalStep = Integer.parseInt(singleobject.getString("RowsCount"));
                            tv_step.setText(NumberFunctions.PerisanNumber(singleobject.getString("RowsCount") + "تعداد"));
                            tv_step.setVisibility(View.VISIBLE);

                            switch (state) {
                                case "n":
                                case "N":
                                    break;
                                default:

                                    for (int i = 0; i < ObjectSize; i++) {

                                        singleobject = arrayobject.getJSONObject(i);
                                        String reptype = singleobject.getString("RLOpType");
                                        String repcode = singleobject.getString("RepLogDataCode");

                                        String code = singleobject.getString(replicatedetail.getServerPrimaryKey());
                                        int columnDetail = tableDetails.size();
                                        StringBuilder qCol = new StringBuilder();

                                        switch (reptype) {
                                            case "U":
                                            case "u":
                                            case "I":
                                            case "i":

                                                for (TableDetail singletabale : tableDetails) {

                                                    if (singleobject.has(singletabale.getName())) {
                                                        singletabale.setText(singleobject.getString(singletabale.getName()));
                                                        if (singletabale.getText() != null)
                                                            singletabale.setText(singletabale.getText().replace("'", " "));
                                                    }
                                                }

                                                @SuppressLint("Recycle") Cursor d = database.rawQuery("Select Count(*) AS cntRec From " + replicatedetail.getClientTable() + " Where " + replicatedetail.getClientPrimaryKey() + " = " + code, null);
                                                d.moveToFirst();
                                                @SuppressLint("Range") int nc = d.getInt(d.getColumnIndex("cntRec"));
                                                if (nc == 0) {


                                                    qCol = new StringBuilder("INSERT INTO " + replicatedetail.getClientTable() + " ( ");
                                                    int QueryConditionCount = 0;
                                                    for (int z = 0; z < columnDetail; z++) {
                                                        if (tableDetails.get(z).getText() != null) {
                                                            if (QueryConditionCount > 0)
                                                                qCol.append(" , ");
                                                            qCol.append(" ").append(tableDetails.get(z).getName());
                                                            QueryConditionCount++;
                                                        }
                                                    }
                                                    qCol.append(" ) Select  ");
                                                    QueryConditionCount = 0;

                                                    for (int z = 0; z < columnDetail; z++) {
                                                        if (tableDetails.get(z).getText() != null) {
                                                            if (QueryConditionCount > 0)
                                                                qCol.append(" , ");
                                                            String valuetype = tableDetails.get(z).getType().substring(0, 2);
                                                            if (!tableDetails.get(z).getText().equals("null")) {
                                                                if (valuetype.equals("CH")) {
                                                                    qCol.append(" '").append(tableDetails.get(z).getText()).append("' ");
                                                                } else {
                                                                    qCol.append(" ").append(tableDetails.get(z).getText());
                                                                }
                                                            } else {
                                                                qCol.append(" ").append(tableDetails.get(z).getText());
                                                            }
                                                            QueryConditionCount++;
                                                        }
                                                    }
                                                } else {
                                                    qCol = new StringBuilder("Update " + replicatedetail.getClientTable() + "  Set ");
                                                    int QueryConditionCount = 0;
                                                    for (int z = 1; z < columnDetail; z++) {
                                                        if (tableDetails.get(z).getText() != null) {
                                                            if (QueryConditionCount > 0)
                                                                qCol.append(" , ");
                                                            if (!tableDetails.get(z).getText().equals("null")) {

                                                                String valuetype = tableDetails.get(z).getType().substring(0, 2);
                                                                if (valuetype.equals("CH")) {
                                                                    qCol.append(" ").append(tableDetails.get(z).getName()).append(" = '").append(tableDetails.get(z).getText()).append("' ");
                                                                } else if (valuetype.equals("BO")) {
                                                                    if (!tableDetails.get(z).getText().equals(""))
                                                                    {
                                                                        qCol.append(" ").append(tableDetails.get(z).getName()).append(" = ").append(tableDetails.get(z).getText()).append(" ");

                                                                    }else{
                                                                        qCol.append(" ").append(tableDetails.get(z).getName()).append(" = null ");
                                                                    }

                                                                }else {
                                                                    qCol.append(" ").append(tableDetails.get(z).getName()).append(" = ").append(tableDetails.get(z).getText()).append(" ");
                                                                }


                                                            } else {
                                                                qCol.append(" ").append(tableDetails.get(z).getName()).append(" = ").append(tableDetails.get(z).getText()).append(" ");
                                                            }
                                                            QueryConditionCount++;
                                                        }
                                                    }
                                                    qCol.append(" Where ").append(replicatedetail.getClientPrimaryKey()).append(" = ").append(code);

                                                }

                                                try {
                                                    callMethod.Log("kowsar_qCol="+repcode + " = " + qCol.toString());
                                                    database.execSQL(qCol.toString());
                                                    LastRepCode = repcode;
                                                } catch (Exception e) {
                                                    callMethod.Log(e.getMessage());
                                                }

                                                d.close();
                                                break;
                                            case "D":
                                            case "d":


                                                if (!replicatedetail.getServerTable().equals("")) {
                                                    String repObjectCode = singleobject.getString("RLObjectRef");
                                                    qCol = new StringBuilder("Delete from " + replicatedetail.getClientTable() + "  Where ").append(replicatedetail.getClientPrimaryKey()).append(" = ").append(repObjectCode);
                                                    try {
                                                        database.execSQL(qCol.toString());
                                                        LastRepCode = repcode;
                                                    } catch (Exception ignored) {
                                                    }
                                                }

                                                break;
                                        }
                                    }

                                    database.execSQL("Update ReplicationTable Set LastRepLogCode = " + LastRepCode + " Where ServerTable = '" + replicatedetail.getServerTable() + "' ");
                                    break;
                            }

                            if (arrayobject.length() >= RepRowCount) {
                                RetrofitReplicate(replicatelevel);
                            } else {

                                if (Integer.parseInt(LastRepCode) < 0) {

                                    database.execSQL("Update ReplicationTable Set LastRepLogCode = " + broker_dbh.ReadConfig("MaxRepLogCode") + " Where ServerTable = '" + replicatedetail.getServerTable() + "' ");

                                    RetrofitReplicate(replicatelevel);
                                } else {

                                    tv_step.setVisibility(View.GONE);
                                    RetrofitReplicate(replicatelevel + 1);
                                }
                            }
                        } catch (Exception ignored) {

                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    callMethod.Log("9");
                    callMethod.Log("kowsar_____"+t.getMessage());
                    RetrofitReplicate(replicatelevel);
                }
            });

        } else {
            replicateGoodImageChange();
        }
    }


    public void RetrofitReplicateAuto(Integer replevel) {
        broker_dbh.closedb();
        replicationModels = broker_dbh.GetReplicationTable();
        if (replevel < replicationModels.size()) {
            ReplicationModel replicatedetail = replicationModels.get(replevel);
            tableDetails = broker_dbh.GetTableDetail(replicatedetail.getClientTable());

            FinalStep = 0;
            LastRepCode = String.valueOf(replicatedetail.getLastRepLogCode());


            String where = replicatedetail.getCondition().replace("BrokerCondition", broker_dbh.ReadConfig("BrokerCode"));

            callMethod.Log("LastRepCode=" + LastRepCode);
            Call<RetrofitResponse> call1 = broker_apiInterface.RetrofitReplicate("RetrofitReplicate",
                    LastRepCode,
                    replicatedetail.getServerTable(),
                    "",
                    "1",
                    "100"
            );

            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        new Thread(() -> {

                            assert response.body() != null;
                            try {
                                JSONArray arrayobject = new JSONArray(response.body().getText());
                                int ObjectSize = arrayobject.length();
                                JSONObject singleobject = arrayobject.getJSONObject(0);
                                String state = singleobject.getString("RLOpType");

                                switch (state) {
                                    case "n":
                                    case "N":
                                        break;
                                    default:

                                        for (int i = 0; i < ObjectSize; i++) {

                                            singleobject = arrayobject.getJSONObject(i);
                                            String reptype = singleobject.getString("RLOpType");
                                            String repcode = singleobject.getString("RepLogDataCode");
                                            String code = singleobject.getString(replicatedetail.getServerPrimaryKey());

                                            int columnDetail = tableDetails.size();
                                            StringBuilder qCol = new StringBuilder();

                                            switch (reptype) {
                                                case "U":
                                                case "u":
                                                case "I":
                                                case "i":

                                                    for (TableDetail singletabale : tableDetails) {

                                                        if (singleobject.has(singletabale.getName())) {
                                                            singletabale.setText(singleobject.getString(singletabale.getName()));
                                                            if (singletabale.getText() != null)
                                                                singletabale.setText(singletabale.getText().replace("'", " "));
                                                        }
                                                    }

                                                    @SuppressLint("Recycle") Cursor d = database.rawQuery("Select Count(*) AS cntRec From " + replicatedetail.getClientTable() + " Where " + replicatedetail.getClientPrimaryKey() + " = " + code, null);
                                                    d.moveToFirst();
                                                    @SuppressLint("Range") int nc = d.getInt(d.getColumnIndex("cntRec"));
                                                    if (nc == 0) {


                                                        qCol = new StringBuilder("INSERT INTO " + replicatedetail.getClientTable() + " ( ");
                                                        int QueryConditionCount = 0;
                                                        for (int z = 0; z < columnDetail; z++) {
                                                            if (tableDetails.get(z).getText() != null) {
                                                                if (QueryConditionCount > 0)
                                                                    qCol.append(" , ");
                                                                qCol.append(" ").append(tableDetails.get(z).getName());
                                                                QueryConditionCount++;
                                                            }
                                                        }
                                                        qCol.append(" ) Select  ");
                                                        QueryConditionCount = 0;

                                                        for (int z = 0; z < columnDetail; z++) {
                                                            if (tableDetails.get(z).getText() != null) {
                                                                if (QueryConditionCount > 0)
                                                                    qCol.append(" , ");
                                                                String valuetype = tableDetails.get(z).getType().substring(0, 2);
                                                                if (!tableDetails.get(z).getText().equals("null")) {
                                                                    if (valuetype.equals("CH")) {
                                                                        qCol.append(" '").append(tableDetails.get(z).getText()).append("' ");
                                                                    } else {
                                                                        qCol.append(" ").append(tableDetails.get(z).getText());
                                                                    }
                                                                } else {
                                                                    qCol.append(" ").append(tableDetails.get(z).getText());
                                                                }
                                                                QueryConditionCount++;
                                                            }

                                                        }


                                                    } else {

                                                        qCol = new StringBuilder("Update " + replicatedetail.getClientTable() + "  Set ");
                                                        int QueryConditionCount = 0;
                                                        for (int z = 1; z < columnDetail; z++) {
                                                            if (tableDetails.get(z).getText() != null) {
                                                                if (QueryConditionCount > 0)
                                                                    qCol.append(" , ");
                                                                if (!tableDetails.get(z).getText().equals("null")) {
                                                                    String valuetype = tableDetails.get(z).getType().substring(0, 2);
                                                                    if (valuetype.equals("CH")) {
                                                                        qCol.append(" ").append(tableDetails.get(z).getName()).append(" = '").append(tableDetails.get(z).getText()).append("' ");
                                                                    } else {
                                                                        qCol.append(" ").append(tableDetails.get(z).getName()).append(" = ").append(tableDetails.get(z).getText()).append(" ");
                                                                    }
                                                                } else {
                                                                    qCol.append(" ").append(tableDetails.get(z).getName()).append(" = ").append(tableDetails.get(z).getText()).append(" ");
                                                                }
                                                                QueryConditionCount++;
                                                            }
                                                        }
                                                        qCol.append(" Where ").append(replicatedetail.getClientPrimaryKey()).append(" = ").append(code);

                                                    }

                                                    try {
                                                        database.execSQL(qCol.toString());
                                                        LastRepCode = repcode;

                                                    } catch (Exception ignored) {
                                                    }

                                                    d.close();
                                                    break;

                                                case "D":
                                                case "d":


                                                    if (!replicatedetail.getServerTable().equals("")) {
                                                        String repObjectCode = singleobject.getString("RLObjectRef");
                                                        qCol = new StringBuilder("Delete from " + replicatedetail.getClientTable() + "  Where ").append(replicatedetail.getClientPrimaryKey()).append(" = ").append(repObjectCode);
                                                        try {
                                                            database.execSQL(qCol.toString());
                                                            LastRepCode = repcode;
                                                        } catch (Exception ignored) {
                                                        }
                                                    }

                                                    break;
                                            }
                                        }
                                        database.execSQL("Update ReplicationTable Set LastRepLogCode = " + LastRepCode + " Where ServerTable = '" + replicatedetail.getServerTable() + "' ");
                                        break;
                                }
                                if (arrayobject.length() >= RepRowCount) {
                                    RetrofitReplicateAuto(replevel);
                                } else {
                                    if (Integer.parseInt(LastRepCode) < 0) {

                                        database.execSQL("Update ReplicationTable Set LastRepLogCode = " + broker_dbh.ReadConfig("MaxRepLogCode") + " Where ServerTable = '" + replicatedetail.getServerTable() + "' ");

                                        RetrofitReplicateAuto(replevel);
                                    } else {
                                        RetrofitReplicateAuto(replevel + 1);
                                    }
                                }
                            } catch (JSONException ignored) {
                            }

                        }).start();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                }
            });

        }


    }
    public void replicateGoodImageChange() {

        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی عکس"));
        FinalStep = 0;
        String RepTable = "KsrImage";
        cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='KsrImage_LastRepCode'", null);
        cursor.moveToFirst();
        LastRepCode = cursor.getString(0);
        cursor.close();

        Call<RetrofitResponse> call1 = broker_apiInterface.RetrofitReplicate("repinfo",
                 LastRepCode
                , RepTable
                ,""
                , "1"
                , String.valueOf(400)
        );
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    try {
                        JSONArray arrayobject = new JSONArray(response.body().getText());
                        int ObjectSize = arrayobject.length();
                        JSONObject singleobject = arrayobject.getJSONObject(0);
                        String state = singleobject.getString("RLOpType");

                        switch (state) {
                            case "n":
                            case "N":

                                break;
                            default:
                                tv_step.setVisibility(View.VISIBLE);
                                FinalStep = Integer.parseInt(arrayobject.getJSONObject(0).getString("RowsCount"));
                                for (int i = 0; i < ObjectSize; i++) {
                                    tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                                    singleobject = arrayobject.getJSONObject(i);
                                    String optype = singleobject.getString("RLOpType");
                                    String repcode = singleobject.getString("RepLogDataCode");
                                    String code = singleobject.getString("KsrImageCode");
                                    String qCol = "";
                                    String ObjectRef = singleobject.getString("ObjectRef");
                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From KsrImage Where KsrImageCode =" + code, null);

                                    d.moveToFirst();
                                    @SuppressLint("Range") int nc = d.getInt(d.getColumnIndex("cntRec"));

                                    switch (optype) {
                                        case "U":
                                        case "u":
                                        case "I":
                                        case "i":
                                            if (nc != 0) {
                                                qCol = "Delete from KsrImage Where KsrImageCode= " + code;
                                                try {
                                                    database.execSQL(qCol);
                                                    callMethod.Log("qCol=" + qCol);
                                                } catch (Exception ignored) {
                                                }
                                                image_info.DeleteImage(code);
                                            }

                                            qCol = "INSERT INTO KsrImage(KsrImageCode, ObjectRef,IsDefaultImage) Select " + code + "," + ObjectRef + ",'false'";

                                            try {
                                                database.execSQL(qCol);
                                                callMethod.Log("qCol=" + qCol);
                                            } catch (Exception ignored) {
                                            }
                                            d.close();
                                            break;

                                        case "D":
                                        case "d":

                                            qCol = "Delete from KsrImage Where KsrImageCode= " + code;
                                            image_info.DeleteImage(code);


                                            try {
                                                database.execSQL(qCol);
                                                callMethod.Log("qCol=" + qCol);
                                            } catch (Exception ignored) {
                                            }
                                            d.close();
                                            break;
                                    }
                                    LastRepCode = repcode;
                                }
                                database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'KsrImage_LastRepCode'");
                                break;
                        }

                        if (arrayobject.length() >= 400) {
                            replicateGoodImageChange();
                        } else {
                            tv_step.setVisibility(View.GONE);
                            dialog.dismiss();
                            intent = new Intent(mContext, Broker_NavActivity.class);
                            mContext.startActivity(intent);
                            ((Activity) mContext).finish();
                            callMethod.showToast("بروز رسانی انجام شد");
                        }
                    } catch (JSONException ignored) {

                    }

                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                replicateGoodImageChange();
            }
        });
    }

    public void BrokerStack() {

        UserInfo userInfo = broker_dbh.LoadPersonalInfo();
        broker_dbh.DatabaseCreate();
        Call<RetrofitResponse> call1 = broker_apiInterface.BrokerStack( "BrokerStack",userInfo.getBrokerCode());
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().getText().equals(broker_dbh.ReadConfig("BrokerStack"))) {
                        broker_dbh.SaveConfig("BrokerStack", response.body().getText());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });
        MenuBroker();
    }

    public void GroupCodeDefult() {

//        Call<RetrofitResponse> call1 = broker_apiInterface.DbSetupvalue("DbSetupvalue", "AppBroker_DefaultGroupCode");
        Call<RetrofitResponse> call1 = broker_apiInterface.info("kowsar_info", "AppBroker_DefaultGroupCode");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    if (!response.body().getText().equals("")) {
                        if (!response.body().getText().equals(broker_dbh.ReadConfig("GroupCodeDefult"))) {
                            broker_dbh.SaveConfig("GroupCodeDefult", response.body().getText());
                        }
                    } else {
                        broker_dbh.SaveConfig("GroupCodeDefult", "0");
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
            }
        });
    }

    public void MenuBroker() {
        //Call<RetrofitResponse> call1 = broker_apiInterface.GetMenuBroker("GetMenuBroker");
        Call<RetrofitResponse> call1 = broker_apiInterface.MenuBroker("GetMenuBroker");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().getText().equals(broker_dbh.ReadConfig("MenuBroker"))) {
                        broker_dbh.SaveConfig("MenuBroker", response.body().getText());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });
        GroupCodeDefult();
    }

    public void GoodTypeReplication() {

        Call<RetrofitResponse> call1 = broker_apiInterface.GetGoodType("GetGoodType");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Column> columns = response.body().getColumns();
                    for (Column column : columns) {
                        broker_dbh.ReplicateGoodtype(column);
                    }
                    columnReplication(0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });


    }

    public void columnReplication(Integer i) {

callMethod.Log(""+i);
        if (i < 4) {
            Call<RetrofitResponse> call2 = broker_apiInterface.GetColumnList( "GetColumnList","" + i, "1", "1");
            callMethod.Log(call2.request().toString());
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        ArrayList<Column> columns = response.body().getColumns();
                        int j = 0;
                        for (Column column : columns) {
                            broker_dbh.ReplicateColumn(column, i);
                            j++;
                        }
                        if (j == columns.size()) {
                            columnReplication(i + 1);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                    callMethod.Log(t.getMessage());
                }
            });
        } else {
            DoingReplicate();

        }
    }


    @SuppressLint("Range")
    public void SendGpsLocationnew() {

        locationsNew.clear();

        cursor = sqLiteDatabase.rawQuery("select * from GpsLocationNew where GpsLocationCode > " + broker_dbh.ReadConfig("LastGpsLocationCodeNew") + " order by GpsLocationCode limit 20", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                locationnew = new KowsarLocationNew();
                locationnew.setGpsLocationCode(String.valueOf(cursor.getInt(cursor.getColumnIndex("GpsLocationCode"))));
                locationsNew.add(locationnew);
            }
        }

        assert cursor != null;
        String GpsLocationString = CursorToJson(cursor);
        cursor.close();

        callMethod.Log(GpsLocationString);

        if (locationsNew.size()>0) {
            Call<RetrofitResponse> call1 = broker_apiInterface.UpdateLocation( "UpdateLocationNew",GpsLocationString);
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;

                        broker_dbh.SaveConfig("LastGpsLocationCodeNew", locationsNew.get(locationsNew.size() - 1).getGpsLocationCode());

                        cursor = sqLiteDatabase.rawQuery("select * from GpsLocation where GpsLocationCode > " + broker_dbh.ReadConfig("LastGpsLocationCodeNew"), null);
                        if (cursor.getCount() > 1) {
                            cursor.close();
                            SendGpsLocationnew();
                        } else {
                            cursor.close();
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                }
            });
        } else {
            callMethod.Log("kowsar_Gps zero size");
        }
    }



    @SuppressLint("Range")
    public void SendGpsLocation() {

        locations.clear();

        cursor = sqLiteDatabase.rawQuery("select * from GpsLocation where GpsLocationCode > " + broker_dbh.ReadConfig("LastGpsLocationCode") + " order by GpsLocationCode limit 20", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                location = new KowsarLocation();
                location.setGpsLocationCode(String.valueOf(cursor.getInt(cursor.getColumnIndex("GpsLocationCode"))));
                locations.add(location);
            }
        }

        assert cursor != null;
        String GpsLocationString = CursorToJson(cursor);
        cursor.close();

        if (locations.size()>0) {
            Call<RetrofitResponse> call1 = broker_apiInterface.UpdateLocation( "UpdateLocation",GpsLocationString);
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;

                        broker_dbh.SaveConfig("LastGpsLocationCode", locations.get(locations.size() - 1).getGpsLocationCode());

                        cursor = sqLiteDatabase.rawQuery("select * from GpsLocation where GpsLocationCode > " + broker_dbh.ReadConfig("LastGpsLocationCode"), null);
                        if (cursor.getCount() > 1) {
                            cursor.close();
                            SendGpsLocation();
                        } else {
                            cursor.close();
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                }
            });
        } else {
            callMethod.Log("kowsar_Gps zero size");
        }
    }


    public String CursorToJson(Cursor cursor) {
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("CursorToJson_Error: ", Objects.requireNonNull(e.getMessage()));
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet.toString();
    }




}
