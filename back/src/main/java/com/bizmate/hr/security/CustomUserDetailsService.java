package com.bizmate.hr.security;

import com.bizmate.hr.domain.Permission;
import com.bizmate.hr.domain.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.bizmate.hr.security.UserPrincipal;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.UserRepository; // 가정된 Repository 경로
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * [CustomUserDetailsService]
 * - Spring Security의 인증 처리를 위해 사용자 정보를 DB에서 로드하는 핵심 서비스
 * - 로그인 ID (username)을 사용하여 User 엔티티를 조회하고 UserDetails(UserDTO)로 변환합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    // UserRepository를 주입받아 DB 접근
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("▶▶▶ loadUserByUsername: 사용자 정보를 로드합니다. [입력된 ID: {}] ", username);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2) 권한 수집 (ROLE_x + PERM_x를 GrantedAuthority로)
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
            // Permission이 Role에 매핑돼 있다면 퍼미션도 추가
            for (Permission p : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(p.getPermName()));
            }
        }

        // 3) 상태 값 매핑 (Entity의 필드명에 맞게)
        boolean active = "Y".equalsIgnoreCase(user.getIsActive());
        boolean locked = "Y".equalsIgnoreCase(user.getIsLocked());

        Long empId = null;
        if(user.getEmployee() != null){
            empId = user.getEmployee().getEmpId();
        }

        // 4) UserPrincipal 생성
        UserPrincipal principal = new UserPrincipal(
                user.getUserId(),
                empId,
                user.getUsername(),
                user.getPwHash(),      // 비밀번호 해시
                active,
                locked,
                authorities
        );
// 5️⃣ ✅ 추가 정보 세팅 (JWT 클레임용)
        if (user.getEmployee() != null) {
            principal.setEmpName(user.getEmployee().getEmpName()); // ✅ 사원 이름
        } else {
            principal.setEmpName("미등록");
        }

        principal.setEmail(user.getEmail()); // ✅ 이메일 추가
        principal.setDeptCode(user.getEmployee().getDepartment().getDeptCode());
        principal.setDeptName(user.getEmployee().getDepartment().getDeptName());

        log.info("✅ 로그인 사용자 로드 완료: userId={}, username={}, empName={}, email={}, deptCode={}, deptName={}",
                principal.getUserId(), principal.getUsername(), principal.getEmpName(), principal.getEmail(), principal.getDeptCode(), principal.getDeptName());

        return principal;
    }
}