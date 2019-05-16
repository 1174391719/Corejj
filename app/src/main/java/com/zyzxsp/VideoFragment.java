package com.zyzxsp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.log.L;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ainemo.module.call.data.Enums;
import com.ainemo.module.call.data.FECCCommand;
import com.ainemo.module.call.data.NewStatisticsInfo;
import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.NemoSDKErrorCode;
import com.ainemo.sdk.otf.NemoSDKListener;
import com.ainemo.sdk.otf.RecordingState;
import com.ainemo.sdk.otf.VideoInfo;
import com.ainemo.shared.MediaSourceID;
import com.ainemo.shared.UserActionListener;
import com.zyzxsp.myInterface.CallListener;
import com.zyzxsp.utils.AlertUtil;
import com.zyzxsp.utils.CommonTime;
import com.zyzxsp.utils.VolumeManager;
import com.zyzxsp.utils.ZLog;
import com.zyzxsp.view.CallStatisticsView;
import com.zyzxsp.view.Dtmf;
import com.zyzxsp.view.StatisticsRender;
import com.zyzxsp.view.VideoGroupView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.zyzxsp.fragment.HomeFragment.CLOSE_CAMERA;
import static com.zyzxsp.fragment.HomeFragment.CLOSE_VOICE;


/**
 * 通话界面
 */
@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class VideoFragment extends Fragment implements CallListener,
        View.OnClickListener, VideoGroupView.ForceLayoutListener, VideoGroupView.BGCellLayoutInfoListener, VolumeManager.MuteCallback {
    private static final String TAG = "VideoFragment";
    //更新统计信息延迟
    private final static int REFRESH_STATISTICS_INFO_DELAYED = 2000;
    //音视频源
    private VideoGroupView mVideoView;
    //共享content
    private ImageView mContent;
    //底部导航
    private LinearLayout bottomNavigation;
    //头部导航
    private LinearLayout topNavigation;
    //视频界面功能隐藏\显示控制
    private LinearLayout videoFunctionHideShow;
    //视频容器
    private RelativeLayout videoContainer;
    //外部容器
    private RelativeLayout outgoingContainer;
    //摄像头隐藏\显示控制
    private LinearLayout switchCameraHideShow;
    //切换摄像头控件
    private ImageButton mSwitchCamera;
    //取消当前通话
    private ImageButton mButtonCancel;
    //关闭视频控件
    private ImageButton mCloseVideo;
    //关闭视频标签
    private TextView mCloseVideoLabel;
    //听筒模式控件
    private ImageButton mSwitchSpeakerMode;
    //听筒模式模标签
    private TextView mSwitchSpeakerLabel;
    //挂断当前通话
    private RelativeLayout mDropCall;
    //麦克风容器
    private RelativeLayout mMicContainer;
    //麦克风控件
    private ImageButton mMuteMicBtn;
    //关闭、打开本地麦克风标签
    private TextView mMuteMicLabel;
    //录制控件
    private ImageButton mRecordingVideoBtn;
    //录制标签
    private TextView mRecordingVideoLabel;
    //录制计时
    private TextView mRecordingTimer;
    //录制隐藏\显示控制
    private LinearLayout mRecordingTimerHideShow;
    //录制icon闪烁
    private ImageView mFlashView;
    //语音模式控件
    private ImageButton mAudioOnlyBtn;
    //语音模式标签
    private TextView mAudioOnlyLabel;
    //网络质量显示控件
    private ImageView mNetworkState;
    //会议计时
    private TextView mMeetingTimer;
    //呼叫会议号
    private TextView mCallMeetingNumber;
    //统计信息
    private CallStatisticsView callStatisticsView;
    //更多控件
    // private ImageButton mMeetingMore;
    //会议键盘对话框
    private LinearLayout moreDialog;
    //键盘
    private TextView keyboard;
    //双音多频控件
    private RelativeLayout dtmfLayout;
    //双音多频自定义按键
    private Dtmf dtmf;
    //双音多频按键放置（位置）
    private LinearLayout dtmfLay;
    //锁屏
    private LinearLayout lockPeople;
    //统计渲染
    private StatisticsRender mStatisticsRender;
    //举手容器
    private RelativeLayout mHandupContainer;
    //举手按钮控件
    private ImageButton mHandupBtn;
    //举手标签
    private TextView mHandupLabel;
    /*统计*/
    private Button mStats;

    /**
     * FECC
     **/
    //向左远端摄像机控制
    private ImageButton mFeccLeftBtn;
    //向右远端摄像机控制
    private ImageButton mFeccRightBtn;
    //向上远端摄像机控制
    private ImageButton mFeccUpBtn;
    //向下远端摄像机控制
    private ImageButton mFeccDownBtn;
    //远端摄像机控制
    private LinearLayout mFeccControl;
    //远端摄像机控制默认背景
    private ImageView mFeccControlBg;
    //远端摄像机控制向左背景
    private ImageView mFeccControlBgLeft;
    //远端摄像机控制向右背景
    private ImageView mFeccControlBgRight;
    //远端摄像机控制向上背景
    private ImageView mFeccControlBgUp;
    //远端摄像机控制向下背景
    private ImageView mFeccControlBgDown;
    //远端摄像机控制圆盘view
    private ImageView mFeccPanView;
    //放大+
    private ImageView zoomInAdd;
    //放在倍数
    private ImageView zoomInPlus;
    //默认水平方向远端摄像机控制为false
    private boolean feccHorizontalControl = false;
    //默认垂直方向远端摄像机控制为false
    private boolean feccVerticalControl = false;
    //发送指令
    private int lastFeccCommand = UserActionListener.USER_ACTION_FECC_STOP;
    //用户使用监听
    private UserActionListener actionListener;
    //数据源
    private VideoInfo videoInfo = null;
    //布局是否隐藏
    private boolean mVisible = true;
    //初始化handler
    private Handler handler = new Handler();
    //前置摄像头默认为true
    private boolean foregroundCamera = true;
    //0:后置 1:前置
    private int cameraId = 1;
    //true时为语音通话， false为视频通话
    private boolean audioMode = false;
    //true 关闭视频，false 打开视频
    private boolean videoMute = false;
    //true 关闭听筒，false 打开听筒
    private boolean speakerMode = true;
    //true 开启录制,false 关闭录制
    private boolean isControlEnable = false;
    //录制计时延迟
    private final static int TIMER_DELAYED = 1000;
    //图标闪烁延迟
    private final static int FLASH_ICON_DELAYED = 500;
    //初始默认时间
    private long recordingDuration = 0;
    //true 已静音，fasle 未静音
    private boolean isMicphoneMuted = true;
    //true继续，false暂停
    private boolean isStopCount = false;
    //是否是大屏
    private boolean mLocalFullScreen;
    //是否已经锁屏
    private boolean isLock = false;
    //是否隐藏
    private boolean isWhiteBoardLock = false;
    //音量管理
    private VolumeManager mVolumeManager;
    //如果小于0，音量递减，如果大于0，音量递增
    private int currentVolume = -1;
    //初始化videoInfo
    private List<VideoInfo> layoutInfo = new ArrayList<>();
    // 本地状态
    private boolean isHandupNow;
    // 会控强制静音
    private boolean isMuteDisable;
    //
    private int mMuteBtnLongPress;
    //呼叫会议接收值
    private String callNumber;
    //用户名
    private String mDisplayName;
    //计录时间
    private long timer = 0;
    //默认第一次为空
    private String timeStr = "";
    //
    private AtomicBoolean audioMuteStatus = new AtomicBoolean(false);

    private RecordingState mState;

    private boolean mCloseCameraFromActivity = false;

    private boolean mCloseVoiceFromActivity = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.call_fragment_layout, container, false);

    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        countTimer();
        initialization(view);
        refreshMuteMicBtn();
        Bundle mGetBundle = getArguments();
        if (mGetBundle != null) {
            mCloseCameraFromActivity = mGetBundle.getBoolean(CLOSE_CAMERA, false);
            videoMute = mCloseCameraFromActivity;
            closeVideo();
            mCloseVoiceFromActivity = mGetBundle.getBoolean(CLOSE_VOICE, false);
            Log.d(TAG, "11111 mGetBundle != null onViewCreated: mCloseCameraFromActivity  " + mCloseCameraFromActivity + "   mCloseVoiceFromActivity   " + mCloseVoiceFromActivity);
            isMicphoneMuted = mCloseVoiceFromActivity;
            closeVoice(isMicphoneMuted);
            Log.d(TAG, "11111 mGetBundle != null onViewCreated: videoMute  " + videoMute + "   isMicphoneMuted  " + isMicphoneMuted);
        }
        super.onViewCreated(view, savedInstanceState);

    }

    public static VideoFragment newInstance(boolean closeCamera, boolean closeVoice) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(CLOSE_CAMERA, closeCamera);
        bundle.putBoolean(CLOSE_VOICE, closeVoice);
        Log.d(TAG, "11111 newInstance closeCamera  " + closeCamera + "   closeVoice  " + closeVoice);
        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    //初始化控件
    private void initialization(View view) {
        //外置取消当前通话
        mButtonCancel = (view.findViewById(R.id.conn_mt_cancelcall_btn));
        //音视频源
        mVideoView = view.findViewById(R.id.remote_video_view);
        //挂断当前通话
        mDropCall = view.findViewById(R.id.ll_drop_call);
        //录制相关
        mRecordingVideoBtn = view.findViewById(R.id.start_record_video);
        mRecordingTimer = view.findViewById(R.id.video_recording_timer);
        mFlashView = view.findViewById(R.id.video_recording_icon);
        mRecordingVideoLabel = view.findViewById(R.id.record_video_text);
        mRecordingTimerHideShow = view.findViewById(R.id.conversation_recording_layout);
        //语音模式
        mAudioOnlyBtn = view.findViewById(R.id.audio_only_btn);
        mAudioOnlyLabel = view.findViewById(R.id.audio_only_text);
        //关闭视频
        mCloseVideo = view.findViewById(R.id.close_video);
        mCloseVideoLabel = view.findViewById(R.id.video_mute_text);
        //听筒模式
        mSwitchSpeakerMode = (view.findViewById(R.id.switch_speaker_mode));
        mSwitchSpeakerLabel = view.findViewById(R.id.switch_speaker_text);
        //麦克风
        mMuteMicBtn = view.findViewById(R.id.mute_mic_btn);
        mMicContainer = view.findViewById(R.id.mic_mute_container);
        mMuteMicLabel = view.findViewById(R.id.mute_mic_btn_label);
        if (mMuteMicBtn != null) {
            mMuteMicBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mMuteBtnLongPress++;
                    if (mMuteBtnLongPress >= 3) {
                        displayDebugButton();
                        mMuteBtnLongPress = 0;
                    }
                    return true;
                }
            });

        }
        //举手
        mHandupContainer = (RelativeLayout) view.findViewById(R.id.handup_view);
        mHandupBtn = (ImageButton) view.findViewById(R.id.handup_btn);
        mHandupLabel = (TextView) view.findViewById(R.id.handup_label);
        //切换摄像头
        mSwitchCamera = (ImageButton) view.findViewById(R.id.switch_camera);
        switchCameraHideShow = (LinearLayout) view.findViewById(R.id.switch_camera_layout);
        //外部容器
        outgoingContainer = (RelativeLayout) view.findViewById(R.id.outgoing_container);
        //视频容器
        videoContainer = (RelativeLayout) view.findViewById(R.id.video_container);
        //网络状态
        mNetworkState = (ImageView) view.findViewById(R.id.network_state);
        mNetworkState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRefreshStatisticsInfo();
            }
        });
        //共享content
        mContent = (ImageView) view.findViewById(R.id.shared_content);
        //头部、底部导航
        topNavigation = (LinearLayout) view.findViewById(R.id.ll_top_hide_show);
        bottomNavigation = (LinearLayout) view.findViewById(R.id.ll_bottom);
        //会议计时
        mMeetingTimer = (TextView) view.findViewById(R.id.network_state_timer);
        //呼叫会议号
        mCallMeetingNumber = (TextView) view.findViewById(R.id.tv_call_number);
        mCallMeetingNumber.setText(callNumber);
        //视频界面功能隐藏\显示控制
        videoFunctionHideShow = (LinearLayout) view.findViewById(R.id.ll_video_function);

        mStats = (Button) view.findViewById(R.id.stats_btn);
        //系统监听
        mStats.setOnClickListener(this);
        mDropCall.setOnClickListener(this);
        mSwitchCamera.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        mMuteMicBtn.setOnClickListener(this);
        mSwitchSpeakerMode.setOnClickListener(this);
        mCloseVideo.setOnClickListener(this);
        mAudioOnlyBtn.setOnClickListener(this);
        mRecordingVideoBtn.setOnClickListener(this);

        //视频源相关
        mVideoView.init();
        mVideoView.setOnHoldMode(false);
        mVideoView.setBGCellLayoutInfoListener(this);
        mVideoView.setForceLayoutListener(this);
        mVideoView.setFrameCellLongClickListener(videoFrameLongClickListener);
        mVideoView.setLocalLayoutInfo(buildLocalLayoutInfo());
        mVideoView.setLocalFullScreen(false, false);

        //统计信息相关
        callStatisticsView = (CallStatisticsView) view.findViewById(R.id.conversation_statics);
        callStatisticsView.setOnCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callStatisticsView.setVisibility(GONE);
                stopRefreshMediaStatistics();
                callStatisticsView.clearData();
                Log.i(TAG, "callStatisticsView1");

            }
        });

        //统计渲染
        ViewStub stub = (ViewStub) view.findViewById(R.id.view_statistics_info);
        mStatisticsRender = new StatisticsRender(stub, new StatisticsRender.StatisticsOperationListener() {
            @Override
            public void stopStatisticsInfo() {
                stopRefreshStatisticsInfo();
            }
        });
        //释放旧数据
        if (videoInfo != null) {
            mVideoView.removeAddotherCell(videoInfo);
        }

        //FECC 相关
        mFeccLeftBtn = (ImageButton) view.findViewById(R.id.fecc_left);
        mFeccRightBtn = (ImageButton) view.findViewById(R.id.fecc_right);
        mFeccUpBtn = (ImageButton) view.findViewById(R.id.fecc_up);
        mFeccDownBtn = (ImageButton) view.findViewById(R.id.fecc_down);
        mFeccControl = (LinearLayout) view.findViewById(R.id.fecc_control);
        mFeccControlBg = (ImageView) view.findViewById(R.id.fecc_control_bg);
        mFeccControlBgLeft = (ImageView) view.findViewById(R.id.fecc_control_bg_left);
        mFeccControlBgRight = (ImageView) view.findViewById(R.id.fecc_control_bg_right);
        mFeccControlBgUp = (ImageView) view.findViewById(R.id.fecc_control_bg_up);
        mFeccControlBgDown = (ImageView) view.findViewById(R.id.fecc_control_bg_down);
        mFeccPanView = (ImageView) view.findViewById(R.id.fecc_pan);
        zoomInAdd = (ImageView) view.findViewById(R.id.zoom_in_add);
        zoomInPlus = (ImageView) view.findViewById(R.id.zoom_out_plus);
        FECCListeners();
        initFeccEventListeners();
        //更多相关
        moreDialog = (LinearLayout) view.findViewById(R.id.more_layout_dialog);
