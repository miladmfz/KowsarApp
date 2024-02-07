package com.kits.kowsarapp.model.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Activation;
import com.kits.kowsarapp.model.base.Good;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Order_DBH extends SQLiteOpenHelper {


    CallMethod callMethod;
    ArrayList<Good> goods;

    Cursor cursor;

    String query = "";
    String result = "";


    public Order_DBH(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        this.callMethod = new CallMethod(context);
        this.goods = new ArrayList<>();

    }


    public void CreateActivationDb() {
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Activation (" +
                "AppBrokerCustomerCode TEXT," +
                "ActivationCode TEXT," +
                "PersianCompanyName TEXT," +
                "EnglishCompanyName TEXT," +
                "ServerURL TEXT," +
                "SQLiteURL TEXT," +
                "MaxDevice TEXT)");
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
            getWritableDatabase().execSQL(" Insert Into Activation(AppBrokerCustomerCode,ActivationCode,PersianCompanyName, EnglishCompanyName,ServerURL,SQLiteURL,MaxDevice)" +
                    " Select '" + activation.getAppBrokerCustomerCode() + "','" + activation.getActivationCode() + "','" +
                    activation.getPersianCompanyName() + "','" + activation.getEnglishCompanyName() + "','" +
                    activation.getServerURL() + "','" + activation.getSQLiteURL() + "','" + activation.getMaxDevice() + "'");

        }


    }


    public void DatabaseCreate() {
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Config (ConfigCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, KeyValue TEXT , DataValue TEXT)");


        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'BrokerCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerCode')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'GroupCodeDefult', '0' Where Not Exists(Select * From Config Where KeyValue = 'GroupCodeDefult')");
        getWritableDatabase().execSQL("INSERT INTO config(keyvalue, datavalue) Select 'VersionInfo', '" + BuildConfig.VERSION_NAME + "' Where Not Exists(Select * From Config Where KeyValue = 'VersionInfo')");

    }

    public void SaveConfig(String key, String Value) {

        query = " Insert Into Config(KeyValue, DataValue) Select '" + key + "', '" + Value + "' Where Not Exists(Select * From Config Where KeyValue = '" + key + "');";
        getWritableDatabase().execSQL(query);
        query = " Update Config set DataValue = '" + Value + "' Where KeyValue = '" + key + "' ;";
        getWritableDatabase().execSQL(query);

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