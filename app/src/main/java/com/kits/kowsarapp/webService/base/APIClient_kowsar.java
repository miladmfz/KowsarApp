package com.kits.kowsarapp.webService.base;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient_kowsar {

    private static Retrofit t = null;


    private static final String BASE_URL_log = "http://192.168.1.20:60005/api/";

    public static Retrofit getCleint_log() {
        if (t == null) {
            t = new Retrofit.Builder()
                    .baseUrl(BASE_URL_log)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .build();
        }
        return t;
    }
}


