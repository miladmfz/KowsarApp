package com.kits.kowsarapp.adapter.broker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.viewholder.broker.Broker_BasketItemViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;


public class Broker_BasketItemAdapter extends RecyclerView.Adapter<Broker_BasketItemViewHolder> {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final Broker_APIInterface broker_apiInterface;
    private final ImageInfo image_info;
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Good> goods;
    private final Broker_DBH dbh;
    Intent intent;
    Broker_Action action;

    public Broker_BasketItemAdapter(ArrayList<Good> goods, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.dbh = new Broker_DBH(mContext, callMethod.ReadString("DatabaseName"));
        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);
        action = new Broker_Action(mContext);
    }

    @NonNull
    @Override
    public Broker_BasketItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_basket_card, parent, false);
        return new Broker_BasketItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Broker_BasketItemViewHolder holder, @SuppressLint("RecyclerView") int position) {

callMethod.Log(goods.get(position).getGoodFieldValue("Shortage"));
        holder.bind(goods.get(position));
        holder.Action(goods.get(position), mContext, dbh, callMethod, action, image_info);


        if (!image_info.Image_exist(goods.get(position).getGoodFieldValue("KsrImageCode"))) {
            Call<RetrofitResponse> call2 = broker_apiInterface.GetImageFromKsr("GetImageFromKsr",
                    goods.get(position).getGoodFieldValue("KsrImageCode")
            );
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull retrofit2.Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            image_info.SaveImage(
                                    BitmapFactory.decodeByteArray(
                                            Base64.decode(response.body().getText(), Base64.DEFAULT),
                                            0,
                                            Base64.decode(response.body().getText(), Base64.DEFAULT).length
                                    ),
                                    goods.get(position).getGoodFieldValue("KsrImageCode")
                            );
                            notifyItemChanged(position);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    callMethod.Log(t.getMessage());

                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
