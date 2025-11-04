package com.bizmate.project.domain;

import com.bizmate.project.domain.enums.task.TaskPriority;
import com.bizmate.project.domain.enums.task.TaskStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "PROJECT_TASKS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    @JsonBackReference
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNEE_MEMBER_ID")
    @JsonIgnoreProperties({"project", "employee"})
    private ProjectMember assignee; // 담당자

    @Column(nullable = false, length = 150)
    private String taskName;

    @Column(length = 500)
    private String taskDescription;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Builder.Default
    private TaskStatus status = TaskStatus.PLANNED;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    private int progressRate; // 0~100%

    @PrePersist
    @PreUpdate
    public void validateProgressRate() {
        if (progressRate < 0) progressRate = 0;
        if (progressRate > 100) progressRate = 100;
        if (status == null) status = TaskStatus.PLANNED;
        if (progressRate == 100 && status != TaskStatus.COMPLETED) {
            status = TaskStatus.COMPLETED;
        }
    }
}
