package com.kits.kowsarapp.adapter.broker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.PreFactor;
import com.kits.kowsarapp.viewholder.broker.Broker_PFOpenViewHolder;

import java.util.ArrayList;

public class Broker_PFOpenAdapter extends RecyclerView.Adapter<Broker_PFOpenViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<PreFactor> PreFactors;


    public Broker_PFOpenAdapter(ArrayList<PreFactor> PreFactors, Context mContext) {
        this.mContext = mContext;
        this.PreFactors = PreFactors;
        this.callMethod = new CallMethod(mContext);
    }

    @NonNull
    @Override
    public Broker_PFOpenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_pfopenitem_card, parent, false);
        return new Broker_PFOpenViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Broker_PFOpenViewHolder holder, final int position) {


        holder.bind(PreFactors.get(position), mContext, callMethod);

    }

    @Override
    public int getItemCount() {
        return PreFactors.size();
    }


}
