package com.umc.yeogi_gal_lae.global.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ReasonDto {
    private HttpStatus status;
    private String code;
    private String message;
}
