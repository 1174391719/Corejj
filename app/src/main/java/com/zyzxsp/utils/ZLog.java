package com.zyzxsp.utils;


import android.util.Log;

/**
 * Created by Administrator on 2019/05/07.
 */

public class ZLog {
    public static final boolean use_config_data = true;
    private static final String TAG_D = "ZyMeeting/D:";
    private static final String TAG_I = "ZyMeeting/I:";
    private static final String TAG_E = "ZyMeeting/E:";

    public static void d(String tag, String msg) {
        Log.i(TAG_D + tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(TAG_I + tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG_E + tag, msg);
    }
}
