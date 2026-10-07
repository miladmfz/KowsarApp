package com.kits.kowsarapp.model.ocr;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.Utilities;

import java.util.ArrayList;


public class Ocr_DBH extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private final CallMethod callMethod;
    private boolean SH_ArabicText;

    public Ocr_DBH(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.callMethod = new CallMethod(context);
    }

    public void GetPreference() {
        this.SH_ArabicText = callMethod.ReadBoolan("ArabicText");
    }

    public void DatabaseCreate() {
        callMethod.Log("Ocr DatabaseCreate");
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS FactorScan (RowCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE " +
                    ", AppOCRFactorCode TEXT" +
                    ", FactorBarcode TEXT" +
                    ", FactorPrivateCode TEXT" +
                    ", FactorImage TEXT" +
                    ", CameraImage TEXT" +
                    ", SignatureImage TEXT" +
                    ", FactorDate TEXT" +
                    ", ScanDate TEXT" +
                    ", IsSent TEXT" +
                    ", CustomerName TEXT" +
                    ", CustomerCode TEXT" +
                    ", Deliverer TEXT" +
                    ", DbName TEXT)");

            database.execSQL("CREATE TABLE IF NOT EXISTS PackDetailReader (PackDetailReader INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , Reader TEXT )");
            database.execSQL("CREATE TABLE IF NOT EXISTS PackDetailControler (PackDetailControler INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , Controler TEXT)");
            database.execSQL("CREATE TABLE IF NOT EXISTS PackDetailpack (PackDetailpack INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , pack TEXT)");
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @SuppressLint("Range")
    public ArrayList<Factor> factorscan(String IsSent, String SearchTarget, String signature) {
        String normalizedSearch = SearchTarget == null ? "" : SearchTarget.replace(" ", "%");
        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<String> arguments = new ArrayList<>();

        if (!normalizedSearch.isEmpty()) {
            conditions.add("(FactorBarcode LIKE ? OR FactorPrivateCode LIKE ? OR CustomerCode LIKE ? OR CustomerName LIKE ? OR CustomerName LIKE ?)");
            arguments.add("%" + normalizedSearch + "%");
            arguments.add("%" + normalizedSearch + "%");
            arguments.add("%" + normalizedSearch + "%");
            arguments.add("%" + GetPersianText(normalizedSearch) + "%");
            arguments.add("%" + GetArabicText(normalizedSearch) + "%");
        }

        if ("0".equals(IsSent)) {
            conditions.add("IsSent = ?");
            arguments.add("0");
        }

        if ("1".equals(signature)) {
            conditions.add("SignatureImage = ?");
            arguments.add("");
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM FactorScan WHERE 1=1");
        for (String condition : conditions) {
            sql.append(" AND ").append(condition);
        }
        sql.append(" ORDER BY FactorBarcode DESC");

        ArrayList<Factor> factors = new ArrayList<>();
        callMethod.Log("Ocr factorscan query executed");
        try (Cursor cursor = getReadableDatabase().rawQuery(
                sql.toString(), arguments.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                Factor factorDetail = new Factor();
                factorDetail.setAppOCRFactorCode(cursor.getString(cursor.getColumnIndex("AppOCRFactorCode")));
                factorDetail.setFactorBarcode(cursor.getString(cursor.getColumnIndex("FactorBarcode")));
                factorDetail.setFactorPrivateCode(cursor.getString(cursor.getColumnIndex("FactorPrivateCode")));
                factorDetail.setSignatureImage(cursor.getString(cursor.getColumnIndex("SignatureImage")));
                factorDetail.setFactorImage(cursor.getString(cursor.getColumnIndex("FactorImage")));
                factorDetail.setCameraImage(cursor.getString(cursor.getColumnIndex("CameraImage")));
                factorDetail.setFactorDate(cursor.getString(cursor.getColumnIndex("FactorDate")));
                factorDetail.setScanDate(cursor.getString(cursor.getColumnIndex("ScanDate")));
                factorDetail.setIsSent(cursor.getString(cursor.getColumnIndex("IsSent")));
                factorDetail.setCustName(cursor.getString(cursor.getColumnIndex("CustomerName")));
                factorDetail.setCustomerCode(cursor.getString(cursor.getColumnIndex("CustomerCode")));
                factorDetail.setDeliverer(cursor.getString(cursor.getColumnIndex("Deliverer")));
                factorDetail.setDbname(cursor.getString(cursor.getColumnIndex("DbName")));
                factorDetail.setCheck(false);
                factors.add(factorDetail);
            }
        }
        return factors;
    }

    @SuppressLint("Range")
    public String getimagefromfactor(String FactorBarcode, String ImageRequest) {
        if (!isImageColumn(ImageRequest)) {
            return "";
        }

        try (Cursor cursor = getReadableDatabase().query(
                "FactorScan",
                new String[]{ImageRequest},
                "FactorBarcode = ?",
                new String[]{legacyText(FactorBarcode)},
                null,
                null,
                null)) {
            if (cursor.moveToFirst()) {
                String value = cursor.getString(cursor.getColumnIndex(ImageRequest));
                return value == null ? "" : value;
            }
        }
        return "";
    }

    private boolean isImageColumn(String columnName) {
        return "SignatureImage".equals(columnName)
                || "FactorImage".equals(columnName)
                || "CameraImage".equals(columnName);
    }

    public String GetRegionText(String value) {
        GetPreference();
        return SH_ArabicText ? GetArabicText(value) : GetPersianText(value);
    }

    public String GetPersianText(String value) {
        return OcrTextNormalizer.toPersian(value);
    }

    public String GetArabicText(String value) {
        return OcrTextNormalizer.toArabic(value);
    }

    public void InsertScan(String AppOCRFactorCode, String factorbarcode, String factorprivatecode,
                           String FactorDate, String customername, String customercode) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try (Cursor cursor = database.query(
                "FactorScan",
                new String[]{"RowCode"},
                "FactorBarcode = ?",
                new String[]{legacyText(factorbarcode)},
                null,
                null,
                null,
                "1")) {
            if (cursor.moveToFirst()) {
                callMethod.showToast("ظپط§ع©طھظˆط± ط§ط³ع©ظ† ط´ط¯ظ‡ ط§ط³طھ");
            } else {
                ContentValues values = new ContentValues();
                values.put("AppOCRFactorCode", legacyText(AppOCRFactorCode));
                values.put("FactorBarcode", legacyText(factorbarcode));
                values.put("FactorPrivateCode", legacyText(factorprivatecode));
                values.put("SignatureImage", "");
                values.put("FactorImage", "");
                values.put("CameraImage", "");
                values.put("IsSent", "0");
                values.put("FactorDate", legacyText(FactorDate));
                values.put("ScanDate", Utilities.getCurrentShamsidate());
                values.put("CustomerName", legacyText(customername));
                values.put("CustomerCode", legacyText(customercode));
                values.put("Deliverer", callMethod.ReadString("Deliverer"));
                values.put("DbName", callMethod.ReadString("FactorDbName"));
                database.insertOrThrow("FactorScan", null, values);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void Insert_signature(String factorbarcode, String Image) {
        updateFactorValue("SignatureImage", Image, factorbarcode);
    }

    public ArrayList<String> Packdetail(String Key) {
        ArrayList<String> packDetails = new ArrayList<>();
        packDetails.add("");

        String tableName = packDetailTable(Key);
        if (tableName == null) {
            return packDetails;
        }

        try (Cursor cursor = getReadableDatabase().query(
                tableName,
                new String[]{Key},
                null,
                null,
                null,
                null,
                null)) {
            int valueIndex = cursor.getColumnIndex(Key);
            while (cursor.moveToNext()) {
                packDetails.add(cursor.getString(valueIndex));
            }
        }
        return packDetails;
    }

    public void Insert_Packdetail(String Key, String value) {
        String tableName = packDetailTable(Key);
        if (tableName == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(Key, legacyText(value));
        getWritableDatabase().insertOrThrow(tableName, null, values);
    }

    private String packDetailTable(String key) {
        if ("Reader".equals(key)) {
            return "PackDetailReader";
        }
        if ("Controler".equals(key)) {
            return "PackDetailControler";
        }
        if ("pack".equals(key)) {
            return "PackDetailpack";
        }
        return null;
    }

    public void Insert_factorImage(String factorbarcode, String Image) {
        updateFactorValue("FactorImage", Image, factorbarcode);
    }

    public void Insert_cameraImage(String factorbarcode, String Image) {
        updateFactorValue("CameraImage", Image, factorbarcode);
    }

    public void Insert_IsSent(String factorbarcode) {
        updateFactorValue("IsSent", "1", factorbarcode);
    }

    private void updateFactorValue(String columnName, String value, String factorBarcode) {
        ContentValues values = new ContentValues();
        values.put(columnName, legacyText(value));
        getWritableDatabase().update(
                "FactorScan",
                values,
                "FactorBarcode = ?",
                new String[]{legacyText(factorBarcode)});
    }

    public void deletescan(String barcode) {
        getWritableDatabase().delete(
                "FactorScan",
                "FactorBarcode = ?",
                new String[]{legacyText(barcode)});
    }

    private static String legacyText(String value) {
        return String.valueOf(value);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}
