package com.umc.yeogi_gal_lae.api.user.converter;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.dto.response.UserResponseDTO;

public class UserConverter {
    public static UserResponseDTO.JoinResultDTO toJoinResultDTO(User user) {
        return new UserResponseDTO.JoinResultDTO(
                user.getEmail(),
                user.getUsername(),
                user.getProfileImage()
        );
    }

    public static UserResponseDTO.JoinInfoResultDTO toJoinInfoResultDTO(User user) {
        return new UserResponseDTO.JoinInfoResultDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getProfileImage()
        );
    }
}