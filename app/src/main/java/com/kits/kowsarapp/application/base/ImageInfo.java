package com.kits.kowsarapp.application.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.kits.kowsarapp.application.base.CallMethod;

import java.io.File;
import java.io.FileOutputStream;

public class ImageInfo {


    private final Context mContext;
    CallMethod callMethod;


    public ImageInfo(Context mContext) {
        this.mContext = mContext;
        callMethod = new CallMethod(mContext);
    }


    public void SaveImage(Bitmap finalBitmap, String code) {

        File dir = new File(Environment.getExternalStorageDirectory() + "/Kowsar/" + callMethod.ReadString("EnglishCompanyNameUse") + "/");
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
        }
        String fname = code + ".jpg";
        File file = new File(dir, fname);
        file.setWritable(true);
        try {
            FileOutputStream out = new FileOutputStream(file, true);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            callMethod.Log(e.getMessage());

        }

    }

    public void SaveImage_factor(Bitmap finalBitmap, String code) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Kowsar/factorimage/");
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }
        String fname = code + ".jpg";
        File file = new File(dir, fname);
        file.setWritable(true);
        try {
            FileOutputStream out = new FileOutputStream(file, true);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }
    }


    public void DeleteImage(String code) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/Kowsar/" + callMethod.ReadString("EnglishCompanyNameUse") + "/");
        myDir.mkdirs();

        String fname = code + ".jpg";
        File file = new File(myDir, fname);
        try {
            file.delete();
        } catch (Exception e) {

            callMethod.Log(e.getMessage());
        }
    }


    public Boolean Image_exist(String code) {
        String root = Environment.getExternalStorageDirectory() + "/Kowsar";
        File imagefile = new File(root + "/" + callMethod.ReadString("EnglishCompanyNameUse") + "/" + code + ".jpg");
        return imagefile.exists();

    }
    public void SaveLogo(Bitmap finalBitmap) {

        File dir = new File(Environment.getExternalStorageDirectory() + "/Kowsar/" + callMethod.ReadString("EnglishCompanyNameUse") + "/");
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
        }
        String fname ="Logo.jpg";
        File file = new File(dir, fname);
        file.setWritable(true);
        try {
            FileOutputStream out = new FileOutputStream(file, true);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            callMethod.Log(e.getMessage());
        }

    }
    public Bitmap LoadLogo() {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File imagefile = new File(root + "/Kowsar/" +
                callMethod.ReadString("EnglishCompanyNameUse") + "/Logo.jpg"
        );
        return BitmapFactory.decodeFile(imagefile.getAbsolutePath());

    }


}
