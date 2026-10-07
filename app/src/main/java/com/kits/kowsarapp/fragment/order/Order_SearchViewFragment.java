package com.kits.kowsarapp.fragment.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.order.Order_BasketActivity;
import com.kits.kowsarapp.adapter.order.Order_GoodAdapter;
import com.kits.kowsarapp.adapter.order.Order_GrpAdapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.LatestRequestGate;
import com.kits.kowsarapp.application.base.NetworkUtils;
import com.kits.kowsarapp.application.base.SafeValueParser;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.order.Order_DBH;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_SearchViewFragment extends Fragment {

    CallMethod callMethod;
    Order_APIInterface order_apiInterface;
    View view;
    RecyclerView rc_grp;
    RecyclerView rc_good;
    EditText ed_search;
    final Handler handler = new Handler(Looper.getMainLooper());
    final LatestRequestGate groupRequestGate = new LatestRequestGate();
    final LatestRequestGate goodsRequestGate = new LatestRequestGate();
    String searchtarget = "", Where = "";
    FragmentManager fragmentManager;
    Call<RetrofitResponse> call;
    Call<RetrofitResponse> groupCall;
    Order_GoodAdapter order_goodAdapter;
    ArrayList<Good> Goods = new ArrayList<>();
    LottieAnimationView progressBar;
    LottieAnimationView img_lottiestatus;
    TextView tv_lottiestatus;
    Button Btn_GoodToOrder;
    Order_DBH order_dbh;
    String Parent_GourpCode;
    String good_GourpCode;

    public void setParent_GourpCode(String parent_GourpCode) {
        Parent_GourpCode = parent_GourpCode;
    }

    public void setGood_GourpCode(String good_GourpCode) {
        this.good_GourpCode = good_GourpCode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.order_fragment_goodview, container, false);

        rc_grp = view.findViewById(R.id.ord_fragment_grp_recy);
        rc_good = view.findViewById(R.id.ord_fragment_good_recy);
        ed_search = view.findViewById(R.id.ord_fragment_good_search);
        Btn_GoodToOrder = view.findViewById(R.id.ord_fragment_good_to_order);

        progressBar = view.findViewById(R.id.ord_fragment_good_prog);
        img_lottiestatus = view.findViewById(R.id.ord_fragment_good_lottie);
        tv_lottiestatus = view.findViewById(R.id.ord_fragment_good_tvstatus);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        callMethod = new CallMethod(requireActivity());
        order_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        order_dbh = new Order_DBH(requireActivity(), callMethod.ReadString("DatabaseName"));

        fragmentManager = requireActivity().getSupportFragmentManager();


        ed_search.setText(searchtarget);
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                handler.removeCallbacksAndMessages(null);
                String query = editable == null ? "" : editable.toString();
                handler.postDelayed(() -> {
                    if (!isUiActive()) return;
                    searchtarget = NumberFunctions.EnglishNumber(query);
                    Where = "GoodName Like N''%" + searchtarget.replaceAll(" ", "%") + "%'' ";
                    good_GourpCode=order_dbh.ReadConfig("GroupCodeDefult");
                    allgood();
                }, Math.max(0, SafeValueParser.intOrDefault(callMethod.ReadString("Delay"), 300)));
            }
        });


        Btn_GoodToOrder.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), Order_BasketActivity.class);
            startActivity(intent);
        });
        allgrp();
        allgood();


    }


    void allgrp() {
        //Call<RetrofitResponse> call = order_apiInterface.GetOrdergroupList("GetOrdergroupList", Parent_GourpCode);
        if (groupCall != null) groupCall.cancel();
        int requestToken = groupRequestGate.begin();
        groupCall = order_apiInterface.Getgrp("GoodGroupInfo", Parent_GourpCode);

        callMethod.Log(groupCall.request().url().toString());
        groupCall.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (!groupRequestGate.isCurrent(requestToken) || !isUiActive()) return;
                if (Order_SearchViewFragment.this.groupCall == call) {
                    Order_SearchViewFragment.this.groupCall = null;
                }
                RetrofitResponse body = response.body();
                if (!response.isSuccessful() || body == null || body.getGroups() == null) {
                    rc_grp.setVisibility(View.GONE);
                    return;
                }

                Context context = getContext();
                if (context == null) return;
                callMethod.Log(body.getGroups().size()+"");
                Order_GrpAdapter adapter = new Order_GrpAdapter(
                        body.getGroups(), Parent_GourpCode, good_GourpCode, fragmentManager, context);
                rc_grp.setVisibility(View.VISIBLE);
                rc_grp.setLayoutManager(new LinearLayoutManager(context));
                rc_grp.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                if (call.isCanceled() || !groupRequestGate.isCurrent(requestToken) || !isUiActive()) return;
                if (Order_SearchViewFragment.this.groupCall == call) {
                    Order_SearchViewFragment.this.groupCall = null;
                }
                handleNetworkFailure(rc_grp);
            }
        });
    }


    void allgood() {
        if (!isUiActive()) return;
        Goods.clear();
        progressBar.setVisibility(View.VISIBLE);
        img_lottiestatus.setVisibility(View.GONE);
        tv_lottiestatus.setVisibility(View.GONE);



//        String RequestBody_str  = "";
//
//        RequestBody_str =callMethod.CreateJson("Where", Where, "");
//        RequestBody_str =callMethod.CreateJson("GroupCode", good_GourpCode, RequestBody_str);
//        RequestBody_str =callMethod.CreateJson("AppBasketInfoRef", callMethod.ReadString("AppBasketInfoCode"), RequestBody_str);
//
//
//        Call<RetrofitResponse> call = order_apiInterface.GetOrderGoodList(callMethod.RetrofitBody(RequestBody_str));

        if (call != null) call.cancel();
        int requestToken = goodsRequestGate.begin();
        call = order_apiInterface.GetGoodFromGroup("GetOrderGoodList",
                Where,
                good_GourpCode,
                callMethod.ReadString("AppBasketInfoCode"));



        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (!goodsRequestGate.isCurrent(requestToken) || !isUiActive()) return;
                if (Order_SearchViewFragment.this.call == call) {
                    Order_SearchViewFragment.this.call = null;
                }
                RetrofitResponse body = response.body();
                if (!response.isSuccessful() || body == null || body.getGoods() == null) {
                    Goods.clear();
                    callrecycler();
                    return;
                }

                Goods = body.getGoods();
                callrecycler();
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                if (call.isCanceled() || !goodsRequestGate.isCurrent(requestToken) || !isUiActive()) return;
                if (Order_SearchViewFragment.this.call == call) {
                    Order_SearchViewFragment.this.call = null;
                }
                handleNetworkFailure(null);
                Goods.clear();
                callrecycler();

            }
        });
    }


    private void callrecycler() {
        if (!isUiActive()) return;
        Context context = getContext();
        if (context == null) return;

        progressBar.setVisibility(View.GONE);
        rc_good.setVisibility(View.VISIBLE);

        order_goodAdapter = new Order_GoodAdapter(Goods, context);
        if (order_goodAdapter.getItemCount() == 0) {
            tv_lottiestatus.setText(R.string.textvalue_notfound);
            img_lottiestatus.setVisibility(View.VISIBLE);
            tv_lottiestatus.setVisibility(View.VISIBLE);
        } else {
            img_lottiestatus.setVisibility(View.GONE);
            tv_lottiestatus.setVisibility(View.GONE);
        }
        rc_good.setLayoutManager(new GridLayoutManager(context, 2));
        rc_good.setAdapter(order_goodAdapter);
        rc_good.setItemAnimator(new DefaultItemAnimator());

    }

    private void handleNetworkFailure(View affectedView) {
        Context context = getContext();
        if (context == null) return;
        try {
            if (!NetworkUtils.isNetworkAvailable(context)) {
                callMethod.showToast("اتصال اینترنت قطع است!");
            } else if (NetworkUtils.isVPNActive()) {
                callMethod.showToast("VPN فعال است، ممکن است ارتباط با سرور مختل شود!");
            } else {
                String serverUrl = callMethod.ReadString("ServerURLUse");
                if (serverUrl != null && !serverUrl.isEmpty() && !NetworkUtils.canReachServer(serverUrl)) {
                    callMethod.showToast("سرور در دسترس نیست یا فیلتر شده است!");
                } else {
                    callMethod.showToast("مشکل در برقراری ارتباط با سرور برای بارگیری عکس");
                }
            }
        } catch (Exception e) {
            callMethod.Log("Network check error: " + e.getClass().getSimpleName());
            callMethod.showToast("خطا در بررسی وضعیت شبکه");
        }
        if (affectedView != null) affectedView.setVisibility(View.GONE);
    }

    private boolean isUiActive() {
        return isAdded() && getView() != null && getActivity() != null
                && !getActivity().isFinishing();
    }






    // In your fragment's onCreateView or where you initiate the Retrofit request


    // In your fragment's onDestroyView or onDestroy
    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        groupRequestGate.invalidate();
        goodsRequestGate.invalidate();
        if (groupCall != null) groupCall.cancel();
        if (call != null) call.cancel();
        groupCall = null;
        call = null;
        if (rc_grp != null) rc_grp.setAdapter(null);
        if (rc_good != null) rc_good.setAdapter(null);
        rc_grp = null;
        rc_good = null;
        ed_search = null;
        Btn_GoodToOrder = null;
        progressBar = null;
        img_lottiestatus = null;
        tv_lottiestatus = null;
        view = null;
        super.onDestroyView();
    }









}
