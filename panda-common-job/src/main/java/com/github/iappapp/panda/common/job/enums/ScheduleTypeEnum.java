package com.github.iappapp.panda.common.job.enums;

public enum ScheduleTypeEnum {
    NONE("无"),
    CRON("CRON"),
    FIX_RATE("固定速度");

    private String title;

    ScheduleTypeEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public static ScheduleTypeEnum match(String name, ScheduleTypeEnum defaultItem) {
        for (ScheduleTypeEnum item : values()) {
            if (item.name().equals(name))
                return item;
        }
        return defaultItem;
    }
}
