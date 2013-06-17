package com.peergreen.deployment.internal.handler.utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;

/**
 * User: guillaume
 * Date: 15/05/13
 * Time: 09:19
 */
public class Types {

    public static Method findMethod(Class<?> base) {

        for (Method method : base.getMethods()) {
            if (isCompatible(method)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Cannot find any public method respecting the following convention: [name]([facet-type], ProcessorContext) in " + base.getName());
    }

    private static boolean isCompatible(final Method method) {

        // Expected format:
        // <name>(<type>, ProcessorContext) throws ProcessorException
        List<Type> parameters = Arrays.asList(method.getGenericParameterTypes());
        if (parameters.size() != 2) {
            return false;
        }

        if (!parameters.get(1).equals(ProcessorContext.class)) {
            return false;
        }

        if (parameters.get(0) instanceof TypeVariable) {
            return false;
        }

        // Accept no Exceptions declared
        // If Exceptions are declared, only 1 of type ProcessorException is accepted
        List<Class<?>> exceptions = Arrays.asList(method.getExceptionTypes());
        if (exceptions.isEmpty()) {
            return true;
        } else if ((exceptions.size() == 1) && (exceptions.contains(ProcessorException.class))) {
            return true;
        }

        return false;
    }

    public static Class<?> findParametrizedType(final Class<? extends Processor> processorType) {
        for (Type type : processorType.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (Processor.class.equals(parameterizedType.getRawType())) {
                    Type param = parameterizedType.getActualTypeArguments()[0];
                    if (param instanceof Class) {
                        return (Class) param;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Cannot find the generic type for Processor<T> that should be implemented by " + processorType.getName());
    }
}
