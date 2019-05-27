package com.zyzxsp.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zyzxsp.R;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.bean.LoginOutResData;
import com.zyzxsp.dialog.DialogPresenter;
import com.zyzxsp.dialog.DialogPresenterImpl;
import com.zyzxsp.presenter.MainPresenterImpl;
import com.zyzxsp.utils.StatusBarUtils;
import com.zyzxsp.utils.Utils;
import com.zyzxsp.utils.ZLog;
import com.zyzxsp.view.HeaderTitleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class ModifyPasswordActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "ModifyPasswordActivity";
    private HeaderTitleView mHeaderTitleView;

    private View mErrorTip = null;
    private TextView mTips = null;
    private EditText mOldPwEditText;
    private ImageView mCleanOldPw;
    private EditText mNewPwEditText;
    private ImageView mCleanNewPw;
    private EditText mNewConfirmPwEditText;
    private ImageView mCleanNewConfirmPw;

    private Button mModifyPasswordBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        setContentView(R.layout.activity_modify_password);
        mHeaderTitleView = findViewById(R.id.modify_password_header_view);
        mErrorTip = findViewById(R.id.ll_modify_pw_error_tip);
        mTips = findViewById(R.id.tv_modify_pw_tip);
        mOldPwEditText = findViewById(R.id.et_modify_pw_old_pw);
        mCleanOldPw = findViewById(R.id.iv_modify_pw_clean_old_pw);
        mNewPwEditText = findViewById(R.id.et_modify_pw_new_pw);
        mCleanNewPw = findViewById(R.id.iv_modify_pw_clean_new_pw);
        mNewConfirmPwEditText = findViewById(R.id.et_modify_pw_new_confirm_pw);
        mCleanNewConfirmPw = findViewById(R.id.iv_modify_pw_clean_new_confirm_pw);
        mModifyPasswordBtn = findViewById(R.id.bt_modify_pw);

        mCleanOldPw = findViewById(R.id.iv_modify_pw_clean_old_pw);
        mCleanNewPw = findViewById(R.id.iv_modify_pw_clean_new_pw);
        mCleanNewConfirmPw = findViewById(R.id.iv_modify_pw_clean_new_confirm_pw);

        mHeaderTitleView.setPadding(0, StatusBarUtils.getStateBarHeight(this), 0, 0);
        mModifyPasswordBtn.setOnClickListener(this);
        mCleanOldPw.setOnClickListener(this);
        mCleanNewPw.setOnClickListener(this);
        mCleanNewConfirmPw.setOnClickListener(this);

        mHeaderTitleView.setmOnHeaderTitleViewClick(new HeaderTitleView.onHeaderTitleViewClick() {
            @Override
            public void leftClick(View view) {
                finish();
            }

            @Override
            public void rightClick(View view) {

            }
        });

        mOldPwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mCleanOldPw.setVisibility(View.INVISIBLE);
                } else {
                    mCleanOldPw.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mNewPwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mCleanNewPw.setVisibility(View.INVISIBLE);
                } else {
                    mCleanNewPw.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mNewConfirmPwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mCleanNewConfirmPw.setVisibility(View.INVISIBLE);
                } else {
                    mCleanNewConfirmPw.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_modify_pw_clean_old_pw:
                mOldPwEditText.setText("");
                break;
            case R.id.iv_modify_pw_clean_new_pw:
                mNewPwEditText.setText("");
            case R.id.iv_modify_pw_clean_new_confirm_pw:
                mNewConfirmPwEditText.setText("");
                break;
            case R.id.bt_modify_pw:
                if (checkNewPassword()) {
                    checkOldPassword(mOldPwEditText.getText().toString());
                }
                break;
            default:
                break;

        }

    }

    //**********************************************************************************************
    private boolean checkNewPassword() {
        String oldPw = mOldPwEditText.getText().toString();
        String newPw = mNewPwEditText.getText().toString();
        String confirmPw = mNewConfirmPwEditText.getText().toString();
        if (TextUtils.isEmpty(newPw) || TextUtils.isEmpty(confirmPw)) {
            return false;
        }
        if (oldPw.equals(newPw) || oldPw.equals(confirmPw)) {
            mTips.setText(R.string.modify_pw_old);
            mErrorTip.setVisibility(View.VISIBLE);
            return false;
        }
        mTips.setText(R.string.modify_pw_new);
        if (!newPw.equals(confirmPw)) {
            mErrorTip.setVisibility(View.VISIBLE);
            return false;
        } else {
            mErrorTip.setVisibility(View.GONE);
            return true;
        }
    }

    public void judgeModifyBtnClick() {
        String oldPasStr = mOldPwEditText.getText().toString().trim();
        String newPassStr = mNewPwEditText.getText().toString().trim();
        String newPassConfirmStr = mNewConfirmPwEditText.getText().toString().trim();
        if (oldPasStr.length() > 0 && newPassStr.length() > 0 && newPassConfirmStr.length() > 0) {
            mModifyPasswordBtn.setClickable(true);
        } else {
            mModifyPasswordBtn.setClickable(false);
        }
    }

    public void checkOldPassword(String oldPassword) {
        String url = ConstantUrl.HOST + ConstantUrl.CHECK_PASSWORD;
        ZLog.d(url);
        Map header = new HashMap();
        header.put("token", MainPresenterImpl.getInstants().getUser().getToken());

        JSONObject object = new JSONObject();
        try {
            object.put("password", Utils.removeInvalidChar(Utils.encryptionByAES(oldPassword)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String objectStr = object.toString();
        OkhttpUtil.okHttpPostJson(url, objectStr, header, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Call mcall = call;
                ZLog.d(e.toString());
                DialogPresenter dialog = new DialogPresenterImpl();
                dialog.confirm(ModifyPasswordActivity.this, null, "原密码有误", "确定");
            }

            @Override
            public void onResponse(String response) {
                ZLog.d(" response:" + response);
                if (response == null) {
                    return;
                }
                Gson json = new Gson();
                LoginOutResData dataBean = json.fromJson(response, LoginOutResData.class);
                if ("0".equals(dataBean.getReturnCode())) {
                    modifyPassword(mOldPwEditText.getText().toString(), mNewPwEditText.getText().toString());

                } else {
                    String errorMess = dataBean.getReturnMessage();
                    if (!TextUtils.isEmpty(errorMess)) {
                        Toast.makeText(ModifyPasswordActivity.this, errorMess, Toast.LENGTH_SHORT).show();
                    }
                    if ("521001".equals(dataBean.getReturnCode())) {
                        MainPresenterImpl.getInstants().logout(ModifyPasswordActivity.this);
                    }
                }
            }
        });
    }

    private void modifyPassword(String oldPassword, String newPassword) {
        String url = ConstantUrl.HOST + ConstantUrl.MODIFY_PASSWORD;
        ZLog.d(" url:" + url);
        Map header = new HashMap();
        header.put("token", MainPresenterImpl.getInstants().getUser().getToken());

        JSONObject object = new JSONObject();
        try {
            object.put("oldPassword", Utils.removeInvalidChar(Utils.encryptionByAES(oldPassword)));
            object.put("newPassword", Utils.removeInvalidChar(Utils.encryptionByAES(newPassword)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String objectStr = object.toString();
        OkhttpUtil.okHttpPostJson(url, objectStr, header, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                ZLog.e("e:" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse. response:" + response);
                if (response == null) {
                    return;
                }
                Gson json = new Gson();
                LoginOutResData dataBean = json.fromJson(response, LoginOutResData.class);
                if ("0".equals(dataBean.getReturnCode())) {
                    Toast.makeText(ModifyPasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                    MainPresenterImpl.getInstants().logout(ModifyPasswordActivity.this);
                } else {
                    String errorMess = dataBean.getReturnMessage();
                    if (!TextUtils.isEmpty(errorMess)) {
                        Toast.makeText(ModifyPasswordActivity.this, errorMess, Toast.LENGTH_SHORT).show();
                    }
                    if ("521001".equals(dataBean.getReturnCode())) {
                        MainPresenterImpl.getInstants().logout(ModifyPasswordActivity.this);
                    }
                }
            }
        });
    }
}
