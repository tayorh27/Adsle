package com.ad.adsle.Activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.ad.adsle.Adapter.AdsAdapter;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.Plans;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.AdUtils;
import com.ad.adsle.Util.Utils;
import com.ad.adsle.services.UpdateService;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.bijoysingh.starter.util.PermissionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterViewFlipper;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AppData data;
    Utils utils;
    User user;
    Settings settings;

    TextView name, email, nBonus;
    NavigationView navigationView;

    static final int CONTACT_PICKER_REQUEST = 123;
    static final int REQUEST_BIND_APPWIDGET = 321;
    private static int PLANS_REQUEST = 143;
    private static int REQUEST_INVITE = 642;

    Plans plan = null;
    AppCompatTextView edit_plan;

    boolean isCampaignExists = false;
    CampaignInformation current_campaign;

    LinearLayout dataLayout, camLayout, rechargeLayout;
    AppCompatTextView camM1, camM2, camM3, viewCam, t1, t2, t3;

    String total_cam_count = "", total_active_cam_count = "", total_inactive_cam_count = "";

    //    GifImageView adImageView;
//    TextView adTextView;
    AdapterViewFlipper adapterViewFlipper;
    AdsAdapter adapter;
    ArrayList<CampaignInformation> campaignInformationArrayList = new ArrayList<>();

    AdUtils adUtils;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        utils = new Utils(HomeActivity.this);
        data = new AppData(HomeActivity.this);
        user = data.getUser();
        settings = data.getSettings();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dataLayout = findViewById(R.id.data_layout);
        camLayout = findViewById(R.id.cam_layout);
        rechargeLayout = findViewById(R.id.recLayout);
        camM1 = findViewById(R.id.cam_menu1);
        camM2 = findViewById(R.id.cam_menu2);
        camM3 = findViewById(R.id.cam_menu3);

        t1 = findViewById(R.id.TMenu1);
        t2 = findViewById(R.id.TMenu2);
        t3 = findViewById(R.id.TMenu3);

        //adImageView = findViewById(R.id.ad_image);
        //adTextView = findViewById(R.id.ad_text);
        adapterViewFlipper = findViewById(R.id.adapter_view_flipper);
        adapter = new AdsAdapter(HomeActivity.this);
        adapterViewFlipper.setAdapter(adapter);
        adapterViewFlipper.setFlipInterval(15000);

        //adUtils = new AdUtils(HomeActivity.this, adImageView, adTextView);
        adUtils = new AdUtils();

        viewCam = findViewById(R.id.cam_current_view);
        nBonus = findViewById(R.id.tvBonus);
        edit_plan = findViewById(R.id.textplan);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CreateCampaignActivity.class));
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        viewCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_campaign != null) {
                    Intent intent = new Intent(HomeActivity.this, ViewCampaignActivity.class);
                    intent.putExtra("current_campaign", current_campaign);
                    startActivity(intent);
                }
            }
        });

        LoadNavHeaderDetails();
        fetchAds();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (data.getFromHomeActivity()) {
                PinAppWidget();
            }
        }
        if(data.getFirstTime()){
            if (user.getTag().contentEquals("user")) {
                new MaterialDialog.Builder(HomeActivity.this)
                        .title("How It works")
                        .content("To start getting free data, click below to know how to set it up.")
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .positiveText("How It Works")
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                data.setFirstTime(false);
                                startActivity(new Intent(HomeActivity.this, HowItWorksActivity.class));
                            }
                        }).show();
            }
        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                adUtils.StartAds();
//            }
//        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void PinAppWidget() {
        AppWidgetManager appWidgetManager = getSystemService(AppWidgetManager.class);
        ComponentName myProvider =
                new ComponentName(this, AdsleWidget.class);
        Log.e("MyApplication", "PinAppWidget: was called 1");

