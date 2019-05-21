package com.ad.adsle.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ad.adsle.Activities.AdsleWidget;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Db.CampaignData;
import com.ad.adsle.Information.AppDetail;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.Information.User;
import com.ad.adsle.MyApplication;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateService extends Service {

    AppData data;
    CampaignData campaignData;
    User user;
    LocationDetails locationDetails;
    CampaignInformation campaignInformation;
    Settings settings;
    Utils utils;

    private PackageManager manager;

    ArrayList<CampaignInformation> campaignInformationArrayList = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyApplication.fetchSettings();
        GetAllCampaignForUser();
        return super.onStartCommand(intent, flags, startId);
    }


    private void GetAllCampaignForUser() {
        data = new AppData(MyApplication.getAppContext());
        utils = new Utils(MyApplication.getAppContext());
        campaignData = new CampaignData(MyApplication.getAppContext());
        user = data.getUser();
        locationDetails = data.getLocationDetails();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Log.e("user", locationDetails.getCity() + "====" + user.getAge());
        Query query = db.collection("campaigns")
                .whereEqualTo("status", true)
                .whereGreaterThanOrEqualTo("age_range_min", user.getAge())
                //.whereLessThanOrEqualTo("age_range_max", user.getAge())
                //.whereLessThan("reach_number", 2223)
                .whereArrayContains("locationDetails", locationDetails.getCity());
//                .whereArrayContains("gender", user.getGender());
//        query.whereArrayContains("religion", user.getReligion());
//        query.whereArrayContains("locationDetails", locationDetails.getCity());//location
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    campaignInformationArrayList.clear();
                    int size = 0;
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        CampaignInformation ci = snapshot.toObject(CampaignInformation.class);
                        //Log.e("UpdateService", "ci ===== " + ci.getCreated_date());
                        campaignInformationArrayList.add(ci);
                        size = size + 1;
                    }
                    //Log.e("UpdateService", "number of campaignData = " + size);
                    campaignData.setCampaignSize(size);
                    if (campaignInformationArrayList.size() > 0) {
                        DisplayAndSaveData(campaignData.getNext());
                        //Log.e("GetAllCampaignForUser", "updateWidgetRemoteView: yes is working");
                    } else {
                        //Log.e("UpdateService", "size ===== 0");
                    }
                } else {
                    //Log.e("UpdateService", "number of campaignData = " + task.getException());
                }
            }
        });
    }//if store data exists. app install and views. clicks not working

    private void DisplayAndSaveData(int next) {
        campaignInformation = campaignInformationArrayList.get(next);
        checkCampaignStatus();

        long campaign_reach = campaignInformation.getCampaign_reach();
        long app_install_number = campaignInformation.getApp_installs_number();
        long click_number = campaignInformation.getClicks_number();
        long reach_number = campaignInformation.getReach_number();

        int days = numberOfDaysToStartDay(campaignInformation);
        if (days < 0) {
            DisplayNextCampaign(next);
            return;
        }

        String link_option = campaignInformation.getCampaign_link_option();
        if (link_option.contentEquals("App Install")) {
            if (app_install_number < campaign_reach) {
                if (campaignData.getAppInstallId(campaignInformation.getId())) {
                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                } else {
                    boolean isAppInstalled = CheckForAppInstallsStatus();
                    if (isAppInstalled) {
                        campaignData.setAppInstallId(true, campaignInformation.getId());///check here
                        updateCampaignData(true, campaignInformation.getId(), "app_installs_number", 1);
                        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                    } else {
                        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                    }
                }
                updateWidgetRemoteView();
            } else {
                DisplayNextCampaign(next);
            }
        } else if (link_option.contentEquals("Click")) {
            if (click_number < campaign_reach) {
                if (campaignData.getClicked(campaignInformation.getId())) {
                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                } else {
                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                }
                updateWidgetRemoteView();
            } else {
                DisplayNextCampaign(next);
            }
        } else if (link_option.contentEquals("Reach")) {
            if (reach_number < campaign_reach) {
                if (campaignData.getReached(campaignInformation.getId())) {
                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                } else {
                    campaignData.setReached(true, campaignInformation.getId());
                    updateCampaignData(true, campaignInformation.getId(), "reach_number", 1);
                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                }
                updateWidgetRemoteView();
            } else {
                DisplayNextCampaign(next);
            }
        }


    }

    private int numberOfDaysToStartDay(CampaignInformation campaignInformation) {
        Calendar calendar = Calendar.getInstance();
        Date firstDate = calendar.getTime();

        Calendar cal = Calendar.getInstance();
        String[] exp = campaignInformation.getCampaign_duration_start().split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(exp[2]));
        cal.set(Calendar.MONTH, Integer.parseInt(exp[1]));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exp[0]));

        Date secondDate = cal.getTime();

        long diff = firstDate.getTime() - secondDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    private int numberOfDaysToExpiryDay(CampaignInformation campaignInformation) {
        Calendar calendar = Calendar.getInstance();
        String[] start = campaignInformation.getCampaign_duration_start().split("-");
        calendar.set(Calendar.YEAR, Integer.parseInt(start[2]));
        calendar.set(Calendar.MONTH, Integer.parseInt(start[1]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(start[0]));
        Date firstDate = calendar.getTime();

        Calendar cal = Calendar.getInstance();
        String[] exp = campaignInformation.getCampaign_duration_end().split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(exp[2]));
        cal.set(Calendar.MONTH, Integer.parseInt(exp[1]));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exp[0]));

        Date secondDate = cal.getTime();

        long diff = secondDate.getTime() - firstDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    private void DisplayNextCampaign(int next) {
        if ((next + 1) < campaignData.getCampaignSize()) {
            campaignData.setNext(next + 1);
            DisplayAndSaveData(campaignData.getNext());
        } else {
            campaignData.setNext(0);
            DisplayAndSaveData(campaignData.getNext());
        }
    }

    private boolean CheckForAppInstallsStatus() {
        boolean isInstalled = false;
        if (!campaignData.getAppInstallId(campaignInformation.getCam_application_id())) {
            manager = getPackageManager();

            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
            for (ResolveInfo ri : availableActivities) {
                String name = ri.activityInfo.packageName;
                if (name.startsWith(campaignInformation.getCam_application_id())) {
                    isInstalled = true;
                    campaignData.setAppInstallId(true, campaignInformation.getId());
                    updateCampaignData(false, campaignInformation.getId(), "app_installs_number", 1);
                    break;
                }
            }
        }
        return isInstalled;
    }

    private void checkCampaignStatus() {
        Calendar calendar = Calendar.getInstance();
        Date firstDate = calendar.getTime();

        Calendar cal = Calendar.getInstance();
        String[] exp = campaignInformation.getCampaign_duration_end().split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(exp[2]));
        cal.set(Calendar.MONTH, Integer.parseInt(exp[1]));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exp[0]));

        Date secondDate = cal.getTime();

        long diff = secondDate.getTime() - firstDate.getTime();
        int days = (int) (diff / (1000 * 60 * 60 * 24));

        if (days < 1) {
            updateCampaignStatus(false, campaignInformation.getId(), "status");
        }
        //CheckForAppInstallsStatus();
    }

    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    Bitmap bitmap = null;

    private void updateWidgetRemoteView() {
        settings = data.getSettings();
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.adsle_widget);
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap = getBitmapFromURL(campaignInformation.getCampaign_image());
            }
        }).start();
        view.setImageViewBitmap(R.id.appwidget_image, bitmap);

        //Log.e(",", campaignInformation.getCampaign_image());
        //Log.e("updateWidgetRemoteView", "updateWidgetRemoteView: is here");

        String link_option = campaignInformation.getCampaign_link_option();
        if (link_option.contentEquals("App Install")) {
            view.setTextViewText(R.id.appwidget_text, "Install app and get " + utils.getExactDataValue(String.valueOf(settings.getApp_install_data())) + " free.");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(campaignInformation.getCampaign_link()));
            // In widget we are not allowing to use intents as usually. We have to use PendingIntent instead of 'startActivity'
            PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getAppContext(), 0, intent, 0);
            // Here the basic operations the remote view can do.
            view.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent);
        } else if (link_option.contentEquals("Click")) {
            view.setTextViewText(R.id.appwidget_text, "Click this ad to get " + utils.getExactDataValue(String.valueOf(settings.getClick_data())) + " free.");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(campaignInformation.getCampaign_link()));
            intent.putExtra("category", "Click");
            intent.putExtra("cam_id", campaignInformation.getId());
            PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getAppContext(), 0, intent, 0);
            view.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent);
        } else if (link_option.contentEquals("Reach")) {
            view.setTextViewText(R.id.appwidget_text, "Powered by Adsle");
        }

        ComponentName theWidget = new ComponentName(this, AdsleWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(theWidget, view);
    }

    private void updateCampaignStatus(boolean status, String cam_id, String field) {
        Map<String, Object> params = new HashMap<>();
        params.put(field, status);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("campaigns").document(cam_id).update(params);
    }

    private void updateCampaignData(boolean giveUserData, String cam_id, String field, long value) {
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
                        if (campaignInformation.getCampaign_link_option().contentEquals("App Install")) {
                            giveUserData(settings.getApp_install_data());
                        } else if (campaignInformation.getCampaign_link_option().contentEquals("Click")) {
                            giveUserData(settings.getClick_data());
                        } else if (campaignInformation.getCampaign_link_option().contentEquals("Reach")) {
                            giveUserData(settings.getReach_data());
                        }
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
                    user.setBonus_data(String.valueOf(newValue));
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
