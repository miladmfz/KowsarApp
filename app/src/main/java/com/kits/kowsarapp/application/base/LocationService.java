package com.kits.kowsarapp.application.base;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
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
    private final PersianCalendar calendar1 = new PersianCalendar();
    private Location lastLocation;
    private boolean stopping;

    @Override
    public void onCreate() {
        super.onCreate();
        callMethod = new CallMethod(getApplicationContext());
        if (!startForegroundServiceWithNotification()) return;

        String databaseName = callMethod.ReadString("DatabaseName");
        if (databaseName == null || databaseName.trim().isEmpty()) {
            callMethod.Log("Location service stopped: profile database is unavailable");
            stopSafely();
            return;
        }

        try {
            broker_dbh = new Broker_DBH(getApplicationContext(), databaseName);
            fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(this);
            locationCallback = createLocationCallback();
            startLocationUpdates();
        } catch (SQLiteException | IllegalArgumentException | IllegalStateException |
                 SecurityException exception) {
            ReleaseLog.error("LocationServiceInit", exception);
            stopSafely();
        }
    }

    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                handleLocationResult(locationResult);
            }
        };
    }

    private void handleLocationResult(LocationResult locationResult) {
        if (stopping || callMethod == null || broker_dbh == null) return;
        Location currentLocation = locationResult.getLastLocation();
        String serverUrl = callMethod.ReadString("ServerURLUse");
        if (currentLocation == null || serverUrl == null || serverUrl.trim().isEmpty()) return;

        calendar1.setTimeInMillis((currentLocation.getTime() + 12600000L) - 86400000L);
        int hour = calendar1.get(Calendar.HOUR_OF_DAY);
        if (hour <= 7 || hour >= 23) return;

        float distance = distanceFromPrevious(lastLocation, currentLocation);
        try {
            String datetime = calendar1.getPersianShortDateTime();
            broker_dbh.UpdateLocationService(locationResult, datetime);
            broker_dbh.UpdateLocationService_New(
                    locationResult, datetime, String.valueOf(distance));
            lastLocation = new Location(currentLocation);
            callMethod.Log("Location Updated: " + datetime + " | Distance: " + distance);
        } catch (SQLiteException | IllegalArgumentException | IllegalStateException |
                 SecurityException exception) {
            ReleaseLog.error("LocationPersistence", exception);
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setFastestInterval(3000); // 3 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5); // at least 5 meters

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callMethod.Log("Permission not granted");
            stopSafely();
            return;
        }

        fusedLocationProviderClient
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .addOnSuccessListener(unused -> callMethod.Log("Started location updates"))
                .addOnFailureListener(exception -> {
                    ReleaseLog.error("LocationRequest", exception);
                    stopSafely();
                });
    }

    private boolean startForegroundServiceWithNotification() {
        String channelId = "location_channel_id";
        String channelName = "Location Service";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (notificationManager != null &&
                    notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel channel = new NotificationChannel(
                        channelId, channelName, NotificationManager.IMPORTANCE_LOW);
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
            return true;
        } catch (IllegalArgumentException | IllegalStateException | SecurityException exception) {
            ReleaseLog.error("LocationForeground", exception);
            stopSafely();
            return false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Already handled in onCreate
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopping = true;
        if (fusedLocationProviderClient != null && locationCallback != null) {
            try {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            } catch (IllegalArgumentException | IllegalStateException | SecurityException exception) {
                ReleaseLog.error("LocationRemoveUpdates", exception);
            }
        }
        if (broker_dbh != null) {
            broker_dbh.closedb();
            broker_dbh = null;
        }
        if (callMethod != null) callMethod.Log("Location updates stopped");
        super.onDestroy();
    }

    private void stopSafely() {
        stopping = true;
        stopSelf();
    }

    static float distanceFromPrevious(
            @Nullable Location previousLocation,
            @NonNull Location currentLocation
    ) {
        return previousLocation == null ? 0f : previousLocation.distanceTo(currentLocation);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
