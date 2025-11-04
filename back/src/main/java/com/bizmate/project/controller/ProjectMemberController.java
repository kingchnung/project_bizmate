package com.bizmate.project.controller;

import com.bizmate.project.dto.projectmember.ProjectMemberDTO;
import com.bizmate.project.dto.projectmember.ProjectMemberRequest;
import com.bizmate.project.dto.projectmember.ProjectMemberResponseDTO;
import com.bizmate.project.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    // ✅ 1. 구성원 등록 (POST)
    @PostMapping
    public ResponseEntity<ProjectMemberDTO> addMember(@RequestBody ProjectMemberRequest request) {
        ProjectMemberDTO created = projectMemberService.addMember(request);
        return ResponseEntity.ok(created);
    }

    // ✅ 2. 단일 구성원 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<ProjectMemberDTO> getMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(projectMemberService.getMemberById(memberId));
    }

    // ✅ 3. 프로젝트별 구성원 조회
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ProjectMemberDTO>> getMembersByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectMemberService.getMembersByProject(projectId));
    }

    // ✅ 4. 직원별 참여 프로젝트 조회
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ProjectMemberDTO>> getMembersByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(projectMemberService.getMembersByEmployee(employeeId));
    }

    // ✅ 5. 구성원 정보 수정 (역할 변경)
    @PatchMapping("/{memberId}")
    public ResponseEntity<ProjectMemberDTO> updateMember(
            @PathVariable Long memberId,
            @RequestBody ProjectMemberRequest request
    ) {
        return ResponseEntity.ok(projectMemberService.updateMember(memberId, request));
    }

    // ✅ 6. 구성원 삭제
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        projectMemberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 7. 프로젝트 내 구성원 일괄 변경
     * - 프론트에서 배열 형태로 구성원 목록 전달 시
     * - 기존 멤버와 비교하여 추가/삭제/수정 자동 처리
     */
    @PutMapping("/project/{projectId}/sync")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_CEO')")
    public ResponseEntity<List<ProjectMemberDTO>> syncProjectMembers(
            @PathVariable Long projectId,
            @RequestBody List<ProjectMemberRequest> memberList
    ) {
        List<ProjectMemberDTO> updated = projectMemberService.syncProjectMembers(projectId, memberList);
        return ResponseEntity.ok(updated);
    }


}
