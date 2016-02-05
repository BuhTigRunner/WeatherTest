package com.borisenkoda.weathertest.helpers;

import android.util.Log;

import com.borisenkoda.weathertest.BuildConfig;

import java.util.Stack;


public class Easy {
    public static final String LOGS_NAME = "LogS";
    public static final String LOG_NAME = "Log";
    private static final boolean enabled = BuildConfig.DEBUG;

    private static Stack<Long> times = new Stack<Long>();


    public static void logD(Object... values) {
        if (BuildConfig.DEBUG)
            Log.d(LOG_NAME, createLogString(values));
    }

    public static void logE(Object... values) {
        if (BuildConfig.DEBUG)
            Log.e(LOG_NAME, createLogString(values));
    }

    public static void logI(Object... values) {
        if (BuildConfig.DEBUG)
            Log.i(LOG_NAME, createLogString(values));
    }

    public static void logE(Throwable throwable) {
        if (BuildConfig.DEBUG)
            Log.e(LOG_NAME, Log.getStackTraceString(throwable));
    }

    public static void logsV() {
        if (!enabled) return;
        logsV("");
    }

    public static void logsV(Object msg) {
        if (!enabled) return;
        if (enabled) Log.v(LOGS_NAME, getLocation() + (msg==null ? "null" : msg.toString()));
    }

    public static void logsV(String msg) {
        if (!enabled) return;
        if (enabled) Log.v(LOGS_NAME, getLocation() + msg);
    }

    public static void logsE(String msg) {
        if (!enabled) return;
        if (enabled) Log.e(LOGS_NAME, getLocation() + msg);
    }

    private static String getLocation() {
        final String className = Easy.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;
        for (int i = 0; i < traces.length; i++) {
            StackTraceElement trace = traces[i];
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + trace + "]: ";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                    continue;
                }
            } catch (ClassNotFoundException e) {
                Log.e("Error " + LOGS_NAME, "Ошибка в LogSypr=" + e);
            }
        }
        return "[]: ";
    }

    private static String createLogString(Object[] values) {
        StringBuilder output = new StringBuilder();
        for (Object v : values) {
            output.append(v);
            output.append(" ");
        }
        return output.toString();
    }
}
