package com.umc.yeogi_gal_lae.api.notification.dto;

import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private String title;
    private String caption;
    private String type; // "VOTE", "COURSE", "BUDGET", etc.
    private Long tripPlanId;
    private TripPlanType tripPlanType;
}