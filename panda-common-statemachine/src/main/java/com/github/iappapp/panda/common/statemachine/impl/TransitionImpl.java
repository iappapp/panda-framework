package com.github.iappapp.panda.common.statemachine.impl;


import java.util.List;

import com.github.iappapp.panda.common.statemachine.Action;
import com.github.iappapp.panda.common.statemachine.Condition;
import com.github.iappapp.panda.common.statemachine.State;
import com.github.iappapp.panda.common.statemachine.Transition;

/**
 * TransitionImpl。
 * <p>
 * This should be designed to be immutable, so that there is no thread-safe risk
 *
 * @author taosy
 * Created by on 2022-05-25 下午5:51
 */
public class TransitionImpl<S, E, C> implements Transition<S, E, C> {

    private State<S, E, C> source;

    private State<S, E, C> target;

    private E event;

    private Condition<C> condition;

    private List<Action<S, E, C>> action;

    private TransitionType type = TransitionType.EXTERNAL;

    @Override
    public State<S, E, C> getSource() {
        return source;
    }

    @Override
    public void setSource(State<S, E, C> state) {
        this.source = state;
    }

    @Override
    public E getEvent() {
        return this.event;
    }

    @Override
    public void setEvent(E event) {
        this.event = event;
    }

    @Override
    public void setType(TransitionType type) {
        this.type = type;
    }

    @Override
    public State<S, E, C> getTarget() {
        return this.target;
    }

    @Override
    public void setTarget(State<S, E, C> target) {
        this.target = target;
    }

    @Override
    public Condition<C> getCondition() {
        return this.condition;
    }

    @Override
    public void setCondition(Condition<C> condition) {
        this.condition = condition;
    }

    @Override
    public List<Action<S, E, C>> getAction() {
        return this.action;
    }

    @Override
    public void setAction(List<Action<S, E, C>> action) {
        this.action = action;
    }

    @Override
    public State<S, E, C> transit(C ctx, boolean checkCondition) {
        this.verify();
        if (!checkCondition || condition == null || condition.isSatisfied(ctx)) {
            if (action != null && action.size() > 0) {
                action.forEach(ac -> {
                    ac.before(source.getId(), target.getId(), event, ctx);
                    ac.execute(source.getId(), target.getId(), event, ctx);
                    ac.after(source.getId(), target.getId(), event, ctx);
                });
            }
            return target;
        }
        return source;
    }

    @Override
    public final String toString() {
        return source + "-[" + event.toString() + ", " + type + "]->" + target;
    }

    @Override
    public void verify() {
        if (type == TransitionType.INTERNAL && source != target) {
            throw new StateMachineException(
                    String.format("Internal transition source state '%s' " + "and target state '%s' must be same.",
                            source, target));
        }
    }
}
