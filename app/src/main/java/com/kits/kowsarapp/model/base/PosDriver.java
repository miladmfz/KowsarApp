package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class PosDriver {


    @SerializedName("PosDriverCode")
    private String PosDriverCode;
    @SerializedName("PosName")
    private String PosName;


    public String getPosDriverCode() {
        return PosDriverCode;
    }

    public void setPosDriverCode(String posDriverCode) {
        PosDriverCode = posDriverCode;
    }

    public String getPosName() {
        return PosName;
    }

    public void setPosName(String posName) {
        PosName = posName;
    }
}
