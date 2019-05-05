package com.zyzxsp;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ainemo.sdk.otf.NemoSDK;
import com.zyzxsp.view.VideoGroupView;

/**
 * 呼出界面
 */
public class CallOutgoingFragment extends Fragment {
    private ImageButton mButtonCancel;
    private ImageView mImageTurn;
    private MediaPlayer mMediaPlayer;
    private VideoGroupView mVideoView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.calloutgoing_fragment, container, false);
        mVideoView = (VideoGroupView) root.findViewById(R.id.remote_video_view);
        mButtonCancel = (ImageButton) (root.findViewById(R.id.conn_mt_cancelcall_btn));
        mImageTurn = (ImageView) (root.findViewById(R.id.bg_turn));
        mButtonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NemoSDK.getInstance().hangup();
            }
        });
       // mVideoView.requestLocalFrame();
        return root;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mImageTurn.clearAnimation();
    }

    @Override
    public void onStop() {
        releaseRingtone();
        super.onStop();
    }

    protected void releaseRingtone() {
        if (mMediaPlayer != null) {
            mMediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  mVideoView.stopLocalFrameRender();
    }

    public void releaseResource() {
       // mVideoView.stopRender();
        mVideoView.destroy();
    }
}

