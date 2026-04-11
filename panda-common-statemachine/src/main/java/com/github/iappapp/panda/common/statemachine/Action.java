/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2022 All Rights Reserved.
 */
package com.github.iappapp.panda.common.statemachine;

/**
 * Action
 *
 * @author taosy
 * Created by on 2022-05-25 下午5:35
 */
public interface Action<S, E, C> {

    default void before(S from, S to, E event, C context) {
    }

    void execute(S from, S to, E event, C context);

    default void after(S from, S to, E event, C context) {
    }
}