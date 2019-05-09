package com.zyzxsp.utils;


import android.util.Log;

/**
 * Created by Administrator on 2019/05/07.
 */

public class ZLog {
    public static final boolean debug = false;
    private static final String TAG = "ZyMeeting:";

    public static void d(String tag, String msg) {
        Log.i(TAG + tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(TAG + tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG + tag, msg);
    }
}
