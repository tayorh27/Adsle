package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.AppDetail;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.LocationGetterBackgroundTask;
import com.ad.adsle.Util.LocationHelper;
import com.ad.adsle.Util.Utils;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.bijoysingh.starter.util.PermissionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignupActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DatePickerDialog.OnDateSetListener {

    private EditText inputEmail, inputPassword, inputFullName, inputNumber, inputReferral, inputDateBirth, inputGender;
    TextView whomRef, fullname, tvD, tvG, tvR;
    private Button btnSignIn, btnSignUp, btnResetPassword, btnUser, btnAdvertiser;
    private ProgressBar progressBar;

    AppData data;

    String email, password, name, number, referral, age, gender, refLink;
    //CallbackManager mCallbackManager;
    String id = "", refEmail = "", refCode = "", tag = "";
    //    BottomSheetDialog dialog;
//    TextView bsClose;
//    Button bsFacebook, bsGoogle;
//    GoogleApiClient mGoogleApiClient;
//    LoginButton loginButton;
//    ProfileTracker profileTracker;
//    String photo = "";
    boolean isExist = false;
    FirebaseAuth auth;
    Utils utils;
    String dataToGive = "";
    private PackageManager manager;
    private List<AppDetail> apps;

    //protected GeoDataClient mGeoDataClient;
    //protected PlaceDetectionClient mPlaceDetectionClient;

    private Location mLastLocation;

    double latitude;
    double longitude;

    LocationHelper locationHelper;


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
        utils = new Utils(SignupActivity.this);
        locationHelper = new LocationHelper(this);
        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputFullName = findViewById(R.id.fullname);
        inputNumber = findViewById(R.id.number);
        inputReferral = findViewById(R.id.refCode);
        inputDateBirth = findViewById(R.id.dob);
        inputGender = findViewById(R.id.gender);
        whomRef = findViewById(R.id.referCoder);
        tvD = findViewById(R.id.tvDob);
        tvG = findViewById(R.id.tvGender);
        tvR = findViewById(R.id.tvReferral);
        fullname = findViewById(R.id.tvFullname);
        progressBar = findViewById(R.id.progressBar);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        btnUser = findViewById(R.id.selection_one);
        btnAdvertiser = findViewById(R.id.selection_two);

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUser.setBackgroundResource(R.color.colorPrimaryDark);
                btnAdvertiser.setBackgroundResource(R.color.colorAccent);
                inputDateBirth.setVisibility(View.VISIBLE);
                inputGender.setVisibility(View.VISIBLE);
                inputReferral.setVisibility(View.VISIBLE);
                tvD.setVisibility(View.VISIBLE);
                tvG.setVisibility(View.VISIBLE);
                tvR.setVisibility(View.VISIBLE);
                fullname.setText("Full Name");
                tag = "user";
            }
        });

        btnAdvertiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdvertiser.setBackgroundResource(R.color.colorPrimaryDark);
                btnUser.setBackgroundResource(R.color.colorAccent);
                inputDateBirth.setVisibility(View.GONE);
                inputGender.setVisibility(View.GONE);
                inputReferral.setVisibility(View.GONE);
                tvD.setVisibility(View.GONE);
                tvG.setVisibility(View.GONE);
                tvR.setVisibility(View.GONE);
                fullname.setText("Company Name");
                tag = "advertiser";
                utils.error("Address = ");
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

        inputGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(SignupActivity.this)
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .title("Select Gender")
                        .items(R.array.gender)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                inputGender.setText(text.toString());
                                return true;
                            }
                        }).show();
            }
        });

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

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                name = inputFullName.getText().toString().trim();
                number = inputNumber.getText().toString().trim();
                referral = inputReferral.getText().toString().trim();
                age = inputDateBirth.getText().toString();
                gender = inputGender.getText().toString().trim();

                if (TextUtils.isEmpty(name) || !name.contains(" ")) {
                    Toast.makeText(getApplicationContext(), "Enter a full name!", Toast.LENGTH_SHORT).show();
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

                if (TextUtils.isEmpty(age)) {
                    Toast.makeText(getApplicationContext(), "Enter date of birth!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(gender)) {
                    Toast.makeText(getApplicationContext(), "Select gender!", Toast.LENGTH_SHORT).show();
                    return;
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
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Random r = new Random();
                        refCode = r.nextInt(9) + name.substring(0, 1) + r.nextInt(9) + email.substring(0, 1) + number.substring(number.length() - 1);
                        BuildDynamicLink(refCode, email);
                        User user = new User("", name, email, number, "", gender, tag, "52428800", refCode, "", data.getRegistrationToken());
                        if (tag.contentEquals("user")) {
                            Calendar calendar = Calendar.getInstance();
                            int year_now = calendar.get(Calendar.YEAR);
                            int selected_year = Integer.parseInt(age.split("-")[2]);
                            int user_age = year_now - selected_year;
                            user.setAge(String.valueOf(user_age));
                        }
                        AfterAccountCreation(user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        utils.dismissDialog();
                        utils.error("An error occurred. Try again. ");
                    }
                });

            }
        });
        GetAllApps();
        ReceiveDeepUrl();
        checkPermissions();
