package com.kits.kowsarapp.viewholder.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;

import java.text.DecimalFormat;


public class Order_GoodBoxItemViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");


    public TextView tv_name;
    public TextView tv_amount;
    public TextView tv_explain;
    public ImageView img_dlt;

    public Order_GoodBoxItemViewHolder(View itemView) {
        super(itemView);

        tv_name = itemView.findViewById(R.id.good_box_item_goodname);
        tv_amount = itemView.findViewById(R.id.good_box_item_amount);
        tv_explain = itemView.findViewById(R.id.good_box_item_explain);
        img_dlt = itemView.findViewById(R.id.good_box_item_delete);
    }


}