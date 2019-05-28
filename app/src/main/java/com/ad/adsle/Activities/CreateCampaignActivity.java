package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.AppDetail;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.Information.Transactions;
import com.ad.adsle.Information.User;
import com.ad.adsle.MyApplication;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.github.bijoysingh.starter.util.PermissionManager;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CreateCampaignActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText inputCamTitle, inputCamLink, inputReached, inputApplicationID;
    Button btnInterest, btnExpire, btnExpire2;
    Spinner inputGender, inputReligion, inputAdLinkOption;
    TextView tvAgeRange, tvSummary;
    LinearLayout tvAT;
    CrystalRangeSeekbar rangeSeekbar;
    EasyFlipView easyFlipView;
    Button autocompleteFragment;
    ImageView imgCamImage;
    private int RESULT_LOAD_IMG = 19;

    String selected_date = "", end_selected_date = "";

    AppData data;

    String cam_title, cam_age, cam_gender, cam_religion, cam_link_options, ad_image, cam_link_text, cam_reached, cam_application_id;
    boolean isExist = false;
    FirebaseAuth auth;
    Utils utils;
    User user;
    Settings settings;
    private PackageManager manager;
    private ArrayList<AppDetail> apps;
    boolean isPlaceSelected = false;
    LocationDetails locationDetails;

    ArrayList<String> interests = new ArrayList<>();
    ArrayList<String> selected_interests = new ArrayList<>();

    ArrayList<String> locations = new ArrayList<>();
    ArrayList<String> selected_locations = new ArrayList<>();

    long total_amount = 0;
    CampaignInformation campaignInformation;

    StorageReference storageReference;
    boolean isPaymentMade = false;
    String authCode, card_reference;
    int _minValue = 0, _maxValue = 100;

    boolean isStart = false, isEnd = false;
    long people = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_campaign);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        data = new AppData(CreateCampaignActivity.this);
        if (auth.getCurrentUser() == null) {
            data.Logout();
            return;
        }
        user = data.getUser();
        settings = data.getSettings();
        utils = new Utils(CreateCampaignActivity.this);
        inputCamTitle = findViewById(R.id.cam_title);
        inputCamLink = findViewById(R.id.cam_link);
        inputReached = findViewById(R.id.cam_num_of_reached);
        tvAgeRange = findViewById(R.id.tvDob);
        tvSummary = findViewById(R.id.cam_summary);
        tvAT = findViewById(R.id.tvAppTitle);
        rangeSeekbar = findViewById(R.id.rangeSeekbar1);
        inputGender = findViewById(R.id.gender);
        inputReligion = findViewById(R.id.religion);
        inputAdLinkOption = findViewById(R.id.cam_link_option);
        inputApplicationID = findViewById(R.id.cam_app_id);
        btnInterest = findViewById(R.id.cam_interest);
        btnExpire = findViewById(R.id.cam_duration);
        btnExpire2 = findViewById(R.id.cam_duration_end);
        easyFlipView = findViewById(R.id.easyFlipView);
        imgCamImage = findViewById(R.id.cam_image);
        autocompleteFragment = findViewById(R.id.autocomplete_fragment);

