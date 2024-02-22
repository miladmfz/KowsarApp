package com.kits.kowsarapp.model.base;


import com.google.gson.annotations.SerializedName;

public class PFheader {
    @SerializedName("PreFactorCode")
    private String PreFactorCode;

    @SerializedName("PreFactorDate")
    private String PreFactorDate;

    @SerializedName("PreFactorExplain")
    private String PreFactorExplain;
    @SerializedName("CustomerRef")

    private String CustomerRef;
    @SerializedName("BrokerRef")

    private String BrokerRef;
    @SerializedName("rwCount")

    private String rwCount;

    // Getters and setters


    public String getPreFactorCode() {
        return PreFactorCode;
    }

    public void setPreFactorCode(String preFactorCode) {
        PreFactorCode = preFactorCode;
    }

    public String getPreFactorDate() {
        return PreFactorDate;
    }

    public void setPreFactorDate(String preFactorDate) {
        PreFactorDate = preFactorDate;
    }

    public String getPreFactorExplain() {
        return PreFactorExplain;
    }

    public void setPreFactorExplain(String preFactorExplain) {
        PreFactorExplain = preFactorExplain;
    }

    public String getCustomerRef() {
        return CustomerRef;
    }

    public void setCustomerRef(String customerRef) {
        CustomerRef = customerRef;
    }

    public String getBrokerRef() {
        return BrokerRef;
    }

    public void setBrokerRef(String brokerRef) {
        BrokerRef = brokerRef;
    }

    public String getRwCount() {
        return rwCount;
    }

    public void setRwCount(String rwCount) {
        this.rwCount = rwCount;
    }
}

