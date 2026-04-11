package com.github.iappapp.panda.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleThreadPoolUtil {
    private static int count = Runtime.getRuntime().availableProcessors() * 12;

    private static ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(count, count, 10, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(), new DefaultThreadFactory());

    public static void execute(Runnable runnable){
        threadPool.execute(runnable);
    }
}
