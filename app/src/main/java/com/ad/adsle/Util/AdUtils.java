package com.ad.adsle.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ad.adsle.Callbacks.AdsCallback;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Db.CampaignData;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.Information.User;
import com.ad.adsle.MyApplication;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class AdUtils {

    Activity context;
    AppData data;
    CampaignData campaignData;
    User user;
    LocationDetails locationDetails;
    CampaignInformation campaignInformation;
    Settings settings;
    Utils utils;
    AdsCallback adsCallback;

    private PackageManager manager;

    ArrayList<CampaignInformation> campaignInformationArrayList = new ArrayList<>();

    GifImageView imageView;
    TextView textView;

    public AdUtils(Activity context, AdsCallback adsCallback) {
        this.context = context;
        this.adsCallback = adsCallback;
        data = new AppData(MyApplication.getAppContext());
        utils = new Utils(MyApplication.getAppContext());
        campaignData = new CampaignData(MyApplication.getAppContext());
        user = data.getUser();
        locationDetails = data.getLocationDetails();
    }

    public void StartAds() {
        MyApplication.fetchSettings();
        GetAllCampaignForUser();
    }

    private void GetAllCampaignForUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("campaigns")
                .whereEqualTo("status", true)
                .whereGreaterThanOrEqualTo("age_range_min", user.getAge())
                .whereArrayContains("locationDetails", locationDetails.getCity());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    campaignInformationArrayList.clear();
                    int size = 0;
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        CampaignInformation ci = snapshot.toObject(CampaignInformation.class);
                        campaignInformationArrayList.add(ci);
                        size = size + 1;
                    }
                    Log.e("UpdateService", "number of campaignData = " + size);
                    campaignData.setCampaignSize(size);
                    if (campaignInformationArrayList.size() > 0) {
                        DisplayAndSaveData(campaignData.getNext());
                        Log.e("GetAllCampaignForUser", "updateWidgetRemoteView: yes is working");
                    } else {
                        Log.e("UpdateService", "size ===== 0");
                    }
                } else {
                    Log.e("UpdateService", "number of campaignData = " + task.getException());
                }
            }
        });
    }//if store data exists. app install and views. clicks not working

    private void DisplayAndSaveData(int next) {
        if (next >= campaignData.getCampaignSize()) {
            campaignData.setNext(0);
//            DisplayAndSaveData(campaignData.getNext());
//            DisplayAndSaveData(0);
            DisplayNextCampaign(0);
            return;
        }
        campaignInformation = campaignInformationArrayList.get(next);
        checkCampaignStatus();

        if (campaignData.getExpired(campaignInformation.getId())) {
            int newNext = next + 1;
//            DisplayAndSaveData(newNext);
            DisplayNextCampaign(newNext);
            return;
        }

        Log.e("UpdateService", "DisplayAndSaveData ================ 0");
        long campaign_reach = campaignInformation.getCampaign_reach();
        long app_install_number = campaignInformation.getApp_installs_number();
        long click_number = campaignInformation.getClicks_number();
        long reach_number = campaignInformation.getReach_number();

        int days = numberOfDaysToStartDay(campaignInformation);
        if (days < 0) {
            int newNext = next + 1;
            DisplayNextCampaign(newNext);
            return;
        }

        Log.e("UpdateService", "DisplayAndSaveData ================ 1");
        Log.e("UpdateService", "Campaign Type ================ " + campaignInformation.getCampaign_link_option());

//        if(reach_number <= campaign_reach){
//
//        }

        String link_option = campaignInformation.getCampaign_link_option();
        if (link_option.contentEquals("App Install")) {
            Log.e("UpdateService", "DisplayAndSaveData ================ 2");
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
                int newNext = next + 1;
                DisplayNextCampaign(newNext);
            } else {
                updateWidgetRemoteView();
                updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                int newNext = next + 1;
                DisplayNextCampaign(newNext);
            }
        } else if (link_option.contentEquals("Click")) {
            Log.e("UpdateService", "DisplayAndSaveData ================ 3");
            if (click_number < campaign_reach) {
                if (campaignData.getClicked(campaignInformation.getId())) {
                    Log.e("UpdateService", "DisplayAndSaveData ================ 3.1");
                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                } else {
                    Log.e("UpdateService", "DisplayAndSaveData ================ 3.2");
                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                }
                Log.e("UpdateService", "DisplayAndSaveData ================ 3.3");
                updateWidgetRemoteView();
                int newNext = next + 1;
                DisplayNextCampaign(newNext);
            } else {
                updateWidgetRemoteView();
                updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                Log.e("UpdateService", "DisplayAndSaveData ================ 3.4");
                int newNext = next + 1;
                DisplayNextCampaign(newNext);
            }
        } else if (link_option.contentEquals("Reach")) {
            Log.e("UpdateService", "DisplayAndSaveData ================ 4");
            if (reach_number < campaign_reach) {
                if (campaignData.getReached(campaignInformation.getId())) {
                    Log.e("UpdateService", "DisplayAndSaveData ================ 4.1");
                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                } else {
                    Log.e("UpdateService", "DisplayAndSaveData ================ 4.2");
                    campaignData.setReached(true, campaignInformation.getId());
                    updateCampaignData(true, campaignInformation.getId(), "reach_number", 1);
                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                }
                updateWidgetRemoteView();
                int newNext = next + 1;
                DisplayNextCampaign(newNext);
            } else {
                updateWidgetRemoteView();
                updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                int newNext = next + 1;
                DisplayNextCampaign(newNext);
            }
        }


    }

    public int numberOfDaysToStartDay(CampaignInformation campaignInformation) {
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

    public int numberOfDaysToExpiryDay(CampaignInformation campaignInformation) {
        Calendar calendar = Calendar.getInstance();
//        String[] start = campaignInformation.getCampaign_duration_start().split("-");
//        calendar.set(Calendar.YEAR, Integer.parseInt(start[2]));
//        calendar.set(Calendar.MONTH, Integer.parseInt(start[1]));
//        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(start[0]));
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
        if (next < campaignData.getCampaignSize()) {
            campaignData.setNext(next);
        } else {
            campaignData.setNext(0);
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //Log.e("DisplayNextCampaign", "NEXT: =========== " + campaignData.getNext());
                //Log.e("DisplayNextCampaign", "DisplayNextCampaign: =========== " + campaignInformationArrayList.get(campaignData.getNext()).getTitle());
                DisplayAndSaveData(campaignData.getNext());
            }
        }, 5000);
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

        long diff = firstDate.getTime() - secondDate.getTime();
        int days = (int) (diff / (1000 * 60 * 60 * 24));

        if (days > 0) {
            updateCampaignStatus(false, campaignInformation.getId(), "status");
            campaignData.setExpired(true, campaignInformation.getId());
        }
        //CheckForAppInstallsStatus();
    }

    private void updateCampaignStatus(boolean status, String cam_id, String field) {
        Map<String, Object> params = new HashMap<>();
        params.put(field, status);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("campaigns").document(cam_id).update(params);
    }

    public void updateCampaignData(boolean giveUserData, String cam_id, String field, long value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference sfDocRef = db.collection("campaigns").document(cam_id);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                long newValue = snapshot.getLong(field) + value;
                transaction.update(sfDocRef, field, newValue);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
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
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Transaction failure.", e);
                    }
                });
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference updateRef = db.collection("campaigns").document(cam_id);
//        updateRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    long given_value = (long) task.getResult().get(field);
//                    long newValue = given_value + value;
//                    Map<String, Object> params = new HashMap<>();
//                    params.put(field, newValue);
//                    updateRef.update(params);
//                    if (giveUserData && user.getTag().contentEquals("user")) {
//                        settings = data.getSettings();
//                        if (campaignInformation.getCampaign_link_option().contentEquals("App Install")) {
//                            giveUserData(settings.getApp_install_data());
//                        } else if (campaignInformation.getCampaign_link_option().contentEquals("Click")) {
//                            giveUserData(settings.getClick_data());
//                        } else if (campaignInformation.getCampaign_link_option().contentEquals("Reach")) {
//                            giveUserData(settings.getReach_data());
//                        }
//                    }
//                    StoreCampaignTrackingData(field, cam_id);
//                }
//            }
//        });
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
        User mUser = data.getUser();
        Calendar calendar = Calendar.getInstance();
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH);
        int y = calendar.get(Calendar.YEAR);
        String date = d + "-" + (m + 1) + "-" + y;
        String path = "adsle-campaign-tracking/" + cam_id + "/" + date + "/" + field + "/" + mUser.getId();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference(path);
        dr.child("signup-data").setValue(data.getCampaignUser());
        dr.child("location-data").setValue(data.getLocationDetails());
        dr.child("device-data").setValue(data.getDeviceDetails());

