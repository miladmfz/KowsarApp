package com.kits.kowsarapp.webService.broker;

import com.kits.kowsarapp.model.base.RetrofitResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Broker_APIInterface {


    String Broker_Url = "Broker/";
    String Kowsar_Url = "Kowsar/";


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
    Call<RetrofitResponse> GetImageFromKsr(
            @Field("tag") String tag
            , @Field("KsrImageCode") String KsrImageCode
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetImageCustom(
            @Field("tag") String tag
            , @Field("ClassName") String ClassName
            , @Field("ObjectRef") String ObjectRef
            , @Field("Scale") String Scale
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> BrokerStack(
            @Field("tag") String tag
            , @Field("BrokerRef") String BrokerRef
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> MenuBroker(
            @Field("tag") String tag
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> info(
            @Field("tag") String tag,
            @Field("Where") String Where
    );




    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> MaxRepLogCode(
            @Field("tag") String tag
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetGoodType(
            @Field("tag") String tag
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetColumnList(
            @Field("tag") String tag
            , @Field("Type") String Type
            , @Field("AppType") String AppType
            , @Field("IncludeZero") String IncludeZero
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> RetrofitReplicate(
            @Field("tag") String tag
            , @Field("code") String code
            , @Field("table") String table
            , @Field("Where") String Where
            , @Field("reptype") String reptype
            , @Field("Reprow") String Reprow
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> BrokerOrder(
            @Field("tag") String tag
            , @Field("PFHDQASW") String PFHDQASW
            , @Field("PFDTQASW") String PFDTQASW

    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetSellBroker(@Field("tag") String tag);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> UpdateLocation(@Field("tag") String tag, @Field("GpsLocations") String GpsLocations);


/*


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



*/

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

