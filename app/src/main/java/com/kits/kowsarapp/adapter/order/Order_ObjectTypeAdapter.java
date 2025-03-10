package com.kits.kowsarapp.adapter.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull Order_MizTypeViewHolder holder, int position) {


        if(objectTypes.get(position).getaType().equals("")){
            holder.tv_name.setText(R.string.textvalue_tagall);
        }else {
            holder.tv_name.setText(callMethod.NumberRegion(objectTypes.get(position).getaType()));
        }


        TypedValue typedValue = new TypedValue();
        Context context = holder.itemView.getContext();



        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue, true);
        int colorOnSecondary = typedValue.data;

        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
        int colorOnPrimary = typedValue.data;

        if (callMethod.ReadString("ObjectType").equals(objectTypes.get(position).getaType())) {
            holder.rltv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_primary));
            holder.tv_name.setTextColor(colorOnPrimary);
        } else {

            holder.rltv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_secondary));
            holder.tv_name.setTextColor(colorOnSecondary);
        }




        holder.rltv.setOnClickListener(v -> {
            Order_TableActivity activity = (Order_TableActivity) mContext;
            callMethod.EditString("ObjectType", objectTypes.get(position).getaType());


            notifyDataSetChanged();
            activity.State = "0";
            activity.CallSpinner();
        });

    }

    @Override
    public int getItemCount() {
        return objectTypes.size();
    }


}
