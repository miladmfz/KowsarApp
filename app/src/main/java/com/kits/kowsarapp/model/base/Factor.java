package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class Factor {

    @SerializedName("FactorBarcode")
    private String FactorBarcode;
    @SerializedName("FactorPrivateCode")
    private String FactorPrivateCode;

    @SerializedName("FactorCode")
    private String FactorCode;
    @SerializedName("ScanDate")
    private String ScanDate;
    @SerializedName("FactorDate")
    private String FactorDate;
    @SerializedName("FactorExplain")
    private String FactorExplain;
    @SerializedName("CustName")
    private String CustName;
    @SerializedName("CustomerCode")
    private String CustomerCode;
    @SerializedName("CustomerRef")
    private String CustomerRef;
    @SerializedName("SumPrice")
    private String SumPrice;
    @SerializedName("NewSumPrice")
    private String NewSumPrice;
    @SerializedName("SumAmount")
    private String SumAmount;
    @SerializedName("RowCount")
    private String RowCount;
    @SerializedName("IsSent")
    private String IsSent;
    @SerializedName("Deliverer")
    private String Deliverer;
    @SerializedName("Address")
    private String Address;
    @SerializedName("Phone")
    private String Phone;
    @SerializedName("Explain")
    private String Explain;
    @SerializedName("SignatureImage")
    private String SignatureImage;
    @SerializedName("CameraImage")
    private String CameraImage;
    @SerializedName("FactorImage")
    private String FactorImage;
    @SerializedName("AppIsControled")
    private String AppIsControled;
    @SerializedName("AppOCRFactorCode")
    private String AppOCRFactorCode;
    @SerializedName("TotalRow")
    private String TotalRow;
    @SerializedName("MandehBedehkar")
    private String MandehBedehkar;

    @SerializedName("dbname")
    private String dbname;
    @SerializedName("AppOCRFactorExplain")
    private String AppOCRFactorExplain;


    @SerializedName("PreFactorCode")
    private String PreFactorCode;
    @SerializedName("PreFactorDate")
    private String PreFactorDate;
    @SerializedName("Flag")
    private String Flag;
    @SerializedName("GoodCode")
    private String GoodCode;


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

    public String getFlag() {
        return Flag;
    }

    public void setFlag(String flag) {
        Flag = flag;
    }

    public String getGoodCode() {
        return GoodCode;
    }

    public void setGoodCode(String goodCode) {
        GoodCode = goodCode;
    }

    public String getAppOCRFactorExplain() {
        if (AppOCRFactorExplain != null)
        {
            return AppOCRFactorExplain;
        }else {
            return "";
        }

    }

    public void setAppOCRFactorExplain(String appOCRFactorExplain) {
        AppOCRFactorExplain = appOCRFactorExplain;
    }

    public String getMandehBedehkar() {

        if (MandehBedehkar != null)
        {
            try {
                return  MandehBedehkar.substring(0,MandehBedehkar.indexOf("."));
            }catch (Exception e){
                return  MandehBedehkar;
            }

        }else {
            return "";
        }


    }

    public void setMandehBedehkar(String mandehBedehkar) {
        MandehBedehkar = mandehBedehkar;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getTotalRow() {
        return TotalRow;
    }

    public void setTotalRow(String totalRow) {
        TotalRow = totalRow;
    }

    @SerializedName("StackClass")
    private String StackClass;

    @SerializedName("AppIsPacked")
    private String AppIsPacked;

    @SerializedName("AppIsDelivered")
    private String AppIsDelivered;

    @SerializedName("AppTcPrintRef")
    private String AppTcPrintRef;

    @SerializedName("CustomerPath")
    private String CustomerPath;

    @SerializedName("IsEdited")
    private String IsEdited;

    @SerializedName("HasShortage")
    private String HasShortage;

    @SerializedName("HasSignature")
    private String HasSignature;

    @SerializedName("ErrCode")
    private String ErrCode;

    @SerializedName("ErrMessage") private String ErrMessage;
    @SerializedName("AppPackCount") private String AppPackCount;



    @SerializedName("customercode") private String customercode;
    @SerializedName("Ersall") private String Ersall;
    @SerializedName("BrokerName") private String BrokerName;

    @SerializedName("AppFactorRef") private String AppFactorRef;

    @SerializedName("AppControlDate") private String AppControlDate;
    @SerializedName("AppPackDate") private String AppPackDate;
    @SerializedName("AppDeliverDate") private String AppDeliverDate;
    @SerializedName("AppReader") private String AppReader;
    @SerializedName("AppControler") private String AppControler;
    @SerializedName("AppPacker") private String AppPacker;
    @SerializedName("AppPackDeliverDate") private String AppPackDeliverDate;

    @SerializedName("AppDeliverer") private String AppDeliverer;
    @SerializedName("AppBrokerRef") private String AppBrokerRef;

    @SerializedName("AppFactorState") private String AppFactorState;


    @SerializedName("DailyCode")
    private String DailyCode;
    @SerializedName("RstMizName")
    private String RstMizName;

    @SerializedName("FactorRowCode")
    private String FactorRowCode;
    @SerializedName("GoodRef")
    private String GoodRef;
    @SerializedName("FacAmount")
    private String FacAmount;
    @SerializedName("CanPrint")
    private String CanPrint;
    @SerializedName("RowExplain")
    private String RowExplain;

    @SerializedName("GoodName")
    private String GoodName;

    @SerializedName("IsExtra")
    private String IsExtra;

    @SerializedName("TimeStart")
    private String TimeStart;



    @SerializedName("ErrDesc")
    private String ErrDesc;

    @SerializedName("MizType")
    private String MizType;


    @SerializedName("InfoState")
    private String InfoState;


    @SerializedName("ReserveStart")
    private String ReserveStart;


    @SerializedName("InfoPrintCount")
    private String InfoPrintCount;

    @SerializedName("AppBasketInfoDate")
    private String AppBasketInfoDate;


    @SerializedName("InfoExplain")
    private String InfoExplain;

    @SerializedName("SumTax")
    private String SumTax;

    @SerializedName("Moghayerat")
    private String Moghayerat;



    @SerializedName("Check")
    private boolean Check;


    public String getSumTax() {

        if (SumTax != null)
        {
            return SumTax;
        }else {
            return "0";
        }

    }

    public void setSumTax(String sumTax) {
        SumTax = sumTax;
    }

    public String getMoghayerat() {
        return Moghayerat;
    }

    public void setMoghayerat(String moghayerat) {
        Moghayerat = moghayerat;
    }

    public String getDailyCode() {
        return DailyCode;
    }

    public void setDailyCode(String dailyCode) {
        DailyCode = dailyCode;
    }

    public String getRstMizName() {
        return RstMizName;
    }

    public void setRstMizName(String rstMizName) {
        RstMizName = rstMizName;
    }

    public String getFactorRowCode() {
        return FactorRowCode;
    }

    public void setFactorRowCode(String factorRowCode) {
        FactorRowCode = factorRowCode;
    }

    public String getGoodRef() {
        return GoodRef;
    }

    public void setGoodRef(String goodRef) {
        GoodRef = goodRef;
    }

    public String getFacAmount() {
        return FacAmount;
    }

    public void setFacAmount(String facAmount) {
        FacAmount = facAmount;
    }

    public String getCanPrint() {
        return CanPrint;
    }

    public void setCanPrint(String canPrint) {
        CanPrint = canPrint;
    }

    public String getRowExplain() {

        if (RowExplain != null)
        {
            return RowExplain;
        }else {
            return "0";
        }
    }

    public void setRowExplain(String rowExplain) {
        RowExplain = rowExplain;
    }

    public String getGoodName() {
        return GoodName;
    }

    public void setGoodName(String goodName) {
        GoodName = goodName;
    }

    public String getIsExtra() {
        return IsExtra;
    }

    public void setIsExtra(String isExtra) {
        IsExtra = isExtra;
    }

    public String getTimeStart() {
        return TimeStart;
    }

    public void setTimeStart(String timeStart) {
        TimeStart = timeStart;
    }

    public String getErrDesc() {
        return ErrDesc;
    }

    public void setErrDesc(String errDesc) {
        ErrDesc = errDesc;
    }

    public String getMizType() {
        return MizType;
    }

    public void setMizType(String mizType) {
        MizType = mizType;
    }

    public String getInfoState() {
        return InfoState;
    }

    public void setInfoState(String infoState) {
        InfoState = infoState;
    }

    public String getReserveStart() {
        return ReserveStart;
    }

    public void setReserveStart(String reserveStart) {
        ReserveStart = reserveStart;
    }

    public String getInfoPrintCount() {
        return InfoPrintCount;
    }

    public void setInfoPrintCount(String infoPrintCount) {
        InfoPrintCount = infoPrintCount;
    }

    public String getAppBasketInfoDate() {
        return AppBasketInfoDate;
    }

    public void setAppBasketInfoDate(String appBasketInfoDate) {
        AppBasketInfoDate = appBasketInfoDate;
    }

    public String getInfoExplain() {
        return InfoExplain;
    }

    public void setInfoExplain(String infoExplain) {
        InfoExplain = infoExplain;
    }

    public String getAppPackCount() {
        return AppPackCount;
    }

    public void setAppPackCount(String appPackCount) {
        AppPackCount = appPackCount;
    }

    public String getCustomercode() {
        return customercode;
    }

    public void setCustomercode(String customercode) {
        this.customercode = customercode;
    }

    public String getErsall() {
        return Ersall;
    }

    public void setErsall(String ersall) {
        Ersall = ersall;
    }

    public String getBrokerName() {

        if (BrokerName != null)
        {
            return BrokerName;
        }else {
            return "";
        }
    }

    public void setBrokerName(String brokerName) {
        BrokerName = brokerName;
    }

    public String getAppFactorRef() {
        return AppFactorRef;
    }

    public void setAppFactorRef(String appFactorRef) {
        AppFactorRef = appFactorRef;
    }

    public String getAppControlDate() {
        return AppControlDate;
    }

    public void setAppControlDate(String appControlDate) {
        AppControlDate = appControlDate;
    }

    public String getAppPackDate() {
        return AppPackDate;
    }

    public void setAppPackDate(String appPackDate) {
        AppPackDate = appPackDate;
    }

    public String getAppDeliverDate() {
        return AppDeliverDate;
    }

    public void setAppDeliverDate(String appDeliverDate) {
        AppDeliverDate = appDeliverDate;
    }

    public String getAppReader() {
        return AppReader;
    }

    public void setAppReader(String appReader) {
        AppReader = appReader;
    }

    public String getAppControler() {
        return AppControler;
    }

    public void setAppControler(String appControler) {
        AppControler = appControler;
    }

    public String getAppPacker() {
        return AppPacker;
    }

    public void setAppPacker(String appPacker) {
        AppPacker = appPacker;
    }

    public String getAppPackDeliverDate() {
        return AppPackDeliverDate;
    }

    public void setAppPackDeliverDate(String appPackDeliverDate) {
        AppPackDeliverDate = appPackDeliverDate;
    }

    public String getAppDeliverer() {
        return AppDeliverer;
    }

    public void setAppDeliverer(String appDeliverer) {
        AppDeliverer = appDeliverer;
    }

    public String getAppBrokerRef() {
        return AppBrokerRef;
    }

    public void setAppBrokerRef(String appBrokerRef) {
        AppBrokerRef = appBrokerRef;
    }

    public String getAppFactorState() {
        return AppFactorState;
    }

    public void setAppFactorState(String appFactorState) {
        AppFactorState = appFactorState;
    }

    public String getCustomerPath() {
        return CustomerPath;
    }

    public String getStackClass() {
        return StackClass;
    }

    public void setStackClass(String stackClass) {
        StackClass = stackClass;
    }

    public void setCustomerPath(String customerPath) {
        CustomerPath = customerPath;
    }

    public String getIsEdited() {
        return IsEdited;
    }

    public void setIsEdited(String isEdited) {
        IsEdited = isEdited;
    }

    public String getHasShortage() {
        return HasShortage;
    }

    public void setHasShortage(String hasShortage) {
        HasShortage = hasShortage;
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

    public String getAppIsControled() {
        return AppIsControled;
    }

    public void setAppIsControled(String appIsControled) {
        AppIsControled = appIsControled;
    }

    public String getAppIsPacked() {
        return AppIsPacked;
    }

    public void setAppIsPacked(String appIsPacked) {
        AppIsPacked = appIsPacked;
    }

    public String getAppIsDelivered() {
        return AppIsDelivered;
    }

    public void setAppIsDelivered(String appIsDelivered) {
        AppIsDelivered = appIsDelivered;
    }

    public String getAppTcPrintRef() {
        return AppTcPrintRef;
    }

    public void setAppTcPrintRef(String appTcPrintRef) {
        AppTcPrintRef = appTcPrintRef;
    }

    public String getAppOCRFactorCode()
    {
        return AppOCRFactorCode;
    }

    public void setAppOCRFactorCode(String appOCRFactorCode) {
        AppOCRFactorCode = appOCRFactorCode;
    }


    public String getCustomerRef() {
        return CustomerRef;
    }

    public void setCustomerRef(String customerRef) {
        CustomerRef = customerRef;
    }

    public String getCustomerCode() {
        return CustomerCode;
    }

    public void setCustomerCode(String customerCode) {
        CustomerCode = customerCode;
    }

    public String getCameraImage() {
        return CameraImage;
    }

    public void setCameraImage(String cameraImage) {
        CameraImage = cameraImage;
    }

    public String getFactorImage() {
        return FactorImage;
    }

    public void setFactorImage(String factorImage) {
        FactorImage = factorImage;
    }

    public String getFactorBarcode() {
        if (FactorBarcode != null)
        {
            return FactorBarcode;
        }else {
            return "";
        }
    }

    public String getExplain() {
        return Explain;
    }

    public void setExplain(String explain) {
        Explain = explain;
    }

    public boolean isCheck() {
        return Check;
    }

    public void setCheck(boolean check) {
        Check = check;
    }

    public void setFactorBarcode(String factorBarcode) {
        FactorBarcode = factorBarcode;
    }

    public String getFactorPrivateCode() {
        if (FactorPrivateCode != null)
        {
            return FactorPrivateCode;
        }else {
            return "";
        }
    }

    public void setFactorPrivateCode(String factorPrivateCode) {
        FactorPrivateCode = factorPrivateCode;
    }

    public String getFactorCode() {
        if (FactorCode != null)
        {
            return FactorCode;
        }else {
            return "";
        }
    }

    public void setFactorCode(String factorCode) {
        FactorCode = factorCode;
    }

    public String getScanDate() {
        if (ScanDate != null)
        {
            return ScanDate;
        }else {
            return "";
        }
    }

    public void setScanDate(String scanDate) {
        ScanDate = scanDate;
    }

    public String getFactorDate() {
        if (FactorDate != null)
        {
            return FactorDate;
        }else {
            return "";
        }
    }

    public void setFactorDate(String factorDate) {
        FactorDate = factorDate;
    }

    public String getFactorExplain() {
        if (FactorExplain != null)
        {
            return FactorExplain;
        }else {
            return "";
        }
    }

    public void setFactorExplain(String factorExplain) {
        FactorExplain = factorExplain;
    }

    public String getCustName() {
        if (CustName != null)
        {
            return CustName;
        }else {
            return "";
        }
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getSumPrice() {
        if (SumPrice != null)
        {
            try {
                return  SumPrice.substring(0,SumPrice.indexOf("."));
            }catch (Exception e){
                return  SumPrice;
            }
        }else {
            return "";
        }
    }

    public void setSumPrice(String sumPrice) {
        SumPrice = sumPrice;
    }

    public String getNewSumPrice() {
        if (NewSumPrice != null)
        {
            try {
                return  NewSumPrice.substring(0,NewSumPrice.indexOf("."));
            }catch (Exception e){
                return  NewSumPrice;
            }

        }else {
            return "";
        }
    }

    public void setNewSumPrice(String newSumPrice) {
        NewSumPrice = newSumPrice;
    }

    public String getSumAmount() {

        if (SumAmount != null)
        {
            try {
                return  SumAmount.substring(0,SumAmount.indexOf("."));
            }catch (Exception e){
                return  SumAmount;
            }

        }else {
            return "";
        }
    }

    public String getHasSignature() {
        return HasSignature;
    }

    public void setHasSignature(String hasSignature) {
        HasSignature = hasSignature;
    }

    public void setSumAmount(String sumAmount) {
        SumAmount = sumAmount;
    }

    public String getRowCount() {
        if (RowCount != null)
        {
            return RowCount;
        }else {
            return "";
        }
    }

    public void setRowCount(String rowCount) {
        RowCount = rowCount;
    }

    public String getSignatureImage() {
        if (SignatureImage != null)
        {
            return SignatureImage;
        }else {
            return "";
        }
    }

    public void setSignatureImage(String signatureImage) {
        SignatureImage = signatureImage;
    }

    public String getIsSent() {
        if (IsSent != null)
        {
            return IsSent;
        }else {
            return "";
        }
    }

    public void setIsSent(String isSent) {
        IsSent = isSent;
    }

    public String getDeliverer() {
        if (Deliverer != null)
        {
            return Deliverer;
        }else {
            return "";
        }
    }

    public void setDeliverer(String Deliverer) {
        Deliverer = Deliverer;
    }

    public String getAddress() {
        if (Address != null)
        {
            return Address;
        }else {
            return "";
        }
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        if (Phone != null)
        {
            return Phone;
        }else {
            return "";
        }
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
