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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Slf4j
public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {
    public static ApiVersionRequestCondition empty =
            new ApiVersionRequestCondition(ApiConverter.convert(ApiConverter.DEFAULT_VERSION), true);

    private ApiItem apiVersion;

    private boolean isNull;

    private ApiVersionProperties apiVersionProperties;

    // Pattern to match version suffix in URL path (ONLY at the end)
    // Supports: /api/users/v1.2.0 ✅
    // NOT supports: /api/v1.2.0/users ❌
    private static final Pattern VERSION_PATTERN = Pattern.compile(".*?/v(\\d+\\.\\d+\\.\\d+)$");

    public ApiVersionRequestCondition() {
    }

    public ApiVersionRequestCondition(ApiItem apiItem, boolean isNull) {
        this.apiVersion = apiItem;
        this.isNull = isNull;
    }

    public ApiVersionRequestCondition(ApiItem apiItem, boolean isNull, ApiVersionProperties properties) {
        this.apiVersion = apiItem;
        this.isNull = isNull;
        this.apiVersionProperties = properties;
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

        // Get version based on configured type
        String version = extractVersion(httpServletRequest);
        
        // If no version found, use default
        if (StringUtils.isEmpty(version)) {
            version = ApiConstant.DEFAULT_VERSION;
        }
        
        ApiItem item = ApiConverter.convert(version);

        if (item.compareTo(ApiItem.API_ITEM_DEFAULT) < 0) {
            throw new IllegalArgumentException("API version illegal: " + version);
        }
        
        // Match if request version >= handler version
        if (item.compareTo(this.apiVersion) >= 0) {
            return this;
        }

        return null;
    }

    /**
     * Extract version based on configured type (HEADER or URI)
     * 
     * @param request the HTTP servlet request
     * @return version string if found, null otherwise
     */
    private String extractVersion(HttpServletRequest request) {
        if (apiVersionProperties == null) {
            // Default to HEADER mode if properties not set
            return request.getHeader(ApiConstant.API_VERSION_HEADER);
        }

        ApiVersionProperties.Type type = apiVersionProperties.getType();
        
        switch (type) {
            case HEADER:
                String headerName = StringUtils.isNotEmpty(apiVersionProperties.getHeader()) 
                    ? apiVersionProperties.getHeader() 
                    : ApiConstant.API_VERSION_HEADER;
                return request.getHeader(headerName);
                
            case URI:
                return extractVersionFromPath(request.getRequestURI());
                
            case PARAM:
                // TODO: Support query parameter mode if needed
                return request.getParameter("api-version");
                
            default:
                log.warn("Unknown API version type: {}, defaulting to HEADER", type);
                return request.getHeader(ApiConstant.API_VERSION_HEADER);
        }
    }

    /**
     * Extract version from URL path (ONLY when version is at the end)
     * Supports patterns like: /api/users/v1.2.0, /products/v1.0.0
     * NOT supports: /api/v1.2.0/users, /v1.0.0/products/list
     * Note: Only supports versions with 'v' prefix at the END of path
     * 
     * @param requestURI the request URI
     * @return version string if found at end, null otherwise
     */
    private String extractVersionFromPath(String requestURI) {
        if (StringUtils.isEmpty(requestURI)) {
            return null;
        }
        
        Matcher matcher = VERSION_PATTERN.matcher(requestURI);
        if (matcher.matches()) {
            String version = matcher.group(1);
            log.debug("Extracted version {} from end of URI: {}", version, requestURI);
            return version;
        }
        
        return null;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest httpServletRequest) {
        // Compare this handler's version with other handler's version
        // Higher version should have higher priority (return negative means this has higher priority)
        return other.getApiVersion().compareTo(this.apiVersion);
    }
}
