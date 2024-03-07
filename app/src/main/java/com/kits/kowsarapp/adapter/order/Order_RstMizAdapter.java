package com.kits.kowsarapp.adapter.order;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.order.Order_BasketActivity;
import com.kits.kowsarapp.activity.order.Order_SearchActivity;
import com.kits.kowsarapp.activity.order.Order_TableActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.order.Order_Action;
import com.kits.kowsarapp.model.base.RetrofitResponse;
import com.kits.kowsarapp.model.order.Order_BasketInfo;
import com.kits.kowsarapp.model.order.Order_DBH;
import com.kits.kowsarapp.viewholder.order.Order_RstMizViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.order.Order_APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_RstMizAdapter extends RecyclerView.Adapter<Order_RstMizViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    ArrayList<Order_BasketInfo> basketInfos;
    Order_APIInterface apiInterface;
    Intent intent;
    Order_DBH dbh;
    String date;
    Call<RetrofitResponse> call;
    Order_Action order_action;

    NotificationManager notificationManager;
    String channel_id = "Kowsarmobile";
    String channel_name = "home";
    String changeTable;

    public Order_RstMizAdapter(ArrayList<Order_BasketInfo> BasketInfos, String changeflag, Context context) {
        this.mContext = context;
        this.basketInfos = BasketInfos;
        this.callMethod = new CallMethod(mContext);
        this.order_action = new Order_Action(mContext);
        this.dbh = new Order_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Order_APIInterface.class);
        call = apiInterface.GetTodeyFromServer("GetTodeyFromServer");
        this.changeTable = changeflag;
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                assert response.body() != null;
                date = response.body().getText();
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });

    }

    @NonNull
    @Override
    public Order_RstMizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (changeTable.equals("0")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_table_card, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_tableempty_card, parent, false);
        }
        if (callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        return new Order_RstMizViewHolder(view);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull final Order_RstMizViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tv_name.setText(callMethod.NumberRegion(basketInfos.get(position).getRstMizName()));



        if (changeTable.equals("0")) {
            if (callMethod.ReadBoolan("ReserveActive")) {
                holder.btn_reserve.setVisibility(View.VISIBLE);

            }else{
                holder.btn_reserve.setVisibility(View.GONE);
            }
            holder.tv_placecount.setText(callMethod.NumberRegion(basketInfos.get(position).getPlaceCount()));

            if (basketInfos.get(position).getExplain().length() > 0) {
                holder.ll_table_mizexplain.setVisibility(View.VISIBLE);
                holder.tv_mizexplain.setText(callMethod.NumberRegion(basketInfos.get(position).getExplain()));
            } else {
                holder.ll_table_mizexplain.setVisibility(View.GONE);
            }

            if (basketInfos.get(position).getInfoExplain().length() > 0) {
                holder.ll_table_infoexplain.setVisibility(View.VISIBLE);
                holder.tv_infoexplain.setText(callMethod.NumberRegion(basketInfos.get(position).getInfoExplain()));
            } else {
                holder.ll_table_infoexplain.setVisibility(View.GONE);
            }

            if (basketInfos.get(position).getRes_BrokerName().length() > 0) {
                holder.ll_table_reserve.setVisibility(View.VISIBLE);
                holder.tv_reservestart.setText(callMethod.NumberRegion(basketInfos.get(position).getReserveStart()));
                holder.tv_reservebrokername.setText(callMethod.NumberRegion(basketInfos.get(position).getPersonName()));
                holder.tv_reservemobileno.setText(callMethod.NumberRegion(basketInfos.get(position).getMobileNo()));
            } else {
                holder.ll_table_reserve.setVisibility(View.GONE);
            }


            switch (basketInfos.get(position).getInfoState()) {
                case "0":
                case "3":
                    holder.ll_table_timebroker.setVisibility(View.GONE);
                    holder.ll_table_print_change.setVisibility(View.GONE);
                    holder.ll_table_mizexplain.setVisibility(View.GONE);
                    holder.ll_table_infoexplain.setVisibility(View.GONE);
                    holder.btn_cleartable.setVisibility(View.GONE);
                    if (basketInfos.get(position).getIsReserved().equals("1")) {
                        holder.btn_cleartable.setVisibility(View.VISIBLE);
                    }
                    break;
                case "1":
                    holder.btn_print.setText(R.string.rstmiz_seeandprintbtn);
                    holder.ll_table_print_change.setVisibility(View.VISIBLE);
                    holder.btn_cleartable.setVisibility(View.VISIBLE);
                    holder.tv_brokername.setText(callMethod.NumberRegion(basketInfos.get(position).getBrokerName()));
                    break;
                case "2":
                    holder.btn_print.setText(R.string.rstmiz_reprint);
                    holder.btn_cleartable.setVisibility(View.GONE);
                    holder.ll_table_print_change.setVisibility(View.VISIBLE);
                    holder.ll_table_timebroker.setVisibility(View.VISIBLE);
                    Calendar time_now = Calendar.getInstance();
                    Calendar time_factor = Calendar.getInstance();
                    Calendar time_duration = Calendar.getInstance();
                    time_factor.set(Calendar.HOUR_OF_DAY, Integer.parseInt(basketInfos.get(position).getTimeStart().substring(0, 2)));
                    time_factor.set(Calendar.MINUTE, Integer.parseInt(basketInfos.get(position).getTimeStart().substring(3, 5)));
                    long bet = (time_now.getTimeInMillis() - time_factor.getTimeInMillis());
                    time_duration.set(Calendar.MILLISECOND, Math.toIntExact(bet));
                    String thourOfDay, tminute, Time;
                    thourOfDay = "0" + (bet / (1000 * 60 * 60));
                    tminute = "0" + ((bet / (1000 * 60)) % 60);
                    Time = thourOfDay.substring(thourOfDay.length() - 2) + ":" + tminute.substring(tminute.length() - 2);
                    basketInfos.get(position).setTime(Time);
                    holder.tv_time.setText(callMethod.NumberRegion(basketInfos.get(position).getTime()));
                    holder.tv_brokername.setText(callMethod.NumberRegion(basketInfos.get(position).getBrokerName()));
                    if (Integer.parseInt(basketInfos.get(position).getTime().substring(0, 2)) > 1) {
                        noti_Messaging("اتمام زمان ", basketInfos.get(position).getRstMizName());
                    }


                    break;
                default:
                    break;
            }


            holder.btn_select.setOnClickListener(v -> {
                callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName());
                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());

                if (call.isExecuted()) {
                    call.cancel();
                }

                if (basketInfos.get(position).getInfoState().equals("0") || basketInfos.get(position).getInfoState().equals("3")) {

                    if (basketInfos.get(position).getIsReserved().equals("1")) {



                        String Body_str  = "";

                        Body_str =callMethod.CreateJson("Broker", dbh.ReadConfig("BrokerCode"), Body_str);
                        Body_str =callMethod.CreateJson("Miz", basketInfos.get(position).getRstmizCode(), Body_str);
                        Body_str =callMethod.CreateJson("PersonName", basketInfos.get(position).getPersonName(), Body_str);
                        Body_str =callMethod.CreateJson("Mobile", basketInfos.get(position).getMobileNo(), Body_str);
                        Body_str =callMethod.CreateJson("InfoExplain", basketInfos.get(position).getExplain(), Body_str);
                        Body_str =callMethod.CreateJson("Prepayed",  "0", Body_str);
                        Body_str =callMethod.CreateJson("ReserveStartTime", basketInfos.get(position).getReserveStart(), Body_str);
                        Body_str =callMethod.CreateJson("ReserveEndTime", basketInfos.get(position).getReserveEnd(), Body_str);
                        Body_str =callMethod.CreateJson("Date", basketInfos.get(position).getToday(), Body_str);
                        Body_str =callMethod.CreateJson("State", "1", Body_str);
                        Body_str =callMethod.CreateJson("InfoCode", basketInfos.get(position).getReserve_AppBasketInfoCode(), Body_str);



                        call = apiInterface.OrderInfoInsert(callMethod.RetrofitBody(Body_str));



                        call.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                assert response.body() != null;
                                if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                    callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                                } else {

                                    callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName());
                                    callMethod.EditString("MizType", basketInfos.get(position).getMizType());

                                    callMethod.EditString("RstmizCode", basketInfos.get(position).getRstmizCode());
                                    callMethod.EditString("PersonName", basketInfos.get(position).getPersonName());
                                    callMethod.EditString("MobileNo", basketInfos.get(position).getMobileNo());
                                    callMethod.EditString("InfoExplain", basketInfos.get(position).getInfoExplain());
                                    callMethod.EditString("Prepayed", basketInfos.get(position).getPrepayed());
                                    callMethod.EditString("ReserveStart", basketInfos.get(position).getReserveStart());
                                    callMethod.EditString("ReserveEnd", basketInfos.get(position).getReserveEnd());
                                    callMethod.EditString("Today", basketInfos.get(position).getToday());
                                    callMethod.EditString("InfoState", basketInfos.get(position).getInfoState());
                                    callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getInfoState());


                                    intent = new Intent(mContext, Order_SearchActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    mContext.startActivity(intent);
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            }
                        });
                    } else {

                        String Body_str  = "";

                        Body_str =callMethod.CreateJson("Broker", dbh.ReadConfig("BrokerCode"), Body_str);
                        Body_str =callMethod.CreateJson("Miz", basketInfos.get(position).getRstmizCode(), Body_str);
                        Body_str =callMethod.CreateJson("PersonName", "", Body_str);
                        Body_str =callMethod.CreateJson("Mobile", "", Body_str);
                        Body_str =callMethod.CreateJson("InfoExplain", "", Body_str);
                        Body_str =callMethod.CreateJson("Prepayed",  "0", Body_str);
                        Body_str =callMethod.CreateJson("ReserveStartTime", "", Body_str);
                        Body_str =callMethod.CreateJson("ReserveEndTime", "", Body_str);
                        Body_str =callMethod.CreateJson("Date", basketInfos.get(position).getToday(), Body_str);
                        Body_str =callMethod.CreateJson("State", "1", Body_str);
                        Body_str =callMethod.CreateJson("InfoCode", "0", Body_str);



                        call = apiInterface.OrderInfoInsert(callMethod.RetrofitBody(Body_str));


                        call.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                assert response.body() != null;
                                if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                    callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                                } else {
                                    callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName());

                                    callMethod.EditString("AppBasketInfoCode", response.body().getBasketInfos().get(0).getAppBasketInfoCode());
                                    intent = new Intent(mContext, Order_SearchActivity.class);
                                    mContext.startActivity(intent);
                                }


                            }

                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            }
                        });
                    }


                } else {

                    if (Integer.parseInt(basketInfos.get(position).getTime().substring(0, 2)) < 2) {



                        intent = new Intent(mContext, Order_SearchActivity.class);
                        mContext.startActivity(intent);
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
                        builder.setTitle(R.string.textvalue_allert);
                        builder.setMessage("زمان میز تمام شده است مایل به سفارش می باشید ؟");

                        builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {

                            intent = new Intent(mContext, Order_SearchActivity.class);
                            mContext.startActivity(intent);
                        });

                        builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                            // code to handle negative button click
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();


                    }
                }


            });

            holder.btn_cleartable.setOnClickListener(v -> {
                if (call.isExecuted()) {
                    call.cancel();
                }

                String Body_str  = "";

                Body_str =callMethod.CreateJson("Broker", dbh.ReadConfig("BrokerCode"), Body_str);
                Body_str =callMethod.CreateJson("Miz", basketInfos.get(position).getRstmizCode(), Body_str);
                Body_str =callMethod.CreateJson("PersonName",basketInfos.get(position).getRstmizCode(), Body_str);
                Body_str =callMethod.CreateJson("Mobile",basketInfos.get(position).getPersonName(), Body_str);
                Body_str =callMethod.CreateJson("InfoExplain",basketInfos.get(position).getMobileNo(), Body_str);
                Body_str =callMethod.CreateJson("Prepayed",  "0", Body_str);
                Body_str =callMethod.CreateJson("ReserveStartTime",basketInfos.get(position).getReserveStart(), Body_str);
                Body_str =callMethod.CreateJson("ReserveEndTime",basketInfos.get(position).getReserveEnd(), Body_str);
                Body_str =callMethod.CreateJson("State", "3", Body_str);




                if (basketInfos.get(position).getIsReserved().equals("1")) {

                    Body_str =callMethod.CreateJson("Date", date, Body_str);
                    Body_str =callMethod.CreateJson("InfoCode", basketInfos.get(position).getReserve_AppBasketInfoCode(), Body_str);


                } else {
                    Body_str =callMethod.CreateJson("Date", basketInfos.get(position).getToday(), Body_str);
                    Body_str =callMethod.CreateJson("InfoCode", basketInfos.get(position).getAppBasketInfoCode(), Body_str);


                }
                call = apiInterface.OrderInfoInsert(callMethod.RetrofitBody(Body_str));


                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
                builder.setTitle(R.string.textvalue_allert);
                builder.setMessage(R.string.textvalue_freetable);

                builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {


                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                            } else {
                                Order_TableActivity activity = (Order_TableActivity) mContext;
                                activity.CallSpinner();
                                callMethod.showToast(activity.getString(R.string.textvalue_recorded));
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        }
                    });
                });

                builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                    // code to handle negative button click
                });

                AlertDialog dialog = builder.create();
                dialog.show();


            });

            holder.btn_reserve.setOnClickListener(v -> order_action.ReserveBoxDialog(basketInfos.get(position)));


            holder.btn_print.setOnClickListener(v -> {
                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                if (basketInfos.get(position).getInfoState().equals("2")) {
                   




                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
                    builder.setTitle(R.string.textvalue_allert);
                    builder.setMessage(R.string.textvalue_reprinting);

                    builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {


                        call = apiInterface.Order_CanPrint("Order_CanPrint", callMethod.ReadString("AppBasketInfoCode"), "1");
                        call.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                                if (response.isSuccessful()) {
                                    assert response.body() != null;
                                    if (response.body().getText().equals("Done")) {
                                        order_action.OrderPrintFactor();
                                    }

                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                            }
                        });
                    });

                    builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                        // code to handle negative button click
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();


                } else {
                    intent = new Intent(mContext, Order_BasketActivity.class);
                    mContext.startActivity(intent);
                }
            });

            holder.btn_changemiz.setOnClickListener(v -> {


                Order_BasketInfo basketInfo =basketInfos.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
                builder.setTitle(R.string.textvalue_allert);
                builder.setMessage(R.string.text_doyouchangemiz);

                builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {

                    callMethod.Log(basketInfo.getRstMizName()+"");

                    callMethod.EditString("RstMizName", basketInfo.getRstMizName());
                    callMethod.EditString("MizType", basketInfo.getMizType());
                    callMethod.EditString("RstmizCode", basketInfo.getRstmizCode());
                    callMethod.EditString("PersonName", basketInfo.getPersonName());
                    callMethod.EditString("MobileNo", basketInfo.getMobileNo());
                    callMethod.EditString("InfoExplain", basketInfo.getInfoExplain());
                    callMethod.EditString("ReserveStart", basketInfo.getReserveStart());
                    callMethod.EditString("ReserveEnd", basketInfo.getReserveEnd());
                    callMethod.EditString("Today", basketInfo.getToday());
                    callMethod.EditString("InfoState", basketInfo.getInfoState());
                    callMethod.EditString("AppBasketInfoCode", basketInfo.getAppBasketInfoCode());
                    callMethod.EditString("Prepayed", "0");

                    intent = new Intent(mContext, Order_TableActivity.class);
                    intent.putExtra("State", "3");
                    intent.putExtra("EditTable", "1");
                    mContext.startActivity(intent);
                });

                builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                    // code to handle negative button click
                });

                AlertDialog dialog = builder.create();
                dialog.show();



            });

            holder.btn_explainedit.setOnClickListener(v -> order_action.EditBasketInfoExplain(basketInfos.get(position)));
        } else {

            holder.tv_name.setTextColor(R.color.black);

            holder.btn_select.setOnClickListener(v -> {


                String explainvalue="";

                if (callMethod.ReadString("InfoExplain").contains("*")) {
                    int startsub = callMethod.ReadString("InfoExplain").indexOf("*");
                    String temp = callMethod.ReadString("InfoExplain").substring(startsub);
                    int endsub = temp.indexOf("*");
                    explainvalue = temp.substring(0, endsub);
                }

                String extraexplain = mContext.getString(R.string.textvalue_transfertext) + callMethod.ReadString("RstMizName") + mContext.getString(R.string.textvalue_transfer_to) + basketInfos.get(position).getRstMizName() + ") ";


                String Body_str  = "";

                Body_str =callMethod.CreateJson("Broker", dbh.ReadConfig("BrokerCode"), Body_str);
                Body_str =callMethod.CreateJson("Miz",callMethod.ReadString("RstmizCode"), Body_str);
                Body_str =callMethod.CreateJson("PersonName",callMethod.ReadString("PersonName"), Body_str);
                Body_str =callMethod.CreateJson("Mobile",callMethod.ReadString("MobileNo"), Body_str);
                Body_str =callMethod.CreateJson("InfoExplain", explainvalue + extraexplain, Body_str);
                Body_str =callMethod.CreateJson("Prepayed",  "0", Body_str);
                Body_str =callMethod.CreateJson("ReserveStartTime", callMethod.ReadString("ReserveStart"), Body_str);
                Body_str =callMethod.CreateJson("ReserveEndTime", callMethod.ReadString("ReserveEnd"), Body_str);
                Body_str =callMethod.CreateJson("Date", callMethod.ReadString("Today"), Body_str);
                Body_str =callMethod.CreateJson("State", callMethod.ReadString("InfoState"), Body_str);
                Body_str =callMethod.CreateJson("InfoCode", callMethod.ReadString("AppBasketInfoCode"), Body_str);



                call = apiInterface.OrderInfoInsert(callMethod.RetrofitBody(Body_str));


                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                            } else {
                                order_action.ChangeTable(basketInfos.get(position));

                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                        Log.e("test", t.getMessage());

                    }
                });


            });
        }

    }

    @Override
    public int getItemCount() {
        return basketInfos.size();
    }

    public void noti_Messaging(String title, String message) {

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel Channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(Channel);
        }
        Intent notificationIntent = new Intent(mContext, Order_TableActivity.class);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notcompat = new NotificationCompat.Builder(mContext, channel_id)
                .setContentTitle(title)
                .setContentText(message)
                .setOnlyAlertOnce(false)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(contentIntent);

        notificationManager.notify(1, notcompat.build());
    }


}
