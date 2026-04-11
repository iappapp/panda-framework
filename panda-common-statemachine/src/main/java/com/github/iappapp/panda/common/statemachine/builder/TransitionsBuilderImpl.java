package com.github.iappapp.panda.common.statemachine.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.iappapp.panda.common.statemachine.Action;
import com.github.iappapp.panda.common.statemachine.Condition;
import com.github.iappapp.panda.common.statemachine.State;
import com.github.iappapp.panda.common.statemachine.Transition;
import com.github.iappapp.panda.common.statemachine.impl.StateHelper;
import com.github.iappapp.panda.common.statemachine.impl.TransitionType;

/**
 * TransitionsBuilderImpl
 *
 * @author taosy
 * Created by on 2022-05-25 下午5:46
 */
public class TransitionsBuilderImpl<S, E, C> extends TransitionBuilderImpl<S, E, C> implements ExternalTransitionsBuilder<S, E, C> {
    /**
     * This is for fromAmong where multiple sources can be configured to point to one target
     */
    private final List<State<S, E, C>> sources = new ArrayList<>();

    private final List<Transition<S, E, C>> transitions = new ArrayList<>();

    public TransitionsBuilderImpl(Map<S, State<S, E, C>> stateMap, TransitionType transitionType) {
        super(stateMap, transitionType);
    }

    @SafeVarargs
    @Override
    public final From<S, E, C> fromAmong(S... stateIds) {
        for (S stateId : stateIds) {
            sources.add(StateHelper.getState(super.stateMap, stateId));
        }
        return this;
    }

    @Override
    public On<S, E, C> on(E event) {
        for (State<S, E, C> source : sources) {
            Transition<S, E, C> transition = source.addTransition(event, super.target, super.transitionType);
            transitions.add(transition);
        }
        return this;
    }

    @Override
    public When<S, E, C> when(Condition<C> condition) {
        for (Transition<S, E, C> transition : transitions) {
            transition.setCondition(condition);
        }
        return this;
    }

    @Override
    public void perform(Action<S, E, C>... action) {
        for (Transition<S, E, C> transition : transitions) {
            transition.setAction(Stream.of(action).collect(Collectors.toList()));
        }
    }
}
