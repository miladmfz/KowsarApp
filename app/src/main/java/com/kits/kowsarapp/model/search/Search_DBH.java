package com.kits.kowsarapp.model.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Activation;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.UserInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Search_DBH extends SQLiteOpenHelper {


    CallMethod callMethod;
    ArrayList<Search_Good> goods;
    int limitcolumn;
    ArrayList<Column> columns;

    Cursor cursor;

    String query = "";
    String result = "";


    public Search_DBH(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        this.callMethod = new CallMethod(context);
        this.goods = new ArrayList<>();

    }
    public void DatabaseCreate() {
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Config (ConfigCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, KeyValue TEXT , DataValue TEXT)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS BrokerColumn ( ColumnCode INTEGER PRIMARY KEY, SortOrder TEXT, ColumnName TEXT, ColumnDesc TEXT, GoodType TEXT, ColumnDefinition TEXT, ColumnType TEXT, Condition TEXT, OrderIndex TEXT, AppType INTEGER)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS GoodType ( GoodTypeCode INTEGER PRIMARY KEY, GoodType TEXT, IsDefault TEXT)");


        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'BrokerCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'GroupCodeDefult', '0' Where Not Exists(Select * From Config Where KeyValue = 'GroupCodeDefult')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'VersionInfo', '" + BuildConfig.VERSION_NAME + "' Where Not Exists(Select * From Config Where KeyValue = 'VersionInfo')");
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
    public void deleteColumn() {
        getWritableDatabase().execSQL("delete from BrokerColumn");
        getWritableDatabase().execSQL("delete from GoodType");
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
    @SuppressLint("Range")
    public ArrayList<Activation> getActivation() {

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
                } catch (Exception ignored) {
                }
                activations.add(activation);

            }
        }
        assert cursor != null;
        cursor.close();
        return activations;
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
    public void UpdateUrl(String ActivationCode,String ServerURL) {

        getWritableDatabase().execSQL("Update Activation set " +
                "ServerURL = '" + ServerURL + "' " +
                "Where ActivationCode= '" + ActivationCode + "'");



    }



    public void SaveConfig(String key, String Value) {

        query = " Insert Into Config(KeyValue, DataValue) Select '" + key + "', '" + Value + "' Where Not Exists(Select * From Config Where KeyValue = '" + key + "');";
        getWritableDatabase().execSQL(query);
        query = " Update Config set DataValue = '" + Value + "' Where KeyValue = '" + key + "' ;";
        getWritableDatabase().execSQL(query);

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
            Log.e("kowsar_query", e.getMessage());
        }
    }
    @SuppressLint("Range")
    public ArrayList<Column> GetColumns(String code, String goodtype, @NonNull String AppType) {

        //GetLimitColumn(AppType);
        //query = "Select * from BrokerColumn where AppType = " + AppType + " limit " + limitcolumn;
        query = "Select * from BrokerColumn ";


        Log.e("kowsar_query", query);
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
    public String ReadConfig(String key) {

        query = "SELECT DataValue  FROM Config  Where KeyValue= '" + key + "' ;";

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("DataValue"));
            cursor.close();
        }
        return result;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}