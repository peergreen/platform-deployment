package com.peergreen.deployment.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Stereotype;

/**
 * Note: Do not change the @RetentionPolicy
 */
@Component
@Instantiate
@HandlerDeclaration("<ns:processor xmlns:ns='com.peergreen.deployment' />")
@Stereotype
@Target(ElementType.TYPE)
public @interface Processor {
}
