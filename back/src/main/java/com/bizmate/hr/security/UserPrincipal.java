package com.bizmate.hr.security;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 인증 전용 UserDetails 구현체
 * - API 응답 DTO와 철저히 분리
 * - pwHash 등 민감 정보는 @JsonIgnore로 절대 외부 노출 금지
 */
@Getter
@Setter

public class UserPrincipal implements UserDetails {
    private final Long userId;
    private final Long empId;
    private final String username;
    @JsonIgnore
    private final String pwHash;
    private final boolean active;
    private final boolean locked;
    private final Collection<? extends GrantedAuthority> authorities;

    private String empName;
    private String email;
    private String deptCode;
    private String deptName;

    public UserPrincipal(Long userId,
                         Long empId,
                         String username,
                         String pwHash,
                         boolean active,
                         boolean locked,
                         Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.empId = empId;
        this.username = username;
        this.pwHash = pwHash;
        this.active = active;
        this.locked = locked;
        this.authorities = authorities;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return pwHash; }
    @Override public String getUsername() { return username; }


    // 활성화/잠금에 맞춰 UserDetails 표준값 매핑
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    /** ✅ 특정 ROLE 보유 여부 확인 */
    public boolean hasRole(String roleName) {
        if (authorities == null) return false;
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equalsIgnoreCase(roleName) || auth.equalsIgnoreCase("ROLE_" + roleName));
    }

    /** ✅ 특정 권한(perm) 보유 여부 확인 */
    public boolean hasPermission(String permission) {
        if (authorities == null) return false;
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equalsIgnoreCase(permission));
    }

    /** ✅ 관리자 여부 (ROLE_CEO, sys:admin 등 통합 판단) */
    public boolean isAdmin() {
        return hasRole("ROLE_CEO") || hasRole("ROLE_ADMIN") || hasPermission("sys:admin");
    }
}
