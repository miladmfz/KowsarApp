package com.kits.kowsarapp.viewholder.find;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.find.Find_SearchActivity;

import com.kits.kowsarapp.application.base.Base_NetworkFailure;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.LatestRequestGate;
import com.kits.kowsarapp.application.base.SafeValueParser;
import com.kits.kowsarapp.application.find.Find_Action;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.find.Find_DBH;
import com.kits.kowsarapp.model.find.Find_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.find.Find_APIInterface;


import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Find_GoodItemViewHolder extends RecyclerView.ViewHolder {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");

    private final LinearLayoutCompat mainline;
    private final ImageView img;
    public MaterialCardView rltv;


    private final Context mContext;
    private final LatestRequestGate imageRequestGate = new LatestRequestGate();
    CallMethod callMethod;

    Find_DBH find_dbh;
    Find_APIInterface find_apiInterface;
    public Call<RetrofitResponse> call;

    Find_Action find_action;
    ArrayList<Column> Columns;
    private Button btnadd;


    public Find_GoodItemViewHolder(View itemView, Context context) {
        super(itemView);

        this.mContext = context;
        this.callMethod = new CallMethod(mContext);
        this.find_dbh = new Find_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.find_action = new Find_Action(mContext);
        this.Columns = find_dbh.GetColumns("id", "", "1");
        this.find_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Find_APIInterface.class);

        mainline = itemView.findViewById(R.id.find_good_c_mainline);
        img = itemView.findViewById(R.id.find_good_c_img);
        rltv = itemView.findViewById(R.id.find_good_c_prosearch);
        btnadd = itemView.findViewById(R.id.find_good_c_btn);

    }


    public void bind(ArrayList<Column> Columns, Find_Good good, Context mContext, CallMethod callMethod) {



        mainline.removeAllViews();

        for (Column Column : Columns) {

            if (SafeValueParser.intOrDefault(Column.getSortOrder(), 0) > 1) {
                TextView extra_TextView = new TextView(mContext);
                extra_TextView.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))));
                extra_TextView.setBackgroundResource(R.color.white);
                extra_TextView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                extra_TextView.setTextSize(
                        Math.max(1, SafeValueParser.intOrDefault(
                                callMethod.ReadString("BodySize"), 16
                        ))
                );
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


        if (callMethod.ReadBoolan("disableSelectedFeild")){
            btnadd.setVisibility(View.GONE);
        }else{
            btnadd.setVisibility(View.VISIBLE);
        }



        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                good.setGoodFieldValue("SelectedFeild",good.getGoodFieldValue("SelectedFeild"));

                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }
                dialog.setContentView(R.layout.find_selectedfeild);


                Button explain_btn = dialog.findViewById(R.id.find_select_c_btn);
                final TextView goodname_tv = dialog.findViewById(R.id.find_select_c_tv);
                final EditText selectedfeild_et1 = dialog.findViewById(R.id.find_select_c_et1);
                final EditText selectedfeild_et2 = dialog.findViewById(R.id.find_select_c_et2);
                final EditText selectedfeild_et3 = dialog.findViewById(R.id.find_select_c_et3);
                final EditText selectedfeild_et4 = dialog.findViewById(R.id.find_select_c_et4);


                goodname_tv.setText(good.getGoodFieldValue("GoodName"));

                selectedfeild_et1.setText(good.getGoodFieldValue("GoodName"));
                selectedfeild_et2.setText(good.getGoodFieldValue("MaxSellPrice"));
                selectedfeild_et3.setText(good.getGoodFieldValue("GoodExplain3"));
                selectedfeild_et4.setText(good.getGoodFieldValue("SellPrice6"));



                dialog.show();
                selectedfeild_et1.requestFocus();
                selectedfeild_et1.postDelayed(() -> {
                    if (!isContextActive() || !dialog.isShowing()) return;
                    InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(selectedfeild_et1, InputMethodManager.SHOW_IMPLICIT);
                }, 500);




                explain_btn.setOnClickListener(view -> {
                    find_action.showdialogProg();
                    String GoodName_str = selectedfeild_et1.getText().toString().replaceAll("[;'\"--#/*]", "");
                    String MaxSellPrice_str = selectedfeild_et2.getText().toString().replaceAll("[;'\"--#/*]", "");
                    String GoodExplain3_str = selectedfeild_et3.getText().toString().replaceAll("[;'\"--#/*]", "");
                    String SellPrice6_str = selectedfeild_et4.getText().toString().replaceAll("[;'\"--#/*]", "");


//
//                    Call<RetrofitResponse> call2 = find_apiInterface.SetSelectedFeild(
//                            "SetSelectedFeild",
//                            good.getGoodFieldValue("GoodCode"),
//                            NumberFunctions.EnglishNumber(GoodName_str),
//                            NumberFunctions.EnglishNumber(MaxSellPrice_str),
//                            NumberFunctions.EnglishNumber(GoodExplain3_str),
//                            NumberFunctions.EnglishNumber(SellPrice6_str)
//                    );
//
                    Call<RetrofitResponse> call2 = find_apiInterface.SetGoodDetail(
                            "SetGoodDetail",
                            good.getGoodFieldValue("GoodCode"),
                            NumberFunctions.EnglishNumber(GoodName_str),
                            NumberFunctions.EnglishNumber(MaxSellPrice_str),
                            NumberFunctions.EnglishNumber(GoodExplain3_str),
                            NumberFunctions.EnglishNumber(SellPrice6_str)
                    );

                    call2.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                            if (response.isSuccessful() && response.body() != null
                                    && isContextActive()
                                    && mContext instanceof Find_SearchActivity) {
                                good.setGoodFieldValue("GoodName",NumberFunctions.EnglishNumber(GoodName_str));
                                good.setGoodFieldValue("MaxSellPrice",NumberFunctions.EnglishNumber(MaxSellPrice_str));
                                good.setGoodFieldValue("GoodExplain3",NumberFunctions.EnglishNumber(GoodExplain3_str));
                                good.setGoodFieldValue("SellPrice6",NumberFunctions.EnglishNumber(SellPrice6_str));
                                Find_SearchActivity activity = (Find_SearchActivity) mContext;
                                activity.refresh();
                                find_action.dialogdissmiss();

                                if (dialog.isShowing()) dialog.dismiss();
                                callMethod.showToast("ثبت گردید");
                            } else {
                                if (isContextActive()) {
                                    find_action.dialogdissmiss();
                                    if (dialog.isShowing()) dialog.dismiss();
                                    callMethod.showToast("ثبت نگردید");
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                            Base_NetworkFailure.show(
                                    mContext,
                                    callMethod,
                                    "Find good detail update",
                                    call,
                                    t
                            );
                            if (isContextActive()) {
                                find_action.dialogdissmiss();
                                if (dialog.isShowing()) dialog.dismiss();
                                callMethod.showToast("ثبت نگردید");
                            }

                        }
                    });
                });





            }
        });



    }





    public void callimage(Find_Good good){

        imageRequestGate.invalidate();
        if (call != null) call.cancel();

        if (!callMethod.ReadBoolan("ShowGoodImage")) {
            img.setVisibility(View.GONE);


        }else{

            img.setVisibility(View.VISIBLE);

            Glide.with(img)
                    .asBitmap()
                    .load(R.drawable.img_base_no_photo)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(img);

            String cachedImage = good.getGoodImageName();
            if (cachedImage != null && !cachedImage.isEmpty()) {
                Glide.with(img)
                        .asBitmap()
                        .load(R.drawable.img_white)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter()
                        .into(img);
                img.setVisibility(View.VISIBLE);

                loadImagePayload(cachedImage);


            } else {

                Glide.with(img)
                        .asBitmap()
                        .load(R.drawable.img_white)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter()
                        .into(img);
                img.setVisibility(View.VISIBLE);

                int requestToken = imageRequestGate.begin();
                call = find_apiInterface.GetImagefind("getImage",good.getGoodFieldValue("GoodCode"),0,150);
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(Call<RetrofitResponse> call2, Response<RetrofitResponse> response) {
                        if (!canHandleImage(requestToken) || !response.isSuccessful()
                                || response.body() == null) return;

                        String payload = response.body().getText();
                        if (payload == null || payload.isEmpty() || payload.equals("no_photo")) {
                            showNoPhoto();
                            return;
                        }

                        if (loadImagePayload(payload)) {
                            good.setGoodImageName(payload);
                        }
                    }
                    @Override
                    public void onFailure(Call<RetrofitResponse> call2, Throwable t) {
                        if (call2.isCanceled() || !imageRequestGate.isCurrent(requestToken)) return;
                        Base_NetworkFailure.logOnly(
                                callMethod,
                                "Find good image",
                                call2,
                                t
                        );
                    }
                });
            }
        }


    }

    private boolean loadImagePayload(String payload) {
        if (!isContextActive()) return false;
        try {
            Glide.with(img)
                    .asBitmap()
                    .load(Base64.decode(payload, Base64.DEFAULT))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(img);
            return true;
        } catch (IllegalArgumentException exception) {
            callMethod.Log("Find image payload was invalid");
            showNoPhoto();
            return false;
        }
    }

    private void showNoPhoto() {
        if (!isContextActive()) return;
        Glide.with(img)
                .asBitmap()
                .load(R.drawable.img_base_no_photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .into(img);
    }

    private boolean canHandleImage(int requestToken) {
        return imageRequestGate.isCurrent(requestToken)
                && isContextActive();
    }

    public void recycle() {
        imageRequestGate.invalidate();
        if (call != null) {
            call.cancel();
            call = null;
        }
        Glide.with(img).clear(img);
    }

    private boolean isContextActive() {
        if (!(mContext instanceof Activity)) return true;
        Activity activity = (Activity) mContext;
        return !activity.isFinishing() && !activity.isDestroyed();
    }


}
