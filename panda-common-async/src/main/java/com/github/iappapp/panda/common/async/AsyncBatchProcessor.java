package com.github.iappapp.panda.common.async;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用异步批量任务处理器
 *
 * @param <T> 任务输入类型
 * @param <R> 任务输出类型
 */
@Slf4j
public class AsyncBatchProcessor<T, R> {

    private final ExecutorService executor;

    private final JobCallback<T, R> callbacks;

    private AsyncBatchProcessor(ExecutorService executor, JobCallback<T, R> callbacks) {
        this.executor = executor;
        this.callbacks = callbacks;
    }

    /**
     * 执行批处理任务
     *
     * @param tasks       输入数据列表
     * @param taskHandler 具体的业务逻辑处理函数
     * @return CompletableFuture 包含最终的所有结果列表
     */
    public CompletableFuture<List<R>> process(List<T> tasks, Function<T, R> taskHandler) {
        if (tasks == null || tasks.isEmpty()) {
            return CompletableFuture.completedFuture(java.util.Collections.emptyList());
        }

        // 初始化统计器
        JobStatistic stats = new JobStatistic(tasks.size());

        // 将每个输入 T 转换为一个 CompletableFuture<TaskResult<R>>
        List<CompletableFuture<TaskResult<R>>> futures = tasks.stream()
                .map(task -> CompletableFuture.supplyAsync(() -> {
                    try {
                        // 执行业务逻辑
                        R result = taskHandler.apply(task);
                        return TaskResult.success(result);
                    } catch (Throwable e) {
                        return TaskResult.<R>failure(e);
                    }
                }, executor).thenApply(result -> {
                    // 后置处理：更新统计、触发回调
                    // 注意：这里仍然在线程池中执行
                    updateStatsAndTriggerCallbacks(task, result, stats);
                    return result;
                }))
                .collect(Collectors.toList());

        // 等待所有任务完成，并提取结果
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        // 此时已全部完成，join不会阻塞
                        .map(CompletableFuture::join)
                        // 仅返回成功的结果，或者根据需求返回所有
                        .filter(r -> Objects.nonNull(r))
                        .map(r -> r.data)
                        .collect(Collectors.toList())
                );
    }

    private void updateStatsAndTriggerCallbacks(T task, TaskResult<R> result, JobStatistic stats) {
        if (result.success) {
            stats.incrementSuccess();
            safeCallback(() -> callbacks.onSuccess.accept(task, result.data));
        } else {
            stats.incrementFailure();
            safeCallback(() -> callbacks.onFailure.accept(task, result.error));
        }
        // 触发进度回调
        safeCallback(() -> callbacks.onProgress.accept(stats));
    }

    private void safeCallback(Runnable r) {
        try {
            r.run();
        } catch (Exception ex) {
            // 防止回调函数本身的异常中断流程
            log.info("Callback execution failed: {}",  ex.getMessage());
        }
    }

    // --- Builder 模式构建器 ---
    public static <T, R> Builder<T, R> builder() {
        return new Builder<>();
    }

    public static class Builder<T, R> {
        private ExecutorService executor;

        private final JobCallback<T, R> callbacks = new JobCallback<>();

        public Builder<T, R> threadPool(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public Builder<T, R> callbacks(Consumer<JobCallback<T, R>> configurer) {
            configurer.accept(this.callbacks);
            return this;
        }

        public AsyncBatchProcessor<T, R> build() {
            if (executor == null) {
                // 默认使用 ForkJoinPool，建议生产环境自定义线程池
                executor =
                        Executors.newFixedThreadPool(Math.min(4, Runtime.getRuntime().availableProcessors()));
            }
            return new AsyncBatchProcessor<>(executor, callbacks);
        }
    }
}