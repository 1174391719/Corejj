package com.zyzxsp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 本地数据保存
 */
public class SharePerferenceUtils {
    private static final String shareName = "zyzxData";
    //首页apk更新提示标志
    public static final String HOME_UPDATE_APK_NOTICED_FLAG = "home_update_apk_noticed_flag";
    private SharePerferenceUtils mInstace;

    private SharePerferenceUtils() {

    }

    public SharePerferenceUtils getInstace() {
        if (mInstace == null) {
            synchronized (SharePerferenceUtils.class) {
                if (mInstace == null) {
                    mInstace = new SharePerferenceUtils();
                }
            }
        }
        return mInstace;
    }

    /**
     * 保存字符串数据
     * @param context
     * @param key
     * @param data
     */
    public void saveStringData(Context context,String key,String data){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(shareName,Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(key,data);
        mEditor.commit();
    }

    /**
     * 获取字符串数据
     * @param context
     * @param key
     * @return
     */
    public String getStringData(Context context,String key){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(shareName,Context.MODE_PRIVATE);
        String value = mSharedPreferences.getString(key,"");
        return value;
    }

    /**
     * 保存boolean数据
     * @param context
     * @param key
     * @param data
     */
    public void saveBooleanData(Context context,String key,boolean data){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(shareName,Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key,data);
        mEditor.commit();
    }

    /**
     * 获取boolean数据
     * @param context
     * @param key
     * @return
     */
    public boolean getBooleanData(Context context,String key){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(shareName,Context.MODE_PRIVATE);
        boolean value = mSharedPreferences.getBoolean(key,false);
        return value;
    }

}
