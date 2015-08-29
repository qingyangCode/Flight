package com.xiaoqing.flight.util;

/**
 * Created by QingYang on 15/8/16.
 */
public class MACUtil {

    public static final String TYPE_CE560 = "CE560";
    public static final String TYPE_CE680 = "CE680";
    public static final String TYPE_CE750 = "CE750";
    public static final String TYPE_G450 = "G450";

    public static float get560Mac(float weightCg) {
        double mac = (weightCg - 306.59) / 0.8223;
        return (float)mac;
    }

    public static float get680Mac(float weightCg) {
        double mac = (weightCg - 382.68) / 1.0706;
        return (float)mac;
    }

    public static float get750Mac(float weightCg) {

        double mac = (weightCg - 387.60) / 1.1860;
        return (float)mac;
    }

    public static float getG450Mac(float weightCg) {
        double mac = (weightCg - 387.7) / 1.6622;
        return (float) mac;
    }
}
