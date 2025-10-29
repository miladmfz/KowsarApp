package com.kits.kowsarapp.viewholder.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.NetworkUtils;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_GoodItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView img;
    public MaterialCardView rltv;
    public TextView tv_name;
    public TextView tv_price;
    public Call<RetrofitResponse> call;
    public Order_APIInterface order_apiInterface;
    Context mContex;
    CallMethod callMethod;
    boolean multi_select1;

    public Order_GoodItemViewHolder(View itemView, Context context) {
        super(itemView);
        this.mContex = context;
        this.callMethod = new CallMethod(context);
        this.order_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        img = itemView.findViewById(R.id.ord_gooditem_c_img);
        tv_name = itemView.findViewById(R.id.ord_gooditem_c_name);
        tv_price = itemView.findViewById(R.id.ord_gooditem_c_price);
        rltv = itemView.findViewById(R.id.order_gooditem_card);

    }


    public void callimage(Good good) {

        if (!good.getGoodImageName().equals("")) {


            Glide.with(img)
                    .asBitmap()
                    .load(Base64.decode(good.getGoodImageName(), Base64.DEFAULT))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(img);


        } else {

            call = order_apiInterface.GetImage(
                    "getImage",
                    String.valueOf(good.getGoodCode()),
                    "TGood",
                    "0",
                    "200"
            );
            call.enqueue(new Callback<RetrofitResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            good.setGoodImageName(response.body().getText());
                        } else {
                            good.setGoodImageName(String.valueOf(R.string.no_photo));

                        }
                        try {
                            Glide.with(img)
                                    .asBitmap()
                                    .load(Base64.decode(good.getGoodImageName(), Base64.DEFAULT))
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .fitCenter()
                                    .into(img);
                        }catch (Exception e){}

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    try {
                        // 🟢 بررسی وضعیت اتصال
                        if (!NetworkUtils.isNetworkAvailable(mContex)) {
                            callMethod.showToast("اتصال اینترنت قطع است!");
                        } else if (NetworkUtils.isVPNActive()) {
                            callMethod.showToast("VPN فعال است، ممکن است ارتباط با سرور مختل شود!");
                        } else {
                            String serverUrl = callMethod.ReadString("ServerURLUse");
                            if (serverUrl != null && !serverUrl.isEmpty() && !NetworkUtils.canReachServer(serverUrl)) {
                                callMethod.showToast("سرور در دسترس نیست یا فیلتر شده است!");
                            } else {
                                callMethod.showToast("مشکل در برقراری ارتباط با سرور برای بارگیری عکس");
                            }
                        }
                    } catch (Exception e) {
                        callMethod.Log("Network check error: " + e.getMessage());
                        callMethod.showToast("خطا در بررسی وضعیت شبکه");
                    }
                }
            });
        }

    }


}