package com.zyzxsp.presenter;

import com.ainemo.sdk.otf.NemoSDK;
import com.zyzxsp.view.CallView;

/**
 * Created by Administrator on 2019/05/23.
 */

public class CallPresenterImpl implements CallPresenter {
    private CallView mCallView = null;

    @Override
    public void setView(CallView callView) {
        mCallView = callView;
    }

    @Override
    public void closeMicrophone(boolean close) {
        NemoSDK.getInstance().enableMic(close, true);
        mCallView.onCloseMicrophone(close);
    }
}
