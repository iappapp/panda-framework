/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 */
package com.github.iappapp.panda.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class BatchOperationResultDTO {
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger failCount = new AtomicInteger(0);
    private List<Map<String, Object>> results = new Vector<Map<String, Object>>();

    public void addSuccessInfo(String keyName, Object value) {
        HashMap<String, Object> map = new HashMap<String, Object>(2);
        map.put(keyName, value);
        map.put("success", true);
        this.results.add(map);
        this.successCount.incrementAndGet();
    }

    @Deprecated
    public void addSuccessInfo(Map<String, Object> values) {
        HashMap<String, Object> map = new HashMap<String, Object>(2);
        map.putAll(values);
        map.put("success", true);
        this.results.add(map);
        this.successCount.getAndAdd(values.size());
    }

    public void addFailInfo(String keyName, Object value, String errorCode, String errorMsg) {
        HashMap<String, Object> map = new HashMap<>(2);
        map.put(keyName, value);
        map.put("success", false);
        map.put("code", errorCode);
        map.put("message", errorMsg);
        this.results.add(map);
        this.failCount.incrementAndGet();
    }

    @Deprecated
    public void addFailInfo(Map<String, Object> values, String errorCode, String errorMsg) {
        HashMap<String, Object> map = new HashMap<String, Object>(2);
        map.putAll(values);
        map.put("success", false);
        map.put("code", errorCode);
        map.put("message", errorMsg);
        this.results.add(map);
        this.failCount.getAndAdd(values.size());
    }

}

