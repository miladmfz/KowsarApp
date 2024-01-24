package com.kits.kowsarapp.application;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class WManager extends Worker {

    Context mcontext;
    Replication replication;
    CallMethod callMethod;

    public WManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mcontext = context;
        callMethod = new CallMethod(context);
        replication = new Replication(getApplicationContext());

    }
    @NonNull
    @Override
    public Result doWork() {
        if (callMethod.ReadBoolan("AutoReplication")) {
            replication.DoingReplicateAuto();
        }
        replication.SendGpsLocation();
        return Result.success();
    }

}
