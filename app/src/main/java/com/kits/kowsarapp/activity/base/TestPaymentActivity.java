package com.kits.kowsarapp.activity.base;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.ThirdPartyRequest;
import com.kits.kowsarapp.application.base.ThirdPartyResult;
import com.kits.kowsarapp.model.base.Base_DBH;

import retrofit2.converter.gson.GsonConverterFactory;

public class TestPaymentActivity extends AppCompatActivity {

    private static final int REQUEST_POS = 9001;

    private EditText edtAmount;
    private TextView txtResult;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_payment);

        edtAmount = findViewById(R.id.edtAmount);
        txtResult = findViewById(R.id.txtResult);
        Button btnPayTest = findViewById(R.id.btnPayTest);

        btnPayTest.setOnClickListener(v -> startPosPayment());
    }

    private void startPosPayment() {
        String amount = edtAmount.getText().toString().trim();

        if (amount.isEmpty()) {
            edtAmount.setError("مبلغ را وارد کنید");
            return;
        }

        // ساخت درخواست طبق مستند به‌پرداخت (روش بدون کتابخانه) :contentReference[oaicite:0]{index=0}
        ThirdPartyRequest request = new ThirdPartyRequest();
        request.versionName = "2.0.0";
        request.sessionId = "TEST_" + System.currentTimeMillis();
        request.applicationId = 10135;             // 👈 این رو با applicationId واقعی خودت عوض کن
        request.totalAmount = amount;
        request.transactionType = "PURCHASE";     // نوع تراکنش: خرید
        request.echoData = "TestEcho";

        String json = gson.toJson(request);

        Intent posIntent = new Intent("com.behpardakht.thirdparty.payment");

        posIntent.setPackage("com.behpardakht.app");

        posIntent.putExtra("paymentData", json);

        // کلید طبق مستند: paymentData :contentReference[oaicite:1]{index=1}

        try {
            startActivityForResult(posIntent, REQUEST_POS);
            txtResult.setText("در حال ارسال درخواست به پوز...");
        } catch (Exception e) {
            txtResult.setText("خطا در اجرای اپ پرداخت: " + e.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_POS) return;

        // یک لاگ خام که همیشه ذخیره می‌کنیم (حتی اگر JSON نیاد)
        StringBuilder rawLog = new StringBuilder();
        rawLog.append("activityResultCode=").append(resultCode).append("\n");

        if (data != null && data.getExtras() != null) {
            rawLog.append("---- extras ----\n");
            for (String key : data.getExtras().keySet()) {
                Object v = data.getExtras().get(key);
                rawLog.append(key).append("=").append(String.valueOf(v)).append("\n");
            }
        } else {
            rawLog.append("extras=null\n");
        }

        // JSON نتیجه
        String resultJson = (data == null) ? null : data.getStringExtra("paymentResult");
        if (resultJson == null || resultJson.trim().isEmpty()) {
            rawLog.append("paymentResult=NULL_OR_EMPTY\n");
        } else {
            rawLog.append("---- paymentResult ----\n");
            rawLog.append(resultJson);
        }

        // تلاش برای parse (اگر شد)
        ThirdPartyResult res = null;
        try {
            if (resultJson != null && !resultJson.trim().isEmpty()) {
                res = gson.fromJson(resultJson, ThirdPartyResult.class);
            }
        } catch (Exception ignored) {
            // لاگش رو در DB/File می‌فرستیم، لازم نیست اینجا کاری کنیم
        }

        // PreFac (اگر echoData = PreFac)
        String preFac = "0";
        if (res != null && res.echoData != null && !res.echoData.trim().isEmpty()) {
            preFac = res.echoData;
        }

        // ✅ در همه حالت‌ها: هم فایل، هم دیتابیس
        String filePath = savePaymentResultFile(preFac, rawLog.toString());
//        long rowId = insertPaymentResultDb(preFac, res, rawLog.toString());

        // نمایش خلاصه
        if (resultCode != RESULT_OK || data == null) {
            txtResult.setText("تراکنش لغو/ناموفق شد"
                    + "\nفایل: " + filePath
                    + "\nDB RowId: "  );
            return;
        }

        if (res == null) {
            txtResult.setText("نتیجه نامعتبر/عدم امکان parse"
                    + "\nفایل: " + filePath
                    + "\nDB RowId: "  );
            return;
        }

        txtResult.setText(
                "کد نتیجه: " + res.resultCode +
                        "\nتوضیح: " + res.resultDescription +
                        "\nمبلغ: " + res.transactionAmount +
                        "\nRefId: " + res.referenceID +
                        "\n\nفایل: " + filePath +
                        "\nDB RowId: "
        );
    }


    private String savePaymentResultFile(String preFac, String rawLog) {
        try {
            String ts = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US)
                    .format(new java.util.Date());

            String safePreFac = (preFac == null ? "NA" : preFac.replaceAll("[^0-9A-Za-z_-]", "_"));

            java.io.File dir = new java.io.File(getFilesDir(), "payment_logs");
            if (!dir.exists()) dir.mkdirs();

            java.io.File file = new java.io.File(dir, "Payment_" + safePreFac + "_" + ts + ".txt");

            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            fos.write(rawLog.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            fos.flush();
            fos.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            return "FileSaveError: " + e.getMessage();
        }
    }

//
//    private long insertPaymentResultDb(String preFac, ThirdPartyResult res, String rawJson) {
//        try {
//            Base_DBH base_dbh = new Base_DBH(App.getContext(), "/data/data/com.kits.kowsarapp/databases/KowsarDb.sqlite");
//            base_dbh.CreatePaymentLog();
//            return base_dbh.InsertPaymentLog(preFac, res, rawJson);
//        } catch (Exception e) {
//            return -1;
//        }
//    }


//    private long insertPaymentResultDb(String preFac, @Nullable ThirdPartyResult res, String rawLog) {
//        try {
//            // اگر parse نشده، یک نتیجه ساختگی بساز که جدول خالی نماند
//            if (res == null) {
//                res = new ThirdPartyResult();
//                res.echoData = preFac;
//                res.resultCode = "NO_PARSE";
//                res.resultDescription = "Result not parsed / missing JSON";
//                res.transactionAmount = null;
//                res.referenceID = null;
//                res.terminalID = null;
//                res.maskedCardNumber = null;
//                res.dateOfTransaction = null;
//                res.timeOfTransaction = null;
//            }
//
//            // rawLog را در ستون RawJson ذخیره می‌کنیم (اسم ستون تو جدول: RawJson)
////            return broker_dbh.InsertPaymentLog(preFac, res, rawLog);
//
//        } catch (Exception e) {
//            return -1;
//        }
//    }

}
