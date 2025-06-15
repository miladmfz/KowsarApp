package com.kits.kowsarapp.application.base;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.Calendar;


public class LocationService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private CallMethod callMethod;
    private Broker_DBH broker_dbh;
    private PersianCalendar calendar1 = new PersianCalendar();

    @Override
    public void onCreate() {
        super.onCreate();

        callMethod = new CallMethod(App.getContext());
        broker_dbh = new Broker_DBH(App.getContext(), callMethod.ReadString("DatabaseName"));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                if (location != null && !callMethod.ReadString("ServerURLUse").isEmpty()) {
                    calendar1.setTimeInMillis((location.getTime() + 12600000)-86400000); // GMT+3:30 adjustment
                    int hour = calendar1.get(Calendar.HOUR_OF_DAY);
                    Location lastLocation = null;

                    if (hour > 7 && hour < 23) {
                        try {
                            Location currentLocation = locationResult.getLastLocation();

                            if (lastLocation == null) {
                                lastLocation = currentLocation; // اولین بار مقداردهی کن
                            }

//                            if (lastLocation == null) {
//                                lastLocation = broker_dbh.getLastSavedLocation(); // متدی که آخرین مختصات رو از SQLite می‌خونه
//                            }
                            float distance = lastLocation.distanceTo(currentLocation); // فاصله برحسب متر
                            String datetime = calendar1.getPersianShortDateTime();
                            broker_dbh.UpdateLocationService(locationResult, datetime);
                            broker_dbh.UpdateLocationService_New(locationResult, datetime,String.valueOf(distance));
                            callMethod.Log("Location Updated: " + datetime + " | Distance: " + distance);
                            lastLocation = currentLocation; // موقعیت جدید رو ذخیره کن



                            if (distance > 5) {

                            }else{
                                callMethod.Log("  Distance: " + distance);
                            }
                        } catch (Exception ignored) { }
                    }


//
//                    if (hour > 7 && hour < 23) {
//                        try {
//                            String datetime = calendar1.getPersianShortDateTime();
//                            broker_dbh.UpdateLocationService(locationResult, datetime);
//                            broker_dbh.UpdateLocationService_New(locationResult, datetime);
//                            callMethod.Log("Location Updated: " + datetime);
//                        }catch (Exception ignored){ }
//                    }
                }
            }
        };

        startLocationUpdates();
        startForegroundServiceWithNotification();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 15 seconds
        locationRequest.setFastestInterval(3000); // 10 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5); // فقط اگر حداقل ۱۰ متر حرکت کرده

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callMethod.Log("Permission not granted");
            stopSelf();
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        callMethod.Log("Started location updates");
    }

    private void startForegroundServiceWithNotification() {
        String channelId = "location_channel_id";
        String channelName = "Location Service";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Channel for Location Service");
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Kowsar service active")
                .setSmallIcon(R.drawable.img_logo_kits_jpg)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSilent(true)
                .build();

        startForeground(Constants.Location_Service_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Already handled in onCreate
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        callMethod.Log("Location updates stopped");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
