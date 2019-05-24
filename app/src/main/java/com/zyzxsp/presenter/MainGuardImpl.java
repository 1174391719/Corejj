package com.zyzxsp.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.zyzxsp.utils.ZLog;


/**
 * Created by Administrator on 2019/05/23.
 */

public class MainGuardImpl extends BroadcastReceiver implements MainGuard {
    private Handler mHandler = null;
    private Runnable mRunnable = null;

    public MainGuardImpl() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void register(Activity activity) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        activity.registerReceiver(this, filter);
    }

    public void unRegister(Activity activity) {
        activity.unregisterReceiver(this);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        ZLog.i("onReceive. CONNECTED");
                        mHandler.removeCallbacks(mRunnable);
                    }
                } else {
                    ZLog.i("onReceive. DISCONNECTED");
                    Toast.makeText(context, "网络连接已断开", Toast.LENGTH_LONG).show();
                    if (mRunnable != null) {
                        return;
                    }
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mHandler.removeCallbacks(mRunnable);
                            mRunnable = null;
                            Toast.makeText(context, "网络异常，通话断开", Toast.LENGTH_LONG).show();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainPresenterImpl.getInstants().getCallPresenter().hangUp();
                                }
                            }, 1000);
                        }
                    };
                    mHandler.postDelayed(mRunnable, 10 * 1000);
                }
            }
        }
    }
}
