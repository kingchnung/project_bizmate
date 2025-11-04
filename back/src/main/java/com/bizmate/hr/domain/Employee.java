package com.bizmate.hr.domain;

import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.domain.code.Position;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [직원 Entity]
 * 데이터베이스의 EMPLOYEES 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "EMPLOYEES")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    // 1. 직원ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long empId;

    // 2. 사번 (유일키)
    @Column(name = "emp_no", unique = true, nullable = false, length = 12)
    private String empNo;

    // 3. 직원명
    @Column(name = "emp_name", nullable = false, length = 100)
    private String empName;

    // 4. 성별
    @Column(name = "gender", length = 1)
    private String gender;

    // 5. 생년월일
    @Column(name = "birth_date")
    private LocalDate birthDate;

    // 6. 전화번호
    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    // 7. 이메일 (유일키)
    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    // 8. 주소
    @Column(name = "address", length = 400)
    private String address;

    // 9. 부서ID (FK) -> Department Entity 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    // 10. 직책
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_code", nullable = false)
    private Position position;

    // 11. 직급
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_code", nullable = false)
    private Grade grade;

    // 12. 입사일
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 13. 퇴사일
    @Column(name = "leave_date")
    private LocalDate leaveDate;

    // 14. 재직상태
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    // 15. 경력연수
    @Column(name = "career_years")
    private Double careerYears;

    // 16. 주민번호 마스킹
    @Column(name = "ssn_mask", length = 20)
    private String ssnMask;

    // 17. 생성자 (FK) -> User Entity 참조 (누가 데이터를 만들었는지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cre_by")
    private UserEntity creBy;

    // 18. 생성일
    @Column(name = "cre_date")
    private LocalDateTime creDate;

    // 19. 수정자 (FK) -> User Entity 참조 (누가 데이터를 수정했는지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upd_by")
    private UserEntity updBy;

    // 20. 수정일
    @Column(name = "upd_date")
    private LocalDateTime updDate;

    // 참고: Department와 User 엔티티는 별도로 정의되어 있어야 합니다.
}
