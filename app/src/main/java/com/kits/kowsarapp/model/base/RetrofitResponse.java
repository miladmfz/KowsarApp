package com.kits.kowsarapp.model.base;

import com.google.gson.annotations.SerializedName;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.model.order.Order_BasketInfo;
import com.kits.kowsarapp.model.order.Order_RstMiz;
import com.kits.kowsarapp.model.find.Find_Good;

import java.util.ArrayList;

public class RetrofitResponse {

    @SerializedName("Goods")
    private ArrayList<Good> Goods;

    @SerializedName("SearchGoods")
    private ArrayList<Find_Good> find_Goods;
    @SerializedName("OcrGoods")
    private ArrayList<Ocr_Good> Ocr_Goods;
    @SerializedName("Customers")
    private ArrayList<Customer> Customers;
    @SerializedName("Columns")
    private ArrayList<Column> Columns;
    @SerializedName("PreFactors")
    private ArrayList<PreFactor> PreFactors;
    @SerializedName("Activations")
    private ArrayList<Activation> Activations;
    @SerializedName("Locations")
    private ArrayList<KowsarLocation> kowsarLocations;
    @SerializedName("AppPrinters")
    private ArrayList<AppPrinter> AppPrinters;
    @SerializedName("RstMizs")
    private ArrayList<Order_RstMiz> RstMizs;
    @SerializedName("BasketInfos")
    private ArrayList<Order_BasketInfo> BasketInfos;
    @SerializedName("Factors")
    private ArrayList<Factor> Factors;
    @SerializedName("Jobs")
    private ArrayList<Job> Jobs;
    @SerializedName("JobPersons")
    private ArrayList<JobPerson> JobPersons;
    @SerializedName("Values")
    private ArrayList<DistinctValue> Values;
    @SerializedName("Groups")
    private ArrayList<GoodGroup> Groups;
    @SerializedName("ObjectTypes")
    private ArrayList<ObjectType> ObjectTypes;
    @SerializedName("SellBrokers")
    private ArrayList<SellBroker> SellBrokers;

    @SerializedName("PosDrivers")
    private ArrayList<PosDriver> posDrivers;



    @SerializedName("Good")
    private Good good;

    @SerializedName("OcrGood")
    private Ocr_Good ocr_good;
    @SerializedName("Group")
    private GoodGroup group;

    @SerializedName("Customer")
    private Customer customer;
    @SerializedName("PreFactor")
    private PreFactor preFactor;
    @SerializedName("Factor")
    private Factor Factor;

    @SerializedName("Job")
    private Job Job;
    @SerializedName("JobPerson")
    private JobPerson JobPerson;

    @SerializedName("RstMiz")
    private Order_RstMiz RstMiz;
    @SerializedName("BasketInfo")
    private Order_BasketInfo BasketInfo;


    @SerializedName("ObjectType")
    private ObjectType objectType;


    @SerializedName("Column")
    private Column column;

    @SerializedName("Activation")
    private Activation activation;
    @SerializedName("Location")
    private KowsarLocation kowsarLocation;

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


    public ArrayList<PosDriver> getPosDrivers() {
        return posDrivers;
    }

    public void setPosDrivers(ArrayList<PosDriver> posDrivers) {
        this.posDrivers = posDrivers;
    }

    public ArrayList<Find_Good> getSearch_Goods() {
        return find_Goods;
    }

    public void setSearch_Goods(ArrayList<Find_Good> find_Goods) {
        this.find_Goods = find_Goods;
    }

    public ArrayList<Ocr_Good> getOcr_Goods() {
        return Ocr_Goods;
    }

    public void setOcr_Goods(ArrayList<Ocr_Good> ocr_Goods) {
        Ocr_Goods = ocr_Goods;
    }

    public Ocr_Good getOcr_good() {
        return ocr_good;
    }

    public void setOcr_good(Ocr_Good ocr_good) {
        this.ocr_good = ocr_good;
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

    public ArrayList<Activation> getActivations() {
        return Activations;
    }

    public void setActivations(ArrayList<Activation> activations) {
        Activations = activations;
    }

    public ArrayList<KowsarLocation> getKowsarLocations() {
        return kowsarLocations;
    }

    public void setKowsarLocations(ArrayList<KowsarLocation> kowsarLocations) {
        this.kowsarLocations = kowsarLocations;
    }

    public ArrayList<AppPrinter> getAppPrinters() {
        return AppPrinters;
    }

    public void setAppPrinters(ArrayList<AppPrinter> appPrinters) {
        AppPrinters = appPrinters;
    }



    public ArrayList<Factor> getFactors() {
        return Factors;
    }

    public void setFactors(ArrayList<Factor> factors) {
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

    public ArrayList<SellBroker> getSellBrokers() {
        return SellBrokers;
    }

    public void setSellBrokers(ArrayList<SellBroker> sellBrokers) {
        SellBrokers = sellBrokers;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public GoodGroup getGroup() {
        return group;
    }

    public void setGroup(GoodGroup group) {
        this.group = group;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public PreFactor getPreFactor() {
        return preFactor;
    }

    public void setPreFactor(PreFactor preFactor) {
        this.preFactor = preFactor;
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

    public ArrayList<Order_RstMiz> getRstMizs() {
        return RstMizs;
    }

    public void setRstMizs(ArrayList<Order_RstMiz> rstMizs) {
        RstMizs = rstMizs;
    }

    public ArrayList<Order_BasketInfo> getBasketInfos() {
        return BasketInfos;
    }

    public void setBasketInfos(ArrayList<Order_BasketInfo> basketInfos) {
        BasketInfos = basketInfos;
    }

    public Order_RstMiz getRstMiz() {
        return RstMiz;
    }

    public void setRstMiz(Order_RstMiz rstMiz) {
        RstMiz = rstMiz;
    }

    public Order_BasketInfo getBasketInfo() {
        return BasketInfo;
    }

    public void setBasketInfo(Order_BasketInfo basketInfo) {
        BasketInfo = basketInfo;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public KowsarLocation getKowsarLocation() {
        return kowsarLocation;
    }

    public void setKowsarLocation(KowsarLocation kowsarLocation) {
        this.kowsarLocation = kowsarLocation;
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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
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
}
