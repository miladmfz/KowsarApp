package com.kits.kowsarapp.model;

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

    @SerializedName("dbname")
    private String dbname;


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




    @SerializedName("Check")
    private boolean Check;

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
            return SumPrice;
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
            return NewSumPrice;
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
            return SumAmount;
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
