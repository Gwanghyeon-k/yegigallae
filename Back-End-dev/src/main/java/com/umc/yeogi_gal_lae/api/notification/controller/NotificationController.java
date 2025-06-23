package com.umc.yeogi_gal_lae.api.notification.controller;

import com.umc.yeogi_gal_lae.api.notification.dto.NotificationDto;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;
import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 시작 알림 생성 - 테스트용
     */
    @PostMapping("/start")
    public ResponseEntity<Response<Void>> createStartNotification(
            @RequestParam String roomName,
            @RequestParam String userName,
            @RequestParam String userEmail,
            @RequestParam NotificationType type,
            @RequestParam Long tripPlanId,
            @RequestParam TripPlanType tripPlanType
    ) {
        notificationService.createStartNotification(roomName, userName, userEmail, type, tripPlanId, tripPlanType);
        return ResponseEntity.ok(Response.of(SuccessCode.NOTIFICATION_START_OK));
    }

    /**
     * 종료 알림 생성 - 테스트용
     */
    @PostMapping("/end")
    public ResponseEntity<Response<Void>> createEndNotification(
            @RequestParam String roomName,
            @RequestParam String userEmail,
            @RequestParam NotificationType type,
            @RequestParam Long tripPlanId,
            @RequestParam TripPlanType tripPlanType
    ) {
        notificationService.createEndNotification(roomName, userEmail, type,tripPlanId, tripPlanType);
        return ResponseEntity.ok(Response.of(SuccessCode.NOTIFICATION_END_OK));
    }

    /**
     * 최신 알림 조회 API
     */
    @GetMapping
    public ResponseEntity<Response<List<NotificationDto>>> getUserNotifications() {

        // 현재 로그인한 사용자의 이메일 가져오기
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        List<NotificationDto> notifications = notificationService.getUserNotifications(userEmail);

        return ResponseEntity.ok(Response.of(SuccessCode.NOTIFICATION_FETCH_OK, notifications));
    }

    /**
     * 특정 알림 읽음 처리
     */
    @Operation(summary = "특정 알림 읽음 처리", description = "로그인한 현재 사용자의 읽지 않은 알림을 읽음으로 처리합니다.")
    @PatchMapping("/{id}/read")
    public ResponseEntity<Response<Void>> markNotificationAsRead(
        @Parameter(description = "읽음 처리할 알림 ID", required = true, example = "1")
        @PathVariable Long id) {
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();
        notificationService.markNotificationAsRead(id, userEmail);
        return ResponseEntity.ok(Response.of(SuccessCode.NOTIFICATION_READ_OK));
    }
}