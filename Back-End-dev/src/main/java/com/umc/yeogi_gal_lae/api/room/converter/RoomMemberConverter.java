package com.umc.yeogi_gal_lae.api.room.converter;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;

import com.umc.yeogi_gal_lae.api.room.domain.RoomMemberId;
import com.umc.yeogi_gal_lae.api.room.dto.response.RoomMemberResponse;
import com.umc.yeogi_gal_lae.api.user.domain.User;

public class RoomMemberConverter {

    public static RoomMemberResponse toResponse(RoomMember roomMember) {
        return RoomMemberResponse.builder()
                .id(roomMember.getId())
                .roomId(roomMember.getRoom().getId())
                .userId(roomMember.getId().getUserId())
                .profileImage(roomMember.getUser().getProfileImage())
                .build();
    }

    public static RoomMember fromRequest(Room room, User user) {
        RoomMemberId id = new RoomMemberId(room.getId(), user.getId());
        return RoomMember.builder()
                .id(id)
                .room(room)
                .user(user)
                .build();
    }
}