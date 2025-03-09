package com.kits.kowsarapp.adapter.base;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.PRDownloader;
import com.downloader.Status;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Activation;
import com.kits.kowsarapp.viewholder.base.Base_AllAppViewHolder;

import java.util.ArrayList;

public class Base_AllAppAdapter extends RecyclerView.Adapter<Base_AllAppViewHolder> {

    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Activation> activations;



    public Base_AllAppAdapter(ArrayList<Activation> activations_input, Context context) {
        this.mContext = context;
        this.activations = activations_input;
        this.callMethod = new CallMethod(mContext);
    }

    @NonNull
    @Override
    public Base_AllAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.default_allapp_card, parent, false);
        return new Base_AllAppViewHolder(view, mContext);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Base_AllAppViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.bind(activations.get(position),mContext,callMethod);
        holder.Actionbtn(activations.get(position),mContext,callMethod);
    }

    @Override
    public int getItemCount() {
        return activations.size();
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull Base_AllAppViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (PRDownloader.getStatus(holder.downloadId) == Status.RUNNING) {
            PRDownloader.cancel(holder.downloadId);

        }
    }




}
