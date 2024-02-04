package com.kits.kowsarapp.viewholder.broker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.model.Good;
import com.kits.kowsarapp.model.NumberFunctions;

import java.io.File;
import java.text.DecimalFormat;

;

public class Broker_GoodBasketHistoryViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");


    private final TextView goodnameTextView;
    private final TextView priceTextView;
    private final TextView total;
    private final TextView amount;
    private final TextView code;
    private final TextView maxsellpriceTextView;
    private final TextView maxtotal;
    private final ImageView img;


    public Broker_GoodBasketHistoryViewHolder(View itemView) {
        super(itemView);
        goodnameTextView = itemView.findViewById(R.id.good_buy_history_name);
        maxsellpriceTextView = itemView.findViewById(R.id.good_buy_history_maxprice);
        maxtotal = itemView.findViewById(R.id.good_buy_history_maxtotal);
        priceTextView = itemView.findViewById(R.id.good_buy_history_price);
        total = itemView.findViewById(R.id.good_buy_history_total);
        amount = itemView.findViewById(R.id.good_buy_history_amount);
        code = itemView.findViewById(R.id.good_buy_history_code);
        img = itemView.findViewById(R.id.good_buy_history_img);
    }

    public void bind(Good good, String itemposition) {

        int sellprice = Integer.parseInt(good.getGoodFieldValue("Price"));
        int fac_amount = Integer.parseInt(good.getGoodFieldValue("FactorAmount"));
        int unit_value = Integer.parseInt(good.getGoodFieldValue("DefaultUnitValue"));

        long price = (long) sellprice * fac_amount * unit_value;
        int maxsellprice = Integer.parseInt(good.getGoodFieldValue("MaxSellPrice"));
        long maxprice = (long) maxsellprice * fac_amount * unit_value;


        goodnameTextView.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue("GoodName")));
        priceTextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(good.getGoodFieldValue("Price")))));
        amount.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue("FactorAmount")));
        code.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue("GoodCode")));
        total.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt("" + price))));

        if (itemposition.equals("1")) {

            maxsellpriceTextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")))));
            maxtotal.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt("" + maxprice))));


        }
    }


    public void Conditionbind(Good good, ImageInfo image_info, CallMethod callMethod) {


        if (image_info.Image_exist(good.getGoodFieldValue("KsrImageCode"))) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    good.getGoodFieldValue("KsrImageCode") + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            img.setImageBitmap(myBitmap);

        }
    }


}