//        autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                // TODO: Get info about the selected place.
//                isPlaceSelected = true;
//                locationDetails = new LocationDetails(place.getName(), place.getName(), place.getName(), place.getAddress(), place.getAddress(), place.getLatLng().latitude + "," + place.getLatLng().longitude);
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//                // TODO: Handle the error.
//                Log.e("onError", "An error occurred: " + status);
//            }
//        });
        autocompleteFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locations.size() <= 0) {
                    utils.error("Please refresh page.");
                    return;
                }
                new MaterialDialog.Builder(CreateCampaignActivity.this)
                        .title("Select multiple locations")
                        .items(locations)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                selected_locations.clear();
                                for (CharSequence item : text) {
                                    selected_locations.add(String.valueOf(item));
                                }
                                StringBuilder _interest = new StringBuilder();
                                if (selected_locations.size() > 0) {
                                    for (String tx : selected_locations) {
                                        _interest.append(tx + ",");
                                    }
                                    String output = _interest.toString().substring(0, _interest.toString().length() - 1);
                                    autocompleteFragment.setText(output);
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

        btnExpire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStart = true;
                isEnd = false;
                Calendar calendar = Calendar.getInstance();
                new SpinnerDatePickerDialogBuilder()
                        .context(CreateCampaignActivity.this)
                        .callback(CreateCampaignActivity.this)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .defaultDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .minDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .build()
                        .show();
            }
        });

        btnExpire2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStart = false;
                isEnd = true;
                Calendar calendar = Calendar.getInstance();
                new SpinnerDatePickerDialogBuilder()
                        .context(CreateCampaignActivity.this)
                        .callback(CreateCampaignActivity.this)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .defaultDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .minDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .build()
                        .show();
            }
        });

        inputAdLinkOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                inputCamLink.setText("");
                switch (position) {
                    case 0:
                        inputCamLink.setHint("Enter app link");
                        inputCamLink.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                        tvAT.setVisibility(View.VISIBLE);
                        inputCamLink.setVisibility(View.VISIBLE);
                        //inputAdLinkOption.setBackgroundResource(0);
                        break;
                    case 1:
                        inputCamLink.setHint("Enter url link");
                        inputCamLink.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                        tvAT.setVisibility(View.GONE);
                        inputCamLink.setVisibility(View.VISIBLE);
                        //inputAdLinkOption.setBackgroundResource(0);
                        break;
                    case 2:
                        inputCamLink.setHint("Enter email address");
                        inputCamLink.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        tvAT.setVisibility(View.GONE);
                        inputCamLink.setVisibility(View.GONE);
                        //inputAdLinkOption.setBackgroundResource(R.drawable.editbg);
                        break;
                }
                if (!TextUtils.isEmpty(selected_date) && !TextUtils.isEmpty(end_selected_date)) {
                    String[] date = selected_date.split("-");
                    String[] end_date = end_selected_date.split("-");
                    summaryMaker(false, people, Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(end_date[0]), Integer.parseInt(end_date[1]), Integer.parseInt(end_date[2]));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inputReached.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String reached = String.valueOf(s);
                String[] date = selected_date.split("-");
                String[] end_date = end_selected_date.split("-");
                if (!TextUtils.isEmpty(reached)) {
                    people = Long.parseLong(reached);
                    long total_users = settings.getTotal_users();
                    if (people > total_users) {
                        inputReached.setText("");
                        Toast.makeText(CreateCampaignActivity.this, "Maximum number of reach cannot exceed " + total_users, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!TextUtils.isEmpty(selected_date) && !TextUtils.isEmpty(end_selected_date)) {
                        summaryMaker(false, people, Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(end_date[0]), Integer.parseInt(end_date[1]), Integer.parseInt(end_date[2]));
                    }
                } else {
                    if (!TextUtils.isEmpty(selected_date) && !TextUtils.isEmpty(end_selected_date)) {
                        summaryMaker(false, 0, Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(end_date[0]), Integer.parseInt(end_date[1]), Integer.parseInt(end_date[2]));
                    } else {
                        summaryMaker(true, 0, 0, 0, 0, 0, 0, 0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvAgeRange.setText(String.valueOf(minValue) + " - " + String.valueOf(maxValue) + "+");
                _minValue = Integer.parseInt(String.valueOf(minValue));
                _maxValue = Integer.parseInt(String.valueOf(maxValue));
            }
        });

        imgCamImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AskForPermission();
            }
        });

        btnInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interests.size() <= 0) {
                    utils.error("Please refresh page.");
                    return;
                }
                new MaterialDialog.Builder(CreateCampaignActivity.this)
                        .title("Select multiple interest")
                        .items(interests)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                selected_interests.clear();
                                for (CharSequence item : text) {
                                    selected_interests.add(String.valueOf(item));
                                }
                                StringBuilder _interest = new StringBuilder();
                                if (selected_interests.size() > 0) {
                                    for (String tx : selected_interests) {
                                        _interest.append(tx + ",");
                                    }
                                    String output = _interest.toString().substring(0, _interest.toString().length() - 1);
                                    btnInterest.setText(output);
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
        summaryMaker(true, 0, 0, 0, 0, 0, 0, 0);
        MyApplication.fetchSettings();
        loadInterestsAndLocations();
    }

    public void PayAndCreateAd(View view) {
        String[] _genders = getResources().getStringArray(R.array.cam_gender);
        String[] _religions = getResources().getStringArray(R.array.cam_religion);
        String[] _link_options = getResources().getStringArray(R.array.cam_link_options);

        cam_title = inputCamTitle.getText().toString().trim();
        cam_application_id = inputApplicationID.getText().toString().trim();
        cam_link_text = inputCamLink.getText().toString().trim();
        cam_reached = inputReached.getText().toString().trim();
        cam_age = tvAgeRange.getText().toString().replace(" ", "");
        cam_gender = _genders[inputGender.getSelectedItemPosition()];
        cam_religion = _religions[inputReligion.getSelectedItemPosition()];
        cam_link_options = _link_options[inputAdLinkOption.getSelectedItemPosition()];

        if (TextUtils.isEmpty(cam_title)) {
            Toast.makeText(getApplicationContext(), "Enter campaign title!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selected_locations.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter target locations!", Toast.LENGTH_SHORT).show();
            return;
        }

        //locationDetails = new LocationDetails("EX", "EX", "EX", "EX", "XE", "XE");

        if (inputAdLinkOption.getSelectedItemPosition() < 2) {
            if (TextUtils.isEmpty(cam_link_text)) {
                Toast.makeText(getApplicationContext(), "Enter ad link!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cam_link_text.startsWith("http")) {
                Toast.makeText(getApplicationContext(), "Please link must start with http!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (inputAdLinkOption.getSelectedItemPosition() == 0) {
            if (TextUtils.isEmpty(cam_application_id)) {
                Toast.makeText(getApplicationContext(), "Enter package id of your app!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (TextUtils.isEmpty(cam_reached)) {
            Toast.makeText(getApplicationContext(), "Enter number of people to reach this ad!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ad_image)) {
            Toast.makeText(getApplicationContext(), "Set an image for your campaign", Toast.LENGTH_SHORT).show();
            return;
        } else {
            File imageFile = new File(ad_image);
            long size = imageFile.length();
            if (size > 204800) {
                Toast.makeText(getApplicationContext(), "Image size must not be greater than 200KB", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (TextUtils.isEmpty(selected_date)) {
            Toast.makeText(getApplicationContext(), "Set a start date for your campaign", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(end_selected_date)) {
            Toast.makeText(getApplicationContext(), "Set an end date for your campaign", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> create_gender = new ArrayList<>();
        ArrayList<String> create_religion = new ArrayList<>();
        if (inputGender.getSelectedItemPosition() == 2) {
            create_gender.add("Male");
            create_gender.add("Female");
        } else {
            create_gender.add(cam_gender);
        }

        if (inputReligion.getSelectedItemPosition() == 2) {
            create_religion.add("Christianity");
            create_religion.add("Islam");
        } else {
            create_religion.add(cam_religion);
        }

        DatabaseReference refId = FirebaseDatabase.getInstance().getReference();
        String id = refId.push().getKey();
        campaignInformation = new CampaignInformation(id, user.getEmail(), cam_title, _minValue, _maxValue, create_gender, create_religion, selected_locations, selected_interests, "", cam_link_options, cam_link_text, Long.parseLong(cam_reached), selected_date, end_selected_date, "₦" + String.valueOf(total_amount),
                cam_application_id, false, 0, 0, 0,
                0, new Date().toLocaleString());
        if (isPaymentMade) {
            isPaymentMade = true;
            campaignInformation.setStatus(true);
            UploadAdImageToServer(authCode, card_reference);
        } else {
            Bundle bundle = new Bundle();
            bundle.putLong("total_amount", total_amount);
            Intent mIntent = new Intent(CreateCampaignActivity.this, PaymentActivity.class).putExtras(bundle);
            startActivityForResult(mIntent, 900);
        }
    }

    private void summaryMaker(boolean start, long people, int start_day, int start_month, int start_year, int end_day, int end_month, int end_year) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        Calendar calendar = Calendar.getInstance();
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH);
        int y = calendar.get(Calendar.YEAR);
        if (start) {
            tvSummary.setText("You will spend ₦0.00. This ad will run for 0 day, ending on " + months[m] + " " + d + ", " + y + ".");
        } else {

            Calendar start_calendar = Calendar.getInstance();
            start_calendar.set(Calendar.DAY_OF_MONTH, start_day);
            start_calendar.set(Calendar.MONTH, start_month);
            start_calendar.set(Calendar.YEAR, start_year);
            Date startDate = start_calendar.getTime();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, end_day);
            cal.set(Calendar.MONTH, end_month);
            cal.set(Calendar.YEAR, end_year);
            Date endDate = cal.getTime();


            long diff = endDate.getTime() - startDate.getTime();
            int days = (int) (diff / (1000 * 60 * 60 * 24));

            if (end_day == 0 || end_month == 0 || end_year == 0) {
                return;
            }

            if (diff < 1) {
                end_selected_date = "";
                btnExpire2.setText("");
                utils.error("Please select a future date.");
                return;
            }

            int position = inputAdLinkOption.getSelectedItemPosition();
            long amount = 0;
            if (position == 0) {
                amount = people * days * settings.getAmount_per_app_install();
            } else if (position == 1) {
                amount = people * days * settings.getAmount_per_click();
            } else if (position == 2) {
                amount = people * days * settings.getAmount_per_reach();
            }
            double percent = (amount * 1.5) / 100;
            total_amount = (long) (amount + percent);
            tvSummary.setText("You will spend ₦" + total_amount + ".00 . This ad will run for " + days + " day(s), ending on " + months[end_month] + " " + end_day + ", " + end_year + ".");
        }

    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);
    }

    private void AskForPermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION};
        PermissionManager manager = new PermissionManager(this, permissions);
        if (!manager.hasAllPermissions()) {
            manager.requestPermissions(234);
        } else {
            selectImage();
        }
    }

    private void loadInterestsAndLocations() {
        utils.displayDialog("Please wait...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("settings").document("interests").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    interests.clear();
                    interests = (ArrayList<String>) task.getResult().get("data");
                    db.collection("settings").document("locations").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            utils.dismissDialog();
                            if (task.isSuccessful()) {
                                locations.clear();
                                locations = (ArrayList<String>) task.getResult().get("data");
                            }
                        }
                    });
                }
            }
        });
    }

    public void NextClicked(View view) {
        easyFlipView.flipTheView();
    }

    public void BackClicked(View view) {
        easyFlipView.flipTheView();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 234) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                utils.error("Please allow access to storage to upload your ad image.");
                AskForPermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG) {
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    String ext = getFileExtension(data.getData());
                    if (ext.contains("gif")) {
                        ad_image = getImagePath(data.getData());
                        Log.e("kjsf", "onActivityResult: " + ad_image);
                        Bitmap bitmap = BitmapFactory.decodeFile(ad_image);
                        //Bitmap bitmap = getBitmapFromUri(data.getData());
                        imgCamImage.setImageBitmap(bitmap);
                        return;
                    }
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAutoZoomEnabled(true)
                            .setOutputCompressQuality(70)
                            .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                            .start(this);
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                try {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        ad_image = resultUri.getPath();
                        Log.e("cc", "CropImage: " + ad_image);
                        Bitmap bitmap = BitmapFactory.decodeFile(ad_image);
                        imgCamImage.setImageBitmap(bitmap);
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == 900) {
                if (resultCode == RESULT_OK) {
                    boolean status = data.getBooleanExtra("status", false);
                    authCode = data.getStringExtra("authCode");
                    card_reference = data.getStringExtra("card_reference");
                    if (status) {
                        isPaymentMade = true;
                        campaignInformation.setStatus(true);
                        UploadAdImageToServer(authCode, card_reference);
                    } else {
                        utils.error("Payment processing failed. Try again.");
                    }
                }
            }
        } catch (Exception e) {
            Log.e("onActivityResult", "something went wrong - " + e.toString());
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void UploadAdImageToServer(String authCode, String card_reference) {
        utils.displayDialog("Initializing campaign...");
        storageReference = FirebaseStorage.getInstance().getReference();
        File imageFile = new File(ad_image);
        Uri file = Uri.fromFile(imageFile);
        String img_name = file.getLastPathSegment();
        String getExtension = img_name.substring(img_name.lastIndexOf(".") + 1);
        //String getExtension = getFileExtension(file);
        //Log.e("getExtension", "UploadAdImageToServer: " + getExtension);
//        imgCamImage.setDrawingCacheEnabled(true);
//        imgCamImage.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) imgCamImage.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        //byte[] data = baos.toByteArray();
        final StorageReference path = storageReference.child("images/" + System.currentTimeMillis() + "." + getExtension);
        //UploadTask uploadTask = path.putBytes(data);
        UploadTask uploadTask = path.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    utils.dismissDialog();
                    Toast.makeText(CreateCampaignActivity.this, "Something went wrong. Try again",
                            Toast.LENGTH_SHORT).show();
                }
                // Continue with the task to get the download URL
                return path.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    utils.dismissDialog();
                    Uri downloadUri = task.getResult();
                    campaignInformation.setCampaign_image(downloadUri.toString());
                    ContinueWithAdTask(authCode, card_reference);
                } else {
                    errorOccurred();
                }
            }
        });
    }

    private void ContinueWithAdTask(String authCode, String card_reference) {
        utils.displayDialog("Creating your campaign...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();//user.getEmail()
        db.collection("campaigns").document(campaignInformation.getId()).set(campaignInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> settings_params = new HashMap<>();
                    settings_params.put("current_campaign", campaignInformation.getId());
                    settings_params.put("authCode", authCode);
                    db.collection("users").document(user.getEmail()).collection("user-data").document("settings").set(settings_params);
                    DatabaseReference refId = FirebaseDatabase.getInstance().getReference();
                    String id = refId.push().getKey();
                    Transactions transactions = new Transactions(id, campaignInformation.getId(), String.valueOf(total_amount), new Date().toLocaleString(), card_reference);
                    db.collection("users").document(user.getEmail()).collection("user-data").document("transactions").collection("user-trans").document(id).set(transactions);
                    Toast.makeText(CreateCampaignActivity.this, "Campaign created successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(CreateCampaignActivity.this, HomeActivity.class));
                    finish();
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (isStart) {
            int people = (TextUtils.isEmpty(inputReached.getText().toString())) ? 0 : Integer.parseInt(inputReached.getText().toString());
            int ed = 0, em = 0, ey = 0;
            if (!TextUtils.isEmpty(end_selected_date)) {
                String[] date = end_selected_date.split("-");
                ed = Integer.parseInt(date[0]);
                em = Integer.parseInt(date[1]);
                ey = Integer.parseInt(date[2]);
            }
            summaryMaker(false, people, dayOfMonth, monthOfYear, year, ed, em, ey);
            selected_date = dayOfMonth + "-" + monthOfYear + "-" + year;
            String _selected_date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            btnExpire.setText(_selected_date);
        }
        if (isEnd) {
            int people = (TextUtils.isEmpty(inputReached.getText().toString())) ? 0 : Integer.parseInt(inputReached.getText().toString());
            int sd = 0, sm = 0, sy = 0;
            if (!TextUtils.isEmpty(selected_date)) {
                String[] date = selected_date.split("-");
                sd = Integer.parseInt(date[0]);
                sm = Integer.parseInt(date[1]);
                sy = Integer.parseInt(date[2]);
            }
            summaryMaker(false, people, sd, sm, sy, dayOfMonth, monthOfYear, year);
            end_selected_date = dayOfMonth + "-" + monthOfYear + "-" + year;
            String _selected_date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            btnExpire2.setText(_selected_date);
        }
    }
}
