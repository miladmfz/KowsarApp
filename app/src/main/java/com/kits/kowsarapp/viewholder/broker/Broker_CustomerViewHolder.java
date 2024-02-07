package com.kits.kowsarapp.viewholder.broker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_ConfigActivity;
import com.kits.kowsarapp.activity.broker.Broker_PFActivity;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.model.base.Customer;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.NumberFunctions;

import java.text.DecimalFormat;

;

public class Broker_CustomerViewHolder extends RecyclerView.ViewHolder {

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    private final TextView cus_code;
    private final TextView cus_name;
    private final TextView cus_manage;
    private final TextView cus_phone;
    private final TextView cus_addres;
    private final TextView cus_bes;
    public LinearLayout cus_ll;
    private final MaterialCardView fac_rltv;

    public Broker_CustomerViewHolder(View itemView) {
        super(itemView);
        cus_code = itemView.findViewById(R.id.b_customer_c_code);
        cus_name = itemView.findViewById(R.id.b_customer_c_name);
        cus_manage = itemView.findViewById(R.id.b_customer_c_manage);
        cus_phone = itemView.findViewById(R.id.b_customer_c_phone);
        cus_addres = itemView.findViewById(R.id.b_customer_c_address);
        cus_bes = itemView.findViewById(R.id.b_customer_c_bes);
        cus_ll = itemView.findViewById(R.id.b_customer_c_credit_ll);
        fac_rltv = itemView.findViewById(R.id.broker_customer_card);
    }

    public void bind(Customer customer) {
        cus_code.setText(NumberFunctions.PerisanNumber(customer.getCustomerFieldValue("CustomerCode")));
        cus_name.setText(NumberFunctions.PerisanNumber(customer.getCustomerFieldValue("CustomerName")));
        cus_manage.setText(NumberFunctions.PerisanNumber(customer.getCustomerFieldValue("Manager")));


        if (customer.getCustomerFieldValue("Address").equals("null")) {
            cus_addres.setText("");
        } else {
            cus_addres.setText(NumberFunctions.PerisanNumber(customer.getCustomerFieldValue("Address")));
        }

        if (customer.getCustomerFieldValue("Phone").equals("null")) {
            cus_phone.setText("");
        } else {
            cus_phone.setText(NumberFunctions.PerisanNumber(customer.getCustomerFieldValue("Phone")));
        }


        if (Integer.parseInt(customer.getCustomerFieldValue("Bestankar")) > -1) {
            cus_bes.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(customer.getCustomerFieldValue("Bestankar")))));
            cus_bes.setTextColor(ContextCompat.getColor(App.getContext(), R.color.green_900));
        } else {
            int a = (Integer.parseInt(customer.getCustomerFieldValue("Bestankar"))) * (-1);
            cus_bes.setText(NumberFunctions.PerisanNumber(decimalFormat.format(a)));
            cus_bes.setTextColor(ContextCompat.getColor(App.getContext(), R.color.red_900));
        }


    }


    public void Action(Customer customer
            , Broker_DBH dbh
            , CallMethod callMethod
            , Broker_Action action
            , String edit
            , String factor_target
            , Context mContext) {


        fac_rltv.setOnClickListener(v -> {
            if (edit.equals("0")) {

                if (Integer.parseInt(dbh.ReadConfig("BrokerCode")) > 0) {
                    action.addfactordialog(customer.getCustomerFieldValue("CustomerCode"));
                } else {
                    Intent intent = new Intent(mContext, Broker_ConfigActivity.class);
                    callMethod.showToast("کد بازاریاب را وارد کنید");

                    mContext.startActivity(intent);
                }

            } else {
                dbh.UpdatePreFactorHeader_Customer(factor_target, customer.getCustomerFieldValue("CustomerCode"));
                Intent intent = new Intent(mContext, Broker_PFActivity.class);
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0, 0);
            }
        });


    }


}