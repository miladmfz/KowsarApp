package com.kits.kowsarapp.adapter.ocr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.ocr.Ocr_Check_Confirm_Activity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;

import java.util.ArrayList;


public class Ocr_Check_ListApi_Adapter extends RecyclerView.Adapter<Ocr_Check_ListApi_Adapter.facViewHolder> {
    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;
    private final Context mContext;
    Intent intent;
    ArrayList<Factor> factors ;

    Ocr_Action ocr_action;
    String state ;
    CallMethod callMethod;

    public Ocr_Check_ListApi_Adapter(ArrayList<Factor> retrofitFactors, String State, Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(context);
        this.ocr_action =new Ocr_Action(context);
        this.state = State;
        this.factors = retrofitFactors;
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

    }

    @NonNull
    @Override
    public Ocr_Check_ListApi_Adapter.facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ocr_check_listapi_card, parent, false);
        return new Ocr_Check_ListApi_Adapter.facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Ocr_Check_ListApi_Adapter.facViewHolder holder, final int position) {


        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {
            if (factors.get(position).getDbname().equals(callMethod.ReadString("DbName"))){
                holder.fac_rltv_ll.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }else {
                holder.fac_rltv_ll.setBackgroundColor(mContext.getResources().getColor(R.color.purple_100));
            }

        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrGostaresh")){

            holder.fac_rltv_ll.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        }else{

            holder.fac_rltv_ll.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }


        Factor factor =factors.get(position);

        holder.fac_customer.setText(NumberFunctions.PerisanNumber(factor.getCustName()));
        holder.fac_code.setText(NumberFunctions.PerisanNumber(factor.getFactorPrivateCode()));
        holder.fac_customercode.setText(NumberFunctions.PerisanNumber(factors.get(position).getCustomerCode()));



        if (factors.get(position).getExplain() != null&&factors.get(position).getExplain().length()>0){
            holder.fac_factor_explain_ll.setVisibility(View.VISIBLE);
            holder.fac_explain.setText(NumberFunctions.PerisanNumber(factors.get(position).getExplain()));

        }else {
            holder.fac_factor_explain_ll.setVisibility(View.GONE);
        }





        holder.fac_factor_state_ll.setVisibility(View.GONE);

        holder.fac_kowsardate.setText(NumberFunctions.PerisanNumber(factor.getFactorDate()));


        holder.fac_factor_btn.setOnClickListener(v -> {
            callMethod.EditString("FactorDbName", factors.get(position).getDbname());


                if (position < Integer.parseInt(callMethod.ReadString("AccessCount"))) {

                    callMethod.EditString("LastTcPrint", factors.get(position).getAppTcPrintRef());

                    intent = new Intent(mContext, Ocr_Check_Confirm_Activity.class);

                    intent.putExtra("ScanResponse", factor.getAppTcPrintRef());
                    intent.putExtra("State", state);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "فاکتور های قبلی را تکمیل کنید", Toast.LENGTH_SHORT).show();
                }



        });


    }

    @Override
    public int getItemCount() {
        return factors.size();
    }

    static class facViewHolder extends RecyclerView.ViewHolder {
        private final TextView fac_customer;
        private final TextView fac_customercode;
        private final TextView fac_code;
        private final TextView fac_hasedite;
        private final TextView fac_hasshortage;
        private final TextView fac_kowsardate;
        private final TextView fac_state;
        private final TextView fac_explain;
        private final TextView fac_stackclass;
        private final Button fac_factor_btn;
        private final LinearLayout fac_factor_explain_ll;
        private final LinearLayout fac_factor_state_ll;

        private final LinearLayout fac_factor_ocrexplain_ll;
        private final TextView fac_ocrexplain;

        MaterialCardView fac_rltv;
        LinearLayoutCompat fac_rltv_ll;

        facViewHolder(View itemView) {
            super(itemView);

            fac_customer = itemView.findViewById(R.id.ocr_check_listapi_c_customer);
            fac_customercode = itemView.findViewById(R.id.ocr_check_listapi_c_customercode);
            fac_factor_explain_ll = itemView.findViewById(R.id.ocr_check_listapi_c_ll_explain);
            fac_factor_state_ll = itemView.findViewById(R.id.ocr_check_listapi_c_ll_state);
            fac_stackclass = itemView.findViewById(R.id.ocr_check_listapi_c_stackclass);

            fac_ocrexplain = itemView.findViewById(R.id.ocr_check_listapi_c_ocrexplain);
            fac_factor_ocrexplain_ll = itemView.findViewById(R.id.ocr_check_listapi_c_ll_ocrexplain);

            fac_code = itemView.findViewById(R.id.ocr_check_listapi_c_privatecode);
            fac_hasedite = itemView.findViewById(R.id.ocr_check_listapi_c_hasedited);
            fac_hasshortage = itemView.findViewById(R.id.ocr_check_listapi_c_hasshortage);
            fac_kowsardate = itemView.findViewById(R.id.ocr_check_listapi_c_kowsardate);
            fac_state = itemView.findViewById(R.id.ocr_check_listapi_c_state);
            fac_factor_btn = itemView.findViewById(R.id.ocr_check_listapi_c_btn);
            fac_explain = itemView.findViewById(R.id.ocr_check_listapi_c_explain);
            fac_rltv_ll = itemView.findViewById(R.id.ocr_check_listapi_c_ll_main);

            fac_rltv = itemView.findViewById(R.id.ocr_check_listapi_card);
        }
    }




}
