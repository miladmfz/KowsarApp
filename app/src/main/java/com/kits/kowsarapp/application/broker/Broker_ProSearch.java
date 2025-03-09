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

import java.util.ArrayList;

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


    public void pro_c(String Goodtype) {

        try {
            Columns = broker_dbh.GetColumns("", Goodtype, "3");
        } catch (Exception E) {

        }


        for (Column Column : Columns) {

            Column.setSearch("");

            if (Integer.parseInt(Column.getColumnFieldValue("SortOrder")) > 1) {

                layout_view.setOrientation(LinearLayoutCompat.VERTICAL);
                LinearLayoutCompat layout_view_child = new LinearLayoutCompat(mContext);
                layout_view_child.setOrientation(LinearLayoutCompat.HORIZONTAL);
                layout_view_child.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                layout_view_child.setWeightSum(1);
                layout_view_child.setPadding(5, 5, 5, 5);

                TextView extra_TextView1 = new TextView(mContext);
                extra_TextView1.setText(NumberFunctions.PerisanNumber(Column.getColumnFieldValue("ColumnDesc")));
                extra_TextView1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 0.7));
                extra_TextView1.setTextSize(14);
                extra_TextView1.setPadding(2, 2, 2, 2);
                extra_TextView1.setGravity(Gravity.CENTER);
                layout_view_child.addView(extra_TextView1);

                EditText extra_EditText = new EditText(mContext);
                extra_EditText.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 0.3));
                extra_EditText.setTextSize(15);
                extra_EditText.setId(Integer.parseInt(Column.getColumnFieldValue("sortorder")));
                extra_EditText.setHint(Column.getColumnFieldValue("ColumnCode"));
                extra_EditText.setText(Column.getColumnFieldValue("Condition"));
                extra_EditText.setBackgroundResource(R.drawable.bg_editbox);
                extra_EditText.setId(View.generateViewId());
                extra_EditText.setPadding(2, 2, 2, 2);
                extra_EditText.setGravity(Gravity.CENTER);
                layout_view_child.addView(extra_EditText);


                layout_view.addView(layout_view_child);


            }
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
