package com.zyzxsp.utils;

import android.text.TextUtils;

/**
 * Created by Administrator on 2019/05/14.
 */

public class StringUtils {
    private static final String TAG = "StringUtils";

    public static String addSpace(String str, int... locations) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        str = str.replace(" ", "");
        int length = str.length();
        int startIndex = 0;
        int endIndex = 0;
        String result = "";
        for (int location : locations) {
            if (length > location) {
                endIndex = location;
                result = result + str.substring(startIndex, endIndex) + " ";
                startIndex = endIndex;
            }
        }
        result = result + str.substring(endIndex, length) + " ";
        return result.substring(0, result.length() - 1);
    }
}
