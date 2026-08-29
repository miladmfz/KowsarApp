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
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.viewholder.broker.Broker_GoodItemViewHolder;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;

import java.util.ArrayList;

public class Broker_GoodAdapter extends RecyclerView.Adapter<Broker_GoodItemViewHolder> {

    private final Context mContext;
    private final ArrayList<Good> goods;

    CallMethod callMethod;
    Broker_DBH broker_dbh;
    Broker_APIInterface apiInterface;
    final ImageInfo image_info;

    public boolean multi_select;

    ArrayList<Column> Columns;
    Broker_Action broker_action;

    boolean showalarm = false;


    public Broker_GoodAdapter(ArrayList<Good> goods, Context context) {

        this.mContext = context;
        this.goods = goods != null ? goods : new ArrayList<>();

        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.broker_action = new Broker_Action(mContext);

        this.broker_dbh = new Broker_DBH(
                mContext,
                callMethod.ReadString("DatabaseName")
        );

        this.Columns = broker_dbh.GetColumns("id", "", "1");

        this.apiInterface =
                APIClient
                        .getCleint(callMethod.ReadString("ServerURLUse"))
                        .create(Broker_APIInterface.class);

        if (callMethod.ReadBoolan("LastUpdateAlarm")) {

            showalarm =
                    broker_action.IsLastUpdateOlderThanMinutes(
                            safeInt(
                                    callMethod.ReadString("LastUpdateAlarmTime"),
                                    0
                            )
                    );

        } else {

            showalarm = false;
        }
    }


    @NonNull
    @Override
    public Broker_GoodItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view;

        if (callMethod.ReadBoolan("LineView")) {

            callMethod.Log(" broker_good_line_card ");

            view =
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(
                                    R.layout.broker_good_line_card,
                                    parent,
                                    false
                            );

        } else {

            view =
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(
                                    R.layout.broker_good_grid_card,
                                    parent,
                                    false
                            );
        }

