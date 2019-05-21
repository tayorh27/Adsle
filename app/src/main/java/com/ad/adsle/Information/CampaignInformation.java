package com.ad.adsle.Information;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CampaignInformation implements Parcelable {

    String id, email, title;
    ArrayList<String> gender, religion;
    int age_range_min, age_range_max;
    ArrayList<String> locationDetails;
    ArrayList<String> interests;
    String campaign_image, campaign_link_option, campaign_link, campaign_duration_start, campaign_duration_end, campaign_amount_paid, cam_application_id;
    boolean status;

    long campaign_reach, views_number, reach_number, clicks_number, app_installs_number;
    String created_date;

    public CampaignInformation() {

    }

    public CampaignInformation(String id, String email, String title, int age_range_min, int age_range_max, ArrayList<String> gender, ArrayList<String> religion, ArrayList<String> locationDetails, ArrayList<String> interests, String campaign_image, String campaign_link_option, String campaign_link, long campaign_reach, String campaign_duration_start, String campaign_duration_end, String campaign_amount_paid, String cam_application_id, boolean status, long views_number, long reach_number, long clicks_number, long app_installs_number, String created_date) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.age_range_min = age_range_min;
        this.age_range_max = age_range_max;
        this.gender = gender;
        this.religion = religion;
        this.locationDetails = locationDetails;
        this.interests = interests;
        this.campaign_image = campaign_image;
        this.campaign_link_option = campaign_link_option;
        this.campaign_link = campaign_link;
        this.campaign_reach = campaign_reach;
        this.campaign_duration_start = campaign_duration_start;
        this.campaign_duration_end = campaign_duration_end;
        this.campaign_amount_paid = campaign_amount_paid;
        this.cam_application_id = cam_application_id;
        this.status = status;
        this.views_number = views_number;
        this.reach_number = reach_number;
        this.clicks_number = clicks_number;
        this.app_installs_number = app_installs_number;
        this.created_date = created_date;
    }

    protected CampaignInformation(Parcel in) {
        id = in.readString();
        email = in.readString();
        title = in.readString();
        gender = in.createStringArrayList();
        religion = in.createStringArrayList();
        age_range_min = in.readInt();
        age_range_max = in.readInt();
        locationDetails = in.createStringArrayList();
        interests = in.createStringArrayList();
        campaign_image = in.readString();
        campaign_link_option = in.readString();
        campaign_link = in.readString();
        campaign_reach = in.readLong();
        campaign_duration_start = in.readString();
        campaign_duration_end = in.readString();
        campaign_amount_paid = in.readString();
        cam_application_id = in.readString();
        status = in.readByte() != 0;
        views_number = in.readLong();
        reach_number = in.readLong();
        clicks_number = in.readLong();
        app_installs_number = in.readLong();
        created_date = in.readString();
    }

    public static final Creator<CampaignInformation> CREATOR = new Creator<CampaignInformation>() {
        @Override
        public CampaignInformation createFromParcel(Parcel in) {
            return new CampaignInformation(in);
        }

        @Override
        public CampaignInformation[] newArray(int size) {
            return new CampaignInformation[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAge_range_min() {
        return age_range_min;
    }

    public void setAge_range_min(int age_range_min) {
        this.age_range_min = age_range_min;
    }

    public int getAge_range_max() {
        return age_range_max;
    }

    public void setAge_range_max(int age_range_max) {
        this.age_range_max = age_range_max;
    }

    public ArrayList<String> getGender() {
        return gender;
    }

    public void setGender(ArrayList<String> gender) {
        this.gender = gender;
    }

    public ArrayList<String> getReligion() {
        return religion;
    }

    public void setReligion(ArrayList<String> religion) {
        this.religion = religion;
    }

    public ArrayList<String> getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(ArrayList<String> locationDetails) {
        this.locationDetails = locationDetails;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public String getCampaign_image() {
        return campaign_image;
    }

    public void setCampaign_image(String campaign_image) {
        this.campaign_image = campaign_image;
    }

    public String getCampaign_link_option() {
        return campaign_link_option;
    }

    public void setCampaign_link_option(String campaign_link_option) {
        this.campaign_link_option = campaign_link_option;
    }

    public String getCampaign_link() {
        return campaign_link;
    }

    public void setCampaign_link(String campaign_link) {
        this.campaign_link = campaign_link;
    }

    public long getCampaign_reach() {
        return campaign_reach;
    }

    public void setCampaign_reach(long campaign_reach) {
        this.campaign_reach = campaign_reach;
    }

    public String getCampaign_duration_start() {
        return campaign_duration_start;
    }

    public void setCampaign_duration_start(String campaign_duration_start) {
        this.campaign_duration_start = campaign_duration_start;
    }

    public String getCampaign_duration_end() {
        return campaign_duration_end;
    }

    public void setCampaign_duration_end(String campaign_duration_end) {
        this.campaign_duration_end = campaign_duration_end;
    }

    public String getCampaign_amount_paid() {
        return campaign_amount_paid;
    }

    public void setCampaign_amount_paid(String campaign_amount_paid) {
        this.campaign_amount_paid = campaign_amount_paid;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getViews_number() {
        return views_number;
    }

    public void setViews_number(long views_number) {
        this.views_number = views_number;
    }

    public long getReach_number() {
        return reach_number;
    }

    public void setReach_number(long reach_number) {
        this.reach_number = reach_number;
    }

    public long getClicks_number() {
        return clicks_number;
    }

    public void setClicks_number(long clicks_number) {
        this.clicks_number = clicks_number;
    }

    public long getApp_installs_number() {
        return app_installs_number;
    }

    public void setApp_installs_number(long app_installs_number) {
        this.app_installs_number = app_installs_number;
    }

    public String getCam_application_id() {
        return cam_application_id;
    }

    public void setCam_application_id(String cam_application_id) {
        this.cam_application_id = cam_application_id;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(title);
        dest.writeStringList(gender);
        dest.writeStringList(religion);
        dest.writeInt(age_range_min);
        dest.writeInt(age_range_max);
        dest.writeStringList(locationDetails);
        dest.writeStringList(interests);
        dest.writeString(campaign_image);
        dest.writeString(campaign_link_option);
        dest.writeString(campaign_link);
        dest.writeLong(campaign_reach);
        dest.writeString(campaign_duration_start);
        dest.writeString(campaign_duration_end);
        dest.writeString(campaign_amount_paid);
        dest.writeString(cam_application_id);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeLong(views_number);
        dest.writeLong(reach_number);
        dest.writeLong(clicks_number);
        dest.writeLong(app_installs_number);
        dest.writeString(created_date);
    }
}
