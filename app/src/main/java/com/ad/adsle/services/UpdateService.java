package com.ad.adsle.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ad.adsle.Activities.AdsleWidget;
import com.ad.adsle.Activities.GetCurrentAdActivity;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Db.CampaignData;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.Information.User;
import com.ad.adsle.MyApplication;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.AnimationListener;

public class UpdateService extends IntentService {

    public UpdateService() {
        super("UpdateService");
    }

    AppData data;
    CampaignData campaignData;
    User user;
    LocationDetails locationDetails;
    CampaignInformation campaignInformation;
    Settings settings;
    Utils utils;

    private PackageManager manager;

    ArrayList<CampaignInformation> campaignInformationArrayList = new ArrayList<>();
    Bitmap bitmap = null;

    private WebView webView;
    private WindowManager winManager;

    private final WebViewClient client = new WebViewClient() {
        public void onPageFinished(WebView view, String url) {
            final Point p = new Point();
            winManager.getDefaultDisplay().getSize(p);

            webView.measure(View.MeasureSpec.makeMeasureSpec((p.x < p.y ? p.y : p.x),
                    View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec((p.x < p.y ? p.x : p.y),
                            View.MeasureSpec.EXACTLY));
            webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());

            webView.postDelayed(capture, 1000);
        }
    };

