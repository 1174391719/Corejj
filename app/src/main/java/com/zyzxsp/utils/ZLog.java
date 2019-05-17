package com.zyzxsp.utils;


import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2019/05/07.
 */

public class ZLog {
    public static final boolean use_config_data = false;
    private static final String TAG_D = "ZyMeeting/D";
    private static final String TAG_I = "ZyMeeting/I";
    private static final String TAG_E = "ZyMeeting/E";

    public static void d(String msg) {
        Log.i(generateTag(TAG_D), msg);
    }

    public static void i(String msg) {
        Log.i(generateTag(TAG_I), msg);
    }

    public static void e(String msg) {
        Log.i(generateTag(TAG_E), msg);
    }

    private static String generateTag(String prefixTag) {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(prefixTag) ? tag : prefixTag + ":" + tag;
        return tag;
    }
}
