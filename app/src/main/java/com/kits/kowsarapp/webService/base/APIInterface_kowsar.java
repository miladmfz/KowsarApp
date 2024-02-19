package com.kits.kowsarapp.webService.base;

import com.kits.kowsarapp.model.base.RetrofitResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface_kowsar {



    String Kits_Url="kits/";



    @GET(Kits_Url+"Activation")
    Call<RetrofitResponse> Activation(
            @Query("ActivationCode") String ActivationCode
    );


    @GET(Kits_Url+"LogReport")
    Call<RetrofitResponse> LogReport(
              @Query("tag") String tag
            , @Query("Device_Id") String Device_Id
            , @Query("Address_Ip") String Address_Ip
            , @Query("Server_Name") String Server_Name
            , @Query("Factor_Code") String Factor_Code
            , @Query("StrDate") String StrDate
            , @Query("Broker") String Broker
            , @Query("Explain") String Explain
    );

    @GET(Kits_Url+"Errorlog")
    Call<RetrofitResponse> Errorlog(
              @Query("tag") String tag
            , @Query("ErrorLog") String ErrorLog
            , @Query("Broker") String Broker
            , @Query("DeviceId") String DeviceId
            , @Query("ServerName") String ServerName
            , @Query("StrDate") String StrDate
            , @Query("VersionName") String VersionName
    );




















































/*

    @GET(Kits_Url+"Activation")
    Call<RetrofitResponse> Activation(
            @Query("tag") String tag,
            @Query("ActivationCode") String ActivationCode
    );




    @GET(Kits_Url+"Kowsar_log")
    Call<RetrofitResponse> Kowsar_log(
            @Query("tag") String tag,
            @Query("Device_Id") String Device_Id,
            @Query("Address_Ip") String Address_Ip,
            @Query("Server_Name") String Server_Name,
            @Query("Factor_Code") String Factor_Code,
            @Query("StrDate") String StrDate,
            @Query("Broker") String Broker,
            @Query("Explain") String Explain
    );

    @GET(Kits_Url+"Errorlog")
    Call<RetrofitResponse> Errorlog(
            @Query("tag") String tag,
            @Query("ErrorLog") String ErrorLog,
            @Query("Broker") String Broker,
            @Query("DeviceId") String DeviceId,
            @Query("ServerName") String ServerName,
            @Query("StrDate") String StrDate,
            @Query("VersionName") String VersionName
    );







    @GET(kowsar_Url+"GetImageFromKsr")
    Call<RetrofitResponse> GetImageFromKsr(
            @Query("tag") String tag,
            @Query("KsrImageCode") String KsrImageCode
    );

    @GET(kowsar_Url+"GetImageCustom")
    Call<RetrofitResponse> GetImageCustom(
            @Query("tag") String tag,
            @Query("ClassName") String ClassName,
            @Query("ObjectRef") String ObjectRef,
            @Query("Scale") String Scale
    );

    @GET(kowsar_Url+"DbSetupvalue")
    Call<RetrofitResponse> info(
            @Query("tag") String tag,
            @Query("Where") String Where
    );

    @GET(kowsar_Url+"customer_insert")
    Call<RetrofitResponse> customer_insert(
            @Query("tag") String tag,
            @Query("BrokerRef") String BrokerRef,
            @Query("CityCode") String CityCode,
            @Query("KodeMelli") String KodeMelli,
            @Query("FName") String FName,
            @Query("LName") String LName,
            @Query("Address") String Address,
            @Query("Phone") String Phone,
            @Query("Mobile") String Mobile,
            @Query("EMail") String EMail,
            @Query("PostCode") String PostCode,
            @Query("ZipCode") String ZipCode
    );

    @GET(kowsar_Url+"GetGoodType")
    Call<RetrofitResponse> GetGoodType(
            @Query("tag") String tag
    );



    @GET(kowsar_Url+"GetSellBroker")
    Call<RetrofitResponse> GetSellBroker(
            @Query("tag") String tag
    );





    @GET(Broker_Url+"GetColumnList")
    Call<RetrofitResponse> GetColumnList(
            @Query("tag") String tag,
            @Query("Type") String Type,
            @Query("AppType") String AppType,
            @Query("IncludeZero") String IncludeZero
    );
    @GET(Broker_Url+"BrokerStack")
    Call<RetrofitResponse> BrokerStack(@Query("tag") String tag,@Query("BrokerRef") String brokerRef);

    @GET(Broker_Url+"GetMenuBroker")
    Call<RetrofitResponse> MenuBroker(@Query("tag") String tag);

    @GET(Broker_Url+"GetMaxRepLog")
    Call<RetrofitResponse> MaxRepLogCode(@Query("tag") String tag);

    @GET(Broker_Url+"BrokerOrder")
    Call<String> BrokerOrder(
            @Query("tag") String tag,
            @Query("HeaderDetail") String HeaderDetail,
            @Query("RowDetail") String RowDetail
    );
    @POST(Broker_Url+"BrokerOrder")
    Call<String> BrokerOrder1(
            @Query("tag") String tag,
            @Field("HeaderDetail") String HeaderDetail,
            @Field("RowDetail") String RowDetail
    );

    @POST(Broker_Url+"BrokerOrder") // Replace with your actual API endpoint
    Call<ResponseBody> sendData(@Query("tag") String tag,@Body RequestBody requestBody);



    @GET(Broker_Url+"repinfo")
    Call<RetrofitResponse> RetrofitReplicate(
            @Query("tag") String tag,
            @Query("code") String code,
            @Query("table") String table,
            @Query("Where") String Where,
            @Query("reptype") String reptype,
            @Query("Reprow") String Reprow
    );

    @GET(Broker_Url+"UpdateLocation")
    Call<RetrofitResponse> UpdateLocation(
            @Query("tag") String tag,
            @Query("GpsLocations") String GpsLocations
    );



    @GET(Broker_Url+"GetAppPrinter")
    Call<RetrofitResponse> GetAppPrinter(@Query("tag") String tag);

     @GET(Broker_Url+"AppBrokerPrint")
    Call<RetrofitResponse> AppBrokerPrint(
             @Query("tag") String tag,
            @Query("Image") String Image,
            @Query("Code") String Code,
            @Query("PrinterName") String PrinterName,
            @Query("PrintCount") String PrintCount
    );

*/
}

