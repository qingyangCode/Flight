package com.uandme.flight.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by QingYang on 15/8/16.
 */
public class DateFormatUtil {

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
}
