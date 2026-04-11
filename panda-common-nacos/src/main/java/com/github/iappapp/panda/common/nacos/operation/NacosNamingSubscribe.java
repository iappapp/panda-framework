/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.nacos.api.naming.listener.Event
 */
package com.github.iappapp.panda.common.nacos.operation;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.naming.listener.Event;

public interface NacosNamingSubscribe {
    /**
     *
     * @return
     */
    String getServiceName();

    /**
     *
     * @return
     */
    String getGroupName();

    /**
     *
     * @param event
     */
    void onlineCallback(Event event);

    /**
     *
     * @param event
     */
    void offlineCallback(Event event);

    default String[] getServiceNames() {
        if (StrUtil.isBlank(this.getServiceName())) {
            return new String[0];
        }
        return StrUtil.split(this.getServiceName(), ";");
    }

    default String[] getGroupNames() {
        if (StrUtil.isBlank(this.getGroupName())) {
            return new String[0];
        }
        return StrUtil.split(this.getGroupName(), ";");
    }
}

