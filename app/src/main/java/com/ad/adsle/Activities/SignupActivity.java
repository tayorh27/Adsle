package com.ad.adsle.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.AppDetail;
import com.ad.adsle.Information.DeviceDetails;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.LocationGetterBackgroundTask;
import com.ad.adsle.Util.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.an.deviceinfo.device.model.Device;
import com.an.deviceinfo.device.model.Network;
import com.an.deviceinfo.location.DeviceLocation;
import com.an.deviceinfo.location.LocationInfo;
import com.github.bijoysingh.starter.util.PermissionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText inputEmail, inputPassword, inputFullName, inputNumber, inputReferral;
    Button inputDateBirth;
    Spinner inputGender, inputReligion;
    TextView whomRef, fullname, tvD, tvG, tvR, tvRe;
    private Button btnSignIn, btnSignUp, btnResetPassword, btnUser, btnAdvertiser;
    private ProgressBar progressBar;

    AppData data;
    com.ad.adsle.Information.Settings settings;

    String email, password, name, number, referral, age, gender, religion, refLink;
    //CallbackManager mCallbackManager;
    String id = "", refEmail = "", refCode = "", tag = "user";
    boolean isExist = false;
    FirebaseAuth auth;
    Utils utils;
    String dataToGive = "";
    private PackageManager manager;
    private ArrayList<AppDetail> apps;
    boolean isLinkDone = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        data = new AppData(SignupActivity.this);
        settings = data.getSettings();
        utils = new Utils(SignupActivity.this);
        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputFullName = findViewById(R.id.fullname);
        inputNumber = findViewById(R.id.number);
        inputReferral = findViewById(R.id.refCode);
        inputDateBirth = findViewById(R.id.dob);
        inputGender = findViewById(R.id.gender);
        inputReligion = findViewById(R.id.religion);
        whomRef = findViewById(R.id.referCoder);
        tvD = findViewById(R.id.tvDob);
        tvG = findViewById(R.id.tvGender);
        tvR = findViewById(R.id.tvReferral);
        tvRe = findViewById(R.id.tvReligion);
        fullname = findViewById(R.id.tvFullname);
        progressBar = findViewById(R.id.progressBar);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        btnUser = findViewById(R.id.selection_one);
        btnAdvertiser = findViewById(R.id.selection_two);

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUser.setBackgroundResource(R.color.fabColor);
                btnAdvertiser.setBackgroundResource(R.color.colorAccent);
                inputDateBirth.setVisibility(View.VISIBLE);
                inputGender.setVisibility(View.VISIBLE);
                inputReligion.setVisibility(View.VISIBLE);
                inputReferral.setVisibility(View.VISIBLE);
                tvD.setVisibility(View.VISIBLE);
                tvG.setVisibility(View.VISIBLE);
                tvR.setVisibility(View.VISIBLE);
                tvRe.setVisibility(View.VISIBLE);
                fullname.setText("Full Name");
                tag = "user";
            }
        });

        btnAdvertiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdvertiser.setBackgroundResource(R.color.fabColor);
                btnUser.setBackgroundResource(R.color.colorAccent);
                inputDateBirth.setVisibility(View.GONE);
                inputGender.setVisibility(View.GONE);
                inputReligion.setVisibility(View.GONE);
                inputReferral.setVisibility(View.GONE);
                tvD.setVisibility(View.GONE);
                tvG.setVisibility(View.GONE);
                tvR.setVisibility(View.GONE);
                tvRe.setVisibility(View.GONE);
                fullname.setText("Company Name");
                tag = "advertiser";
            }
        });

        inputDateBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new SpinnerDatePickerDialogBuilder()
                        .context(SignupActivity.this)
                        .callback(SignupActivity.this)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .defaultDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .maxDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .minDate(1800, 0, 1)
                        .build()
                        .show();
            }
        });

