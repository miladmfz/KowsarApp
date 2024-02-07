package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;

public class Column {


    @SerializedName("ColumnCode")
    private String ColumnCode;
    @SerializedName("GoodType")
    private String GoodType;
    @SerializedName("ColumnName")
    private String ColumnName;
    @SerializedName("ColumnDesc")
    private String ColumnDesc;
    @SerializedName("ColumnDefinition")
    private String ColumnDefinition;
    @SerializedName("ColumnType")
    private String ColumnType;
    @SerializedName("BrokerSearch")
    private String BrokerSearch;
    @SerializedName("OrderIndex")
    private String OrderIndex;
    @SerializedName("Condition")
    private String Condition;


    @SerializedName("Search")
    private String Search;
    @SerializedName("SortOrder")
    private String SortOrder;
    @SerializedName("IsDefault")
    private String IsDefault;



    //region $ getter setter
    public String getColumnFieldValue(String AKey) {
        String iKey = AKey.toLowerCase();
        String Res = "";
        switch (iKey) {
            case "columncode":
                Res = ColumnCode;
                break;
            case "columnname":
                Res = ColumnName;
                break;
            case "goodtype":
                if (GoodType == null) Res = "";
                else Res = GoodType;
                break;
            case "columndesc":
                if (ColumnDesc == null) Res = "";
                else Res = ColumnDesc;
                break;
            case "columndefinition":
                if (ColumnDefinition == null) Res = "";
                else Res = ColumnDefinition;
                break;
            case "search":
                if (Search == null) Res = "";
                else Res = Search;
                break;
            case "sortorder":
                if (SortOrder == null) Res = "";
                else Res = SortOrder;
                break;
            case "isdefault":
                if (IsDefault == null) Res = "";
                else Res = IsDefault;
                break;
            case "columntype":
                if (ColumnType == null) Res = "";
                else Res = ColumnType;
                break;
            case "brokersearch":
                if (BrokerSearch == null) Res = "";
                else Res = BrokerSearch;
                break;
            case "orderindex":
                if (OrderIndex == null) Res = "";
                else Res = OrderIndex;
                break;
            case "condition":
                if (Condition == null) Res = "";
                else Res = Condition;
                break;
        }
        return Res;
    }


    public String getGoodType() {
        return GoodType;
    }

    public void setGoodType(String goodType) {
        GoodType = goodType;
    }

    public String getColumnName() {
        return ColumnName;
    }

    public void setColumnName(String columnName) {
        ColumnName = columnName;
    }

    public String getColumnDesc() {
        return ColumnDesc;
    }

    public void setColumnDesc(String columnDesc) {
        ColumnDesc = columnDesc;
    }

    public String getColumnDefinition() {
        return ColumnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        ColumnDefinition = columnDefinition;
    }

    public String getColumnType() {
        return ColumnType;
    }

    public void setColumnType(String columnType) {
        ColumnType = columnType;
    }

    public String getSearch() {
        return Search;
    }

    public void setSearch(String search) {
        Search = search;
    }

    public String getSortOrder() {
        return SortOrder;
    }

    public void setSortOrder(String sortOrder) {
        SortOrder = sortOrder;
    }

    public String getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(String isDefault) {
        IsDefault = isDefault;
    }

    public String getBrokerSearch() {
        return BrokerSearch;
    }

    public void setBrokerSearch(String brokerSearch) {
        BrokerSearch = brokerSearch;
    }

    public String getOrderIndex() {
        return OrderIndex;
    }

    public void setOrderIndex(String orderIndex) {
        OrderIndex = orderIndex;
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String condition) {
        Condition = condition;
    }

    public String getColumnCode() {
        return ColumnCode;
    }

    public void setColumnCode(String columnCode) {
        ColumnCode = columnCode;
    }
    //endregion

}
