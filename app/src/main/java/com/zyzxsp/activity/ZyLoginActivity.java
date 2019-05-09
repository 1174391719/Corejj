package com.zyzxsp.activity;

import android.content.Intent;
import android.log.L;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ainemo.sdk.otf.ConnectNemoCallback;
import com.ainemo.sdk.otf.LoginResponseData;
import com.ainemo.sdk.otf.NemoSDK;
import com.google.gson.Gson;
import com.zyzxsp.R;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.bean.LoginResData;
import com.zyzxsp.dialog.DialogPresenter;
import com.zyzxsp.dialog.DialogPresenterImpl;
import com.zyzxsp.utils.Utils;
import com.zyzxsp.utils.ZLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class ZyLoginActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "ZyLoginActivity";

    private EditText mLoginName;
    private EditText mLoginPassword;
    private Button mLoginBtn;
    private CheckBox mPasswordVisibility = null;
    private View mCleanPassword = null;
    //private Button mXyLoginBtn;

    private NemoSDK nemoSDK = NemoSDK.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zy_login);
        mLoginName = findViewById(R.id.login_name);
        mLoginPassword = findViewById(R.id.login_password);
        mLoginBtn = findViewById(R.id.login_button);
        mPasswordVisibility = findViewById(R.id.login_pw_visibility);
        mCleanPassword = findViewById(R.id.login_pw_clean);
        // mXyLoginBtn = findViewById(R.id.xylogin_button);
        mLoginBtn.setOnClickListener(this);
//        //  mXyLoginBtn.setOnClickListener(this);


        mCleanPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginPassword.setText("");
            }
        });
        mLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPasswordVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    mLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login_button:
                String loginNameStr = ZLog.debug ? "13678888889" : mLoginName.getText().toString();
                String loginPasswordStr = ZLog.debug ? "686460zs" : mLoginPassword.getText().toString();
                mLoginPassword.getText().toString();
                if (loginNameStr.length() == 0 || loginPasswordStr.length() == 0) {
                    Toast.makeText(getApplicationContext(), "账号或密码为空，请输入账号或密码", Toast.LENGTH_SHORT).show();
                    break;
                }
                requestLogin(loginNameStr, loginPasswordStr);
                break;
//            case R.id.xylogin_button:
//                final String loginName = ZLog.debug ? "18721106192" : mLoginName.getText().toString();
//                String loginPassword = ZLog.debug ? "106192" : mLoginPassword.getText().toString();
//                if (loginName.length() == 0 || loginPassword.length() == 0) {
//                    Toast.makeText(getApplicationContext(), "账号或密码为空，请输入账号或密码", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//
//                nemoSDK.loginXYlinkAccount(loginName, loginPassword, new ConnectNemoCallback() {
//                    @Override
//                    public void onFailed(final int i) {
//                        ZLog.d(TAG, "使用小鱼账号登录失败，错误码：" + i);
//                        try {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(ZyLoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        } catch (Exception e) {
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onSuccess(LoginResponseData data, boolean isDetectingNetworkTopology) {
//                        ZLog.i(TAG, "匿名登录成功，号码为：" + data.getCallNumber());
//                        try {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(ZyLoginActivity.this, "小鱼账号登录成功", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        } catch (Exception e) {
//                        }
//
//                        Intent intent = new Intent(ZyLoginActivity.this, ZyHomeActivity.class);
//                        intent.putExtra("MY_NUMBER", data.getCallNumber());
//                        intent.putExtra("displayName", loginName);
//                        ZLog.i(TAG, "displayNameCallActivity11:" + loginName + "MY_NUMBER:" + data.getCallNumber());
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onNetworkTopologyDetectionFinished(LoginResponseData resp) {
//                        L.i(TAG, "net detect onNetworkTopologyDetectionFinished 2");
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(ZyLoginActivity.this, "网络探测已完成", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//                break;
            default:
                break;
        }

    }

    /**
     * 登录请求
     */
    private void requestLogin(String name, String password) {
        String url = ConstantUrl.HOST + ConstantUrl.LOGIN;
        ZLog.d(TAG, "requestLogin: 登录 url  " + url);

        JSONObject object = new JSONObject();
        String objectStr = "";
        try {
            object.put("phone", name);
            object.put("password", Utils.removeInvalidChar(Utils.encryptionByAES(password, "abcdef0123456789")));
            objectStr = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ZLog.i(TAG, "requestLogin:   请求参数是  " + objectStr);
        OkhttpUtil.okHttpPostJson(url, objectStr, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Call mcall = call;
                ZLog.e(TAG, "onFailure. e:" + e.toString());
                DialogPresenter dialog = new DialogPresenterImpl();
                if (e instanceof ConnectException) {
                    dialog.confirm(ZyLoginActivity.this, null, "网络连接已断开，请检查网络设置", "确定");
                } else if (e instanceof SocketTimeoutException) {
                    dialog.confirm(ZyLoginActivity.this, null, "网络连接异常，请重试", "确定");
                } else {
                    dialog.confirm(ZyLoginActivity.this, null, "暂时无法登录，请稍后重试", "确定");
                }
            }

            @Override
            public void onResponse(String response) {
                ZLog.i(TAG, "onResponse:  " + response);
                if (response == null) {
                    return;
                }
                Gson json = new Gson();
                LoginResData dataBean = json.fromJson(response, LoginResData.class);
                if ("0".equals(dataBean.getReturnCode())) {
                    LoginResData.LoginData mLoginData = dataBean.getObject();
                    String token = mLoginData.getToken();
                    ZyHomeActivity.sUserBean.setToken(token);
                    ZLog.d(TAG, "onResponse. token:" + token);
                    goHomeActivity();
                } else {
                    DialogPresenter dialog = new DialogPresenterImpl();
                    dialog.confirm(ZyLoginActivity.this, null, "账号或者密码错误", "确定");
                }
            }
        });
    }

    private void goHomeActivity() {
        Intent intent = new Intent(ZyLoginActivity.this, ZyHomeActivity.class);
        intent.putExtra("MY_NUMBER", "");
        intent.putExtra("displayName", "");
        // ZLog.i(TAG, "displayNameCallActivity11:" + loginName + "MY_NUMBER:" + data.getCallNumber());
        startActivity(intent);
    }
}
