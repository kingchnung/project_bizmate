package com.bizmate.hr.domain.code;

import jakarta.persistence.*;
import lombok.*;

/**
 * [직책 Entity - Position]
 * 데이터베이스의 POSITIONS 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "POSITIONS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Position {

    // 1. 직책코드 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_code")
    private Long positionCode;

    // 2. 직책명
    @Column(name = "position_name", unique = true, nullable = false, length = 100)
    private String positionName;

    // 3. 직무기술
    @Column(name = "description", length = 500)
    private String description;

    // 4. 사용여부
    @Column(name = "is_used",  length = 1)
    @Builder.Default
    private String isUsed = "Y";

    // 참고: Employee 엔티티와의 연관관계는 Employee 쪽에서 ManyToOne으로 처리합니다.
}