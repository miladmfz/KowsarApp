package com.kits.kowsarapp.webService.order;


import com.kits.kowsarapp.model.base.RetrofitResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Order_APIInterface {

    String Order_Url = "Order/";
    String Kowsar_Url = "Kowsar/";


//
//
//
//
//
//    @GET(Order_Url+"GetObjectTypeFromDbSetup")
//    Call<RetrofitResponse> GetObjectTypeFromDbSetup(
//            @Query("tag") String tag,
//            @Query("ObjectType") String ObjectType
//    );
//    @GET(Order_Url+"DbSetupvalue")
//    Call<RetrofitResponse> DbSetupvalue(
//            @Query("tag") String tag,
//            @Query("Where") String Where
//    );
//
//
//    @GET(Order_Url+"GetOrdergroupList")
//    Call<RetrofitResponse> GetOrdergroupList(
//            @Query("tag") String tag,
//            @Query("GroupCode") String GroupCode
//    );
//
//
//
//
//    @GET(Order_Url+"DeleteGoodFromBasket")
//    Call<RetrofitResponse> DeleteGoodFromBasket(
//            @Query("tag") String tag,
//            @Query("RowCode") String RowCode,
//            @Query("AppBasketInfoRef") String AppBasketInfoRef
//    );
//
//
//    @GET(Kowsar_Url+"GetImage")
//    Call<RetrofitResponse> GetImage(
//            @Query("tag") String tag,
//            @Query("ObjectRef") String ObjectRef,
//            @Query("ClassName") String ClassName,
//            @Query("IX") String IX,
//            @Query("Scale") String Scale
//    );
//
//
//
//    @GET(Order_Url+"OrderReserveList")
//    Call<RetrofitResponse> OrderReserveList(
//            @Query("tag") String tag,
//            @Query("MizRef") String Broker
//    );
//
//
//
//    @GET(Order_Url+"GetDistinctValues")
//    Call<RetrofitResponse> GetDistinctValues(
//            @Query("tag") String tag,
//            @Query("TableName") String TableName,
//            @Query("FieldNames") String FieldNames,
//            @Query("WhereClause") String WhereClause
//    );
//
//    @GET(Order_Url+"GetTodeyFromServer")
//    Call<RetrofitResponse> GetTodeyFromServer(
//            @Query("tag") String tag
//    );
//
//
//
//    @GET(Order_Url+"GetOrderSum")
//    Call<RetrofitResponse> GetOrderSum(
//            @Query("tag") String tag,
//            @Query("AppBasketInfoRef") String AppBasketInfoRef
//    );
//
//
//    @GET(Order_Url+"OrderGet")
//    Call<RetrofitResponse> OrderGet(
//            @Query("tag") String tag,
//            @Query("AppBasketInfoRef") String AppBasketInfoRef,
//            @Query("AppType") String AppType
//    );
//    @GET(Order_Url+"OrderGetFactor")
//    Call<RetrofitResponse> OrderGetFactor(
//            @Query("tag") String tag,
//            @Query("AppBasketInfoRef") String AppBasketInfoRef
//    );
//
//    @GET(Order_Url+"OrderToFactor")
//    Call<RetrofitResponse> OrderToFactor(
//            @Query("tag") String tag,
//            @Query("AppBasketInfoRef") String AppBasketInfoRef
//    );
//
//
//
//    @GET(Order_Url+"Order_CanPrint")
//    Call<RetrofitResponse> Order_CanPrint(
//            @Query("tag") String tag,
//            @Query("AppBasketInfoRef") String AppBasketInfoRef,
//            @Query("CanPrint") String CanPrint
//    );
//
//    @GET(Order_Url+"OrderDeleteAll")
//    Call<RetrofitResponse> OrderDeleteAll(
//            @Query("tag") String tag,
//            @Query("AppBasketInfoRef") String AppBasketInfoRef
//    );
//
//
//    @GET(Order_Url+"OrderInfoReserveDelete")
//    Call<RetrofitResponse> OrderInfoReserveDelete(
//            @Query("tag") String tag,
//            @Query("AppBasketInfoRef") String AppBasketInfoRef
//    );
//
//    @GET(Order_Url+"GetSellBroker")
//    Call<RetrofitResponse> GetSellBroker(
//            @Query("tag") String tag
//    );
//
//
//
//    @GET(Order_Url+"OrderChangeTable")
//    Call<RetrofitResponse> OrderChangeTable(
//            @Query("tag") String tag,
//            @Query("AppBasketInfoRef") String AppBasketInfoRef
//    );
//    @GET(Order_Url+"OrderPrintFactor")
//    Call<RetrofitResponse> OrderPrintFactor(
//            @Query("tag") String tag,
//            @Query("AppBasketInfoRef") String AppBasketInfoRef
//    );
//
//
//    @POST(Order_Url+"OrderRowInsert")
//    Call<RetrofitResponse> OrderRowInsert(@Body RequestBody requestBody );
//    @POST(Order_Url+"OrderInfoInsert")
//    Call<RetrofitResponse> OrderInfoInsert(@Body RequestBody requestBody );
//    @POST(Order_Url+"GetOrderGoodList")
//    Call<RetrofitResponse> GetOrderGoodList(@Body RequestBody requestBody );
//    @POST(Order_Url+"OrderMizList")
//    Call<RetrofitResponse> OrderMizList(@Body RequestBody requestBody );
//    @POST(Order_Url+"OrderEditInfoExplain")
//    Call<RetrofitResponse> OrderEditInfoExplain(@Body RequestBody requestBody );
//




//**********************************************************************************

    //@GET(Order_Url+"OrderGetAppPrinter")
    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderGetAppPrinter(
            @Query("tag") String tag
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderGetSummmary(@Field("tag") String tag, @Field("AppBasketInfoRef") String AppBasketInfoRef);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Factor_Payment_Pos(@Field("tag") String tag
            , @Field("FactorRef") String FactorRef
            , @Field("PosRef") String PosRef
            , @Field("Mablagh") String Mablagh
    );



    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Factor_Payment_Cash(@Field("tag") String tag
            , @Field("FactorRef") String FactorRef
            , @Field("Mablagh") String Mablagh
            , @Field("Increment") String Increment
            , @Field("Decrement") String Decrement
    );
    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderGetPosDriver(@Field("tag") String tag);

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

