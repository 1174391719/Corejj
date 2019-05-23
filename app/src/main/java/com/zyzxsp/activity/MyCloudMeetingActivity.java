package com.zyzxsp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zyzxsp.R;
import com.zyzxsp.bean.MeetingRoomBean;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.utils.StatusBarUtils;
import com.zyzxsp.utils.ZLog;
import com.zyzxsp.view.HeaderTitleView;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class MyCloudMeetingActivity extends AppCompatActivity {
    private static final String TAG = "MyCloudMeetingActivity";

    private HeaderTitleView mHeaderTitleView;
    private TextView mMeetNameText;
    private TextView mMeetNumberText;
    private TextView mMeetCapacityText;
    private TextView mMeetPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        setContentView(R.layout.activity_my_cloud_meetting);
        mHeaderTitleView = findViewById(R.id.my_cloud_meet_header_view);
        mMeetNameText = findViewById(R.id.meet_name_text);
        mMeetNumberText = findViewById(R.id.meet_number_text);
        mMeetCapacityText = findViewById(R.id.meet_capacity_text);
        mMeetPasswordText = findViewById(R.id.meet_password_text);

        mHeaderTitleView.setPadding(0, StatusBarUtils.getStateBarHeight(this), 0, 0);
        mHeaderTitleView.setmOnHeaderTitleViewClick(new HeaderTitleView.onHeaderTitleViewClick() {
            @Override
            public void leftClick(View view) {
                finish();
            }

            @Override
            public void rightClick(View view) {

            }
        });
        updateInfo();
    }

    public void updateInfo() {
        String url = ConstantUrl.HOST + ConstantUrl.GET_MEETING_INFO;
        ZLog.d("url:" + url);
        Map map = new HashMap();
        map.put("token", ZyMainActivity.sUserBean.getToken());

        OkhttpUtil.okHttpPostJson(url, null, map, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Call mcall = call;
                ZLog.d(e.toString());
            }

            @Override
            public void onResponse(String response) {
                ZLog.d(response);
                if (response == null) {
                    return;
                }
                Gson json = new Gson();
                MeetingRoomBean data = json.fromJson(response, MeetingRoomBean.class);
                if (data == null || !"0".equals(data.getReturnCode())) {
                    return;
                }
                if (data.getObject() == null || data.getObject().getYunConferenceInfo() == null) {
                    return;
                }
                mMeetNameText.setText(data.getObject().getYunConferenceInfo().getConferenceName());
                mMeetCapacityText.setText(data.getObject().getYunConferenceInfo().getConferenceLimt() + "方");
                mMeetNumberText.setText(data.getObject().getYunConferenceInfo().getConferenceAccount());
                mMeetPasswordText.setText(data.getObject().getYunConferenceInfo().getPassword());
            }
        });
    }
}
