/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2019 All Rights Reserved.
 */
package com.github.iappapp.panda.exception;

import com.github.iappapp.panda.code.IErrorCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 所以业务异常继承该类
 * 
 * @author huxuan
 * Created by on 2019-01-18 3:09 PM
 */
@Getter
@Setter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = -3116525963766185969L;

    private String            code;

    private String            extraMsg;

    private String            message;

    public BizException() {

    }

    public BizException(String message) {
        super(message);
        this.extraMsg = message;
        this.message = message;
    }

    public BizException(String message, Throwable t) {
        super(message, t);
        this.extraMsg = message;
        this.message = message;
    }

    public BizException(Throwable t) {
        super(t);
    }

    public BizException(String code, String extraMsg) {
        super(extraMsg);
        this.extraMsg = extraMsg;
        this.code = code;
        this.message = extraMsg;
    }

    public BizException(String code, String extraMsg, String message) {
        super(message);
        this.extraMsg = extraMsg;
        this.code = code;
        this.message = extraMsg;
    }

    public BizException(String code, String extraMsg, Throwable t) {
        super(t);
        this.code = code;
        this.extraMsg = extraMsg;
        this.message = extraMsg;
    }

    public BizException(IErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
        this.extraMsg = errorCode.getMessage();
    }
}
