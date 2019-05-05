package com.zyzxsp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.log.L;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ainemo.sdk.otf.ConnectNemoCallback;
import com.ainemo.sdk.otf.LoginResponseData;
import com.ainemo.sdk.otf.NemoSDK;
import com.zyzxsp.R;

/**
 * Created by chenshuliang on 2018/4/6.
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    private String TAG = "XYLink_LoginActivity";
    private NemoSDK nemoSDK = NemoSDK.getInstance();
    private EditText displayName;
    private EditText externalId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sign).setOnClickListener(this);
        findViewById(R.id.connect_nemo).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        displayName = (EditText) findViewById(R.id.display_name);
        displayName.setHint("请输入手机号/邮箱");
        externalId = (EditText) findViewById(R.id.register_external_id);
        externalId.setHint("请输入密码");
        checkPermission();

    }

    private void checkPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                !(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 0);
        } else if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        } else if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        } else if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 0);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.connect_nemo:
                L.e(TAG, "调用登陆");
                if (displayName.getText().toString().length() == 0 || externalId.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "账号或密码为空，请输入账号或密码", Toast.LENGTH_SHORT).show();
                    break;
                }
                nemoSDK.loginXYlinkAccount(displayName.getText().toString(), externalId.getText().toString(), new ConnectNemoCallback() {
                    @Override
                    public void onFailed(final int i) {
                        L.e(TAG, "使用小鱼账号登录失败，错误码：" + i);
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {

                        }

                    }

                    @Override
                    public void onSuccess(LoginResponseData data, boolean isDetectingNetworkTopology) {
                        L.i(TAG, "匿名登录成功，号码为：" + data.getCallNumber());
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "小鱼账号登录成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                        }

                        Intent intent = new Intent(LoginActivity.this, CallActivity.class);
                        intent.putExtra("MY_NUMBER", data.getCallNumber());
                        intent.putExtra("displayName", displayName.getText().toString());
                        L.i(TAG, "displayNameCallActivity11=" + displayName.getText().toString() + "MY_NUMBER" + data.getCallNumber());
                        startActivity(intent);
                    }

                    @Override
                    public void onNetworkTopologyDetectionFinished(LoginResponseData resp) {
                        L.i(TAG, "net detect onNetworkTopologyDetectionFinished 2");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "网络探测已完成", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


                break;
            case R.id.sign:
                break;
            case R.id.login:


                break;
            default:
                break;
        }
    }
}
