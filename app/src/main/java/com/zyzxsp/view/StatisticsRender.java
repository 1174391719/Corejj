package com.zyzxsp.view;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewStub;

import com.ainemo.module.call.data.NewStatisticsInfo;
import com.ainemo.sdk.utils.Base64Utils;
import com.zyzxsp.R;
import com.zyzxsp.utils.Optionals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//网络探测
public class StatisticsRender {
    private static final String TAG = "StatisticsRender";

    public static final String KEY_BIT_WIDTH = "bitWidth";
    public static final String KEY_PACKAGE_LOST = "packageLost";
    public static final String KEY_RTT = NewStatisticsInfo.KEY_RTT;
    public static final String KEY_JITTER = "jitter";
    public static final String KEY_NAME = "name";

    private final ViewStub mViewStub;
    private StringMatrixView mNetworkView;
    private StringMatrixView mContentView;
    private StringMatrixView mPeopleView;
    private View mParent;
    private final List<Pair<String, String>> mContentColumns;
    private final List<Pair<String, String>> mPeopleColumns;
    private final List<Pair<String, String>> mNetworkColumns;

    private StatisticsOperationListener mStatisticsActionListener;
    private View contentTitle;
    private View closeBtn;

    public interface StatisticsOperationListener {
        void stopStatisticsInfo();
    }

    public StatisticsRender(ViewStub viewStub, StatisticsOperationListener actionListener) {
        this.mStatisticsActionListener = actionListener;
        mViewStub = viewStub;
        Context ctx = viewStub.getContext();
        mContentColumns = new ArrayList<>(5);
        mContentColumns.add(new Pair<>(KEY_NAME, ctx.getString(R.string.statistics_channel_name)));
        mContentColumns.add(new Pair<>(NewStatisticsInfo.KEY_CODEC_TYPE, ctx.getString(R.string.statistics_codec)));
        mContentColumns.add(new Pair<>(NewStatisticsInfo.KEY_RESOLUTION, ctx.getString(R.string.statistics_resolution)));
        mContentColumns.add(new Pair<>(NewStatisticsInfo.KEY_FRAME_RATE, ctx.getString(R.string.statistics_frame_rate)));
        mContentColumns.add(new Pair<>(NewStatisticsInfo.KEY_ACT_BW, ctx.getString(R.string.statistics_band_width)));

        mPeopleColumns = new ArrayList<>(5);
        mPeopleColumns.add(new Pair<>(NewStatisticsInfo.KEY_DIS_NAME, ""));
        mPeopleColumns.add(new Pair<>(KEY_NAME, ctx.getString(R.string.statistics_channel_name)));
        mPeopleColumns.add(new Pair<>(NewStatisticsInfo.KEY_CODEC_TYPE, ctx.getString(R.string.statistics_codec)));
        mPeopleColumns.add(new Pair<>(NewStatisticsInfo.KEY_RESOLUTION, ctx.getString(R.string.statistics_resolution)));
        mPeopleColumns.add(new Pair<>(NewStatisticsInfo.KEY_FRAME_RATE, ctx.getString(R.string.statistics_frame_rate)));
        mPeopleColumns.add(new Pair<>(NewStatisticsInfo.KEY_ACT_BW, ctx.getString(R.string.statistics_bit_rate)));

        mNetworkColumns = new ArrayList<>(5);
        mNetworkColumns.add(new Pair<>(KEY_NAME, ctx.getString(R.string.statistics_channel_name)));
        mNetworkColumns.add(new Pair<>(KEY_BIT_WIDTH, ctx.getString(R.string.statistics_band_width)));
        mNetworkColumns.add(new Pair<>(KEY_PACKAGE_LOST, ctx.getString(R.string.statistics_package_lost)));
        mNetworkColumns.add(new Pair<>(KEY_RTT, ctx.getString(R.string.statistics_rtt)));
        mNetworkColumns.add(new Pair<>(KEY_JITTER, ctx.getString(R.string.statistics_jitter)));
    }

