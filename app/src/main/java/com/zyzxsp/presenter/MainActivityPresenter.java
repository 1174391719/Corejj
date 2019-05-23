package com.zyzxsp.presenter;

import android.content.Context;

/**
 * Created by Administrator on 2019/05/23.
 */

public interface MainActivityPresenter {
    void setContext(Context context);

    void setView(MainActivityView view);

    void showLogoutDialog();

    interface MainActivityView {

    }
}
