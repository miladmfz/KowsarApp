package com.kits.kowsarapp.adapter.search;


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
import com.kits.kowsarapp.model.search.Search_DBH;
import com.kits.kowsarapp.model.search.Search_Good;
import com.kits.kowsarapp.viewholder.search.Search_GoodItemViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.search.Search_APIInterface;

import java.util.ArrayList;


public class Search_GoodAdapter extends RecyclerView.Adapter<Search_GoodItemViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Search_Good> goods;
    Search_DBH dbh;

    Search_APIInterface apiInterface;
    public boolean multi_select;
    ArrayList<Column> Columns;


    public Search_GoodAdapter(ArrayList<Search_Good> goods, Context context) {
        this.mContext = context;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Search_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.Columns = dbh.GetColumns("id", "", "1");
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Search_APIInterface.class);
        Log.e("kowsar colimn size " , Columns.size()+"");
        Log.e("kowsar goods size " , goods.size()+"");
    }

    @NonNull
    @Override
    public Search_GoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.search_good_item_card, parent, false);


        return new Search_GoodItemViewHolder(view, mContext);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Search_GoodItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.bind(Columns, goods.get(position), mContext, callMethod);


        holder.callimage(goods.get(position));


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull Search_GoodItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.call.isExecuted()) {
            holder.call.cancel();

        }
    }
}
