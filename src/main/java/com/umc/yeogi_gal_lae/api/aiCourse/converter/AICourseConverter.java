package com.umc.yeogi_gal_lae.api.aiCourse.converter;

import com.umc.yeogi_gal_lae.api.aiCourse.domain.AICourse;
import com.umc.yeogi_gal_lae.api.aiCourse.dto.AICourseItineraryResponse;
import com.umc.yeogi_gal_lae.api.aiCourse.dto.AICourseResponse;
import com.umc.yeogi_gal_lae.api.aiCourse.dto.DailyItineraryResponse;
import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AICourseConverter {

    public static List<DailyItineraryResponse> toDailyItineraryResponseList(Map<String, List<Place>> courseMap,
                                                                            String startDate) {
        // 기존의 roomName과 totalRoomMember 정보를 제거하고 day, places만 포함하도록 함
        return courseMap.entrySet().stream()
                .map(entry -> DailyItineraryResponse.builder()
                        .day(entry.getKey())
                        .places(entry.getValue().stream()
                                .map(PlaceConverter::toPlaceResponse)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    public static AICourseResponse toAICourseResponse(AICourse aiCourse) {
        return AICourseResponse.builder()
                .id(aiCourse.getId())
                .tripPlanId(aiCourse.getTripPlan().getId())
                .roomId(aiCourse.getTripPlan().getRoom().getId())
                .build();
    }

    public static AICourseItineraryResponse toAICourseItineraryResponse(AICourse aiCourse,
                                                                        Map<String, List<Place>> courseMap) {
        // TripPlan의 startDate를 "yyyy-MM-dd" 형식으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = aiCourse.getTripPlan().getStartDate().format(formatter);

        // dailyItineraries 생성 (각 DailyItineraryResponse에 startDate 포함)
        List<DailyItineraryResponse> dailyItineraries = toDailyItineraryResponseList(courseMap, startDate);

        String roomName = aiCourse.getTripPlan().getRoom().getName();
        int totalRoomMember = (aiCourse.getTripPlan().getRoom().getRoomMembers() != null)
                ? aiCourse.getTripPlan().getRoom().getRoomMembers().size() : 0;

        return AICourseItineraryResponse.builder()
                .roomName(roomName)
                .totalRoomMember(totalRoomMember)
                .startDate(startDate)
                .dailyItineraries(dailyItineraries)
                .build();
    }
}
