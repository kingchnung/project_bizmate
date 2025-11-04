package com.bizmate.project.dto.project;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProjectResponseDTO {

    private Long projectId;
    private String projectNo;
    private String projectName;
    private String status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String importance;
    private String manager;

    private String departmentName;
    private String authorName;

    private String clientCeo;
    private String clientCompany;
    private String clientEmail;
    private String clientContact;

}
