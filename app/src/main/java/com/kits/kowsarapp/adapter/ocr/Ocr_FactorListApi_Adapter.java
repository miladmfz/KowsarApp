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
import com.kits.kowsarapp.activity.ocr.Ocr_ConfirmActivity;
import com.kits.kowsarapp.activity.ocr.Ocr_FactorDetailActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ocr_FactorListApi_Adapter extends RecyclerView.Adapter<Ocr_FactorListApi_Adapter.facViewHolder> {
    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface ;
    private final Context mContext;
    Intent intent;
    ArrayList<Factor> factors ;

    Ocr_Action ocr_action;
    String state ;
    CallMethod callMethod;

    public Ocr_FactorListApi_Adapter(ArrayList<Factor> retrofitFactors, String State, Context context) {
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
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ocr_factoronline_card, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, final int position) {


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



        if (factors.get(position).getAppOCRFactorExplain() != null&&factors.get(position).getAppOCRFactorExplain().length()>0){
            holder.fac_factor_ocrexplain_ll.setVisibility(View.VISIBLE);
            holder.fac_ocrexplain.setText(NumberFunctions.PerisanNumber(factors.get(position).getAppOCRFactorExplain()));
            holder.fac_rltv_ll.setBackgroundColor(mContext.getResources().getColor(R.color.red_50));

        }else {
            holder.fac_factor_ocrexplain_ll.setVisibility(View.GONE);
        }



        holder.fac_factor_state_ll.setVisibility(View.GONE);

        if(state.equals("0")){
            try {
                try {
                    if (factors.get(position).getStackClass().charAt(0) == ','){
                        holder.fac_stackclass.setText(NumberFunctions.PerisanNumber(factors.get(position).getStackClass().substring(1)));
                    }else{
                        holder.fac_stackclass.setText(NumberFunctions.PerisanNumber(factors.get(position).getStackClass()));
                    }
                }catch (Exception ignored){
                    holder.fac_stackclass.setText("انبار مشخص نیست");
                }
                //if(factor.getIsEdited().equals("True")){
                if(factor.getIsEdited().equals("1")){
                    holder.fac_factor_state_ll.setVisibility(View.VISIBLE);
                    holder.fac_hasedite.setText("اصلاح شده");
                }else {
                    holder.fac_hasedite.setText(" ");
                }
                //if(factor.getHasShortage().equals("True")){
                if(factor.getHasShortage().equals("1")){
                    holder.fac_factor_state_ll.setVisibility(View.VISIBLE);
                    holder.fac_hasshortage.setText("کسری موجودی");
                }else {
                    holder.fac_hasshortage.setText(" ");
                }
            }catch (Exception ignored){}
        }

        holder.fac_kowsardate.setText(NumberFunctions.PerisanNumber(factor.getFactorDate()));




//        if(factors.get(position).getAppIsControled().equals("True")) {
//            if (factors.get(position).getAppIsPacked().equals("True")) {
//                if (factors.get(position).getAppIsDelivered().equals("True")) {
//                    if (factors.get(position).getHasSignature().equals("True")) {
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

            if (factors.get(position).getDbname().equals(callMethod.ReadString("DbName"))){
                if(factors.get(position).getStackClass().length()>1){
                    if(callMethod.ReadString("Category").equals("5")) {
                        ocr_action.GetOcrFactorDetail(factors.get(position));
                    }else {
                        if (position < Integer.parseInt(callMethod.ReadString("AccessCount"))) {

                            if (callMethod.ReadString("Category").equals("4")) {
                                callMethod.EditString("LastTcPrint", factors.get(position).getAppTcPrintRef());

//                                Call<RetrofitResponse> call;
//
//
//                                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//                                    call =apiInterface.OcrDeliverd("OcrDeliverd", factor.getAppOCRFactorCode(), "1", callMethod.ReadString("Deliverer"));
//                                }else {
//                                    call =secendApiInterface.OcrDeliverd("OcrDeliverd", factor.getAppOCRFactorCode(), "1", callMethod.ReadString("Deliverer"));
//                                }
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
                                                intent = new Intent(mContext, Ocr_FactorDetailActivity.class);
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
            }
            else {

                if(callMethod.ReadString("Category").equals("5")) {

                    ocr_action.GetOcrFactorDetail(factors.get(position));

                }else {
                    if (position < Integer.parseInt(callMethod.ReadString("AccessCount"))) {

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
                                            intent = new Intent(mContext, Ocr_FactorDetailActivity.class);
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

            fac_customer = itemView.findViewById(R.id.ocr_factoronline_c_customer);
            fac_customercode = itemView.findViewById(R.id.ocr_factoronline_c_customercode);
            fac_factor_explain_ll = itemView.findViewById(R.id.ocr_factoronline_c_ll_explain);
            fac_factor_state_ll = itemView.findViewById(R.id.ocr_factoronline_c_ll_state);
            fac_stackclass = itemView.findViewById(R.id.ocr_factoronline_c_stackclass);

            fac_ocrexplain = itemView.findViewById(R.id.ocr_factoronline_c_ocrexplain);
            fac_factor_ocrexplain_ll = itemView.findViewById(R.id.ocr_factoronline_c_ll_ocrexplain);

            fac_code = itemView.findViewById(R.id.ocr_factoronline_c_privatecode);
            fac_hasedite = itemView.findViewById(R.id.ocr_factoronline_c_hasedited);
            fac_hasshortage = itemView.findViewById(R.id.ocr_factoronline_c_hasshortage);
            fac_kowsardate = itemView.findViewById(R.id.ocr_factoronline_c_kowsardate);
            fac_state = itemView.findViewById(R.id.ocr_factoronline_c_state);
            fac_factor_btn = itemView.findViewById(R.id.ocr_factoronline_c_btn);
            fac_explain = itemView.findViewById(R.id.ocr_factoronline_c_explain);
            fac_rltv_ll = itemView.findViewById(R.id.ocr_factoronline_c_ll_main);

            fac_rltv = itemView.findViewById(R.id.ocr_factoronline_card);
        }
    }




}
