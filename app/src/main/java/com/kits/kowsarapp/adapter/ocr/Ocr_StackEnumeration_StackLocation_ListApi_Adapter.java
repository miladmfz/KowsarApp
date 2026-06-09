package com.kits.kowsarapp.adapter.ocr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;

public class Ocr_StackEnumeration_StackLocation_ListApi_Adapter extends RecyclerView.Adapter<Ocr_StackEnumeration_StackLocation_ListApi_Adapter.facViewHolder> {

    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;
    private final Context mContext;
    Intent intent;
    ArrayList<Ocr_Good> goods ;

    Ocr_Action ocr_action;
    String LocationCode ;
    String StackEnumerationCode ;
    CallMethod callMethod;



    public Ocr_StackEnumeration_StackLocation_ListApi_Adapter(ArrayList<Ocr_Good> re_goods, String input_StackEnumerationCode, String input_LocationCode, Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(context);
        this.ocr_action =new Ocr_Action(context);
        this.StackEnumerationCode = input_StackEnumerationCode;
        this.LocationCode = input_LocationCode;
        this.goods = re_goods;
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

    }


    @NonNull
    @Override
    public Ocr_StackEnumeration_StackLocation_ListApi_Adapter.facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ocr_stackenumeration_locationstack_listapi_card, parent, false);
        return new Ocr_StackEnumeration_StackLocation_ListApi_Adapter.facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Ocr_StackEnumeration_StackLocation_ListApi_Adapter.facViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.stackenum_locationstack_tv_goodname.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodName()));

        holder.stackenum_locationstack_tv_firstnum.setText(NumberFunctions.PerisanNumber(goods.get(position).getNum1()));

        holder.stackenum_rltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ocr_action.StackEnum_good(goods.get(position),StackEnumerationCode,LocationCode);
            }
        });


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    static class facViewHolder extends RecyclerView.ViewHolder {

        private final TextView stackenum_locationstack_tv_goodname;
        private final TextView stackenum_locationstack_tv_firstnum;


        MaterialCardView stackenum_rltv;
        LinearLayoutCompat stackenum_rltv_ll;

        facViewHolder(View itemView) {
            super(itemView);
            stackenum_locationstack_tv_goodname = itemView.findViewById(R.id.ocr_stackenumeration_locationstack_listapi_c_goodname);
            stackenum_locationstack_tv_firstnum = itemView.findViewById(R.id.ocr_stackenumeration_locationstack_listapi_c_firstnum);

            stackenum_rltv_ll = itemView.findViewById(R.id.ocr_stackenumeration_locationstack_listapi_c_ll_main);

            stackenum_rltv = itemView.findViewById(R.id.ocr_stackenumeration_locationstack_listapi_card);
        }
    }



}
