package com.bizmate.project.controller;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import com.bizmate.project.dto.project.ProjectDetailResponseDTO;
import com.bizmate.project.dto.project.ProjectRequestDTO;
import com.bizmate.project.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    /** ✅ 수동 테스트용 프로젝트 생성 API */
    @PostMapping("/create")
    public Project createProjectbyApproval(@RequestBody ProjectRequestDTO dto) {
        // 실제 전자결재 승인 시 ApprovalDocuments 주입됨
        ApprovalDocuments dummyDoc = new ApprovalDocuments();
        dummyDoc.setDocId("DOC-TEST-001");
        log.info("🧩 프로젝트 수동 생성 요청: {}", dto.getProjectName());
        return projectService.createProjectByApproval(dto, dummyDoc);
    }

    //상세조회
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_CEO')")
    public ResponseEntity<ProjectDetailResponseDTO> getProject(@PathVariable Long id) {
        try {
            ProjectDetailResponseDTO project = projectService.getProject(id);
            return ResponseEntity.ok(project);
        } catch (EntityNotFoundException e) {
            log.warn("⚠️ 프로젝트를 찾을 수 없습니다: id={}", id);
            return ResponseEntity.notFound().build();
        }
    }

    //상태값변경
    @PatchMapping("/{projectId}/status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER','ROLE_CEO')")
    public ResponseEntity<ProjectDetailResponseDTO> updateStatus(
            @PathVariable Long projectId,
            @RequestParam ProjectStatus status) {

        ProjectDetailResponseDTO updated = projectService.updateProjectStatus(projectId, status);
        return ResponseEntity.ok(updated);
    }

    //프로젝트생성
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CEO')")
    public ResponseEntity<?> createProject(@RequestBody ProjectRequestDTO dto) {
        try {
            ProjectDetailResponseDTO created = projectService.createProject(dto);
            return ResponseEntity.ok(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("프로젝트 생성 중 오류 발생: " + e.getMessage());
        }
    }



    // 🔹 일반 유저용 (진행 중 프로젝트만)
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE')")
    public ResponseEntity<List<ProjectDetailResponseDTO>> getActiveProjects() {
        List<ProjectDetailResponseDTO> activeList = projectService.getActiveProjects();
        return ResponseEntity.ok(activeList);
    }

    // 🔹 관리자용 전체 목록
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO','ROLE_MANAGER')")
    public ResponseEntity<List<ProjectDetailResponseDTO>> getAllProjectsForAdmin() {
        List<ProjectDetailResponseDTO> adminList = projectService.getAllProjectsForAdmin();
        return ResponseEntity.ok(adminList);
    }

    // 🔹 프로젝트 종료 (논리삭제)
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAuthority('data:write:all')")
    public ResponseEntity<Void> closeProject(@PathVariable Long id) {
        projectService.closeProject(id);
        log.info("🧾 프로젝트 종료 처리 완료 (id={})", id);
        return ResponseEntity.noContent().build();
    }

    /** ✅ 프로젝트 수정: 작성자 또는 관리자(ADMIN/CEO/MANAGER)만 */
    @PutMapping("/{projectId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER','ROLE_CEO','ROLE_EMPLOYEE')")
    public ResponseEntity<?> updateProject(
            @PathVariable Long projectId,
            @RequestBody ProjectRequestDTO dto,
            Authentication authentication
    ) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            boolean isPrivileged = principal.isAdmin() || principal.hasRole("ROLE_MANAGER");

            ProjectDetailResponseDTO updated =
                    projectService.updateProject(projectId, dto, principal.getUserId(), isPrivileged);

            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("수정 중 오류: " + e);
        }
    }




}
