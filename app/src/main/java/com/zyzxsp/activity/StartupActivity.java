package com.zyzxsp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.zyzxsp.R;
import com.zyzxsp.utils.ZLog;

public class StartupActivity extends BaseActivity {
    public static final String TAG = "StartupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                            Thread.sleep(1500);
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
