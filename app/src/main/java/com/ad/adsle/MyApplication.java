package com.ad.adsle;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.Settings;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import co.paystack.android.PaystackSdk;
import io.fabric.sdk.android.Fabric;
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
        Fabric.with(this, new Crashlytics());
        sInstance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Light.ttf")
                //.setDefaultFontPath("Inconsolata-g.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        //initialize sdk
        PaystackSdk.initialize(this);
        fetchSettings();
        //Places.initialize(getApplicationContext(), getAppContext().getString(R.string.google_place_maps_key));

        // Create a new Places client instance.
        //PlacesClient placesClient = Places.createClient(this);
        //Slider.init(new PicassoImageLoadingService(getAppContext()));
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static void fetchSettings() {
        AppData data = new AppData(getAppContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("settings").document("app-settings").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Settings settings = task.getResult().toObject(Settings.class);
                    data.StoreSettings(settings);
                }
            }
        });
    }
}
