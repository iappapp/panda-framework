package com.github.iappapp.panda.common.job.enums;

public enum ExecutorBlockStrategyEnum {
    SERIAL_EXECUTION("Serial execution"),
    DISCARD_LATER("Discard Later"),
    COVER_EARLY("Cover Early");

    private String title;

    ExecutorBlockStrategyEnum(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public static ExecutorBlockStrategyEnum match(String name, ExecutorBlockStrategyEnum defaultItem) {
        if (name != null)
            for (ExecutorBlockStrategyEnum item : values()) {
                if (item.name().equals(name))
                    return item;
            }
        return defaultItem;
    }
}
