package com.kits.kowsarapp.model.base;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ThirdPartyResult;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Base_DBH extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private final CallMethod callMethod;

    public Base_DBH(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        callMethod = new CallMethod(context);
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
                + "UsedDevice TEXT,"
                + "SecendServerURL TEXT,"
                + "DbName TEXT,"
                + "AppType TEXT,"
                + "ServerPort TEXT,"
                + "ServerPathApi TEXT,"
                + "ServerIp TEXT)");
    }

    public void CreatePaymentLog() {
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS PaymentLog ("
                + "Id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "PreFac TEXT,"
                + "SessionId TEXT,"
                + "ResultCode TEXT,"
                + "ResultDescription TEXT,"
                + "TransactionAmount TEXT,"
                + "ReferenceID TEXT,"
                + "RetrievalReferencedNumber TEXT,"
                + "MaskedCardNumber TEXT,"
                + "TerminalID TEXT,"
                + "DateOfTransaction TEXT,"
                + "TimeOfTransaction TEXT,"
                + "EchoData TEXT,"
                + "RawJson TEXT,"
                + "CreateDate TEXT)");
    }

    public long InsertPaymentLog(
            String preFac,
            ThirdPartyResult result,
            String rawJson
    ) {
        ContentValues values = new ContentValues();
        values.put("PreFac", preFac);
        values.put("SessionId", result.sessionId);
        values.put("ResultCode", result.resultCode);
        values.put("ResultDescription", result.resultDescription);
        values.put("TransactionAmount", result.transactionAmount);
        values.put("ReferenceID", result.referenceID);
        values.put("RetrievalReferencedNumber",
                result.retrievalReferencedNumber == null
                        ? null
                        : String.valueOf(result.retrievalReferencedNumber));
        values.put("MaskedCardNumber", result.maskedCardNumber);
        values.put("TerminalID", result.terminalID);
        values.put("DateOfTransaction", result.dateOfTransaction);
        values.put("TimeOfTransaction", result.timeOfTransaction);
        values.put("EchoData", result.echoData);
        values.put("RawJson", rawJson);
        values.put("CreateDate",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                        .format(new Date()));
        return getWritableDatabase().insert("PaymentLog", null, values);
    }

    public void UpdateActivation(Activation activation) {
        getWritableDatabase().update(
                "Activation",
                activationValues(activation, false),
                "ActivationCode=?",
                new String[]{legacyText(activation.getActivationCode())}
        );
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

    @SuppressLint("Range")
    public ArrayList<Activation> getActivation() {
        ArrayList<Activation> activations = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM Activation ORDER BY 1 DESC", null)) {
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
                    activation.setUsedDevice(cursor.getString(
                            cursor.getColumnIndex("UsedDevice")));
                    activation.setSecendServerURL(cursor.getString(
                            cursor.getColumnIndex("SecendServerURL")));
                    activation.setDbName(cursor.getString(cursor.getColumnIndex("DbName")));
                    activation.setAppType(cursor.getString(cursor.getColumnIndex("AppType")));
                    activation.setServerIp(cursor.getString(cursor.getColumnIndex("ServerIp")));
                    activation.setServerPort(cursor.getString(
                            cursor.getColumnIndex("ServerPort")));
                    activation.setServerPathApi(cursor.getString(
                            cursor.getColumnIndex("ServerPathApi")));
                } catch (RuntimeException exception) {
                    callMethod.Log("Activation row is incomplete");
                }
                activations.add(activation);
            }
        }
        return activations;
    }

    public void DeleteActivation(@NotNull Activation activation) {
        getWritableDatabase().delete(
                "Activation",
                "ActivationCode=?",
                new String[]{legacyText(activation.getActivationCode())}
        );
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

            if (exists) {
                database.update(
                        "Activation",
                        activationValues(activation, false),
                        "ActivationCode=?",
                        new String[]{legacyText(activation.getActivationCode())}
                );
            } else {
                database.insertOrThrow(
                        "Activation",
                        null,
                        activationValues(activation, true)
                );
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private ContentValues activationValues(Activation activation, boolean includeCode) {
        ContentValues values = new ContentValues();
        values.put("AppBrokerCustomerCode",
                legacyText(activation.getAppBrokerCustomerCode()));
        if (includeCode) {
            values.put("ActivationCode", legacyText(activation.getActivationCode()));
        }
        values.put("PersianCompanyName", legacyText(activation.getPersianCompanyName()));
        values.put("EnglishCompanyName", legacyText(activation.getEnglishCompanyName()));
        values.put("ServerURL", legacyText(activation.getServerURL()));
        values.put("SQLiteURL", legacyText(activation.getSQLiteURL()));
        values.put("MaxDevice", legacyText(activation.getMaxDevice()));
        values.put("UsedDevice", legacyText(activation.getUsedDevice()));
        values.put("ServerIp", legacyText(activation.getServerIp()));
        values.put("ServerPort", legacyText(activation.getServerPort()));
        values.put("ServerPathApi", legacyText(activation.getServerPathApi()));
        values.put("SecendServerURL", legacyText(activation.getSecendServerURL()));
        values.put("DbName", legacyText(activation.getDbName()));
        values.put("AppType", legacyText(activation.getAppType()));
        return values;
    }

    private static String legacyText(String value) {
        return String.valueOf(value);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // The activation database is initialized explicitly by CreateActivationDb().
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Version remains 1 until a real installed database can be migration-tested.
    }
}
