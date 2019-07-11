package com.corejj.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.corejj.CustomDialog;
import com.corejj.R;

import java.util.Timer;
import java.util.TimerTask;

public class CDialog {
    private static Dialog sDialog;

    public static void show(Context context, View contentView) {
        if (sDialog != null) {
            sDialog.dismiss();
            sDialog = null;
        }
        sDialog = new Dialog(context, R.style.dialog_dark_bg);

        sDialog.setCanceledOnTouchOutside(true);
        sDialog.setCancelable(true);
        Window window = sDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setContentView(contentView);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        sDialog.show();

    }

    public static void cancel() {
        if (sDialog != null && sDialog.isShowing()) {
            sDialog.dismiss();
        }
        sDialog = null;
    }
}
