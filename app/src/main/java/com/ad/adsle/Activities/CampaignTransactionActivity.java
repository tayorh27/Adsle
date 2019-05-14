package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ad.adsle.Adapter.TransAdapter;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.Transactions;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CampaignTransactionActivity extends AppCompatActivity {

    AppData data;
    User user;
    TransAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout layout;
    ArrayList<Transactions> transactionsArrayList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_transaction);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = new AppData(CampaignTransactionActivity.this);
        user = data.getUser();

        adapter = new TransAdapter(CampaignTransactionActivity.this);
        recyclerView = findViewById(R.id.recycler_view_trans);
        recyclerView.setLayoutManager(new LinearLayoutManager(CampaignTransactionActivity.this));
        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.myProgress);
        layout = findViewById(R.id.linerLayout);

        LoadTransactions();
    }

    private void LoadTransactions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("users").document(user.getEmail()).collection("user-data").document("transactions").collection("user-trans");
        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    transactionsArrayList.clear();
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        transactionsArrayList.add(snapshot.toObject(Transactions.class));
                    }
                    if(transactionsArrayList.isEmpty()){
                        layout.setVisibility(View.VISIBLE);
                    }
                    adapter.updateLayout(transactionsArrayList);
                } else {
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });
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
