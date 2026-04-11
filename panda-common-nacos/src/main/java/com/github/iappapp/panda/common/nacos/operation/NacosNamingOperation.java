/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.thread.ThreadUtil
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.nacos.api.exception.NacosException
 *  com.alibaba.nacos.api.naming.NamingService
 *  com.alibaba.nacos.api.naming.listener.EventListener
 *  com.alibaba.nacos.api.naming.listener.NamingEvent
 *  com.alibaba.nacos.api.naming.pojo.Instance
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.github.iappapp.panda.common.nacos.operation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.iappapp.panda.common.nacos.bean.ServiceDTO;
import com.github.iappapp.panda.common.nacos.configuration.NacosCommonProperties;
import com.github.iappapp.panda.common.nacos.enums.ProtocolEnum;
import com.github.iappapp.panda.common.nacos.operation.NacosNamingEventDecorator;
import com.github.iappapp.panda.common.nacos.operation.NacosNamingSubscribe;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class NacosNamingOperation implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(NacosNamingOperation.class);
    @Autowired
    private NamingService namingService;

    @Autowired(required=false)
    private Set<NacosNamingSubscribe> nacosNamingSubscribes;

    @Autowired
    private NacosCommonProperties nacosCommonProperties;

    public String getSchemaUrl(String serviceName, ProtocolEnum protocolEnum)
            throws NacosException {
        Instance instance = this.namingService.selectOneHealthyInstance(serviceName);
        return protocolEnum.getProtocol() + instance.toInetAddr();
    }

    public String getSchemaUrl(String serviceName, String groupName, ProtocolEnum protocolEnum)
            throws NacosException {
        Instance instance = this.namingService.selectOneHealthyInstance(serviceName, groupName);
        return protocolEnum.getProtocol() + instance.toInetAddr();
    }

    public void unsubscribe(String serviceName) throws NacosException {
        this.namingService.unsubscribe(serviceName, event -> {});
    }

    public List<Instance> selectInstances(String serviceName, String groupName, boolean healthy) {
        int retryTime = this.nacosCommonProperties.getConfigRetryTime();
        while (true) {
            try {
                return this.namingService.selectInstances(serviceName, groupName, healthy);
            }
            catch (Exception e) {
                log.error("select instance error", e);
                ThreadUtil.sleep(this.nacosCommonProperties.getConfigRetryInterval(), TimeUnit.SECONDS);
                log.error("selectInstances failed retryTime remains {}", retryTime);
                if (retryTime-- > 0 || this.nacosCommonProperties.getConfigRetryLast()) {
                    continue;
                }
                log.error("selectInstances failed");
                return new ArrayList<>();
            }
        }
    }

    public void checkServiceExist(List<ServiceDTO> serviceDTOList) {
        if (CollUtil.isEmpty(serviceDTOList)) {
            return;
        }
        serviceDTOList.forEach(serviceDTO -> {
            if (StrUtil.isEmpty(serviceDTO.getGroup())) {
                serviceDTO.setGroup("DEFAULT_GROUP");
            }
            if (CollUtil.isNotEmpty(this.selectInstances(serviceDTO.getServiceName(), serviceDTO.getGroup(), true))) {
                serviceDTO.setIsAvailable(Boolean.TRUE);
            } else {
                serviceDTO.setIsAvailable(Boolean.FALSE);
            }
        });
    }

    public void unsubscribe(String serviceName, String groupName) throws NacosException {
        this.namingService.unsubscribe(serviceName, groupName, event -> {});
    }

    public void subscribe(String serviceName, String groupName, NacosNamingSubscribe nacosNamingSubscribe)
            throws NacosException {
        EventListener eventListener = event -> {
            List<Instance> instances = ((NamingEvent)event).getInstances();
            if (instances.isEmpty()) {
                nacosNamingSubscribe.offlineCallback(event);
            } else {
                nacosNamingSubscribe.onlineCallback(event);
            }
        };
        this.namingService.subscribe(serviceName, groupName, new NacosNamingEventDecorator(eventListener));
    }

    public boolean subscribe(String serviceName, String groupName, EventListener listener) {
        int retryTime = this.nacosCommonProperties.getConfigRetryTime();
        while (true) {
            try {
                this.namingService.subscribe(serviceName, groupName, new NacosNamingEventDecorator(listener));
                return true;
            }
            catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                if (retryTime-- > 0 || this.nacosCommonProperties.getConfigRetryLast()) {
                    continue;
                }
                log.error("subscribeService failed serviceName: {} , groupName: {}", serviceName, groupName);
                return false;
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (CollUtil.isNotEmpty(nacosNamingSubscribes)) {
            nacosNamingSubscribes.forEach(nacosNamingSubscribe -> {
                try {
                    for (int i = 0; i < nacosNamingSubscribe.getServiceNames().length; ++i) {
                        namingService.subscribe(StrUtil.trim(nacosNamingSubscribe.getServiceNames()[i]), nacosNamingSubscribe.getGroupNames()[i], event -> {
                            List<Instance> instances = ((NamingEvent)event).getInstances();
                            if (instances.isEmpty()) {
                                nacosNamingSubscribe.offlineCallback(event);
                            } else {
                                nacosNamingSubscribe.onlineCallback(event);
                            }
                        });
                    }
                }
                catch (NacosException e) {
                    log.error("service subscribe error", e);
                }
            });
        }
    }
}

