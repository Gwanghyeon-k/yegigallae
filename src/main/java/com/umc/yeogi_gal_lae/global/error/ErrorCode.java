package com.umc.yeogi_gal_lae.global.error;

import com.umc.yeogi_gal_lae.global.common.response.ReasonDto;
import com.umc.yeogi_gal_lae.global.common.status.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseStatus {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러입니다. 관리자에게 문의하세요."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "찾을 수 없는 요청입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_405", "허용되지 않은 메소드입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    INPUT_VALUE_INVALID(HttpStatus.BAD_REQUEST, "REQUEST_400", "요청사항에 필수 인자가 누락되었습니다"),
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "G005", "request message body가 없거나, 값 타입이 올바르지 않습니다."),

    // User Error
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER_401", "로그인 정보가 없습니다."),
    USER_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "USER_401", "로그인 하지 않았습니다."),
    USER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "USER_403", "권한이 없습니다."),

    // Token Error
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_401", "리프레시 토큰이 유효하지 않습니다."),

    // Place Error
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE_404", "장소를 찾을 수 없습니다."),

    //TripPlan Error
    TRIP_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "TRIP_PLAN_404", "일치하는 여행 계획이 없습니다."),

    // Room Error
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "ROOM_404", "룸을 찾을 수 없습니다."),

    INVALID_PLACE_FOR_ROOM(HttpStatus.BAD_REQUEST, "ROOM_400", "룸에 속하지 않은 장소입니다."),

    // Vote Error
    VOTE_NOT_COMPLETED_YET(HttpStatus.BAD_REQUEST, "VOTE_400", "아직 투표가 종료되지 않았습니다."),
    VOTE_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "VOTE_401", "요청 하신 투표 방을 찾을 수 없습니다."),
    VOTE_RESULT_FAILED(HttpStatus.BAD_REQUEST, "VOTE_403", "여행 확정에 실패하셨습니다. 이 방은 사라집니다."),
    DUPLICATE_VOTE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "VOTE_404", "중복 투표는 불가능합니다."),
    VOTE_CONCURRENT_UPDATE(HttpStatus.BAD_REQUEST, "VOTE_404", "동시 투표는 이용이 제한 됩니다."),
    VOTE_NOT_ALLOWED_FOR_COURSE(HttpStatus.BAD_REQUEST, "VOTE_404", "코스는 투표가 허용되지 않습니다."),

    // Room Member Error
    ROOM_MEMBER_NOT_EXIST(HttpStatus.BAD_REQUEST, "ROOM_MEMBER_404", "방에 멤버가 존재하지 않습니다."),

    // 클라이언트 오류 코드
    PLACE_NOT_BELONG_TO_ROOM(HttpStatus.BAD_REQUEST, "40001", "해당 장소는 요청한 방에 속하지 않습니다."),
    NO_PLACES_FOUND(HttpStatus.BAD_REQUEST, "40002", "해당 방에 속한 장소가 없습니다."),

    // Image Error
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE_404", "이미지를 찾을 수 없습니다."),

    //날짜 선택 오류
    DATE_ERROR(HttpStatus.BAD_REQUEST, "DATE_401", "적절한 날짜 선택이 아닙니다."),

    // 서버 오류 코드
    AI_TRIP_PLAN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50001", "여행 일정 생성에 실패했습니다."),

    // 투표방 오류
    VOTE_ROOM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "VOTE_400", "이미 존재하는 투표 방입니다."),

    // 알림 오류
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION404", "알림을 찾을 수 없습니다."),
    INVALID_PLACE_FOR_TRIP_PLAN(HttpStatus.NOT_FOUND, "INVALID_PLACE_FOR_TRIP_PLAN", "해당 장소는 지정된 여행 계획에 속하지 않습니다.");

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
