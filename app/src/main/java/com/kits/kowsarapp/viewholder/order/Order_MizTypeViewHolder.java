package com.kits.kowsarapp.viewholder.order;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;

import java.text.DecimalFormat;


public class Order_MizTypeViewHolder extends RecyclerView.ViewHolder {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");


    public CardView rltv;
    public TextView tv_name;


    public Order_MizTypeViewHolder(View itemView) {
        super(itemView);
        tv_name = itemView.findViewById(R.id.ord_miztype_c_itype);
        rltv = itemView.findViewById(R.id.order_miztype_card);
    }


}