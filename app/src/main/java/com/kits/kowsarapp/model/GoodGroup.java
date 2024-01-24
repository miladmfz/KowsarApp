package com.kits.kowsarapp.model;

import com.google.gson.annotations.SerializedName;

public class GoodGroup {

    @SerializedName("GoodGroupCode")
    private Integer GoodGroupCode;
    @SerializedName("GroupCode")
    private Integer GroupCode;
    @SerializedName("Name")
    private String Name;
    @SerializedName("L1")
    private Integer L1;
    @SerializedName("L2")
    private Integer L2;
    @SerializedName("L3")
    private Integer L3;
    @SerializedName("L4")
    private Integer L4;
    @SerializedName("L5")
    private Integer L5;

    @SerializedName("ChildNo")
    private Integer ChildNo;


    public String getGoodGroupFieldValue(String AKey) {

        String iKey = AKey.toLowerCase();
        String Res = "";

        switch (iKey) {
            case "goodgroupcode":
                if (GoodGroupCode == null) Res = "";
                else Res = GoodGroupCode.toString();
                break;
            case "groupcode":
                if (GroupCode == null) Res = "";
                else Res = GroupCode.toString();
                break;
            case "name":
                if (Name == null) Res = "";
                else Res = Name;
                break;
            case "l1":
                if (L1 == null) Res = "";
                else Res = L1.toString();
                break;
            case "l2":
                if (L2 == null) Res = "";
                else Res = L2.toString();
                break;
            case "l3":
                if (L3 == null) Res = "";
                else Res = L3.toString();
                break;
            case "l4":
                if (L4 == null) Res = "";
                else Res = L4.toString();
                break;
            case "l5":
                if (L5 == null) Res = "";
                else Res = L5.toString();
                break;
            case "childno":
                if (ChildNo == null) Res = "0";
                else Res = ChildNo.toString();
                break;
        }

        return Res;

    }

    public void setGoodGroupCode(Integer goodGroupCode) {
        GoodGroupCode = goodGroupCode;
    }

    public void setGroupCode(Integer groupCode) {
        GroupCode = groupCode;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setL1(Integer l1) {
        L1 = l1;
    }

    public void setL2(Integer l2) {
        L2 = l2;
    }

    public void setL3(Integer l3) {
        L3 = l3;
    }

    public void setL4(Integer l4) {
        L4 = l4;
    }

    public void setL5(Integer l5) {
        L5 = l5;
    }


    public void setChildNo(Integer childNo) {
        ChildNo = childNo;
    }
}
