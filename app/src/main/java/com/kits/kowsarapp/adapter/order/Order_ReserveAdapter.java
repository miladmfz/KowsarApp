package com.kits.kowsarapp.adapter.order;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.order.Order_SearchActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.order.Order_Action;
import com.kits.kowsarapp.model.order.Order_BasketInfo;
import com.kits.kowsarapp.viewholder.order.Order_ReserveViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import java.util.ArrayList;


public class Order_ReserveAdapter extends RecyclerView.Adapter<Order_ReserveViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    ArrayList<Order_BasketInfo> basketInfos;
    Intent intent;
   Order_Action order_action;


    public Order_ReserveAdapter(ArrayList<Order_BasketInfo> BasketInfos, Context context) {
        this.mContext = context;
        this.basketInfos = BasketInfos;
        this.callMethod = new CallMethod(mContext);
        this.order_action = new Order_Action(mContext);

    }

    @NonNull
    @Override
    public Order_ReserveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_reserve_card, parent, false);
        if (callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return new Order_ReserveViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Order_ReserveViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.tv_reservestart.setText(callMethod.NumberRegion(basketInfos.get(position).getReserveStart()));
        holder.tv_reserveend.setText(callMethod.NumberRegion(basketInfos.get(position).getReserveEnd()));
        holder.tv_reservebrokername.setText(callMethod.NumberRegion(basketInfos.get(position).getRes_BrokerName()));
        holder.tv_reservepersonname.setText(callMethod.NumberRegion(basketInfos.get(position).getPersonName()));
        holder.tv_reserveeplain.setText(callMethod.NumberRegion(basketInfos.get(position).getInfoExplain()));
        holder.tv_reservemobileno.setText(callMethod.NumberRegion(basketInfos.get(position).getMobileNo()));
        holder.tv_reservedate.setText(callMethod.NumberRegion(basketInfos.get(position).getAppBasketInfoDate()));


        holder.rltv.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage(R.string.textvalue_ordering);

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {

                callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName() + R.string.textvalue_tagreserve);
                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                intent = new Intent(mContext, Order_SearchActivity.class);
                mContext.startActivity(intent);

            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                // code to handle negative button click
            });

            AlertDialog dialog = builder.create();
            dialog.show();


        });

           


        holder.rltv.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage(R.string.textvalue_resdel);

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {

                order_action.DeleteReserveDialog(basketInfos.get(position));

            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                // code to handle negative button click
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        });

           


    }

    @Override
    public int getItemCount() {
        return basketInfos.size();
    }


}
