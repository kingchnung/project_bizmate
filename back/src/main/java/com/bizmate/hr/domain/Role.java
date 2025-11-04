package com.bizmate.hr.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * [역할 Entity - Role]
 * 데이터베이스의 ROLES 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "ROLES")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    // 1. 역할ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    // 2. 역할명 (유일키, 필수)
    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String roleName;

    // 3. 설명
    @Column(name = "description", length = 255)
    private String description;

    @Builder.Default
    @Column(name = "is_used", length = 1,  nullable = false)
    private String isUsed = "Y";


    // N:M 관계 매핑 1: 사용자 - 역할 (USER_ROLES 테이블)
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserEntity> userEntities = new HashSet<>();

    // N:M 관계 매핑 2: 역할 - 권한 (ROLE_PERMISSIONS 테이블)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ROLE_PERMISSIONS",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "perm_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}