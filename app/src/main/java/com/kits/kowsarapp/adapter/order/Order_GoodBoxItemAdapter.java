package com.kits.kowsarapp.adapter.order;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.order.Order_Action;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.order.Order_DBH;
import com.kits.kowsarapp.viewholder.order.Order_GoodBoxItemViewHolder;

import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_GoodBoxItemAdapter extends RecyclerView.Adapter<Order_GoodBoxItemViewHolder> {
    Context mContext;
    CallMethod callMethod;
    ArrayList<Good> goods;
    Order_DBH dbh;

    Order_APIInterface apiInterface;
   Order_Action action;
    Call<RetrofitResponse> call;


    public Order_GoodBoxItemAdapter(ArrayList<Good> goods, Context context) {
        this.mContext = context;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Order_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Order_Action(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);

    }

    @NonNull
    @Override
    public Order_GoodBoxItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_goodorder_card, parent, false);
        if (callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return new Order_GoodBoxItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final Order_GoodBoxItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tv_name.setText(callMethod.NumberRegion(goods.get(position).getGoodName()));
        holder.tv_amount.setText(callMethod.NumberRegion(goods.get(position).getAmount()));
        holder.tv_explain.setText(callMethod.NumberRegion(goods.get(position).getExplain()));

        if (goods.get(position).getFactorCode() == null) {
            holder.img_dlt.setVisibility(View.VISIBLE);
            holder.img_dlt.setOnClickListener(v ->{

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
                builder.setTitle(R.string.textvalue_allert);
                builder.setMessage(R.string.textvalue_ifdelete);

                builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {


                    call = apiInterface.DeleteGoodFromBasket("DeleteGoodFromBasket", goods.get(position).getRowCode(), goods.get(position).getAppBasketInfoRef());
                    call.enqueue(new Callback<RetrofitResponse>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                if (response.body().getText().equals("Done")) {
                                    goods.remove(goods.get(position));
                                    notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                        }
                    });


                });

                builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                    // code to handle negative button click
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            });


        } else {
            holder.img_dlt.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
