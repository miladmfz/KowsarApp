package com.kits.kowsarapp.application.base;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.Calendar;
import java.util.Objects;


public class LocationService extends Service {

    Broker_DBH broker_dbh;
    CallMethod callMethod = new CallMethod(App.getContext());
    PersianCalendar calendar1 = new PersianCalendar();





    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if(!callMethod.ReadString("ServerURLUse").equals("")) {

                calendar1.setTimeInMillis((Objects.requireNonNull(locationResult.getLastLocation()).getTime() + 12600000));
                if (calendar1.get(Calendar.HOUR_OF_DAY) > 7 && calendar1.get(Calendar.HOUR_OF_DAY) < 20) {
                    broker_dbh.UpdateLocationService(locationResult, calendar1.getPersianShortDateTime());
                    callMethod.Log("startLocationService");
                }
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("onBind");
    }

    private void startLocationService() {
        callMethod.Log("startLocationService");
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultintent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultintent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.img_logo_kits_jpg);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Kowsar Service");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setSound(null);
        builder.setNotificationSilent();


        builder.setPriority(NotificationCompat.DEFAULT_ALL);

        if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "LocationService", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("this channel is used by locationservice ");
            notificationManager.createNotificationChannel(notificationChannel);
        }


        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setNumUpdates(1);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Constants.Location_Service_ID, builder.build());


    }


    private void stopLocationService() {

        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }


    private boolean isLocationServiceRunning() {

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {

            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {

                if (LocationService.class.getName().equals(serviceInfo.service.getClassName())) {

                    if (serviceInfo.foreground) {

                        return true;
                    }
                }
            }

            return false;
        }

        return false;
    }

    @Override
    public void onCreate() {

        if (broker_dbh == null) {
            CallMethod callMethod = new CallMethod(App.getContext());
            broker_dbh = new Broker_DBH(App.getContext(), callMethod.ReadString("DatabaseName"));
        } else {
            callMethod.Log("dbh=null");

        }
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!isLocationServiceRunning()) {
            startLocationService();
        } else {
            stopLocationService();
            startLocationService();
        }
        return super.onStartCommand(intent, flags, startId);

    }

}
