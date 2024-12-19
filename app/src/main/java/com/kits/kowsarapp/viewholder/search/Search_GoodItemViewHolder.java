package com.kits.kowsarapp.viewholder.search;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.search.Search_DBH;
import com.kits.kowsarapp.model.search.Search_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.search.Search_APIInterface;


import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Search_GoodItemViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    private final LinearLayoutCompat mainline;
    private final ImageView img;
    public MaterialCardView rltv;

    boolean multi_select1;

    public TextView tv_line_name;
    public TextView tv_line_maxsellprice;
    public TextView tv_line_amount;


    private final Context mContext;
    CallMethod callMethod;

    Search_DBH dbh;

    Search_APIInterface apiInterface;
    public Call<RetrofitResponse> call;

    ArrayList<Column> Columns;


    public Search_GoodItemViewHolder(View itemView, Context context) {
        super(itemView);

        this.mContext = context;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new Search_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.Columns = dbh.GetColumns("id", "", "1");
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Search_APIInterface.class);

        mainline = itemView.findViewById(R.id.sea_good_c_mainline);
        img = itemView.findViewById(R.id.sea_good_c_img);
        rltv = itemView.findViewById(R.id.sea_good_c_prosearch);

    }


    public void bind(ArrayList<Column> Columns, Search_Good good, Context mContext, CallMethod callMethod) {



        mainline.removeAllViews();

        for (Column Column : Columns) {
            Log.e("kowsar "+Column.getColumnName(),good.getGoodFieldValue(Column.getColumnFieldValue("columnname")));

            if (Integer.parseInt(Column.getSortOrder()) > 1) {
                TextView extra_TextView = new TextView(mContext);
                extra_TextView.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))));
                extra_TextView.setBackgroundResource(R.color.white);
                extra_TextView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                extra_TextView.setTextSize(Integer.parseInt(callMethod.ReadString("BodySize")));
                extra_TextView.setGravity(Gravity.CENTER);
                extra_TextView.setTextColor(mContext.getColor(R.color.grey_1000));

                try {
                    if (Integer.parseInt(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))) > 999) {
                        extra_TextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))))));
                    } else {
                        extra_TextView.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))));
                    }
                } catch (Exception e) {
                    extra_TextView.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))));
                }

                if (Column.getSortOrder().equals("2")) {
                    extra_TextView.setLines(3);
                    if (extra_TextView.getText().toString().length() > 50) {
                        String lowText = extra_TextView.getText().toString().substring(0, 50) + "...";
                        extra_TextView.setText(lowText);
                    }
                }


                mainline.addView(extra_TextView);
            }
        }
    }





    public void callimage(Search_Good good){

        if (!good.getGoodImageName().equals("")) {
            Glide.with(img)
                    .asBitmap()
                    .load(R.drawable.white)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(img);
            img.setVisibility(View.VISIBLE);

            Glide.with(img)
                    .asBitmap()
                    .load(Base64.decode(good.getGoodFieldValue("GoodImageName"), Base64.DEFAULT))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(img);


        } else {

            Glide.with(img)
                    .asBitmap()
                    .load(R.drawable.white)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(img);
            img.setVisibility(View.VISIBLE);

            call = apiInterface.GetImage("getImage",good.getGoodFieldValue("GoodCode"),0,150);
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(Call<RetrofitResponse> call2, Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        try {
                            if(!response.body().getText().equals("no_photo")) {
                                good.setGoodImageName(response.body().getText());
                                Glide.with(img)
                                        .asBitmap()
                                        .load(Base64.decode(response.body().getText(), Base64.DEFAULT))
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .fitCenter()
                                        .into(img);

                            } else {
                                Glide.with(img)
                                        .asBitmap()
                                        .load(R.drawable.no_photo)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .fitCenter()
                                        .into(img);

                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }


                    }
                }
                @Override
                public void onFailure(Call<RetrofitResponse> call2, Throwable t) {
                    Log.e("onFailure",""+t.toString());
                }
            });
        }



    }


}