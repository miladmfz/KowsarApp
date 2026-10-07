package com.kits.kowsarapp.model.order;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.model.base.Activation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Order_DBH extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public Order_DBH(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    public void DatabaseCreate() {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS Config ("
                    + "ConfigCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, "
                    + "KeyValue TEXT, DataValue TEXT)");
            insertDefaultConfig(database, "BrokerCode", "0");
            insertDefaultConfig(database, "GroupCodeDefult", "0");
            insertDefaultConfig(database, "VersionInfo", BuildConfig.VERSION_NAME);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void CreateActivationDb() {
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Activation ("
                + "AppBrokerCustomerCode TEXT,"
                + "ActivationCode TEXT,"
                + "PersianCompanyName TEXT,"
                + "EnglishCompanyName TEXT,"
                + "ServerURL TEXT,"
                + "SQLiteURL TEXT,"
                + "MaxDevice TEXT)");
    }

    @SuppressLint("Range")
    public ArrayList<Activation> getActivation() {
        ArrayList<Activation> activations = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM Activation", null)) {
            while (cursor.moveToNext()) {
                Activation activation = new Activation();
                try {
                    activation.setAppBrokerCustomerCode(cursor.getString(
                            cursor.getColumnIndex("AppBrokerCustomerCode")));
                    activation.setActivationCode(cursor.getString(
                            cursor.getColumnIndex("ActivationCode")));
                    activation.setPersianCompanyName(cursor.getString(
                            cursor.getColumnIndex("PersianCompanyName")));
                    activation.setEnglishCompanyName(cursor.getString(
                            cursor.getColumnIndex("EnglishCompanyName")));
                    activation.setServerURL(cursor.getString(
                            cursor.getColumnIndex("ServerURL")));
                    activation.setSQLiteURL(cursor.getString(
                            cursor.getColumnIndex("SQLiteURL")));
                    activation.setMaxDevice(cursor.getString(
                            cursor.getColumnIndex("MaxDevice")));
                } catch (RuntimeException ignored) {
                    // Keep the legacy partial-row behavior for old profile snapshots.
                }
                activations.add(activation);
            }
        }
        return activations;
    }

    public void InsertActivation(@NotNull Activation activation) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            boolean exists;
            try (Cursor cursor = database.rawQuery(
                    "SELECT 1 FROM Activation WHERE ActivationCode=? LIMIT 1",
                    new String[]{legacyText(activation.getActivationCode())})) {
                exists = cursor.moveToFirst();
            }

            ContentValues values = new ContentValues();
            values.put("ServerURL", legacyText(activation.getServerURL()));
            values.put("SQLiteURL", legacyText(activation.getSQLiteURL()));
            if (exists) {
                database.update(
                        "Activation",
                        values,
                        "ActivationCode=?",
                        new String[]{legacyText(activation.getActivationCode())}
                );
            } else {
                values.put("AppBrokerCustomerCode",
                        legacyText(activation.getAppBrokerCustomerCode()));
                values.put("ActivationCode", legacyText(activation.getActivationCode()));
                values.put("PersianCompanyName",
                        legacyText(activation.getPersianCompanyName()));
                values.put("EnglishCompanyName",
                        legacyText(activation.getEnglishCompanyName()));
                values.put("MaxDevice", legacyText(activation.getMaxDevice()));
                database.insertOrThrow("Activation", null, values);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void SaveConfig(String key, String value) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            insertDefaultConfig(database, legacyText(key), legacyText(value));
            database.execSQL(
                    "UPDATE Config SET DataValue=? WHERE KeyValue=?",
                    new Object[]{legacyText(value), legacyText(key)}
            );
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @SuppressLint("Range")
    public String ReadConfig(String key) {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT DataValue FROM Config WHERE KeyValue=? LIMIT 1",
                new String[]{legacyText(key)})) {
            if (cursor.moveToFirst()) {
                String value = cursor.getString(cursor.getColumnIndex("DataValue"));
                return value == null ? "" : value;
            }
            return "";
        }
    }

    private void insertDefaultConfig(SQLiteDatabase database, String key, String value) {
        database.execSQL(
                "INSERT INTO Config(KeyValue, DataValue) "
                        + "SELECT ?, ? WHERE NOT EXISTS("
                        + "SELECT 1 FROM Config WHERE KeyValue=?)",
                new Object[]{key, value, key}
        );
    }

    private static String legacyText(String value) {
        return String.valueOf(value);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Profile databases are downloaded snapshots and initialized explicitly.
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Version remains 1 until a real production snapshot can be migration-tested.
    }
}
