package com.github.iappapp.panda.common.async;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

// 定义回调集合
public class JobCallback<T, R> {
    // 任务成功回调：输入参数, 结果
    BiConsumer<T, R> onSuccess = (t, r) -> {};
    // 任务失败回调：输入参数, 异常
    BiConsumer<T, Throwable> onFailure = (t, e) -> {};
    // 进度变更回调：当前统计快照
    Consumer<JobStatistic> onProgress = (stats) -> {};

    // 链式设置方法
    public JobCallback<T, R> onSuccess(BiConsumer<T, R> callback) {
        this.onSuccess = callback;
        return this;
    }

    public JobCallback<T, R> onFailure(BiConsumer<T, Throwable> callback) {
        this.onFailure = callback;
        return this;
    }

    public JobCallback<T, R> onProgress(Consumer<JobStatistic> callback) {
        this.onProgress = callback;
        return this;
    }
}