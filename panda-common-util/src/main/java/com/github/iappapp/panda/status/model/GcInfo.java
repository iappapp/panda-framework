package com.github.iappapp.panda.status.model;

import lombok.ToString;

import java.util.Objects;

/**
 * @author iappapp
 * @date 2020/11/12 14:37
 */
@ToString
public class GcInfo {
    private String m_name;

    private long m_count;

    private long m_time;

    public GcInfo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GcInfo gcInfo = (GcInfo) o;
        return m_count == gcInfo.m_count &&
                m_time == gcInfo.m_time &&
                Objects.equals(m_name, gcInfo.m_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_name, m_count, m_time);
    }

    public long getCount() {
        return m_count;
    }

    public String getName() {
        return m_name;
    }

    public long getTime() {
        return m_time;
    }


    public GcInfo setCount(long count) {
        m_count = count;
        return this;
    }

    public GcInfo setName(String name) {
        m_name = name;
        return this;
    }

    public GcInfo setTime(long time) {
        m_time = time;
        return this;
    }

}

