package com.umc.yeogi_gal_lae.api.budget.dto;

import com.umc.yeogi_gal_lae.api.budget.domain.BudgetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetAssignment {
    private String placeName;
    private BudgetType budgetType;
    private Double recommendedAmount;
}