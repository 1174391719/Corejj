package com.zyzxsp.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.log.L;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.NemoSDKErrorCode;
import com.ainemo.sdk.otf.NemoSDKListener;
import com.ainemo.sdk.otf.RecordingState;
import com.ainemo.sdk.otf.Roster;
import com.ainemo.sdk.otf.RosterWrapper;
import com.ainemo.sdk.otf.VideoInfo;
import com.zyzxsp.R;
import com.zyzxsp.VideoFragment;
import com.zyzxsp.utils.BackHandledFragment;
import com.zyzxsp.utils.BackHandledInterface;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.zyzxsp.fragment.HomeFragment.CLOSE_CAMERA;
import static com.zyzxsp.fragment.HomeFragment.CLOSE_VOICE;

public class ZyCallActivity extends FragmentActivity implements BackHandledInterface ,VideoFragment.HangupListener {
    public static final String  TAG ="ZyCallActivity";

    private VideoFragment mVideoFragment;
    private FragmentManager manager;
    private BackHandledFragment mBackHandedFragment;
    private String callNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zy_call);

        Intent intent = getIntent();
        String myNumber = intent.getStringExtra("MY_NUMBER");
        String displayName=intent.getStringExtra("displayName");
        boolean closeCamera = intent.getBooleanExtra(CLOSE_CAMERA,false);
        boolean closeVoice = intent.getBooleanExtra(CLOSE_VOICE,false);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d(TAG, "11111 跳转到 ZyCallActivity  closeCamera   " + closeCamera +"   closeVoice  " +closeVoice);
        mVideoFragment = VideoFragment.newInstance(closeCamera,closeVoice);
        mVideoFragment.setHangupListener(this);

        manager = getFragmentManager();
        manager.beginTransaction().add(R.id.content_frame_zy, mVideoFragment).commitAllowingStateLoss();



        L.i(TAG, "displayNameCallActivity11=" + displayName);
