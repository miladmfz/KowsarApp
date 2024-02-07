package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class ObjectType {

    @SerializedName("tid")
    private String tid;
    @SerializedName("aType")
    private String aType;
    @SerializedName("IsDefault")
    private String IsDefault;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getaType() {
        return aType;
    }

    public void setaType(String aType) {
        this.aType = aType;
    }

    public String getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(String isDefault) {
        IsDefault = isDefault;
    }
}
