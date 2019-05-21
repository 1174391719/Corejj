package com.zyzxsp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.ainemo.sdk.otf.NemoSDK;
import com.zyzxsp.R;
import com.zyzxsp.activity.ZyCallActivity;
import com.zyzxsp.dialog.DialogPresenter;
import com.zyzxsp.dialog.DialogPresenterImpl;
import com.zyzxsp.utils.PermissionUtils;
import com.zyzxsp.utils.StatusBarUtils;
import com.zyzxsp.utils.Utils;
import com.zyzxsp.utils.ZLog;

public class MeetingFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "MeetingFragment";
    public static final String CLOSE_CAMERA = "close_camera";
    public static final String CLOSE_VOICE = "close_voice";
    private View mView;
    private EditText mMeetNumberEdit;
    private Button mJionMeetBtn;
    private CheckBox mClcoseCameraCheckBox;
    private CheckBox mCloseVoiceCheckBox;
    private View mHeadView = null;

    private CallNumberInterface callBack;

    private String myNumber;
    private String mCallNumber;
    private String mDisplayName;
    private boolean mClcoseCamera = false;
    private boolean mCloseVoice = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_meeting_layout, container, false);
        }
        mHeadView = mView.findViewById(R.id.meet_header_view);
        mMeetNumberEdit = mView.findViewById(R.id.input_meet_number);
        mJionMeetBtn = mView.findViewById(R.id.join_meet);
        mClcoseCameraCheckBox = mView.findViewById(R.id.close_camera);
        mCloseVoiceCheckBox = mView.findViewById(R.id.close_voice);

        mHeadView.setPadding(0, StatusBarUtils.getStateBarHeight(getContext()), 0, 0);

        mJionMeetBtn.setOnClickListener(this);
        mClcoseCameraCheckBox.setOnCheckedChangeListener(listener);
        mCloseVoiceCheckBox.setOnCheckedChangeListener(listener);
        return mView;
    }

    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            if (R.id.close_camera == id) {
                Log.d(TAG, "11111 摄像头 onCheckedChanged:  isChecked   " + isChecked);
                if (isChecked) {
                    Toast.makeText(getContext(), getContext().getString(R.string.join_meet_will_close_camera_str), Toast.LENGTH_LONG).show();
                    mClcoseCamera = true;
                } else {
                    mClcoseCamera = false;
                }
            }
            if (R.id.close_voice == id) {
                Log.d(TAG, "11111 麦克风 onCheckedChanged:  isChecked   " + isChecked);
                if (isChecked) {
                    Toast.makeText(getContext(), getContext().getString(R.string.join_meet_will_close_voice_str), Toast.LENGTH_LONG).show();
                    mCloseVoice = true;
                } else {
                    mCloseVoice = false;
                }
            }

        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.join_meet:
                if (mMeetNumberEdit.getText().toString().trim().length() == 0) {
                    Toast.makeText(getActivity(), "请输入呼叫号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Utils.isNetworkConnected(getContext())) {
                    DialogPresenter dialogPresenter = new DialogPresenterImpl();
                    dialogPresenter.confirm(getContext(), null, "网络连接异常，请重试", "确定");
                    return;
                }
                if (!PermissionUtils.allowJoinMeeting(getActivity())) {
                    PermissionUtils.checkPermission(getActivity());
                    return;
                }
                mCallNumber = mMeetNumberEdit.getText().toString();
                NemoSDK.getInstance().makeCall(mCallNumber, "");
                NemoSDK.getInstance().setPortraitLandscape(false);//设置横屏
                Intent intent = new Intent(getContext(), ZyCallActivity.class);
                intent.putExtra(CLOSE_CAMERA, mClcoseCamera);
                intent.putExtra(CLOSE_VOICE, mCloseVoice);
                intent.putExtra("MY_NUMBER", mMeetNumberEdit.getText().toString());
                ZLog.d("跳转时  mCloseCamera   " + mClcoseCamera + "   mCloseVoice  " + mCloseVoice);
                startActivity(intent);
                break;
        }
    }

    public void setMyNumber(String myNumber) {
        this.myNumber = myNumber;
    }

    public void setDisplayName(String displayName) {
        this.mDisplayName = displayName;
    }

    /*接口*/
    public interface CallNumberInterface {
        /*定义一个获取信息的方法*/
        public void getResult(String callNumber);

        public void getDisplayName(String displayName);

    }

    /*设置监听器*/
    public void setCallBack(CallNumberInterface callBack) {
        /*获取文本框的信息,当然你也可以传其他类型的参数,看需求咯*/
        this.callBack = callBack;

    }

}
