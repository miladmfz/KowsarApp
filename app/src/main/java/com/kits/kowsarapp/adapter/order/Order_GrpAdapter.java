package com.kits.kowsarapp.adapter.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.order.Order_ValueParser;
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
    FragmentManager fragmentManager;
    CallMethod callMethod;
    String Parent_GourpCode;
    String selectedGroup;

    Order_APIInterface order_apiInterface;


    public Order_GrpAdapter(ArrayList<GoodGroup> GoodGroups, String parentcode, String selectedGroup, FragmentManager fragmentManager, Context mContext) {
        this.GoodGroups = GoodGroups;
        this.mContext = mContext;
        this.Parent_GourpCode = parentcode;
        this.selectedGroup = selectedGroup;
        this.fragmentManager = fragmentManager;
        this.callMethod = new CallMethod(mContext);
        this.order_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);

    }

    @NonNull
    @Override
    public GoodGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_grp_card, parent, false);
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
        cancelImageCall(holder);
        GoodGroup goodGroup = GoodGroups.get(position);

        holder.grpname.setText(goodGroup.getGoodGroupFieldValue("Name"));
        if (Order_ValueParser.nonNegativeLongOrDefault(
                goodGroup.getGoodGroupFieldValue("ChildNo"), 0) > 0) {
            holder.extraimg.setVisibility(View.VISIBLE);
        } else {
            holder.extraimg.setVisibility(View.GONE);
        }


        TypedValue typedValue = new TypedValue();
        Context context = holder.itemView.getContext();

        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue, true);
        int colorOnSecondary = typedValue.data;

        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
        int colorOnPrimary = typedValue.data;

        if (selectedGroup.equals(goodGroup.getGoodGroupFieldValue("GroupCode"))) {
            holder.rltv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_primary));
            holder.grpname.setTextColor(colorOnPrimary);
        } else {
            holder.rltv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_secondary));
           holder.grpname.setTextColor(colorOnSecondary);
        }



        if (!goodGroup.getGoodGroupFieldValue("GoodGroupImageName").equals("")) {

            holder.img.setVisibility(View.VISIBLE);
            Glide.with(holder.img).asBitmap().load(Base64.decode(goodGroup.getGoodGroupFieldValue("GoodGroupImageName"), Base64.DEFAULT)).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(holder.img);


        } else {

            Call<RetrofitResponse> imageCall = order_apiInterface.GetImage(
                    "getImage",
                    goodGroup.getGoodGroupFieldValue("GroupCode"),
                    "TGoodsGrp",
                    "0",
                    "400");
            holder.imageCall = imageCall;
            imageCall.enqueue(new Callback<RetrofitResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if (holder.imageCall != call) return;
                    holder.imageCall = null;
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getText() != null) {
                        if (!"no_photo".equals(response.body().getText())) {
                            goodGroup.setGoodGroupImageName(response.body().getText());
                            int modelPosition = GoodGroups.indexOf(goodGroup);
                            if (modelPosition >= 0) {
                                notifyItemChanged(modelPosition);
                            }
                        }else{
                            callMethod.Log("test"+position+" = "+response.body().getText());

                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    if (holder.imageCall == call) {
                        holder.imageCall = null;
                    }
                    if (!call.isCanceled()) {
                        callMethod.Log("Order group image failed: "
                                + t.getClass().getSimpleName());
                    }
                }
            });
        }

        View.OnClickListener groupClick = v -> navigateToGroup(goodGroup);
        holder.grpname.setOnClickListener(groupClick);
        holder.rltv.setOnClickListener(groupClick);
        holder.extraimg.setOnClickListener(groupClick);

    }

    private void navigateToGroup(GoodGroup goodGroup) {
        if (fragmentManager == null || fragmentManager.isStateSaved()) return;

        Order_SearchViewFragment searchViewFragment = new Order_SearchViewFragment();
        searchViewFragment.setParent_GourpCode(Parent_GourpCode);
        searchViewFragment.setGood_GourpCode(goodGroup.getGoodGroupFieldValue("GroupCode"));
        fragmentManager.beginTransaction()
                .replace(R.id.ord_search_a_framelayout, searchViewFragment)
                .commit();
    }

    private void cancelImageCall(GoodGroupViewHolder holder) {
        if (holder.imageCall != null) {
            holder.imageCall.cancel();
            holder.imageCall = null;
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull GoodGroupViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        cancelImageCall(holder);
    }

    @Override
    public void onViewRecycled(@NonNull GoodGroupViewHolder holder) {
        cancelImageCall(holder);
        super.onViewRecycled(holder);
    }
    @Override
    public int getItemCount() {
        return GoodGroups.size();
    }

    static class GoodGroupViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView extraimg;
        TextView grpname;
        LinearLayoutCompat rltv;
        Call<RetrofitResponse> imageCall;

        GoodGroupViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.ord_grp_c_image);
            extraimg = itemView.findViewById(R.id.ord_grp_c_imgv);
            grpname = itemView.findViewById(R.id.ord_grp_c_name);
            rltv = itemView.findViewById(R.id.ord_grp_c_list);
        }
    }
}
