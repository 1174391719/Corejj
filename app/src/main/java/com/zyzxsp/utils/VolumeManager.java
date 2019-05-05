package com.zyzxsp.utils;

import android.content.Context;
import android.log.L;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.zyzxsp.R;


public class VolumeManager {
    private int mMaxVolume;
    private int mVolume = Integer.MIN_VALUE;
    private int streamType = AudioManager.STREAM_VOICE_CALL;

    private AudioManager mAudioManager;

    private View mVolumeLayout;
    private ImageView mOperationPercent;
    private AlphaAnimation mAlphHideAnimation;
    private ImageView mVolumeMuteState;
    private ImageView mVolumeFull;

    private MuteCallback muteCallback;
    /**
     * 定时隐藏
     */
    private Callback callback = new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mVolume = Integer.MIN_VALUE;
            // 隐藏
            if (mVolumeLayout.getVisibility() != View.GONE && mVolumeLayout.getAlpha() == 1) {
                mVolumeLayout.startAnimation(mAlphHideAnimation);
            }
            return false;
        }
    };
    private Handler mDismissHandler = new Handler(callback);

    public VolumeManager(Context context, View rootView, int streamType) {
        this.streamType = streamType;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(streamType);
        mVolumeLayout = rootView;
        mOperationPercent = (ImageView) mVolumeLayout.findViewById(R.id.operation_percent);
        mVolumeMuteState = (ImageView) mVolumeLayout.findViewById(R.id.volume_mute_state);
        mVolumeFull = (ImageView) mVolumeLayout.findViewById(R.id.operation_full);

        mAlphHideAnimation = new AlphaAnimation(1, 0);
        mAlphHideAnimation.setDuration(300);
        mAlphHideAnimation.setFillAfter(true);
    }

    public void setMuteCallback(MuteCallback muteCallback) {
        this.muteCallback = muteCallback;
    }

    public void onVolumeUp() {
        mVolume = mAudioManager.getStreamVolume(streamType);
        mVolume++;
        Log.i("TAG", "print onVolumeDown-->mVolume=" + mVolume);
        keyAdjustVolume(mVolume);
    }

    private void keyAdjustVolume(int index) {
        mDismissHandler.removeMessages(0);
        mVolumeLayout.clearAnimation();
        mVolumeLayout.setVisibility(View.VISIBLE);

        updateVolumn(index);
        onVolumeSlideEnd();
    }

    public void onVolumeDown() {
        mVolume = mAudioManager.getStreamVolume(streamType);
        mVolume--;
        Log.i("TAG", "print onVolumeDown-->mVolume=" + mVolume);
        keyAdjustVolume(mVolume);
    }

    public void onVolumeUnmute() {
        mVolume = 3;

        keyAdjustVolume(mVolume);
    }

    public void onVolumeMute() {
        mVolume = 0;
        keyAdjustVolume(mVolume);
    }

    public int getVolume() {
        return mAudioManager.getStreamVolume(streamType);
    }

    public void setVolume(int volume) {

        mAudioManager.setStreamVolume(streamType, volume, AudioManager.FLAG_PLAY_SOUND);
    }

    public boolean isVolumeMute() {
        if (mVolume <= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void onVolumeSlide(float percent) {
        if (mVolume == Integer.MIN_VALUE) {
            mVolume = mAudioManager.getStreamVolume(streamType);
            if (mVolume < 0)
                mVolume = 0;
            int index = (int) (percent * mMaxVolume) + mVolume;
            if (index == mVolume) {
                mVolume = Integer.MIN_VALUE;
                return;
            }
            mVolumeLayout.setVisibility(View.VISIBLE);
        }
        mVolumeLayout.clearAnimation();

        int index = (int) (percent * mMaxVolume) + mVolume;
        updateVolumn(index);
    }

    public void updateVolumn(int index) {

        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }
        // 变更声音
        mAudioManager.setStreamVolume(streamType, index, 0);
        boolean muted = index == 0;
        if (muteCallback != null) {
            muteCallback.muteChanged(muted);
        }
        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = mVolumeFull.getLayoutParams().width * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
        Log.i("TAG", "print updateVolumn-->index=" + index);
        mVolumeMuteState.setImageResource(index == 0 ? R.drawable.ic_volume_mute : R.drawable.ic_volume_un_mute);
    }

    /**
     * 手势结束
     */
    public void onVolumeSlideEnd() {
        L.i("volume slide end volume = " + mVolume + "MIN_VALUE" + Integer.MIN_VALUE);
        if (mVolume == Integer.MIN_VALUE)
            return;
        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    public interface MuteCallback {
        void muteChanged(boolean mute);
    }
}
