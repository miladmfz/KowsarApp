package com.kits.kowsarapp.webService.base;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient_kowsar {

    private static Retrofit t = null;

    private static final String BASE_URL_log = "http://5.160.152.173:60005/api/";

    //private static final String BASE_URL_log = "http://itmali.ir/api/";

    public static synchronized Retrofit getCleint_log() {
        if (t == null) {
            t = new Retrofit.Builder()
                    .baseUrl(EndpointSecurityPolicy.normalizeFixedInfrastructureUrl(BASE_URL_log))
                    .client(NetworkClientFactory.client())
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .build();
        }
        return t;
    }
}


