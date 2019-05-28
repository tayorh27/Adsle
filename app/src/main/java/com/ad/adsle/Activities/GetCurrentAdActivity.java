package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Db.CampaignData;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.Information.User;
import com.ad.adsle.MyApplication;
import com.ad.adsle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GetCurrentAdActivity extends AppCompatActivity {

    CampaignData campaignData;
    AppData data;
    Settings settings;
    User user;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_current_ad);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        campaignData = new CampaignData(GetCurrentAdActivity.this);
        data = new AppData(GetCurrentAdActivity.this);
        user = data.getUser();
        settings = data.getSettings();

        processAdIntent(getIntent());
    }

    private void processAdIntent(Intent intent) {
        String category = intent.getStringExtra("category");
        String link = intent.getStringExtra("link");
        String cam_id = intent.getStringExtra("cam_id");

        Intent mInt = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        if (!campaignData.getClicked(cam_id)) {
            campaignData.setClicked(true, cam_id);
            updateCampaignData(true, cam_id, "clicks_number", 1);
        }
//        else {
//            updateCampaignData(false, cam_id, "clicks_number", 1);
//        }

        if (mInt.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(mInt, "Adsle"));
        }
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(GetCurrentAdActivity.this, HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
