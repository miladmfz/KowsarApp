package com.kits.kowsarapp.adapter.find;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.find.Find_DBH;
import com.kits.kowsarapp.model.find.Find_Good;
import com.kits.kowsarapp.viewholder.find.Find_GoodItemViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.find.Find_APIInterface;

import java.util.ArrayList;


public class Find_GoodAdapter extends RecyclerView.Adapter<Find_GoodItemViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Find_Good> goods;
    Find_DBH dbh;

    Find_APIInterface apiInterface;
    public boolean multi_select;
    ArrayList<Column> Columns;


    public Find_GoodAdapter(ArrayList<Find_Good> goods, Context context) {
        this.mContext = context;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Find_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.Columns = dbh.GetColumns("id", "", "1");
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Find_APIInterface.class);
        Log.e("kowsar colimn size " , Columns.size()+"");
        Log.e("kowsar goods size " , goods.size()+"");
    }

    @NonNull
    @Override
    public Find_GoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.find_good_item_card, parent, false);


        return new Find_GoodItemViewHolder(view, mContext);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Find_GoodItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.bind(Columns, goods.get(position), mContext, callMethod);


        holder.callimage(goods.get(position));


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull Find_GoodItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.call.isExecuted()) {
            holder.call.cancel();

        }
    }
}
