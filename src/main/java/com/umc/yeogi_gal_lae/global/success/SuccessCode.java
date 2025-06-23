package com.umc.yeogi_gal_lae.global.success;

import com.umc.yeogi_gal_lae.global.common.response.ReasonDto;
import com.umc.yeogi_gal_lae.global.common.status.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseStatus {
    // Common Success
    OK(HttpStatus.OK, "COMMON_200", "성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON_201", "성공적으로 생성되었습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON_204", "성공적으로 삭제되었습니다."),

    // Home API Success
    ONGOING_VOTE_ROOMS_FETCH_OK(HttpStatus.OK, "HOME_200", "진행 중인 투표방 조회 성공"),
    COMPLETED_VOTE_ROOMS_FETCH_OK(HttpStatus.OK, "HOME_201", "완료된 투표방 조회 성공"),
    COMPLETED_TRIP_PLANS_FETCH_OK(HttpStatus.OK, "HOME_202", "완료된 여행 계획 조회 성공"),

    // User Success
    USER_FETCH_OK(HttpStatus.OK, "USER_200", "유저 정보 조회 성공"),
    TOKEN_REISSUE_OK(HttpStatus.OK, "TOKEN_200", "토큰 재발급 성공"),
    USER_LOGOUT_OK(HttpStatus.OK, "USER_200", "유저 로그아웃 성공"),
    USER_LOGIN_OK(HttpStatus.OK, "USER_200", "유저 로그인 성공"),


    // Place Success
    PLACE_ADD_OK(HttpStatus.OK, "PLACE_200", "여행 장소 등록 성공."),
    PLACE_DELETED(HttpStatus.OK, "PLACE_200", "여행 장소 삭제 성공."),
    PLACES_FETCHED(HttpStatus.OK, "PLACE_201", "여행 장소 전체 조회 성공y."),

    // Vote Success
    VOTE_ROOM_CREATED_OK(HttpStatus.OK, "VOTE_200", "투표방 생성에 성공하였습니다."),
    VOTE_CREATED_OK(HttpStatus.OK, "VOTE_201", "투표 생성에 성공하였습니다."),
    VOTE_RESULTS_OK(HttpStatus.OK, "VOTE_202", "투표 결과 조회에 성공하였습니다."),
    VOTE_FINISH_OK(HttpStatus.OK, "VOTE_203", "여행 계획 확정(투표)에 성공하였습니다."),


    // Room Success
    ROOM_CREATED_OK(HttpStatus.OK, "ROOM_200", "방 생성에 성공하였습니다."),
    ROOM_DETAILS_OK(HttpStatus.OK, "ROOM_201", "방 상세 조회에 성공하였습니다."),
    ROOM_MEMBERS_OK(HttpStatus.OK, "ROOM_202", "방 멤버 조회에 성공하였습니다."),
    ROOM_MEMBERS_ADDED_OK(HttpStatus.OK, "ROOM_203", "방 멤버 추가에 성공하였습니다."),
    ROOM_LIST_FETCHED_OK(HttpStatus.OK, "ROOM_200", "사용자가 속한 방 리스트 조회 성공"),


    // Notification Success
    NOTIFICATION_FETCH_OK(HttpStatus.OK, "NOTIFICATION_200", "알림 조회 성공"),
    NOTIFICATION_START_OK(HttpStatus.CREATED, "NOTIFICATION_201", "알림 시작 성공"),
    NOTIFICATION_END_OK(HttpStatus.OK, "NOTIFICATION_202", "알림 종료 성공"),
    NOTIFICATION_READ_OK(HttpStatus.OK, "NOTIFICATION_203", "알림 읽음"),


    // AI Success
    AI_TRIP_PLAN_GENERATED(HttpStatus.OK, "20001", "여행 일정이 성공적으로 생성되었습니다."),

    // Friend & Invite Success
    INVITE_CREATED_OK(HttpStatus.OK, "INVITE_200", "초대가 성공적으로 생성되었습니다."),
    FRIENDSHIP_CREATED_OK(HttpStatus.OK, "FRIEND_200", "친구 관계가 성공적으로 생성되었습니다."),
    FRIEND_LIST_OK(HttpStatus.OK, "FRIEND_201", "친구 목록 조회에 성공하였습니다."),
    FRIEND_DELETED_OK(HttpStatus.OK, "FRIEND_202", "친구 삭제에 성공하였습니다."),

    // TripPlan Success
    TRIP_PLAN_CREATED_OK(HttpStatus.OK, "TRIP_PLAN200", "여행계획 생성 성공했습니다."),
    TRIP_PLAN_RESULT_OK(HttpStatus.OK, "TRIP_PLAN201", "여행계획 조회 성공했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .status(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
