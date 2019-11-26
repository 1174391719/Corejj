package com.corejj;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.corejj.callback.NetworkCallback;
import com.corejj.utils.ZLog;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
