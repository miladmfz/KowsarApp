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

public class Ocr_StackEnumeration_Row_ListApi_Adapter extends RecyclerView.Adapter<Ocr_StackEnumeration_Row_ListApi_Adapter.facViewHolder> {

    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;
    private final Context mContext;
    Intent intent;
    ArrayList<Ocr_Good> goods ;

    Ocr_Action ocr_action;
    String state ;
    CallMethod callMethod;



    public Ocr_StackEnumeration_Row_ListApi_Adapter(ArrayList<Ocr_Good> re_goods, String State, Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(context);
        this.ocr_action =new Ocr_Action(context);
        this.state = State;
        this.goods = re_goods;
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

    }


    @NonNull
    @Override
    public Ocr_StackEnumeration_Row_ListApi_Adapter.facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ocr_stackenumeration_good_listapi_card, parent, false);
        return new Ocr_StackEnumeration_Row_ListApi_Adapter.facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Ocr_StackEnumeration_Row_ListApi_Adapter.facViewHolder holder, final int position) {

        holder.stackenum_good_tv_goodname.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodName()));

        holder.stackenum_good_tv_firstnum.setText(NumberFunctions.PerisanNumber(goods.get(position).getFirstNumeration()));


        holder.stackenum_good_ll_secondnum.setVisibility(View.GONE);
        holder.stackenum_good_ll_thirdnum.setVisibility(View.GONE);


        holder.stackenum_location_btn.setOnClickListener(v -> {

            callMethod.showToast(goods.get(position).getGoodName());

//            intent = new Intent(mContext, Ocr_StackEnumeration_Janamaie_Check_Activity.class);
//
//            intent.putExtra("StackEnumerationCode", locations.get(position).getStackEnumerationRef());
//            intent.putExtra("LocationCode", locations.get(position).getLocationCode());
//            intent.putExtra("LocationTitle", locations.get(position).getLocationTitle());
//
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    static class facViewHolder extends RecyclerView.ViewHolder {
        private final TextView stackenum_good_tv_goodname;

        private final TextView stackenum_good_tv_firstnum;
        private final TextView stackenum_good_tv_secondnum;
        private final TextView stackenum_good_tv_thirdnum;

        private final LinearLayoutCompat stackenum_good_ll_firstnum;
        private final LinearLayoutCompat stackenum_good_ll_secondnum;
        private final LinearLayoutCompat stackenum_good_ll_thirdnum;

        private final TextView stackenum_location_btn;


        MaterialCardView stackenum_rltv;
        LinearLayoutCompat stackenum_rltv_ll;

        facViewHolder(View itemView) {
            super(itemView);
            stackenum_good_tv_goodname = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_c_goodname);


            stackenum_good_tv_firstnum = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_c_firstnum);
            stackenum_good_tv_secondnum = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_c_secondnum);
            stackenum_good_tv_thirdnum = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_c_thirdnum);

            stackenum_good_ll_firstnum = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_c_ll_firstnum);
            stackenum_good_ll_secondnum = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_c_ll_secondnum);
            stackenum_good_ll_thirdnum = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_c_ll_thirdnum);

            stackenum_location_btn = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_c_btn);
            stackenum_rltv_ll = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_c_ll_main);

            stackenum_rltv = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_card);
        }
    }




}
