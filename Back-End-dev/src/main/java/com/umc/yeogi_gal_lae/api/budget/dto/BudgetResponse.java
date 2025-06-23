package com.umc.yeogi_gal_lae.api.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BudgetResponse {
    private Long id;           // Budget 엔티티 기본키
    private Long aiCourseId;   // 연결된 AICourse의 id
    private Long tripPlanId;   // AICourse에 연결된 TripPlan의 id
    private Long roomId;       // TripPlan에 연결된 Room의 id
}
