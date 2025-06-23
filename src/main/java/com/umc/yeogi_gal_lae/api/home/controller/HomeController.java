package com.umc.yeogi_gal_lae.api.home.controller;

import com.umc.yeogi_gal_lae.api.home.dto.HomeResponse;
import com.umc.yeogi_gal_lae.api.home.service.HomeService;
import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "진행 중인 투표방 조회", description = "사용자가 속한 진행 중인 투표방과 개수를 조회합니다.")
    @GetMapping("/ongoing-vote-rooms")
    public Response<HomeResponse.OngoingVoteRoomList> getOngoingVoteRooms() {
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();
        return homeService.getOngoingVoteRooms(userEmail);
    }

    @Operation(summary = "완료된 투표 기반 예정된 여행 조회", description = "완료된 투표 상태의 여행 중 종료 날짜가 현재 시간과 같거나 미래인 여행들을 조회합니다.")
    @GetMapping("/completed-vote-rooms")
    public Response<HomeResponse.CompletedVoteRoomList> getCompletedVoteRooms() {
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();
        return homeService.getFutureVoteBasedTrips(userEmail);
    }

    @Operation(summary = "완료된 여행 계획 조회", description = "사용자가 속한 완료된 여행 계획과 개수를 조회합니다.")
    @GetMapping("/completed-trip-plans")
    public Response<HomeResponse.CompletedTripPlanList> getCompletedTripPlans() {
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();
        return homeService.getCompletedTripPlans(userEmail);
    }

    @Operation(summary = "홈 화면 알림 확인 여부", description = "특정 사용자가 읽지 않은 알림이 있는지 여부를 반환합니다.")
    @GetMapping("/notification-status")
    public Response<HomeResponse.NotificationStatus> getNotificationStatus() {
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();
        return homeService.getNotificationStatus(userEmail);
    }
}
