package com.zyzxsp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ainemo.sdk.otf.NemoSDK;
import com.zyzxsp.R;
import com.zyzxsp.utils.AlertUtil;

public class FeedbackActivity extends Activity {

    //private UploadLogUtil uploadLogUtil;
    private Button mSendFeedbackButton;
    private EditText FeedbackEditText;
    private TextWatcher textWatcher;
    private LinearLayout mFeedBackLayout;
    private static Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mSendFeedbackButton = (Button) findViewById(R.id.send_feedback_bt);
        mFeedBackLayout = (LinearLayout) findViewById(R.id.action_layout);
        textWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (FeedbackEditText.getText().length() > 0) {
                    mSendFeedbackButton.setEnabled(true);
                } else {
                    mSendFeedbackButton.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        };

        FeedbackEditText = (EditText) findViewById(R.id.FeedbackEditText);
        FeedbackEditText.addTextChangedListener(textWatcher);

        //FeedbackEditText.
        mSendFeedbackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });

        mSendFeedbackButton.setEnabled(false);
        mSendFeedbackButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // uploadLogUtil.startZipUploadLogs("Android_feedback_" + VersionUtil.getVersionName(FeedbackActivity.this) + "_" + FeedbackEditText.getText().toString());
                return false;
            }
        });


        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setTitle(R.string.feedback);

        mFeedBackLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void sendFeedback() {
        Log.i("TAG", "sendFeedback Android_feedback_=" + FeedbackEditText.getText().toString());
        NemoSDK.getInstance().sendFeedbackLog(FeedbackEditText.getText().toString());
        AlertUtil.toastText(R.string.feedback_success);
        finish();
    }

//    @Override
//    protected void onViewAndServiceReady(IServiceAIDL service) {
//        super.onViewAndServiceReady(service);
//        uploadLogUtil = new UploadLogUtil(FeedbackActivity.this, null, Uris.getDebugLogUpload().toString(), UploadLogUtil.UploadType.UploadTypeLog);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    protected void onMessage(Message msg) {
//        super.onMessage(msg);
//    }


}
