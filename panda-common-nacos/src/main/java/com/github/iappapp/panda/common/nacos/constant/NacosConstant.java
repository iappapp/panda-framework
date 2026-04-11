/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.nacos.api.PropertyKeyConst
 */
package com.github.iappapp.panda.common.nacos.constant;

import com.alibaba.nacos.api.PropertyKeyConst;
import java.util.regex.Pattern;

public class NacosConstant
extends PropertyKeyConst {
    public static final String SERVER_ADDR = "panda.nacos.server-addr";
    public static final String USERNAME = "panda.nacos.username";
    public static final String PASSWORD = "panda.nacos.password";
    public static final String NACOS_CONFIG_TIMEOUT = "panda.nacos.config.timout";
    public static final long NACOS_DEFAULT_TIMEOUT = 30000L;
    public static final String SPRING_GLOBAL_NACOS_PROPERTIES_BEAN_NAME = "globalNacosProperties";
    public static final String SPRING_CLOUD_NACOS_CONFIG_PREFIX = "spring.cloud.nacos.config.";
    public static final String SPRING_BOOT_NACOS_CONFIG_PREFIX = "nacos.config.";
    public static final String SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR_KEY = "spring.cloud.nacos.config.server-addr";
    public static final String SPRING_CLOUD_NACOS_CONFIG_NAMESPACE_KEY = "spring.cloud.nacos.config.namespace";
    public static final String SPRING_BOOT_NACOS_CONFIG_SERVER_ADDR_KEY = "nacos.config.server-addr";
    public static final String SPRING_BOOT_NACOS_CONFIG_USERNAME_KEY = "nacos.config.username";
    public static final String SPRING_BOOT_NACOS_CONFIG_PASSWORD_KEY = "nacos.config.password";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_NAMESPACE_KEY = "nacos.discovery.namespace";
    public static final String FORMAT_PROPERTIES_COMMENT_START_SYMBOL = "#";
    public static final String DEFAULT_NAMESPACE_ID = "public";
    public static final String SPRING_CLOUD_NACOS_CONFIG_ENABLE_KEY = "spring.cloud.nacos.config.enabled";
    public static final String SPRING_BOOT_NACOS_CONFIG_NAMESPACE_KEY = "nacos.config.namespace";
    public static final String SPRING_CLOUD_NACOS_DISCOVERY_PREFIX = "spring.cloud.nacos.discovery.";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_PREFIX = "nacos.discovery.register.";
    public static final String SPRING_CLOUD_NACOS_DISCOVERY_NAMESPACE_KEY = "spring.cloud.nacos.discovery.namespace";
    public static final String SPRING_CLOUD_NACOS_DISCOVERY_IP_KEY = "spring.cloud.nacos.discovery.ip";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_IP_KEY = "nacos.discovery.register.ip";
    public static final String SPRING_CLOUD_NACOS_DISCOVERY_PORT_KEY = "spring.cloud.nacos.discovery.port";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_PORT_KEY = "nacos.discovery.register.port";
    public static final String SPRING_CLOUD_NACOS_DISCOVERY_WEIGHT_KEY = "spring.cloud.nacos.discovery.weight";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_WEIGHT_KEY = "nacos.discovery.register.weight";
    public static final String SPRING_CLOUD_NACOS_DISCOVERY_CLUSTER_NAME_KEY = "spring.cloud.nacos.discovery.cluster-name";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_CLUSTER_NAME_KEY = "nacos.discovery.register.cluster-name";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_EPHEMERAL_KEY = "nacos.discovery.register.ephemeral";
    public static final String SPRING_CLOUD_NACOS_DISCOVERY_EPHEMERAL_KEY = "spring.cloud.nacos.discovery.ephemeral";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_HEALTHY_KEY = "nacos.discovery.register.healthy";
    public static final String SPRINGCLOUD_METADATA_REGULAR = ".*nacos\\.discovery\\.metadata\\[([\\w.]+?)\\]";
    public static final String SPRINGBOOT_METADATA_REGULAR = ".*nacos\\.discovery\\.register\\.metadata\\[([\\w.]+?)\\]";
    public static final Pattern SPRINGCLOUD_METADATA_PATTERN = Pattern.compile(".*nacos\\.discovery\\.metadata\\[([\\w.]+?)\\]");
    public static final Pattern SPRINGBOOT_METADATA_PATTERN = Pattern.compile(".*nacos\\.discovery\\.register\\.metadata\\[([\\w.]+?)\\]");
    public static final String SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR_KEY = "spring.cloud.nacos.discovery.server-addr";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_SERVER_ADDR_KEY = "nacos.discovery.server-addr";
    public static final String SPRING_CLOUD_NACOS_DISCOVERY_GROUP_KEY = "spring.cloud.nacos.discovery.group";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_GROUP_KEY = "nacos.discovery.register.group-name";
    public static final String SPRING_CLOUD_NACOS_DISCOVERY_SERVER_NAME_KEY = "spring.cloud.nacos.discovery.service";
    public static final String SPRING_BOOT_NACOS_DISCOVERY_SERVER_NAME_KEY = "nacos.discovery.register.service-name";
    public static final String IS_PUBLISH_CONFIG_KEY_SPRING_CLOUD = "spring.cloud.nacos.config.enabled";
    public static final String IS_PUBLISH_CONFIG_KEY_SPRING_BOOT = "nacos.config.bootstrap.enable";
    public static final String NACOS_SERVER_ADDR_KEY = "nacos.server-addr";
}

