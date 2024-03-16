package com.maxi.corejj;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

public class CToast {
    private CToastBuilder mBuilder;

    private CToast(CToastBuilder b) {
        mBuilder = b;
    }

    public static CToastBuilder with(Context context) {
        return new CToastBuilder(context);
    }

    public void show() {
        Toast toast = Toast.makeText(mBuilder.mContext, "", Toast.LENGTH_SHORT);
        toast.setDuration(Toast.LENGTH_SHORT);
        if (!TextUtils.isEmpty(mBuilder.mText)) {
            toast.setText(mBuilder.mText);
        }
        if (mBuilder.mView != null) {
            toast.setView(mBuilder.mView);
        }
        if (mBuilder.mGravity != 0) {
            toast.setGravity(mBuilder.mGravity, 0, 0);//设置偏移量
        }
        toast.show();
    }


    public static final class CToastBuilder {
        Context mContext;
        String mText;
        View mView;
        int mGravity = 0;

        private CToastBuilder(Context context) {
            mContext = context;
        }

        public CToastBuilder text(String text) {
            mText = text;
            return this;
        }

        public CToastBuilder view(View view) {
            mView = view;
            return this;
        }

        public CToastBuilder gravity(int gravity) {
            mGravity = gravity;
            return this;
        }

        public void show() {
            new CToast(this).show();
        }
    }
}
