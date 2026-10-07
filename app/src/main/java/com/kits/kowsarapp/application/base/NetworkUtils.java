package com.kits.kowsarapp.application.base;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.NetworkOnMainThreadException;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.net.SocketException;
import java.util.Enumeration;

public final class NetworkUtils {

    private static final int PROBE_TIMEOUT_MILLIS = 3000;

    private NetworkUtils() {
    }

    @SuppressWarnings("deprecation")
    public static boolean isNetworkAvailable(@Nullable Context context) {
        if (context == null) return false;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        } catch (SecurityException exception) {
            ReleaseLog.error("NetworkAvailability", exception);
            return false;
        }
    }

    public static boolean isVPNActive() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces == null) return false;
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                String name = networkInterface.getName();
                if (networkInterface.isUp() && name != null && name.contains("tun")) return true;
            }
        } catch (SocketException | SecurityException exception) {
            ReleaseLog.error("VpnDetection", exception);
        }
        return false;
    }

    public static boolean canReachServer(String url) {
        if (url == null || url.trim().isEmpty()) return false;
        HttpURLConnection connection = null;
        try {
            URLConnection openedConnection = new URL(url.trim()).openConnection();
            if (!(openedConnection instanceof HttpURLConnection)) return false;
            connection = (HttpURLConnection) openedConnection;
            connection.setConnectTimeout(PROBE_TIMEOUT_MILLIS);
            connection.setReadTimeout(PROBE_TIMEOUT_MILLIS);
            connection.setUseCaches(false);
            connection.connect();
            int code = connection.getResponseCode();
            return (200 <= code && code <= 299);
        } catch (IOException | IllegalArgumentException | SecurityException |
                 NetworkOnMainThreadException exception) {
            return false;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
}