//        if (myNumber != null)
//            mDialFragment.setMyNumber(myNumber);
//
//        if(displayName!=null)
//            mDialFragment.setDisplayName(displayName);

        boolean isIncomingCall = intent.getBooleanExtra("isIncomingCall", false);
        if (isIncomingCall) {
            final int callIndex = intent.getIntExtra("callIndex", -1);
            String callerName = intent.getStringExtra("callerName");
            String callerNumber = intent.getStringExtra("callerNumber");
            Log.i(TAG,"showIncomingCallDialog="+callIndex);
            showIncomingCallDialog(callIndex, callerName, callerNumber);
        }


        NemoSDK.getInstance().setNemoSDKListener(new NemoSDKListener() {
            @Override
            public void onContentStateChanged(ContentState contentState) {
                mVideoFragment.onContentStateChanged(contentState);
            }

            @Override
            public void onNewContentReceive(Bitmap bitmap) {
                mVideoFragment.onNewContentReceive(bitmap);
            }

            @Override
            public void onCallFailed(int errorCode) {
                Observable.just(errorCode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                L.e(TAG, "error code is " + integer);
                                if (NemoSDKErrorCode.WRONG_PASSWORD == integer) {
                                    Toast.makeText(ZyCallActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                    //  Toast.makeText(CallActivity.this, "wrong password", Toast.LENGTH_SHORT).show();
                                } else if (NemoSDKErrorCode.INVALID_PARAM == integer) {
                                    Toast.makeText(ZyCallActivity.this, "无效终端号", Toast.LENGTH_SHORT).show();
                                    // Toast.makeText(CallActivity.this, "wrong param", Toast.LENGTH_SHORT).show();
                                } else if (NemoSDKErrorCode.NETWORK_UNAVAILABLE == integer) {
                                    Toast.makeText(ZyCallActivity.this, "网络不可达", Toast.LENGTH_SHORT).show();
                                    // Toast.makeText(CallActivity.this, "network_unavailable", Toast.LENGTH_SHORT).show();
                                } else if (NemoSDKErrorCode.HOST_ERROR == integer) {
                                    Toast.makeText(ZyCallActivity.this, "私有云host设置错误", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(CallActivity.this, "host error", Toast.LENGTH_SHORT).show();
                                }else if (NemoSDKErrorCode.RECORD_PERMISSION == integer) {
                                    Toast.makeText(ZyCallActivity.this, "云会议号没有录制权限", Toast.LENGTH_SHORT).show();
                                } else if (NemoSDKErrorCode.RECORD_STORAGE == integer) {
                                    Toast.makeText(ZyCallActivity.this, "云会议号空间不足，请联系管理员", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onCallStateChange(final CallState state, final String reason) {
                Log.i(TAG,"onCallStateChangeNemoSdk state=="+state+"=reason=="+state);
                Observable.just(state)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<CallState>() {
                            @Override
                            public void accept(CallState callState) throws Exception {
                                switch (state) {
                                    case CONNECTING:
                                        hideSoftKeyboard();
                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
//                                        manager.beginTransaction().hide(mDialFragment).commitAllowingStateLoss();
                                        if (mVideoFragment.isAdded()) {
                                            manager.beginTransaction().show(mVideoFragment).commitAllowingStateLoss();
                                        } else {
                                            manager.beginTransaction().add(R.id.content_frame, mVideoFragment).commitAllowingStateLoss();
                                        }
                                        mVideoFragment.showOutgoingContainer();
                                        break;
                                    case CONNECTED:
                                        hideSoftKeyboard();
                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
                                        if (mVideoFragment.isAdded()) {
                                            manager.beginTransaction().show(mVideoFragment).commitAllowingStateLoss();
//                                            manager.beginTransaction().hide(mDialFragment).show(mVideoFragment).commitAllowingStateLoss();
                                        } else {
//                                            manager.beginTransaction().add(R.id.content_frame, mVideoFragment).hide(mDialFragment).commitAllowingStateLoss();
                                            manager.beginTransaction().add(R.id.content_frame, mVideoFragment).commitAllowingStateLoss();
                                        }
                                        mVideoFragment.showVideoContainer();
                                        break;
                                    case DISCONNECTED:

                                        Log.i(TAG,"CallInfo nemoSDKDidReceiveCall onCallStateChange  is=="+reason);
                                        if (reason.equals("CANCEL")) {
                                            Toast.makeText(ZyCallActivity.this, "call canceled", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                        if (reason.equals("BUSY")) {
                                            Toast.makeText(ZyCallActivity.this, "the side is busy, please call later", Toast.LENGTH_SHORT).show();
//                                            manager.beginTransaction().hide(mDialFragment).show(mVideoFragment).commitAllowingStateLoss();
//                                            manager.beginTransaction().show(mVideoFragment).commitAllowingStateLoss();
                                            finish();
                                        }
                                        if("NORMAL_CONF_SESSION_EXCEED".equals(reason)){
                                            Toast.makeText(ZyCallActivity.this, "您呼叫的会议已达最大支持人数", Toast.LENGTH_LONG).show();
                                            finish();
                                        }


                                        if (callNumber!=null&&reason.equals("STATUS_OK")){
                                            L.i(TAG, "mVideoFragment reason is==" + reason +"==callNumber=="+callNumber);
                                            finish();
                                            return;
//
                                        }else {
                                            L.i(TAG, "mVideoFragment reason is 222==" + reason +"==callNumber=="+callNumber);
                                            if (mVideoFragment.isAdded()) {
                                                mVideoFragment.releaseResource();
//                                                manager.beginTransaction().hide(mVideoFragment).show(mDialFragment).commitAllowingStateLoss();
//                                                manager.beginTransaction().hide(mVideoFragment).commitAllowingStateLoss();
                                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                            }
                                            if(mVideoFragment.isAdded()){
                                                // L.i(TAG, "mVideoFragment.isAdded()== " + reason);
                                                mVideoFragment.releaseSwitchResource();
//                                                manager.beginTransaction().hide(mVideoFragment).show(mDialFragment).commitAllowingStateLoss();
//                                                manager.beginTransaction().hide(mVideoFragment).commitAllowingStateLoss();
                                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                            }

                                            if(mVideoFragment.isAdded()){
                                                // L.i(TAG, "MicphoneMutedResource== " + reason);
                                                mVideoFragment.RecordVideoResource();
//                                                manager.beginTransaction().hide(mVideoFragment).show(mDialFragment).commitAllowingStateLoss();
//                                                manager.beginTransaction().hide(mVideoFragment).commitAllowingStateLoss();
                                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                            }

                                            if(mVideoFragment.isAdded()){
                                                // L.i(TAG, "MicphoneMutedResource== " + reason);
                                                mVideoFragment.MicPhoneResource();
//                                                manager.beginTransaction().hide(mVideoFragment).show(mDialFragment).commitAllowingStateLoss();
//                                                manager.beginTransaction().hide(mVideoFragment) .commitAllowingStateLoss();
                                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                            }

                                            finish();
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
            }

            @Override
            public void onVideoDataSourceChange(List<VideoInfo> videoInfos) {
                Observable.just(videoInfos)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<VideoInfo>>() {
                            @Override
                            public void accept(List<VideoInfo> videoInfos) {
                                mVideoFragment.onVideoDataSourceChange(videoInfos);
                                Log.i(TAG," onVideoDataSourceChange is "+videoInfos.toString());
                            }
                        });
            }

            @Override
            public void onConfMgmtStateChanged(final int callIndex, final String opearation, final boolean isMuteDisable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoFragment.onConfMgmtStateChanged(callIndex, opearation, isMuteDisable);
                    }
                });
            }

            @Override
            public void onRecordStatusNotification(final int callIndex,final boolean isStart,final String displayName){
                L.i(TAG, "onRecordStatusNotification called");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoFragment.onRecordStatusNotification(callIndex, isStart, displayName);

                    }
                });
            }

            @Override
            public void onRecordingStateChanged(final String s, final RecordingState recordingState, final String s1) {
                Log.i(TAG, "zhengdan   print onRecordingStateChanged-->controlUri:" + s + ":state:" + recordingState + ":reason:" + s1);
                Log.i(TAG, "zhengdan    onRecordingStateChanged    录制状态改变  ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoFragment.onRecordingStateChanged(s, recordingState, s1);
                    }
                });
            }

            @Override
            public void onKickOut(final int code, final int reason) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        L.i(TAG, "code:" + code + ":reason:" + reason);
                        Toast.makeText(ZyCallActivity.this, "被踢了", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

            }

            @Override
            public void onRosterChange(RosterWrapper roster){
                L.i(TAG, "onRosterChange called. roster.size="+roster.getRosters().size());
                if (roster != null) {
                    for (Roster r : roster.getRosters()) {
                        L.i(TAG, "onRosterChange deviceName="+r.getDeviceName()+", pid="+r.getParticipantId());
                    }
                }
            }

            @Override
            public void onNetworkIndicatorLevel(final int level) {
                L.i(TAG, "onNetworkIndicatorLevel called. level="+level);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoFragment.onNetworkIndicatorLevel(level);
                    }
                });
            }

            @Override
            public void onVideoStatusChange(final int videoStatus) {
                L.i(TAG, "onVideoStatusChange called. videoStatus="+videoStatus);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(videoStatus==VideoStatus.VIDEO_STATUS_NORMAL.getStatus()){
                            Toast.makeText(ZyCallActivity.this,"网络正常",Toast.LENGTH_SHORT).show();
                        }else if(videoStatus==VideoStatus.VIDEO_STATUS_LOW_AS_LOCAL_BW.getStatus()){
                            Toast.makeText(ZyCallActivity.this,"本地网络不稳定",Toast.LENGTH_SHORT).show();
                        }else if(videoStatus==VideoStatus.VIDEO_STATUS_LOW_AS_LOCAL_HARDWARE.getStatus()){
                            Toast.makeText(ZyCallActivity.this,"系统忙，视频质量降低",Toast.LENGTH_SHORT).show();
                        }else if(videoStatus==VideoStatus.VIDEO_STATUS_LOW_AS_REMOTE.getStatus()){
                            Toast.makeText(ZyCallActivity.this,"对方网络不稳定",Toast.LENGTH_SHORT).show();
                        }else if(videoStatus==VideoStatus.VIDEO_STATUS_NETWORK_ERROR.getStatus()){
                            Toast.makeText(ZyCallActivity.this,"网络不稳定，请稍候",Toast.LENGTH_SHORT).show();
                        }else if(videoStatus==VideoStatus.VIDEO_STATUS_LOCAL_WIFI_ISSUE.getStatus()){
                            Toast.makeText(ZyCallActivity.this,"WiFi信号不稳定",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onIMNotification(final int callIndex,final String type, final String values) {
                L.i(TAG, "onIMNotification called. type=="+type+"==values="+values);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("[]".equals(values)) {
                            Toast.makeText(ZyCallActivity.this,R.string.im_notification_ccs_transfer,Toast.LENGTH_SHORT).show();
                        } else {
                            String val = new String();
                            val = values.replace("[", "");
                            val = val.replace("]", "");
                            val = val.replace('"', ' ');
                            val = val.replace('"', ' ');
                            L.i(TAG, "onIMNotification called. type==2" + val);
                            String str = String.format("%s%s%s", getResources().getString(R.string.queen_top_part),
                                    val, getResources().getString(R.string.queen_bottom_part));
                            L.i(TAG, "onIMNotification called. type==1" + str);

                            Toast.makeText(ZyCallActivity.this,str , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCallReceive(String name, String number, int callIndex) {
                Log.i(TAG,"CallInfo nemoSDKDidReceiveCall callActivity is"+name+"==number=="+number+"==callIndex=="+callIndex);
                callNumber=number;
            }

            @Override
            public void onDualStreamStateChange(int callIndex, NemoDualState state, int mode, String reason) {
                Log.i(TAG,"nemoSDK onDualStreamStateChange CallActivity is=::"+callIndex+"=state="+state+"=mode="+mode+"=reason="+reason);



            }
        });
    }

    @Override
    public void hangup() {
        finish();

    }

    public enum VideoStatus {
        VIDEO_STATUS_NORMAL(0), VIDEO_STATUS_LOW_AS_LOCAL_BW(1), VIDEO_STATUS_LOW_AS_LOCAL_HARDWARE(2), VIDEO_STATUS_LOW_AS_REMOTE(3),VIDEO_STATUS_NETWORK_ERROR(4),VIDEO_STATUS_LOCAL_WIFI_ISSUE(5);
        private int status;

        private VideoStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }


//    public interface CallListener {
//
//        void onContentStateChanged(NemoSDKListener.ContentState contentState);
//
//        void onNewContentReceive(Bitmap bitmap);
//
//        void onVideoDataSourceChange(List<VideoInfo> videoInfos);
//    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        L.i(TAG, "onBackPressed==");
        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                super.onBackPressed();
            }else{
                getSupportFragmentManager().popBackStack();
                NemoSDK.getInstance().logout();
            }
        }
        finish();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean isIncomingCall = intent.getBooleanExtra("isIncomingCall", false);
        if (isIncomingCall) {
            int callIndex = intent.getIntExtra("callIndex", -1);
            String callerName = intent.getStringExtra("callerName");
            String callerNumber = intent.getStringExtra("callerNumber");
            showIncomingCallDialog(callIndex, callerNumber, callerName);
        }
    }


    private void showIncomingCallDialog(final int callIndex, String callerNumber, String callerName) {
        new AlertDialog.Builder(ZyCallActivity.this)
                .setTitle("来电")
                .setMessage("呼叫人: " + callerName + "\n" + "来电号码： " + callerNumber)
                .setNegativeButton("接听", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NemoSDK.getInstance().answerCall(callIndex, true);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NemoSDK.getInstance().answerCall(callIndex, false);
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
