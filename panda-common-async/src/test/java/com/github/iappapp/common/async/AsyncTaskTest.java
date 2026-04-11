package com.github.iappapp.common.async;

import com.github.iappapp.panda.common.async.AsyncBatchProcessor;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskTest {

    @Test
    public void test() {
        // 1. 准备数据
        List<String> urls = Arrays.asList(
                "http://google.com",
                "http://github.com",
                "http://stackoverflow.com",
                "http://invalid-url.xyz",
                "http://java.com",
                "http://openai.com"
        );

        // 2. 自定义线程池 (可选，生产环境推荐)
        ExecutorService myPool = Executors.newFixedThreadPool(4);

        // 3. 构建处理器
        AsyncBatchProcessor<String, String> processor = AsyncBatchProcessor.<String, String>builder()
            .threadPool(myPool)
            .callbacks(cb -> cb
                // 成功回调
                .onSuccess((url, result) -> System.out.println("✅ [成功] " + url + " -> len:" + result.length()))
                // 失败回调
                .onFailure((url, err) -> System.err.println("❌ [失败] " + url + " -> " + err.getMessage()))
                // 进度回调 (比如用来推送到 WebSocket 或更新 UI)
                .onProgress(stats -> {
                    // 简单的控制台进度条
                    System.out.println(">> " + stats.getSummary());
                })
            )
            .build();

        System.out.println("开始批量任务...");

        // 4. 执行任务 (定义具体的业务逻辑)
        processor.process(urls, url -> {
            // --- 模拟业务逻辑 ---
            simulateWork(); 
            if (url.contains("invalid")) {
                throw new RuntimeException("404 Not Found");
            }
            return "Content of " + url; // 返回结果
            // ------------------
        }).thenAccept(results -> {
            // 5. 所有任务完成后的最终处理
            System.out.println("\n🎉 所有任务处理完毕!");
            System.out.println("收集到成功结果数量: " + results.size());
            // 关闭线程池
            myPool.shutdown();
        }).join(); // 阻塞主线程等待演示结束
    }

    // 模拟耗时
    private static void simulateWork() {
        try {
            Thread.sleep(new Random().nextInt(1000) + 500);
        } catch (InterruptedException e) {}
    }
}