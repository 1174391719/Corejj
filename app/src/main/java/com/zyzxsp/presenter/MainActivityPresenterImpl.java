package com.zyzxsp.presenter;

import android.content.Context;
import android.view.View;

import com.zyzxsp.dialog.DialogPresenter;
import com.zyzxsp.dialog.DialogPresenterImpl;
import com.zyzxsp.utils.ZLog;

/**
 * Created by Administrator on 2019/05/23.
 */

public class MainActivityPresenterImpl implements MainActivityPresenter {
    private Context mContext = null;
    private MainActivityView mView = null;

    @Override
    public void setView(MainActivityView view) {
        mView = view;
    }

    @Override
    public void showLogoutDialog() {
        ZLog.d("");
        DialogPresenter dialogPresenter = new DialogPresenterImpl();
        dialogPresenter.confirm(mContext, new DialogPresenter.Callback() {
            @Override
            public void onPositiveClick() {
                MainPresenterImpl.getInstants().logout(mContext);
            }

            @Override
            public void onNegativeClick() {

            }
        }, "该账号已在其他地方登录，请重新登录", "知道了");

    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }
}
