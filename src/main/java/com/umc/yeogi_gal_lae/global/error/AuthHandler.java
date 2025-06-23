package com.umc.yeogi_gal_lae.global.error;

import com.umc.yeogi_gal_lae.global.common.response.BaseResponse;
import lombok.Getter;

@Getter
public class AuthHandler extends RuntimeException {
    private final ErrorStatus errorStatus;

    public AuthHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public BaseResponse<String> toResponse() {
        return new BaseResponse<>(false, errorStatus.getCode(), errorStatus.getMessage());
    }
}
