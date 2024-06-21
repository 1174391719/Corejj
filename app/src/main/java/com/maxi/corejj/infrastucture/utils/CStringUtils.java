package com.maxi.corejj.utils;

import android.text.TextUtils;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CStringUtils {
    public static String toPureString(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        str = str.replace(" ", "");
        return replaceBlank(str);
    }

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

    public static boolean equals(String str1, String str2) {
        try {
            return str1.equals(str2);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * MD5加密
     *
     * @param byteStr 需要加密的内容
     * @return 返回 byteStr的md5值
     */
    public static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
//            return Base64.encodeToString(byteArray,Base64.NO_WRAP);
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    public static void setPhoneStyleString(CharSequence s, int start, int before, EditText editText) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }
        if (!sb.toString().equals(s.toString())) {
            int index = start + 1;
            if (sb.charAt(start) == ' ') {
                if (before == 0) {
                    index++;
                } else {
                    index--;
                }
            } else {
                if (before == 1) {
                    index--;
                }
            }
            editText.setText(sb.toString());
            editText.setSelection(index);
        }
    }

}
