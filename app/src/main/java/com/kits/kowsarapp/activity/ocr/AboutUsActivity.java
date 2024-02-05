package com.kits.kowsarapp.activity.ocr;

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
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.kits.ocrkowsar.BuildConfig;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.databinding.ActivityAboutUsBinding;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.NumberFunctions;

import java.io.File;


public class AboutUsActivity extends AppCompatActivity {
    CallMethod callMethod;
    DatabaseHelper dbh;

    ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Config();
        setPersianText(binding.tv1);
        setPersianText(binding.tv2);
        setPersianText(binding.tv3);
        setPersianText(binding.tv4);

        binding.tv5.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage("آخرین نسخه دانلود شود؟");

            builder.setPositiveButton(R.string.textvalue_yes, (dialogalert, which) -> {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!getPackageManager().canRequestPackageInstalls()) {
                        // Open the permission settings for the user to enable the permission
                        Intent intent1 = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                        intent1.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent1, 1);
                    } else {

                        final Dialog dialog = new Dialog(AboutUsActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.loginconfig);
                        EditText ed_password = dialog.findViewById(R.id.edloginconfig);
                        MaterialButton btn_login = dialog.findViewById(R.id.btnloginconfig);
                        btn_login.setOnClickListener(vs -> {
                            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {
                                DownloadFun();
                            } else {
                                callMethod.showToast("رمز عبور صیحیح نیست");
                            }
                        });
                        dialog.show();
                    }
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
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        setSupportActionBar(binding.AboutusActivityToolbar);

    }

    private void setPersianText(TextView textView) {
        textView.setText(NumberFunctions.PerisanNumber(textView.getText().toString()));
    }

    private void DownloadFun() {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://87.107.78.234:60005/app/Ocrkowsar.apk"));
        request.setTitle("Ocrkowsar");
        request.setDescription("Downloading New Version");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "Ocrkowsar.apk");

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
                                    AboutUsActivity.this,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    new File(Environment.getExternalStorageDirectory() + "/Android/data/com.kits.Ocrkowsar/files/Download/Ocrkowsar.apk")
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (getPackageManager().canRequestPackageInstalls()) {
                    DownloadFun();
                }
            }
        }
    }


}
