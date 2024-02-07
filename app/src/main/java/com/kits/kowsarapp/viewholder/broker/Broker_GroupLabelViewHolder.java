package com.kits.kowsarapp.viewholder.broker;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_SearchActivity;
import com.kits.kowsarapp.model.base.GoodGroup;
import com.kits.kowsarapp.model.base.NumberFunctions;

public class Broker_GroupLabelViewHolder extends RecyclerView.ViewHolder {

    private final TextView grpname;
    private final ImageView img;
    private final FrameLayout rltv;

    public Broker_GroupLabelViewHolder(View itemView) {
        super(itemView);

        grpname = itemView.findViewById(R.id.b_grp_c_name);
        rltv = itemView.findViewById(R.id.broker_grp_card);
        img = itemView.findViewById(R.id.b_grp_c_image);
    }


    public void bind(GoodGroup goodGroup, Context mContext) {
        grpname.setText(NumberFunctions.PerisanNumber(goodGroup.getGoodGroupFieldValue("Name")));


        if (Integer.parseInt(goodGroup.getGoodGroupFieldValue("ChildNo")) > 0) {
            img.setVisibility(View.VISIBLE);
        } else
            img.setVisibility(View.GONE);


        grpname.setOnClickListener(v -> {

            Intent intent = new Intent(mContext, Broker_SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", goodGroup.getGoodGroupFieldValue("GroupCode"));
            intent.putExtra("title", goodGroup.getGoodGroupFieldValue("Name"));
            mContext.startActivity(intent);
        });
    }


}