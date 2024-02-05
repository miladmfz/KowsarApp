package com.kits.kowsarapp.model;

import com.google.gson.annotations.SerializedName;

public class SellBroker {

    @SerializedName("brokerCode") private String brokerCode;
    @SerializedName("BrokerNameWithoutType") private String BrokerNameWithoutType;


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
    //endregion
}
