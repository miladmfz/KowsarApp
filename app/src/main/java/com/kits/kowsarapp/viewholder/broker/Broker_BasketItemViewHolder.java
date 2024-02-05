package com.kits.kowsarapp.viewholder.broker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_BasketActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.Good;
import com.kits.kowsarapp.model.NumberFunctions;

import java.io.File;
import java.text.DecimalFormat;

;

public class Broker_BasketItemViewHolder extends RecyclerView.ViewHolder {

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final TextView goodnameTextView;
    private final TextView maxsellpriceTextView;
    private final TextView priceTextView;
    private final TextView total;
    private final TextView maxtotal;
    private final TextView amount;
    private final TextView offer;
    private final Button btndlt;
    private final ImageView img;
    private final MaterialCardView rltv;
    private long sum = 0;


    public Broker_BasketItemViewHolder(View itemView) {
        super(itemView);

        goodnameTextView = itemView.findViewById(R.id.b_basket_c_name);
        maxsellpriceTextView = itemView.findViewById(R.id.b_basket_c_maxprice);
        priceTextView = itemView.findViewById(R.id.b_basket_c_price);
        amount = itemView.findViewById(R.id.b_basket_c_amount);
        total = itemView.findViewById(R.id.b_basket_c_total);
        maxtotal = itemView.findViewById(R.id.b_basket_c_maxtotal);
        img = itemView.findViewById(R.id.b_basket_c_img);
        btndlt = itemView.findViewById(R.id.b_basket_c_btndlt);
        offer = itemView.findViewById(R.id.b_basket_c_offer);


        rltv = itemView.findViewById(R.id.broker_basket_card);
    }


    public void bind(Good good) {

        int maxsellprice = Integer.parseInt(good.getGoodFieldValue("MaxSellPrice"));
        int sellprice = Integer.parseInt(good.getGoodFieldValue("Price"));
        int fac_amount = Integer.parseInt(good.getGoodFieldValue("FactorAmount"));
        int unit_value = Integer.parseInt(good.getGoodFieldValue("DefaultUnitValue"));


        long maxprice = (long) maxsellprice * fac_amount * unit_value;
        final long price = (long) sellprice * fac_amount * unit_value;
        sum = sum + price;
        int ws = Integer.parseInt(good.getGoodFieldValue("Shortage"));


        goodnameTextView.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue("GoodName")));
        amount.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue("FactorAmount")));

        maxsellpriceTextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")))));
        priceTextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(good.getGoodFieldValue("Price")))));
        total.setText(NumberFunctions.PerisanNumber(decimalFormat.format(price)));
        maxtotal.setText(NumberFunctions.PerisanNumber(decimalFormat.format(maxprice)));

        try {
            if (good.getGoodFieldValue("SellPriceType").equals("0")) {
                offer.setText("");
            } else {
                offer.setText(NumberFunctions.PerisanNumber((100 - ((sellprice * 100) / maxsellprice)) + " درصد تخفیف "));
            }
        } catch (Exception e) {
            offer.setText("");
            e.printStackTrace();
        }


        if (ws == 1 || ws == 2) {
            rltv.setBackgroundResource(R.drawable.bg_round_red);
        } else {
            rltv.setBackgroundResource(R.drawable.bg_round_green);
        }

    }

    public void Action(Good good, Context mContext, Broker_DBH dbh, CallMethod callMethod, Broker_Action action, ImageInfo imageInfo) {

        if (imageInfo.Image_exist(good.getGoodFieldValue("KsrImageCode"))) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    good.getGoodFieldValue("KsrImageCode") + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            img.setImageBitmap(myBitmap);
        } else {
            byte[] imageByteArray1;
            imageByteArray1 = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
            img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));

        }

        btndlt.setOnClickListener(view ->

                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
                    builder.setTitle("توجه");
                    builder.setMessage("آیا کالا از لیست حذف گردد؟");

                    builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                        dbh.DeletePreFactorRow(callMethod.ReadString("PreFactorCode"), good.getGoodFieldValue("PreFactorRowCode"));
                        callMethod.showToast("از سبد خرید حذف گردید");
                        Intent intent = new Intent(mContext, Broker_BasketActivity.class);
                        intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                        intent.putExtra("showflag", "2");
                        ((Activity) mContext).finish();
                        ((Activity) mContext).overridePendingTransition(0, 0);
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(0, 0);
                    });

                    builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                        // code to handle negative button click
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();


                }


        );


        amount.setOnClickListener(view ->
                action.buydialog(
                        good.getGoodFieldValue("GoodCode"),
                        good.getGoodFieldValue("PrefactorRowCode")
                )
        );


    }


}