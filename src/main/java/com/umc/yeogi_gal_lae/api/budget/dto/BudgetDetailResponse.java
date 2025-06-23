package com.umc.yeogi_gal_lae.api.budget.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetDetailResponse {
    private String location;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyBudgetAssignmentResponse> dailyAssignments;

}
