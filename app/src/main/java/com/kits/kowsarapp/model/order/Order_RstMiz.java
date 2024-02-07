package com.kits.kowsarapp.model.order;

import com.google.gson.annotations.SerializedName;

public class Order_RstMiz {

    @SerializedName("RstMizCode")
    private String RstMizCode;
    @SerializedName("RstMizName")
    private String RstMizName;
    @SerializedName("Explain")
    private String Explain;
    @SerializedName("MizPrice")
    private String MizPrice;
    @SerializedName("Active")
    private String Active;
    @SerializedName("PlaceCount")
    private String PlaceCount;

    public String getRstMizCode() {
        return RstMizCode;
    }

    public void setRstMizCode(String rstMizCode) {
        RstMizCode = rstMizCode;
    }

    public String getRstMizName() {
        return RstMizName;
    }

    public void setRstMizName(String rstMizName) {
        RstMizName = rstMizName;
    }

    public String getExplain() {
        return Explain;
    }

    public void setExplain(String explain) {
        Explain = explain;
    }

    public String getMizPrice() {
        return MizPrice;
    }

    public void setMizPrice(String mizPrice) {
        MizPrice = mizPrice;
    }

    public String getActive() {
        return Active;
    }

    public void setActive(String active) {
        Active = active;
    }

    public String getPlaceCount() {
        return PlaceCount;
    }

    public void setPlaceCount(String placeCount) {
        PlaceCount = placeCount;
    }
}
