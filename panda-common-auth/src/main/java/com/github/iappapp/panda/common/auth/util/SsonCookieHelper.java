package com.github.iappapp.panda.common.auth.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.iappapp.panda.common.auth.model.RequestInfo;
import com.github.iappapp.panda.common.auth.model.UserInfo;
import com.github.iappapp.panda.utils.AESUtil;
import com.github.iappapp.panda.utils.HttpResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author liush2
 * @date 2019/8/31 15:57
 * @remarks 处理cookie工具
 */
public class SsonCookieHelper {

    /**
     * cookie的名称以及域名
     */
    public static final String COOKIE_NAME = "X-Subject-Token";


    /**
     * cookie加密密钥
     */
    @Value("${project.info.domain:}")
    private String cookieDomain;

    @Value("${project.info.cookie:}")
    private String cookieEncryptPassword;

    /**
     * 从cookie中解析用户信息
     *
     * @param request
     * @return
     */
    public String parseUserInfoFromCookie(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String cookieValue = getCookieValue(request, COOKIE_NAME, "");

        String clearCookie = decodeCookie(cookieValue);

        String mobile = getMobileFromCookie(clearCookie);

        return mobile;
    }

    public String parseMobile(String cookie) {
        if (StringUtils.isEmpty(cookie)) {
            return null;
        }

        String clearCookie = decodeCookie(cookie);

        String mobile = getMobileFromCookie(clearCookie);


        return mobile;

    }

    public String getCookieValue(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        return getCookieValue(request, COOKIE_NAME, "");
    }

    /**
     * 解密cookie 得到明文cookie
     *
     * @param signCookie
     * @return
     */
    public String decodeCookie(String signCookie) {
        if (StringUtils.isEmpty(signCookie)) {
            UserInfo userInfo = new UserInfo();
            userInfo.setMobile("13212341234");
            userInfo.setLoginTime(new Date().getTime());
            return JSON.toJSONString(userInfo);
        }
        //解密cookie,得到明文cookie

        return AESUtil.decrypt(signCookie, cookieEncryptPassword);
    }

    /**
     * 加密cookie
     *
     * @param clearCookie
     * @return
     */
    public String encryptCookie(String clearCookie) {
        if (StringUtils.isEmpty(clearCookie)) {
            return null;
        }

        return AESUtil.encrypt(clearCookie, cookieEncryptPassword);


    }

    /**
     * 从明文cookie中获取手机号
     *
     * @param clearCookie
     * @return
     */
    public static String getMobileFromCookie(String clearCookie) {
        if (StringUtils.isEmpty(clearCookie)) {
            return null;
        }

        //从cookie中获取手机号
        UserInfo userInfo = JSONObject.parseObject(clearCookie, UserInfo.class);
        if (userInfo != null) {
            return userInfo.getMobile();
        }
        return null;
    }

    /**
     * 原始数据生成明文cookie
     *
     * @param mobile
     * @return
     */
    public static String generatorClearCookie(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return null;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setMobile(mobile);
        userInfo.setLoginTime(new Date().getTime());
        return JSONObject.toJSONString(userInfo);
    }


    public String generatorCookie(String mobile) {

        return encryptCookie(generatorClearCookie(mobile));
    }


    /**
     * 登出
     *
     * @param response
     */
    public void loginOut(HttpServletResponse response) {
        HttpResponseUtil.deleteCookie(response, COOKIE_NAME, cookieDomain, "/");
    }


    public void setCookie(HttpServletResponse response, String cookie) {
        HttpResponseUtil.setCookie(response, COOKIE_NAME, cookie, cookieDomain, "/", Integer.MAX_VALUE);
    }

    public void setCookie(HttpServletResponse response, String cookie, boolean setDomain) {
        if (setDomain) {
            HttpResponseUtil.setCookie(response, COOKIE_NAME, cookie, cookieDomain, "/", Integer.MAX_VALUE);
        } else {
            HttpResponseUtil.setCookie(response, COOKIE_NAME, cookie, null, "/", Integer.MAX_VALUE);
        }
    }

    /**
     * 从request中获取用户参数
     *
     * @param request
     * @return
     */
    public UserInfo getUserInfo(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        RequestInfo requestInfo = new RequestInfo(request);

        String cookieValue = requestInfo.getCookieValue(COOKIE_NAME, "");

        String clearCookie = decodeCookie(cookieValue);
        return JSONObject.parseObject(clearCookie, UserInfo.class);
    }

    public Cookie getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        } else {
            for (int i = 0; i < cookies.length; ++i) {
                if (cookies[i].getName().equals(cookieName)) {
                    return cookies[i];
                }
            }

            return null;
        }
    }

    public String getCookieValue(HttpServletRequest request, String cookieName, String defaultValue) {
        Cookie c = this.getCookie(request, cookieName);
        if (c == null) {
            return defaultValue;
        } else {
            String value = c.getValue();
            return value != null && value.trim().length() > 0 ? value : defaultValue;
        }
    }
}
