package com.umc.yeogi_gal_lae.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.umc.yeogi_gal_lae.global.common.status.BaseStatus;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"httpStatus", "code", "message", "result"})
public class Response<T> {

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    public static <T> Response<T> of(BaseStatus status, T result) {
        return new Response<>(status.getReason().getStatus(),
                status.getReason().getCode(),
                status.getReason().getMessage(),
                result);
    }

    public static <T> Response<T> of(BaseStatus status) {
        return new Response<>(status.getReason().getStatus(),
                status.getReason().getCode(),
                status.getReason().getMessage(),
                null);
    }

    public static <T> Response<T> ok(BaseStatus status) {
        return new Response(status.getReason().getStatus(),
                status.getReason().getCode(),
                status.getReason().getMessage(),
                null);
    }

    // 생성자 추가: result 없이 동작
    public Response(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.result = null;
    }

    public static <T> Response<T> of(ErrorCode errorCode) {
        return new Response<>(
                errorCode.getHttpStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}
