package com.bizmate.hr.dto.user;

import com.bizmate.hr.domain.Role;
import com.bizmate.hr.domain.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [사용자 계정 조회 및 응답 DTO]
 * - 계정 목록 및 상세 정보를 프론트엔드로 전송할 때 사용
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class UserResponseDTO {

    private final Long userId;
    private final Long empId;
    private final String username;      // 로그인 ID

    private final boolean Active;      // 계정 활성 여부
    private final boolean Locked;      // 계정 잠금 여부
    private final LocalDateTime lastLogin;
    private final Integer failedCount;

    // 조회 편의를 위한 직원 정보 복제 필드
    private final String empName;
    private final String email;
    private final String phone;
    private final String positionName;
    private final String deptName;      // 추가된 부서명

    // 권한 정보
    private final List<String> roleNames; // 사용자가 가진 역할(Role) 이름 목록

    private final LocalDateTime creDate;
    private final LocalDateTime updDate;

    /**
     * Entity -> DTO 변환 메서드
     */
    public static UserResponseDTO fromEntity(UserEntity userEntity) {
        // user.getRoles()를 스트림으로 변환하여 역할 이름을 추출합니다.
        List<String> roles = userEntity.getRoles() == null ? List.of() :
                userEntity.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        return UserResponseDTO.builder()
                .userId(userEntity.getUserId())
                .empId(userEntity.getEmployee() != null ? userEntity.getEmployee().getEmpId() : null)
                .username(userEntity.getUsername())
                .Active("Y".equalsIgnoreCase(userEntity.getIsActive()))
                .Locked("N".equalsIgnoreCase(userEntity.getIsLocked()))
                .lastLogin(userEntity.getLastLogin())
                .failedCount(userEntity.getFailedCount())

                // 복제된 필드
                .empName(userEntity.getEmpName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .positionName(userEntity.getPositionName())
                .deptName(userEntity.getDeptName())

                .roleNames(roles)
                .creDate(userEntity.getCreDate())
                .updDate(userEntity.getUpdDate())
                .build();
    }
}