package com.maxi.corejj.view.dialog;

import android.app.Dialog;

public class Canceler {
    private Dialog mDialog;

    public void cancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void setDialog(Dialog dialog) {
        mDialog = dialog;
    }
}
