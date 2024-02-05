package com.kits.kowsarapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class RetrofitResponse {

    @SerializedName("Goods")
    private ArrayList<Good> Goods;
    @SerializedName("Customers")
    private ArrayList<Customer> Customers;
    @SerializedName("Columns")
    private ArrayList<Column> Columns;
    @SerializedName("PreFactors")
    private ArrayList<PreFactor> PreFactors;
    @SerializedName("Activations")
    private ArrayList<Activation> Activations;
    @SerializedName("Locations")
    private ArrayList<Location> Locations;
    @SerializedName("AppPrinters")
    private ArrayList<AppPrinter> AppPrinters;


    @SerializedName("Good")
    private Good good;
    @SerializedName("Customer")
    private Customer customer;
    @SerializedName("Column")
    private Column column;
    @SerializedName("PreFactor")
    private PreFactor preFactor;
    @SerializedName("Activation")
    private Activation activation;
    @SerializedName("Location")
    private Location Location;

    @SerializedName("value")
    private String value;
    @SerializedName("Text")
    private String Text;

    @SerializedName("response")
    private String response;


    @SerializedName("ErrCode")
    private String ErrCode;
    @SerializedName("ErrDesc")
    private String ErrDesc;
    @SerializedName("SellBrokers")
    private ArrayList<SellBroker> SellBrokers;


    //region $ getter setter



    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }



    public ArrayList<AppPrinter> getAppPrinters() {
        return AppPrinters;
    }

    public void setAppPrinters(ArrayList<AppPrinter> appPrinters) {
        AppPrinters = appPrinters;
    }

    public ArrayList<SellBroker> getSellBrokers() {
        return SellBrokers;
    }

    public void setSellBrokers(ArrayList<SellBroker> sellBrokers) {
        SellBrokers = sellBrokers;
    }

    public ArrayList<Good> getGoods() {
        return Goods;
    }

    public void setGoods(ArrayList<Good> goods) {
        Goods = goods;
    }

    public ArrayList<Customer> getCustomers() {
        return Customers;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        Customers = customers;
    }

    public ArrayList<Column> getColumns() {
        return Columns;
    }

    public void setColumns(ArrayList<Column> columns) {
        Columns = columns;
    }

    public ArrayList<PreFactor> getPreFactors() {
        return PreFactors;
    }

    public void setPreFactors(ArrayList<PreFactor> preFactors) {
        PreFactors = preFactors;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public PreFactor getPreFactor() {
        return preFactor;
    }

    public void setPreFactor(PreFactor preFactor) {
        this.preFactor = preFactor;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getErrCode() {
        return ErrCode;
    }

    public void setErrCode(String errCode) {
        ErrCode = errCode;
    }

    public String getErrDesc() {
        return ErrDesc;
    }

    public void setErrDesc(String errDesc) {
        ErrDesc = errDesc;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public ArrayList<Activation> getActivations() {
        return Activations;
    }

    public void setActivations(ArrayList<Activation> activations) {
        Activations = activations;
    }

    public ArrayList<com.kits.kowsarapp.model.Location> getLocations() {
        return Locations;
    }

    public void setLocations(ArrayList<com.kits.kowsarapp.model.Location> locations) {
        Locations = locations;
    }

    public com.kits.kowsarapp.model.Location getLocation() {
        return Location;
    }

    public void setLocation(com.kits.kowsarapp.model.Location location) {
        Location = location;
    }
    //endregion

}
