package com.github.iappapp.panda.common.job.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("panda.job")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PandaJobProperties {
    private String adminAddresses;

    private String accessToken;

    private String appname;

    private String address;

    private String ip;

    private int port;

    private String logPath;

    private int logRetentionDays;

    private String userName;

    private String password;

}
