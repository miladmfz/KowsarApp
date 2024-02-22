package com.kits.kowsarapp.webService.order;


import com.kits.kowsarapp.model.base.RetrofitResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Order_APIInterface {

    String Order_Url = "Order/";
    String Kowsar_Url = "Kowsar/";




    @GET(Kowsar_Url+"DbSetupvalue")
    Call<RetrofitResponse> DbSetupvalue(
            @Query("tag") String tag,
            @Query("Where") String Where
    );


    @GET(Order_Url+"GetOrdergroupList")
    Call<RetrofitResponse> GetOrdergroupList(
            @Query("tag") String tag,
            @Query("GroupCode") String GroupCode
    );

    @GET(Order_Url+"GetOrderGoodList")
    Call<RetrofitResponse> GetOrderGoodList(
            @Query("tag") String tag,
            @Query("Where") String Where,
            @Query("GroupCode") String GroupCode,
            @Query("AppBasketInfoRef") String AppBasketInfoRef
    );

    @GET(Order_Url+"DeleteGoodFromBasket")
    Call<RetrofitResponse> DeleteGoodFromBasket(
            @Query("tag") String tag,
            @Query("RowCode") String RowCode,
            @Query("AppBasketInfoRef") String AppBasketInfoRef
    );


    @GET(Kowsar_Url+"GetImage")
    Call<RetrofitResponse> GetImage(
            @Query("tag") String tag,
            @Query("ObjectRef") String ObjectRef,
            @Query("ClassName") String ClassName,
            @Query("IX") String IX,
            @Query("Scale") String Scale
    );


    @GET(Order_Url+"OrderInfoInsert")
    Call<RetrofitResponse> OrderInfoInsert(
            @Query("tag") String tag,
            @Query("Broker") String Broker,
            @Query("Miz") String Miz,
            @Query("PersonName") String PersonName,
            @Query("Mobile") String Mobile,
            @Query("InfoExplain") String InfoExplain,
            @Query("Prepayed") String Prepayed,
            @Query("ReserveStartTime") String ReserveStartTime,
            @Query("ReserveEndTime") String ReserveEndTime,
            @Query("Date") String Date,
            @Query("State") String State,
            @Query("InfoCode") String InfoCode

    );


    @GET(Order_Url+"OrderReserveList")
    Call<RetrofitResponse> OrderReserveList(
            @Query("tag") String tag,
            @Query("MizRef") String Broker
    );


    @GET(Kowsar_Url+"GetDistinctValues")
    Call<RetrofitResponse> GetDistinctValues(
            @Query("tag") String tag,
            @Query("TableName") String TableName,
            @Query("FieldNames") String FieldNames,
            @Query("WhereClause") String WhereClause
    );


    @GET(Order_Url+"GetTodeyFromServer")
    Call<RetrofitResponse> GetTodeyFromServer(
            @Query("tag") String tag
    );

    @GET(Order_Url+"GetObjectTypeFromDbSetup")
    Call<RetrofitResponse> GetObjectTypeFromDbSetup(
            @Query("tag") String tag,
            @Query("ObjectType") String ObjectType
    );


    @GET(Order_Url+"OrderMizList")
    Call<RetrofitResponse> OrderMizList(
            @Query("tag") String tag,
            @Query("InfoState") String InfoState,
            @Query("MizType") String MizType
    );


    @GET(Order_Url+"OrderRowInsert")
    Call<RetrofitResponse> OrderRowInsert(
            @Query("tag") String tag,
            @Query("GoodRef") String GoodRef,
            @Query("FacAmount") String FacAmount,
            @Query("Price") String Price,
            @Query("bUnitRef") String bUnitRef,
            @Query("bRatio") String bRatio,
            @Query("Explain") String Explain,
            @Query("InfoRef") String InfoRef,
            @Query("RowCode") String RowCode
    );

    @GET(Order_Url+"GetOrderSum")
    Call<RetrofitResponse> GetOrderSum(
            @Query("tag") String tag,
            @Query("AppBasketInfoRef") String AppBasketInfoRef
    );


    @GET(Order_Url+"OrderGet")
    Call<RetrofitResponse> OrderGet(
            @Query("tag") String tag,
            @Query("AppBasketInfoRef") String AppBasketInfoRef,
            @Query("AppType") String AppType
    );




    @GET(Order_Url+"OrderGetFactor")
    Call<RetrofitResponse> OrderGetFactor(
            @Query("tag") String tag,
            @Query("AppBasketInfoRef") String AppBasketInfoRef
    );


    @GET(Order_Url+"OrderGetFactorRow")
    Call<RetrofitResponse> OrderGetFactorRow(
            @Query("tag") String tag,
            @Query("AppBasketInfoRef") String AppBasketInfoRef,
            @Query("GoodGroups") String GoodGroups,
            @Query("Where") String Where
    );


    @GET(Order_Url+"OrderToFactor")
    Call<RetrofitResponse> OrderToFactor(
            @Query("tag") String tag,
            @Query("AppBasketInfoRef") String AppBasketInfoRef
    );


    @GET(Order_Url+"OrderGetAppPrinter")
    Call<RetrofitResponse> OrderGetAppPrinter(
            @Query("tag") String tag
    );

    @GET(Order_Url+"Order_CanPrint")
    Call<RetrofitResponse> Order_CanPrint(
            @Query("tag") String tag,
            @Query("AppBasketInfoRef") String AppBasketInfoRef,
            @Query("CanPrint") String CanPrint
    );

    @GET(Order_Url+"OrderDeleteAll")
    Call<RetrofitResponse> OrderDeleteAll(
            @Query("tag") String tag,
            @Query("AppBasketInfoRef") String AppBasketInfoRef
    );


    @GET(Order_Url+"OrderInfoReserveDelete")
    Call<RetrofitResponse> OrderInfoReserveDelete(
            @Query("tag") String tag,
            @Query("AppBasketInfoRef") String AppBasketInfoRef
    );

    @GET(Order_Url+"GetSellBroker")
    Call<RetrofitResponse> GetSellBroker(
            @Query("tag") String tag
    );
    @GET(Order_Url+"OrderEditInfoExplain")
    Call<RetrofitResponse> OrderEditInfoExplain(
            @Query("tag") String tag,
            @Query("AppBasketInfoCode") String AppBasketInfoCode,
            @Query("Explain") String Explain
    );




    @GET(Kowsar_Url+"DbSetupvalue")
    Call<RetrofitResponse> OrderSendImage(
            @Query("tag") String tag,
            @Query("Image") String image,
            @Query("Code") String barcode,
            @Query("PrinterName") String PrinterName,
            @Query("PrintCount") String PrintCount
    );





}

