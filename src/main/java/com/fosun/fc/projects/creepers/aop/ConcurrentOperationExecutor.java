package com.fosun.fc.projects.creepers.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import com.fosun.fc.projects.creepers.annotation.IsTryAgain;

@Aspect
@Component
public class ConcurrentOperationExecutor implements Ordered {

    private static final int DEFAULT_MAX_RETRIES = 2;

    private int maxRetries = DEFAULT_MAX_RETRIES;
    private int order = 1;

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    // @Pointcut("execution(* com..creepers.service.impl..saveOrUpdate(..))")
    // public void saveOrUpdate() {
    // }

    @Around(value = "@annotation(isTryAgain)")
    public Object doConcurrentOperation(ProceedingJoinPoint pjp,IsTryAgain isTryAgain) throws Throwable {
        int numAttempts = 0;
        OptimisticLockingFailureException optimisticLockingFailureException = null;
        do {
            numAttempts++;
            try {
                return pjp.proceed();
            } catch (OptimisticLockingFailureException optiEx) {
                optimisticLockingFailureException = optiEx;
            } 
        } while (numAttempts <= this.maxRetries);
        throw optimisticLockingFailureException;
    }

}
