package com.github.iappapp.panda.common.auth.util;


import com.github.iappapp.panda.common.auth.model.UserInfo;

public class UserInfoContext {
    private static final ThreadLocal<UserInfo> userInfoContext = new ThreadLocal<>();

    public static UserInfo getUserInfo() {
        return userInfoContext.get();
    }

    public static void clear() {
        userInfoContext.remove();
    }

    public static void setUserInfo(UserInfo userInfo) {
        userInfoContext.set(userInfo);
    }
}
