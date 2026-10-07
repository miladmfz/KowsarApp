package com.kits.kowsarapp.application.base;

import android.app.Application;
import android.content.Context;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import com.kits.kowsarapp.application.order.Order_OperationJournal;

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

        // A process restart makes an interrupted payment/print result ambiguous.
        // Never report it as failed or auto-retry a financial operation.
        try {
            int recovered = new Order_OperationJournal(this).recoverInterruptedOperations();
            if (recovered > 0) {
                ReleaseLog.debug("OrderOperation", "Recovered interrupted operations count=" + recovered);
            }
        } catch (RuntimeException exception) {
            // Local audit availability must never prevent application startup.
            ReleaseLog.error("OrderOperation", exception);
        }

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
