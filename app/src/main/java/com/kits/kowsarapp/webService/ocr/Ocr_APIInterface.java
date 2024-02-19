package com.kits.kowsarapp.webService.ocr;//package com.kits.test.webService;

import com.kits.kowsarapp.model.base.RetrofitResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Ocr_APIInterface {


    String Kowsar_Url="Kowsar/";

    String Ocr_Url="Ocr/";





    @GET(Ocr_Url+"GetOcrFactor")
    Call<RetrofitResponse> GetOcrFactor(
            @Query("tag") String tag
            , @Query("barcode") String barcode
            , @Query("orderby") String orderby
    );


    @GET(Ocr_Url+"OcrDeliverd")
    Call<RetrofitResponse> OcrDeliverd(
            @Query("tag") String tag
            , @Query("AppOCRCode") String AppOCRCode
            , @Query("State") String State
            , @Query("Deliverer") String Deliverer
    );

    @GET(Ocr_Url+"OcrControlled")
    Call<RetrofitResponse> OcrControlled(
            @Query("tag") String tag
            , @Query("AppOCRCode") String AppOCRCode
            , @Query("State") String State
            , @Query("JobPersonRef") String JobPersonRef
    );

    @GET(Ocr_Url+"SetGoodShortage")
    Call<RetrofitResponse> SetGoodShortage(
            @Query("tag") String tag,
            @Query("OCRFactorRowCode") String OCRFactorRowCode,
            @Query("Shortage") String Shortage
    );


    @GET(Ocr_Url+"GetOcrFactorList")
    Call<RetrofitResponse> GetOcrFactorList(
            @Query("tag") String tag,
            @Query("State") String State,
            @Query("SearchTarget") String SearchTarget,
            @Query("Stack") String Stack,
            @Query("path") String path,
            @Query("HasShortage") String HasShortage,
            @Query("IsEdited") String IsEdited,
            @Query("Row") String Row,
            @Query("PageNo") String PageNo
    );


    @GET(Ocr_Url+"SetPackDetail")
    Call<RetrofitResponse> SetPackDetail(
            @Query("tag") String tag,
            @Query("OcrFactorCode") String OcrFactorCode,
            @Query("Reader") String Reader,
            @Query("Controler") String Controler,
            @Query("Packer") String Packer,
            @Query("PackDeliverDate") String PackDeliverDate,
            @Query("PackCount") String PackCount,
            @Query("AppDeliverDate") String AppDeliverDate
    );


    @GET(Ocr_Url+"GetOcrGoodDetail")
    Call<RetrofitResponse> GetOcrGoodDetail(
            @Query("tag") String tag,
            @Query("GoodCode") String GoodCode
    );


    @GET(Ocr_Url+"GetOcrFactorDetail")
    Call<RetrofitResponse> GetOcrFactorDetail(
            @Query("tag") String tag,
            @Query("OCRFactorCode") String OCRFactorCode
    );


    @GET(Ocr_Url+"GetCustomerPath")
    Call<RetrofitResponse> GetCustomerPath(
            @Query("tag") String tag
    );


    @GET(Ocr_Url+"GetJob")
    Call<RetrofitResponse> GetJob(
            @Query("tag") String tag,
            @Query("Where") String where
    );


    @GET(Ocr_Url+"GetJobPerson")
    Call<RetrofitResponse> GetJobPerson(
            @Query("tag") String tag,
            @Query("Where") String where
    );


    @GET(Ocr_Url+"ExitDelivery")
    Call<RetrofitResponse> ExitDelivery(
            @Query("tag") String tag,
            @Query("Where") String where
    );





    @GET(Ocr_Url+"SaveOcrImage")
    Call<String> SaveOcrImage(
            @Query("tag") String tag,
            @Query("image") String image,
            @Query("barcode") String barcode
    );





    @GET(Kowsar_Url+"GetImage")

    Call<RetrofitResponse> GetImage(
            @Query("tag") String tag,
            @Query("GoodCode") String GoodCode,
            @Query("IX") Integer IX,
            @Query("Scale") Integer Scale
    );

    @GET(Kowsar_Url+"DbSetupvalue")
    Call<RetrofitResponse> DbSetupvalue(
            @Query("tag") String tag,
            @Query("Where") String Where

    );




}

