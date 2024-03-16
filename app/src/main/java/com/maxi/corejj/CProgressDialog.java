package com.maxi.corejj;

import android.content.Context;

public class CProgressDialog {
    private static CustomDialog mCustomDialog;

    public static void show(Context context, String tips) {
        if (mCustomDialog != null) {
            mCustomDialog.dismiss();
        }
        mCustomDialog = new CustomDialog(context, tips);
        mCustomDialog.show();
    }

    public static void cancel() {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        mCustomDialog = null;
    }
}
