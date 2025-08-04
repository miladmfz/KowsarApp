package com.kits.kowsarapp.model.ocr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kits.kowsarapp.BuildConfig;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.Activation;
import com.kits.kowsarapp.model.base.Factor;
import com.kits.kowsarapp.model.base.Good;
import com.kits.kowsarapp.model.base.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class Ocr_DBH extends SQLiteOpenHelper {

    CallMethod callMethod;
    ArrayList<Good> goods;
    Context context;
    Cursor cursor;

    String query = "";

    boolean SH_ArabicText;



    public Ocr_DBH(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        this.callMethod = new CallMethod(context);
        this.goods = new ArrayList<>();
        this.context = context;

    }

    public void GetPreference() {


        this.SH_ArabicText = callMethod.ReadBoolan("ArabicText");
    }



    public void DatabaseCreate() {
        callMethod.Log("Ocr DatabaseCreate");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS FactorScan (RowCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE " +
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

        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS PackDetailReader (PackDetailReader INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , Reader TEXT )");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS PackDetailControler (PackDetailControler INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , Controler TEXT)");
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS PackDetailpack (PackDetailpack INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , pack TEXT)");
    }




    @SuppressLint("Range")
    public ArrayList<Factor> factorscan(String IsSent, String SearchTarget, String signature) {
        String query = "SELECT *  FROM FactorScan ";
        String cond = "";
        SearchTarget = SearchTarget.replaceAll(" ", "%");
        if (SearchTarget.equals("")) {
            cond = "Where 1=1";
        } else {
            cond = "Where (" +
                    "FactorBarcode Like '%" + SearchTarget + "%' or " +
                    "FactorPrivateCode Like '%" + SearchTarget + "%' or " +
                    "CustomerCode Like '%" + SearchTarget + "%' or " +
                    "CustomerName Like '%" + GetPersianText(SearchTarget) + "%' or " +
                    "CustomerName Like '%" + GetArabicText(SearchTarget) + "%'" +
                    ")";
        }

        if (IsSent.equals("0")) {
            cond = cond + " And IsSent = '0' ";
        }

        if (signature.equals("1")) {
             cond = cond+" And SignatureImage = '' ";
        }

        query = query + cond;
        query=query+" Order By FactorBarcode DESC";


        ArrayList<Factor> factors = new ArrayList<Factor>();

        callMethod.Log("query = "+ query);

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Factor factor_detail = new Factor();
                factor_detail.setAppOCRFactorCode(cursor.getString(cursor.getColumnIndex("AppOCRFactorCode")));
                factor_detail.setFactorBarcode(cursor.getString(cursor.getColumnIndex("FactorBarcode")));
                factor_detail.setFactorPrivateCode(cursor.getString(cursor.getColumnIndex("FactorPrivateCode")));
                factor_detail.setSignatureImage(cursor.getString(cursor.getColumnIndex("SignatureImage")));
                factor_detail.setFactorImage(cursor.getString(cursor.getColumnIndex("FactorImage")));
                factor_detail.setCameraImage(cursor.getString(cursor.getColumnIndex("CameraImage")));
                factor_detail.setFactorDate(cursor.getString(cursor.getColumnIndex("FactorDate")));
                factor_detail.setScanDate(cursor.getString(cursor.getColumnIndex("ScanDate")));
                factor_detail.setIsSent(cursor.getString(cursor.getColumnIndex("IsSent")));
                factor_detail.setCustName(cursor.getString(cursor.getColumnIndex("CustomerName")));
                factor_detail.setCustomerCode(cursor.getString(cursor.getColumnIndex("CustomerCode")));
                factor_detail.setDeliverer(cursor.getString(cursor.getColumnIndex("Deliverer")));
                factor_detail.setDbname(cursor.getString(cursor.getColumnIndex("DbName")));
                factor_detail.setCheck(false);

                factors.add(factor_detail);
            }
        }

        assert cursor != null;
        cursor.close();
        return factors;
    }

    @SuppressLint("Range")
    public String getimagefromfactor(String FactorBarcode, String ImageRequest) {
        String bitmap_String = "";
        String query = "SELECT *  FROM FactorScan Where FactorBarcode= '"+FactorBarcode+"'";


        cursor = getWritableDatabase().rawQuery(query, null);

        callMethod.Log("query=" + query);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                bitmap_String =cursor.getString(cursor.getColumnIndex(ImageRequest));
            }
        }
        cursor.close();
        return bitmap_String;
    }

    @SuppressLint("Range")
    public String GetRegionText(String String) {
        GetPreference();
        if(SH_ArabicText) {
            //arabic
            query = "Select Replace(Replace(Cast('" + String + "' as nvarchar(500)),char(1740),char(1610)),char(1705),char(1603)) result  ";
        }else{
            //Persian
            query = "Select Replace(Replace(Cast('" + String + "' as nvarchar(500)),char(1610),char(1740)),char(1603),char(1705)) result  " ;
        }

        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex("result"));
        cursor.close();

        return result;
    }

    @SuppressLint("Range")
    public String GetPersianText(String String) {
        query = "Select Replace(Replace(Cast('" + String + "' as nvarchar(500)),char(1610),char(1740)),char(1603),char(1705)) result  " ;
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex("result"));
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public String GetArabicText(String String) {
        query = "Select Replace(Replace(Cast('" + String + "' as nvarchar(500)),char(1740),char(1610)),char(1705),char(1603)) result  ";
        cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex("result"));
        cursor.close();
        return result;
    }


    public void InsertScan(String AppOCRFactorCode,String factorbarcode, String factorprivatecode, String FactorDate, String customername, String customercode) {
        String Date = Utilities.getCurrentShamsidate();
        String query = "SELECT *  FROM FactorScan where FactorBarcode = '"+factorbarcode+"'";

        cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            callMethod.showToast("فاکتور اسکن شده است");
        } else {
            getWritableDatabase().execSQL("INSERT INTO FactorScan(AppOCRFactorCode,FactorBarcode, FactorPrivateCode, SignatureImage, FactorImage, CameraImage,IsSent, FactorDate, ScanDate, CustomerName, CustomerCode,Deliverer,DbName)" +
                    " VALUES ('"+AppOCRFactorCode+"','"+factorbarcode+"', '"+factorprivatecode+"', '','','', '0', '"+FactorDate+"', '"+Date+"', '"+customername+"', '"+customercode+"', '"+callMethod.ReadString("Deliverer")+"','"+callMethod.ReadString("FactorDbName")+"')");

        }
        cursor.close();
    }





    public void Insert_signature(String factorbarcode, String Image) {

        String sq="Update FactorScan set SignatureImage= '" + Image + "' where FactorBarcode = '"+ factorbarcode+"'";
        getWritableDatabase().execSQL(sq);

    }
