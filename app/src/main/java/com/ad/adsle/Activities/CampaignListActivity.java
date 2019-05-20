package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ad.adsle.Adapter.CampaignAdapter;
import com.ad.adsle.Callbacks.ClickListener;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CampaignListActivity extends AppCompatActivity implements ClickListener {

    AppData data;
    User user;
    CampaignAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout layout;
    ArrayList<CampaignInformation> campaignInformationArrayList = new ArrayList<>();
    FloatingActionButton floatingActionButton;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_list);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = new AppData(CampaignListActivity.this);
        user = data.getUser();

        adapter = new CampaignAdapter(CampaignListActivity.this, this);
        recyclerView = findViewById(R.id.recycler_view_cams);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CampaignListActivity.this, CreateCampaignActivity.class));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(CampaignListActivity.this));
        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.myProgress);
        layout = findViewById(R.id.linerLayout);

        LoadCampaigns();
    }

    private void LoadCampaigns() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query collection = db.collection("campaigns").whereEqualTo("email", user.getEmail());
        collection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                progressBar.setVisibility(View.GONE);
                if (queryDocumentSnapshots != null) {
                    campaignInformationArrayList.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        campaignInformationArrayList.add(snapshot.toObject(CampaignInformation.class));
                    }
                    if (campaignInformationArrayList.isEmpty()) {
                        layout.setVisibility(View.VISIBLE);
                    }
                    adapter.updateLayout(campaignInformationArrayList);
                } else {
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });
//        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                progressBar.setVisibility(View.GONE);
//                if (task.isSuccessful()) {
//                    campaignInformationArrayList.clear();
//                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
//                        campaignInformationArrayList.add(snapshot.toObject(CampaignInformation.class));
//                    }
//                    adapter.updateLayout(campaignInformationArrayList);
//                } else {
//                    layout.setVisibility(View.VISIBLE);
//                }
//            }
//        });
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

    @Override
    public void onViewClick(View view, int position) {
        CampaignInformation current_campaign = campaignInformationArrayList.get(position);
        Intent intent = new Intent(CampaignListActivity.this, ViewCampaignActivity.class);
        intent.putExtra("current_campaign", current_campaign);
        startActivity(intent);
    }
}
