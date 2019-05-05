package com.zyzxsp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zyzxsp.R;
import com.zyzxsp.view.HeaderTitleView;

public class MyCloudMeettingActivity extends AppCompatActivity {

    private HeaderTitleView mHeaderTitleView;
    private TextView mMeetNameText;
    private TextView mMeetNumberText;
    private TextView mMeetCapacityText;
    private TextView mMeetPasswordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cloud_meetting);
        mHeaderTitleView = findViewById(R.id.my_cloud_meet_header_view);
        mMeetNameText = findViewById(R.id.meet_name_text);
        mMeetNumberText = findViewById(R.id.meet_number_text);
        mMeetCapacityText = findViewById(R.id.meet_capacity_text);
        mMeetPasswordText = findViewById(R.id.meet_password_text);

        mHeaderTitleView.setmOnHeaderTitleViewClick(new HeaderTitleView.onHeaderTitleViewClick() {
            @Override
            public void leftClick(View view) {
                finish();
            }

            @Override
            public void rightClick(View view) {

            }
        });

    }
}
