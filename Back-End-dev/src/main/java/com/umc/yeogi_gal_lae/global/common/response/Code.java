package com.umc.yeogi_gal_lae.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Code {

    OK("COM-000", "Ok."),

    // 커스텀해서 사용하시면 됩니다.
    MEMBER_NOT_FOUND("MEM-001", "Member not found."),
    MEMBER_EMAIL_UNAVAILABLE("MEM-002", "Email cannot used."),
    MEMBER_NICKNAME_UNAVAILABLE("MEM-003", "Nickname cannot used."),
    MEMBER_PASSWORD_UNAVAILABLE("MEM-004", "Password cannot used."),
    MEMBER_ALREADY_ON_PROCESS("MEM-999", "Member is already on process."),

    NOT_ACADEMY_EMAIL("EEM-001", "Email is not a university email."),
    AUTH_CODE_NOT_MATCH("ATH-001", "Auth code not match."),
    ACCESS_TOKEN_NOT_FOUND("ATH-002", "Auth token not found."),
    REFRESH_TOKEN_NOT_FOUND("ATH-003", "Refresh token not found."),
    MEMBER_LOGIN_SESSION_EXPIRED("ATH-004", "Auth session expired."),


    SERVER_ERROR("SEV-999", "Check the server."),

    // User
    USER_FETCH_OK("USER_200", "유저 정보 조회 성공"),
    USER_NOT_FOUND("USER_401", "로그인 정보가 없습니다."),

    // Token Success
    TOKEN_REISSUE_OK("TOKEN_200", "토큰 재발급 성공"),

    // User Error
    USER_NOT_AUTHENTICATED("USER_403", "로그인 하지 않았습니다."),

    // Token Error
    INVALID_REFRESH_TOKEN("TOKEN_401", "리프레시 토큰이 유효하지 않습니다.");

    private final String code;
    private final String message;

}