//        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
//        //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, myProvider);
//        // This is the options bundle discussed above
//        //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS, options);
//        startActivityForResult(intent, REQUEST_BIND_APPWIDGET);

        if (appWidgetManager.isRequestPinAppWidgetSupported()) {
            // Create the PendingIntent object only if your app needs to be notified
            // that the user allowed the widget to be pinned. Note that, if the pinning
            // operation fails, your app isn't notified.
            Intent pinnedWidgetCallbackIntent = new Intent(this, UpdateService.class);
            pinnedWidgetCallbackIntent.putExtra("pinnedWidgetCallbackIntent", true);
            // Configure the intent so that your app's broadcast receiver gets
            // the callback successfully. This callback receives the ID of the
            // newly-pinned widget (EXTRA_APPWIDGET_ID).
            PendingIntent successCallback = PendingIntent.getBroadcast(this, 0,
                    pinnedWidgetCallbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback);
            Log.e("MyApplication", "PinAppWidget: was called 2");
        }
    }

    private void fetchAds() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LocationDetails locationDetails = data.getLocationDetails();
        Query query = db.collection("campaigns")
                .whereEqualTo("status", true)
                .whereGreaterThanOrEqualTo("age_range_max", user.getAge())
                .whereArrayContains("locationDetails", locationDetails.getCity());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    campaignInformationArrayList.clear();
                    ArrayList<CampaignInformation> campaignInformationArray = new ArrayList<>();
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        CampaignInformation ci = snapshot.toObject(CampaignInformation.class);
                        campaignInformationArray.add(ci);
                    }

                    for (CampaignInformation ci : campaignInformationArray) {
                        if (adUtils.checkCampaignStatus(ci) < 0) {
                            campaignInformationArrayList.add(ci);
                        }
                    }
                    adapter.setList(campaignInformationArrayList);
                    adapterViewFlipper.startFlipping();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        nBonus.setText(utils.getExactDataValue(String.valueOf(user.getBonus_data())));
    }

    private void LoadNavHeaderDetails() {
        if (user.getTag().contentEquals("advertiser")) {
            dataLayout.setVisibility(View.GONE);
            rechargeLayout.setVisibility(View.GONE);
            camLayout.setVisibility(View.VISIBLE);
        }
        nBonus.setText(utils.getExactDataValue(String.valueOf(user.getBonus_data())));
        name = navigationView.getHeaderView(0).findViewById(R.id.tvName);
        email = navigationView.getHeaderView(0).findViewById(R.id.tvEmail);
        name.setText(user.getName());
        email.setText(user.getEmail());
    }

    String choice = "", numberToRecharge = "";

    public void ChoosePlans(View view) {
        if (!dataCheck()) {
            utils.error("Sorry, you have to accumulate 500MB worth of data before you can recharge.");
            return;
        }
        int id = view.getId();
        if (id == R.id.rechargeM || id == R.id.planchoose) {
            choice = "self";
            numberToRecharge = user.getNumber();
            startActivityForResult(new Intent(HomeActivity.this, PlansActivity.class).putExtra("numberToRecharge", numberToRecharge), PLANS_REQUEST);
        }
        if (id == R.id.rechargeO) {
            choice = "others";
            RechargeOthersPopupDialog();
        }
    }

    public void RechargeNow(View view) {
        if (plan == null) {
            Toast.makeText(getApplicationContext(), "Please select a plan", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(HomeActivity.this, TopupActivity.class);
        intent.putExtra("plan", plan);
        intent.putExtra("number", numberToRecharge);
        intent.putExtra("choice", choice);
        startActivity(intent);
    }

    private void RechargeOthersPopupDialog() {
        new MaterialDialog.Builder(HomeActivity.this)
                .cancelable(false)
                .canceledOnTouchOutside(true)
                .title("Enter contact number")
                .input("08101234567", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        numberToRecharge = String.valueOf(input);
                        startActivityForResult(new Intent(HomeActivity.this, PlansActivity.class).putExtra("numberToRecharge", numberToRecharge), PLANS_REQUEST);
                    }
                }).inputRange(11, 14, ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .inputType(InputType.TYPE_CLASS_PHONE)
                .negativeText("Select Contact")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        PermissionManager pm = new PermissionManager(HomeActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS});
                        if (pm.hasAllPermissions()) {
                            Intent i = new Intent(Intent.ACTION_PICK);
                            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                            startActivityForResult(i, CONTACT_PICKER_REQUEST);
                        } else {
                            pm.requestPermissions(663);
                        }
                    }
                }).show();
    }

    private boolean dataCheck() {
        long current_data = user.getBonus_data();
        long start_data = settings.getWithdrawal_data_check();//524288000;
        return (current_data >= start_data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        data = new AppData(HomeActivity.this);
        user = data.getUser();
        getCurrentCampaign();
    }

    private void updateViews() {
        if (current_campaign != null && isCampaignExists) {
            camLayout.setVisibility(View.VISIBLE);
            camM1.setText(current_campaign.getReach_number() + "");
            camM2.setText(current_campaign.getViews_number() + "");
            camM3.setText(current_campaign.getClicks_number() + "");

            t1.setText(total_cam_count);
            t2.setText(total_active_cam_count);
            t3.setText(total_inactive_cam_count);
        }
    }

    private void addWidgetToHomeScreen() {
//        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
//
//        if (shortcutManager.isRequestPinShortcutSupported()) {
//            // Assumes there's already a shortcut with the ID "my-shortcut".
//            // The shortcut must be enabled.
//            ShortcutInfo pinShortcutInfo =
//                    new ShortcutInfo.Builder(HomeActivity.this, "my-shortcut").build();
//
//            // Create the PendingIntent object only if your app needs to be notified
//            // that the user allowed the shortcut to be pinned. Note that, if the
//            // pinning operation fails, your app isn't notified. We assume here that the
//            // app has implemented a method called createShortcutResultIntent() that
//            // returns a broadcast intent.
//            Intent pinnedShortcutCallbackIntent =
//                    shortcutManager.createShortcutResultIntent(pinShortcutInfo);
//
//            // Configure the intent so that your app's broadcast receiver gets
//            // the callback successfully.For details, see PendingIntent.getBroadcast().
//            PendingIntent successCallback = PendingIntent.getBroadcast(HomeActivity.this, /* request code */ 0,
//                    pinnedShortcutCallbackIntent, /* flags */ 0);
//
//            shortcutManager.requestPinShortcut(pinShortcutInfo,
//                    successCallback.getIntentSender());
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create) {
            startActivity(new Intent(HomeActivity.this, CreateCampaignActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PLANS_REQUEST) {
            plan = data.getParcelableExtra("resultPlan");
            edit_plan.setText(plan.getPrice() + " - " + plan.getTitle());
            Intent intent = new Intent(HomeActivity.this, TopupActivity.class);
            intent.putExtra("plan", plan);
            intent.putExtra("number", numberToRecharge);
            intent.putExtra("choice", choice);
            startActivity(intent);
        }
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(contactUri, projection,
                        null, null, null);
                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIndex);
                    numberToRecharge = number;
                    startActivityForResult(new Intent(HomeActivity.this, PlansActivity.class).putExtra("numberToRecharge", numberToRecharge), PLANS_REQUEST);
                }
                cursor.close();
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            // Handle the camera action
            startActivity(new Intent(HomeActivity.this, CampaignListActivity.class));
        }
        if (id == R.id.nav_how) {
            startActivity(new Intent(HomeActivity.this, HowItWorksActivity.class));
        }
        if (id == R.id.nav_trans) {
            startActivity(new Intent(HomeActivity.this, CampaignTransactionActivity.class));
        }
        if (id == R.id.nav_invite) {
            startActivity(new Intent(HomeActivity.this, InviteActivity.class));
        }
        if (id == R.id.nav_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        }
        if (id == R.id.nav_data_balance) {
            CheckDataBalance();
        }
        if (id == R.id.nav_logout) {
            new MaterialDialog.Builder(HomeActivity.this)
                    .title("Confirmation")
                    .content("Log me out now!")
                    .negativeText("Cancel")
                    .positiveText("Continue")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            data.Logout();
                            finish();
                        }
                    })
                    .show();
        }
        if (id == R.id.nav_feedback) {
            startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));
        }

