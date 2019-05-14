package com.zyzxsp.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Outline;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.view.ViewOutlineProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2019/05/07.
 */

public class Utils {
    private static final String TAG = "Utils";

    public static String encryptionByAES(String str) {
        String key = "abcdef0123456789";
        if (key == null) {
            return null;
        }
        if (key.length() != 16) {
            return null;
        }
        try {
            byte[] raw = key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(str.getBytes("utf-8"));
            return Base64.encodeToString(encrypted, 0);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception e) {
        }
        return null;
    }

    public static String removeInvalidChar(String str) {
        String destination = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            destination = m.replaceAll("");
        }
        return destination;
    }
}
