package com.kits.kowsarapp.adapter.ocr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.activity.ocr.Ocr_ConfirmActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.model.RetrofitResponse;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_GoodScan_Adapter extends RecyclerView.Adapter<Ocr_GoodScan_Adapter.facViewHolder> {
    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;

    private final Context mContext;
    private final ArrayList<Good> goods;
    private final Ocr_Action ocrAction;
    CallMethod callMethod;
    String state;
    String barcodescan;
    Intent intent;


    public Ocr_GoodScan_Adapter(ArrayList<Good> goods, Context context, String state, String barcodescan) {
        this.mContext = context;
        this.goods = goods;
        this.state = state;
        this.barcodescan = barcodescan;
        this.ocrAction = new Ocr_Action(context);
        this.callMethod = new CallMethod(context);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        this.secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);


    }

    @NonNull
    @Override
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_scan_item, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.goodscan_goodname.setText(goods.get(position).getGoodName());
        holder.goodscan_factoramount.setText(goods.get(position).getFacAmount());
        holder.goodscan_goodsellprice.setText(goods.get(position).getGoodMaxSellPrice());

        Call<RetrofitResponse> call2;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call2=apiInterface.GetImage("getImage", goods.get(position).getGoodCode()+"",0,400);
        }else{
            call2=secendApiInterface.GetImage("getImage", goods.get(position).getGoodCode()+"",0,400);
        }

        call2.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    byte[] imageByteArray1;
                    imageByteArray1 = Base64.decode(response.body().getText(), Base64.DEFAULT);
                    holder.goodscan_image.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                Log.e("onFailure", "" + t);
            }
        });


        holder.goodscan_btn.setOnClickListener(view -> {
            if (state.equals("0")){

                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call=apiInterface.CheckState("OcrControlled", goods.get(position).getAppOCRFactorRowCode(), "0", "");
                }else{
                    call=secendApiInterface.CheckState("OcrControlled", goods.get(position).getAppOCRFactorRowCode(), "0", "");
                }

                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {

                            intent = new Intent(mContext, Ocr_ConfirmActivity.class);
                            intent.putExtra("ScanResponse", barcodescan);
                            intent.putExtra("State", "0");
                            ((Activity) mContext).finish();
                            mContext.startActivity(intent);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        Log.e("", t.getMessage());
                    }
                });

            }else if (state.equals("1")) {

                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call=apiInterface.CheckState("OcrControlled", goods.get(position).getAppOCRFactorRowCode(), "2", "");
                }else{
                    call=secendApiInterface.CheckState("OcrControlled", goods.get(position).getAppOCRFactorRowCode(), "2", "");
                }
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {

                            intent = new Intent(mContext, Ocr_ConfirmActivity.class);
                            intent.putExtra("ScanResponse", barcodescan);
                            intent.putExtra("State", "1");
                            ((Activity) mContext).finish();
                            mContext.startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        Log.e("", t.getMessage());
                    }
                });

            }



        });



    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    static class facViewHolder extends RecyclerView.ViewHolder {

        private final TextView goodscan_goodname;
        private final TextView goodscan_factoramount;
        private final TextView goodscan_goodsellprice;
        private final ImageView goodscan_image;
        private final Button goodscan_btn;

        facViewHolder(View itemView) {
            super(itemView);

            goodscan_goodname = itemView.findViewById(R.id.goodscan_item_goodname);
            goodscan_factoramount = itemView.findViewById(R.id.goodscan_item_factoramount);
            goodscan_goodsellprice = itemView.findViewById(R.id.goodscan_item_goodsellprice);
            goodscan_image = itemView.findViewById(R.id.goodscan_item_image);
            goodscan_btn = itemView.findViewById(R.id.goodscan_item_btn);

        }
    }


}
