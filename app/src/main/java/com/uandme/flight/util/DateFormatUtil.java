package com.uandme.flight.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by QingYang on 15/8/16.
 */
public class DateFormatUtil {

    private static SimpleDateFormat sdfZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static SimpleDateFormat sdfT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static String formatTDate() {
        return  sdfT.format(new Date());
    }
}
