package org.ylab.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ContextLogAspect {
    @Pointcut("execution(void javax.servlet.ServletContext.log(String)) && args(message)")
    public void contextLogExecution(String message) {
    }

    @Before(value = "contextLogExecution(message)", argNames = "joinPoint,message")
    public void printMessage(JoinPoint joinPoint, String message) {
        System.out.println("Context log message: " + message);
    }
}