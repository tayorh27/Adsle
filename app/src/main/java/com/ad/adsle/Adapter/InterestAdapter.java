package com.ad.adsle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ad.adsle.Callbacks.InterestClicked;
import com.ad.adsle.Information.Interests;
import com.ad.adsle.R;

import java.util.ArrayList;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> interests = new ArrayList<>();
    InterestClicked interestClicked;

    public InterestAdapter(Context context, InterestClicked interestClicked) {
        this.context = context;
        this.interestClicked = interestClicked;
        inflater = LayoutInflater.from(context);
    }

    public void updateView(ArrayList<String> interests) {
        this.interests = interests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InterestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_interest, parent, false);
        return new InterestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestHolder holder, int position) {
        String current = interests.get(position);
        holder.button.setText(current);
        holder.button.setTag(current + "-0");
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    class InterestHolder extends RecyclerView.ViewHolder {

        Button button;

        InterestHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.btnInt);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (interestClicked != null) {
//                        Button selected_button = (Button) v;
//                        String tag = selected_button.getTag().toString();
//                        String text = selected_button.getText().toString();
//                        if (tag.contains("-0")) {
//                            selected_button.setTag(text + "-1");
//                            selected_button.setBackgroundResource(R.color.colorPrimaryDark);
//                            selected_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_black_24dp, 0);
//                        } else {
//                            selected_button.setTag(text + "-0");
//                            selected_button.setBackgroundResource(R.color.colorAccent);
//                            selected_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black_24dp, 0);
//                        }
//                        interestClicked.onIClicked(v, getPosition(), tag.contains(text + "-0"));
//                    }
//                }
//            });
        }
    }
}
