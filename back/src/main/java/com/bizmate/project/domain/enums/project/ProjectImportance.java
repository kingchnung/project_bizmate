package com.bizmate.project.domain.enums.project;

import lombok.Getter;

@Getter
public enum ProjectImportance {

    HIGH("최우선"),
    MEDIUM("중요"),
    LOW("보통");

    private final String projectImportance;

    ProjectImportance(String projectImportance) {
        this.projectImportance = projectImportance;
    }
}