//        inputGender.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(SignupActivity.this)
//                        .cancelable(false)
//                        .canceledOnTouchOutside(false)
//                        .title("Select Gender")
//                        .items(R.array.gender)
//                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                                inputGender.setText(text.toString());
//                                return true;
//                            }
//                        }).show();
//            }
//        });
//
//        inputReligion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(SignupActivity.this)
//                        .cancelable(false)
//                        .canceledOnTouchOutside(false)
//                        .title("Select Religion")
//                        .items(R.array.religion)
//                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                                inputReligion.setText(text.toString());
//                                return true;
//                            }
//                        }).show();
//            }
//        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] _genders = getResources().getStringArray(R.array.gender);
                String[] _religions = getResources().getStringArray(R.array.religion);

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                name = inputFullName.getText().toString().trim();
                number = inputNumber.getText().toString().trim();
                referral = inputReferral.getText().toString().trim();
                age = inputDateBirth.getText().toString();
                gender = _genders[inputGender.getSelectedItemPosition()];
                religion = _religions[inputReligion.getSelectedItemPosition()];

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), (tag.contentEquals("user")) ? "Enter a full name!" : "Enter company name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(getApplicationContext(), "Enter phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tag.contentEquals("user")) {
                    if (TextUtils.isEmpty(age)) {
                        Toast.makeText(getApplicationContext(), "Enter date of birth!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(gender)) {
                        Toast.makeText(getApplicationContext(), "Select gender!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(religion)) {
                        Toast.makeText(getApplicationContext(), "Select religion!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 4) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum of 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                utils.displayDialog("Please wait...");
                if (auth.getCurrentUser() == null) {
                    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            AfterSignUpOperations();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            utils.dismissDialog();
                            utils.error("An error occurred. Try again. ");
                        }
                    });
                } else {
                    AfterSignUpOperations();
                }
            }
        });
        GetAllApps();
        ReceiveDeepUrl();
        checkPermissions();
    }

    private void AfterSignUpOperations() {
        Random r = new Random();
        refCode = r.nextInt(9) + name.substring(0, 1) + r.nextInt(9) + email.substring(0, 1) + number.substring(number.length() - 1);
        if (BuildDynamicLink(refCode, email)) {
            preOperations();
        } else {
            preOperations();
        }
    }

    public void Terms(View view) {
        new MaterialDialog.Builder(SignupActivity.this)
                .title("Terms & Conditions")
                .content("")
                .positiveText("Agreed")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void preOperations() {
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    data.setRegistrationToken(token);
                    User user = new User("", name, email, number, 0, gender, religion, tag, settings.getSignup_data(), refCode, "", data.getRegistrationToken(), android_id, new Date().toLocaleString(),"0");
                    if (tag.contentEquals("user")) {
                        Calendar calendar = Calendar.getInstance();
                        int year_now = calendar.get(Calendar.YEAR);
                        int selected_year = Integer.parseInt(age.split("-")[2]);
                        int user_age = year_now - selected_year;
                        user.setAge(user_age);
                    }
                    AfterAccountCreation(user);
                }
            }
        });
    }

    private void checkPermissions() {
        PermissionManager pm = new PermissionManager(SignupActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE});
        if (!pm.hasAllPermissions()) {
            pm.requestPermissions(345);
        } else {
            GetDeviceDetails();
            new LocationGetterBackgroundTask().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 345) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                GetDeviceDetails();
                new LocationGetterBackgroundTask().execute();
            } else {
                utils.error("Please allow access to location to continue. Your location is not shared with any third party.");
                checkPermissions();
            }
        }
    }

    private void ReceiveDeepUrl() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                // Get deep link from result (may be null if no link is found)
                Uri deepLink = null;
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.getLink();
                    String refCode = deepLink.getQueryParameter("ref_code");
                    dataToGive = deepLink.getQueryParameter("data");
                    refEmail = deepLink.getQueryParameter("email");
                    inputReferral.setText(refCode);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void GetAllApps() {
        manager = getPackageManager();
        apps = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            AppDetail app = new AppDetail();
            app.label = ri.loadLabel(manager).toString();
            app.name = ri.activityInfo.packageName;
            //app.icon = ri.activityInfo.loadIcon(manager);
            app.icon = String.valueOf(ri.activityInfo.icon);
            apps.add(app);
        }
    }

    private void GetDeviceDetails() {
        Device device = new Device(SignupActivity.this);
        DeviceDetails deviceDetails = new DeviceDetails(
                device.getReleaseBuildVersion(),
                device.getBuildVersionCodeName(),
                device.getManufacturer(),
                device.getModel(),
                device.getProduct(),
                device.getDisplayVersion(),
                device.getOsVersion(),
                String.valueOf(device.getSdkVersion())
        );
        data.StoreDeviceDetails(deviceDetails);
    }

    private boolean BuildDynamicLink(String ref, String email) {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("http://adsle.com?ref_code=" + ref + "&email=" + email + "&data=" + settings.getInvite_bonus_data()))
                .setDomainUriPrefix("https://adsle.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.ad.adsle")
                        .build())
                .setGoogleAnalyticsParameters(new DynamicLink.GoogleAnalyticsParameters.Builder()
                        .setSource("adsle")
                        .setMedium("social")
                        .setCampaign("sharing")
                        .build())
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("Adsle App")
                        .setDescription("Get data by viewing ads.")
                        .build())
                .buildShortDynamicLink().addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        isLinkDone = true;
                        //Log.e("refLinkError", "error" + task.getException());
                        if (task.isSuccessful()) {
                            refLink = task.getResult().getShortLink().toString();
                        } else {
                            Log.e("refLinkError", "error" + task.getException());
                        }
                    }
                });
        return isLinkDone;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String selected_date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        inputDateBirth.setText(selected_date);
    }

    private void GiveUserData() {
        if (!TextUtils.isEmpty(referral) || !TextUtils.isEmpty(refEmail)) {
            //final long mb = Long.parseLong(dataToGive);
            final long mb = settings.getSignup_data();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(refEmail).collection("user-data").document("signup");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        User refUser = snapshot.toObject(User.class);
                        long data = refUser != null ? refUser.getBonus_data() : 0;
                        int count = Integer.parseInt(refUser.getInvite_count() != null ? refUser.getInvite_count() : "0");
                        int newCount = count + 1;
                        if (newCount < 5) {
                            Map<String, Object> param = new HashMap<>();
                            param.put("invite_count", String.valueOf(newCount));
                            docRef.update(param);
                        } else if (newCount == 5) {
                            data = data + mb;
                            Map<String, Object> param = new HashMap<>();
                            param.put("bonus_data", String.valueOf(data));
                            param.put("invite_count", String.valueOf(0));
                            docRef.update(param);
                        }
                    }
                }
            });
        }
    }

    private void AfterAccountCreation(User user) {
        user.setReferralLink(refLink);
        if (tag.contentEquals("user")) {
            GiveUserData();
        }
        LocationDetails locationDetails = data.getLocationDetails();
        //if (locationDetails.getFormatted_address().contains("N/A")) {
        LocationInfo locationInfo = new LocationInfo(SignupActivity.this);
        DeviceLocation location = locationInfo.getLocation();
        locationDetails.setCity(location.getState());
        locationDetails.setArea(location.getCity());
        locationDetails.setInside_area(location.getAddressLine1());
        locationDetails.setFormatted_address(location.getAddressLine1());
        locationDetails.setCountry(location.getCountryCode());
        data.StoreLocationDetails(locationDetails);
        //}
        DatabaseReference refId = FirebaseDatabase.getInstance().getReference();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userRef = db.collection("users").document(email).collection("user-data");
        String id = refId.push().getKey();
        user.setId(id);
        userRef.document("signup").set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    userRef.document("location-data").set(locationDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> param = new HashMap<>();
                                param.put("data", apps);
                                userRef.document("user-apps").set(param).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Map<String, Object> paramDevice = new HashMap<>();
                                            //paramDevice.put("data", apps);
                                            userRef.document("device-details").set(data.getDeviceDetails()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        runTransactionForTotalUsers(user);
                                                    } else {
                                                        errorOccurred("");
                                                    }
                                                }
                                            });
                                        } else {
                                            errorOccurred("");
                                        }
                                    }
                                });
                            } else {
                                errorOccurred("");
                            }
                        }
                    });
                } else {
                    errorOccurred("");
                }
            }
        });
    }

    private void runTransactionForTotalUsers(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference sfDocRef = db.collection("settings").document("app-settings");

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                long newTotalUsers = snapshot.getLong("total_users") + 1;
                transaction.update(sfDocRef, "total_users", newTotalUsers);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                data.setLogged(true);
                data.StoreUsers(user);
                startActivity(new Intent(SignupActivity.this, InterestActivity.class));
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Transaction failure.", e);
                    }
                });
    }

    private void errorOccurred(String error) {
        utils.dismissDialog();
        utils.error("Something went wrong. Try again.");
        Log.e("SignupActivity", "error at " + error);
    }
}
