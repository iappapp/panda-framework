package com.github.iappapp.panda.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author liush2
 * on 2018/7/16
 * 线程池工具类
 */
public class ThreadPoolUtilHelper {


    static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();


    static ExecutorService freeOrderService = new ThreadPoolExecutor(10, 20, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(1024),
            namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

    static ExecutorService queryService = new ThreadPoolExecutor(10, 20, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(1024),
            namedThreadFactory, new ThreadPoolExecutor.DiscardPolicy());

    public static void setFreeOrderService(Runnable task) {
        freeOrderService.execute(task);
    }

    public static void setQueryService(Runnable task) {

        queryService.execute(task);
    }


}
