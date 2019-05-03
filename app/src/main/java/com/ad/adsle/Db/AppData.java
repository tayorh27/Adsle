package com.ad.adsle.Db;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ad.adsle.Activities.LoginActivity;
import com.ad.adsle.Information.User;
import com.google.firebase.auth.FirebaseAuth;

public class AppData {

    Context context;
    SharedPreferences prefs;
    FirebaseAuth auth;

    public AppData(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("adsle_data", 0);
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
        editor.putString("age", user.getAge());
        editor.putString("gender", user.getGender());
        editor.putString("tag", user.getTag());
        editor.putString("bonus_data", user.getBonus_data());
        editor.putString("referralCode", user.getReferralCode());
        editor.putString("referralLink", user.getReferralLink());
        editor.putString("loc_address", user.getLoc_address());
        editor.apply();
    }

    public User getUser() {
        return new User(
                prefs.getString("id", ""),
                prefs.getString("name", ""),
                prefs.getString("email", ""),
                prefs.getString("number", ""),
                prefs.getString("age", ""),
                prefs.getString("gender", ""),
                prefs.getString("tag", ""),
                prefs.getString("bonus_data", ""),
                prefs.getString("referralCode", ""),
                prefs.getString("referralLink", ""),
                prefs.getString("loc_address", "")
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
