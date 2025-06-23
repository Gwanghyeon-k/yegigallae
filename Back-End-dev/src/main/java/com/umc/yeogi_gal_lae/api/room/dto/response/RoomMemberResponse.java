package com.umc.yeogi_gal_lae.api.room.dto.response;

import com.umc.yeogi_gal_lae.api.room.domain.RoomMemberId;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor // 모든 필드를 포함하는 생성자 자동 생성
public class RoomMemberResponse {
    private RoomMemberId id;
    private Long roomId;
    private Long userId;
    private String profileImage;



}