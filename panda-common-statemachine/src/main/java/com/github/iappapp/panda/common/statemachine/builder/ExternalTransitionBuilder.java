/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2022 All Rights Reserved.
 */
package com.github.iappapp.panda.common.statemachine.builder;

/**
 * ExternalTransitionBuilder
 *
 * @author taosy
 * Created by on 2022-05-25 下午5:45
 */
public interface ExternalTransitionBuilder<S, E, C> {

    From<S, E, C> from(S stateId);
}