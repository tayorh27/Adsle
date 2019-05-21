package com.ad.adsle;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
                    //Log.e("MyApplication", "settings test = " + settings.getSignup_data());
                }
            }
        });
//        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
//        db2.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    int users_size = task.getResult().size();
//                    Settings settings = data.getSettings();
//                    settings.setTotal_users(2);
//                    data.StoreSettings(settings);
//                    Log.e("MyApplication", "number of users = " + users_size);
//                }
//            }
//        });
    }
}
