package com.kits.kowsarapp.model;

import com.google.gson.annotations.SerializedName;

public class PreFactor {

    @SerializedName("PreFactorCode")
    private Integer PreFactorCode;
    @SerializedName("PreFactorRef")
    private Integer PreFactorRef;
    @SerializedName("PreFactorRowCode")
    private Integer PreFactorRowCode;
    @SerializedName("PreFactorKowsarCode")
    private Integer PreFactorKowsarCode;
    @SerializedName("PreFactorDate")
    private String PreFactorDate;
    @SerializedName("PreFactorTime")
    private String PreFactorTime;
    @SerializedName("PreFactorkowsarDate")
    private String PreFactorkowsarDate;
    @SerializedName("PreFactorExplain")
    private String PreFactorExplain;
    @SerializedName("Customer")
    private String Customer;
    @SerializedName("SumPrice")
    private long SumPrice;
    @SerializedName("SumAmount")
    private Integer SumAmount;
    @SerializedName("RowCount")
    private Integer RowCount;
    @SerializedName("Act")
    private Integer Act;
    @SerializedName("LastCode")
    private String LastCode;
    @SerializedName("LastFactor")
    private String LastFactor;




    //region $ getter setter
    public String getPreFactorFieldValue(String AKey) {

        String iKey = AKey.toLowerCase();
        String Res = "";
        switch (iKey) {
            case "prefactorcode":
                if (PreFactorCode == null) Res = "";
                else Res = PreFactorCode.toString();
                break;
            case "prefactorref":
                if (PreFactorRef == null) Res = "";
                else Res = PreFactorRef.toString();
                break;
            case "prefactorrowcode":
                if (PreFactorRowCode == null) Res = "";
                else Res = PreFactorRowCode.toString();
                break;
            case "prefactorkowsarcode":
                if (PreFactorKowsarCode == null) Res = "";
                else Res = PreFactorKowsarCode.toString();
                break;
            case "prefactordate":
                if (PreFactorDate == null) Res = "";
                else Res = PreFactorDate;
                break;
            case "prefactortime":
                if (PreFactorTime == null) Res = "";
                else Res = PreFactorTime;
                break;
            case "prefactorkowsardate":
                if (PreFactorkowsarDate == null) Res = "";
                else Res = PreFactorkowsarDate;
                break;
            case "prefactorexplain":
                if (PreFactorExplain == null) Res = "";
                else Res = PreFactorExplain;
                break;
            case "customer":
                if (Customer == null) Res = "";
                else Res = Customer;
                break;
            case "sumprice":
                Res = String.valueOf(SumPrice);
                break;
            case "sumamount":
                if (SumAmount == null) Res = "";
                else Res = SumAmount.toString();
                break;
            case "rowcount":
                if (RowCount == null) Res = "";
                else Res = RowCount.toString();
                break;
            case "act":
                if (Act == null) Res = "";
                else Res = Act.toString();
                break;
            case "lastcode":
                if (LastCode == null) Res = "";
                else Res = LastCode;
                break;
            case "lastfactor":
                if (LastFactor == null) Res = "";
                else Res = LastFactor;
                break;
        }
        return Res;

    }


    public void setPreFactorCode(Integer preFactorCode) {
        PreFactorCode = preFactorCode;
    }

    public void setPreFactorKowsarCode(Integer preFactorKowsarCode) {
        PreFactorKowsarCode = preFactorKowsarCode;
    }

    public void setPreFactorDate(String preFactorDate) {
        PreFactorDate = preFactorDate;
    }

    public void setPreFactorTime(String preFactorTime) {
        PreFactorTime = preFactorTime;
    }

    public void setPreFactorkowsarDate(String preFactorkowsarDate) {
        PreFactorkowsarDate = preFactorkowsarDate;
    }

    public void setPreFactorExplain(String preFactorExplain) {
        PreFactorExplain = preFactorExplain;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public void setSumPrice(long sumPrice) {
        SumPrice = sumPrice;
    }

    public void setSumAmount(Integer sumAmount) {
        SumAmount = sumAmount;
    }

    public void setRowCount(Integer rowCount) {
        RowCount = rowCount;
    }

    public void setAct(Integer act) {
        Act = act;
    }

    public void setLastCode(String lastCode) {
        LastCode = lastCode;
    }

    public void setLastFactor(String lastFactor) {
        LastFactor = lastFactor;
    }

    public void setPreFactorRef(Integer preFactorRef) {
        PreFactorRef = preFactorRef;
    }

    public void setPreFactorRowCode(Integer preFactorRowCode) {
        PreFactorRowCode = preFactorRowCode;
    }

    //endregion

}
