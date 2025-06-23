package com.umc.yeogi_gal_lae.api.budget.converter;

import com.umc.yeogi_gal_lae.api.budget.domain.Budget;
import com.umc.yeogi_gal_lae.api.budget.dto.BudgetAssignment;
import com.umc.yeogi_gal_lae.api.budget.dto.BudgetDetailResponse;
import com.umc.yeogi_gal_lae.api.budget.dto.BudgetResponse;
import com.umc.yeogi_gal_lae.api.budget.dto.DailyBudgetAssignmentResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BudgetConverter {

    public static BudgetResponse toBudgetResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .aiCourseId(budget.getAiCourse().getId())
                .tripPlanId(budget.getAiCourse().getTripPlan().getId())
                .roomId(budget.getAiCourse().getTripPlan().getRoom().getId())
                .build();
    }

    public static List<DailyBudgetAssignmentResponse> toDailyBudgetAssignmentResponseList(
            Map<String, List<BudgetAssignment>> budgetMap) {
        return budgetMap.entrySet().stream()
                .sorted((e1, e2) -> {
                    int day1 = Integer.parseInt(e1.getKey().replaceAll("\\D", ""));
                    int day2 = Integer.parseInt(e2.getKey().replaceAll("\\D", ""));
                    return Integer.compare(day1, day2);
                })
                .map(e -> DailyBudgetAssignmentResponse.builder()
                        .day(e.getKey())
                        .assignments(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    public static BudgetDetailResponse toBudgetDetailResponse(Budget budget,
                                                              Map<String, List<BudgetAssignment>> budgetMap) {
        List<DailyBudgetAssignmentResponse> dailyAssignments = toDailyBudgetAssignmentResponseList(budgetMap);
        return BudgetDetailResponse.builder()
                .dailyAssignments(dailyAssignments)
                .location(budget.getAiCourse().getTripPlan().getLocation())
                .imageUrl(budget.getAiCourse().getTripPlan().getImageUrl())
                .startDate(budget.getAiCourse().getTripPlan().getStartDate())
                .endDate(budget.getAiCourse().getTripPlan().getEndDate())
                .build();
    }

}
