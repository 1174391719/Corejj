package com.zyzxsp;

import android.app.Service;
import android.content.Intent;
import android.log.L;
import android.os.IBinder;

import com.ainemo.sdk.otf.NemoReceivedCallListener;
import com.ainemo.sdk.otf.NemoSDK;

import com.zyzxsp.activity.CallActivity;

public class IncomingCallService extends Service {

    private static final String TAG = "IncomingCallService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NemoSDK.getInstance().setNemoReceivedCallListener(new NemoReceivedCallListener() {
            @Override
            public void onReceivedCall(String name, String number, int callIndex) {

                L.i(TAG, "onReceivedCall called, remoteName="+name+", remoteNumber="+number+", callIndex="+callIndex);

                Intent it = new Intent(IncomingCallService.this, CallActivity.class);
                it.putExtra("callerName", name);    // 来电名称
                it.putExtra("callerNumber", number);    // 来电号码
                it.putExtra("callIndex", callIndex);
                it.putExtra("isIncomingCall", true);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
