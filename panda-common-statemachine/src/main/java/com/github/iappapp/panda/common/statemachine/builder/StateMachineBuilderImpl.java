/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2022 All Rights Reserved.
 */
package com.github.iappapp.panda.common.statemachine.builder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.iappapp.panda.common.statemachine.State;
import com.github.iappapp.panda.common.statemachine.StateMachine;
import com.github.iappapp.panda.common.statemachine.StateMachineFactory;
import com.github.iappapp.panda.common.statemachine.impl.StateMachineImpl;
import com.github.iappapp.panda.common.statemachine.impl.TransitionType;

/**
 * StateMachineBuilderImpl
 *
 * @author taosy
 * Created by on 2022-05-25 下午5:55
 */
public class StateMachineBuilderImpl<S, E, C> implements StateMachineBuilder<S, E, C> {

    private final Map<S, State<S, E, C>> stateMap = new ConcurrentHashMap<>();
    private final StateMachineImpl<S, E, C> stateMachine = new StateMachineImpl<>(stateMap);

    @Override
    public ExternalTransitionBuilder<S, E, C> externalTransition() {
        return new TransitionBuilderImpl<>(stateMap, TransitionType.EXTERNAL);
    }

    @Override
    public ExternalTransitionsBuilder<S, E, C> externalTransitions() {
        return new TransitionsBuilderImpl<>(stateMap, TransitionType.EXTERNAL);
    }

    @Override
    public InternalTransitionBuilder<S, E, C> internalTransition() {
        return new TransitionBuilderImpl<>(stateMap, TransitionType.INTERNAL);
    }

    @Override
    public StateMachine<S, E, C> build(String machineId) {
        stateMachine.setMachineId(machineId);
        stateMachine.setReady(true);
        StateMachineFactory.register(stateMachine);
        return stateMachine;
    }
}