package com.bizmate.project.service;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import com.bizmate.project.dto.project.ProjectDetailResponseDTO;
import com.bizmate.project.dto.project.ProjectRequestDTO;

import java.util.List;

public interface ProjectService {


    Project createProjectByApproval(ProjectRequestDTO dto, ApprovalDocuments document);
    ProjectDetailResponseDTO createProject(ProjectRequestDTO dto);
    ProjectDetailResponseDTO getProject(Long id);
    List<ProjectDetailResponseDTO> getActiveProjects();
    List<ProjectDetailResponseDTO> getAllProjectsForAdmin();
    void closeProject(Long projectId);
    ProjectDetailResponseDTO updateProjectStatus(Long projectId, ProjectStatus status);

    ProjectDetailResponseDTO updateProject(Long projectId, ProjectRequestDTO dto, Long userId, boolean isPrivileged);
}
