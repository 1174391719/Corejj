package com.zyzxsp.activity;

import android.content.Context;
import android.content.Intent;
import android.log.L;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ainemo.sdk.otf.ConnectNemoCallback;
import com.ainemo.sdk.otf.LoginResponseData;
import com.ainemo.sdk.otf.NemoSDK;
import com.google.gson.Gson;
import com.zyzxsp.R;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.data.LoginResData;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class ZyLoginActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "ZyLoginActivity";

    private EditText mLoginName;
    private EditText mLoginPassword;
    private Button mLoginBtn;
    private Button mXyLoginBtn;

    private NemoSDK nemoSDK = NemoSDK.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zy_login);
        mLoginName = findViewById(R.id.login_name);
        mLoginPassword = findViewById(R.id.login_password);
        mLoginBtn = findViewById(R.id.login_button);
        mXyLoginBtn = findViewById(R.id.xylogin_button);
        mLoginBtn.setOnClickListener(this);
        mXyLoginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.login_button:
                String loginNameStr = mLoginName.getText().toString();
                String loginPasswordStr = mLoginPassword.getText().toString();
                if(loginNameStr.length() == 0 || loginPasswordStr.length() == 0){
                    Toast.makeText(getApplicationContext(), "账号或密码为空，请输入账号或密码", Toast.LENGTH_SHORT).show();
                    break;
                }
                requestLogin(loginNameStr,loginPasswordStr);
                break;
            case R.id.xylogin_button:
                final String loginName = mLoginName.getText().toString();
                String loginPassword = mLoginPassword.getText().toString();
                if(loginName.length() == 0 || loginPassword.length() == 0){
                    Toast.makeText(getApplicationContext(), "账号或密码为空，请输入账号或密码", Toast.LENGTH_SHORT).show();
                    break;
                }

                nemoSDK.loginXYlinkAccount(loginName, loginPassword, new ConnectNemoCallback() {
                    @Override
                    public void onFailed(final int i) {
                        L.e(TAG, "使用小鱼账号登录失败，错误码：" + i);
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ZyLoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(ZyLoginActivity.this, "小鱼账号登录成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                        }

                        Intent intent = new Intent(ZyLoginActivity.this, ZyHomeActivity.class);
                        intent.putExtra("MY_NUMBER", data.getCallNumber());
                        intent.putExtra("displayName", loginName);
                        L.i(TAG, "displayNameCallActivity11=" + loginName + "MY_NUMBER" + data.getCallNumber());
                        startActivity(intent);
                    }

                    @Override
                    public void onNetworkTopologyDetectionFinished(LoginResponseData resp) {
                        L.i(TAG, "net detect onNetworkTopologyDetectionFinished 2");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ZyLoginActivity.this, "网络探测已完成", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
        }

    }

    /**
     * 登录请求
     */
    public void requestLogin(String name,String password){
        String url = ConstantUrl.headerUrl + ConstantUrl.loginUrl;
        Log.d(TAG, "requestLogin: 登录 url  " + url);

        JSONObject object = new JSONObject();
        String  objectStr = "";
        try {
            object.put("phone",name);
            object.put("password",password);
            objectStr = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "requestLogin:   请求参数是  " + objectStr);
        OkhttpUtil.okHttpPostJson(url,objectStr, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Call mcall = call;
                Log.d(TAG, "onFailure:  "+ e.toString());

            }

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse:  "+ response);
                if(response == null){
                    return;
                }
                Gson json = new Gson();
                LoginResData dataBean = json.fromJson(response,LoginResData.class);
                if("0".equals(dataBean.getReturnCode())){
                    LoginResData.LoginData mLoginData = dataBean.getObject();
                    String token = mLoginData.getToken();
                }else{
                    String errorMess = dataBean.getReturnMessage();
                    if(!TextUtils.isEmpty(errorMess)){
                        Toast.makeText( ZyLoginActivity.this,errorMess,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
