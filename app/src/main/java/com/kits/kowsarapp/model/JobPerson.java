package com.kits.kowsarapp.model;

import com.google.gson.annotations.SerializedName;

public class JobPerson {

    @SerializedName("JobCode")
    private String JobCode;

    @SerializedName("JobPersonCode")
    private String JobPersonCode;


    @SerializedName("Title")
    private String Title;


    @SerializedName("Name")
    private String Name;


    @SerializedName("FName")
    private String FName;

    @SerializedName("Text")
    private String Text;

    //region $ getter setter

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }


    public String getJobCode() {
        return JobCode;
    }

    public void setJobCode(String jobCode) {
        JobCode = jobCode;
    }

    public String getJobPersonCode() {
        return JobPersonCode;
    }

    public void setJobPersonCode(String jobPersonCode) {
        JobPersonCode = jobPersonCode;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }
    //endregion
}
