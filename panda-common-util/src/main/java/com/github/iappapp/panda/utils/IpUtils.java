/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.github.iappapp.panda.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtils {
    private static final Logger log = LoggerFactory.getLogger(IpUtils.class);
    private static final String CMD_GET_HOST_IP_OS7 = "ifconfig {0} | grep \"inet\" | awk ''$1==\"inet\" '{print $2'}''";
    private static final Pattern IP_PATTERN =
            Pattern.compile("((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}");

    public static String getIpByNetwork(String networkName) {
        String localIP = "";
        if (StringUtils.isEmpty(networkName)) {
            networkName = "eth0";
        }
        String getIpCmd = "";
        try {
            getIpCmd = MessageFormat.format(CMD_GET_HOST_IP_OS7, networkName);
            localIP = ShellUtils.execCommand(getIpCmd);
            Matcher m = IP_PATTERN.matcher(localIP);
            if (m.find()) {
                localIP = m.group(0);
            }
        }
        catch (Throwable e) {
            log.error("get host ip by cmd failed, getIpCmd: {}", getIpCmd, e);
        }
        return localIP;
    }

    public static String getMacByIp(String ip) {
        try {
            if (StringUtils.isEmpty(ip)) {
                return "";
            }
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
            if (networkInterface == null) {
                return "";
            }
            byte[] macBytes = networkInterface.getHardwareAddress();
            if (macBytes == null) {
                return "";
            }
            StringBuffer macBuffer = new StringBuffer("");
            for (int i = 0; i < macBytes.length; ++i) {
                int temp;
                String str;
                if (i > 0) {
                    macBuffer.append("-");
                }
                if ((str = Integer.toHexString(temp = macBytes[i] & 0xFF)).length() == 1) {
                    macBuffer.append("0").append(str);
                    continue;
                }
                macBuffer.append(str);
            }
            return macBuffer.toString().toUpperCase();
        }
        catch (SocketException | UnknownHostException e) {
            log.error("get host mac (linux, by NetworkInterface) failed!", e);
            return "";
        }
    }
}

