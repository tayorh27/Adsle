package com.ad.adsle.Db;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.ad.adsle.Activities.LoginActivity;
import com.ad.adsle.Information.DeviceDetails;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AppData {

    Context context;
    SharedPreferences prefs;
    FirebaseAuth auth;

    public AppData(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("adsle_data", 0);
    }

    public void setRegistrationToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mToken", token);
        editor.apply();
    }

    public String getRegistrationToken() {
        return prefs.getString("mToken", "");
    }

    public void setLogged(boolean logged) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("logged", logged);
        editor.apply();
    }

    public boolean getLogged() {
        return prefs.getBoolean("logged", false);
    }

    public void setInterestSelected(boolean connected) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("connected", connected);
        editor.apply();
    }

    public boolean getInterestSelected() {
        return prefs.getBoolean("connected", false);
    }

    public void StoreUsers(User user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", user.getId());
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("number", user.getNumber());
        editor.putInt("age", user.getAge());
        editor.putString("gender", user.getGender());
        editor.putString("religion", user.getReligion());
        editor.putString("tag", user.getTag());
        editor.putString("bonus_data", user.getBonus_data());
        editor.putString("referralCode", user.getReferralCode());
        editor.putString("referralLink", user.getReferralLink());
        editor.putString("msgId", user.getMsgId());
        editor.putString("deviceId", user.getDeviceId());
        editor.putString("created_date", user.getCreated_date());
        editor.apply();
    }

    public User getUser() {
        return new User(
                prefs.getString("id", ""),
                prefs.getString("name", ""),
                prefs.getString("email", ""),
                prefs.getString("number", ""),
                prefs.getInt("age", 0),
                prefs.getString("gender", ""),
                prefs.getString("religion", ""),
                prefs.getString("tag", ""),
                prefs.getString("bonus_data", ""),
                prefs.getString("referralCode", ""),
                prefs.getString("referralLink", ""),
                prefs.getString("msgId", ""),
                prefs.getString("deviceId", ""),
                prefs.getString("created_date", "")
        );
    }

    public void StoreDeviceDetails(DeviceDetails deviceDetails) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Release_Build_Version", deviceDetails.getRelease_build_version());
        editor.putString("Build_Version_Code_Name", deviceDetails.getBuild_version_code_name());
        editor.putString("Manufacturer", deviceDetails.getManufacturer());
        editor.putString("Model", deviceDetails.getModel());
        editor.putString("Product", deviceDetails.getProduct());
        editor.putString("Display_Version", deviceDetails.getDisplay_version());
        editor.putString("Os_Version", deviceDetails.getOs_version());
        editor.putString("SDK_Version", deviceDetails.getSdk_version());
        editor.apply();
    }

    public DeviceDetails getDeviceDetails() {
        return new DeviceDetails(
                prefs.getString("Release_Build_Version", ""),
                prefs.getString("Build_Version_Code_Name", ""),
                prefs.getString("Manufacturer", ""),
                prefs.getString("Model", ""),
                prefs.getString("Product", ""),
                prefs.getString("Display_Version", ""),
                prefs.getString("Os_Version", ""),
                prefs.getString("SDK_Version", "")
        );
    }

    public void StoreLocationDetails(LocationDetails locationDetails) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("city", locationDetails.getCity());
        editor.putString("area", locationDetails.getArea());
        editor.putString("inside_area", locationDetails.getInside_area());
        editor.putString("formatted_address", locationDetails.getFormatted_address());
        editor.putString("country", locationDetails.getCountry());
        editor.putString("latlng", locationDetails.getLatlng());
        editor.apply();
    }

    public LocationDetails getLocationDetails() {
        return new LocationDetails(
                prefs.getString("city", ""),
                prefs.getString("area", ""),
                prefs.getString("inside_area", ""),
                prefs.getString("formatted_address", ""),
                prefs.getString("country", ""),
                prefs.getString("latlng", "")
        );
    }

    public void SaveTokenForData(String token, String date_expires) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        editor.putString("date_expires", date_expires);
        editor.apply();
    }

    public String[] getTokenForData() {
        return new String[]{
                prefs.getString("token", ""),
                prefs.getString("date_expires", ""),
        };
    }

    public void Logout() {
//        String number = getUser().getNumber();
//        String email = getUser().getEmail();
//        String pass = getUser().getPin();
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
//        setTourShown(true);
//        setLoginNumber(number);
//        setLoginEmail(email);
//        setLoginPassword(pass);
        context.startActivity(new Intent(context, LoginActivity.class));
    }

}
