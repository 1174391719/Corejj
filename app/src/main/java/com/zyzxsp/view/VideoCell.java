package com.zyzxsp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import com.ainemo.module.call.data.Enums;
import com.ainemo.sdk.otf.OpenGLTextureView;
import com.ainemo.sdk.otf.VideoInfo;


public class VideoCell extends ViewGroup implements CellStateView.OnCellStateEventListener {
    private static final String TAG = "VideoCell";
    public static final int LOCAL_VIEW_ID = 99;
    public static final int NULL_SSRC = 0;
    public static final int CONTENT_VIDEO = -1;
    public static final int LOCAL_VIDEO = -2;
    public static final int CONTENT_SEND_VIDEO = -3;
    public static final int ACTIVE_SPEAKER_COLOR = 0xff0055ff;    // orange
    public static final int NORMAL_PARTICIPANT_COLOR = 0x26ffffff;    // white
    public static final int SITE_NAME_COLOR = 0xffffffff;    // white
    public static final int VIDEO_ROTATE_0 = 0;
    public static final int VIDEO_ROTATE_90 = 90;
    public static final int VIDEO_ROTATE_180 = 180;
    public static final int VIDEO_ROTATE_270 = 270;
    private static final int LAYOUT_ANIMATION_DURATION = 800;
    protected CellRectView rectView;
    protected CellStateView cellStateView;
    protected OpenGLTextureView videoView;

    protected OnCellEventListener mCellEventListener = null;
    private ScaleGestureDetector mScaleGestureDetector;
    protected float fontHeightPix = 30;
    private SurfaceGestureListener mGestureListener = new SurfaceGestureListener();
    private GestureDetector mGestureDetector;
    private int mLastDragX, mLastDragY;

    private CachedLayoutPosition position = null;

    private VideoInfo layoutInfo = null;
    private boolean fullScreen = false, mDraged = false;
    private float scale = 1.0f;
    private float downSpan;
    private float downScale;
    private float transX;
    private float transY;
    private boolean isScaling = false;
    private boolean isUvcCamera;

    private int dragLeft, dragTop;
    private boolean flags;

    private ScaleGestureDetector.SimpleOnScaleGestureListener mSimpleOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float factorX = (downScale - 1) / transX;
            float factorY = (downScale - 1) / transY;
            scale = (detector.getCurrentSpan() - downSpan) / downSpan + scale;
            float currentTransX = (scale - 1) / factorX;
            float currentTransY = (scale - 1) / factorY;
            if (scale < 1) {
                scale = 1;
            } else if (scale > 3) {
                scale = 3;
            }

            if (scale == 1) {
                VideoCell.this.setTranslationX(0);
                VideoCell.this.setTranslationY(0);
            } else if (downScale != 1) {
                if (Math.abs(currentTransX) < Math.abs(transX)) {
                    if ((transX > 0 && currentTransX >= 0) || (transX < 0 && currentTransX <= 0)) {
                        VideoCell.this.setTranslationX(currentTransX);
                    } else {
                        VideoCell.this.setTranslationX(0);
                    }
                }

                if (Math.abs(currentTransY) < Math.abs(transY)) {
                    if ((transY > 0 && currentTransY >= 0) || (transY < 0 && currentTransY <= 0)) {
                        VideoCell.this.setTranslationY(currentTransY);
                    } else {
                        VideoCell.this.setTranslationY(0);
                    }
                }
            }


            VideoCell.this.setScaleX(scale);
            VideoCell.this.setScaleY(scale);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            transX = VideoCell.this.getTranslationX();
            transY = VideoCell.this.getTranslationY();
            downScale = scale;
            downSpan = detector.getCurrentSpan();
            isScaling = true;
            return true;
        }
    };

    public VideoCell(Context context) {
        super(context);
        init(false);
    }

    public VideoCell(boolean playCreateAnimation, Context context, OnCellEventListener mCellEventListener) {
        super(context);
        setCellEventListener(mCellEventListener);
        init(playCreateAnimation);
    }

    public VideoCell(boolean isUvc, boolean playCreateAnimation, Context context, OnCellEventListener mCellEventListener) {
        super(context);
        this.isUvcCamera = isUvc;
        setCellEventListener(mCellEventListener);
        init(playCreateAnimation);
    }

    public VideoCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(false);
    }

    private void initGesture() {
        mGestureDetector = new GestureDetector(getContext(), mGestureListener);
        mGestureDetector.setIsLongpressEnabled(true);
        mGestureDetector.setOnDoubleTapListener(mGestureListener);
    }

    public void init(boolean playCreateAnimation) {
        this.cellStateView = new CellStateView(getContext(), this);
        initGesture();
        initRectView();
        initVideoView();

        addView(cellStateView);

        setClipChildren(false);

        if (cellStateView != null) {
            cellStateView.bringToFront();
        }
        if (rectView != null) {
            rectView.bringToFront();
        }

        if (playCreateAnimation) {
            final TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
            animation.setDuration(LAYOUT_ANIMATION_DURATION);
            this.startAnimation(animation);
        }
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), mSimpleOnScaleGestureListener);
    }


    protected void initRectView() {
        rectView = new CellRectView(getContext());
        addView(rectView);
        setRectColor(NORMAL_PARTICIPANT_COLOR);
    }

    protected void initVideoView() {
        videoView = new OpenGLTextureView(getContext());
        addView(videoView);
    }

