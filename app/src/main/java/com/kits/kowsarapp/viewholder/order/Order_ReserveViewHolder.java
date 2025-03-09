package com.kits.kowsarapp.viewholder.order;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;

import java.text.DecimalFormat;


public class Order_ReserveViewHolder extends RecyclerView.ViewHolder {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");

    public MaterialCardView rltv;
    public TextView tv_reservestart;
    public TextView tv_reserveend;
    public TextView tv_reservebrokername;
    public TextView tv_reservepersonname;
    public TextView tv_reserveeplain;
    public TextView tv_reservemobileno;
    public TextView tv_reservedate;

    public LinearLayout ll_table_reserve;


    public Order_ReserveViewHolder(View itemView) {
        super(itemView);

        rltv = itemView.findViewById(R.id.order_reserve_card);

        tv_reservestart = itemView.findViewById(R.id.ord_reserve_c_reservestart);
        tv_reserveend = itemView.findViewById(R.id.ord_reserve_c_reserveend);
        tv_reservebrokername = itemView.findViewById(R.id.ord_reserve_c_reservebrokername);
        tv_reservepersonname = itemView.findViewById(R.id.ord_reserve_c_reservepersonname);
        tv_reserveeplain = itemView.findViewById(R.id.ord_reserve_c_reserveeplain);
        tv_reservemobileno = itemView.findViewById(R.id.ord_reserve_c_reservemobileno);
        tv_reservedate = itemView.findViewById(R.id.ord_reserve_c_reservedate);

    }


}