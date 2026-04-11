package com.github.iappapp.panda.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author liush2
 * @date 2019/8/27 14:17
 * @remarks
 */
@Getter
@Setter
@ToString
public class HttpClientResult {
    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应数据
     */
    private String content;

    public HttpClientResult() {
    }

    public HttpClientResult(int code) {
        this.code = code;
    }

    public HttpClientResult(int code, String content) {
        this.code = code;
        this.content = content;
    }


    public boolean isRequestSuccess() {
        if (this.code != null && code == 200) {
            return true;
        }
        return false;
    }

}
