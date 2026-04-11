package com.github.iappapp.panda.common.job.enums;

public enum MisfireStrategyEnum {
    DO_NOTHING("忽略"),
    FIRE_ONCE_NOW("立即执行一次");

    private String title;

    MisfireStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public static MisfireStrategyEnum match(String name, MisfireStrategyEnum defaultItem) {
        for (MisfireStrategyEnum item : values()) {
            if (item.name().equals(name))
                return item;
        }
        return defaultItem;
    }
}
