package com.zyzxsp.presenter;

import com.ainemo.sdk.otf.NemoSDK;
import com.zyzxsp.utils.ZLog;
import com.zyzxsp.view.CallView;

import static android.view.View.GONE;

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
        ZLog.d("close:" + close + " isMicMuted:" + NemoSDK.getInstance().isMicMuted());
        NemoSDK.getInstance().enableMic(close, true);
        mCallView.onCloseMicrophone(close);
    }

    @Override
    public void hangUp() {
        ZLog.d("");
        NemoSDK.getInstance().hangup();
        mCallView.onHangUp();
    }
}
