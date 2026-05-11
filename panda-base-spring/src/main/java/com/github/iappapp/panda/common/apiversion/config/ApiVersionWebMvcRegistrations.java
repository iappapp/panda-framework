package com.github.iappapp.panda.common.apiversion.config;

import com.github.iappapp.panda.common.apiversion.annotation.ApiVersion;
import com.github.iappapp.panda.common.apiversion.constant.ApiConstant;
import com.github.iappapp.panda.common.apiversion.util.ApiConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Slf4j
@AllArgsConstructor
public class ApiVersionWebMvcRegistrations implements WebMvcRegistrations {

    private final ApiVersionProperties apiVersionProperties;

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new HeaderVersionRequestMappingHandlerMapping(apiVersionProperties);
    }

    /**
     * Custom RequestMappingHandlerMapping for HEADER mode
     */
    @Slf4j
    @AllArgsConstructor
    private static class HeaderVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

        private final ApiVersionProperties apiVersionProperties;

        @Override
        protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
            return buildFrom(AnnotationUtils.findAnnotation(handlerType, ApiVersion.class));
        }

        @Override
        protected RequestCondition<?> getCustomMethodCondition(Method method) {
            return buildFrom(AnnotationUtils.findAnnotation(method, ApiVersion.class));
        }

        private ApiVersionRequestCondition buildFrom(ApiVersion apiVersion) {
            // If @ApiVersion annotation is not present, use default version 1.0.0
            if (apiVersion == null) {
                return getDefaultCondition();
            }
            
            return new ApiVersionRequestCondition(
                ApiConverter.convert(apiVersion.value()), false, apiVersionProperties);
        }

        private ApiVersionRequestCondition getDefaultCondition() {
            // Default version 1.0.0, marked as isNull=true
            return new ApiVersionRequestCondition(
                ApiConverter.convert(ApiConstant.DEFAULT_VERSION), true, apiVersionProperties);
        }
    }
}
