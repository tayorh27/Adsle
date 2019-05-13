package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ad.adsle.Adapter.PlansAdapter;
import com.ad.adsle.Callbacks.ClickListener;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.Plans;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PlansActivity extends AppCompatActivity implements ClickListener {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    PlansAdapter adapter;
    ArrayList<Plans> myPlans = new ArrayList<>();
    AppData data;
    Utils utils;
    User user;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        data = new AppData(PlansActivity.this);
        utils = new Utils(PlansActivity.this);
        user = data.getUser();

        adapter = new PlansAdapter(PlansActivity.this, this);
        recyclerView = findViewById(R.id.plan_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(PlansActivity.this));
        recyclerView.setAdapter(adapter);

        LoadDataFromFireBase();
    }

    private void LoadDataFromFireBase() {
        utils.displayDialog("Loading data plans...");
        String numberToRecharge = getIntent().getStringExtra("numberToRecharge");
        String network = utils.GetNetworkProviderType(numberToRecharge);
        Log.e("Plans", "LoadDataFromFireBase: " + network);
        db = FirebaseFirestore.getInstance();
        DocumentReference cR = db.collection("settings").document("data-plans").collection(network).document(network + "-data");
        cR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                utils.dismissDialog();
                if (task.isSuccessful()) {
                    myPlans.clear();
                    List<Map<String, Object>> params = (List<Map<String, Object>>) task.getResult().get("data");
                    for (Map<String, Object> p : params) {
                        myPlans.add(new Plans(p.get("id").toString(), p.get("title").toString(), p.get("description").toString(), p.get("validity").toString(), p.get("price").toString(), p.get("data").toString(), p.get("bonus_data").toString(), p.get("product_id").toString()));
                    }
                    adapter.setData(myPlans);
                }
            }
        });
    }

    @Override
    public void onPlanClick(View view, int position) {
        Plans clickedPlan = myPlans.get(position);
        Intent intent = new Intent();
        intent.putExtra("resultPlan", clickedPlan);
        setResult(RESULT_OK, intent);
        finish();
    }
}
