package com.bizmate.project.dto.budgetitem;

import com.bizmate.project.domain.ProjectBudgetItem;
import lombok.Getter;

@Getter
public class ProjectBudgetItemResponseDTO {
    private final Long itemId;
    private final String itemName;
    private final Long amount;

    public ProjectBudgetItemResponseDTO(ProjectBudgetItem item) {
        this.itemId = item.getItemId();
        this.itemName = item.getItemName();
        this.amount = item.getAmount();
    }
}
