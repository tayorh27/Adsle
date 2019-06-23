package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FeedbackActivity extends AppCompatActivity {

    EditText editText;
    Utils utils;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        utils = new Utils(FeedbackActivity.this);

        editText = findViewById(R.id.feedback_text);
    }

    public void onSendClick(View view) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            return;
        }
        utils.displayDialog("Sending feedback");
        Map<String, Object> fb = new HashMap<>();
        fb.put("text", editText.getText().toString());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("feedback");
        String id = reference.push().getKey();
        reference.child(id).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                utils.dismissDialog();
                if (task.isSuccessful()) {
                    editText.setText("");
                    Toast.makeText(FeedbackActivity.this, "Thank you for your feedback.", Toast.LENGTH_SHORT).show();
                } else {
                    utils.error("Failed to send feedback. Try again");
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
