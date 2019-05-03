package com.ad.adsle.services;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ad.adsle.AppConfig;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.User;
import com.ad.adsle.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gisanrin on 03/05/2019.
 */

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    AppData data;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        storeRegIdInPref(s);

        // sending reg id to your server
        sendRegistrationToServer(s);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(AppConfig.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", s);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        data = new AppData(MyApplication.getAppContext());
        User user = data.getUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (!TextUtils.isEmpty(user.getEmail())) {
            Map<String, Object> param = new HashMap<>();
            param.put("msgId", token);
            DocumentReference ref = db.collection("users").document(user.getEmail()).collection("user-data").document("signup");
            ref.update(param).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    storeRegIdInPref(token);
                }
            });
        }
    }

    private void storeRegIdInPref(String token) {
        AppData data = new AppData(MyApplication.getAppContext());
        data.setRegistrationToken(token);
    }


}
