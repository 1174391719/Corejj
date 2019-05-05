package com.zyzxsp.view;

import android.content.Context;
import android.graphics.Rect;
import android.log.L;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ainemo.module.call.data.Enums;
import com.ainemo.sdk.otf.VideoInfo;
import com.ainemo.shared.MediaSourceID;
import com.zyzxsp.CallState;
import com.zyzxsp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VideoGroupView extends ViewGroup {
    private static final String TAG = "VideoGroupView";

    // video
    private static final int THUMB_CELL_COUNT = 5;
    private static final int EMPTY_PART_ID = 0;
    private static final int LAYOUT_ANIMATION_DURATION = 400;
    public long requestRenderFramerate = 15;

    //UI views
    protected volatile VideoCell mLocalVideoCell;
    private Handler handler = new Handler();
    //UI data
    private VideoInfo mLocalCellLayoutInfo;
    private int initCellWidth, initCellHeight, mCellWidth, mCellHeight, mCellPadding, mThumbCellTop;
    private volatile boolean mLocalFullScreen = true;

    private boolean mForceLayoutMode = false;
    private int mForceLayoutParticipantId;

    private boolean needCreateAnimation = false;
    private Rect mScreenRect = new Rect();
    private volatile int animatingCount = 0;
    private volatile boolean isPause = false;
    // private UserActionListener actionListener;
    private boolean mShowContent = false;
    private boolean mReceiveContent = false;
    private boolean mContenInCorner = false;
    private boolean mHasVideoContent = false;
    private volatile boolean mFirstAniFinished;
    private Context mContext;


    private OnClickListener mFrameCellClickListener;
    private OnLongClickListener mFrameCellLongClickListener;
    private GestureDetector.OnDoubleTapListener mFrameCellDoubleTapListener;
    private VolumeRequester mVolumeListener;
    private ForceLayoutListener mForceLayoutListener;
    private BGCellLayoutInfoListener mBGCellLayoutInfoListener;
    private ContentModeListener mSetContentModeListener;
    private boolean isRectClicked = false;

    //whiteboard
    // private WhiteBoardCell mWhiteBoardCell;
    private static boolean mShowWhiteBoard;
    private boolean mWhiteBoardInCorner = false;
    private GestureDetector mGestureDetector;

    private boolean mReciveContentInCorner = false;

    private CallState.LayoutStatus mLayoutStatus = CallState.LayoutStatus.LOCAL;
    private volatile List<VideoCell> mThumbCells = new CopyOnWriteArrayList<VideoCell>();
    private volatile List<VideoInfo> mCachedLayoutInfos = null;
    //private ContentCell mContentCell;

    private Rect cachedThembCell1Rect = new Rect();  // 双流时，记住第一个小窗口位置

    private Runnable requestRenderRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLocalVideoCell != null && mLayoutStatus != CallState.LayoutStatus.OBSERVER) {
                Log.i(TAG,"mLocalVideoCellCSL=="+mLocalVideoCell+"==mLayoutStatus=="+mLayoutStatus+"==LayoutStatus.OBSERVER=="+ CallState.LayoutStatus.OBSERVER);
                if (!showContent() && !mShowWhiteBoard) {
                    mLocalVideoCell.requestRender();
                }
            }
            for (VideoCell cell : mThumbCells) {
                cell.requestRender();
            }
            requestRender(true);
        }
    };

    private boolean closeThumbCell = false;
    private boolean isOnHoldMode;

    public VideoGroupView(Context context) {
        super(context);
    }

    public VideoGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLayoutStatus(CallState.LayoutStatus status) {
        L.i("layout status changed, to:" + status);
        // mLayoutStatus = status;
        // updateCellSizeWithState();
        requestLayout();
    }

    public static interface VideoGroupListener {
        void showWhiteboardToolBar(boolean show);

        void onWhiteboardStateChanged(boolean show);
    }

    public void setListener(VideoGroupListener listener) {
//        mListener = listener;
    }

