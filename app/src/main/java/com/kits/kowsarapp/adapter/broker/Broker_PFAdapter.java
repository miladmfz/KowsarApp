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
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.PreFactor;
import com.kits.kowsarapp.viewholder.broker.Broker_PFViewHolder;

import java.util.ArrayList;


public class Broker_PFAdapter extends RecyclerView.Adapter<Broker_PFViewHolder> {

    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<PreFactor> PreFactors;
    private final Broker_DBH dbh;
    private final Broker_Action action;


    public Broker_PFAdapter(ArrayList<PreFactor> PreFactors, Context mContext) {
        this.mContext = mContext;
        this.PreFactors = PreFactors;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Broker_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Broker_Action(mContext);

    }

    @NonNull
    @Override
    public Broker_PFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_pfitem_card, parent, false);
        return new Broker_PFViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Broker_PFViewHolder holder, final int position) {


        holder.bind(PreFactors.get(position));
        holder.Action(PreFactors.get(position), mContext, dbh, callMethod, action);


    }

    @Override
    public int getItemCount() {
        return PreFactors.size();
    }


}
