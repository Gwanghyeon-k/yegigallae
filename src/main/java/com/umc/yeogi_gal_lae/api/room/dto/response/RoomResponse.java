package com.umc.yeogi_gal_lae.api.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RoomResponse {
    private Long roomId; // 방 ID
    private String roomName; // 방 이름
    private List<SimpleRoomMemberResponse> members;
}
