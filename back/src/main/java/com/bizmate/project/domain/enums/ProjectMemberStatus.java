package com.bizmate.project.domain.enums;

import lombok.Getter;

@Getter
public enum ProjectMemberStatus {

    PRODUCT_MANAGER("PM"), PRODUCT_OWNER("PO");


    private final String pmRoleName;

    ProjectMemberStatus(String pmRoleName) {
        this.pmRoleName = pmRoleName;
    }

}
