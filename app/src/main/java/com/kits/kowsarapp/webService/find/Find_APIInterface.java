package com.kits.kowsarapp.webService.find;//package com.kits.test.webService;

import com.kits.kowsarapp.model.base.RetrofitResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Find_APIInterface {


    @GET("kits/Activation")
    Call<RetrofitResponse> Activation(
            @Query("ActivationCode") String ActivationCode,
            @Query("Flag") String Flag
    );
    @POST("index.php")
    @FormUrlEncoded
    Call <RetrofitResponse> GetImagefind(@Field("tag") String tag,
                                     @Field("ObjectRef") String ObjectRef,
                                     @Field("IX") Integer IX,
                                     @Field("Scale") Integer Scale);
    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetGoodList(@Field("tag") String tag,
                                       @Field("SearchTarget") String SearchTarget
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> BrokerStack(
            @Field("tag") String tag
            , @Field("BrokerRef") String BrokerRef
    );
    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetSellBroker(@Field("tag") String tag);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetGoodType(
            @Field("tag") String tag
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetGoodSelectedById(
            @Field("tag") String tag,
            @Field("GoodCode") String GoodCode
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
    Call<RetrofitResponse> SetSelectedFeild(@Field("tag") String tag,
                                            @Field("GoodCode") String GoodCode,
                                            @Field("SelectedFeild") String SelectedFeild
    );



}

