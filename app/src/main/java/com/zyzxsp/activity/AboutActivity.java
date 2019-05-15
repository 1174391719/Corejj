package com.zyzxsp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zyzxsp.BuildConfig;
import com.zyzxsp.R;
import com.zyzxsp.download.ApkUpdateRequest;
import com.zyzxsp.utils.StatusBarUtils;
import com.zyzxsp.view.HeaderTitleView;

public class AboutActivity extends AppCompatActivity {

    private HeaderTitleView mHeaderTitleView;
    private RelativeLayout mCheckUpdateLayout;
    private TextView mVersion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        setContentView(R.layout.activity_about);
        mHeaderTitleView = findViewById(R.id.about_header_view);
        mCheckUpdateLayout = findViewById(R.id.check_update_layout);
        mVersion = findViewById(R.id.app_version_text);

        mHeaderTitleView.setPadding(0, StatusBarUtils.getStateBarHeight(this), 0, 0);
        mVersion.setText(BuildConfig.VERSION_NAME);
        mHeaderTitleView.setmOnHeaderTitleViewClick(new HeaderTitleView.onHeaderTitleViewClick() {
            @Override
            public void leftClick(View view) {
                finish();
            }

            @Override
            public void rightClick(View view) {

            }
        });

        mCheckUpdateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApkUpdateRequest.requestUpdateApk(AboutActivity.this);
            }
        });
    }
}
