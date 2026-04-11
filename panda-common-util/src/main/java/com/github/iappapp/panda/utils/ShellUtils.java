/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.github.iappapp.panda.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellUtils {
    private static final Logger log = LoggerFactory.getLogger(ShellUtils.class);
    private static final String CHMOD_CMD = "chmod 777 ";
    private static final String SH_CMD = "sh ";

    public static String execCommand(String command) {
        if (StringUtils.isEmpty(command)) {
            log.debug("shell command is empty or invalid: {}", command);
            return "";
        }
        log.debug("exec cmd: {}", command);
        String result = null;
        try {
            String[] commands = new String[]{"/bin/sh", "-c", command};
            Process process = Runtime.getRuntime().exec(commands);
            if (process != null
                    && (result = ShellUtils.processStdout(process.getInputStream(), Charset.defaultCharset().toString())) != null) {
                result = result.trim();
            }
        }
        catch (Exception e) {
            log.error("exec command fail!", e);
        }
        return result;
    }

    public static String execFile(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            log.debug("shell file path is empty or invalid: {}", filePath);
            return "";
        }
        log.debug("shell file path: {}", filePath);
        String result = "";
        String chmodCmd = CHMOD_CMD + filePath;
        try {
            Runtime.getRuntime().exec(chmodCmd).waitFor();
            Process process = Runtime.getRuntime().exec(SH_CMD + filePath);
            if (process != null
                    && (result = ShellUtils.processStdout(process.getInputStream(), Charset.defaultCharset().toString())) != null) {
                result = result.trim();
            }
        }
        catch (Exception e) {
            log.error("execute shell file fail!", e);
        }
        return result;
    }

    private static String processStdout(InputStream in, String charset) {
        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        try {
            while (in.read(buf) != -1) {
                sb.append(new String(buf, charset));
            }
        }
        catch (IOException e) {
            log.error("IOException ", e);
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                log.error("IOException ", e);
            }
        }
        return sb.toString();
    }
}

