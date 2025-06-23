package com.umc.yeogi_gal_lae.api.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class RoomListResponse {
    private List<RoomResponse> rooms; // 여러 개의 방 정보
}