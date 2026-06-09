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
    @SerializedName("AppRowIsControled")private String AppRowIsControled;
    @SerializedName("AppRowIsPacked")private String AppRowIsPacked;
    @SerializedName("AppOCRFactorRowCode")private String AppOCRFactorRowCode;
    @SerializedName("ShortageAmount")private String ShortageAmount;
    @SerializedName("CachedBarCode")private String CachedBarCode;
    @SerializedName("BarCodePrintState")private String BarCodePrintState;
    @SerializedName("GoodRef")private String GoodRef;




    @SerializedName("StackEnumerationRowCode")private String StackEnumerationRowCode	;
    @SerializedName("FirstNumeration")private String FirstNumeration	;
    @SerializedName("Aux11")private String Aux11	;
    @SerializedName("Aux12")private String Aux12	;
    @SerializedName("Aux13")private String Aux13	;
    @SerializedName("rwno")private String rwno;



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

    @SerializedName("GoodExplain1")private String GoodExplain1;
    @SerializedName("GoodExplain2")private String GoodExplain2;
    @SerializedName("GoodExplain3")private String GoodExplain3;
    @SerializedName("GoodExplain4")private String GoodExplain4;
    @SerializedName("GoodExplain5")private String GoodExplain5;
    @SerializedName("GoodExplain6")private String GoodExplain6;

    @SerializedName("CountedAmount1")private String CountedAmount1;
    @SerializedName("CountedAmount2")private String CountedAmount2;
    @SerializedName("CountedAmount3")private String CountedAmount3;

    @SerializedName("MaxsellPrice")private String MaxsellPrice;
    @SerializedName("GoodMainCode")private String GoodMainCode;
    @SerializedName("GoodSubCode")private String GoodSubCode;


    @SerializedName("Auxn11")private String Auxn11;
    @SerializedName("Auxn12")private String Auxn12;
    @SerializedName("Auxn13")private String Auxn13;

    @SerializedName("Num1")private String Num1;
    @SerializedName("Num2")private String Num2;
    @SerializedName("Num3")private String Num3;


    @SerializedName("StackEnumerationRef")private String StackEnumerationRef;
    @SerializedName("LocationRef")private String LocationRef;
    @SerializedName("LocationStackCode")private String LocationStackCode;


    public String getStackEnumerationRef() {
        return StackEnumerationRef;
    }

    public void setStackEnumerationRef(String stackEnumerationRef) {
        StackEnumerationRef = stackEnumerationRef;
    }

    public String getLocationRef() {
        return LocationRef;
    }

    public void setLocationRef(String locationRef) {
        LocationRef = locationRef;
    }

    public String getLocationStackCode() {
        return LocationStackCode;
    }

    public void setLocationStackCode(String locationStackCode) {
        LocationStackCode = locationStackCode;
    }

    public String getNum1() {
        return Num1;
    }

    public void setNum1(String num1) {
        Num1 = num1;
    }

    public String getNum2() {
        return Num2;
    }

    public void setNum2(String num2) {
        Num2 = num2;
    }

    public String getNum3() {
        return Num3;
    }

    public void setNum3(String num3) {
        Num3 = num3;
    }

    public String getGoodRef() {
        return GoodRef;
    }

    public void setGoodRef(String goodRef) {
        GoodRef = goodRef;
    }

    public String getAuxn11() {
        return Auxn11;
    }

    public void setAuxn11(String auxn11) {
        Auxn11 = auxn11;
    }

    public String getAuxn12() {
        return Auxn12;
    }

    public void setAuxn12(String auxn12) {
        Auxn12 = auxn12;
    }

    public String getAuxn13() {
        return Auxn13;
    }

    public void setAuxn13(String auxn13) {
        Auxn13 = auxn13;
    }

    public String getMaxsellPrice() {

        if (MaxsellPrice != null)
        {
            try{
                return  MaxsellPrice.substring(0,MaxsellPrice.indexOf("."));

            }
            catch (Exception e){
                return  MaxsellPrice;
            }

        }else {
            return "";
        }
    }

    public void setMaxsellPrice(String maxsellPrice) {
        MaxsellPrice = maxsellPrice;
    }

    public String getGoodMainCode() {
        return GoodMainCode;
    }

    public void setGoodMainCode(String goodMainCode) {
        GoodMainCode = goodMainCode;
    }

    public String getGoodSubCode() {
        return GoodSubCode;
    }

    public void setGoodSubCode(String goodSubCode) {
        GoodSubCode = goodSubCode;
    }

    public String getStackEnumerationRowCode() {
        return StackEnumerationRowCode;
    }

    public void setStackEnumerationRowCode(String stackEnumerationRowCode) {
        StackEnumerationRowCode = stackEnumerationRowCode;
    }

    public String getFirstNumeration() {
        return FirstNumeration;
    }

    public void setFirstNumeration(String firstNumeration) {
        FirstNumeration = firstNumeration;
    }

    public String getAux11() {
        return Aux11;
    }

    public void setAux11(String aux11) {
        Aux11 = aux11;
    }

    public String getAux12() {
        return Aux12;
    }

    public void setAux12(String aux12) {
        Aux12 = aux12;
    }

    public String getAux13() {
        return Aux13;
    }

    public void setAux13(String aux13) {
        Aux13 = aux13;
    }

    public String getRwno() {
        return rwno;
    }

    public void setRwno(String rwno) {
        this.rwno = rwno;
    }
    public String getCountedAmount1() {
        return CountedAmount1;
    }

    public void setCountedAmount1(String countedAmount1) {
        CountedAmount1 = countedAmount1;
    }

    public String getCountedAmount2() {
        return CountedAmount2;
    }

    public void setCountedAmount2(String countedAmount2) {
        CountedAmount2 = countedAmount2;
    }

    public String getCountedAmount3() {
        return CountedAmount3;
    }

    public void setCountedAmount3(String countedAmount3) {
        CountedAmount3 = countedAmount3;
    }

    @SerializedName("MinAmount")
    private String MinAmount;

    private int checkBoxId;

    public int getCheckBoxId() {
        return checkBoxId;
    }

    public void setCheckBoxId(int checkBoxId) {
        this.checkBoxId = checkBoxId;
    }

    public String getGoodExplain1() {
        return GoodExplain1;
    }

    public void setGoodExplain1(String goodExplain1) {
        GoodExplain1 = goodExplain1;
    }

    public String getGoodExplain3() {
        return GoodExplain3;
    }

    public void setGoodExplain3(String goodExplain3) {
        GoodExplain3 = goodExplain3;
    }

    public String getGoodExplain5() {
        return GoodExplain5;
    }

    public void setGoodExplain5(String goodExplain5) {
        GoodExplain5 = goodExplain5;
    }

    public String getGoodExplain6() {
        return GoodExplain6;
    }

    public void setGoodExplain6(String goodExplain6) {
        GoodExplain6 = goodExplain6;
    }

    public String getBarCodePrintState() {
        return BarCodePrintState;
    }

    public void setBarCodePrintState(String barCodePrintState) {
        BarCodePrintState = barCodePrintState;
    }

    public String getMinAmount() {


        if (MinAmount != null)
        {
            try{
                return  MinAmount.substring(0,MinAmount.indexOf("."));

            }
            catch (Exception e){
                return  MinAmount;
            }

        }else {
            return "";
        }

    }

    public void setMinAmount(String minAmount) {
        MinAmount = minAmount;
    }

    public String getStackLocation() {
        return StackLocation;
    }

    public void setStackLocation(String stackLocation) {
        StackLocation = stackLocation;
    }

    public String getFormNo() {

        if (FormNo != null)
        {
            try {
                return  FormNo.substring(0,FormNo.indexOf("."));
            }catch (Exception e){
                return  FormNo;
            }

        }else {
            return "";
        }

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

        if (MaxSellPrice != null)
        {
            try{
                return  MaxSellPrice.substring(0,MaxSellPrice.indexOf("."));

            }
            catch (Exception e){
                return  MaxSellPrice;
            }

        }else {
            return "";
        }


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
        if (GoodMaxSellPrice != null)
        {
            try{
                return  GoodMaxSellPrice.substring(0,GoodMaxSellPrice.indexOf("."));

            }
            catch (Exception e){
                return  GoodMaxSellPrice;
            }

        }else {
            return "";
        }


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
        if (FacAmount != null)
        {

            try{
                return  FacAmount.substring(0,FacAmount.indexOf("."));

            }
            catch (Exception e){
                return  FacAmount;
            }


        }else {
            return "";
        }
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
