package com.bizmate.project.dto.project;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import com.bizmate.project.dto.projectmember.ProjectMemberDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ✅ 프로젝트 상세 정보 DTO
 * - 엔티티(Project) <-> DTO 양방향 변환 포함
 * - 참여자, 예산, 업무 등은 별도 DTO로 분리 관리
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDetailResponseDTO {

    private Long projectId;
    private String projectName;
    private String projectGoal;
    private String projectOverview;
    private String expectedEffect;
    private Long totalBudget;
    @JsonProperty("pmId")
    private Long pmEmpId;
    private String pmName;
    private SimpleAuthorDTO author;
    private SimpleDepartmentDTO department;
    @Builder.Default
    private List<ProjectMemberDTO> participants = new ArrayList<>();

    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;


    // ------------------------------------------------------
    // ✅ [1] 엔티티 → DTO 변환 생성자
    // ------------------------------------------------------
    public ProjectDetailResponseDTO(Project project) {
        this.projectId = project.getProjectId();
        this.projectName = project.getProjectName();
        this.projectGoal = project.getProjectGoal();
        this.projectOverview = project.getProjectOverview();
        this.expectedEffect = project.getExpectedEffect();
        this.totalBudget = project.getTotalBudget();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.status = project.getStatus();


        // 작성자 및 부서 요약
        this.author = project.getAuthor() != null ? new SimpleAuthorDTO(project.getAuthor()) : null;
        this.department = project.getDepartment() != null ? new SimpleDepartmentDTO(project.getDepartment()) : null;

        if (project.getParticipants() != null) {
            this.participants = project.getParticipants().stream()
                    .map(ProjectMemberDTO::new)
                    .collect(Collectors.toList());

            // ✅ PM 한 명 추출
            project.getParticipants().stream()
                    .filter(m -> "PM".equalsIgnoreCase(m.getProjectRole()))
                    .findFirst()
                    .ifPresent(pm -> {
                        if (pm.getEmployee() != null) {
                            this.pmEmpId = pm.getEmployee().getEmpId();
                            this.pmName = pm.getEmployee().getEmpName();
                        }
                    });
        }
    }

    // ------------------------------------------------------
    // ✅ [2] DTO → 엔티티 변환
    //  - 연관 객체는 ID 기준으로 매핑해야 하므로
    //    Service 단에서 Repository로 실제 Entity 주입 필요
    // ------------------------------------------------------
    public Project toEntity(UserEntity author, Department department) {
        return Project.builder()
                .projectId(this.projectId)
                .projectName(this.projectName)
                .projectGoal(this.projectGoal)
                .projectOverview(this.projectOverview)
                .expectedEffect(this.expectedEffect)
                .totalBudget(this.totalBudget)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .status(this.status != null ? this.status : ProjectStatus.PLANNING)
                .author(author)
                .department(department)
                .build();
    }
}
