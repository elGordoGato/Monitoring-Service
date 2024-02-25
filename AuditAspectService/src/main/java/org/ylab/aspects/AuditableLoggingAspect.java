package org.ylab.aspects;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.ylab.domain.entity.UserEntity;

@Aspect
@Component
public class AuditableLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(AuditableLoggingAspect.class);

    @Pointcut("within(@org.ylab.annotations.Auditable *) && execution(public * *(..))")
    public void auditableClass() {}

    @Pointcut("execution(* *(.., org.ylab.domain.entity.UserEntity, ..)) && args(loggedUser, ..) && @annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(requestMapping)")
    public void auditableMethod(UserEntity loggedUser, RequestMapping requestMapping) {}

    @Before("auditableClass() && auditableMethod(loggedUser, requestMapping)")
    public void logBefore(JoinPoint joinPoint, UserEntity loggedUser, RequestMapping requestMapping) {
        String methodName = joinPoint.getSignature().getName();
        String requestUrl = requestMapping.value()[0];
        logger.info("Entering method {} with loggedUser {} and requestUrl {}", methodName, loggedUser, requestUrl);
    }

    @After("auditableClass() && auditableMethod(loggedUser, requestMapping)")
    public void logAfter(JoinPoint joinPoint, UserEntity loggedUser, RequestMapping requestMapping) {
        String methodName = joinPoint.getSignature().getName();
        String requestUrl = requestMapping.value()[0];
        logger.info("Exiting method {} with loggedUser {} and requestUrl {}", methodName, loggedUser, requestUrl);
    }
}