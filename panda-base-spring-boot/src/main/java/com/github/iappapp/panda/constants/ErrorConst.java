/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2019 All Rights Reserved.
 */
package com.github.iappapp.panda.constants;

/**
 * 基础错误枚举值定义
 * 约定所有的系统常用异常都是这几个错误码
 * 
 * @author huxuan
 * Created by on 2019-01-18 2:19 PM
 */
public enum ErrorConst {
    /** success*/
    SUCCESS("00000", "成功"),

    INVALID_PARAM("00001", "非法参数"),

    SESSION_TIME_OUT("99997", "登录超时，请重新登录"),

    BIZ_SERVICE_ERROR("99998", "业务端返回错误"),

    SYSTEM_ERROR("99999", "系统错误");

    private String code;

    private String desc;

    private ErrorConst(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}