//    public void updateCamrea(boolean isUvc)
//    {
//        if(videoView!=null){
//            videoView.updateCamrea(isUvc);
//        }
//    }

    public void onResume() {
        if (videoView != null) videoView.onResume();
    }

    public void onPause() {
        if (videoView != null) videoView.onPause();
    }

    public OpenGLTextureView getVideoView() {
        return videoView;
    }

    public void setRectColor(int color) {
        if (rectView != null) {
            rectView.setRectColor(color);
            rectView.invalidate();
        }
    }

    public void setRectVisible(boolean visible) {
        if (rectView != null) {
            rectView.setVisibility(visible ? VISIBLE : GONE);
            layoutSelf();
        }
    }

    public void shake() {
        TranslateAnimation animation = new TranslateAnimation(0, -5, 0, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setInterpolator(getContext(), android.R.interpolator.accelerate_quint);
        animation.setDuration(20);
        animation.setRepeatCount(30);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
                mCellEventListener.onShakeDone(VideoCell.this);
            }
        });
        startAnimation(animation);

    }

    public void setCellEventListener(OnCellEventListener mCellEventListener) {
        this.mCellEventListener = mCellEventListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (fullScreen) {
            mScaleGestureDetector.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isScaling = false;
                mLastDragX = (int) event.getRawX();
                mLastDragY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isScaling) {
                    float deltaX = event.getRawX() - mLastDragX + VideoCell.this.getTranslationX();
                    float deltaY = event.getRawY() - mLastDragY + VideoCell.this.getTranslationY();
                    if (fullScreen && scale > 1) {
                        if (Math.abs(deltaX) < VideoCell.this.getWidth() * (scale - 1) / 2) {
                            VideoCell.this.setTranslationX(deltaX);
                        }

                        if (Math.abs(deltaY) < VideoCell.this.getHeight() * (scale - 1) / 2) {
                            VideoCell.this.setTranslationY(deltaY);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastDragX = 0;
                mLastDragY = 0;
                break;
        }
        mGestureDetector.onTouchEvent(event);

        // TODO: 16/5/17
//        if (VideoGroupView.isShowWhiteBoard()) {
//            return !isFullScreen();
//        } else {
//            return true;
//        }
        return true;
    }

    public int getDragLeft() {
        return dragLeft;
    }

    public void setDragLeft(int dragLeft) {
        this.dragLeft = dragLeft;
    }

    public int getDragTop() {
        return dragTop;
    }

    public void setDragTop(int dragTop) {
        this.dragTop = dragTop;
    }

    public VideoInfo getLayoutInfo() {
        return layoutInfo;
    }

    public void setLayoutInfo(VideoInfo layoutInfo) {
        this.layoutInfo = layoutInfo;
        if (layoutInfo != null) {
            Log.i(TAG, "getLayoutVideoStateFlage chenshuliang1935 setLayoutInfo22 ==" + layoutInfo);
            cellStateView.setProfileName(layoutInfo.getRemoteName());
            // cellStateView.setDeviceType(layoutInfo.getRemoteType());
            //cellStateView.setProfilePicture(layoutInfo.getRemotePicture());

            // cellStateView.setAddotherState(layoutInfo.getLayoutVideoState(), layoutInfo.getRemoteType());

            cellStateView.setLoading(isLodingState());
            cellStateView.setNoVideo(layoutInfo.getLayoutVideoState(), isNoVideoState(), layoutInfo.getVideoMuteReason());

            cellStateView.setAudioOnly(layoutInfo.getLayoutVideoState().equals(Enums.LAYOUT_STATE_AUDIO_ONLY) ||
                    layoutInfo.getLayoutVideoState().equals(Enums.LAYOUT_STATE_RECEIVED_AUDIO_ONLY));

            Log.i(TAG, "CommitInfo setAudioOnlyModeCSL11==" + layoutInfo.getLayoutVideoState() + "==LAYOUT_STATE_AUDIO_ONLY==" + Enums.LAYOUT_STATE_AUDIO_ONLY);
            Log.i(TAG, "CommitInfo setAudioOnlyModeCSL22==" + layoutInfo.getLayoutVideoState() + "==LAYOUT_STATE_RECEIVED_AUDIO_ONLY==" + Enums.LAYOUT_STATE_RECEIVED_AUDIO_ONLY);


            cellStateView.setUsingPSTN(layoutInfo.getLayoutVideoState() == Enums.LAYOUT_STATE_TELEPHONE);

            setMuteVideo(layoutInfo.isVideoMute(), layoutInfo.getVideoMuteReason());
            setMuteAudio(layoutInfo.isAudioMute());

            cellStateView.setObserverMode(layoutInfo.getLayoutVideoState() == Enums.LAYOUT_STATE_OBSERVING);

            if (layoutInfo.getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER ||
                    layoutInfo.getLayoutVideoState() == Enums.LAYOUT_STATE_OBSERVING ||
                    layoutInfo.getLayoutVideoState() == Enums.LAYOUT_STATE_TELEPHONE) {
                videoView.setVisibility(GONE);
            } else {
                videoView.setVisibility(VISIBLE);
            }
            if (videoView != null) {
                videoView.setSourceID(layoutInfo.getDataSourceID());
                videoView.setContent(layoutInfo.isContent());
            }
        }
    }

    private boolean isLodingState() {
        if (layoutInfo != null) {
            return layoutInfo.getLayoutVideoState().equals(Enums.LAYOUT_STATE_REQUESTING) ||
                    layoutInfo.getLayoutVideoState().equals(Enums.LAYOUT_STATE_IDLE);
        }
        return false;
    }

    private boolean isNoVideoState() {
        if (layoutInfo != null) {
            return layoutInfo.getLayoutVideoState().equals(Enums.LAYOUT_STATE_NO_DECODER) ||
                    layoutInfo.getLayoutVideoState().equals(Enums.LAYOUT_STATE_NO_BANDWIDTH) ||
                    layoutInfo.getLayoutVideoState().equals(Enums.LAYOUT_STATE_MUTE);
        }
        return false;
    }

    public void setMuteVideo(boolean mute, String reason) {

        cellStateView.setMuteVideo(mute, reason);
        if (layoutInfo != null) {
            layoutInfo.setVideoMute(mute);
        }
    }

    public void setMuteAudio(boolean mute) {
        cellStateView.setMuteAudio(mute);
        if (layoutInfo != null) {
            layoutInfo.setAudioMute(mute);
        }
    }

    public void setAudioOnly(boolean flag) {
        flags = flag;
        Log.i(TAG, "CommitInfo  setAudioOnlyModeCSL33==" + flag);
        cellStateView.setAudioOnly(flag);
        if (layoutInfo != null) {
            Log.i(TAG, "CommitInfo setAudioOnlyMode44==" + flag);
            layoutInfo.setAudioOnly(flag);
            Log.i(TAG, "CommitInfo setAudioOnlyMode55==" + flag);
        }
    }

    public void setAudioReceived(boolean flag) {

    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (position == null) {
            position = new CachedLayoutPosition(l, t, r, b);
        } else {
            position.setVals(l, t, r, b);
        }


        if (scale != 1) {
            scale = 1;
        }
        VideoCell.this.setScaleX(1.0f);
        VideoCell.this.setScaleY(1.0f);
        VideoCell.this.setTranslationY(0);
        VideoCell.this.setTranslationX(0);

        Log.d("VideoCell", "onlayout r-l" + (r - l) + "   b-t" + (b - t));
        rectView.layout(0, 0, r - l, b - t);

        int borderWidth = rectView.getBorderWidth();
        if (cellStateView != null) {
            cellStateView.layout(borderWidth, borderWidth, r - l - borderWidth, b - t - borderWidth);
        }
        if (videoView != null) {
            videoView.layout(borderWidth, borderWidth, r - l - borderWidth, b - t - borderWidth);
        }
    }

    @SuppressLint("WrongCall")
    protected void layoutSelf() {
        if (position != null) {
            onLayout(true, position.getL(), position.getT(), position.getR(), position.getB());
            invalidate();
        } else {
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            View child = getChildAt(childIndex);
            child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
        }
    }

    public CellRectView getRectView() {
        return rectView;
    }

    public void requestRender() {
        if (videoView != null) videoView.requestRender();
    }

    public boolean isDraged() {
        return mDraged;
    }

    public void setDraged(boolean mDraged) {
        this.mDraged = mDraged;
    }

    @Override
    public void onClickCancelAddother(CellStateView state) {
        // TODO Auto-generated method stub
        mCellEventListener.onCancelAddother(VideoCell.this);
    }

    public interface OnCellEventListener {
        void onLongPress(MotionEvent e, VideoCell cell);

        boolean onDoubleTap(MotionEvent e, VideoCell cell);

        boolean onSingleTapConfirmed(MotionEvent e, VideoCell cell);

        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, VideoCell cell);

        void onShakeDone(VideoCell cell);

        void onCancelAddother(VideoCell cell);
    }

    public class SurfaceGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float dx = e2.getRawX() - mLastDragX;
            float dy = e2.getRawY() - mLastDragY;
            boolean result = (mCellEventListener != null) ? mCellEventListener.onScroll(e1, e2, dx, dy, VideoCell.this) : false;
            mLastDragX = (int) e2.getRawX();
            mLastDragY = (int) e2.getRawY();
            return result;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return (mCellEventListener != null) ? mCellEventListener.onDoubleTap(e, VideoCell.this) : false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return (mCellEventListener != null) ? mCellEventListener.onSingleTapConfirmed(e, VideoCell.this) : false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mCellEventListener != null) {
                mCellEventListener.onLongPress(e, VideoCell.this);
            }
        }

    }

    public void updateCamrea(boolean isUvc)
    {
        if(videoView!=null){
            videoView.updateCamrea(isUvc);
        }
    }
}