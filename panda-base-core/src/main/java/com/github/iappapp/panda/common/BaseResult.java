/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2019 All Rights Reserved.
 */
package com.github.iappapp.panda.common;


import com.github.iappapp.panda.code.IErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * integration层的标准返回对象
 * 
 * @author huxuan
 * Created by on 2019-01-18 2:31 PM
 */
@Getter
@Setter
@ToString
public class BaseResult<T> {

    private static final long serialVersionUID = -7707598871808989165L;

    private boolean           isSuccess;

    private String            code;

    private String            desc;

    private T                 data;

    /**
     * 构造成功结果
     * 
     * @param result
     * @param <T>
     * @return
     */
    public static <T> BaseResult<T> success(T result) {
        BaseResult<T> baseResult = new BaseResult<>();
        baseResult.setSuccess(true);
        baseResult.setCode("0");
        baseResult.setDesc("success");
        baseResult.setData(result);
        return baseResult;
    }

    /**
     * 构造失败结果
     * 
     * @param result
     * @param errorMsg
     * @param errorCode
     * @param <T>
     * @return
     */
    public static <T> BaseResult<T> fail(T result, String errorCode, String errorMsg) {
        BaseResult<T> baseResult = new BaseResult<>();
        baseResult.setData(result);
        baseResult.setCode(errorCode);
        baseResult.setDesc(errorMsg);
        baseResult.setSuccess(false);
        return baseResult;
    }

    /**
     *
     * @param result
     * @param errorCode
     * @return
     * @param <T>
     */
    public static <T> BaseResult<T> fail(T result, IErrorCode errorCode) {
        BaseResult<T> baseResult = new BaseResult<>();
        baseResult.setData(result);
        baseResult.setCode(errorCode.getCode());
        baseResult.setDesc(errorCode.getMessage());
        baseResult.setSuccess(false);
        return baseResult;
    }
}
