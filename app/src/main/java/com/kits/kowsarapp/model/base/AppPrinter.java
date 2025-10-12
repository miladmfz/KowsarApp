package com.kits.kowsarapp.model.base;

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

    @SerializedName("FilePath")
    private String FilePath;
    @SerializedName("AppType")
    private String AppType;
    @SerializedName("ChangePrint")
    private String ChangePrint;




    //region $ getter setter

    public String getChangePrint() {
        return ChangePrint;
    }

    public void setChangePrint(String changePrint) {
        ChangePrint = changePrint;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getAppType() {
        return AppType;
    }

    public void setAppType(String appType) {
        AppType = appType;
    }
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
