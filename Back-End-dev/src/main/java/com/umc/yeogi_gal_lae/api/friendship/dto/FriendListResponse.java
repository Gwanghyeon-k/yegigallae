package com.umc.yeogi_gal_lae.api.friendship.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendListResponse {
    private Long friendId;
    private String friendName;
    private String profileImageUrl;
}
