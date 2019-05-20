package com.ad.adsle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ViewCampaignActivity extends AppCompatActivity {

    CampaignInformation current_campaign;
    AppCompatTextView camM1, camM2, camM3, camM4, viewCam, t1, t2, t3;
    ListView listView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_campaign);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        current_campaign = getIntent().getParcelableExtra("current_campaign");

        getSupportActionBar().setTitle(current_campaign.getTitle());

        listView = findViewById(R.id.list_view_cam_details);
        camM1 = findViewById(R.id.cam_menu1);
        camM2 = findViewById(R.id.cam_menu2);
        camM3 = findViewById(R.id.cam_menu3);
        camM4 = findViewById(R.id.cam_menu4);

        updateViews();
    }

    private void updateViews() {
        if (current_campaign != null) {
            camM1.setText(current_campaign.getApp_installs_number() + "");
            camM2.setText(current_campaign.getClicks_number() + "");
            camM3.setText(current_campaign.getReach_number() + "");
            camM4.setText(current_campaign.getViews_number() + "");
        }
        List<String> stringList = new ArrayList<>();
        if (current_campaign.getGender().size() == 1) {
            stringList.add("Gender:\n" + current_campaign.getGender().get(0));
        }
        if (current_campaign.getGender().size() == 2) {
            stringList.add("Gender:\n" + current_campaign.getGender().get(0) + "," + current_campaign.getGender().get(1));
        }
        if (current_campaign.getReligion().size() == 1) {
            stringList.add("Religion:\n" + current_campaign.getReligion().get(0));
        }
        if (current_campaign.getReligion().size() == 2) {
            stringList.add("Religion:\n" + current_campaign.getReligion().get(0) + "," + current_campaign.getReligion().get(1));
        }
        StringBuilder _interest = new StringBuilder();
        for (String text : current_campaign.getInterests()) {
            _interest.append(text + ",");
        }
        String output_interest = _interest.toString().substring(0, _interest.toString().length() - 1);

        StringBuilder _locations = new StringBuilder();
        for (String text : current_campaign.getLocationDetails()) {
            _locations.append(text + ",");
        }
        String output_location = _locations.toString().substring(0, _locations.toString().length() - 1);

        stringList.add("Minimum Age Range:\n" + current_campaign.getAge_range_min());
        stringList.add("Maximum Age Range:\n" + current_campaign.getAge_range_max());
        stringList.add("Campaign Option:\n" + current_campaign.getCampaign_link_option());
        stringList.add("Campaign Link:\n" + current_campaign.getCampaign_link());
        stringList.add("Campaign Start Date:\n" + current_campaign.getCampaign_duration_start());
        stringList.add("Campaign End Date:\n" + current_campaign.getCampaign_duration_end());
        stringList.add("Campaign Interests:\n" + output_interest);
        stringList.add("Campaign Locations:\n" + output_location);
        stringList.add("Amount Paid:\n" + current_campaign.getCampaign_amount_paid());
        stringList.add("Created Date:\n" + current_campaign.getCreated_date());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewCampaignActivity.this, android.R.layout.simple_list_item_1, stringList);
        listView.setAdapter(adapter);
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
