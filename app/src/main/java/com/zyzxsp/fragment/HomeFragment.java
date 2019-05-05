package com.zyzxsp.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

public class HomeFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "HomeFragment";
    public static final String CLOSE_CAMERA = "close_camera";
    public static final String CLOSE_VOICE = "close_voice";
    private View mView;
    private EditText mMeetNumberEdit;
    private Button mJionMeetBtn;
    private CheckBox mClcoseCameraCheckBox;
    private CheckBox mCloseVoiceCheckBox;

    private CallNumberInterface callBack;

    private String myNumber;
    private String mCallNumber;
    private String mDisplayName;
    private boolean mClcoseCamera = false;
    private boolean mCloseVoice = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView == null){
            mView  = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home_layout,container,false);
        }
        mMeetNumberEdit = mView.findViewById(R.id.input_meet_number);
        mJionMeetBtn = mView.findViewById(R.id.join_meet);
        mClcoseCameraCheckBox = mView.findViewById(R.id.close_camera);
        mCloseVoiceCheckBox = mView.findViewById(R.id.close_voice);

        mJionMeetBtn.setOnClickListener(this);
        mClcoseCameraCheckBox.setOnCheckedChangeListener(listener);
        mCloseVoiceCheckBox.setOnCheckedChangeListener(listener);
        return mView;
    }

    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id  = buttonView.getId();
            if(R.id.close_camera == id){
                Log.d(TAG, "11111 摄像头 onCheckedChanged:  isChecked   " + isChecked);
                if(isChecked){
                    Toast.makeText(getContext(),getContext().getString(R.string.join_meet_will_close_camera_str),Toast.LENGTH_LONG).show();
                    mClcoseCamera = true;
                }else{
                    mClcoseCamera = false;
                }
            }
            if(R.id.close_voice == id){
                Log.d(TAG, "11111 麦克风 onCheckedChanged:  isChecked   " + isChecked);
                if(isChecked){
                    Toast.makeText(getContext(),getContext().getString(R.string.join_meet_will_close_voice_str),Toast.LENGTH_LONG).show();
                    mCloseVoice = true;
                }else{
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
        switch (id){
            case R.id.join_meet:
                if (mMeetNumberEdit.getText().toString().trim().length() == 0) {
                    Toast.makeText(getActivity(), "请输入呼叫号码", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkPermission();
                mCallNumber = mMeetNumberEdit.getText().toString();
//                callBack.getResult(mCallNumber);
//                callBack.getDisplayName(mDisplayName);
//                NemoSDK.getInstance().setPortraitLandscape(false);//设置横屏
////                NemoSDK.getInstance().makeCall(mCallNumber, password.getText().toString());
                NemoSDK.getInstance().makeCall(mCallNumber, "");
                NemoSDK.getInstance().setPortraitLandscape(false);//设置横屏
                Intent intent = new Intent(getContext(),ZyCallActivity.class);
                intent.putExtra(CLOSE_CAMERA,mClcoseCamera);
                intent.putExtra(CLOSE_VOICE,mCloseVoice);
                Log.d(TAG, "11111 跳转时  mClcoseCamera   " + mClcoseCamera +"   mCloseVoice  " +mCloseVoice);
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

    private void checkPermission() {
        if (!(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                !(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 0);
        } else if (!(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        } else if (!(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
        }
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
