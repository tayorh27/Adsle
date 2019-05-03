package com.ad.adsle.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ad.adsle.Activities.HomeActivity;
import com.ad.adsle.AppConfig;
import com.ad.adsle.Util.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Gisanrin on 03/05/2019.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            //Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION_WITHOUT_DATA);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        } else {
            // If the app is in background, firebase itself handles the notification
            //save message to to DB
        }
    }

    private void handleDataMessage(String title, String message, JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        String timestamp = new Date().toString();

        try {
            //JSONArray data = json.getJSONArray("data_array");
            //JSONObject others_object = data.getJSONObject((data.length() - 1));

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION_WITH_DATA);
                //Intent pushNotification = new Intent(getApplicationContext(), CookOrderPreviewActivity.class);
                //pushNotification.putExtras(bundle);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound("incoming");
            } else {
                // app is in background, show the notification in notification tray
                //Intent resultIntent = new Intent(AppConfig.PUSH_NOTIFICATION_WITH_DATA);
                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                //resultIntent.putExtras(bundle);
                LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

                // check for image attachment
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);

            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, "");
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

}
