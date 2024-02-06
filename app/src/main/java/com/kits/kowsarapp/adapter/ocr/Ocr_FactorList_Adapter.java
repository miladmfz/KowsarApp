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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.ocr.Ocr_ConfirmActivity;
import com.kits.kowsarapp.activity.ocr.Ocr_FactorActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.Factor;
import com.kits.kowsarapp.model.NumberFunctions;
import com.kits.kowsarapp.model.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_FactorList_Adapter extends RecyclerView.Adapter<Ocr_FactorList_Adapter.facViewHolder> {
    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;

    private final Context mContext;
    Intent intent;
    ArrayList<Factor> factors ;

    Ocr_Action ocrAction;
    String state ;
    CallMethod callMethod;

    Ocr_DBH dbh;
    public Ocr_FactorList_Adapter(ArrayList<Factor> retrofitFactors, String State, Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(context);
        this.ocrAction =new Ocr_Action(context);
        this.dbh = new Ocr_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.state = State;
        this.factors = retrofitFactors;
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);

    }

    @NonNull
    @Override
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.factor_list, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, final int position) {



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

        if(state.equals("0")){
            try {

                holder.fac_stackclass.setText(NumberFunctions.PerisanNumber(factors.get(position).getStackClass().substring(1)));
                if(factor.getIsEdited().equals("1")){
                    holder.fac_factor_state_ll.setVisibility(View.VISIBLE);
                    holder.fac_hasedite.setText("اصلاح شده");
                }else {
                    holder.fac_hasedite.setText(" ");
                }
                if(factor.getHasShortage().equals("1")){
                    holder.fac_factor_state_ll.setVisibility(View.VISIBLE);
                    holder.fac_hasshortage.setText("کسری موجودی");
                }else {
                    holder.fac_hasshortage.setText(" ");
                }
            }catch (Exception ignored){}
        }

        holder.fac_kowsardate.setText(NumberFunctions.PerisanNumber(factor.getFactorDate()));




        if(factors.get(position).getAppIsControled().equals("1")) {
            if (factors.get(position).getAppIsPacked().equals("1")) {
                if (factors.get(position).getAppIsDelivered().equals("1")) {
                    if (factors.get(position).getHasSignature().equals("1")) {
                        holder.fac_state.setText("تحویل شده");
                    }else {
                        holder.fac_state.setText("باربری");
                    }
                }else {
                    holder.fac_state.setText("آماده ارسال");
                }
            }else {
                holder.fac_state.setText("بسته بندی");
            }
        }else {
            holder.fac_state.setText("انبار");
        }




        if(callMethod.ReadString("Category").equals("4")) {
            holder.fac_factor_btn.setText("دریافت فاکتور");
        }

        if(state.equals("4")){
            holder.fac_stackclass.setText(NumberFunctions.PerisanNumber(factors.get(position).getStackClass().substring(1)));
            holder.fac_factor_btn.setVisibility(View.GONE);
        }else {
            holder.fac_factor_btn.setVisibility(View.VISIBLE);
        }



        if(callMethod.ReadString("Category").equals("5")){
            holder.fac_factor_btn.setText("نمایش جزئیات فاکتور");
            holder.fac_factor_btn.setVisibility(View.VISIBLE);

        }


        holder.fac_factor_btn.setOnClickListener(v -> {
            callMethod.EditString("FactorDbName", factors.get(position).getDbname());
            
            
            if(factors.get(position).getStackClass().length()>1){

                if(callMethod.ReadString("Category").equals("5")) {
                    
                    ocrAction.GetOcrFactorDetail(factors.get(position));
                    
                }else {
                    if (position < 5) {

                        if (callMethod.ReadString("Category").equals("4")) {
                            callMethod.EditString("LastTcPrint", factors.get(position).getAppTcPrintRef());

                            Call<RetrofitResponse> call;
                            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                                call =apiInterface.CheckState("OcrDeliverd", factor.getAppOCRFactorCode(), "1", callMethod.ReadString("Deliverer"));
                            }else {
                                call =secendApiInterface.CheckState("OcrDeliverd", factor.getAppOCRFactorCode(), "1", callMethod.ReadString("Deliverer"));
                            }


                            call.enqueue(new Callback<RetrofitResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
                                        if (response.body().getFactors().get(0).getErrCode().equals("0")) {
                                            intent = new Intent(mContext, Ocr_FactorActivity.class);
                                            intent.putExtra("ScanResponse", factor.getAppTcPrintRef());
                                            intent.putExtra("FactorImage", "");
                                            mContext.startActivity(intent);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                                }
                            });


                        } else {

                            callMethod.EditString("LastTcPrint", factors.get(position).getAppTcPrintRef());

                            intent = new Intent(mContext, Ocr_ConfirmActivity.class);
                            intent.putExtra("ScanResponse", factor.getAppTcPrintRef());
                            intent.putExtra("State", state);
                            mContext.startActivity(intent);
                        }
                    } else {
                        Toast.makeText(mContext, "فاکتور های قبلی را تکمیل کنید", Toast.LENGTH_SHORT).show();
                    }

                }
            }else{
                Toast.makeText(mContext, "فاکتور خالی می باشد", Toast.LENGTH_SHORT).show();
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

        MaterialCardView fac_rltv;

        facViewHolder(View itemView) {
            super(itemView);

            fac_customer = itemView.findViewById(R.id.factor_list_customer);
            fac_customercode = itemView.findViewById(R.id.factor_list_customercode);
            fac_factor_explain_ll = itemView.findViewById(R.id.factor_list_ll_explain);
            fac_factor_state_ll = itemView.findViewById(R.id.factor_list_ll_state);
            fac_stackclass = itemView.findViewById(R.id.factor_list_stackclass);

            fac_code = itemView.findViewById(R.id.factor_list_privatecode);
            fac_hasedite = itemView.findViewById(R.id.factor_list_hasedited);
            fac_hasshortage = itemView.findViewById(R.id.factor_list_hasshortage);
            fac_kowsardate = itemView.findViewById(R.id.factor_list_kowsardate);
            fac_state = itemView.findViewById(R.id.factor_list_state);
            fac_factor_btn = itemView.findViewById(R.id.factor_list_btn);
            fac_explain = itemView.findViewById(R.id.factor_list_explain);

            fac_rltv = itemView.findViewById(R.id.factor_list);
        }
    }




}
