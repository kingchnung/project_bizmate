package com.bizmate.project.service;

import com.bizmate.project.dto.projectmember.ProjectMemberDTO;
import com.bizmate.project.dto.projectmember.ProjectMemberRequest;

import java.util.List;

public interface ProjectMemberService {

    // ✅ 구성원 등록
    ProjectMemberDTO addMember(ProjectMemberRequest request);

    // ✅ 단일 구성원 조회
    ProjectMemberDTO getMemberById(Long projectMemberId);

    // ✅ 특정 프로젝트의 모든 구성원 조회
    List<ProjectMemberDTO> getMembersByProject(Long projectId);

    // ✅ 특정 직원이 참여한 프로젝트 목록 조회
    List<ProjectMemberDTO> getMembersByEmployee(Long employeeId);

    // ✅ 구성원 정보 수정 (역할 변경 등)
    ProjectMemberDTO updateMember(Long projectMemberId, ProjectMemberRequest request);

    // ✅ 구성원 삭제
    void deleteMember(Long projectMemberId);
    List<ProjectMemberDTO> syncProjectMembers(Long projectId, List<ProjectMemberRequest> memberList);
}
