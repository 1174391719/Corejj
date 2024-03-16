package com.maxi.corejj.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.maxi.corejj.R;

public class CDialog {
    public static CDialogBuilder with(Context context) {
        return new CDialogBuilder(context);
    }

    public static void cancel(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static class CDialogBuilder {
        private Dialog mDialog;
        private Canceler mCanceler;
        private Context mContext;
        private View mContentView;
        private int mWidth;
        private int mHeight;
        private int mGravity = Gravity.CENTER;
        private DialogInterface.OnDismissListener mOnDismissListener;
        private int mX = 0;
        private int mY = 0;
        private boolean mCanceledOnTouchOutside = true;
        private boolean mCancelable = true;


        private CDialogBuilder(Context context) {
            mContext = context;
        }

        public CDialogBuilder view(View contentView) {
            mContentView = contentView;
            return this;
        }

        public CDialogBuilder canceler(Canceler canceler) {
            mCanceler = canceler;
            return this;
        }

        public CDialogBuilder onDismissListener(DialogInterface.OnDismissListener listener) {
            mOnDismissListener = listener;
            return this;
        }

        public CDialogBuilder widthAndHeight(int width, int height) {
            mWidth = width;
            mHeight = height;
            return this;
        }

        public CDialogBuilder x(int x) {
            mX = x;
            return this;
        }

        public CDialogBuilder y(int y) {
            mY = y;
            return this;
        }

        public CDialogBuilder gravity(int gravity) {
            mGravity = gravity;
            return this;
        }

        public CDialogBuilder canceledOnTouchOutside(boolean cancel) {
            mCanceledOnTouchOutside = cancel;
            return this;
        }



        public CDialogBuilder cancelable(boolean cancel) {
            mCancelable = cancel;
            return this;
        }

        public Dialog show() {
            initShow();
            return mDialog;
        }

        /***
         ***********************************************************************************************
         * */
        private void initShow() {

            mDialog = new Dialog(mContext, R.style.dialog_dark_bg);
            if (mCanceler != null) {
                mCanceler.setDialog(mDialog);
            }

            mDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
            mDialog.setCancelable(mCancelable);
            Window window = mDialog.getWindow();
            window.setGravity(mGravity);
            window.setContentView(mContentView);
            WindowManager.LayoutParams params = window.getAttributes();
            if (mWidth != 0 || mHeight != 0) {
                params.width = mWidth;
                params.height = mHeight;
            }
            params.x = mX;
            params.y = mY;
            window.setAttributes(params);

            if (mOnDismissListener != null) {
                mDialog.setOnDismissListener(mOnDismissListener);
            }
            mDialog.show();
        }
    }
}
