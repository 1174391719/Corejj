package com.corejj.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.corejj.R;

public class CFullScreenDialog {
    private static Dialog sDialog;

    public static void show(Context context, View contentView) {
        if (sDialog != null) {
            sDialog.dismiss();
            sDialog = null;
        }
        showDialog(context, contentView, null);
    }

    public static void show(Context context, View contentView, DialogInterface.OnDismissListener listener) {
        if (sDialog != null) {
            sDialog.dismiss();
            sDialog = null;
        }
        showDialog(context, contentView, listener);
    }

    public static void cancel() {
        if (sDialog != null && sDialog.isShowing()) {
            sDialog.dismiss();
        }
        sDialog = null;
    }

    private static void showDialog(Context context, View contentView, DialogInterface.OnDismissListener listener) {
        sDialog = new Dialog(context, R.style.dialog_bottom_full);
        sDialog.setCanceledOnTouchOutside(true);
        sDialog.setCancelable(true);
        Window window = sDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);

        window.setContentView(contentView);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        if (listener != null) {
            sDialog.setOnDismissListener(listener);
        }
        sDialog.show();
    }
}
