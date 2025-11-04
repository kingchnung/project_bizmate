package com.bizmate.hr.repository;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * ✅ 1️⃣ 기본 로그인용 조회 (권한·직원정보까지 함께 로드)
     *  - 영속성 유지 + Lazy 로딩 방지 + 로그인 실패 횟수 정상 반영 가능
     *  - AuthService.login()에서 반드시 이 메서드를 사용하세요.
     */
    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions",
            "employee",
            "employee.department",
            "employee.position",
            "employee.grade"
    })
    @Query("SELECT u FROM UserEntity u WHERE u.username = :username")
    Optional<UserEntity> findActiveUserWithDetails(@Param("username") String username);

    /**
     * ✅ 2️⃣ 이메일 또는 사번 기반 조회 (비밀번호 초기화 등에서 활용)
     *  - 계정 복구 / 임시비밀번호 발급 시 용도
     */
    @EntityGraph(attributePaths = {
            "employee",
            "roles",
            "roles.permissions"
    })
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    /**
     * ✅ 3️⃣ 직원 엔티티로부터 사용자 조회
     *  - DataInitializer / EmployeeService 동기화 시 사용
     */
    Optional<UserEntity> findByEmployee(Employee employee);

    /**
     * ✅ 4️⃣ 단순 username 조회 (가급적 비권한 로직에서만 사용)
     *  - 권한/직원정보가 필요하지 않은 단순 체크용.
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * ✅ 5️⃣ username 존재 여부 (중복체크)
     */
    boolean existsByUsername(String username);

    /**
     * ✅ 6️⃣ 이메일 중복체크
     */
    boolean existsByEmailIgnoreCase(String email);

    Optional<UserEntity> findByEmpName(String empName);
}
