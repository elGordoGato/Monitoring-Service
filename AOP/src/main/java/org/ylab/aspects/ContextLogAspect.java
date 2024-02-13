package org.ylab.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ContextLogAspect {
    @Pointcut("call(void jakarta.servlet.ServletContext.log(String)) && args(message)")
    public void contextLogCall(String message) {
    }

    @Before(value = "contextLogCall(message)", argNames = "joinPoint,message")
    public void printMessage(JoinPoint joinPoint, String message) {
        System.out.println("User audit message: " + message);
    }
}