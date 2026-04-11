package com.github.iappapp.panda.status.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author iappapp
 * @date 2020/11/12 14:27
 */
public class MemoryInfo {
    private long m_max;

    private long m_total;

    private long m_free;

    private long m_heapUsage;

    private long m_nonHeapUsage;

    private List<GcInfo> m_gcs = new ArrayList<GcInfo>();

    public MemoryInfo() {
    }

    public MemoryInfo addGc(GcInfo gc) {
        m_gcs.add(gc);
        return this;
    }

    public long getFree() {
        return m_free;
    }

    public List<GcInfo> getGcs() {
        return m_gcs;
    }

    public long getHeapUsage() {
        return m_heapUsage;
    }

    public long getMax() {
        return m_max;
    }

    public long getNonHeapUsage() {
        return m_nonHeapUsage;
    }

    public long getTotal() {
        return m_total;
    }

    public MemoryInfo setFree(long free) {
        m_free = free;
        return this;
    }

    public MemoryInfo setHeapUsage(long heapUsage) {
        m_heapUsage = heapUsage;
        return this;
    }

    public MemoryInfo setMax(long max) {
        m_max = max;
        return this;
    }

    public MemoryInfo setNonHeapUsage(long nonHeapUsage) {
        m_nonHeapUsage = nonHeapUsage;
        return this;
    }

    public MemoryInfo setTotal(long total) {
        m_total = total;
        return this;
    }

    @Override
    public String toString() {
        return "MemoryUsage{" +
                "m_max=" + m_max +
                "MB, m_total=" + m_total +
                "MB, m_free=" + m_free +
                "MB, m_heapUsage=" + m_heapUsage +
                "MB, m_nonHeapUsage=" + m_nonHeapUsage +
                "MB, m_gcs=" + m_gcs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryInfo that = (MemoryInfo) o;
        return m_max == that.m_max &&
                m_total == that.m_total &&
                m_free == that.m_free &&
                m_heapUsage == that.m_heapUsage &&
                m_nonHeapUsage == that.m_nonHeapUsage &&
                Objects.equals(m_gcs, that.m_gcs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_max, m_total, m_free, m_heapUsage, m_nonHeapUsage, m_gcs);
    }
}
