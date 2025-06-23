package com.umc.yeogi_gal_lae.api.aiCourse.dto;

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
public class AICourseResponse {
    private Long id;         // AICourse 엔티티의 기본 키
    private Long tripPlanId; // 해당 TripPlan의 ID
    private Long roomId;     // TripPlan에 연결된 Room의 ID
}