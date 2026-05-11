package com.github.iappapp.panda.common.apiversion.config;

import com.github.iappapp.panda.common.apiversion.annotation.ApiVersion;
import com.github.iappapp.panda.common.apiversion.constant.ApiConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@AllArgsConstructor
public class UriApiVersionWebMvcRegistrations implements WebMvcRegistrations {

    private final ApiVersionProperties apiVersionProperties;

    // Pattern to match and extract version from URL path (ONLY at the end)
    private static final Pattern VERSION_PATH_PATTERN = Pattern.compile("^(.*?)/v(\\d+\\.\\d+\\.\\d+)$");

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new UriVersionRequestMappingHandlerMapping(apiVersionProperties);
    }

    /**
     * Custom RequestMappingHandlerMapping for URI mode that strips version from path
     * Supports both original URL matching AND versioned URL matching
     */
    @Slf4j
    @AllArgsConstructor
    private static class UriVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

        private final ApiVersionProperties apiVersionProperties;


        @Override
        protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
            // Check if method has @ApiVersion annotation
            ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
            
            if (apiVersion != null && !apiVersion.value().equals(ApiConstant.DEFAULT_VERSION)) {
                // For methods with explicit @ApiVersion annotation:
                // ONLY register the versioned URL (e.g., /api/users/v1.2.0)
                // Do NOT register the original URL to avoid ambiguous mapping conflicts
                RequestMappingInfo versionedMapping = createVersionedMapping(mapping, apiVersion.value());
                
                super.registerHandlerMethod(handler, method, versionedMapping);
                log.debug("[URI Mode] Registered versioned mapping: {} for method {}", 
                    versionedMapping.getPatternsCondition(), method.getName());
            } else {
                // For methods without @ApiVersion or with default version 1.0.0:
                // Register the original URL (e.g., /api/users)
                super.registerHandlerMethod(handler, method, mapping);
                log.debug("[URI Mode] Registered original mapping (default v1.0.0): {} for method {}", 
                    mapping.getPatternsCondition(), method.getName());
            }
        }

        /**
         * Create a new RequestMappingInfo with version suffix appended to all path patterns
         * 
         * @param originalMapping the original request mapping
         * @param version the API version (e.g., "1.2.0")
         * @return new mapping with versioned paths
         */
        private RequestMappingInfo createVersionedMapping(RequestMappingInfo originalMapping, String version) {
            // Get original patterns
            Set<String> originalPatterns = originalMapping.getPatternsCondition().getPatterns();
            
            // Create versioned patterns by appending /v{version}
            Set<String> versionedPatterns = new HashSet<>();
            for (String pattern : originalPatterns) {
                // Remove trailing slash if present, then append version
                String cleanPattern = pattern.endsWith("/") ? pattern.substring(0, pattern.length() - 1) : pattern;
                versionedPatterns.add(cleanPattern + "/v" + version);
            }
            
            // Build new mapping with versioned patterns
            RequestMappingInfo.Builder builder = RequestMappingInfo.paths(versionedPatterns.toArray(new String[0]));
            
            // Preserve methods condition
            Set<RequestMethod> methods = originalMapping.getMethodsCondition().getMethods();
            if (!methods.isEmpty()) {
                builder.methods(methods.toArray(new RequestMethod[0]));
            }
            
            // Preserve params condition
            Set<String> params = originalMapping.getParamsCondition().getExpressions().stream()
                .map(expr -> expr.toString())
                .collect(java.util.stream.Collectors.toSet());
            if (!params.isEmpty()) {
                builder.params(params.toArray(new String[0]));
            }
            
            // Preserve headers condition
            Set<String> headers = originalMapping.getHeadersCondition().getExpressions().stream()
                .map(expr -> expr.toString())
                .collect(java.util.stream.Collectors.toSet());
            if (!headers.isEmpty()) {
                builder.headers(headers.toArray(new String[0]));
            }
            
            // Preserve consumes condition
            Set<String> consumes = originalMapping.getConsumesCondition().getExpressions().stream()
                .map(expr -> expr.toString())
                .collect(java.util.stream.Collectors.toSet());
            if (!consumes.isEmpty()) {
                builder.consumes(consumes.toArray(new String[0]));
            }
            
            // Preserve produces condition
            Set<String> produces = originalMapping.getProducesCondition().getExpressions().stream()
                .map(expr -> expr.toString())
                .collect(java.util.stream.Collectors.toSet());
            if (!produces.isEmpty()) {
                builder.produces(produces.toArray(new String[0]));
            }
            
            // Preserve custom condition (ApiVersionRequestCondition)
            RequestCondition<?> customCondition = originalMapping.getCustomCondition();
            if (customCondition != null) {
                builder.customCondition(customCondition);
            }
            
            return builder.build();
        }

        @Override
        protected RequestMappingInfo getMatchingMapping(RequestMappingInfo info, HttpServletRequest request) {
            String requestUri = request.getRequestURI();
            String contextPath = request.getContextPath();
            
            // Remove context path if present
            String pathWithoutContext = requestUri;
            if (contextPath != null && !contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
                pathWithoutContext = requestUri.substring(contextPath.length());
            }
            
            // Try to strip version from path
            String strippedPath = stripVersionFromPath(pathWithoutContext);
            
            if (strippedPath != null && !strippedPath.equals(pathWithoutContext)) {
                log.debug("[URI Mode] Stripped version from path: {} -> {}", pathWithoutContext, strippedPath);
                
                // Create a modified request with stripped path for matching
                HttpServletRequest wrappedRequest = new VersionStrippedHttpServletRequest(request, 
                    contextPath + strippedPath);
                RequestMappingInfo mapping = super.getMatchingMapping(info, wrappedRequest);
                if (mapping != null) {
                    log.debug("[URI Mode] Found matching mapping with stripped version path: {}", mapping.getPatternsCondition());
                    return mapping;
                }
            }
            // fallback
            return super.getMatchingMapping(info, request);
        }

        private String stripVersionFromPath(String path) {
            if (path == null || path.isEmpty()) {
                return path;
            }
            
            Matcher matcher = VERSION_PATH_PATTERN.matcher(path);
            if (matcher.matches()) {
                String prefix = matcher.group(1);
                log.debug("[URI Mode] Stripped version from end of path: {} -> {}", path, prefix);
                return prefix;
            }
            
            return path;
        }

        /**
         * HttpServletRequest wrapper that overrides getRequestURI
         */
        private static class VersionStrippedHttpServletRequest extends javax.servlet.http.HttpServletRequestWrapper {
            private final String strippedRequestURI;

            public VersionStrippedHttpServletRequest(HttpServletRequest request, String strippedURI) {
                super(request);
                this.strippedRequestURI = strippedURI;
            }

            @Override
            public String getRequestURI() {
                return strippedRequestURI;
            }

            @Override
            public StringBuffer getRequestURL() {
                StringBuffer originalUrl = super.getRequestURL();
                String originalUri = super.getRequestURI();
                if (originalUri != null && originalUri.length() > 0) {
                    int endIndex = originalUrl.length() - originalUri.length();
                    return new StringBuffer(originalUrl.substring(0, endIndex) + strippedRequestURI);
                }
                return originalUrl;
            }
        }
    }
}