//        CollectionReference storeRef = db.collection("adsle-campaign-tracking").document(cam_id).collection(date);
//        storeRef.document(field).collection(user.getId()).document("signup-data").set(data.getCampaignUser());
//        storeRef.document(field).collection(user.getId()).document("location-data").set(data.getLocationDetails());
//        storeRef.document(field).collection(user.getId()).document("device-data").set(data.getDeviceDetails());

//        Map<String, Object> params = new HashMap<>();
//        params.put("track-date", date);
//        storeRef.document().set(params);
    }


//    private void DisplayAndSaveData(int next) {
//        if (next >= campaignData.getCampaignSize()) {
//            campaignData.setNext(0);
//            DisplayAndSaveData(campaignData.getNext());
//            return;
//        }
//        campaignInformation = campaignInformationArrayList.get(next);
//        checkCampaignStatus(campaignInformation);
//        Log.e("UpdateService", "DisplayAndSaveData ================ 0");
//        long campaign_reach = campaignInformation.getCampaign_reach();
//        long app_install_number = campaignInformation.getApp_installs_number();
//        long click_number = campaignInformation.getClicks_number();
//        long reach_number = campaignInformation.getReach_number();
//
//        int days = numberOfDaysToStartDay(campaignInformation);
//        if (days < 0) {
//            int newNext = next + 1;
//            DisplayNextCampaign(newNext);
//            return;
//        }
//
//        Log.e("UpdateService", "DisplayAndSaveData ================ 1");
//        Log.e("UpdateService", "Campaign Type ================ " + campaignInformation.getCampaign_link_option());
//
//        String link_option = campaignInformation.getCampaign_link_option();
//        if (link_option.contentEquals("App Install")) {
//            Log.e("UpdateService", "DisplayAndSaveData ================ 2");
//            if (app_install_number < campaign_reach) {
//                if (campaignData.getAppInstallId(campaignInformation.getId())) {
//                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//                } else {
//                    boolean isAppInstalled = CheckForAppInstallsStatus();
//                    if (isAppInstalled) {
//                        campaignData.setAppInstallId(true, campaignInformation.getId());///check here
//                        updateCampaignData(true, campaignInformation.getId(), "app_installs_number", 1);
//                        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//                    } else {
//                        updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//                    }
//                }
//                updateWidgetRemoteView();
//                int newNext = next + 1;
//                DisplayNextCampaign(newNext);
//            } else {
//                updateWidgetRemoteView();
//                updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//                int newNext = next + 1;
//                DisplayNextCampaign(newNext);
//            }
//        } else if (link_option.contentEquals("Click")) {
//            Log.e("UpdateService", "DisplayAndSaveData ================ 3");
//            if (click_number < campaign_reach) {
//                if (campaignData.getClicked(campaignInformation.getId())) {
//                    Log.e("UpdateService", "DisplayAndSaveData ================ 3.1");
//                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//                } else {
//                    Log.e("UpdateService", "DisplayAndSaveData ================ 3.2");
//                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//                }
//                Log.e("UpdateService", "DisplayAndSaveData ================ 3.3");
//                updateWidgetRemoteView();
//                int newNext = next + 1;
//                DisplayNextCampaign(newNext);
//            } else {
//                updateWidgetRemoteView();
//                updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//                Log.e("UpdateService", "DisplayAndSaveData ================ 3.4");
//                int newNext = next + 1;
//                DisplayNextCampaign(newNext);
//            }
//        } else if (link_option.contentEquals("Reach")) {
//            Log.e("UpdateService", "DisplayAndSaveData ================ 4");
//            if (reach_number < campaign_reach) {
//                if (campaignData.getReached(campaignInformation.getId())) {
//                    Log.e("UpdateService", "DisplayAndSaveData ================ 4.1");
//                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//                } else {
//                    Log.e("UpdateService", "DisplayAndSaveData ================ 4.2");
//                    campaignData.setReached(true, campaignInformation.getId());
//                    updateCampaignData(true, campaignInformation.getId(), "reach_number", 1);
//                    updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//                }
//                updateWidgetRemoteView();
//                int newNext = next + 1;
//                DisplayNextCampaign(newNext);
//            } else {
//                updateWidgetRemoteView();
//                updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
//                int newNext = next + 1;
//                DisplayNextCampaign(newNext);
//            }
//        }
//
//
//    }
//
//    private void DisplayNextCampaign(int next) {
//        if (next < campaignData.getCampaignSize()) {
//            campaignData.setNext(next);
//        } else {
//            campaignData.setNext(0);
//        }
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                DisplayAndSaveData(campaignData.getNext());
//            }
//        }, 60000);
//    }

    public boolean CheckForAppInstallsStatus() {
        boolean isInstalled = false;
        if (!campaignData.getAppInstallId(campaignInformation.getCam_application_id())) {
            manager = context.getPackageManager();

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

    public int checkCampaignStatus(CampaignInformation current) {
        Calendar calendar = Calendar.getInstance();
        Date firstDate = calendar.getTime();

        Calendar cal = Calendar.getInstance();
//        String[] exp = campaignInformation.getCampaign_duration_end().split("-");
        String[] exp = current.getCampaign_duration_end().split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(exp[2]));
        cal.set(Calendar.MONTH, Integer.parseInt(exp[1]));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exp[0]));

        Date secondDate = cal.getTime();

        long diff = firstDate.getTime() - secondDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));

