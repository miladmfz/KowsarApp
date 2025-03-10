package com.kits.kowsarapp.application.base;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.kits.kowsarapp.model.base.NumberFunctions;

import org.json.JSONObject;

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
        if (toast!=null){
            toast.cancel();
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }
    public void Log(String message) {
        Log.e("KowsarApp = ",message);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString()+"";
    }
    public RequestBody RetrofitBody(String jsonRequestBody) {

        Log(jsonRequestBody);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonRequestBody);

        return requestBody;
    }




}

