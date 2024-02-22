package com.kits.kowsarapp.webService.base;

import com.kits.kowsarapp.model.base.RetrofitResponse;

import retrofit2.Call;
import retrofit2.http.GET;
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





}

