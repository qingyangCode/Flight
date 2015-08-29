package com.xiaoqing.flight.util;

import java.text.DecimalFormat;

/**
 * Created by QingYang on 15/8/20.
 */
public class FormatUtil {

    private static DecimalFormat def = new DecimalFormat("0.00");


    public static String formatTo2Decimal(double number) {
        return def.format(number);
    }

}
