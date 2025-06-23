package com.umc.yeogi_gal_lae.api.budget.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyBudgetAssignmentResponse {
    private String day; // 예: "1일차", "2일차", ...
    private List<BudgetAssignment> assignments;
}
