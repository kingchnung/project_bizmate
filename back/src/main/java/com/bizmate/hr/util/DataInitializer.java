package com.bizmate.hr.util;

import com.bizmate.hr.domain.*;
import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private static final String COMPANY_CODE = "50";
    private static final String INITIAL_PASSWORD = "1234";

    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final GradeRepository gradeRepository;
    private final DepartmentRepository departmentRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final Random random = new Random();
    private final Map<String, Integer> deptSerialCounter = new HashMap<>();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("▶▶▶ DataInitializer 실행 시작");

        initBaseData();

//        if (employeeRepository.count() > 0) {
//            clearEmployeeData();  // 🔹 이 위치에 추가
//        }

        // 직원이 없을 경우 기본 세트 생성
        if (employeeRepository.count() == 0) {
            initDefaultEmployees();
        }

        // Employee ↔ User 정합성 점검 및 보정
        syncEmployeesAndUsers();

        log.info("✅ DataInitializer 실행 완료");
    }

    // =========================================================
    // 1️⃣ 기본 코드/부서 초기화
    // =========================================================
    private void initBaseData() {
        log.info("▶ 기본 코드 데이터 확인 중...");

        // ---- 권한 생성 ----
        Permission permSysAdmin = createPermission("sys:admin", "시스템 설정 및 관리 권한");
        Permission permSysManage = createPermission("sys:manage", "시스템 자원 관리 권한");
        Permission permDataReadAll = createPermission("data:read:all", "모든 부서 및 직원 데이터 조회");
        Permission permDataWriteAll = createPermission("data:write:all", "모든 직원 데이터 수정/삭제");
        Permission permDataReadSelf = createPermission("data:read:self", "본인 정보만 조회/수정");

        // ---- 역할 생성 ----
        Role roleCEO = createRole("CEO", "최고 경영자 역할",
                Set.of(permSysAdmin, permDataReadAll, permDataWriteAll, permDataReadSelf));
        Role roleMANAGER = createRole("MANAGER", "팀 관리자 역할",
                Set.of(permDataReadAll, permDataWriteAll, permDataReadSelf));
        Role roleEMPLOYEE = createRole("EMPLOYEE", "일반 직원 역할",
                Set.of(permDataReadSelf));

        // ---- admin 전용 역할 생성 ----
        Role roleADMIN = createRole("ADMIN", "시스템 관리자",
                Set.of(permSysAdmin, permSysManage, permDataReadAll, permDataWriteAll));

        // ---- 코드 테이블 기본값 생성 ----
        createPosition("CEO", "최고 의사 결정권자");
        createPosition("팀장", "팀 운영 및 관리 책임");
        createPosition("사원", "일반 실무자");

        createGrade("임원", 100);
        createGrade("부장/차장", 70);
        createGrade("사원/대리", 30);

        Department deptManagement = createDepartment("10", "경영관리부", null);
        Department deptSales = createDepartment("20", "영업부", null);
        Department deptDevelopment = createDepartment("30", "개발부", null);

        createDepartment("11", "경영지원팀", deptManagement);
        createDepartment("12", "회계팀", deptManagement);
        createDepartment("21", "영업팀", deptSales);
        createDepartment("31", "개발1팀", deptDevelopment);
        createDepartment("32", "개발2팀", deptDevelopment);
        createDepartment("33", "개발3팀", deptDevelopment);

        // ---- 관리자 계정 생성 (직원 없음) ----
        createAdminAccount(roleADMIN);

        log.info("✅ 기본 코드/부서 데이터 점검 완료");
    }

