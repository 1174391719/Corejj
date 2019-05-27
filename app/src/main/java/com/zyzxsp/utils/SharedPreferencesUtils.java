package com.zyzxsp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2019/05/27.
 */

public class SharedPreferencesUtils {
    public static void putString(Context context, String key, String value) {
        SharedPreferences myPreference = context.getSharedPreferences("zyzx", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences myPreference = context.getSharedPreferences("zyzx", Context.MODE_PRIVATE);
        return myPreference.getString(key, "");
    }
}
