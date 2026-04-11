/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2022 All Rights Reserved.
 */
package com.github.iappapp.panda.common.statemachine;

/**
 * Condition
 *
 * @author taosy
 * Created by on 2022-05-25 下午5:49
 */
public interface Condition<C> {

    boolean isSatisfied(C context);
}