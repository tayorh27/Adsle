package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.DeviceDetails;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    User user;
    Utils utils;
    AppData data;

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    TextView tvFinger;
    //FingerPrintAuthHelper mFingerPrintAuthHelper;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        data = new AppData(LoginActivity.this);
        utils = new Utils(LoginActivity.this);
        auth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.btn_reset_password);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (!utils.isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                utils.displayDialog("Verifying login...");
//                progressBar.setVisibility(View.VISIBLE);
//
//                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                //utils.dismissDialog();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    utils.dismissDialog();
                                    if (password.length() < 4) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();


                                    DocumentReference docRef = db.collection("users").document(email).collection("user-data").document("signup");
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                user = document.toObject(User.class);
                                                data.StoreUsers(user);
                                                data.setLogged(true);

                                                db.collection("users").document(email).collection("user-data").document("location-data").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            LocationDetails locationDetails = task.getResult().toObject(LocationDetails.class);
                                                            data.StoreLocationDetails(locationDetails);
                                                            db.collection("users").document(email).collection("user-data").document("device-details").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DeviceDetails deviceDetails = task.getResult().toObject(DeviceDetails.class);
                                                                        data.StoreDeviceDetails(deviceDetails);
                                                                        FirebaseFirestore dbI = FirebaseFirestore.getInstance();
                                                                        dbI.collection("users").document(email).collection("user-data").document("interests").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    ArrayList<String> interests = (ArrayList<String>) task.getResult().get("data");
                                                                                    if (interests.size() > 0) {
                                                                                        data.setInterestSelected(true);
                                                                                    }
                                                                                    utils.dismissDialog();
                                                                                    if (data.getInterestSelected()) {
                                                                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                                                        startActivity(intent);
                                                                                        finish();
                                                                                    } else {
                                                                                        Intent intent = new Intent(LoginActivity.this, InterestActivity.class);
                                                                                        startActivity(intent);
                                                                                        finish();
                                                                                    }
                                                                                } else {
                                                                                    errorOccurred();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        errorOccurred();
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            errorOccurred();
                                                        }
                                                    }
                                                });
                                            } else {
                                                errorOccurred();
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        });
    }

    private void errorOccurred() {
        utils.dismissDialog();
        utils.error("Something went wrong. Try again.");
    }
}
