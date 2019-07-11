package com.corejj.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CStringUtils {
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Matcher m = Pattern.compile("\\s*|\t|\r|\n").matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static boolean isContainNumber(String str) {
        Pattern p = Pattern.compile("[0-9]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isContainStr(String str) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(str);
        return m.matches();
    }

    public static boolean containUpperCase(String str) {
        if (!isContainStr(str)) {
            return false;
        }
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            Character.isUpperCase(ch);
            return true;
        }
        return false;
    }

    public static boolean containLowerCase(String str) {
        if (!isContainStr(str)) {
            return false;
        }
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (!Character.isUpperCase(ch)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() != 11 || !isInteger(phone)) {
            return false;
        }
        return true;
    }
}
