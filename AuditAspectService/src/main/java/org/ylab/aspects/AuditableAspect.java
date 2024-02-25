package org.ylab.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class AuditableAspect {
    @Pointcut("within(@org.ylab.annotations.Auditable *) && execution(public * *(..))")
    public void annotatedByAuditable() {
    }

    @Around("annotatedByAuditable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("Calling method " + proceedingJoinPoint.getSignature());
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println("Execution of method " + proceedingJoinPoint.getSignature() +
                " finished. Execution time is " + (endTime - startTime) + " ms");
        return result;
    }

    @AfterThrowing(value = "annotatedByAuditable()", throwing = "ex")
    public void executeAfterThrowingLoggable(RuntimeException ex) {
        System.out.println("ERROR: " + ex.getMessage());
    }
}
