package com.zyzxsp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.log.L;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ainemo.sdk.otf.ConnectNemoCallback;
import com.ainemo.sdk.otf.LoginResponseData;
import com.ainemo.sdk.otf.NemoSDK;
import com.zyzxsp.R;
import com.zyzxsp.dialog.LoadingDialog;
import com.zyzxsp.download.PackageInfoUtil;
import com.zyzxsp.view.HeaderTitleView;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class MyTestActivity extends Activity implements View.OnClickListener {
    private String TAG = "MyTestActivity";
    private NemoSDK nemoSDK = NemoSDK.getInstance();
    private EditText displayName;
    private EditText externalId;
    private HeaderTitleView mHeaderTitleView;
    private Button mGocehi;

    private CountDownTimer mTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_layout);
        findViewById(R.id.sign).setOnClickListener(this);
        findViewById(R.id.connect_nemo).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.get_signinfo).setOnClickListener(this);
        findViewById(R.id.go_zyhome).setOnClickListener(this);
        displayName = (EditText) findViewById(R.id.display_name);
        displayName.setHint("请输入手机号/邮箱");
        externalId = (EditText) findViewById(R.id.register_external_id);
        externalId.setHint("请输入密码");
        mHeaderTitleView = findViewById(R.id.test_header_view);
        checkPermission();
        mHeaderTitleView.setmOnHeaderTitleViewClick(new HeaderTitleView.onHeaderTitleViewClick() {
            @Override
            public void leftClick(View view) {
                finish();
            }

            @Override
            public void rightClick(View view) {
//                Toast.makeText(MyTestActivity.this,"点击右边啦",Toast.LENGTH_LONG).show();
            }
        });

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
                                    Toast.makeText(MyTestActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(MyTestActivity.this, "小鱼账号登录成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                        }

                        Intent intent = new Intent(MyTestActivity.this, CallActivity.class);
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
                                Toast.makeText(MyTestActivity.this, "网络探测已完成", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


                break;
            case R.id.sign:

               // ApkUpdateRequest.requestUpdateApk(MyTestActivity.this);
                break;
            case R.id.login:
                String url =displayName.getText().toString();
                if(TextUtils.isEmpty(url)){
                    Toast.makeText(MyTestActivity.this,"请输入网络地址",Toast.LENGTH_LONG).show();
                    return;
                }
                OkhttpUtil.okHttpGet(url, new CallBackUtil.CallBackString() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        Call mcall = call;
                        Log.d(TAG, "onFailure:  "+ e.toString());

                    }

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse:  "+ response);


                    }
                });

                break;
            case R.id.get_signinfo:
                String singinfo = PackageInfoUtil.getInstallApkSign(MyTestActivity.this);
                Log.d(TAG, "onClick:   获取签名信息　　　"+ singinfo);
                final LoadingDialog mLoadingDialog = LoadingDialog.getInstance();
                if(mTimer == null){
                    mTimer = new CountDownTimer((long) (5 * 1000), 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if(!MyTestActivity.this.isFinishing()){
                                int remainTime = (int) (millisUntilFinished / 1000L);
                                Log.e(TAG,"======remainTime=====" + remainTime);
                            }

                        }

                        @Override
                        public void onFinish() {
                            Log.e(TAG,"======onFinish=====  ");
//                            mLoadingDialog.dismisLoading();
                        }
                    };

                }
                mLoadingDialog.showLoadingHasNotice(MyTestActivity.this,"加载中...",true);
                mTimer.start();
                break;
            case R.id.go_zyhome:
                Intent intent = new Intent(MyTestActivity.this,ZyMainActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
