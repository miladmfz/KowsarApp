package com.kits.kowsarapp.model.find;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Activation;
import com.kits.kowsarapp.model.base.Column;
import com.kits.kowsarapp.model.base.UserInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Find_DBH extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private final CallMethod callMethod;
    private int limitcolumn;

    public Find_DBH(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        callMethod = new CallMethod(context);
    }

    public void DatabaseCreate() {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS Config ("
                    + "ConfigCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, "
                    + "KeyValue TEXT, DataValue TEXT)");
            database.execSQL("CREATE TABLE IF NOT EXISTS BrokerColumn ("
                    + "ColumnCode INTEGER PRIMARY KEY, SortOrder TEXT, ColumnName TEXT, "
                    + "ColumnDesc TEXT, GoodType TEXT, ColumnDefinition TEXT, "
                    + "ColumnType TEXT, Condition TEXT, OrderIndex TEXT, AppType INTEGER)");
            database.execSQL("CREATE TABLE IF NOT EXISTS GoodType ("
                    + "GoodTypeCode INTEGER PRIMARY KEY, GoodType TEXT, IsDefault TEXT)");
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
                + "MaxDevice TEXT,"
                + "SecendServerURL TEXT,"
                + "DbName TEXT,"
                + "AppType TEXT)");
    }

    public void deleteColumn() {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.delete("BrokerColumn", null, null);
            database.delete("GoodType", null, null);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @SuppressLint("Range")
    public void ReplicateGoodtype(Column column) {
        String goodType = legacyText(column.getColumnFieldValue("GoodType"));
        boolean exists;
        SQLiteDatabase database = getWritableDatabase();
        try (Cursor cursor = database.rawQuery(
                "SELECT 1 FROM GoodType WHERE GoodType=? LIMIT 1",
                new String[]{goodType})) {
            exists = cursor.moveToFirst();
        }
        if (!exists) {
            ContentValues values = new ContentValues();
            values.put("GoodType", goodType);
            values.put("IsDefault", legacyText(column.getColumnFieldValue("IsDefault")));
            database.insertOrThrow("GoodType", null, values);
        }
    }

    @SuppressLint("Range")
    public UserInfo LoadPersonalInfo() {
        UserInfo user = new UserInfo();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT KeyValue, DataValue FROM Config", null)) {
            while (cursor.moveToNext()) {
                String key = cursor.getString(cursor.getColumnIndex("KeyValue"));
                String value = cursor.getString(cursor.getColumnIndex("DataValue"));
                if (key == null) {
                    continue;
                }
                switch (key) {
                    case "Email": user.setEmail(value); break;
                    case "NameFamily": user.setNameFamily(value); break;
                    case "Address": user.setAddress(value); break;
                    case "Mobile": user.setMobile(value); break;
                    case "Phone": user.setPhone(value); break;
                    case "BirthDate": user.setBirthDate(value); break;
                    case "PostalCode": user.setPostalCode(value); break;
                    case "MelliCode": user.setMelliCode(value); break;
                    case "ActiveCode": user.setActiveCode(value); break;
                    case "BrokerCode": user.setBrokerCode(value); break;
                    default: break;
                }
            }
        }
        return user;
    }

    public void SavePersonalInfo(UserInfo user) {
        if (user == null || user.getBrokerCode() == null
                || user.getBrokerCode().isEmpty()) {
            return;
        }
        SaveConfig("BrokerCode", user.getBrokerCode());
    }

    public void ReplicateColumn(Column column, Integer appType) {
        ContentValues values = new ContentValues();
        values.put("SortOrder", legacyText(column.getColumnFieldValue("SortOrder")));
        values.put("ColumnName", legacyText(column.getColumnFieldValue("ColumnName")));
        values.put("ColumnDesc", legacyText(column.getColumnFieldValue("ColumnDesc")));
        values.put("GoodType", legacyText(column.getColumnFieldValue("GoodType")));
        values.put("ColumnDefinition",
                legacyText(column.getColumnFieldValue("ColumnDefinition")));
        values.put("ColumnType", legacyText(column.getColumnFieldValue("ColumnType")));
        values.put("Condition", legacyText(column.getColumnFieldValue("Condition")));
        values.put("OrderIndex", legacyText(column.getColumnFieldValue("OrderIndex")));
        if (appType == null) {
            values.putNull("AppType");
        } else {
            values.put("AppType", appType);
        }
        getWritableDatabase().insertOrThrow("BrokerColumn", null, values);
    }

    @SuppressLint("Range")
    public ArrayList<Activation> getActivation() {
        ArrayList<Activation> activations = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM Activation", null)) {
            while (cursor.moveToNext()) {
                Activation activation = new Activation();
                activation.setAppBrokerCustomerCode(
                        optionalString(cursor, "AppBrokerCustomerCode"));
                activation.setActivationCode(optionalString(cursor, "ActivationCode"));
                activation.setPersianCompanyName(
                        optionalString(cursor, "PersianCompanyName"));
                activation.setEnglishCompanyName(
                        optionalString(cursor, "EnglishCompanyName"));
                activation.setServerURL(optionalString(cursor, "ServerURL"));
                activation.setSQLiteURL(optionalString(cursor, "SQLiteURL"));
                activation.setMaxDevice(optionalString(cursor, "MaxDevice"));
                activations.add(activation);
            }
        }
        return activations;
    }

    @SuppressLint("Range")
    public String GetColumnscount() {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) AS result FROM BrokerColumn", null)) {
            return cursor.moveToFirst()
                    ? String.valueOf(cursor.getInt(cursor.getColumnIndex("result")))
                    : "0";
        }
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
                database.update("Activation", values, "ActivationCode=?",
                        new String[]{legacyText(activation.getActivationCode())});
            } else {
                values.put("AppBrokerCustomerCode",
                        legacyText(activation.getAppBrokerCustomerCode()));
                values.put("ActivationCode", legacyText(activation.getActivationCode()));
                values.put("PersianCompanyName",
                        legacyText(activation.getPersianCompanyName()));
                values.put("EnglishCompanyName",
                        legacyText(activation.getEnglishCompanyName()));
                values.put("MaxDevice", legacyText(activation.getMaxDevice()));
                values.put("SecendServerURL",
                        legacyText(activation.getSecendServerURL()));
                values.put("DbName", legacyText(activation.getDbName()));
                values.put("AppType", legacyText(activation.getAppType()));
                database.insertOrThrow("Activation", null, values);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void UpdateUrl(String activationCode, String serverUrl) {
        ContentValues values = new ContentValues();
        values.put("ServerURL", legacyText(serverUrl));
        getWritableDatabase().update(
                "Activation",
                values,
                "ActivationCode=?",
                new String[]{legacyText(activationCode)}
        );
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
    public void GetLimitColumn(String appType) {
        try {
            int goodTypeCount = readCount(
                    "SELECT COUNT(*) FROM GoodType", null);
            int columnCount = readCount(
                    "SELECT COUNT(*) FROM BrokerColumn WHERE AppType=?",
                    new String[]{legacyText(appType)});
            if (goodTypeCount <= 0) {
                throw new IllegalStateException("GoodType is empty");
            }
            limitcolumn = columnCount / goodTypeCount;
        } catch (RuntimeException exception) {
            callMethod.showToast("تنظیم جدول از سمت دیتابیس مشکل دارد");
            callMethod.Log("Find column configuration is invalid");
        }
    }

    @SuppressLint("Range")
    public ArrayList<Column> GetColumns(
            String code,
            String goodtype,
            @NonNull String appType
    ) {
        ArrayList<Column> columns = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM BrokerColumn", null)) {
            while (cursor.moveToNext()) {
                Column column = new Column();
                column.setColumnCode(optionalString(cursor, "ColumnCode"));
                column.setSortOrder(optionalString(cursor, "SortOrder"));
                column.setColumnName(optionalString(cursor, "ColumnName"));
                column.setColumnDesc(optionalString(cursor, "ColumnDesc"));
                column.setGoodType(optionalString(cursor, "GoodType"));
                column.setColumnType(optionalString(cursor, "ColumnType"));
                column.setColumnDefinition(optionalString(cursor, "ColumnDefinition"));
                column.setCondition(optionalString(cursor, "Condition"));
                column.setOrderIndex(optionalString(cursor, "OrderIndex"));
                columns.add(column);
            }
        }
        return columns;
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

    private int readCount(String sql, String[] args) {
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, args)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    private static String optionalString(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex < 0 || cursor.isNull(columnIndex)) return null;
        return cursor.getString(columnIndex);
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
        // Downloaded profile snapshots are initialized explicitly.
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Version remains 1 until a real installed database can be migration-tested.
    }
}
