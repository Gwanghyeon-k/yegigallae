package com.umc.yeogi_gal_lae.api.tripPlan.dto;

import com.umc.yeogi_gal_lae.api.tripPlan.types.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TripPlanRequest {
    private String location; // 여행 장소
    private String startDate;
    private String endDate;

    private TripType tripType;
    private VoteLimitTime voteLimitTime; // "30분", "60분", "4시간", "6시간"
    private Long roomId;
    private String imageUrl;
    private Double latitude;
    private Double longitude;

    // 이너 클래스 정의
    @Getter
    public static class ScheduleDetails {
        private String message; // 일정 메시지
        private String price; // 가격 정보
    }

    @Getter
    public static class CourseDetails {
        private String message; // 코스 메시지
    }

    private ScheduleDetails scheduleDetails;
    private CourseDetails courseDetails;
}