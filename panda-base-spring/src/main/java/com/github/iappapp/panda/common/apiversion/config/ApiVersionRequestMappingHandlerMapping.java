package com.github.iappapp.panda.common.apiversion.config;

import com.github.iappapp.panda.common.apiversion.annotation.ApiVersion;
import com.github.iappapp.panda.common.apiversion.constant.ApiConstant;
import com.github.iappapp.panda.common.apiversion.util.ApiConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Slf4j
@AllArgsConstructor
public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return buildFrom(AnnotationUtils.findAnnotation(handlerType, ApiVersion.class));
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return buildFrom(AnnotationUtils.findAnnotation(method, ApiVersion.class));
    }

    public ApiVersionRequestCondition buildFrom(ApiVersion apiVersion) {
        ApiVersionRequestCondition requestCondition =
                apiVersion == null ? getDefaultCondition()
                        : new ApiVersionRequestCondition(ApiConverter.convert(apiVersion.value()), false);

        return requestCondition;
    }

    public ApiVersionRequestCondition getDefaultCondition() {
        return new ApiVersionRequestCondition(ApiConverter.convert(ApiConstant.DEFAULT_VERSION), true);
    }
}
