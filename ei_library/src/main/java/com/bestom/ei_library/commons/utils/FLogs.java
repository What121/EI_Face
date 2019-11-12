package com.bestom.ei_library.commons.utils;

import android.util.Log;

/**
 * 日志打印类，需要打印日志的时候请使用此类来处理，以便在不需要打印日志的时候统一关闭日志打印，或者统一设置日志打印级别
 */
public class FLogs {
    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;
    /**
     * 日志开关
     */
    private static boolean logFlagIsOpen = true;
    private static int logLevel = 0;

    /**
     * 打印log
     * @param tag      标记
     * @param msg      消息
     * @param curLevel 消息类型：0:v, 1:d, 2:i, 3:w, 4:e,
     */
    private static void log(String tag, String msg, int curLevel) {
        if (!logFlagIsOpen) {
            return;
        }
        if (curLevel >= logLevel) {
            if (curLevel == INFO) {
                Log.i(tag == null ? "" : tag, msg == null ? "" : msg);
            } else if (curLevel == ERROR) {
                Log.e(tag == null ? "" : tag, msg == null ? "" : msg);
            } else if (curLevel == DEBUG) {
                Log.d(tag == null ? "" : tag, msg == null ? "" : msg);
            } else if (curLevel == VERBOSE) {
                Log.v(tag == null ? "" : tag, msg == null ? "" : msg);
            } else if (curLevel == WARN) {
                Log.w(tag == null ? "" : tag, msg == null ? "" : msg);
            }
        }
    }

    public static void v(String tag, String msg) {
        log(tag, msg, VERBOSE);
    }

    public static void d(String tag, String msg) {
        log(tag, msg, DEBUG);
    }

    public static void i(String tag, String msg) {
        log(tag, msg, INFO);
    }

    public static void w(String tag, String msg) {
        log(tag, msg, WARN);
    }

    public static void e(String tag, String msg) {
        log(tag, msg, ERROR);
    }

    public static void setLogFlag(boolean isOpen) {
        logFlagIsOpen = isOpen;
    }

    /**
     * 设置日志打印的级别
     *
     * @param level 0:v, 1:d, 2:i, 3:w, 4:e,
     */
    public static void setLogLevel(int level) {
        if (level >= 0 && level <= 4) {
            logLevel = level;
        }
    }
}
