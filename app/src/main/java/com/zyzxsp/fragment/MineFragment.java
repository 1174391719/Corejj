package com.zyzxsp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.zyzxsp.R;
import com.zyzxsp.activity.AboutActivity;
import com.zyzxsp.activity.MyCloudMeettingActivity;
import com.zyzxsp.activity.UserInfoActivity;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.data.LoginOutResData;
import com.zyzxsp.data.UserInfoResData;

import org.json.JSONObject;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class MineFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = "MineFragment";
    private ConstraintLayout mUserInfoLayout;
    private SimpleDraweeView mSimpleDraweeView;
    private TextView mUserNameText;
    private TextView mPhoneText;
    private RelativeLayout mMyMeetLayout;
    private RelativeLayout mAboutLayout;
    private View mView;

    private UserInfoResData.UserInfo mUserinfo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView == null){
            mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_mine_layout,container,false);
        }
        mUserInfoLayout = mView.findViewById(R.id.uesr_info_constraintLayout);
        mSimpleDraweeView = mView.findViewById(R.id.user_img);
        mUserNameText = mView.findViewById(R.id.user_name_textview);
        mPhoneText = mView.findViewById(R.id.user_phone_textView);
        mMyMeetLayout = mView.findViewById(R.id.my_meet_layout);
        mAboutLayout = mView.findViewById(R.id.about_layout);

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
        switch (id){
            case R.id.uesr_info_constraintLayout:
//                Intent intentUserInfo = new Intent(getContext(),MyUserInfoActivity.class);
                Intent intentUserInfo = new Intent(getContext(),UserInfoActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("userInfoData",mUserinfo);
                intentUserInfo.putExtra("userInfoBundle",mBundle);
                startActivity(intentUserInfo);
                break;
            case R.id.my_meet_layout:
                Intent intentMeet = new Intent(getContext(),MyCloudMeettingActivity.class);
                startActivity(intentMeet);
                break;
            case R.id.about_layout:
                Intent intentAbout = new Intent(getContext(),AboutActivity.class);
                startActivity(intentAbout);
                break;
        }
    }

    /**
     * 获取用户信息请求
     */
    public void requestGetUserInfo(){
        String url = ConstantUrl.headerUrl + ConstantUrl.getUserUrl;
        Log.d(TAG, "requestGetUserInfo: 登出 url  " + url);

        JSONObject object = new JSONObject();
        String  objectStr = object.toString();

        Log.d(TAG, "requestGetUserInfo:   请求参数是  " + objectStr);
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
                UserInfoResData dataBean = json.fromJson(response,UserInfoResData.class);
                if("0".equals(dataBean.getReturnCode())){
                    mUserinfo = dataBean.getObject();
                    if(mUserinfo != null){
                        String mName = mUserinfo.getName();
                        mUserNameText.setText(TextUtils.isEmpty(mName) ? "" : mName);
                        String mPhone = mUserinfo.getPhone();
                        mPhoneText.setText(TextUtils.isEmpty(mPhone) ? "" : mPhone);
                    }

                }else{
                    String errorMess = dataBean.getReturnMessage();
                    if(!TextUtils.isEmpty(errorMess)){
                        Toast.makeText( getContext(),errorMess,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
