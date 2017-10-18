package com.contacthelpersqliteversion;

import android.app.Application;
import android.content.Context;


public class ContactHelperApp extends Application {

    private static Context context;
    private static String AppPath;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AppPath = context.getFilesDir().getAbsolutePath() + "/";
    }

    public static Context getContext() {
        return context;
    }

    public static String getAppPath() {
        return AppPath;
    }

}
