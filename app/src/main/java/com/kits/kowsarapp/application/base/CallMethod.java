package com.kits.kowsarapp.application.base;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.RequestBody;


public class CallMethod extends Application {
    private final SharedPreferences shPref;
    private SharedPreferences.Editor sEdit;
    Context context;

    Toast toast;
    public CallMethod(Context mContext) {
        this.context = mContext;
        this.shPref = context.getSharedPreferences("profile", Context.MODE_PRIVATE);
    }


    public String NumberRegion(String String) {

        if (ReadString("LANG").equals("fa")) {
            return NumberFunctions.PerisanNumber(String);
        } else if (ReadString("LANG").equals("ar")) {
            return NumberFunctions.PerisanNumber(String);
        } else {
            return NumberFunctions.EnglishNumber(String);
        }

    }




    public void EditString(String Key, String Value) {
        sEdit = shPref.edit();
        sEdit.putString(Key, Value);
        sEdit.apply();
    }

    public String ReadString(String Key) {

        return shPref.getString(Key, "");
    }

    public boolean IsDebugBuild(Context context) {
        return BuildConfig.DEBUG;
    }



    public boolean ReadBoolan(String Key) {
        return shPref.getBoolean(Key, true);
    }

    public void EditBoolan(String Key, boolean Value) {
        sEdit = shPref.edit();
        sEdit.putBoolean(Key, Value);
        sEdit.apply();
    }

    public boolean firstStart() {

        return shPref.getBoolean("FirstStart", true);
    }


    public void showToast(String message) {
        if (context == null || message == null) return;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) return;
        }

        Runnable show = () -> {
            try {
                if (toast != null) toast.cancel();
                Context toastContext = context.getApplicationContext();
                if (toastContext == null) toastContext = context;
                toast = Toast.makeText(toastContext, message, Toast.LENGTH_LONG);
                toast.show();
            } catch (RuntimeException exception) {
                ReleaseLog.error("Toast", exception);
            }
        };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            show.run();
        } else {
            new Handler(Looper.getMainLooper()).post(show);
        }
    }
    public void Log(String message) {
        ReleaseLog.debug("App", message);
    }


    public String CreateJson(String key, String value, String existingJson) {

        JSONObject jsonObject = null;
        try {
            if (existingJson != null && !existingJson.isEmpty()) {
                jsonObject = new JSONObject(existingJson);
            } else {
                jsonObject = new JSONObject();
            }
            jsonObject.put(key, value);

        } catch (Exception exception) {
            ReleaseLog.error("CreateJson", exception);
        }

        return jsonObject == null ? "{}" : jsonObject.toString();
    }
    public RequestBody RetrofitBody(String jsonRequestBody) {
        String safeBody = jsonRequestBody == null ? "" : jsonRequestBody;
        if (BuildConfig.DEBUG) {
            ReleaseLog.debug("Network", "JSON request prepared; bytes="
                    + safeBody.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        }
        return RequestBody.create(MediaType.parse("application/json"), safeBody);
    }




}

