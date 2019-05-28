package com.juphoon.lemon.ui;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;

public class MtcUtils {

    public static final String MTC_DATA_DIRECTORY = "mtc_data_directory";
    public static final String MTC_DATA_DIRECTORY_KEY = "mtc_data_directory_key";

    public static final String MTC_DATA_DIRECTORY_EXTERNAL = "external";
    public static final String MTC_DATA_DIRECTORY_DATA = "data";

    @SuppressLint("NewApi") public static String getDataDir(Context context) {
        String dir = null;
        SharedPreferences sp = context.getSharedPreferences(MTC_DATA_DIRECTORY, Context.MODE_PRIVATE);
        String fstDir = sp.getString(MTC_DATA_DIRECTORY_KEY, "");
        if (fstDir.equals(MTC_DATA_DIRECTORY_EXTERNAL)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else if (fstDir.equals(MTC_DATA_DIRECTORY_DATA)) {
            return context.getFilesDir().getAbsolutePath();
        } else {
            String state = Environment.getExternalStorageState();
            boolean emulated = true;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                emulated = Environment.isExternalStorageEmulated();
            }
            if (Environment.MEDIA_MOUNTED.equals(state) && (emulated || !Environment.isExternalStorageRemovable())) {
                dir = Environment.getExternalStorageDirectory().getAbsolutePath();
                sp.edit().putString(MTC_DATA_DIRECTORY_KEY, MTC_DATA_DIRECTORY_EXTERNAL).commit();
            } else {
                dir = context.getFilesDir().getAbsolutePath();
                sp.edit().putString(MTC_DATA_DIRECTORY_KEY, MTC_DATA_DIRECTORY_DATA).commit();
            }
        }
        return dir;
    }
    
    public static String getAppVersion(Context c) {
        PackageManager pm = c.getPackageManager();
        String versionName = null;
        try {
            PackageInfo pi = pm.getPackageInfo(c.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (NameNotFoundException e) {
            // TODO get version error
            e.printStackTrace();
        }
        if (versionName == null) {
            versionName = "";
        }
        return versionName;
    }
    
    public static void saveAssetFile(Context context, String sourceName, String desFilePath) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(sourceName);
            out = new FileOutputStream(desFilePath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setActivityFullScreen(Activity activity, boolean fullScreen) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();
        if (fullScreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(attrs);
        } else {
            attrs.flags &= ~(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attrs);
        }
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        String packageName = context.getPackageName();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

}
