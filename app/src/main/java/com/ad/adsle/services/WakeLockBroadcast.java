package com.ad.adsle.services;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.stats.GCoreWakefulBroadcastReceiver;

public class WakeLockBroadcast extends GCoreWakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, UpdateService.class);
        //Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
        try {
            startWakefulService(context, service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
