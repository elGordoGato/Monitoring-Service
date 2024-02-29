package org.ylab.aspects;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.ylab.domain.dto.UserDto;
import org.ylab.domain.entity.UserEntity;
import org.ylab.domain.mapper.UserMapper;
import org.ylab.entity.AuditEntry;
import org.ylab.repository.AuditRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class AuditAspect {
    private final UserMapper mapper;
    private final AuditRepository repository;

    @Pointcut("within(@org.ylab.annotations.Auditable *) && execution(public * *(..))")
    public void annotatedByAuditable() {
    }

    @Pointcut("within(@org.ylab.annotations.AuthAudit *) && execution(public * *(..))")
    public void annotatedByAuthAudit() {
    }

    /**
     * @param joinPoint we can find inside it all the details of the method
     */
    @Before("annotatedByAuditable()")
    public void logRequest(JoinPoint joinPoint) {
        AuditEntry entryToSave = getAuditEntryFromJoinPoint(joinPoint);
        AuditEntry savedEntry = repository.save(entryToSave);
        log.info(savedEntry.toString());
    }

    @AfterReturning(pointcut = "annotatedByAuthAudit()", returning = "userDto")
    private void logAuth(JoinPoint joinPoint, UserDto userDto) {
        UserEntity user = mapper.toUser(userDto);
        AuditEntry entryToSave = getAuditEntryFromJoinPoint(joinPoint, user);
        AuditEntry savedEntry = repository.save(entryToSave);
        log.info(savedEntry.toString());
    }

    /**
     * @param joinPoint we need to use it to see attributes in the original method
     * @return will return AuditEntry after building all the attributes
     */
    private AuditEntry getAuditEntryFromJoinPoint(JoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Object[] obj = joinPoint.getArgs();

        AuditEntry entry = new AuditEntry();
        entry.setController(ms.getDeclaringTypeName());
        entry.setMethod(ms.getName());
        List<String> params = new ArrayList<>();
        Arrays.stream(obj).forEach(x -> {
            if (x instanceof UserEntity) {
                entry.setRequester((UserEntity) x);
            } else {
                params.add(x.toString());
            }
        });
        entry.setParams(params);
        return entry;
    }

    private AuditEntry getAuditEntryFromJoinPoint(JoinPoint joinPoint, UserEntity user) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();

        AuditEntry entry = new AuditEntry();
        entry.setController(ms.getDeclaringTypeName());
        entry.setMethod(ms.getName());
        entry.setRequester(user);
        return entry;
    }
}