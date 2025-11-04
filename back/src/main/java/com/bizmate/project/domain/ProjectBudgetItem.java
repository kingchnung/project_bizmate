package com.bizmate.project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PROJECT_BUDGET_ITEMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectBudgetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    @Column(nullable = false, length = 100)
    private String itemName; // 예: 장비비, 인건비, 회의비 등

    @Column(nullable = false)
    private Long amount;
}
