package com.ad.adsle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ad.adsle.Callbacks.ClickListener;
import com.ad.adsle.Information.Plans;
import com.ad.adsle.R;

import java.util.ArrayList;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.PlanHolder> {
    Context context;
    LayoutInflater inflater;
    ArrayList<Plans> plans = new ArrayList<>();
    ClickListener clickListener;

    public PlansAdapter(Context context, ClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        inflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<Plans> plans) {
        this.plans = plans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_plan, parent, false);
        return new PlanHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanHolder holder, int position) {
        Plans plan = plans.get(position);
        holder.tvPrice.setText(plan.getPrice());
        holder.tvTitle.setText(plan.getTitle());
        holder.tvDescription.setText(plan.getDescription());
        holder.tvValidity.setText("Validity: " + plan.getValidity());
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    class PlanHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        TextView tvPrice, tvTitle, tvDescription, tvValidity;

        PlanHolder(View itemView) {
            super(itemView);

            tvPrice = itemView.findViewById(R.id.plan_price);
            tvTitle = itemView.findViewById(R.id.plan_title);
            tvDescription = itemView.findViewById(R.id.plan_description);
            tvValidity = itemView.findViewById(R.id.plan_validity);
            relativeLayout = itemView.findViewById(R.id.main);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
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
