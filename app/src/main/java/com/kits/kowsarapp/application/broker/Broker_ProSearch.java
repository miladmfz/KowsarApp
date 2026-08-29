package com.kits.kowsarapp.application.broker;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.kits.kowsarapp.R;
import com.kits.kowsarapp.activity.broker.Broker_SearchActivity;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.webService.base.APIClient;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.broker.Broker_DBH;
import com.kits.kowsarapp.webService.broker.Broker_APIInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import androidx.core.graphics.ColorUtils;

import com.google.android.material.color.MaterialColors;
import java.util.ArrayList;
import java.util.Objects;

public class Broker_ProSearch {

    private final Context mContext;
    LinearLayoutCompat layout_view;
    MaterialButton btn_search;
    Dialog dialog;
    CallMethod callMethod;
    Spinner spinner;


    private final Broker_DBH broker_dbh;
    Broker_APIInterface broker_apiInterface;

    ArrayList<Column> Goodtype= new ArrayList<>();
    ArrayList<String> Goodtype_array = new ArrayList<>();
    ArrayList<Column> Columns= new ArrayList<>();

    String Where;

    
    public Broker_ProSearch(Context context) {
        this.mContext = context;
        this.Where = "";
        callMethod = new CallMethod(mContext);

        this.broker_dbh = new Broker_DBH(mContext, callMethod.ReadString("DatabaseName"));

        broker_apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(Broker_APIInterface.class);

    }


    public void search_pro() {


        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//title laye nadashte bashim
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setContentView(R.layout.broker_prosearch_box);

        spinner = dialog.findViewById(R.id.b_prosearch_spinner);
        layout_view = dialog.findViewById(R.id.b_prosearch_layout);

        int i = 0;
        int j = 0;
        Goodtype = broker_dbh.GetAllGoodType();
        for (Column Column_Goodtype : Goodtype) {
            Goodtype_array.add(Column_Goodtype.getColumnFieldValue("goodtype"));
            if (Integer.parseInt(Column_Goodtype.getColumnFieldValue("IsDefault")) == 1) {
                j = i;
            }
            i++;
        }

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_item, Goodtype_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setSelection(j);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                layout_view.removeAllViews();
                pro_c(Goodtype_array.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dialog.show();

    }

    private int dp(int value) {
        return Math.round(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        value,
                        mContext.getResources()
                                .getDisplayMetrics()
                )
        );
    }

    private int safeInt(
            String value,
            int fallback
    ) {
        try {
            return Integer.parseInt(
                    safeText(value)
            );
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private String safeText(String value) {
        return value == null
                ? ""
                : value.trim();
    }
    private GradientDrawable createSimpleBackground(
            int backgroundColor,
            float radius
    ) {
        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(backgroundColor);
        drawable.setCornerRadius(radius);

        return drawable;
    }
    private GradientDrawable createSearchRowBackground(
            int surfaceColor,
            int primaryColor
    ) {
        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(surfaceColor);
        drawable.setCornerRadius(dp(8));

        drawable.setStroke(
                dp(1),
                ColorUtils.setAlphaComponent(
                        primaryColor,
                        45
                )
        );

        return drawable;
    }
    private LinearLayoutCompat createSearchRow(
            Column column,
            int sortOrder
    ) {
        int primaryColor = MaterialColors.getColor(
                mContext,
                com.google.android.material.R.attr.colorPrimary,
                Color.DKGRAY
        );

        int surfaceColor = MaterialColors.getColor(
                mContext,
                com.google.android.material.R.attr.colorSurface,
                Color.WHITE
        );

        int onBackgroundColor = MaterialColors.getColor(
                mContext,
                com.google.android.material.R.attr.colorOnBackground,
                Color.BLACK
        );

        int softPrimaryColor = ColorUtils.blendARGB(
                surfaceColor,
                primaryColor,
                0.08f
        );

        LinearLayoutCompat row = new LinearLayoutCompat(mContext);

        row.setOrientation(LinearLayoutCompat.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        row.setPadding(dp(4), dp(3), dp(4), dp(3));

        LinearLayoutCompat.LayoutParams rowParams =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        dp(46)
                );

        rowParams.setMargins(
                0,
                0,
                0,
                dp(4)
        );

        row.setLayoutParams(rowParams);

        row.setBackground(
                createSearchRowBackground(
                        surfaceColor,
                        primaryColor
                )
        );

        TextView titleView = new TextView(mContext);

        titleView.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        0.40f
                )
        );

        titleView.setText(
                NumberFunctions.PerisanNumber(
                        safeText(
                                column.getColumnFieldValue("ColumnDesc")
                        )
                )
        );

        titleView.setGravity(
                Gravity.START | Gravity.CENTER_VERTICAL
        );

        titleView.setPaddingRelative(
                dp(7),
                0,
                dp(6),
                0
        );

        titleView.setMaxLines(1);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setTextColor(onBackgroundColor);
        titleView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                11
        );

