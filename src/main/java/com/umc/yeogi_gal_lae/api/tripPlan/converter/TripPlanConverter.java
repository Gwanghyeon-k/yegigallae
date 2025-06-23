package com.umc.yeogi_gal_lae.api.tripPlan.converter;

import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanDTO;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanRequest;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TripPlanConverter {

    public static TripPlan toEntity(TripPlanRequest request, User user, Room room, TripPlanType tripPlanType) {

        TripPlan.TripPlanBuilder builder = TripPlan.builder()
                .location(request.getLocation())
                .startDate(request.getStartDate() != null ? LocalDate.parse(request.getStartDate()) : null)
                .endDate(request.getEndDate() != null ? LocalDate.parse(request.getEndDate()) : null)
                .tripPlanType(tripPlanType)
                .tripType(request.getTripType())
                .voteLimitTime(request.getVoteLimitTime())
                .imageUrl(request.getImageUrl()) // 클라이언트가 입력한 URL 사용
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(user)
                .room(room);

        switch (tripPlanType) {
            case SCHEDULE:
                if (request.getScheduleDetails() != null) {
                    builder.description(request.getScheduleDetails().getMessage())
                            .price(request.getScheduleDetails().getPrice()); // price는 SCHEDULE 타입에서만 사용
                }
                break;
            case COURSE:
                if (request.getCourseDetails() != null) {
                    builder.description(request.getCourseDetails().getMessage());
                }
                break;
        }

        return builder.build();
    }

    public static TripPlanResponse toResponse(TripPlan tripPlan) {
        TripPlanResponse.TripPlanResponseBuilder responseBuilder = TripPlanResponse.builder()
                .id(tripPlan.getId())
                .roomId(tripPlan.getRoom().getId())
                .masterId(tripPlan.getRoom().getMaster().getId())
                .voteRoomId(tripPlan.getVoteRoom() != null ? tripPlan.getVoteRoom().getId() : null)
                .location(tripPlan.getLocation())
                .startDate(tripPlan.getStartDate() != null ? tripPlan.getStartDate().toString() : null)
                .endDate(tripPlan.getEndDate() != null ? tripPlan.getEndDate().toString() : null)
                .tripPlanType(tripPlan.getTripPlanType())
                .tripType(tripPlan.getTripType())
                .voteLimitTime(tripPlan.getVoteLimitTime())
                .roomName(tripPlan.getRoom().getName())
                .description(tripPlan.getDescription())
                .imageUrl(tripPlan.getImageUrl()) // 클라이언트가 입력한 URL 반환
                .latitude(tripPlan.getLatitude())
                .longitude(tripPlan.getLongitude());

        // price는 SCHEDULE 타입에서만 반환
        if (tripPlan.getTripPlanType() == TripPlanType.SCHEDULE) {
            responseBuilder.price(tripPlan.getPrice());
        }

        return responseBuilder.build();
    }

    public static TripPlanDTO toDTO(TripPlan tripPlan) {
        return TripPlanDTO.builder()
                .id(tripPlan.getId())
                .location(tripPlan.getLocation())
                // 필요한 다른 필드들 설정...
                .places(tripPlan.getPlaces() == null
                        ? List.of()
                        : tripPlan.getPlaces().stream()
                                .map(PlaceConverter::toDTO)
                                .collect(Collectors.toList()))
                .build();
    }
}