package com.kits.kowsarapp.webService.find;//package com.kits.test.webService;

import com.kits.kowsarapp.model.base.RetrofitResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Find_APIInterface {

    String Search_Url = "Search/";
    String Kowsar_Url = "Kowsar/";



    @GET(Kowsar_Url+"GetImage")
    Call<RetrofitResponse> GetImage(
            @Query("tag") String tag,
            @Query("ObjectRef") String ObjectRef,
            @Query("IX") String IX,
            @Query("Scale") String Scale
    );
    @GET(Kowsar_Url+"GetSellBroker")
    Call<RetrofitResponse> GetSellBroker(
            @Query("tag") String tag
    );

    @GET(Kowsar_Url+"GetGoodType")

    Call<RetrofitResponse> GetGoodType(
            @Query("tag") String tag
    );



    @POST(Search_Url+"GetGoodList")
    @FormUrlEncoded
    Call<RetrofitResponse> GetGoodList(@Body RequestBody requestBody );




    @GET(Search_Url+"BrokerStack")

    Call<RetrofitResponse> BrokerStack(
            @Query("tag") String tag
            , @Query("BrokerRef") String BrokerRef
    );




    @GET(Search_Url+"GetColumnList")

    Call<RetrofitResponse> GetColumnList(
            @Query("tag") String tag
            , @Query("Type") String Type
            , @Query("AppType") String AppType
            , @Query("IncludeZero") String IncludeZero
    );








}

