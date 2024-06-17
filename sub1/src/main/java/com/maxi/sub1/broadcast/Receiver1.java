package com.maxi.sub1.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.maxi.sub1.L;


public class Receiver1 extends BroadcastReceiver {
    private static final String TAG = "Receiver1";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取广播内容，这里假设我们发送了一个字符串类型的数据
        String message = intent.getStringExtra("message_key");
        if (message != null) {
            // 处理接收到的消息
            L.k(TAG, "onReceive. message777: " + message);
        }
        try {
            Thread.sleep(2000);

        } catch (Exception e) {
        }
    }
}
