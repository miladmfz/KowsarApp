package com.kits.kowsarapp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationResult;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.application.CallMethod;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {
    CallMethod callMethod;
    ArrayList<Column> columns;
    ArrayList<Good> goods;

    Cursor cursor;
    Column column;
    Good gooddetail;

    int limitcolumn;
    String query = "";
    String result = "";
    String Search_Condition = "";
    String SH_selloff;
    String SH_grid;
    String LimitAmount;
    String SH_delay;
    String SH_brokerstack;
    String SH_prefactor_code;
    String SH_prefactor_good;
    String SH_MenuBroker;
    boolean SH_activestack;
    boolean SH_real_amount;
    boolean SH_goodamount;
    boolean SH_ArabicText;
    int k = 0;

    String StackAmountString;
    String BrokerStackString;
    String joinDetail;
    String joinbasket;

    public DatabaseHelper(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        this.callMethod = new CallMethod(context);
        this.goods = new ArrayList<>();

    }


    public void GetLastDataFromOldDataBase(String tempDbPath) {

        getWritableDatabase().execSQL("ATTACH DATABASE '" + tempDbPath + "' AS tempDb");

        getWritableDatabase().execSQL("INSERT INTO main.Prefactor SELECT * FROM tempDb.Prefactor ");
        getWritableDatabase().execSQL("INSERT INTO main.PreFactorRow SELECT * FROM tempDb.PreFactorRow ");
        getWritableDatabase().execSQL("INSERT INTO main.Config SELECT * FROM tempDb.Config ");

        getWritableDatabase().execSQL("DETACH DATABASE 'tempDb' ");
        getWritableDatabase().close();

    }

    public void CreateActivationDb() {
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Activation (" +
                "AppBrokerCustomerCode TEXT," +
                "ActivationCode TEXT," +
                "PersianCompanyName TEXT," +
                "EnglishCompanyName TEXT," +
                "ServerURL TEXT," +
                "SQLiteURL TEXT," +
                "MaxDevice TEXT," +
                "SecendServerURL TEXT," +
                "DbName TEXT," +
                "AppType TEXT)");
        getWritableDatabase().close();
    }

    public void InitialConfigInsert() {


        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'BrokerCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'BrokerStack', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerStack')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'GroupCodeDefult', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerStack')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'MenuBroker', '0' Where Not Exists(Select * From Config Where KeyValue = 'MenuBroker')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'KsrImage_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'KsrImage_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'MaxRepLogCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'MaxRepLogCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'LastGpsLocationCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'LastGpsLocationCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'VersionInfo', '" + BuildConfig.VERSION_NAME + "' Where Not Exists(Select * From Config Where KeyValue = 'VersionInfo')");
        getWritableDatabase().close();
    }

    public void DatabaseCreate() {
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS GpsLocation (GpsLocationCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE ,Longitude TEXT, Latitude TEXT, Speed TEXT, BrokerRef TEXT, GpsDate TEXT)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS PreFactorRow (PreFactorRowCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE ,PreFactorRef INTEGER, GoodRef INTEGER, FactorAmount INTEGER, Shortage INTEGER, PreFactorDate TEXT,  Price INTEGER)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Prefactor ( PreFactorCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, PreFactorDate TEXT," +
                " PreFactorTime TEXT, PreFactorKowsarCode INTEGER, PreFactorKowsarDate TEXT, PreFactorExplain TEXT, CustomerRef INTEGER, BrokerRef INTEGER)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Config (ConfigCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, KeyValue TEXT , DataValue TEXT)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS BrokerColumn ( ColumnCode INTEGER PRIMARY KEY, SortOrder TEXT, ColumnName TEXT, ColumnDesc TEXT, GoodType TEXT, ColumnDefinition TEXT, ColumnType TEXT, Condition TEXT, OrderIndex TEXT, AppType INTEGER)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS GoodType ( GoodTypeCode INTEGER PRIMARY KEY, GoodType TEXT, IsDefault TEXT)");


        getWritableDatabase().execSQL("Create Index IF Not Exists IX_GoodGroup_GoodRef on GoodGroup (GoodRef,GoodGroupRef)");

        getWritableDatabase().execSQL("Create Index IF Not Exists IX_Prefactor_CustomerRef on Prefactor (CustomerRef)");

        getWritableDatabase().execSQL("Create Index IF Not Exists IX_PreFactorRow_GoodRef on PreFactorRow (GoodRef,PreFactorRef)");
        getWritableDatabase().execSQL("Create Index IF Not Exists IX_PreFactorRow_PreFactorRef on PreFactorRow (PreFactorRef)");

        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_GoodRef_stackref ON GoodStack (GoodRef,StackRef)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_GoodRef ON GoodStack (GoodRef)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_StackRef ON GoodStack (StackRef)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_Amount ON GoodStack (Amount)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_ReservedAmount ON GoodStack (ReservedAmount)");

        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_KsrImage_ObjectRef ON KsrImage (ObjectRef)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_KsrImage_IsDefaultImage ON KsrImage (IsDefaultImage)");

        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Customer_PriceTip ON Customer (PriceTip)");


        try {
            getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_JobRef ON JobPerson (JobRef)");
            getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_AddressRef ON JobPerson (AddressRef)");
            getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_CentralRef ON JobPerson (CentralRef)");
            getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_Good_JobPersonRef ON JobPerson_Good (JobPersonRef)");
            getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_Good_GoodRef ON JobPerson_Good (GoodRef)");

        } catch (Exception ignored) {
        }

        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodName ON Good (GoodName)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodMainCode ON Good (GoodMainCode)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain1 ON Good (GoodExplain1)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain2 ON Good (GoodExplain2)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain3 ON Good (GoodExplain3)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain4 ON Good (GoodExplain4)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain5 ON Good (GoodExplain5)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain6 ON Good (GoodExplain6)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_SellPriceType ON Good (SellPriceType)");
        getWritableDatabase().execSQL("Create Index IF Not Exists IX_Good_GoodUnitRef on Good (GoodUnitRef)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar1 ON Good (Nvarchar1)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar2 ON Good (Nvarchar2)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar3 ON Good (Nvarchar3)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar4 ON Good (Nvarchar4)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar5 ON Good (Nvarchar5)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar6 ON Good (Nvarchar6)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar7 ON Good (Nvarchar7)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar8 ON Good (Nvarchar8)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar9 ON Good (Nvarchar9)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar10 ON Good (Nvarchar10)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar11 ON Good (Nvarchar11)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar12 ON Good (Nvarchar12)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar13 ON Good (Nvarchar13)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar14 ON Good (Nvarchar14)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar15 ON Good (Nvarchar15)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar16 ON Good (Nvarchar16)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar17 ON Good (Nvarchar17)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar18 ON Good (Nvarchar18)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar19 ON Good (Nvarchar19)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar20 ON Good (Nvarchar20)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Float1 ON Good (Float1)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Float2 ON Good (Float2)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Float3 ON Good (Float3)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Float4 ON Good (Float4)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Float5 ON Good (Float5)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Date1 ON Good (Date1)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Date2 ON Good (Date2)");
        getWritableDatabase().execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Text1 ON Good (Text1)");
        getWritableDatabase().close();

    }


    public void closedb() {
        getWritableDatabase().close();
    }

    @SuppressLint("Range")
    public ArrayList<ReplicationModel> GetReplicationTable() {
        query = "SELECT * from ReplicationTable";
        ArrayList<ReplicationModel> replicationModels = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ReplicationModel ReplicationModel = new ReplicationModel();
                try {

                    ReplicationModel.setReplicationCode(cursor.getInt(cursor.getColumnIndex("ReplicationCode")));
                    ReplicationModel.setServerTable(cursor.getString(cursor.getColumnIndex("ServerTable")));
                    ReplicationModel.setClientTable(cursor.getString(cursor.getColumnIndex("ClientTable")));
                    ReplicationModel.setServerPrimaryKey(cursor.getString(cursor.getColumnIndex("ServerPrimaryKey")));
                    ReplicationModel.setClientPrimaryKey(cursor.getString(cursor.getColumnIndex("ClientPrimaryKey")));
                    ReplicationModel.setCondition(cursor.getString(cursor.getColumnIndex("Condition")));
                    ReplicationModel.setConditionDelete(cursor.getString(cursor.getColumnIndex("ConditionDelete")));
                    ReplicationModel.setLastRepLogCode(cursor.getInt(cursor.getColumnIndex("LastRepLogCode")));
                    ReplicationModel.setLastRepLogCodeDelete(cursor.getInt(cursor.getColumnIndex("LastRepLogCodeDelete")));


                } catch (Exception ignored) {
                }
                replicationModels.add(ReplicationModel);
            }
        }
        assert cursor != null;
        cursor.close();
        return replicationModels;
    }


    @SuppressLint("Range")
    public ArrayList<TableDetail> GetTableDetail(String TableName) {
        query = "PRAGMA table_info( " + TableName + " )";
        ArrayList<TableDetail> tableDetails = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                TableDetail tableDetail = new TableDetail();
                try {
                    tableDetail.setCid(cursor.getInt(cursor.getColumnIndex("cid")));
                    tableDetail.setName(cursor.getString(cursor.getColumnIndex("name")));
                    tableDetail.setType(cursor.getString(cursor.getColumnIndex("type")));
                    tableDetail.setText(null);
                } catch (Exception ignored) {
                }
                tableDetails.add(tableDetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return tableDetails;
    }

    @SuppressLint("Range")
    public void GetLimitColumn(String AppType) {

        try {
            query = "select Count(*) count from GoodType ";

            cursor = getWritableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            String goodtypecount = cursor.getString(cursor.getColumnIndex("count"));
            cursor.close();
            query = "select Count(*) count from BrokerColumn Where Replace(Replace(AppType,char(1740),char(1610)),char(1705),char(1603)) = Replace(Replace('" + AppType + "',char(1740),char(1610)),char(1705),char(1603))";

            cursor = getWritableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            String columnscount = cursor.getString(cursor.getColumnIndex("count"));
            cursor.close();
            limitcolumn = Integer.parseInt(columnscount) / Integer.parseInt(goodtypecount);
        } catch (Exception e) {
            callMethod.showToast("تنظیم جدول از سمت دیتابیس مشکل دارد");
            callMethod.Log(e.getMessage());
        }
    }

    public void GetPreference() {


        this.SH_brokerstack = ReadConfig("BrokerStack");
        this.SH_MenuBroker = ReadConfig("MenuBroker");
        this.SH_selloff = callMethod.ReadString("SellOff");
        this.SH_grid = callMethod.ReadString("Grid");

        this.SH_delay = callMethod.ReadString("Delay");
        this.SH_prefactor_code = callMethod.ReadString("PreFactorCode");
        this.SH_prefactor_good = callMethod.ReadString("PreFactorGood");
        this.SH_activestack = callMethod.ReadBoolan("ActiveStack");
        this.SH_real_amount = callMethod.ReadBoolan("RealAmount");
        this.SH_goodamount = callMethod.ReadBoolan("GoodAmount");
        this.SH_ArabicText = callMethod.ReadBoolan("ArabicText");
        LimitAmount = String.valueOf(Integer.parseInt(SH_grid) * 11);


        BrokerStackString = "Where StackRef in (" + SH_brokerstack + ")";
        StackAmountString = "";


        joinbasket = " FROM Good g " +
                " Join Units on UnitCode =GoodUnitRef " +
                " Left Join (Select GoodRef, Sum(FactorAmount) FactorAmount , Sum(FactorAmount*Price) Price " +
                " From PreFactorRow Where PreFactorRef = " + SH_prefactor_code + " Group BY GoodRef) pf on pf.GoodRef = g.GoodCode  " +
                " Left Join PreFactor h on h.PreFactorCode = " + SH_prefactor_code +
                " Left Join Customer c on c.CustomerCode=h.CustomerRef ";

        joinDetail = " FROM Good g ,FilterTable Join Units u on u.UnitCode = g.GoodUnitRef " +
                " Left Join CacheGoodGroup cgg on cgg.GoodRef = g.Goodcode ";


    }

    @SuppressLint("Range")
    public String GetGoodTypeFromGood(String code) {
        query = "select GoodType from good where GoodCode = " + code;
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("GoodType"));
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public ArrayList<Column> GetColumns(String code, String goodtype, @NonNull String AppType) {

        switch (AppType) {
            case "0"://        0-detail

                query = "Select * from BrokerColumn where Replace(Replace(GoodType,char(1740),char(1610)),char(1705),char(1603)) = '" + GetRegionText(GetGoodTypeFromGood(code)) + "' And AppType = 0";

                break;
            case "1"://        1-list
            case "2"://        2-basket
                GetLimitColumn(AppType);
                query = "Select * from BrokerColumn where AppType = " + AppType + " limit " + limitcolumn;
                break;
            case "3"://        3-search

                query = "Select * from BrokerColumn where Replace(Replace(GoodType,char(1740),char(1610)),char(1705),char(1603)) = '" + GetRegionText(goodtype) + "' And AppType = 3";

                break;
        }

        callMethod.Log(query);
        columns = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Column column = new Column();
                try {
                    column.setColumnCode(cursor.getString(cursor.getColumnIndex("ColumnCode")));
                    column.setSortOrder(cursor.getString(cursor.getColumnIndex("SortOrder")));
                    column.setColumnName(cursor.getString(cursor.getColumnIndex("ColumnName")));
                    column.setColumnDesc(cursor.getString(cursor.getColumnIndex("ColumnDesc")));
                    column.setGoodType(cursor.getString(cursor.getColumnIndex("GoodType")));
                    column.setColumnType(cursor.getString(cursor.getColumnIndex("ColumnType")));
                    column.setColumnDefinition(cursor.getString(cursor.getColumnIndex("ColumnDefinition")));
                    column.setCondition(cursor.getString(cursor.getColumnIndex("Condition")));
                    column.setOrderIndex(cursor.getString(cursor.getColumnIndex("OrderIndex")));
                } catch (Exception ignored) {
                }
                columns.add(column);
            }
        }
        assert cursor != null;
        cursor.close();
        return columns;
    }

    @SuppressLint("Range")
    public String GetColumnscount() {

        query = "Select Count(*) result from BrokerColumn ";
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        int result = cursor.getInt(cursor.getColumnIndex("result"));
        cursor.close();

        return String.valueOf(result);
    }

    @SuppressLint("Range")
    public String GetRegionText(String String) {


        query = "Select Replace(Replace(Cast('" + String + "' as nvarchar(500)),char(1740),char(1610)),char(1705),char(1603)) result  ";

        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex("result"));
        cursor.close();

        return result;
    }


    @SuppressLint("Range")
    public ArrayList<Column> GetAllGoodType() {
        query = "Select * from GoodType ";
        columns = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                column = new Column();
                try {
                    column.setGoodType(cursor.getString(cursor.getColumnIndex("GoodType")));
                    column.setIsDefault(cursor.getString(cursor.getColumnIndex("IsDefault")));
                } catch (Exception ignored) {
                }
                columns.add(column);
            }
        }
        assert cursor != null;
        cursor.close();
        return columns;
    }


    @SuppressLint({"Recycle", "Range"})
    public ArrayList<Good> getAllGood(String search_target, String aGroupCode, String MoreCallData) {
        goods.clear();
        GetPreference();


        columns = GetColumns("", "", "1");

        String search = GetRegionText(search_target);
        search = search.replaceAll(" ", "%").replaceAll("'", "%");

        Search_Condition = " '%" + search + "%' ";

        query = " With FilterTable As (Select 0 as SecondField) SELECT ";

        k = 0;

        for (Column column : columns) {

            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }

            if (!column.getColumnName().equals("")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (!column.getColumnDefinition().equals("")) {
                    query = query + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    query = query + column.getColumnName();
                }
                k++;
            }
        }

        query = query + " FROM Good g , FilterTable ";
        k = 0;
        boolean digitsOnly = TextUtils.isDigitsOnly(search);
        if (!search.equals("")) {
            for (Column column : columns) {


                if (!(!column.getColumnType().equals("0") && !digitsOnly)) {
                    if (Integer.parseInt(column.getColumnFieldValue("SortOrder")) > 0 && Integer.parseInt(column.getColumnFieldValue("SortOrder")) < 10) {
                        if (k == 0) {
                            query = query + " Where (";
                        } else {
                            query = query + " or ";
                        }
                        if (column.getColumnType().equals("0")) {
                            query = query + "Replace(Replace(" + column.getColumnName() + ",char(1740),char(1610)),char(1705),char(1603)) Like '%" + search + "%' ";
                        } else {
                            query = query + column.getColumnName() + " Like '%" + search + "%' ";
                        }
                        k++;
                    }
                }

            }

            for (Column column : columns) {
                if (column.getColumnType().equals("")) {
                    query = query + " or " + column.getColumnDefinition();
                }
            }

            query = query + " )";
        } else {
            query = query + "where 1=1 ";
        }

        query = query + " And Exists(Select 1 From GoodStack stackCondition ActiveCondition And GoodRef=GoodCode AmountCondition)";


        if (SH_activestack) {
            query = query.replaceAll("ActiveCondition", " And ActiveStack = 1 ");
        } else {
            query = query.replaceAll("ActiveCondition", " ");
        }

        if (SH_goodamount) {
            query = query.replaceAll("AmountCondition", " GROUP BY GoodRef HAVING " + StackAmountString + " > 0 ");
        } else {
            query = query.replaceAll("AmountCondition", " ");
        }

        query = query.replaceAll("stackCondition", BrokerStackString);

        query = query.replaceAll("SearchCondition", Search_Condition);


        try{
            Integer.parseInt(aGroupCode);
        }catch (Exception e){
            aGroupCode="0";
        }
        if (Integer.parseInt(aGroupCode) > 0) {
            query = query + " And GoodCode in(Select GoodRef From GoodGroup p "
                    + "Join GoodsGrp s on p.GoodGroupRef = s.GroupCode "
                    + "Where s.GroupCode = " + aGroupCode + " or s.L1 = " + aGroupCode
                    + " or s.L2 = " + aGroupCode + " or s.L3 = " + aGroupCode
                    + " or s.L4 = " + aGroupCode + " or s.L5 = " + aGroupCode + ")";
        }
        query = query + " order by ";
        int k = 0;
        for (Column column : columns) {
            if (!column.getOrderIndex().equals("0")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Then") + 5, column.getColumnDefinition().indexOf("Then") + 12);
                        query = query + newSt;
                        //Case When SecondField=1 Then g.Date2 Else g.Date2 End
                        //query = query + column.getColumnName();
                    } else {
                        query = query + column.getColumnName();
                    }
                } else {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Then") + 5, column.getColumnDefinition().indexOf("Then") + 12);
                        query = query + newSt + " DESC ";
                    } else {
                        query = query + column.getColumnName() + " DESC ";
                    }
                }
                k++;
            }
        }

        query = query + " LIMIT  " + LimitAmount;
        query = query + " OFFSET " + (Integer.parseInt(LimitAmount) * Integer.parseInt(MoreCallData));
        cursor = getWritableDatabase().rawQuery(query, null);
        callMethod.Log(query);

        if (cursor != null) {

            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {

                    try {
                        switch (column.getColumnType()) {
                            case "0":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        cursor.getString(cursor.getColumnIndex(column.getColumnName()))
                                );
                                break;
                            case "1":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                            case "2":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                        }
                    } catch (Exception ignored) {
                    }
                }
                gooddetail.setCheck(false);
                gooddetail.setGoodFieldValue("ActiveStack", String.valueOf(cursor.getInt(cursor.getColumnIndex("ActiveStack"))));

                goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();


        return goods;
    }

    @SuppressLint("Range")
    public ArrayList<Good> getAllGood_Extended(String searchbox_result, String aGroupCode, String MoreCallData) {
        goods.clear();
        GetPreference();
        columns = GetColumns("", "", "1");


        query = "With FilterTable As (Select 0 as SecondField) SELECT ";
        k = 0;
        for (Column column : columns) {
            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }
            if (!column.getColumnName().equals("")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (!column.getColumnDefinition().equals("")) {
                    query = query + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    query = query + column.getColumnName();
                }
                k++;
            }
        }


        query = query + " FROM Good g , FilterTable ";

        query = query + " Where  1=1 ";

        query = query + searchbox_result;

        query = query + " And Exists(Select 1 From GoodStack stackCondition ActiveCondition And GoodRef=GoodCode AmountCondition)";


        if (SH_activestack) {
            query = query.replaceAll("ActiveCondition", " And ActiveStack = 1 ");
        } else {
            query = query.replaceAll("ActiveCondition", " ");
        }

        if (SH_goodamount) {
            query = query.replaceAll("AmountCondition", " GROUP BY GoodRef HAVING " + StackAmountString + " > 0 ");
        } else {
            query = query.replaceAll("AmountCondition", " ");
        }

        query = query.replaceAll("stackCondition", BrokerStackString);

        query = query.replaceAll("SearchCondition", Search_Condition);


        if (Integer.parseInt(aGroupCode) > 0) {
            query = query + " And GoodCode in(Select GoodRef From GoodGroup p "
                    + "Join GoodsGrp s on p.GoodGroupRef = s.GroupCode "
                    + "Where s.GroupCode = " + aGroupCode + " or s.L1 = " + aGroupCode
                    + " or s.L2 = " + aGroupCode + " or s.L3 = " + aGroupCode
                    + " or s.L4 = " + aGroupCode + " or s.L5 = " + aGroupCode + ")";
        }


        query = query + " order by ";
        int k = 0;
        for (Column column : columns) {
            if (!column.getOrderIndex().equals("0")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Then") + 5, column.getColumnDefinition().indexOf("Then") + 12);
                        query = query + newSt;
                    } else {
                        query = query + column.getColumnName();
                    }
                } else {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Then") + 5, column.getColumnDefinition().indexOf("Then") + 12);
                        query = query + newSt + " DESC ";
                    } else {
                        query = query + column.getColumnName() + " DESC ";
                    }
                }
                k++;
            }
        }
        query = query + " LIMIT  " + LimitAmount;
        query = query + " OFFSET " + (Integer.parseInt(LimitAmount) * Integer.parseInt(MoreCallData));

        callMethod.Log(query);
        cursor = getWritableDatabase().rawQuery(query, null);

        callMethod.Log(query);
        if (cursor != null) {

            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {

                    try {
                        switch (column.getColumnType()) {
                            case "0":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        cursor.getString(cursor.getColumnIndex(column.getColumnName()))
                                );
                                break;
                            case "1":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                            case "2":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                        }
                    } catch (Exception ignored) {
                    }
                }
                gooddetail.setCheck(false);
                gooddetail.setGoodFieldValue("ActiveStack", cursor.getString(cursor.getColumnIndex("ActiveStack")));
                goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return goods;
    }

    @SuppressLint("Range")
    public ArrayList<Good> getAllGood_ByDate(String xDayAgo, String MoreCallData) {

        goods.clear();
        GetPreference();
        columns = GetColumns("", "", "1");
        query = "  With FilterTable As (Select 1 as SecondField) SELECT ";
        k = 0;
        for (Column column : columns) {
            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }
            if (!column.getColumnName().equals("")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (!column.getColumnDefinition().equals("")) {
                    query = query + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    query = query + column.getColumnName();
                }
                k++;
            }
        }

        String newSt = "Date";
        for (Column column : columns) {

            if (column.getColumnName().equals("Date")) {
                newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Else") + 4, column.getColumnDefinition().indexOf("Else") + 12);
            }
        }


        query = query + " FROM Good g , FilterTable Where " + newSt + ">='" + xDayAgo + "' ";

        query = query + " And Exists(Select 1 From GoodStack stackCondition ActiveCondition And GoodRef=GoodCode AmountCondition)";


        if (SH_activestack) {
            query = query.replaceAll("ActiveCondition", " And ActiveStack = 1 ");
        } else {
            query = query.replaceAll("ActiveCondition", " ");
        }

        if (SH_goodamount) {
            query = query.replaceAll("AmountCondition", " GROUP BY GoodRef HAVING " + StackAmountString + " > 0 ");
        } else {
            query = query.replaceAll("AmountCondition", " ");
        }

        query = query.replaceAll("stackCondition", BrokerStackString);


        query = query + " order by ";
        int k = 0;
        for (Column column : columns) {
            if (!column.getOrderIndex().equals("0")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    if (column.getColumnName().equals("Date")) {
                        newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Else") + 4, column.getColumnDefinition().indexOf("Else") + 12);
                        query = query + newSt;
                    } else {
                        query = query + column.getColumnName();
                    }
                } else {
                    if (column.getColumnName().equals("Date")) {
                        newSt = column.getColumnDefinition().substring(column.getColumnDefinition().indexOf("Else") + 4, column.getColumnDefinition().indexOf("Else") + 12);
                        query = query + newSt + " DESC ";
                    } else {
                        query = query + column.getColumnName() + " DESC ";
                    }
                }
                k++;
            }
        }
        query = query + " LIMIT  " + LimitAmount;
        query = query + " OFFSET " + (Integer.parseInt(LimitAmount) * Integer.parseInt(MoreCallData));

        callMethod.Log(query);
        goods = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {
                    try {
                        switch (column.getColumnType()) {
                            case "0":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        cursor.getString(cursor.getColumnIndex(column.getColumnName()))
                                );
                                break;
                            case "1":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                            case "2":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                        }
                    } catch (Exception ignored) {
                    }
                }

                gooddetail.setCheck(false);
                gooddetail.setGoodFieldValue("ActiveStack", cursor.getString(cursor.getColumnIndex("ActiveStack")));
                goods.add(gooddetail);
            }
        }

        assert cursor != null;
        cursor.close();
        return goods;
    }


    @SuppressLint("Range")
    public Good getGoodByCode(String code) {
        GetPreference();
        columns = GetColumns(code, "", "0");

        query = "With FilterTable As (Select 0 as SecondField) SELECT ";
        k = 0;
        for (Column column : columns) {
            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }
            if (!column.getColumnName().equals("ksrImageCode")) {
                if (k != 0) {
                    query = query + " , ";
                }
                if (!column.getColumnDefinition().equals("")) {
                    query = query + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    query = query + column.getColumnName();
                }
                k++;
            }
        }
        query = query + joinDetail;
        Search_Condition = "'%%'";


        query = query.replaceAll("stackCondition", BrokerStackString);
        query = query.replaceAll("SearchCondition", Search_Condition);


        query = query + " WHERE GoodCode = " + code;

        callMethod.Log(query);
        gooddetail = new Good();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (Column column : columns) {
                try {
                    switch (column.getColumnType()) {
                        case "0":
                            gooddetail.setGoodFieldValue(
                                    column.getColumnName(),
                                    cursor.getString(cursor.getColumnIndex(column.getColumnName()))
                            );
                            break;
                        case "1":
                            gooddetail.setGoodFieldValue(
                                    column.getColumnName(),
                                    String.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumnName())))
                            );
                            break;
                        case "2":
                            gooddetail.setGoodFieldValue(
                                    column.getColumnName(),
                                    String.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumnName())))
                            );
                            break;
                    }
                } catch (Exception ignored) {
                }
            }
            gooddetail.setCheck(false);
            gooddetail.setGoodFieldValue("ActiveStack", cursor.getString(cursor.getColumnIndex("ActiveStack")));

        }
        cursor.close();
        return gooddetail;
    }


    public void InsertActivation(@NotNull Activation activation) {

        query = "select * from Activation Where ActivationCode= '" + activation.getActivationCode() + "'";
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            getWritableDatabase().execSQL("Update Activation set " +
                    "ServerURL = '" + activation.getServerURL() + "' " +
                    "Where ActivationCode= '" + activation.getActivationCode() + "'");

            getWritableDatabase().execSQL("Update Activation set " +
                    "SQLiteURL = '" + activation.getSQLiteURL() + "' " +
                    "Where ActivationCode= '" + activation.getActivationCode() + "'");

        } else {
            getWritableDatabase().execSQL(" Insert Into Activation(AppBrokerCustomerCode,ActivationCode,PersianCompanyName, EnglishCompanyName,ServerURL,SQLiteURL,MaxDevice,SecendServerURL,DbName,AppType)" +
                    " Select '" + activation.getAppBrokerCustomerCode() + "','" + activation.getActivationCode() + "','" +
                    activation.getPersianCompanyName() + "','" + activation.getEnglishCompanyName() + "','" +
                    activation.getServerURL() + "','" + activation.getSQLiteURL() + "','" + activation.getMaxDevice() + "','" + activation.getSecendServerURL() + "','" + activation.getDbName() + "','" + activation.getAppType() + "'");

        }


    }


    @SuppressLint("Range")
    public String GetActivationCode() {

        query = " select * from Activation Where EnglishCompanyName='"+callMethod.ReadString("EnglishCompanyNameUse")+"'";
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = String.valueOf(cursor.getInt(cursor.getColumnIndex("ActivationCode")));
        }
        cursor.close();
        return result;
    }




























    @SuppressLint("Range")
    public ArrayList<Activation> getActivation() {
        callMethod.Log("db=start");
        query = "Select * From Activation";
        cursor = getWritableDatabase().rawQuery(query, null);
        ArrayList<Activation> activations = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Activation activation = new Activation();
                try {
                    activation.setAppBrokerCustomerCode(cursor.getString(cursor.getColumnIndex("AppBrokerCustomerCode")));
                    activation.setActivationCode(cursor.getString(cursor.getColumnIndex("ActivationCode")));
                    activation.setPersianCompanyName(cursor.getString(cursor.getColumnIndex("PersianCompanyName")));
                    activation.setEnglishCompanyName(cursor.getString(cursor.getColumnIndex("EnglishCompanyName")));
                    activation.setServerURL(cursor.getString(cursor.getColumnIndex("ServerURL")));
                    activation.setSQLiteURL(cursor.getString(cursor.getColumnIndex("SQLiteURL")));
                    activation.setMaxDevice(cursor.getString(cursor.getColumnIndex("MaxDevice")));
                    activation.setSecendServerURL(cursor.getString(cursor.getColumnIndex("SecendServerURL")));
                    activation.setDbName(cursor.getString(cursor.getColumnIndex("DbName")));
                    activation.setAppType(cursor.getString(cursor.getColumnIndex("AppType")));
                } catch (Exception ignored) {
                    callMethod.Log("db="+ignored.getMessage());
                }
                activations.add(activation);

            }
        }
        assert cursor != null;
        cursor.close();
        return activations;
    }

    @SuppressLint("Range")
    public Good getGoodBuyBox(String code) {
        GetPreference();

        query = " SELECT IfNull(pf.FactorAmount,0) as FactorAmount ,  DefaultUnitValue,  UnitName ," +
                " IfNull(pf.Price,0) as Price , SellPriceType, MaxSellPrice ," +
                " Case c.PriceTip When 1 Then  SellPrice1 When 2 Then SellPrice2 When 3 Then SellPrice3 " +
                " When 4 Then SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 Else " +
                " Case When g.SellPriceType = 0 Then MaxSellPrice Else 100 End End *  " +
                " Case When g.SellPriceType = 0 Then 1 Else MaxSellPrice/100 End as SellPrice " +
                " FROM Good g " +
                " Join Units on UnitCode =GoodUnitRef " +
                " Left Join (Select GoodRef, Sum(FactorAmount) FactorAmount , Sum(FactorAmount*Price) Price " +
                " From PreFactorRow Where PreFactorRef = " + SH_prefactor_code + " Group BY GoodRef) pf on pf.GoodRef = g.GoodCode  " +
                " Left Join PreFactor h on h.PreFactorCode = " + SH_prefactor_code +
                " Left Join Customer c on c.CustomerCode=h.CustomerRef " +
                " WHERE GoodCode = " + code;


        callMethod.Log(query);
        gooddetail = new Good();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                gooddetail.setGoodFieldValue("FactorAmount", cursor.getString(cursor.getColumnIndex("FactorAmount")));
                gooddetail.setGoodFieldValue("UnitName", cursor.getString(cursor.getColumnIndex("UnitName")));
                gooddetail.setGoodFieldValue("Price", cursor.getString(cursor.getColumnIndex("Price")));
                gooddetail.setGoodFieldValue("MaxSellPrice", cursor.getLong(cursor.getColumnIndex("MaxSellPrice")) + "");
                gooddetail.setGoodFieldValue("SellPrice", cursor.getLong(cursor.getColumnIndex("SellPrice")) + "");
                gooddetail.setGoodFieldValue("SellPriceType", cursor.getLong(cursor.getColumnIndex("SellPriceType")) + "");
                gooddetail.setGoodFieldValue("DefaultUnitValue", cursor.getLong(cursor.getColumnIndex("DefaultUnitValue")) + "");
            } catch (Exception ignored) {
            }
        }
        cursor.close();
        return gooddetail;
    }

    @SuppressLint("Range")
    public Good getGooddata(String code) {
        GetPreference();

        query = " SELECT * FROM Good g WHERE GoodCode = " + code;


        callMethod.Log(query);
        Good good_data = new Good();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            good_data.setGoodFieldValue("GoodCode", cursor.getString(cursor.getColumnIndex("GoodCode")));
            good_data.setGoodFieldValue("SellPriceType", cursor.getString(cursor.getColumnIndex("SellPriceType")));
            good_data.setGoodFieldValue("MaxSellPrice", cursor.getString(cursor.getColumnIndex("MaxSellPrice")));
            good_data.setGoodFieldValue("MinSellPrice", cursor.getString(cursor.getColumnIndex("MinSellPrice")));
            good_data.setGoodFieldValue("SellPrice1", cursor.getString(cursor.getColumnIndex("SellPrice1")));
            good_data.setGoodFieldValue("SellPrice2", cursor.getString(cursor.getColumnIndex("SellPrice2")));
            good_data.setGoodFieldValue("SellPrice3", cursor.getString(cursor.getColumnIndex("SellPrice3")));
            good_data.setGoodFieldValue("SellPrice4", cursor.getString(cursor.getColumnIndex("SellPrice4")));
            good_data.setGoodFieldValue("SellPrice5", cursor.getString(cursor.getColumnIndex("SellPrice5")));
            good_data.setGoodFieldValue("SellPrice6", cursor.getString(cursor.getColumnIndex("SellPrice6")));
            good_data.setGoodFieldValue("GoodUnitRef", cursor.getString(cursor.getColumnIndex("GoodUnitRef")));
            good_data.setGoodFieldValue("DefaultUnitValue", cursor.getString(cursor.getColumnIndex("DefaultUnitValue")));

        }
        cursor.close();

        return good_data;
    }

    public ArrayList<Good> getAllGood_pfcode() {
        goods.clear();
        GetPreference();
        query = "SELECT * from PrefactorRow  Where ifnull(PreFactorRef,0)= " + SH_prefactor_code;
        goods = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                gooddetail = new Good();
                gooddetail.setCheck(false);
                goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return goods;
    }

    @SuppressLint("Range")
    public void InsertPreFactorHeader(String Search_target, String CustomerRef) {
        String Customer = GetRegionText(Search_target);

        String Date = Utilities.getCurrentShamsidate();
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strDate = sdf.format(calendar.getTime());

        UserInfo user = new UserInfo();
        query = "Select * From Config Where KeyValue = 'BrokerCode' ";
        String key;
        String val = "";
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                key = cursor.getString(cursor.getColumnIndex("KeyValue"));
                val = cursor.getString(cursor.getColumnIndex("DataValue"));
                switch (key) {
                    case "ActiveCode":
                        user.setActiveCode(val);
                        break;
                    case "BrokerCode":
                        user.setBrokerCode(val);
                        break;

                }
            }
        }
        getWritableDatabase().execSQL("INSERT INTO Prefactor" +
                "(PreFactorKowsarCode,PreFactorDate ,PreFactorKowsarDate ,PreFactorTime,PreFactorExplain,CustomerRef,BrokerRef) " +
                "VALUES(0,'" + Date + "','-----','" + strDate + "','" + Customer + "','" + CustomerRef + "','" + val + "'); ");
    }

    @SuppressLint("Range")
    public void InsertPreFactor(String pfcode, String goodcode, String FactorAmount, String price, String BasketFlag) {
        if (Integer.parseInt(BasketFlag) > 0) {
            if (Float.parseFloat(price) >= 0) {
                query = "Update PreFactorRow set FactorAmount = " + FactorAmount + ", Price = " + price + " Where PreFactorRowCode=" + BasketFlag;
            } else {
                query = "Update PreFactorRow set FactorAmount = " + FactorAmount + " Where PreFactorRowCode=" + BasketFlag;
            }
            getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
        } else {
            query = " Select * From PreFactorRow Where IfNull(PreFactorRef,0)=" + pfcode + " And GoodRef =" + goodcode;
            if (Float.parseFloat(price) >= 0) {
                query = query + " And Price =" + price;
            }
            cursor = getWritableDatabase().rawQuery(query, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                getWritableDatabase().execSQL("Update PreFactorRow set FactorAmount = FactorAmount +" + FactorAmount + " Where PreFactorRowCode=" + cursor.getString(cursor.getColumnIndex("PreFactorRowCode")) + ";");
            } else {
                query = "INSERT INTO PreFactorRow(PreFactorRef, GoodRef, FactorAmount, Price) "
                        //Select " + pfcode + "," + goodcode + ", " + FactorAmount + "," +price
                        + "select PreFactorCode ,GoodCode," + FactorAmount + ", Case When " + price + ">0 Then " + price
                        + " When g.SellPrice1>0 And c.PriceTip= 1 Then Case When g.SellPriceType = 0 Then g.SellPrice1 Else g.SellPrice1 * g.MaxSellPrice /100 End "
                        + " When g.SellPrice2>0 And c.PriceTip= 2 Then Case When g.SellPriceType = 0 Then g.SellPrice2 Else g.SellPrice2 * g.MaxSellPrice /100 End "
                        + " When g.SellPrice3>0 And c.PriceTip= 3 Then Case When g.SellPriceType = 0 Then g.SellPrice3 Else g.SellPrice3 * g.MaxSellPrice /100 End "
                        + " When g.SellPrice4>0 And c.PriceTip= 4 Then Case When g.SellPriceType = 0 Then g.SellPrice4 Else g.SellPrice4 * g.MaxSellPrice /100 End "
                        + " When g.SellPrice5>0 And c.PriceTip= 5 Then Case When g.SellPriceType = 0 Then g.SellPrice5 Else g.SellPrice5 * g.MaxSellPrice /100 End "
                        + " When g.SellPrice6>0 And c.PriceTip= 6 Then Case When g.SellPriceType = 0 Then g.SellPrice6 Else g.SellPrice6 * g.MaxSellPrice /100 End "
                        + " Else MaxSellPrice End "
                        + " From PreFactor p Join Customer c on p.CustomerRef = c.CustomerCode "
                        + " Join Good g on GoodCode=" + goodcode
                        + " Where PreFactorCode=" + pfcode + " Limit 1 ";

//                query = "INSERT INTO PreFactorRow(PreFactorRef, GoodRef, FactorAmount, Price) Select " + pfcode + "," + goodcode + ", " + FactorAmount + "," +
//                        "Case When " + price + ">=0 Then " + price + " Else Case PriceTip When 1 Then SellPrice1 When 2 Then SellPrice2 When 3 Then SellPrice3 When 4 Then SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 End" +
//                        "* Case When SellPriceType = 1 Then MaxSellPrice/100 Else 1 End End " +
//                        "From Good g Join PreFactor h on 1=1 Join Customer c on h.CustomerRef=c.CustomerCode " +
//                        "Where h.PreFactorCode =" + pfcode + " And GoodCode = " + goodcode;
                callMethod.Log(query);
                getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
            }
            cursor.close();
        }
    }


    @SuppressLint("Range")
    public void InsertPreFactorwithPercent(String pfcode, String goodcode, String FactorAmount, String price, String BasketFlag) {
        if (Integer.parseInt(BasketFlag) > 0) {
            if (Float.parseFloat(price) >= 0) {
                query = "Update PreFactorRow set FactorAmount = " + FactorAmount + ", Price = " + price + " Where PreFactorRowCode=" + BasketFlag;
            } else {
                query = "Update PreFactorRow set FactorAmount = " + FactorAmount + " Where PreFactorRowCode=" + BasketFlag;
            }
            getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
        } else {
            query = " Select * From PreFactorRow Where IfNull(PreFactorRef,0)=" + pfcode + " And GoodRef =" + goodcode;
            if (Float.parseFloat(price) >= 0) {
                query = query + " And Price =" + price;
            }
            cursor = getWritableDatabase().rawQuery(query, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                getWritableDatabase().execSQL("Update PreFactorRow set FactorAmount = FactorAmount +" + FactorAmount + " Where PreFactorRowCode=" + cursor.getString(cursor.getColumnIndex("PreFactorRowCode")) + ";");
            } else {
                query = "INSERT INTO PreFactorRow(PreFactorRef, GoodRef, FactorAmount, Price) "
                        + "select PreFactorCode ,GoodCode," + FactorAmount + "," + price
                        + " From PreFactor "
                        + " Join Good g on GoodCode=" + goodcode
                        + " Where PreFactorCode=" + pfcode + " Limit 1 ";


                callMethod.Log(query);
                getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
            }
            cursor.close();
        }
    }


    @SuppressLint("Range")
    public ArrayList<PreFactor> getAllPrefactorHeader(String Search_target) {

        String name = GetRegionText(Search_target);

        query = " SELECT h.*, s.SumAmount , s.SumPrice , s.RowCount ,n.Title || ' ' || n.FName|| ' ' || n.Name CustomerName FROM PreFactor h Join Customer c  on c.CustomerCode = h.CustomerRef " +
                " join Central n on c.CentralRef=n.CentralCode "
                + " Left Join (SELECT P.PreFactorRef, sum(p.FactorAmount) as SumAmount , sum(p.FactorAmount * p.Price*g.DefaultUnitValue) as SumPrice, count(*) as RowCount "
                + " From Good g Join Units on UnitCode = GoodUnitRef  Join PreFactorRow p on GoodRef = GoodCode  Where IfNull(PreFactorRef, 0)>0 "
                + " Group BY PreFactorRef ) s on h.PreFactorCode = s.PreFactorRef "
                + " Where Replace(Replace(CustomerName,char(1740),char(1610)),char(1705),char(1603)) Like '%" + name + "%'"
                + " Order By h.PreFactorCode DESC";

        ArrayList<PreFactor> prefactor_header = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                PreFactor prefactor = new PreFactor();
                try {
                    prefactor.setPreFactorCode(cursor.getInt(cursor.getColumnIndex("PreFactorCode")));
                    prefactor.setPreFactorDate(cursor.getString(cursor.getColumnIndex("PreFactorDate")));
                    prefactor.setPreFactorTime(cursor.getString(cursor.getColumnIndex("PreFactorTime")));
                    prefactor.setPreFactorkowsarDate(cursor.getString(cursor.getColumnIndex("PreFactorKowsarDate")));
                    prefactor.setPreFactorKowsarCode(cursor.getInt(cursor.getColumnIndex("PreFactorKowsarCode")));
                    prefactor.setPreFactorExplain(cursor.getString(cursor.getColumnIndex("PreFactorExplain")));
                    prefactor.setCustomer(cursor.getString(cursor.getColumnIndex("CustomerName")));
                    prefactor.setSumAmount(cursor.getInt(cursor.getColumnIndex("SumAmount")));
                    prefactor.setSumPrice(cursor.getInt(cursor.getColumnIndex("SumPrice")));
                    prefactor.setRowCount(cursor.getInt(cursor.getColumnIndex("RowCount")));
                } catch (Exception ignored) {
                }
                prefactor_header.add(prefactor);
            }
        }
        assert cursor != null;
        cursor.close();
        return prefactor_header;
    }

    @SuppressLint("Range")
    public ArrayList<PreFactor> getAllPrefactorHeaderopen() {
        query = "SELECT h.*, s.SumAmount , s.SumPrice, s.RowCount ,n.Title || ' ' || n.FName|| ' ' || n.Name CustomerName  " +
                "FROM PreFactor h Join Customer c  on c.CustomerCode = h.CustomerRef "
                + " join Central n on c.CentralRef=n.CentralCode "
                + "Left Join (SELECT P.PreFactorRef, sum(p.FactorAmount) as SumAmount , sum(p.FactorAmount * p.Price*g.DefaultUnitValue) as SumPrice, count(*) as RowCount "
                + "From Good g Join Units on UnitCode = GoodUnitRef  Join PreFactorRow p on GoodRef = GoodCode  Where IfNull(PreFactorRef, 0)>0 "
                + "Group BY PreFactorRef ) s on h.PreFactorCode = s.PreFactorRef Where NOT IfNull(PreFactorKowsarCode, 0)>0 "
                + "Order By h.PreFactorCode DESC";

        ArrayList<PreFactor> prefactor_header = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                PreFactor prefactor = new PreFactor();
                try {


                    prefactor.setPreFactorCode(cursor.getInt(cursor.getColumnIndex("PreFactorCode")));
                    prefactor.setPreFactorDate(cursor.getString(cursor.getColumnIndex("PreFactorDate")));
                    prefactor.setPreFactorTime(cursor.getString(cursor.getColumnIndex("PreFactorTime")));
                    prefactor.setPreFactorkowsarDate(cursor.getString(cursor.getColumnIndex("PreFactorKowsarDate")));
                    prefactor.setPreFactorKowsarCode(cursor.getInt(cursor.getColumnIndex("PreFactorKowsarCode")));
                    prefactor.setPreFactorExplain(cursor.getString(cursor.getColumnIndex("PreFactorExplain")));
                    prefactor.setCustomer(cursor.getString(cursor.getColumnIndex("CustomerName")));
                    prefactor.setSumAmount(cursor.getInt(cursor.getColumnIndex("SumAmount")));
                    prefactor.setSumPrice(cursor.getInt(cursor.getColumnIndex("SumPrice")));
                    prefactor.setRowCount(cursor.getInt(cursor.getColumnIndex("RowCount")));
                } catch (Exception ignored) {
                }
                prefactor_header.add(prefactor);
            }
        }
        assert cursor != null;
        cursor.close();
        return prefactor_header;
    }

    @SuppressLint("Range")
    public ArrayList<Good> getAllPreFactorRows(String Search_target, String aPreFactorCode) {
        String name = GetRegionText(Search_target);
        name = name.replaceAll(" ", "%");
        GetPreference();

        columns = GetColumns("", "", "2");


        query = "SELECT ";

        k = 0;
        for (Column column : columns) {
            if (k != 0) {
                query = query + " , ";
            }
            if (!column.getColumnDefinition().equals("")) {
                query = query + column.getColumnDefinition() + " as " + column.getColumnName();
            } else {
                query = query + column.getColumnName();
            }
            k++;
        }

        query = query + " FROM Good g  " +
                "Join PreFactorRow pf on GoodRef = GoodCode " +
                "Join Units u on u.UnitCode = g.GoodUnitRef  " +
                "Where (Replace(Replace(GoodName,char(1740),char(1610)),char(1705),char(1603)) Like '%" + name + "%' and PreFactorRef = " + aPreFactorCode + ") order by PreFactorRowCode DESC ";


        cursor = getWritableDatabase().rawQuery(query, null);
        callMethod.Log(query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                gooddetail = new Good();
                for (Column column : columns) {
                    try {
                        switch (column.getColumnType()) {
                            case "0":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        cursor.getString(cursor.getColumnIndex(column.getColumnName()))
                                );
                                break;
                            case "1":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                            case "2":
                                gooddetail.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumnName())))
                                );
                                break;
                        }
                    } catch (Exception ignored) {
                    }
                }

                goods.add(gooddetail);

            }
        }
        assert cursor != null;
        cursor.close();
        return goods;
    }

    @SuppressLint("Range")
    public void UpdatePreFactorHeader_Customer(String pfcode, String Search_target) {
        String Customer = GetRegionText(Search_target);

        query = "Update Prefactor set CustomerRef='" + Customer + "' where PreFactorCode = " + pfcode;
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
        query = "Select * From ( Select Case PriceTip " +
                "When 1 Then  SellPrice1 When 2 Then SellPrice2 When 3 Then SellPrice3  " +
                "When 4 Then   SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 " +
                "Else  Case When g.SellPriceType = 0 Then MaxSellPrice Else 100 End End * " +
                " Case When g.SellPriceType = 0 Then 1 Else MaxSellPrice/100 End as " +
                "NewPrice, Price, GoodCode From PreFactorRow p " +
                "Join PreFactor h on h.PreFactorCode = p.PreFactorRef " +
                "Join Customer on CustomerCode = CustomerRef " +
                "Join Good g on GoodRef = GoodCode Where h.PreFactorCode = " + pfcode + ") ss " +
                "Where Price<> NewPrice";


        cursor = getWritableDatabase().rawQuery(query, null);


        if (cursor != null) {
            while (cursor.moveToNext()) {

                getWritableDatabase().execSQL("Update PreFactorRow set Price=" + cursor.getString(cursor.getColumnIndex("NewPrice"))
                        + " Where PreFactorRef =" + pfcode + " And GoodRef =" + cursor.getString(cursor.getColumnIndex("GoodCode")));
            }
        }
        assert cursor != null;
        cursor.close();
    }

    @SuppressLint("Range")
    public Integer GetLastPreFactorHeader() {

        query = "SELECT PreFactorCode FROM Prefactor Where PreFactorKowsarCode = 0 order by PreFactorCode DESC";

        int Res = 0;
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            Res = cursor.getInt(cursor.getColumnIndex("PreFactorCode"));
        }
        cursor.close();
        return Res;
    }

    public void update_explain(String pfcode, String explain) {

        query = "Update PreFactor set PreFactorExplain = '" + explain + "' Where IfNull(PreFactorCode,0)=" + pfcode;
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
    }

    public void DeletePreFactorRow(String pfcode, String rowcode) {
        query = " Delete From PreFactorRow Where IfNull(PreFactorRef,0)=" + pfcode + " And (PreFactorRowCode =" + rowcode + " or 0=" + rowcode + ")";
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
    }

    public void DeletePreFactor(String pfcode) {
        query = " Delete From Prefactor Where IfNull(PreFactorCode,0)=" + pfcode;
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
    }

    public void DeleteEmptyPreFactor() {
        query = " DELETE FROM Prefactor WHERE PreFactorCode NOT IN (SELECT PreFactorRef FROM PrefactorRow )";
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
        
    }

    public void UpdatePreFactor(String PreFactorCode, String PreFactorKowsarCode, String PreFactorDate) {
        query = "Update PreFactor Set PreFactorKowsarCode = " + PreFactorKowsarCode + ", PreFactorKowsarDate = '" + PreFactorDate + "' Where ifnull(PreFactorCode ,0)= " + PreFactorCode + ";";
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
    }

    @SuppressLint("Range")
    public String getFactorSum(String pfcode) {
        query = " select sum(FactorAmount*price*DefaultUnitValue) as result From PreFactorRow join Good on GoodRef=GoodCode Where IfNull(PreFactorRef,0)=" + pfcode;
        callMethod.Log(query);
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(cursor.getColumnIndex("result"));
        cursor.close();

        return String.valueOf(result);
    }

    @SuppressLint("Range")
    public String getFactorSumAmount(String pfcode) {
        query = "select sum(FactorAmount) as result From PreFactorRow join Good on GoodRef=GoodCode Where IfNull(PreFactorRef,0)=" + pfcode;
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        int result = cursor.getInt(cursor.getColumnIndex("result"));
        cursor.close();
        return String.valueOf(result);
    }

    @SuppressLint("Range")
    public String getFactordate(String pfcode) {
        query = "select PreFactorDate as result From Prefactor  Where IfNull(PreFactorCode,0)=" + pfcode;
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("result"));
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public String getPricetipCustomer(String pfcode) {
        int resultint = 0;
        query = "SELECT PriceTip  FROM PreFactor h " +
                " Join Customer c  on c.CustomerCode = h.CustomerRef " +
                " join Central n on c.CentralRef=n.CentralCode " +
                " Where IfNull(PreFactorCode,0)= " + pfcode;

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            resultint = cursor.getInt(cursor.getColumnIndex("PriceTip"));
            cursor.close();
        } else {
            result = "فاکتوری انتخاب نشده";
        }
        return String.valueOf(resultint);
    }

    @SuppressLint("Range")
    public String getFactorCustomer(String pfcode) {

        query = "SELECT n.Title || ' ' || n.FName|| ' ' || n.Name CustomerName  FROM PreFactor h " +
                " Join Customer c  on c.CustomerCode = h.CustomerRef " +
                " join Central n on c.CentralRef=n.CentralCode " +
                " Where IfNull(PreFactorCode,0)= " + pfcode;

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("CustomerName"));
            cursor.close();
        } else {
            result = "فاکتوری انتخاب نشده";
        }
        return result;
    }

    @SuppressLint("Range")
    public long getsum_sumfactor() {
        query = "select sum(price) as sm From PreFactorRow";

        long Res = 0;
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            Res = cursor.getLong(cursor.getColumnIndex("sm"));
        }
        cursor.close();
        return Res;
    }

    @SuppressLint("Range")
    public ArrayList<Customer> AllCustomer(String search_target, boolean aOnlyActive) {

        String name = GetRegionText(search_target);
        name = name.replaceAll(" ", "%").replaceAll("'", "%");
        query = "SELECT u.CustomerCode,u.PriceTip,c.Title || ' ' || c.FName|| ' ' || c.Name CentralName,Address,Manager,Mobile,Phone,Delegacy,y.Name CityName, CustomerBestankar - CustomerBedehkar Bestankar, Active, CentralPrivateCode, EtebarNaghd" +
                ",EtebarCheck, Takhfif, MobileName, Email, Fax, ZipCode, PostCode FROM Customer u " +
                "join Central c on u.CentralRef= c.CentralCode " +
                "Left join Address d on u.AddressRef=d.AddressCode " +
                "Left join City y on d.CityCode=y.CityCode " +
                "join BrokerCustomer cb on cb.CustomerRef=u.CustomerCode " +
                " Where cb.BrokerRef=" + ReadConfig("BrokerCode") +
                " And ((Replace(Replace(CentralName,char(1740),char(1610)),char(1705),char(1603)) Like '%" + name + "%' or " +
                " CustomerCode Like '%" + name + "%' or  " +
                " Replace(Replace( Manager,char(1740),char(1610)),char(1705),char(1603)) Like '%" + name + "%'))";

        if (aOnlyActive) {
            query = query + " And Active = 0";
        }

        query = query + " order by CustomerCode DESC  LIMIT 200";
        ArrayList<Customer> Customers = new ArrayList<>();
        callMethod.Log(query);
        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Customer customerdetail = new Customer();
                try {
                    customerdetail.setCustomerCode(cursor.getInt(cursor.getColumnIndex("CustomerCode")));
                    customerdetail.setCustomerName(cursor.getString(cursor.getColumnIndex("CentralName")));
                    customerdetail.setManager(cursor.getString(cursor.getColumnIndex("Manager")));
                    customerdetail.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
                    customerdetail.setPhone(cursor.getString(cursor.getColumnIndex("Phone")));
                    customerdetail.setBestankar(cursor.getInt(cursor.getColumnIndex("Bestankar")));
                } catch (Exception ignored) {
                }

                Customers.add(customerdetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return Customers;
    }

    @SuppressLint("Range")
    public Integer Customer_check(String name) {
        int res = 0;
        query = "select centralcode from central where d_codemelli ='" + name + "'";

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                res = cursor.getInt(cursor.getColumnIndex("CentralCode"));
            }
        }
        assert cursor != null;
        cursor.close();
        return res;
    }

    @SuppressLint("Range")
    public ArrayList<Customer> city() {

        query = "SELECT * from city";
        ArrayList<Customer> city = new ArrayList<>();
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Customer customerdetail = new Customer();
                try {
                    customerdetail.setCityName(cursor.getString(cursor.getColumnIndex("CityName")));
                    customerdetail.setCityCode(cursor.getString(cursor.getColumnIndex("CityCode")));
                } catch (Exception ignored) {
                }
                city.add(customerdetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return city;
    }

    @SuppressLint("Range")
    public String GetksrImage(String code) {
        query = "select ksrImageCode from ksrImage where ObjectRef = " + code + " limit 1";
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("KsrImageCode"));
        cursor.close();
        return result;
    }


    @SuppressLint("Range")
    public ArrayList<Good> GetksrImageCodes(String code) {
        query = "SELECT ksrImageCode from KsrImage where ObjectRef = " + code;
        ArrayList<Good> Goods = new ArrayList<>();
        callMethod.Log(query);
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Good gooddetail = new Good();
                try {
                    gooddetail.setGoodFieldValue("KsrImageCode", cursor.getString(cursor.getColumnIndex("KsrImageCode")));
                } catch (Exception ignored) {
                }
                Goods.add(gooddetail);
            }
        }
        assert cursor != null;
        cursor.close();
        return Goods;
    }

    @SuppressLint("Range")
    public String GetLastksrImageCode(String code) {
        query = "SELECT ksrImageCode from KsrImage where ObjectRef = " + code + " limit 1 ";
        String ksrimageCode = "";

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ksrimageCode = String.valueOf(cursor.getInt(cursor.getColumnIndex("KsrImageCode")));
            }
        }
        assert cursor != null;
        cursor.close();
        return ksrimageCode;
    }

    @SuppressLint("Range")
    public ArrayList<GoodGroup> getAllGroups(String Glstr) {

        String GL = "0";
        if (!Glstr.equals("")) {
            GL = Glstr;
        }

        query = "SELECT * ," +
                "case When L1=0 Then (Select Count(*) From GoodsGrp s Where s.L1=g.GroupCode) " +
                "When L2=0 Then (Select Count(*) From GoodsGrp s Where s.L2=g.GroupCode) " +
                "When L3=0 Then (Select Count(*) From GoodsGrp s Where s.L3=g.GroupCode) " +
                "When L4=0 Then (Select Count(*) From GoodsGrp s Where s.L4=g.GroupCode) " +
                "When L5=0 Then (Select Count(*) From GoodsGrp s Where s.L5=g.GroupCode) " +
                "Else 0 End  ChildNo " +
                " FROM GoodsGrp g WHERE 1=1 ";
        if (Integer.parseInt(GL) > 0) {
            query = query + " And ((L1=" + GL + " And L2=0) or (L2=" + GL + " And L3=0) or (L3=" + GL + " And L4=0) or (L4=" + GL + " And L5=0) or (L5=" + GL + "))";
        } else {
            query = query + " order by 1 desc";
        }

        ArrayList<GoodGroup> groups = new ArrayList<>();

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                GoodGroup grp = new GoodGroup();
                try {
                    grp.setGroupCode(cursor.getInt(cursor.getColumnIndex("GroupCode")));
                    grp.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    grp.setL1(cursor.getInt(cursor.getColumnIndex("L1")));
                    grp.setL2(cursor.getInt(cursor.getColumnIndex("L2")));
                    grp.setL3(cursor.getInt(cursor.getColumnIndex("L3")));
                    grp.setL4(cursor.getInt(cursor.getColumnIndex("L4")));
                    grp.setL5(cursor.getInt(cursor.getColumnIndex("L5")));
                    grp.setChildNo(cursor.getInt(cursor.getColumnIndex("ChildNo")));
                } catch (Exception ignored) {
                }
                groups.add(grp);

            }
        }
        assert cursor != null;
        cursor.close();
        return groups;
    }

    @SuppressLint("Range")
    public ArrayList<GoodGroup> getmenuGroups() {
        GetPreference();
        if (!SH_MenuBroker.equals(""))
            query = "SELECT * FROM GoodsGrp Where Groupcode in (" + SH_MenuBroker + ")";
        else
            query = "SELECT * FROM GoodsGrp Where Groupcode in (9999)";

        ArrayList<GoodGroup> groups = new ArrayList<>();
        try {
            cursor = getWritableDatabase().rawQuery(query, null);
        } catch (Exception e) {
            query = "SELECT * FROM GoodsGrp Where Groupcode in (9999)";
            cursor = getWritableDatabase().rawQuery(query, null);
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                GoodGroup grp = new GoodGroup();
                try {
                    grp.setGroupCode(cursor.getInt(cursor.getColumnIndex("GroupCode")));

                    grp.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    grp.setL1(cursor.getInt(cursor.getColumnIndex("L1")));
                    grp.setL2(cursor.getInt(cursor.getColumnIndex("L2")));
                    grp.setL3(cursor.getInt(cursor.getColumnIndex("L3")));
                    grp.setL4(cursor.getInt(cursor.getColumnIndex("L4")));
                    grp.setL5(cursor.getInt(cursor.getColumnIndex("L5")));
                } catch (Exception ignored) {
                }
                groups.add(grp);

            }
        }
        assert cursor != null;
        cursor.close();
        return groups;
    }

    @SuppressLint("Range")
    public UserInfo LoadPersonalInfo() {
        UserInfo user = new UserInfo();
        query = "Select * From Config";
        String key;
        String val;
        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                key = cursor.getString(cursor.getColumnIndex("KeyValue"));
                val = cursor.getString(cursor.getColumnIndex("DataValue"));

                switch (key) {
                    case "Email":
                        user.setEmail(val);
                        break;
                    case "NameFamily":
                        user.setNameFamily(val);
                        break;
                    case "Address":
                        user.setAddress(val);
                        break;
                    case "Mobile":
                        user.setMobile(val);
                        break;
                    case "Phone":
                        user.setPhone(val);
                        break;
                    case "BirthDate":
                        user.setBirthDate(val);
                        break;
                    case "PostalCode":
                        user.setPostalCode(val);
                        break;
                    case "MelliCode":
                        user.setMelliCode(val);
                        break;
                    case "ActiveCode":
                        user.setActiveCode(val);
                        break;
                    case "BrokerCode":
                        user.setBrokerCode(val);
                        break;
                }
            }
        }
        cursor.close();
        return user;
    }

    public void SavePersonalInfo(UserInfo user) {

        if (!user.getBrokerCode().equals("")) {
            query = " Update Config set DataValue = '" + user.getBrokerCode() + "' Where KeyValue = 'BrokerCode';";
            getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
            query = " Insert Into Config(KeyValue, DataValue) " +
                    "  Select 'BrokerCode', '" + user.getBrokerCode() + "' Where Not Exists(Select * From Config Where KeyValue = 'BrokerCode');";
            getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
        }

    }

    public void SaveConfig(String key, String Value) {

        query = " Insert Into Config(KeyValue, DataValue) Select '" + key + "', '" + Value + "' Where Not Exists(Select * From Config Where KeyValue = '" + key + "');";
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
        query = " Update Config set DataValue = '" + Value + "' Where KeyValue = '" + key + "' ;";
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();

    }

    @SuppressLint("Range")
    public String ReadConfig(String key) {

        query = "SELECT DataValue  FROM Config  Where KeyValue= '" + key + "' ;";

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("DataValue"));

        }
        cursor.close();
        return result;

    }

    @SuppressLint("Range")
    public void ReplicateGoodtype(Column column) {
        cursor = getWritableDatabase().rawQuery("Select Count(*) AS cntRec From GoodType Where GoodType = '" + column.getColumnFieldValue("GoodType") + "'", null);
        cursor.moveToFirst();

        int nc = cursor.getInt(cursor.getColumnIndex("cntRec"));
        if (nc == 0) {
            getWritableDatabase().execSQL("INSERT INTO GoodType (GoodType,IsDefault)" +
                    " VALUES ('" + column.getColumnFieldValue("GoodType") +
                    "','" + column.getColumnFieldValue("IsDefault") + "'); ");
        }
        cursor.close();
    }

    public void UpdateSearchColumn(Column column) {
        query = "update BrokerColumn set condition = '" + column.getCondition() + "' where ColumnCode= " + column.getColumnCode();
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
    }
    public void UpdateLocationService(LocationResult locationResult, String gpsDate) {


        query = "Insert Into  GpsLocation (Longitude , Latitude ,Speed, BrokerRef , GpsDate )" +
                " Values ('"+locationResult.getLastLocation().getLongitude()+"' , '"+locationResult.getLastLocation().getLatitude()+"', '"+locationResult.getLastLocation().getSpeed()+"', '"+ReadConfig("BrokerCode")+"' , '"+gpsDate+"')";
        callMethod.Log(query);

            getWritableDatabase().execSQL(query);
            getWritableDatabase().close();

    }

    public void ClearSearchColumn() {
        query = "update BrokerColumn set condition = '' ";
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
    }

    public void ReplicateColumn(Column column, Integer Apptype) {

        query = "INSERT INTO BrokerColumn" +
                "(SortOrder,ColumnName ,ColumnDesc ,GoodType,ColumnDefinition,ColumnType,Condition,OrderIndex,AppType) " +
                " VALUES ('" + column.getColumnFieldValue("SortOrder") +
                "','" + column.getColumnFieldValue("ColumnName") +
                "','" + column.getColumnFieldValue("ColumnDesc") +
                "','" + column.getColumnFieldValue("GoodType") +
                "','" + column.getColumnFieldValue("ColumnDefinition") +
                "','" + column.getColumnFieldValue("ColumnType") +
                "','" + column.getColumnFieldValue("Condition") +
                "','" + column.getColumnFieldValue("OrderIndex") +
                "'," + Apptype + "); ";
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
    }

    public void deleteColumn() {
        getWritableDatabase().execSQL("delete from BrokerColumn");
        getWritableDatabase().execSQL("delete from GoodType");
    }

    @SuppressLint("Range")
    public String GpsLocationCode() {

        query = " select GpsLocationCode from GpsLocation where GpsLocationCode> "+ReadConfig("LastGpsLocationCode")+"   limit 1 OFFSET 2";


        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = String.valueOf(cursor.getInt(cursor.getColumnIndex("GpsLocationCode")));

        }else
        {
            result=ReadConfig("LastGpsLocationCode");
        }
        cursor.close();
        return result;
    }





    public void ExecQuery(String Query) {
        getWritableDatabase().execSQL(query);
        getWritableDatabase().close();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}