        titleView.setTypeface(
                titleView.getTypeface(),
                Typeface.BOLD
        );

        titleView.setBackground(
                createSimpleBackground(
                        softPrimaryColor,
                        dp(6)
                )
        );

        row.addView(titleView);

        View divider = new View(mContext);

        LinearLayoutCompat.LayoutParams dividerParams =
                new LinearLayoutCompat.LayoutParams(
                        dp(1),
                        dp(28)
                );

        dividerParams.setMargins(
                dp(4),
                0,
                dp(4),
                0
        );

        divider.setLayoutParams(dividerParams);

        divider.setBackgroundColor(
                ColorUtils.setAlphaComponent(
                        primaryColor,
                        45
                )
        );

        row.addView(divider);

        EditText searchInput = new EditText(mContext);

        searchInput.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        0,
                        dp(38),
                        0.60f
                )
        );

        /*
         * در کد قبلی setId دو مرتبه انجام می‌شد
         * و مقدار اول بلافاصله از بین می‌رفت.
         */
        searchInput.setId(View.generateViewId());

        searchInput.setHint(
                safeText(
                        column.getColumnFieldValue("ColumnCode")
                )
        );

        searchInput.setText(
                safeText(
                        column.getColumnFieldValue("Condition")
                )
        );

        searchInput.setTag(column);
        searchInput.setBackgroundResource(R.drawable.bg_editbox);

        searchInput.setGravity(
                Gravity.START | Gravity.CENTER_VERTICAL
        );

        searchInput.setPaddingRelative(
                dp(9),
                0,
                dp(9),
                0
        );

        searchInput.setSingleLine(true);
        searchInput.setSelectAllOnFocus(true);
        searchInput.setTextDirection(
                View.TEXT_DIRECTION_FIRST_STRONG_RTL
        );

        searchInput.setTextColor(onBackgroundColor);

        searchInput.setHintTextColor(
                ColorUtils.setAlphaComponent(
                        onBackgroundColor,
                        130
                )
        );

        searchInput.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                12
        );

        searchInput.setInputType(
                InputType.TYPE_CLASS_TEXT
        );

        searchInput.setImeOptions(
                EditorInfo.IME_ACTION_NEXT
        );

        row.addView(searchInput);

        return row;
    }

    public void pro_c(String Goodtype) {

        try {
            Columns = broker_dbh.GetColumns("", Goodtype, "3");
        } catch (Exception E) {

        }


        layout_view.removeAllViews();
        layout_view.setOrientation(LinearLayoutCompat.VERTICAL);

        for (Column column : Columns) {

            column.setSearch("");

            int sortOrder = safeInt(
                    column.getColumnFieldValue("SortOrder"),
                    0
            );

            if (sortOrder <= 1) {
                continue;
            }

            LinearLayoutCompat row = createSearchRow(
                    column,
                    sortOrder
            );

            layout_view.addView(row);
        }


        btn_search = new MaterialButton(mContext);

        btn_search.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        btn_search.setText(NumberFunctions.PerisanNumber("اعمال فیلتر ها"));
        btn_search.setTextSize(12);
        btn_search.setTextColor(mContext.getColor(R.color.grey_1000));
        btn_search.setStrokeColor(ColorStateList.valueOf(mContext.getColor(R.color.grey_1000)));
        btn_search.setStrokeWidth(2);
        btn_search.setBackgroundTintList(ColorStateList.valueOf(mContext.getColor(R.color.white)));
        btn_search.setOnClickListener(v -> {
            for (int i = 0; i < layout_view.getChildCount(); i++) {
                if (layout_view.getChildAt(i) instanceof LinearLayoutCompat) {
                    LinearLayoutCompat LinearLayoutCompat = (androidx.appcompat.widget.LinearLayoutCompat) layout_view.getChildAt(i);
                    for (int j = 0; j < LinearLayoutCompat.getChildCount(); j++) {
                        if (LinearLayoutCompat.getChildAt(j) instanceof EditText) {
                            EditText et = (EditText) LinearLayoutCompat.getChildAt(j);
                            for (Column Column : Columns) {
                                if (et.getHint().toString().equals(Column.getColumnFieldValue("ColumnCode"))) {

                                    Column.setSearch(NumberFunctions.EnglishNumber(et.getText().toString()));
                                    Column.setCondition(NumberFunctions.EnglishNumber(et.getText().toString()));

                                    broker_dbh.UpdateSearchColumn(Column);

                                }
                            }
                        }
                    }
                }
            }
            Where = " And Replace(Replace(GoodType,char(1740),char(1610)),char(1705),char(1603))= Replace(Replace('" + Goodtype + "',char(1740),char(1610)),char(1705),char(1603)) ";
            for (Column Column : Columns) {

                String search =Column.getColumnFieldValue("search");
                search=search.replaceAll(" ", "%").replaceAll("'", "%");


                if (!Column.getColumnFieldValue("search").equals("")) {
                    if (Column.getColumnType().equals("0")) {
                        if (!Column.getColumnName().equals("")) {
                            if (!Column.getColumnFieldValue("columndefinition").equals(""))
                                Where = Where + " And Replace(Replace(" + Column.getColumnFieldValue("columndefinition") + ",char(1740),char(1610)),char(1705),char(1603)) Like '%" + broker_dbh.GetRegionText(search) + "%'  ";
                            else
                                Where = Where + " And Replace(Replace(" + Column.getColumnFieldValue("ColumnName") + ",char(1740),char(1610)),char(1705),char(1603)) Like '%" + broker_dbh.GetRegionText(search) + "%' ";
                        } else {
                            String search_condition = " Replace(Replace('%" + broker_dbh.GetRegionText(Column.getColumnFieldValue("search")) + "%',char(1740),char(1610)),char(1705),char(1603)) ";
                            Where = Where + " And " + Column.getColumnFieldValue("columndefinition");
                            Where = Where.replace("SearchCondition", search_condition);
                        }
                    } else {
                        if (!Column.getColumnName().equals("")) {
                            if (!Column.getColumnFieldValue("columndefinition").equals(""))
                                Where = Where + " And " + Column.getColumnFieldValue("columndefinition") + " Like '%" + broker_dbh.GetRegionText(search) + "%'  ";
                            else
                                Where = Where + " And " + Column.getColumnFieldValue("ColumnName") + " Like '%" + broker_dbh.GetRegionText(search) + "%' ";
                        } else {
                            String search_condition = " '%" + broker_dbh.GetRegionText(Column.getColumnFieldValue("search")) + "%' ";
                            Where = Where + " And " + Column.getColumnFieldValue("columndefinition");
                            Where = Where.replace("SearchCondition", search_condition);
                        }
                    }
                }
            }

            Broker_SearchActivity activity = (Broker_SearchActivity) mContext;

            activity.proSearchCondition = Where;
            activity.PageMoreData = "0";
            activity.goods.clear();
            activity.GetDataFromDataBase();
            dialog.dismiss();

        });
        layout_view.addView(btn_search);
    }


}
