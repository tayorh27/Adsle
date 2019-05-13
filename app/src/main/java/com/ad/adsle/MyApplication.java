package com.ad.adsle;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import co.paystack.android.PaystackSdk;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends Application {

    private static MyApplication sInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Light.ttf")
                //.setDefaultFontPath("Inconsolata-g.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        //initialize sdk
        PaystackSdk.initialize(this);
        Places.initialize(getApplicationContext(), getAppContext().getString(R.string.google_place_maps_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        //Slider.init(new PicassoImageLoadingService(getAppContext()));
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public static MyApplication getInstance() {
        return sInstance;
    }
}
