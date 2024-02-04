package com.kits.kowsarapp.application.base;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Settings;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_NavActivity;
import com.kits.kowsarapp.model.RetrofitResponse;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.base.APIClient_kowsar;
import com.kits.kowsarapp.webService.base.APIInterface_kowsar;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Base_Action {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    Context mContext;
    CallMethod callMethod;
    Broker_DBH dbh;
    Intent intent;
    Cursor cursor;
    Integer il;
    String url;
    Broker_APIInterface broker_apiInterface;
    public Base_Action(Context mContext)   {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Broker_DBH(mContext, callMethod.ReadString("DatabaseName"));
        url = callMethod.ReadString("ServerURLUse");
        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);

    }



    public void lottiereceipt() {

        Dialog dialog1 = new Dialog(mContext);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.default_lottie);
        LottieAnimationView animationView = dialog1.findViewById(R.id.d_lottie_name);
        animationView.setAnimation(R.raw.receipt);
        dialog1.show();
        animationView.setRepeatCount(0);

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dialog1.dismiss();

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });


    }

    public void lottieok() {
        if (mContext != null) {
            Dialog dialog1 = new Dialog(mContext);
            dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setContentView(R.layout.default_lottie);
            LottieAnimationView animationView = dialog1.findViewById(R.id.d_lottie_name);
            animationView.setAnimation(R.raw.oklottie);
            try {
                dialog1.show();
            } catch (Exception e) {
                Log.e("Lottie", "Error while showing the dialog: " + e.getMessage());
            }
            animationView.setRepeatCount(0);

            animationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    dialog1.dismiss();
                    intent = new Intent(mContext, Broker_NavActivity.class);
                    ((Activity) mContext).finish();
                    ((Activity) mContext).overridePendingTransition(0, 0);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(0, 0);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

        }
    }



    @SuppressLint("HardwareIds")
    public void app_info() {

        @SuppressLint("HardwareIds") String android_id = BuildConfig.BUILD_TYPE.equals("release") ?
                Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID) :
                "debug";
        PersianCalendar calendar1 = new PersianCalendar();
        calendar1.setTimeZone(TimeZone.getDefault());
        String version = BuildConfig.VERSION_NAME;



        APIInterface_kowsar apiInterface = APIClient_kowsar.getCleint_log().create(APIInterface_kowsar.class);
        Call<RetrofitResponse> call = apiInterface.Kowsar_log("Kowsar_log", android_id
                , url
                , callMethod.ReadString("PersianCompanyNameUse")
                , callMethod.ReadString("PreFactorCode")
                , calendar1.getPersianShortDateTime()
                , dbh.ReadConfig("BrokerCode")
                , version);

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                } else {
                    // Handle unsuccessful response
                }
            }


            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                // Handle failure
            }
        });






    }


    public String CursorToJson(Cursor cursor) {
        JSONArray resultSet = new JSONArray();
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    JSONObject rowObject = new JSONObject();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String columnName = cursor.getColumnName(i);
                        if (columnName != null) {
                            rowObject.put(columnName, cursor.getString(i));
                        }
                    }
                    resultSet.put(rowObject);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("CursorToJson", "Error while converting cursor to JSON: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return resultSet.toString();
    }


    public String cursorToJson(Cursor cursor) {
        JSONArray resultSet = new JSONArray();
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    JSONObject rowObject = new JSONObject();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String columnName = cursor.getColumnName(i);
                        if (columnName != null) {
                            rowObject.put(columnName, cursor.getString(i));
                        }
                    }
                    resultSet.put(rowObject);
                } while (cursor.moveToNext());
            }
        } catch (JSONException e) {
            Log.e("CursorToJson", "Error while converting cursor to JSON: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return resultSet.toString();
    }



}
