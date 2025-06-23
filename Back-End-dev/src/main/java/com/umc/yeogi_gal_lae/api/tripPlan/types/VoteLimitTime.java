package com.umc.yeogi_gal_lae.api.tripPlan.types;

import lombok.Getter;

@Getter
public enum VoteLimitTime {
    THIRTY_MINUTES("30분", 1800), // 30분 = 1800초
    SIXTY_MINUTES("60분", 3600), // 60분 = 3600초
    FOUR_HOURS("4시간", 14400),   // 4시간 = 14400초
    SIX_HOURS("6시간", 21600);   // 6시간 = 21600초

    private final String description;
    private final int seconds;

    VoteLimitTime(String description, int seconds) {
        this.description = description;
        this.seconds = seconds;
    }
}
