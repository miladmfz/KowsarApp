package com.kits.kowsarapp.model.ocr;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Ocr_Good implements Serializable {




    @SerializedName("GoodCode")private String GoodCode;
    @SerializedName("GoodMaxSellPrice")private String GoodMaxSellPrice;
    @SerializedName("FactorRowCode")private String FactorRowCode;
    @SerializedName("GoodName")private String GoodName;
    @SerializedName("Price")private String Price;
    @SerializedName("FacAmount")private String FacAmount;
    @SerializedName("GoodExplain4")private String GoodExplain4;
    @SerializedName("AppRowIsControled")private String AppRowIsControled;
    @SerializedName("AppRowIsPacked")private String AppRowIsPacked;
    @SerializedName("AppOCRFactorRowCode")private String AppOCRFactorRowCode;
    @SerializedName("ShortageAmount")private String ShortageAmount;
    @SerializedName("CachedBarCode")private String CachedBarCode;

    @SerializedName("StackLocation")
    private String StackLocation;

    @SerializedName("FormNo")
    private String FormNo;
    @SerializedName("TotalAvailable")
    private String TotalAvailable;

    @SerializedName("size")
    private String size;

    @SerializedName("CoverType")
    private String CoverType;

    @SerializedName("PageNo")
    private String PageNo;



    @SerializedName("SumPrice")
    private String SumPrice;



    @SerializedName("ErrCode")
    private String ErrCode;

    @SerializedName("ErrMessage")
    private String ErrMessage;


    @SerializedName("GoodImageName")
    private String GoodImageName;



    @SerializedName("Amount")
    private String Amount;

    @SerializedName("RowCode")
    private String RowCode;
    @SerializedName("Explain")
    private String Explain;


    @SerializedName("ErrDesc")
    private String ErrDesc;

    @SerializedName("SumFacAmount")
    private String SumFacAmount;

    @SerializedName("CountGood")
    private String CountGood;

    @SerializedName("PreFactorCode")
    private String PreFactorCode;

    @SerializedName("FactorCode")
    private String FactorCode;

    @SerializedName("AppBasketInfoRef")
    private String AppBasketInfoRef;


    @SerializedName("AppBasketInfoCode")
    private String AppBasketInfoCode;


    @SerializedName("InfoState")
    private String InfoState;
    @SerializedName("StackAmount")
    private String StackAmount;

    @SerializedName("MaxSellPrice")
    private String MaxSellPrice;
    @SerializedName("GoodExplain2")
    private String GoodExplain2;


    public String getStackLocation() {
        return StackLocation;
    }

    public void setStackLocation(String stackLocation) {
        StackLocation = stackLocation;
    }

    public String getFormNo() {
        return FormNo;
    }

    public void setFormNo(String formNo) {
        FormNo = formNo;
    }

    public String getStackAmount() {
        return StackAmount;
    }

    public void setStackAmount(String stackAmount) {
        StackAmount = stackAmount;
    }

    public String getMaxSellPrice() {
        return MaxSellPrice;
    }

    public void setMaxSellPrice(String maxSellPrice) {
        MaxSellPrice = maxSellPrice;
    }

    public String getGoodExplain2() {
        return GoodExplain2;
    }

    public void setGoodExplain2(String goodExplain2) {
        GoodExplain2 = goodExplain2;
    }

    public String getTotalAvailable() {
        return TotalAvailable;
    }

    public void setTotalAvailable(String totalAvailable) {
        TotalAvailable = totalAvailable;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCoverType() {
        return CoverType;
    }

    public void setCoverType(String coverType) {
        CoverType = coverType;
    }

    public String getPageNo() {
        return PageNo;
    }

    public void setPageNo(String pageNo) {
        PageNo = pageNo;
    }

    public String getSumPrice() {
        return SumPrice;
    }

    public void setSumPrice(String sumPrice) {
        SumPrice = sumPrice;
    }

    public String getErrCode() {
        return ErrCode;
    }

    public void setErrCode(String errCode) {
        ErrCode = errCode;
    }

    public String getErrMessage() {
        return ErrMessage;
    }

    public void setErrMessage(String errMessage) {
        ErrMessage = errMessage;
    }

    public String getGoodImageName() {
        return GoodImageName;
    }

    public void setGoodImageName(String goodImageName) {
        GoodImageName = goodImageName;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getRowCode() {
        return RowCode;
    }

    public void setRowCode(String rowCode) {
        RowCode = rowCode;
    }

    public String getExplain() {
        return Explain;
    }

    public void setExplain(String explain) {
        Explain = explain;
    }

    public String getErrDesc() {
        return ErrDesc;
    }

    public void setErrDesc(String errDesc) {
        ErrDesc = errDesc;
    }

    public String getSumFacAmount() {
        return SumFacAmount;
    }

    public void setSumFacAmount(String sumFacAmount) {
        SumFacAmount = sumFacAmount;
    }

    public String getCountGood() {
        return CountGood;
    }

    public void setCountGood(String countGood) {
        CountGood = countGood;
    }

    public String getPreFactorCode() {
        return PreFactorCode;
    }

    public void setPreFactorCode(String preFactorCode) {
        PreFactorCode = preFactorCode;
    }

    public String getFactorCode() {
        return FactorCode;
    }

    public void setFactorCode(String factorCode) {
        FactorCode = factorCode;
    }

    public String getAppBasketInfoRef() {
        return AppBasketInfoRef;
    }

    public void setAppBasketInfoRef(String appBasketInfoRef) {
        AppBasketInfoRef = appBasketInfoRef;
    }

    public String getAppBasketInfoCode() {
        return AppBasketInfoCode;
    }

    public void setAppBasketInfoCode(String appBasketInfoCode) {
        AppBasketInfoCode = appBasketInfoCode;
    }

    public String getInfoState() {
        return InfoState;
    }

    public void setInfoState(String infoState) {
        InfoState = infoState;
    }

    public String getGoodCode() {
        return GoodCode;
    }

    public void setGoodCode(String goodCode) {
        GoodCode = goodCode;
    }

    public String getGoodMaxSellPrice() {
        return GoodMaxSellPrice;
    }

    public void setGoodMaxSellPrice(String goodMaxSellPrice) {
        GoodMaxSellPrice = goodMaxSellPrice;
    }

    public String getFactorRowCode() {
        return FactorRowCode;
    }

    public void setFactorRowCode(String factorRowCode) {
        FactorRowCode = factorRowCode;
    }

    public String getGoodName() {
        return GoodName;
    }

    public void setGoodName(String goodName) {
        GoodName = goodName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getFacAmount() {
        return FacAmount;
    }

    public void setFacAmount(String facAmount) {
        FacAmount = facAmount;
    }

    public String getGoodExplain4() {
        return GoodExplain4;
    }

    public void setGoodExplain4(String goodExplain4) {
        GoodExplain4 = goodExplain4;
    }

    public String getAppRowIsControled() {
        return AppRowIsControled;
    }

    public void setAppRowIsControled(String appRowIsControled) {
        AppRowIsControled = appRowIsControled;
    }

    public String getAppRowIsPacked() {
        return AppRowIsPacked;
    }

    public void setAppRowIsPacked(String appRowIsPacked) {
        AppRowIsPacked = appRowIsPacked;
    }

    public String getAppOCRFactorRowCode() {
        return AppOCRFactorRowCode;
    }

    public void setAppOCRFactorRowCode(String appOCRFactorRowCode) {
        AppOCRFactorRowCode = appOCRFactorRowCode;
    }

    public String getShortageAmount() {
        if (ShortageAmount == null) {ShortageAmount = "0";}
        else if (ShortageAmount.equals("")) {ShortageAmount = "0";}

        return ShortageAmount;

    }

    public void setShortageAmount(String shortageAmount) {
        ShortageAmount = shortageAmount;
    }

    public String getCachedBarCode() {
        return CachedBarCode;
    }

    public void setCachedBarCode(String cachedBarCode) {
        CachedBarCode = cachedBarCode;
    }
}
