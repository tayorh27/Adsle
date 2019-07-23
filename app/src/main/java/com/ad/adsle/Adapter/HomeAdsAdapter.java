package com.ad.adsle.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ad.adsle.Callbacks.AdsCallback;
import com.ad.adsle.Callbacks.HomeAdsCallback;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Db.CampaignData;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.R;
import com.ad.adsle.Util.AdUtils;
import com.ad.adsle.Util.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HomeAdsAdapter extends RecyclerView.Adapter<HomeAdsAdapter.AdHolder> {

    Activity context;
    Utils utils;
    AppData data;
    Settings settings;
    ArrayList<CampaignInformation> campaignInformation = new ArrayList<>();
    LayoutInflater inflater;
    HomeAdsCallback adsCallback;
    AdUtils adUtils;
    CampaignData campaignData;

    public HomeAdsAdapter(Activity context, HomeAdsCallback adsCallback) {
        this.context = context;
        this.adsCallback = adsCallback;
        utils = new Utils(context);
        data = new AppData(context);
        settings = data.getSettings();
        inflater = LayoutInflater.from(context);
        adUtils = new AdUtils(context, null);
        campaignData = new CampaignData(context);
    }

    public void setList(ArrayList<CampaignInformation> campaignInformation) {
        this.campaignInformation = campaignInformation;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_ad, parent, false);
        return new AdHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdHolder holder, int position) {
        CampaignInformation current = campaignInformation.get(position);
        if (current.getCampaign_image().toLowerCase().contains(".gif")) {
            try {
                Glide.with(context) //GifDrawable drawable =
                        .asGif()
                        .load(current.getCampaign_image()).into(holder.adImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Glide.with(context)//Bitmap bitmap =
                        .asBitmap()
                        .load(current.getCampaign_image()).into(holder.adImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String link_option = current.getCampaign_link_option();
        if (link_option.contentEquals("App Install")) {
            holder.adClick.setVisibility(View.VISIBLE);
            holder.adClick.setText("INSTALL APP");
            holder.adData.setText(utils.getExactDataValue(String.valueOf(settings.getApp_install_data())));
            holder.adText.setText("Install app and get " + utils.getExactDataValue(String.valueOf(settings.getApp_install_data())) + " free.");
        } else if (link_option.contentEquals("Click")) {
            holder.adClick.setVisibility(View.VISIBLE);
            holder.adClick.setText("VIEW");
            holder.adData.setText(utils.getExactDataValue(String.valueOf(settings.getClick_data())));
            holder.adText.setText("Click this ad to get " + utils.getExactDataValue(String.valueOf(settings.getClick_data())) + " free.");
        } else {
            holder.adClick.setVisibility(View.GONE);
            holder.adData.setText(utils.getExactDataValue(String.valueOf(settings.getReach_data())));
            holder.adText.setText("Powered by Adsle");
        }

        new BackGroundAnalyze(current).execute();
    }

    @Override
    public int getItemCount() {
        return campaignInformation.size();
    }

    private void analyzeDataAds(CampaignInformation campaignInformation) {

        long campaign_reach = campaignInformation.getCampaign_reach();
        long app_install_number = campaignInformation.getApp_installs_number();
        long click_number = campaignInformation.getClicks_number();
        long reach_number = campaignInformation.getReach_number();

        String link_option = campaignInformation.getCampaign_link_option();
        if (link_option.contentEquals("App Install")) {
            Log.e("HomeAdsAdapter", "DisplayAndSaveData ================ 2");
            if (app_install_number < campaign_reach) {
                if (campaignData.getAppInstallId(campaignInformation.getId())) {
                    adUtils.updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                } else {
                    boolean isAppInstalled = adUtils.CheckForAppInstallsStatus();
                    if (isAppInstalled) {
                        campaignData.setAppInstallId(true, campaignInformation.getId());///check here
                        adUtils.updateCampaignData(true, campaignInformation.getId(), "app_installs_number", 1);
                        adUtils.updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                    } else {
                        adUtils.updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                    }
                }
            } else {
                adUtils.updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
            }
        } else if (link_option.contentEquals("Click")) {
            Log.e("HomeAdsAdapter", "DisplayAndSaveData ================ 3");
            if (click_number < campaign_reach) {
                if (campaignData.getClicked(campaignInformation.getId())) {
                    Log.e("HomeAdsAdapter", "DisplayAndSaveData ================ 3.1");
                    adUtils.updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                } else {
                    Log.e("HomeAdsAdapter", "DisplayAndSaveData ================ 3.2");
                    adUtils.updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                }
                Log.e("HomeAdsAdapter", "DisplayAndSaveData ================ 3.3");
            } else {
                adUtils.updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                Log.e("HomeAdsAdapter", "DisplayAndSaveData ================ 3.4");
            }
        } else if (link_option.contentEquals("Reach")) {
            Log.e("HomeAdsAdapter", "DisplayAndSaveData ================ 4");
            if (reach_number < campaign_reach) {
                if (campaignData.getReached(campaignInformation.getId())) {
                    Log.e("HomeAdsAdapter", "DisplayAndSaveData ================ 4.1");
                    adUtils.updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                } else {
                    Log.e("HomeAdsAdapter", "DisplayAndSaveData ================ 4.2");
                    campaignData.setReached(true, campaignInformation.getId());
                    adUtils.updateCampaignData(true, campaignInformation.getId(), "reach_number", 1);
                    adUtils.updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
                }
            } else {
                adUtils.updateCampaignData(false, campaignInformation.getId(), "views_number", 1);
            }
        }
    }

    class BackGroundAnalyze extends AsyncTask<Void, Void, Void> {
        CampaignInformation _campaignInformation;

        BackGroundAnalyze(CampaignInformation _campaignInformation) {
            this._campaignInformation = _campaignInformation;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            analyzeDataAds(_campaignInformation);
            return null;
        }
    }


    class AdHolder extends RecyclerView.ViewHolder {

        ImageView adImage;
        TextView adData, adText;
        Button adClick;

        AdHolder(View itemView) {
            super(itemView);
            adImage = itemView.findViewById(R.id.appwidget_image);
            adData = itemView.findViewById(R.id.tvBonus);
            adText = itemView.findViewById(R.id.appwidget_text);
            adClick = itemView.findViewById(R.id.appAdClick);
            adClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adsCallback != null) {
                        adsCallback.onAdClick(v, getPosition());
                    }
                }
            });
        }
    }
}
