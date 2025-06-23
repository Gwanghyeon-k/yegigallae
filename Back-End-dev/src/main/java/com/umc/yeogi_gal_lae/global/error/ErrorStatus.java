package com.umc.yeogi_gal_lae.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorStatus {
    // 공통 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러입니다. 관리자에게 문의하세요."),

    // 카카오 OAuth2 관련 에러
    KAKAO_AUTH_FAILED(HttpStatus.BAD_REQUEST, "KAKAO_400", "카카오 인증 요청 중 오류가 발생했습니다."),
    KAKAO_INVALID_GRANT(HttpStatus.UNAUTHORIZED, "KAKAO_401", "유효하지 않은 카카오 인증 코드입니다."),
    KAKAO_API_ERROR(HttpStatus.BAD_REQUEST, "KAKAO_402", "카카오 API 호출 중 문제가 발생했습니다."),
    KAKAO_JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "KAKAO_500", "카카오 응답 JSON 파싱 중 오류가 발생했습니다."),

    // JWT 관련 에러
    JWT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JWT_500", "JWT 토큰 생성 중 오류가 발생했습니다."),
    JWT_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_401", "유효하지 않은 JWT 토큰입니다."),
    JWT_EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "JWT_402", "만료된 JWT 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
