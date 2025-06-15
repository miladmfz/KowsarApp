package com.kits.kowsarapp.adapter.broker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.viewholder.broker.Broker_BasketItemHistoryViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;

import java.util.ArrayList;

public class Broker_BasketItemHistoryAdapter extends RecyclerView.Adapter<Broker_BasketItemHistoryViewHolder> {
    ArrayList<Good> goods;
    Context mContext;
    CallMethod callMethod;
    String itemposition;
    Broker_APIInterface apiInterface;
    ImageInfo image_info;
    Broker_Action action;

    public Broker_BasketItemHistoryAdapter(ArrayList<Good> goods, String Itemposition, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.itemposition = Itemposition;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);
        action = new Broker_Action(mContext);

    }

    @NonNull
    @Override
    public Broker_BasketItemHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (itemposition.equals("0")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_baskethistory_line_card, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_baskethistory_grid_card, parent, false);

        }
        return new Broker_BasketItemHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Broker_BasketItemHistoryViewHolder holder, int position) {

        holder.bind(goods.get(position), itemposition);
        holder.Conditionbind(goods.get(position), image_info, callMethod);

    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    public void updateList(ArrayList<Good> newGoods, String newItemPosition) {
        this.goods = newGoods;
        notifyDataSetChanged();
    }

}
