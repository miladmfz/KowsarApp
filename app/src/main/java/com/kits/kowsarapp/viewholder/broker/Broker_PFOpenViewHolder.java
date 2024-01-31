package com.kits.kowsarapp.viewholder.broker;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.CallMethod;
import com.kits.kowsarapp.model.NumberFunctions;
import com.kits.kowsarapp.model.PreFactor;

import java.text.DecimalFormat;

;

public class Broker_PFOpenViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    public TextView fac_code;
    public TextView fac_date;
    public TextView fac_time;
    public TextView fac_detail;
    public TextView fac_row;
    public TextView fac_count;
    public TextView fac_price;
    public TextView fac_customer;

    public MaterialCardView fac_rltv;

    public Broker_PFOpenViewHolder(View itemView) {
        super(itemView);
        fac_code = itemView.findViewById(R.id.pf_header_box_code);
        fac_date = itemView.findViewById(R.id.pf_header_box_date);
        fac_time = itemView.findViewById(R.id.pf_header_box_time);
        fac_row = itemView.findViewById(R.id.pf_header_box_row);
        fac_count = itemView.findViewById(R.id.pf_header_box_count);
        fac_price = itemView.findViewById(R.id.pf_header_box_price);
        fac_detail = itemView.findViewById(R.id.pf_header_box_detail);
        fac_customer = itemView.findViewById(R.id.pf_header_box_customer);


        fac_rltv = itemView.findViewById(R.id.pf_header_box);

    }

    public void bind(PreFactor preFactor, Context mContext, CallMethod callMethod) {

        fac_date.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorDate"))));
        fac_time.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorTime"))));
        fac_code.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorCode"))));
        fac_detail.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorExplain"))));
        fac_customer.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("Customer"))));
        fac_row.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("RowCount"))));
        fac_count.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("SumAmount"))));
        fac_price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(String.valueOf(preFactor.getPreFactorFieldValue("SumPrice"))))));


        fac_rltv.setOnClickListener(v -> {

            final String prefactor_code = "PreFactorCode";
            callMethod.EditString(prefactor_code, preFactor.getPreFactorFieldValue("PreFactorCode"));
            callMethod.showToast("فاکتور مورد نظر انتخاب شد");
            ((Activity) mContext).overridePendingTransition(0, 0);
            ((Activity) mContext).finish();
            ((Activity) mContext).overridePendingTransition(0, 0);


        });

    }


}