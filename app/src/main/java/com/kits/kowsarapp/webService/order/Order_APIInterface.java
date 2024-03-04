package com.kits.kowsarapp.webService.order;


import com.kits.kowsarapp.model.base.RetrofitResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Order_APIInterface {

    String Order_Url = "Order/";
    String Kowsar_Url = "Kowsar/";



    @GET(Order_Url+"GetObjectTypeFromDbSetup")
    Call<RetrofitResponse> GetObjectTypeFromDbSetup(
            @Query("tag") String tag,
            @Query("ObjectType") String ObjectType
    );
    @GET(Kowsar_Url+"DbSetupvalue")
    Call<RetrofitResponse> DbSetupvalue(
            @Query("tag") String tag,
            @Query("Where") String Where
    );

    @POST(Order_Url+"OrderMizList")
    Call<RetrofitResponse> OrderMizList(@Body RequestBody requestBody );

    @GET(Order_Url+"GetOrdergroupList")
    Call<RetrofitResponse> GetOrdergroupList(
            @Query("tag") String tag,
            @Query("GroupCode") String GroupCode
    );


    @POST(Order_Url+"GetOrderGoodList")
    Call<RetrofitResponse> GetOrderGoodList(@Body RequestBody requestBody );

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

    @POST(Order_Url+"OrderInfoInsert")
    Call<RetrofitResponse> OrderInfoInsert(@Body RequestBody requestBody );


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

    @POST(Order_Url+"OrderRowInsert")
    Call<RetrofitResponse> OrderRowInsert(@Body RequestBody requestBody );


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

    @GET(Kowsar_Url+"GetSellBroker")
    Call<RetrofitResponse> GetSellBroker(
            @Query("tag") String tag
    );

    @GET(Order_Url+"OrderEditInfoExplain")
    Call<RetrofitResponse> OrderEditInfoExplain(@Body RequestBody requestBody );



//**********************************************************************************


    @GET(Order_Url+"OrderGetFactorRow")
    Call<RetrofitResponse> OrderGetFactorRow(
            @Query("tag") String tag,
            @Query("AppBasketInfoRef") String AppBasketInfoRef,
            @Query("GoodGroups") String GoodGroups,
            @Query("Where") String Where
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

