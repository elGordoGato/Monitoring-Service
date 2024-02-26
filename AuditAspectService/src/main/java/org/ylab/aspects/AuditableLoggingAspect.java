package org.ylab.aspects;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.ylab.domain.entity.UserEntity;

import java.time.Instant;
import java.util.Arrays;

@Aspect
@Component
public class AuditableLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(AuditableLoggingAspect.class);

    @Pointcut("within(@org.ylab.annotations.Auditable *) && execution(public * *(..))")
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
        Object[] obj = joinPoint.getArgs();
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
                if (x instanceof UserEntity) {
                    requestValue.append("\nFrom user with email: ");
                    requestValue.append(((UserEntity) x).getEmail());
                    requestValue.append(" and id: ");
                    requestValue.append(((UserEntity) x).getId());
                } else {
                    requestValue.append("\nWith parameters: ");
                    requestValue.append(x.toString());
                }
            });
        }
        requestValue.append("\nReturning ");
        requestValue.append(ms.getReturnType().getName());
        return requestValue.toString();
    }
}