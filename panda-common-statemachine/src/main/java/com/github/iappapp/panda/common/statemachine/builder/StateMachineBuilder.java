/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2022 All Rights Reserved.
 */
package com.github.iappapp.panda.common.statemachine.builder;

import com.github.iappapp.panda.common.statemachine.StateMachine;

/**
 * StateMachineBuilder
 *
 * @param <S> the type of state
 * @param <E> the type of event
 * @param <C> the type of user defined context, which is used to hold application data
 * @author taosy
 * Created by on 2022-05-25 下午5:45
 */
public interface StateMachineBuilder<S, E, C> {

    ExternalTransitionBuilder<S, E, C> externalTransition();

    ExternalTransitionsBuilder<S, E, C> externalTransitions();

    InternalTransitionBuilder<S, E, C> internalTransition();

    StateMachine<S, E, C> build(String machineId);
}