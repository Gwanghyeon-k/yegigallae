package com.umc.yeogi_gal_lae.api.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserRequestDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginRequestDTO {
        private String email;
        private String nickname;
        private String profileImage;
    }
}
