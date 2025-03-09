package com.kits.kowsarapp.application.base;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_NavActivity;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.base.APIClient_kowsar;
import com.kits.kowsarapp.webService.base.Kowsar_APIInterface;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Base_Action {

    Context mContext;
    CallMethod callMethod;
    Broker_DBH broker_dbh;
    Intent intent;
    Integer il;
    String url;
    Broker_APIInterface broker_apiInterface;
    public Base_Action(Context mContext)   {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.broker_dbh = new Broker_DBH(mContext, callMethod.ReadString("DatabaseName"));
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

        Log.e("Debug Build.VERSION.SDK_INT =", Build.VERSION.SDK_INT+"");
        Log.e("Debug isVpnConnection =",getIpAddress(true)+" / "+isVpnConnection()+"");


        @SuppressLint("HardwareIds")
        String android_id = BuildConfig.BUILD_TYPE.equals("release") ?
                Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID) :
                "debug";
        PersianCalendar calendar1 = new PersianCalendar();
        calendar1.setTimeZone(TimeZone.getDefault());
        String version = BuildConfig.VERSION_NAME;



        Kowsar_APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(Kowsar_APIInterface.class);
//        Call<RetrofitResponse> call = apiInterface.Kowsar_log("Kowsar_log", android_id
//                , url
//                , callMethod.ReadString("PersianCompanyNameUse")
//                , callMethod.ReadString("PreFactorCode")
//                , calendar1.getPersianShortDateTime()
//                , dbh.ReadConfig("BrokerCode")
//                , version);
//
//

        String Body_str  = "";
        Body_str =callMethod.CreateJson("Device_Id", android_id, Body_str);
        Body_str =callMethod.CreateJson("Address_Ip", url, Body_str);
        Body_str =callMethod.CreateJson("Server_Name", callMethod.ReadString("PersianCompanyNameUse"), Body_str);
        Body_str =callMethod.CreateJson("Factor_Code", callMethod.ReadString("PreFactorCode"), Body_str);
        Body_str =callMethod.CreateJson("StrDate", calendar1.getPersianShortDateTime(), Body_str);
        Body_str =callMethod.CreateJson("Broker",  broker_dbh.ReadConfig("BrokerCode"), Body_str);
        Body_str =callMethod.CreateJson("Explain", version, Body_str);
        Body_str =callMethod.CreateJson("DeviceAgant", Build.BRAND+" / "+Build.MODEL+" / "+Build.HARDWARE, Body_str);
        Body_str =callMethod.CreateJson("SdkVersion", Build.VERSION.SDK_INT+"", Body_str);
        Body_str =callMethod.CreateJson("DeviceIp", getIpAddress(true)+" / "+isVpnConnection(), Body_str);

        Log.e("e=",""+Body_str);
        Call<RetrofitResponse> call = apiInterface.LogReport(callMethod.RetrofitBody(Body_str));
        Log.e("ec=",""+call.request().url());
        Log.e("ec=",""+call.request().body());


        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                Log.e("res=",""+response.body().toString());

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
    @SuppressLint("DefaultLocale")
    public String getIpAddress(boolean useIPv4){
        int delim = 0;
        String finalAdress = "";
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for(NetworkInterface intf : interfaces){
                List<InetAddress> addresses = Collections.list(intf.getInetAddresses());
                for(InetAddress addr : addresses){
                    if(!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0 ;
                        if(useIPv4){
                            if(isIPv4)
                                finalAdress = sAddr;
                        } else {
                            if(!isIPv4){
                                delim = sAddr.indexOf('%');
                                finalAdress =  delim<0 ? sAddr.toUpperCase() : sAddr.substring(0 , delim);
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
            int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
            finalAdress = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        }
        return finalAdress;
    }

    public boolean isVpnConnection(){
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "vpn_state", 0) == 1 || isvpn1() || isvpn2();
    }
    private boolean isvpn1() {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();
                Log.d("DEBUG", "IFACE NAME: " + iface);
                if ( iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        return false;
    }
    private boolean isvpn2() {
        ConnectivityManager cm = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm.getActiveNetwork();
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
    }



}
