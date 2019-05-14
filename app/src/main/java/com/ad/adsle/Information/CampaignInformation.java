package com.ad.adsle.Information;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CampaignInformation implements Parcelable {

    String id, email, title, age_range_min, age_range_max, gender, religion;
    LocationDetails locationDetails;
    ArrayList<String> interests;
    String campaign_image, campaign_link_option, campaign_link, campaign_reach, campaign_duration, campaign_amount_paid;
    boolean status;

    String views_number, reach_number, clicks_number, app_installs_number, created_date;

    public CampaignInformation() {

    }

    public CampaignInformation(String id, String email, String title, String age_range_min, String age_range_max, String gender, String religion, LocationDetails locationDetails, ArrayList<String> interests, String campaign_image, String campaign_link_option, String campaign_link, String campaign_reach, String campaign_duration, String campaign_amount_paid, boolean status, String views_number, String reach_number, String clicks_number, String app_installs_number, String created_date) {
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
        this.campaign_duration = campaign_duration;
        this.campaign_amount_paid = campaign_amount_paid;
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
        age_range_min = in.readString();
        age_range_max = in.readString();
        gender = in.readString();
        religion = in.readString();
        interests = in.createStringArrayList();
        campaign_image = in.readString();
        campaign_link_option = in.readString();
        campaign_link = in.readString();
        campaign_reach = in.readString();
        campaign_duration = in.readString();
        campaign_amount_paid = in.readString();
        status = in.readByte() != 0;
        views_number = in.readString();
        reach_number = in.readString();
        clicks_number = in.readString();
        app_installs_number = in.readString();
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

    public String getAge_range_min() {
        return age_range_min;
    }

    public void setAge_range_min(String age_range_min) {
        this.age_range_min = age_range_min;
    }

    public String getAge_range_max() {
        return age_range_max;
    }

    public void setAge_range_max(String age_range_max) {
        this.age_range_max = age_range_max;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public LocationDetails getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(LocationDetails locationDetails) {
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

    public String getCampaign_reach() {
        return campaign_reach;
    }

    public void setCampaign_reach(String campaign_reach) {
        this.campaign_reach = campaign_reach;
    }

    public String getCampaign_duration() {
        return campaign_duration;
    }

    public void setCampaign_duration(String campaign_duration) {
        this.campaign_duration = campaign_duration;
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

    public String getViews_number() {
        return views_number;
    }

    public void setViews_number(String views_number) {
        this.views_number = views_number;
    }

    public String getReach_number() {
        return reach_number;
    }

    public void setReach_number(String reach_number) {
        this.reach_number = reach_number;
    }

    public String getClicks_number() {
        return clicks_number;
    }

    public void setClicks_number(String clicks_number) {
        this.clicks_number = clicks_number;
    }

    public String getApp_installs_number() {
        return app_installs_number;
    }

    public void setApp_installs_number(String app_installs_number) {
        this.app_installs_number = app_installs_number;
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
        dest.writeString(age_range_min);
        dest.writeString(age_range_max);
        dest.writeString(gender);
        dest.writeString(religion);
        dest.writeStringList(interests);
        dest.writeString(campaign_image);
        dest.writeString(campaign_link_option);
        dest.writeString(campaign_link);
        dest.writeString(campaign_reach);
        dest.writeString(campaign_duration);
        dest.writeString(campaign_amount_paid);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(views_number);
        dest.writeString(reach_number);
        dest.writeString(clicks_number);
        dest.writeString(app_installs_number);
        dest.writeString(created_date);
    }
}
