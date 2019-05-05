package com.zyzxsp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;
import android.os.Process;

import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.Settings;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.zyzxsp.utils.AlertUtil;

import java.util.List;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);

//        Settings settings = new Settings("346d569fc78988f82ec183111ded661761f20aea");   // 小鱼开发环境extID
//        settings.setDebug(true);

        // 注意：Settings中的企业ID: 请到管理后台申请, 并配置package对应的SHA1
        Settings settings = new Settings("c27efa2adc3c2998bf8f517a1155559803b6c4e9");   // 小鱼生产环境extID
        //Settings settings = new Settings("3e816492058911e7a31d000c29971af5");

//        settings.setPrivateCloudAddress("private.xylink.com");
        //settings.setPrivateCloudAddress("47.95.0.220");//   101.89.65.217
        AlertUtil.init(getApplicationContext());
        // settings.setSpeakerOnModeDefault(false); // 默认使用扬声器或听筒模式
        //settings.setDefaultCameraId(1);//0:后置 默认1:前置
        int pId = Process.myPid();
        String processName = "";
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> ps = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo p : ps) {
            if (p.pid == pId) {
                processName = p.processName;
                break;
            }
        }

        // 避免被初始化多次
        if (processName.equals(getPackageName())) {
            NemoSDK nemoSDK = NemoSDK.getInstance();
            nemoSDK.init(this, settings);

            // 被叫服务，不使用被叫功能的请忽略
            Intent incomingCallService = new Intent(this, IncomingCallService.class);
            startService(incomingCallService);
        }
    }
}
