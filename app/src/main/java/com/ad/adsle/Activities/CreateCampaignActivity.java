package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.AppDetail;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.Transactions;
import com.ad.adsle.Information.User;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CreateCampaignActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText inputCamTitle, inputCamLink, inputReached;
    Button btnInterest, btnExpire;
    Spinner inputGender, inputReligion, inputAdLinkOption;
    TextView tvAgeRange, tvSummary;
    CrystalRangeSeekbar rangeSeekbar;
    EasyFlipView easyFlipView;
    AutocompleteSupportFragment autocompleteFragment;
    ImageView imgCamImage;
    private int RESULT_LOAD_IMG = 19;

    String selected_date = "";

    AppData data;

    String cam_title, cam_age, cam_gender, cam_religion, cam_link_options, ad_image, cam_link_text, cam_reached;
    boolean isExist = false;
    FirebaseAuth auth;
    Utils utils;
    User user;
    private PackageManager manager;
    private ArrayList<AppDetail> apps;
    boolean isPlaceSelected = false;
    LocationDetails locationDetails;

    ArrayList<String> interests = new ArrayList<>();
    ArrayList<String> selected_interests = new ArrayList<>();

    long total_amount = 0;
    CampaignInformation campaignInformation;

    StorageReference storageReference;
    boolean isPaymentMade = false;
    String authCode, card_reference;
    String _minValue = "0", _maxValue = "100";

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
        utils = new Utils(CreateCampaignActivity.this);
        inputCamTitle = findViewById(R.id.cam_title);
        inputCamLink = findViewById(R.id.cam_link);
        inputReached = findViewById(R.id.cam_num_of_reached);
        tvAgeRange = findViewById(R.id.tvDob);
        tvSummary = findViewById(R.id.cam_summary);
        rangeSeekbar = findViewById(R.id.rangeSeekbar1);
        inputGender = findViewById(R.id.gender);
        inputReligion = findViewById(R.id.religion);
        inputAdLinkOption = findViewById(R.id.cam_link_option);
        btnInterest = findViewById(R.id.cam_interest);
        btnExpire = findViewById(R.id.cam_duration);
        easyFlipView = findViewById(R.id.easyFlipView);
        imgCamImage = findViewById(R.id.cam_image);

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                isPlaceSelected = true;
                locationDetails = new LocationDetails(place.getName(), place.getName(), place.getName(), place.getAddress(), place.getAddress(), place.getLatLng().latitude + "," + place.getLatLng().longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.e("onError", "An error occurred: " + status);
            }
        });

        btnExpire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        break;
                    case 1:
                        inputCamLink.setHint("Enter phone number");
                        inputCamLink.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case 2:
                        inputCamLink.setHint("Enter email address");
                        inputCamLink.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case 3:
                        inputCamLink.setHint("Enter url link");
                        inputCamLink.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                        break;
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
                if (!TextUtils.isEmpty(reached)) {
                    if (!TextUtils.isEmpty(selected_date)) {
                        int people = Integer.parseInt(reached);
                        summaryMaker(false, people, Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
                    }
                } else {
                    if (!TextUtils.isEmpty(selected_date)) {
                        summaryMaker(false, 0, Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
                    } else {
                        summaryMaker(true, 0, 0, 0, 0);
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
                tvAgeRange.setText(String.valueOf(minValue) + " - " + String.valueOf(maxValue));
                _minValue = String.valueOf(minValue);
                _maxValue = String.valueOf(maxValue);
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
        summaryMaker(true, 0, 0, 0, 0);
        loadInterests();

    }

    public void PayAndCreateAd(View view) {
        String[] _genders = getResources().getStringArray(R.array.cam_gender);
        String[] _religions = getResources().getStringArray(R.array.cam_religion);
        String[] _link_options = getResources().getStringArray(R.array.cam_link_options);

        cam_title = inputCamTitle.getText().toString().trim();
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

//        if (!isPlaceSelected) {
//            Toast.makeText(getApplicationContext(), "Enter target location!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        locationDetails = new LocationDetails("EX", "EX", "EX", "EX", "XE", "XE");

        if (TextUtils.isEmpty(cam_link_text)) {
            Toast.makeText(getApplicationContext(), "Enter ad link!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(cam_reached)) {
            Toast.makeText(getApplicationContext(), "Enter number of people to reach this ad!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ad_image)) {
            Toast.makeText(getApplicationContext(), "Set an image for your campaign", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selected_date)) {
            Toast.makeText(getApplicationContext(), "Set a duration for your campaign", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference refId = FirebaseDatabase.getInstance().getReference();
        String id = refId.push().getKey();
        campaignInformation = new CampaignInformation(id, user.getEmail(), cam_title, _minValue, _maxValue, cam_gender, cam_religion, locationDetails, selected_interests, "", cam_link_options, cam_link_text, cam_reached, selected_date, String.valueOf(total_amount),
                false, "0", "0", "0",
                "0", new Date().toLocaleString());
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

    private void summaryMaker(boolean start, int people, int day, int month, int year) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        Calendar calendar = Calendar.getInstance();
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH);
        int y = calendar.get(Calendar.YEAR);
        if (start) {
            tvSummary.setText("You will spend ₦0.00. This ad will run for 0 day, ending on " + months[m] + " " + d + ", " + y + ".");
        } else {

            Date firstDate = calendar.getTime();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, year);
            Date secondDate = cal.getTime();


            long diff = secondDate.getTime() - firstDate.getTime();
            int days = (int) (diff / (1000 * 60 * 60 * 24));

            if (diff < 1) {
                utils.error("Please select a future date.");
                return;
            }

            long amount = people * days;

            double percent = (amount * 1.5) / 100;

            total_amount = (long) (amount + percent);

            tvSummary.setText("You will spend ₦" + total_amount + ".00 . This ad will run for " + days + " day(s), ending on " + months[month] + " " + day + ", " + year + ".");
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

    private void loadInterests() {
        utils.displayDialog("Please wait...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("settings").document("interests").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                utils.dismissDialog();
                if (task.isSuccessful()) {
                    interests.clear();
                    interests = (ArrayList<String>) task.getResult().get("data");
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
                if (resultCode == RESULT_OK && data != null && data.getData() != null)
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAutoZoomEnabled(true)
                            .setOutputCompressQuality(80)
                            //.setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                            .start(this);
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                try {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        ad_image = resultUri.getPath();
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
        int people = (TextUtils.isEmpty(inputReached.getText().toString())) ? 0 : Integer.parseInt(inputReached.getText().toString());
        summaryMaker(false, people, dayOfMonth, monthOfYear, year);
        selected_date = dayOfMonth + "-" + monthOfYear + "-" + year;
        String _selected_date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        btnExpire.setText(_selected_date);
    }
}
