package com.uandme.flight.util;

import android.util.Log;
import com.uandme.flight.BuildConfig;

public final class LogUtil {

    private static int logLevel = Log.VERBOSE;

    public static void v(String tag, String format, Object... args) {
        if (!BuildConfig.ENABLE_LOG)return;
        if (logLevel <= Log.VERBOSE) {
            Log.v(tag, String.format(null, format, args));
        }
    }

    public static void d(String tag, String format, Object... args) {
        if (!BuildConfig.ENABLE_LOG)return;
        if (logLevel <= Log.DEBUG) {
            Log.d(tag, String.format(null, format, args));
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (!BuildConfig.ENABLE_LOG)return;
        if (logLevel <= Log.INFO) {
            Log.i(tag, String.format(null, format, args));
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (!BuildConfig.ENABLE_LOG)return;
        if (logLevel <= Log.WARN) {
            Log.w(tag, String.format(null, format, args));
        }
    }

    public static void e(String tag, String format, Object... args) {
        if (!BuildConfig.ENABLE_LOG)return;
        if (logLevel <= Log.ERROR) {
            Log.e(tag, String.format(null, format, args));
        }
    }

    public static void setLogLevel(int level) {
        if (level < Log.VERBOSE) {
            logLevel = Log.VERBOSE;
        } else if (level > Log.ERROR) {
            logLevel = Log.ERROR;
        } else {
            logLevel = level;
        }
    }

    public static void LOGD(String tag, String msg) {
        if (BuildConfig.ENABLE_LOG) {
            Log.d("zepptest---" + tag, msg);
        }
    }

    /**
     * 打印方法调用栈
     * @param obj  can not be null
     * @param tag
     * @param msg
     */
    public static void printStackTrace(Object obj, String tag, String msg) {
        if (BuildConfig.ENABLE_LOG && obj != null) {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            StringBuilder sb = new StringBuilder();
            if (elements != null) {
                for (int i = 0; i < elements.length; i++) {
                    sb.append(elements[i].getMethodName()).append(",  ");
                }
            }
            Log.d("zepptest---" + tag, obj.getClass().getSimpleName() + "  [[" + sb.toString() + "]] " + msg);
        }
    }

}
