package com.kits.kowsarapp.application.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ImageInfo {

    private static final String KOWSAR_DIRECTORY = "Kowsar";
    private static final String FACTOR_IMAGE_DIRECTORY = "factorimage";
    private static final String LOGO_FILE_NAME = "Logo.jpg";

    private final CallMethod callMethod;
    private final File storageRoot;

    public ImageInfo(Context context) {
        this(context, Environment.getExternalStorageDirectory());
    }

    ImageInfo(Context context, File storageRoot) {
        callMethod = new CallMethod(context);
        this.storageRoot = storageRoot;
    }

    public void SaveImage(Bitmap finalBitmap, String code) {
        saveJpeg(finalBitmap, companyDirectory(), imageFileName(code), "SaveImage");
    }

    public void SaveImage_factor(Bitmap finalBitmap, String code) {
        saveJpeg(finalBitmap, factorImageDirectory(), imageFileName(code), "SaveImage_factor");
    }

    public void DeleteImage(String code) {
        File file = safeChild(companyDirectory(), imageFileName(code));
        if (file == null || !file.exists()) return;
        try {
            if (!file.isFile() || !file.delete()) {
                callMethod.Log("ImageInfo DeleteImage did not remove the target");
            }
        } catch (SecurityException exception) {
            logFailure("DeleteImage", exception);
        }
    }

    public Boolean Image_exist(String code) {
        File file = safeChild(companyDirectory(), imageFileName(code));
        return file != null && file.isFile();
    }

    public void SaveLogo(Bitmap finalBitmap) {
        saveJpeg(finalBitmap, companyDirectory(), LOGO_FILE_NAME, "SaveLogo");
    }

    public Bitmap LoadLogo() {
        File imageFile = safeChild(companyDirectory(), LOGO_FILE_NAME);
        if (imageFile == null || !imageFile.isFile()) return null;
        try {
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        } catch (SecurityException | IllegalArgumentException exception) {
            logFailure("LoadLogo", exception);
            return null;
        } catch (OutOfMemoryError error) {
            callMethod.Log("ImageInfo LoadLogo failed: OutOfMemoryError");
            return null;
        }
    }

    private File companyDirectory() {
        String companyName = callMethod.ReadString("EnglishCompanyNameUse");
        return safeChild(new File(storageRoot, KOWSAR_DIRECTORY), companyName);
    }

    private File factorImageDirectory() {
        return new File(new File(storageRoot, KOWSAR_DIRECTORY), FACTOR_IMAGE_DIRECTORY);
    }

    private String imageFileName(String code) {
        return isSimpleName(code) ? code + ".jpg" : null;
    }

    private void saveJpeg(Bitmap bitmap, File directory, String fileName, String operation) {
        if (bitmap == null || bitmap.isRecycled()) {
            callMethod.Log("ImageInfo " + operation + " ignored an unavailable bitmap");
            return;
        }
        File target = safeChild(directory, fileName);
        if (target == null || !ensureDirectory(directory, operation)) return;

        File temporary = null;
        try {
            temporary = File.createTempFile(fileName + ".", ".tmp", directory);
            try (FileOutputStream output = new FileOutputStream(temporary, false)) {
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)) {
                    throw new IOException("Bitmap compression returned false");
                }
                output.flush();
            }
            replaceFile(temporary, target);
            temporary = null;
        } catch (IOException | SecurityException | IllegalStateException exception) {
            logFailure(operation, exception);
        } catch (OutOfMemoryError error) {
            callMethod.Log("ImageInfo " + operation + " failed: OutOfMemoryError");
        } finally {
            if (temporary != null && temporary.exists() && !temporary.delete()) {
                callMethod.Log("ImageInfo " + operation + " could not remove its temporary file");
            }
        }
    }

    private boolean ensureDirectory(File directory, String operation) {
        if (directory == null) {
            callMethod.Log("ImageInfo " + operation + " rejected an invalid directory");
            return false;
        }
        try {
            if (directory.isDirectory()) return true;
            if (directory.exists() || !directory.mkdirs()) {
                callMethod.Log("ImageInfo " + operation + " could not create its directory");
                return false;
            }
            return true;
        } catch (SecurityException exception) {
            logFailure(operation, exception);
            return false;
        }
    }

    private void replaceFile(File temporary, File target) throws IOException {
        try {
            Files.move(
                    temporary.toPath(),
                    target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    temporary.toPath(),
                    target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private File safeChild(File directory, String name) {
        if (directory == null || !isSimpleName(name)) return null;
        try {
            File canonicalDirectory = directory.getCanonicalFile();
            File candidate = new File(canonicalDirectory, name).getCanonicalFile();
            String directoryPrefix = canonicalDirectory.getPath() + File.separator;
            return candidate.getPath().startsWith(directoryPrefix) ? candidate : null;
        } catch (IOException | SecurityException exception) {
            logFailure("path validation", exception);
            return null;
        }
    }

    private boolean isSimpleName(String value) {
        return value != null
                && !value.trim().isEmpty()
                && !".".equals(value)
                && !"..".equals(value)
                && value.indexOf('/') < 0
                && value.indexOf('\\') < 0;
    }

    private void logFailure(String operation, Throwable throwable) {
        callMethod.Log("ImageInfo " + operation + " failed: "
                + throwable.getClass().getSimpleName());
    }
}
