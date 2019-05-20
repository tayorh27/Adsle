package com.ad.adsle.Db;

import android.content.Context;
import android.content.SharedPreferences;

public class CampaignData {

    Context context;
    SharedPreferences prefs;

    public CampaignData(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("adsle_campaign_data", 0);
    }

    public void setCampaignSize(int size) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("CampaignSize", size);
        editor.apply();
    }

    public int getCampaignSize() {
        return prefs.getInt("CampaignSize", 0);
    }

    public void setNext(int next) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("next", next);
        editor.apply();
    }

    public int getNext() {
        return prefs.getInt("next", 0);
    }

    public void setReached(boolean reached, String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, reached);
        editor.apply();
    }

    public boolean getReached(String key) {
        return prefs.getBoolean(key, false);
    }

    public void setAppInstallId(boolean isAppInstall, String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, isAppInstall);
        editor.apply();
    }

    public boolean getAppInstallId(String key) {
        return prefs.getBoolean(key, false);
    }

    public void setClicked(boolean isClick, String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, isClick);
        editor.apply();
    }

    public boolean getClicked(String key) {
        return prefs.getBoolean(key, false);
    }
}
