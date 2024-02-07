package com.kits.kowsarapp.viewholder.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;

public class Order_GoodBasketViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout ll_explain;
    public LinearLayout ll_amount;
    public TextView tv_goodname;
    public TextView tv_amount;
    public TextView tv_explain;
    public ImageView btn_dlt;


    public Order_GoodBasketViewHolder(View itemView) {
        super(itemView);

        ll_explain = itemView.findViewById(R.id.good_basket_item_ll_explain);
        ll_amount = itemView.findViewById(R.id.good_basket_item_ll_amount);
        tv_goodname = itemView.findViewById(R.id.good_basket_item_goodname);
        tv_amount = itemView.findViewById(R.id.good_basket_item_amount);
        tv_explain = itemView.findViewById(R.id.good_basket_item_explain);
        btn_dlt = itemView.findViewById(R.id.good_basket_item_dlt);
    }


}