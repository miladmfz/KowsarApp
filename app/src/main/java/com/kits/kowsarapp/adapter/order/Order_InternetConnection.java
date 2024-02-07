package com.kits.kowsarapp.adapter.order;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;


import com.kits.kowsarapp.application.base.App;

import java.lang.reflect.Method;

public class Order_InternetConnection {

    private final Context mContext;

    public Order_InternetConnection(Context mContext) {
        this.mContext = mContext;
    }

    public boolean has() {
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void data() {
        try {
            TelephonyManager telephonyService = (TelephonyManager) App.getContext().getSystemService(Context.TELEPHONY_SERVICE);

            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

            setMobileDataEnabledMethod.invoke(telephonyService, setMobileDataEnabledMethod);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
