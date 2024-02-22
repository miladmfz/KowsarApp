package com.kits.kowsarapp.model.base;


import com.google.gson.annotations.SerializedName;

public class PFrow {
    @SerializedName("GoodRef")
    private String GoodRef;
    @SerializedName("FactorAmount")
    private String FactorAmount;
    @SerializedName("Price")
    private String Price;

    // Getters and setters


    public String getGoodRef() {
        return GoodRef;
    }

    public void setGoodRef(String goodRef) {
        GoodRef = goodRef;
    }

    public String getFactorAmount() {
        return FactorAmount;
    }

    public void setFactorAmount(String factorAmount) {
        FactorAmount = factorAmount;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