        return new Broker_GoodItemViewHolder(view, mContext);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(
            @NonNull final Broker_GoodItemViewHolder holder,
            final int position
    ) {

        Good bindGood = getGoodAtPosition(position);

        if (bindGood == null) {

            callMethod.Log(
                    "BIND SKIP => invalid position=" +
                            position +
                            " | size=" +
                            goods.size()
            );

            clearHolderListeners(holder);
            return;
        }


        /*
         * خیلی مهم:
         * ViewHolder ممکن است recycle شده باشد.
         * listenerهای bind قبلی را قبل از تنظیم listener جدید پاک می‌کنیم.
         */
        clearHolderListeners(holder);


        if (!callMethod.ReadBoolan("LineView")) {

            holder.bind(
                    Columns,
                    bindGood,
                    mContext,
                    callMethod,
                    showalarm
            );

        } else {

            holder.bindLine(
                    Columns,
                    bindGood,
                    mContext,
                    callMethod,
                    showalarm
            );
        }


        holder.callimage(bindGood);
        holder.rltv.setChecked(bindGood.isCheck());


        if (callMethod.ReadBoolan("ShowGoodImage")) {

            holder.img.setVisibility(View.VISIBLE);

        } else {

            holder.img.setVisibility(View.GONE);
        }


        if (callMethod.ReadBoolan("CanUseInactive")) {

            holder.btnadd.setText("افزودن");
            holder.btnadd.setVisibility(View.VISIBLE);

        } else {

            if (isGoodActive(bindGood)) {

                holder.btnadd.setText("افزودن");
                holder.btnadd.setVisibility(View.VISIBLE);

            } else {

                holder.btnadd.setText("غیر فعال ");
                holder.btnadd.setVisibility(View.INVISIBLE);
            }
        }


        /*
         * Long Click
         */
        holder.rltv.setOnLongClickListener(view -> {

            Good currentGood = getCurrentGood(holder);

            if (currentGood == null) {

                callMethod.Log("LONG CLICK SKIP => item is no longer available");
                return true;
            }

            boolean canUseInactive =
                    callMethod.ReadBoolan("CanUseInactive");

            if (!canUseInactive && !isGoodActive(currentGood)) {

                callMethod.showToast("این کالا غیر فعال می باشد");
                return true;
            }


            if (getPreFactorCode() != 0) {

                multi_select = true;

                toggleGoodSelection(
                        holder,
                        currentGood
                );

            } else {

                openPreFactorActivity();
            }

            return true;
        });


        /*
         * Show Detail
         */
        if (callMethod.ReadBoolan("ShowDetail")) {

            holder.btnadd.setVisibility(View.VISIBLE);


            holder.rltv.setOnClickListener(v -> {

                Good currentGood = getCurrentGood(holder);

                if (currentGood == null) {

                    callMethod.Log(
                            "ROW CLICK SKIP => item is no longer available"
                    );
                    return;
                }


                callMethod.Log(
                        "getClass = " +
                                mContext.getClass().getName()
                );


                if (multi_select) {

                    /*
                     * رفتار قبلی حفظ شده:
                     * در حالت multi_select کالا باید ActiveStack=1 باشد.
                     */
                    if (!isGoodActive(currentGood)) {

                        callMethod.showToast(
                                "این کالا غیر فعال می باشد"
                        );
                        return;
                    }

                    toggleGoodSelection(
                            holder,
                            currentGood
                    );

                } else {

                    openGoodDetail(currentGood);
                }
            });


            holder.btnadd.setOnClickListener(v -> {

                Good currentGood = getCurrentGood(holder);

                if (currentGood == null) {

                    callMethod.Log(
                            "ADD CLICK SKIP => item is no longer available"
                    );
                    return;
                }


                if (multi_select) {

                    if (!isGoodActive(currentGood)) {

                        callMethod.showToast(
                                "این کالا غیر فعال می باشد"
                        );
                        return;
                    }

                    toggleGoodSelection(
                            holder,
                            currentGood
                    );

                } else {

                    holder.Actionrltv(
                            currentGood,
                            false
                    );
                }
            });


        } else {

            holder.btnadd.setVisibility(View.GONE);


            if (callMethod.ReadBoolan("ShowGoodBuyBtn")) {

                holder.rltv.setOnClickListener(v -> {

                    Good currentGood = getCurrentGood(holder);

                    if (currentGood == null) {

                        callMethod.Log(
                                "BUY CLICK SKIP => item is no longer available"
                        );
                        return;
                    }


                    if (multi_select) {

                        if (!isGoodActive(currentGood)) {

                            callMethod.showToast(
                                    "این کالا غیر فعال می باشد"
                            );
                            return;
                        }

                        toggleGoodSelection(
                                holder,
                                currentGood
                        );

                    } else {

                        holder.Actionrltv(
                                currentGood,
                                false
                        );
                    }
                });
            }
        }


        /*
         * وضعیت نهایی دکمه خرید
         */
        if (callMethod.ReadBoolan("ShowGoodBuyBtn")) {

            holder.btnadd.setVisibility(View.VISIBLE);

        } else {

            holder.btnadd.setVisibility(View.GONE);
        }
    }


    /*
     * به هیچ عنوان داخل Click Listener از position زمان bind استفاده نکن.
     *
     * RecyclerView ممکن است بعد از notify/change/recycle، position قبلی را
     * نامعتبر کند. این تابع در لحظه کلیک position واقعی ViewHolder را می‌گیرد.
     */
    private Good getCurrentGood(
            @NonNull Broker_GoodItemViewHolder holder
    ) {

        int currentPosition =
                holder.getBindingAdapterPosition();

        if (currentPosition == RecyclerView.NO_POSITION) {

            callMethod.Log(
                    "GET ITEM SKIP => RecyclerView.NO_POSITION"
            );

            return null;
        }

        return getGoodAtPosition(currentPosition);
    }


