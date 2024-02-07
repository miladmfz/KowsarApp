package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class Customer {

    @SerializedName("CustomerCode")
    private Integer CustomerCode;
    @SerializedName("PriceTip")
    private Integer PriceTip;
    @SerializedName("CustomerName")
    private String CustomerName;
    @SerializedName("Address")
    private String Address;
    @SerializedName("Manager")
    private String Manager;
    @SerializedName("Mobile")
    private String Mobile;
    @SerializedName("Phone")
    private String Phone;
    @SerializedName("Delegacy")
    private String Delegacy;
    @SerializedName("CityName")
    private String CityName;
    @SerializedName("CityCode")
    private String CityCode;
    @SerializedName("Bestankar")
    private Integer Bestankar;
    @SerializedName("Active")
    private Integer Active;
    @SerializedName("CentralPrivateCode")
    private Integer CentralPrivateCode;
    @SerializedName("EtebarNaghd")
    private Integer EtebarNaghd;
    @SerializedName("EtebarCheck")
    private Integer EtebarCheck;
    @SerializedName("Takhfif")
    private Integer Takhfif;
    @SerializedName("MobileName")
    private String MobileName;
    @SerializedName("Email")
    private String Email;
    @SerializedName("Fax")
    private String Fax;
    @SerializedName("ZipCode")
    private String ZipCode;
    @SerializedName("PostCode")
    private String PostCode;
    @SerializedName("ErrCode")
    private String ErrCode;
    @SerializedName("ErrDesc")
    private String ErrDesc;
    @SerializedName("IsExist")
    private String IsExist;
    @SerializedName("KodeMelli")
    private String KodeMelli;

    //region $ getter setter
    public String getCustomerFieldValue(String AKey) {
        String iKey = AKey.toLowerCase();
        String Res = "";

        switch (iKey) {
            case "customercode":
                if (CustomerCode == null) Res = "";
                else Res = CustomerCode.toString();
                break;
            case "pricetip":
                if (PriceTip == null) Res = "";
                else Res = PriceTip.toString();
                break;
            case "customername":
                if (CustomerName == null) Res = "";
                else Res = CustomerName;
                break;
            case "address":
                if (Address == null) Res = "";
                else Res = Address;
                break;
            case "manager":
                if (Manager == null) Res = "";
                else Res = Manager;
                break;
            case "mobile":
                if (Mobile == null) Res = "";
                else Res = Mobile;
                break;
            case "phone":
                if (Phone == null) Res = "";
                else Res = Phone;
                break;
            case "delegacy":
                if (Delegacy == null) Res = "";
                else Res = Delegacy;
                break;
            case "cityname":
                if (CityName == null) Res = "";
                else Res = CityName;
                break;
            case "citycode":
                if (CityCode == null) Res = "";
                else Res = CityCode;
                break;
            case "bestankar":
                if (Bestankar == null) Res = "";
                else Res = Bestankar.toString();
                break;
            case "active":
                if (Active == null) Res = "";
                else Res = Active.toString();
                break;
            case "centralprivatecode":
                if (CentralPrivateCode == null) Res = "";
                else Res = CentralPrivateCode.toString();
                break;
            case "etebarnaghd":
                if (EtebarNaghd == null) Res = "";
                else Res = EtebarNaghd.toString();
                break;
            case "etebarcheck":
                if (EtebarCheck == null) Res = "";
                else Res = EtebarCheck.toString();
                break;
            case "takhfif":
                if (Takhfif == null) Res = "";
                else Res = Takhfif.toString();
                break;
            case "mobilename":
                if (MobileName == null) Res = "";
                else Res = MobileName;
                break;
            case "email":
                if (Email == null) Res = "";
                else Res = Email;
                break;
            case "fax":
                if (Fax == null) Res = "";
                else Res = Fax;
                break;
            case "zipcode":
                if (ZipCode == null) Res = "";
                else Res = ZipCode;
                break;
            case "postcode":
                if (PostCode == null) Res = "";
                else Res = PostCode;
                break;
            case "errcode":
                if (ErrCode == null) Res = "";
                else Res = ErrCode;
                break;
            case "errdesc":
                if (ErrDesc == null) Res = "";
                else Res = ErrDesc;
                break;
            case "isexist":
                if (IsExist == null) Res = "";
                else Res = IsExist;
                break;
            case "kodemelli":
                if (KodeMelli == null) Res = "";
                else Res = KodeMelli;
                break;
        }

        return Res;

    }


    public void setCustomerCode(Integer customerCode) {
        CustomerCode = customerCode;
    }

    public void setPriceTip(Integer priceTip) {
        PriceTip = priceTip;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setManager(String manager) {
        Manager = manager;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setDelegacy(String delegacy) {
        Delegacy = delegacy;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public void setCityCode(String cityCode) {
        CityCode = cityCode;
    }

    public void setBestankar(Integer bestankar) {
        Bestankar = bestankar;
    }

    public void setActive(Integer active) {
        Active = active;
    }

    public void setCentralPrivateCode(Integer centralPrivateCode) {
        CentralPrivateCode = centralPrivateCode;
    }

    public void setEtebarNaghd(Integer etebarNaghd) {
        EtebarNaghd = etebarNaghd;
    }

    public void setEtebarCheck(Integer etebarCheck) {
        EtebarCheck = etebarCheck;
    }

    public void setTakhfif(Integer takhfif) {
        Takhfif = takhfif;
    }

    public void setMobileName(String mobileName) {
        MobileName = mobileName;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setFax(String fax) {
        Fax = fax;
    }

    public void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public void setPostCode(String postCode) {
        PostCode = postCode;
    }

    public void setErrCode(String errCode) {
        ErrCode = errCode;
    }

    public void setErrDesc(String errDesc) {
        ErrDesc = errDesc;
    }

    public void setIsExist(String isExist) {
        IsExist = isExist;
    }

    public void setKodeMelli(String kodeMelli) {
        KodeMelli = kodeMelli;
    }
    //endregion

}
