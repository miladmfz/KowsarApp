package com.kits.kowsarapp.application.broker;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_NavActivity;
import com.kits.kowsarapp.application.base.Base_NetworkFailure;
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
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Broker_Replication {
    private final Context mContext;
    private final SQLiteDatabase database;
    private final Broker_DBH broker_dbh;
    private final ExecutorService replicationExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final BrokerReplicationSession replicationSession = new BrokerReplicationSession();
    private final BrokerReplicationCallTracker<Call<RetrofitResponse>> replicationCalls =
            new BrokerReplicationCallTracker<>();
    private final Object replicationLifecycleLock = new Object();
    private volatile int currentRunToken;
    private volatile boolean currentRunAutomatic;
    private static final int LEGACY_REPLICATION_ROW_COUNT = 100;
    private final LinkedHashSet<String> changedGoodCodesForFTS = new LinkedHashSet<>();

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


    private boolean isTextColumnType(String type) {
        if (type == null) {
            return false;
        }

        String normalized = type.trim().toUpperCase();

        return normalized.startsWith("CHAR") ||
                normalized.startsWith("NCHAR") ||
                normalized.startsWith("VARCHAR") ||
                normalized.startsWith("NVARCHAR") ||
                normalized.startsWith("TEXT") ||
                normalized.startsWith("CLOB");
    }

    private boolean isBooleanColumnType(String type) {
        if (type == null) {
            return false;
        }

        return type.trim().toUpperCase().startsWith("BO");
    }


    public void DoingReplicate() {

        int runToken = beginReplicationRun(false);

        dialog = new Dialog(mContext);
        dialog();
        SendGpsLocation();
        SendGpsLocationnew();


        if (broker_dbh.GetColumnscount().equals("0")) {
            tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی تنظیم جدول"));
            GoodTypeReplication(runToken);
        } else {
//            Call<RetrofitResponse> call1 = broker_apiInterface.GetMaxRepLog("MaxRepLogCode");
            Call<RetrofitResponse> call1 = broker_apiInterface.MaxRepLogCode("MaxRepLogCode");
            if (!trackReplicationCall(runToken, call1)) {
                return;
            }
            callMethod.Log(call1.request().toString()+"");
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (!acceptReplicationCallback(runToken, call)) {
                        return;
                    }
                    String maxReplicationCode = responseText(response);
                    if (maxReplicationCode == null) {
                        failReplication(runToken, "Broker MaxRepLogCode", null);
                        return;
                    }
                    try {
                        Long.parseLong(maxReplicationCode);
                    } catch (NumberFormatException exception) {
                        failReplication(runToken, "Broker MaxRepLogCode", exception);
                        return;
                    }
                    broker_dbh.SaveConfig("MaxRepLogCode", maxReplicationCode);
                    changedGoodCodesForFTS.clear();
                    requestReplicationStep(0, false, runToken);
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (!acceptReplicationCallback(runToken, call) || call.isCanceled()) {
                        return;
                    }
                    Base_NetworkFailure.show(
                            mContext,
                            callMethod,
                            "Broker MaxRepLogCode",
                            call,
                            t
                    );
                    failReplication(runToken, "Broker MaxRepLogCode", t);
                }
            });
        }


    }

    public void DoingReplicateAuto() {
        int runToken = beginReplicationRun(true);
        Call<RetrofitResponse> call1 = broker_apiInterface.MaxRepLogCode("MaxRepLogCode");
        if (!trackReplicationCall(runToken, call1)) {
            return;
        }
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (!acceptReplicationCallback(runToken, call)) {
                    return;
                }
                String maxReplicationCode = responseText(response);
                if (maxReplicationCode == null) {
                    failReplication(runToken, "Broker automatic MaxRepLogCode", null);
                    return;
                }
                try {
                    Long.parseLong(maxReplicationCode);
                } catch (NumberFormatException exception) {
                    failReplication(runToken, "Broker automatic MaxRepLogCode", exception);
                    return;
                }
                broker_dbh.SaveConfig("MaxRepLogCode", maxReplicationCode);
                changedGoodCodesForFTS.clear();
                requestReplicationStep(0, true, runToken);
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                if (!acceptReplicationCallback(runToken, call) || call.isCanceled()) {
                    return;
                }
                Base_NetworkFailure.show(
                        mContext,
                        callMethod,
                        "Broker automatic MaxRepLogCode",
                        call,
                        t
                );
                failReplication(runToken, "Broker automatic MaxRepLogCode", t);
            }
        });

    }

    public void dialog() {
        dialog.setContentView(R.layout.broker_spinner_box);
        tv_rep = dialog.findViewById(R.id.b_spinner_text);
        tv_step = dialog.findViewById(R.id.b_spinner_step);
        dialog.show();
        dialog.setOnCancelListener(ignored -> cancelReplication());


    }

    public void Closedialog() {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }


    }

    private void GoNextReplicationStepAfterFTS(int currentStep, int nextLevel) {

        if ((currentStep == 1 || currentStep == 16) && broker_dbh.IsGoodSearchFTSReady() && changedGoodCodesForFTS.size() > 0) {

            broker_dbh.SyncGoodsSearchFTSBatchAsync(
                    new ArrayList<>(changedGoodCodesForFTS),
                    new Broker_DBH.OneGoodFTSCallback() {

                        @Override
                        public void onStart(String message) {
                            if (tv_step != null) {
                                tv_step.setVisibility(View.VISIBLE);
                                tv_step.setText(NumberFunctions.PerisanNumber(message));
                            }
                        }

                        @Override
                        public void onDone(String message) {
                            changedGoodCodesForFTS.clear();

                            if (tv_step != null) {
                                tv_step.setVisibility(View.GONE);
                            }

                            callMethod.Log(message);
                            RetrofitReplicate(nextLevel);
                        }

                        @Override
                        public void onError(Exception e) {
                            changedGoodCodesForFTS.clear();

                            if (tv_step != null) {
                                tv_step.setVisibility(View.GONE);
                            }

                            callMethod.Log("FTS Batch Error = " + e.getMessage());
                            RetrofitReplicate(nextLevel);
                        }
                    }
            );

        } else {

            if (tv_step != null) {
                tv_step.setVisibility(View.GONE);
            }

            RetrofitReplicate(nextLevel);
        }
    }

    private void GoNextReplicationAutoStepAfterFTS(int currentStep, int nextLevel) {

        if ((currentStep == 1 || currentStep == 16) && broker_dbh.IsGoodSearchFTSReady() && changedGoodCodesForFTS.size() > 0) {

            broker_dbh.SyncGoodsSearchFTSBatchAsync(
                    new ArrayList<>(changedGoodCodesForFTS),
                    new Broker_DBH.OneGoodFTSCallback() {

                        @Override
                        public void onStart(String message) {
                            callMethod.Log(message);
                        }

                        @Override
                        public void onDone(String message) {
                            changedGoodCodesForFTS.clear();
                            callMethod.Log(message);
                            RetrofitReplicateAuto(nextLevel);
                        }

                        @Override
                        public void onError(Exception e) {
                            changedGoodCodesForFTS.clear();
                            callMethod.Log("FTS Auto Batch Error = " + e.getMessage());
                            RetrofitReplicateAuto(nextLevel);
                        }
                    }
            );

        } else {
            RetrofitReplicateAuto(nextLevel);
        }
    }

    private int beginReplicationRun(boolean automatic) {
        int token;
        Call<RetrofitResponse> previousCall;
        synchronized (replicationLifecycleLock) {
            token = replicationSession.begin();
            previousCall = replicationCalls.clear();
            currentRunToken = token;
            currentRunAutomatic = automatic;
        }
        cancelCall(previousCall);
        return token;
    }

    private boolean trackReplicationCall(int token, Call<RetrofitResponse> call) {
        Call<RetrofitResponse> previousCall = null;
        boolean accepted;
        synchronized (replicationLifecycleLock) {
            accepted = replicationSession.isActive(token);
            if (accepted) {
                previousCall = replicationCalls.replace(call);
            }
        }
        if (!accepted) {
            cancelCall(call);
            return false;
        }
        cancelCall(previousCall);
        return true;
    }

    private boolean acceptReplicationCallback(int token, Call<RetrofitResponse> call) {
        synchronized (replicationLifecycleLock) {
            return replicationSession.isActive(token) && replicationCalls.complete(call);
        }
    }

    private void cancelCall(Call<RetrofitResponse> call) {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    public void cancelReplication() {
        Call<RetrofitResponse> call;
        synchronized (replicationLifecycleLock) {
            int token = currentRunToken;
            if (!replicationSession.cancel(token)) {
                return;
            }
            call = replicationCalls.clear();
        }
        broker_dbh.cancelPendingAsync();
        cancelCall(call);
        callMethod.Log("Broker replication cancelled; committed checkpoint preserved");
        closeDialogSafely();
    }

    public void release() {
        Call<RetrofitResponse> call;
        synchronized (replicationLifecycleLock) {
            replicationSession.cancel(currentRunToken);
            call = replicationCalls.clear();
        }
        cancelCall(call);
        mainHandler.removeCallbacksAndMessages(null);
        broker_dbh.closedb();
        closeDialogSafely();
    }

    private void failReplication(int token, String operation, Throwable throwable) {
        Call<RetrofitResponse> call;
        synchronized (replicationLifecycleLock) {
            if (!replicationSession.fail(token)) {
                return;
            }
            call = replicationCalls.clear();
        }
        cancelCall(call);
        String reason = throwable == null
                ? "invalid response"
                : throwable.getClass().getSimpleName();
        callMethod.Log(operation + " stopped safely: " + reason);
        if (!currentRunAutomatic) {
            mainHandler.post(() -> {
                closeDialogSafely();
                callMethod.showToast("بروزرسانی کامل نشد؛ از آخرین مرحله موفق دوباره اجرا کنید");
            });
        }
    }

    public void RetrofitReplicate(Integer replevel) {
        requestReplicationStep(replevel, false, currentRunToken);
    }

    public void RetrofitReplicateAuto(Integer replevel) {
        requestReplicationStep(replevel, true, currentRunToken);
    }

    private void requestReplicationStep(int level, boolean automatic, int runToken) {
        if (!replicationSession.isActive(runToken)) {
            return;
        }

        ArrayList<ReplicationModel> models = broker_dbh.GetReplicationTable();
        if (models.isEmpty()) {
            failReplication(runToken, "Broker replication table", null);
            return;
        }
        if (level < 0 || level >= models.size()) {
            if (automatic) {
                replicationSession.complete(runToken);
                callMethod.Log("Broker automatic replication completed");
            } else {
                mainHandler.post(() -> {
                    if (replicationSession.isActive(runToken)) {
                        replicateGoodImageChange();
                    }
                });
            }
            return;
        }

        ReplicationModel model = models.get(level);
        BrokerReplicationPolicy.StepSpec step =
                BrokerReplicationPolicy.step(model.getReplicationCode());
        try {
            BrokerReplicationBatchApplier.validateModelIdentifiers(model);
        } catch (RuntimeException exception) {
            failReplication(runToken, "Broker replication identifiers", exception);
            return;
        }
        ArrayList<TableDetail> schema = broker_dbh.GetTableDetail(model.getClientTable());
        try {
            BrokerReplicationBatchApplier.validateContract(model, schema);
        } catch (RuntimeException exception) {
            failReplication(runToken, "Broker replication contract", exception);
            return;
        }

        updateReplicationProgress(runToken, automatic, step, 0, 0);
        Call<RetrofitResponse> call = broker_apiInterface.RetrofitReplicate(
                "repinfo",
                String.valueOf(model.getLastRepLogCode()),
                model.getServerTable(),
                "",
                "1",
                String.valueOf(step.getBatchSize())
        );
        if (!trackReplicationCall(runToken, call)) {
            return;
        }
        callMethod.Log("Broker replication request step=" + step.getCode()
                + " checkpoint=" + model.getLastRepLogCode());

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<RetrofitResponse> completedCall,
                    @NonNull Response<RetrofitResponse> response
            ) {
                if (!acceptReplicationCallback(runToken, completedCall)) {
                    return;
                }
                String responseText = responseText(response);
                if (responseText == null) {
                    failReplication(runToken, "Broker replication response", null);
                    return;
                }

                boolean ftsWasReady = broker_dbh.IsGoodSearchFTSReady();
                replicationExecutor.execute(() -> applyReplicationResponse(
                        level,
                        automatic,
                        runToken,
                        model,
                        schema,
                        step,
                        ftsWasReady,
                        responseText
                ));
            }

            @Override
            public void onFailure(
                    @NonNull Call<RetrofitResponse> failedCall,
                    @NonNull Throwable throwable
            ) {
                if (!acceptReplicationCallback(runToken, failedCall) || failedCall.isCanceled()) {
                    return;
                }
                if (automatic) {
                    Base_NetworkFailure.logOnly(
                            callMethod,
                            "Broker automatic replication step",
                            failedCall,
                            throwable
                    );
                } else {
                    Base_NetworkFailure.show(
                            mContext,
                            callMethod,
                            "Broker replication step",
                            failedCall,
                            throwable
                    );
                }
                failReplication(runToken, "Broker replication network", throwable);
            }
        });
    }

    private void applyReplicationResponse(
            int level,
            boolean automatic,
            int runToken,
            ReplicationModel model,
            ArrayList<TableDetail> schema,
            BrokerReplicationPolicy.StepSpec step,
            boolean ftsWasReady,
            String responseText
    ) {
        if (!replicationSession.isActive(runToken)) {
            return;
        }

        try {
            JSONArray rows = new JSONArray(responseText);
            BrokerReplicationBatchApplier.Result result =
                    BrokerReplicationBatchApplier.apply(
                            database,
                            model,
                            schema,
                            rows,
                            step.getCode(),
                            broker_dbh.ReadConfig("MaxRepLogCode")
                    );
            LastRepCode = result.getLastReplicationCode();
            int expectedRows = readExpectedRows(rows, step.getBatchSize());
            updateReplicationProgress(
                    runToken, automatic, step, result.getAppliedRows(), expectedRows);

            if (!replicationSession.isActive(runToken)) {
                return;
            }

            if (result.isCheckpointRebased()) {
                continueAfterReplicationBatch(
                        level,
                        automatic,
                        runToken,
                        step.getCode(),
                        ftsWasReady,
                        result.getAffectedGoodCodes()
                );
                return;
            }

            BrokerReplicationPolicy.NextAction action = result.isNoChanges()
                    ? BrokerReplicationPolicy.NextAction.NEXT_STEP
                    : BrokerReplicationPolicy.nextAction(
                            result.getReceivedRows(), step.getBatchSize());
            int nextLevel = action == BrokerReplicationPolicy.NextAction.REPEAT_STEP
                    ? level
                    : level + 1;
            continueAfterReplicationBatch(
                    nextLevel,
                    automatic,
                    runToken,
                    step.getCode(),
                    ftsWasReady,
                    result.getAffectedGoodCodes()
            );
        } catch (Exception exception) {
            failReplication(runToken, "Broker replication batch", exception);
        }
    }

    private void continueAfterReplicationBatch(
            int nextLevel,
            boolean automatic,
            int runToken,
            int currentStep,
            boolean ftsWasReady,
            List<String> affectedGoodCodes
    ) {
        if (!replicationSession.isActive(runToken)) {
            return;
        }

        Runnable continueReplication = () -> requestReplicationStep(
                nextLevel, automatic, runToken);
        if ((currentStep == 1 || currentStep == 16)
                && !affectedGoodCodes.isEmpty()
                && ftsWasReady) {
            broker_dbh.SyncGoodsSearchFTSBatchAsync(
                    new ArrayList<>(affectedGoodCodes),
                    new Broker_DBH.OneGoodFTSCallback() {
                        @Override
                        public void onStart(String message) {
                            updateStepText(runToken, automatic, message);
                        }

                        @Override
                        public void onDone(String message) {
                            broker_dbh.SaveFTSReady("1");
                            callMethod.Log(message);
                            if (replicationSession.isActive(runToken)) {
                                continueReplication.run();
                            }
                        }

                        @Override
                        public void onError(Exception exception) {
                            broker_dbh.SaveFTSReady("0");
                            callMethod.Log("Broker derived FTS update failed; replication checkpoint is safe");
                            if (replicationSession.isActive(runToken)) {
                                continueReplication.run();
                            }
                        }
                    }
            );
        } else {
            continueReplication.run();
        }
    }

    private int readExpectedRows(JSONArray rows, int fallback) {
        if (rows.length() == 0) {
            return fallback;
        }
        try {
            int expected = rows.getJSONObject(0).optInt("RowsCount", fallback);
            return expected > 0 ? expected : fallback;
        } catch (JSONException ignored) {
            return fallback;
        }
    }

    private void updateReplicationProgress(
            int runToken,
            boolean automatic,
            BrokerReplicationPolicy.StepSpec step,
            int appliedRows,
            int expectedRows
    ) {
        if (automatic) {
            return;
        }
        mainHandler.post(() -> {
            if (!replicationSession.isActive(runToken)) {
                return;
            }
            if (tv_rep != null) {
                String message = "مرحله " + step.getCode() + " از "
                        + BrokerReplicationPolicy.TOTAL_STEPS
                        + " در حال بروز رسانی " + step.getTitle();
                tv_rep.setText(NumberFunctions.PerisanNumber(message));
            }
            if (tv_step != null) {
                int percent = BrokerReplicationPolicy.overallProgressPercent(
                        step.getCode(), appliedRows, expectedRows);
                tv_step.setVisibility(View.VISIBLE);
                tv_step.setText(NumberFunctions.PerisanNumber(
                        percent + "% - " + appliedRows + " از " + expectedRows));
            }
        });
    }

    private void updateStepText(int runToken, boolean automatic, String message) {
        if (automatic) {
            callMethod.Log(message);
            return;
        }
        mainHandler.post(() -> {
            if (!replicationSession.isActive(runToken)) {
                return;
            }
            if (tv_step != null) {
                tv_step.setVisibility(View.VISIBLE);
                tv_step.setText(NumberFunctions.PerisanNumber(message));
            }
        });
    }

    @Deprecated
    private void RetrofitReplicateLegacy(Integer replevel) {
        broker_dbh.closedb();
        replicatelevel = replevel;
        replicationModels = broker_dbh.GetReplicationTable();

        if (replicatelevel < replicationModels.size()) {

            ReplicationModel replicatedetail = replicationModels.get(replicatelevel);

            String tableName;
            String RowExec;

            int currentStep = replicatedetail.getReplicationCode();
            int totalSteps = 16;

            switch (currentStep) {
                case 1:
                    tableName = "کالا";
                    RowExec = "100";
                    break;
                case 2:
                    tableName = "موجودی انبار";
                    RowExec = "400";
                    break;
                case 3:
                    tableName = "سرگروه";
                    RowExec = "400";
                    break;
                case 4:
                    tableName = "گروه کالا";
                    RowExec = "600";
                    break;
                case 5:
                    tableName = "اجزای پایه";
                    RowExec = "300";
                    break;
                case 6:
                    tableName = "شهر";
                    RowExec = "400";
                    break;
                case 7:
                    tableName = "ادرس";
                    RowExec = "300";
                    break;
                case 8:
                    tableName = "مشتری";
                    RowExec = "300";
                    break;
                case 9:
                    tableName = "خصوصیات اضافه";
                    RowExec = "200";
                    break;
                case 10:
                    tableName = "گروهیندی ها";
                    RowExec = "600";
                    break;
                case 11:
                    tableName = "سمت";
                    RowExec = "600";
                    break;
                case 12:
                    tableName = "سمت شخص";
                    RowExec = "600";
                    break;
                case 13:
                    tableName = "سمت شخص کالا";
                    RowExec = "300";
                    break;
                case 14:
                    tableName = "مشتریان بازاریاب";
                    RowExec = "500";
                    break;
                case 15:
                    tableName = "واحد سنجش";
                    RowExec = "200";
                    break;
                case 16:
                    tableName = "بارکدهای کالا";
                    RowExec = "500";
                    break;
                default:
                    tableName = "نامشخص";
                    RowExec = "100";
                    break;
            }

            String message = "مرحله " + currentStep + " از " + totalSteps + " در حال بروز رسانی " + tableName;
            tv_rep.setText(NumberFunctions.PerisanNumber(message));

            tableDetails = broker_dbh.GetTableDetail(replicatedetail.getClientTable());
            FinalStep = 0;
            LastRepCode = String.valueOf(replicatedetail.getLastRepLogCode());

            Call<RetrofitResponse> call1 = broker_apiInterface.RetrofitReplicate(
                    "repinfo",
                    String.valueOf(replicatedetail.getLastRepLogCode()),
                    replicatedetail.getServerTable(),
                    "",
                    "1",
                    RowExec
            );

            callMethod.Log(call1.request().toString());
            callMethod.Log("lastreplog= " + String.valueOf(replicatedetail.getLastRepLogCode()));

            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        try {
                            JSONArray arrayobject = null;
                            RetrofitResponse responseBody = successfulBody(response);

                            if (responseBody != null) {
                                arrayobject = new JSONArray(
                                        BrokerReplicationResponsePolicy.text(responseBody));
                            }

                            if (arrayobject == null || arrayobject.length() == 0) {
                                GoNextReplicationStepAfterFTS(currentStep, replicatelevel + 1);
                                return;
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

                                                        if (singletabale.getText() != null) {
                                                            singletabale.setText(singletabale.getText().replace("'", " "));
                                                        }
                                                    }
                                                }

                                                Cursor d = null;

                                                try {
                                                    d = database.rawQuery(
                                                            "Select Count(*) AS cntRec From " +
                                                                    replicatedetail.getClientTable() +
                                                                    " Where " +
                                                                    replicatedetail.getClientPrimaryKey() +
                                                                    " = " +
                                                                    code,
                                                            null
                                                    );

                                                    d.moveToFirst();

                                                    @SuppressLint("Range")
                                                    int nc = d.getInt(d.getColumnIndex("cntRec"));

                                                    if (nc == 0) {

                                                        qCol = new StringBuilder("INSERT INTO " + replicatedetail.getClientTable() + " ( ");

                                                        int QueryConditionCount = 0;

                                                        for (int z = 0; z < columnDetail; z++) {
                                                            if (tableDetails.get(z).getText() != null) {
                                                                if (QueryConditionCount > 0) {
                                                                    qCol.append(" , ");
                                                                }

                                                                qCol.append(" ").append(tableDetails.get(z).getName());
                                                                QueryConditionCount++;
                                                            }
                                                        }

                                                        qCol.append(" ) Select  ");

                                                        QueryConditionCount = 0;

                                                        for (int z = 0; z < columnDetail; z++) {
                                                            if (tableDetails.get(z).getText() != null) {
                                                                if (QueryConditionCount > 0) {
                                                                    qCol.append(" , ");
                                                                }

                                                                String valueType = tableDetails.get(z).getType();

                                                                if (!tableDetails.get(z).getText().equals("null")) {
                                                                    if (isTextColumnType(valueType)) {
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

                                                                if (QueryConditionCount > 0) {
                                                                    qCol.append(" , ");
                                                                }

                                                                if (!tableDetails.get(z).getText().equals("null")) {

                                                                    String valueType = tableDetails.get(z).getType();

                                                                    if (isTextColumnType(valueType)) {

                                                                        qCol.append(" ")
                                                                                .append(tableDetails.get(z).getName())
                                                                                .append(" = '")
                                                                                .append(tableDetails.get(z).getText())
                                                                                .append("' ");

                                                                    } else if (isBooleanColumnType(valueType)) {

                                                                        if (!tableDetails.get(z).getText().equals("")) {
                                                                            qCol.append(" ")
                                                                                    .append(tableDetails.get(z).getName())
                                                                                    .append(" = ")
                                                                                    .append(tableDetails.get(z).getText())
                                                                                    .append(" ");
                                                                        } else {
                                                                            qCol.append(" ")
                                                                                    .append(tableDetails.get(z).getName())
                                                                                    .append(" = null ");
                                                                        }

                                                                    } else {

                                                                        qCol.append(" ")
                                                                                .append(tableDetails.get(z).getName())
                                                                                .append(" = ")
                                                                                .append(tableDetails.get(z).getText())
                                                                                .append(" ");
                                                                    }

                                                                } else {

                                                                    qCol.append(" ")
                                                                            .append(tableDetails.get(z).getName())
                                                                            .append(" = ")
                                                                            .append(tableDetails.get(z).getText())
                                                                            .append(" ");
                                                                }

                                                                QueryConditionCount++;
                                                            }
                                                        }

                                                        qCol.append(" Where ")
                                                                .append(replicatedetail.getClientPrimaryKey())
                                                                .append(" = ")
                                                                .append(code);
                                                    }

                                                    callMethod.Log("kowsar_qCol=" + repcode + " = " + qCol.toString());
                                                    database.execSQL(qCol.toString());

                                                    if ((currentStep == 1 || currentStep == 16) && broker_dbh.IsGoodSearchFTSReady()) {
                                                        changedGoodCodesForFTS.add(code);
                                                    }

                                                    LastRepCode = repcode;

                                                } catch (Exception e) {
                                                    callMethod.Log(e.getMessage());
                                                } finally {
                                                    if (d != null) {
                                                        d.close();
                                                    }
                                                }

                                                break;

                                            case "D":
                                            case "d":

                                                if (!replicatedetail.getServerTable().equals("")) {

                                                    String repObjectCode = singleobject.getString("RLObjectRef");

                                                    qCol = new StringBuilder("Delete from " + replicatedetail.getClientTable() + "  Where ")
                                                            .append(replicatedetail.getClientPrimaryKey())
                                                            .append(" = ")
                                                            .append(repObjectCode);

                                                    try {
                                                        database.execSQL(qCol.toString());

                                                        if (broker_dbh.IsGoodSearchFTSReady()) {
                                                            if (currentStep == 1) {
                                                                broker_dbh.DeleteOneGoodSearchFTS(repObjectCode);
                                                            } else if (currentStep == 16) {
                                                                // Only the barcode row was deleted; rebuild the Good FTS row without it.
                                                                changedGoodCodesForFTS.add(repObjectCode);
                                                            }
                                                        }

                                                        LastRepCode = repcode;

                                                    } catch (Exception e) {
                                                        callMethod.Log(e.getMessage());
                                                    }
                                                }

                                                break;
                                        }
                                    }

                                    database.execSQL(
                                            "Update ReplicationTable Set LastRepLogCode = " +
                                                    LastRepCode +
                                                    " Where ServerTable = '" +
                                                    replicatedetail.getServerTable() +
                                                    "' "
                                    );

                                    break;
                            }

                            if (arrayobject.length() >= LEGACY_REPLICATION_ROW_COUNT) {

                                RetrofitReplicate(replicatelevel);

                            } else {

                                if (Integer.parseInt(LastRepCode) < 0) {

                                    database.execSQL(
                                            "Update ReplicationTable Set LastRepLogCode = " +
                                                    broker_dbh.ReadConfig("MaxRepLogCode") +
                                                    " Where ServerTable = '" +
                                                    replicatedetail.getServerTable() +
                                                    "' "
                                    );

                                    RetrofitReplicate(replicatelevel);

                                } else {

                                    GoNextReplicationStepAfterFTS(currentStep, replicatelevel + 1);
                                }
                            }

                        } catch (Exception e) {
                            callMethod.Log("RetrofitReplicate onResponse Error = " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Base_NetworkFailure.show(
                            mContext,
                            callMethod,
                            "Broker replication step",
                            call,
                            t
                    );

                    RetrofitReplicate(replicatelevel);
                }
            });

        } else {
            replicateGoodImageChange();
        }
    }
    public void RetrofitReplicate1(Integer replevel) {
        // نسخه قدیمی برای جلوگیری از دو مسیر متفاوت نگه داشته شده است.
        // مسیر اصلی بروزرسانی از RetrofitReplicate استفاده می‌کند تا FTS فقط بعد از آماده بودن، Batch شود.
        RetrofitReplicate(replevel);
    }


    @Deprecated
    private void RetrofitReplicateAutoLegacy(Integer replevel) {
        broker_dbh.closedb();
        replicationModels = broker_dbh.GetReplicationTable();
        if (replevel < replicationModels.size()) {
            ReplicationModel replicatedetail = replicationModels.get(replevel);
            tableDetails = broker_dbh.GetTableDetail(replicatedetail.getClientTable());

            FinalStep = 0;
            LastRepCode = String.valueOf(replicatedetail.getLastRepLogCode());








            String tableName;
            String RowExec;

            int currentStep = replicatedetail.getReplicationCode();
            int totalSteps = 16;


            switch (currentStep) {
                case 1:
                    tableName = "کالا";
                    RowExec = "100";
                    break;
                case 2:
                    tableName = "موجودی انبار";
                    RowExec = "400";
                    break;
                case 3:
                    tableName = "سرگروه";
                    RowExec = "400";
                    break;
                case 4:
                    tableName = "گروه کالا";
                    RowExec = "600";
                    break;
                case 5:
                    tableName = "اجزای پایه";
                    RowExec = "300";
                    break;
                case 6:
                    tableName = "شهر";
                    RowExec = "400";
                    break;
                case 7:
                    tableName = "ادرس";
                    RowExec = "300";
                    break;
                case 8:
                    tableName = "مشتری";
                    RowExec = "300";
                    break;
                case 9:
                    tableName = "خصوصیات اضافه";
                    RowExec = "200";
                    break;
                case 10:
                    tableName = "گروهیندی ها";
                    RowExec = "600";
                    break;
                case 11:
                    tableName = "سمت";
                    RowExec = "600";
                    break;
                case 12:
                    tableName = "سمت شخص";
                    RowExec = "600";
                    break;
                case 13:
                    tableName = "سمت شخص کالا";
                    RowExec = "300";
                    break;
                case 14:
                    tableName = "مشتریان بازاریاب";
                    RowExec = "500";
                    break;
                case 15:
                    tableName = "واحد سنجش";
                    RowExec = "200";
                    break;
                case 16:
                    tableName = "بارکدهای کالا";
                    RowExec = "500";
                    break;
                default:
                    tableName = "نامشخص";
                    RowExec = "100";
                    break;
            }



            Call<RetrofitResponse> call1 = broker_apiInterface.RetrofitReplicate("repinfo",
                    LastRepCode,
                    replicatedetail.getServerTable(),
                    "",
                    "1",
                    RowExec
            );

            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    String responseText = responseText(response);
                    if (responseText != null) {
                        new Thread(() -> {
                            try {
                                JSONArray arrayobject = new JSONArray(responseText);
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
                                                                String valueType = tableDetails.get(z).getType();
                                                                if (!tableDetails.get(z).getText().equals("null")) {
                                                                    if (isTextColumnType(valueType)) {
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
                                                                    String valueType = tableDetails.get(z).getType();
                                                                    if (isTextColumnType(valueType)) {
                                                                        qCol.append(" ").append(tableDetails.get(z).getName()).append(" = '").append(tableDetails.get(z).getText()).append("' ");
                                                                    } else if (isBooleanColumnType(valueType) && tableDetails.get(z).getText().equals("")) {
                                                                        qCol.append(" ").append(tableDetails.get(z).getName()).append(" = null ");
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
                                                        if ((currentStep == 1 || currentStep == 16) && broker_dbh.IsGoodSearchFTSReady()) {
                                                            changedGoodCodesForFTS.add(code);
                                                        }

                                                        LastRepCode = repcode;

                                                    } catch (Exception exception) {
                                                        logLegacyFailure("automatic row upsert", exception);
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
                                                            if (broker_dbh.IsGoodSearchFTSReady()) {
                                                                if (currentStep == 1) {
                                                                    broker_dbh.DeleteOneGoodSearchFTSAsync(
                                                                            repObjectCode,
                                                                            new Broker_DBH.OneGoodFTSCallback() {

                                                                                @Override
                                                                                public void onStart(String message) {
                                                                                    if (tv_step != null) {
                                                                                        tv_step.setVisibility(View.VISIBLE);
                                                                                        tv_step.setText(message);
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onDone(String message) {
                                                                                    callMethod.Log(message);
                                                                                }

                                                                                @Override
                                                                                public void onError(Exception e) {
                                                                                    callMethod.Log(e.getMessage());
                                                                                }
                                                                            }
                                                                    );
                                                                } else if (currentStep == 16) {
                                                                    // Only the barcode row was deleted; rebuild the Good FTS row without it.
                                                                    changedGoodCodesForFTS.add(repObjectCode);
                                                                }
                                                            }
                                                            LastRepCode = repcode;
                                                        } catch (Exception exception) {
                                                            logLegacyFailure("automatic row delete", exception);
                                                        }
                                                    }

                                                    break;
                                            }
                                        }
                                        database.execSQL("Update ReplicationTable Set LastRepLogCode = " + LastRepCode + " Where ServerTable = '" + replicatedetail.getServerTable() + "' ");
                                        break;
                                }
                                if (arrayobject.length() >= LEGACY_REPLICATION_ROW_COUNT) {
                                    RetrofitReplicateAuto(replevel);
                                } else {
                                    if (Integer.parseInt(LastRepCode) < 0) {

                                        database.execSQL("Update ReplicationTable Set LastRepLogCode = " + broker_dbh.ReadConfig("MaxRepLogCode") + " Where ServerTable = '" + replicatedetail.getServerTable() + "' ");

                                        RetrofitReplicateAuto(replevel);
                                    } else {
                                        GoNextReplicationAutoStepAfterFTS(currentStep, replevel + 1);
                                    }
                                }
                            } catch (JSONException exception) {
                                callMethod.Log("Broker automatic replication JSON failed: "
                                        + exception.getClass().getSimpleName());
                            }

                        }).start();
                    } else {
                        callMethod.Log("Broker automatic replication response is empty or invalid");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Base_NetworkFailure.logOnly(
                            callMethod,
                            "Broker automatic replication step",
                            call,
                            t
                    );
                }
            });

        }


    }
    public void replicateGoodImageChange() {
        int runToken = currentRunToken;
        if (!replicationSession.isActive(runToken)) {
            return;
        }
        mainHandler.post(() -> {
            if (replicationSession.isActive(runToken) && tv_rep != null) {
                tv_rep.setText(NumberFunctions.PerisanNumber(
                        "در حال بروزرسانی عکس"));
            }
        });

        String checkpoint = broker_dbh.ReadConfig("KsrImage_LastRepCode");
        try {
            Long.parseLong(checkpoint);
        } catch (NumberFormatException exception) {
            failReplication(runToken, "Broker image checkpoint", exception);
            return;
        }

        Call<RetrofitResponse> call = broker_apiInterface.RetrofitReplicate(
                "repinfo", checkpoint, "KsrImage", "", "1", "400");
        if (!trackReplicationCall(runToken, call)) {
            return;
        }
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(
                    @NotNull Call<RetrofitResponse> completedCall,
                    @NotNull Response<RetrofitResponse> response
            ) {
                if (!acceptReplicationCallback(runToken, completedCall)) {
                    return;
                }
                String responseText = responseText(response);
                if (responseText == null) {
                    failReplication(runToken, "Broker image replication", null);
                    return;
                }
                replicationExecutor.execute(() -> applyImageReplicationResponse(
                        runToken, checkpoint, responseText));
            }

            @Override
            public void onFailure(
                    @NotNull Call<RetrofitResponse> failedCall,
                    @NotNull Throwable throwable
            ) {
                if (!acceptReplicationCallback(runToken, failedCall) || failedCall.isCanceled()) {
                    return;
                }
                Base_NetworkFailure.show(
                        mContext,
                        callMethod,
                        "Broker image replication",
                        failedCall,
                        throwable
                );
                failReplication(runToken, "Broker image replication", throwable);
            }
        });
    }

    private void applyImageReplicationResponse(
            int runToken,
            String checkpoint,
            String responseText
    ) {
        try {
            JSONArray rows = new JSONArray(responseText);
            BrokerImageReplicationBatchApplier.Result result =
                    BrokerImageReplicationBatchApplier.apply(database, rows, checkpoint);
            LastRepCode = result.getLastReplicationCode();
            for (String imageCode : result.getFilesToDelete()) {
                image_info.DeleteImage(imageCode);
            }
            if (!replicationSession.isActive(runToken)) {
                return;
            }
            if (!result.isNoChanges() && result.getReceivedRows() >= 400) {
                replicateGoodImageChange();
            } else {
                mainHandler.post(() -> finishReplicationAfterImages(runToken));
            }
        } catch (Exception exception) {
            failReplication(runToken, "Broker image replication batch", exception);
        }
    }

    private void finishReplicationAfterImages(int runToken) {
        if (!replicationSession.isActive(runToken)) {
            return;
        }
        if (!broker_dbh.IsGoodSearchFTSHealthy()) {
            broker_dbh.ForceRebuildGoodSearchFTS();
            if (tv_step != null) {
                tv_step.setVisibility(View.VISIBLE);
                tv_step.setText(NumberFunctions.PerisanNumber("شروع ساخت جستجو"));
            }
            if (tv_rep != null) {
                tv_rep.setVisibility(View.VISIBLE);
                tv_rep.setText(NumberFunctions.PerisanNumber(
                        "در حال آماده سازی جستجوی کالا"));
            }
            broker_dbh.SyncGoodSearchFTSAsync(new Broker_DBH.ProgressCallback() {
                @Override
                public void onProgress(int percent, int done, int total) {
                    if (!replicationSession.isActive(runToken) || tv_step == null) {
                        return;
                    }
                    tv_step.setText(NumberFunctions.PerisanNumber(
                            percent + "% - " + done + " از " + total));
                }

                @Override
                public void onDone() {
                    if (!replicationSession.isActive(runToken)) {
                        return;
                    }
                    if (broker_dbh.IsGoodSearchFTSHealthy()) {
                        finishReplicationUpdate(runToken);
                    } else {
                        broker_dbh.SaveFTSReady("0");
                        failReplication(runToken, "Broker FTS health", null);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    broker_dbh.SaveFTSReady("0");
                    failReplication(runToken, "Broker FTS rebuild", exception);
                }
            });
        } else {
            finishReplicationUpdate(runToken);
        }
    }

    private void finishReplicationUpdate(int runToken) {
        if (!replicationSession.complete(runToken)) {
            return;
        }
        PersianCalendar calendar = new PersianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
        broker_dbh.SaveConfig("LastUpdate", calendar.getPersianShortDateTime());
        closeDialogSafely();
        Intent navigation = new Intent(mContext, Broker_NavActivity.class);
        navigation.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(navigation);
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
        callMethod.showToast("بروز رسانی انجام شد");
    }

    @Deprecated
    private void replicateGoodImageChangeLegacy() {

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
                String responseText = responseText(response);
                if (responseText != null) {
                    try {
                        JSONArray arrayobject = new JSONArray(responseText);
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
                                                } catch (Exception exception) {
                                                    logLegacyFailure("image row replace-delete", exception);
                                                }
                                                image_info.DeleteImage(code);
                                            }

                                            qCol = "INSERT INTO KsrImage(KsrImageCode, ObjectRef,IsDefaultImage) Select " + code + "," + ObjectRef + ",'false'";

                                            try {
                                                database.execSQL(qCol);
                                                callMethod.Log("qCol=" + qCol);
                                            } catch (Exception exception) {
                                                logLegacyFailure("image row insert", exception);
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
                                            } catch (Exception exception) {
                                                logLegacyFailure("image row delete", exception);
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

                            if (arrayobject.length() >= 400) {

                                replicateGoodImageChange();

                            } else {


                                if (!broker_dbh.IsGoodSearchFTSHealthy()) {

                                    broker_dbh.ForceRebuildGoodSearchFTS();

                                    tv_step.setVisibility(View.VISIBLE);
                                    tv_rep.setVisibility(View.VISIBLE);

                                    tv_rep.setText(NumberFunctions.PerisanNumber("در حال آماده‌سازی جستجوی کالا..."));
                                    tv_step.setText(NumberFunctions.PerisanNumber("شروع ساخت جستجو"));

                                    broker_dbh.SyncGoodSearchFTSAsync(
                                            new Broker_DBH.ProgressCallback() {

                                                @SuppressLint("SetTextI18n")
                                                @Override
                                                public void onProgress(int percent, int done, int total) {

                                                    tv_step.setText(
                                                            NumberFunctions.PerisanNumber(String.valueOf(percent)) +
                                                                    "%  -  " +
                                                                    NumberFunctions.PerisanNumber(String.valueOf(done)) +
                                                                    " از " +
                                                                    NumberFunctions.PerisanNumber(String.valueOf(total))
                                                    );

                                                    callMethod.Log(
                                                            "FTS Progress => " +
                                                                    percent + "% , done=" +
                                                                    done + " , total=" + total
                                                    );
                                                }

                                                @Override
                                                public void onDone() {

                                                    if (broker_dbh.IsGoodSearchFTSHealthy()) {

                                                        callMethod.Log("FTS Build Done And Healthy");
                                                        FinishUpdate();

                                                    } else {

                                                        int goodCount = broker_dbh.GetGoodTableCount();
                                                        int ftsCount = broker_dbh.GetGoodSearchFTSCount();
                                                        int stateCount = broker_dbh.GetGoodSearchFTSStateTableCount();

                                                        callMethod.Log(
                                                                "FTS Build Done But Not Healthy => Good=" +
                                                                        goodCount +
                                                                        " FTS=" +
                                                                        ftsCount +
                                                                        " State=" +
                                                                        stateCount
                                                        );

                                                        broker_dbh.SaveFTSReady("0");

                                                        tv_rep.setText(NumberFunctions.PerisanNumber("جستجوی کالا کامل ساخته نشد"));
                                                        tv_step.setText(
                                                                NumberFunctions.PerisanNumber(
                                                                        "Good=" + goodCount +
                                                                                " FTS=" + ftsCount +
                                                                                " State=" + stateCount
                                                                )
                                                        );

                                                        callMethod.showToast("ساخت جستجوی کالا کامل نشد. دوباره بروزرسانی بزنید.");
                                                    }
                                                }

                                                @Override
                                                public void onError(Exception e) {

                                                    String errorMessage = "unknown";

                                                    if (e != null && e.getMessage() != null) {
                                                        errorMessage = e.getMessage();
                                                    }

                                                    callMethod.Log("FTS Build Error = " + errorMessage);

                                                    broker_dbh.SaveFTSReady("0");

                                                    tv_rep.setText(NumberFunctions.PerisanNumber("خطا در ساخت جستجوی کالا"));
                                                    tv_step.setVisibility(View.VISIBLE);
                                                    tv_step.setText(errorMessage);

                                                    callMethod.showToast("خطا در ساخت جستجوی کالا؛ لاگ را بررسی کنید");

                                                    // مهم:
                                                    // اینجا FinishUpdate نزن.
                                                    // چون اگر خطا بخورد، نباید برنامه فکر کند بروزرسانی کامل شده.
                                                }
                                            }
                                    );

                                } else {

                                    callMethod.Log("FTS Is Healthy. FinishUpdate");
                                    FinishUpdate();
                                }
                            }
                            /*
                            tv_step.setVisibility(View.GONE);
                            dialog.dismiss();
                            intent = new Intent(mContext, Broker_NavActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                            mContext.startActivity(intent);
                            ((Activity) mContext).finish();
                            callMethod.showToast("بروز رسانی انجام شد");

                            PersianCalendar calendar1 = new PersianCalendar();
                            calendar1.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
                            calendar1.add(Calendar.DAY_OF_MONTH, -1);
                            database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'LastUpdate', '0' Where Not Exists(Select * From Config Where KeyValue = 'LastUpdate')");
                            database.execSQL("Update Config Set DataValue = '" + calendar1.getPersianShortDateTime() + "' Where KeyValue = 'LastUpdate'");
                            */
                        }
                    } catch (JSONException exception) {
                        stopReplication("Broker image replication JSON");
                    }
                } else {
                    stopReplication("Broker image replication");
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                closeDialogSafely();
                Base_NetworkFailure.show(
                        mContext,
                        callMethod,
                        "Broker image replication",
                        call,
                        t
                );
            }
        });
    }

    public void FinishUpdate() {

        tv_step.setVisibility(View.GONE);

        dialog.dismiss();

        PersianCalendar calendar1 = new PersianCalendar();

        calendar1.setTimeZone(
                TimeZone.getTimeZone("Asia/Tehran")
        );


        database.execSQL(
                "INSERT INTO config(keyvalue, datavalue) " +
                        "Select 'LastUpdate', '0' " +
                        "Where Not Exists(Select * From Config Where KeyValue = 'LastUpdate')"
        );

        database.execSQL(
                "Update Config Set DataValue = '" +
                        calendar1.getPersianShortDateTime() +
                        "' Where KeyValue = 'LastUpdate'"
        );

        intent = new Intent(mContext, Broker_NavActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mContext.startActivity(intent);

        ((Activity) mContext).finish();

        callMethod.showToast("بروز رسانی انجام شد");
    }

    public void BrokerStack() {

        UserInfo userInfo = broker_dbh.LoadPersonalInfo();
        broker_dbh.DatabaseCreate();
        Call<RetrofitResponse> call1 = broker_apiInterface.BrokerStack( "BrokerStack",userInfo.getBrokerCode());
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                String responseText = responseText(response);
                if (responseText != null) {
                    if (!responseText.equals(broker_dbh.ReadConfig("BrokerStack"))) {
                        broker_dbh.SaveConfig("BrokerStack", responseText);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Base_NetworkFailure.show(
                        mContext,
                        callMethod,
                        "Broker stack",
                        call,
                        t
                );
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
                String responseText = responseText(response);
                if (responseText != null) {
                    if (!responseText.equals("")) {
                        if (!responseText.equals(broker_dbh.ReadConfig("GroupCodeDefult"))) {
                            broker_dbh.SaveConfig("GroupCodeDefult", responseText);
                        }
                    } else {
                        broker_dbh.SaveConfig("GroupCodeDefult", "0");
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                Base_NetworkFailure.logOnly(
                        callMethod,
                        "Broker default group",
                        call,
                        t
                );
            }
        });
    }

    public void MenuBroker() {
        //Call<RetrofitResponse> call1 = broker_apiInterface.GetMenuBroker("GetMenuBroker");
        Call<RetrofitResponse> call1 = broker_apiInterface.MenuBroker("GetMenuBroker");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                String responseText = responseText(response);
                if (responseText != null) {
                    if (!responseText.equals(broker_dbh.ReadConfig("MenuBroker"))) {
                        broker_dbh.SaveConfig("MenuBroker", responseText);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Base_NetworkFailure.logOnly(
                        callMethod,
                        "Broker menu",
                        call,
                        t
                );
            }
        });
        GroupCodeDefult();
    }

    public void GoodTypeReplication() {
        GoodTypeReplication(currentRunToken);
    }

    private void GoodTypeReplication(int runToken) {

        Call<RetrofitResponse> call1 = broker_apiInterface.GetGoodType("GetGoodType");
        if (!trackReplicationCall(runToken, call1)) {
            return;
        }
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (!acceptReplicationCallback(runToken, call)) {
                    return;
                }
                ArrayList<Column> columns = responseColumns(response);
                if (columns != null) {
                    for (Column column : columns) {
                        broker_dbh.ReplicateGoodtype(column);
                    }
                    columnReplication(0, runToken);
                } else {
                    failReplication(runToken, "Broker good-type replication", null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                if (!acceptReplicationCallback(runToken, call) || call.isCanceled()) {
                    return;
                }
                Base_NetworkFailure.show(
                        mContext,
                        callMethod,
                        "Broker good-type replication",
                        call,
                        t
                );
                failReplication(runToken, "Broker good-type replication", t);
            }
        });


    }

    public void columnReplication(Integer i) {
        columnReplication(i, currentRunToken);
    }

    private void columnReplication(Integer i, int runToken) {

        if (!replicationSession.isActive(runToken)) {
            return;
        }
        callMethod.Log(""+i);
        if (i < 4) {
            Call<RetrofitResponse> call2 = broker_apiInterface.GetColumnList( "GetColumnList","" + i, "1", "1");
            if (!trackReplicationCall(runToken, call2)) {
                return;
            }
            callMethod.Log(call2.request().toString());
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (!acceptReplicationCallback(runToken, call)) {
                        return;
                    }
                    ArrayList<Column> columns = responseColumns(response);
                    if (columns != null) {
                        int j = 0;
                        for (Column column : columns) {
                            broker_dbh.ReplicateColumn(column, i);
                            j++;
                        }
                        if (j == columns.size()) {
                            columnReplication(i + 1, runToken);
                        }
                    } else {
                        failReplication(runToken, "Broker column replication", null);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (!acceptReplicationCallback(runToken, call) || call.isCanceled()) {
                        return;
                    }
                    Base_NetworkFailure.show(
                            mContext,
                            callMethod,
                            "Broker column replication",
                            call,
                            t
                    );
                    failReplication(runToken, "Broker column replication", t);
                }
            });
        } else {
            closeDialogSafely();
            DoingReplicate();

        }
    }


    @SuppressLint("Range")
    public void SendGpsLocationnew() {

        ArrayList<KowsarLocationNew> pageLocations = new ArrayList<>();
        String checkpoint = broker_dbh.ReadConfig("LastGpsLocationCodeNew");
        try {
            Long.parseLong(checkpoint);
        } catch (NumberFormatException exception) {
            callMethod.Log("Broker new GPS checkpoint is invalid");
            return;
        }

        Cursor gpsCursor = sqLiteDatabase.rawQuery(
                "select * from GpsLocationNew where GpsLocationCode > ? "
                        + "order by GpsLocationCode limit 20",
                new String[]{checkpoint});

        if (gpsCursor != null) {
            while (gpsCursor.moveToNext()) {
                KowsarLocationNew pageLocation = new KowsarLocationNew();
                pageLocation.setGpsLocationCode(String.valueOf(
                        gpsCursor.getInt(gpsCursor.getColumnIndex("GpsLocationCode"))));
                pageLocations.add(pageLocation);
            }
        }

        String GpsLocationString = CursorToJson(gpsCursor);
        gpsCursor.close();

        callMethod.Log(GpsLocationString);

        int pageSize = pageLocations.size();
        if (pageSize > 0) {
            String lastPageCode = pageLocations.get(pageSize - 1).getGpsLocationCode();
            Call<RetrofitResponse> call1 = broker_apiInterface.UpdateLocation( "UpdateLocationNew",GpsLocationString);
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (hasLocationsResponse(response)) {
                        broker_dbh.SaveConfig("LastGpsLocationCodeNew", lastPageCode);
                        if (pageSize >= 20) {
                            SendGpsLocationnew();
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Base_NetworkFailure.logOnly(
                            callMethod,
                            "Broker new GPS upload",
                            call,
                            t
                    );
                }
            });
        } else {
            callMethod.Log("kowsar_Gps zero size");
        }
    }



    @SuppressLint("Range")
    public void SendGpsLocation() {

        ArrayList<KowsarLocation> pageLocations = new ArrayList<>();
        String checkpoint = broker_dbh.ReadConfig("LastGpsLocationCode");
        try {
            Long.parseLong(checkpoint);
        } catch (NumberFormatException exception) {
            callMethod.Log("Broker GPS checkpoint is invalid");
            return;
        }

        Cursor gpsCursor = sqLiteDatabase.rawQuery(
                "select * from GpsLocation where GpsLocationCode > ? "
                        + "order by GpsLocationCode limit 20",
                new String[]{checkpoint});

        if (gpsCursor != null) {
            while (gpsCursor.moveToNext()) {
                KowsarLocation pageLocation = new KowsarLocation();
                pageLocation.setGpsLocationCode(String.valueOf(
                        gpsCursor.getInt(gpsCursor.getColumnIndex("GpsLocationCode"))));
                pageLocations.add(pageLocation);
            }
        }

        String GpsLocationString = CursorToJson(gpsCursor);
        gpsCursor.close();

        int pageSize = pageLocations.size();
        if (pageSize > 0) {
            String lastPageCode = pageLocations.get(pageSize - 1).getGpsLocationCode();
            Call<RetrofitResponse> call1 = broker_apiInterface.UpdateLocation( "UpdateLocation",GpsLocationString);
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (hasLocationsResponse(response)) {
                        broker_dbh.SaveConfig("LastGpsLocationCode", lastPageCode);
                        if (pageSize >= 20) {
                            SendGpsLocation();
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Base_NetworkFailure.logOnly(
                            callMethod,
                            "Broker GPS upload",
                            call,
                            t
                    );
                }
            });
        } else {
            callMethod.Log("kowsar_Gps zero size");
        }
    }

    private RetrofitResponse successfulBody(Response<RetrofitResponse> response) {
        boolean successful = response != null && response.isSuccessful();
        RetrofitResponse body = successful ? response.body() : null;
        return BrokerReplicationResponsePolicy.successfulBody(successful, body);
    }

    private String responseText(Response<RetrofitResponse> response) {
        return BrokerReplicationResponsePolicy.text(successfulBody(response));
    }

    private ArrayList<Column> responseColumns(Response<RetrofitResponse> response) {
        return BrokerReplicationResponsePolicy.columns(successfulBody(response));
    }

    private boolean hasLocationsResponse(Response<RetrofitResponse> response) {
        return BrokerReplicationResponsePolicy.hasLocations(successfulBody(response));
    }

    private void logLegacyFailure(String operation, Exception exception) {
        callMethod.Log("Broker legacy replication " + operation + " failed: "
                + exception.getClass().getSimpleName());
    }

    private void stopReplication(String operation) {
        callMethod.Log(operation + " response is empty or invalid");
        closeDialogSafely();
        callMethod.showToast("بروزرسانی کامل نشد؛ دوباره تلاش کنید");
    }

    private void closeDialogSafely() {
        try {
            if (dialog != null && dialog.isShowing()) dialog.dismiss();
        } catch (RuntimeException exception) {
            callMethod.Log("Broker replication dialog close failed: "
                    + exception.getClass().getSimpleName());
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
                        callMethod.Log("Cursor serialization failed: "
                                + e.getClass().getSimpleName());
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
