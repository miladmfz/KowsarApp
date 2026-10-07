package com.kits.kowsarapp.activity.ocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.LatestRequestGate;
import com.kits.kowsarapp.application.base.SafeListAccess;
import com.kits.kowsarapp.application.base.SafeValueParser;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.fragment.ocr.Ocr_StackFragment;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_SelectionActivity extends AppCompatActivity {
    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;

    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    ArrayList<Ocr_Good> ocr_goods= new ArrayList<>();
    ArrayList<Ocr_Good> ocr_goods_scan=new ArrayList<>();


    LinearLayoutCompat ll_main;
    CallMethod callMethod;
    FragmentManager fragmentManager ;
    FragmentTransaction fragmentTransaction;
    Ocr_StackFragment stackFragment;

    EditText ed_barcode;

    String BarcodeScan;
    String State;
    int width=1;
    Ocr_Action action;
    Handler handler;

    Integer state_category;
    public String searchtarget = "";


    LottieAnimationView progressBar;
    LottieAnimationView img_lottiestatus;
    Call<RetrofitResponse> call;
    TextView tv_lottiestatus;
    private final LatestRequestGate requestGate = new LatestRequestGate();
    private Dialog startupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));

        setContentView(R.layout.ocr_activity_selection);
        Config();
        readIntent();
        stackFragment.setBarcodeScan(BarcodeScan);
        startupDialog = new Dialog(this);

        try {

            startupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(startupDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            startupDialog.setContentView(R.layout.ocr_spinner_box);
            TextView repw = startupDialog.findViewById(R.id.ocr_spinner_text);
            repw.setText("در حال خواندن اطلاعات");
            startupDialog.show();
        }catch (Exception e){
            callMethod.Log(e.getMessage());
        }

        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this::init, 100);
            handler.postDelayed(this::dismissStartupDialog, 1000);
        }catch (Exception e){
            callMethod.Log(e.getMessage());
        }


    }
    ////////////////////////////////////////////////////


    public void readIntent(){
        Bundle bundle =getIntent().getExtras();
        BarcodeScan = bundle == null ? "" : bundle.getString("ScanResponse", "");
        State = bundle == null ? "" : bundle.getString("State", "");

    }


    public void Config() {

        callMethod = new CallMethod(this);

        action = new Ocr_Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

        handler=new Handler(Looper.getMainLooper());

        ll_main = findViewById(R.id.ocr_confirm_a_layout);
        ed_barcode = findViewById(R.id.ocr_confirm_a_barcode);
        progressBar = findViewById(R.id.ocr_confirm_a_good_prog);
        img_lottiestatus = findViewById(R.id.ocr_confirm_a_good_lottie);
        tv_lottiestatus = findViewById(R.id.ocr_confirm_a_good_tvstatus);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        stackFragment = new Ocr_StackFragment();

        ocr_goods_scan.clear();
    }



    public void init(){

        try {
            state_category=Integer.parseInt(callMethod.ReadString("Category"));
        }catch (Exception e){
            state_category=0;
        }

        if(state_category==6){
            StackLocation();
        }

    }

    public void StackLocation(){

        tv_lottiestatus.setText("اسکن کنید");
        tv_lottiestatus.setVisibility(View.VISIBLE);
        if (BarcodeScan.length()>0){

            progressBar.setVisibility(View.VISIBLE);
            tv_lottiestatus.setText("در حال جستجو");
            tv_lottiestatus.setVisibility(View.VISIBLE);
            ed_barcode.setText(BarcodeScan);
            ed_barcode.selectAll();
            Search_call();
        }
        ed_barcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 100));
        ed_barcode.setPadding(5, 5, 5, 5);



        img_lottiestatus.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        ed_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_barcode.setFocusable(true);
                ed_barcode.requestFocus();
                ed_barcode.selectAll();
            }
        });


        tv_lottiestatus.setOnClickListener(view -> {
            Intent intent = new Intent(this, Ocr_ScanCodeActivity.class);
            startActivity(intent);
            finish();
        });
        ed_barcode.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }



                    @Override
                    public void afterTextChanged( Editable editable) {


                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {


                            if (ed_barcode.getText().toString().length()>0){

                                Search_call();

                            }else {
                                if (!ocr_goods.isEmpty()) {
                                    ocr_goods.clear();
                                }

                                img_lottiestatus.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                tv_lottiestatus.setText("اسکن کنید");
                                tv_lottiestatus.setVisibility(View.VISIBLE);
                            }



                        }, Math.max(0, SafeValueParser.intOrDefault(
                                callMethod.ReadString("Delay"), 300
                        )));




                    }
                }
        );



        ed_barcode.setFocusable(true);
        ed_barcode.requestFocus();
    }


    public void Search_call(){
        if (!isUiActive()) {
            return;
        }
        searchtarget = NumberFunctions.EnglishNumber(ed_barcode.getText().toString());
        searchtarget = searchtarget.replaceAll(" ", "%");

        requestGate.invalidate();
        if (call != null) {
            call.cancel();
        }
        int requestToken = requestGate.begin();
        call=apiInterface.GetOcrGoodList("GetOcrGoodList",searchtarget);
        action.dialogProg();


        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (!canHandleRequest(requestToken)) {
                    return;
                }
                action.dialogProg_dismiss();
                if (!response.isSuccessful() || response.body() == null) {
                    showEmptyResult("GetOcrGoodList invalid HTTP response");
                    return;
                }

                    ocr_goods = SafeListAccess.mutableNonNullCopyOrEmpty(
                            response.body().getOcr_Goods()
                    );

                    if (ocr_goods.size()> 0) {
                        try {
                            img_lottiestatus.setVisibility(View.GONE);
                            tv_lottiestatus.setText("اسکن کنید");
                            tv_lottiestatus.setVisibility(View.VISIBLE);


                            FragmentManager fragmentManager = getSupportFragmentManager();
                            Ocr_StackFragment stackFragment = (Ocr_StackFragment) fragmentManager.findFragmentByTag("STACK_FRAGMENT");

                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            if (stackFragment != null) {
                                // Update the existing fragment's data
                                stackFragment.setOcr_goods(ocr_goods);
                                stackFragment.setBarcodeScan(BarcodeScan);
                                stackFragment.callrecycler() ;
                            } else {
                                // Create a new instance of StackFragment if not already added
                                stackFragment = new Ocr_StackFragment();
                                stackFragment.setOcr_goods(ocr_goods);
                                stackFragment.setBarcodeScan(BarcodeScan);
                                fragmentTransaction.replace(R.id.ocr_confirm_a_framelayout, stackFragment, "STACK_FRAGMENT");
                            }

                            fragmentTransaction.commitAllowingStateLoss();

                            progressBar.setVisibility(View.GONE);

                        }catch (Exception e){
                            callMethod.Log(e.getMessage());

                        }
                    } else {
                        showEmptyResult(null);
                    }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                if (call.isCanceled() || !canHandleRequest(requestToken)) {
                    return;
                }
                action.dialogProg_dismiss();
                callMethod.Log("GetOcrGoodList failed: " + t.getClass().getSimpleName());
                callMethod.showToast("مشکلی در برقراری ارتباط");
                showEmptyResult(null);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (ed_barcode == null || !isUiActive()) {
            super.onWindowFocusChanged(hasFocus);
            return;
        }
        ed_barcode.setFocusable(true);
        ed_barcode.requestFocus();
        ed_barcode.selectAll();


        super.onWindowFocusChanged(hasFocus);
    }

    private boolean canHandleRequest(int requestToken) {
        return isUiActive() && requestGate.isCurrent(requestToken);
    }

    private boolean isUiActive() {
        return !isFinishing() && !isDestroyed();
    }

    private void showEmptyResult(String diagnostic) {
        if (!isUiActive()) {
            return;
        }
        if (diagnostic != null) {
            callMethod.Log(diagnostic);
        }
        ocr_goods.clear();
        progressBar.setVisibility(View.GONE);
        tv_lottiestatus.setText("موردی یافت نشد");
        img_lottiestatus.setVisibility(View.VISIBLE);
        tv_lottiestatus.setVisibility(View.VISIBLE);
    }

    private void dismissStartupDialog() {
        if (startupDialog != null && startupDialog.isShowing()) {
            startupDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        requestGate.invalidate();
        if (call != null) {
            call.cancel();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        dismissStartupDialog();
        if (action != null) {
            action.cancelActiveSubmission();
            action.dialogProg_dismiss();
        }
        super.onDestroy();
    }




}