//        mMeetingMore = (ImageButton) view.findViewById(R.id.hold_meeting_more);
//        mMeetingMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (moreDialog.getVisibility() == INVISIBLE) {
//                    moreDialog.setVisibility(VISIBLE);
//                } else hideMoreDialog(moreDialog);
//            }
//        });

        //虚拟键盘
        keyboard = (TextView) view.findViewById(R.id.keyboard);
        keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.i(TAG, "show keyboard true");
                TimeHide();
                showDtmfLayout();
                hideMoreDialog(moreDialog);
            }
        });
        //双音多频相关
        dtmfLayout = (RelativeLayout) view.findViewById(R.id.dtmf_layout);
        dtmfLay = (LinearLayout) view.findViewById(R.id.dtmf);
        dtmf = new Dtmf(dtmfLay, new Dtmf.DtmfListener() {
            @Override
            public void onDtmfKey(String key) {
                L.i(TAG, "onDtmfKey key::" + key);
                if (layoutInfo != null) {
                    if (layoutInfo.size() > 0) {
                        L.i("sendDtmf 1：" + layoutInfo.get(0).getRemoteID() + ":key:" + key);
                        NemoSDK.getInstance().sendDtmf(layoutInfo.get(0).getRemoteID(), key);
                    }
                }

            }

        });

        //锁屏
        lockPeople = (LinearLayout) view.findViewById(R.id.layout_lock_people);
        if (lockPeople != null) {

            lockPeople.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    L.i("setForceLayout onClick:" + mLocalFullScreen);
                    if (mLocalFullScreen) {
                        mVideoView.switchLocalViewToSmallCell();
                    } else {
                        NemoSDK.getInstance().forceLayout(0);
                    }
                    lockPeople.setVisibility(View.GONE);
                    isLock = false;
                    L.i("callActivity localFullScreen:" + mLocalFullScreen);
                }
            });
        }

        //音量管理
        mVolumeManager = new VolumeManager(getActivity(), view.findViewById(R.id.operation_volume_brightness), AudioManager.STREAM_VOICE_CALL);
        mVolumeManager.setMuteCallback(this);
        currentVolume = mVolumeManager.getVolume();

        TimeHide();
    }

    //隐藏更多对话框
    public void hideMoreDialog(LinearLayout moreDialog) {
        if (moreDialog != null) {
            if (moreDialog.getVisibility() == VISIBLE) {
                moreDialog.setVisibility(INVISIBLE);
            }
        }
    }

    //显示双音频布局
    private void showDtmfLayout() {
        dtmfLayout.setVisibility(View.VISIBLE);
        if (dtmf != null) {
            dtmf.show();
        }
    }

    //录制计时延迟
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            recordingDuration += 1000;
            mRecordingTimer.setText(getResources().getString(R.string.recording_text) + " " + CommonTime.formatDuration(recordingDuration));
            mRecordingTimer.postDelayed(timerRunnable, TIMER_DELAYED);


        }
    };
    //图标闪烁延迟
    private Runnable flashingViewRunnable = new Runnable() {
        @Override
        public void run() {
            mFlashView.setVisibility(mFlashView.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);
            mFlashView.postDelayed(flashingViewRunnable, FLASH_ICON_DELAYED);
        }
    };
    //会议计时
    private Runnable TimerRunnable = new Runnable() {

        @Override
        public void run() {
            if (!isStopCount) {
                timer += 1000;
                timeStr = CommonTime.formatDuration(timer);
                mMeetingTimer.setText(timeStr);
            }
            countTimer();
        }
    };

    private void countTimer() {
        handler.postDelayed(TimerRunnable, 1000);
    }


    //刷新媒体统计
    private Runnable refreshMSRunnable = new Runnable() {
        @Override
        public void run() {
            startRefreshMediaStatistics();
        }
    };
    //刷新统计信息
    private Runnable refreshStatisticsInfoRunnable = new Runnable() {
        @Override
        public void run() {
            startRefreshStatisticsInfo();
        }
    };

    //隐藏头部、底部、视频界面
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            topNavigation.setVisibility(INVISIBLE);
            bottomNavigation.setVisibility(INVISIBLE);
            switchCameraHideShow.setVisibility(INVISIBLE);
        }
    };

    //toolbar显示延迟
    private void StartToolbarVisibleTimer() {
        handler.removeCallbacks(runnable);
        // handler.postDelayed(runnable, 5000);
    }

    //toolbar清除
    private void StopToolbarVisibleTimer() {
        handler.removeCallbacks(runnable);
    }


    //视频界面控制
    private void TimeHide() {
        if (topNavigation.getVisibility() == VISIBLE
                && bottomNavigation.getVisibility() == VISIBLE
                && switchCameraHideShow.getVisibility() == VISIBLE) {
            StartToolbarVisibleTimer();
            setFECCButtonVisible(false);
        } else {
            topNavigation.setVisibility(VISIBLE);
            bottomNavigation.setVisibility(VISIBLE);
            switchCameraHideShow.setVisibility(VISIBLE);
            StopToolbarVisibleTimer();
            setFECCButtonVisible(videoInfo != null
                    && isAudioOnly()
                    && !videoInfo.isVideoMute()
                    && (isSupportHorizontalFECC(videoInfo.getFeccOri())
                    || isSupportVerticalFECC(videoInfo.getFeccOri()))); // 左右或上下至少支持一种才行，否则不显示。
        }

    }

    /**
     * 音量大小监听
     *
     * @param mute
     */
    @Override
    public void muteChanged(boolean mute) {
        NemoSDK.getInstance().setSpeakerMute(mute);
    }

    //视频界面隐藏、显示监听
    private View.OnClickListener videoFrameCellClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "print videoFrameCellClickListener-->mVisible::" + mVisible);
            if (topNavigation.getVisibility() == VISIBLE
                    && bottomNavigation.getVisibility() == VISIBLE
                    && switchCameraHideShow.getVisibility() == VISIBLE) {
                topNavigation.setVisibility(INVISIBLE);
                bottomNavigation.setVisibility(INVISIBLE);
                switchCameraHideShow.setVisibility(INVISIBLE);
                mVisible = false;
                setFECCButtonVisible(false);
            } else {
                mVisible = true;
                TimeHide();
                // handler.postDelayed(menuRunnable, 5000);
            }
            if (moreDialog.getVisibility() == VISIBLE) {
                moreDialog.setVisibility(INVISIBLE);

            } else {
                moreDialog.setVisibility(INVISIBLE);
                if (dtmf != null) {
                    dtmf.hide();
                }
            }
            if (isLock) {
                lockPeople.setVisibility(VISIBLE);
                if (!isWhiteBoardLock) {
                    lockPeople.setVisibility(mVisible ? View.GONE : View.VISIBLE);
                }
            } else {
                lockPeople.setVisibility(GONE);
            }
        }
    };

    // 重置小窗口位置
    private View.OnLongClickListener videoFrameLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            mVideoView.moveThumbCellsToOriginal();
            return false;
        }
    };

    //获取会议号
    public void setCallNumber(String callNumber) {
        Log.i(TAG, "onViewCreated setDisplayName=" + callNumber);
        this.callNumber = callNumber;

    }

    //获取用户名
    public void setDisplayName(String displayName) {
        Log.i(TAG, "setDisplayName=" + displayName);
        this.mDisplayName = displayName;

    }


    private void displayDebugButton() {
        mStats.setVisibility(VISIBLE);
//        mRoster.setVisibility(VISIBLE);
//        mSaveDump.setVisibility(VISIBLE);
    }

    //语音模式标签、图标切换
    private void setSwitchCallState(boolean audioMode) {
        Log.i(TAG, "print setSwitchCallState-->audioMode" + audioMode);
        mVideoView.setAudioOnlyMode(audioMode);
        if (this.audioMode) {
            mCloseVideo.setEnabled(false);
            mAudioOnlyBtn.setImageResource(R.mipmap.ic_call_open_camera);
            mAudioOnlyLabel.setText(R.string.close_switch_call_module);

        } else {
            mCloseVideo.setEnabled(true);
            mAudioOnlyLabel.setText(R.string.switch_call_module);
            mAudioOnlyBtn.setImageResource(R.mipmap.ic_call_close_camera);
        }
    }

    //开启、关闭视频标签、图标切换
    private void setVideoState(boolean videoMute) {
        //改动视频流状态
        mVideoView.setMuteLocalVideo(videoMute, getActivity().getString(R.string.call_video_mute));
        if (this.videoMute) {
            mAudioOnlyBtn.setEnabled(false);
            mCloseVideo.setImageResource(R.mipmap.ic_call_open_camera);
            mCloseVideoLabel.setText(getResources().getString(R.string.open_video));
        } else {
            mAudioOnlyBtn.setEnabled(true);
            mCloseVideo.setImageResource(R.mipmap.ic_call_close_camera);
            mCloseVideoLabel.setText(getResources().getString(R.string.close_video));
        }
    }

    //开启、关闭麦克风标签、图标切换
    public void setMicPhoneMuted(boolean isMicPhoneMuted) {
        Log.i(TAG, "print isMicPhoneMuted--> isMicPhoneMuted=" + isMicPhoneMuted);
        if (mVideoView != null) {
            audioMuteStatus.set(isMicPhoneMuted);
            //改变远端mute icon
            mVideoView.setMuteLocalAudio(isMicPhoneMuted);
            if (this.isMicphoneMuted) {
                mMuteMicBtn.setImageResource(R.mipmap.ic_call_open_voice_tube_clicked);
                mMuteMicLabel.setText(R.string.unmute_mic);
            } else {
                mMuteMicBtn.setImageResource(R.mipmap.ic_call_open_voice_tube);
                mMuteMicLabel.setText(R.string.mute_mic);
            }

        }


    }

    @Override
    public void onResume() {
        super.onResume();
        boolean audioStatus = audioMuteStatus.get();
        Log.i(TAG, "onResume: audioStatus:" + audioStatus);
        setMicPhoneMuted(audioStatus);
        NemoSDK.getInstance().enableMic(audioStatus, false);
        if (mVideoView != null) {
            mVideoView.setFrameCellClickListener(videoFrameCellClickListener);
        }
        if (currentVolume >= 0) {
            Log.i(TAG, "print onResume-->currentVolume=" + currentVolume);
            mVolumeManager.setVolume(0);
        }
    }

    /**
     * 接收content状态变化
     *
     * @param contentState ON_START, 开始分享。ON_STOP, 结束分享
     */
    @Override
    public void onContentStateChanged(NemoSDKListener.ContentState contentState) {
        if (NemoSDKListener.ContentState.ON_START == contentState) {
            mContent.setVisibility(VISIBLE);
        } else {
            mContent.setVisibility(GONE);
        }
    }

    @Override
    public void onStart() {
        mVideoView.onResume();
        super.onStart();
        //回到当前打开视频流
        NemoSDK.getInstance().muteVideo(false);


    }

    @Override
    public void onStop() {
        super.onStop();
        mVideoView.onPause();
        //退到后台暂停视频流
        NemoSDK.getInstance().muteVideo(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (null == mVideoView) {
            return;
        }
        //隐藏时所做的操作
        if (!hidden) {
            NemoSDK.getInstance().enableMic(false, true);
            mRecordingVideoBtn.setEnabled(true);
            mCallMeetingNumber.setText(callNumber);
        }
    }

    /**
     * 刷新静音后要做的动作
     */
    public void refreshMuteMicBtn() {
        if (isMuteDisable) {
            mMicContainer.setVisibility(GONE);
            mHandupContainer.setVisibility(VISIBLE);

            if (mCloseVoiceFromActivity) {
                isMicphoneMuted = mCloseVoiceFromActivity;
            } else {
                isMicphoneMuted = NemoSDK.getInstance().isMicMuted();
            }
//            isMicphoneMuted = NemoSDK.getInstance().isMicMuted();
            if (isMicphoneMuted) {
                if (isHandupNow) {
                    //取消举手
                    mHandupBtn.setImageResource(R.mipmap.ic_toolbar_handdown);
                    mHandupLabel.setText(R.string.hand_down);
                } else {
                    // 举手发言
                    mHandupBtn.setImageResource(R.mipmap.ic_toolbar_hand_up);
                    mHandupLabel.setText(R.string.hand_up);
                }
            } else {
                // 结束发言
                mHandupBtn.setImageResource(R.mipmap.ic_toolbar_end_speech);
                mHandupLabel.setText(R.string.end_speech);
            }
        } else {
            mMicContainer.setVisibility(VISIBLE);
            mHandupContainer.setVisibility(GONE);

            isMicphoneMuted = !isMicphoneMuted;
            setMicPhoneMuted(isMicphoneMuted);
        }
    }


    /**
     * 本地网络质量提示
     *
     * @param level 1、2、3、4个等级,差-中-良-优
     */
    public void onNetworkIndicatorLevel(final int level) {
        L.i(TAG, "onNetworkIndicatorLevel" + level);
        if (mNetworkState != null) {
            switch (level) {
                case 4:
                    L.i(TAG, "onNetworkIndicatorLevel=" + level);
                    mNetworkState.setImageResource(R.drawable.network_state_four);
                    break;
                case 3:
                    L.i(TAG, "onNetworkIndicatorLevel=" + level);
                    mNetworkState.setImageResource(R.drawable.network_state_three);
                    break;
                case 2:
                    L.i(TAG, "onNetworkIndicatorLevel=" + level);
                    mNetworkState.setImageResource(R.drawable.network_state_two);
                    break;
                case 1:
                    L.i(TAG, "onNetworkIndicatorLevel=" + level);
                    mNetworkState.setImageResource(R.drawable.network_state_one);
                    break;
            }
        }
    }

    /**
     * 处理本地消息
     *
     * @param controlUri
     * @param state
     * @param reason
     */
    public void onRecordingStateChanged(final String controlUri, final RecordingState state, final String reason) {
        Log.i(TAG, "print onRecordingStateChanged-->controlUri=" + controlUri + ",state=" + state + ",reason=" + reason);
        mState = state;
        if (state.equals(RecordingState.RECORDING_STATE_ACTING) && state.equals(RecordingState.RECORDING_STATE_ACTING)) {
            Log.i(TAG, "print onRecordingStateChanged show record");
            //显示录制
            mRecordingTimerHideShow.setVisibility(View.VISIBLE);
            mRecordingVideoBtn.setImageResource(R.mipmap.ic_toolbar_recording_ing);
            mRecordingVideoLabel.setText(R.string.button_text_stop);
            mFlashView.postDelayed(flashingViewRunnable, FLASH_ICON_DELAYED);
            mRecordingTimer.postDelayed(timerRunnable, FLASH_ICON_DELAYED);
            mRecordingTimer.setText(R.string.recording_text_preparing);
        } else if (state.equals(RecordingState.RECORDING_STATE_STOPING)) {
            Log.i(TAG, "print onRecordingStateChanged hide record");
            //隐藏录制
            mRecordingTimerHideShow.setVisibility(GONE);
            mRecordingVideoLabel.setText(R.string.button_text_record);
            mRecordingVideoBtn.setImageResource(R.drawable.ic_toolbar_recording);
            mFlashView.removeCallbacks(flashingViewRunnable);
            mRecordingTimer.removeCallbacks(timerRunnable);
            String content = getString(R.string.third_conf_record_notice);
            AlertUtil.toastText(content);
        }
    }

    /**
     * 处理远程信息
     *
     * @param callIndex
     * @param isStart
     * @param displayName
     */
    public void onRecordStatusNotification(final int callIndex, final boolean isStart, String displayName) {

        //mainCallIndex = callIndex;
        Log.i(TAG, " csl onRecordStatusNotification" + isStart);
        if (isStart) {
            mRecordingVideoBtn.setEnabled(false);
            mRecordingTimer.setText(displayName + "正在录制");
            mRecordingTimerHideShow.setVisibility(View.VISIBLE);
            mFlashView.postDelayed(flashingViewRunnable, FLASH_ICON_DELAYED);
            mRecordingVideoBtn.setImageResource(R.mipmap.ic_toolbar_recording_ing);
            mRecordingVideoLabel.setText(R.string.button_text_stop);
        } else {
            mRecordingVideoBtn.setEnabled(true);
            mFlashView.removeCallbacks(flashingViewRunnable);
            mRecordingTimerHideShow.setVisibility(GONE);
            mRecordingVideoLabel.setText(R.string.button_text_record);
            mRecordingVideoBtn.setImageResource(R.drawable.ic_toolbar_recording);
        }


    }

    /**
     * 处理会控消息
     * 控制操作：静音、非静音
     * 控制状态：举手发言、取消举手、结束发言
     *
     * @param callIndex
     * @param opearation    操作：mute/unmute
     * @param isMuteDisable 是否为强制静音
     */
    public void onConfMgmtStateChanged(int callIndex, final String opearation, boolean isMuteDisable) {

        this.isMuteDisable = isMuteDisable;
        if (opearation.equals("")) {
            return;
        }
        L.i(TAG, "print onConfMgmtStateChanged called, callIndex=" + callIndex + ", opearation=" + opearation + ", isMuteDisable=" + isMuteDisable);


        if (opearation.equals("mute")) {
            Log.i(TAG, "print onConfMgmtStateChanged-->opearation==" + opearation);
            mMuteMicBtn.setImageResource(R.mipmap.ic_toolbar_mic_muted);
            NemoSDK.getInstance().enableMic(true, false);
            if (mVideoView != null) {
                mVideoView.setMuteLocalAudio(true);
            }
        } else if (opearation.equals("unmute")) {
            mMuteMicBtn.setImageResource(R.mipmap.ic_toolbar_mic);
            NemoSDK.getInstance().enableMic(false, false);
            if (mVideoView != null) {
                mVideoView.setMuteLocalAudio(false);
            }

        }

        isHandupNow = false;
        L.i(TAG, "isMicMutedNow=" + NemoSDK.getInstance().isMicMuted());
        refreshMuteMicBtn();
    }

    /**
     * 显示外部容器
     */
    public void showOutgoingContainer() {
        if (outgoingContainer != null && videoContainer != null && videoFunctionHideShow != null && bottomNavigation != null) {
            outgoingContainer.setVisibility(VISIBLE);
            videoContainer.setVisibility(GONE);
            bottomNavigation.setVisibility(GONE);
            videoFunctionHideShow.setVisibility(GONE);
        }

    }

    /**
     * 显示视频容器
     */
    public void showVideoContainer() {
        if (outgoingContainer != null && videoContainer != null && videoFunctionHideShow != null && bottomNavigation != null) {
            outgoingContainer.setVisibility(GONE);
            videoContainer.setVisibility(VISIBLE);
            bottomNavigation.setVisibility(VISIBLE);
            videoFunctionHideShow.setVisibility(VISIBLE);
        }

    }

    /**
     * 通话视频信息改变回调
     *
     * @param videoInfos 当前通话视频信息
     */
    @Override
    public void onVideoDataSourceChange(List<VideoInfo> videoInfos) {
        if (mVideoView != null) {
            mVideoView.setLayoutInfo(videoInfos, false, false);
        }
        this.layoutInfo = videoInfos;

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    //视频初始状态
    public void releaseResource() {
        mVideoView.destroy();
        if (videoMute) {
            videoMute = false;
            setVideoState(videoMute);
        }

    }

    //语音初始状态
    public void releaseSwitchResource() {
        //  mVideoView.stopRender();
        mVideoView.destroy();
        if (audioMode) {
            audioMode = false;
            setSwitchCallState(audioMode);
        }
    }

    //录制初始状态
    public void RecordVideoResource() {
        Log.i(TAG, "zhengdan    RecordVideoResource    开始录制  ");
        isControlEnable = false;
        mRecordingTimer.removeCallbacks(timerRunnable);
        mFlashView.removeCallbacks(flashingViewRunnable);
        isControlEnable = !isControlEnable;
        mRecordingTimerHideShow.setVisibility(GONE);
        mRecordingVideoLabel.setText(R.string.button_text_record);
        mRecordingVideoBtn.setImageResource(R.drawable.ic_toolbar_recording);
        NemoSDK.getInstance().stopRecord();
    }

    //静音初始状态
    public void MicPhoneResource() {
        mVideoView.destroy();
        if (isMicphoneMuted) {
            isMicphoneMuted = false;
            setMicPhoneMuted(isMicphoneMuted);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(TimerRunnable);
        StopToolbarVisibleTimer();

    }

    //启动媒体统计
    private void startRefreshMediaStatistics() {
        Map<String, Object> map = NemoSDK.getInstance().getStatistics();
        callStatisticsView.updateStatistics(map);
        if (null == map) {
            return;
        }
        handler.removeCallbacks(refreshMSRunnable);
        handler.postDelayed(refreshMSRunnable, REFRESH_STATISTICS_INFO_DELAYED);
    }

    //清除媒体统计
    private void stopRefreshMediaStatistics() {

        handler.removeCallbacks(refreshMSRunnable);
    }

    //启动信息统计
    private void startRefreshStatisticsInfo() {
        NewStatisticsInfo newInfo = NemoSDK.getInstance().getStatisticsInfo();
        if (null == newInfo) {
            return;
        }
        mStatisticsRender.show();
        mStatisticsRender.onValue(newInfo);

        handler.removeCallbacks(refreshStatisticsInfoRunnable);
        handler.postDelayed(refreshStatisticsInfoRunnable, REFRESH_STATISTICS_INFO_DELAYED);
    }

    //清除信息统计
    private void stopRefreshStatisticsInfo() {
        handler.removeCallbacks(refreshStatisticsInfoRunnable);
    }


    //data
    private VideoInfo buildLocalLayoutInfo() {
        VideoInfo li = null;
        if (li == null) {
            li = new VideoInfo();
            li.setLayoutVideoState(Enums.LAYOUT_STATE_RECEIVED);
            li.setDataSourceID(MediaSourceID.SOURCE_ID_LOCAL_PREVIEW);
            li.setRemoteName(mDisplayName);
            Log.i(TAG, "getLayoutVideoState buildLocalLayoutInfo: " + li.toString());

        }
        return li;
    }

    /**
     * 接收content的内容， 刷新频率为300ms
     *
     * @param bitmap 接收到的content
     */
    public void onNewContentReceive(Bitmap bitmap) {
        mContent.setImageBitmap(bitmap);
    }


    //onClick事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_camera:
                foregroundCamera = !foregroundCamera;
                cameraId = foregroundCamera ? 1 : 0;
                NemoSDK.getInstance().switchCamera(cameraId);  // 0：后置 1：前置
                break;
            case R.id.conn_mt_cancelcall_btn:
                NemoSDK.getInstance().hangup();
                if (mHangupListener != null) {
                    mHangupListener.hangup();
                }
                break;
            case R.id.mute_mic_btn:
                mMuteBtnLongPress = 0;
                Log.i(TAG, "print onClick-->isMicMuted=" + !NemoSDK.getInstance().isMicMuted());
                NemoSDK.getInstance().enableMic(!NemoSDK.getInstance().isMicMuted(), true);
                refreshMuteMicBtn();
                break;
            case R.id.handup_btn:
                if (NemoSDK.getInstance().isMicMuted()) {
                    if (isHandupNow) {
                        // 取消举手
                        NemoSDK.getInstance().handDown();
                        isHandupNow = false;
                    } else {
                        // 举手发言
                        NemoSDK.getInstance().handUp();
                        isHandupNow = true;
                    }
                } else {
                    // 结束发言
                    NemoSDK.getInstance().endSpeech();
                }

                refreshMuteMicBtn();
                break;
            case R.id.switch_speaker_mode:
                speakerMode = !speakerMode;
                if (speakerMode) {
                    mSwitchSpeakerLabel.setText(getResources().getString(R.string.close_switch_speaker_mode));
                } else {
                    mSwitchSpeakerLabel.setText(getResources().getString(R.string.switch_speaker_mode));
                }
                NemoSDK.getInstance().switchSpeakerOnModle(speakerMode);
                break;
            case R.id.close_video:
                videoMute = !videoMute;
//                setVideoState(videoMute);
//                Log.i(TAG, "print onClick-->videoMute" + videoMute);
//                NemoSDK.getInstance().setVideoMute(videoMute);
//                mRecordingTimerHideShow.setVisibility(GONE);
                closeVideo();
                break;
            case R.id.ll_drop_call:
                NemoSDK.getInstance().hangup();
                if (mHangupListener != null) {
                    mHangupListener.hangup();
                }
                mStats.setVisibility(GONE);
                break;
            case R.id.remote_video_view:
                break;
            case R.id.audio_only_btn://语音模式
                audioMode = !audioMode;
                Log.i(TAG, "audio_only_btn==" + audioMode);
                setSwitchCallState(audioMode);
                NemoSDK.getInstance().switchCallMode(audioMode);
                break;
            case R.id.start_record_video:
//                if (isControlEnable) {
//                    Log.i(TAG, "print onClick start record," + isControlEnable + "callNumber   " +callNumber);
//                    NemoSDK.getInstance().startRecord(callNumber);
//                    isControlEnable = false;
//                    recordingDuration = 0;
//                } else {
////                    if (mState.equals(RecordingState.RECORDING_STATE_IDLE)) {
////                        Log.i(TAG, "print onClick state ");
////                        Toast.makeText(getActivity(), "云会议号没有录制权限", Toast.LENGTH_SHORT).show();
////                    }
//                    Log.i(TAG, "print onClick stop record," + isControlEnable);
//                    NemoSDK.getInstance().stopRecord();
//                    isControlEnable = true;
//                }


                Log.i(TAG, "zhengdan  print onClick if record," + isControlEnable);
                if (isControlEnable == false) {
                    Log.i(TAG, "zhengdan  print onClick start record :start," + isControlEnable);
                    NemoSDK.getInstance().startRecord(callNumber);
                    isControlEnable = true;
                    recordingDuration = 0;
                    if (7 == NemoSDKErrorCode.RECORD_PERMISSION || 8 == NemoSDKErrorCode.RECORD_STORAGE) {
                        Log.i(TAG, "zhengdan  print onClick start record :error,");
                        return;
                    }
                    Log.i(TAG, "zhengdan  print onClick if to you bet record," + isControlEnable);
                    if (mState.equals(RecordingState.RECORDING_STATE_ACTING) && mState.equals(RecordingState.RECORDING_STATE_ACTING)) {
                        isControlEnable = true;
                    }
                    Log.i(TAG, "zhengdan  print onClick start record :end ," + isControlEnable);
                } else {
                    Log.i(TAG, "zhengdan  print onClick stop record," + isControlEnable);
                    NemoSDK.getInstance().stopRecord();
                    isControlEnable = false;
                }
                Log.i(TAG, "zhengdan  print onClick else record," + isControlEnable);
                break;
            case R.id.conversation_roster:
                break;
            case R.id.save_dump:
                //mVolumeManager.onVolumeDown();
                break;
            case R.id.roster_btn:
                //mVolumeManager.onVolumeUp();
                break;
            case R.id.stats_btn:
                startRefreshMediaStatistics();
                callStatisticsView.setVisibility(VISIBLE);
                break;

        }
    }

    public void closeVideo() {
        setVideoState(videoMute);
        Log.i(TAG, "print onClick-->videoMute" + videoMute);
        NemoSDK.getInstance().setVideoMute(videoMute);
        mRecordingTimerHideShow.setVisibility(GONE);
    }

    public void closeVoice(boolean enableMic) {
        Log.i(TAG, "closeVoice  =" + !NemoSDK.getInstance().isMicMuted());
        if (enableMic) {
            NemoSDK.getInstance().enableMic(true, false);
        } else {
            NemoSDK.getInstance().enableMic(false, false);
        }
        mMicContainer.setVisibility(VISIBLE);
        mHandupContainer.setVisibility(GONE);
        setMicPhoneMuted(isMicphoneMuted);

    }

    private HangupListener mHangupListener;

    public interface HangupListener {
        void hangup();
    }

    public void setHangupListener(HangupListener listener) {
        this.mHangupListener = listener;
    }

    /////////////////////////////////////////////以下是FECC（远端硬件控制--->小鱼设备）相关////////////////////////////////////////////////////////

    //远端硬件控制接口
    private void handleFECCControl(FECCCommand command) {
        if (videoInfo != null) {
            NemoSDK.getInstance().farEndHardwareControl(videoInfo.getParticipantId(), command, 10);
            Log.i(TAG, "print handleFECCControl-->getParticipantId,: " + videoInfo.getParticipantId() + ",command,:" + command);
        }
    }

    /**
     * 改变FECC隐藏显示监听
     *
     * @param layoutInfo
     */
    @Override
    public void onChanged(VideoInfo layoutInfo) {
        videoInfo = layoutInfo;
        if (videoInfo != null) {
            L.i(TAG, "main cell " + layoutInfo.getRemoteName() + ":layoutInfo:" + layoutInfo.toString());
        }
        setFECCButtonVisible(videoInfo != null
                && isAudioOnly()
                && !videoInfo.isVideoMute()
                && (isSupportHorizontalFECC(videoInfo.getFeccOri()) || isSupportVerticalFECC(videoInfo.getFeccOri()))); // 左右或上下至少支持一种才行，否则不显示。
        setZoomInOutVisible(videoInfo != null && isSupportZoomInOut(videoInfo.getFeccOri()));
        setFeccTiltControl(videoInfo != null
                && isSupportHorizontalFECC(videoInfo.getFeccOri()), layoutInfo != null && isSupportVerticalFECC(videoInfo.getFeccOri()));
    }

    /**
     * 判断音频当前状态
     *
     * @return true 显示， false 隐藏
     */
    public boolean isAudioOnly() {
        if (videoInfo.getLayoutVideoState().equals(Enums.LAYOUT_STATE_RECEIVED)) {
            return true;
        } else {
            return false;
        }
    }

    //FECC
    private float GetFeccBtnPositon(ImageButton feccButton) {
        float animator = 0f;

        if (feccButton == mFeccRightBtn) {
            animator = mFeccRightBtn.getRight() - mFeccPanView.getWidth() + 40;
        } else if (feccButton == mFeccLeftBtn) {
            animator = mFeccLeftBtn.getX();
        } else if (feccButton == mFeccUpBtn) {
            animator = mFeccUpBtn.getY();
        } else if (feccButton == mFeccDownBtn) {
            animator = mFeccDownBtn.getBottom() - mFeccPanView.getHeight() + 30;
        }
        return animator;
    }

    private void FeccPanTurnSide(final ImageButton feccButton) {
        float animator = GetFeccBtnPositon(feccButton);
        ObjectAnimator fadeIn = null;

        if (feccButton == mFeccLeftBtn) {
            mFeccControlBgLeft.setVisibility(View.VISIBLE);
            fadeIn = ObjectAnimator.ofFloat(mFeccPanView, "x", animator);
        } else if (feccButton == mFeccRightBtn) {
            mFeccControlBgRight.setVisibility(View.VISIBLE);
            fadeIn = ObjectAnimator.ofFloat(mFeccPanView, "x", animator);
        } else if (feccButton == mFeccUpBtn) {
            mFeccControlBgUp.setVisibility(View.VISIBLE);
            fadeIn = ObjectAnimator.ofFloat(mFeccPanView, "y", animator);
        } else if (feccButton == mFeccDownBtn) {
            mFeccControlBgDown.setVisibility(View.VISIBLE);
            fadeIn = ObjectAnimator.ofFloat(mFeccPanView, "y", animator);
        }

        fadeIn.setDuration(100);
        fadeIn.start();
        mFeccControlBg.setVisibility(View.VISIBLE);
    }

    private void FeccPanTurnPingPong(final ImageButton feccButton) {
        float animator = GetFeccBtnPositon(feccButton);
        ObjectAnimator fadeIn = null;
        if (feccButton == mFeccLeftBtn) {
            fadeIn = ObjectAnimator.ofFloat(mFeccPanView, "x", mFeccPanView.getLeft(), animator, mFeccPanView.getLeft());
            fadeIn.setDuration(200);
        } else if (feccButton == mFeccRightBtn) {
            fadeIn = ObjectAnimator.ofFloat(mFeccPanView, "x", mFeccPanView.getLeft(), animator, mFeccPanView.getLeft());
            fadeIn.setDuration(200);
        } else if (feccButton == mFeccUpBtn) {
            fadeIn = ObjectAnimator.ofFloat(mFeccPanView, "y", mFeccPanView.getTop(), animator, mFeccPanView.getTop());
            fadeIn.setDuration(200);
        } else if (feccButton == mFeccDownBtn) {
            fadeIn = ObjectAnimator.ofFloat(mFeccPanView, "y", mFeccPanView.getTop(), animator, mFeccPanView.getTop());
            fadeIn.setDuration(200);
        }


        fadeIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator arg0) {
                if (feccButton == mFeccLeftBtn) {
                    mFeccControlBg.setVisibility(View.VISIBLE);
                    mFeccControlBgLeft.setVisibility(View.VISIBLE);
                } else if (feccButton == mFeccRightBtn) {
                    mFeccControlBg.setVisibility(View.VISIBLE);
                    mFeccControlBgRight.setVisibility(View.VISIBLE);
                } else if (feccButton == mFeccUpBtn) {
                    mFeccControlBg.setVisibility(View.VISIBLE);
                    mFeccControlBgUp.setVisibility(View.VISIBLE);
                } else if (feccButton == mFeccDownBtn) {
                    mFeccControlBg.setVisibility(View.VISIBLE);
                    mFeccControlBgDown.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {

            }

            @Override
            public void onAnimationCancel(Animator arg0) {

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                mFeccControlBg.setVisibility(View.VISIBLE);
                if (feccButton == mFeccLeftBtn) {
                    mFeccControlBgLeft.setVisibility(GONE);
                } else if (feccButton == mFeccRightBtn) {
                    mFeccControlBgRight.setVisibility(GONE);
                } else if (feccButton == mFeccUpBtn) {
                    mFeccControlBgUp.setVisibility(GONE);
                } else if (feccButton == mFeccDownBtn) {
                    mFeccControlBgDown.setVisibility(GONE);
                }
            }
        });
        fadeIn.start();

    }

    private void FeccPanTurnOrigin() {
        float animatorx = 0f;
        float animatory = 0f;
        animatorx = mFeccPanView.getLeft();
        animatory = mFeccPanView.getTop();
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mFeccPanView, "x", animatorx);
        fadeIn.setDuration(100);
        fadeIn.start();

        ObjectAnimator fadeIny = ObjectAnimator.ofFloat(mFeccPanView, "y", animatory);
        fadeIny.setDuration(100);
        fadeIny.start();

        mFeccControlBg.setVisibility(View.VISIBLE);
        mFeccControlBgUp.setVisibility(GONE);
        mFeccControlBgDown.setVisibility(GONE);
        mFeccControlBgRight.setVisibility(GONE);
        mFeccControlBgLeft.setVisibility(GONE);
    }

    private void initFeccEventListeners() {
        createFeccBtnGestureDetector(mFeccLeftBtn, UserActionListener.USER_ACTION_FECC_LEFT, UserActionListener.USER_ACTION_FECC_STEP_LEFT);
        createFeccBtnGestureDetector(mFeccRightBtn, UserActionListener.USER_ACTION_FECC_RIGHT, UserActionListener.USER_ACTION_FECC_STEP_RIGHT);
        createFeccBtnGestureDetector(mFeccUpBtn, UserActionListener.USER_ACTION_FECC_UP, UserActionListener.USER_ACTION_FECC_STEP_UP);
        createFeccBtnGestureDetector(mFeccDownBtn, UserActionListener.USER_ACTION_FECC_DOWN, UserActionListener.USER_ACTION_FECC_STEP_DOWN);
        if (zoomInAdd != null) {

            createZoomInGestureDetector(zoomInAdd);
        }
        if (zoomInPlus != null) {

            createZoomOutGestureDetector(zoomInPlus);
        }
        createFeccPanGestureDetector(mFeccControlBg, mFeccPanView, UserActionListener.USER_ACTION_FECC_LEFT, UserActionListener.USER_ACTION_FECC_STEP_LEFT);
        createFeccPanGestureDetector(mFeccControlBg, mFeccPanView, UserActionListener.USER_ACTION_FECC_RIGHT, UserActionListener.USER_ACTION_FECC_STEP_RIGHT);
        createFeccPanGestureDetector(mFeccControlBg, mFeccPanView, UserActionListener.USER_ACTION_FECC_UP, UserActionListener.USER_ACTION_FECC_STEP_UP);
        createFeccPanGestureDetector(mFeccControlBg, mFeccPanView, UserActionListener.USER_ACTION_FECC_DOWN, UserActionListener.USER_ACTION_FECC_STEP_DOWN);
    }

    private void createFeccBtnGestureDetector(final ImageButton feccButton, final int actionTurn, final int actionStep) {
        final GestureDetector mGestureDetector = new GestureDetector(feccButton.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                sendFeccCommand(actionTurn);
                FeccPanTurnSide(feccButton);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                sendFeccCommand(actionStep);
                FeccPanTurnPingPong(feccButton);
                return true;
            }

        });

        feccButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mGestureDetector.onTouchEvent(event))
                    return true;

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        FeccPanTurnOrigin();
                        if (actionTurn == UserActionListener.USER_ACTION_FECC_LEFT
                                || actionTurn == UserActionListener.USER_ACTION_FECC_RIGHT
                                || actionStep == UserActionListener.USER_ACTION_FECC_STEP_LEFT
                                || actionStep == UserActionListener.USER_ACTION_FECC_STEP_RIGHT) {
                            sendFeccCommand(UserActionListener.USER_ACTION_FECC_STOP);
                        } else if (actionTurn == UserActionListener.USER_ACTION_FECC_UP
                                || actionTurn == UserActionListener.USER_ACTION_FECC_DOWN
                                || actionStep == UserActionListener.USER_ACTION_FECC_STEP_UP
                                || actionStep == UserActionListener.USER_ACTION_FECC_STEP_DOWN) {
                            sendFeccCommand(UserActionListener.USER_ACTION_FECC_UP_DOWN_STOP);
                        }
                        return true;
                }
                return true;
            }
        });
    }

    private void createZoomInGestureDetector(ImageView zoomInAdd) {
        final GestureDetector zoomGestureDetector = new GestureDetector(zoomInAdd.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                L.i(TAG, "createZoomInGestureDetector onLongPress...");
                actionListener.onUserAction(UserActionListener.FECC_ZOOM_IN, null);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                L.i(TAG, "createZoomInGestureDetector onSingleTapConfirmed...");
                actionListener.onUserAction(UserActionListener.FECC_STEP_ZOOM_IN, null);
                return true;
            }
        });

        zoomInAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (zoomGestureDetector.onTouchEvent(event)) {
                    return true;
                }
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        L.i(TAG, "createZoomInGestureDetector ACTION_UP...");
                        actionListener.onUserAction(UserActionListener.FECC_ZOOM_TURN_STOP, null);
                        return true;
                }
                return true;
            }
        });
    }

    private void createZoomOutGestureDetector(ImageView zoomInPlus) {
        final GestureDetector zoomGestureDetector = new GestureDetector(zoomInPlus.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                L.i(TAG, "createZoomOutGestureDetector onLongPress...");
                actionListener.onUserAction(UserActionListener.FECC_ZOOM_OUT, null);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                L.i(TAG, "createZoomOutGestureDetector onSingleTapConfirmed...");
                actionListener.onUserAction(UserActionListener.FECC_STEP_ZOOM_OUT, null);
                return true;
            }
        });

        zoomInPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (zoomGestureDetector.onTouchEvent(event)) {
                    return true;
                }
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        L.i(TAG, "createZoomOutGestureDetector ACTION_UP...");
                        actionListener.onUserAction(UserActionListener.FECC_ZOOM_TURN_STOP, null);
                        return true;
                }
                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createFeccPanGestureDetector(final ImageView feccBigCircle, final ImageView feccSmallCircle, final int actionTurn, final int actionStep) {
        feccBigCircle.setLongClickable(true);
        feccBigCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mFeccPanView.setImageResource(R.drawable.fecc_middle_icon);
                        FeccPanTurnOrigin();
                        sendFeccCommand(UserActionListener.USER_ACTION_FECC_STOP);
                        sendFeccCommand(UserActionListener.USER_ACTION_FECC_UP_DOWN_STOP);
                        if (actionTurn == UserActionListener.USER_ACTION_FECC_LEFT
                                || actionTurn == UserActionListener.USER_ACTION_FECC_RIGHT
                                || actionStep == UserActionListener.USER_ACTION_FECC_STEP_LEFT
                                || actionStep == UserActionListener.USER_ACTION_FECC_STEP_RIGHT) {
                            sendFeccCommand(UserActionListener.USER_ACTION_FECC_STOP);
                        } else if (actionTurn == UserActionListener.USER_ACTION_FECC_UP
                                || actionTurn == UserActionListener.USER_ACTION_FECC_DOWN
                                || actionStep == UserActionListener.USER_ACTION_FECC_STEP_UP
                                || actionStep == UserActionListener.USER_ACTION_FECC_STEP_DOWN) {
                            sendFeccCommand(UserActionListener.USER_ACTION_FECC_UP_DOWN_STOP);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getPointerCount() >= 1) {
                            mFeccPanView.setImageResource(R.drawable.fecc_middle_icon_press);
                            int bigR = feccBigCircle.getWidth() / 2;
                            int smallR = feccSmallCircle.getWidth() / 2;
                            int r = bigR - smallR;

                            float eventx = event.getX(0);
                            float eventy = event.getY(0);

                            float absRelX = Math.abs(eventx - bigR);
                            float absRelY = Math.abs(eventy - bigR);

                            if (eventx > bigR && absRelX > absRelY && feccHorizontalControl) {
                                mFeccControlBg.setVisibility(View.VISIBLE);
                                mFeccControlBgRight.setVisibility(View.VISIBLE);
                                mFeccControlBgLeft.setVisibility(GONE);
                                mFeccControlBgUp.setVisibility(GONE);
                                mFeccControlBgDown.setVisibility(GONE);
                                sendFeccCommand(UserActionListener.USER_ACTION_FECC_RIGHT);
                            } else if (eventx < bigR && absRelX > absRelY && feccHorizontalControl) {
                                mFeccControlBg.setVisibility(View.VISIBLE);
                                mFeccControlBgLeft.setVisibility(View.VISIBLE);
                                mFeccControlBgRight.setVisibility(GONE);
                                mFeccControlBgUp.setVisibility(GONE);
                                mFeccControlBgDown.setVisibility(GONE);
                                sendFeccCommand(UserActionListener.USER_ACTION_FECC_LEFT);
                            } else if (eventy > bigR && absRelY > absRelX && feccVerticalControl) {
                                mFeccControlBg.setVisibility(View.VISIBLE);
                                mFeccControlBgLeft.setVisibility(GONE);
                                mFeccControlBgRight.setVisibility(GONE);
                                mFeccControlBgUp.setVisibility(GONE);
                                mFeccControlBgDown.setVisibility(View.VISIBLE);
                                sendFeccCommand(UserActionListener.USER_ACTION_FECC_DOWN);
                            } else if (eventy < bigR && absRelY > absRelX && feccVerticalControl) {
                                mFeccControlBg.setVisibility(View.VISIBLE);
                                mFeccControlBgLeft.setVisibility(GONE);
                                mFeccControlBgRight.setVisibility(GONE);
                                mFeccControlBgDown.setVisibility(GONE);
                                mFeccControlBgUp.setVisibility(View.VISIBLE);
                                sendFeccCommand(UserActionListener.USER_ACTION_FECC_UP);
                            }

                            double d = Math.sqrt((eventx - bigR) * (eventx - bigR) + (eventy - bigR) * (eventy - bigR));
                            r += 25; // critical pixel 包含小圆发光距离

                            if (d > r) { // moving out of the big circle
                                float fx = ((float) bigR + ((float) r) * (eventx - (float) bigR) / (float) d);
                                float fy = ((float) bigR + ((float) r) * (eventy - (float) bigR) / (float) d);

                                if (feccHorizontalControl) {
                                    feccSmallCircle.setX(fx - smallR + 15); // FIXME: 2017/10/18 temp fix
                                }
                                if (feccVerticalControl) {
                                    feccSmallCircle.setY(fy - smallR);
                                }
                            } else {  // moving inside of the big circle
                                if (feccHorizontalControl) {
                                    feccSmallCircle.setX(eventx - smallR);
                                }
                                if (feccVerticalControl) {
                                    feccSmallCircle.setY(eventy - smallR);
                                }
                            }

                            v.invalidate();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void sendFeccCommand(int command) {
        if (command == UserActionListener.USER_ACTION_FECC_LEFT || command == UserActionListener.USER_ACTION_FECC_RIGHT) {
            if (lastFeccCommand == UserActionListener.USER_ACTION_FECC_UP || lastFeccCommand == UserActionListener.USER_ACTION_FECC_DOWN) {
                actionListener.onUserAction(UserActionListener.USER_ACTION_FECC_UP_DOWN_STOP, null);
            }
        } else if (command == UserActionListener.USER_ACTION_FECC_UP || command == UserActionListener.USER_ACTION_FECC_DOWN) {
            if (lastFeccCommand == UserActionListener.USER_ACTION_FECC_LEFT || lastFeccCommand == UserActionListener.USER_ACTION_FECC_RIGHT) {
                actionListener.onUserAction(UserActionListener.USER_ACTION_FECC_STOP, null);
            }
        }
        lastFeccCommand = command;
        actionListener.onUserAction(command, null);
    }

    public void setFECCButtonVisible(final boolean visible) {
        Log.i(TAG, " cslName kunkka setFECCButtonVisible==" + visible);
        if (mFeccControl != null) {
            mFeccControl.setVisibility(visible ? VISIBLE : INVISIBLE);
        }
    }

    public void setZoomInOutVisible(boolean visible) {
        if (zoomInAdd != null && zoomInPlus != null) {
            zoomInPlus.setVisibility(visible ? VISIBLE : INVISIBLE);
            zoomInAdd.setVisibility(visible ? VISIBLE : INVISIBLE);
            if (mFeccUpBtn != null && mFeccDownBtn != null) {
                if (visible) {
                    mFeccUpBtn.setImageResource(R.drawable.fecc_up);
                    mFeccDownBtn.setImageResource(R.drawable.fecc_down);
                } else {
                    mFeccUpBtn.setImageResource(R.drawable.fecc_up_disabled);
                    mFeccDownBtn.setImageResource(R.drawable.fecc_down_disabled);
                }
            }
        }
    }

    public void setFeccTiltControl(final boolean horizontalStatus, final boolean verticalStatus) {

        feccHorizontalControl = horizontalStatus;
        feccVerticalControl = verticalStatus;

        if (mFeccControlBgLeft != null) {
            mFeccControlBgLeft.setImageResource(R.drawable.fecc_left_bg);
        }
        if (mFeccControlBgRight != null) {
            mFeccControlBgRight.setImageResource(R.drawable.fecc_right_bg);
        }
        if (mFeccControlBgUp != null) {
            mFeccControlBgUp.setImageResource(R.drawable.fecc_up_bg);
        }
        if (mFeccControlBgDown != null) {
            mFeccControlBgDown.setImageResource(R.drawable.fecc_down_bg);
        }

        if (feccHorizontalControl && !feccVerticalControl) {    // only support horizontal
            if (mFeccUpBtn != null) {
                mFeccUpBtn.setImageResource(R.drawable.fecc_up_disabled);
            }
            if (mFeccDownBtn != null) {
                mFeccDownBtn.setImageResource(R.drawable.fecc_down_disabled);
            }
            if (mFeccControlBg != null) {
                mFeccControlBg.setImageResource(R.drawable.bg_toolbar_fecc_pan);
            }
        } else {
            if (mFeccControlBg != null) {
                mFeccControlBg.setImageResource(R.drawable.bg_toolbar_fecc_pan);
            }
            if (mFeccDownBtn != null) {
                mFeccDownBtn.setImageResource(R.drawable.fecc_down);
            }
            if (mFeccUpBtn != null) {
                mFeccUpBtn.setImageResource(R.drawable.fecc_up);
            }
        }

        if (feccVerticalControl && !feccHorizontalControl) {     // only support vertical
            if (mFeccLeftBtn != null) {
                mFeccLeftBtn.setImageResource(R.drawable.fecc_left_disabled);
            }
            if (mFeccRightBtn != null) {
                mFeccRightBtn.setImageResource(R.drawable.fecc_right_disabled);
            }

        } else {
            if (mFeccLeftBtn != null) {
                mFeccLeftBtn.setImageResource(R.drawable.fecc_left);
            }
            if (mFeccRightBtn != null) {
                mFeccRightBtn.setImageResource(R.drawable.fecc_right);
            }
        }

        if (feccHorizontalControl) {
            if (mFeccLeftBtn != null) {
                mFeccLeftBtn.setVisibility(View.VISIBLE);
            }
            if (mFeccRightBtn != null) {
                mFeccRightBtn.setVisibility(View.VISIBLE);
            }
        }

        if (feccVerticalControl) {
            if (mFeccUpBtn != null) {
                mFeccUpBtn.setVisibility(View.VISIBLE);
            }
            if (mFeccDownBtn != null) {
                mFeccDownBtn.setVisibility(View.VISIBLE);
            }
        } else {
            if (mFeccUpBtn != null) {
                mFeccUpBtn.setVisibility(View.VISIBLE);
            }
            if (mFeccDownBtn != null) {
                mFeccDownBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setForceLayout(int participantId) {
        NemoSDK.getInstance().forceLayout(participantId);
    }

    @Override
    public void notificationLockPeople(boolean isLockClick, boolean mLocalFullScreen, boolean isMute) {
        this.mLocalFullScreen = mLocalFullScreen;
        if (isLockClick) {
            isLock = isLockClick;
            Log.i(TAG, "notificationLockPeople==::" + isLock);
            if (!isWhiteBoardLock) {
                lockPeople.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void notificationMute(boolean isRemoteVideo, boolean isMute) {

    }

    //FECC
    public void FECCListeners() {
        setActionListener(new UserActionListener() {
            @Override
            public void onUserAction(int nAction, Bundle args) {

                switch (nAction) {
                    case UserActionListener.USER_ACTION_FECC_LEFT:
                        handleFECCControl(FECCCommand.FECC_TURN_LEFT);
                        break;
                    case UserActionListener.USER_ACTION_FECC_RIGHT:
                        handleFECCControl(FECCCommand.FECC_TURN_RIGHT);
                        break;
                    case UserActionListener.USER_ACTION_FECC_STOP:
                        handleFECCControl(FECCCommand.FECC_TURN_STOP);
                        break;
                    case UserActionListener.USER_ACTION_FECC_STEP_LEFT:
                        handleFECCControl(FECCCommand.FECC_STEP_LEFT);
                        break;
                    case UserActionListener.USER_ACTION_FECC_STEP_RIGHT:
                        handleFECCControl(FECCCommand.FECC_STEP_RIGHT);
                        break;
                    case UserActionListener.USER_ACTION_FECC_UP:
                        handleFECCControl(FECCCommand.TILT_CAMERA_TURN_UP);
                        break;
                    case UserActionListener.USER_ACTION_FECC_DOWN:
                        handleFECCControl(FECCCommand.TILT_CAMERA_TURN_DOWN);
                        break;
                    case UserActionListener.USER_ACTION_FECC_STEP_UP:
                        handleFECCControl(FECCCommand.TILT_CAMERA_STEP_UP);
                        break;
                    case UserActionListener.USER_ACTION_FECC_STEP_DOWN:
                        handleFECCControl(FECCCommand.TILT_CAMERA_STEP_DOWN);
                        break;
                    case UserActionListener.USER_ACTION_FECC_UP_DOWN_STOP:
                        handleFECCControl(FECCCommand.TILT_CAMERA_TURN_STOP);
                        break;
                    case FECC_ZOOM_IN:
                        handleFECCControl(FECCCommand.FECC_ZOOM_IN);
                        break;
                    case FECC_STEP_ZOOM_IN:
                        handleFECCControl(FECCCommand.FECC_STEP_ZOOM_IN);
                        break;
                    case FECC_ZOOM_OUT:
                        handleFECCControl(FECCCommand.FECC_ZOOM_OUT);
                        break;
                    case FECC_STEP_ZOOM_OUT:
                        handleFECCControl(FECCCommand.FECC_STEP_ZOOM_OUT);
                        break;
                    case FECC_ZOOM_TURN_STOP:
                        handleFECCControl(FECCCommand.FECC_ZOOM_TURN_STOP);
                        break;
                }


            }
        });
    }

    private boolean isSupportHorizontalFECC(int capability) {
        return (capability & 1 << 1) != 0;
    }

    private boolean isSupportVerticalFECC(int capability) {
        return (capability & 1 << 2) != 0;
    }

    private boolean isSupportZoomInOut(int capability) {
        return (capability & 1 << 4) != 0;
    }


    /**
     * 处理FECC监听事件
     *
     * @param actionListener
     */
    public void setActionListener(UserActionListener actionListener) {
        this.actionListener = actionListener;
    }


}

