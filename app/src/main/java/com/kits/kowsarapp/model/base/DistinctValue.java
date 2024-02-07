package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class DistinctValue {

    @SerializedName("Value")
    private String Value;

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
