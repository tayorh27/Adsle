package com.ad.adsle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ad.adsle.Information.Transactions;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;

import java.util.ArrayList;

public class TransAdapter extends RecyclerView.Adapter<TransAdapter.TransHolder> {

    Context context;
    Utils utils;
    ArrayList<Transactions> transactionsArrayList = new ArrayList<>();
    LayoutInflater inflater;

    public TransAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        utils = new Utils(context);
    }

    public void updateLayout(ArrayList<Transactions> transactions) {
        this.transactionsArrayList = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_trans, parent, false);
        return new TransHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransHolder holder, int position) {
        Transactions transactions = transactionsArrayList.get(position);
        holder.da.setText("₦" + transactions.getAmount());
        holder.dRef.setText("RefID: " + transactions.getReference());
        holder.dNumber.setText(transactions.getDate());
    }

    @Override
    public int getItemCount() {
        return transactionsArrayList.size();
    }


    class TransHolder extends RecyclerView.ViewHolder {

        TextView da, dRef, dNumber;

        TransHolder(View itemView) {
            super(itemView);

            da = itemView.findViewById(R.id.data_amount);
            dRef = itemView.findViewById(R.id.reference);
            dNumber = itemView.findViewById(R.id.number);
        }
    }
}
