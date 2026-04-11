/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2019 All Rights Reserved.
 */
package com.github.iappapp.panda.controlleradvice;

import java.io.File;

import com.alibaba.fastjson.JSON;
import com.github.iappapp.panda.common.BaseResult;
import com.github.iappapp.panda.constants.ErrorConst;
import com.github.iappapp.panda.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 封装RestController的结果
 *
 * Created by on 2019-01-18 3:40 PM
 */
@RestControllerAdvice
public class PandaResponseBodyAdvice implements ResponseBodyAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(PandaResponseBodyAdvice.class);

    /**
     * 需要拦截的方法类
     * 目前只拦截RestController
     * 参考org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor#supportsReturnType(org.springframework.core.MethodParameter)
     * 
     * @param methodParameter
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return (AnnotatedElementUtils.hasAnnotation(methodParameter.getContainingClass(),
            ResponseBody.class) || methodParameter.hasMethodAnnotation(ResponseBody.class));
    }

    /**
     * 捕获当前业务类的业务异常
     * 
     * @param e
     * @return
     */
    @ExceptionHandler(value = BizException.class)
    public BaseResult<Object> bizErrorErrorHandler(BizException e) {
        LOGGER.warn("A business exception has occurred to the system", e);
        return BaseResult.fail(null, e.getCode(), e.getExtraMsg());
    }

    @ExceptionHandler(value = Exception.class)
    public BaseResult<Object> errorHandler(Exception e) {
        LOGGER.warn("A system exception has occurred to the system", e);
        return BaseResult.fail(null, ErrorConst.SYSTEM_ERROR.getCode(),
            ErrorConst.SYSTEM_ERROR.getDesc());
    }

    /**
     * 
     * @param body
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                  Class aClass, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        if (body == null) {
            return null;
        }

        if (body instanceof BaseResult || excludeResult(body)) {
            return body;
        } else if (body instanceof File) {
            return body;
        } else if (body instanceof String) {
            BaseResult baseResult = BaseResult.success(body);
            return JSON.toJSONString(baseResult);
        } else {
            return BaseResult.success(body);
        }
    }

    /**
     * 为了之前系统中的JsonResp，增加过滤方法，使得业务系统在使用时复写方法，这样兼容旧代码
     * 
     * @return
     */
    public boolean excludeResult(Object body) {
        return false;
    }
}
