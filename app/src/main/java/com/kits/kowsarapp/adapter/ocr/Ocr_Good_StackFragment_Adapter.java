package com.kits.kowsarapp.adapter.ocr;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Ocr_Good_StackFragment_Adapter extends RecyclerView.Adapter<Ocr_Good_StackFragment_Adapter.GoodViewHolder>{
    private final Context mContext;
    DecimalFormat decimalFormat= new DecimalFormat("0,000");
    private List<Ocr_Good> ocr_goods;
    Call<RetrofitResponse> call2;
    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;

    Ocr_Action action;
    CallMethod callMethod;



    public Ocr_Good_StackFragment_Adapter(List<Ocr_Good> ocr_goods, Context context)
    {
        this.mContext = context;
        this.ocr_goods = ocr_goods;
        this.action = new Ocr_Action(context);
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

        Log.e("kowsar","10 start"+ position );


        holder.goodnameTextView.setText(NumberFunctions.PerisanNumber(ocr_goods.get(position).getGoodName()));
        holder.sellprice_tv.setText(NumberFunctions.PerisanNumber(ocr_goods.get(position).getMaxSellPrice().substring(0,ocr_goods.get(position).getMaxSellPrice().indexOf("."))));
        holder.amount_tv.setText(NumberFunctions.PerisanNumber(ocr_goods.get(position).getStackAmount().substring(0,ocr_goods.get(position).getStackAmount().indexOf("."))));
        holder.stacklocation_tv.setText(NumberFunctions.PerisanNumber(ocr_goods.get(position).getStackLocation()));

        holder.goodnameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        holder.sellprice_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        holder.amount_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        holder.stacklocation_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));

        if (!ocr_goods.get(position).getGoodImageName().equals("")) {


            Glide.with(holder.img)
                    .asBitmap()
                    .load(Base64.decode(ocr_goods.get(position).getGoodImageName(), Base64.DEFAULT))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.img);


        } else
        {


            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call2=apiInterface.GetImage("getImage", ocr_goods.get(position).getGoodCode(),0,400);
            }else{
                call2=secendApiInterface.GetImage("getImage", ocr_goods.get(position).getGoodCode(),0,400);
            }


            call2.enqueue(new Callback<RetrofitResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        try {
                        if (!response.body().getText().equals("no_photo")) {
                            ocr_goods.get(position).setGoodImageName(response.body().getText());
                        } else {
                            ocr_goods.get(position).setGoodImageName(String.valueOf(R.string.no_photo));

                        }

                            Glide.with(holder.img)
                                    .asBitmap()
                                    .load(Base64.decode(ocr_goods.get(position).getGoodImageName(), Base64.DEFAULT))
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .fitCenter()
                                    .into(holder.img);
                        }catch (Exception e){
                            Log.e("kowsar","10 Exception"+ e.getMessage() );
                        }


                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    Log.e("kowsar","10 Throwable"+ t.getMessage() );

                }
            });
        }







        holder.btnadd.setOnClickListener(view -> {
            action.GoodStackLocation(ocr_goods.get(position));


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
        if (call2.isExecuted()) {
            call2.cancel();
        }
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
    }



}
