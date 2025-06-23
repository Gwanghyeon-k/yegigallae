package com.umc.yeogi_gal_lae.api.room.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class AddRoomMemberRequest {
    private Long roomId;
    private List<Long> userIds;
}