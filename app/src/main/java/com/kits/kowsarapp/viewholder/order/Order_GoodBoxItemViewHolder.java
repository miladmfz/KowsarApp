package com.kits.kowsarapp.viewholder.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;



public class Order_GoodBoxItemViewHolder extends RecyclerView.ViewHolder {


    public TextView tv_name;
    public TextView tv_amount;
    public TextView tv_explain;
    public ImageView img_dlt;

    public Order_GoodBoxItemViewHolder(View itemView) {
        super(itemView);

        tv_name = itemView.findViewById(R.id.ord_goodorder_c_goodname);
        tv_amount = itemView.findViewById(R.id.ord_goodorder_c_amount);
        tv_explain = itemView.findViewById(R.id.ord_goodorder_c_explain);
        img_dlt = itemView.findViewById(R.id.ord_goodorder_c_delete);
    }


}