//
//
//    public void DeleteLastWeek() throws ParseException {
//        String query = "SELECT date('now','-10 day') As xDay";
//        Cursor dc = getWritableDatabase().rawQuery(query, null);
//        dc.moveToFirst();
//
//        Utilities utilities = new Utilities();
//        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
//        @SuppressLint("Range") Date mDate = frmt.parse(dc.getString(dc.getColumnIndex("xDay")));
//        String xDate = utilities.getShamsidate(mDate);
//
//        String sq="Delete from  FactorScan Where ScanDate <="+xDate;
//        getWritableDatabase().execSQL(sq);
//
//    }

    public ArrayList<String> Packdetail(String Key) {

        ArrayList<String> Packdetails = new ArrayList<String>();
        String query ="";
        Packdetails.add("");
        switch (Key) {
            case "Reader":
                query="SELECT Reader  FROM PackDetailReader";
                break;
            case "Controler":
                query="SELECT Controler  FROM PackDetailControler";
                break;
            case "pack":
                query="SELECT pack  FROM PackDetailpack";
                break;
        }

        cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {

                @SuppressLint("Range") String s=cursor.getString(cursor.getColumnIndex(Key));
                Packdetails.add(s);
            }
        }
        assert cursor != null;
        cursor.close();

        return Packdetails;
    }

    public void Insert_Packdetail(String Key, String value) {
        switch (Key) {
            case "Reader":
                getWritableDatabase().execSQL("INSERT INTO PackDetailReader(Reader) VALUES ('" + value + "')");
                break;
            case "Controler":
                getWritableDatabase().execSQL("INSERT INTO PackDetailControler(Controler) VALUES ('" + value + "')");
                break;
            case "pack":
                getWritableDatabase().execSQL("INSERT INTO PackDetailpack(pack) VALUES ('" + value + "')");
                break;
        }
    }

    public void Insert_factorImage(String factorbarcode, String Image) {

        String sq="Update FactorScan set FactorImage= '" + Image + "' where FactorBarcode = '"+ factorbarcode+"'";
        getWritableDatabase().execSQL(sq);

    }

    public void  Insert_cameraImage(String factorbarcode, String Image) {

        String sq="Update FactorScan set CameraImage= '" + Image + "' where FactorBarcode = '"+ factorbarcode+"'";
        getWritableDatabase().execSQL(sq);

    }

    public void Insert_IsSent(String factorbarcode) {

        String sq="Update FactorScan set IsSent= '1' where FactorBarcode = '"+ factorbarcode+"'";

        getWritableDatabase().execSQL(sq);

    }

    public void deletescan(String barcode) {
        String query = " Delete From FactorScan Where FactorBarcode= '" + barcode+"'";
        getWritableDatabase().execSQL(query);
    }


        @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}