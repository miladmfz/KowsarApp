package com.kits.kowsarapp.application.base;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


public class CallMethod extends Application {
    private final SharedPreferences shPref;
    private SharedPreferences.Editor sEdit;
    Context context;

    Toast toast;
    public CallMethod(Context mContext) {
        this.context = mContext;
        this.shPref = context.getSharedPreferences("profile", Context.MODE_PRIVATE);
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
        Log.e("KowsarApp",message);
    }



}
