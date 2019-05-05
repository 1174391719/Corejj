package com.zyzxsp.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.zyzxsp.R;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CallStatisticsView extends RelativeLayout {

    private TextView statisticsText;

    private TextView pvtxTV;
    private String pvtxKey = "videoTxStatisticsList";
    private TextView pvrxTV;
    private String pvrxKey = "videoRxStatisticsList";
    private TextView atxTV;
    private String atxKey = "audioTxStatisticsList";
    private TextView arxTV;
    private String arxKey = "audioRxStatisticsList";
    private TextView othersTV;
    private String resourceKey = "resourceStatistics";
    private String signalKey = "signalStatistics";
    private String natKey = "natStatistics";
    private TextView dbaTV;
    private String dbaKey = "dbaStatistics";
    private DisplayChannelEnum currDispChannel = DisplayChannelEnum.pvtx;
    private Map<String, Object> mediaStatisticsMap;
    private ImageView mCloseBtn;
    private AtomicBoolean needClearStis = new AtomicBoolean(false);
    private StringBuffer currTypeStatisticsInfo;

    public CallStatisticsView(Context context) {
        super(context);
        initView(context);
    }

    public CallStatisticsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CallStatisticsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void updateCurrDispChannel(DisplayChannelEnum c) {
        if (c == DisplayChannelEnum.dba) {
            needClearStis.set(true);
        }
        currDispChannel = c;
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.call_statistics, this);

        statisticsText = (TextView) findViewById(R.id.statistics_textview);
        pvtxTV = (TextView) findViewById(R.id.call_statistics_pvtx);
        pvtxTV.setClickable(true);
        pvtxTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrDispChannel(DisplayChannelEnum.pvtx);
                setActiveState(pvtxTV);
                updateStatistics(mediaStatisticsMap);
            }
        });
        pvrxTV = (TextView) findViewById(R.id.call_statistics_pvrx);
        pvrxTV.setClickable(true);
        pvtxTV.setTextColor(Color.parseColor("#ff000000"));
        pvrxTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrDispChannel(DisplayChannelEnum.pvrx);
                setActiveState(pvrxTV);
                updateStatistics(mediaStatisticsMap);
            }
        });
        atxTV = (TextView) findViewById(R.id.call_statistics_atx);
        atxTV.setClickable(true);
        atxTV.setTextColor(Color.parseColor("#ff000000"));
        atxTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrDispChannel(DisplayChannelEnum.atx);
                setActiveState(atxTV);
                updateStatistics(mediaStatisticsMap);
            }
        });
        arxTV = (TextView) findViewById(R.id.call_statistics_arx);
        arxTV.setClickable(true);
        arxTV.setTextColor(Color.parseColor("#ff000000"));
        arxTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrDispChannel(DisplayChannelEnum.arx);
                setActiveState(arxTV);
                updateStatistics(mediaStatisticsMap);
            }
        });
        othersTV = (TextView) findViewById(R.id.call_statistics_others);
        othersTV.setClickable(true);
        othersTV.setTextColor(Color.parseColor("#ff000000"));
        othersTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrDispChannel(DisplayChannelEnum.others);
                setActiveState(othersTV);
                updateStatistics(mediaStatisticsMap);
            }
        });

        dbaTV = (TextView) findViewById(R.id.call_statistics_dba);
        dbaTV.setClickable(true);
        dbaTV.setTextColor(Color.parseColor("#ff000000"));
        dbaTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrDispChannel(DisplayChannelEnum.dba);
                setActiveState(dbaTV);
                updateStatistics(mediaStatisticsMap);
            }
        });

        mCloseBtn = (ImageView) findViewById(R.id.statistics_close_btn);

        setActiveState(pvtxTV);
    }

    private void setActiveState(TextView activeTV) {
        if (activeTV == pvtxTV) {
            pvtxTV.setActivated(true);
            pvtxTV.setTextColor(Color.parseColor("#ffffffff"));
        } else {
            pvtxTV.setActivated(false);
            pvtxTV.setTextColor(Color.parseColor("#ff000000"));
        }

        if (activeTV == pvrxTV) {
            pvrxTV.setActivated(true);
            pvrxTV.setTextColor(Color.parseColor("#ffffffff"));
        } else {
            pvrxTV.setActivated(false);
            pvrxTV.setTextColor(Color.parseColor("#ff000000"));
        }

        if (activeTV == atxTV) {
            atxTV.setActivated(true);
            atxTV.setTextColor(Color.parseColor("#ffffffff"));
        } else {
            atxTV.setActivated(false);
            atxTV.setTextColor(Color.parseColor("#ff000000"));
        }

        if (activeTV == arxTV) {
            arxTV.setActivated(true);
            arxTV.setTextColor(Color.parseColor("#ffffffff"));
        } else {
            arxTV.setActivated(false);
            arxTV.setTextColor(Color.parseColor("#ff000000"));
        }

        if (activeTV == othersTV) {
            othersTV.setActivated(true);
            othersTV.setTextColor(Color.parseColor("#ffffffff"));
        } else {
            othersTV.setActivated(false);
            othersTV.setTextColor(Color.parseColor("#ff000000"));
        }

        if (activeTV == dbaTV) {
            dbaTV.setActivated(true);
            dbaTV.setTextColor(Color.parseColor("#ffffffff"));
        } else {
            dbaTV.setActivated(false);
            dbaTV.setTextColor(Color.parseColor("#ff000000"));
        }
    }

    public void updateStatistics(Map<String, Object> ms) {

        Log.i("currTypeStatisticsInfo1","======"+ms);
        if (currDispChannel == DisplayChannelEnum.dba) {
            if (needClearStis.getAndSet(false)) {
                currTypeStatisticsInfo = new StringBuffer();
            }
        } else {
            currTypeStatisticsInfo = new StringBuffer();
        }
        getCurrTypeStatInof(ms);

        mediaStatisticsMap = ms;

        Log.i("currTypeStatisticsInfo2","======"+currTypeStatisticsInfo.toString());
        statisticsText.setText(currTypeStatisticsInfo.toString());
    }

    public void clearData() {
        currTypeStatisticsInfo = new StringBuffer();
        statisticsText.setText("");
    }

    private void getCurrTypeStatInof(Map<String, Object> ms) {
        Log.i("currTypeStatisticsInfo5","======"+ms);
        if (currDispChannel == DisplayChannelEnum.pvtx) {

            Object value = ms.get(pvtxKey);
            Log.i("currTypeStatisticsInfo3","======"+value);
            currTypeStatisticsInfo.append(value);
        } else if (currDispChannel == DisplayChannelEnum.pvrx) {
            Object value = ms.get(pvrxKey);
            Log.i("currTypeStatisticsInfo4","======"+value);
            currTypeStatisticsInfo.append(value);
        } else if (currDispChannel == DisplayChannelEnum.atx) {
            Object value = ms.get(atxKey);
            currTypeStatisticsInfo.append(value);
        } else if (currDispChannel == DisplayChannelEnum.arx) {
            Object value = ms.get(arxKey);
            currTypeStatisticsInfo.append(value);
        } else if (currDispChannel == DisplayChannelEnum.dba) {
            Object value = ms.get(dbaKey);
            currTypeStatisticsInfo.append(value);
        } else {
            // get others pannel info
            Object value = ms.get(resourceKey);
            currTypeStatisticsInfo.append(value).append("\n");
            value = ms.get(signalKey);
            currTypeStatisticsInfo.append(value).append("\n");
            value = ms.get(natKey);
            currTypeStatisticsInfo.append(value).append("\n");
        }
    }

    public void setOnCloseListener(OnClickListener listener) {
        mCloseBtn.setOnClickListener(listener);
        Log.i("callStatisticsView","callStatisticsView2");
    }

    enum DisplayChannelEnum {
        pvtx, pvrx, atx, arx, others, dba
    }
}
