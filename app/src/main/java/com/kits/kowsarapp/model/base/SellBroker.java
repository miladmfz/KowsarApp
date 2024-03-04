package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class SellBroker {

    @SerializedName("brokerCode") private String brokerCode;
    @SerializedName("BrokerNameWithoutType") private String BrokerNameWithoutType;
    @SerializedName("CentralRef") private String CentralRef;
    @SerializedName("Active") private String Active;



    //region $ getter setter


    public String getBrokerCode() {
        return brokerCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public String getBrokerNameWithoutType() {
        return BrokerNameWithoutType;
    }

    public void setBrokerNameWithoutType(String brokerNameWithoutType) {
        BrokerNameWithoutType = brokerNameWithoutType;
    }

    public String getCentralRef() {
        return CentralRef;
    }

    public void setCentralRef(String centralRef) {
        CentralRef = centralRef;
    }

    public String getActive() {
        return Active;
    }

    public void setActive(String active) {
        Active = active;
    }
    //endregion
}
