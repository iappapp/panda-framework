/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2019 All Rights Reserved.
 */
package com.github.iappapp.panda.utils;

import java.util.Collection;

import com.github.iappapp.panda.constants.ErrorConst;
import com.github.iappapp.panda.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author huxuan
 * Created by on 2019-03-02 10:21 AM
 */
public class AssertUtil {

    /**
     * 判断对象不为空
     *
     * @param object 对象
     * @exception
     */
    public static void notNull(Object object) {
        if (object == null) {
            throw new BizException(ErrorConst.INVALID_PARAM.getCode(),
                ErrorConst.INVALID_PARAM.getDesc());
        }
    }

    /**
     * 判断对象不为空
     *
     * @param object 对象
     * @exception
     */
    public static void notNull(Object object, String errorMsg) {
        if (object == null) {
            throw new BizException(ErrorConst.INVALID_PARAM.getCode(), errorMsg);
        }
    }

    /**
     *
     * @param object
     */
    public static void notEmpty(String object) {
        if (StringUtils.isEmpty(object)) {
            throw new BizException(ErrorConst.INVALID_PARAM.getCode(),
                ErrorConst.INVALID_PARAM.getDesc());
        }
    }

    /**
     *
     * @param object
     */
    public static void notEmpty(String object, String errorMsg) {
        if (StringUtils.isEmpty(object)) {
            throw new BizException(ErrorConst.INVALID_PARAM.getCode(), errorMsg);
        }
    }

    /**
     * 集合参数不能为空
     * 
     * @param collection
     */
    public static void notEmpty(Collection collection) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BizException(ErrorConst.INVALID_PARAM.getCode(),
                ErrorConst.INVALID_PARAM.getDesc());
        }
    }

    /**
     * 集合参数不能为空
     * 
     * @param collection
     * @param errorMsg
     */
    public static void notEmpty(Collection collection, String errorMsg) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BizException(ErrorConst.INVALID_PARAM.getCode(), errorMsg);
        }
    }
}
