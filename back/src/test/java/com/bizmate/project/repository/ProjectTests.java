package com.bizmate.project.repository;


import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.repository.UserRepository;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
public class ProjectTests {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    private final List<String> roles = List.of("PM", "Backend", "Frontend", "Designer", "QA");

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Test
    void insertDummyProjects() {

        // ✅ 1️⃣ 프로젝트 담당 부서 목록 (개발1,2,3팀)
        List<String> deptCodes = List.of("31", "32", "33");

        // ✅ 영업팀 팀장 Employee → UserEntity 조회
        var employee = employeeRepository.findByEmpNo("5021001")
                .orElseThrow(() -> new RuntimeException("직원(5021001)을 찾을 수 없습니다."));
        var author = userRepository.findByUsername(employee.getEmpNo()) // 혹은 email / loginId 기준
                .orElseThrow(() -> new RuntimeException("해당 직원의 UserEntity를 찾을 수 없습니다."));



        for (int i = 1; i <= 5; i++) {

            // ✅ 랜덤 부서 선택
            String deptCode = deptCodes.get(random.nextInt(deptCodes.size()));
            Department dept = departmentRepository.findByDeptCode(deptCode)
                    .orElseThrow(() -> new RuntimeException("부서코드 " + deptCode + "를 찾을 수 없습니다."));

            // ✅ 랜덤 예산 (6천만~1.5억)
            long budget = 60_000_000L + (long) (random.nextDouble() * 90_000_000L);

            // ✅ 시작일: 오늘 기준 -3개월~+1개월
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusDays(random.nextInt(90)) // 최대 3개월 전
                    .plusDays(random.nextInt(30)); // 최대 1개월 후

            // ✅ 종료일: 시작일 + 100일
            LocalDate endDate = startDate.plusDays(100);

            // ✅ 문서코드: 부서코드-00X
            String docCode = String.format("%s-%03d", deptCode, i);

            // ✅ 프로젝트 이름, 목표, 개요 랜덤하게 생성
            String projectName = switch (random.nextInt(5)) {
                case 0 -> "사내 협업 플랫폼 개선";
                case 1 -> "고객 관리 시스템 리뉴얼";
                case 2 -> "업무 자동화 툴 개발";
                case 3 -> "매출 분석 대시보드 구축";
                default -> "인사관리 기능 통합 프로젝트";
            };

            String goal = switch (random.nextInt(4)) {
                case 0 -> "업무 효율성을 높이고 협업 환경 개선";
                case 1 -> "매출 데이터를 실시간으로 시각화";
                case 2 -> "반복 업무를 자동화하여 인력 비용 절감";
                default -> "인사·급여 데이터를 통합 관리";
            };

            String overview = switch (random.nextInt(3)) {
                case 0 -> "Spring Boot + React 기반으로 개발하며, 내부 시스템과의 연동을 포함한다.";
                case 1 -> "DB 설계부터 API 개발, 화면 디자인까지 전반적인 리뉴얼 작업을 포함한다.";
                default -> "사용자 피드백을 반영하여 UI/UX를 개선하고, 유지보수성을 강화한다.";
            };

            // ✅ Project 엔티티 생성
            Project project = Project.builder()
                    .projectName(projectName)
                    .projectGoal(goal)
                    .projectOverview(overview)
                    .expectedEffect("업무 효율 및 데이터 품질 향상 기대")
                    .totalBudget(budget)
                    .startDate(startDate)
                    .endDate(endDate)
                    .status(ProjectStatus.PLANNING)
                    .author(author) // Employee → UserEntity 연결 가정
                    .department(dept)
                    .approvalDocument(null) // 결재문서 미연결
                    .build();

            projectRepository.save(project);

            System.out.printf(
                    "✅ [%d] %s (%s) 등록 완료 | 부서:%s | 예산:%,d원 | 문서번호:%s | 기간:%s~%s%n",
                    i, projectName, project.getStatus(), dept.getDeptName(), budget, docCode, startDate, endDate
            );
        }
    }

}