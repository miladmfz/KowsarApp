package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class KowsarLocationNew {


    @SerializedName("GpsLocationCode")private String GpsLocationCode;
    @SerializedName("Longitude")private String Longitude;
    @SerializedName("Latitude")private String Latitude;
    @SerializedName("BrokerRef")private String BrokerRef;
    @SerializedName("GpsDate")private String GpsDate;


    @SerializedName("Speed")private String Speed;
    @SerializedName("Accuracy")private String Accuracy;
    @SerializedName("NextGpsDate")private String NextGpsDate;
    @SerializedName("DurationInSeconds")private String DurationInSeconds;
    @SerializedName("Status")private String Status;
    @SerializedName("LocationDescription")private String LocationDescription;


    public String getGpsLocationCode() {
        return GpsLocationCode;
    }

    public void setGpsLocationCode(String gpsLocationCode) {
        GpsLocationCode = gpsLocationCode;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getBrokerRef() {
        return BrokerRef;
    }

    public void setBrokerRef(String brokerRef) {
        BrokerRef = brokerRef;
    }

    public String getGpsDate() {
        return GpsDate;
    }

    public void setGpsDate(String gpsDate) {
        GpsDate = gpsDate;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public String getAccuracy() {
        return Accuracy;
    }

    public void setAccuracy(String accuracy) {
        Accuracy = accuracy;
    }

    public String getNextGpsDate() {
        return NextGpsDate;
    }

    public void setNextGpsDate(String nextGpsDate) {
        NextGpsDate = nextGpsDate;
    }

    public String getDurationInSeconds() {
        return DurationInSeconds;
    }

    public void setDurationInSeconds(String durationInSeconds) {
        DurationInSeconds = durationInSeconds;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getLocationDescription() {
        return LocationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        LocationDescription = locationDescription;
    }
}
