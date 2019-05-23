package com.zyzxsp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zyzxsp.R;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.bean.LoginOutResData;
import com.zyzxsp.bean.UserInfoResData;
import com.zyzxsp.dialog.DialogPresenter;
import com.zyzxsp.dialog.DialogPresenterImpl;
import com.zyzxsp.presenter.MainPresenterImpl;
import com.zyzxsp.utils.StatusBarUtils;
import com.zyzxsp.utils.ZLog;
import com.zyzxsp.view.HeaderTitleView;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "UserInfoActivity";
    private TextView mUserNameTextView;
    private TextView mUserAccountTextView;
    private TextView mUserMailBoxTextView;
    private RelativeLayout mModifyPasswordLayout;
    private RelativeLayout mLoginoutTextView;
    private HeaderTitleView mHeaderTitleView;

    private UserInfoResData.UserInfo mUserinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        setContentView(R.layout.activity_userinfo);
        mHeaderTitleView = findViewById(R.id.userinfo_header_view);
        mUserNameTextView = findViewById(R.id.user_name_text);
        mUserAccountTextView = findViewById(R.id.user_account_text);
        mUserMailBoxTextView = findViewById(R.id.user_mailbox_text);
        mModifyPasswordLayout = findViewById(R.id.modify_password_layout);
        mLoginoutTextView = findViewById(R.id.login_out);

        mHeaderTitleView.setPadding(0, StatusBarUtils.getStateBarHeight(this), 0, 0);
        mModifyPasswordLayout.setOnClickListener(this);
        mLoginoutTextView.setOnClickListener(this);

        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle mBundle = intent.getBundleExtra("userInfoBundle");
            if (mBundle != null) {
                mUserinfo = (UserInfoResData.UserInfo) mBundle.getSerializable("userInfoData");
                if (mUserinfo != null) {
                    String mName = mUserinfo.getName();
                    mUserNameTextView.setText(TextUtils.isEmpty(mName) ? "" : mName);
                    String mPhone = mUserinfo.getPhone();
                    mUserAccountTextView.setText(TextUtils.isEmpty(mPhone) ? "" : mPhone);
                    String mEmail = mUserinfo.getEmail();
                    mUserMailBoxTextView.setText(TextUtils.isEmpty(mEmail) ? "" : mEmail);
                }
            }
        }


        mHeaderTitleView.setmOnHeaderTitleViewClick(new HeaderTitleView.onHeaderTitleViewClick() {
            @Override
            public void leftClick(View view) {
                finish();
            }

            @Override
            public void rightClick(View view) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.modify_password_layout:
                Intent intent = new Intent(UserInfoActivity.this, ModifyPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.login_out:
                DialogPresenter dialog = new DialogPresenterImpl();
                dialog.choose(UserInfoActivity.this, new DialogPresenter.Callback() {
                    @Override
                    public void onPositiveClick() {
                        requestLoginOut();
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                }, "确认退出？", "取消", "确定");
                break;
        }
    }


    /**
     * 登出请求
     */
    public void requestLoginOut() {
        String url = ConstantUrl.HOST + ConstantUrl.CHECKOUT;
        Log.d(TAG, "requestLoginOut: 登出 url  " + url);
        Map map = new HashMap();
        map.put("token", ZyMainActivity.sUserBean.getToken());

        OkhttpUtil.okHttpPostJson(url, null, map, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                ZLog.e(e.toString());
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
                    Toast.makeText(UserInfoActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                    MainPresenterImpl.getInstants().logout(UserInfoActivity.this);
                } else {
                    String errorMess = dataBean.getReturnMessage();
                    if (!TextUtils.isEmpty(errorMess)) {
                        Toast.makeText(UserInfoActivity.this, errorMess, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
