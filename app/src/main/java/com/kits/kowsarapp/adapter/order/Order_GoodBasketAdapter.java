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
import com.kits.kowsarapp.activity.order.Order_BasketActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.order.Order_Action;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.viewholder.order.Order_GoodBasketViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_GoodBasketAdapter extends RecyclerView.Adapter<Order_GoodBasketViewHolder> {
    private final Order_APIInterface apiInterface;
    private final Context mContext;
    private final ArrayList<Good> goods;
    CallMethod callMethod;
   Order_Action action;
    Call<RetrofitResponse> call;

    public Order_GoodBasketAdapter(ArrayList<Good> goods, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        action = new Order_Action(mContext);
    }

    @NonNull
    @Override
    public Order_GoodBasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_basketitem_card, parent, false);
        if (callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return new Order_GoodBasketViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Order_GoodBasketViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.tv_goodname.setText(callMethod.NumberRegion(goods.get(position).getGoodName()));
        holder.tv_amount.setText(goods.get(position).getAmount());
        holder.tv_explain.setText(callMethod.NumberRegion(goods.get(position).getExplain()));

        if (goods.get(position).getExplain().length() > 0) {
            holder.ll_explain.setVisibility(View.VISIBLE);
        } else {
            holder.ll_explain.setVisibility(View.INVISIBLE);
        }
        holder.ll_amount.setOnClickListener(v -> action.GoodBoxDialog(goods.get(position), "1"));
        holder.tv_explain.setOnClickListener(v -> action.GoodBoxDialog(goods.get(position), "1"));
        holder.tv_goodname.setOnClickListener(v -> action.GoodBoxDialog(goods.get(position), "1"));


        if (goods.get(position).getFactorCode().equals("0")) {
            holder.btn_dlt.setVisibility(View.VISIBLE);
        } else if (Integer.parseInt(goods.get(position).getFactorCode()) > 0) {
            holder.btn_dlt.setVisibility(View.INVISIBLE);
        }


        holder.btn_dlt.setOnClickListener(v ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage(R.string.textvalue_ifdelete);

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                call = apiInterface.DeleteGoodFromBasket(
                        "DeleteGoodFromBasket",
                        goods.get(position).getRowCode(),
                        goods.get(position).getAppBasketInfoRef()
                );
                call.enqueue(new Callback<RetrofitResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (response.body().getText().equals("Done")) {
                                goods.remove(goods.get(position));
                                notifyDataSetChanged();
                                Order_BasketActivity activity = (Order_BasketActivity) mContext;
                                activity.RefreshState();
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


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
