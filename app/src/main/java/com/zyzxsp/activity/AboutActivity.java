package com.zyzxsp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.zyzxsp.R;
import com.zyzxsp.download.ApkUpdateRequest;
import com.zyzxsp.view.HeaderTitleView;

public class AboutActivity extends AppCompatActivity {

    private HeaderTitleView mHeaderTitleView;
    private RelativeLayout mCheckUpdateLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mHeaderTitleView = findViewById(R.id.about_header_view);
        mCheckUpdateLayout = findViewById(R.id.check_update_layout);

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
