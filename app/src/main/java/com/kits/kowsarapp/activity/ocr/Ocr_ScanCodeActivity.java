package com.kits.kowsarapp.activity.ocr;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Ocr_ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    CallMethod callMethod;
    Intent intent;
    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void handleResult(Result result) {
        callMethod=new CallMethod(this);

        String scan_result = result.getText();

        if(callMethod.ReadString("Category").equals("1")){
            intent = new Intent(Ocr_ScanCodeActivity.this, Ocr_ConfirmActivity.class);
            intent.putExtra("ScanResponse", scan_result);
        }else if(callMethod.ReadString("Category").equals("6")){
            intent = new Intent(Ocr_ScanCodeActivity.this, Ocr_ConfirmActivity.class);
            intent.putExtra("ScanResponse", scan_result);
            intent.putExtra("State", "6");
            intent.putExtra("FactorImage", "");
        }else {
            intent = new Intent(Ocr_ScanCodeActivity.this, Ocr_FactorDetailActivity.class);
            intent.putExtra("ScanResponse", scan_result);
            intent.putExtra("FactorImage", "");
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();


    }
}
