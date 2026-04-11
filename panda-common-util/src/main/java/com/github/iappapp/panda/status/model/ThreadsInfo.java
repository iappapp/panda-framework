package com.github.iappapp.panda.status.model;

import lombok.ToString;

import java.util.Objects;

/**
 * @author iappapp
 * @date 2020/11/13 10:18
 */
@ToString
public class ThreadsInfo {
    private int m_count;

    private int m_daemonCount;

    private int m_peekCount;

    private int m_totalStartedCount;

    private int m_pigeonThreadCount;

    private int m_httpThreadCount;

    public ThreadsInfo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreadsInfo that = (ThreadsInfo) o;
        return m_count == that.m_count &&
                m_daemonCount == that.m_daemonCount &&
                m_peekCount == that.m_peekCount &&
                m_totalStartedCount == that.m_totalStartedCount &&
                m_pigeonThreadCount == that.m_pigeonThreadCount &&
                m_httpThreadCount == that.m_httpThreadCount;
    }

    public int getCount() {
        return m_count;
    }

    public int getDaemonCount() {
        return m_daemonCount;
    }

    public int getHttpThreadCount() {
        return m_httpThreadCount;
    }

    public int getPeekCount() {
        return m_peekCount;
    }

    public int getPigeonThreadCount() {
        return m_pigeonThreadCount;
    }

    public int getTotalStartedCount() {
        return m_totalStartedCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_count, m_daemonCount, m_peekCount, m_totalStartedCount, m_pigeonThreadCount, m_httpThreadCount);
    }

    public ThreadsInfo setCount(int count) {
        m_count = count;
        return this;
    }

    public ThreadsInfo setDaemonCount(int daemonCount) {
        m_daemonCount = daemonCount;
        return this;
    }

    public ThreadsInfo setHttpThreadCount(int httpThreadCount) {
        m_httpThreadCount = httpThreadCount;
        return this;
    }

    public ThreadsInfo setPeekCount(int peekCount) {
        m_peekCount = peekCount;
        return this;
    }

    public ThreadsInfo setPigeonThreadCount(int pigeonThreadCount) {
        m_pigeonThreadCount = pigeonThreadCount;
        return this;
    }

    public ThreadsInfo setTotalStartedCount(int totalStartedCount) {
        m_totalStartedCount = totalStartedCount;
        return this;
    }

}
