package com.zyzxsp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ainemo.sdk.otf.NemoSDK;
import com.zyzxsp.utils.BackHandledFragment;

import com.zyzxsp.activity.FeedbackActivity;


/**
 * 拨号界面
 */
public class DialFragment extends BackHandledFragment {

    private static final String TAG = "DialFragment";
    private String myNumber;
    private String mCallNumber;
    private CallNumberInterface callBack;
    private boolean hadIntercept;
    private String mDisplayName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dial_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (null != myNumber)
            ((TextView) view.findViewById(R.id.local_number)).setText("我的号码：" + myNumber);

        final EditText number = (EditText) view.findViewById(R.id.number);
        final EditText password = (EditText) view.findViewById(R.id.password);
       // NemoSDK.getInstance().setSpeakerOnModeDefault(false);

        view.findViewById(R.id.make_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (number.getText().toString().trim().length() == 0) {
                    Toast.makeText(getActivity(), "请输入呼叫号码", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkPermission();
                mCallNumber = number.getText().toString();
                callBack.getResult(mCallNumber);
                callBack.getDisplayName(mDisplayName);
                NemoSDK.getInstance().setPortraitLandscape(false);//设置横屏
                NemoSDK.getInstance().makeCall(mCallNumber, password.getText().toString());


            }
        });
        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.bt_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NemoSDK.getInstance().logout();
                getActivity().finish();
            }
        });
        super.onViewCreated(view, savedInstanceState);
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


    @Override
    public boolean onBackPressed() {
        if (hadIntercept) {
            return false;
        } else {
            hadIntercept = true;
            return true;
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
