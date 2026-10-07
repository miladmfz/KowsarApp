package com.kits.kowsarapp.adapter.order;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.order.Order_Action;
import com.kits.kowsarapp.application.order.Order_ValueParser;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.viewholder.order.Order_GoodItemViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;


public class Order_GoodAdapter extends RecyclerView.Adapter<Order_GoodItemViewHolder> {

    private final Context mContext;
    private final ArrayList<Good> goods;
    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    CallMethod callMethod;

    Order_Action order_action;
    public Call<RetrofitResponse> call;


    public Order_GoodAdapter(ArrayList<Good> goods, Context context) {
        this.mContext = context;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.order_action = new Order_Action(mContext);

    }

    @NonNull
    @Override
    public Order_GoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_good_card, parent, false);
        if (callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return new Order_GoodItemViewHolder(view, mContext);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Order_GoodItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Good good = goods.get(position);
        holder.tv_name.setText(callMethod.NumberRegion(good.getGoodName()));
        long price = Order_ValueParser.nonNegativeLongOrDefault(good.getMaxSellPrice(), 0);
        holder.tv_price.setText(callMethod.NumberRegion(decimalFormat.format(price)));
        holder.rltv.setOnClickListener(v -> order_action.GoodBoxDialog(good, "0"));
        holder.callimage(good);


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull Order_GoodItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.call != null && holder.call.isExecuted()) {
            holder.call.cancel();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        order_action.cancelPending();
        super.onDetachedFromRecyclerView(recyclerView);
    }

}
