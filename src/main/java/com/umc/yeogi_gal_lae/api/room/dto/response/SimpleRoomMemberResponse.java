package com.umc.yeogi_gal_lae.api.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SimpleRoomMemberResponse {
    private Long userId;
    private String profileImage;
}