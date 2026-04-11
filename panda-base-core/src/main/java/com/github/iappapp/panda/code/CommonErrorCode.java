/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.code;

/**
 * 通用错误码
 *
 */
public enum CommonErrorCode implements IErrorCode {
    SUCCESS("200", "", 200),
    CLIENT_ERROR("400", "client error", 400),
    TOKEN_NULL_ERROR("401", "The token is null!", 401),
    TOKEN_INVALID_ERROR("402", "The token is invalid!", 402),
    REQUEST_FORBIDDEN("403", "Forbidden!", 403),
    NO_HANDLER_ERROR("404", "No handler found Exception!", 404),
    OLD_PARAMS_VALID_ERROR("405", "params validation failed \uff1a{0}", 405),
    SYSTEM_ERROR("500", "System error!"),
    BUSINESS_ERROR("520", "Business error!", 520),
    XSS_ERROR("600", "XSS attack found, access denied !", 600),

    INVALID_PARAM("00001", "非法参数", 405),

    SESSION_TIME_OUT("99997", "登录超时，请重新登录", 402),

    BIZ_SERVICE_ERROR("99998", "业务端返回错误", 520),
    ;

    String code;
    String message;
    int statusCode;

    private CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private CommonErrorCode(String code, String message, int statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    @Override
    public String getCode() {
        return String.valueOf(this.code);
    }

    public String getCodeInt() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Integer getStatusCode() {
        return this.statusCode;
    }
}

