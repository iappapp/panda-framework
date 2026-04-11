/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2022 All Rights Reserved.
 */
package com.github.iappapp.panda.common.statemachine.builder;

/**
 * @author taosy
 * Created by on 2022-05-25 下午5:46
 */
public interface To<S, E, C> {

    On<S, E, C> on(E event);
}