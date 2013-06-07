package com.peergreen.deployment.internal.handler.internal.utils;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;

/**
 * User: guillaume
 * Date: 24/05/13
 * Time: 11:48
 */
public class TypesTestCase {


    @Test
    public void testFindParametrizedType() throws Exception {
        assertEquals(Types.findParametrizedType(ParametrizedProcessor.class), Artifact.class);
    }

    @Test
    public void testFindParametrizedTypeWithInheritance() throws Exception {
        assertEquals(Types.findParametrizedType(ExtendedParametrizedProcessor2.class), Artifact.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFindParametrizedTypeWithFailingInheritance() throws Exception {
        Types.findParametrizedType(ExtendedParametrizedProcessor.class);
    }

    @Test
    public void testFindMethod() throws Exception {
        assertEquals(Types.findMethod(SimpleProcessor.class).getParameterTypes()[0], Artifact.class);
    }

    @Test
    public void testFindMethodWithSuperClass() throws Exception {
        assertEquals(Types.findMethod(ExtendedProcessor.class).getParameterTypes()[0], Artifact.class);
    }

/*
    @Test
    public void testFindMethodWithSuperClassAndGenerics() throws Exception {
        Types Types = new Types();
        assertEquals(Types.findMethod(ExtendedProcessor2.class).getParameterTypes()[0], Artifact.class);
    }
*/

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFindMethodWithMissingPublicMethod() throws Exception {
        Types.findMethod(MissingPublicMethodProcessor.class);
    }

    @Test
    public void testFindMethodWithZeroException() throws Exception {
        assertEquals(Types.findMethod(ZeroExceptionProcessor.class).getParameterTypes()[0], Artifact.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFindMethodWithTooMuchExceptions() throws Exception {
        Types.findMethod(TooMuchExceptionProcessor.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFindMethodWithOneInvalidException() throws Exception {
        Types.findMethod(OneInvalidExceptionProcessor.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFindMethodWithMissingProcessorContext() throws Exception {
        Types.findMethod(MissingProcessorContextProcessor.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFindMethodWithMissingType() throws Exception {
        Types.findMethod(MissingTypeProcessor.class);
    }

    public static class SimpleProcessor {

        public void handle(final Artifact instance, final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static abstract class BaseProcessor {

        public void handle(final Artifact instance, final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class ExtendedProcessor extends BaseProcessor { }

/*
    public static abstract class BaseProcessor2<T> {

        public void handle(final T instance, final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class ExtendedProcessor2 extends BaseProcessor2<Artifact> { }
*/

    public static class MissingPublicMethodProcessor {
        private void handle(final Artifact instance, final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class MissingTypeProcessor {
        public void doNotMatter(final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class ZeroExceptionProcessor {
        public void doNotMatter(Artifact o, final ProcessorContext processorContext) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class TooMuchExceptionProcessor {
        public void doNotMatter(Object o, final ProcessorContext processorContext) throws ProcessorException, IllegalAccessException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class OneInvalidExceptionProcessor {
        public void doNotMatter(Object o, final ProcessorContext processorContext) throws IllegalAccessException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class MissingProcessorContextProcessor {
        public void doNotMatter(Object o) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class ParametrizedProcessor implements Processor<Artifact> {
        @Override
        public void handle(final Artifact instance, final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static abstract class AbstractParametrizedProcessor<T> implements Processor<T> {
        @Override
        public void handle(final T instance, final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class ExtendedParametrizedProcessor extends AbstractParametrizedProcessor<Artifact> {
        @Override
        public void handle(final Artifact instance, final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class ExtendedParametrizedProcessor2 extends AbstractParametrizedProcessor<Artifact> implements Processor<Artifact> {
        @Override
        public void handle(final Artifact instance, final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

}
