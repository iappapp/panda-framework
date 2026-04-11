/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2022 All Rights Reserved.
 */
package com.github.iappapp.panda.common.statemachine.builder;

import com.github.iappapp.panda.common.statemachine.Action;

/**
 * When
 *
 * @author taosy
 * Created by on 2022-05-25 下午5:48
 */
public interface When<S, E, C> {

    void perform(Action<S, E, C>... action);
}