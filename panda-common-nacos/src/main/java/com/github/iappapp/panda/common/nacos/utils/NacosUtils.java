/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.nacos.api.NacosFactory
 *  com.alibaba.nacos.api.config.ConfigService
 *  com.alibaba.nacos.api.exception.NacosException
 *  com.alibaba.nacos.api.naming.NamingMaintainFactory
 *  com.alibaba.nacos.api.naming.NamingMaintainService
 *  com.alibaba.nacos.api.naming.NamingService
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.ApplicationContext
 *  org.springframework.stereotype.Service
 */
package com.github.iappapp.panda.common.nacos.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.github.iappapp.panda.common.nacos.configuration.NacosCommonProperties;
import com.github.iappapp.panda.common.nacos.utils.Ecc256EncryUtil;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@ConditionalOnProperty(prefix = "panda.nacos", name = "enable", havingValue = "true", matchIfMissing = false)
public class NacosUtils {
    private static final Logger log = LoggerFactory.getLogger(NacosUtils.class);
    private String systemServerAddr;
    private String systemNameSpace;
    private NamingService systemNamingService;
    private NamingMaintainService systemNamingMaintainService;
    private ConfigService systemConfigService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private NacosCommonProperties nacosCommonProperties;
    @Value(value="${jasypt.encryptor.password:-1}")
    private String appJasyptEncryptorPassword;

    public ConfigService getConfigService(String serverAddr, String namespace) throws NacosException {
        ConfigService configService = NacosFactory.createConfigService(this.makeProperties(serverAddr, namespace));
        return configService;
    }

    public NamingService getNamingService(String serverAddr, String namespace) throws NacosException {
        return NacosFactory.createNamingService(this.makeProperties(serverAddr, namespace));
    }

    public NamingMaintainService getNamingMaintainService(String serverAddr, String namespace) throws NacosException {
        return NamingMaintainFactory.createMaintainService(this.makeProperties(serverAddr, namespace));
    }

    private Properties makeProperties(String serverAddr, String namespace) {
        Properties properties = new Properties();
        if (StrUtil.isNotBlank(serverAddr)) {
            properties.put("serverAddr", serverAddr);
        }
        if (StrUtil.isNotBlank(namespace)) {
            properties.put("namespace", namespace);
        }
        String ru = Ecc256EncryUtil.getDecryption(nacosCommonProperties.getUsername(), appJasyptEncryptorPassword);
        String rp = Ecc256EncryUtil.getDecryption(nacosCommonProperties.getPassword(), appJasyptEncryptorPassword);
        properties.setProperty("username", ru);
        properties.setProperty("password", rp);
        return properties;
    }

    public boolean publishConfig(String serverAddr, String dataId, String group, String namespace,
                                 String content, boolean isOverrideExist) throws NacosException {
        ConfigService configService = getConfigService(serverAddr, namespace);
        String existConfig = configService.getConfig(dataId, group, 5000L);
        if (null != existConfig && !isOverrideExist) {
            return true;
        }
        return configService.publishConfig(dataId, group, content);
    }

    public boolean publishConfig(ConfigService configService, String dataId, String group,
                                 String content, boolean isOverrideExist) throws NacosException {
        String existConfig = configService.getConfig(dataId, group, 5000L);
        if (null != existConfig && !isOverrideExist) {
            return true;
        }
        return configService.publishConfig(dataId, group, content);
    }

    public static String mergeProperties(Map<String, Object> newContent, String oldContent) {
        CharSequence[] oldContentLines;
        Set<String> newContentKeys = newContent.keySet();
        if (null != oldContent && !"".equals(oldContent.trim())) {
            oldContentLines = oldContent.split(System.lineSeparator());
            for (String newContentKey : newContentKeys) {
                for (int j = 0; j < oldContentLines.length; ++j) {
                    CharSequence oldContentLine = oldContentLines[j];
                    if (((String)oldContentLine).startsWith("#")) {
                        continue;
                    }
                    try {
                        String oldContentkey = ((String)oldContentLine).substring(0, ((String)oldContentLine).indexOf("="));
                        if (!newContentKey.equals(oldContentkey.trim())){
                            continue;
                        }
                        oldContentLines[j] = newContentKey + "=" + newContent.get(newContentKey);
                    }
                    catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
                        // empty catch block
                    }
                }
            }
        } else {
            oldContentLines = newContentKeys.stream().map(key -> key + "=" + newContent.get(key)).toArray(String[]::new);
        }
        return String.join(System.lineSeparator(), oldContentLines);
    }

    public String getConfig(String serverAddr, String dataId, String group, String namespace)
            throws NacosException {
        ConfigService configService = getConfigService(serverAddr, namespace);
        return configService.getConfig(dataId, group, 5000L);
    }

    public boolean patchConfigToSystemConfigService(String dataId, String group, Map<String, Object> content)
            throws NacosException {
        String oldContent = getConfigFromSystemConfigService(dataId, group);
        String mergedContent = NacosUtils.mergeProperties(content, oldContent);
        return systemConfigService.publishConfig(dataId, group, mergedContent);
    }

    public String getConfigFromSystemConfigService(String dataId, String group) throws NacosException {
        if (systemConfigService != null) {
            return systemConfigService.getConfig(dataId, group, 5000L);
        }
        throw new NacosException(403, "no avariable config service");
    }

    @PostConstruct
    public void init() {
        if (!nacosCommonProperties.isEnable()) {
            return;
        }
        Properties springGlobalNacosProperties = null;
        try {
            springGlobalNacosProperties = applicationContext.getBean("globalNacosProperties", Properties.class);
        }
        catch (Exception e) {
            log.debug(e.getLocalizedMessage(), e);
        }
        if (springGlobalNacosProperties != null) {
            systemServerAddr = springGlobalNacosProperties.getProperty("serverAddr");
            systemNameSpace = springGlobalNacosProperties.getProperty("namespace");
        } else {
            systemServerAddr = applicationContext.getEnvironment().getProperty("nacos.config.server-addr");
            systemNameSpace = applicationContext.getEnvironment().getProperty("nacos.discovery.namespace");
        }
        if (StrUtil.isBlank(systemServerAddr)) {
            systemServerAddr = applicationContext.getEnvironment().getProperty("spring.cloud.nacos.config.server-addr");
            systemNameSpace = applicationContext.getEnvironment().getProperty("spring.cloud.nacos.config.namespace");
        }
        if (StrUtil.isBlank(systemServerAddr)) {
            systemServerAddr = applicationContext.getEnvironment().getProperty("panda.nacos.server-addr");
            systemNameSpace = applicationContext.getEnvironment().getProperty("nacos.discovery.namespace");
        }
        try {
            systemConfigService = getConfigService(systemServerAddr, systemNameSpace);
            systemNamingService = getNamingService(systemServerAddr, systemNameSpace);
            systemNamingMaintainService = getNamingMaintainService(systemServerAddr, this.systemNameSpace);
        }
        catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public String getSystemServerAddr() {
        return this.systemServerAddr;
    }

    public NamingService getSystemNamingService() {
        return this.systemNamingService;
    }

    public ConfigService getSystemConfigService() {
        return this.systemConfigService;
    }
}

