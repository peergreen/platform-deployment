package com.peergreen.deployment.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.peergreen.deployment.DiscoveryPhasesLifecycle;

/**
 * User: guillaume
 * Date: 13/05/13
 * Time: 13:48
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Discovery {
    DiscoveryPhasesLifecycle value();
}
