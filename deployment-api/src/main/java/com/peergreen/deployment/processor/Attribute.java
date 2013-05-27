package com.peergreen.deployment.processor;

/**
 * User: guillaume
 * Date: 13/05/13
 * Time: 16:28
 */
public @interface Attribute {
    String name();
    String value() default "";
}
