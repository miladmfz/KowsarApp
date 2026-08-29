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
import com.kits.kowsarapp.activity.ocr.Ocr_StackEnumeration_Janamaie_Check_Activity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.model.ocr.Ocr_Location;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Ocr_StackEnumeration_Good_ListApi_Adapter extends RecyclerView.Adapter<Ocr_StackEnumeration_Good_ListApi_Adapter.facViewHolder> {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");

    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;
    private final Context mContext;
    Intent intent;
    ArrayList<Ocr_Good> goods ;

    Ocr_Action ocr_action;
    String LocationCode ;
    String StackEnumerationCode ;
    CallMethod callMethod;



    public Ocr_StackEnumeration_Good_ListApi_Adapter(ArrayList<Ocr_Good> re_goods, String input_StackEnumerationCode, String input_LocationCode, Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(context);
        this.ocr_action =new Ocr_Action(context);
        this.LocationCode = input_LocationCode;
        this.StackEnumerationCode = input_StackEnumerationCode;
        this.goods = re_goods;
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

    }


    @NonNull
    @Override
    public Ocr_StackEnumeration_Good_ListApi_Adapter.facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ocr_stackenumeration_good_listapi_card, parent, false);
        return new Ocr_StackEnumeration_Good_ListApi_Adapter.facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Ocr_StackEnumeration_Good_ListApi_Adapter.facViewHolder holder, final int position) {

        holder.stackenum_good_tv_goodname.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodName()));
        holder.stackenum_good_tv_maxsellprice.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(goods.get(position).getMaxSellPrice()))+""));
        holder.stackenum_good_tv_firstnum.setText(NumberFunctions.PerisanNumber(goods.get(position).getFirstNumeration()));

        holder.stackenum_good_ll_firstnum.setVisibility(View.GONE);
        holder.stackenum_good_ll_secondnum.setVisibility(View.GONE);
        holder.stackenum_good_ll_thirdnum.setVisibility(View.GONE);


        try {
            String value = goods.get(position).getFirstNumeration();

            double firstNumeration = value == null ? 0 : Double.parseDouble(value);

            holder.stackenum_rltv_ll.setBackgroundColor(
                    firstNumeration > 0
                            ? mContext.getResources().getColor(R.color.red_100)
                            : mContext.getResources().getColor(R.color.white)
            );

        } catch (NumberFormatException e) {
            holder.stackenum_rltv_ll.setBackgroundColor(
                    mContext.getResources().getColor(R.color.white)
            );
        }
        holder.stackenum_location_btn.setOnClickListener(v -> {
            if (!callMethod.ReadBoolan("HintMoghayerat")){
                ocr_action.StackEnum_good(goods.get(position),StackEnumerationCode,LocationCode);

            }else{
                ocr_action.StackEnum_good_hint_moghayerat(goods.get(position),StackEnumerationCode,LocationCode);
            }
        });



    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    static class facViewHolder extends RecyclerView.ViewHolder {
        private final TextView stackenum_good_tv_goodname;
        private final TextView stackenum_good_tv_maxsellprice;

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
            stackenum_good_tv_maxsellprice = itemView.findViewById(R.id.ocr_stackenumeration_good_listapi_c_maxsellprice);


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
