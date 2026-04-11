package com.github.iappapp.panda.common.apiversion.config;

import com.github.iappapp.panda.common.apiversion.constant.ApiConstant;
import com.github.iappapp.panda.common.apiversion.util.ApiConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;

@Getter
@Setter
@Slf4j
public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {
    public static ApiVersionRequestCondition empty =
            new ApiVersionRequestCondition(ApiConverter.convert(ApiConverter.DEFAULT_VERSION), true);

    private ApiItem apiVersion;

    private boolean isNull;

    public ApiVersionRequestCondition() {
    }

    public ApiVersionRequestCondition(ApiItem apiItem, boolean isNull) {
        this.apiVersion = apiItem;
        this.isNull = isNull;
    }

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        if (other.isNull) {
            return this;
        }

        return other;
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest httpServletRequest) {
        if (CorsUtils.isPreFlightRequest(httpServletRequest)) {
            return empty;
        }

        String version = httpServletRequest.getHeader(ApiConstant.API_VERSION_HEADER);
        if (StringUtils.isEmpty(version)) {
            version = ApiConstant.DEFAULT_VERSION;
        }
        ApiItem item = ApiConverter.convert(version);

        if (item.compareTo(ApiItem.API_ITEM_DEFAULT) < 0) {
            throw new IllegalArgumentException("API version illegal");
        }
        if (item.compareTo(this.apiVersion) >= 0) {
            return this;
        }

        return null;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest httpServletRequest) {
        String version = httpServletRequest.getHeader(ApiConstant.API_VERSION_HEADER);
        if (StringUtils.isEmpty(version)) {
            return 0;
        }
        ApiItem apiItem = new ApiItem(version);
        // TODO
        int compare = apiItem.compareTo(other.getApiVersion());

        return compare;
    }
}
