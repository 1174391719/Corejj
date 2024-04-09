package com.maxi.corejj.infrastucture.utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class PasswordUtil {
    public static String TAG = "PasswordUtil";

    public static String encryptText(final String alias, final String textToEncrypt) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));

            byte[] iv = cipher.doFinal(textToEncrypt.getBytes("UTF-8"));
            return Base64.encodeToString(iv, Base64.DEFAULT);
        } catch (Exception e) {

        }
        return null;
    }


    public String decryptData(final String alias, final byte[] encryptedData, final byte[] encryptionIv) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec);
            return new String(cipher.doFinal(encryptedData), "UTF-8");
        } catch (Exception e) {

        }
        return null;
    }

    private static SecretKey getSecretKey(final String alias) {
        try {
            final KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());
            return keyGenerator.generateKey();

        } catch (Exception e) {

        }
        return null;
    }
}
