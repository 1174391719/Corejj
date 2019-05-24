package com.zyzxsp.presenter;

import android.app.Activity;

/**
 * Created by Administrator on 2019/05/23.
 */

public interface MainGuard {
    void register(Activity activity);

    void unRegister(Activity activity);
}
