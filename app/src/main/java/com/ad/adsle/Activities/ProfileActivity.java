package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity {

    ImageView iv;
    TextView username, email, phone, gender, religion, mInterests;
    AppData data;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = new AppData(ProfileActivity.this);
        User user = data.getUser();

        iv = findViewById(R.id.imageViewDp);
        username = findViewById(R.id.tvUsername);
        email = findViewById(R.id.tvEmail);
        phone = findViewById(R.id.tvPhone);

        gender = findViewById(R.id.tvGender);
        religion = findViewById(R.id.tvReligion);
        mInterests = findViewById(R.id.tvInterest);

        username.setText(user.getName());
        email.setText(user.getEmail());
        phone.setText(user.getNumber());
        gender.setText(user.getGender());
        religion.setText(user.getReligion());

        String _username = user.getName();
        String[] nm = _username.split(" ");
        String second_line = (nm.length > 1) ? "" + nm[1].charAt(0) : "";
        String um = nm[0].charAt(0) + "" + second_line;
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        //title.setTextColor(color1);
        //int color2 = generator.getColor("user@gmail.com")
        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .height(128)
                .width(128)
                .toUpperCase()
                .endConfig()
                .buildRoundRect(um, color1, 64);
        iv.setImageDrawable(textDrawable);

        FirebaseFirestore dbI = FirebaseFirestore.getInstance();
        dbI.collection("users").document(user.getEmail()).collection("user-data").document("interests").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> interests = (ArrayList<String>) task.getResult().get("data");
                    if (interests.size() > 0) {
                        StringBuilder _interest = new StringBuilder();
                        for (String text : interests) {
                            _interest.append(text + ",");
                        }
                        String output = _interest.toString().substring(0, _interest.toString().length() - 1);
                        mInterests.setText(output);
                    }
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
