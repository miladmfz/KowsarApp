package com.kits.kowsarapp.fragment.ocr;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.adapter.ocr.Ocr_Good_StackFragment_Adapter;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.ocr.Ocr_Print;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.model.ocr.Ocr_Good;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.ocr.APIClientSecond;
import com.kits.kowsarapp.webService.ocr.Ocr_APIInterface;
import com.kits.kowsarapp.R;


import java.util.ArrayList;


public class Ocr_StackFragment extends Fragment {

    Ocr_APIInterface apiInterface;
    Ocr_APIInterface secendApiInterface;
    Ocr_DBH ocr_dbh;
    ArrayList<String> GoodCodeCheck=new ArrayList<>();
    LinearLayoutCompat ll_main;

    CallMethod callMethod;
    ArrayList<Ocr_Good> ocr_goods;

    String BarcodeScan;

    int width=1;
    Handler handler;
    Ocr_Good_StackFragment_Adapter adapter;
    Ocr_Print print;
    View view;
    Dialog dialogProg;
    RecyclerView rc_good;


    public ArrayList<Ocr_Good> getOcr_goods() {
        return ocr_goods;
    }

    public void setOcr_goods(ArrayList<Ocr_Good> ocr_goods) {
        this.ocr_goods = ocr_goods;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.ocr_fragment_stack, container, false);
        ll_main = view.findViewById(R.id.ocr_stackfragment_layout);
        rc_good = view.findViewById(R.id.ocr_stackfragment_good_recy);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("kowsar","7 " );


        callMethod = new CallMethod(requireActivity());
        ocr_dbh = new Ocr_DBH(requireActivity(), callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Ocr_APIInterface.class);
        secendApiInterface = APIClientSecond.getCleint(callMethod.ReadString("SecendServerURL")).create(Ocr_APIInterface.class);
        handler=new Handler();
        print=new Ocr_Print(requireActivity());

        dialogProg = new Dialog(requireActivity());
        dialogProg.setContentView(R.layout.ocr_spinner_box);
        dialogProg.findViewById(R.id.ocr_spinner_text).setVisibility(View.GONE);

        callrecycler();

    }




    public void callrecycler() {


        adapter = new Ocr_Good_StackFragment_Adapter(ocr_goods, requireActivity());

        rc_good.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        rc_good.setAdapter(adapter);
        rc_good.setItemAnimator(new DefaultItemAnimator());

    }


    public String getBarcodeScan() {
        return BarcodeScan;
    }

    public void setBarcodeScan(String barcodeScan) {
        BarcodeScan = barcodeScan;
    }
}