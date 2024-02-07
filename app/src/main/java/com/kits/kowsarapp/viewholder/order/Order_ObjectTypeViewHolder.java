package com.kits.kowsarapp.viewholder.order;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;

import java.text.DecimalFormat;


public class Order_ObjectTypeViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");


    public CardView rltv;
    public TextView tv_name;


    public Order_ObjectTypeViewHolder(View itemView) {
        super(itemView);
        tv_name = itemView.findViewById(R.id.objecttype_item_itype);
        rltv = itemView.findViewById(R.id.objecttype_item);
    }


}