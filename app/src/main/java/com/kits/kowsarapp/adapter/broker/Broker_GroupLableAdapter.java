package com.kits.kowsarapp.adapter.broker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.GoodGroup;
import com.kits.kowsarapp.viewholder.broker.Broker_GroupLabelViewHolder;


import java.util.ArrayList;

public class Broker_GroupLableAdapter extends RecyclerView.Adapter<Broker_GroupLabelViewHolder> {

    Context mContext;
    CallMethod callMethod;
    ArrayList<GoodGroup> GoodGroups;


    public Broker_GroupLableAdapter(ArrayList<GoodGroup> GoodGroups, Context mContext) {
        this.mContext = mContext;
        this.GoodGroups = GoodGroups;
        this.callMethod = new CallMethod(mContext);

    }

    @NonNull
    @Override
    public Broker_GroupLabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_grp_card, parent, false);
        return new Broker_GroupLabelViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Broker_GroupLabelViewHolder holder, int position) {

        holder.bind(GoodGroups.get(position), mContext);


    }

    @Override
    public int getItemCount() {
        return GoodGroups.size();
    }


}
