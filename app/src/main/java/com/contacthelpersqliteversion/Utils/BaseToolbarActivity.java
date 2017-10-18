package com.contacthelpersqliteversion.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;


public class BaseToolbarActivity extends AppCompatActivity {

    public static final int PERMISSION_PHONE_REQUEST_CODE = 10;
    public static final int PERMISSION_CAMERA_REQUEST_CODE = 21;
    public static final int PERMISSION_GALLERY_REQUEST_CODE = 22;
    public static final int PERMISSIONS_AWARE_REQUEST_CODE = 23;

    @TargetApi(Build.VERSION_CODES.M)
    public void requestMultiplePermissions(int request) {
        int hasPermission = 0;
        String[] permission = null;
        switch (request) {
            case PERMISSION_CAMERA_REQUEST_CODE: {
                hasPermission = checkSelfPermission(Manifest.permission.CAMERA);
                permission = new String[]{Manifest.permission.CAMERA};
            }
            break;

            case PERMISSION_GALLERY_REQUEST_CODE: {
                hasPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            }
            break;

            case PERMISSION_PHONE_REQUEST_CODE: {
                hasPermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                permission = new String[]{Manifest.permission.READ_PHONE_STATE};
            }
            break;

            case PERMISSIONS_AWARE_REQUEST_CODE: {
                hasPermission = -1;
                permission = new String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION};
            }
            break;
        }

        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permission, request);
        } else {
            onRequestAccessPermissionResult(request);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String toastText = null;
        boolean grantResult = false;
        if (grantResults.length == 1) {
            grantResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            switch (requestCode) {
                case PERMISSION_CAMERA_REQUEST_CODE:
//                    toastText = "Sorry, camera will not available";
//                    requestIsAccessCamera = grantResult;
                    break;
                case PERMISSION_GALLERY_REQUEST_CODE:
//                    toastText = "Sorry, gallery will not available";
//                    requestIsAccessGallery = grantResult;
                    break;
                default:
                    return;
            }
        } else {
            for (int res : grantResults) {
                if (res == PackageManager.PERMISSION_GRANTED) {
                    grantResult = true;
                } else {
                    grantResult = false;
//                    toastText = "Sorry but not at all permission have been granted";
                    break;
                }
            }
        }
        if (grantResult) {
            onRequestAccessPermissionResult(requestCode);
        } else {
            // Permission Denied.
            onRequestDeclinePermissionResult(requestCode);
            if (!TextUtils.isEmpty(toastText)) {
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onRequestDeclinePermissionResult(int requestCode) {
    }

    protected void onRequestAccessPermissionResult(int requestCode) {
    }

}