    public void show() {
        if (mParent == null) {
            mParent = mViewStub.inflate();
            if (closeBtn == null) {
                closeBtn = mParent.findViewById(R.id.close_btn);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hide();
                        if (mStatisticsActionListener != null) {
                            mStatisticsActionListener.stopStatisticsInfo();
                        }
                    }
                });
            }
            mNetworkView = (StringMatrixView) mParent.findViewById(R.id.statistics_network);
            mContentView = (StringMatrixView) mParent.findViewById(R.id.statistics_content);
            mPeopleView = (StringMatrixView) mParent.findViewById(R.id.statistics_participant);
        }
        mParent.setVisibility(View.VISIBLE);
        mParent.requestFocus();
    }

    public void hide() {
        if (mParent != null) {
            mParent.setVisibility(View.INVISIBLE);
            mNetworkView.setValues(null, null);
            mPeopleView.setValues(null, null);
            mContentView.setValues(null, null);
        }
    }

    public void onValue(NewStatisticsInfo allInfo) {
    Log.i(TAG,"onValue NewStatisticsInfo=="+allInfo);

        if (mParent != null && mParent.getVisibility() == View.VISIBLE) {
            setNetworkValue(allInfo.networkInfo);
            if (contentTitle == null) {  // 内容共享
                contentTitle = mParent.findViewById(R.id.title_content);
            }
            contentTitle.setVisibility(allInfo.hasContent() ? View.VISIBLE : View.GONE);
            setContentValue(allInfo.content);
            setPeopleValues(allInfo.people);
            mParent.requestLayout();
        }
    }


    private void setContentValue(Map<String, List<Map<String, Object>>> content) {
        Log.i(TAG, "LISTKEY_AUDIO_RX_INFOS setContentValue atx setPeopleValues1==" +content);
        Context ctx = mContentView.getContext();

        List<Map<String, Object>> peopleInfo = new ArrayList<>();
        List<Map<String, Object>> atx = content.get(NewStatisticsInfo.KEY_AUDIO_TX_INFO);

        if (!Optionals.isEmtpy(atx)) {
            int size = atx.size();
            String name = ctx.getString(R.string.statistics_atx);
            for (int i = 0; i < size; i++) {
                Map<String, Object> value = atx.get(i);
                Log.i(TAG, "KEY_AUDIO_RX_INFOS setContentValue atx setPeopleValues1==" +value);
                String str=String.valueOf(value.get(NewStatisticsInfo.KEY_ACT_BW));
                value.put(KEY_NAME, name);
                value.put(NewStatisticsInfo.KEY_ACT_BW, str.substring(0,str.length()-2));
            }
            peopleInfo.addAll(atx);
        }

        List<Map<String, Object>> arx = content.get(NewStatisticsInfo.KEY_AUDIO_RX_INFO);
        if (!Optionals.isEmtpy(arx)) {
            int size = arx.size();
            String name = ctx.getString(R.string.statistics_arx);
            for (int i = 0; i < size; i++) {
                Map<String, Object> value = arx.get(i);
                Log.i(TAG, "KEY_AUDIO_RX_INFOS setContentValue atx setPeopleValues2==" +value);
                String str=String.valueOf(value.get(NewStatisticsInfo.KEY_ACT_BW));
                value.put(KEY_NAME, name);
                value.put(NewStatisticsInfo.KEY_ACT_BW, str.substring(0,str.length()-2));
                base64DecodeValue(value);
            }
            peopleInfo.addAll(arx);
        }

        List<Map<String, Object>> vtx = content.get(NewStatisticsInfo.KEY_VIDEO_TX_INFO);
        if (!Optionals.isEmtpy(vtx)) {
            int size = vtx.size();
            String name = ctx.getString(R.string.statistics_vtx);
            for (int i = 0; i < size; i++) {
                Map<String, Object> value = vtx.get(i);
                Log.i(TAG, "KEY_AUDIO_RX_INFOS setContentValue atx setPeopleValues3==" +value);
                String str=String.valueOf(value.get(NewStatisticsInfo.KEY_ACT_BW));
                value.put(KEY_NAME, name);
                value.put(NewStatisticsInfo.KEY_ACT_BW, str.substring(0,str.length()-2));
            }
            peopleInfo.addAll(vtx);
        }

        List<Map<String, Object>> vrx = content.get(NewStatisticsInfo.KEY_VIDEO_RX_INFO);
        if (!Optionals.isEmtpy(vrx)) {
            int size = vrx.size();
            String name = ctx.getString(R.string.statistics_vrx);
            for (int i = 0; i < size; i++) {
                Map<String, Object> value = vrx.get(i);
                Log.i(TAG, "KEY_AUDIO_RX_INFOS setContentValue atx setPeopleValues4==" +value);
                String str=String.valueOf(value.get(NewStatisticsInfo.KEY_ACT_BW));
                value.put(KEY_NAME, name);
                value.put(NewStatisticsInfo.KEY_ACT_BW, str.substring(0,str.length()-2));
                base64DecodeValue(value);
            }
            peopleInfo.addAll(vrx);
        }
        Log.i(TAG, "onValue setContentValue==" + peopleInfo + "==mPeopleColumns==" + mContentColumns + "==content==" + content);
        mContentView.setValues(mContentColumns, peopleInfo);
    }

    private void setPeopleValues(Map<String, List<Map<String, Object>>> people) {
        Log.i(TAG, "LISTMAP onValue atx setPeopleValues people2222=="+people);
        Context ctx = mPeopleView.getContext();

        List<Map<String, Object>> peopleInfo = new ArrayList<>();



        String localName = ctx.getString(R.string.statistics_local_name);
        List<Map<String, Object>> atx = people.get(NewStatisticsInfo.KEY_AUDIO_TX_INFO);
        if (!Optionals.isEmtpy(atx)) {
            int size = atx.size();
            String name = ctx.getString(R.string.statistics_atx);
            for (int i = 0; i < size; i++) {
                Map<String, Object> value = atx.get(i);
                Log.i(TAG, "onValue atx setPeopleValues2==" +value);
                String str=String.valueOf(value.get(NewStatisticsInfo.KEY_ACT_BW));
               // String frameRate=String.valueOf(value.get(NewStatisticsInfo.KEY_FRAME_RATE));
                Log.i(TAG, "onValue atx setPeopleValues3==" +str.substring(0,str.length()-2));
                value.put(KEY_NAME, name);
                value.put(NewStatisticsInfo.KEY_DIS_NAME, localName);
                value.put(NewStatisticsInfo.KEY_ACT_BW, str.substring(0,str.length()-2));
               // value.put(NewStatisticsInfo.KEY_FRAME_RATE, frameRate.substring(0,frameRate.length()-2));
            }
            peopleInfo.addAll(atx);
        }

        List<Map<String, Object>> arx = people.get(NewStatisticsInfo.KEY_AUDIO_RX_INFO);
        if (!Optionals.isEmtpy(arx)) {
            int size = arx.size();
            String name = ctx.getString(R.string.statistics_arx);
            for (int i = 0; i < size; i++) {
                Log.i(TAG,"String cslName onValue ==KEY_ATX_JITTER=="+arx.get(i));
                Map<String, Object> value = arx.get(i);
                Log.i(TAG, "String cslName onValue atx setPeopleValues4==" +value);
                String mJitter=String.valueOf(value.get(NewStatisticsInfo.KEY_ACT_BW));
                //String frameRate=String.valueOf(value.get(NewStatisticsInfo.KEY_FRAME_RATE));
                Log.i(TAG, "String cslName onValue atx setPeopleValues5==" +mJitter.substring(0,mJitter.length()-2));
                value.put(KEY_NAME, name);
                value.put(NewStatisticsInfo.KEY_ACT_BW, mJitter.substring(0,mJitter.length()-2));
                //value.put(NewStatisticsInfo.KEY_FRAME_RATE, frameRate.substring(0,frameRate.length()-2));
                base64DecodeValue(value);
            }
            peopleInfo.addAll(arx);
        }

        List<Map<String, Object>> vtx = people.get(NewStatisticsInfo.KEY_VIDEO_TX_INFO);
        if (!Optionals.isEmtpy(vtx)) {
            int size = vtx.size();
            String name = ctx.getString(R.string.statistics_vtx);
            for (int i = 0; i < size; i++) {
                Map<String, Object> value = vtx.get(i);
                Log.i(TAG, "KEY_VIDEO_TX_INFO cslName onValue atx setPeopleValues6==" +value);
                String str=String.valueOf(value.get(NewStatisticsInfo.KEY_ACT_BW));
                String frameRate=String.valueOf(value.get(NewStatisticsInfo.KEY_FRAME_RATE));
                value.put(KEY_NAME, name);
                value.put(NewStatisticsInfo.KEY_DIS_NAME, localName);
                value.put(NewStatisticsInfo.KEY_ACT_BW, str.substring(0,str.length()-2));
                value.put(NewStatisticsInfo.KEY_FRAME_RATE, frameRate.substring(0,frameRate.length()-2));
            }
            peopleInfo.addAll(vtx);
        }

        List<Map<String, Object>> vrx = people.get(NewStatisticsInfo.KEY_VIDEO_RX_INFO);
        if (!Optionals.isEmtpy(vrx)) {
            int size = vrx.size();
            String name = ctx.getString(R.string.statistics_vrx);
            for (int i = 0; i < size; i++) {
                Map<String, Object> value = vrx.get(i);
                String str=String.valueOf(value.get(NewStatisticsInfo.KEY_ACT_BW));
                String frameRate=String.valueOf(value.get(NewStatisticsInfo.KEY_FRAME_RATE));
                value.put(KEY_NAME, name);
                value.put(NewStatisticsInfo.KEY_ACT_BW, str.substring(0,str.length()-2));
                value.put(NewStatisticsInfo.KEY_FRAME_RATE, frameRate.substring(0,frameRate.length()-2));
                base64DecodeValue(value);
            }
            peopleInfo.addAll(vrx);
        }
        Log.i(TAG, "onValue22 setPeopleValues==" + peopleInfo + "==mPeopleColumns==" + mPeopleColumns + "==people==");

        mPeopleView.setValues(mPeopleColumns, peopleInfo);
    }

    private void setNetworkValue(Map<String, Object> info) {
        Context ctx = mNetworkView.getContext();
        List<Map<String, Object>> networkInfos = new ArrayList<>(2);






        HashMap<String, Object> send = new HashMap<>(5);
        String keyTXDetedtBW = String.valueOf(info.get(NewStatisticsInfo.KEY_TX_DETECT_BW));
        String txLost=String.valueOf(info.get(NewStatisticsInfo.KEY_TX_LOST));
        String rtt=String.valueOf(info.get(NewStatisticsInfo.KEY_RTT));
        String txJitter=String.valueOf(info.get(NewStatisticsInfo.KEY_RTT));
        Log.i(TAG,"keyTXDetedtBW=="+keyTXDetedtBW);
        send.put(KEY_NAME, ctx.getString(R.string.statistics_tx));
        send.put(KEY_BIT_WIDTH, keyTXDetedtBW.substring(0, keyTXDetedtBW.length() - 2));
        send.put(KEY_PACKAGE_LOST,txLost.substring(0,txLost.length()-2)) ;
        send.put(KEY_RTT, rtt.substring(0,rtt.length()-2));
        send.put(KEY_JITTER, txJitter.substring(0,txJitter.length()-2));
        networkInfos.add(send);

        HashMap<String, Object> receive = new HashMap<>(5);

        String rxDetectBw = String.valueOf(info.get(NewStatisticsInfo.KEY_RX_DETECT_BW));
        String rxLost=String.valueOf(info.get(NewStatisticsInfo.RX_LOST));
        String rtts=String.valueOf(info.get(NewStatisticsInfo.KEY_RTT));
        String rxJitter=String.valueOf(info.get(NewStatisticsInfo.KEY_RX_JITTER));

        receive.put(KEY_NAME, ctx.getString(R.string.statistics_rx));
        receive.put(KEY_BIT_WIDTH, rxDetectBw.substring(0,rxDetectBw.length()-2));
        receive.put(KEY_PACKAGE_LOST, rxLost.substring(0,rxLost.length()-2));
        receive.put(KEY_RTT, rtts.substring(0,rtts.length()-2));
        receive.put(KEY_JITTER, rxJitter.substring(0,rxJitter.length()-2));
        networkInfos.add(receive);
        mNetworkView.setValues(mNetworkColumns, networkInfos);
    }



    /**
     * @param map
     * @return String
     * @Description: Map按key进行排序拼接value(无拼接符)
     */
    public static String sortStringByKeyGetValueNoSplice(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                return str1.compareTo(str2);
            }
        });
        sortMap.putAll(map);
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : sortMap.entrySet()) {
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

    private void base64DecodeValue(Map<String, Object> value) {
        if (null == value) {
            return;
        }
        for (String k : value.keySet()) {
            if (k.equals(NewStatisticsInfo.KEY_DIS_NAME)) {  // disName字段Base64解码
                if (value.get(k) instanceof String) {
                    value.put(k, Base64Utils.decode(value.get(k).toString(), ""));
                    break;
                }
            }
        }
    }

}
