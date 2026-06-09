package com.kits.kowsarapp.model.ocr;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Ocr_Location implements Serializable {


    @SerializedName ("LocationCode") private String LocationCode;
    @SerializedName ("LocationTitle") private String LocationTitle;
    @SerializedName ("StackRef") private String StackRef;
    @SerializedName ("IsFinal") private String IsFinal;
    @SerializedName ("LocationExplain") private String LocationExplain	;
    @SerializedName ("MaxGoodCount") private String MaxGoodCount;

    @SerializedName ("rwCount") private String rwCount;
    @SerializedName ("rwFirstNumeration") private String rwFirstNumeration;
    @SerializedName ("StackEnumerationRef") private String StackEnumerationRef;


    @SerializedName ("Owner") private String Owner	;
    @SerializedName ("CreationDate") private String CreationDate	;
    @SerializedName ("Reformer") private String Reformer	;
    @SerializedName ("ReformDate") private String ReformDate	;
    @SerializedName ("rowguid") private String rowguid	;
    @SerializedName ("rwno") private String rwno;


    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(String creationDate) {
        CreationDate = creationDate;
    }

    public String getReformer() {
        return Reformer;
    }

    public void setReformer(String reformer) {
        Reformer = reformer;
    }

    public String getReformDate() {
        return ReformDate;
    }

    public void setReformDate(String reformDate) {
        ReformDate = reformDate;
    }

    public String getRowguid() {
        return rowguid;
    }

    public void setRowguid(String rowguid) {
        this.rowguid = rowguid;
    }

    public String getRwno() {
        return rwno;
    }

    public void setRwno(String rwno) {
        this.rwno = rwno;
    }

    public String getStackEnumerationRef() {
        return StackEnumerationRef;
    }

    public void setStackEnumerationRef(String stackEnumerationRef) {
        StackEnumerationRef = stackEnumerationRef;
    }

    public String getRwCount() {
        return rwCount;
    }

    public void setRwCount(String rwCount) {
        this.rwCount = rwCount;
    }

    public String getRwFirstNumeration() {
        return rwFirstNumeration;
    }

    public void setRwFirstNumeration(String rwFirstNumeration) {
        this.rwFirstNumeration = rwFirstNumeration;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public String getLocationTitle() {
        return LocationTitle;
    }

    public void setLocationTitle(String locationTitle) {
        LocationTitle = locationTitle;
    }

    public String getStackRef() {
        return StackRef;
    }

    public void setStackRef(String stackRef) {
        StackRef = stackRef;
    }

    public String getIsFinal() {
        return IsFinal;
    }

    public void setIsFinal(String isFinal) {
        IsFinal = isFinal;
    }

    public String getLocationExplain() {
        return LocationExplain;
    }

    public void setLocationExplain(String locationExplain) {
        LocationExplain = locationExplain;
    }

    public String getMaxGoodCount() {
        return MaxGoodCount;
    }

    public void setMaxGoodCount(String maxGoodCount) {
        MaxGoodCount = maxGoodCount;
    }
}
