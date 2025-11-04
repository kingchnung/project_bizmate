package com.bizmate.hr.dto.user;

import com.bizmate.hr.domain.Role;
import com.bizmate.hr.domain.Permission;
import com.bizmate.hr.domain.UserEntity; // 엔티티 이름 변경 적용


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "pwHash")
public class UserDTO extends User {
    private static final long serialVersionUID = 1L;

    // DTO의 final 필드들
    private final Long userId;
    private final Long empId;
    private final String username;
    private final String pwHash;
    private final String empName;
    private final boolean isAccountNonLocked;
    private final boolean isActive;
    private String email;
    private String phone;
    private String deptName;
    private String deptCode;
    private final List<String> roleNames;
    private final List<String> permissionNames;
    private final String positionName;
    private final Integer failedCount;
    private final LocalDateTime lastLogin;

    /**
     * Spring Security의 UserDetails를 상속받는 생성자
     * 주의: authorities 컬렉션을 외부(fromEntity)에서 미리 생성하여 전달받습니다.
     */
    public UserDTO(
            Long userId,
            Long empId,
            String username,
            String pwHash,
            String empName,
            boolean isAccountNonLocked,
            boolean isActive,
            String email,
            List<String> roleNames,
            List<String> permissionNames,
            String deptName,
            String positionName,
            Integer failedCount,
            LocalDateTime lastLogin,
            // ★★★ 이미 생성된 authorities를 인수로 받음 ★★★
            Collection<? extends GrantedAuthority> authorities) {

        // 1. super() 호출 (첫 줄)
        // username, password(pwHash), enabled(true), accountNonExpired(true),
        // credentialsNonExpired(true), accountNonLocked, authorities
        super(username, pwHash, true, true, true, isAccountNonLocked, authorities);

        // 2. DTO 필드 초기화 (super() 호출 후)
        this.userId = userId;
        this.empId = empId;
        this.username = username;
        this.pwHash = pwHash;
        this.empName = empName;
        this.email = email;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isActive = isActive;
        this.roleNames = roleNames;
        this.permissionNames = permissionNames;

        this.deptName = deptName;
        this.positionName = positionName;
        this.failedCount = failedCount;
        this.lastLogin = lastLogin;
    }

    /**
     * ✅ “비밀번호 불필요한” 경량 생성자 (컨트롤러 등에서 간단히 생성할 때)
     */
    public UserDTO(Long userId, String username, String empName, String email, Long empId) {
        super(
                username != null ? username : "anonymous",
                "dummy-password",
                true, true, true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        this.userId = userId;
        this.empId = empId;
        this.username = username;
        this.pwHash = "dummy-password";
        this.empName = empName;
        this.email = email;
        this.isAccountNonLocked = true;
        this.isActive = true;
        this.roleNames = List.of("USER");
        this.permissionNames = List.of();

        this.positionName = null;
        this.failedCount = 0;
        this.lastLogin = null;
    }

    // --- Private Static 헬퍼 메서드 영역 ---

    /**
     * Spring Security의 Authorities 목록을 생성합니다. (DTO에서 독립적인 기능)
     */
    private static Collection<? extends GrantedAuthority> createAuthorities(
            List<String> roleNames, List<String> permissionNames) {

        return Stream.concat(
                roleNames.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)),
                permissionNames.stream().map(SimpleGrantedAuthority::new)
        ).distinct().collect(Collectors.toList());

    }

    /**
     * JWT 클레임 생성을 위한 Map 반환 메서드
     */
    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("username", username);
        dataMap.put("empName", empName);

        dataMap.put("roles", roleNames);
        dataMap.put("perms", permissionNames);

        return dataMap;
    }

    /**
     * User Entity -> UserDTO 변환을 위한 헬퍼 메서드
     * (이 메서드에서 authorities를 생성하여 생성자에 전달)
     */
    public static UserDTO fromEntity(UserEntity user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        List<String> permissionNames = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermName)
                .distinct()
                .collect(Collectors.toList());

        boolean isLocked = "Y".equalsIgnoreCase(user.getIsLocked());
        boolean isActive = "Y".equalsIgnoreCase(user.getIsActive());

        // ★★★ Authorities를 미리 생성 ★★★
        Collection<? extends GrantedAuthority> authorities =
                createAuthorities(roleNames, permissionNames);

        return new UserDTO(
                user.getUserId(),
                user.getEmployee() != null ? user.getEmployee().getEmpId() : null,
                user.getUsername(),
                user.getPwHash(),
                user.getEmployee() != null ? user.getEmployee().getEmpName() : null,
                !isLocked,
                isActive,
                user.getEmail(),
                roleNames,
                permissionNames,
                user.getDeptName(),
                user.getPositionName(),
                user.getFailedCount(),
                user.getLastLogin(),
                authorities // ★★★ 생성된 authorities 전달 ★★★
        );
    }
}