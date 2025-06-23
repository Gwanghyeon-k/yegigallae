package com.umc.yeogi_gal_lae.api.room.domain;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "room_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 생성
//@AllArgsConstructor // 모든 필드를 초기화하는 생성자
public class RoomMember {

    @EmbeddedId
    private RoomMemberId id;

    @MapsId("roomId") // EmbeddedId의 roomId와 매핑
    @ManyToOne
    @JoinColumn(name = "room_id") // room_id로 매핑
    private Room room;

    @MapsId("userId") // EmbeddedId의 userId와 매핑
    @ManyToOne
    @JoinColumn(name = "user_id") // user_id로 매핑
    private User user;

    @Builder// id 추가해서 500에러 해결..
    public RoomMember(RoomMemberId id, Room room, User user) {
        this.id = id;
        this.room = room;
        this.user = user;
    }
}