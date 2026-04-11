/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.thread.ThreadUtil
 *  com.alibaba.nacos.api.config.ConfigService
 *  com.alibaba.nacos.api.config.listener.Listener
 *  com.alibaba.nacos.api.exception.NacosException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.core.env.Environment
 */
package com.github.iappapp.panda.common.nacos.operation;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.iappapp.panda.common.nacos.configuration.NacosCommonProperties;
import com.github.iappapp.panda.common.nacos.operation.NacosConfigSubscribe;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

public class NacosConfigOperation {
    private static final Logger log = LoggerFactory.getLogger(NacosConfigOperation.class);
    @Autowired
    private ConfigService nacosConfigService;
    @Autowired
    private Environment environment;
    @Autowired
    private NacosCommonProperties nacosCommonProperties;
    @Value(value="${panda.base.spring.log.return.enable:true}")
    private boolean logReturnEnable;

    public String getConfig(String group, String dataId) {
        long timeout = this.environment.getProperty("panda.nacos.config.timout", Long.class, 30000L);
        int retryTime = this.nacosCommonProperties.getConfigRetryTime();
        while (true) {
            try {
                return this.nacosConfigService.getConfig(dataId, group, timeout);
            }
            catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                if (retryTime-- > 0 || this.nacosCommonProperties.getConfigRetryLast().booleanValue()) {
                    continue;
                }
                log.error("getConfig failed groupName: {} , dataId: {}", group, dataId);
                return null;
            }
        }
    }

    public boolean removeConfig(String group, String dataId) throws NacosException {
        return this.nacosConfigService.removeConfig(dataId, group);
    }

    public boolean publishConfig(String group, String dataId, String config) {
        int retryTime = this.nacosCommonProperties.getConfigRetryTime();
        boolean publishConfigResult = false;
        do {
            try {
                publishConfigResult = this.nacosConfigService.publishConfig(dataId, group, config);
                if (publishConfigResult) {
                    break;
                }
            }
            catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
            ThreadUtil.sleep(this.nacosCommonProperties.getConfigRetryInterval(), TimeUnit.SECONDS);
            log.error("push config failed retryTime remains {}", retryTime);
        } while (retryTime-- > 0 || this.nacosCommonProperties.getConfigRetryLast());
        if (!this.logReturnEnable) {
            config = "Do not display";
        }
        if (publishConfigResult) {
            log.info("push config [ group: {} | dataId: {} | content: {} ] success", group, dataId, config);
        } else {
            log.error("push config [ group: {} | dataId: {} | content: {} ] failed ", group, dataId, config);
        }
        return publishConfigResult;
    }

    public String getConfigAndSignListener(String group, String dataId, final Executor executor, final NacosConfigSubscribe subscribeCallback) {
        long timeout = this.environment.getProperty("panda.nacos.config.timout", Long.class, 30000L);
        int retryTime = this.nacosCommonProperties.getConfigRetryTime();
        while (true) {
            try {
                Listener configListener = new Listener(){

                    public void receiveConfigInfo(String config) {
                        subscribeCallback.callback(config);
                    }

                    public Executor getExecutor() {
                        return executor;
                    }
                };
                return this.nacosConfigService.getConfigAndSignListener(dataId, group, timeout, configListener);
            }
            catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                if (retryTime-- > 0 || this.nacosCommonProperties.getConfigRetryLast()) continue;
                log.error("getConfigAndSignListener failed groupName: {} , dataId: {}", group, dataId);
                return null;
            }
        }
    }

    public Listener subscribeConfig(String group, String dataId, final Executor executor,
                                    final NacosConfigSubscribe subscribeCallback) {
        int retryTime = this.nacosCommonProperties.getConfigRetryTime();
        while (true) {
            try {
                Listener configListener = new Listener(){

                    public void receiveConfigInfo(String config) {
                        subscribeCallback.callback(config);
                    }

                    public Executor getExecutor() {
                        return executor;
                    }
                };
                this.nacosConfigService.addListener(dataId, group, configListener);
                return configListener;
            }
            catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                if (retryTime-- > 0 || this.nacosCommonProperties.getConfigRetryLast()) {
                    continue;
                }
                log.error("subscribeConfig failed groupName: {} , dataId: {}", group, dataId);
                return null;
            }
        }
    }

    public void unsubscribeConfig(String group, String dataId, Listener configListener) {
        this.nacosConfigService.removeListener(dataId, group, configListener);
    }
}

