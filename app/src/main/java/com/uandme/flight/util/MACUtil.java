package com.uandme.flight.util;

/**
 * Created by QingYang on 15/8/16.
 */
public class MACUtil {

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
}
