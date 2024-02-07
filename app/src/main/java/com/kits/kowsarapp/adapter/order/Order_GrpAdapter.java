package com.kits.kowsarapp.adapter.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.fragment.order.Order_SearchViewFragment;
import com.kits.kowsarapp.model.base.GoodGroup;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_GrpAdapter extends RecyclerView.Adapter<Order_GrpAdapter.GoodGroupViewHolder> {

    ArrayList<GoodGroup> GoodGroups;
    Context mContext;
    FragmentTransaction fragmentTransaction;
    CallMethod callMethod;
    String Parent_GourpCode;

    Order_APIInterface apiInterface;
    Call<RetrofitResponse> call2;


    public Order_GrpAdapter(ArrayList<GoodGroup> GoodGroups, String parentcode, FragmentTransaction fragmentTransaction, Context mContext) {
        this.GoodGroups = GoodGroups;
        this.mContext = mContext;
        this.Parent_GourpCode = parentcode;
        this.fragmentTransaction = fragmentTransaction;
        this.callMethod = new CallMethod(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);

    }

    @NonNull
    @Override
    public GoodGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grp_list, parent, false);
        if (callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return new GoodGroupViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GoodGroupViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.grpname.setText(GoodGroups.get(position).getGoodGroupFieldValue("Name"));
        if (Integer.parseInt(GoodGroups.get(position).getGoodGroupFieldValue("ChildNo")) > 0) {
            holder.extraimg.setVisibility(View.VISIBLE);
        } else {
            holder.extraimg.setVisibility(View.GONE);
        }


        if (!GoodGroups.get(position).getGoodGroupFieldValue("GoodGroupImageName").equals("")) {
            Log.e("test"+position,"0000");

            holder.img.setVisibility(View.VISIBLE);
            Glide.with(holder.img).asBitmap().load(Base64.decode(GoodGroups.get(position).getGoodGroupFieldValue("GoodGroupImageName"), Base64.DEFAULT)).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(holder.img);


        } else {

            call2 = apiInterface.GetImage("getImage", GoodGroups.get(position).getGoodGroupFieldValue("GroupCode"), "TGoodsGrp", "0", "400");
            call2.enqueue(new Callback<RetrofitResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            Log.e("test"+position,response.body().getText());
                            GoodGroups.get(position).setGoodGroupImageName(response.body().getText());
                            notifyItemChanged(position);
                        }else{
                            Log.e("test"+position,response.body().getText());
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {

                }
            });
        }

        holder.rltv.setOnClickListener(v -> {
            Order_SearchViewFragment searchViewFragment = new Order_SearchViewFragment();
            searchViewFragment.setParent_GourpCode(Parent_GourpCode);
            searchViewFragment.setGood_GourpCode(GoodGroups.get(position).getGoodGroupFieldValue("GroupCode"));
            fragmentTransaction.replace(R.id.searchactivity_framelayout, searchViewFragment);
            fragmentTransaction.commit();
        });

        holder.extraimg.setOnClickListener(v -> {
            Order_SearchViewFragment searchViewFragment = new Order_SearchViewFragment();
            searchViewFragment.setParent_GourpCode(GoodGroups.get(position).getGoodGroupFieldValue("GroupCode"));
            searchViewFragment.setGood_GourpCode(GoodGroups.get(position).getGoodGroupFieldValue("GroupCode"));
            fragmentTransaction.replace(R.id.searchactivity_framelayout, searchViewFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


    }
    @Override
    public void onViewDetachedFromWindow(@NonNull GoodGroupViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (call2.isExecuted()) {
            call2.cancel();
        }
    }
    @Override
    public int getItemCount() {
        return GoodGroups.size();
    }

    static class GoodGroupViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView extraimg;
        TextView grpname;
        LinearLayout rltv;

        GoodGroupViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.grp_list_image);
            extraimg = itemView.findViewById(R.id.grp_list_imgv);
            grpname = itemView.findViewById(R.id.grp_list_name);
            rltv = itemView.findViewById(R.id.grp_list);
        }
    }
}
