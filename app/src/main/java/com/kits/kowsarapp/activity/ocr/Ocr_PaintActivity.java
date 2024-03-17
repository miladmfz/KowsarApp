package com.kits.kowsarapp.activity.ocr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;

import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.adapter.ocr.Ocr_PaintView;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ocr_PaintActivity extends AppCompatActivity {
    static String strSDCardPathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/kowsar" + "/";

    private Ocr_PaintView paintView;
    String BarcodeScan;
    String bitmap_signature_base;
    Bitmap bitmap_signature;
    Ocr_DBH dbh ;
    Ocr_Action action;
    LinearLayoutCompat main_layout;
    LinearLayoutCompat paint_layout;
    List<Uri> list_imageUri=new ArrayList<>();
    Intent intent;
    String bitmap_factor_base64;
    ImageView imagefactor;
    int width=1;
    ArrayList<String> Multi_barcode = new ArrayList<>();

    String ImageOcrPath="";
    Uri photoURI;
    File photoFile;
    Button button;
    CallMethod callMethod;

    EditText ed_signexplain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_activity_paint);

        Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.ocr_spinner_box);
        TextView repw = dialog1.findViewById(R.id.ocr_spinner_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        }catch (Exception e){
            callMethod.Log(e.getMessage());
        }


    }

    public void init() {

        button.setText("ثبت امضا");
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP,26);

        button.setOnClickListener(view -> {

            if(BarcodeScan.equals("Multi_sign")){

                for (String s : Multi_barcode) {

                    TextView Deliverer = new TextView(getApplicationContext());
                    Deliverer.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Deliverer")));
                    Deliverer.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                    Deliverer.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                    Deliverer.setTextColor(getColor(R.color.colorPrimaryDark));
                    Deliverer.setGravity(Gravity.CENTER);
                    Deliverer.setBackgroundColor(getColor(R.color.white));
                    Deliverer.setPadding(0, 10, 0, 20) ;


                    TextView factorbarcode = new TextView(getApplicationContext());
                    factorbarcode.setText(NumberFunctions.PerisanNumber(s));
                    factorbarcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                    factorbarcode.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                    factorbarcode.setTextColor(getColor(R.color.colorPrimaryDark));
                    factorbarcode.setGravity(Gravity.CENTER);
                    factorbarcode.setBackgroundColor(getColor(R.color.white));
                    factorbarcode.setPadding(0, 10, 0, 20) ;

                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(NumberFunctions.PerisanNumber(ed_signexplain.getText().toString()));
                    textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                    textView.setTextColor(getColor(R.color.colorPrimaryDark));
                    textView.setBackgroundColor(getColor(R.color.white));
                    textView.setGravity(Gravity.CENTER);

                    textView.setPadding(0, 10, 0, 20);

                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                    imageView.setPadding(0, 10, 0, 30);
                    imageView.setImageBitmap(paintView.getpaintview());

                    imagefactor = new ImageView(getApplicationContext());
                    imagefactor.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                    imagefactor.setPadding(0, 0, 0, 0);

                    bitmap_factor_base64=dbh.getimagefromfactor(s,"FactorImage");

                    byte[] imageByteArray1 = Base64.decode(bitmap_factor_base64, Base64.DEFAULT);
                    imagefactor.setImageBitmap(Bitmap.createScaledBitmap(
                            BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length),
                            BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth(),
                            BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight(),
                            false));

                    main_layout.addView(Deliverer);
                    main_layout.addView(factorbarcode);
                    main_layout.addView(textView);
                    main_layout.addView(imageView);
                    main_layout.addView(imagefactor,0);

                    bitmap_signature=loadBitmapFromView(main_layout);


                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap_signature.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    bitmap_signature_base= Base64.encodeToString(byteArray, Base64.DEFAULT);

                    dbh.Insert_signature(s,bitmap_signature_base);
                    main_layout.removeAllViews();
                }
                 callMethod.showToast("با موفقیت ثبت گردید");
                finish();
            }else {
                bitmap_factor_base64=dbh.getimagefromfactor(BarcodeScan,"FactorImage");
                TextView Deliverer = new TextView(getApplicationContext());
                Deliverer.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Deliverer")));
                Deliverer.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                Deliverer.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                Deliverer.setTextColor(getColor(R.color.colorPrimaryDark));
                Deliverer.setGravity(Gravity.CENTER);
                Deliverer.setBackgroundColor(getColor(R.color.white));
                Deliverer.setPadding(0, 10, 0, 20) ;

                TextView factorbarcode = new TextView(getApplicationContext());

                factorbarcode.setText(NumberFunctions.PerisanNumber(BarcodeScan));
                factorbarcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                factorbarcode.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                factorbarcode.setTextColor(getColor(R.color.colorPrimaryDark));
                factorbarcode.setGravity(Gravity.CENTER);
                factorbarcode.setBackgroundColor(getColor(R.color.white));
                factorbarcode.setPadding(0, 10, 0, 20) ;

                TextView textView = new TextView(getApplicationContext());
                textView.setText(NumberFunctions.PerisanNumber(ed_signexplain.getText().toString()));
                textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                textView.setTextColor(getColor(R.color.colorPrimaryDark));
                textView.setBackgroundColor(getColor(R.color.white));
                textView.setGravity(Gravity.CENTER);

                textView.setPadding(0, 10, 0, 20);

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                imageView.setPadding(0, 10, 0, 30);
                imageView.setImageBitmap(paintView.getpaintview());

                imagefactor = new ImageView(getApplicationContext());
                imagefactor.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                imagefactor.setPadding(0, 0, 0, 0);
                byte[] imageByteArray1 = Base64.decode(bitmap_factor_base64, Base64.DEFAULT);
                imagefactor.setImageBitmap(Bitmap.createScaledBitmap(
                        BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length),
                        BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth(),
                        BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight(),
                        false));

                main_layout.addView(Deliverer);
                main_layout.addView(factorbarcode);
                main_layout.addView(textView);
                main_layout.addView(imageView);
                main_layout.addView(imagefactor,0);




                bitmap_signature=loadBitmapFromView(main_layout);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap_signature.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                bitmap_signature_base= Base64.encodeToString(byteArray, Base64.DEFAULT);
                dbh.Insert_signature(BarcodeScan,bitmap_signature_base);

                Button button1 =  new Button(getApplicationContext());
                button1.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                button1.setBackgroundResource(R.color.green_900);
                button1.setText("تایید و ارسال");
                button1.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                button1.setTextColor(getColor(R.color.white));
                button1.setPadding(0, 5, 0, 5);
                button1.setOnClickListener(v -> action.sendfactor(BarcodeScan,bitmap_signature_base));
                Button btn_pic=  new Button(getApplicationContext());
                btn_pic.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                btn_pic.setBackgroundResource(R.color.green_900);
                btn_pic.setText("اضافه کردن عکس");
                btn_pic.setTextColor(getColor(R.color.white));
                btn_pic.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                btn_pic.setOnClickListener(v -> {

                    final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

                    AlertDialog.Builder builder = new AlertDialog.Builder(Ocr_PaintActivity.this);
                    builder.setTitle("Choose your profile picture");

                    builder.setItems(options, (dialog, item) -> {

                        if (options[item].equals("Take Photo")) {
                            dispatchTakePictureIntent();
                        } else if (options[item].equals("Choose from Gallery")) {
                            intent = new Intent();
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent , 1);

                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                });
                paint_layout.setVisibility(View.GONE);
                main_layout.addView(button1,0);
                main_layout.addView(btn_pic,0);

            }

        });

    }
    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));

        action =new Ocr_Action(Ocr_PaintActivity.this);

        main_layout= findViewById(R.id.ocr_paint_a_mainlayout);
        paint_layout= findViewById(R.id.ocr_paint_a_paint);
        main_layout.setGravity(Gravity.CENTER);
        paint_layout.setGravity(Gravity.CENTER);

        paintView = findViewById(R.id.ocr_paint_a_paintView);
        button = findViewById(R.id.ocr_paint_a_send);

        ed_signexplain = findViewById(R.id.ocr_paint_a_explain);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
    }


    public  void intent(){
        Bundle bundle =getIntent().getExtras();
        assert bundle != null;
        BarcodeScan=bundle.getString("ScanResponse");
        bitmap_factor_base64 = bundle.getString("FactorImage");
        width = Integer.parseInt(bundle.getString("Width"));
        if(BarcodeScan.equals("Multi_sign")){
            Multi_barcode = bundle.getStringArrayList( "list");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                assert data != null;
                if(data.getClipData() != null) {

                    main_layout.removeViewAt(0);
                    main_layout.removeViewAt(0);
                    main_layout.removeView(imagefactor);
                    int count = data.getClipData().getItemCount();
                    main_layout.addView(imagefactor,0);

                    for(int i = 0; i < count; i++) {

                        ImageView imageView2 = new ImageView(getApplicationContext());
                        imageView2.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

                        list_imageUri.add(data.getClipData().getItemAt(i).getUri());
                        imageView2.setImageURI(list_imageUri.get(i));
                        main_layout.addView(imageView2,0);
                    }

                    bitmap_signature=loadBitmapFromView(main_layout);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap_signature.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    bitmap_signature_base= Base64.encodeToString(byteArray, Base64.DEFAULT);
                    dbh.Insert_signature(BarcodeScan,bitmap_signature_base);

                    Button button=  new Button(getApplicationContext());
                    button.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                    button.setBackgroundResource(R.color.green_900);
                    button.setText("تایید و ارسال");
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                    button.setTextColor(getColor(R.color.white));
                    button.setPadding(0, 10, 0, 10);
                    button.setOnClickListener(v -> action.sendfactor(BarcodeScan,bitmap_signature_base));
                    main_layout.addView(button,0);

                } else {
                    main_layout.removeViewAt(0);
                    main_layout.removeViewAt(0);
                    main_layout.removeView(imagefactor);

                    list_imageUri.add(data.getData());
                    ImageView imageView1 = new ImageView(getApplicationContext());
                    imageView1.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

                    imageView1.setImageURI(list_imageUri.get(0));
                    main_layout.addView(imagefactor,0);

                    main_layout.addView(imageView1,0);

                    bitmap_signature=loadBitmapFromView(main_layout);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap_signature.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    bitmap_signature_base= Base64.encodeToString(byteArray, Base64.DEFAULT);
                    dbh.Insert_signature(BarcodeScan,bitmap_signature_base);

                    Button button=  new Button(getApplicationContext());
                    button.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                    button.setBackgroundResource(R.color.green_900);
                    button.setText("تایید و ارسال");
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                    button.setTextColor(getColor(R.color.white));
                    button.setPadding(0, 10, 0, 10);
                    button.setOnClickListener(v -> action.sendfactor(BarcodeScan,bitmap_signature_base));
                    main_layout.addView(button,0);

                }
            } else {
                callMethod.showToast("فایلی انتخاب نشد");
            }
        }

        if(requestCode == 2 ){

                main_layout.removeViewAt(0);
                main_layout.removeViewAt(0);

                ImageView imageView1 = new ImageView(getApplicationContext());
                imageView1.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                imageView1.setImageURI(photoURI);
                File file =  new File(ImageOcrPath);
                file.delete();

                main_layout.addView(imageView1,0);

                bitmap_signature=loadBitmapFromView(main_layout);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap_signature.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                bitmap_signature_base= Base64.encodeToString(byteArray, Base64.DEFAULT);
            dbh.Insert_signature(BarcodeScan,bitmap_signature_base);

            Button button=  new Button(this);
                button.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                button.setBackgroundResource(R.color.green_900);
                button.setText("تایید و ارسال");
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                button.setTextColor(getColor(R.color.white));
                button.setPadding(0, 10, 0, 10);
                button.setOnClickListener(v -> action.sendfactor(BarcodeScan,bitmap_signature_base));
                main_layout.addView(button,0);


        }
    }
    public Bitmap loadBitmapFromView(View v) {
        v.measure(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.draw(c);
        return b;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("intent_exception", ex.getMessage());
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, "com.kits.ocrkowsar.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 2);
            }
        }
    }
    private File createImageFile() throws IOException {

        File folder = new File(strSDCardPathName);
        try
        {
            if (!folder.exists()) {
                folder.mkdir();
            }
        }catch(Exception ignored){}

        String imageFileName = "ocr";
        File storageDir = new File(strSDCardPathName);
        File image = File.createTempFile(imageFileName, ".jpg",storageDir);
        ImageOcrPath=image.getAbsolutePath();
        return image;
    }





}