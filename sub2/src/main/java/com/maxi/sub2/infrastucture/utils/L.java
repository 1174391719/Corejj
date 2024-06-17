package com.maxi.sub2;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2019/05/07.
 */

public class L {
    public static final boolean offline = false;
    private static final String PREFIX = "kkucom.maxi.sub2:";
    private static final String TAG_D = "CoreJJ/D";
    private static final String TAG_I = "CoreJJ/I";
    private static final String TAG_E = "CoreJJ/E";

    public static void k(String tag, String msg) {
        Log.d(PREFIX + tag, msg);
    }

    public static void d(String msg) {
        Log.i(generateTag(TAG_D), msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void i(String msg) {
        Log.i(generateTag(TAG_I), msg);
    }

    public static void e(String msg) {
        Log.i(generateTag(TAG_E), msg);
    }

    public static void e(Exception e) {
        e.fillInStackTrace();
        Log.e(generateTag(TAG_E), "here:", e);
    }

    public static void p() {
        RuntimeException exception = new RuntimeException("kk");
        exception.fillInStackTrace();
        Log.e(TAG_E, "here:", exception);
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
