package com.kits.kowsarapp.viewholder.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.kits.kowsarapp.application.base.CallMethod;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Base_AllAppViewHolder extends RecyclerView.ViewHolder {

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
    Broker_DBH broker_dbh;
    Ocr_DBH ocr_dbh;
    Order_DBH order_dbh;
    Find_DBH search_dbh;


    public Base_AllAppViewHolder(View itemView, Context context) {
        super(itemView);

        base_dbh = new Base_DBH(context, "/data/data/com.kits.kowsarapp/databases/KowsarDb.sqlite");
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

        tv_persianname.setText(activation.getPersianCompanyName());

        switch (activation.getAppType()) {
            case "1":  // broker
                tv_apptype.setText("بازاریابی");
                img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_broker_logo));
                break;
            case "2":  // ocr
                tv_apptype.setText("جمع آوری و توضیع");
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


        Kowsar_APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(Kowsar_APIInterface.class);

        Activation activationsss=activations;

        btn_login.setOnClickListener(view -> {

            if (!new File(activationsss.getDatabaseFilePath()).exists()) {

                DownloadRequest(activationsss,mcontext);
            } else {
                callMethod.EditString("PersianCompanyNameUse", activationsss.getPersianCompanyName());
                callMethod.EditString("EnglishCompanyNameUse", activationsss.getEnglishCompanyName());
                callMethod.EditString("ServerURLUse", activationsss.getServerURL());
                if (activationsss.getSecendServerURL() == null || activationsss.getSecendServerURL().isEmpty()) {
                    callMethod.EditString("SecendServerURL", activationsss.getServerURL());
                }else{
                    callMethod.EditString("SecendServerURL", activationsss.getSecendServerURL());
                }
                callMethod.EditString("DatabaseName", activationsss.getDatabaseFilePath());
                callMethod.EditString("ActivationCode", activationsss.getActivationCode());
                callMethod.EditString("AppType", activationsss.getAppType());
                callMethod.EditString("DbName", activationsss.getDbName());

                Intent intent = new Intent(mcontext, Base_SplashActivity.class);
                mcontext.startActivity(intent);
                ((Activity) mcontext).finish();

            }
        });


        btn_recall.setOnClickListener(view -> {
            Call<RetrofitResponse> call1 = apiInterface.Activation(

                    activationsss.getActivationCode(),"0"
            );
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        activationres = response.body().getActivations().get(0);
                        base_dbh.InsertActivation(activationres);
                        ((Activity) mcontext).finish();
                        mcontext.startActivity(((Activity) mcontext).getIntent());

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

//                    callMethod.ErrorLog(t.getMessage());
                }
            });
        });


        btn_delete.setOnClickListener(view -> {

            if (!new File(activationsss.getDatabaseFilePath()).exists()) {
                base_dbh.DeleteActivation(activationsss);
                Intent intent = new Intent(mcontext, Base_SplashActivity.class);
                ((Activity) mcontext).finish();
                mcontext.startActivity(intent);
            } else {


                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext, R.style.AlertDialogCustom);
                builder.setTitle(R.string.textvalue_allert);
                builder.setMessage(" آیا از حذف تمامی اطلاعات"+activationsss.getPersianCompanyName()+" مطمئن هستید؟ ");

                builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {




                    final Dialog dialog1 = new Dialog(mcontext);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
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

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();


    }




    public void Deletedb(Activation activation,Context mcontext) {
        base_dbh.DeleteActivation(activation);

        File currentFile = new File(mcontext.getApplicationInfo().dataDir + "/databases/" + activation.getEnglishCompanyName() + "/KowsarDb.sqlite");
        File newFile = new File(mcontext.getApplicationInfo().dataDir + "/databases/" + activation.getEnglishCompanyName() + "/deleteddb");
        File databasedir = new File(mcontext.getApplicationInfo().dataDir + "/databases/" + activation.getEnglishCompanyName());
        deleteRecursive(databasedir);
        base_dbh.DeleteActivation(activation);
        Intent intent = new Intent(mcontext, Base_SplashActivity.class);
        ((Activity) mcontext).finish();
        mcontext.startActivity(intent);
    }

    private boolean rename(File from, File to) {
        return Objects.requireNonNull(from.getParentFile()).exists() && from.exists() && from.renameTo(to);
    }
    public void DownloadRequest(Activation activation,Context mcontext) {

        btn_prog.setOnClickListener(view -> DownloadRequest(activation,mcontext));


        //String downloadurl="http://5.160.152.173:60005/api/kits/GetDb?Code="+activation.getActivationCode();
        String downloadurl="http://itmali.ir/webapi/kits/GetDb?Code="+activation.getActivationCode();

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();

        PRDownloader.initialize(mcontext, config);

        // Setting timeout globally for the download network requests:
        PRDownloaderConfig config1 = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(mcontext, config1);


        downloadId = PRDownloader.download(
                        downloadurl,
                        activation.getDatabaseFolderPath(),
                        "KowsarDbTemp.sqlite"
                )

                .build()
                .setOnStartOrResumeListener(() -> {
                    dialog.show();
                    dialog.setCancelable(false);
                })
                .setOnPauseListener(() -> {

                })
                .setOnCancelListener(() -> {
                    File DownloadTemp = new File(activation.getDatabaseFolderPath() + "/KowsarDbTemp.sqlite");
                    DownloadTemp.delete();
                })

                .setOnProgressListener(progress -> {
                    tv_rep.setText("در حال بارگیری...");
                    tv_step.setVisibility(View.VISIBLE);
                    tv_step.setText(NumberFunctions.PerisanNumber((((progress.currentBytes) * 100) / progress.totalBytes) + "/100"));
                })

                .start(new OnDownloadListener() {
                    @SuppressLint("SdCardPath")
                    @Override

                    public void onDownloadComplete() {


                        File downloadTemp = new File(activation.getDatabaseFolderPath() + "/KowsarDbTemp.sqlite");
                        File completeFile = new File(activation.getDatabaseFolderPath() + "/KowsarDb.sqlite");

// تغییر نام فایل
                        if (downloadTemp.renameTo(completeFile)) {
                            callMethod.EditString("DatabaseName", activation.getDatabaseFilePath());

                            broker_dbh = new Broker_DBH(mcontext, callMethod.ReadString("DatabaseName"));
                            ocr_dbh = new Ocr_DBH(mcontext, callMethod.ReadString("DatabaseName"));
                            order_dbh = new Order_DBH(mcontext, callMethod.ReadString("DatabaseName"));
                            search_dbh = new Find_DBH(mcontext, callMethod.ReadString("DatabaseName"));

                            switch (activation.getAppType()) {
                                case "1":  // broker
                                    broker_dbh.DatabaseCreate();
                                    broker_dbh.InitialConfigInsert();
                                    break;
                                case "2":  // ocr
                                    ocr_dbh.DatabaseCreate();
                                    break;
                                case "3":  // order
                                    order_dbh.DatabaseCreate();
                                    break;
                                case "4":  // search
                                    search_dbh.DatabaseCreate();
                                    break;
                            }

                            callMethod.EditString("PersianCompanyNameUse", activation.getPersianCompanyName());
                            callMethod.EditString("EnglishCompanyNameUse", activation.getEnglishCompanyName());
                            callMethod.EditString("ServerURLUse", activation.getServerURL());
                            callMethod.EditString("IpConfig", "");
                            callMethod.EditString("AppType", activation.getAppType());
                            callMethod.EditString("DbName", activation.getDbName());
                            callMethod.EditString("ActivationCode", activation.getActivationCode());

                            if (activation.getSecendServerURL() == null || activation.getSecendServerURL().isEmpty()) {
                                callMethod.EditString("SecendServerURL", activation.getServerURL());
                            }else{
                                callMethod.EditString("SecendServerURL", activation.getSecendServerURL());
                            }

                            Intent intent = new Intent(App.getContext(), Base_SplashActivity.class);
                            mcontext.startActivity(intent);
                            ((Activity) mcontext).finish();
                            dialog.dismiss();

                        } else {
                            callMethod.Log("Error: Database rename failed!");
                        }



//                        File DownloadTemp = new File(activation.getDatabaseFolderPath() + "/KowsarDbTemp.sqlite");
//                        File CompletefILE = new File(activation.getDatabaseFolderPath() + "/KowsarDb.sqlite");
//                        DownloadTemp.renameTo(CompletefILE);
//                        callMethod.Log("");
//                        callMethod.EditString("DatabaseName", activation.getDatabaseFilePath());
//
//                        broker_dbh = new Broker_DBH(mcontext, callMethod.ReadString("DatabaseName"));
//                        ocr_dbh = new Ocr_DBH(mcontext, callMethod.ReadString("DatabaseName"));
//                        order_dbh = new Order_DBH(mcontext, callMethod.ReadString("DatabaseName"));
//                        search_dbh = new Find_DBH(mcontext, callMethod.ReadString("DatabaseName"));
//                        switch (activation.getAppType()) {
//                            case "1":  // broker
//                                broker_dbh.DatabaseCreate();
//                                broker_dbh.InitialConfigInsert();
//                                break;
//                            case "2":  // ocr
//                                ocr_dbh.DatabaseCreate();
//                                break;
//                            case "3":  // order
//                                order_dbh.DatabaseCreate();
//                                break;
//                            case "4":  // search
//                                search_dbh.DatabaseCreate();
//                                break;
//                        }
//


                    }


                    @Override
                    public void onError(Error error) {
                        btn_prog.setVisibility(View.VISIBLE);
                        File DownloadTemp = new File(activation.getDatabaseFolderPath() + "/KowsarDbTemp.sqlite");
                        DownloadTemp.delete();
                        tv_step.setText("مشکل ارتباطی لطفا دوباره امتحان کنید");

                    }
                });

    }


}