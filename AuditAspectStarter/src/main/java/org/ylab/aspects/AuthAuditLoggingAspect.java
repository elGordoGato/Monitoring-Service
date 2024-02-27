package org.ylab.aspects;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ylab.domain.dto.UserDto;

import java.time.Instant;
import java.util.Arrays;

@Aspect
public class AuthAuditLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(AuthAuditLoggingAspect.class);

    @Pointcut("within(@org.ylab.annotations.AuthAudit *) && execution(public * *(..))")
    public void annotatedByAuditable() {
    }

    /**
     * @param joinPoint we can find inside it all the details of the method
     */
    @Before("annotatedByAuditable()")
    public void logRequest(JoinPoint joinPoint) {
        logger.info(getLogEntryFromJoinPoint(joinPoint));
    }

    /**
     * @param joinPoint we need to use it to see attributes in the original method
     * @return will return String after building all the attributes
     */
    private String getLogEntryFromJoinPoint(JoinPoint joinPoint) {
        var obj = joinPoint.getArgs();
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        StringBuilder requestValue = new StringBuilder(Instant.now().toString());
        requestValue.append("\n");
        requestValue.append(ms.getDeclaringTypeName());
        requestValue.append(" received request to ");
        requestValue.append(ms.getName());
        if (obj.length < 1) {
            requestValue.append("\nWithout parameters");
        } else {
            Arrays.stream(obj).forEach(x -> {
                if (x instanceof UserDto) {
                    requestValue.append("\nWith parameters: ");
                    requestValue.append(x);
                }
            });
        }
        return requestValue.toString();
    }
}