//    public void setActionListener(UserActionListener actionListener) {
//        this.actionListener = actionListener;
//    }


    public void setContext(Context context) {
        mContext = context;
    }

    private void updateCellSizeWithState() {
        Log.i(TAG, "==mLayoutStatus22==" + mLayoutStatus + "==P2P_NO_HARD==" + CallState.LayoutStatus.P2P_NO_HARD);
        if (mLayoutStatus == CallState.LayoutStatus.P2P_NO_HARD) {
            Log.i(TAG, "==mLayoutStatus==" + mLayoutStatus + "==P2P_NO_HARD==" + CallState.LayoutStatus.P2P_NO_HARD);
            mCellWidth = initCellWidth;
            mCellHeight = initCellHeight;
        } else {
            mCellWidth = initCellHeight;
            mCellHeight = initCellWidth;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int largeSide = Math.max(w, h);
        initCellHeight = (largeSide - mCellPadding) / THUMB_CELL_COUNT - mCellPadding;
        initCellWidth = initCellHeight / 4 * 3;

        updateCellSizeWithState();
        requestLayout();
    }

    public void init() {
        //background color
        setBackgroundColor(getResources().getColor(R.color.no_video_bg));
        // init resuorce values
        mCellPadding = (int) getResources().getDimension(R.dimen.local_cell_pandding);
        setClipChildren(false);
        // init fixed video components
        updateCellSizeWithState();
        createLocalCell(false);
//        mContentCell = new ContentCell(mContext, new SimpleContentCellEventListener());
//        addView(mContentCell.getCellLayout());
//        mContentCell.getCellLayout().setVisibility(View.GONE);



        mGestureDetector = new GestureDetector(mContext, mGestureListener);


    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            int winH = getHeight();
            if (winH <= 0 || isAnimating()) {
                return false;
            }

            if (mShowWhiteBoard && !mWhiteBoardInCorner) {
                return true;
            }

            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mFrameCellClickListener != null) {
                mFrameCellClickListener.onClick(null);
                return true;
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }
    };

    private void requestRender(boolean request) {
        handler.removeCallbacksAndMessages(null);
        if (request) {
            if (getVisibility() == VISIBLE) {
                handler.postDelayed(requestRenderRunnable, 1000 / requestRenderFramerate);
            }
        }
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        requestRender(visibility == VISIBLE);
        super.onVisibilityChanged(changedView, visibility);
    }

    public void destroy() {
        mThumbCells.clear();
        requestRender(false);
        //  mContentCell.onDestory();
    }

    @Override
    public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
        super.addOnLayoutChangeListener(listener);
    }


    public void onResume() {
        if (mLocalVideoCell != null) {
            mLocalVideoCell.onResume();
        }

        isPause = false;
        if (mCachedLayoutInfos != null) {
            L.i("chenshuliang1935 onResume layoutInfos:" + mCachedLayoutInfos);
            setLayoutInfo(mCachedLayoutInfos, mShowContent, mHasVideoContent);
        }

        for (VideoCell cell : mThumbCells) {
            cell.onResume();
        }

        // mContentCell.onResume();

        requestRender(true);
    }

    public void onPause() {
        if (mLocalVideoCell != null) {
            mLocalVideoCell.onPause();
        }
        for (VideoCell cell : mThumbCells) {
            cell.onPause();
        }
        // mContentCell.onPause();
        requestRender(false);
        isPause = true;
    }

    public boolean ismLocalFullScreen() {
        return mLocalFullScreen;
    }

    public synchronized void switchLocalViewToSmallCell() {

        L.i(TAG, "ResizeMoveAnimation switchLocalViewToSmallCell " + System.currentTimeMillis());
        if (mThumbCells.size() == 0 || isAnimating() || !mLocalFullScreen) {
//            updateAnimatingState(false);
            return;
        }

        updateAnimatingState(true);

        final View thumbView = mThumbCells.get(0);
        final View mainView = mLocalVideoCell;
        thumbView.setVisibility(VISIBLE);
        if (closeThumbCell) {
            mainView.setVisibility(GONE);
        } else {
            mainView.setVisibility(VISIBLE);
        }
        mLocalFullScreen = false;
        updateAnimatingState(false);
        mainView.bringToFront();
        requestLayout();

    }

    public void setLocalFullScreen(final boolean fullScreen, final boolean requestLayout) {
        if (mThumbCells.size() == 0 || isAnimating()) {
//            updateAnimatingState(false);
            return;
        }
        L.d(" kunkka ResizeMoveAnimation22 layoutinfo--setLocalFullScreen, fullScreen:" + fullScreen);
        // updateAnimatingState(true);
        final View thumbView = fullScreen ? mLocalVideoCell : mThumbCells.get(0);
        final View mainView = fullScreen ? mThumbCells.get(0) : mLocalVideoCell;
        mLocalFullScreen = fullScreen;
        updateAnimatingState(false);
        mForceLayoutListener.notificationLockPeople(true, mLocalFullScreen, false);
        mainView.bringToFront();
        if (requestLayout) {
            requestLayout();
        }

    }

    public void removeAddotherByLayoutInfo(VideoInfo layoutInfo) {
        VideoCell removelayoutInfo = null;
        for (VideoCell cell : mThumbCells) {
            if (cell.getLayoutInfo().getRemoteID().equalsIgnoreCase(layoutInfo.getRemoteID())) {
                removelayoutInfo = cell;
            }
        }

        if (removelayoutInfo != null) {
            removelayoutInfo.setLayoutInfo(layoutInfo);
            removelayoutInfo.shake();
        }
    }

    public void removeAddotherCell(VideoInfo layoutInfo) {
        VideoCell removelayoutInfo = null;
        for (VideoCell cell : mThumbCells) {
            if (cell.getLayoutInfo().getRemoteID().equalsIgnoreCase(layoutInfo.getRemoteID())) {
                removelayoutInfo = cell;
            }
        }

        if (removelayoutInfo != null) {
            removelayoutInfo.setLayoutInfo(layoutInfo);
            removeView(removelayoutInfo);
            mThumbCells.remove(removelayoutInfo);
        }
    }

    private void relayout(boolean hideMainView) {
        L.i("relayout:hide=" + hideMainView + " localFullScreen="
                + mLocalFullScreen);
        mReceiveContent = hideMainView;
    }




    private boolean showContent() {
        return mShowContent && mReceiveContent;
    }




    private boolean checkOnlyAddOther(List<VideoInfo> layoutInfos) {
        if (layoutInfos != null) {
            for (VideoInfo info : layoutInfos) {
                if (info.getLayoutVideoState() != Enums.LAYOUT_STATE_ADDOTHER) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized void setLayoutInfo(List<VideoInfo> layoutInfos, boolean showContent, boolean hasVideoContent) {
        if (layoutInfos == null) {
            return;
        }

        mCachedLayoutInfos = layoutInfos;
        mHasVideoContent = hasVideoContent;

        if (isPause) {
            return;
        }


        if (mThumbCells.isEmpty() && mLocalFullScreen && !checkOnlyAddOther(mCachedLayoutInfos)) {
            mLocalFullScreen = false;
        }

        if (mCachedLayoutInfos.isEmpty()) {
            mLocalFullScreen = true;
        }

        mShowContent = showContent;
        if (mShowContent && mLayoutStatus != CallState.LayoutStatus.OBSERVER) {
            //  showCRXLoadingView();
        } else {
            // hideCRXView();
        }
        L.i(TAG, "setLayoutInfo mLayoutStatus:" + mLayoutStatus);
        if (mHasVideoContent) { // Dual stream, hide localpreview
            mLocalVideoCell.setVisibility(GONE);
        } else if (mLayoutStatus == CallState.LayoutStatus.OBSERVER) { // Observer mode, ignore svc cells
            mLocalVideoCell.setVisibility(GONE);
            mCachedLayoutInfos = mCachedLayoutInfos.subList(0, 1);
        } else {
            mLocalVideoCell.setVisibility(VISIBLE);
        }

        // 双流时，记住小窗口位置
        if (mHasVideoContent && mThumbCells.size() > 1 && !mThumbCells.get(1).isFullScreen() && mThumbCells.get(1).isDraged()) {
            VideoCell cell1 = mThumbCells.get(1);
            cachedThembCell1Rect.set(cell1.getLeft(), cell1.getTop(), cell1.getRight(), cell1.getBottom());
        }

        // Delete cells
        List<VideoCell> toDel = new ArrayList<>();
        l:
        for (VideoCell cell : mThumbCells) {
            for (VideoInfo info : mCachedLayoutInfos) {
                if (cell.getLayoutInfo().getParticipantId() == info.getParticipantId()) {
                    continue l;
                }
            }
            if (!MediaSourceID.SOURCE_ID_LOCAL_PREVIEW.equals(cell.getLayoutInfo().getDataSourceID())
                    && cell.getLayoutInfo().getLayoutVideoState() != Enums.LAYOUT_STATE_ADDOTHER_FAILED
                    && cell.getLayoutInfo().getLayoutVideoState() != Enums.LAYOUT_STATE_ADDOTHER_FAILED) {
                toDel.add(cell);
            }
        }
        for (VideoCell cell : toDel) {
            removeView(cell);
            mThumbCells.remove(cell);

            if (mForceLayoutMode && cell.getLayoutInfo().getParticipantId() == mForceLayoutParticipantId) {
                boolean isMute = cell.getLayoutInfo() == null ? true : cell.getLayoutInfo().isVideoMute();
                mForceLayoutListener.notificationLockPeople(!showContent, mLocalFullScreen, isMute);
                mForceLayoutMode = false;
                mForceLayoutParticipantId = 0;

            }
        }
        toDel = null;

        for (int i = 0; i < mCachedLayoutInfos.size(); i++) {
            VideoInfo info = mCachedLayoutInfos.get(i);
            if (i < mThumbCells.size()) { // 这个位置是否有cell
                if (mThumbCells.get(i).getLayoutInfo().getParticipantId() == info.getParticipantId()) {
                    Log.i(TAG,"getLayoutVideoState mThumbCells="+info);
                    mThumbCells.get(i).setLayoutInfo(info);
                    continue; // 位置不变
                } else {
                    // 找到这个cell，挪到当前位置，如果找不到，创建一个
                    int position = - 1;
                    VideoCell  cell = null;
                    for (int j = 0; j < mThumbCells.size(); j++) {
                        VideoCell jv = mThumbCells.get(j);
                        if (jv.getLayoutInfo().getParticipantId() == info.getParticipantId()) {
                            position = j;
                            cell = jv;
                            break;
                        }
                    }

                    Log.i(TAG, "setLayoutInfo cell=" + cell);
                    if (cell == null) {
                        cell = createRemoteCell(info, i != 0 && needCreateAnimation && !closeThumbCell);
                        mThumbCells.add(i, cell);
                    } else {
                        Log.i(TAG,"getLayoutVideoState info="+info);
                        cell. setLayoutInfo(info);
                        Log.i(TAG, "setLayoutInfo cell=" + cell.getLeft() + " " + cell.getTop() + " " + cell.getRight() + " " + cell.getBottom());
                        Log.i(TAG, "setLayoutInfo cell=" + mThumbCells.get(0).getLeft() + " " + mThumbCells.get(0).getTop()
                                + " " + mThumbCells.get(0).getRight() + " " + mThumbCells.get(0).getBottom());

                        int poindex = mThumbCells.indexOf(cell);

                        if (i == 0 && !mLocalFullScreen && !showContent() && !mContenInCorner && !mShowWhiteBoard && !mWhiteBoardInCorner) {

                            Collections.swap(mThumbCells, 0, mThumbCells.indexOf(cell));

                            Log.i(TAG, "setLayoutInfo cell= poindex:" + poindex);
                            Log.i(TAG, "setLayoutInfo cell=  swfcell" + cell.getLeft() + " " + cell.getTop() + " " + cell.getRight() + " " + cell.getBottom());
                            Log.i(TAG, "setLayoutInfo cell=  swf0" + mThumbCells.get(0).getLeft() + " " + mThumbCells.get(0).getTop()
                                    + " " + mThumbCells.get(0).getRight() + " " + mThumbCells.get(0).getBottom());

                            Log.i(TAG, "setLayoutInfo cell=  swf poindex=" + poindex + "  " + mThumbCells.get(poindex).getLeft() + " " + mThumbCells.get(poindex).getTop()
                                    + " " + mThumbCells.get(poindex).getRight() + " " + mThumbCells.get(poindex).getBottom());
                        } else if (!mLocalFullScreen && i == 0 && ((showContent && !mContenInCorner) || (mShowWhiteBoard && !mWhiteBoardInCorner))) {  // 位置互换
                            VideoCell index0 = mThumbCells.get(0);
                            mThumbCells.set(0, cell);
                            mThumbCells.set(position, index0);
                        }
                    }
                }
            } else { // 直接创建一个cell
                VideoCell cell = createRemoteCell(info, i != 0 && needCreateAnimation && !closeThumbCell);
                mThumbCells.add(cell);
            }
        }

        checkPIP();

//        if (!showContent() && !mShowWhiteBoard && !hasVideoContent) {
//            animateSmallCells(mCachedLayoutInfos);
//        }
        needCreateAnimation = !hasVideoContent; // dual stream, stop small cell coming animation.

        requestLayout();
    }

    private synchronized boolean hasScreenVideo() {
        if (mThumbCells.size() > 1) {
            for (int i = 1; i < mThumbCells.size(); i++) {

                VideoCell cell = mThumbCells.get(i);
                if (cell.isFullScreen()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void checkPIP() {
        if (closeThumbCell) {
            if (mLocalFullScreen) {
                mLocalVideoCell.setVisibility(VISIBLE);
                for (VideoCell vc : mThumbCells) {
                    vc.setVisibility(INVISIBLE);
                }
            } else {
                mLocalVideoCell.setVisibility(INVISIBLE);
                for (int i = 0; i < mThumbCells.size(); i++) {
                    VideoCell vc = mThumbCells.get(i);
                    vc.setVisibility(i == 0 ? VISIBLE : INVISIBLE);
                }
            }
        } else {
            mLocalVideoCell.setVisibility(mLayoutStatus == CallState.LayoutStatus.OBSERVER ? INVISIBLE : VISIBLE);
            for (VideoCell vc : mThumbCells) {
                vc.setVisibility(VISIBLE);
            }
        }
    }

    private void animateSmallCells(List<VideoInfo> layoutInfos) {
        for (int i = 0; i < layoutInfos.size(); i++) {
            if (!mLocalFullScreen && i == 0) {
                continue;
            }
            VideoInfo info = layoutInfos.get(i);
            int cellIndex = getCellIndex(info);
            if (i != cellIndex && cellIndex > -1) {
                //move
//                animateCellToIndex(mThumbCells.get(cellIndex), cellIndex, i);
                VideoCell cell = mThumbCells.get(cellIndex);
                if (mThumbCells.size() > cellIndex && mThumbCells.size() > i) {
                    mThumbCells.remove(cell);
                    mThumbCells.add(cellIndex, cell);
                    requestLayout();
                }
            }
        }
    }

    private int getCellIndex(VideoInfo info) {

        for (int i = 0; i < mThumbCells.size(); i++) {
            if (info.getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER || info.getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER_FAILED) {
                if (mThumbCells.get(i).getLayoutInfo().getRemoteID().equalsIgnoreCase(info.getRemoteID())) {
                    return i;
                }
            } else if (mThumbCells.get(i).getLayoutInfo().getParticipantId() == info.getParticipantId()) {
                return i;
            }
        }
        L.d("layoutinfo" + info + "--getCellIndex index is -1!!");
        return -1;
    }



    public void setFrameCellClickListener(OnClickListener listener) {
        this.mFrameCellClickListener = listener;
    }

    public void setFrameCellLongClickListener(OnLongClickListener listener) {
        this.mFrameCellLongClickListener = listener;
    }

//    public void setFrameCellDoubleTapListener(GestureDetector.OnDoubleTapListener listener) {
//        this.mFrameCellDoubleTapListener = listener;
//    }

    public void setMuteLocalVideo(boolean mute, String reason) {
        mLocalVideoCell.setMuteVideo(mute, reason);
    }

    public void setLocalLayoutInfo(VideoInfo localCellLayoutInfo) {
        this.mLocalCellLayoutInfo = localCellLayoutInfo;
        Log.i(TAG,"getLayoutVideoState setLocalLayoutInfo"+mLocalCellLayoutInfo);
        mLocalVideoCell.setLayoutInfo(mLocalCellLayoutInfo);
    }

    public void setMuteLocalAudio(boolean mute) {
        mLocalVideoCell.setMuteAudio(mute);
    }

    public void setAudioOnlyMode(boolean flag) {
        Log.i(TAG,"CellStateViewCSL setAudioOnlyMode11=="+flag);
        mLocalVideoCell.setAudioOnly(flag);
    }


    public void moveThumbCellsToOriginal() {
        if (mLocalFullScreen) {
            int mThumbCellLeft = mCellPadding;
            if (mThumbCells.size() > 0) {
                for (int i = 0; i < mThumbCells.size(); i++) {
                    VideoCell cell = mThumbCells.get(i);
                    cell.setDraged(false);
                    layoutThumbVideoCellToOrignal(cell, mThumbCellLeft, mThumbCellTop);
                    mThumbCellLeft += (mCellWidth + mCellPadding);
                }
            }
        } else {
            int mThumbCellLeft = mCellPadding;
            if (!mHasVideoContent) {
                layoutThumbVideoCellToOrignal(mLocalVideoCell, mThumbCellLeft, mThumbCellTop);
            }
            if (mThumbCells.size() > 1) {
                for (int i = 1; i < mThumbCells.size(); i++) {
                    VideoCell cell = mThumbCells.get(i);
                    cell.setDraged(false);
                    if (mHasVideoContent) {
                        mThumbCellLeft += (mCellPadding);
                    } else {
                        mThumbCellLeft += (mCellWidth + mCellPadding);
                    }
                    layoutThumbVideoCellToOrignal(cell, mThumbCellLeft, mThumbCellTop);
                }
            }
        }
    }



    private VideoCell createRemoteCell(VideoInfo layoutInfo, boolean playCreateAnimation) {
        VideoCell cell = new VideoCell(playCreateAnimation, getContext(), new SimpleCellEventListener());
        cell.setLayoutInfo(layoutInfo);
        cell.bringToFront();

        addView(cell);
        return cell;
    }

    private void createLocalCell(boolean isUvc) {
        mLocalVideoCell = new VideoCell(isUvc,false, getContext(), new SimpleCellEventListener());
        mLocalVideoCell.setId(VideoCell.LOCAL_VIEW_ID);
        mLocalVideoCell.bringToFront();
        mLocalVideoCell.setLayoutInfo(mLocalCellLayoutInfo);

        addView(mLocalVideoCell);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int measuredWidth = 1280;
        if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSpecSize;
        }

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int measuredHeight = 720;
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.EXACTLY) {
            measuredHeight = heightSpecSize;
        }

        mScreenRect.right = measuredWidth;
        mScreenRect.bottom = measuredHeight;
        setMeasuredDimension(measuredWidth, measuredHeight);
        // mWhiteBoardCell.measure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setForceLayout(VideoCell cell) {
        Log.i(TAG, "setForceLayout !");
        if (mForceLayoutListener != null) {
            mForceLayoutMode = true;
            mForceLayoutListener.setForceLayout(cell.getLayoutInfo().getParticipantId());
            mForceLayoutParticipantId = cell.getLayoutInfo().getParticipantId();
        }
    }

    public void setContentModeListener(ContentModeListener setContentModeListener) {
        this.mSetContentModeListener = setContentModeListener;
    }

    private void setContentMode(boolean isThumbnail) {
        if (mSetContentModeListener != null) {
            mSetContentModeListener.setContentMode(isThumbnail);
        }
    }


    private synchronized void updateAnimatingState(boolean animating) {

        Log.i(TAG, "before  animating:" + animating + "  animatingCount:" + animatingCount);
        this.animatingCount += animating ? 1 : -1;
        if (animatingCount < 0) {
            animatingCount = 0;
        }
        Log.i(TAG, "after  animating:" + animating + "  animatingCount:" + animatingCount);
//        this.removeCallbacks(resetAnimatingStateRunnable);
//        this.postDelayed(resetAnimatingStateRunnable, 2000);
    }

    private synchronized boolean isAnimating() {
        return animatingCount > 0;
    }


    private Rect getCellRect(final VideoCell thumbCell) {
        Rect mCellRect = new Rect(thumbCell.getLeft(), thumbCell.getTop(), thumbCell.getRight(), thumbCell.getBottom());
        return mCellRect;
    }


    protected synchronized void swapToMainCell(final VideoCell thumbView, final VideoCell mainView, final boolean forceLayout) {
        if (isAnimating()) {
            return;
        }
        L.i(TAG, "kunkka ResizeMoveAnimation swapToMainCell 走着。。。"
                + System.currentTimeMillis() + "==thumbView==" + thumbView.toString() +
                "==mainView==" + mainView.toString() + "==forceLayout==" + forceLayout);

        updateAnimatingState(true);
        updateAnimatingState(false);
        thumbView.setDraged(false);
        if (mThumbCells.indexOf(thumbView) > -1) {
            Collections.swap(mThumbCells, 0, mThumbCells.indexOf(thumbView));
        }
        mainView.bringToFront();
        requestLayout();
        if (forceLayout) {
            setForceLayout(thumbView);
        }

        mLocalFullScreen = false;

    }

    @Override
    protected synchronized void onLayout(boolean changed, int l, int t, int r, int b) {


        mThumbCellTop = (b - t) - mCellHeight - mCellPadding;

        if (mLocalFullScreen) {
              layoutFullScreenVideoCell(mLocalVideoCell, l, t, r, b);

            int mThumbCellLeft = mCellPadding;
            if (mThumbCells.size() > 0) {
                boolean firstHalfView = true;
                L.i(TAG, "mLocalFullScreen:true");
                for (int i = 0; i < mThumbCells.size(); i++) {
                    VideoCell cell = mThumbCells.get(i);
                    if (!cell.isFullScreen()) {
                        cell.bringToFront();
                    }

                    if (firstHalfView) {
                        layoutThumbVideoCell(cell, mThumbCellLeft, mThumbCellTop);
                        mThumbCellLeft += (mCellWidth / 2 + mCellPadding)/26;
                        firstHalfView = false;
                    } else {
                        layoutThumbVideoCell(cell, mThumbCellLeft, mThumbCellTop);
                        mThumbCellLeft += (mCellWidth / 2 + mCellPadding)/26;
                    }
                    layoutThumbVideoCell(cell, mThumbCellLeft, mThumbCellTop);
                    mThumbCellLeft += (mCellWidth + mCellPadding);

                }
            }
        } else {


            if (mThumbCells.size() > 0) {
                VideoCell cell = mThumbCells.get(0);
                layoutFullScreenVideoCell(cell, l, t, r, b);
            }

            int mThumbCellLeft = mCellPadding;
            layoutThumbVideoCell(mLocalVideoCell, l, r, mThumbCellLeft, mThumbCellTop);
            if (mThumbCells.size() > 1) {
                boolean firstHalfView = true;
                L.i(TAG, "mLocalFullScreen:false");
                L.i(TAG, "cell:getLayoutInfo:");
                for (int i = 1; i < mThumbCells.size(); i++) {
                    VideoCell cell = mThumbCells.get(i);
                    L.i(TAG, "VideoCellname" + cell.getLayoutInfo().getRemoteID() + "   i=" + mThumbCells.get(i).getRectView().getRight());

                    cell.bringToFront();
                    if (firstHalfView) {
                        mThumbCellLeft += (mCellWidth + mCellPadding)/28;
                        layoutThumbVideoCell(cell, mThumbCellLeft, mThumbCellTop);
                        firstHalfView = false;
                    } else {
                        mThumbCellLeft += (mCellWidth / 2 + mCellPadding)/26;
                        layoutThumbVideoCell(cell, mThumbCellLeft, mThumbCellTop);
                    }
                    mThumbCellLeft += (mCellWidth + mCellPadding);
                    layoutThumbVideoCell(cell, l, r, mThumbCellLeft, mThumbCellTop);

                }
            }
        }
    }


    public void setBGCellLayoutInfoListener(BGCellLayoutInfoListener mBGCellLayoutInfoListener) {
        this.mBGCellLayoutInfoListener = mBGCellLayoutInfoListener;
    }


    private void layoutFullScreenVideoCell(VideoCell cell, int l, int t, int r, int b) {
        Log.i(TAG,"layoutFullScreenVideoCell1=="+cell);
        if (cell == null) {
            Log.i(TAG,"layoutFullScreenVideoCell2=="+cell);
            if (mBGCellLayoutInfoListener != null) {
                mBGCellLayoutInfoListener.onChanged(null);
            }
        } else {
            Log.i(TAG,"layoutFullScreenVideoCell3=="+cell.getLayoutInfo());
            if (cell.getLayoutInfo() != null) {
                Log.i(TAG,"layoutFullScreenVideoCell4=="+cell.getLayoutInfo());
                if (mBGCellLayoutInfoListener != null) {
                    mBGCellLayoutInfoListener.onChanged(cell.getLayoutInfo());
                }
            }

            cell.setFullScreen(true);
            cell.setRectVisible(false);
            cell.setDraged(false);
            cell.layout(l, t, r, b);
        }

    }

    // 双流时，记住小窗口位置
    private void layoutThumbCellWhenDualStream(VideoCell cell, int left, int top) {
        cell.setRectVisible(true);
        cell.bringToFront();

        if (cell.isDraged() && !cell.isFullScreen()) { // 使用全屏 或 cell.getLeft()>0 均可
            cell.layout(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getBottom());
            cachedThembCell1Rect.set(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getBottom());
        } else {
            if (cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER ||
                    cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER_FAILED ||
                    cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_OBSERVING) {
                cell.layout(left, top, left + mCellWidth / 2, top + mCellHeight);
            } else {
                if (cachedThembCell1Rect.width() > 0) { // 获取缓存的小窗口位置
                    cell.layout(cachedThembCell1Rect.left, cachedThembCell1Rect.top, cachedThembCell1Rect.right, cachedThembCell1Rect.bottom);
                } else {
                    cell.layout(left, top, left + mCellWidth, top + mCellHeight);
                }
            }
        }
        cell.setFullScreen(false);
    }

    private void layoutThumbVideoCell(VideoCell cell, int left, int top) {
        cell.setRectVisible(true);
        cell.setFullScreen(false);
        cell.bringToFront();
        if (cell.isDraged()) {
            cell.layout(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getBottom());
        } else {

            if (cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER ||
                    cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER_FAILED ||
                    cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_OBSERVING) {

                cell.layout(left, top, left + mCellWidth / 2, top + mCellHeight);
            } else {
                cell.layout(left, top, left + mCellWidth, top + mCellHeight);
            }
        }
    }

    private void layoutThumbVideoCell(VideoCell cell, int l, int r, int left, int top) {
        cell.setRectVisible(true);
        cell.setFullScreen(false);
        cell.bringToFront();
        if (cell.isDraged()) {

            cell.layout(cell.getLeft(), cell.getDragTop(), cell.getLeft() + mCellWidth, cell.getDragTop() + mCellHeight);
        } else {

            if (cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER ||
                    cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER_FAILED ||
                    cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_OBSERVING) {

                cell.layout(left, top, left + mCellWidth / 2, top + mCellHeight);
            } else {
                cell.layout(left, top, left + mCellWidth, top + mCellHeight);
            }
        }
    }

    private void layoutThumbVideoCellToOrignal(VideoCell cell, int left, int top) {
        cell.setRectVisible(true);
        cell.setFullScreen(false);
        cell.setDraged(false);
        cell.bringToFront();
        cell.layout(left, top, left + mCellWidth / 2, top + mCellHeight);
        cell.layout(left, top, left + mCellWidth, top + mCellHeight);
        if (cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER ||
                cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER_FAILED ||
                cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_OBSERVING) {
            cell.layout(left, top, left + mCellWidth / 2, top + mCellHeight);
        } else {
            cell.layout(left, top, left + mCellWidth, top + mCellHeight);
        }

    }

    public void setRequestRenderFramerate(long requestRenderFramerate) {
        if (requestRenderFramerate > 0)
            this.requestRenderFramerate = requestRenderFramerate;
    }

    public void setForceLayoutListener(ForceLayoutListener forceLayoutListener) {
        this.mForceLayoutListener = forceLayoutListener;
    }


    public interface BGCellLayoutInfoListener {
        void onChanged(VideoInfo layoutInfo);
    }

    public interface ForceLayoutListener {
        void setForceLayout(int participantId);

        void notificationLockPeople(boolean isLockClick, boolean mLocalFullScreen, boolean isMute);

        void notificationMute(boolean isRemoteVideo, boolean isMute);
    }

    public interface ContentModeListener {
        void setContentMode(boolean isThumbnail);
    }

    private class SimpleCellEventListener implements VideoCell.OnCellEventListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e, VideoCell cell) {
            // Log.i(TAG, "onSingleTapConfirmed1=" + cell);
            if (cell.isFullScreen() || isOnHoldMode) {
                Log.i(TAG, "onSingleTapConfirmed2==" + cell.isFullScreen() + "==isOnHoldMode==" + isOnHoldMode);
                if (mFrameCellClickListener != null) {
                    mFrameCellClickListener.onClick(null);
                    return true;
                }
            } else {
                if (cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_OBSERVING ||
                        cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER_FAILED) {
                    return false;
                }

                if (cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER) {
                    cell.cellStateView.setCancleAddother(!cell.cellStateView.getCancelAddother());
                    return true;
                }

                if (isAnimating()) {
                    return false;
                }

                boolean isMute = (cell == null || cell.getLayoutInfo() == null) ? true : cell.getLayoutInfo().isVideoMute();
                if (cell.getId() == VideoCell.LOCAL_VIEW_ID) {
                    mForceLayoutListener.notificationLockPeople(true, mLocalFullScreen, false);
                    setLocalFullScreen(true, true);
                } else if (mLocalFullScreen) {
                    mForceLayoutListener.notificationLockPeople(true, mLocalFullScreen, isMute);
                    swapToMainCell(cell, mLocalVideoCell, true);
                } else {
                    mForceLayoutListener.notificationLockPeople(true, mLocalFullScreen, isMute);
                    swapToMainCell(cell, mThumbCells.get(0), true);
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e, VideoCell cell) {

            if (mShowWhiteBoard && cell.isFullScreen()) {
                return;
            }
            if (cell.getId() == VideoCell.LOCAL_VIEW_ID
                    || (!cell.isFullScreen() && (mShowContent || mHasVideoContent || mShowWhiteBoard))) {
            } else {
                if (mFrameCellLongClickListener != null) {
                    mFrameCellLongClickListener.onLongClick(null);
                }
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e, VideoCell cell) {
            if (mFrameCellDoubleTapListener != null) {
                if (cell.isFullScreen()) {
                    mFrameCellDoubleTapListener.onDoubleTap(e);
                }
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, VideoCell cell) {
            if (cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER ||
                    cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_ADDOTHER_FAILED ||
                    cell.getLayoutInfo().getLayoutVideoState() == Enums.LAYOUT_STATE_OBSERVING) {
                return true;
            }
            if (cell.isFullScreen()) {
                float mOldY = e1.getY();
                int y = (int) e2.getRawY();
                int windowHeight = Math.min(getWidth(), getHeight());
                windowHeight = (int) (windowHeight * 0.5);
                if (mVolumeListener != null) {
                    mVolumeListener.onVolumeSlide((mOldY - y) / windowHeight);
                    mVolumeListener.onVolumeSlideEnd();
                }
            } else {
                int left = cell.getLeft() + (int) distanceX;
                int top = cell.getTop() + (int) distanceY;
                int right = cell.getRight() + (int) distanceX;
                int bottom = cell.getBottom() + (int) distanceY;

                if (left < 0) {
                    left = 0;
                    right = left + cell.getWidth();
                }
                if (right > getWidth()) {
                    right = getWidth();
                    left = right - cell.getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + cell.getHeight();
                }
                if (bottom > getHeight()) {
                    bottom = getHeight();
                    top = bottom - cell.getHeight();
                }
                cell.setDraged(true);
                cell.layout(left, top, right, bottom);
                cell.setDragLeft(left);
                cell.setDragTop(top);
            }
            return true;
        }

        @Override
        public void onShakeDone(VideoCell cell) {
            removeView(cell);
            mThumbCells.remove(cell);
        }

        @Override
        public void onCancelAddother(VideoCell cell) {

        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mShowWhiteBoard) {  // 目前只有白板使用，为防止事件重复，不是白板直接return
            return true;
        }

        mGestureDetector.onTouchEvent(event);

        return true;
    }


    public static boolean isShowWhiteBoard() {
        return mShowWhiteBoard;
    }

    public void setOnHoldMode(boolean onHoldMode) {
        isOnHoldMode = onHoldMode;
    }

    public VideoCell getmLocalVideoCell() {
        return mLocalVideoCell;
    }

    public void updateCamera(boolean isUvc)
    {
        if(mLocalVideoCell!=null){
            mLocalVideoCell.updateCamrea(isUvc);
        }
    }
}
