package com.kits.kowsarapp.model.order;

import com.google.gson.annotations.SerializedName;

public class Order_BasketInfo {


    @SerializedName("RstmizCode")
    private String RstmizCode;
    @SerializedName("RstMizName")
    private String RstMizName;
    @SerializedName("Explain")
    private String Explain;
    @SerializedName("RstActive")
    private String RstActive;
    @SerializedName("PlaceCount")
    private String PlaceCount;
    @SerializedName("MizPrice")
    private String MizPrice;
    @SerializedName("AppBasketInfoCode")
    private String AppBasketInfoCode;
    @SerializedName("TimeStart")
    private String TimeStart;
    @SerializedName("InfoExplain")
    private String InfoExplain;
    @SerializedName("InfoState")
    private String InfoState;
    @SerializedName("BrokerName")
    private String BrokerName;
    @SerializedName("Res_AppBasketInfoCode")
    private String Reserve_AppBasketInfoCode;
    @SerializedName("ReserveStart")
    private String ReserveStart;
    @SerializedName("ReserveEnd")
    private String ReserveEnd;
    @SerializedName("PersonName")
    private String PersonName;
    @SerializedName("MobileNo")
    private String MobileNo;
    @SerializedName("Res_BrokerName")
    private String Res_BrokerName;
    @SerializedName("IsReserved")
    private String IsReserved;
    @SerializedName("MizType")
    private String MizType;
    @SerializedName("BrokerRef")
    private String BrokerRef;
    @SerializedName("RstMizRef")
    private String RstMizRef;
    @SerializedName("AppBasketInfoDate")
    private String AppBasketInfoDate;

    @SerializedName("ErrCode")
    private String ErrCode;

    @SerializedName("ErrDesc")
    private String ErrDesc;

    @SerializedName("Today")
    private String Today;
    @SerializedName("PreFactorCode")
    private String PreFactorCode;

    @SerializedName("FactorCode")
    private String FactorCode;

    @SerializedName("Prepayed")
    private String Prepayed;
    @SerializedName("Time")
    private String Time;

    @SerializedName("SumTaxAndMayor")
    private String SumTaxAndMayor;
    @SerializedName("SumFacAmount")    private String SumFacAmount;
    @SerializedName("SumPrice")    private String SumPrice;
    @SerializedName("CountGood")    private String CountGood;
    @SerializedName("NewSumPrice")    private String NewSumPrice;
    @SerializedName("Received")    private String Received;
    @SerializedName("NotReceived")    private String NotReceived;
    @SerializedName("DecrementValue")    private String DecrementValue;
    @SerializedName("ShopNaghdReceive")    private String ShopNaghdReceive;

    public String getSumTaxAndMayor() {
        return SumTaxAndMayor;
    }

    public void setSumTaxAndMayor(String sumTaxAndMayor) {
        SumTaxAndMayor = sumTaxAndMayor;
    }

    public String getSumFacAmount() {
        return SumFacAmount;
    }

    public void setSumFacAmount(String sumFacAmount) {
        SumFacAmount = sumFacAmount;
    }

    public String getSumPrice() {
        return SumPrice;
    }

    public void setSumPrice(String sumPrice) {
        SumPrice = sumPrice;
    }

    public String getCountGood() {
        return CountGood;
    }

    public void setCountGood(String countGood) {
        CountGood = countGood;
    }

    public String getNewSumPrice() {
        return NewSumPrice;
    }

    public void setNewSumPrice(String newSumPrice) {
        NewSumPrice = newSumPrice;
    }

    public String getReceived() {
        return Received;
    }

    public void setReceived(String received) {
        Received = received;
    }

    public String getNotReceived() {
        return NotReceived;
    }

    public void setNotReceived(String notReceived) {
        NotReceived = notReceived;
    }

    public String getDecrementValue() {
        return DecrementValue;
    }

    public void setDecrementValue(String decrementValue) {
        DecrementValue = decrementValue;
    }

    public String getShopNaghdReceive() {
        return ShopNaghdReceive;
    }

    public void setShopNaghdReceive(String shopNaghdReceive) {
        ShopNaghdReceive = shopNaghdReceive;
    }

