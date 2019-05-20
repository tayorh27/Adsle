package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InviteActivity extends AppCompatActivity {

    AppData data;
    User user;
    Settings settings;
    Utils utils;
    TextView tvInvite;

    boolean isLinkDone = false;
    String refLink = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = new AppData(InviteActivity.this);
        utils = new Utils(InviteActivity.this);
        user = data.getUser();
        settings = data.getSettings();

        tvInvite = findViewById(R.id.tvInviteText);
        String dataToGet = utils.getExactDataValue(String.valueOf(settings.getInvite_bonus_data()));
        tvInvite.setText("Invite 5 of your friends and get FREE " + dataToGet + " data when they signup using your referral link.");
    }

    public void SendInvite(View view) {
        if (user.getReferralLink() == null || TextUtils.isEmpty(user.getReferralLink())) {
            utils.displayDialog("Please wait...");
            BuildDynamicLink(user.getReferralCode(), user.getEmail());
            return;
        }
        sendInvite();
    }

    private void sendInvite() {
        String txt = "Hey,\n\nAdsle App is a fast, simple app that I use to get over 2gb of  FREE mobile data monthly by just allowing it to show ads on my phone home screen.\n\nAnd it does not intrude or disturb the way I use my phone.\n\nGet it for free at\n" + user.getReferralLink();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, txt);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Refer a friend"));
        }
    }

    private void BuildDynamicLink(String ref, String email) {
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
                        utils.dismissDialog();
                        if (task.isSuccessful()) {
                            refLink = task.getResult().getShortLink().toString();
                            user.setReferralLink(refLink);
                            data.StoreUsers(user);
                            SaveToDatabase(refLink);
                            sendInvite();
                        } else {
                            Log.e("refLinkError", "error" + task.getException());
                        }
                    }
                });
    }

    private void SaveToDatabase(String refLink) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getEmail()).collection("user-data").document("signup");
        docRef.update("referralLink", refLink);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //
            startActivity(new Intent(InviteActivity.this, HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
