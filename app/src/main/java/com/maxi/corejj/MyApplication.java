package com.maxi.corejj;

import android.app.Application;

import com.maxi.corejj.infrastucture.utils.L;

public class MyApplication extends Application {
    private static final String TAG = "FilePathUtils";
    private static Application sApplication;

    public static Application instance() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.d(TAG, "onCreate. ");
        sApplication = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        L.d(TAG, "onTerminate. ");
    }
}
