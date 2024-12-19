package com.kits.kowsarapp.webService.base;

import com.kits.kowsarapp.model.base.RetrofitResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Kowsar_APIInterface {



    String Kits_Url="kits/";



    @GET(Kits_Url+"Activation")
    Call<RetrofitResponse> Activation(
            @Query("ActivationCode") String ActivationCode,
            @Query("Flag") String Flag
    );
    @POST("kits/LogReport")
    Call<RetrofitResponse> LogReport(@Body RequestBody requestBody);


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

