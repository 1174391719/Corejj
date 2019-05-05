package com.zyzxsp.myInterface;

import android.graphics.Bitmap;

import com.ainemo.sdk.otf.NemoSDKListener;
import com.ainemo.sdk.otf.VideoInfo;

import java.util.List;

public interface CallListener {
    void onContentStateChanged(NemoSDKListener.ContentState contentState);

    void onNewContentReceive(Bitmap bitmap);

    void onVideoDataSourceChange(List<VideoInfo> videoInfos);
}
