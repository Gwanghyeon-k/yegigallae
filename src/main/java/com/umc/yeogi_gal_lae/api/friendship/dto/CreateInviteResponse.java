package com.umc.yeogi_gal_lae.api.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateInviteResponse {
    private String inviteUrl; // 생성된 초대 URL
}