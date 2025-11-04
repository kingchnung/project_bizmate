package com.bizmate.hr.domain;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.domain.Department; // Department 엔티티
import com.bizmate.hr.domain.code.Grade; // Grade 엔티티 (직급 코드)
import com.bizmate.hr.domain.code.Position; // Position 엔티티 (직책 코드)

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [인사발령 이력 Entity]
 * 데이터베이스의 ASSIGNMENTS_HISTORY 테이블과 매핑됩니다.
 * 이력 관리 목적으로, 수정/삭제가 불가능하도록 설계됩니다.
 */
@Entity
@Table(name = "ASSIGNMENTS_HISTORY")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentsHistory {

    // 1. 발령ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ass_id")
    private Long assId;

    // 2. 직원ID (FK) - 필수
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    // 3. 발령일자 - 필수
    @Column(name = "ass_date", nullable = false)
    private LocalDate assDate;


    // --- 변경 전 정보 (이전 정보는 null일 수 있음 - 신규 입사자의 첫 발령 등) ---

    // 4. 이전부서ID (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_dept_id")
    private Department previousDepartment;

    // 6. 이전 직책코드 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_position_code")
    private Position previousPosition;

    // 8. 이전 직급코드 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_grade_code")
    private Grade previousGrade;


    // --- 변경 후 정보 (신규 정보는 필수) ---

    // 5. 신규부서ID (FK) - 필수
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_dept_id", nullable = false)
    private Department newDepartment;

    // 7. 신규 직책코드 (FK) - 필수
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_position_code", nullable = false)
    private Position newPosition;

    // 9. 신규 직급코드 (FK) - 필수
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_grade_code", nullable = false)
    private Grade newGrade;


    // 10. 발령사유
    @Column(name = "reason", length = 1000)
    private String reason;

    // 11. 등록자 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cre_by")
    private UserEntity createdBy;

    // 12. 등록일
    @Column(name = "cre_date", updatable = false)
    @Builder.Default
    private LocalDateTime creDate = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 기본값 설정

    @PrePersist
    public void prePersist(){
        if(this.creDate == null) this.creDate = LocalDateTime.now();
    }

    // 참고: 이 엔티티는 이력 기록용이므로 Setter는 사용하지 않고 Getter만 유지하는 것을 권장합니다.
    // Lombok의 @Builder를 사용하여 필요한 모든 필드를 초기화합니다.
}