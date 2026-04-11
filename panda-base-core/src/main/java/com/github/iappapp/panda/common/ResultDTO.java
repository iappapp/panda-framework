/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 */
package com.github.iappapp.panda.common;

import com.github.iappapp.panda.code.IErrorCode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResultDTO<T> {
    private static final long serialVersionUID = -3826124069030603542L;
    private T data;
    private String code;
    private String message;

    public static <T> ResultDTO<T> success() {
        return ResultDTO.createResult(null, null, null);
    }

    public static <T> ResultDTO<T> success(T data) {
        ResultDTO<T> result = ResultDTO.success();
        result.setData(data);
        return result;
    }

    public static <T> ResultDTO<T> fail(IErrorCode errorCode) {
        return ResultDTO.fail(errorCode.getCode(), errorCode.getValue(), errorCode.getMessage(), errorCode.getSource());
    }

    public static <T> ResultDTO<T> fail(IErrorCode errorCode, List<String> value) {
        return ResultDTO.fail(errorCode, value, errorCode.getSource());
    }

    public static <T> ResultDTO<T> fail(IErrorCode errorCode, List<String> value, String source) {
        return ResultDTO.fail(errorCode.getCode(), value, errorCode.getMessage(), source);
    }

    public static <T> ResultDTO<T> fail(IErrorCode errorCode, String message) {
        return ResultDTO.fail(errorCode.getCode(), errorCode.getValue(), message, errorCode.getSource());
    }

    public static <T> ResultDTO<T> fail(String code, String message) {
        return ResultDTO.createResult(null, code, message);
    }

    public static <T> ResultDTO<T> fail(String code, List<String> value, String message) {
        return ResultDTO.fail(code, value, message, "");
    }

    public static <T> ResultDTO<T> fail(String code, List<String> value, String message, String source) {
        value = value == null ? new ArrayList<>() : value;
        source = source == null ? "" : source;
        return ResultDTO.createResult(null, code, message);
    }

    public static <T> ResultDTO<T> createResult(T data, String code, String message) {
        ResultDTO<T> result = new ResultDTO<T>();
        result.setData(data);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

}

