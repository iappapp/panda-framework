/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2022 All Rights Reserved.
 */
package com.github.iappapp.panda.common.statemachine.builder;


import com.github.iappapp.panda.common.statemachine.Condition;

/**
 * On
 *
 * @author taosy
 * Created by on 2022-05-25 下午5:46
 */
public interface On<S, E, C> extends When<S, E, C> {

    When<S, E, C> when(Condition<C> condition);
}