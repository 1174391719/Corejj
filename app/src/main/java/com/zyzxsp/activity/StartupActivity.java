package com.zyzxsp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.zyzxsp.R;
import com.zyzxsp.presenter.MainPresenterImpl;
import com.zyzxsp.utils.StatusBarUtils;
import com.zyzxsp.utils.ZLog;

public class StartupActivity extends BaseActivity {
    public static final String TAG = "StartupActivity";
    private boolean mStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZLog.i("Init...");
        StatusBarUtils.setTransparent(this);
        setContentView(R.layout.activity_startup);
        MainPresenterImpl.getInstants();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!mStart) {
            return;
        }
        mStart = false;
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }
                        Intent intent = new Intent(StartupActivity.this, ZyLoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }.start();
    }
}