    /*
     * تمام دسترسی‌های goods.get(...) از این تابع رد می‌شوند.
     *
     * حتی اگر لیست بین دو callback تغییر کرده باشد،
     * برنامه دیگر با IndexOutOfBoundsException کرش نمی‌کند.
     */
    private Good getGoodAtPosition(int position) {

        try {

            if (position < 0) {
                return null;
            }

            if (position >= goods.size()) {
                return null;
            }

            return goods.get(position);

        } catch (IndexOutOfBoundsException e) {

            callMethod.Log(
                    "GET GOOD ERROR => position=" +
                            position +
                            " | size=" +
                            goods.size() +
                            " | " +
                            e.getMessage()
            );

            return null;

        } catch (Exception e) {

            callMethod.Log(
                    "GET GOOD ERROR => " +
                            e.getMessage()
            );

            return null;
        }
    }


    private boolean isGoodActive(Good good) {

        if (good == null) {
            return false;
        }

        return "1".equals(
                good.getGoodFieldValue("ActiveStack")
        );
    }


    /*
     * انتخاب/لغو انتخاب کالا
     */
    private void toggleGoodSelection(
            @NonNull Broker_GoodItemViewHolder holder,
            @NonNull Good good
    ) {

        boolean newCheckedState =
                !good.isCheck();

        good.setCheck(newCheckedState);
        holder.rltv.setChecked(newCheckedState);

        notifyGoodSelectionChanged(good);
    }


    /*
     * اطلاع به Activity مربوطه
     */
    private void notifyGoodSelectionChanged(
            @NonNull Good good
    ) {

        if (mContext instanceof Broker_SearchActivity) {

            Broker_SearchActivity activity =
                    (Broker_SearchActivity) mContext;

            activity.good_select_function(good);
        }


        if (mContext instanceof Broker_ByDateActivity) {

            Broker_ByDateActivity activity =
                    (Broker_ByDateActivity) mContext;

            activity.good_select_function(good);
        }
    }


    private void openGoodDetail(
            @NonNull Good good
    ) {

        String goodCode =
                good.getGoodFieldValue("GoodCode");

        if (goodCode == null ||
                goodCode.trim().isEmpty()) {

            callMethod.Log(
                    "OPEN DETAIL SKIP => GoodCode is EMPTY"
            );

            return;
        }


        Intent intent =
                new Intent(
                        mContext,
                        Broker_DetailActivity.class
                );

        intent.putExtra(
                "id",
                goodCode
        );

        intent.putExtra(
                "ws",
                safeString(
                        good.getGoodFieldValue("Shortage")
                )
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        mContext.startActivity(intent);
    }


    private void openPreFactorActivity() {

        Intent intent =
                new Intent(
                        mContext,
                        Broker_PFOpenActivity.class
                );

        intent.putExtra(
                "fac",
                "0"
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        mContext.startActivity(intent);
    }


    private int getPreFactorCode() {

        return safeInt(
                callMethod.ReadString("PreFactorCode"),
                0
        );
    }


    private int safeInt(
            String value,
            int defaultValue
    ) {

        try {

            if (value == null) {
                return defaultValue;
            }

            value = value.trim();

            if (value.isEmpty()) {
                return defaultValue;
            }

            return Integer.parseInt(value);

        } catch (Exception ignored) {

            return defaultValue;
        }
    }


    private String safeString(String value) {

        return value == null ? "" : value;
    }


    /*
     * جلوگیری از باقی ماندن listener مربوط به آیتم قبلی
     * وقتی ViewHolder توسط RecyclerView دوباره استفاده می‌شود.
     */
    private void clearHolderListeners(
            @NonNull Broker_GoodItemViewHolder holder
    ) {

        holder.rltv.setOnClickListener(null);
        holder.rltv.setOnLongClickListener(null);
        holder.btnadd.setOnClickListener(null);
    }


    @Override
    public int getItemCount() {

        return goods.size();
    }


    @Override
    public void onViewDetachedFromWindow(
            @NonNull Broker_GoodItemViewHolder holder
    ) {

        super.onViewDetachedFromWindow(holder);

        if (holder.call != null &&
                holder.call.isExecuted()) {

            holder.call.cancel();
        }
    }
}
