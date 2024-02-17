package com.kits.kowsarapp.adapter.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.order.Order_TableActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.ObjectType;
import com.kits.kowsarapp.viewholder.order.Order_MizTypeViewHolder;

import java.util.ArrayList;

public class Order_ObjectTypeAdapter extends RecyclerView.Adapter<Order_MizTypeViewHolder> {

    Context mContext;
    CallMethod callMethod;
    ArrayList<ObjectType> objectTypes;


    public Order_ObjectTypeAdapter(ArrayList<ObjectType> objectTypes, Context mContext) {
        this.mContext = mContext;
        this.objectTypes = objectTypes;
        this.callMethod = new CallMethod(mContext);

    }

    @NonNull
    @Override
    public Order_MizTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_miztype_card, parent, false);
        if (callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return new Order_MizTypeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Order_MizTypeViewHolder holder, int position) {


        if(objectTypes.get(position).getaType().equals("")){
            holder.tv_name.setText(R.string.textvalue_tagall);
        }else {
            holder.tv_name.setText(callMethod.NumberRegion(objectTypes.get(position).getaType()));
        }
        holder.rltv.setOnClickListener(v -> {
            Order_TableActivity activity = (Order_TableActivity) mContext;
            callMethod.EditString("ObjectType", objectTypes.get(position).getaType());

            activity.State = "0";
            activity.CallSpinner();
        });

    }

    @Override
    public int getItemCount() {
        return objectTypes.size();
    }


}
