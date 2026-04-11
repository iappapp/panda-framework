/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.common.task.quartz.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix="panda.task")
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class TaskConfigBuilder {
    /**
     *
     */
    private Boolean clusterEnable = false;

    /**
     *
     */
    private Boolean enable = false;

    /**
     *
     */
    private final FailJob failJob = new FailJob();
    /**
     *
     */
    private final DataSource dataSource = new DataSource();

    @Data
    @NoArgsConstructor
    public class DataSource {
        private String host = "127.0.0.1";
        private String port = "3306";
        private String username;
        private String password;
        private String driver = "org.mariadb.jdbc.Driver";
        private String type = "com.alibaba.druid.pool.DruidDataSource";
    }

    @EqualsAndHashCode
    @ToString
    public class FailJob {
        private long retryNum = 10L;
        private long retryInterval = 60L;

        public long getRetryInterval() {
            if (this.retryInterval <= 0L) {
                this.retryInterval = 60L;
            }
            return this.retryInterval;
        }

        public long getRetryNum() {
            return this.retryNum;
        }

        public void setRetryNum(long retryNum) {
            this.retryNum = retryNum;
        }

        public void setRetryInterval(long retryInterval) {
            this.retryInterval = retryInterval;
        }

    }
}

