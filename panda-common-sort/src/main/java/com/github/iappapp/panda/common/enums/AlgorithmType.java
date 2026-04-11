package com.github.iappapp.panda.common.enums;

public enum AlgorithmType {
        BUBBLE("冒泡排序"),
        SELECTION("选择排序"),
        INSERTION("插入排序"),
        SHELL("希尔排序"),
        QUICK("快速排序"),
        MERGE("归并排序"),
        HEAP("堆排序");

        private final String displayName;

        AlgorithmType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }