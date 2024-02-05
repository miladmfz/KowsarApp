package com.kits.kowsarapp.model;

import com.google.gson.annotations.SerializedName;

public class AppPrinter {

    @SerializedName("AppPrinterCode")
    private String AppPrinterCode;
    @SerializedName("PrinterName")
    private String PrinterName;
    @SerializedName("PrinterExplain")
    private String PrinterExplain;
    @SerializedName("GoodGroups")
    private String GoodGroups;
    @SerializedName("WhereClause")
    private String WhereClause;
    @SerializedName("PrintCount")
    private String PrintCount;
    @SerializedName("PrinterActive")
    private String PrinterActive;

    //region $ getter setter
    public String getAppPrinterCode() {
        return AppPrinterCode;
    }

    public void setAppPrinterCode(String appPrinterCode) {
        AppPrinterCode = appPrinterCode;
    }

    public String getPrinterName() {
        return PrinterName;
    }

    public void setPrinterName(String printerName) {
        PrinterName = printerName;
    }

    public String getPrinterExplain() {
        return PrinterExplain;
    }

    public void setPrinterExplain(String printerExplain) {
        PrinterExplain = printerExplain;
    }

    public String getGoodGroups() {
        return GoodGroups;
    }

    public void setGoodGroups(String goodGroups) {
        GoodGroups = goodGroups;
    }

    public String getWhereClause() {
        return WhereClause;
    }

    public void setWhereClause(String whereClause) {
        WhereClause = whereClause;
    }

    public String getPrintCount() {
        return PrintCount;
    }

    public void setPrintCount(String printCount) {
        PrintCount = printCount;
    }

    public String getPrinterActive() {
        return PrinterActive;
    }

    public void setPrinterActive(String printerActive) {
        PrinterActive = printerActive;
    }
    //endregion


}
