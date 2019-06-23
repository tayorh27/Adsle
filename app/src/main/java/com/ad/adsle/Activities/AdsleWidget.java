package com.ad.adsle.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Db.CampaignData;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.Information.User;
import com.ad.adsle.MyApplication;
import com.ad.adsle.R;
import com.ad.adsle.services.UpdateService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class AdsleWidget extends AppWidgetProvider {

    private PendingIntent service;
    User user;
    AppData data;
    Settings settings;

//    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
//                                int appWidgetId) {
//
//        Log.e("updateAppWidget", "updateAppWidget: is called 1 ");
//        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.adsle_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
//        Log.e("updateAppWidget", "updateAppWidget: is called 2");
//    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent i = new Intent(context, UpdateService.class);

        if (service == null) {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 3600000, service);
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
        Log.e("onUpdate", "onUpdate: is called 3");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.e("onReceive", "onReceive: is called 5");
        if (intent != null) {
            if (intent.hasExtra("pinnedWidgetCallbackIntent")) {
                boolean isFromHomeActivity = intent.getBooleanExtra("pinnedWidgetCallbackIntent", false);
                data = new AppData(MyApplication.getAppContext());
                data.setFromHomeActivity(isFromHomeActivity);
            }
        }
//        Log.e("onReceive", "onReceive");
//        Log.e("onReceive", intent.getAction());
//        Toast.makeText(context, "Button Clicked.....!!!", Toast.LENGTH_SHORT).show();
//        Bundle bundle = intent.getExtras();
//
//        if (bundle != null && bundle.getString("category") != null) {
//            String id = intent.getStringExtra("cam_id");
//            CampaignData data = new CampaignData(MyApplication.getAppContext());
//            data.setClicked(true, id);
//            updateCampaignData(true, id, "clicks_number", 1);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.e("onEnabled", "onEnabled: is called 4");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

//    2019-05-14 18:31:17.398 1965-1965/com.ad.adsle E/onEnabled: onEnabled: is called 4
//            2019-05-14 18:31:17.408 1965-1965/com.ad.adsle E/updateAppWidget: updateAppWidget: is called 1
//            2019-05-14 18:31:17.412 1965-1965/com.ad.adsle E/updateAppWidget: updateAppWidget: is called 2
//            2019-05-14 18:31:17.412 1965-1965/com.ad.adsle E/onUpdate: onUpdate: is called 3

    private void updateCampaignData(boolean giveUserData, String cam_id, String field, long value) {
        data = new AppData(MyApplication.getAppContext());
        user = data.getUser();
        settings = data.getSettings();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference updateRef = db.collection("campaigns").document(cam_id);
        updateRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    long given_value = (long) task.getResult().get(field);
                    long newValue = given_value + value;
                    Map<String, Object> params = new HashMap<>();
                    params.put(field, newValue);
                    updateRef.update(params);
                    if (giveUserData && user.getTag().contentEquals("user")) {
                        settings = data.getSettings();
                        giveUserData(settings.getClick_data());
                    }
                    StoreCampaignTrackingData(field, cam_id);
                }
            }
        });
    }

    private void giveUserData(long dataBonus) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference updateUserRef = db.collection("users").document(user.getEmail()).collection("user-data").document("signup");
        updateUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    long bonus_data = Long.parseLong(task.getResult().get("bonus_data").toString());
                    long newValue = bonus_data + dataBonus;
                    Map<String, Object> params = new HashMap<>();
                    params.put("bonus_data", newValue);
                    user.setBonus_data(newValue);
                    data.StoreUsers(user);
                    updateUserRef.update(params);
                }
            }
        });
    }

    private void StoreCampaignTrackingData(String field, String cam_id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Calendar calendar = Calendar.getInstance();
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH);
        int y = calendar.get(Calendar.YEAR);
        String date = d + "-" + (m + 1) + "-" + y;
        CollectionReference storeRef = db.collection("adsle-campaign-tracking").document(cam_id).collection(date);
        storeRef.document(field).collection("signup").document("user-data").set(data.getCampaignUser());
        storeRef.document(field).collection("location").document("user-data").set(data.getLocationDetails());
        storeRef.document(field).collection("device").document("user-data").set(data.getDeviceDetails());

        Map<String, Object> params = new HashMap<>();
        params.put("track-date", date);
        storeRef.document().set(params);
    }
}

