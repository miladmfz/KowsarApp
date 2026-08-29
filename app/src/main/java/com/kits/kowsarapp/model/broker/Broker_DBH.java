package com.kits.kowsarapp.model.broker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationResult;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.application.base.App;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Activation;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.Customer;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.GoodGroup;
import com.kits.kowsarapp.model.base.PreFactor;
import com.kits.kowsarapp.model.base.ReplicationModel;
import com.kits.kowsarapp.model.base.TableDetail;
import com.kits.kowsarapp.model.base.UserInfo;
import com.kits.kowsarapp.model.base.Utilities;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Broker_DBH extends SQLiteOpenHelper {
    CallMethod callMethod;

    int limitcolumn;

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

    String StackAmountString;
    String BrokerStackString;
    String joinDetail;
    String joinbasket;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private static final String FTS_CONTENT_VERSION = "3";

    private static final String[] GOOD_FTS_SEARCH_COLUMNS = {
            "GoodCode",
            "GoodMainCode",
            "GoodName",
            "GoodType",
            "FirstBarCode",

            "GoodExplain1",
            "GoodExplain2",
            "GoodExplain3",
            "GoodExplain4",
            "GoodExplain5",
            "GoodExplain6",

            "Nvarchar1",
            "Nvarchar2",
            "Nvarchar3",
            "Nvarchar4",
            "Nvarchar5",
            "Nvarchar6",
            "Nvarchar7",
            "Nvarchar8",
            "Nvarchar9",
            "Nvarchar10",
            "Nvarchar11",
            "Nvarchar12",
            "Nvarchar13",
            "Nvarchar14",
            "Nvarchar15",
            "Nvarchar16",
            "Nvarchar17",
            "Nvarchar18",
            "Nvarchar19",
            "Nvarchar20",

            "Text1",
            "Text2",
            "Text3",
            "Text4",
            "Text5"
    };

    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());
    public Broker_DBH(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        this.callMethod = new CallMethod(context);

    }

    private SQLiteDatabase db() {
        return getWritableDatabase();
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
    public interface DbCallback<T> {
        void onResult(T result);
        void onError(Exception e);
    }

    public void GetLastDataFromOldDataBase(String tempDbPath) {

        getWritableDatabase().execSQL("ATTACH DATABASE '" + tempDbPath + "' AS tempDb");

        getWritableDatabase().execSQL("INSERT INTO main.Prefactor SELECT * FROM tempDb.Prefactor ");
        getWritableDatabase().execSQL("INSERT INTO main.PreFactorRow SELECT * FROM tempDb.PreFactorRow ");
        getWritableDatabase().execSQL("INSERT INTO main.Config SELECT * FROM tempDb.Config ");

        getWritableDatabase().execSQL("DETACH DATABASE 'tempDb' ");
        //getWritableDatabase().close();

    }



    public void InitialConfigInsert() {


        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'BrokerCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'BrokerStack', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerStack')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'GroupCodeDefult', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerStack')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'MenuBroker', '0' Where Not Exists(Select * From Config Where KeyValue = 'MenuBroker')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'KsrImage_LastRepCode', '-1' Where Not Exists(Select * From Config Where KeyValue = 'KsrImage_LastRepCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'MaxRepLogCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'MaxRepLogCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'LastGpsLocationCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'LastGpsLocationCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'LastGpsLocationCodeNew', '0' Where Not Exists(Select * From Config Where KeyValue = 'LastGpsLocationCodeNew')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'LastUpdate', '0' Where Not Exists(Select * From Config Where KeyValue = 'LastUpdate')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'VersionInfo', '" + BuildConfig.VERSION_NAME + "' Where Not Exists(Select * From Config Where KeyValue = 'VersionInfo')");
        //getWritableDatabase().close();
    }
    public synchronized void drop() {

        SQLiteDatabase database = db();

        database.execSQL(
                "DROP TABLE IF EXISTS GoodSearchFTS"
        );
        database.execSQL(
                "DROP TABLE IF EXISTS GoodSearchFTSState"
        );
        SaveFTSReady(database, "0");
    }

    public void DatabaseCreate() {

        SQLiteDatabase database = db();

        try {


            database.execSQL("CREATE TABLE IF NOT EXISTS GoodSearchCache (GoodRef INTEGER, SearchToken TEXT)"   );

            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS IX_GoodSearchCache_SearchToken_GoodRef " +
                            "ON GoodSearchCache (SearchToken, GoodRef)"
            );

            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS IX_GoodSearchCache_GoodRef " +
                            "ON GoodSearchCache (GoodRef)"
            );
            database.execSQL("CREATE TABLE IF NOT EXISTS GpsLocation (GpsLocationCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE ,Longitude TEXT, Latitude TEXT, Speed TEXT, BrokerRef TEXT, GpsDate TEXT)");

            database.execSQL("CREATE TABLE IF NOT EXISTS GpsLocationNew (GpsLocationCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE ,Longitude TEXT, Latitude TEXT, Speed TEXT, Accuracy TEXT,BrokerRef TEXT," +
                    "GpsDate TEXT,NextGpsDate TEXT,DurationInSeconds TEXT,Status TEXT,LocationDescription TEXT)");

            database.execSQL("CREATE TABLE IF NOT EXISTS PreFactorRow (PreFactorRowCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE ,PreFactorRef INTEGER, GoodRef INTEGER, FactorAmount INTEGER, Shortage INTEGER, PreFactorDate TEXT,  Price DECIMAL)");

            database.execSQL("CREATE TABLE IF NOT EXISTS Prefactor ( PreFactorCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, PreFactorDate TEXT," +
                    " PreFactorTime TEXT, PreFactorKowsarCode INTEGER, PreFactorKowsarDate TEXT, PreFactorExplain TEXT, CustomerRef INTEGER, BrokerRef INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS Config (ConfigCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, KeyValue TEXT , DataValue TEXT)");

            database.execSQL("CREATE TABLE IF NOT EXISTS BrokerColumn ( ColumnCode INTEGER PRIMARY KEY, SortOrder TEXT, ColumnName TEXT, ColumnDesc TEXT, GoodType TEXT, ColumnDefinition TEXT, ColumnType TEXT, Condition TEXT, OrderIndex TEXT, AppType INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS GoodType ( GoodTypeCode INTEGER PRIMARY KEY, GoodType TEXT, IsDefault TEXT)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_GoodRef_StackRef ON GoodStack (GoodRef,StackRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_StackRef_GoodRef ON GoodStack (StackRef,GoodRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_GoodRef_ActiveStack ON GoodStack (GoodRef,ActiveStack)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_GoodRef_Amount ON GoodStack (GoodRef,Amount)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodStack_StackRef_ActiveStack_Amount ON GoodStack (StackRef,ActiveStack,Amount)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodGroup_GoodRef_GoodGroupRef ON GoodGroup (GoodRef,GoodGroupRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodGroup_GoodGroupRef_GoodRef ON GoodGroup (GoodGroupRef,GoodRef)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodsGrp_GroupCode ON GoodsGrp (GroupCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodsGrp_L1 ON GoodsGrp (L1)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodsGrp_L2 ON GoodsGrp (L2)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodsGrp_L3 ON GoodsGrp (L3)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodsGrp_L4 ON GoodsGrp (L4)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodsGrp_L5 ON GoodsGrp (L5)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_PreFactorRow_PreFactorRef_GoodRef ON PreFactorRow (PreFactorRef,GoodRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_PreFactorRow_GoodRef_PreFactorRef ON PreFactorRow (GoodRef,PreFactorRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_PreFactorRow_PreFactorRowCode ON PreFactorRow (PreFactorRowCode)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Prefactor_PreFactorCode ON Prefactor (PreFactorCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Prefactor_CustomerRef ON Prefactor (CustomerRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Prefactor_PreFactorKowsarCode ON Prefactor (PreFactorKowsarCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Prefactor_PreFactorKowsarCode_PreFactorCode ON Prefactor (PreFactorKowsarCode,PreFactorCode)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Customer_CustomerCode ON Customer (CustomerCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Customer_CentralRef ON Customer (CentralRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Customer_AddressRef ON Customer (AddressRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Customer_PriceTip ON Customer (PriceTip)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_BrokerCustomer_BrokerRef_CustomerRef ON BrokerCustomer (BrokerRef,CustomerRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_BrokerCustomer_CustomerRef_BrokerRef ON BrokerCustomer (CustomerRef,BrokerRef)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Central_CentralCode ON Central (CentralCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Address_AddressCode ON Address (AddressCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Address_CityCode ON Address (CityCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_City_CityCode ON City (CityCode)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Units_UnitCode ON Units (UnitCode)");

            try {
                database.execSQL("CREATE INDEX IF NOT EXISTS IX_CacheBarCode_GoodRef ON CacheBarCode (GoodRef)");
            } catch (Exception e) {
                callMethod.Log("CacheBarCode index error = " + e.getMessage());
            }

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_KsrImage_ObjectRef ON KsrImage (ObjectRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_KsrImage_ObjectRef_IsDefaultImage ON KsrImage (ObjectRef,IsDefaultImage)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_KsrImage_IsDefaultImage ON KsrImage (IsDefaultImage)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Config_KeyValue ON Config (KeyValue)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_BrokerColumn_AppType ON BrokerColumn (AppType)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_BrokerColumn_AppType_GoodType ON BrokerColumn (AppType,GoodType)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GoodType_GoodType ON GoodType (GoodType)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GpsLocation_GpsLocationCode ON GpsLocation (GpsLocationCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GpsLocationNew_GpsLocationCode ON GpsLocationNew (GpsLocationCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_GpsLocationNew_GpsDate ON GpsLocationNew (GpsDate)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodCode ON Good (GoodCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodName ON Good (GoodName)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodMainCode ON Good (GoodMainCode)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodUnitRef ON Good (GoodUnitRef)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_SellPriceType ON Good (SellPriceType)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodType ON Good (GoodType)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain1 ON Good (GoodExplain1)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain2 ON Good (GoodExplain2)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain3 ON Good (GoodExplain3)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain4 ON Good (GoodExplain4)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain5 ON Good (GoodExplain5)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_GoodExplain6 ON Good (GoodExplain6)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar1 ON Good (Nvarchar1)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar2 ON Good (Nvarchar2)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar3 ON Good (Nvarchar3)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar4 ON Good (Nvarchar4)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar5 ON Good (Nvarchar5)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar6 ON Good (Nvarchar6)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar7 ON Good (Nvarchar7)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar8 ON Good (Nvarchar8)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar9 ON Good (Nvarchar9)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Nvarchar10 ON Good (Nvarchar10)");

            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Date1 ON Good (Date1)");
            database.execSQL("CREATE INDEX IF NOT EXISTS IX_Good_Date2 ON Good (Date2)");

            try {
                database.execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_JobRef ON JobPerson (JobRef)");
                database.execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_AddressRef ON JobPerson (AddressRef)");
                database.execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_CentralRef ON JobPerson (CentralRef)");
                database.execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_Good_JobPersonRef ON JobPerson_Good (JobPersonRef)");
                database.execSQL("CREATE INDEX IF NOT EXISTS IX_JobPerson_Good_GoodRef ON JobPerson_Good (GoodRef)");
            } catch (Exception ignored) {
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    public void closedb() {
        try {
            close();
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }
    }


    @SuppressLint("Range")
    public ArrayList<ReplicationModel> GetReplicationTable() {

        String sql = "SELECT * FROM ReplicationTable ORDER BY ReplicationCode";

        ArrayList<ReplicationModel> replicationModels =
                new ArrayList<>();

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    ReplicationModel replicationModel =
                            new ReplicationModel();

                    try {

                        replicationModel.setReplicationCode(
                                localCursor.getInt(localCursor.getColumnIndex("ReplicationCode"))
                        );

                        replicationModel.setServerTable(
                                localCursor.getString(localCursor.getColumnIndex("ServerTable"))
                        );

                        replicationModel.setClientTable(
                                localCursor.getString(localCursor.getColumnIndex("ClientTable"))
                        );

                        replicationModel.setServerPrimaryKey(
                                localCursor.getString(localCursor.getColumnIndex("ServerPrimaryKey"))
                        );

                        replicationModel.setClientPrimaryKey(
                                localCursor.getString(localCursor.getColumnIndex("ClientPrimaryKey"))
                        );

                        replicationModel.setCondition(
                                localCursor.getString(localCursor.getColumnIndex("Condition"))
                        );

                        replicationModel.setConditionDelete(
                                localCursor.getString(localCursor.getColumnIndex("ConditionDelete"))
                        );

                        replicationModel.setLastRepLogCode(
                                localCursor.getInt(localCursor.getColumnIndex("LastRepLogCode"))
                        );

                        replicationModel.setLastRepLogCodeDelete(
                                localCursor.getInt(localCursor.getColumnIndex("LastRepLogCodeDelete"))
                        );

                    } catch (Exception ignored) {
                    }

                    replicationModels.add(replicationModel);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return replicationModels;
    }

    @SuppressLint("Range")
    public ArrayList<TableDetail> GetTableDetail(String TableName) {

        String sql = "PRAGMA table_info( " + TableName + " )";

        ArrayList<TableDetail> tableDetails =
                new ArrayList<>();

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    TableDetail tableDetail =
                            new TableDetail();

                    try {

                        tableDetail.setCid(
                                localCursor.getInt(localCursor.getColumnIndex("cid"))
                        );

                        tableDetail.setName(
                                localCursor.getString(localCursor.getColumnIndex("name"))
                        );

                        tableDetail.setType(
                                localCursor.getString(localCursor.getColumnIndex("type"))
                        );

                        tableDetail.setText(null);

                    } catch (Exception ignored) {
                    }

                    tableDetails.add(tableDetail);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return tableDetails;
    }
    @SuppressLint("Range")
    public synchronized void GetLimitColumn(String AppType) {

        Cursor goodTypeCursor = null;
        Cursor columnCursor = null;

        try {

            String sql = "select Count(*) count from GoodType ";

            goodTypeCursor = db().rawQuery(sql, null);

            String goodtypecount = "0";

            if (goodTypeCursor != null && goodTypeCursor.moveToFirst()) {

                goodtypecount =
                        goodTypeCursor.getString(
                                goodTypeCursor.getColumnIndex("count")
                        );
            }

            sql =
                    "select Count(*) count from BrokerColumn " +
                            "Where Replace(Replace(AppType,char(1740),char(1610)),char(1705),char(1603)) = " +
                            "Replace(Replace('" + AppType + "',char(1740),char(1610)),char(1705),char(1603))";

            columnCursor = db().rawQuery(sql, null);

            String columnscount = "0";

            if (columnCursor != null && columnCursor.moveToFirst()) {

                columnscount =
                        columnCursor.getString(
                                columnCursor.getColumnIndex("count")
                        );
            }

            int goodTypeCountInt =
                    Integer.parseInt(goodtypecount);

            int columnsCountInt =
                    Integer.parseInt(columnscount);

            if (goodTypeCountInt > 0) {
                limitcolumn = columnsCountInt / goodTypeCountInt;
            } else {
                limitcolumn = 0;
            }

        } catch (Exception e) {

            callMethod.showToast(
                    "تنظیم جدول از سمت دیتابیس مشکل دارد"
            );

            callMethod.Log(e.getMessage());

            limitcolumn = 0;

        } finally {

            closeCursor(goodTypeCursor);
            closeCursor(columnCursor);
        }
    }
    public synchronized void GetPreference() {

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

        try {
            LimitAmount =
                    String.valueOf(
                            Integer.parseInt(SH_grid) * 11
                    );
        } catch (Exception e) {
            LimitAmount = "11";
            callMethod.Log(e.getMessage());
        }

        BrokerStackString =
                "Where StackRef in (" + SH_brokerstack + ")";

        StackAmountString = "";

        joinbasket =
                " FROM Good g " +
                        " Join Units on UnitCode =GoodUnitRef " +
                        " Left Join (Select GoodRef, Sum(FactorAmount) FactorAmount , Sum(FactorAmount*Price) Price " +
                        " From PreFactorRow Where PreFactorRef = " + SH_prefactor_code + " Group BY GoodRef) pf on pf.GoodRef = g.GoodCode  " +
                        " Left Join PreFactor h on h.PreFactorCode = " + SH_prefactor_code +
                        " Left Join Customer c on c.CustomerCode=h.CustomerRef ";

        joinDetail =
                " FROM Good g ,FilterTable Join Units u on u.UnitCode = g.GoodUnitRef " +
                        " Left Join CacheGoodGroup cgg on cgg.GoodRef = g.Goodcode ";
    }
    @SuppressLint("Range")
    public String GetGoodTypeFromGood(String code) {

        if (code == null || code.trim().isEmpty()) {
            return "";
        }

        String resultValue = "";
        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(
                    "SELECT GoodType FROM Good WHERE GoodCode = ? LIMIT 1",
                    new String[]{code.trim()}
            );

            if (localCursor != null && localCursor.moveToFirst()) {

                resultValue = localCursor.getString(
                        localCursor.getColumnIndex("GoodType")
                );
            }

        } catch (Exception e) {

            callMethod.Log(
                    "GetGoodTypeFromGood ERROR => code=" +
                            code +
                            " | " +
                            e.getMessage()
            );

        } finally {

            closeCursor(localCursor);
        }

        return resultValue == null ? "" : resultValue;
    }
    @SuppressLint("Range")
    public synchronized ArrayList<Column> GetColumns(
            String code,
            String goodtype,
            @NonNull String AppType
    ) {

        String sql;

        switch (AppType) {

            case "0":

                sql =
                        "Select * from BrokerColumn " +
                                "where Replace(Replace(GoodType,char(1740),char(1610)),char(1705),char(1603)) = ? " +
                                "And AppType = 0";

                goodtype = GetRegionText(GetGoodTypeFromGood(code));
                break;

            case "1":
            case "2":

                GetLimitColumn(AppType);

                sql =
                        "Select * from BrokerColumn " +
                                "where AppType = ? " +
                                "limit " + limitcolumn;

                break;

            case "3":

                sql =
                        "Select * from BrokerColumn " +
                                "where Replace(Replace(GoodType,char(1740),char(1610)),char(1705),char(1603)) = ? " +
                                "And AppType = 3";

                goodtype = GetRegionText(goodtype);
                break;

            default:

                sql = "Select * from BrokerColumn where 1 = 0";
                break;
        }

        callMethod.Log(sql);

        ArrayList<Column> resultColumns = new ArrayList<>();
        Cursor localCursor = null;

        try {

            if (AppType.equals("0") || AppType.equals("3")) {

                localCursor = db().rawQuery(
                        sql,
                        new String[]{goodtype == null ? "" : goodtype}
                );

            } else if (AppType.equals("1") || AppType.equals("2")) {

                localCursor = db().rawQuery(
                        sql,
                        new String[]{AppType}
                );

            } else {

                localCursor = db().rawQuery(sql, null);
            }

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    Column column = new Column();

                    try {

                        column.setColumnCode(
                                localCursor.getString(localCursor.getColumnIndex("ColumnCode"))
                        );

                        column.setSortOrder(
                                localCursor.getString(localCursor.getColumnIndex("SortOrder"))
                        );

                        column.setColumnName(
                                localCursor.getString(localCursor.getColumnIndex("ColumnName"))
                        );

                        column.setColumnDesc(
                                localCursor.getString(localCursor.getColumnIndex("ColumnDesc"))
                        );

                        column.setGoodType(
                                localCursor.getString(localCursor.getColumnIndex("GoodType"))
                        );

                        column.setColumnType(
                                localCursor.getString(localCursor.getColumnIndex("ColumnType"))
                        );

                        column.setColumnDefinition(
                                localCursor.getString(localCursor.getColumnIndex("ColumnDefinition"))
                        );

                        column.setCondition(
                                localCursor.getString(localCursor.getColumnIndex("Condition"))
                        );

                        column.setOrderIndex(
                                localCursor.getString(localCursor.getColumnIndex("OrderIndex"))
                        );

                    } catch (Exception e) {
                        callMethod.Log("GetColumns row ERROR => " + e.getMessage());
                    }

                    resultColumns.add(column);
                }
            }

        } catch (Exception e) {

            callMethod.Log("GetColumns ERROR => " + e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultColumns;
    }
    @SuppressLint("Range")
    public String GetColumnscount() {

        String sql = "Select Count(*) result from BrokerColumn ";

        String resultValue = "0";

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null && localCursor.moveToFirst()) {

                resultValue =
                        String.valueOf(
                                localCursor.getInt(
                                        localCursor.getColumnIndex("result")
                                )
                        );
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultValue;
    }
    @SuppressLint("Range")
    public String GetRegionText(String value) {

        if (value == null) {
            return "";
        }

        // همان نرمال‌سازی قبلی SQLite:
        // char(1740) -> char(1610)  |  ی -> ي
        // char(1705) -> char(1603)  |  ک -> ك
        return value
                .replace('\u06CC', '\u064A')
                .replace('\u06A9', '\u0643');
    }

    @SuppressLint("Range")
    public ArrayList<Column> GetAllGoodType() {
        String sql = "Select * from GoodType ";
        ArrayList<Column> resultColumns = new ArrayList<>();
        Cursor localCursor = null;

        try {
            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {
                while (localCursor.moveToNext()) {
                    Column itemColumn = new Column();

                    try {
                        itemColumn.setGoodType(localCursor.getString(localCursor.getColumnIndex("GoodType")));
                        itemColumn.setIsDefault(localCursor.getString(localCursor.getColumnIndex("IsDefault")));
                    } catch (Exception ignored) {
                    }

                    resultColumns.add(itemColumn);
                }
            }

        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        } finally {
            closeCursor(localCursor);
        }

        return resultColumns;
    }



    @SuppressLint({"Recycle", "Range"})
    public synchronized ArrayList<Good> getAllGood11(String search_target, String aGroupCode, String MoreCallData) {

        ArrayList<Good> resultGoods = new ArrayList<>();

        GetPreference();

        ArrayList<Column> localColumns = GetColumns("", "", "1");

        String search = GetRegionText(search_target);
        search = search.replaceAll("'", " ").trim();

        try {
            Integer.parseInt(aGroupCode);
        } catch (Exception e) {
            aGroupCode = "0";
        }

        int offsetValue = 0;

        try {
            offsetValue =
                    Integer.parseInt(LimitAmount) *
                            Integer.parseInt(MoreCallData);
        } catch (Exception e) {
            offsetValue = 0;
        }

        String selectQuery = "";
        String whereQuery;
        String orderQuery;

        int k = 0;

        for (Column column : localColumns) {

            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString =
                        column.getColumnDefinition().substring(
                                column.getColumnDefinition().indexOf("Sum"),
                                column.getColumnDefinition().indexOf(")") + 1
                        );
            }

            if (!column.getColumnName().equals("")) {

                if (k != 0) {
                    selectQuery = selectQuery + " , ";
                }

                if (!column.getColumnDefinition().equals("")) {
                    selectQuery =
                            selectQuery +
                                    column.getColumnDefinition() +
                                    " as " +
                                    column.getColumnName();
                } else {
                    selectQuery =
                            selectQuery +
                                    "g." +
                                    column.getColumnName();
                }

                k++;
            }
        }

        if (selectQuery.equals("")) {
            selectQuery = "g.GoodCode";
        }

        if (!search.equals("")) {

            String ftsSearch =
                    search.replaceAll("\\s+", " ").trim();

            String[] words =
                    ftsSearch.split(" ");

            String matchQuery = "";

            for (String word : words) {

                word = word.trim();

                if (!word.equals("")) {

                    word = word
                            .replace("*", "")
                            .replace("\"", "")
                            .replace("'", "")
                            .replace(":", "")
                            .replace("-", " ");

                    if (!word.equals("")) {

                        if (!matchQuery.equals("")) {
                            matchQuery = matchQuery + " ";
                        }

                        matchQuery = matchQuery + word + "*";
                    }
                }
            }

            if (!matchQuery.equals("")) {

                whereQuery =
                        " Where g.GoodCode in (" +
                                " Select Cast(GoodCode as INTEGER) " +
                                " From GoodSearchFTS " +
                                " Where GoodSearchFTS Match '" +
                                matchQuery +
                                "' ) ";

            } else {

                whereQuery = " Where 1=1 ";
            }

        } else {

            whereQuery = " Where 1=1 ";
        }

        whereQuery =
                whereQuery +
                        " And Exists(Select 1 From GoodStack stackCondition ActiveCondition And GoodRef=GoodCode AmountCondition)";

        if (SH_activestack) {
            whereQuery =
                    whereQuery.replaceAll(
                            "ActiveCondition",
                            " And ActiveStack = 1 "
                    );
        } else {
            whereQuery =
                    whereQuery.replaceAll(
                            "ActiveCondition",
                            " "
                    );
        }

        if (SH_goodamount) {
            whereQuery =
                    whereQuery.replaceAll(
                            "AmountCondition",
                            " GROUP BY GoodRef HAVING " +
                                    StackAmountString +
                                    " > 0 "
                    );
        } else {
            whereQuery =
                    whereQuery.replaceAll(
                            "AmountCondition",
                            " "
                    );
        }

        whereQuery =
                whereQuery.replaceAll(
                        "stackCondition",
                        BrokerStackString
                );

        if (Integer.parseInt(aGroupCode) > 0) {

            whereQuery =
                    whereQuery +
                            " And GoodCode in(Select GoodRef From GoodGroup p "
                            + "Join GoodsGrp s on p.GoodGroupRef = s.GroupCode "
                            + "Where s.GroupCode = " + aGroupCode
                            + " or s.L1 = " + aGroupCode
                            + " or s.L2 = " + aGroupCode
                            + " or s.L3 = " + aGroupCode
                            + " or s.L4 = " + aGroupCode
                            + " or s.L5 = " + aGroupCode + ")";
        }

        orderQuery = " order by ";

        int orderCount = 0;

        for (Column column : localColumns) {

            if (!column.getOrderIndex().equals("0")) {

                if (orderCount != 0) {
                    orderQuery = orderQuery + " , ";
                }

                if (Integer.parseInt(column.getOrderIndex()) > 0) {

                    if (column.getColumnName().equals("Date")) {

                        String newSt =
                                column.getColumnDefinition().substring(
                                        column.getColumnDefinition().indexOf("Then") + 5,
                                        column.getColumnDefinition().indexOf("Then") + 12
                                );

                        orderQuery = orderQuery + newSt;

                    } else {

                        orderQuery = orderQuery + column.getColumnName();
                    }

                } else {

                    if (column.getColumnName().equals("Date")) {

                        String newSt =
                                column.getColumnDefinition().substring(
                                        column.getColumnDefinition().indexOf("Then") + 5,
                                        column.getColumnDefinition().indexOf("Then") + 12
                                );

                        orderQuery = orderQuery + newSt + " DESC ";

                    } else {

                        orderQuery = orderQuery + column.getColumnName() + " DESC ";
                    }
                }

                orderCount++;
            }
        }

        if (orderCount == 0) {
            orderQuery = " order by GoodCode DESC ";
        }

        String sql =
                " With FilterTable As (Select 0 as SecondField), " +
                        " GoodsLimited As ( " +
                        " Select g.GoodCode " +
                        " From Good g , FilterTable " +
                        whereQuery +
                        orderQuery +
                        " LIMIT " +
                        LimitAmount +
                        " OFFSET " +
                        offsetValue +
                        " ) " +
                        " SELECT " +
                        selectQuery +
                        " FROM GoodsLimited gl " +
                        " Join Good g on g.GoodCode = gl.GoodCode " +
                        " , FilterTable " +
                        orderQuery;

        callMethod.Log(sql);

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    Good itemGood = new Good();

                    for (Column column : localColumns) {

                        try {

                            switch (column.getColumnType()) {

                                case "0":

                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            localCursor.getString(
                                                    localCursor.getColumnIndex(
                                                            column.getColumnName()
                                                    )
                                            )
                                    );

                                    break;

                                case "1":

                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(
                                                    localCursor.getInt(
                                                            localCursor.getColumnIndex(
                                                                    column.getColumnName()
                                                            )
                                                    )
                                            )
                                    );

                                    break;

                                case "2":

                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(
                                                    localCursor.getFloat(
                                                            localCursor.getColumnIndex(
                                                                    column.getColumnName()
                                                            )
                                                    )
                                            )
                                    );

                                    break;
                            }

                        } catch (Exception ignored) {
                        }
                    }

                    itemGood.setCheck(false);

                    try {

                        itemGood.setGoodFieldValue(
                                "ActiveStack",
                                String.valueOf(
                                        localCursor.getInt(
                                                localCursor.getColumnIndex(
                                                        "ActiveStack"
                                                )
                                        )
                                )
                        );

                    } catch (Exception ignored) {
                    }

                    resultGoods.add(itemGood);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultGoods;
    }





    @SuppressLint({"Recycle", "Range"})
    public synchronized ArrayList<Good> getAllGood1(String search_target, String aGroupCode, String MoreCallData) {
        ArrayList<Good> resultGoods = new ArrayList<>();
        GetPreference();

        ArrayList<Column> localColumns = GetColumns("", "", "1");

        String search = GetRegionText(search_target);
        search = search.replaceAll(" ", "%").replaceAll("'", "%");

        Search_Condition = " '%" + search + "%' ";

        String sql = " With FilterTable As (Select 0 as SecondField) SELECT ";

        int selectCount = 0;

        for (Column column : localColumns) {

            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }

            if (!column.getColumnName().equals("")) {
                if (selectCount != 0) {
                    sql = sql + " , ";
                }
                if (!column.getColumnDefinition().equals("")) {
                    sql = sql + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    sql = sql + column.getColumnName();
                }
                selectCount++;
            }
        }

        sql = sql + " FROM Good g , FilterTable ";
        selectCount = 0;

        boolean digitsOnly = TextUtils.isDigitsOnly(search);

        if (!search.equals("")) {

            for (Column column : localColumns) {

                if (!(!column.getColumnType().equals("0") && !digitsOnly)) {

                    if (Integer.parseInt(column.getColumnFieldValue("SortOrder")) > 0 &&
                            Integer.parseInt(column.getColumnFieldValue("SortOrder")) < 10) {

                        if (selectCount == 0) {
                            sql = sql + " Where (";
                        } else {
                            sql = sql + " or ";
                        }

                        sql = sql +
                                column.getColumnName() +
                                " Like '%" +
                                search +
                                "%' ";

                        selectCount++;
                    }
                }
            }

            for (Column column : localColumns) {
                if (column.getColumnType().equals("")) {
                    sql = sql + " or " + column.getColumnDefinition();
                }
            }

            sql = sql + " )";

        } else {

            sql = sql + "where 1=1 ";
        }

        sql = sql + " And Exists(Select 1 From GoodStack stackCondition ActiveCondition And GoodRef=GoodCode AmountCondition)";

        if (SH_activestack) {
            sql = sql.replaceAll("ActiveCondition", " And ActiveStack = 1 ");
        } else {
            sql = sql.replaceAll("ActiveCondition", " ");
        }

        if (SH_goodamount) {
            sql = sql.replaceAll("AmountCondition", " GROUP BY GoodRef HAVING " + StackAmountString + " > 0 ");
        } else {
            sql = sql.replaceAll("AmountCondition", " ");
        }

        sql = sql.replaceAll("stackCondition", BrokerStackString);
        sql = sql.replaceAll("SearchCondition", Search_Condition);

        try {
            Integer.parseInt(aGroupCode);
        } catch (Exception e) {
            aGroupCode = "0";
        }

        if (Integer.parseInt(aGroupCode) > 0) {
            sql = sql + " And GoodCode in(Select GoodRef From GoodGroup p "
                    + "Join GoodsGrp s on p.GoodGroupRef = s.GroupCode "
                    + "Where s.GroupCode = " + aGroupCode + " or s.L1 = " + aGroupCode
                    + " or s.L2 = " + aGroupCode
                    + " or s.L3 = " + aGroupCode
                    + " or s.L4 = " + aGroupCode
                    + " or s.L5 = " + aGroupCode + ")";
        }

        sql = sql + " order by ";

        int k = 0;

        for (Column column : localColumns) {
            if (!column.getOrderIndex().equals("0")) {
                if (k != 0) {
                    sql = sql + " , ";
                }

                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(
                                column.getColumnDefinition().indexOf("Then") + 5,
                                column.getColumnDefinition().indexOf("Then") + 12
                        );
                        sql = sql + newSt;
                    } else {
                        sql = sql + column.getColumnName();
                    }
                } else {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(
                                column.getColumnDefinition().indexOf("Then") + 5,
                                column.getColumnDefinition().indexOf("Then") + 12
                        );
                        sql = sql + newSt + " DESC ";
                    } else {
                        sql = sql + column.getColumnName() + " DESC ";
                    }
                }

                k++;
            }
        }

        sql = sql + " LIMIT  " + LimitAmount;
        sql = sql + " OFFSET " + (Integer.parseInt(LimitAmount) * Integer.parseInt(MoreCallData));

        callMethod.Log(sql);

        Cursor localCursor = null;

        try {
            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {
                while (localCursor.moveToNext()) {
                    Good itemGood = new Good();

                    for (Column column : localColumns) {
                        try {
                            switch (column.getColumnType()) {
                                case "0":
                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            localCursor.getString(localCursor.getColumnIndex(column.getColumnName()))
                                    );
                                    break;

                                case "1":
                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(localCursor.getInt(localCursor.getColumnIndex(column.getColumnName())))
                                    );
                                    break;

                                case "2":
                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(localCursor.getFloat(localCursor.getColumnIndex(column.getColumnName())))
                                    );
                                    break;
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    itemGood.setCheck(false);

                    try {
                        itemGood.setGoodFieldValue(
                                "ActiveStack",
                                String.valueOf(localCursor.getInt(localCursor.getColumnIndex("ActiveStack")))
                        );
                    } catch (Exception ignored) {
                    }

                    resultGoods.add(itemGood);
                }
            }

        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        } finally {
            closeCursor(localCursor);
        }

        return resultGoods;
    }


    @SuppressLint("Range")
    public synchronized ArrayList<Good> getAllGood_Extended(String searchbox_result, String aGroupCode, String MoreCallData) {
        ArrayList<Good> resultGoods = new ArrayList<>();
        GetPreference();

        ArrayList<Column> localColumns = GetColumns("", "", "1");

        String sql = "With FilterTable As (Select 0 as SecondField) SELECT ";

        int selectCount = 0;

        for (Column column : localColumns) {
            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }

            if (!column.getColumnName().equals("")) {
                if (selectCount != 0) {
                    sql = sql + " , ";
                }

                if (!column.getColumnDefinition().equals("")) {
                    sql = sql + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    sql = sql + column.getColumnName();
                }

                selectCount++;
            }
        }

        sql = sql + " FROM Good g , FilterTable ";
        sql = sql + " Where  1=1 ";
        sql = sql + searchbox_result;
        sql = sql + " And Exists(Select 1 From GoodStack stackCondition ActiveCondition And GoodRef=GoodCode AmountCondition)";

        if (SH_activestack) {
            sql = sql.replaceAll("ActiveCondition", " And ActiveStack = 1 ");
        } else {
            sql = sql.replaceAll("ActiveCondition", " ");
        }

        if (SH_goodamount) {
            sql = sql.replaceAll("AmountCondition", " GROUP BY GoodRef HAVING " + StackAmountString + " > 0 ");
        } else {
            sql = sql.replaceAll("AmountCondition", " ");
        }

        sql = sql.replaceAll("stackCondition", BrokerStackString);
        sql = sql.replaceAll("SearchCondition", Search_Condition);

        try {
            Integer.parseInt(aGroupCode);
        } catch (Exception e) {
            aGroupCode = "0";
        }

        if (Integer.parseInt(aGroupCode) > 0) {
            sql = sql + " And GoodCode in(Select GoodRef From GoodGroup p "
                    + "Join GoodsGrp s on p.GoodGroupRef = s.GroupCode "
                    + "Where s.GroupCode = " + aGroupCode + " or s.L1 = " + aGroupCode
                    + " or s.L2 = " + aGroupCode
                    + " or s.L3 = " + aGroupCode
                    + " or s.L4 = " + aGroupCode
                    + " or s.L5 = " + aGroupCode + ")";
        }

        sql = sql + " order by ";

        int k = 0;

        for (Column column : localColumns) {
            if (!column.getOrderIndex().equals("0")) {
                if (k != 0) {
                    sql = sql + " , ";
                }

                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(
                                column.getColumnDefinition().indexOf("Then") + 5,
                                column.getColumnDefinition().indexOf("Then") + 12
                        );
                        sql = sql + newSt;
                    } else {
                        sql = sql + column.getColumnName();
                    }
                } else {
                    if (column.getColumnName().equals("Date")) {
                        String newSt = column.getColumnDefinition().substring(
                                column.getColumnDefinition().indexOf("Then") + 5,
                                column.getColumnDefinition().indexOf("Then") + 12
                        );
                        sql = sql + newSt + " DESC ";
                    } else {
                        sql = sql + column.getColumnName() + " DESC ";
                    }
                }

                k++;
            }
        }

        sql = sql + " LIMIT  " + LimitAmount;
        sql = sql + " OFFSET " + (Integer.parseInt(LimitAmount) * Integer.parseInt(MoreCallData));

        callMethod.Log(sql);

        Cursor localCursor = null;

        try {
            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {
                while (localCursor.moveToNext()) {
                    Good itemGood = new Good();

                    for (Column column : localColumns) {
                        try {
                            switch (column.getColumnType()) {
                                case "0":
                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            localCursor.getString(localCursor.getColumnIndex(column.getColumnName()))
                                    );
                                    break;

                                case "1":
                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(localCursor.getInt(localCursor.getColumnIndex(column.getColumnName())))
                                    );
                                    break;

                                case "2":
                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(localCursor.getFloat(localCursor.getColumnIndex(column.getColumnName())))
                                    );
                                    break;
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    itemGood.setCheck(false);

                    try {
                        itemGood.setGoodFieldValue(
                                "ActiveStack",
                                localCursor.getString(localCursor.getColumnIndex("ActiveStack"))
                        );
                    } catch (Exception ignored) {
                    }

                    resultGoods.add(itemGood);
                }
            }

        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        } finally {
            closeCursor(localCursor);
        }

        return resultGoods;
    }
    @SuppressLint("Range")
    public synchronized ArrayList<Good> getAllGood_ByDate(String xDayAgo, String MoreCallData) {
        ArrayList<Good> resultGoods = new ArrayList<>();
        GetPreference();

        ArrayList<Column> localColumns = GetColumns("", "", "1");

        String sql = "  With FilterTable As (Select 1 as SecondField) SELECT ";

        int selectCount = 0;

        for (Column column : localColumns) {
            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Sum"),
                        column.getColumnDefinition().indexOf(")") + 1
                );
            }

            if (!column.getColumnName().equals("")) {
                if (selectCount != 0) {
                    sql = sql + " , ";
                }

                if (!column.getColumnDefinition().equals("")) {
                    sql = sql + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    sql = sql + column.getColumnName();
                }

                selectCount++;
            }
        }

        String newSt = "Date";

        for (Column column : localColumns) {
            if (column.getColumnName().equals("Date")) {
                newSt = column.getColumnDefinition().substring(
                        column.getColumnDefinition().indexOf("Else") + 4,
                        column.getColumnDefinition().indexOf("Else") + 12
                );
            }
        }

        sql = sql + " FROM Good g , FilterTable Where " + newSt + ">='" + xDayAgo + "' ";
        sql = sql + " And Exists(Select 1 From GoodStack stackCondition ActiveCondition And GoodRef=GoodCode AmountCondition)";

        if (SH_activestack) {
            sql = sql.replaceAll("ActiveCondition", " And ActiveStack = 1 ");
        } else {
            sql = sql.replaceAll("ActiveCondition", " ");
        }

        if (SH_goodamount) {
            sql = sql.replaceAll("AmountCondition", " GROUP BY GoodRef HAVING " + StackAmountString + " > 0 ");
        } else {
            sql = sql.replaceAll("AmountCondition", " ");
        }

        sql = sql.replaceAll("stackCondition", BrokerStackString);
        sql = sql + " order by ";

        int k = 0;

        for (Column column : localColumns) {
            if (!column.getOrderIndex().equals("0")) {
                if (k != 0) {
                    sql = sql + " , ";
                }

                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    if (column.getColumnName().equals("Date")) {
                        newSt = column.getColumnDefinition().substring(
                                column.getColumnDefinition().indexOf("Else") + 4,
                                column.getColumnDefinition().indexOf("Else") + 12
                        );
                        sql = sql + newSt;
                    } else {
                        sql = sql + column.getColumnName();
                    }
                } else {
                    if (column.getColumnName().equals("Date")) {
                        newSt = column.getColumnDefinition().substring(
                                column.getColumnDefinition().indexOf("Else") + 4,
                                column.getColumnDefinition().indexOf("Else") + 12
                        );
                        sql = sql + newSt + " DESC ";
                    } else {
                        sql = sql + column.getColumnName() + " DESC ";
                    }
                }

                k++;
            }
        }

        sql = sql + " LIMIT  " + LimitAmount;
        sql = sql + " OFFSET " + (Integer.parseInt(LimitAmount) * Integer.parseInt(MoreCallData));

        callMethod.Log(sql);

        resultGoods = new ArrayList<>();
        Cursor localCursor = null;

        try {
            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {
                while (localCursor.moveToNext()) {
                    Good itemGood = new Good();

                    for (Column column : localColumns) {
                        try {
                            switch (column.getColumnType()) {
                                case "0":
                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            localCursor.getString(localCursor.getColumnIndex(column.getColumnName()))
                                    );
                                    break;

                                case "1":
                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(localCursor.getInt(localCursor.getColumnIndex(column.getColumnName())))
                                    );
                                    break;

                                case "2":
                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(localCursor.getFloat(localCursor.getColumnIndex(column.getColumnName())))
                                    );
                                    break;
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    itemGood.setCheck(false);

                    try {
                        itemGood.setGoodFieldValue(
                                "ActiveStack",
                                localCursor.getString(localCursor.getColumnIndex("ActiveStack"))
                        );
                    } catch (Exception ignored) {
                    }

                    resultGoods.add(itemGood);
                }
            }

        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        } finally {
            closeCursor(localCursor);
        }

        return resultGoods;
    }

    @SuppressLint("Range")
    public synchronized Good getGoodByCode(String code) {

        if (code == null || code.trim().isEmpty()) {
            callMethod.Log("getGoodByCode SKIP => GoodCode is EMPTY");
            return new Good();
        }

        GetPreference();

        ArrayList<Column> localColumns = GetColumns(code, "", "0");

        String sql = "With FilterTable As (Select 0 as SecondField) SELECT ";

        int k = 0;

        for (Column column : localColumns) {

            if (column.getColumnDefinition().indexOf("Sum") > 0) {

                StackAmountString =
                        column.getColumnDefinition().substring(
                                column.getColumnDefinition().indexOf("Sum"),
                                column.getColumnDefinition().indexOf(")") + 1
                        );
            }

            if (!column.getColumnName().equals("ksrImageCode")) {

                if (k != 0) {
                    sql = sql + " , ";
                }

                if (!column.getColumnDefinition().equals("")) {
                    sql = sql + column.getColumnDefinition() + " as " + column.getColumnName();
                } else {
                    sql = sql + column.getColumnName();
                }

                k++;
            }
        }

        sql = sql + joinDetail;

        Search_Condition = "'%%'";

        sql = sql.replaceAll("stackCondition", BrokerStackString);
        sql = sql.replaceAll("SearchCondition", Search_Condition);

        sql = sql + " WHERE GoodCode = ?";

        callMethod.Log(sql);

        Good resultGood = new Good();

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, new String[]{code.trim()});

            if (localCursor != null && localCursor.moveToFirst()) {

                for (Column column : localColumns) {

                    try {

                        switch (column.getColumnType()) {

                            case "0":

                                resultGood.setGoodFieldValue(
                                        column.getColumnName(),
                                        localCursor.getString(
                                                localCursor.getColumnIndex(
                                                        column.getColumnName()
                                                )
                                        )
                                );

                                break;

                            case "1":

                                resultGood.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(
                                                localCursor.getInt(
                                                        localCursor.getColumnIndex(
                                                                column.getColumnName()
                                                        )
                                                )
                                        )
                                );

                                break;

                            case "2":

                                resultGood.setGoodFieldValue(
                                        column.getColumnName(),
                                        String.valueOf(
                                                localCursor.getFloat(
                                                        localCursor.getColumnIndex(
                                                                column.getColumnName()
                                                        )
                                                )
                                        )
                                );

                                break;
                        }

                    } catch (Exception ignored) {
                    }
                }

                resultGood.setCheck(false);

                try {

                    resultGood.setGoodFieldValue(
                            "ActiveStack",
                            localCursor.getString(
                                    localCursor.getColumnIndex("ActiveStack")
                            )
                    );

                } catch (Exception ignored) {
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultGood;
    }

    @SuppressLint("Range")
    public ArrayList<Activation> getActivation() {

        callMethod.Log("db=start");

        String sql = "Select * From Activation";

        ArrayList<Activation> activations =
                new ArrayList<>();

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    Activation activation =
                            new Activation();

                    try {

                        activation.setAppBrokerCustomerCode(
                                localCursor.getString(
                                        localCursor.getColumnIndex(
                                                "AppBrokerCustomerCode"
                                        )
                                )
                        );

                        activation.setActivationCode(
                                localCursor.getString(
                                        localCursor.getColumnIndex(
                                                "ActivationCode"
                                        )
                                )
                        );

                        activation.setPersianCompanyName(
                                localCursor.getString(
                                        localCursor.getColumnIndex(
                                                "PersianCompanyName"
                                        )
                                )
                        );

                        activation.setEnglishCompanyName(
                                localCursor.getString(
                                        localCursor.getColumnIndex(
                                                "EnglishCompanyName"
                                        )
                                )
                        );

                        activation.setServerURL(
                                localCursor.getString(
                                        localCursor.getColumnIndex(
                                                "ServerURL"
                                        )
                                )
                        );

                        activation.setSQLiteURL(
                                localCursor.getString(
                                        localCursor.getColumnIndex(
                                                "SQLiteURL"
                                        )
                                )
                        );

                        activation.setMaxDevice(
                                localCursor.getString(
                                        localCursor.getColumnIndex(
                                                "MaxDevice"
                                        )
                                )
                        );

                        activation.setSecendServerURL(
                                localCursor.getString(
                                        localCursor.getColumnIndex(
                                                "SecendServerURL"
                                        )
                                )
                        );

                        activation.setDbName(
                                localCursor.getString(
                                        localCursor.getColumnIndex(
                                                "DbName"
                                        )
                                )
                        );

                        activation.setAppType(
                                localCursor.getString(
                                        localCursor.getColumnIndex(
                                                "AppType"
                                        )
                                )
                        );

                    } catch (Exception ignored) {

                        callMethod.Log(
                                "db=" + ignored.getMessage()
                        );
                    }

                    activations.add(activation);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return activations;
    }

    @SuppressLint("Range")
    public synchronized Good getGoodBuyBox(String code) {

        if (code == null || code.trim().isEmpty()) {
            callMethod.Log("getGoodBuyBox SKIP => GoodCode is EMPTY");
            return new Good();
        }

        GetPreference();

        String sql =
                " SELECT " +
                        " IfNull(pf.FactorAmount, 0) AS FactorAmount, " +
                        " g.DefaultUnitValue, " +
                        " u.UnitName, " +
                        " IfNull(pf.Price, 0) AS Price, " +
                        " g.SellPriceType, " +
                        " g.MaxSellPrice, " +

                        " CASE " +
                        "   WHEN g.SellPriceType = 0 THEN " +
                        "       CASE c.PriceTip " +
                        "           WHEN 1 THEN IfNull(g.SellPrice1, g.MaxSellPrice) " +
                        "           WHEN 2 THEN IfNull(g.SellPrice2, g.MaxSellPrice) " +
                        "           WHEN 3 THEN IfNull(g.SellPrice3, g.MaxSellPrice) " +
                        "           WHEN 4 THEN IfNull(g.SellPrice4, g.MaxSellPrice) " +
                        "           WHEN 5 THEN IfNull(g.SellPrice5, g.MaxSellPrice) " +
                        "           WHEN 6 THEN IfNull(g.SellPrice6, g.MaxSellPrice) " +
                        "           ELSE g.MaxSellPrice " +
                        "       END " +
                        "   ELSE " +
                        "       (CASE c.PriceTip " +
                        "           WHEN 1 THEN IfNull(g.SellPrice1, 100) " +
                        "           WHEN 2 THEN IfNull(g.SellPrice2, 100) " +
                        "           WHEN 3 THEN IfNull(g.SellPrice3, 100) " +
                        "           WHEN 4 THEN IfNull(g.SellPrice4, 100) " +
                        "           WHEN 5 THEN IfNull(g.SellPrice5, 100) " +
                        "           WHEN 6 THEN IfNull(g.SellPrice6, 100) " +
                        "           ELSE 100 " +
                        "       END) * g.MaxSellPrice / 100.0 " +
                        " END AS SellPrice " +

                        " FROM Good g " +

                        " JOIN Units u " +
                        " ON u.UnitCode = g.GoodUnitRef " +

                        " LEFT JOIN (" +
                        "   SELECT " +
                        "       GoodRef, " +
                        "       SUM(FactorAmount) AS FactorAmount, " +
                        "       SUM(FactorAmount * Price) AS Price " +
                        "   FROM PreFactorRow " +
                        "   WHERE PreFactorRef = " + SH_prefactor_code +
                        "   GROUP BY GoodRef " +
                        " ) pf ON pf.GoodRef = g.GoodCode " +

                        " LEFT JOIN PreFactor h " +
                        " ON h.PreFactorCode = " + SH_prefactor_code + " " +

                        " LEFT JOIN Customer c " +
                        " ON c.CustomerCode = h.CustomerRef " +

                        " WHERE g.GoodCode = ?";

        callMethod.Log(sql);

        Good resultGood = new Good();
        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, new String[]{code.trim()});

            if (localCursor != null && localCursor.moveToFirst()) {

                resultGood.setGoodFieldValue(
                        "FactorAmount",
                        localCursor.getString(localCursor.getColumnIndex("FactorAmount"))
                );

                resultGood.setGoodFieldValue(
                        "UnitName",
                        localCursor.getString(localCursor.getColumnIndex("UnitName"))
                );

                resultGood.setGoodFieldValue(
                        "Price",
                        localCursor.getString(localCursor.getColumnIndex("Price"))
                );

                resultGood.setGoodFieldValue(
                        "MaxSellPrice",
                        String.valueOf(
                                localCursor.getLong(
                                        localCursor.getColumnIndex("MaxSellPrice")
                                )
                        )
                );

                resultGood.setGoodFieldValue(
                        "SellPrice",
                        String.valueOf(
                                localCursor.getLong(
                                        localCursor.getColumnIndex("SellPrice")
                                )
                        )
                );

                resultGood.setGoodFieldValue(
                        "SellPriceType",
                        String.valueOf(
                                localCursor.getLong(
                                        localCursor.getColumnIndex("SellPriceType")
                                )
                        )
                );

                resultGood.setGoodFieldValue(
                        "DefaultUnitValue",
                        String.valueOf(
                                localCursor.getLong(
                                        localCursor.getColumnIndex("DefaultUnitValue")
                                )
                        )
                );
            }

        } catch (Exception e) {

            callMethod.Log(
                    "getGoodBuyBox ERROR => " + e.getMessage()
            );

        } finally {

            closeCursor(localCursor);
        }

        return resultGood;
    }
    @SuppressLint("Range")
    public Good getGoodBuyBox1(String code) {
        // متد قدیمی نگه داشته شده تا Call Siteهای قبلی نشکنند،
        // ولی محاسبه قیمت فقط از یک مسیر انجام می‌شود.
        return getGoodBuyBox(code);
    }
    @SuppressLint("Range")
    public Good getGooddata(String code) {

        if (code == null || code.trim().isEmpty()) {
            callMethod.Log("getGooddata SKIP => GoodCode is EMPTY");
            return new Good();
        }


        String sql = "SELECT * FROM Good g WHERE GoodCode = ?";

        callMethod.Log(sql);

        Good good_data = new Good();

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, new String[]{code.trim()});

            if (localCursor != null && localCursor.moveToFirst()) {

                good_data.setGoodFieldValue("GoodCode", localCursor.getString(localCursor.getColumnIndex("GoodCode")));
                good_data.setGoodFieldValue("SellPriceType", localCursor.getString(localCursor.getColumnIndex("SellPriceType")));
                good_data.setGoodFieldValue("MaxSellPrice", localCursor.getString(localCursor.getColumnIndex("MaxSellPrice")));
                good_data.setGoodFieldValue("MinSellPrice", localCursor.getString(localCursor.getColumnIndex("MinSellPrice")));
                good_data.setGoodFieldValue("SellPrice1", localCursor.getString(localCursor.getColumnIndex("SellPrice1")));
                good_data.setGoodFieldValue("SellPrice2", localCursor.getString(localCursor.getColumnIndex("SellPrice2")));
                good_data.setGoodFieldValue("SellPrice3", localCursor.getString(localCursor.getColumnIndex("SellPrice3")));
                good_data.setGoodFieldValue("SellPrice4", localCursor.getString(localCursor.getColumnIndex("SellPrice4")));
                good_data.setGoodFieldValue("SellPrice5", localCursor.getString(localCursor.getColumnIndex("SellPrice5")));
                good_data.setGoodFieldValue("SellPrice6", localCursor.getString(localCursor.getColumnIndex("SellPrice6")));
                good_data.setGoodFieldValue("GoodUnitRef", localCursor.getString(localCursor.getColumnIndex("GoodUnitRef")));
                good_data.setGoodFieldValue("DefaultUnitValue", localCursor.getString(localCursor.getColumnIndex("DefaultUnitValue")));
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return good_data;
    }

    @SuppressLint("Range")
    public void InsertPreFactorHeader(
            String Search_target,
            String CustomerRef
    ) {

        String Customer =
                GetRegionText(Search_target);

        String Date =
                Utilities.getCurrentShamsidate();

        Calendar calendar =
                Calendar.getInstance();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf =
                new SimpleDateFormat("HH:mm:ss");

        String strDate =
                sdf.format(calendar.getTime());

        UserInfo user =
                new UserInfo();

        String sql =
                "Select * From Config Where KeyValue = 'BrokerCode' ";

        String key;
        String val = "";

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    key =
                            localCursor.getString(
                                    localCursor.getColumnIndex("KeyValue")
                            );

                    val =
                            localCursor.getString(
                                    localCursor.getColumnIndex("DataValue")
                            );

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

            db().execSQL(
                    "INSERT INTO Prefactor" +
                            "(PreFactorKowsarCode,PreFactorDate ,PreFactorKowsarDate ,PreFactorTime,PreFactorExplain,CustomerRef,BrokerRef) " +
                            "VALUES(0,'" + Date + "','-----','" + strDate + "','" + Customer + "','" + CustomerRef + "','" + val + "'); "
            );

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }
    }
    @SuppressLint("Range")
    public void InsertPreFactor(
            String pfcode,
            String goodcode,
            String FactorAmount,
            String price,
            String BasketFlag
    ) {

        Cursor localCursor = null;
        String sql;

        try {

            if (Integer.parseInt(BasketFlag) > 0) {

                if (Float.parseFloat(price) >= 0) {

                    sql =
                            "Update PreFactorRow set FactorAmount = " +
                                    FactorAmount +
                                    ", Price = " +
                                    price +
                                    " Where PreFactorRowCode=" +
                                    BasketFlag;

                } else {

                    sql =
                            "Update PreFactorRow set FactorAmount = " +
                                    FactorAmount +
                                    " Where PreFactorRowCode=" +
                                    BasketFlag;
                }

                db().execSQL(sql);

            } else {

                sql =
                        " Select * From PreFactorRow Where IfNull(PreFactorRef,0)=" +
                                pfcode +
                                " And GoodRef =" +
                                goodcode;

                if (Float.parseFloat(price) >= 0) {
                    sql = sql + " And Price =" + price;
                }

                localCursor = db().rawQuery(sql, null);

                if (localCursor != null && localCursor.moveToFirst()) {

                    db().execSQL(
                            "Update PreFactorRow set FactorAmount = FactorAmount +" +
                                    FactorAmount +
                                    " Where PreFactorRowCode=" +
                                    localCursor.getString(
                                            localCursor.getColumnIndex(
                                                    "PreFactorRowCode"
                                            )
                                    ) +
                                    ";"
                    );

                } else {

                    sql =
                            "INSERT INTO PreFactorRow(PreFactorRef, GoodRef, FactorAmount, Price) "
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

                    callMethod.Log(sql);

                    db().execSQL(sql);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }
    }
    @SuppressLint("Range")
    public void InsertPreFactorwithPercent(
            String pfcode,
            String goodcode,
            String FactorAmount,
            String price,
            String BasketFlag
    ) {

        Cursor localCursor = null;
        String sql;

        try {

            if (Integer.parseInt(BasketFlag) > 0) {

                if (Float.parseFloat(price) >= 0) {

                    sql =
                            "Update PreFactorRow set FactorAmount = " +
                                    FactorAmount +
                                    ", Price = " +
                                    price +
                                    " Where PreFactorRowCode=" +
                                    BasketFlag;

                } else {

                    sql =
                            "Update PreFactorRow set FactorAmount = " +
                                    FactorAmount +
                                    " Where PreFactorRowCode=" +
                                    BasketFlag;
                }

                db().execSQL(sql);

            } else {

                sql =
                        " Select * From PreFactorRow Where IfNull(PreFactorRef,0)=" +
                                pfcode +
                                " And GoodRef =" +
                                goodcode;

                if (Float.parseFloat(price) >= 0) {
                    sql = sql + " And Price =" + price;
                }

                localCursor = db().rawQuery(sql, null);

                if (localCursor != null && localCursor.moveToFirst()) {

                    db().execSQL(
                            "Update PreFactorRow set FactorAmount = FactorAmount +" +
                                    FactorAmount +
                                    " Where PreFactorRowCode=" +
                                    localCursor.getString(
                                            localCursor.getColumnIndex(
                                                    "PreFactorRowCode"
                                            )
                                    ) +
                                    ";"
                    );

                } else {

                    sql =
                            "INSERT INTO PreFactorRow(PreFactorRef, GoodRef, FactorAmount, Price) "
                                    + "select PreFactorCode ,GoodCode," + FactorAmount + "," + price
                                    + " From PreFactor "
                                    + " Join Good g on GoodCode=" + goodcode
                                    + " Where PreFactorCode=" + pfcode + " Limit 1 ";

                    callMethod.Log(sql);

                    db().execSQL(sql);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }
    }

    @SuppressLint("Range")
    public ArrayList<PreFactor> getAllPrefactorHeader(String Search_target) {

        String name = GetRegionText(Search_target);

        String sql = " SELECT h.*, s.SumAmount , s.SumPrice , s.RowCount ,n.Title || ' ' || n.FName|| ' ' || n.Name CustomerName FROM PreFactor h Join Customer c  on c.CustomerCode = h.CustomerRef " +
                " join Central n on c.CentralRef=n.CentralCode "
                + " Left Join (SELECT P.PreFactorRef, sum(p.FactorAmount) as SumAmount , sum(p.FactorAmount * p.Price*g.DefaultUnitValue) as SumPrice, count(*) as RowCount "
                + " From Good g Join Units on UnitCode = GoodUnitRef  Join PreFactorRow p on GoodRef = GoodCode  Where IfNull(PreFactorRef, 0)>0 "
                + " Group BY PreFactorRef ) s on h.PreFactorCode = s.PreFactorRef "
                + " Where Replace(Replace(CustomerName,char(1740),char(1610)),char(1705),char(1603)) Like '%" + name + "%'"
                + " Order By h.PreFactorCode DESC";

        ArrayList<PreFactor> prefactor_header = new ArrayList<>();

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    PreFactor prefactor = new PreFactor();

                    try {

                        prefactor.setPreFactorCode(localCursor.getInt(localCursor.getColumnIndex("PreFactorCode")));
                        prefactor.setPreFactorDate(localCursor.getString(localCursor.getColumnIndex("PreFactorDate")));
                        prefactor.setPreFactorTime(localCursor.getString(localCursor.getColumnIndex("PreFactorTime")));
                        prefactor.setPreFactorkowsarDate(localCursor.getString(localCursor.getColumnIndex("PreFactorKowsarDate")));
                        prefactor.setPreFactorKowsarCode(localCursor.getInt(localCursor.getColumnIndex("PreFactorKowsarCode")));
                        prefactor.setPreFactorExplain(localCursor.getString(localCursor.getColumnIndex("PreFactorExplain")));
                        prefactor.setCustomer(localCursor.getString(localCursor.getColumnIndex("CustomerName")));
                        prefactor.setSumAmount(localCursor.getInt(localCursor.getColumnIndex("SumAmount")));
                        prefactor.setSumPrice(localCursor.getInt(localCursor.getColumnIndex("SumPrice")));
                        prefactor.setRowCount(localCursor.getInt(localCursor.getColumnIndex("RowCount")));

                    } catch (Exception ignored) {
                    }

                    prefactor_header.add(prefactor);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return prefactor_header;
    }
    @SuppressLint("Range")
    public ArrayList<PreFactor> getAllPrefactorHeaderopen() {

        String sql = "SELECT h.*, s.SumAmount , s.SumPrice, s.RowCount ,n.Title || ' ' || n.FName|| ' ' || n.Name CustomerName  " +
                "FROM PreFactor h Join Customer c  on c.CustomerCode = h.CustomerRef "
                + " join Central n on c.CentralRef=n.CentralCode "
                + "Left Join (SELECT P.PreFactorRef, sum(p.FactorAmount) as SumAmount , sum(p.FactorAmount * p.Price*g.DefaultUnitValue) as SumPrice, count(*) as RowCount "
                + "From Good g Join Units on UnitCode = GoodUnitRef  Join PreFactorRow p on GoodRef = GoodCode  Where IfNull(PreFactorRef, 0)>0 "
                + "Group BY PreFactorRef ) s on h.PreFactorCode = s.PreFactorRef Where NOT IfNull(PreFactorKowsarCode, 0)>0 "
                + "Order By h.PreFactorCode DESC";

        ArrayList<PreFactor> prefactor_header = new ArrayList<>();

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    PreFactor prefactor = new PreFactor();

                    try {

                        prefactor.setPreFactorCode(localCursor.getInt(localCursor.getColumnIndex("PreFactorCode")));
                        prefactor.setPreFactorDate(localCursor.getString(localCursor.getColumnIndex("PreFactorDate")));
                        prefactor.setPreFactorTime(localCursor.getString(localCursor.getColumnIndex("PreFactorTime")));
                        prefactor.setPreFactorkowsarDate(localCursor.getString(localCursor.getColumnIndex("PreFactorKowsarDate")));
                        prefactor.setPreFactorKowsarCode(localCursor.getInt(localCursor.getColumnIndex("PreFactorKowsarCode")));
                        prefactor.setPreFactorExplain(localCursor.getString(localCursor.getColumnIndex("PreFactorExplain")));
                        prefactor.setCustomer(localCursor.getString(localCursor.getColumnIndex("CustomerName")));
                        prefactor.setSumAmount(localCursor.getInt(localCursor.getColumnIndex("SumAmount")));
                        prefactor.setSumPrice(localCursor.getDouble(localCursor.getColumnIndex("SumPrice")));
                        prefactor.setRowCount(localCursor.getInt(localCursor.getColumnIndex("RowCount")));

                    } catch (Exception ignored) {
                    }

                    prefactor_header.add(prefactor);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return prefactor_header;
    }
    @SuppressLint("Range")
    public ArrayList<Good> getAllPreFactorRows(
            String Search_target,
            String aPreFactorCode
    ) {

        ArrayList<Good> resultGoods = new ArrayList<>();

        String name = GetRegionText(Search_target);

        name = name.replaceAll(" ", "%");


        ArrayList<Column> localColumns = GetColumns("", "", "2");

        String sql = "SELECT ";

        int k = 0;

        for (Column column : localColumns) {

            if (k != 0) {
                sql = sql + " , ";
            }

            if (!column.getColumnDefinition().equals("")) {

                sql =
                        sql +
                                column.getColumnDefinition() +
                                " as " +
                                column.getColumnName();

            } else {

                sql =
                        sql +
                                column.getColumnName();
            }

            k++;
        }

        sql =
                sql +
                        " FROM Good g  " +
                        "Join PreFactorRow pf on GoodRef = GoodCode " +
                        "Join Units u on u.UnitCode = g.GoodUnitRef  " +
                        "Where (Replace(Replace(GoodName,char(1740),char(1610)),char(1705),char(1603)) Like '%" +
                        name +
                        "%' and PreFactorRef = " +
                        aPreFactorCode +
                        ") order by PreFactorRowCode DESC ";

        callMethod.Log(sql);

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    Good itemGood = new Good();

                    for (Column column : localColumns) {

                        try {

                            switch (column.getColumnType()) {

                                case "0":

                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            localCursor.getString(
                                                    localCursor.getColumnIndex(
                                                            column.getColumnName()
                                                    )
                                            )
                                    );

                                    break;

                                case "1":

                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(
                                                    localCursor.getInt(
                                                            localCursor.getColumnIndex(
                                                                    column.getColumnName()
                                                            )
                                                    )
                                            )
                                    );

                                    break;

                                case "2":

                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(
                                                    localCursor.getFloat(
                                                            localCursor.getColumnIndex(
                                                                    column.getColumnName()
                                                            )
                                                    )
                                            )
                                    );

                                    break;
                            }

                        } catch (Exception ignored) {
                        }
                    }

                    resultGoods.add(itemGood);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultGoods;
    }
    @SuppressLint("Range")
    public void UpdatePreFactorHeader_Customer(
            String pfcode,
            String Search_target
    ) {

        String Customer =
                GetRegionText(Search_target);

        Cursor updateCursor = null;

        try {

            String sql =
                    "Update Prefactor set CustomerRef='" +
                            Customer +
                            "' where PreFactorCode = " +
                            pfcode;

            db().execSQL(sql);

            sql =
                    "Select * From ( Select Case PriceTip " +
                            "When 1 Then  SellPrice1 When 2 Then SellPrice2 When 3 Then SellPrice3  " +
                            "When 4 Then   SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 " +
                            "Else  Case When g.SellPriceType = 0 Then MaxSellPrice Else 100 End End * " +
                            " Case When g.SellPriceType = 0 Then 1 Else MaxSellPrice/100 End as " +
                            "NewPrice, Price, GoodCode From PreFactorRow p " +
                            "Join PreFactor h on h.PreFactorCode = p.PreFactorRef " +
                            "Join Customer on CustomerCode = CustomerRef " +
                            "Join Good g on GoodRef = GoodCode Where h.PreFactorCode = " +
                            pfcode +
                            ") ss " +
                            "Where Price<> NewPrice";

            updateCursor =
                    db().rawQuery(sql, null);

            if (updateCursor != null) {

                while (updateCursor.moveToNext()) {

                    db().execSQL(
                            "Update PreFactorRow set Price=" +
                                    updateCursor.getString(
                                            updateCursor.getColumnIndex("NewPrice")
                                    ) +
                                    " Where PreFactorRef =" +
                                    pfcode +
                                    " And GoodRef =" +
                                    updateCursor.getString(
                                            updateCursor.getColumnIndex("GoodCode")
                                    )
                    );
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(updateCursor);
        }
    }
    @SuppressLint("Range")
    public Integer GetLastPreFactorHeader() {

        String sql =
                "SELECT PreFactorCode FROM Prefactor " +
                        "Where PreFactorKowsarCode = 0 " +
                        "order by PreFactorCode DESC";

        int Res = 0;

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null && localCursor.moveToFirst()) {

                Res =
                        localCursor.getInt(
                                localCursor.getColumnIndex(
                                        "PreFactorCode"
                                )
                        );
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return Res;
    }
    public void update_explain(
            String pfcode,
            String explain
    ) {

        try {

            String sql =
                    "Update PreFactor set PreFactorExplain = '" +
                            explain +
                            "' Where IfNull(PreFactorCode,0)=" +
                            pfcode;

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    public void DeletePreFactorRow(String pfcode, String rowcode) {

        try {

            String sql =
                    " Delete From PreFactorRow Where IfNull(PreFactorRef,0)=" +  pfcode +
                            " And (PreFactorRowCode =" + rowcode +" or 0=" +  rowcode +  ")";

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    public void DeletePreFactor(String pfcode) {

        try {

            String sql =
                    " Delete From Prefactor Where IfNull(PreFactorCode,0)=" +
                            pfcode;

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    public void DeleteEmptyPreFactor() {

        try {

            String sql =
                    " DELETE FROM Prefactor WHERE PreFactorCode NOT IN (SELECT PreFactorRef FROM PrefactorRow )";

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    public void UpdatePreFactor(
            String PreFactorCode,
            String PreFactorKowsarCode,
            String PreFactorDate
    ) {

        try {

            String sql =
                    "Update PreFactor Set PreFactorKowsarCode = " +
                            PreFactorKowsarCode +
                            ", PreFactorKowsarDate = '" +
                            PreFactorDate +
                            "' Where ifnull(PreFactorCode ,0)= " +
                            PreFactorCode +
                            ";";

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    @SuppressLint("Range")
    public String getFactorSum(String pfcode) {

        String resultValue = "0";

        String sql =
                " select sum(FactorAmount*price*DefaultUnitValue) as result " +
                        " From PreFactorRow join Good on GoodRef=GoodCode " +
                        " Where IfNull(PreFactorRef,0)=" +
                        pfcode;

        Cursor localCursor = null;

        try {

            localCursor =
                    db().rawQuery(sql, null);

            if (localCursor != null && localCursor.moveToFirst()) {

                resultValue =
                        String.valueOf(
                                localCursor.getDouble(
                                        localCursor.getColumnIndex("result")
                                )
                        );
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultValue;
    }


    @SuppressLint("Range")
    public String getFactorSumAmount(String pfcode) {

        String resultValue = "0";

        String sql =
                "select sum(FactorAmount) as result " +
                        "From PreFactorRow join Good on GoodRef=GoodCode " +
                        "Where IfNull(PreFactorRef,0)=" +
                        pfcode;

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null && localCursor.moveToFirst()) {

                resultValue =
                        String.valueOf(
                                localCursor.getInt(
                                        localCursor.getColumnIndex("result")
                                )
                        );
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultValue;
    }
    @SuppressLint("Range")
    public String getFactordate(String pfcode) {

        String resultValue = "";

        String sql =
                "select PreFactorDate as result " +
                        "From Prefactor " +
                        "Where IfNull(PreFactorCode,0)=" +
                        pfcode;

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null && localCursor.moveToFirst()) {

                resultValue =
                        localCursor.getString(
                                localCursor.getColumnIndex("result")
                        );
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultValue;
    }
    @SuppressLint("Range")
    public String getPricetipCustomer(String pfcode) {

        int resultint = 0;

        String sql =
                "SELECT PriceTip FROM PreFactor h " +
                        " Join Customer c on c.CustomerCode = h.CustomerRef " +
                        " join Central n on c.CentralRef=n.CentralCode " +
                        " Where IfNull(PreFactorCode,0)= " +
                        pfcode;

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null && localCursor.moveToFirst()) {

                resultint =
                        localCursor.getInt(
                                localCursor.getColumnIndex("PriceTip")
                        );

            } else {

                String resultValue = "فاکتوری انتخاب نشده";
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return String.valueOf(resultint);
    }
    @SuppressLint("Range")
    public String getFactorCustomer(String pfcode) {

        String resultValue = "فاکتوری انتخاب نشده";

        String sql =
                "SELECT n.Title || ' ' || n.FName|| ' ' || n.Name CustomerName FROM PreFactor h " +
                        " Join Customer c on c.CustomerCode = h.CustomerRef " +
                        " join Central n on c.CentralRef=n.CentralCode " +
                        " Where IfNull(PreFactorCode,0)= " +
                        pfcode;

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null && localCursor.moveToFirst()) {

                resultValue =
                        localCursor.getString(
                                localCursor.getColumnIndex("CustomerName")
                        );
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultValue;
    }


    @SuppressLint("Range")
    public ArrayList<Customer> AllCustomer(String search_target, boolean aOnlyActive) {

        String name = GetRegionText(search_target);
        name = name.replaceAll(" ", "%").replaceAll("'", "%");

        String sql = "SELECT u.CustomerCode,u.PriceTip,c.Title || ' ' || c.FName|| ' ' || c.Name CentralName,Address,Manager,Mobile,Phone,Delegacy,y.Name CityName, CustomerBestankar - CustomerBedehkar Bestankar, Active, CentralPrivateCode, EtebarNaghd" +
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
            sql = sql + " And Active = 0";
        }

        sql = sql + " order by CustomerCode DESC  LIMIT 200";

        ArrayList<Customer> Customers = new ArrayList<>();

        callMethod.Log(sql);

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    Customer customerdetail = new Customer();

                    try {
                        customerdetail.setCustomerCode(localCursor.getInt(localCursor.getColumnIndex("CustomerCode")));
                        customerdetail.setCustomerName(localCursor.getString(localCursor.getColumnIndex("CentralName")));
                        customerdetail.setManager(localCursor.getString(localCursor.getColumnIndex("Manager")));
                        customerdetail.setAddress(localCursor.getString(localCursor.getColumnIndex("Address")));
                        customerdetail.setPhone(localCursor.getString(localCursor.getColumnIndex("Phone")));
                        customerdetail.setBestankar(localCursor.getDouble(localCursor.getColumnIndex("Bestankar")));
                    } catch (Exception ignored) {
                    }

                    Customers.add(customerdetail);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return Customers;
    }
    @SuppressLint("Range")
    public Integer Customer_check(String name) {

        int res = 0;

        String sql = "select centralcode from central where d_codemelli ='" + name + "'";

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    res = localCursor.getInt(
                            localCursor.getColumnIndex("CentralCode")
                    );
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return res;
    }
    @SuppressLint("Range")
    public ArrayList<Customer> city() {

        String sql = "SELECT * from city";

        ArrayList<Customer> city = new ArrayList<>();

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    Customer customerdetail = new Customer();

                    try {
                        customerdetail.setCityName(localCursor.getString(localCursor.getColumnIndex("CityName")));
                        customerdetail.setCityCode(localCursor.getString(localCursor.getColumnIndex("CityCode")));
                    } catch (Exception ignored) {
                    }

                    city.add(customerdetail);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return city;
    }
    @SuppressLint("Range")
    public String GetksrImage(String code) {

        if (code == null || code.trim().isEmpty()) {
            callMethod.Log("GetksrImage SKIP => GoodCode is EMPTY");
            return "";
        }

        String resultValue = "";

        String sql =
                "SELECT KsrImageCode " +
                        "FROM KsrImage " +
                        "WHERE ObjectRef = ? " +
                        "ORDER BY IsDefaultImage DESC, KsrImageCode " +
                        "LIMIT 1";

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(
                    sql,
                    new String[]{code.trim()}
            );

            if (localCursor.moveToFirst()) {

                resultValue =
                        localCursor.getString(
                                localCursor.getColumnIndex("KsrImageCode")
                        );
            }

        } catch (Exception e) {

            callMethod.Log(
                    "GetksrImage ERROR => code=" +
                            code +
                            " | " +
                            e.getMessage()
            );

        } finally {

            closeCursor(localCursor);
        }

        return resultValue;
    }
    @SuppressLint("Range")
    public ArrayList<Good> GetksrImageCodes(String code) {

        ArrayList<Good> resultGoods = new ArrayList<>();

        if (code == null || code.trim().isEmpty()) {
            callMethod.Log("GetksrImageCodes SKIP => GoodCode is EMPTY");
            return resultGoods;
        }

        String sql =
                "SELECT KsrImageCode " +
                        "FROM KsrImage " +
                        "WHERE ObjectRef = ? " +
                        "ORDER BY IsDefaultImage DESC, KsrImageCode";

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(
                    sql,
                    new String[]{code.trim()}
            );

            while (localCursor.moveToNext()) {

                Good item = new Good();

                item.setGoodFieldValue(
                        "KsrImageCode",
                        localCursor.getString(
                                localCursor.getColumnIndex("KsrImageCode")
                        )
                );

                resultGoods.add(item);
            }

        } catch (Exception e) {

            callMethod.Log(
                    "GetksrImageCodes ERROR => code=" +
                            code +
                            " | " +
                            e.getMessage()
            );

        } finally {

            closeCursor(localCursor);
        }

        return resultGoods;
    }
    @SuppressLint("Range")
    public String GetLastksrImageCode(String code) {

        if (code == null || code.trim().isEmpty()) {
            callMethod.Log("GetLastksrImageCode SKIP => GoodCode is EMPTY");
            return "";
        }

        String resultValue = "";

        String sql =
                "SELECT KsrImageCode " +
                        "FROM KsrImage " +
                        "WHERE ObjectRef = ? " +
                        "ORDER BY IsDefaultImage DESC, KsrImageCode " +
                        "LIMIT 1";

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(
                    sql,
                    new String[]{code.trim()}
            );

            if (localCursor.moveToFirst()) {
                resultValue = localCursor.getString(
                        localCursor.getColumnIndex("KsrImageCode")
                );
            }

        } catch (Exception e) {

            callMethod.Log(
                    "GetLastksrImageCode ERROR => code=" +
                            code +
                            " | " +
                            e.getMessage()
            );

        } finally {
            closeCursor(localCursor);
        }

        return resultValue;
    }
    @SuppressLint("Range")
    public ArrayList<GoodGroup> getAllGroups(String Glstr) {

        String GL = "0";

        if (!Glstr.equals("")) {
            GL = Glstr;
        }

        String sql = "SELECT * ," +
                "case When L1=0 Then (Select Count(*) From GoodsGrp s Where s.L1=g.GroupCode) " +
                "When L2=0 Then (Select Count(*) From GoodsGrp s Where s.L2=g.GroupCode) " +
                "When L3=0 Then (Select Count(*) From GoodsGrp s Where s.L3=g.GroupCode) " +
                "When L4=0 Then (Select Count(*) From GoodsGrp s Where s.L4=g.GroupCode) " +
                "When L5=0 Then (Select Count(*) From GoodsGrp s Where s.L5=g.GroupCode) " +
                "Else 0 End  ChildNo " +
                " FROM GoodsGrp g WHERE 1=1 ";

        try {
            Integer.parseInt(GL);
        } catch (Exception e) {
            GL = "0";
        }

        if (Integer.parseInt(GL) > 0) {
            sql = sql + " And ((L1=" + GL + " And L2=0) or (L2=" + GL + " And L3=0) or (L3=" + GL + " And L4=0) or (L4=" + GL + " And L5=0) or (L5=" + GL + "))";
        } else {
            sql = sql + " order by 1 desc";
        }

        ArrayList<GoodGroup> groups = new ArrayList<>();

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    GoodGroup grp = new GoodGroup();

                    try {
                        grp.setGroupCode(localCursor.getInt(localCursor.getColumnIndex("GroupCode")));
                        grp.setName(localCursor.getString(localCursor.getColumnIndex("Name")));
                        grp.setL1(localCursor.getInt(localCursor.getColumnIndex("L1")));
                        grp.setL2(localCursor.getInt(localCursor.getColumnIndex("L2")));
                        grp.setL3(localCursor.getInt(localCursor.getColumnIndex("L3")));
                        grp.setL4(localCursor.getInt(localCursor.getColumnIndex("L4")));
                        grp.setL5(localCursor.getInt(localCursor.getColumnIndex("L5")));
                        grp.setChildNo(localCursor.getInt(localCursor.getColumnIndex("ChildNo")));
                    } catch (Exception ignored) {
                    }

                    groups.add(grp);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return groups;
    }
    @SuppressLint("Range")
    public synchronized ArrayList<GoodGroup> getmenuGroups() {

        GetPreference();

        String sql;

        if (!SH_MenuBroker.equals("")) {
            sql = "SELECT * FROM GoodsGrp Where Groupcode in (" + SH_MenuBroker + ")";
        } else {
            sql = "SELECT * FROM GoodsGrp Where Groupcode in (9999)";
        }

        ArrayList<GoodGroup> groups = new ArrayList<>();

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

            closeCursor(localCursor);

            localCursor = null;

            try {

                sql = "SELECT * FROM GoodsGrp Where Groupcode in (9999)";
                localCursor = db().rawQuery(sql, null);

            } catch (Exception ex) {

                callMethod.Log(ex.getMessage());
            }
        }

        try {

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    GoodGroup grp = new GoodGroup();

                    try {
                        grp.setGroupCode(localCursor.getInt(localCursor.getColumnIndex("GroupCode")));
                        grp.setName(localCursor.getString(localCursor.getColumnIndex("Name")));
                        grp.setL1(localCursor.getInt(localCursor.getColumnIndex("L1")));
                        grp.setL2(localCursor.getInt(localCursor.getColumnIndex("L2")));
                        grp.setL3(localCursor.getInt(localCursor.getColumnIndex("L3")));
                        grp.setL4(localCursor.getInt(localCursor.getColumnIndex("L4")));
                        grp.setL5(localCursor.getInt(localCursor.getColumnIndex("L5")));
                    } catch (Exception ignored) {
                    }

                    groups.add(grp);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return groups;
    }
    @SuppressLint("Range")
    public UserInfo LoadPersonalInfo() {

        UserInfo user = new UserInfo();

        String sql = "Select * From Config";

        String key;
        String val;

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    key = localCursor.getString(localCursor.getColumnIndex("KeyValue"));
                    val = localCursor.getString(localCursor.getColumnIndex("DataValue"));

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

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return user;
    }
    public void SavePersonalInfo(UserInfo user) {

        try {

            if (!user.getBrokerCode().equals("")) {

                String sql =
                        " Update Config set DataValue = '" +
                                user.getBrokerCode() +
                                "' Where KeyValue = 'BrokerCode';";

                db().execSQL(sql);

                sql =
                        " Insert Into Config(KeyValue, DataValue) " +
                                " Select 'BrokerCode', '" +
                                user.getBrokerCode() +
                                "' Where Not Exists(Select * From Config Where KeyValue = 'BrokerCode');";

                db().execSQL(sql);
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    public void SaveConfig(String key, String Value) {

        try {

            String sql =
                    " Insert Into Config(KeyValue, DataValue) " +
                            "Select '" +
                            key +
                            "', '" +
                            Value +
                            "' Where Not Exists(Select * From Config Where KeyValue = '" +
                            key +
                            "');";

            db().execSQL(sql);

            sql =
                    " Update Config set DataValue = '" +
                            Value +
                            "' Where KeyValue = '" +
                            key +
                            "' ;";

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    @SuppressLint("Range")
    public String ReadConfig(String key) {

        String resultValue = "";

        String sql =
                "SELECT DataValue FROM Config Where KeyValue= '" +
                        key +
                        "' ;";

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null && localCursor.moveToFirst()) {

                resultValue =
                        localCursor.getString(
                                localCursor.getColumnIndex("DataValue")
                        );
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultValue;
    }
    @SuppressLint("Range")
    public void ReplicateGoodtype(Column column) {

        Cursor localCursor = null;

        try {

            localCursor =
                    db().rawQuery(
                            "Select Count(*) AS cntRec From GoodType Where GoodType = '" +
                                    column.getColumnFieldValue("GoodType") +
                                    "'",
                            null
                    );

            int nc = 0;

            if (localCursor != null && localCursor.moveToFirst()) {

                nc =
                        localCursor.getInt(
                                localCursor.getColumnIndex("cntRec")
                        );
            }

            if (nc == 0) {

                db().execSQL(
                        "INSERT INTO GoodType (GoodType,IsDefault)" +
                                " VALUES ('" +
                                column.getColumnFieldValue("GoodType") +
                                "','" +
                                column.getColumnFieldValue("IsDefault") +
                                "'); "
                );
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }
    }
    public void UpdateSearchColumn(Column column) {

        try {

            String sql =
                    "update BrokerColumn set condition = '" +
                            column.getCondition() +
                            "' where ColumnCode= " +
                            column.getColumnCode();

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    public void UpdateLocationService_New(
            LocationResult locationResult,
            String gpsDate,
            String distance
    ) {

        try {

            Location location =
                    locationResult.getLastLocation();

            if (location == null) {
                return;
            }

            String longitude =
                    String.valueOf(location.getLongitude());

            String latitude =
                    String.valueOf(location.getLatitude());

            String speed =
                    String.valueOf(location.getSpeed());

            String accuracy =
                    String.valueOf(location.getAccuracy());

            String brokerRef =
                    ReadConfig("BrokerCode");

            String CorrectgpsDate =
                    gpsDate;

            String lastgpsDate =
                    GetLastLocationTime();

            String durationInSeconds =
                    "0";

            String status =
                    location.getSpeed() < 1 ? "Stopped" : "Moving";

            String locationDescription =
                    "";

            try {

                Geocoder geocoder =
                        new Geocoder(App.getContext(), Locale.getDefault());

                List<Address> addresses =
                        geocoder.getFromLocation(
                                location.getLatitude(),
                                location.getLongitude(),
                                1
                        );

                if (addresses != null && !addresses.isEmpty()) {

                    Address address =
                            addresses.get(0);

                    StringBuilder sb =
                            new StringBuilder();

                    if (address.getThoroughfare() != null) {
                        sb.append(address.getThoroughfare()).append(", ");
                    }

                    if (address.getSubLocality() != null) {
                        sb.append(address.getSubLocality()).append(", ");
                    }

                    if (address.getLocality() != null) {
                        sb.append(address.getLocality()).append(", ");
                    }

                    if (address.getCountryName() != null) {
                        sb.append(address.getCountryName());
                    }

                    locationDescription =
                            sb.toString();

                } else {

                    locationDescription =
                            "Unknown Location";
                }

            } catch (Exception e) {

                locationDescription =
                        distance;

                callMethod.Log(e.getMessage());
            }

            String sql =
                    "INSERT INTO GpsLocationNew " +
                            "(Longitude, Latitude, Speed, Accuracy, BrokerRef, GpsDate, NextGpsDate, DurationInSeconds, Status, LocationDescription) " +
                            "VALUES ('" +
                            longitude +
                            "', '" +
                            latitude +
                            "', '" +
                            speed +
                            "', '" +
                            accuracy +
                            "', '" +
                            brokerRef +
                            "', '" +
                            CorrectgpsDate +
                            "', '" +
                            lastgpsDate +
                            "', '" +
                            durationInSeconds +
                            "', '" +
                            status +
                            "', '" +
                            locationDescription +
                            "')";

            callMethod.Log(sql);

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }

    public void UpdateLocationService(
            LocationResult locationResult,
            String gpsDate
    ) {

        try {

            Location location =
                    locationResult.getLastLocation();

            if (location == null) {
                return;
            }

            String sql =
                    "Insert Into  GpsLocation " +
                            "(Longitude , Latitude ,Speed, BrokerRef , GpsDate )" +
                            " Values ('" +
                            location.getLongitude() +
                            "' , '" +
                            location.getLatitude() +
                            "', '" +
                            location.getSpeed() +
                            "', '" +
                            ReadConfig("BrokerCode") +
                            "' , '" +
                            gpsDate +
                            "')";

            callMethod.Log(sql);

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    public void ClearSearchColumn() {

        try {

            String sql = "update BrokerColumn set condition = '' ";

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    public void ReplicateColumn(
            Column column,
            Integer Apptype
    ) {

        try {

            String sql =
                    "INSERT INTO BrokerColumn" +
                            "(SortOrder,ColumnName ,ColumnDesc ,GoodType,ColumnDefinition,ColumnType,Condition,OrderIndex,AppType) " +
                            " VALUES ('" +
                            column.getColumnFieldValue("SortOrder") +
                            "','" +
                            column.getColumnFieldValue("ColumnName") +
                            "','" +
                            column.getColumnFieldValue("ColumnDesc") +
                            "','" +
                            column.getColumnFieldValue("GoodType") +
                            "','" +
                            column.getColumnFieldValue("ColumnDefinition") +
                            "','" +
                            column.getColumnFieldValue("ColumnType") +
                            "','" +
                            column.getColumnFieldValue("Condition") +
                            "','" +
                            column.getColumnFieldValue("OrderIndex") +
                            "'," +
                            Apptype +
                            "); ";

            db().execSQL(sql);

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    public void deleteColumn() {

        try {

            db().execSQL("delete from BrokerColumn");

            db().execSQL("delete from GoodType");

        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }
    @SuppressLint("Range")
    public String GpsLocationCode() {

        String resultValue =
                ReadConfig("LastGpsLocationCode");

        String sql =
                " select GpsLocationCode from GpsLocation " +
                        " where GpsLocationCode > " +
                        ReadConfig("LastGpsLocationCode") +
                        " limit 1 OFFSET 2";

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null && localCursor.moveToFirst()) {

                resultValue =
                        String.valueOf(
                                localCursor.getInt(
                                        localCursor.getColumnIndex(
                                                "GpsLocationCode"
                                        )
                                )
                        );
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultValue;
    }

    @SuppressLint("Range")
    public String GetLastLocationTime() {

        String resultValue = "";

        String sql =
                " select GpsLocationCode,GpsDate " +
                        " from GpsLocationNew " +
                        " order by 1 desc limit 1 OFFSET 0";

        Cursor localCursor = null;

        try {

            localCursor = db().rawQuery(sql, null);

            if (localCursor != null && localCursor.moveToFirst()) {

                resultValue =
                        localCursor.getString(
                                localCursor.getColumnIndex("GpsDate")
                        );
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultValue;
    }



    public void getAllGoodAsync(
            String search_target,
            String aGroupCode,
            String MoreCallData,
            DbCallback<ArrayList<Good>> callback
    ) {

        executorService.execute(new Runnable() {

            @Override
            public void run() {

                try {

                    final ArrayList<Good> result =
                            getAllGood(
                                    search_target,
                                    aGroupCode,
                                    MoreCallData
                            );

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onResult(result);
                        }
                    });

                } catch (final Exception e) {

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }


/*--------------------------------------------------------------
    getAllGood_ExtendedAsync
--------------------------------------------------------------*/

    public void getAllGood_ExtendedAsync(
            String searchbox_result,
            String aGroupCode,
            String MoreCallData,
            DbCallback<ArrayList<Good>> callback
    ) {

        executorService.execute(new Runnable() {

            @Override
            public void run() {

                try {

                    final ArrayList<Good> result =
                            getAllGood_Extended(
                                    searchbox_result,
                                    aGroupCode,
                                    MoreCallData
                            );

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onResult(result);
                        }
                    });

                } catch (final Exception e) {

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }


/*--------------------------------------------------------------
    getAllGood_ByDateAsync
--------------------------------------------------------------*/

    public void getAllGood_ByDateAsync(
            String xDayAgo,
            String MoreCallData,
            DbCallback<ArrayList<Good>> callback
    ) {

        executorService.execute(new Runnable() {

            @Override
            public void run() {

                try {

                    final ArrayList<Good> result =
                            getAllGood_ByDate(
                                    xDayAgo,
                                    MoreCallData
                            );

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onResult(result);
                        }
                    });

                } catch (final Exception e) {

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }


/*--------------------------------------------------------------
    getGoodByCodeAsync
--------------------------------------------------------------*/

    public void getGoodByCodeAsync(
            String code,
            DbCallback<Good> callback
    ) {

        executorService.execute(new Runnable() {

            @Override
            public void run() {

                try {

                    final Good result =
                            getGoodByCode(code);

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onResult(result);
                        }
                    });

                } catch (final Exception e) {

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }


/*--------------------------------------------------------------
    AllCustomerAsync
--------------------------------------------------------------*/

    public void AllCustomerAsync(
            String search_target,
            boolean aOnlyActive,
            DbCallback<ArrayList<Customer>> callback
    ) {

        executorService.execute(new Runnable() {

            @Override
            public void run() {

                try {

                    final ArrayList<Customer> result =
                            AllCustomer(
                                    search_target,
                                    aOnlyActive
                            );

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onResult(result);
                        }
                    });

                } catch (final Exception e) {

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }


/*--------------------------------------------------------------
    getAllPrefactorHeaderAsync
--------------------------------------------------------------*/

    public void getAllPrefactorHeaderAsync(
            String Search_target,
            DbCallback<ArrayList<PreFactor>> callback
    ) {

        executorService.execute(new Runnable() {

            @Override
            public void run() {

                try {

                    final ArrayList<PreFactor> result =
                            getAllPrefactorHeader(Search_target);

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onResult(result);
                        }
                    });

                } catch (final Exception e) {

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }


/*--------------------------------------------------------------
    getAllPreFactorRowsAsync
--------------------------------------------------------------*/

    public void getAllPreFactorRowsAsync(
            String Search_target,
            String aPreFactorCode,
            DbCallback<ArrayList<Good>> callback
    ) {

        executorService.execute(new Runnable() {

            @Override
            public void run() {

                try {

                    final ArrayList<Good> result =
                            getAllPreFactorRows(
                                    Search_target,
                                    aPreFactorCode
                            );

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onResult(result);
                        }
                    });

                } catch (final Exception e) {

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }




    public void ExecQuery(String Query) {

        if (Query == null || Query.trim().isEmpty()) {
            callMethod.Log("ExecQuery SKIP => Query is EMPTY");
            return;
        }

        getWritableDatabase().execSQL(Query);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    //////////////////////////////////////////////////////////////

    public interface ProgressCallback {
        void onProgress(int percent, int done, int total);
        void onDone();
        void onError(Exception e);
    }


    @SuppressLint("Range")
    private String BuildGoodSearchText(Cursor cursor) {

        StringBuilder searchText = new StringBuilder(1024);

        for (String columnName : GOOD_FTS_SEARCH_COLUMNS) {

            String value = GetCursorString(cursor, columnName);

            if (!value.equals("")) {

                // اگر داخل هر فیلد چند مقدار با * جدا شده باشد،
                // هر کدام به یک Token جدا برای FTS تبدیل می‌شود.
                value = value.replace("*", " ");

                searchText
                        .append(value)
                        .append(' ');
            }
        }

        // بارکدهای اضافه‌ی CacheBarCode هم داخل همان FTS قرار می‌گیرند.
        String cachedBarCode = GetCursorString(cursor, "CachedBarCode");

        if (!cachedBarCode.equals("")) {
            searchText
                    .append(cachedBarCode.replace("*", " "))
                    .append(' ');
        }

        return NormalizeGoodFTSText(searchText.toString());
    }


    @SuppressLint("Range")
    private String GetCursorString(Cursor cursor, String columnName) {

        int index = cursor.getColumnIndex(columnName);

        if (index < 0 || cursor.isNull(index)) {
            return "";
        }

        String value = cursor.getString(index);

        return value == null ? "" : value.trim();
    }


    private String NormalizeGoodFTSText(String text) {

        if (text == null) {
            return "";
        }

        return text
                // همان نرمال‌سازی GetRegionText بدون Query جدا برای هر کالا
                .replace('\u06CC', '\u064A')   // ی -> ي
                .replace('\u06A9', '\u0643')   // ک -> ك
                .replaceAll("\\s+", " ")
                .trim();
    }


    private String GetGoodFTSSelectFields(boolean includeOldSearchHash) {

        StringBuilder sql = new StringBuilder();

        for (String columnName : GOOD_FTS_SEARCH_COLUMNS) {

            if (sql.length() > 0) {
                sql.append(", ");
            }

            if (columnName.equals("GoodCode")) {
                sql.append("Cast(g.GoodCode AS Text) GoodCode");
            } else {
                sql.append("IfNull(g.")
                        .append(columnName)
                        .append(",'') ")
                        .append(columnName);
            }
        }

        sql.append(", IfNull(cb.CachedBarCode,'') CachedBarCode");

        if (includeOldSearchHash) {
            sql.append(", IfNull(s.SearchHash,'') OldSearchHash");
        }

        return sql.toString();
    }


    private String MakeSearchHash(String text) {

        if (text == null) {
            return "";
        }

        return String.valueOf(text.hashCode());
    }

    @SuppressLint("Range")
    private int GetGoodCount(SQLiteDatabase database) {

        Cursor cursor = null;

        try {

            cursor = database.rawQuery(
                    "SELECT Count(*) AS Cnt FROM Good",
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("Cnt"));
            }

        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        } finally {
            closeCursor(cursor);
        }

        return 0;
    }

    @SuppressLint("Range")
    private int GetGoodSearchFTSStateCount(SQLiteDatabase database) {

        Cursor cursor = null;

        try {

            cursor = database.rawQuery(
                    "SELECT Count(*) AS Cnt FROM GoodSearchFTSState",
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("Cnt"));
            }

        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        } finally {
            closeCursor(cursor);
        }

        return 0;
    }


    private void SendFTSProgress(
            ProgressCallback callback,
            int percent,
            int done,
            int total
    ) {

        final int finalPercent = percent;
        final int finalDone = done;
        final int finalTotal = total;

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onProgress(finalPercent, finalDone, finalTotal);
                }
            }
        });
    }


    private void SaveFTSReady(SQLiteDatabase database, String value) {

        database.execSQL(
                "INSERT INTO Config(KeyValue, DataValue) " +
                        "SELECT 'FTSReady', '0' " +
                        "WHERE NOT EXISTS(SELECT * FROM Config WHERE KeyValue = 'FTSReady')"
        );

        database.execSQL(
                "UPDATE Config SET DataValue = '" + value + "' WHERE KeyValue = 'FTSReady'"
        );
    }


    @SuppressLint("Range")
    private String ReadFTSReady(SQLiteDatabase database) {

        Cursor cursor = null;

        try {

            database.execSQL(
                    "INSERT INTO Config(KeyValue, DataValue) " +
                            "SELECT 'FTSReady', '0' " +
                            "WHERE NOT EXISTS(SELECT * FROM Config WHERE KeyValue = 'FTSReady')"
            );

            cursor = database.rawQuery(
                    "SELECT DataValue FROM Config WHERE KeyValue = 'FTSReady'",
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("DataValue"));
            }

        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        } finally {
            closeCursor(cursor);
        }

        return "0";
    }


    private void SaveFTSContentVersion(SQLiteDatabase database, String value) {

        database.execSQL(
                "INSERT INTO Config(KeyValue, DataValue) " +
                        "SELECT 'FTSContentVersion', '0' " +
                        "WHERE NOT EXISTS(SELECT * FROM Config WHERE KeyValue = 'FTSContentVersion')"
        );

        database.execSQL(
                "UPDATE Config SET DataValue = '" + value + "' WHERE KeyValue = 'FTSContentVersion'"
        );
    }

    @SuppressLint("Range")
    private String ReadFTSContentVersion(SQLiteDatabase database) {

        Cursor cursor = null;

        try {
            database.execSQL(
                    "INSERT INTO Config(KeyValue, DataValue) " +
                            "SELECT 'FTSContentVersion', '0' " +
                            "WHERE NOT EXISTS(SELECT * FROM Config WHERE KeyValue = 'FTSContentVersion')"
            );

            cursor = database.rawQuery(
                    "SELECT DataValue FROM Config WHERE KeyValue = 'FTSContentVersion'",
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("DataValue"));
            }

        } catch (Exception e) {
            callMethod.Log("ReadFTSContentVersion Error = " + e.getMessage());
        } finally {
            closeCursor(cursor);
        }

        return "0";
    }

    private void EnsureFTSContentVersion(SQLiteDatabase database) {

        String currentVersion = ReadFTSContentVersion(database);

        if (!FTS_CONTENT_VERSION.equals(currentVersion)) {
            callMethod.Log(
                    "FTS Content Version Changed: " +
                            currentVersion + " -> " + FTS_CONTENT_VERSION
            );

            database.execSQL("DROP TABLE IF EXISTS GoodSearchFTS");
            database.execSQL("DROP TABLE IF EXISTS GoodSearchFTSState");

            SaveFTSReady(database, "0");
            SaveFTSContentVersion(database, FTS_CONTENT_VERSION);
            CreateGoodSearchFTSTables(database);
        }
    }

    public synchronized void SyncGoodSearchFTS(ProgressCallback callback) {

        SQLiteDatabase database = db();

        EnsureFTSContentVersion(database);
        CreateGoodSearchFTSTables(database);

        String ftsReady = ReadFTSReady(database);

        callMethod.Log("FTS Ready Read Value = " + ftsReady);

        if (ftsReady.equals("1")) {

            callMethod.Log("FTS Mode = UPDATE_CHECK");

            SyncGoodSearchFTSUpdateMode(callback);

        } else {

            callMethod.Log("FTS Mode = FIRST_INSERT_CONTINUE");

            SyncGoodSearchFTSInsertMissingOnly(callback);
        }
    }


    @SuppressLint("Range")
    private void SyncGoodSearchFTSInsertMissingOnly(ProgressCallback callback) {

        SQLiteDatabase database = db();

        SQLiteStatement insertFTSStatement = null;
        SQLiteStatement insertStateStatement = null;

        final int PAGE_SIZE = 500;
        int totalInserted = 0;
        int lastPercent = -1;

        try {

            CreateGoodSearchFTSTables(database);

            SaveFTSReady(database, "0");

            int allGoods = GetGoodCount(database);

            callMethod.Log("FTS AllGoods = " + allGoods);

            insertFTSStatement = database.compileStatement(
                    "INSERT OR REPLACE INTO GoodSearchFTS(GoodCode, SearchText, SearchHash) VALUES(?, ?, ?)"
            );

            insertStateStatement = database.compileStatement(
                    "INSERT OR REPLACE INTO GoodSearchFTSState(GoodCode, SearchHash) VALUES(?, ?)"
            );

            while (true) {

                Cursor cursor = null;
                int pageInserted = 0;

                try {

                    cursor = database.rawQuery(
                            "SELECT " +
                                    GetGoodFTSSelectFields(false) + " " +
                                    "FROM Good g " +
                                    "LEFT JOIN CacheBarCode cb ON cb.GoodRef = g.GoodCode " +
                                    "LEFT JOIN GoodSearchFTSState s " +
                                    "ON s.GoodCode = Cast(g.GoodCode AS Text) " +
                                    "WHERE s.GoodCode IS NULL " +
                                    "ORDER BY g.GoodCode " +
                                    "LIMIT " + PAGE_SIZE,
                            null
                    );

                    if (cursor == null || cursor.getCount() == 0) {
                        break;
                    }

                    int idxGoodCode = cursor.getColumnIndex("GoodCode");

                    database.beginTransaction();

                    try {

                        while (cursor.moveToNext()) {

                            String goodCode = cursor.getString(idxGoodCode);

                            String searchText = BuildGoodSearchText(cursor);
                            String searchHash = MakeSearchHash(searchText);

                            insertFTSStatement.clearBindings();
                            insertFTSStatement.bindString(1, goodCode);
                            insertFTSStatement.bindString(2, searchText);
                            insertFTSStatement.bindString(3, searchHash);
                            insertFTSStatement.executeInsert();

                            insertStateStatement.clearBindings();
                            insertStateStatement.bindString(1, goodCode);
                            insertStateStatement.bindString(2, searchHash);
                            insertStateStatement.executeInsert();

                            pageInserted++;
                            totalInserted++;

                            int currentDone = GetGoodSearchFTSStateCount(database);

                            int percent =
                                    allGoods == 0
                                            ? 100
                                            : (currentDone * 100) / allGoods;

                            if (percent != lastPercent) {
                                lastPercent = percent;
                                SendFTSProgress(callback, percent, currentDone, allGoods);
                            }
                        }

                        database.setTransactionSuccessful();

                    } finally {
                        database.endTransaction();
                    }

                } finally {
                    closeCursor(cursor);
                }

                callMethod.Log(
                        "FTS Page Inserted = " +
                                pageInserted +
                                " | TotalInserted = " +
                                totalInserted +
                                " | StateCount = " +
                                GetGoodSearchFTSStateCount(database) +
                                " / " +
                                allGoods
                );

                if (pageInserted == 0) {
                    break;
                }
            }

            int goodCount = GetGoodCount(database);
            int ftsCount = GetGoodSearchFTSCount();
            int stateCount = GetGoodSearchFTSStateTableCount();

            callMethod.Log(
                    "FTS Final Check => Good=" +
                            goodCount +
                            " FTS=" +
                            ftsCount +
                            " State=" +
                            stateCount
            );

            if (goodCount > 0 && goodCount == ftsCount && goodCount == stateCount) {

                SaveFTSReady(database, "1");
                SaveFTSContentVersion(database, FTS_CONTENT_VERSION);

                SendFTSProgress(callback, 100, goodCount, goodCount);

                callMethod.Log("FTS First Insert Finished Successfully");

            } else {

                SaveFTSReady(database, "0");

                throw new Exception(
                        "FTS Not Healthy After Build | Good=" +
                                goodCount +
                                " FTS=" +
                                ftsCount +
                                " State=" +
                                stateCount
                );
            }

        } catch (Exception e) {

            SaveFTSReady(database, "0");
            callMethod.Log("FTS Insert Missing Error = " + e.getMessage());
            throw new RuntimeException(e);

        } finally {

            if (insertFTSStatement != null) {
                insertFTSStatement.close();
            }

            if (insertStateStatement != null) {
                insertStateStatement.close();
            }
        }
    }


    @SuppressLint("Range")
    private void SyncGoodSearchFTSUpdateMode(ProgressCallback callback) {

        Cursor cursor = null;
        Cursor countCursor = null;

        SQLiteDatabase database = db();

        SQLiteStatement deleteFTSStatement = null;
        SQLiteStatement insertFTSStatement = null;
        SQLiteStatement insertStateStatement = null;

        boolean transactionOpen = false;

        final int COMMIT_COUNT = 5000;

        try {

            CreateGoodSearchFTSTables(database);

            countCursor = database.rawQuery(
                    "SELECT Count(*) AS Cnt FROM Good",
                    null
            );

            int total = 0;

            if (countCursor != null && countCursor.moveToFirst()) {
                total = countCursor.getInt(countCursor.getColumnIndex("Cnt"));
            }

            String query =
                    "SELECT " +
                            GetGoodFTSSelectFields(true) + " " +
                            "FROM Good g " +
                            "LEFT JOIN CacheBarCode cb ON cb.GoodRef = g.GoodCode " +
                            "LEFT JOIN GoodSearchFTSState s " +
                            "ON s.GoodCode = Cast(g.GoodCode AS Text)";

            cursor = database.rawQuery(query, null);

            deleteFTSStatement = database.compileStatement(
                    "DELETE FROM GoodSearchFTS WHERE GoodCode = ?"
            );

            insertFTSStatement = database.compileStatement(
                    "INSERT INTO GoodSearchFTS(GoodCode, SearchText, SearchHash) VALUES(?, ?, ?)"
            );

            insertStateStatement = database.compileStatement(
                    "INSERT OR REPLACE INTO GoodSearchFTSState(GoodCode, SearchHash) VALUES(?, ?)"
            );

            database.beginTransaction();
            transactionOpen = true;

            database.execSQL(
                    "DELETE FROM GoodSearchFTS " +
                            "WHERE GoodCode NOT IN (" +
                            "SELECT Cast(GoodCode AS Text) FROM Good" +
                            ")"
            );

            database.execSQL(
                    "DELETE FROM GoodSearchFTSState " +
                            "WHERE GoodCode NOT IN (" +
                            "SELECT Cast(GoodCode AS Text) FROM Good" +
                            ")"
            );

            int done = 0;
            int inserted = 0;
            int updated = 0;
            int lastPercent = -1;

            if (cursor != null) {

                int idxGoodCode = cursor.getColumnIndex("GoodCode");
                int idxOldSearchHash = cursor.getColumnIndex("OldSearchHash");

                while (cursor.moveToNext()) {

                    String goodCode = cursor.getString(idxGoodCode);
                    String oldHash = cursor.getString(idxOldSearchHash);

                    String searchText = BuildGoodSearchText(cursor);
                    String newHash = MakeSearchHash(searchText);

                    if (oldHash == null || oldHash.length() == 0) {

                        insertFTSStatement.clearBindings();
                        insertFTSStatement.bindString(1, goodCode);
                        insertFTSStatement.bindString(2, searchText);
                        insertFTSStatement.bindString(3, newHash);
                        insertFTSStatement.executeInsert();

                        insertStateStatement.clearBindings();
                        insertStateStatement.bindString(1, goodCode);
                        insertStateStatement.bindString(2, newHash);
                        insertStateStatement.executeInsert();

                        inserted++;

                    } else if (!newHash.equals(oldHash)) {

                        deleteFTSStatement.clearBindings();
                        deleteFTSStatement.bindString(1, goodCode);
                        deleteFTSStatement.executeUpdateDelete();

                        insertFTSStatement.clearBindings();
                        insertFTSStatement.bindString(1, goodCode);
                        insertFTSStatement.bindString(2, searchText);
                        insertFTSStatement.bindString(3, newHash);
                        insertFTSStatement.executeInsert();

                        insertStateStatement.clearBindings();
                        insertStateStatement.bindString(1, goodCode);
                        insertStateStatement.bindString(2, newHash);
                        insertStateStatement.executeInsert();

                        updated++;
                    }

                    done++;

                    if (done % COMMIT_COUNT == 0) {

                        database.setTransactionSuccessful();
                        database.endTransaction();

                        transactionOpen = false;

                        callMethod.Log(
                                "FTS Update Commit => " +
                                        done +
                                        " / " +
                                        total +
                                        " | Inserted: " +
                                        inserted +
                                        " | Updated: " +
                                        updated
                        );

                        database.beginTransaction();
                        transactionOpen = true;
                    }

                    int percent =
                            total == 0
                                    ? 100
                                    : (done * 100) / total;

                    if (percent != lastPercent) {
                        lastPercent = percent;
                        SendFTSProgress(callback, percent, done, total);
                    }
                }
            }

            SaveFTSContentVersion(database, FTS_CONTENT_VERSION);
            database.setTransactionSuccessful();

            callMethod.Log(
                    "FTS Update Finished | Checked = " +
                            done +
                            " | Inserted = " +
                            inserted +
                            " | Updated = " +
                            updated
            );

        } catch (Exception e) {

            callMethod.Log("FTS Update Error = " + e.getMessage());
            throw e;

        } finally {

            closeCursor(cursor);
            closeCursor(countCursor);

            if (deleteFTSStatement != null) {
                deleteFTSStatement.close();
            }

            if (insertFTSStatement != null) {
                insertFTSStatement.close();
            }

            if (insertStateStatement != null) {
                insertStateStatement.close();
            }

            if (transactionOpen) {
                try {
                    database.endTransaction();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void SyncGoodsSearchFTSBatchAsync(ArrayList<String> goodCodes, OneGoodFTSCallback callback) {

        final int total = goodCodes == null ? 0 : goodCodes.size();

        mainHandler.post(() -> {
            if (callback != null) {
                callback.onStart(
                        "در حال بروزرسانی جستجوی کالا... 0 از " + total
                );
            }
        });

        executorService.execute(() -> {
            try {

                if (goodCodes == null || goodCodes.isEmpty()) {
                    mainHandler.post(() -> {
                        if (callback != null) {
                            callback.onDone("کالایی برای بروزرسانی وجود ندارد");
                        }
                    });
                    return;
                }

                SQLiteDatabase database = db();
                long startTime = System.currentTimeMillis();
                int lastPercent = -1;

                database.beginTransaction();
                try {
                    for (int i = 0; i < total; i++) {

                        String goodCode = goodCodes.get(i);
                        SyncOneGoodSearchFTS(goodCode);

                        int done = i + 1;
                        int percent = (int) ((done * 100L) / total);

                        long elapsed = System.currentTimeMillis() - startTime;
                        long averagePerItem = done > 0 ? elapsed / done : 0;
                        long remainingMillis = averagePerItem * (total - done);
                        long remainingSeconds = remainingMillis / 1000L;

                        // برای جلوگیری از ارسال تعداد زیاد پیام به UI،
                        // فقط وقتی درصد تغییر می‌کند progress ارسال می‌شود.
                        if (percent != lastPercent || done == total) {
                            lastPercent = percent;

                            final int finalPercent = percent;
                            final int finalDone = done;
                            final long finalRemainingSeconds = remainingSeconds;

                            mainHandler.post(() -> {
                                if (callback != null) {
                                    callback.onProgress(
                                            finalPercent,
                                            finalDone,
                                            total,
                                            finalRemainingSeconds
                                    );
                                }
                            });
                        }
                    }

                    database.setTransactionSuccessful();

                } finally {
                    database.endTransaction();
                }

                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onDone(
                                "جستجوی " + total + " کالا بروزرسانی شد"
                        );
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onError(e);
                    }
                });
            }
        });
    }

    public void SyncGoodSearchFTSAsync(ProgressCallback callback) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    SyncGoodSearchFTS(callback);

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onDone();
                            }
                        }
                    });

                } catch (final Exception e) {

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onError(e);
                            }
                        }
                    });
                }
            }
        });
    }

    private void CreateGoodSearchFTSTables(SQLiteDatabase database) {

        database.execSQL(
                "CREATE VIRTUAL TABLE IF NOT EXISTS GoodSearchFTS " +
                        "USING fts4(" +
                        "GoodCode, " +
                        "SearchText, " +
                        "SearchHash" +
                        ")"
        );

        database.execSQL(
                "CREATE TABLE IF NOT EXISTS GoodSearchFTSState (" +
                        "GoodCode TEXT PRIMARY KEY, " +
                        "SearchHash TEXT" +
                        ")"
        );
    }

    private String BuildGoodFTSMatchQuery(String search) {

        search = GetRegionText(search);
        search = search.replaceAll("'", " ");
        search = search.replaceAll("\"", " ");
        search = search.replaceAll(":", " ");
        search = search.replaceAll("-", " ");
        search = search.replaceAll("\\*", " ");
        search = search.replaceAll("\\s+", " ").trim();

        if (search.equals("")) {
            return "";
        }

        String[] words = search.split(" ");

        StringBuilder matchQuery = new StringBuilder();

        for (String word : words) {

            word = word.trim();

            if (!word.equals("")) {

                if (matchQuery.length() > 0) {
                    matchQuery.append(" ");
                }

                matchQuery.append(word).append("*");
            }
        }

        return matchQuery.toString();
    }
    @SuppressLint({"Recycle", "Range"})
    public synchronized ArrayList<Good> getAllGood(String search_target, String aGroupCode, String MoreCallData) {

        ArrayList<Good> resultGoods = new ArrayList<>();

        GetPreference();

        ArrayList<Column> localColumns = GetColumns("", "", "1");

        String search = GetRegionText(search_target);
        search = search.replaceAll("'", " ").trim();

        try {
            Integer.parseInt(aGroupCode);
        } catch (Exception e) {
            aGroupCode = "0";
        }

        int offsetValue = 0;

        try {
            offsetValue =                    Integer.parseInt(LimitAmount) *                            Integer.parseInt(MoreCallData);        } catch (Exception e) {            offsetValue = 0;
        }

        String selectQuery = "";
        String whereQuery;
        String orderQuery;

        ArrayList<String> argsList = new ArrayList<>();

        int k = 0;

        for (Column column : localColumns) {

            if (column.getColumnDefinition().indexOf("Sum") > 0) {
                StackAmountString =
                        column.getColumnDefinition().substring(
                                column.getColumnDefinition().indexOf("Sum"),
                                column.getColumnDefinition().indexOf(")") + 1
                        );
            }

            if (!column.getColumnName().equals("")) {

                if (k != 0) {
                    selectQuery = selectQuery + " , ";
                }

                if (!column.getColumnDefinition().equals("")) {

                    String columnDefinition = column.getColumnDefinition();

                    columnDefinition =
                            columnDefinition.replace(
                                    "stackCondition",
                                    BrokerStackString
                            );

                    columnDefinition =
                            columnDefinition.replace(
                                    "GoodRef=GoodCode",
                                    "GoodRef=g.GoodCode"
                            );

                    columnDefinition =
                            columnDefinition.replace(
                                    "GoodRef = GoodCode",
                                    "GoodRef = g.GoodCode"
                            );

                    selectQuery =
                            selectQuery +
                                    columnDefinition +
                                    " as " +
                                    column.getColumnName();

                } else {

                    String columnName = column.getColumnName();

                    if (!columnName.contains(".")) {
                        columnName = "g." + columnName;
                    }

                    selectQuery = selectQuery + columnName;
                }

                k++;
            }
        }

        boolean hasGoodCodeColumn = false;

        for (Column column : localColumns) {
            if ("GoodCode".equalsIgnoreCase(column.getColumnName())) {
                hasGoodCodeColumn = true;
                break;
            }
        }

        if (selectQuery.equals("")) {
            selectQuery = "g.GoodCode AS GoodCode";
        } else if (!hasGoodCodeColumn) {
            // GoodCode برای Adapter/Image الزامی است؛ حتی اگر در BrokerColumn مخفی شده باشد.
            selectQuery = "g.GoodCode AS GoodCode, " + selectQuery;
        }

        if (!search.equals("")) {

            String matchQuery = BuildGoodFTSMatchQuery(search);

            if (!matchQuery.equals("")) {

                whereQuery =
                        " Where g.GoodCode in (" +
                                " Select Cast(GoodCode as INTEGER) " +
                                " From GoodSearchFTS " +
                                " Where GoodSearchFTS Match ? " +
                                " ) ";

                argsList.add(matchQuery);

            } else {
                whereQuery = " Where 1=1 ";
            }

        } else {
            whereQuery = " Where 1=1 ";
        }

        whereQuery =
                whereQuery +
                        " And Exists(" +
                        " Select 1 " +
                        " From GoodStack " +
                        " stackCondition " +
                        " ActiveCondition " +
                        " And GoodRef = g.GoodCode " +
                        " AmountCondition " +
                        ")";

        if (SH_activestack) {
            whereQuery =
                    whereQuery.replace(
                            "ActiveCondition",
                            " And ActiveStack = 1 "
                    );
        } else {
            whereQuery =
                    whereQuery.replace(
                            "ActiveCondition",
                            " "
                    );
        }

        if (SH_goodamount) {
            whereQuery =
                    whereQuery.replace(
                            "AmountCondition",
                            " GROUP BY GoodRef HAVING " +
                                    StackAmountString +
                                    " > 0 "
                    );
        } else {
            whereQuery =
                    whereQuery.replace(
                            "AmountCondition",
                            " "
                    );
        }

        whereQuery =
                whereQuery.replace(
                        "stackCondition",
                        BrokerStackString
                );

        if (Integer.parseInt(aGroupCode) > 0) {

            whereQuery =
                    whereQuery +
                            " And g.GoodCode in(Select GoodRef From GoodGroup p " +
                            "Join GoodsGrp s on p.GoodGroupRef = s.GroupCode " +
                            "Where s.GroupCode = " + aGroupCode +
                            " or s.L1 = " + aGroupCode +
                            " or s.L2 = " + aGroupCode +
                            " or s.L3 = " + aGroupCode +
                            " or s.L4 = " + aGroupCode +
                            " or s.L5 = " + aGroupCode + ")";
        }

        orderQuery = " order by ";

        int orderCount = 0;

        for (Column column : localColumns) {

            if (!column.getOrderIndex().equals("0")) {

                if (orderCount != 0) {
                    orderQuery = orderQuery + " , ";
                }

                String orderColumn;

                if (column.getColumnName().equals("Date")) {

                    orderColumn =
                            column.getColumnDefinition().substring(
                                    column.getColumnDefinition().indexOf("Then") + 5,
                                    column.getColumnDefinition().indexOf("Then") + 12
                            );

                } else if (column.getColumnName().equals("GoodCode")) {

                    orderColumn = "g.GoodCode";

                } else {

                    orderColumn = column.getColumnName();

                    if (!orderColumn.contains(".")) {
                        orderColumn = "g." + orderColumn;
                    }
                }

                if (Integer.parseInt(column.getOrderIndex()) > 0) {
                    orderQuery = orderQuery + orderColumn;
                } else {
                    orderQuery = orderQuery + orderColumn + " DESC ";
                }

                orderCount++;
            }
        }

        if (orderCount == 0) {
            orderQuery = " order by g.GoodCode DESC ";
        }

        String sql =
                " With FilterTable As (Select 0 as SecondField), " +
                        " GoodsLimited As ( " +
                        " Select g.GoodCode " +
                        " From Good g , FilterTable " +
                        whereQuery +
                        orderQuery +
                        " LIMIT " +
                        LimitAmount +
                        " OFFSET " +
                        offsetValue +
                        " ) " +
                        " SELECT " +
                        selectQuery +
                        " FROM GoodsLimited gl " +
                        " Join Good g on g.GoodCode = gl.GoodCode " +
                        " , FilterTable " +
                        orderQuery;

        callMethod.Log(sql);
        callMethod.Log("FTS Args = " + argsList.toString());
        Cursor localCursor = null;

        try {

            String[] args = argsList.toArray(new String[0]);

            localCursor = db().rawQuery(sql, args);

            if (localCursor != null) {

                while (localCursor.moveToNext()) {

                    Good itemGood = new Good();

                    // GoodCode را مستقل از تنظیمات ستون‌ها همیشه داخل مدل نگه می‌داریم.
                    int goodCodeIndex = localCursor.getColumnIndex("GoodCode");
                    if (goodCodeIndex >= 0 && !localCursor.isNull(goodCodeIndex)) {
                        itemGood.setGoodFieldValue(
                                "GoodCode",
                                localCursor.getString(goodCodeIndex)
                        );
                    }

                    for (Column column : localColumns) {

                        try {

                            switch (column.getColumnType()) {

                                case "0":

                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            localCursor.getString(
                                                    localCursor.getColumnIndex(
                                                            column.getColumnName()
                                                    )
                                            )
                                    );

                                    break;

                                case "1":

                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(
                                                    localCursor.getInt(
                                                            localCursor.getColumnIndex(
                                                                    column.getColumnName()
                                                            )
                                                    )
                                            )
                                    );

                                    break;

                                case "2":

                                    itemGood.setGoodFieldValue(
                                            column.getColumnName(),
                                            String.valueOf(
                                                    localCursor.getFloat(
                                                            localCursor.getColumnIndex(
                                                                    column.getColumnName()
                                                            )
                                                    )
                                            )
                                    );

                                    break;
                            }

                        } catch (Exception ignored) {
                        }
                    }

                    itemGood.setCheck(false);

                    try {

                        itemGood.setGoodFieldValue(
                                "ActiveStack",
                                String.valueOf(
                                        localCursor.getInt(
                                                localCursor.getColumnIndex(
                                                        "ActiveStack"
                                                )
                                        )
                                )
                        );

                    } catch (Exception ignored) {
                    }

                    resultGoods.add(itemGood);
                }
            }

        } catch (Exception e) {

            callMethod.Log(e.getMessage());

        } finally {

            closeCursor(localCursor);
        }

        return resultGoods;
    }

    @SuppressLint("Range")
    public synchronized void SyncOneGoodSearchFTS(String goodCode) {

        Cursor cursor = null;

        SQLiteDatabase database = db();

        SQLiteStatement deleteFTS = null;
        SQLiteStatement insertFTS = null;
        SQLiteStatement insertState = null;

        try {

            CreateGoodSearchFTSTables(database);

            deleteFTS = database.compileStatement(
                    "DELETE FROM GoodSearchFTS WHERE GoodCode = ?"
            );

            insertFTS = database.compileStatement(
                    "INSERT INTO GoodSearchFTS(GoodCode, SearchText, SearchHash) VALUES(?, ?, ?)"
            );

            insertState = database.compileStatement(
                    "INSERT OR REPLACE INTO GoodSearchFTSState(GoodCode, SearchHash) VALUES(?, ?)"
            );

            cursor = database.rawQuery(
                    "SELECT " +
                            GetGoodFTSSelectFields(false) + " " +
                            "FROM Good g " +
                            "LEFT JOIN CacheBarCode cb ON cb.GoodRef = g.GoodCode " +
                            "WHERE g.GoodCode = ?",
                    new String[]{goodCode}
            );

            deleteFTS.clearBindings();
            deleteFTS.bindString(1, goodCode);
            deleteFTS.executeUpdateDelete();

            if (cursor != null && cursor.moveToFirst()) {

                String searchText = BuildGoodSearchText(cursor);
                String searchHash = MakeSearchHash(searchText);

                insertFTS.clearBindings();
                insertFTS.bindString(1, goodCode);
                insertFTS.bindString(2, searchText);
                insertFTS.bindString(3, searchHash);
                insertFTS.executeInsert();

                insertState.clearBindings();
                insertState.bindString(1, goodCode);
                insertState.bindString(2, searchHash);
                insertState.executeInsert();
            }

        } catch (Exception e) {

            callMethod.Log("SyncOneGoodSearchFTS Error = " + e.getMessage());

        } finally {

            closeCursor(cursor);

            if (deleteFTS != null) deleteFTS.close();
            if (insertFTS != null) insertFTS.close();
            if (insertState != null) insertState.close();
        }
    }
    public synchronized void DeleteOneGoodSearchFTS(String goodCode) {

        SQLiteDatabase database = db();

        SQLiteStatement deleteFTS = null;
        SQLiteStatement deleteState = null;

        try {

            CreateGoodSearchFTSTables(database);

            deleteFTS = database.compileStatement(
                    "DELETE FROM GoodSearchFTS WHERE GoodCode = ?"
            );

            deleteState = database.compileStatement(
                    "DELETE FROM GoodSearchFTSState WHERE GoodCode = ?"
            );

            deleteFTS.clearBindings();
            deleteFTS.bindString(1, goodCode);
            deleteFTS.executeUpdateDelete();

            deleteState.clearBindings();
            deleteState.bindString(1, goodCode);
            deleteState.executeUpdateDelete();

        } catch (Exception e) {

            callMethod.Log("DeleteOneGoodSearchFTS Error = " + e.getMessage());

        } finally {

            if (deleteFTS != null) deleteFTS.close();
            if (deleteState != null) deleteState.close();
        }
    }
    public boolean IsGoodSearchFTSReady() {
        SQLiteDatabase database = db();
        return ReadConfig("FTSReady").equals("1") &&
                FTS_CONTENT_VERSION.equals(ReadFTSContentVersion(database));
    }


    public void SyncOneGoodSearchFTSAsync(String goodCode, OneGoodFTSCallback callback) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onStart("در حال بروزرسانی جستجوی کالا...");
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    SyncOneGoodSearchFTS(goodCode);

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onDone("جستجوی کالا بروزرسانی شد");
                            }
                        }
                    });

                } catch (final Exception e) {

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onError(e);
                            }
                        }
                    });
                }
            }
        });
    }

    public void DeleteOneGoodSearchFTSAsync(String goodCode, OneGoodFTSCallback callback) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onStart("در حال حذف کالا از جستجو...");
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    DeleteOneGoodSearchFTS(goodCode);

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onDone("کالا از جستجو حذف شد");
                            }
                        }
                    });

                } catch (final Exception e) {

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onError(e);
                            }
                        }
                    });
                }
            }
        });
    }
    public int GetGoodTableCount() {
        return GetGoodCount(db());
    }

    @SuppressLint("Range")
    public int GetGoodSearchFTSCount() {
        Cursor cursor = null;

        try {
            cursor = db().rawQuery(
                    "SELECT Count(*) AS Cnt FROM GoodSearchFTS",
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("Cnt"));
            }

        } catch (Exception e) {
            callMethod.Log("GetGoodSearchFTSCount Error = " + e.getMessage());
        } finally {
            closeCursor(cursor);
        }

        return 0;
    }

    public int GetGoodSearchFTSStateTableCount() {
        return GetGoodSearchFTSStateCount(db());
    }

    public boolean IsGoodSearchFTSHealthy() {
        int goodCount = GetGoodTableCount();
        int ftsCount = GetGoodSearchFTSCount();
        int stateCount = GetGoodSearchFTSStateTableCount();
        String contentVersion = ReadFTSContentVersion(db());

        callMethod.Log(
                "FTS Health => Good=" + goodCount +
                        " FTS=" + ftsCount +
                        " State=" + stateCount +
                        " ContentVersion=" + contentVersion
        );

        return goodCount > 0 &&
                goodCount == ftsCount &&
                goodCount == stateCount &&
                FTS_CONTENT_VERSION.equals(contentVersion) &&
                IsGoodSearchFTSReady();
    }

    public synchronized void ForceRebuildGoodSearchFTS() {
        SQLiteDatabase database = db();

        database.execSQL("DROP TABLE IF EXISTS GoodSearchFTS");
        database.execSQL("DROP TABLE IF EXISTS GoodSearchFTSState");

        SaveFTSReady(database, "0");
        SaveFTSContentVersion(database, FTS_CONTENT_VERSION);

        CreateGoodSearchFTSTables(database);
    }
    public void SaveFTSReady(String value) {
        SaveFTSReady(db(), value);
    }
    public interface OneGoodFTSCallback {
        void onStart(String message);

        default void onProgress(
                int percent,
                int done,
                int total,
                long remainingSeconds
        ) {
            // Optional: callers that need progress can override this method.
        }

        void onDone(String message);
        void onError(Exception e);
    }


}

