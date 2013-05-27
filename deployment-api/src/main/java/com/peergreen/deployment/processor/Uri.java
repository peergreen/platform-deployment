package com.peergreen.deployment.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Express a Processor's requirement(s) to the URI of the processed artifact.
 * @author Guillaume Sauthier
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Uri {

    /**
     * Required URI's scheme.
     * For instance, int the following URI {@literal http://www.example.org},
     * {@literal http} is the scheme.
     * @return required URI's scheme
     */
    String value() default "";

    /**
     * Required path extension.
     * For instance, int the following URI {@literal http://www.example.org/file.zip},
     * {@literal zip} is the extension.
     * @return required URI's path extension
     */
    String extension() default "";
}
