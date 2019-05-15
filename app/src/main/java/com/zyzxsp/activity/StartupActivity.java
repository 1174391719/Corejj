package com.zyzxsp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.zyzxsp.R;
import com.zyzxsp.utils.ZLog;

public class StartupActivity extends BaseActivity {
    public static final String TAG = "StartupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_startup);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
