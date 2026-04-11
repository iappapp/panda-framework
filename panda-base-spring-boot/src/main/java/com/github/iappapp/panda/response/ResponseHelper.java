/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 *  com.dahua.panda.base.core.code.CommonErrorCode
 *  com.dahua.panda.base.core.code.IErrorCode
 *  com.dahua.panda.base.core.exception.SystemException
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.response;

import com.alibaba.fastjson.JSON;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

import com.github.iappapp.panda.code.CommonErrorCode;
import com.github.iappapp.panda.code.IErrorCode;
import com.github.iappapp.panda.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResponseHelper {

    public void writeJsonResponse(HttpServletResponse response, Object data) {
        if (data == null || response == null) {
            log.error("Data or HttpServletResponse is null");
            return;
        }
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "application/json");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0L);
        try (PrintWriter writer = response.getWriter()){
            writer.write(JSON.toJSONString(data));
        }
        catch (Exception e) {
            throw new SystemException(CommonErrorCode.SYSTEM_ERROR, e);
        }
    }
}

