package com.zyzxsp.presenter;

import com.zyzxsp.view.CallView;

/**
 * Created by Administrator on 2019/05/23.
 */

public interface CallPresenter {
    void setView(CallView callView);

    void closeMicrophone(boolean close);
}
