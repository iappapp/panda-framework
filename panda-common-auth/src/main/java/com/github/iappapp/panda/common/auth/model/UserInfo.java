package com.github.iappapp.panda.common.auth.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author iappapp
 * @date 2020/10/22 17:08
 * <p>
 * 免密获取用户信息对象
 */
@Getter
@Setter
@ToString
public class UserInfo implements Serializable {
    /**
     *
     */
    private int code;
    /**
     *
     */
    private String err;
    /**
     *
     */
    private Biz biz;

    private int is5g;

    private String mobile;

    private String province;

    private int isTelecom;

    private long LoginTime;


    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Biz implements Serializable{
        private String mobile;
    }
}
