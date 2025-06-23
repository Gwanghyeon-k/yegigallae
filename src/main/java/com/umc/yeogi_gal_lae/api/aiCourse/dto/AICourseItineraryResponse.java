package com.umc.yeogi_gal_lae.api.aiCourse.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AICourseItineraryResponse {
    private String roomName;
    private int totalRoomMember;
    private String startDate;
    private List<DailyItineraryResponse> dailyItineraries;
}
