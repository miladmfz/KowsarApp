package com.kits.kowsarapp.application.base;

import android.app.Application;
import android.content.Context;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import com.behpardakht.behthirdparty.BehThirdparty;

public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iransansmobile_medium.ttf")
                .setFontAttrId(uk.co.chrisjenx.calligraphy.R.attr.fontPath)
                .build()
        );

        // راه‌اندازی SDK به‌پرداخت
//        BehThirdparty.INSTANCE.initialize(Integer.parseInt(String.valueOf()));
    }

    public App() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
}
