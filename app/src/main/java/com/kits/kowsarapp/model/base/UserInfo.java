package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class UserInfo {


    @SerializedName("Email")
    private String Email;
    @SerializedName("NameFamily")
    private String NameFamily;
    @SerializedName("Address")
    private String Address;
    @SerializedName("Mobile")
    private String Mobile;
    @SerializedName("Phone")
    private String Phone;
    @SerializedName("BirthDate")
    private String BirthDate;
    @SerializedName("MelliCode")
    private String MelliCode;
    @SerializedName("PostalCode")
    private String PostalCode;
    @SerializedName("ActiveCode")
    private String ActiveCode;
    @SerializedName("BrokerCode")
    private String BrokerCode;


    //region $ getter setter



    public String getBrokerCode() {
        return BrokerCode;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setNameFamily(String nameFamily) {
        NameFamily = nameFamily;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public void setMelliCode(String melliCode) {
        MelliCode = melliCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public void setActiveCode(String activeCode) {
        ActiveCode = activeCode;
    }

    public void setBrokerCode(String brokerCode) {
        BrokerCode = brokerCode;
    }
    //endregion

}
