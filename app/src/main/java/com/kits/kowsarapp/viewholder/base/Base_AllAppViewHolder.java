package com.kits.kowsarapp.viewholder.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.base.Base_SplashActivity;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.Base_NetworkFailure;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.LatestRequestGate;
import com.kits.kowsarapp.application.base.ProfileDatabaseContract;
import com.kits.kowsarapp.application.base.ProfileDatabaseHealth;
import com.kits.kowsarapp.application.base.ProfileDatabaseInstaller;
import com.kits.kowsarapp.application.base.SafeListAccess;
import com.kits.kowsarapp.application.base.SqliteFileHeaderValidator;
import com.kits.kowsarapp.model.base.Activation;
import com.kits.kowsarapp.model.base.Base_DBH;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.find.Find_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.order.Order_DBH;
import com.kits.kowsarapp.webService.base.APIClient_kowsar;
import com.kits.kowsarapp.webService.base.Kowsar_APIInterface;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Base_AllAppViewHolder extends RecyclerView.ViewHolder {

    private static final ExecutorService PROFILE_DATABASE_EXECUTOR =
            Executors.newSingleThreadExecutor();
    private static final LatestRequestGate PROFILE_DATABASE_REQUEST_GATE =
            new LatestRequestGate();

    private final ImageView img;
    public MaterialCardView rltv;
    public int downloadId;


    public TextView tv_persianname;
    public TextView tv_apptype;
    public TextView tv_englishname;
    public TextView tv_serverurl;
    public Button btn_login;
    public Button btn_recall;
    public Button btn_delete;
    Dialog dialog;
    TextView tv_rep;
    TextView tv_step;
    Button btn_prog;
    Activation activationres;
    Base_DBH base_dbh;

    CallMethod callMethod;

    public Call<RetrofitResponse> call;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final LatestRequestGate holderLifecycleGate = new LatestRequestGate();
    private final Context databaseContext;
    private Call<RetrofitResponse> recallCall;
    private int holderLifecycleToken = holderLifecycleGate.begin();


    public Base_AllAppViewHolder(View itemView, Context context) {
        super(itemView);

        databaseContext = context.getApplicationContext();
        base_dbh = new Base_DBH(databaseContext, "/data/data/com.kits.kowsarapp/databases/KowsarDb.sqlite");
        callMethod=new CallMethod(context);
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.broker_spinner_box);

        tv_rep = dialog.findViewById(R.id.b_spinner_text);
        tv_step = dialog.findViewById(R.id.b_spinner_step);
        btn_prog = dialog.findViewById(R.id.b_spinner_btn);




        tv_persianname = itemView.findViewById(R.id.base_allapp_c_persianname);
        tv_apptype = itemView.findViewById(R.id.base_allapp_c_apptype);
        tv_englishname = itemView.findViewById(R.id.base_allapp_c_englishname);
        tv_serverurl = itemView.findViewById(R.id.base_allapp_c_serverurl);
        img = itemView.findViewById(R.id.base_allapp_c_image);
        btn_login = itemView.findViewById(R.id.base_allapp_c_login);
        btn_recall = itemView.findViewById(R.id.base_allapp_c_recall);
        btn_delete = itemView.findViewById(R.id.base_allapp_c_ِdelet);



    }


    public void bind( Activation activation, Context mContext, CallMethod callMethod) {
        holderLifecycleToken = holderLifecycleGate.begin();
        if (activation == null) {
            tv_persianname.setText("");
            tv_apptype.setText("نامشخص");
            tv_englishname.setText("");
            tv_serverurl.setText("");
            img.setImageDrawable(null);
            return;
        }
        tv_persianname.setText(activation.getPersianCompanyName());

        String appType = activation.getAppType() == null ? "" : activation.getAppType();
        img.setImageDrawable(null);
        switch (appType) {
            case "1":  // broker
                tv_apptype.setText("بازاریابی");
                img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_broker_logo));
                break;
            case "2":  // ocr
                tv_apptype.setText("پردازش و توزیع");
                img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_logo_ocr));

                break;
            case "3":  // order
                tv_apptype.setText("سفارشگیری");
                img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_order_logo));

                break;
            case "4":  // find
                tv_apptype.setText("کالایاب");
                img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_find_logo));

                break;
            default:
                tv_apptype.setText("نامشخص");
                break;
        }



        tv_englishname.setText(activation.getEnglishCompanyName());
        String fullUrl = activation.getServerURL(); // مقدار کامل URL
        String ipAddress = "";

        if (fullUrl != null && fullUrl.startsWith("http")) {
            // جدا کردن پروتکل و مسیر اضافی
            String temp = fullUrl.replace("http://", "").replace("https://", "");
            // جدا کردن فقط IP
            int colonIndex = temp.indexOf(':');
            if (colonIndex != -1) {
                ipAddress = temp.substring(0, colonIndex); // بخش IP قبل از ':'
            } else {
                int slashIndex = temp.indexOf('/');
                ipAddress = (slashIndex != -1) ? temp.substring(0, slashIndex) : temp;
            }
        }



        tv_serverurl.setText(ipAddress);

    }






    public void Actionbtn(final Activation activations,Context mcontext, CallMethod callMethod) {
        if (activations == null) {
            btn_login.setEnabled(false);
            btn_recall.setEnabled(false);
            btn_delete.setEnabled(false);
            return;
        }

        Kowsar_APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(Kowsar_APIInterface.class);

        Activation activationsss=activations;

        btn_login.setOnClickListener(view -> {
            if (!hasSafeProfileName(activationsss)) {
                callMethod.showToast("نام پروفایل معتبر نیست");
                return;
            }
            if (!new File(activationsss.getDatabaseFilePath()).exists()) {

                DownloadRequest(activationsss,mcontext);
            } else {
                validateAndActivateExistingProfile(activationsss, mcontext);

            }
        });


        btn_recall.setOnClickListener(view -> {
            if (recallCall != null) recallCall.cancel();
            final int lifecycleToken = holderLifecycleToken;
            btn_recall.setEnabled(false);
            recallCall = apiInterface.Activation(activationsss.getActivationCode(),"0");
            recallCall.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (recallCall != call) return;
                    recallCall = null;
                    if (!canUpdateUi(mcontext, lifecycleToken)) return;
                    btn_recall.setEnabled(true);
                    RetrofitResponse body = response.body();
                    Activation recalledActivation = body == null
                            ? null
                            : SafeListAccess.firstOrNull(body.getActivations());
                    if (!response.isSuccessful() || recalledActivation == null) {
                        callMethod.Log("Activation recall response is empty or invalid");
                        callMethod.showToast("پاسخ بازیابی فعال‌سازی معتبر نیست؛ دوباره تلاش کنید");
                        return;
                    }
                    activationres = recalledActivation;
                    try {
                        base_dbh.InsertActivation(activationres);
                    } catch (SQLiteException | IllegalArgumentException
                             | IllegalStateException exception) {
                        callMethod.Log("Activation recall persistence failed: "
                                + exception.getClass().getSimpleName());
                        callMethod.showToast("ذخیره‌سازی فعال‌سازی انجام نشد؛ دوباره تلاش کنید");
                        return;
                    }
                    restartHostActivity(mcontext);
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (recallCall != call) return;
                    recallCall = null;
                    if (!canUpdateUi(mcontext, lifecycleToken)) return;
                    btn_recall.setEnabled(true);
                    Base_NetworkFailure.show(
                            mcontext,
                            callMethod,
                            "Activation recall",
                            call,
                            t
                    );
                }
            });
        });


        btn_delete.setOnClickListener(view -> {
            if (!hasSafeProfileName(activationsss)) {
                callMethod.showToast("نام پروفایل معتبر نیست");
                return;
            }
            if (!new File(activationsss.getDatabaseFilePath()).exists()) {
                if (deleteActivationRecord(activationsss)) {
                    startSplashAndFinishHost(mcontext);
                }
            } else {


                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext, R.style.AlertDialogCustom);
                builder.setTitle(R.string.textvalue_allert);
                builder.setMessage(" آیا از حذف تمامی اطلاعات"+activationsss.getPersianCompanyName()+" مطمئن هستید؟ ");

                builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {




                    final Dialog dialog1 = new Dialog(mcontext);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    Window dialogWindow = dialog1.getWindow();
                    if (dialogWindow != null) {
                        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
                    }
                    dialog1.setContentView(R.layout.default_loginconfig);
                    EditText ed_password = dialog1.findViewById(R.id.d_loginconfig_ed);
                    MaterialButton btn_login = dialog1.findViewById(R.id.d_loginconfig_btn);



                    ed_password.addTextChangedListener(
                            new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                }

                                @Override
                                public void afterTextChanged(final Editable editable) {

                                    if(NumberFunctions.EnglishNumber(ed_password.getText().toString()).length()>5) {
                                        if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(activationsss.getActivationCode())) {

                                            Deletedb(activationsss,mcontext);


                                        } else {
                                            callMethod.showToast("رمز عبور صیحیح نیست");
                                        }

                                    }
                                }
                            });

                    btn_login.setOnClickListener(v -> {

                        if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(activationsss.getActivationCode())) {
                            Deletedb(activationsss,mcontext);


                        }else {
                            callMethod.showToast("رمز عبور صیحیح نیست");
                        }


                    });
                    dialog1.show();

                });

                builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                    // code to handle negative button click
                });

                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });



    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory == null || !fileOrDirectory.exists()) {
            return;
        }
        try {
            if (fileOrDirectory.isDirectory()) {
                File[] children = fileOrDirectory.listFiles();
                if (children != null) {
                    for (File child : children) {
                        deleteRecursive(child);
                    }
                }
            }
            if (fileOrDirectory.exists() && !fileOrDirectory.delete()) {
                callMethod.Log("Profile path could not be deleted");
            }
        } catch (SecurityException exception) {
            callMethod.Log("Profile path deletion failed: "
                    + exception.getClass().getSimpleName());
        }
    }




    public void Deletedb(Activation activation,Context mcontext) {
        if (!hasSafeProfileName(activation)) {
            callMethod.Log("Profile deletion rejected an invalid company name");
            callMethod.showToast("نام پروفایل معتبر نیست");
            return;
        }
        if (!deleteActivationRecord(activation)) return;
        File databasedir = new File(mcontext.getApplicationInfo().dataDir + "/databases/" + activation.getEnglishCompanyName());
        deleteRecursive(databasedir);
        startSplashAndFinishHost(mcontext);
    }
    public void DownloadRequest(Activation activation,Context mcontext) {
        if (!hasSafeProfileName(activation)) {
            callMethod.Log("Profile download rejected an invalid company name");
            callMethod.showToast("نام پروفایل معتبر نیست");
            return;
        }
        final int installToken = PROFILE_DATABASE_REQUEST_GATE.begin();
        final int lifecycleToken = holderLifecycleToken;
        final String temporaryFileName = "KowsarDbTemp-" + installToken + ".sqlite";
        final File downloadTemp = new File(
                activation.getDatabaseFolderPath(),
                temporaryFileName
        );

        btn_prog.setOnClickListener(view -> {
            cancelDownload();
            DownloadRequest(activation,mcontext);
        });


        //String downloadurl="http://5.160.152.173:60005/api/kits/GetDb?Code="+activation.getActivationCode();
        String downloadurl="https://itmali.ir/webapi/kits/GetDb?Code="+activation.getActivationCode();

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(mcontext.getApplicationContext(), config);


        downloadId = PRDownloader.download(
                        downloadurl,
                        activation.getDatabaseFolderPath(),
                        temporaryFileName
                )

                .build()
                .setOnStartOrResumeListener(() -> {
                    if (!PROFILE_DATABASE_REQUEST_GATE.isCurrent(installToken)
                            || !canUpdateUi(mcontext, lifecycleToken)) {
                        return;
                    }
                    dialog.show();
                    dialog.setCancelable(false);
                })
                .setOnCancelListener(() -> {
                    if (PROFILE_DATABASE_REQUEST_GATE.isCurrent(installToken)) {
                        downloadTemp.delete();
                    }
                    dismissProgressDialog();
                })

                .setOnProgressListener(progress -> {
                    if (!PROFILE_DATABASE_REQUEST_GATE.isCurrent(installToken)
                            || !canUpdateUi(mcontext, lifecycleToken)) {
                        return;
                    }
                    tv_rep.setText("در حال بارگیری...");
                    tv_step.setVisibility(View.VISIBLE);
                    if (progress.totalBytes > 0) {
                        long percent = Math.min(100L,
                                (progress.currentBytes * 100L) / progress.totalBytes);
                        tv_step.setText(NumberFunctions.PerisanNumber(percent + "/100"));
                    } else {
                        tv_step.setText("...");
                    }
                })

                .start(new OnDownloadListener() {
                    @SuppressLint("SdCardPath")
                    @Override

                    public void onDownloadComplete() {
                        if (!PROFILE_DATABASE_REQUEST_GATE.isCurrent(installToken)
                                || !holderLifecycleGate.isCurrent(lifecycleToken)) {
                            downloadTemp.delete();
                            return;
                        }
                        File completeFile = new File(activation.getDatabaseFolderPath() + "/KowsarDb.sqlite");
                        installDownloadedDatabase(
                                activation,
                                mcontext,
                                downloadTemp,
                                completeFile,
                                installToken,
                                lifecycleToken
                        );
                    }


                    @Override
                    public void onError(Error error) {
                        if (!PROFILE_DATABASE_REQUEST_GATE.isCurrent(installToken)
                                || !canUpdateUi(mcontext, lifecycleToken)) {
                            downloadTemp.delete();
                            return;
                        }
                        btn_prog.setVisibility(View.VISIBLE);
                        downloadTemp.delete();
                        tv_step.setText("مشکل ارتباطی لطفا دوباره امتحان کنید");

                    }
                });

    }

    private void validateAndActivateExistingProfile(
            Activation activation,
            Context context
    ) {
        final int validationToken = PROFILE_DATABASE_REQUEST_GATE.begin();
        final int lifecycleToken = holderLifecycleToken;
        final File databaseFile = new File(activation.getDatabaseFilePath());
        btn_login.setEnabled(false);

        PROFILE_DATABASE_EXECUTOR.execute(() -> {
            boolean valid = PROFILE_DATABASE_REQUEST_GATE.isCurrent(validationToken)
                    && holderLifecycleGate.isCurrent(lifecycleToken)
                    && ProfileDatabaseContract.isKnownAppType(activation.getAppType())
                    && ProfileDatabaseHealth.isHealthyDownloadedDatabase(databaseFile)
                    && initializeAndVerifyProfileDatabase(
                            activation,
                            databaseContext,
                            databaseFile);

            mainHandler.post(() -> {
                if (!PROFILE_DATABASE_REQUEST_GATE.isCurrent(validationToken)
                        || !canUpdateUi(context, lifecycleToken)) {
                    return;
                }
                btn_login.setEnabled(true);
                if (!valid) {
                    callMethod.showToast("دیتابیس این مجموعه معتبر نیست؛ آن را مجدداً دریافت کنید");
                    return;
                }
                activateExistingProfile(activation, context, databaseFile);
            });
        });
    }

    private void activateExistingProfile(
            Activation activation,
            Context context,
            File databaseFile
    ) {
        callMethod.EditString("PersianCompanyNameUse", activation.getPersianCompanyName());
        callMethod.EditString("EnglishCompanyNameUse", activation.getEnglishCompanyName());
        callMethod.EditString("ServerURLUse", activation.getServerURL());
        if (activation.getSecendServerURL() == null
                || activation.getSecendServerURL().isEmpty()) {
            callMethod.EditString("SecendServerURL", activation.getServerURL());
        } else {
            callMethod.EditString("SecendServerURL", activation.getSecendServerURL());
        }
        callMethod.EditString("DatabaseName", databaseFile.getAbsolutePath());
        callMethod.EditString("ActivationCode", activation.getActivationCode());
        callMethod.EditString("AppType", activation.getAppType());
        callMethod.EditString("DbName", activation.getDbName());

        Intent intent = new Intent(context, Base_SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    private void installDownloadedDatabase(
            Activation activation,
            Context context,
            File downloadTemp,
            File completeFile,
            int installToken,
            int lifecycleToken
    ) {
        PROFILE_DATABASE_EXECUTOR.execute(() -> {
            if (!PROFILE_DATABASE_REQUEST_GATE.isCurrent(installToken)
                    || !holderLifecycleGate.isCurrent(lifecycleToken)) {
                downloadTemp.delete();
                return;
            }
            ProfileDatabaseInstaller.Result installResult = null;
            if (ProfileDatabaseHealth.isHealthyDownloadedDatabase(downloadTemp)) {
                installResult = ProfileDatabaseInstaller.install(
                        downloadTemp,
                        completeFile,
                        SqliteFileHeaderValidator::isValid,
                        installedFile -> initializeAndVerifyProfileDatabase(
                                activation,
                                databaseContext,
                                installedFile)
                );
            }

            ProfileDatabaseInstaller.Result finalInstallResult = installResult;
            mainHandler.post(() -> {
                if (!PROFILE_DATABASE_REQUEST_GATE.isCurrent(installToken)
                        || !canUpdateUi(context, lifecycleToken)) {
                    return;
                }
                if (finalInstallResult == null || !finalInstallResult.isInstalled()) {
                    if (downloadTemp.exists()) {
                        downloadTemp.delete();
                    }
                    btn_prog.setVisibility(View.VISIBLE);
                    tv_step.setVisibility(View.VISIBLE);
                    tv_step.setText("فایل دیتابیس معتبر نیست؛ دوباره تلاش کنید");
                    return;
                }

                activateInstalledProfile(activation, context, completeFile);
            });
        });
    }

    private boolean initializeAndVerifyProfileDatabase(
            Activation activation,
            Context context,
            File databaseFile
    ) {
        if (activation == null
                || databaseFile == null
                || !ProfileDatabaseContract.isKnownAppType(activation.getAppType())) {
            return false;
        }
        try {
            String databasePath = databaseFile.getAbsolutePath();
            switch (activation.getAppType()) {
                case ProfileDatabaseContract.BROKER_APP_TYPE:
                    try (Broker_DBH helper = new Broker_DBH(context, databasePath)) {
                        helper.DatabaseCreate();
                        helper.InitialConfigInsert();
                        return ProfileDatabaseHealth.hasRequiredTables(
                                databaseFile,
                                ProfileDatabaseContract.requiredTablesFor(activation.getAppType())
                        );
                    }
                case ProfileDatabaseContract.OCR_APP_TYPE:
                    try (Ocr_DBH helper = new Ocr_DBH(context, databasePath)) {
                        helper.DatabaseCreate();
                        return ProfileDatabaseHealth.hasRequiredTables(
                                databaseFile,
                                ProfileDatabaseContract.requiredTablesFor(activation.getAppType())
                        );
                    }
                case ProfileDatabaseContract.ORDER_APP_TYPE:
                    try (Order_DBH helper = new Order_DBH(context, databasePath)) {
                        helper.DatabaseCreate();
                        return ProfileDatabaseHealth.hasRequiredTables(
                                databaseFile,
                                ProfileDatabaseContract.requiredTablesFor(activation.getAppType())
                        );
                    }
                case ProfileDatabaseContract.FIND_APP_TYPE:
                    try (Find_DBH helper = new Find_DBH(context, databasePath)) {
                        helper.DatabaseCreate();
                        return ProfileDatabaseHealth.hasRequiredTables(
                                databaseFile,
                                ProfileDatabaseContract.requiredTablesFor(activation.getAppType())
                        );
                    }
                default:
                    return false;
            }
        } catch (SQLiteException | IllegalArgumentException
                 | IllegalStateException | SecurityException exception) {
            callMethod.Log("Profile database verification failed: "
                    + exception.getClass().getSimpleName());
            return false;
        }
    }

    private void activateInstalledProfile(
            Activation activation,
            Context context,
            File databaseFile
    ) {
        callMethod.EditString("DatabaseName", databaseFile.getAbsolutePath());
        callMethod.EditString("PersianCompanyNameUse", activation.getPersianCompanyName());
        callMethod.EditString("EnglishCompanyNameUse", activation.getEnglishCompanyName());
        callMethod.EditString("ServerURLUse", activation.getServerURL());
        callMethod.EditString("IpConfig", "");
        callMethod.EditString("AppType", activation.getAppType());
        callMethod.EditString("DbName", activation.getDbName());
        callMethod.EditString("ActivationCode", activation.getActivationCode());
        if (activation.getSecendServerURL() == null
                || activation.getSecendServerURL().isEmpty()) {
            callMethod.EditString("SecendServerURL", activation.getServerURL());
        } else {
            callMethod.EditString("SecendServerURL", activation.getSecendServerURL());
        }

        Intent intent = new Intent(App.getContext(), Base_SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void onAttachedToWindow() {
        holderLifecycleToken = holderLifecycleGate.begin();
        if (base_dbh == null) {
            base_dbh = new Base_DBH(
                    databaseContext,
                    "/data/data/com.kits.kowsarapp/databases/KowsarDb.sqlite");
        }
    }

    public void onDetachedFromWindow() {
        holderLifecycleGate.invalidate();
        cancelActiveWork();
    }

    public void release() {
        onDetachedFromWindow();
        if (base_dbh != null) {
            base_dbh.close();
            base_dbh = null;
        }
    }

    private void cancelActiveWork() {
        if (recallCall != null) {
            recallCall.cancel();
            recallCall = null;
        }
        cancelDownload();
        mainHandler.removeCallbacksAndMessages(null);
        dismissProgressDialog();
    }

    private void cancelDownload() {
        if (downloadId > 0) {
            PRDownloader.cancel(downloadId);
            downloadId = 0;
        }
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    private boolean canUpdateUi(Context context, int lifecycleToken) {
        if (!holderLifecycleGate.isCurrent(lifecycleToken)
                || !itemView.isAttachedToWindow()) {
            return false;
        }
        if (!(context instanceof Activity)) return true;
        Activity activity = (Activity) context;
        return !activity.isFinishing() && !activity.isDestroyed();
    }

    private void restartHostActivity(Context context) {
        if (!(context instanceof Activity)) {
            callMethod.Log("Activation recall host is not an Activity");
            return;
        }
        Activity activity = (Activity) context;
        if (activity.isFinishing() || activity.isDestroyed()) return;
        Intent restartIntent = activity.getIntent();
        activity.finish();
        activity.startActivity(restartIntent);
    }

    private void startSplashAndFinishHost(Context context) {
        if (!(context instanceof Activity)) {
            callMethod.Log("Profile selection host is not an Activity");
            return;
        }
        Activity activity = (Activity) context;
        if (activity.isFinishing() || activity.isDestroyed()) return;
        Intent splashIntent = new Intent(context, Base_SplashActivity.class);
        splashIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.finish();
        context.startActivity(splashIntent);
    }

    private boolean deleteActivationRecord(Activation activation) {
        try {
            base_dbh.DeleteActivation(activation);
            return true;
        } catch (SQLiteException | IllegalArgumentException
                 | IllegalStateException exception) {
            callMethod.Log("Profile activation deletion failed: "
                    + exception.getClass().getSimpleName());
            callMethod.showToast("حذف پروفایل انجام نشد؛ دوباره تلاش کنید");
            return false;
        }
    }

    private boolean hasSafeProfileName(Activation activation) {
        if (activation == null) return false;
        String companyName = activation.getEnglishCompanyName();
        return companyName != null
                && !companyName.trim().isEmpty()
                && !".".equals(companyName)
                && !"..".equals(companyName)
                && companyName.indexOf('/') < 0
                && companyName.indexOf('\\') < 0;
    }


}
