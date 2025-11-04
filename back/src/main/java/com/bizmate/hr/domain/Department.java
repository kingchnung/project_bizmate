package com.bizmate.hr.domain;

import com.bizmate.hr.domain.Employee;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * [부서 Entity - Department]
 * 데이터베이스의 DEPARTMENTS 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "DEPARTMENTS")
@Getter
@Setter // 코드 테이블도 수정 관리를 위해 Setter 허용
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    // 1. 부서ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long deptId;

    // 2. 부서명
    @Column(name = "dept_name", unique = true, nullable = false, length = 50)
    private String deptName;

    // 3. 부서코드
    @Column(name = "dept_code", unique = true, nullable = false, length = 10)
    private String deptCode;

    // 4. 상위부서ID (Self-Join for Hierarchy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_dept_id")
    @JsonBackReference
    private Department parentDept;


    // 계층 구조 관리를 위한 자식 부서 목록 (편의상)
    @OneToMany(mappedBy = "parentDept", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<Department> childDepts = new ArrayList<>();

    // 5. 사용여부
    @Column(name = "is_used",  length = 1)
    @Builder.Default
    private String isUsed = "Y";

    // ★ 7. 생성일 (추가)
    @Column(name = "cre_date", nullable = false)
    @Builder.Default
    private LocalDateTime creDate = LocalDateTime.now();

    // ★ 8. 수정일 (추가)
    @Column(name = "upd_date")
    private LocalDateTime updDate;

    // 연관 관계 (직원) - 부서가 삭제될 때 직원에 대한 영향 고려 필요 (비즈니스 로직으로 처리)
    @OneToMany(mappedBy = "department")
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    //부서장 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id") // 부서장 Employee의 ID를 FK로 가짐
    private Employee manager; // 부서장 Employee 객체
}