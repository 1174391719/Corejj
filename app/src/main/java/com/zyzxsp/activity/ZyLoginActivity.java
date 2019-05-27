package com.zyzxsp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zyzxsp.R;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.bean.LoginResData;
import com.zyzxsp.dialog.DialogPresenter;
import com.zyzxsp.dialog.DialogPresenterImpl;
import com.zyzxsp.presenter.MainPresenterImpl;
import com.zyzxsp.utils.StatusBarUtils;
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
    private View mCleanAccount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        setContentView(R.layout.activity_zy_login);
        ZLog.i("Init...");
        mLoginName = findViewById(R.id.login_name);
        mLoginPassword = findViewById(R.id.login_password);
        mLoginBtn = findViewById(R.id.login_button);
        mPasswordVisibility = findViewById(R.id.login_pw_visibility);
        mCleanPassword = findViewById(R.id.login_pw_clean);
        mCleanAccount = findViewById(R.id.login_account_clean);
        mLoginBtn.setOnClickListener(this);
        mCleanAccount.setOnClickListener(this);
        mCleanPassword.setOnClickListener(this);
        mLoginName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButton();
                boolean nullStr = TextUtils.isEmpty(mLoginName.getText());
                if (nullStr) {
                    if (mCleanAccount.getVisibility() == View.VISIBLE) {
                        mCleanAccount.setVisibility(View.INVISIBLE);
                    }
                    return;
                } else {
                    if (mCleanAccount.getVisibility() == View.INVISIBLE) {
                        mCleanAccount.setVisibility(View.VISIBLE);
                    }
                }
                if (s == null || s.length() == 0) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                        continue;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    mLoginName.setText(sb.toString());
                    mLoginName.setSelection(index);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButton();
                boolean nullStr = TextUtils.isEmpty(mLoginPassword.getText());
                if (nullStr) {
                    if (mCleanPassword.getVisibility() == View.VISIBLE) {
                        mCleanPassword.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (mCleanPassword.getVisibility() == View.INVISIBLE) {
                        mCleanPassword.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPasswordVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()

        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    mLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                mLoginPassword.setSelection(mLoginPassword.length());
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login_button:
                String loginNameStr = ZLog.use_config_data ? "13678888889" : mLoginName.getText().toString();
                String loginPasswordStr = ZLog.use_config_data ? "123456a" : mLoginPassword.getText().toString();
                mLoginPassword.getText().toString();
                if (loginNameStr.length() == 0 || loginPasswordStr.length() == 0) {
                    Toast.makeText(getApplicationContext(), "账号或密码为空，请输入账号或密码", Toast.LENGTH_SHORT).show();
                    break;
                }
                requestLogin(loginNameStr.replace(" ", ""), loginPasswordStr.replace(" ", ""));
                break;
            case R.id.login_account_clean:
                mLoginName.setText("");
                break;
            case R.id.login_pw_clean:
                mLoginPassword.setText("");
                break;
            default:
                break;
        }

    }

//***********************************************************************************************

    /**
     * 登录请求
     */

    private void requestLogin(final String name, String password) {
        //    goHomeActivity();
        String url = ConstantUrl.HOST + ConstantUrl.LOGIN;
        ZLog.d("登录 url  " + url);

        JSONObject object = new JSONObject();
        String objectStr = "";
        try {
            object.put("phone", name);
            object.put("password", Utils.removeInvalidChar(Utils.encryptionByAES(password)));
            objectStr = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ZLog.i(" 请求参数是  " + objectStr);
        OkhttpUtil.okHttpPostJson(url, objectStr, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Call mcall = call;
                ZLog.e(e.toString());
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
                ZLog.i(response);
                if (response == null) {
                    return;
                }
                try {
                    LoginResData dataBean = new Gson().fromJson(response, LoginResData.class);
                    if ("0".equals(dataBean.getReturnCode())) {
                        LoginResData.LoginData mLoginData = dataBean.getObject();
                        String token = mLoginData.getToken();
                        MainPresenterImpl.getInstants().getUser().setToken(token);
                        MainPresenterImpl.getInstants().getUser().setAccount(name);
                        ZLog.d("token:" + token);
                        goHomeActivity();
                        finish();
                    } else {
                        DialogPresenter dialog = new DialogPresenterImpl();
                        dialog.confirm(ZyLoginActivity.this, null, "账号或者密码错误", "确定");
                    }
                } catch (Exception e) {
                    ZLog.d("e:" + e);
                }
            }
        });
    }

    private void goHomeActivity() {
        Intent intent = new Intent(ZyLoginActivity.this, ZyMainActivity.class);
        startActivity(intent);
    }

    private void updateLoginButton() {
        if (TextUtils.isEmpty(mLoginName.getText()) || TextUtils.isEmpty(mLoginPassword.getText())) {
            mLoginBtn.setBackgroundResource(R.drawable.round_corner_clicked_unavailable);
            mLoginBtn.setClickable(false);
        } else {
            mLoginBtn.setBackgroundResource(R.drawable.round_corner_clicked);
            mLoginBtn.setClickable(true);
        }
    }
}
