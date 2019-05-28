package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ad.adsle.Adapter.InterestAdapter;
import com.ad.adsle.Callbacks.InterestClicked;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.Interests;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InterestActivity extends AppCompatActivity implements InterestClicked {

    RecyclerView recyclerView;
    InterestAdapter adapter;
    AppData data;
    Utils utils;
    ArrayList<String> interests = new ArrayList<>();
    ArrayList<String> selected_interests = new ArrayList<>();

    FloatingActionButton floatingActionButton;
    LinearLayout linearLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);

        utils = new Utils(InterestActivity.this);
        data = new AppData(InterestActivity.this);
        adapter = new InterestAdapter(InterestActivity.this, this);
        recyclerView = findViewById(R.id.recycler_view);
        linearLayout = findViewById(R.id.linerLayout);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(InterestActivity.this)
                        .title("Select your interests")
                        .items(interests)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                selected_interests.clear();
                                for (CharSequence item : text) {
                                    selected_interests.add(String.valueOf(item));
                                }
                                adapter.updateView(selected_interests);
                                if (selected_interests.size() > 0) {
                                    linearLayout.setVisibility(View.GONE);
                                }
                                return true;
                            }
                        }).positiveText("Done")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(InterestActivity.this));
        recyclerView.setAdapter(adapter);

        utils.displayDialog("Loading data...");
        getInterests();
    }

    private void getInterests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("settings").document("interests").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    utils.dismissDialog();
                    interests.clear();
                    interests = (ArrayList<String>) task.getResult().get("data");
                    //adapter.updateView(interests);
                } else {
                    errorOccurred();
                }
            }
        });
    }

    private void errorOccurred() {
        utils.dismissDialog();
        utils.error("Something went wrong. Try again.");
    }

    private void continueToHome() {
        User user = data.getUser();
        if (selected_interests.size() > 0) {
            utils.displayDialog("Setting up user data...");
            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            Map<String, Object> param = new HashMap<>();
            param.put("data", selected_interests);
            db1.collection("users").document(user.getEmail()).collection("user-data").document("interests").set(param).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        utils.dismissDialog();
                        data.setInterestSelected(true);
                        startActivity(new Intent(InterestActivity.this, InviteActivity.class));
                        finish();
                    } else {
                        errorOccurred();
                    }
                }
            });
        } else {
            utils.dismissDialog();
            utils.error("Please select at least one interest.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.interest, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_interest_done) {
            continueToHome();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIClicked(View view, int position, boolean selected) {
//        Button selected_button = (Button) view;
//        String selected_text = selected_button.getText().toString();
//        if (selected) {
//            selected_interests.add(selected_text);
//        } else {
//            selected_interests.remove(selected_text);
//        }
    }
}
