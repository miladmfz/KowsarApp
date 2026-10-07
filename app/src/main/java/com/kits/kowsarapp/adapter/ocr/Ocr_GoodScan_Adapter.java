package com.kits.kowsarapp.adapter.ocr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.activity.ocr.Ocr_Check_Confirm_Activity;
import com.kits.kowsarapp.activity.ocr.Ocr_Collect_Confirm_Activity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.Base_NetworkFailure;
import com.kits.kowsarapp.application.ocr.OcrImagePipeline;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;

import java.util.ArrayList;
import java.util.Objects;

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
        holder.goodscan_goodcode.setText(ocr_goods.get(position).getGoodCode());
        holder.recycleImageRequest();
        holder.boundGoodCode = ocr_goods.get(position).getGoodCode();
        holder.goodscan_image.setImageResource(R.drawable.img_base_no_photo);

        Call<RetrofitResponse> call2;
        if (Objects.equals(callMethod.ReadString("FactorDbName"), callMethod.ReadString("DbName"))){
            call2=apiInterface.GetImage("getImage", ocr_goods.get(position).getGoodCode()+"",0,250);
        }else{
            call2=secendApiInterface.GetImage("getImage", ocr_goods.get(position).getGoodCode()+"",0,250);
        }
        holder.imageCall = call2;
        final String requestedGoodCode = holder.boundGoodCode;

        call2.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                if (call2 != holder.imageCall
                        || !Objects.equals(requestedGoodCode, holder.boundGoodCode)
                        || !response.isSuccessful()
                        || response.body() == null) {
                    return;
                }
                Bitmap bitmap = OcrImagePipeline.decodeBase64(response.body().getText());
                if (bitmap != null) {
                    holder.goodscan_image.setImageBitmap(bitmap);
                } else {
                    callMethod.Log("OCR good image payload was empty or invalid");
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                if (call2.isCanceled() || call2 != holder.imageCall
                        || !Objects.equals(requestedGoodCode, holder.boundGoodCode)) {
                    return;
                }
                Base_NetworkFailure.show(
                        mContext, callMethod, "OCR scan image", call2, t);
            }
        });


        holder.goodscan_btn.setOnClickListener(view -> {
            if (state.equals("0")){

                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call=apiInterface.OcrControlled("OcrControlled", ocr_goods.get(position).getAppOCRFactorRowCode(), "0", callMethod.ReadString("JobPersonRef"));
                }else{
                    call=secendApiInterface.OcrControlled("OcrControlled", ocr_goods.get(position).getAppOCRFactorRowCode(), "0", callMethod.ReadString("JobPersonRef"));
                }

                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {

                            intent = new Intent(mContext, Ocr_Collect_Confirm_Activity.class);
                            intent.putExtra("ScanResponse", barcodescan);
                            intent.putExtra("State", "0");
                            intent.putExtra("ShowGoodDetail", "0");

                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                            ((Activity) mContext).finish();
                            mContext.startActivity(intent);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        Base_NetworkFailure.show(
                                mContext, callMethod, "OCR scan confirmation", call, t);
                    }
                });

            }else if (state.equals("1")) {

                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call=apiInterface.OcrControlled("OcrControlled", ocr_goods.get(position).getAppOCRFactorRowCode(), "2", callMethod.ReadString("JobPersonRef"));
                }else{
                    call=secendApiInterface.OcrControlled("OcrControlled", ocr_goods.get(position).getAppOCRFactorRowCode(), "2", callMethod.ReadString("JobPersonRef"));
                }
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {

                            intent = new Intent(mContext, Ocr_Check_Confirm_Activity.class);
                            intent.putExtra("ScanResponse", barcodescan);
                            intent.putExtra("State", "1");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                            ((Activity) mContext).finish();
                            mContext.startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        Base_NetworkFailure.show(
                                mContext, callMethod, "OCR scan confirmation", call, t);
                    }
                });

            }



        });



    }

    @Override
    public int getItemCount() {
        return ocr_goods.size();
    }

    @Override
    public void onViewRecycled(@NonNull facViewHolder holder) {
        holder.recycleImageRequest();
        super.onViewRecycled(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull facViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    static class facViewHolder extends RecyclerView.ViewHolder {

        private final TextView goodscan_goodname;
        private final TextView goodscan_factoramount;
        private final TextView goodscan_goodsellprice;
        private final TextView goodscan_goodcode;
        private final ImageView goodscan_image;
        private final Button goodscan_btn;
        private Call<RetrofitResponse> imageCall;
        private String boundGoodCode;

        facViewHolder(View itemView) {
            super(itemView);

            goodscan_goodname = itemView.findViewById(R.id.ocr_goodscan_c_goodname);
            goodscan_factoramount = itemView.findViewById(R.id.ocr_goodscan_c_factoramount);
            goodscan_goodsellprice = itemView.findViewById(R.id.ocr_goodscan_c_goodsellprice);
            goodscan_goodcode = itemView.findViewById(R.id.ocr_goodscan_c_goodcode);
            goodscan_image = itemView.findViewById(R.id.ocr_goodscan_c_image);
            goodscan_btn = itemView.findViewById(R.id.ocr_goodscan_c_btn);

        }

        void recycleImageRequest() {
            if (imageCall != null) {
                imageCall.cancel();
                imageCall = null;
            }
            boundGoodCode = null;
            goodscan_image.setImageDrawable(null);
        }
    }


}