//    private void clearEmployeeData() {
//        log.warn("⚠ 직원 및 관련 데이터 초기화 시작");
//
//        // 외래키 참조 순서대로 삭제 (존재하는 Repository에 맞춰 조정)
//        userRepository.deleteAll();                // UserEntity → Employee FK
//        departmentRepository.deleteAll();
//        employeeRepository.deleteAll();            // Employee 최종 삭제
//
//
//        log.info("✅ 직원 관련 데이터 초기화 완료");
//    }


    // =========================================================
    // 2️⃣ 직원/유저 정합성 점검 및 보정
    // =========================================================
    private void syncEmployeesAndUsers() {
        log.info("▶ Employee ↔ User 정합성 점검 시작");

        List<Employee> allEmployees = employeeRepository.findAll();
        if (allEmployees.isEmpty()) {
            log.warn("직원 데이터가 없어 정합성 점검을 건너뜁니다.");
            return;
        }

        int created = 0, updated = 0;

        Role defaultRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseThrow(() -> new IllegalStateException("기본 역할 'EMPLOYEE'가 없습니다."));

        for (Employee emp : allEmployees) {
            Optional<UserEntity> optUser = userRepository.findByEmployee(emp);

            if (optUser.isEmpty()) {
                createUserAccount(emp, defaultRole);
                created++;
            } else {
                UserEntity user = optUser.get();
                boolean changed = false;

                if (!Objects.equals(user.getEmpName(), emp.getEmpName())) {
                    user.setEmpName(emp.getEmpName());
                    changed = true;
                }
                if (!Objects.equals(user.getEmail(), emp.getEmail())) {
                    user.setEmail(emp.getEmail());
                    changed = true;
                }
                if (!Objects.equals(user.getPhone(), emp.getPhone())) {
                    user.setPhone(emp.getPhone());
                    changed = true;
                }
                if (emp.getDepartment() != null && !Objects.equals(user.getDeptName(), emp.getDepartment().getDeptName())) {
                    user.setDeptName(emp.getDepartment().getDeptName());
                    changed = true;
                }
                if (emp.getPosition() != null && !Objects.equals(user.getPositionName(), emp.getPosition().getPositionName())) {
                    user.setPositionName(emp.getPosition().getPositionName());
                    changed = true;
                }
                if (emp.getDepartment() != null && !Objects.equals(user.getDeptCode(), emp.getDepartment().getDeptCode())) {
                    user.setDeptCode(emp.getDepartment().getDeptCode());
                    changed = true;
                }

                if (changed) {
                    user.setUpdDate(LocalDateTime.now());
                    userRepository.saveAndFlush(user);
                    updated++;
                }
            }
        }

        log.info("✅ 정합성 완료: 신규 User {}건 생성, 기존 User {}건 갱신", created, updated);
    }

    // =========================================================
    // 3️⃣ 헬퍼 메서드들
    // =========================================================
    private Permission createPermission(String name, String desc) {
        return permissionRepository.findByPermName(name)
                .orElseGet(() -> {
                    log.info(" - Permission '{}' 생성", name);
                    return permissionRepository.save(Permission.builder()
                            .permName(name)
                            .description(desc)

                            .build());
                });
    }

    private Role createRole(String name, String desc, Set<Permission> perms) {
        return roleRepository.findByRoleName(name)
                .orElseGet(() -> {
                    log.info(" - Role '{}' 생성", name);
                    return roleRepository.save(Role.builder()
                            .roleName(name)
                            .description(desc)
                            .permissions(perms)

                            .build());
                });
    }

    private Position createPosition(String name, String desc) {
        return positionRepository.findByPositionName(name)
                .orElseGet(() -> positionRepository.save(Position.builder()
                        .positionName(name)
                        .description(desc)
                        .isUsed("Y")
                        .build()));
    }

    private Grade createGrade(String name, Integer order) {
        return gradeRepository.findByGradeName(name)
                .orElseGet(() -> gradeRepository.save(Grade.builder()
                        .gradeName(name)
                        .gradeOrder(order)
                        .isUsed("Y")
                        .build()));
    }

    private Department createDepartment(String code, String name, Department parent) {
        return departmentRepository.findByDeptCode(code)
                .orElseGet(() -> departmentRepository.save(Department.builder()
                        .deptCode(code)
                        .deptName(name)
                        .parentDept(parent)
                        .creDate(LocalDateTime.now())
                        .build()));
    }

    // =========================================================
    // 4️⃣ 직원/유저 생성
    // =========================================================
    private String generateEmpNo(String deptCode) {
        int nextSerial = deptSerialCounter.getOrDefault(deptCode, 0) + 1;
        deptSerialCounter.put(deptCode, nextSerial);
        return COMPANY_CODE + deptCode + String.format("%03d", nextSerial);
    }

    private double calculateCareerYears(LocalDate startDate) {
        if (startDate == null) return 0.0;

        long months = ChronoUnit.MONTHS.between(startDate, LocalDate.now());
        double years = months / 12.0;

        // 소수점 1자리 반올림 (예: 7.4년)
        return Math.round(years * 10) / 10.0;
    }

    private String generateRandomPhone() {
        return String.format("010-%04d-%04d",
                random.nextInt(9000) + 1000,
                random.nextInt(9000) + 1000);
    }

    private Employee createEmployee(
            String empNo,
            String name,
            Department dept,
            Position pos,
            Grade grade,
            String status
    ) {
        String email = empNo + "@bizmate.com";
        String phone = generateRandomPhone();
        String address = "서울특별시 강남구 테헤란로 100";
        String gender = random.nextBoolean() ? "M" : "F";
        LocalDate birthDate = LocalDate.of(random.nextInt(14) + 1985, random.nextInt(12) + 1, random.nextInt(28) + 1);

        double careerYears = Math.round((random.nextDouble() * 9 + 2) * 10) / 10.0; // 2.0 ~ 11.0

        int totalMonths = (int) Math.round(careerYears * 12);

        LocalDate startDate = LocalDate.now().minusMonths(totalMonths);

        int ageAtHire = startDate.getYear() - birthDate.getYear();
        if (ageAtHire < 19) {
            startDate = birthDate.plusYears(19);
        }




        Employee emp = Employee.builder()
                .empNo(empNo)
                .empName(name)
                .department(dept)
                .position(pos)
                .grade(grade)
                .status(status)
                .email(email)
                .phone(phone)
                .address(address)
                .birthDate(birthDate)
                .gender(gender)
                .startDate(startDate)
                .careerYears(careerYears)
                .creDate(LocalDateTime.now())
                .build();

        return employeeRepository.save(emp);
    }

    private UserEntity createUserAccount(Employee employee, Role role) {
        Set<Role> roles = new HashSet<>(Collections.singletonList(role));

        UserEntity user = UserEntity.builder()
                .username(employee.getEmpNo().equals("5010001") ? "ceo" : employee.getEmpNo())
                .pwHash(passwordEncoder.encode(INITIAL_PASSWORD))
                .employee(employee)
                .isActive("Y")
                .isLocked("N")
                .failedCount(0)
                .creDate(LocalDateTime.now())
                .roles(roles)
                .empName(employee.getEmpName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .deptName(employee.getDepartment() != null ? employee.getDepartment().getDeptName() : null)
                .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
                .deptCode(employee.getDepartment() != null ? employee.getDepartment().getDeptCode() : null)
                .build();

        return userRepository.save(user);
    }

    private void createAdminAccount(Role adminRole) {
        String username = "admin";

        if (userRepository.existsByUsername(username)) {
            log.info("🔹 관리자 계정 '{}' 이미 존재", username);
            return;
        }

        UserEntity adminUser = UserEntity.builder()
                .employee(null) // 직원 연결 없음
                .username(username)
                .pwHash(passwordEncoder.encode("1234"))
                .isActive("Y")
                .isLocked("N")
                .failedCount(0)
                .creDate(LocalDateTime.now())
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(adminUser);
        log.info("✅ 관리자 계정 '{}' 생성 완료", username);
    }

    // =========================================================
    // 5️⃣ 초기 직원 생성 (환경별 동일 보장)
    // =========================================================
    private void initDefaultEmployees() {
        log.info("▶ 기본 직원(30명) 자동 생성 시작");

        // ===== 공통 레퍼런스 엔티티 조회 =====
        Department deptMgmt = departmentRepository.findByDeptCode("10").orElseThrow();
        Department deptSupport = departmentRepository.findByDeptCode("11").orElseThrow();
        Department deptAccounting = departmentRepository.findByDeptCode("12").orElseThrow();
        Department deptSales = departmentRepository.findByDeptCode("21").orElseThrow();
        Department deptDev1 = departmentRepository.findByDeptCode("31").orElseThrow();
        Department deptDev2 = departmentRepository.findByDeptCode("32").orElseThrow();
        Department deptDev3 = departmentRepository.findByDeptCode("33").orElseThrow();

        Position posCEO = positionRepository.findByPositionName("CEO").orElseThrow();
        Position posManager = positionRepository.findByPositionName("팀장").orElseThrow();
        Position posEmployee = positionRepository.findByPositionName("사원").orElseThrow();

        Grade gradeExec = gradeRepository.findByGradeName("임원").orElseThrow();
        Grade gradeManager = gradeRepository.findByGradeName("부장/차장").orElseThrow();
        Grade gradeStaff = gradeRepository.findByGradeName("사원/대리").orElseThrow();

        Role roleCEO = roleRepository.findByRoleName("CEO").orElseThrow();
        Role roleMANAGER = roleRepository.findByRoleName("MANAGER").orElseThrow();
        Role roleEMPLOYEE = roleRepository.findByRoleName("EMPLOYEE").orElseThrow();

        // ===== CEO (1명) =====
        Employee ceo = createEmployee(generateEmpNo("10"), "홍길동", deptMgmt, posCEO, gradeExec, "ACTIVE");
        createUserAccount(ceo, roleCEO);  // 🔹 CEO 권한 부여

        // 팀장 6명
        createUserAccount(createEmployee(generateEmpNo("11"), "김지원", deptSupport, posManager, gradeManager, "ACTIVE"), roleMANAGER);
        createUserAccount(createEmployee(generateEmpNo("12"), "이회계", deptAccounting, posManager, gradeManager, "ACTIVE"), roleMANAGER);
        createUserAccount(createEmployee(generateEmpNo("21"), "박영업", deptSales, posManager, gradeManager, "ACTIVE"), roleMANAGER);
        createUserAccount(createEmployee(generateEmpNo("31"), "최개발", deptDev1, posManager, gradeManager, "ACTIVE"), roleMANAGER);
        createUserAccount(createEmployee(generateEmpNo("32"), "정개발", deptDev2, posManager, gradeManager, "ACTIVE"), roleMANAGER);
        createUserAccount(createEmployee(generateEmpNo("33"), "오개발", deptDev3, posManager, gradeManager, "ACTIVE"), roleMANAGER);

        // 일반 직원 (모두 EMPLOYEE)
        for (int i = 1; i <= 5; i++)
            createUserAccount(createEmployee(generateEmpNo("11"), "경영사원" + i, deptSupport, posEmployee, gradeStaff, "ACTIVE"), roleEMPLOYEE);

        for (int i = 1; i <= 3; i++)
            createUserAccount(createEmployee(generateEmpNo("12"), "회계사원" + i, deptAccounting, posEmployee, gradeStaff, "ACTIVE"), roleEMPLOYEE);

        for (int i = 1; i <= 4; i++)
            createUserAccount(createEmployee(generateEmpNo("21"), "영업사원" + i, deptSales, posEmployee, gradeStaff, "ACTIVE"), roleEMPLOYEE);

        for (int i = 1; i <= 4; i++)
            createUserAccount(createEmployee(generateEmpNo("31"), "개발1팀사원" + i, deptDev1, posEmployee, gradeStaff, "ACTIVE"), roleEMPLOYEE);

        for (int i = 1; i <= 3; i++)
            createUserAccount(createEmployee(generateEmpNo("32"), "개발2팀사원" + i, deptDev2, posEmployee, gradeStaff, "ACTIVE"), roleEMPLOYEE);

        for (int i = 1; i <= 4; i++)
            createUserAccount(createEmployee(generateEmpNo("33"), "개발3팀사원" + i, deptDev3, posEmployee, gradeStaff, "ACTIVE"), roleEMPLOYEE);

        log.info("✅ 기본 직원(30명) + 권한 매핑 완료");


    }

//    // =========================================================
//// 6️⃣ 게시판 & 전자결재 더미 데이터
//// =========================================================
//
//    private BoardRepository boardRepository;
//    private ApprovalDocumentsRepository approvalDocumentsRepository;
//
//    private void initBoardDummy(List<UserEntity> users) {
//        if (boardRepository.count() > 0) {
//            log.info("📘 게시판 데이터 이미 존재 — skip");
//            return;
//        }
//
//        String[] titles = {
//                "업무 공지", "개발 회의", "사내 공모전", "보안 점검 안내", "팀 프로젝트 공유",
//                "주간 업무 보고", "사내 이벤트", "제안사항", "시스템 점검 안내", "신입 환영 게시글"
//        };
//
//        String[] contents = {
//                "이번 주 중으로 처리 예정입니다.",
//                "회의록을 공유드립니다.",
//                "검토 후 의견 부탁드립니다.",
//                "협조 바랍니다.",
//                "좋은 하루 보내세요!"
//        };
//
//        BoardType[] types = BoardType.values();
//        List<Board> boards = new ArrayList<>();
//
//        for (int i = 1; i <= 32; i++) {
//            UserEntity author = users.get(random.nextInt(users.size()));
//            BoardType type = types[random.nextInt(types.length)];
//
//            Board board = new Board();
//            board.setBoardType(type);
//            board.setTitle(titles[random.nextInt(titles.length)] + " #" + i);
//            board.setContent(contents[random.nextInt(contents.length)]);
//            board.setAuthorId(author.getUsername());
//            board.setAuthorName(type == BoardType.SUGGESTION ? "익명" : author.getEmpName());
//            board.setDeleted(false);
//
//            // 감사정보 (UserDTO 직접 생성)
//            UserDTO dto = new UserDTO(
//                    author.getUserId(),
//                    author.getUsername(),
//                    author.getEmpName(),
//                    author.getEmail()
//            );
//            board.markCreated(dto);
//
//            boards.add(board);
//        }
//
//        boardRepository.saveAll(boards);
//        log.info("✅ 게시판 더미데이터 32건 생성 완료");
//    }
//
//    // =========================================================
//    // 2️⃣ 전자결재 더미 데이터 생성
//    // =========================================================
//    private void initApprovalDummy(List<UserEntity> users, List<Department> depts) {
//        if (approvalDocumentsRepository.count() > 0) {
//            log.info("📄 전자결재 데이터 이미 존재 — skip");
//            return;
//        }
//
//        String[] docTitles = {
//                "품의서", "프로젝트 기획안", "견적서/제안서 발송 품의서", "지출결의서", "구매 품의서"
//                , "휴가 신청서", "퇴직서", "인사발령"
//        };
//
//        List<ApprovalDocuments> docs = new ArrayList<>();
//
//        for (int i = 1; i <= 32; i++) {
//            UserEntity author = users.get(random.nextInt(users.size()));
//            Department dept = depts.get(random.nextInt(depts.size()));
//
//            ApprovalDocuments doc = new ApprovalDocuments();
//            doc.setDocId(depts.contains(users.));
//            doc.setDocId("DOC-" + String.format("%03d", i));
//            doc.setDocType(DocumentType.APPROVAL);
//            doc.setTitle(docTitles[random.nextInt(docTitles.length)] + " #" + i);
//            doc.setDepartment(dept);
//            doc.setAuthorUser(author);
//            doc.setAuthorEmployee(author.getEmployee());
//            doc.setAuthorRole(author.getRoles().stream().findFirst().orElse(null));
//
//            // 상태는 초안/진행/완료 랜덤
//            DocumentStatus[] statuses = DocumentStatus.values();
//            doc.setStatus(statuses[random.nextInt(statuses.length)]);
//
//            // 결재선 3단계 생성
//            List<ApproverStep> approvers = new ArrayList<>();
//            for (int step = 1; step <= 3; step++) {
//                UserEntity approver = users.get(random.nextInt(users.size()));
//                approvers.add(new ApproverStep(
//                        step,
//                        approver.getEmpName(),
//                        approver.getUsername(),
//                        step == 3 ? Decision.APPROVED : "검토",
//                        step <= 1 ? "완료" : "대기중"
//                ));
//            }
//            doc.setApprovalLine(approvers);
//
//            // 내용 데이터 (JSON)
//            Map<String, Object> content = new LinkedHashMap<>();
//            content.put("항목", "테스트 데이터");
//            content.put("금액", random.nextInt(1000000) + "원");
//            content.put("비고", "자동생성 더미");
//            doc.setDocContent(content);
//
//            // 감사정보
//            doc.markCreated(UserDTO.from(author));
//
//            docs.add(doc);
//        }
//
//        approvalRepository.saveAll(docs);
//        log.info("✅ 전자결재 더미데이터 32건 생성 완료");
//    }
}
