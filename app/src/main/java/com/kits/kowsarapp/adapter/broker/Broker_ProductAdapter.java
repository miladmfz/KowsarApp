package com.kits.kowsarapp.adapter.broker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.model.base.Category;
import com.kits.kowsarapp.model.base.Product;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.viewholder.broker.Broker_CategoryViewHolder;
import com.kits.kowsarapp.viewholder.broker.Broker_ProductViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class Broker_ProductAdapter extends ExpandableRecyclerViewAdapter<Broker_CategoryViewHolder, Broker_ProductViewHolder> {

    Context mContext;
    CallMethod callMethod;
    Broker_APIInterface apiInterface;
    ImageInfo image_info;
    Call<RetrofitResponse> call;


    public Broker_ProductAdapter(List<? extends ExpandableGroup> groups, Context mContext) {
        super(groups);
        this.mContext = mContext;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);


    }

    @Override
    public Broker_CategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_parentview_card, parent, false);
        return new Broker_CategoryViewHolder(v);
    }

    @Override
    public Broker_ProductViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_childview_card, parent, false);
        return new Broker_ProductViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(Broker_ProductViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

        final Product product = (Product) group.getItems().get(childIndex);
        holder.bind(product);
        holder.intent(product, App.getContext());


        if (image_info.Image_exist("TGoodsGrp" + product.getId())) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    "TGoodsGrp" + product.getId() + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            holder.bindimage(myBitmap);

        } else {

            call = apiInterface.GetImageCustom("GetImageCustom",
                    "TGoodsGrp"
                    , String.valueOf(product.id)
                    , "500"
            );
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull retrofit2.Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            image_info.SaveImage(
                                    BitmapFactory.decodeByteArray(
                                            Base64.decode(response.body().getText(), Base64.DEFAULT),
                                            0,
                                            Base64.decode(response.body().getText(), Base64.DEFAULT).length
                                    ),
                                    "TGoodsGrp" + product.getId()
                            );

                            notifyItemChanged(flatPosition);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    callMethod.Log(t.getMessage());
                }
            });

        }


    }

    @Override
    public void onBindGroupViewHolder(Broker_CategoryViewHolder holder, int flatPosition, ExpandableGroup group) {
        final Category company = (Category) group;


        holder.bind(company);
        holder.intent(company, App.getContext());
        holder.hide(company);


        if (image_info.Image_exist("TGoodsGrp" + company.getId())) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    "TGoodsGrp" + company.getId() + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            holder.bindimage(myBitmap);

        } else {

            call = apiInterface.GetImageCustom("GetImageCustom",
                     "TGoodsGrp"
                    , String.valueOf(company.id)
                    , "500"
            );
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull retrofit2.Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            image_info.SaveImage(
                                    BitmapFactory.decodeByteArray(
                                            Base64.decode(response.body().getText(), Base64.DEFAULT),
                                            0,
                                            Base64.decode(response.body().getText(), Base64.DEFAULT).length
                                    ),
                                    "TGoodsGrp" + company.getId()
                            );

                            notifyItemChanged(flatPosition);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    callMethod.Log(t.getMessage());
                }
            });

        }

    }
}
