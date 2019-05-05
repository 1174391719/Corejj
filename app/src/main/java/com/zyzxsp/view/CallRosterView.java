package com.zyzxsp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zyzxsp.R;

import java.util.Map;

public class CallRosterView extends RelativeLayout {

    private TextView rosterText;
    private ImageView mCloseBtn;

    public CallRosterView(Context context) {
        super(context);
        initView(context);
    }

    public CallRosterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CallRosterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.call_roster, this);
        rosterText = (TextView) findViewById(R.id.roster_textview);
        mCloseBtn = (ImageView) findViewById(R.id.roster_close_btn);
    }

    public void updateRoster(Map<String, Object> stats) {
        String layoutStatistics = stats.get("layoutStatistics").toString();
        rosterText.setText(layoutStatistics);
    }

    public void setOnCloseListener(OnClickListener listener) {
        mCloseBtn.setOnClickListener(listener);
    }
}
