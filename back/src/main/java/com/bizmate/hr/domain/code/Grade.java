package com.bizmate.hr.domain.code;

import jakarta.persistence.*;
import lombok.*;

/**
 * [직급 Entity - Grade]
 * 데이터베이스의 GRADES 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "GRADES")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    // 1. 직급코드 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_code")
    private Long gradeCode;

    // 2. 직급명
    @Column(name = "grade_name", unique = true, nullable = false, length = 50)
    private String gradeName;

    // 3. 직급순서 (위계)
    @Column(name = "grade_order", unique = true, nullable = false)
    private Integer gradeOrder;

    // 4. 사용여부
    @Column(name = "is_used",  length = 1)
    @Builder.Default
    private String isUsed = "Y";

    // 참고: Employee 엔티티와의 연관관계는 Employee 쪽에서 ManyToOne으로 처리합니다.
    // AssignmentsHistory 엔티티와의 연관관계도 마찬가지입니다.
}