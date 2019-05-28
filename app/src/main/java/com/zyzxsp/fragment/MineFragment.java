package com.zyzxsp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ainemo.sdk.otf.ConnectNemoCallback;
import com.ainemo.sdk.otf.LoginResponseData;
import com.ainemo.sdk.otf.NemoSDK;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.zyzxsp.R;
import com.zyzxsp.activity.AboutActivity;
import com.zyzxsp.activity.MyCloudMeetingActivity;
import com.zyzxsp.activity.UserInfoActivity;
import com.zyzxsp.activity.ZyMainActivity;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.bean.UserInfoResData;
import com.zyzxsp.dialog.DialogPresenter;
import com.zyzxsp.dialog.DialogPresenterImpl;
import com.zyzxsp.presenter.MainPresenterImpl;
import com.zyzxsp.utils.StatusBarUtils;
import com.zyzxsp.utils.ZLog;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class MineFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "MineFragment";
    private LinearLayout mUserInfoLayout;
    private SimpleDraweeView mSimpleDraweeView;
    private TextView mUserNameText;
    private TextView mPhoneText;
    private RelativeLayout mMyMeetLayout;
    private RelativeLayout mAboutLayout;
    private View mView;

    private UserInfoResData.UserInfo mUserinfo;
    private View mHeadView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_mine_layout, container, false);
        }
        mUserInfoLayout = mView.findViewById(R.id.uesr_info_constraintLayout);
        mHeadView = mView.findViewById(R.id.mine_header_view);
        mSimpleDraweeView = mView.findViewById(R.id.user_img);
        mUserNameText = mView.findViewById(R.id.user_name_textview);
        mPhoneText = mView.findViewById(R.id.user_phone_textView);
        mMyMeetLayout = mView.findViewById(R.id.my_meet_layout);
        mAboutLayout = mView.findViewById(R.id.about_layout);

        mHeadView.setPadding(0, StatusBarUtils.getStateBarHeight(getContext()), 0, 0);
        mUserInfoLayout.setOnClickListener(this);
        mMyMeetLayout.setOnClickListener(this);
        mAboutLayout.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestGetUserInfo();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.uesr_info_constraintLayout:
//                Intent intentUserInfo = new Intent(getContext(),MyUserInfoActivity.class);
                Intent intentUserInfo = new Intent(getContext(), UserInfoActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("userInfoData", mUserinfo);
                intentUserInfo.putExtra("userInfoBundle", mBundle);
                startActivity(intentUserInfo);
                break;
            case R.id.my_meet_layout:
                Intent intentMeet = new Intent(getContext(), MyCloudMeetingActivity.class);
                startActivity(intentMeet);
                break;
            case R.id.about_layout:
                Intent intentAbout = new Intent(getContext(), AboutActivity.class);
                startActivity(intentAbout);
                break;
        }
    }

    /**
     * 获取用户信息请求
     */
    public void requestGetUserInfo() {
        if (MainPresenterImpl.getInstants().getUser() == null || MainPresenterImpl.getInstants().getUser().getToken() == null) {
            return;
        }
        String url = ConstantUrl.HOST + ConstantUrl.GET_USER_INFO;
        ZLog.d("url:" + url);
        Map map = new HashMap();
        map.put("token", MainPresenterImpl.getInstants().getUser().getToken());

        OkhttpUtil.okHttpPostJson(url, null, map, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Call mcall = call;
                ZLog.e(e.toString());
                DialogPresenter dialog = new DialogPresenterImpl();
                if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
                    dialog.confirm(getContext(), null, "网络连接异常，请重试", "确定");
                } else {
                    dialog.confirm(getContext(), null, "信息获取异常，请收受重试", "确定");
                }
            }

            @Override
            public void onResponse(String response) {
                ZLog.d(response);
                if (response == null) {
                    return;
                }
                Gson json = new Gson();
                UserInfoResData dataBean = json.fromJson(response, UserInfoResData.class);
                if ("0".equals(dataBean.getReturnCode())) {
                    mUserinfo = dataBean.getObject().getUser();
                    if (mUserinfo != null) {
                        String mName = mUserinfo.getName();
                        ZLog.d("mName:" + mName);
                        mUserNameText.setText(TextUtils.isEmpty(mName) ? "" : mName);
                        String mPhone = mUserinfo.getPhone();
                        mPhoneText.setText(TextUtils.isEmpty(mPhone) ? "" : mPhone);
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setUri(ConstantUrl.HOST + mUserinfo.getAvatarPath())
                                .setAutoPlayAnimations(true)
                                .build();
                        mSimpleDraweeView.setController(controller);
                        MainPresenterImpl.getInstants().getUser().setName(mUserinfo.getName());
                        loginXiaoyu();
                    }

                } else {
                    String errorMess = dataBean.getReturnMessage();
                    if (!TextUtils.isEmpty(errorMess)) {
                        Toast.makeText(getContext(), errorMess, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //**********************************************************************************************
    private void loginXiaoyu() {
        ZLog.i("Name:" + MainPresenterImpl.getInstants().getUser().getName() + " account:" +
                MainPresenterImpl.getInstants().getUser().getAccount());
        NemoSDK.getInstance().loginExternalAccount(MainPresenterImpl.getInstants().getUser().getName(),
                MainPresenterImpl.getInstants().getUser().getAccount(), new ConnectNemoCallback() {
                    @Override
                    public void onFailed(int i) {
                        ZLog.e("i:" + i);
                    }

                    @Override
                    public void onSuccess(LoginResponseData loginResponseData, boolean b) {
                        ZLog.d("loginResponseData:" + loginResponseData);
                    }

                    @Override
                    public void onNetworkTopologyDetectionFinished(LoginResponseData loginResponseData) {
                        ZLog.e("loginResponseData:" + loginResponseData);
                    }
                });
    }
}