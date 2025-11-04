package com.bizmate.hr.service;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.Role;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.*;
import com.bizmate.hr.repository.*;
import com.bizmate.hr.service.MailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final MailService mailService;

    // ==========================================================
    // ✅ 1. 계정 생성
    // ==========================================================
    @Override
    @Transactional
    public UserDTO createUserAccount(Employee employee, String initialPassword) {
        if (userRepository.existsByUsername(employee.getEmpNo())) {
            throw new IllegalStateException("이미 존재하는 사용자 계정명입니다: " + employee.getEmpNo());
        }

        String encodedPassword = passwordEncoder.encode(initialPassword);

        UserEntity user = new UserEntity();
        user.setUsername(employee.getEmpNo());
        user.setPwHash(encodedPassword);
        user.setEmployee(employee);
        user.setIsLocked("N");
        user.setEmpName(employee.getEmpName());
        user.setEmail(employee.getEmail());
        user.setPhone(employee.getPhone());
        user.setDeptName(employee.getDepartment().getDeptName());
        user.setPositionName(employee.getPosition().getPositionName());
        user.setDeptCode(employee.getDepartment().getDeptCode());

        Role employeeRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRoleName("EMPLOYEE");
                    newRole.setDescription("기본 직원 역할");
                    return roleRepository.save(newRole);
                });

        user.setRoles(new HashSet<>(Collections.singletonList(employeeRole)));
        UserEntity savedUser = userRepository.save(user);
        log.info("✅ 직원 [{}] 계정 자동 생성 완료 (UserID: {})", employee.getEmpName(), savedUser.getUserId());
        return UserDTO.fromEntity(savedUser);
    }

    @Transactional
    public UserDTO createUserAccount(Employee employee) {
        return createUserAccount(employee, "0000");
    }

    // ==========================================================
    // ✅ 2. 사용자 조회/수정/삭제
    // ==========================================================
    @Override
    @Transactional(readOnly = true)
    public UserDTO getUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 ID " + userId + "를 찾을 수 없습니다."));
        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserUpdateRequestDTO updateDTO) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("수정하려는 사용자 ID " + userId + "를 찾을 수 없습니다."));

        user.setIsLocked(updateDTO.isAccountNonLocked() ? "N" : "Y");

        if (updateDTO.getNewPassword() != null && !updateDTO.getNewPassword().isEmpty()) {
            user.setPwHash(passwordEncoder.encode(updateDTO.getNewPassword()));
            log.info("비밀번호 재설정 완료 (userId: {})", userId);
        }

        List<Long> roleIds = updateDTO.getRoleIds();
        if (roleIds != null) {
            if (roleIds.isEmpty()) {
                user.getRoles().clear();
            } else {
                List<Role> newRoles = roleRepository.findAllById(roleIds);
                user.getRoles().clear();
                user.getRoles().addAll(newRoles);
            }
        }

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("삭제하려는 사용자 ID " + userId + "를 찾을 수 없습니다.");
        }
        userRepository.deleteById(userId);
    }

    // ==========================================================
    // ✅ 3. 계정 활성화/비활성화
    // ==========================================================
    @Override
    @Transactional
    public void setUserActiveStatus(Long userId, String activeStatus) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (!"Y".equalsIgnoreCase(activeStatus) && !"N".equalsIgnoreCase(activeStatus)) {
            throw new IllegalArgumentException("활성 상태 값은 'Y' 또는 'N' 이어야 합니다.");
        }

        user.setIsActive(activeStatus.toUpperCase());
        user.setUpdDate(LocalDateTime.now());
        userRepository.save(user);
    }

    // ==========================================================
    // ✅ 4. 로그인 사용자의 비밀번호 변경
    // ==========================================================
    @Override
    public void changePw(Long userId, UserPwChangeRequest dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object principal = auth.getPrincipal();
        Long currentUserId;

        if (principal instanceof com.bizmate.hr.security.UserPrincipal userPrincipal) {
            currentUserId = userPrincipal.getUserId();
        } else {
            throw new AccessDeniedException("인증 정보를 확인할 수 없습니다.");
        }

        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("본인 계정만 수정할 수 있습니다.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(dto.getCurrentPw(), user.getPwHash())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPwHash(passwordEncoder.encode(dto.getNewPw()));
        userRepository.save(user);
    }

    // ==========================================================
    // ✅ 5. 관리자용 계정 잠금 해제
    // ==========================================================
    @Override
    @Transactional
    public void adminUnlockAccount(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        user.setIsLocked("N");
        user.setFailedCount(0);
        user.setUpdDate(LocalDateTime.now());
        userRepository.save(user);
        log.info("🔓 관리자에 의해 잠금 해제됨 (userId: {})", userId);
    }

    // ==========================================================
    // ✅ 6. 관리자용 비밀번호 초기화 + 잠금 해제
    // ==========================================================
    @Transactional
    public String adminResetPassword(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다."));

        String tempPw = generateTempPassword();
        user.setPwHash(passwordEncoder.encode(tempPw));
        user.setIsLocked("N");
        user.setIsActive("Y");
        user.setFailedCount(0);
        user.setUpdDate(LocalDateTime.now());
        userRepository.saveAndFlush(user);

        mailService.sendPasswordResetMail(user.getEmail(), tempPw);
        log.info("🔁 관리자에 의해 임시비밀번호 발송 (userId: {})", userId);
        return tempPw;
    }

    private String generateTempPassword() {
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // ==========================================================
    // ✅ 7. 로그인 실패/성공 처리
    // ==========================================================
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int processLoginFailure(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + username));

        int prev = Optional.ofNullable(user.getFailedCount()).orElse(0);
        int newCount = prev + 1;
        user.setFailedCount(newCount);

        if (newCount >= 5) {
            user.setIsLocked("Y");
            log.warn("🔒 계정 [{}] 잠금 처리됨 (실패 {}회)", user.getUsername(), newCount);
        }

        userRepository.save(user);
        return newCount;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processLoginSuccess(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + username));

        user.setFailedCount(0);
        user.setIsLocked("N");
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        log.info("✅ 계정 [{}] 로그인 성공 처리", username);
    }

    // ==========================================================
    // ✅ 8. 전체 사용자 조회
    // ==========================================================
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
