package com.github.iappapp.panda.utils;

/**
 * Created by iadore
 * @author iadore
 * @date 2020-09-07
 *
 * 运行时间计算类
 **/
public class StopWatch {

    private final long start;

    /**
     * Initialize a stopwatch object.
     */
    public StopWatch() {
        start = System.currentTimeMillis();
    }

    /**
     * Returns the elapsed time (in seconds) since this object was created.
     */
    public double elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - start) / 1000.0;
    }

    /**
     * Returns the elapsed time (in milliseconds) since this object was created.
     */
    public long elapsedTimeByMillis() {
        long now = System.currentTimeMillis();
        return now - start;
    }
}