    public String getTime() {
        if (Time == null) {
            return "00:00";
        } else {
            return Time;
        }
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getPrepayed() {
        return Prepayed;
    }

    public void setPrepayed(String prepayed) {
        Prepayed = prepayed;
    }

    public String getFactorCode() {
        return FactorCode;
    }

    public void setFactorCode(String factorCode) {
        FactorCode = factorCode;
    }

    public String getPreFactorCode() {
        return PreFactorCode;
    }

    public void setPreFactorCode(String preFactorCode) {
        PreFactorCode = preFactorCode;
    }

    public String getRstmizCode() {
        if (RstmizCode == null) RstmizCode = "";
        return RstmizCode;
    }

    public void setRstmizCode(String rstmizCode) {
        RstmizCode = rstmizCode;
    }

    public String getRstMizName() {

        if (RstMizName == null) RstMizName = "";
        return RstMizName;
    }

    public void setRstMizName(String rstMizName) {
        RstMizName = rstMizName;
    }

    public String getExplain() {

        if (Explain == null) Explain = "";
        return Explain;
    }

    public void setExplain(String explain) {
        Explain = explain;
    }

    public String getRstActive() {

        if (RstActive == null) RstActive = "";
        return RstActive;
    }

    public void setRstActive(String rstActive) {
        RstActive = rstActive;
    }

    public String getPlaceCount() {

        if (PlaceCount == null) PlaceCount = "";
        return PlaceCount;
    }

    public void setPlaceCount(String placeCount) {
        PlaceCount = placeCount;
    }

    public String getMizPrice() {

        if (MizPrice == null) MizPrice = "";
        return MizPrice;
    }

    public void setMizPrice(String mizPrice) {
        MizPrice = mizPrice;
    }

    public String getAppBasketInfoCode() {

        if (AppBasketInfoCode == null) AppBasketInfoCode = "";
        return AppBasketInfoCode;
    }

    public void setAppBasketInfoCode(String appBasketInfoCode) {
        AppBasketInfoCode = appBasketInfoCode;
    }

    public String getTimeStart() {

        if (TimeStart == null) TimeStart = "";
        return TimeStart;
    }

    public void setTimeStart(String timeStart) {
        TimeStart = timeStart;
    }

    public String getInfoExplain() {

        if (InfoExplain == null) InfoExplain = "";
        return InfoExplain;
    }

    public void setInfoExplain(String infoExplain) {
        InfoExplain = infoExplain;
    }

    public String getInfoState() {

        if (InfoState == null) InfoState = "";
        return InfoState;
    }

    public void setInfoState(String infoState) {
        InfoState = infoState;
    }

    public String getBrokerName() {

        if (BrokerName == null) BrokerName = "";
        return BrokerName;
    }

    public void setBrokerName(String brokerName) {
        BrokerName = brokerName;
    }

    public String getReserve_AppBasketInfoCode() {

        if (Reserve_AppBasketInfoCode == null) Reserve_AppBasketInfoCode = "";
        return Reserve_AppBasketInfoCode;
    }

    public void setReserve_AppBasketInfoCode(String reserve_AppBasketInfoCode) {
        Reserve_AppBasketInfoCode = reserve_AppBasketInfoCode;
    }

    public String getReserveStart() {

        if (ReserveStart == null) ReserveStart = "";
        return ReserveStart;
    }

    public void setReserveStart(String reserveStart) {
        ReserveStart = reserveStart;
    }

    public String getReserveEnd() {

        if (ReserveEnd == null) ReserveEnd = "";
        return ReserveEnd;
    }

    public void setReserveEnd(String reserveEnd) {
        ReserveEnd = reserveEnd;
    }

    public String getPersonName() {

        if (PersonName == null) PersonName = "";
        return PersonName;
    }

    public void setPersonName(String personName) {
        PersonName = personName;
    }

    public String getMobileNo() {

        if (MobileNo == null) MobileNo = "";
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getRes_BrokerName() {

        if (Res_BrokerName == null) Res_BrokerName = "";
        return Res_BrokerName;
    }

    public void setRes_BrokerName(String res_BrokerName) {
        Res_BrokerName = res_BrokerName;
    }

    public String getIsReserved() {

        if (IsReserved == null) IsReserved = "";
        return IsReserved;
    }

    public void setIsReserved(String isReserved) {
        IsReserved = isReserved;
    }


    public String getMizType() {
        if (MizType == null) MizType = "";
        return MizType;
    }

    public void setMizType(String mizType) {
        MizType = mizType;
    }

    public String getBrokerRef() {
        if (BrokerRef == null) BrokerRef = "";
        return BrokerRef;
    }

    public void setBrokerRef(String brokerRef) {
        BrokerRef = brokerRef;
    }

    public String getRstMizRef() {
        if (RstMizRef == null) RstMizRef = "";
        return RstMizRef;
    }

    public void setRstMizRef(String rstMizRef) {
        RstMizRef = rstMizRef;
    }

    public String getAppBasketInfoDate() {
        if (AppBasketInfoDate == null) AppBasketInfoDate = "";
        return AppBasketInfoDate;
    }

    public void setAppBasketInfoDate(String appBasketInfoDate) {
        AppBasketInfoDate = appBasketInfoDate;
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

    public String getToday() {
        return Today;
    }

    public void setToday(String today) {
        Today = today;
    }
}










