    private final Runnable capture = new Runnable() {
        @Override
        public void run() {
            try {
                final Bitmap bmp = Bitmap.createBitmap(webView.getWidth(),
                        webView.getHeight(), Bitmap.Config.ARGB_8888);
                final Canvas c = new Canvas(bmp);
                webView.draw(c);

                //updateWidgets(bmp);
                bitmap = bmp;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

            stopSelf();
        }
    };


    private void setUpWebView(String ad_image) {
        winManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        webView = new WebView(this);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebViewClient(client);

        final WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.width = 0;
        params.height = 0;

        final FrameLayout frame = new FrameLayout(this);
        frame.addView(webView);
        winManager.addView(frame, params);

        webView.loadUrl(ad_image);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("Adsle", "onHandleIntent: here");
        MyApplication.fetchSettings();
        GetAllCampaignForUser();
        WakeLockBroadcast.completeWakefulIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Adsle", "onStartCommand: here");
        return super.onStartCommand(intent, flags, startId);
    }


    public void GetAllCampaignForUser() {
        data = new AppData(MyApplication.getAppContext());
        utils = new Utils(MyApplication.getAppContext());
        campaignData = new CampaignData(MyApplication.getAppContext());
        user = data.getUser();
        locationDetails = data.getLocationDetails();
        //campaignData.Clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Log.e("user", locationDetails.getCity() + "====" + user.getAge());
        Query query = db.collection("campaigns")
                .whereEqualTo("status", true)
                //.whereGreaterThanOrEqualTo("age_range_min", user.getAge())//make max
                //.whereLessThanOrEqualTo("age_range_min", user.getAge())
                .whereGreaterThanOrEqualTo("age_range_max", user.getAge())
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

    public void DisplayAndSaveData(int next) {
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
        }, 60000);
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

        long diff = firstDate.getTime() - secondDate.getTime();
        int days = (int) (diff / (1000 * 60 * 60 * 24));

        if (days > 0) {
            updateCampaignStatus(false, campaignInformation.getId(), "status");
            campaignData.setExpired(true, campaignInformation.getId());
        }
        //CheckForAppInstallsStatus();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void updateWidgetRemoteView() {
        settings = data.getSettings();
        ComponentName theWidget = new ComponentName(this, AdsleWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.adsle_widget);
//        FutureTarget<Bitmap> futureTarget = Glide.with(MyApplication.getAppContext())
//                .asBitmap()
//                .load(campaignInformation.getCampaign_image())
//                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
//        try {
//            view.setImageViewBitmap(R.id.appwidget_image, futureTarget.get());
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        Glide.with(MyApplication.getAppContext()).clear(futureTarget);
        //https://firebasestorage.googleapis.com/v0/b/adsle-ce462.appspot.com/o/images%2F1558698363078.gif?alt=media&token=be9a0e23-bcb8-4196-b1ce-33041ab5500e

        if (campaignInformation.getCampaign_image().toLowerCase().contains(".gif")) {
            Log.e("updateService", "updateWidgetRemoteView: isGif");
            ImageView imageView = new ImageView(MyApplication.getAppContext());
            imageView.setId(R.id.appwidget_image);
            try {
//                ImageViewTarget<GifDrawable> imageViewTarget = new ImageViewTarget<GifDrawable>() {
//                    @Override
//                    protected void setResource(@Nullable GifDrawable resource) {
//                        resource.start();
//                    }
//                };
                GifDrawable gifDrawable = Glide.with(MyApplication.getAppContext())
                        .asGif()
                        .load(campaignInformation.getCampaign_image())
                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
                pl.droidsonroids.gif.GifDrawable drawable = new pl.droidsonroids.gif.GifDrawable(gifDrawable.getBuffer());
                view.setImageViewBitmap(R.id.appwidget_image, drawable.getCurrentFrame());
//                //Bitmap bitmap = drawableToBitmap(drawable);
//                Bitmap bitmap = (BitmapDrawable)drawable;
//                view.setImageViewBitmap(R.id.appwidget_image, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                AppWidgetTarget appWidgetTarget = new AppWidgetTarget(MyApplication.getAppContext(), R.id.appwidget_image, view, theWidget);
                Glide.with(MyApplication.getAppContext())
                        .asBitmap()
                        .load(campaignInformation.getCampaign_image()).into(appWidgetTarget);
//                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                        .get();
//
//                view.setImageViewBitmap(R.id.appwidget_image, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //Glide.with(MyApplication.getAppContext()).;
        //view.setImageViewUri(R.id.appwidget_image, Uri.parse(campaignInformation.getCampaign_image()));

        //Log.e(",", campaignInformation.getCampaign_image());
        Log.e("updateWidgetRemoteView", "updateWidgetRemoteView: is here");

        String link_option = campaignInformation.getCampaign_link_option();
        if (link_option.contentEquals("App Install")) {
            view.setTextViewText(R.id.appwidget_text, "Install app and get " + utils.getExactDataValue(String.valueOf(settings.getApp_install_data())) + " free.");
            Intent intent = new Intent(MyApplication.getAppContext(), GetCurrentAdActivity.class);
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(campaignInformation.getCampaign_link()));
            //intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, manager.getAppWidgetIds(theWidget));
            intent.putExtra("category", "App Install");
            intent.putExtra("link", campaignInformation.getCampaign_link());
            intent.putExtra("cam_id", campaignInformation.getId());
            // In widget we are not allowing to use intents as usually. We have to use PendingIntent instead of 'startActivity'
            PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getAppContext(), 0, intent, 0);
            // Here the basic operations the remote view can do.
            view.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent);
        } else if (link_option.contentEquals("Click")) {
            view.setTextViewText(R.id.appwidget_text, "Click this ad to get " + utils.getExactDataValue(String.valueOf(settings.getClick_data())) + " free.");
            Intent intent = new Intent(MyApplication.getAppContext(), GetCurrentAdActivity.class);
            //intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, manager.getAppWidgetIds(theWidget));
            intent.putExtra("category", "Click");
            intent.putExtra("link", campaignInformation.getCampaign_link());
            intent.putExtra("cam_id", campaignInformation.getId());
            PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getAppContext(), 0, intent, 0);
            view.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent);

        } else if (link_option.contentEquals("Reach")) {
            view.setTextViewText(R.id.appwidget_text, "Powered by Adsle");
        }

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
        Calendar calendar = Calendar.getInstance();
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH);
        int y = calendar.get(Calendar.YEAR);
        String date = d + "-" + (m + 1) + "-" + y;
        String path = "adsle-campaign-tracking/" + cam_id + "/" + date + "/" + field + "/" + user.getId();
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
}
