package com.zyzxsp.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by chenshuliang on 2018/5/31.
 */

public class AlertUtil {
    private static Toast mToast;
    private static WeakReference<Context> mContext = null;

    public static void init(Context context) {
        if (mContext == null || mContext.get() == null) {
            mContext = new WeakReference<Context>(context);
        }
    }

    public synchronized static void toastText(int textId) {
        if (mToast == null && mContext.get() != null) {
            mToast = Toast.makeText(mContext.get(), "", Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(textId);
        mToast.show();
    }

    public synchronized static void toastText(String textString) {
        if (mToast == null && mContext.get() != null) {
            mToast = Toast.makeText(mContext.get(), "", Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(textString);
        mToast.show();
    }

//    public synchronized static void toastText(int textId, int milliseconds) {
//        if (mToast == null && mContext.get() != null) {
//            mToast = Toast.makeText(mContext.get(), "", Toast.LENGTH_SHORT);
//            mToast.setGravity(Gravity.CENTER, 0, 0);
//        }
//        if (milliseconds == CALL_TOAST_DISPLAY_TIME) {
//            mToast.setDuration(Toast.LENGTH_LONG);
//        } else {
//            mToast.setDuration(Toast.LENGTH_SHORT);
//        }
//        mToast.setText(textId);
//        mToast.show();
//    }
//
//    public synchronized static void toastText(String textString, int milliseconds) {
//        if (mToast == null && mContext.get() != null) {
//            mToast = Toast.makeText(mContext.get(), "", Toast.LENGTH_SHORT);
//            mToast.setGravity(Gravity.CENTER, 0, 0);
//        }
//        if (milliseconds == CALL_TOAST_DISPLAY_TIME) {
//            mToast.setDuration(Toast.LENGTH_LONG);
//        } else {
//            mToast.setDuration(Toast.LENGTH_SHORT);
//        }
//        mToast.setText(textString);
//        mToast.show();
//    }
}
