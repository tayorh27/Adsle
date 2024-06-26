package com.ad.adsle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.ad.adsle.Callbacks.ClickListener;
import com.ad.adsle.Information.CampaignInformation;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.CamHolder> {

    Context context;
    Utils utils;
    ArrayList<CampaignInformation> campaignInformationArrayList = new ArrayList<>();
    LayoutInflater inflater;
    ClickListener clickListener;

    public CampaignAdapter(Context context, ClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        inflater = LayoutInflater.from(context);
        utils = new Utils(context);
    }

    public void updateLayout(ArrayList<CampaignInformation> campaignInformations) {
        this.campaignInformationArrayList = campaignInformations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CamHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_cams, parent, false);
        return new CamHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CamHolder holder, int position) {
        CampaignInformation current = campaignInformationArrayList.get(position);
        holder.cMenu1.setText(current.getReach_number() + "");
        holder.cMenu2.setText(current.getViews_number() + "");
        holder.cMenu3.setText(current.getClicks_number() + "");

        holder.cTitle.setText(current.getTitle());
        holder.cCreatedDate.setText(current.getCreated_date());

        Calendar cal_current = Calendar.getInstance();
        Date currentDate = cal_current.getTime();

        Calendar cal_start = Calendar.getInstance();
        String[] start = current.getCampaign_duration_start().split("-");
        cal_start.set(Calendar.YEAR, Integer.parseInt(start[2]));
        cal_start.set(Calendar.MONTH, Integer.parseInt(start[1]));
        cal_start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(start[0]));
        Date startDate = cal_start.getTime();

        long mDiff = currentDate.getTime() - startDate.getTime();
        int mDays = (int) (mDiff / (1000 * 60 * 60 * 24));

        if (mDays < 0) {
            holder.cDaysLeft.setText("Pending");
            return;
        }

        Calendar cal_end = Calendar.getInstance();
        String[] exp = current.getCampaign_duration_end().split("-");
        cal_end.set(Calendar.YEAR, Integer.parseInt(exp[2]));
        cal_end.set(Calendar.MONTH, Integer.parseInt(exp[1]));
        cal_end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exp[0]));

        Date endDate = cal_end.getTime();

        long diff = currentDate.getTime() - endDate.getTime();
        int days = (int) Math.abs((diff / (1000 * 60 * 60 * 24)));
        //Log.e("adapter", "onBindViewHolder: " + days);

        if (days > 0) {
            holder.cDaysLeft.setText("Expired");
        } else {
            holder.cDaysLeft.setText(days + " day(s) left");
        }
    }

    @Override
    public int getItemCount() {
        return campaignInformationArrayList.size();
    }

    class CamHolder extends RecyclerView.ViewHolder {

        TextView cTitle, cDaysLeft, cCreatedDate;
        AppCompatTextView cMenu1, cMenu2, cMenu3;
        Button next;

        CamHolder(View itemView) {
            super(itemView);

            cTitle = itemView.findViewById(R.id.cam_title);
            cDaysLeft = itemView.findViewById(R.id.days_left);
            cCreatedDate = itemView.findViewById(R.id.created_date);
            cMenu1 = itemView.findViewById(R.id.menu1);
            cMenu2 = itemView.findViewById(R.id.menu2);
            cMenu3 = itemView.findViewById(R.id.menu3);
            next = itemView.findViewById(R.id.btnNext);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onViewClick(v, getPosition());
                    }
                }
            });
        }
    }
}
