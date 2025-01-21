//package com.muzam.aop;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//import java.util.Optional;
//
//@Component
//@Aspect
//public class AuditTrailAspect {
//
//    private final AuditLogRepository auditLogRepository;
//    private final CustomerRepository customerRepository;
//
//    public AuditTrailAspect(AuditLogRepository auditLogRepository, CustomerRepository customerRepository) {
//        this.auditLogRepository = auditLogRepository;
//        this.customerRepository = customerRepository;
//    }
//
//    @Pointcut("@annotation(com.muzam.aop.Auditable)")
//    public void auditableMethods() {}
//
//    @Around("auditableMethods()")
//    public Object logAuditTrail(ProceedingJoinPoint joinPoint) throws Throwable {
//        Object result = null;
//        try {
//            Object[] args = joinPoint.getArgs();
//
//            // Pre-execution: Capture old value if updating or deleting
//            String oldValue = null;
//            if (args.length > 0 && args[0] instanceof Customer) {
//                Customer customer = (Customer) args[0];
//                Optional<Customer> existingCustomer = null == customer.getId() ? Optional.empty():
//                        customerRepository.findById(customer.getId());
//                if (existingCustomer.isPresent()) {
//                    oldValue = existingCustomer.get().toString();
//                }
//            }
//
//            result = joinPoint.proceed(); // Execute the method
//
//            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//            Method method = signature.getMethod();
//            Auditable auditable = method.getAnnotation(Auditable.class);
//
//            // Post-execution: Build audit log
//            AuditLog auditLog = new AuditLog();
//            auditLog.setAction(auditable.action());
//            auditLog.setEntityName(auditable.entityName());
//            auditLog.setOldValue(oldValue);
//
//            // Capture new value
//            if (result instanceof Customer) {
//                auditLog.setEntityId(String.valueOf(((Customer) result).getId()));
//                auditLog.setNewValue(result.toString());
//            } else if (args.length > 0 && args[0] instanceof Long) { // Handle delete
//                auditLog.setEntityId(String.valueOf(args[0]));
//            }
//
//            auditLog.setUsername("system"); // Replace with actual username from context
//
//            // Save audit log
//            auditLogRepository.save(auditLog);
//        } catch (Throwable throwable) {
//            throw throwable;
//        }
//        return result;
//    }
//}