//        if (id == R.id.nav_survey) {
//            new MaterialDialog.Builder(HomeActivity.this)
//                    .title("Message")
//                    .content("This feature is not yet available.")
//                    .negativeText("OK")
//                    .show();
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void CheckDataBalance() {
        PermissionManager permissionManager = new PermissionManager(HomeActivity.this, new String[]{android.Manifest.permission.CALL_PHONE});
        if (!permissionManager.hasAllPermissions()) {
            permissionManager.requestPermissions(232);
        } else {
            dialDataBalanceCode();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 232) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dialDataBalanceCode();
            }
        }
        if (requestCode == 663) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, CONTACT_PICKER_REQUEST);
            }
        }
    }

    private void dialDataBalanceCode() {
        String code = "";
        if (utils.GetNetworkProviderType(user.getNumber()).contentEquals("glo")) {
            code = "%23124%23";
        }
        if (utils.GetNetworkProviderType(user.getNumber()).contentEquals("mtn")) {
            code = "*131*4%23";
        }
        if (utils.GetNetworkProviderType(user.getNumber()).contentEquals("airtel")) {
            code = "*123%23";
        }
        if (utils.GetNetworkProviderType(user.getNumber()).contentEquals("9mobile")) {
            code = "*232%23";
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + code));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    private void getCurrentCampaign() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getEmail()).collection("user-data").document("settings").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult() != null) {
                    Map<String, Object> snapshot = task.getResult().getData();
                    if (snapshot != null) {
                        isCampaignExists = true;
                        String currentTitle = String.valueOf(snapshot.get("current_campaign"));
                        db.collection("campaigns").document(currentTitle).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                if (documentSnapshot != null) {
                                    current_campaign = documentSnapshot.toObject(CampaignInformation.class);
                                    updateViews();
                                }
                            }
                        });

                        db.collection("campaigns").whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().getDocuments().isEmpty() || task.getResult() == null) {
                                    total_cam_count = "0";
                                } else {
                                    total_cam_count = "" + task.getResult().getDocuments().size();
                                }
                                updateViews();
                            }
                        });

                        db.collection("campaigns").whereEqualTo("email", user.getEmail()).whereEqualTo("status", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().getDocuments().isEmpty() || task.getResult() == null) {
                                    total_active_cam_count = "0";
                                } else {
                                    total_active_cam_count = "" + task.getResult().getDocuments().size();
                                }
                                updateViews();
                            }
                        });

                        db.collection("campaigns").whereEqualTo("email", user.getEmail()).whereEqualTo("status", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().getDocuments().isEmpty() || task.getResult() == null) {
                                    total_inactive_cam_count = "0";
                                } else {
                                    total_inactive_cam_count = "" + task.getResult().getDocuments().size();
                                }
                                updateViews();
                            }
                        });
                    }
                }
            }
        });
    }
}
