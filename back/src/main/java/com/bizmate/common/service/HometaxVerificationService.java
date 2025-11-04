package com.bizmate.common.service;

import com.bizmate.common.exception.VerificationFailedException;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties;

public class HometaxVerificationService implements ExternalVerificationService{

    private static final String HOMETAX_API_URL = "https://hometax.gov.kr/api/check";
    @Override
    public boolean verifyBusinessRegistrationNumber(String registrationNumber, String representationName) throws VerificationFailedException {

        //1. 입력 데이터 유효성 검사
        if(registrationNumber == null || registrationNumber.length() != 10) {
            throw new IllegalArgumentException("유효하지 않은 사업자등록번호 형식입니다.");
        }

        System.out.println("--- [외부 API 호출 시뮬레이션] ---");
        System.out.printf("대상 : %s, 대표자 : %s%n", registrationNumber, representationName);

        try {
            //외부 API 통신 지연 시뮬레이션
            Thread.sleep(500);

            // API 응답 시뮬레이션
            if(registrationNumber.endsWith("0000")) {
                return false;
            }

            System.out.println("Result: 사업자등록번호 유효함.");
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new VerificationFailedException("API 통신 중 서버 중단 오류 발생.", e);
        } catch (Exception e) {
            throw new VerificationFailedException("외부 국세청 API 통신 중 오류 발생.", e);
        }
    }
}
