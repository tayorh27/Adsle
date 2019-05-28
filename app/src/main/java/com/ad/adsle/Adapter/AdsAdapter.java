package com.ad.adsle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.Information.Settings;
import com.ad.adsle.MyApplication;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AdsAdapter extends BaseAdapter {

    Context context;
    Utils utils;
    AppData data;
    Settings settings;
    ArrayList<CampaignInformation> campaignInformation = new ArrayList<>();

    public AdsAdapter(Context context) {
        this.context = context;
        utils = new Utils(context);
        data = new AppData(context);
        settings = data.getSettings();
    }

    public void setList(ArrayList<CampaignInformation> campaignInformation) {
        this.campaignInformation = campaignInformation;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return campaignInformation.size();
    }

    @Override
    public Object getItem(int position) {
        return campaignInformation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.custom_ad_display, parent, false);
        }
        CampaignInformation current = campaignInformation.get(position);
        //TextView textView = convertView.findViewById(R.id.ad_text);
        ImageView imageView = convertView.findViewById(R.id.ad_image);

//        String link_option = current.getCampaign_link_option();
//        if (link_option.contentEquals("App Install")) {
//            textView.setText("Install app and get " + utils.getExactDataValue(String.valueOf(settings.getApp_install_data())) + " free.");
//        } else if (link_option.contentEquals("Click")) {
//            textView.setText("Click this ad to get " + utils.getExactDataValue(String.valueOf(settings.getClick_data())) + " free.");
//        } else {
//            textView.setText("Powered by Adsle");
//        }
        if (current.getCampaign_image().toLowerCase().contains(".gif")) {
            try {
                Glide.with(context) //GifDrawable drawable =
                        .asGif()
                        .load(current.getCampaign_image()).into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Glide.with(context)//Bitmap bitmap =
                        .asBitmap()
                        .load(current.getCampaign_image()).into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link_option = current.getCampaign_link_option();
                if (link_option.contentEquals("App Install") || link_option.contentEquals("Click")) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(current.getCampaign_link())));
                }
            }
        });

        return convertView;
    }
}
