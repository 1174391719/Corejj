package com.zyzxsp.utils;


/**
 * Created by chenshuliang on 2018/4/11.
 * teim
 */

public class CommonTime {

    public static String formatDuration(final long time) {
        int arg = ((Long) time).intValue();
        int t = arg / 1000;
        int minute = t / 60;
        int hour = minute / 60;
        int second = t % 60;
        StringBuffer result = new StringBuffer();
        if (hour > 0) {
            result.append(hour < 10 ? "0" + hour : hour);
            result.append(":");
            minute = minute % 60;
        }
        result.append(hour < 10 ? "0" + hour : hour);
        result.append(":");
        result.append(minute < 10 ? "0" + minute : minute);
        result.append(":");
        result.append(second < 10 ? "0" + second : second);
        return result.toString();
    }
}
