package com.github.lkq.smesh.docker;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class TimingProxyFactory {
    private static Logger logger = LoggerFactory.getLogger(TimingProxyFactory.class);

    public static <T> T create(T target) {
        try {
            T proxy = (T) Enhancer.create(target.getClass(), new Interceptor(target));
            logger.info("created proxy for {}", target.getClass().getSimpleName());
            return proxy;
        } catch (IllegalArgumentException e) {
            logger.info("required default constructor");
            throw e;
        }
    }

    static class Interceptor implements MethodInterceptor {

        private Object target;
        private Logger logger;

        public <T> Interceptor(T target) {
            this.target = target;
            logger = LoggerFactory.getLogger(target.getClass());
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            Timing timingAnnotation = method.getDeclaredAnnotation(Timing.class);
            long startTime = 0;
            if (timingAnnotation != null) {
                startTime = System.currentTimeMillis();
            }
            Object result = method.invoke(target, objects);
            if (timingAnnotation != null) {
                logger.info("{} execution time: {} ms", method.getName(), System.currentTimeMillis() - startTime);
            }
            return result;
        }
    }
}
