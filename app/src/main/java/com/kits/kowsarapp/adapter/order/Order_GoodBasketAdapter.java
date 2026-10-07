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
import com.kits.kowsarapp.application.order.Order_NetworkFailure;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.viewholder.order.Order_GoodBasketViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_GoodBasketAdapter extends RecyclerView.Adapter<Order_GoodBasketViewHolder> {
    private final Order_APIInterface order_apiInterface;
    private final Context mContext;
    private final ArrayList<Good> goods;
    CallMethod callMethod;
    Order_Action order_action;
    Call<RetrofitResponse> call;
    private final Set<String> deletingRows = new HashSet<>();

    public Order_GoodBasketAdapter(ArrayList<Good> goods, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        order_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        order_action = new Order_Action(mContext);
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

        Good good = goods.get(position);

        holder.tv_goodname.setText(callMethod.NumberRegion(good.getGoodName()));
        holder.tv_amount.setText(good.getAmount());
        holder.tv_explain.setText(callMethod.NumberRegion(good.getExplain()));

        if (!safe(good.getExplain()).isEmpty()) {
            holder.ll_explain.setVisibility(View.VISIBLE);
        } else {
            holder.ll_explain.setVisibility(View.INVISIBLE);
        }
        holder.ll_amount.setOnClickListener(v -> order_action.GoodBoxDialog(good, "1"));
        holder.tv_explain.setOnClickListener(v -> order_action.GoodBoxDialog(good, "1"));
        holder.tv_goodname.setOnClickListener(v -> order_action.GoodBoxDialog(good, "1"));


        // Pending rows are deleted normally. Printed rows are also allowed to
        // request a cancellation, but through audited OrderAdjustmentInsert so
        // the original kitchen/financial row is never deleted or overwritten.
        holder.btn_dlt.setVisibility(View.VISIBLE);


        holder.btn_dlt.setOnClickListener(v ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage(R.string.textvalue_ifdelete);

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                if (!"0".equals(good.getFactorCode())) {
                    order_action.CancelPrintedGood(good);
                    return;
                }

                final String deleteKey = safe(good.getAppBasketInfoRef())
                        + ":" + safe(good.getRowCode());
                if (!deletingRows.add(deleteKey)) {
                    callMethod.showToast("حذف این ردیف در حال انجام است...");
                    return;
                }
                holder.btn_dlt.setEnabled(false);

                try {
                    call = order_apiInterface.DeleteGoodFromBasket(
                            "DeleteGoodFromBasket",
                            good.getRowCode(),
                            good.getAppBasketInfoRef()
                    );
                    call.enqueue(new Callback<RetrofitResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && "Done".equals(response.body().getText())) {
                            deletingRows.remove(deleteKey);
                            goods.remove(good);
                            notifyDataSetChanged();
                            if (mContext instanceof Order_BasketActivity) {
                                ((Order_BasketActivity) mContext).RefreshState();
                            }
                        } else {
                            finishDelete(deleteKey, holder);
                            callMethod.showToast("پاسخ حذف ردیف نامعتبر بود.");
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                        finishDelete(deleteKey, holder);
                        Order_NetworkFailure.show(mContext, callMethod,
                                "DeleteGoodFromBasket", call, t);
                    }
                    });
                } catch (RuntimeException exception) {
                    finishDelete(deleteKey, holder);
                    callMethod.Log("DeleteGoodFromBasket enqueue failed: "
                            + (exception.getMessage() == null ? "" : exception.getMessage()));
                    callMethod.showToast("خطا در شروع حذف ردیف سفارش");
                }
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

    private void finishDelete(String deleteKey, Order_GoodBasketViewHolder holder) {
        deletingRows.remove(deleteKey);
        holder.btn_dlt.setEnabled(true);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        if (call != null) call.cancel();
        order_action.cancelPending();
        deletingRows.clear();
        super.onDetachedFromRecyclerView(recyclerView);
    }


}
