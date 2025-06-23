package com.umc.yeogi_gal_lae.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserResponseDTO {
    @Getter
    @AllArgsConstructor
    public static class JoinResultDTO {
        private String email;
        private String nickname;
        private String profileImage;
    }
    @Getter
    @AllArgsConstructor
    public static class JoinInfoResultDTO {
        private Long userId;
        private String email;
        private String nickname;
        private String profileImage;
    }
}
