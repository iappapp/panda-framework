package com.github.iappapp.panda.common.job.enums;

public enum ExecutorRouteStrategyEnum {
    FIRST("第一个"),
    LAST("最后一个"),
    ROUND("轮询"),
    RANDOM("随机"),
    CONSISTENT_HASH("一致性HASH"),
    LEAST_FREQUENTLY_USED("最不经常使用"),
    LEAST_RECENTLY_USED("最近最久未使用"),
    FAILOVER("故障转移"),
    BUSYOVER("忙碌转移"),
    SHARDING_BROADCAST("分片广播");

    private String title;

    ExecutorRouteStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem) {
        if (name != null)
            for (ExecutorRouteStrategyEnum item : values()) {
                if (item.name().equals(name))
                    return item;
            }
        return defaultItem;
    }
}
