package com.kits.kowsarapp.adapter.broker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.model.Customer;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.viewholder.broker.Broker_CustomerViewHolder;

import java.util.ArrayList;

public class Broker_CustomerAdapter extends RecyclerView.Adapter<Broker_CustomerViewHolder> {
    Context mContext;
    CallMethod callMethod;
    ArrayList<Customer> customers;
    String edit;
    String factor_target;
    Broker_DBH dbh;
    Broker_Action action;


    public Broker_CustomerAdapter(ArrayList<Customer> customers, Context mContext, String edit, String factor_target) {
        this.mContext = mContext;
        this.customers = customers;
        this.edit = edit;
        this.factor_target = factor_target;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Broker_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Broker_Action(mContext);

    }

    @NonNull
    @Override
    public Broker_CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer, parent, false);
        return new Broker_CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Broker_CustomerViewHolder holder, int position) {


        holder.bind(customers.get(position));

        holder.Action(customers.get(position)
                , dbh
                , callMethod
                , action
                , edit
                , factor_target
                , mContext
        );

        if (callMethod.ReadBoolan("ShowCustomerCredit")){
            holder.cus_ll.setVisibility(View.VISIBLE);
        }else {
            holder.cus_ll.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return customers.size();
    }


}
