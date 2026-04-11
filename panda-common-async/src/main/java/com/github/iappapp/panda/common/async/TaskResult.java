package com.github.iappapp.panda.common.async;

// 单个任务结果封装
public class TaskResult<R> {
    boolean success;
    R data;
    Throwable error;

    public static <R> TaskResult<R> success(R data) {
        TaskResult<R> r = new TaskResult<>();
        r.success = true;
        r.data = data;
        return r;
    }

    public static <R> TaskResult<R> failure(Throwable error) {
        TaskResult<R> r = new TaskResult<>();
        r.success = false;
        r.error = error;
        return r;
    }
}