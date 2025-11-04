package com.bizmate.common.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 사용자 로그 생성 및 감사(Audit) 기능을 중앙 집중화하는 AOP Aspect.
 * '등록/수정/삭제' 이벤트가 발생할 때 자동으로 로그를 기록합니다.
 */

@Aspect
@Component
public class UserLoggingAspect {

    // 권한 검증 어노테이션 정의 (사용자 정의 어노테이션)
    // 이 어노테이션을 서비스 메서드에 붙여서 필요한 권한을 명시할 수 있습니다.
    public @interface RequiredPermission {
        String value(); // 필요한 권한 (예: "ROLE_ADMIN", "PERMISSION_CUSTOMER_WRITE")
    }

    // Pointcut 정의:
    // com.example.demo.service 패키지 내의 모든 클래스, 모든 메서드
    @Pointcut("execution(* com.bizmate.service.*.*(..))")
    public void serviceLayerMethods() {}

    /**
     * [권한 검증 로직]
     * @RequiredPermission 어노테이션이 붙은 메서드가 실행되기 전에 권한을 체크합니다.
     */
    @Before("@annotation(permission)")
    public void checkPermission(JoinPoint joinPoint, RequiredPermission permission) throws Throwable {
        String requiredRole = permission.value(); //권한 제한 기준값
        String currentUserId = "로그인되어있는 계정";
        String userRoles = "유저 권한들 리스트";

//        if(!권한여부) {
//            System.err.printf("[권한 오류] 사용자 %s는 %s권한이 없어 접근이 거부되었습니다.\n", currentUserId, requiredRole);
//            throw new RuntimeException("Access Denied: Required permission " + requiredRole + " is missing.");
//        }
//        System.out.printf("[권한 체크] 사용자 %s, 권한 %s 확인 완료.%n", currentUserId, requiredRole);

    }

    /**
     * [사용자 로그 기록 로직]
     * CRUD 작업(create, update, delete로 시작하는 메서드)이 성공적으로 완료(반환)된 후 로그를 기록합니다.
     * 트랜잭션이 커밋된 후를 보장하기 위해 서비스 계층에서 호출되는 메서드에 적용됩니다.
     */
    @AfterReturning(pointcut = "serviceLayerMethods() && (execution(* *..create*(..)) || execution(* *..update*(..)) || execution(* *..delete*(..)))", returning = "result")
    public void logAfterCrudOperation(JoinPoint joinPoint, Object result) {

        String actionType = getActionType(joinPoint.getSignature().getName());
        String targetModule = getTargetModule(joinPoint.getTarget().getClass().getSimpleName());

        //로그 테이블에 기록하는 로직 작성 필요
    }

    private String getTargetModule(String className) {
        if(className.contains("모듈1")) return "거래처 관리";
        if(className.contains("모듈2")) return "매출 관리";
        if(className.contains("모듈3")) return "프로젝트 관리";
        if(className.contains("모듈4")) return "인사 관리";
        if(className.contains("모듈5")) return "그룹웨어";
        return className.replace("Service", "");

    }

    private String getActionType(String methodName) {
        if(methodName.toLowerCase().contains("create")) return "등록(CREATE)";
        if(methodName.toLowerCase().contains("update")) return "수정(UPDATE)";
        if(methodName.toLowerCase().contains("delete")) return "삭제(DELETE)";
        return "기타 작업";
    }
}
