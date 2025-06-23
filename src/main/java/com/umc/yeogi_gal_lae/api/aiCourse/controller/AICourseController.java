package com.umc.yeogi_gal_lae.api.aiCourse.controller;

import com.umc.yeogi_gal_lae.api.aiCourse.converter.AICourseConverter;
import com.umc.yeogi_gal_lae.api.aiCourse.domain.AICourse;
import com.umc.yeogi_gal_lae.api.aiCourse.dto.AICourseIdResponse;
import com.umc.yeogi_gal_lae.api.aiCourse.dto.AICourseItineraryResponse;
import com.umc.yeogi_gal_lae.api.aiCourse.dto.AICourseResponse;
import com.umc.yeogi_gal_lae.api.aiCourse.repository.AICourseRepository;
import com.umc.yeogi_gal_lae.api.aiCourse.service.AICourseService;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aiCourse")
@RequiredArgsConstructor
public class AICourseController {

    private final TripPlanRepository tripPlanRepository;
    private final AICourseRepository aiCourseRepository;
    private final AICourseService aiCourseService;

    /**
     * POST /api/ai-course/gpt/{tripPlanId} TripPlan ID를 기반으로 GPT API를 호출하여 AICourse를 생성하고, 생성된 AICourse의 id를 반환합니다.
     */
    @PostMapping("/tripPlan/{tripPlanId}")
    public Response<AICourseResponse> generateAndStoreAICourse(@PathVariable Long tripPlanId) {
        Optional<TripPlan> tripPlanOpt = tripPlanRepository.findById(tripPlanId);
        if (tripPlanOpt.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND, null);
        }
        TripPlan tripPlan = tripPlanOpt.get();
        var aiCourse = aiCourseService.generateAndStoreAICourse(tripPlan);
        if (aiCourse == null) {
            return Response.of(ErrorCode.INTERNAL_SERVER_ERROR, null);
        }
        return Response.of(SuccessCode.OK, AICourseConverter.toAICourseResponse(aiCourse));
    }

    /**
     * GET /api/ai-course/room/{roomId}/{courseId} Room ID와 courseId를 기반으로 저장된 AICourse를 조회하여 DTO로 반환합니다.
     */
    @GetMapping("/tripPlan/{tripPlanId}/{aiCourseId}")
    public Response<AICourseItineraryResponse> getStoredAICourse(
            @PathVariable Long tripPlanId,
            @PathVariable Long aiCourseId) {
        Optional<AICourse> aiCourseOpt = aiCourseRepository.findById(aiCourseId);
        if (aiCourseOpt.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND);
        }
        AICourse aiCourse = aiCourseOpt.get();
        if (!aiCourse.getTripPlan().getId().equals(tripPlanId)) {
            return Response.of(ErrorCode.NOT_FOUND);
        }
        Map<String, List<Place>> courseMap = aiCourseService.getStoredAICourseById(aiCourseId);
        if (courseMap.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND);
        }
        AICourseItineraryResponse responseDTO = AICourseConverter.toAICourseItineraryResponse(aiCourse, courseMap);
        return Response.of(SuccessCode.OK, responseDTO);
    }

    @GetMapping("/tripPlan/{tripPlanId}/ids")
    public Response<List<AICourseIdResponse>> getAICourseIdsByTripPlanId(@PathVariable Long tripPlanId) {
        List<AICourse> aiCourses = aiCourseRepository.findAllByTripPlanId(tripPlanId);
        if (aiCourses.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND, Collections.emptyList());
        }
        List<AICourseIdResponse> aiCourseIdResponses = aiCourses.stream()
                .map(aiCourse -> new AICourseIdResponse(aiCourse.getId()))
                .collect(Collectors.toList());
        return Response.of(SuccessCode.OK, aiCourseIdResponses);
    }
}
