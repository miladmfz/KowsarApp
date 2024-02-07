package com.kits.kowsarapp.adapter.broker;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_ByDateActivity;
import com.kits.kowsarapp.activity.broker.Broker_DetailActivity;
import com.kits.kowsarapp.activity.broker.Broker_PFOpenActivity;
import com.kits.kowsarapp.activity.broker.Broker_SearchActivity;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ImageInfo;
import com.kits.kowsarapp.application.broker.Broker_Action;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.viewholder.broker.Broker_GoodItemViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;

import java.util.ArrayList;

public class Broker_GoodAdapter extends RecyclerView.Adapter<Broker_GoodItemViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Good> goods;
    Broker_DBH dbh;

    Broker_APIInterface apiInterface;
    final ImageInfo image_info;
    public boolean multi_select;
    Broker_Action action;
    ArrayList<Column> Columns;


    public Broker_GoodAdapter(ArrayList<Good> goods, Context context) {
        this.mContext = context;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.dbh = new Broker_DBH(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Broker_Action(mContext);
        this.Columns = dbh.GetColumns("id", "", "1");
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);
    }

    @NonNull
    @Override
    public Broker_GoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view;

        if (callMethod.ReadBoolan("LineView")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_good_line_card, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_good_grid_card, parent, false);

        }


//        GoodItemCardviewBinding binding = GoodItemCardviewBinding.inflate(
//                LayoutInflater.from(parent.getContext())
//        );
//        return new GoodItemViewHolder(binding);
        return new Broker_GoodItemViewHolder(view, mContext);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Broker_GoodItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (!callMethod.ReadBoolan("LineView")) {
            holder.bind(Columns, goods.get(position), mContext, callMethod);

        }else {
            holder.bindLine(Columns, goods.get(position), mContext, callMethod);
        }

        holder.callimage(goods.get(position));
        holder.rltv.setChecked(goods.get(position).isCheck());

        holder.rltv.setOnLongClickListener(view ->
        {
            if (goods.get(position).getGoodFieldValue("ActiveStack").equals("1")) {
                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    multi_select = true;
                    holder.rltv.setChecked(!holder.rltv.isChecked());
                    goods.get(position).setCheck(!goods.get(position).isCheck());

                    if (goods.get(position).isCheck()) {
                        if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_SearchActivity")) {
                            Broker_SearchActivity activity = (Broker_SearchActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                        if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_ByDateActivity")) {
                            Broker_ByDateActivity activity = (Broker_ByDateActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }

                    } else {
                        if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_SearchActivity")) {
                            Broker_SearchActivity activity = (Broker_SearchActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                        if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_ByDateActivity")) {
                            Broker_ByDateActivity activity = (Broker_ByDateActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }


                    }
                } else {

                    Intent intent = new Intent(mContext, Broker_PFOpenActivity.class);
                    intent.putExtra("fac", "0");
                    mContext.startActivity(intent);

                }
            } else {
                callMethod.showToast("این کالا غیر فعال می باشد");
            }

            return true;
        });

        if (callMethod.ReadBoolan("ShowDetail")) {
            holder.btnadd.setVisibility(View.VISIBLE);


            holder.rltv.setOnClickListener(v -> {

                if (multi_select) {
                    if (goods.get(position).getGoodFieldValue("ActiveStack").equals("1")) {
                        holder.rltv.setChecked(!holder.rltv.isChecked());
                        goods.get(position).setCheck(!goods.get(position).isCheck());
                        if (goods.get(position).isCheck()) {
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_SearchActivity")) {
                                Broker_SearchActivity activity = (Broker_SearchActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_ByDateActivity")) {
                                Broker_ByDateActivity activity = (Broker_ByDateActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                        } else {
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_SearchActivity")) {
                                Broker_SearchActivity activity = (Broker_SearchActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_ByDateActivity")) {
                                Broker_ByDateActivity activity = (Broker_ByDateActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                        }
                    } else {
                        callMethod.showToast("این کالا غیر فعال می باشد");
                    }
                } else {
                    Intent intent = new Intent(mContext, Broker_DetailActivity.class);
                    intent.putExtra("id", goods.get(position).getGoodFieldValue("GoodCode"));
                    intent.putExtra("ws", goods.get(position).getGoodFieldValue("Shortage"));
                    mContext.startActivity(intent);
                }

            });

            holder.btnadd.setOnClickListener(v -> {
                if (multi_select) {
                    if (goods.get(position).getGoodFieldValue("ActiveStack").equals("1")) {
                        holder.rltv.setChecked(!holder.rltv.isChecked());
                        goods.get(position).setCheck(!goods.get(position).isCheck());
                        if (goods.get(position).isCheck()) {
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_SearchActivity")) {
                                Broker_SearchActivity activity = (Broker_SearchActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_ByDateActivity")) {
                                Broker_ByDateActivity activity = (Broker_ByDateActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                        } else {
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_SearchActivity")) {
                                Broker_SearchActivity activity = (Broker_SearchActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_ByDateActivity")) {
                                Broker_ByDateActivity activity = (Broker_ByDateActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                        }
                    } else {
                        callMethod.showToast("این کالا غیر فعال می باشد");
                    }
                } else {
                    holder.Actionrltv(goods.get(position), multi_select);
                }

            });


        } else {
            holder.btnadd.setVisibility(View.GONE);


            holder.rltv.setOnClickListener(v -> {
                if (multi_select) {
                    if (goods.get(position).getGoodFieldValue("ActiveStack").equals("1")) {
                        holder.rltv.setChecked(!holder.rltv.isChecked());
                        goods.get(position).setCheck(!goods.get(position).isCheck());
                        if (goods.get(position).isCheck()) {
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_SearchActivity")) {
                                Broker_SearchActivity activity = (Broker_SearchActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_ByDateActivity")) {
                                Broker_ByDateActivity activity = (Broker_ByDateActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                        } else {
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_SearchActivity")) {
                                Broker_SearchActivity activity = (Broker_SearchActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                            if (mContext.getClass().getName().equals("com.kits.kowsarapp.activity.Broker_ByDateActivity")) {
                                Broker_ByDateActivity activity = (Broker_ByDateActivity) mContext;
                                activity.good_select_function(goods.get(position));
                            }
                        }
                    } else {
                        callMethod.showToast("این کالا غیر فعال می باشد");
                    }
                } else {
                    holder.Actionrltv(goods.get(position), multi_select);
                }

            });


        }


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull Broker_GoodItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.call.isExecuted()) {
            holder.call.cancel();

        }
    }
}
