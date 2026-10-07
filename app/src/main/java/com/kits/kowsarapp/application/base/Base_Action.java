package com.kits.kowsarapp.application.base;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;


import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_NavActivity;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.webService.base.APIClient_kowsar;
import com.kits.kowsarapp.webService.base.Kowsar_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Base_Action {

    final Context mContext;
    final CallMethod callMethod;

    public Base_Action(Context mContext)   {
        this.mContext = mContext;
        this.callMethod = mContext == null ? null : new CallMethod(mContext);
    }



    public void lottiereceipt() {
        showLottieAnimation(R.raw.receipt, false);
    }

    public void lottieok() {
        showLottieAnimation(R.raw.oklottie, true);
    }

    private void showLottieAnimation(int animationResource, boolean navigateOnCompletion) {
        Activity activity = usableActivity();
        if (activity == null) return;

        Dialog dialog = new Dialog(activity);
        try {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
            dialog.setContentView(R.layout.default_lottie);
            LottieAnimationView animationView = dialog.findViewById(R.id.d_lottie_name);
            if (animationView == null) return;
            animationView.setAnimation(animationResource);
            animationView.setRepeatCount(0);
            dialog.show();

            animationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    dismissDialogSafely(dialog);
                    if (navigateOnCompletion) navigateToBroker(activity);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    dismissDialogSafely(dialog);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        } catch (WindowManager.BadTokenException | IllegalArgumentException |
                 IllegalStateException exception) {
            ReleaseLog.error("BrokerAnimation", exception);
            dismissDialogSafely(dialog);
        }
    }

    private Activity usableActivity() {
        if (!(mContext instanceof Activity)) return null;
        Activity activity = (Activity) mContext;
        return activity.isFinishing() || activity.isDestroyed() ? null : activity;
    }

    private void dismissDialogSafely(Dialog dialog) {
        if (dialog == null || !dialog.isShowing()) return;
        try {
            dialog.dismiss();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            ReleaseLog.error("BrokerAnimationDismiss", exception);
        }
    }

    private void navigateToBroker(Activity activity) {
        if (activity.isFinishing() || activity.isDestroyed()) return;
        Intent navigation = new Intent(activity, Broker_NavActivity.class);
        navigation.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            activity.finish();
            activity.overridePendingTransition(0, 0);
            activity.startActivity(navigation);
            activity.overridePendingTransition(0, 0);
        } catch (ActivityNotFoundException | SecurityException exception) {
            ReleaseLog.error("BrokerNavigation", exception);
        }
    }

    @SuppressLint("HardwareIds")
    public void app_info() {
        if (mContext == null || callMethod == null) return;

        try {
            String androidId = BuildConfig.BUILD_TYPE.equals("release")
                    ? Settings.Secure.getString(
                    mContext.getContentResolver(), Settings.Secure.ANDROID_ID)
                    : "debug";
            PersianCalendar calendar = new PersianCalendar();
            calendar.setTimeZone(TimeZone.getDefault());
            String brokerCode;
            String databaseName = callMethod.ReadString("DatabaseName");
            if (databaseName == null || databaseName.trim().isEmpty()) {
                ReleaseLog.debug("AppInfo", "Telemetry skipped: profile database is unavailable");
                return;
            }
            try (Broker_DBH helper = new Broker_DBH(mContext.getApplicationContext(), databaseName)) {
                brokerCode = helper.ReadConfig("BrokerCode");
            }

            String body = "";
            body = callMethod.CreateJson("Device_Id", androidId, body);
            body = callMethod.CreateJson("Address_Ip", callMethod.ReadString("ServerURLUse"), body);
            body = callMethod.CreateJson("Server_Name", callMethod.ReadString("PersianCompanyNameUse"), body);
            body = callMethod.CreateJson("Factor_Code", callMethod.ReadString("PreFactorCode"), body);
            body = callMethod.CreateJson("StrDate", calendar.getPersianShortDateTime(), body);
            body = callMethod.CreateJson("Broker", brokerCode, body);
            body = callMethod.CreateJson("Explain", BuildConfig.VERSION_NAME, body);
            body = callMethod.CreateJson(
                    "DeviceAgant", Build.BRAND + " / " + Build.MODEL + " / " + Build.HARDWARE, body);
            body = callMethod.CreateJson("SdkVersion", String.valueOf(Build.VERSION.SDK_INT), body);
            body = callMethod.CreateJson("DeviceIp", getIpAddress(true) + " / " + isVpnConnection(), body);

            Kowsar_APIInterface apiInterface =
                    APIClient_kowsar.getCleint_log().create(Kowsar_APIInterface.class);
            Call<RetrofitResponse> call = apiInterface.LogReport(callMethod.RetrofitBody(body));
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(
                        @NonNull Call<RetrofitResponse> call,
                        @NonNull Response<RetrofitResponse> response
                ) {
                    if (!response.isSuccessful()) {
                        ReleaseLog.debug("AppInfo", "Telemetry HTTP status=" + response.code());
                    }
                }

                @Override
                public void onFailure(
                        @NonNull Call<RetrofitResponse> call,
                        @NonNull Throwable throwable
                ) {
                    if (!call.isCanceled()) ReleaseLog.error("AppInfo", throwable);
                }
            });
        } catch (SQLiteException | IllegalArgumentException | IllegalStateException |
                 SecurityException exception) {
            ReleaseLog.error("AppInfo", exception);
        }
    }

    @SuppressLint("DefaultLocale")
    public String getIpAddress(boolean useIPv4){
        String finalAddress = "";
        try {
            Enumeration<NetworkInterface> interfaceEnumeration =
                    NetworkInterface.getNetworkInterfaces();
            if (interfaceEnumeration == null) return finalAddress;
            List<NetworkInterface> interfaces = Collections.list(interfaceEnumeration);
            for(NetworkInterface intf : interfaces){
                List<InetAddress> addresses = Collections.list(intf.getInetAddresses());
                for(InetAddress addr : addresses){
                    if(!addr.isLoopbackAddress()) {
                        String address = addr.getHostAddress();
                        if (address == null || address.isEmpty()) continue;
                        boolean isIPv4 = address.indexOf(':') < 0;
                        if(useIPv4){
                            if(isIPv4) finalAddress = address;
                        } else if(!isIPv4) {
                            int delimiter = address.indexOf('%');
                            finalAddress = delimiter < 0
                                    ? address.toUpperCase(Locale.ROOT)
                                    : address.substring(0, delimiter).toUpperCase(Locale.ROOT);
                        }
                    }
                }
            }
        } catch (SocketException | SecurityException exception) {
            ReleaseLog.error("NetworkAddress", exception);
            finalAddress = getWifiIpAddress();
        }
        return finalAddress;
    }

    @SuppressWarnings("deprecation")
    private String getWifiIpAddress() {
        try {
            WifiManager wifiManager =
                    (WifiManager) mContext.getApplicationContext()
                            .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null || wifiManager.getConnectionInfo() == null) return "";
            int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
            return String.format(
                    Locale.US,
                    "%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        } catch (SecurityException | IllegalStateException exception) {
            ReleaseLog.error("WifiAddress", exception);
            return "";
        }
    }

    public boolean isVpnConnection(){
        if (mContext == null) return false;
        try {
            if (Settings.Secure.getInt(mContext.getContentResolver(), "vpn_state", 0) == 1) {
                return true;
            }
        } catch (SecurityException exception) {
            ReleaseLog.error("VpnSetting", exception);
        }
        return isvpn1() || isvpn2();
    }

    private boolean isvpn1() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces == null) return false;
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp()) continue;
                String name = networkInterface.getName();
                if (name == null) continue;
                ReleaseLog.debug("NetworkInterface", "Interface detected: " + name);
                if (name.contains("tun") || name.contains("ppp") || name.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException | SecurityException exception) {
            ReleaseLog.error("NetworkInterface", exception);
        }
        return false;
    }

    private boolean isvpn2() {
        if (mContext == null) return false;
        try {
            ConnectivityManager manager = (ConnectivityManager)
                    mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager == null) return false;
            Network activeNetwork = manager.getActiveNetwork();
            if (activeNetwork == null) return false;
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(activeNetwork);
            return capabilities != null &&
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        } catch (SecurityException exception) {
            ReleaseLog.error("VpnCapabilities", exception);
            return false;
        }
    }
}
