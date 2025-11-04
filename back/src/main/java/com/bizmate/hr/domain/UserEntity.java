package com.bizmate.hr.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * [사용자 계정 Entity]
 * 데이터베이스의 USERS 테이블과 매핑됩니다.
 * Spring Security 인증 및 권한 부여에 사용되는 핵심 엔티티입니다.
 */
@Entity
@Table(name = "USERS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    // 1. 사용자ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // 2. 직원 ID (FK & Unique - 직원 당 하나의 계정)
    // EMPLOYEES와 1:1 관계 (FK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", unique = true, nullable = true)
    private Employee employee;

    // 3. 사용자명 (로그인 ID)
    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    // 4. 비밀번호 해시
    @Column(name = "pw_hash", nullable = false, length = 255)
    private String pwHash;

    // 5. 계정 활성 여부 ('Y'/'N')
    @Column(name = "is_active", nullable = false, length = 1)
    @Builder.Default
    private String isActive = "Y";

    // 6. 계정 잠금 여부 ('Y'/'N')
    @Column(name = "is_locked", nullable = false, length = 1)
    @Builder.Default
    private String isLocked = "N";

    // 7. 마지막 로그인
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // 8. 로그인 실패 횟수
    @Column(name = "failed_count")
    @Builder.Default
    private Integer failedCount = 0;

    // 9. 생성일
    @Column(name = "cre_date", nullable = false)
    private LocalDateTime creDate;

    // 10. 수정일
    @Column(name = "upd_date")
    private LocalDateTime updDate;

    @PrePersist
    public void prePersist() {
        this.creDate = LocalDateTime.now();
        this.updDate = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.updDate = LocalDateTime.now();
    }

    // 11~15. 조회 편의를 위한 직원 정보 복제 필드 (데이터 정합성 관리가 필요함)
    @Column(name = "emp_name", length = 50)
    private String empName;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "position_name", length = 100)
    private String positionName;

    // 15. 부서명 (추가됨)
    @Column(name = "dept_name", length = 50)
    private String deptName;

    @Column(name = "dept_code", length = 10)
    private String deptCode;


    // N:M 관계 매핑 (사용자-역할 매핑)
    // user_roles 테이블을 통해 다대다 관계를 매핑합니다.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}