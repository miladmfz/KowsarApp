package com.kits.kowsarapp.webService.broker;

import com.kits.kowsarapp.model.base.RetrofitResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Broker_APIInterface {


    String Broker_Url = "Broker/";
    String Kowsar_Url = "Kowsar/";





    @GET(Broker_Url+"BrokerStack")

    Call<RetrofitResponse> BrokerStack(
            @Query("tag") String tag
            , @Query("BrokerRef") String BrokerRef
    );

    @GET(Broker_Url+"GetMenuBroker")

    Call<RetrofitResponse> GetMenuBroker(
            @Query("tag") String tag
    );



    @GET(Broker_Url+"GetMaxRepLog")

    Call<RetrofitResponse> GetMaxRepLog(
            @Query("tag") String tag
    );


    @GET(Broker_Url+"GetColumnList")

    Call<RetrofitResponse> GetColumnList(
            @Query("tag") String tag
            , @Query("Type") String Type
            , @Query("AppType") String AppType
            , @Query("IncludeZero") String IncludeZero
    );


    @GET(Broker_Url+"RetrofitReplicate")

    Call<RetrofitResponse> RetrofitReplicate(
            @Query("tag") String tag
            , @Query("code") String code
            , @Query("table") String table
            , @Query("Where") String Where
            , @Query("reptype") String reptype
            , @Query("Reprow") String Reprow
    );
    @GET(Broker_Url+"UpdateLocation")
    Call<RetrofitResponse> UpdateLocation(
            @Query("tag") String tag,
            @Query("GpsLocations") String GpsLocations
    );


    @POST(Broker_Url+"BrokerOrder") // Replace with your actual API endpoint
    Call<RetrofitResponse> BrokerOrder(@Body RequestBody requestBody);


//    @GET(Broker_Url+"BrokerOrder") // Replace with your actual API endpoint
//    Call<RetrofitResponse> sendData(
//            @Query("HeaderDetails") String HeaderDetail,
//            @Query("RowDetails") String RowDetail
//    );

    @GET(Kowsar_Url+"GetImageFromKsr")
    Call<RetrofitResponse> GetImageFromKsr(
            @Query("tag") String tag
            , @Query("KsrImageCode") String KsrImageCode
    );

    @GET(Kowsar_Url+"GetImageCustom")
    Call<RetrofitResponse> GetImageCustom(
            @Query("tag") String tag
            , @Query("ClassName") String ClassName
            , @Query("ObjectRef") String ObjectRef
            , @Query("Scale") String Scale
    );


    @GET(Kowsar_Url+"DbSetupvalue")
    Call<RetrofitResponse> DbSetupvalue(
            @Query("tag") String tag,
            @Query("Where") String Where
    );

    @GET(Kowsar_Url+"GetSellBroker")
    Call<RetrofitResponse> GetSellBroker(
            @Query("tag") String tag
    );

    @GET(Kowsar_Url+"GetGoodType")

    Call<RetrofitResponse> GetGoodType(
            @Query("tag") String tag
    );





/*

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetAppPrinter(
            @Field("tag") String tag
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> AppBrokerPrint(
            @Field("tag") String tag,
            @Field("Image") String Image,
            @Field("Code") String Code,
            @Field("PrinterName") String PrinterName,
            @Field("PrintCount") String PrintCount
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> SetLocation(
            @Field("tag") String tag
            , @Field("Longitude") String Altitude
            , @Field("Latitude") String Latitude
            , @Field("BrokerRef") String BrokerRef
            , @Field("GpsDate") String GpsDate
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetLocation(
            @Field("tag") String tag
    );

*/
}

