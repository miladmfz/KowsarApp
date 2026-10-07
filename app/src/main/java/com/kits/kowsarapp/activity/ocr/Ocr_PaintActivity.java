package com.kits.kowsarapp.activity.ocr;

import android.annotation.SuppressLint;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.kits.kowsarapp.application.ocr.OcrImagePipeline;
import com.kits.kowsarapp.application.ocr.OcrImagePolicy;
import com.kits.kowsarapp.application.ocr.Ocr_Action;
import com.kits.kowsarapp.adapter.ocr.Ocr_PaintView;
import com.kits.kowsarapp.application.base.CallMethod;
import com.kits.kowsarapp.model.base.NumberFunctions;
import com.kits.kowsarapp.model.ocr.Ocr_DBH;
import com.kits.kowsarapp.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ocr_PaintActivity extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 3;

    private Ocr_PaintView paintView;
    String BarcodeScan;
    String bitmap_signature_base;
    Bitmap bitmap_signature;
    Ocr_DBH ocr_dbh ;
    Ocr_Action ocr_action;
    LinearLayoutCompat main_layout;
    LinearLayoutCompat paint_layout;
    List<Uri> list_imageUri=new ArrayList<>();
    ArrayList<String> Multi_barcode = new ArrayList<>();


    Intent intent;
    String bitmap_factor_base64;
    ImageView imagefactor;
    int width=1;

    String ImageOcrPath="";
    Uri photoURI;
    File photoFile;
    Button button;
    CallMethod callMethod;

    EditText ed_signexplain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.RoyalGoldTheme));
        setContentView(R.layout.ocr_activity_paint);

        Config();
        if (!readIntent()) {
            callMethod.showToast("اطلاعات فاکتور معتبر نیست");
            finish();
            return;
        }

        Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.ocr_spinner_box);
        TextView repw = dialog1.findViewById(R.id.ocr_spinner_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        try {
            Handler handler = new Handler(getMainLooper());
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

                    bitmap_factor_base64=ocr_dbh.getimagefromfactor(s,"FactorImage");

                    Bitmap factorBitmap = OcrImagePipeline.decodeBase64(bitmap_factor_base64);
                    if (factorBitmap != null) {
                        imagefactor.setImageBitmap(factorBitmap);
                    }

                    main_layout.addView(Deliverer);
                    main_layout.addView(factorbarcode);
                    main_layout.addView(textView);
                    main_layout.addView(imageView);
                    main_layout.addView(imagefactor,0);

                    if (!renderAndStoreSignature(s)) {
                        callMethod.showToast("امکان ساخت تصویر امضا وجود ندارد");
                        main_layout.removeAllViews();
                        return;
                    }
                    main_layout.removeAllViews();
                }
                callMethod.showToast("با موفقیت ثبت گردید");
                finish();
            }else {
                bitmap_factor_base64=ocr_dbh.getimagefromfactor(BarcodeScan,"FactorImage");
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
                Bitmap factorBitmap = OcrImagePipeline.decodeBase64(bitmap_factor_base64);
                if (factorBitmap != null) {
                    imagefactor.setImageBitmap(factorBitmap);
                }

                main_layout.addView(Deliverer);
                main_layout.addView(factorbarcode);
                main_layout.addView(textView);
                main_layout.addView(imageView);
                main_layout.addView(imagefactor,0);




                if (!renderAndStoreSignature(BarcodeScan)) {
                    callMethod.showToast("امکان ساخت تصویر امضا وجود ندارد");
                    return;
                }

                Button button1 =  new Button(getApplicationContext());
                button1.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                button1.setBackgroundResource(R.color.green_900);
                button1.setText("تایید و ارسال");
                button1.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                button1.setTextColor(getColor(R.color.white));
                button1.setPadding(0, 5, 0, 5);
                button1.setOnClickListener(v -> ocr_action.sendfactor(BarcodeScan,bitmap_signature_base));
                Button btn_pic=  new Button(getApplicationContext());
                btn_pic.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                btn_pic.setBackgroundResource(R.color.green_900);
                btn_pic.setText("اضافه کردن عکس");
                btn_pic.setTextColor(getColor(R.color.white));
                btn_pic.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
                btn_pic.setOnClickListener(v -> {

                    final CharSequence[] options = { "گرفتن عکس", "انتخاب از نصویر موجود","لغو" };

                    AlertDialog.Builder builder = new AlertDialog.Builder(Ocr_PaintActivity.this);
                    builder.setTitle("Choose your profile picture");

                    builder.setItems(options, (dialog, item) -> {

                        if (options[item].equals( "گرفتن عکس")) {
                            requestCameraCapture();
                        } else if (options[item].equals("انتخاب از نصویر موجود")) {
                            intent = new Intent();
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent , REQUEST_GALLERY);

                        } else if (options[item].equals("لغو")) {
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
        ocr_dbh = new Ocr_DBH(this, callMethod.ReadString("DatabaseName"));

        ocr_action =new Ocr_Action(Ocr_PaintActivity.this);

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


    public boolean readIntent(){
        Bundle bundle =getIntent().getExtras();
        if (bundle == null) {
            return false;
        }
        BarcodeScan=bundle.getString("ScanResponse");
        bitmap_factor_base64 = bundle.getString("FactorImage");
        try {
            width = Integer.parseInt(bundle.getString("Width", "1"));
        } catch (NumberFormatException ignored) {
            width = getResources().getDisplayMetrics().widthPixels;
        }
        if (width <= 0) {
            width = getResources().getDisplayMetrics().widthPixels;
        }
        if (BarcodeScan == null || BarcodeScan.trim().isEmpty()) {
            return false;
        }
        if(BarcodeScan.equals("Multi_sign")){
            Multi_barcode = bundle.getStringArrayList( "list");
            if (Multi_barcode == null || Multi_barcode.isEmpty()) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY) {
            if (resultCode != Activity.RESULT_OK || data == null) {
                callMethod.showToast("فایلی انتخاب نشد");
                return;
            }
            ArrayList<Uri> selected = new ArrayList<>();
            if (data.getClipData() != null) {
                int count = Math.min(
                        data.getClipData().getItemCount(),
                        OcrImagePolicy.MAX_GALLERY_IMAGES
                );
                for (int i = 0; i < count; i++) {
                    selected.add(data.getClipData().getItemAt(i).getUri());
                }
                if (data.getClipData().getItemCount() > count) {
                    callMethod.showToast("حداکثر شش تصویر پردازش می‌شود");
                }
            } else if (data.getData() != null) {
                selected.add(data.getData());
            }
            applyAttachments(selected);
            return;
        }

        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK && photoURI != null) {
                ArrayList<Uri> selected = new ArrayList<>();
                selected.add(photoURI);
                applyAttachments(selected);
            } else {
                callMethod.showToast("عکسی ثبت نشد");
            }
            deletePendingCameraFile();
        }
    }

    public Bitmap loadBitmapFromView(View view) {
        return OcrImagePipeline.renderView(view);
    }

    private void applyAttachments(List<Uri> selected) {
        if (selected == null || selected.isEmpty()) {
            callMethod.showToast("تصویر معتبری انتخاب نشد");
            return;
        }
        removeActionButtons();
        if (imagefactor != null && imagefactor.getParent() == main_layout) {
            main_layout.removeView(imagefactor);
            main_layout.addView(imagefactor, 0);
        }

        int added = 0;
        list_imageUri.clear();
        for (Uri uri : selected) {
            Bitmap bitmap = OcrImagePipeline.decodeUri(getContentResolver(), uri);
            if (bitmap == null) {
                continue;
            }
            list_imageUri.add(uri);
            ImageView imageView = new ImageView(this);
            imageView.setAdjustViewBounds(true);
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    width,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            ));
            imageView.setImageBitmap(bitmap);
            main_layout.addView(imageView, 0);
            added++;
        }
        if (added == 0 || !renderAndStoreSignature(BarcodeScan)) {
            callMethod.showToast("تصویر انتخاب‌شده قابل پردازش نیست");
            return;
        }
        addSendButton();
    }

    private void removeActionButtons() {
        int removed = 0;
        for (int index = main_layout.getChildCount() - 1; index >= 0 && removed < 2; index--) {
            View child = main_layout.getChildAt(index);
            if (child instanceof Button) {
                main_layout.removeViewAt(index);
                removed++;
            }
        }
    }

    private void addSendButton() {
        Button sendButton = new Button(this);
        sendButton.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                width,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        ));
        sendButton.setBackgroundResource(R.color.green_900);
        sendButton.setText("تایید و ارسال");
        sendButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, safeTitleSize());
        sendButton.setTextColor(getColor(R.color.white));
        sendButton.setPadding(0, 10, 0, 10);
        sendButton.setOnClickListener(v -> ocr_action.sendfactor(BarcodeScan, bitmap_signature_base));
        main_layout.addView(sendButton, 0);
    }

    private boolean renderAndStoreSignature(String factorCode) {
        Bitmap rendered = OcrImagePipeline.renderView(main_layout);
        if (rendered == null) {
            return false;
        }
        String encoded = OcrImagePipeline.encodeJpeg(rendered, 10);
        rendered.recycle();
        if (!OcrImagePolicy.isEncodedPayloadAllowed(encoded)) {
            return false;
        }
        bitmap_signature = null;
        bitmap_signature_base = encoded;
        ocr_dbh.Insert_signature(factorCode, encoded);
        return true;
    }

    private int safeTitleSize() {
        try {
            return Integer.parseInt(callMethod.ReadString("TitleSize"));
        } catch (RuntimeException ignored) {
            return 18;
        }
    }

    private void requestCameraCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
            return;
        }
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CAMERA_PERMISSION) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            callMethod.showToast("بدون دسترسی دوربین می‌توانید تصویر را از گالری انتخاب کنید");
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception e) {
                callMethod.Log(e.getMessage());
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider",
                        photoFile
                );
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        } else {
            callMethod.showToast("برنامه دوربین در دسترس نیست");
        }
    }
    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            storageDir = getCacheDir();
        }
        File image = File.createTempFile("ocr_", ".jpg", storageDir);
        ImageOcrPath=image.getAbsolutePath();
        return image;
    }

    private void deletePendingCameraFile() {
        if (ImageOcrPath == null || ImageOcrPath.isEmpty()) {
            return;
        }
        File file = new File(ImageOcrPath);
        if (file.exists() && !file.delete()) {
            callMethod.Log("Unable to delete OCR camera temp file");
        }
        ImageOcrPath = "";
        photoFile = null;
        photoURI = null;
    }

    @Override
    protected void onDestroy() {
        if (ocr_action != null) {
            ocr_action.cancelActiveSubmission();
        }
        deletePendingCameraFile();
        super.onDestroy();
    }





}
