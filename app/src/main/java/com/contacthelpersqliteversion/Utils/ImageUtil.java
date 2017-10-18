package com.contacthelpersqliteversion.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import com.contacthelpersqliteversion.ContactHelperApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


public class ImageUtil {

    public static final String FILE_NAME = "ContactImage";
    public static final int MAX_IMAGE_SIZE = 1024;

    public static final File imagesPath = new File(ContactHelperApp.getAppPath(), "images");
    public static final File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    //REQUEST CODES
    public static final int REQUEST_IMAGE_GALLERY = 1042;
    public static final int REQUEST_IMAGE_CAMERA = 4242;

    public static void clearTempImages() {
        try {
            File file = imagesPath;
            imagesPath.mkdirs();
            String[] entries = file.list();
            if (entries != null) {
                for (String s : entries) {
                    File currentFile = new File(file.getPath(), s);
                    currentFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap scaleImage(File file, int width, int height) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.max(photoW / width, photoH / height);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = 0;
        if (exif != null)
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        return rotateBitmap(bitmap, orientation);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveImageToDisk(Bitmap results, String fileName) {
        try {

            String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());

            File file = new File(directory + "/" + FILE_NAME + imageName +".jpg");
            //File file = new File(imagesPath + fileName); //было

            Uri uriFile = Uri.fromFile(file);
            uriFile.getPath();
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

            file.deleteOnExit();
            file.createNewFile();
            file.setWritable(true, false);
            file.setReadable(true, false);
            FileOutputStream outStream;
            outStream = new FileOutputStream(file);
            results.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String onCameraStart(BaseToolbarActivity context) {
        clearTempImages();
        String mCurrentPhotoPath = null;
        File imageFile = null;
        try {
            imageFile = File.createTempFile("temp", ".jpg", imagesPath);
            //imageFile = File.createTempFile(FILE_NAME,".jpg",directory);
            imageFile.deleteOnExit();
            imageFile.createNewFile();
            imageFile.setWritable(true, false);
            imageFile.setReadable(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageFile != null) {
            mCurrentPhotoPath = "file:" + imageFile.getAbsolutePath();
            Uri mCapturedImageURI = FileProvider.getUriForFile(context,
                    context
                            .getPackageName() + ".provider",
                    imageFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, mCapturedImageURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            context.startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
        }
        return mCurrentPhotoPath;
    }

    public static void onGalleryOpen(Fragment fragment) {
        clearTempImages();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    public static String getPath(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }
}