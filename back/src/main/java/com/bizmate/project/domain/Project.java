package com.bizmate.project.domain;

import com.bizmate.common.domain.BaseEntity;
import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROJECTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false, length = 150)
    private String projectName;

    @Column(length = 500)
    private String projectGoal;

    @Lob
    private String projectOverview;

    @Lob
    private String expectedEffect;


    private Long totalBudget;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANNING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHOR_ID")
    private UserEntity author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_ID")
    private Department department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_ID")
    private ApprovalDocuments approvalDocument;

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> participants = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectBudgetItem> budgetItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTask> tasks = new ArrayList<>();

    // 유틸 메서드
    public void addParticipant(ProjectMember p) {
        p.setProject(this);
        participants.add(p);
    }

    public void addBudgetItem(ProjectBudgetItem item) {
        item.setProject(this);
        budgetItems.add(item);
    }

    public void addTask(ProjectTask task) {
        task.setProject(this);
        tasks.add(task);
    }
}
