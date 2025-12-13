package com.kits.kowsarapp.model.base;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationResult;
import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.application.base.ThirdPartyResult;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Base_DBH extends SQLiteOpenHelper {
    CallMethod callMethod;
    Cursor cursor;
    String query = "";

    public Base_DBH(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        this.callMethod = new CallMethod(context);
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
                "UsedDevice TEXT," +
                "SecendServerURL TEXT," +
                "DbName TEXT," +
                "AppType TEXT," +
                "ServerPort TEXT," +
                "ServerPathApi TEXT," +
                "ServerIp TEXT)");
        getWritableDatabase().close();
    }



    public void CreatePaymentLog() {
        getWritableDatabase().execSQL(
                "CREATE TABLE IF NOT EXISTS PaymentLog (" +
                        "Id INTEGER PRIMARY KEY AUTOINCREMENT," +

                        "PreFac TEXT," +
                        "SessionId TEXT," +

                        "ResultCode TEXT," +
                        "ResultDescription TEXT," +

                        "TransactionAmount TEXT," +
                        "ReferenceID TEXT," +
                        "RetrievalReferencedNumber TEXT," +
                        "MaskedCardNumber TEXT," +

                        "TerminalID TEXT," +
                        "DateOfTransaction TEXT," +
                        "TimeOfTransaction TEXT," +

                        "EchoData TEXT," +

                        "RawJson TEXT," +        // کل JSON برگشتی از پوز
                        "CreateDate TEXT" +      // زمان ثبت در برنامه
                        ")"
        );

        getWritableDatabase().close();
    }


    public long InsertPaymentLog(
            String preFac,
            ThirdPartyResult res,
            String rawJson
    ) {
        callMethod.Log("preFac =" + preFac);


        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("PreFac", preFac);
        cv.put("SessionId", res.sessionId);

        cv.put("ResultCode", res.resultCode);
        cv.put("ResultDescription", res.resultDescription);

        cv.put("TransactionAmount", res.transactionAmount);
        cv.put("ReferenceID", res.referenceID);

        cv.put(
                "RetrievalReferencedNumber",
                res.retrievalReferencedNumber == null
                        ? null
                        : String.valueOf(res.retrievalReferencedNumber)
        );

        cv.put("MaskedCardNumber", res.maskedCardNumber);

        cv.put("TerminalID", res.terminalID);
        cv.put("DateOfTransaction", res.dateOfTransaction);
        cv.put("TimeOfTransaction", res.timeOfTransaction);

        cv.put("EchoData", res.echoData);

        cv.put("RawJson", rawJson);

        cv.put(
                "CreateDate",
                new java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        java.util.Locale.US
                ).format(new java.util.Date())
        );

        long rowId = db.insert("PaymentLog", null, cv);
        db.close();

        return rowId;
    }




    public void UpdateActivation(Activation activation) {

        getWritableDatabase().execSQL("Update Activation set " +

                "PersianCompanyName = '" + activation.getPersianCompanyName() + "' ," +
                "EnglishCompanyName = '" + activation.getEnglishCompanyName() + "' ," +
                "ServerURL = '" + activation.getServerURL() + "' ," +
                "SQLiteURL = '" + activation.getSQLiteURL() + "' ," +
                "MaxDevice = '" + activation.getMaxDevice() + "' ," +
                "UsedDevice = '" + activation.getUsedDevice() + "' ," +
                "ServerIp = '" + activation.getServerIp() + "' ," +
                "ServerPort = '" + activation.getServerPort() + "' ," +
                "ServerPathApi = '" + activation.getServerPathApi() + "' ," +
                "SecendServerURL = '" + activation.getSecendServerURL() + "' ," +
                "DbName = '" + activation.getDbName() + "' ," +
                "AppType = '" + activation.getAppType() + "' " +
                "Where ActivationCode= '" + activation.getActivationCode() + "'");
    }
    public void UpdateUrl(String ActivationCode,String ServerURL) {

        getWritableDatabase().execSQL("Update Activation set " +
                "ServerURL = '" + ServerURL + "' " +
                "Where ActivationCode= '" + ActivationCode + "'");



    }

    @SuppressLint("Range")
    public ArrayList<Activation> getActivation() {
        query = "Select * From Activation ORDER BY 1 DESC";
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
                    activation.setUsedDevice(cursor.getString(cursor.getColumnIndex("UsedDevice")));
                    activation.setSecendServerURL(cursor.getString(cursor.getColumnIndex("SecendServerURL")));
                    activation.setDbName(cursor.getString(cursor.getColumnIndex("DbName")));
                    activation.setAppType(cursor.getString(cursor.getColumnIndex("AppType")));
                    activation.setServerIp(cursor.getString(cursor.getColumnIndex("ServerIp")));
                    activation.setServerPort(cursor.getString(cursor.getColumnIndex("ServerPort")));
                    activation.setServerPathApi(cursor.getString(cursor.getColumnIndex("ServerPathApi")));
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


    public void DeleteActivation(@NotNull Activation activation) {
        getWritableDatabase().execSQL("Delete from Activation Where ActivationCode= '" + activation.getActivationCode() + "'");

    }

    public void InsertActivation(@NotNull Activation activation) {

        query = "select * from Activation Where ActivationCode= '" + activation.getActivationCode() + "'";
        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.getCount() > 0) {
            UpdateActivation( activation);
        } else {
            getWritableDatabase().execSQL(" Insert Into Activation(AppBrokerCustomerCode,ActivationCode,PersianCompanyName, EnglishCompanyName,ServerURL,SQLiteURL,MaxDevice,UsedDevice,SecendServerURL,DbName,AppType,ServerIp,ServerPort,ServerPathApi)" +
                     " Select '" + activation.getAppBrokerCustomerCode() + "','" + activation.getActivationCode() + "','" +
                    activation.getPersianCompanyName() + "','" + activation.getEnglishCompanyName() + "','" +
                    activation.getServerURL() + "','" + activation.getSQLiteURL() + "','" + activation.getMaxDevice() + "','" +
                    activation.getUsedDevice() + "','" + activation.getSecendServerURL() + "','" + activation.getDbName() + "','" +
                    activation.getAppType() + "','" + activation.getServerIp() + "','" + activation.getServerPort() + "','" +
                    activation.getServerPathApi() + "'");

        }


    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {}
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}