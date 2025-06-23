package com.umc.yeogi_gal_lae.api.friendship.controller;

import com.umc.yeogi_gal_lae.api.friendship.dto.CreateInviteResponse;
import com.umc.yeogi_gal_lae.api.friendship.dto.FriendListResponse;
import com.umc.yeogi_gal_lae.api.friendship.service.FriendshipService;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final UserRepository userRepository;

    @Operation(summary = "친구 초대 URL 생성", description = "사용자가 친구 초대를 위해 공유 가능한 URL을 생성합니다.")
    @PostMapping("/invite")
    public Response<CreateInviteResponse> createInvite() {
        // 인증된 사용자 이메일 가져오기
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        // 이메일을 기반으로 사용자 객체 조회
        User inviter = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("요청하신 이메일과 일치하는 유저가 존재하지 않습니다."));

        // 초대 URL 생성
        String inviteUrl = friendshipService.generateInviteUrl(inviter.getId());

        CreateInviteResponse response = new CreateInviteResponse(inviteUrl);
        return Response.of(SuccessCode.INVITE_CREATED_OK, response);
    }

    @Operation(
            summary = "친구 초대 수락",
            description = "친구 초대 URL을 통해 친구 요청을 수락하고 관계를 생성합니다."
    )
    @PostMapping("/friendship/accept")
    public Response<Void> acceptInvite(@RequestParam @NotNull(message = "토큰은 필수입니다.") String token) {

        // 친구 초대 수락자의 유저 정보
        String inviteeEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        friendshipService.acceptInvite(token, inviteeEmail);
        return Response.of(SuccessCode.FRIENDSHIP_CREATED_OK);
    }

    @Validated
    @Operation(
            summary = "친구 목록 조회 API",
            description = "현재 사용자의 친구 목록을 반환합니다."
    )
    @GetMapping("/friendship/friends")
    public Response<List<FriendListResponse>> getFriends() {

        // 토큰에서 인증된 사용자 이메일 가져오기
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        // 이메일로 사용자 ID 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Long userId = user.getId();

        // 친구 목록 조회 서비스 호출
        List<FriendListResponse> friends = friendshipService.getFriendList(userId);

        return Response.of(SuccessCode.FRIEND_LIST_OK, friends);
    }

    @Operation(
            summary = "친구 삭제 API",
            description = "친구 관계를 삭제합니다."
    )
    @DeleteMapping("/friendship/friends/{friendId}")
    public Response<Void> deleteFriend(@PathVariable Long friendId) {

        // 토큰에서 인증된 사용자 이메일 가져오기
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        // 이메일로 사용자 ID 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Long userId = user.getId();

        // 친구 삭제 서비스 호출
        friendshipService.deleteFriendship(userId, friendId);

        return Response.ok(SuccessCode.FRIEND_DELETED_OK);
    }
}