//        if (days > 0) {
//            updateCampaignStatus(false, campaignInformation.getId(), "status");
//        }
        //CheckForAppInstallsStatus();
    }

    private void updateWidgetRemoteView() {
        settings = data.getSettings();
        String adTitle = "", adType = "", adLink = "";
        String link_option = campaignInformation.getCampaign_link_option();
        if (link_option.contentEquals("App Install")) {
            adTitle = "Install app and get " + utils.getExactDataValue(String.valueOf(settings.getApp_install_data())) + " free.";
            adType = "App Install";
            adLink = campaignInformation.getCampaign_link();
        } else if (link_option.contentEquals("Click")) {
            adTitle = "Click this ad to get " + utils.getExactDataValue(String.valueOf(settings.getClick_data())) + " free.";
            adType = "Click";
            adLink = campaignInformation.getCampaign_link();
        } else {
            adTitle = "Powered by Adsle";
            adType = "Reach";
            adLink = "";
        }
        if (adsCallback != null) {
            adsCallback.setCurrentAd(campaignInformation.getId(), campaignInformation.getCampaign_image(), adTitle, adType, adLink);
            adsCallback.onImageAdClicked(campaignInformation.getId(), adType, adLink);
        }
    }

    private void updateWidgetRemoteView2() {
        settings = data.getSettings();
        if (campaignInformation.getCampaign_image().toLowerCase().contains(".gif")) {
            try {
                GifDrawable gifFromUri = new GifDrawable(context.getContentResolver(), Uri.parse(campaignInformation.getCampaign_image()));
                imageView.setImageDrawable(gifFromUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Glide.with(context)
                        .asBitmap()
                        .load(campaignInformation.getCampaign_image())//.into(appWidgetTarget);
                        .into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e("updateWidgetRemoteView", "updateWidgetRemoteView: is here");

        String link_option = campaignInformation.getCampaign_link_option();
        if (link_option.contentEquals("App Install")) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Install app and get " + utils.getExactDataValue(String.valueOf(settings.getApp_install_data())) + " free.");
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!campaignData.getClicked(campaignInformation.getCampaign_link())) {
                                campaignData.setClicked(true, campaignInformation.getCampaign_link());
                                updateCampaignData(false, campaignInformation.getId(), "clicks_number", 1);
                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(campaignInformation.getCampaign_link()));
                            context.startActivity(intent);
                        }
                    });
                }
            });
        } else if (link_option.contentEquals("Click")) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Click this ad to get " + utils.getExactDataValue(String.valueOf(settings.getClick_data())) + " free.");
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!campaignData.getClicked(campaignInformation.getCampaign_link())) {
                                campaignData.setClicked(true, campaignInformation.getCampaign_link());
                                updateCampaignData(true, campaignInformation.getId(), "clicks_number", 1);
                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(campaignInformation.getCampaign_link()));
                            context.startActivity(intent);
                        }
                    });
                }
            });
        } else if (link_option.contentEquals("Reach")) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Powered by Adsle");
                }
            });
        }
    }
}
