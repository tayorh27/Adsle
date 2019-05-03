package com.ad.adsle.Util;

import android.os.AsyncTask;

import com.ad.adsle.network.UserLocationService;

public class LocationGetterBackgroundTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
        new UserLocationService().GetCurrentLocation();
        return null;
    }
}
