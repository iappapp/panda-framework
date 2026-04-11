package com.github.iappapp.panda.common.async;

/**
 *
 */
import java.util.concurrent.atomic.LongAdder;

// 任务统计快照（线程安全）
public class JobStatistic {
    private final long total;
    // 使用 LongAdder 在高并发下性能优于 AtomicLong
    private final LongAdder successCount = new LongAdder();

    private final LongAdder failureCount = new LongAdder();

    private final long startTime;

    public JobStatistic(long total) {
        this.total = total;
        this.startTime = System.currentTimeMillis();
    }

    public void incrementSuccess() {
        successCount.increment();
    }

    public void incrementFailure() {
        failureCount.increment();
    }

    public long getTotal() {
        return total;
    }

    public long getSuccess() {
        return successCount.sum();
    }

    public long getFailure() {
        return failureCount.sum();
    }

    public long getProcessed() {
        return successCount.sum() + failureCount.sum();
    }

    // 计算进度百分比 (0.0 - 100.0)
    public double getProgress() {
        if (total == 0) return 100.0;
        return (double) getProcessed() / total * 100.0;
    }

    public String getSummary() {
        long cost = System.currentTimeMillis() - startTime;
        return String.format("进度: %.0f%% | 总数: %d | 成功: %d | 失败: %d | 耗时: %dms",
                getProgress(), total, getSuccess(), getFailure(), cost);
    }
}
