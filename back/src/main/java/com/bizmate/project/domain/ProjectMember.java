package com.bizmate.project.domain;

import com.bizmate.common.domain.BaseEntity;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.project.domain.embeddables.ProjectMemberId;
import com.bizmate.project.domain.enums.ProjectMemberStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project_member")
public class ProjectMember extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    @JsonIgnoreProperties({"projectMembers"})
    private Employee employee;  // HR 모듈 연동

    @Column(length = 50)
    private String projectRole; // 예: PL, 개발, 디자이너 등

}
