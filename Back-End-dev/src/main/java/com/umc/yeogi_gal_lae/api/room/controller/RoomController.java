package com.umc.yeogi_gal_lae.api.room.controller;


import com.umc.yeogi_gal_lae.api.room.dto.request.AddRoomMemberRequest;
import com.umc.yeogi_gal_lae.api.room.dto.request.CreateRoomRequest;
import com.umc.yeogi_gal_lae.api.room.dto.response.RoomListResponse;
import com.umc.yeogi_gal_lae.api.room.dto.response.RoominfoResponse;
import com.umc.yeogi_gal_lae.api.room.dto.response.RoomMemberResponse;
import com.umc.yeogi_gal_lae.api.room.service.RoomMemberService;
import com.umc.yeogi_gal_lae.api.room.service.RoomService;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;

import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;
    private final RoomMemberService roomMemberService;
    private final UserRepository userRepository;


    /**
     * 방 생성 API
     */
    @Operation(summary = "방 생성 API", description = "현재 사용자가 방을 생성합니다.")
    @PostMapping("/room")
    public Response<Void> createRoom(@RequestBody CreateRoomRequest roomRequest) {
        // 인증된 사용자 이메일 가져오기
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();


        // 서비스 호출
        roomService.createRoom(roomRequest, userEmail);

        return Response.of(SuccessCode.ROOM_CREATED_OK, null);
    }

    /**
     * 방 멤버 추가 API
     */
    @Operation(summary = "방 멤버 추가 API", description = "특정 방에 멤버를 추가합니다.")
    @PostMapping("/room/{roomId}/members")
    public Response<Void> addRoomMembers(
            @RequestBody AddRoomMemberRequest request) {

        // 인증된 사용자 이메일 가져오기
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();


        // 서비스 호출: 여기서 에러 나는듯
        roomMemberService.addRoomMembers(request, userEmail);

        return Response.of(SuccessCode.ROOM_MEMBERS_ADDED_OK, null);
    }

    /**
     * 방 상세 조회 API
     */
    @Validated
    @Operation(
            summary = "방 상세 조회 API",
            description = "특정 방의 상세 정보를 반환합니다. 방 이름과 방장 정보를 포함합니다."
    )
    @GetMapping("/room/{roomId}")
    public Response<RoominfoResponse> getRoomDetails(
            @PathVariable @NotNull(message = "방 ID는 필수입니다.") Long roomId) {

        // 인증된 사용자 이메일 가져오기
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        // 서비스 호출
        RoominfoResponse roomResponse = roomService.getRoomDetails(roomId);

        return Response.of(SuccessCode.ROOM_DETAILS_OK, roomResponse);
    }


    /**
     * 방 멤버 조회 API
     */
    @Validated
    @Operation(
            summary = "방 멤버 조회 API",
            description = "특정 방에 속한 멤버 목록을 반환합니다."
    )
    @GetMapping("/room/{roomId}/members")
    public Response<List<RoomMemberResponse>> getRoomMembers(
            @PathVariable @NotNull(message = "방 ID는 필수입니다.") Long roomId) {

        // 인증된 사용자 이메일 가져오기
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        // 서비스 호출
        List<RoomMemberResponse> members = roomService.getRoomMembers(roomId);


        return Response.of(SuccessCode.ROOM_MEMBERS_OK, members);

    }

    @Validated
    @Operation(
            summary = "사용자가 속한 방 목록 조회 API",
            description = "현재 인증된 사용자가 속한 모든 방을 리스트 형태로 반환합니다."
    )
    @GetMapping("/room/list")
    public Response<RoomListResponse> getRoomsByAuthenticatedUser() {
        // 인증된 사용자 이메일 가져오기
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        // 이메일을 기반으로 사용자 객체 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("요청하신 이메일과 일치하는 유저가 존재하지 않습니다."));

        // 사용자 ID 추출
        Long userId = user.getId();

        // 기존 getRoomsByUserId 메서드 호출
        RoomListResponse rooms = roomService.getRoomsByUserId(userId);

        return Response.of(SuccessCode.ROOM_LIST_FETCHED_OK, rooms);
    }

}