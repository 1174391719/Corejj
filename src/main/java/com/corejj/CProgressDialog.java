package com.corejj;

import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

public class CProgressDialog {
    private static CustomDialog mCustomDialog;
    private static int mQuickProtectionDuration = 10;
    private static boolean mForbidDismiss = true;

    public static void show(Context context, String tips) {
        mForbidDismiss = true;
        if (mCustomDialog != null) {
            mCustomDialog.dismiss();
        }
        mCustomDialog = new CustomDialog(context, tips);
        mCustomDialog.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mForbidDismiss = false;
            }
        }, mQuickProtectionDuration);

    }

    public static void cancel() {
        if (mForbidDismiss) {
            mForbidDismiss = false;
            return;
        }
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        mCustomDialog = null;
    }
}
