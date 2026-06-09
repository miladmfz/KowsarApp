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
import com.kits.kowsarapp.activity.ocr.Ocr_StackEnumeration_Good_List_Api_Activity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.ocr.Ocr_Location;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;

public class Ocr_StackEnumeration_Location_ListApi_Adapter extends RecyclerView.Adapter<Ocr_StackEnumeration_Location_ListApi_Adapter.facViewHolder> {

    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;
    private final Context mContext;
    Intent intent;
    ArrayList<Ocr_Location> locations ;

    Ocr_Action ocr_action;
    String StackEnumerationCode ;
    CallMethod callMethod;



    public Ocr_StackEnumeration_Location_ListApi_Adapter(ArrayList<Ocr_Location> re_locations, String input_StackEnumerationCode, Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(context);
        this.ocr_action =new Ocr_Action(context);
        this.StackEnumerationCode = input_StackEnumerationCode;
        this.locations = re_locations;
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

    }


    @NonNull
    @Override
    public Ocr_StackEnumeration_Location_ListApi_Adapter.facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ocr_stackenumeration_location_listapi_card, parent, false);
        return new Ocr_StackEnumeration_Location_ListApi_Adapter.facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Ocr_StackEnumeration_Location_ListApi_Adapter.facViewHolder holder, final int position) {

        holder.stackenum_location_title.setText(NumberFunctions.PerisanNumber(locations.get(position).getLocationTitle()));

        holder.stackenum_location_rows.setText(NumberFunctions.PerisanNumber(locations.get(position).getRwCount()));
        holder.stackenum_location_amount.setText(NumberFunctions.PerisanNumber(locations.get(position).getRwFirstNumeration()));

        holder.stackenum_location_btn.setOnClickListener(v -> {

            intent = new Intent(mContext, Ocr_StackEnumeration_Good_List_Api_Activity.class);

            intent.putExtra("StackEnumerationCode", StackEnumerationCode);
            intent.putExtra("LocationCode", locations.get(position).getLocationCode());
            intent.putExtra("LocationTitle", locations.get(position).getLocationTitle());

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class facViewHolder extends RecyclerView.ViewHolder {
        private final TextView stackenum_location_title;
        private final TextView stackenum_location_rows;
        private final TextView stackenum_location_amount;

        private final TextView stackenum_location_btn;


        MaterialCardView stackenum_rltv;
        LinearLayoutCompat stackenum_rltv_ll;

        facViewHolder(View itemView) {
            super(itemView);


            stackenum_location_btn = itemView.findViewById(R.id.ocr_stackenumeration_location_listapi_c_btn);
            stackenum_location_rows = itemView.findViewById(R.id.ocr_stackenumeration_location_listapi_c_rows);
            stackenum_location_amount = itemView.findViewById(R.id.ocr_stackenumeration_location_listapi_c_amount);
            stackenum_location_title = itemView.findViewById(R.id.ocr_stackenumeration_location_listapi_c_title);
            stackenum_rltv_ll = itemView.findViewById(R.id.ocr_stackenumeration_location_listapi_c_ll_main);

            stackenum_rltv = itemView.findViewById(R.id.ocr_stackenumeration_location_listapi_card);
        }
    }




}
