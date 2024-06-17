package com.maxi.corejj.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.maxi.corejj.infrastucture.utils.L;

public class BroadcastPresenter {
    private static final String TAG = "BroadcastPresenter";
    private static BroadcastPresenter sBroadcastPresenter;

    public static BroadcastPresenter instance() {
        if (sBroadcastPresenter == null) {
            synchronized (BroadcastPresenter.class) {
                if (sBroadcastPresenter == null) {
                    sBroadcastPresenter = new BroadcastPresenter();
                }
            }
        }
        return sBroadcastPresenter;
    }

    private BroadcastPresenter() {
        L.k(TAG, "BroadcastPresenter...  ");
    }

    public void main(Context context) {
        initBroadcastReceiver(context);
        initBroadcastReceiver2(context);

        L.k(TAG, "main.  ");
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 1; i++) {
               //     send(context);
                }
            }
        }.start();
        send(context);
        send2(context);
    }

    private void send(Context context) {
        L.k(TAG, "send...  ");
        Intent intent = new Intent("com.example.MY_CUSTOM_BROADCAST");
        intent.setPackage("com.maxi.sub1");
        // 可以携带一些额外的数据
        intent.putExtra("message_key", "Hello, this is a custom broadcast!");

        // 发送广播，这里使用sendBroadcast，也可以使用sendOrderedBroadcast等
        context.sendBroadcast(intent);
    }

    private void send2(Context context) {
        L.k(TAG, "send2...  ");
        Intent intent = new Intent("com.example.MY_CUSTOM_BROADCAST2");
        intent.setPackage("com.maxi.sub2");
        // 可以携带一些额外的数据
        intent.putExtra("message_key", "Hello, this is a custom broadcast!");

        // 发送广播，这里使用sendBroadcast，也可以使用sendOrderedBroadcast等
        context.sendBroadcast(intent);
    }

    private void initBroadcastReceiver(Context context) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message_key");
                if (message != null) {
                    // 处理接收到的消息
                    L.k(TAG, "initBroadcastReceiver. onReceive. message: " + message);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.MY_CUSTOM_BROADCAST");
        context.registerReceiver(receiver, filter);

    }

    private void initBroadcastReceiver2(Context context) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message_key");
                if (message != null) {
                    // 处理接收到的消息
                    L.k(TAG, "initBroadcastReceiver2. onReceive. message: " + message);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.MY_CUSTOM_BROADCAST");
        context.registerReceiver(receiver, filter);

    }
}
