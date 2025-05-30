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
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_GoodScan_Adapter extends RecyclerView.Adapter<Ocr_GoodScan_Adapter.facViewHolder> {
    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;

    private final Context mContext;
    private final ArrayList<Ocr_Good> ocr_goods;
    CallMethod callMethod;
    String state;
    String barcodescan;
    Intent intent;


    public Ocr_GoodScan_Adapter(ArrayList<Ocr_Good> goods, Context context, String state, String barcodescan) {
        this.mContext = context;
        this.ocr_goods = goods;
        this.state = state;
        this.barcodescan = barcodescan;
        this.callMethod = new CallMethod(context);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        this.secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);


    }

    @NonNull
    @Override
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ocr_goodscan_card, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.goodscan_goodname.setText(ocr_goods.get(position).getGoodName());
        holder.goodscan_factoramount.setText(ocr_goods.get(position).getFacAmount());
        holder.goodscan_goodsellprice.setText(ocr_goods.get(position).getGoodMaxSellPrice());

        Call<RetrofitResponse> call2;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call2=apiInterface.GetImage("getImage", ocr_goods.get(position).getGoodCode()+"",0,400);
        }else{
            call2=secendApiInterface.GetImage("getImage", ocr_goods.get(position).getGoodCode()+"",0,400);
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
                callMethod.Log(t.getMessage());
            }
        });


        holder.goodscan_btn.setOnClickListener(view -> {
            if (state.equals("0")){

                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call=apiInterface.OcrControlled("OcrControlled_new", ocr_goods.get(position).getAppOCRFactorRowCode(), "0", callMethod.ReadString("Deliverer"));
                }else{
                    call=secendApiInterface.OcrControlled("OcrControlled_new", ocr_goods.get(position).getAppOCRFactorRowCode(), "0", callMethod.ReadString("Deliverer"));
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
                        callMethod.Log(t.getMessage());
                    }
                });

            }else if (state.equals("1")) {

                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call=apiInterface.OcrControlled("OcrControlled_new", ocr_goods.get(position).getAppOCRFactorRowCode(), "2", callMethod.ReadString("Deliverer"));
                }else{
                    call=secendApiInterface.OcrControlled("OcrControlled_new", ocr_goods.get(position).getAppOCRFactorRowCode(), "2", callMethod.ReadString("Deliverer"));
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
                        callMethod.Log(t.getMessage());
                    }
                });

            }



        });



    }

    @Override
    public int getItemCount() {
        return ocr_goods.size();
    }

    static class facViewHolder extends RecyclerView.ViewHolder {

        private final TextView goodscan_goodname;
        private final TextView goodscan_factoramount;
        private final TextView goodscan_goodsellprice;
        private final ImageView goodscan_image;
        private final Button goodscan_btn;

        facViewHolder(View itemView) {
            super(itemView);

            goodscan_goodname = itemView.findViewById(R.id.ocr_goodscan_c_goodname);
            goodscan_factoramount = itemView.findViewById(R.id.ocr_goodscan_c_factoramount);
            goodscan_goodsellprice = itemView.findViewById(R.id.ocr_goodscan_c_goodsellprice);
            goodscan_image = itemView.findViewById(R.id.ocr_goodscan_c_image);
            goodscan_btn = itemView.findViewById(R.id.ocr_goodscan_c_btn);

        }
    }


}
