package com.peergreen.deployment.internal.handler.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;

/**
 * User: guillaume
 * Date: 28/05/13
 * Time: 10:31
 */
public class ReflectiveProcessor<T> implements Processor<T> {
    private final T processor;
    private final Method method;

    public ReflectiveProcessor(final T processor, final Method method) {
        this.processor = processor;
        this.method = method;
    }

    @Override
    public void handle(final Object instance, final ProcessorContext processorContext) throws ProcessorException {
        try {
            method.invoke(processor, instance, processorContext);
        } catch (IllegalAccessException e) {
            throw new ProcessorException("Cannot invoke processor's method", e);
        } catch (InvocationTargetException e) {
            throw new ProcessorException("Cannot invoke processor's method", e.getTargetException());
        }
    }
}
