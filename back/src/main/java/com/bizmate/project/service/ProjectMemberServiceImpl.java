package com.bizmate.project.service;


import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.dto.projectmember.ProjectMemberDTO;
import com.bizmate.project.dto.projectmember.ProjectMemberRequest;
import com.bizmate.project.repository.ProjectMemberRepository;
import com.bizmate.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    // ✅ 구성원 등록
    @Override
    public ProjectMemberDTO addMember(ProjectMemberRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다."));
        Employee employee = employeeRepository.findById(request.getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("해당 직원을 찾을 수 없습니다."));

        // 중복 등록 방지
        if (projectMemberRepository.existsByProjectAndEmployee(project, employee)) {
            throw new IllegalStateException("이미 해당 프로젝트에 등록된 직원입니다.");
        }

        ProjectMember newMember = ProjectMember.builder()
                .project(project)
                .employee(employee)
                .projectRole(request.getProjectRole())
                .build();

        return ProjectMemberDTO.fromEntity(projectMemberRepository.save(newMember));
    }

    // ✅ 단일 구성원 조회
    @Override
    @Transactional(readOnly = true)
    public ProjectMemberDTO getMemberById(Long projectMemberId) {
        ProjectMember member = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 구성원을 찾을 수 없습니다."));
        return ProjectMemberDTO.fromEntity(member);
    }

    // ✅ 특정 프로젝트의 모든 구성원 조회
    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberDTO> getMembersByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        return projectMemberRepository.findByProject(project)
                .stream()
                .map(ProjectMemberDTO::fromEntity)
                .toList();
    }

    // ✅ 특정 직원이 참여 중인 모든 프로젝트 조회
    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberDTO> getMembersByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        return projectMemberRepository.findByEmployee(employee)
                .stream()
                .map(ProjectMemberDTO::fromEntity)
                .toList();
    }

    // ✅ 구성원 정보 수정 (역할 변경)
    @Override
    public ProjectMemberDTO updateMember(Long projectMemberId, ProjectMemberRequest request) {
        ProjectMember member = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new IllegalArgumentException("구성원을 찾을 수 없습니다."));

        // 역할 변경만 허용
        member.setProjectRole(request.getProjectRole());
        return ProjectMemberDTO.fromEntity(projectMemberRepository.save(member));
    }

    // ✅ 구성원 삭제
    @Override
    public void deleteMember(Long projectMemberId) {
        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new IllegalArgumentException("삭제할 구성원을 찾을 수 없습니다.");
        }
        projectMemberRepository.deleteById(projectMemberId);
    }

    @Override
    public List<ProjectMemberDTO> syncProjectMembers(Long projectId, List<ProjectMemberRequest> memberList) {
        return List.of();
    }

}

