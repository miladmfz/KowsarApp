package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class Job {

    @SerializedName("JobCode")
    private String JobCode;

    @SerializedName("Title")
    private String Title;

    @SerializedName("Explain")
    private String Explain;

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

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getExplain() {
        return Explain;
    }

    public void setExplain(String explain) {
        Explain = explain;
    }
    //endregion
}
