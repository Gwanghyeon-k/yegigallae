package com.umc.yeogi_gal_lae.api.room.converter;

import com.umc.yeogi_gal_lae.api.room.domain.Room;

import com.umc.yeogi_gal_lae.api.room.dto.response.RoominfoResponse;
import com.umc.yeogi_gal_lae.api.user.domain.User;

public class RoomConverter {

    public static RoominfoResponse toResponse(Room room) {
        return RoominfoResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .masterName(room.getMaster().getUsername())
                .build();
    }

    public static Room fromRequest(String name, User master) {
        return Room.builder()
                .name(name)
                .master(master)
                .build();
    }
}

