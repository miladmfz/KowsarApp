package com.kits.kowsarapp.webService.order;


import com.kits.kowsarapp.model.base.RetrofitResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Order_APIInterface {

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Activation(@Field("tag") String tag, @Field("ActivationCode") String ActivationCode);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> kowsar_info(@Field("tag") String tag, @Field("Where") String Where);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Getgrp(@Field("tag") String tag, @Field("GroupCode") String GroupCode);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetGoodFromGroup(@Field("tag") String tag, @Field("Where") String Where, @Field("GroupCode") String GroupCode, @Field("AppBasketInfoRef") String AppBasketInfoRef);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> DeleteGoodFromBasket(@Field("tag") String tag, @Field("RowCode") String RowCode, @Field("AppBasketInfoRef") String AppBasketInfoRef);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetImage(@Field("tag") String tag, @Field("ObjectRef") String ObjectRef, @Field("ClassName") String ClassName, @Field("IX") String IX, @Field("Scale") String Scale);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderInfoInsert(@Field("tag") String tag, @Field("Broker") String Broker, @Field("Miz") String Miz, @Field("PersonName") String PersonName, @Field("Mobile") String Mobile, @Field("InfoExplain") String InfoExplain, @Field("Prepayed") String Prepayed, @Field("ReserveStartTime") String ReserveStartTime, @Field("ReserveEndTime") String ReserveEndTime, @Field("Date") String Date, @Field("State") String State, @Field("InfoCode") String InfoCode);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderReserveList(@Field("tag") String tag, @Field("MizRef") String Broker);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetDistinctValues(@Field("tag") String tag, @Field("TableName") String TableName, @Field("FieldNames") String FieldNames, @Field("WhereClause") String WhereClause);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetTodeyFromServer(@Field("tag") String tag);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetObjectTypeFromDbSetup(@Field("tag") String tag, @Field("ObjectType") String ObjectType);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderMizList(@Field("tag") String tag, @Field("InfoState") String InfoState, @Field("MizType") String MizType);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderRowInsert(@Field("tag") String tag, @Field("GoodRef") String GoodRef, @Field("FacAmount") String FacAmount, @Field("Price") String Price, @Field("bUnitRef") String bUnitRef, @Field("bRatio") String bRatio, @Field("Explain") String Explain, @Field("InfoRef") String InfoRef, @Field("RowCode") String RowCode);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetbasketSum(@Field("tag") String tag, @Field("AppBasketInfoRef") String AppBasketInfoRef);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderGet(@Field("tag") String tag, @Field("AppBasketInfoRef") String AppBasketInfoRef, @Field("AppType") String AppType);

    @FormUrlEncoded
    @POST("index.php")
    Call<RetrofitResponse> OrderSendImage(@Field("tag") String tag, @Field("Image") String image, @Field("Code") String barcode, @Field("PrinterName") String PrinterName, @Field("PrintCount") String PrintCount);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderGetFactor(@Field("tag") String tag, @Field("AppBasketInfoRef") String AppBasketInfoRef);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderGetFactorRow(@Field("tag") String tag, @Field("AppBasketInfoRef") String AppBasketInfoRef, @Field("GoodGroups") String GoodGroups, @Field("Where") String Where);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderToFactor(@Field("tag") String tag, @Field("AppBasketInfoRef") String AppBasketInfoRef);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderGetAppPrinter(@Field("tag") String tag);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Order_CanPrint(@Field("tag") String tag, @Field("AppBasketInfoRef") String AppBasketInfoRef, @Field("CanPrint") String CanPrint);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderDeleteAll(@Field("tag") String tag, @Field("AppBasketInfoRef") String AppBasketInfoRef);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderInfoReserveDelete(@Field("tag") String tag, @Field("AppBasketInfoRef") String AppBasketInfoRef);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetSellBroker(@Field("tag") String tag);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderEditInfoExplain(@Field("tag") String tag, @Field("AppBasketInfoCode") String AppBasketInfoCode, @Field("Explain") String Explain);


}

