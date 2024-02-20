package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;
import com.kits.kowsarapp.model.order.Order_BasketInfo;
import com.kits.kowsarapp.model.order.Order_RstMiz;

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


    @SerializedName("Goods")
    private ArrayList<Good> goods;


    @SerializedName("Factors")
    private ArrayList<Factor> Factors;

    @SerializedName("Jobs")
    private ArrayList<Job> Jobs;

    @SerializedName("JobPersons")
    private ArrayList<JobPerson> JobPersons;


    @SerializedName("Factor")
    private Factor Factor;

    @SerializedName("Job")
    private Job Job;

    @SerializedName("JobPerson")
    private JobPerson JobPerson;


    @SerializedName("RstMiz")
    private Order_RstMiz orderRstMiz;
    @SerializedName("Group")
    private GoodGroup group;
    @SerializedName("BasketInfo")
    private Order_BasketInfo orderBasketInfo;
    @SerializedName("ObjectType")
    private ObjectType objectType;


    @SerializedName("RstMizs")
    private ArrayList<Order_RstMiz> orderRstMizs;
    @SerializedName("BasketInfos")
    private ArrayList<Order_BasketInfo> orderBasketInfos;


    @SerializedName("Values")
    private ArrayList<DistinctValue> Values;


    @SerializedName("Groups")
    private ArrayList<GoodGroup> Groups;

    @SerializedName("ObjectTypes")
    private ArrayList<ObjectType> ObjectTypes;






    //region $ getter setter


    public Order_RstMiz getRstMiz() {
        return orderRstMiz;
    }

    public void setRstMiz(Order_RstMiz orderRstMiz) {
        this.orderRstMiz = orderRstMiz;
    }

    public GoodGroup getGroup() {
        return group;
    }

    public void setGroup(GoodGroup group) {
        this.group = group;
    }

    public Order_BasketInfo getBasketInfo() {
        return orderBasketInfo;
    }

    public void setBasketInfo(Order_BasketInfo orderBasketInfo) {
        this.orderBasketInfo = orderBasketInfo;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public ArrayList<Order_RstMiz> getRstMizs() {
        return orderRstMizs;
    }

    public void setRstMizs(ArrayList<Order_RstMiz> orderRstMizs) {
        this.orderRstMizs = orderRstMizs;
    }

    public ArrayList<Order_BasketInfo> getBasketInfos() {
        return orderBasketInfos;
    }

    public void setBasketInfos(ArrayList<Order_BasketInfo> orderBasketInfos) {
        this.orderBasketInfos = orderBasketInfos;
    }

    public ArrayList<DistinctValue> getValues() {
        return Values;
    }

    public void setValues(ArrayList<DistinctValue> values) {
        Values = values;
    }

    public ArrayList<GoodGroup> getGroups() {
        return Groups;
    }

    public void setGroups(ArrayList<GoodGroup> groups) {
        Groups = groups;
    }

    public ArrayList<ObjectType> getObjectTypes() {
        return ObjectTypes;
    }

    public void setObjectTypes(ArrayList<ObjectType> objectTypes) {
        ObjectTypes = objectTypes;
    }

    public ArrayList<com.kits.kowsarapp.model.base.Factor> getFactors() {
        return Factors;
    }

    public void setFactors(ArrayList<com.kits.kowsarapp.model.base.Factor> factors) {
        Factors = factors;
    }

    public ArrayList<com.kits.kowsarapp.model.base.Job> getJobs() {
        return Jobs;
    }

    public void setJobs(ArrayList<com.kits.kowsarapp.model.base.Job> jobs) {
        Jobs = jobs;
    }

    public ArrayList<com.kits.kowsarapp.model.base.JobPerson> getJobPersons() {
        return JobPersons;
    }

    public void setJobPersons(ArrayList<com.kits.kowsarapp.model.base.JobPerson> jobPersons) {
        JobPersons = jobPersons;
    }

    public com.kits.kowsarapp.model.base.Factor getFactor() {
        return Factor;
    }

    public void setFactor(com.kits.kowsarapp.model.base.Factor factor) {
        Factor = factor;
    }

    public com.kits.kowsarapp.model.base.Job getJob() {
        return Job;
    }

    public void setJob(com.kits.kowsarapp.model.base.Job job) {
        Job = job;
    }

    public com.kits.kowsarapp.model.base.JobPerson getJobPerson() {
        return JobPerson;
    }

    public void setJobPerson(com.kits.kowsarapp.model.base.JobPerson jobPerson) {
        JobPerson = jobPerson;
    }

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

    public ArrayList<com.kits.kowsarapp.model.base.Location> getLocations() {
        return Locations;
    }

    public void setLocations(ArrayList<com.kits.kowsarapp.model.base.Location> locations) {
        Locations = locations;
    }

    public com.kits.kowsarapp.model.base.Location getLocation() {
        return Location;
    }

    public void setLocation(com.kits.kowsarapp.model.base.Location location) {
        Location = location;
    }
    //endregion

}