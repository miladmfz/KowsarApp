package com.kits.kowsarapp.application.base;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kits.kowsarapp.application.broker.Broker_Replication;


public class WManager extends Worker {

    Context mcontext;
    Broker_Replication replication;
    CallMethod callMethod;

    public WManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mcontext = context;
        callMethod = new CallMethod(context);
        replication = new Broker_Replication(getApplicationContext());

    }
    @NonNull
    @Override
    public Result doWork() {

        if (callMethod.ReadBoolan("AutoReplication")) {
            replication.DoingReplicateAuto();
        }
//        replication.SendGpsLocation();

        return Result.success();
//        return null;
    }

}
