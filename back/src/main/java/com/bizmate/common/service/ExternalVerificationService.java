package com.bizmate.common.service;

import com.bizmate.common.exception.VerificationFailedException;

/**
 * 외부 시스템 연동 (사업자등록증 진위 확인 등)을 위한 인터페이스
 */
public interface ExternalVerificationService {

    /**
     * 사업자등록번호의 진위 여부를 외부 API를 통해 확인합니다.
     * @param registrationNumber 확인할 사업자등록번호
     * @param representationName 대표자명
     * @return 진위 확인 결과 (예: Map<String, Object> 또는 VerificationResult 객체)
     * @throws com.bizmate.common.exception.VerificationFailedException 외부 API 통신 실패 시
     */
    public boolean verifyBusinessRegistrationNumber(String registrationNumber, String representationName) throws VerificationFailedException;
}
