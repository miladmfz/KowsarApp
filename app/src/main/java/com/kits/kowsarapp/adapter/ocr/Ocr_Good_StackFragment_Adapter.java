package com.kits.kowsarapp.adapter.ocr;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.application.ocr.OcrImagePipeline;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Ocr_Good_StackFragment_Adapter extends RecyclerView.Adapter<Ocr_Good_StackFragment_Adapter.GoodViewHolder>{

    private List<Ocr_Good> ocr_goods;
    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;

    Ocr_Action ocr_action;
    CallMethod callMethod;



    public Ocr_Good_StackFragment_Adapter(List<Ocr_Good> ocr_goods, Context context)
    {
        this.ocr_goods = ocr_goods;
        this.ocr_action = new Ocr_Action(context);
        this.callMethod = new CallMethod(context);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        this.secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);


    }
    @NonNull
    @Override
    public GoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ocr_stacklocation_item, parent, false);
        return new GoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodViewHolder holder, @SuppressLint("RecyclerView") int position)
    {
        Ocr_Good good = ocr_goods.get(position);
        holder.recycleImageRequest();
        holder.boundGoodCode = good.getGoodCode();
        holder.img.setImageResource(R.drawable.img_base_no_photo);
        String goodName = good.getGoodName();

        if (goodName != null && goodName.length() > 50) {
            goodName = goodName.substring(0, 50) + "...";
        }

        holder.goodnameTextView.setText(
                NumberFunctions.PerisanNumber(goodName)
        );
        holder.sellprice_tv.setText(NumberFunctions.PerisanNumber(good.getMaxSellPrice()));
        holder.amount_tv.setText(NumberFunctions.PerisanNumber(good.getStackAmount()));
        holder.stacklocation_tv.setText(NumberFunctions.PerisanNumber(good.getStackLocation()));

        Bitmap cachedBitmap = OcrImagePipeline.decodeBase64(good.getGoodImageName());
        if (cachedBitmap != null) {
            holder.img.setImageBitmap(cachedBitmap);
        } else
        {
            Call<RetrofitResponse> imageCall = apiInterface.GetImage(
                    "getImage", good.getGoodCode(), 0, 400
            );
            holder.imageCall = imageCall;
            String requestedGoodCode = good.getGoodCode();
            imageCall.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                    if (call2 != holder.imageCall
                            || !Objects.equals(requestedGoodCode, holder.boundGoodCode)
                            || !response.isSuccessful()
                            || response.body() == null) {
                        return;
                    }
                    String payload = response.body().getText();
                    if (payload == null || payload.trim().isEmpty() || "no_photo".equals(payload)) {
                        return;
                    }
                    Bitmap bitmap = OcrImagePipeline.decodeBase64(payload);
                    if (bitmap != null) {
                        good.setGoodImageName(payload);
                        holder.img.setImageBitmap(bitmap);
                    } else {
                        callMethod.Log("OCR stack image payload was invalid");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    if (!call2.isCanceled()
                            && call2 == holder.imageCall
                            && Objects.equals(requestedGoodCode, holder.boundGoodCode)) {
                        callMethod.Log("OCR stack image failed: " + t.getClass().getSimpleName());
                    }

                }
            });
        }







        holder.btnadd.setOnClickListener(view -> {
            ocr_action.GoodStackLocation(good);


        });

    }

    @Override
    public int getItemCount()
    {
        return ocr_goods.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull GoodViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull GoodViewHolder holder) {
        holder.recycleImageRequest();
        super.onViewRecycled(holder);
    }


    class GoodViewHolder extends RecyclerView.ViewHolder
    {
        private TextView goodnameTextView;
        private TextView sellprice_tv;
        private TextView amount_tv;
        private TextView stacklocation_tv;
        private TextView totalstate;
        private Button btnadd;
        private ImageView img ;
        private LinearLayoutCompat ggg ;
        MaterialCardView rltv;
        private Call<RetrofitResponse> imageCall;
        private String boundGoodCode;

        GoodViewHolder(View itemView)
        {
            super(itemView);
            goodnameTextView = itemView.findViewById(R.id.ocr_stacklocation_c_name);

            sellprice_tv = itemView.findViewById(R.id.ocr_stacklocation_c_sellprice);
            amount_tv = itemView.findViewById(R.id.ocr_stacklocation_c_amount);
            stacklocation_tv = itemView.findViewById(R.id.ocr_stacklocation_c_stacklocation);

            totalstate = itemView.findViewById(R.id.ocr_stacklocation_c_totalstate);
            img =  itemView.findViewById(R.id.ocr_stacklocation_c_img) ;
            rltv =  itemView.findViewById(R.id.ocr_stacklocation_box);
            btnadd = itemView.findViewById(R.id.ocr_stacklocation_c_btn);
            ggg = itemView.findViewById(R.id.ocr_stacklocation_line);
        }

        void recycleImageRequest() {
            if (imageCall != null) {
                imageCall.cancel();
                imageCall = null;
            }
            boundGoodCode = null;
            img.setImageDrawable(null);
        }
    }



}
