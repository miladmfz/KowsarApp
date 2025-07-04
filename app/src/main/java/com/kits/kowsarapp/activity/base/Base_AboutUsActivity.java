package com.kits.kowsarapp.activity.base;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_RegistrationActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.databinding.DefaultActivityAboutusBinding;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.io.File;
import java.util.Objects;


public class Base_AboutUsActivity extends AppCompatActivity {
    CallMethod callMethod;
    DefaultActivityAboutusBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DefaultActivityAboutusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Config();
        setPersianText(binding.tv1);
        setPersianText(binding.tv3);

        binding.tv5.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage("آخرین نسخه دانلود شود؟");

            builder.setPositiveButton(R.string.textvalue_yes, (dialogalert, which) -> {

                if (!getPackageManager().canRequestPackageInstalls()) {
                    // Open the permission settings for the user to enable the permission
                    Intent intent1 = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    intent1.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent1, 1);
                } else {

                    final Dialog dialog = new Dialog(Base_AboutUsActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.setContentView(R.layout.default_loginconfig);
                    EditText ed_password = dialog.findViewById(R.id.d_loginconfig_ed);
                    MaterialButton btn_login = dialog.findViewById(R.id.d_loginconfig_btn);


                    btn_login.setOnClickListener(vs -> {
                        if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {
                            DownloadFun();
                        } else {
                            callMethod.showToast("رمز عبور صیحیح نیست");
                        }
                    });
                    dialog.show();
                }

            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                // code to handle negative button click
            });

            AlertDialog dialog = builder.create();
            dialog.show();


        });
    }

    public void Config() {
        callMethod = new CallMethod(this);
        setSupportActionBar(binding.bAboutusToolbar);

    }

    private void setPersianText(TextView textView) {
        textView.setText(NumberFunctions.PerisanNumber(textView.getText().toString()));
    }

    private void DownloadFun() {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://itmali.ir/app/KowsarApp.apk"));
        request.setTitle("کوثر سامانه");
        request.setDescription("در حال بارگیری آخرین نسخه نرم افزار");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "KowsarApp.apk");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadID = downloadManager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId != -1) {
                    // Check if the download was successful
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadID);
                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int status = cursor.getInt(statusIndex);
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {

                            Uri apkUri = FileProvider.getUriForFile(
                                    Base_AboutUsActivity.this,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    new File(Environment.getExternalStorageDirectory() + "/Android/data/com.kits.kowsarapp/files/Download/KowsarApp.apk")
                            );

                            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            installIntent.setData(apkUri);
                            installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(installIntent);


                        }
                    }
                }
            }
        };

        // Register the BroadcastReceiver
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (getPackageManager().canRequestPackageInstalls()) {
                DownloadFun();
            }
        }
    }


}
