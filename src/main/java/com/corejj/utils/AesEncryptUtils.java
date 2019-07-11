package com.corejj.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author crr
 * * 前后端数据传输加密工具类
 */
public class AesEncryptUtils {
    /**
     * 可配置到Constant中，并读取配置文件注入
     */
    private static final String KEY = "abcdef0123456789";

    /**
     * 参数分别代表 算法名称/加密模式/数据填充方式
     */
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    private AesEncryptUtils() {
    }

    /**
     * 加密
     *
     * @param content    加密的字符串
     * @param encryptKey key值
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String encryptKey) throws Exception {
        //1.构造密钥生成器，指定为AES算法,不区分大小写
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        //2.生成一个128位的随机源
        kgen.init(128);
        //根据指定算法AES自成密码器
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
        //根据密码器的初始化方式--加密：将数据加密
        byte[] b = cipher.doFinal(content.getBytes("utf-8"));
        //将加密后的数据转换为字符串
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    /**
     * 解密
     *
     * @param encryptStr 解密的字符串
     * @param decryptKey 解密的key值
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptStr, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] encryptBytes = Base64.decode(encryptStr, 0);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    public static String encrypt(String content) throws Exception {
        return encrypt(content, KEY);
    }

    public static String decrypt(String encryptStr) {
        try {
            return decrypt(encryptStr, KEY);
        } catch (Exception e) {

        }
        return null;
    }

    public static void main(String[] args) {
        try {
            System.out.println(encrypt("crrcrr03"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
 
