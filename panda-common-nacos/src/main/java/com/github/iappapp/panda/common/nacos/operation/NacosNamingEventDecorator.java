/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.nacos.api.naming.listener.Event
 *  com.alibaba.nacos.api.naming.listener.EventListener
 *  com.alibaba.nacos.api.naming.listener.NamingEvent
 *  com.alibaba.nacos.api.naming.utils.NamingUtils
 */
package com.github.iappapp.panda.common.nacos.operation;

import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.utils.NamingUtils;

public class NacosNamingEventDecorator implements EventListener {
    private EventListener eventListener;

    public NacosNamingEventDecorator(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void onEvent(Event event) {
        NamingEvent namingEvent;
        if (event instanceof NamingEvent && !(namingEvent = (NamingEvent) event).getServiceName().contains("@@")) {
            String combinServiceName = NamingUtils.getGroupedName(namingEvent.getServiceName(), namingEvent.getGroupName());
            namingEvent.setServiceName(combinServiceName);
            this.eventListener.onEvent(namingEvent);
            return;
        }
        this.eventListener.onEvent(event);
    }
}

