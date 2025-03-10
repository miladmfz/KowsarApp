package com.kits.kowsarapp.fragment.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.fragment.app.FragmentTransaction;
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
import retrofit2.http.GET;
import retrofit2.http.Query;


public class Order_SearchViewFragment extends Fragment {

    CallMethod callMethod;
    Order_APIInterface order_apiInterface;
    View view;
    RecyclerView rc_grp;
    RecyclerView rc_good;
    EditText ed_search;
    Handler handler = new Handler();
    String searchtarget = "", Where = "";
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Call<RetrofitResponse> call;
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
        fragmentTransaction = fragmentManager.beginTransaction();


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
                handler.postDelayed(() -> {
                    searchtarget = NumberFunctions.EnglishNumber(ed_search.getText().toString());
                    Where = "GoodName Like N''%" + searchtarget.replaceAll(" ", "%") + "%'' ";
                    good_GourpCode=order_dbh.ReadConfig("GroupCodeDefult");
                    allgood();
                }, Integer.parseInt(callMethod.ReadString("Delay")));
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
        Call<RetrofitResponse> call = order_apiInterface.Getgrp("GoodGroupInfo", Parent_GourpCode);

        callMethod.Log(call.request().url().toString());
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    callMethod.Log(response.body().getGroups().size()+"");
                    assert response.body() != null;

                    Order_GrpAdapter adapter = new Order_GrpAdapter(response.body().getGroups(), Parent_GourpCode,good_GourpCode, fragmentTransaction, requireActivity());
                    rc_grp.setLayoutManager(new LinearLayoutManager(requireActivity()));
                    rc_grp.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                rc_grp.setVisibility(View.GONE);
            }
        });
    }


    void allgood() {
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

        call = order_apiInterface.GetGoodFromGroup("GetOrderGoodList",
                Where,
                good_GourpCode,
                callMethod.ReadString("AppBasketInfoCode"));



        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    Goods = response.body().getGoods();

                    callrecycler();

                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                Goods.clear();
                callrecycler();

            }
        });
    }


    private void callrecycler() {

        progressBar.setVisibility(View.GONE);

        order_goodAdapter = new Order_GoodAdapter(Goods, requireActivity());
        if (order_goodAdapter.getItemCount() == 0) {
            tv_lottiestatus.setText(R.string.textvalue_notfound);
            img_lottiestatus.setVisibility(View.VISIBLE);
            tv_lottiestatus.setVisibility(View.VISIBLE);
        } else {
            img_lottiestatus.setVisibility(View.GONE);
            tv_lottiestatus.setVisibility(View.GONE);
        }
        rc_good.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        rc_good.setAdapter(order_goodAdapter);
        rc_good.setItemAnimator(new DefaultItemAnimator());

    }






    // In your fragment's onCreateView or where you initiate the Retrofit request


    // In your fragment's onDestroyView or onDestroy
    public void onDestroyView() {
        super.onDestroyView();

    }









}