package com.muzam.aop;

import jakarta.persistence.EntityManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;

@Component
@Aspect
public class AuditTrailAspectGeneric {

    private final AuditLogRepository auditLogRepository;
    private final EntityManager entityManager;

    public AuditTrailAspectGeneric(AuditLogRepository auditLogRepository, EntityManager entityManager) {
        this.auditLogRepository = auditLogRepository;
        this.entityManager = entityManager;
    }

    @Pointcut("@annotation(com.muzam.aop.Auditable)")
    public void auditableMethods() {}

    @Around("auditableMethods()")
    public Object logAuditTrail(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        String oldValue = null;
        String entityId = null;

        try {
            Object[] args = joinPoint.getArgs();

            // Pre-execution: Capture old value dynamically
            if (args.length > 0 && args[0] != null) {
                Object entity = args[0];
                Class<?> entityClass = entity.getClass();

                // Check if the entity has an ID
                Object id = getIdFromEntity(entity);
                if (id != null) {
                    Object existingEntity = entityManager.find(entityClass, id);
                    if (existingEntity != null) {
                        oldValue = existingEntity.toString();
                        entityId = id.toString();
                    }
                }
            }

            result = joinPoint.proceed(); // Execute the method

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Auditable auditable = method.getAnnotation(Auditable.class);

            // Post-execution: Build audit log
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(auditable.action());
            auditLog.setEntityName(auditable.entityName());
            auditLog.setOldValue(oldValue);

            // Capture new value
            if (result != null) {
                auditLog.setNewValue(result.toString());
                if (entityId == null) {
                    entityId = getIdFromEntity(result).toString();
                }
            }
            auditLog.setEntityId(entityId);
            auditLog.setUsername("system"); // Replace with actual username from context

            // Save audit log
            auditLogRepository.save(auditLog);

        } catch (Throwable throwable) {
            throw throwable;
        }
        return result;
    }

    private Object getIdFromEntity(Object entity) {
        try {
            return entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            return null;
        }
    }
}
