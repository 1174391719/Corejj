package com.maxi.corejj.infrastucture.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.maxi.corejj.MyApplication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class FilePathUtils {
    private static final String TAG = "FilePathUtils";

    /**
     * response: /data/user/0/com.maxi.corejj
     */
    public static File internalRoot() {
        Context context = MyApplication.instance().getApplicationContext();
        return context.getDataDir();
    }

    /**
     * response: /data/user/0/com.maxi.corejj/cache
     */
    public static File internalFile() {
        Context context = MyApplication.instance().getApplicationContext();
        return context.getFilesDir();
    }

    /**
     * response: /data/user/0/com.maxi.corejj/cache
     */
    public static File internalCache() {
        Context context = MyApplication.instance().getApplicationContext();
        return context.getCacheDir();
    }


    /**
     * Params:type – The type of files directory to return. May be null for the root of the files
     * directory or one of the following constants for a subdirectory: Environment.DIRECTORY_MUSIC,
     * Environment.DIRECTORY_PODCASTS, Environment.DIRECTORY_RINGTONES, Environment.DIRECTORY_ALARMS,
     * Environment.DIRECTORY_NOTIFICATIONS, Environment.DIRECTORY_PICTURES, or Environment.DIRECTORY_MOVIES.
     * <p>
     * <p>
     * response: /storage/emulated/0/Android/data/com.maxi.corejj/files
     */
    public static File externalPrivateFile(String type) {
        Context context = MyApplication.instance().getApplicationContext();
        return context.getExternalFilesDir(type);
    }

    /**
     * response: /storage/emulated/0/Android/data/com.maxi.corejj/cache
     */
    public static File externalPrivateCache() {
        Context context = MyApplication.instance().getApplicationContext();
        return context.getExternalCacheDir();
    }

    /**
     * response: /storage/emulated/0
     */
    public static File externalPublicRoot() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * response: /system
     */
    public static File systemRoot() {
        return Environment.getRootDirectory();
    }

    /**
     * response: /data
     */
    public static File systemData() {
        return Environment.getDataDirectory();
    }
}
