package com.xiaoqing.flight.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by QingYang on 15/8/16.
 */
public class DateFormatUtil {

    public static final int TIME_TWODAYS = 0;
    public static final int TIME_WEEK = 1;
    public static final int TIME_MONTH = 2;
    public static final int TIME_THREEMONTH = 3;
    public static final int TIME_HALFYEAR = 4;
    public static final int TIME_YEAR = 5;
    public static final int TIME_FIVEYEAR = 6;

    private static SimpleDateFormat sdfZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ms Z");
    //private static SimpleDateFormat sdfZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
    private static SimpleDateFormat sdfT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static String formatTDate() {
        String time =  sdfT.format(new Date());
        return time;
    }

    public static String formatZDate() {
        String time =  sdfZ.format(new Date());
        //return time;
        return (time.substring(0,time.length()-2)+":"+time.substring(time.length()-2)).replace(" ", "");
    }

    static Calendar calendar = Calendar.getInstance();

    /**
     * 获取三个月前时间
     * @return
     */
    public static String getTimes(int time) {
        Date dBefore = new Date();
        calendar.setTime(dBefore);//把当前时间赋给日历
        switch (time) {
            case TIME_TWODAYS:
                calendar.add(calendar.DAY_OF_MONTH, -2);
                break;
            case TIME_WEEK://一周前
                calendar.add(calendar.WEEK_OF_YEAR, -1);
                break;
            case TIME_MONTH://一个月前
                calendar.add(calendar.MONTH, -1);
                break;
            case TIME_THREEMONTH://三个月前
                calendar.add(calendar.MONTH, -3);
                break;
            case TIME_HALFYEAR://半年前
                calendar.add(calendar.MONTH, -6);
                break;
            case TIME_YEAR://一年前
                calendar.add(calendar.YEAR, -1);
                break;
            case TIME_FIVEYEAR://五年前
                calendar.add(calendar.YEAR, -5);
                break;
        }
        dBefore = calendar.getTime(); //得到前3月的时间
        String beforetime = sdfZ.format(dBefore);
        return (beforetime.substring(0, beforetime.length()-2) + ":" + beforetime.substring(beforetime.length() - 2)).replace(" ", "");
    }

}