//        if (locationHelper.checkPlayServices()) {
//            // Building the GoogleApi client
//            locationHelper.buildGoogleApiClient();
//        }
        //getCurrentLocation();
    }

    private void checkPermissions() {
        PermissionManager pm = new PermissionManager(SignupActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        if (!pm.hasAllPermissions()) {
            pm.requestPermissions(345);
        } else {
            new LocationGetterBackgroundTask().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 345) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new LocationGetterBackgroundTask().execute();
            } else {
                utils.error("Please allow access to location to continue. Your location is not shared with any third party.");
                checkPermissions();
            }
        }
    }

    private void getCurrentLocation() {
        mLastLocation = locationHelper.getLocation();

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            getAddress();

        } else {
            Toast.makeText(getApplicationContext(), "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_SHORT).show();
        }
    }

    public void getAddress() {
        Address locationAddress;

        locationAddress = locationHelper.getAddress(latitude, longitude);

        if (locationAddress != null) {

            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();


            String currentLocation;

            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation += "\n" + address1;

                if (!TextUtils.isEmpty(city)) {
                    currentLocation += "\n" + city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += " - " + postalCode;
                } else {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += "\n" + postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation += "\n" + state;

                if (!TextUtils.isEmpty(country))
                    currentLocation += "\n" + country;

                Toast.makeText(SignupActivity.this, currentLocation, Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(SignupActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
            app.icon = ri.activityInfo.loadIcon(manager);
            apps.add(app);
        }
    }

    private void BuildDynamicLink(String ref, String email) {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("http://adsle.com?ref_code=" + ref + "&email=" + email + "&data=52428800"))
                .setDynamicLinkDomain("xpgf2.app.goo.gl")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.ad.adsle")
                        .build())
                .setGoogleAnalyticsParameters(new DynamicLink.GoogleAnalyticsParameters.Builder()
                        .setSource("adsle")
                        .setMedium("social")
                        .setCampaign("sharing")
                        .build())
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("Adsle App")
                        .setDescription("Get data ")
                        .build())
                .buildShortDynamicLink().addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            refLink = task.getResult().getShortLink().toString();
                        } else {
                            Log.e("refLinkError", "error" + task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //locationHelper.checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Log.i("Connection failed:", " ConnectionResult.getErrorCode() = "+ result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        //mLastLocation = locationHelper.getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        //locationHelper.connectApiClient();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String selected_date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        inputDateBirth.setText(selected_date);
    }

    private void GiveUserData() {
        if (!TextUtils.isEmpty(referral) || !TextUtils.isEmpty(refEmail)) {
            final long mb = Long.parseLong(dataToGive);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(refEmail).collection("user-data").document("signup");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Map<String, Object> snapshot = task.getResult().getData();
                        long data = Long.parseLong(String.valueOf(snapshot.get("bonus_data")));
                        data = data + mb;
                        Map<String, Object> param = new HashMap<>();
                        param.put("bonus_data", String.valueOf(data));
                        docRef.update(param);
                    }
                }
            });
        }
    }

    private void AfterAccountCreation(User user) {
        user.setReferralLink(refLink);


        GiveUserData();
    }
}
