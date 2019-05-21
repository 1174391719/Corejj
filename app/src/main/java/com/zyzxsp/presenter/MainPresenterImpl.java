package com.zyzxsp.presenter;

import android.content.Context;
import android.content.Intent;

import com.zyzxsp.UserBean;
import com.zyzxsp.activity.BaseActivity;
import com.zyzxsp.activity.UserInfoActivity;
import com.zyzxsp.activity.ZyLoginActivity;
import com.zyzxsp.utils.ZLog;

/**
 * Created by Administrator on 2019/05/18.
 */

public class MainPresenterImpl implements MainPresenter {
    private static MainPresenter sMainPresenter = null;
    private UserBean mUser = null;
    private RecentCallPresenter mRecentCallPresenter = null;

    private MainPresenterImpl() {
        ZLog.i("Init...");
        mUser = new UserBean();
        mRecentCallPresenter = new RecentCallPresenterImpl();
    }

    public static MainPresenter getInstants() {
        if (sMainPresenter == null) {
            sMainPresenter = new MainPresenterImpl();
        }
        return sMainPresenter;
    }

    @Override
    public UserBean getUser() {
        return mUser;
    }

    @Override
    public RecentCallPresenter getRecentCallPresenter() {
        return mRecentCallPresenter;
    }

    @Override
    public void logout(Context context) {
        Intent intent = new Intent();
        intent.setAction(BaseActivity.ACTION_CHECKOUT);
        context.sendBroadcast(intent);
        Intent i = new Intent(context, ZyLoginActivity.class);
        context.startActivity(i);
    }
}