package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class Location {


    @SerializedName("GpsLocationCode")private String GpsLocationCode;
    @SerializedName("Longitude")private String Longitude;
    @SerializedName("Latitude")private String Latitude;
    @SerializedName("BrokerRef")private String BrokerRef;
    @SerializedName("GpsDate")private String GpsDate;



    //region $ getter setter

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
    //endregion

}
