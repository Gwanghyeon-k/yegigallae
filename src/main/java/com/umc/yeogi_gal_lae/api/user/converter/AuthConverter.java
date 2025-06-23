package com.umc.yeogi_gal_lae.api.user.converter;

import com.umc.yeogi_gal_lae.api.user.domain.User;

public class AuthConverter {
    public static User toUser(String email, String nickname, String profileImage) {
        return User.builder()
            .email(email)
            .username(nickname)
            .profileImage(profileImage)
            .build();
    }
}