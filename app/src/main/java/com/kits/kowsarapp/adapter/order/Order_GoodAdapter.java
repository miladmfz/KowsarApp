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
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.order.Order_DBH;
import com.kits.kowsarapp.viewholder.order.Order_GoodItemViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;


public class Order_GoodAdapter extends RecyclerView.Adapter<Order_GoodItemViewHolder> {

    private final Context mContext;
    private final ArrayList<Good> goods;
    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    CallMethod callMethod;
    Order_DBH dbh;
    Order_APIInterface apiInterface;
    Order_Action action;
    public Call<RetrofitResponse> call;


    public Order_GoodAdapter(ArrayList<Good> goods, Context context) {
        this.mContext = context;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Order_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Order_Action(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);

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

        holder.tv_name.setText(callMethod.NumberRegion(goods.get(position).getGoodName()));
        holder.tv_price.setText(callMethod.NumberRegion(decimalFormat.format(Integer.parseInt(goods.get(position).getMaxSellPrice()))));
        holder.rltv.setOnClickListener(v -> action.GoodBoxDialog(goods.get(position), "0"));
        holder.callimage(goods.get(position));


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull Order_GoodItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.call.isExecuted()) {
            holder.call.cancel();
        }
    }

}
