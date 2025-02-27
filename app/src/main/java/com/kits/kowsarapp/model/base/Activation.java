package com.kits.kowsarapp.model.base;

import android.annotation.SuppressLint;

import com.google.gson.annotations.SerializedName;
import com.kits.kowsarapp.application.base.App;

public class Activation {

    @SerializedName("AppBrokerCustomerCode")
    private String AppBrokerCustomerCode;
    @SerializedName("ActivationCode")
    private String ActivationCode;
    @SerializedName("PersianCompanyName")
    private String PersianCompanyName;
    @SerializedName("EnglishCompanyName")
    private String EnglishCompanyName;
    @SerializedName("ServerURL")
    private String ServerURL;
    @SerializedName("SQLiteURL")
    private String SQLiteURL;
    @SerializedName("MaxDevice")
    private String MaxDevice;


    @SerializedName("SecendServerURL")
    private String SecendServerURL;
    @SerializedName("DbName")
    private String DbName;
    @SerializedName("AppType")
    private String AppType;


    @SerializedName("ServerIp")
    private String ServerIp;
    @SerializedName("ServerPort")
    private String ServerPort;
    @SerializedName("ServerPathApi")
    private String ServerPathApi;
    @SerializedName("UsedDevice")
    private String UsedDevice;

    //region $ getter setter


    @SerializedName("ErrCode")
    private String ErrCode;
    @SerializedName("ErrDesc")
    private String ErrDesc;


    public String getServerIp() {
        return ServerIp;
    }

    public void setServerIp(String serverIp) {
        ServerIp = serverIp;
    }

    public String getServerPort() {
        return ServerPort;
    }

    public void setServerPort(String serverPort) {
        ServerPort = serverPort;
    }

    public String getServerPathApi() {
        return ServerPathApi;
    }

    public void setServerPathApi(String serverPathApi) {
        ServerPathApi = serverPathApi;
    }

    public String getUsedDevice() {
        return UsedDevice;
    }

    public void setUsedDevice(String usedDevice) {
        UsedDevice = usedDevice;
    }

    public String getErrCode() {
        return ErrCode;
    }

    public void setErrCode(String errCode) {
        ErrCode = errCode;
    }

    public String getErrDesc() {
        return ErrDesc;
    }

    public void setErrDesc(String errDesc) {
        ErrDesc = errDesc;
    }

    public String getSecendServerURL() {
        return SecendServerURL;
    }

    public void setSecendServerURL(String secendServerURL) {
        SecendServerURL = secendServerURL;
    }

    public String getDbName() {
        return DbName;
    }

    public void setDbName(String dbName) {
        DbName = dbName;
    }

    public String getAppType() {
        return AppType;
    }

    public void setAppType(String appType) {
        AppType = appType;
    }

    @SuppressLint("SdCardPath")
    public String getDatabaseFolderPath() {
        return "/data/data/" + App.getContext().getPackageName() + "/databases/" + EnglishCompanyName;
    }


    @SuppressLint("SdCardPath")
    public String getDatabaseFilePath() {
        return "/data/data/" + App.getContext().getPackageName() + "/databases/" + EnglishCompanyName + "/KowsarDb.sqlite";
    }

    public String getAppBrokerCustomerCode() {
        return AppBrokerCustomerCode;
    }

    public void setAppBrokerCustomerCode(String appBrokerCustomerCode) {
        AppBrokerCustomerCode = appBrokerCustomerCode;
    }

    public String getActivationCode() {
        return ActivationCode;
    }

    public void setActivationCode(String activationCode) {
        ActivationCode = activationCode;
    }

    public String getPersianCompanyName() {
        return PersianCompanyName;
    }

    public void setPersianCompanyName(String persianCompanyName) {
        PersianCompanyName = persianCompanyName;
    }

    public String getEnglishCompanyName() {
        return EnglishCompanyName;
    }

    public void setEnglishCompanyName(String englishCompanyName) {
        EnglishCompanyName = englishCompanyName;
    }

    public String getServerURL() {
        return ServerURL;
    }

    public void setServerURL(String serverURL) {
        ServerURL = serverURL;
    }

    public String getSQLiteURL() {
        return SQLiteURL;
    }

    public void setSQLiteURL(String SQLiteURL) {
        this.SQLiteURL = SQLiteURL;
    }

    public String getMaxDevice() {
        return MaxDevice;
    }

    public void setMaxDevice(String maxDevice) {
        MaxDevice = maxDevice;
    }
    //endregion

}
