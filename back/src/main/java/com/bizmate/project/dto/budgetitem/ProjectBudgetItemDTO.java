package com.bizmate.project.dto.budgetitem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectBudgetItemDTO {
    private String itemName;
    private Long amount;
}