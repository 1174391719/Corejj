package com.zyzxsp.presenter;

import android.content.Context;

import com.zyzxsp.UserBean;

/**
 * Created by Administrator on 2019/05/18.
 */

public interface MainPresenter {
    UserBean getUser();

    RecentCallPresenter getRecentCallPresenter();

    MainActivityPresenter getMainActivityPresenter();

    MainGuard getMainGuard();

    void logout(Context context);

    void setCallPresenter(CallPresenter presenter);

    CallPresenter getCallPresenter();

    NetWorkPresenter getNetWorkPresenter();
}
