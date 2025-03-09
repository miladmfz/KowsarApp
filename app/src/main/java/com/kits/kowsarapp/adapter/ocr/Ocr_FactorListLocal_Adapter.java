package com.kits.kowsarapp.adapter.ocr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;

import com.kits.kowsarapp.activity.ocr.Ocr_FactorDetailActivity;
import com.kits.kowsarapp.activity.ocr.Ocr_FactorListLocalActivity;
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

public class Ocr_FactorListLocal_Adapter extends RecyclerView.Adapter<Ocr_FactorListLocal_Adapter.facViewHolder> {
    Ocr_APIInterface apiInterface ;
    Ocr_APIInterface secendApiInterface;

    private final Context mContext;
    Intent intent;
    private final ArrayList<Factor> factors;
    private final Ocr_Action ocr_action;
    private final Ocr_DBH ocr_dbh;
    Dialog dialog ;
    int width;
    public boolean multi_select;
    CallMethod callMethod;


    public Ocr_FactorListLocal_Adapter(ArrayList<Factor> factors, Context context, Integer metrics) {
        this.mContext = context;
        this.factors = factors;
        this.ocr_action = new Ocr_Action(context);
        this.callMethod = new CallMethod(context);
        ocr_dbh = new Ocr_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ocr_signature);
        this.width =metrics;
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);


    }

    @NonNull
    @Override
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ocr_factorlocal_card, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        if (callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoos") ||
                callMethod.ReadString("EnglishCompanyNameUse").equals("OcrQoqnoosOnline")) {


            if (factors.get(position).getDbname().equals(callMethod.ReadString("DbName"))){
                holder.fac_rltv_ll.setBackgroundColor(mContext.getResources().getColor(R.color.white));

            }else {
                holder.fac_rltv_ll.setBackgroundColor(mContext.getResources().getColor(R.color.purple_100));

            }
        } else if (callMethod.ReadString("EnglishCompanyNameUse").equals("Ocr Gostaresh")){

            holder.fac_rltv_ll.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        }else{

            holder.fac_rltv_ll.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }










        holder.fac_customer.setText(NumberFunctions.PerisanNumber(factors.get(position).getCustName()));
        holder.fac_customercode.setText(NumberFunctions.PerisanNumber(factors.get(position).getCustomerCode()));
        holder.fac_code.setText(NumberFunctions.PerisanNumber(factors.get(position).getFactorPrivateCode()));
        holder.fac_kowsardate.setText(NumberFunctions.PerisanNumber(factors.get(position).getFactorDate()));
        holder.fac_scandate.setText(NumberFunctions.PerisanNumber(factors.get(position).getScanDate()));

        if(!factors.get(position).getSignatureImage().equals("")) {
            holder.fac_signature.setText("دارد");
        }else{
            holder.fac_signature.setText("ناموجود");
        }

        //if (factors.get(position).getIsSent().equals("True")) {
        if (factors.get(position).getIsSent().equals("1")) {
            holder.fac_status.setVisibility(View.VISIBLE);
            holder.fac_status.setText("تاییدیه ارسال شده");
        } else {
            holder.fac_status.setVisibility(View.GONE);
        }

        holder.fac_factor.setOnClickListener(v -> {
            callMethod.EditString("FactorDbName", factors.get(position).getDbname());
            intent = new Intent(mContext, Ocr_FactorDetailActivity.class);
            intent.putExtra("ScanResponse", factors.get(position).getFactorBarcode());
            intent.putExtra("FactorImage", "hasimage");
            mContext.startActivity(intent);
            ((Activity) mContext).finish();

        });


        holder.fac_view.setOnClickListener(v -> {
            callMethod.EditString("FactorDbName", factors.get(position).getDbname());
            if (!factors.get(position).getSignatureImage().equals("")) {
                ImageView imageView=dialog.findViewById(R.id.ocr_signature_fromfactor);
                byte[] imageByteArray1;
                imageByteArray1 = Base64.decode(ocr_dbh.getimagefromfactor(factors.get(position).getFactorBarcode(),"SignatureImage"), Base64.DEFAULT);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth()/2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight()/3, false));

                imageView.setOnClickListener(v1 -> dialog.dismiss());

                dialog.show();
            }else {

                callMethod.showToast("تصویری موجود نمی باشد");
            }



        });

        holder.fac_rltv.setOnClickListener(v -> {
            if (multi_select) {
                Ocr_FactorListLocalActivity activity = (Ocr_FactorListLocalActivity) mContext;

                holder.fac_rltv.setChecked(!holder.fac_rltv.isChecked());
                factors.get(position).setCheck(!factors.get(position).isCheck());

                if(activity.Multi_sign.size()>0) {
                    if (factors.get(position).getCustomerCode().equals(activity.Multi_sign.get(0)[1])) {
                        if (factors.get(position).isCheck()) {
                            activity.factor_select_function(factors.get(position).getFactorBarcode(), factors.get(position).getCustomerCode(), 1);
                        } else {
                            activity.factor_select_function(factors.get(position).getFactorBarcode(), factors.get(position).getCustomerCode(), 0);
                        }
                    } else {
                        callMethod.showToast("مشتری یکسان نمی باشد");
                        holder.fac_rltv.setChecked(!holder.fac_rltv.isChecked());
                        factors.get(position).setCheck(!factors.get(position).isCheck());
                    }
                }else {
                    if (factors.get(position).isCheck()) {
                        activity.factor_select_function(factors.get(position).getFactorBarcode(), factors.get(position).getCustomerCode(), 1);
                    } else {
                        activity.factor_select_function(factors.get(position).getFactorBarcode(), factors.get(position).getCustomerCode(), 0);
                    }
                }

            }
        });

        holder.fac_dlt.setOnClickListener(view -> {

            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.default_loginconfig);
            EditText ed_password =  dialog.findViewById(R.id.d_loginconfig_ed);
            MaterialButton btn_login =  dialog.findViewById(R.id.d_loginconfig_btn);

            btn_login.setOnClickListener(v -> {
                if(NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode")))
                {

                    callMethod.EditString("FactorDbName", factors.get(position).getDbname());
//                    if (factors.get(position).getIsSent().equals("False")) {
                    if (factors.get(position).getIsSent().equals("0")) {
//                        Call<RetrofitResponse> call;
//
//                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
//                            call =apiInterface.OcrDeliverd("OcrDeliverd",factors.get(position).getAppOCRFactorCode(),"0",callMethod.ReadString("Deliverer"));
//                        }else {
//                            call =secendApiInterface.OcrDeliverd("OcrDeliverd",factors.get(position).getAppOCRFactorCode(),"0",callMethod.ReadString("Deliverer"));
//                        }
                        Call<RetrofitResponse> call;

                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                            call =apiInterface.CheckState("OcrDeliverd",factors.get(position).getAppOCRFactorCode(),"0",callMethod.ReadString("Deliverer"));
                        }else {
                            call =secendApiInterface.CheckState("OcrDeliverd",factors.get(position).getAppOCRFactorCode(),"0",callMethod.ReadString("Deliverer"));
                        }

                        call.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                if(response.isSuccessful()) {
                                    ocr_dbh.deletescan(factors.get(position).getFactorBarcode());
                                    intent = new Intent(mContext, Ocr_FactorListLocalActivity.class);
                                    intent.putExtra("IsSent", "0");
                                    intent.putExtra("signature", "0");
                                    ((Activity) mContext).finish();
                                    mContext.startActivity(intent);
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                                Log.e("",t.getMessage()); }
                        });
                    }else {
                        callMethod.showToast("تاییده ارسال شده است");
                    }
                }
            });
            dialog.show();

        });

        holder.fac_send.setOnClickListener(view -> {
            if (!factors.get(position).getSignatureImage().equals("")) {
                callMethod.EditString("FactorDbName", factors.get(position).getDbname());
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا رسید ارسال گردد؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> {

                            ocr_action.sendfactor(factors.get(position).getFactorBarcode(),factors.get(position).getSignatureImage());
                        })
                        .setNegativeButton("خیر", (dialogInterface, i) -> {                   })
                        .show();
            }
        });
        holder.fac_rltv.setCheckedIcon(mContext.getDrawable(R.drawable.ic_baseline_attach_file_24));
        holder.fac_rltv.setChecked(factors.get(position).isCheck());
        holder.fac_rltv.setOnLongClickListener(view -> {
            Ocr_FactorListLocalActivity activity = (Ocr_FactorListLocalActivity) mContext;

            multi_select = true;
            holder.fac_rltv.setChecked(!holder.fac_rltv.isChecked());
            factors.get(position).setCheck(!factors.get(position).isCheck());

            if(activity.Multi_sign.size()>0) {
                if (factors.get(position).getCustomerCode().equals(activity.Multi_sign.get(0)[1])) {
                    if (factors.get(position).isCheck()) {
                        activity.factor_select_function(factors.get(position).getFactorBarcode(), factors.get(position).getCustomerCode(), 1);
                    } else {
                        activity.factor_select_function(factors.get(position).getFactorBarcode(), factors.get(position).getCustomerCode(), 0);
                    }
                } else {
                    callMethod.showToast("مشتری یکسان نمی باشد");
                    holder.fac_rltv.setChecked(!holder.fac_rltv.isChecked());
                    factors.get(position).setCheck(!factors.get(position).isCheck());
                }
            }else {
                if (factors.get(position).isCheck()) {
                    activity.factor_select_function(factors.get(position).getFactorBarcode(), factors.get(position).getCustomerCode(), 1);
                } else {
                    activity.factor_select_function(factors.get(position).getFactorBarcode(), factors.get(position).getCustomerCode(), 0);
                }
            }

            return true;
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
        private final TextView fac_signature;
        private final TextView fac_kowsardate;
        private final TextView fac_scandate;
        private final TextView fac_status;
        private final Button fac_factor;
        private final Button fac_view;
        private final Button fac_send;
        private final Button fac_dlt;
        MaterialCardView fac_rltv;
        LinearLayoutCompat fac_rltv_ll;

        facViewHolder(View itemView) {
            super(itemView);

            fac_customer = itemView.findViewById(R.id.ocr_factorlocal_c_customer);
            fac_customercode = itemView.findViewById(R.id.ocr_factorlocal_c_customercode);
            fac_code = itemView.findViewById(R.id.ocr_factorlocal_c_code);
            fac_signature = itemView.findViewById(R.id.ocr_factorlocal_c_signature);
            fac_kowsardate = itemView.findViewById(R.id.ocr_factorlocal_c_kowsardate);
            fac_scandate = itemView.findViewById(R.id.ocr_factorlocal_c_scandate);
            fac_status = itemView.findViewById(R.id.ocr_factorlocal_c_status);
            fac_factor = itemView.findViewById(R.id.ocr_factorlocal_c_factor);
            fac_view = itemView.findViewById(R.id.ocr_factorlocal_c_view);
            fac_send = itemView.findViewById(R.id.ocr_factorlocal_c_send);
            fac_dlt = itemView.findViewById(R.id.ocr_factorlocal_c_dlt);
            fac_rltv_ll = itemView.findViewById(R.id.ocr_factorlocal_c_ll_main);

            fac_rltv = itemView.findViewById(R.id.ocr_factorlocal_card);
        }
    }


}
