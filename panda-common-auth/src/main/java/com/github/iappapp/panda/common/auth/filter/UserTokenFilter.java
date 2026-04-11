package com.github.iappapp.panda.common.auth.filter;

import com.alibaba.fastjson.JSON;
import com.github.iappapp.panda.common.auth.configuration.AuthTokenProperties;
import com.github.iappapp.panda.common.auth.model.UserInfo;
import com.github.iappapp.panda.common.auth.util.JwtUtils;
import com.github.iappapp.panda.common.auth.util.UserInfoContext;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class UserTokenFilter implements Filter {

    private AuthTokenProperties authTokenProperties;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public UserTokenFilter(AuthTokenProperties authTokenProperties) {
        this.authTokenProperties = authTokenProperties;
    }

    public UserTokenFilter() {
    }

    private final static String JWT_HEADER = "jwtToken";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwtHeader = ((HttpServletRequest) request).getHeader(JWT_HEADER);
        String contextPath = ((HttpServletRequest) request).getContextPath();
        String uri = ((HttpServletRequest) request).getRequestURI();
        String urlPattern = uri.replaceFirst(contextPath, "");

        boolean ignore = false;
        for (String ignoreUrl : authTokenProperties.getIgnoreUrlPatterns()) {
            if (antPathMatcher.match(urlPattern, ignoreUrl)) {
                ignore = true;
                break;
            }
        }

        if (ignore) {
            chain.doFilter(request, response);
            return;
        }

        UserInfo userInfo = null;
        if (StringUtils.isEmpty(jwtHeader)) {
            sendError(request, response);
            return;
        } else {
            try {
                String token = JwtUtils.decodeToken(jwtHeader, authTokenProperties.getSecret());
                userInfo = JSON.parseObject(token, UserInfo.class);
            } catch (Exception ex) {
                sendError(request, response);
                return;
            }
        }
        UserInfoContext.setUserInfo(userInfo);
        chain.doFilter(request, response);
    }

    private void sendError(ServletRequest request, ServletResponse response) {
        try {
            Map<String, Object> result = Maps.newHashMap();
            result.put("code", HttpStatus.UNAUTHORIZED.value());
            result.put("message", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            HttpServletResponse httpServletResponse = ((HttpServletResponse) response);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.getWriter().write(JSON.toJSONString(result));
            httpServletResponse.getWriter().flush();
            httpServletResponse.getWriter().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        // Filter.super.destroy();
        UserInfoContext.clear();
    }
}
