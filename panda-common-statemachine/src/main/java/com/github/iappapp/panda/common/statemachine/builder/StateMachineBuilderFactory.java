/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2022 All Rights Reserved.
 */
package com.github.iappapp.panda.common.statemachine.builder;

/**
 * StateMachineBuilderFactory
 *
 * @author taosy
 * Created by on 2022-05-25 下午5:55
 */
public class StateMachineBuilderFactory {

    public static <S, E, C> StateMachineBuilder<S, E, C> create() {
        return new StateMachineBuilderImpl<>();
    }
}