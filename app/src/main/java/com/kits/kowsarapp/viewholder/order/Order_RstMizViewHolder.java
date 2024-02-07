package com.kits.kowsarapp.viewholder.order;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;


public class Order_RstMizViewHolder extends RecyclerView.ViewHolder {

    public MaterialCardView rltv;
    public TextView tv_name;
    public TextView tv_placecount;
    public TextView tv_mizexplain;
    public TextView tv_infoexplain;
    public TextView tv_time;
    public TextView tv_brokername;

    public TextView tv_reservestart;
    public TextView tv_reservebrokername;
    public TextView tv_reservemobileno;

    public Button btn_select;
    public Button btn_reserve;
    public Button btn_print;
    public Button btn_changemiz;
    public Button btn_explainedit;
    public Button btn_cleartable;

    public LinearLayout ll_table_infoexplain;
    public LinearLayout ll_table_print_change;
    public LinearLayout ll_table_mizexplain;
    public LinearLayout ll_table_timebroker;
    public LinearLayout ll_table_reserve;


    public Order_RstMizViewHolder(View itemView) {
        super(itemView);

        rltv = itemView.findViewById(R.id.table_item);

        tv_name = itemView.findViewById(R.id.table_item_name);
        tv_placecount = itemView.findViewById(R.id.table_item_placecount);
        tv_mizexplain = itemView.findViewById(R.id.table_item_mizexplain);
        tv_infoexplain = itemView.findViewById(R.id.table_item_infoexplain);
        tv_time = itemView.findViewById(R.id.table_item_time);
        tv_brokername = itemView.findViewById(R.id.table_item_brokername);
        tv_reservestart = itemView.findViewById(R.id.table_item_reservestart);
        tv_reservebrokername = itemView.findViewById(R.id.table_item_reservebrokername);
        tv_reservemobileno = itemView.findViewById(R.id.table_item_reservemobileno);
        btn_select = itemView.findViewById(R.id.table_item_select);
        btn_reserve = itemView.findViewById(R.id.table_item_reserve);
        btn_print = itemView.findViewById(R.id.table_item_print);
        btn_changemiz = itemView.findViewById(R.id.table_item_changemiz);
        btn_explainedit = itemView.findViewById(R.id.table_item_explainedit);
        btn_cleartable = itemView.findViewById(R.id.table_item_cleartable);
        ll_table_infoexplain = itemView.findViewById(R.id.table_item_ll_infoexplain);
        ll_table_print_change = itemView.findViewById(R.id.table_item_ll_print_change);
        ll_table_mizexplain = itemView.findViewById(R.id.table_item_ll_mizexplain);
        ll_table_timebroker = itemView.findViewById(R.id.table_item_ll_timebroker);
        ll_table_reserve = itemView.findViewById(R.id.table_item_ll_reserve);
    }


}