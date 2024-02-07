package com.kits.kowsarapp.model.order;

import com.google.gson.annotations.SerializedName;

public class Order_TableDetail {

    @SerializedName("cid")
    private int cid;
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("Text")
    private String Text;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
