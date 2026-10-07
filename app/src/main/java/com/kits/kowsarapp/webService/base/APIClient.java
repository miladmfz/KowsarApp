package com.kits.kowsarapp.webService.base;


import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIClient {

    private static Retrofit retrofit = null;
    public static String apiBaseUrl = "";

    public static synchronized Retrofit getCleint(String BASE_URL) {
        String normalizedBaseUrl = EndpointSecurityPolicy.normalizeBaseUrl(BASE_URL);
        if (retrofit == null || !normalizedBaseUrl.equals(apiBaseUrl)) {
            apiBaseUrl = normalizedBaseUrl;
            retrofit = new Retrofit.Builder()
                    .baseUrl(apiBaseUrl)
                    .client(NetworkClientFactory.client())
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .build();
        }
        return retrofit;
    }


}
