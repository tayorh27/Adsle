package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ad.adsle.Adapter.HomeAdsAdapter;
import com.ad.adsle.Callbacks.HomeAdsCallback;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Db.CampaignData;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.Information.User;
import com.ad.adsle.MyApplication;
import com.ad.adsle.R;
import com.ad.adsle.Util.AdUtils;
import com.ad.adsle.Util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AdsActivity extends AppCompatActivity implements HomeAdsCallback {

    RecyclerView recyclerView;
    HomeAdsAdapter adapter;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    TextView textView;

    ArrayList<CampaignInformation> campaignInformationArrayList = new ArrayList<>();
    AdUtils adUtils;

    AppData data;
    CampaignData campaignData;
    User user;
    LocationDetails locationDetails;
    Settings settings;
    Utils utils;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MyApplication.fetchSettings();
        adUtils = new AdUtils(AdsActivity.this, null);

        recyclerView = findViewById(R.id.adsRecyclerView);
        linearLayout = findViewById(R.id.loadingLayout);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.tvText);

        adapter = new HomeAdsAdapter(AdsActivity.this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdsActivity.this));
        recyclerView.setAdapter(adapter);

        GetAllCampaignForUser();
    }

    private void GetAllCampaignForUser() {
        data = new AppData(MyApplication.getAppContext());
        utils = new Utils(MyApplication.getAppContext());
        campaignData = new CampaignData(MyApplication.getAppContext());
        user = data.getUser();
        locationDetails = data.getLocationDetails();

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
                        int days = adUtils.numberOfDaysToStartDay(ci);
                        boolean isExpired = adUtils.numberOfDaysToExpiryDay(ci) < 0;
                        Log.e("AdsActivity", "days = " + days);
                        if (days > 0 && !isExpired) {
                            campaignInformationArrayList.add(ci);
                            size = size + 1;
                        }
                    }
                    Log.e("AdsActivity", "number of campaignData = " + size);
                    campaignData.setCampaignSize(size);
                    if (campaignInformationArrayList.size() > 0) {
                        linearLayout.setVisibility(View.GONE);
                        adapter.setList(campaignInformationArrayList);
                    } else {
                        linearLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        textView.setText("No available ads at the moment. Please come back later.");
                    }
                } else {
                    Log.e("AdsActivity", "number of campaignData = " + task.getException());
                }
            }
        });
    }

    @Override
    public void onAdClick(View view, int position) {
        CampaignInformation current = campaignInformationArrayList.get(position);
        String link_option = current.getCampaign_link_option();
        Intent intent = new Intent(AdsActivity.this, GetCurrentAdActivity.class);
        if (link_option.contentEquals("App Install")) {
            intent.putExtra("category", "App Install");
            intent.putExtra("link", current.getCampaign_link());
            intent.putExtra("cam_id", current.getId());
            startActivity(intent);
        } else if (link_option.contentEquals("Click")) {
            intent.putExtra("category", "Click");
            intent.putExtra("link", current.getCampaign_link());
            intent.putExtra("cam_id", current.getId());
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
