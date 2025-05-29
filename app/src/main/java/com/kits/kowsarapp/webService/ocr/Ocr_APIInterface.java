package com.kits.kowsarapp.webService.ocr;//package com.kits.test.webService;

import com.kits.kowsarapp.model.base.RetrofitResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Ocr_APIInterface {


    String Kowsar_Url="Kowsar/";

    String Ocr_Url="Ocr/";



    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetFactor(@Field("tag") String tag
            , @Field("barcode") String barcode
            , @Field("orderby") String orderby);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OcrShortageList(@Field("tag") String tag
            , @Field("orderby") String orderby
            , @Field("isGrouped") String isGrouped
    );




    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> CheckState(@Field("tag") String tag
            , @Field("AppOCRCode") String AppOCRCode
            , @Field("State") String State
            , @Field("Deliverer") String Deliverer);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OcrControlled(@Field("tag") String tag
            , @Field("AppOCRCode") String AppOCRCode
            , @Field("State") String State
            , @Field("JobPersonRef") String JobPersonRef);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GoodShortage(@Field("tag") String tag
            , @Field("OCRFactorRowCode") String OCRFactorRowCode
            , @Field("Shortage") String Shortage);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetOcrGoodList(@Field("tag") String tag, @Field("SearchTarget") String SearchTarget);



    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetOcrFactorList(@Field("tag") String tag
            , @Field("State") String State
            , @Field("SearchTarget") String SearchTarget
            , @Field("Stack") String Stack
            , @Field("path") String path
            , @Field("HasShortage") String HasShortage
            , @Field("IsEdited") String IsEdited
            , @Field("Row") String Row
            , @Field("PageNo") String PageNo
            , @Field("SourceFlag") String SourceFlag
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> SetPackDetail(@Field("tag") String tag
            , @Field("OcrFactorCode") String OcrFactorCode
            , @Field("Reader") String Reader
            , @Field("Controler") String Controler
            , @Field("Packer") String Packer
            , @Field("PackDeliverDate") String PackDeliverDate
            , @Field("PackCount") String PackCount
            , @Field("AppOCRFactorExplain") String AppOCRFactorExplain

    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetGoodDetail(@Field("tag") String tag,
                                         @Field("GoodCode") String GoodCode);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetOcrFactorDetail(@Field("tag") String tag,
                                              @Field("OCRFactorCode") String OCRFactorCode);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetCustomerPath(@Field("tag") String tag);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetJob(
            @Field("tag") String tag
            , @Field("Where") String where
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetJobPerson(
            @Field("tag") String tag
            , @Field("Where") String where
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> ExitDelivery(
            @Field("tag") String tag
            , @Field("Where") String where
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<String> test(@Field("tag") String test);


    @POST("index.php")
    @FormUrlEncoded
    Call<String> SendImage(@Field("tag") String tag
            , @Field("barcode") String barcode
            , @Field("image") String image
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OcrDoubleCheck(@Field("tag") String tag
            , @Field("AppOCRCode") String AppOCRCode
    );
    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> SetOcrFactorExplain(@Field("tag") String tag
            , @Field("AppOCRFactorCode") String AppOCRFactorCode
            , @Field("Explain") String Explain
    );
    @POST("index.php")
    @FormUrlEncoded
    Call<String> Kowsar_log(@Field("tag") String tag
            , @Field("Device_Id") String Device_Id
            , @Field("Address_Ip") String Address_Ip
            , @Field("Server_Name") String Server_Name
            , @Field("Factor_Code") String Factor_Code
            , @Field("StrDate") String StrDate
            , @Field("Broker") String Broker
            , @Field("Explain") String Explain);

    @FormUrlEncoded
    @POST("index.php")
    Call<String> getImageData(@Field("tag") String tag,
                              @Field("image") String image,
                              @Field("barcode") String barcode
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetImage(@Field("tag") String tag,
                                    @Field("GoodCode") String GoodCode,
                                    @Field("IX") Integer IX,
                                    @Field("Scale") Integer Scale);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> SetStackLocation(@Field("tag") String tag,
                                            @Field("GoodCode") String GoodCode,
                                            @Field("StackLocation") String StackLocation
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Errorlog(
            @Field("tag") String tag
            , @Field("ErrorLog") String ErrorLog
            , @Field("Broker") String Broker
            , @Field("DeviceId") String DeviceId
            , @Field("ServerName") String ServerName
            , @Field("StrDate") String StrDate
            , @Field("VersionName") String VersionName
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderGetAppPrinter(@Field("tag") String tag);


    @FormUrlEncoded
    @POST("index.php")
    Call<RetrofitResponse> OcrSendImage(@Field("tag") String tag
            , @Field("Image") String image
            , @Field("Code") String barcode
            , @Field("PrinterName") String PrinterName
            , @Field("PrintCount") String PrintCount
    );

    @FormUrlEncoded
    @POST("index.php")
    Call<RetrofitResponse> GetDataDbsetup(@Field("tag") String tag
            , @Field("Where") String Where

    );










/*

    @POST(Ocr_Url+"GetOcrFactor")
    Call<RetrofitResponse> GetOcrFactor(@Body RequestBody requestBody);

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

    @POST(Ocr_Url+"GetOcrFactorList")
    Call<RetrofitResponse> GetOcrFactorList(@Body RequestBody requestBody );
    @POST(Ocr_Url+"SetPackDetail")
    Call<RetrofitResponse> SetPackDetail(@Body RequestBody requestBody );

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

    @GET(Ocr_Url+"GetStackCategory")
    Call<RetrofitResponse> GetStackCategory(
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



    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> SetStackLocation(@Field("tag") String tag,
                                            @Field("GoodCode") String GoodCode,
                                            @Field("StackLocation") String StackLocation
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> OrderGetAppPrinter(@Field("tag") String tag);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetFactor(@Field("tag") String tag
            , @Field("barcode") String barcode
            , @Field("orderby") String orderby);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetOcrGoodList(@Field("tag") String tag, @Field("SearchTarget") String SearchTarget);
    @FormUrlEncoded
    @POST("index.php")
    Call<RetrofitResponse> OcrSendImage(@Field("tag") String tag
            , @Field("Image") String image
            , @Field("Code") String barcode
            , @Field("PrinterName") String PrinterName
            , @Field("PrintCount") String PrintCount
    );

    @POST(Ocr_Url+"OcrPrintControler")
    Call<RetrofitResponse> OcrPrintControler(@Body RequestBody requestBody );
    @POST(Ocr_Url+"OcrPrintPacker")
    Call<RetrofitResponse> OcrPrintPacker(@Body RequestBody requestBody );


    @POST(Ocr_Url+"SaveOcrImage")
    Call<RetrofitResponse> SaveOcrImage(@Body RequestBody requestBody);



*/

}

