package com.umc.yeogi_gal_lae.api.room.domain;


import com.umc.yeogi_gal_lae.api.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter

@Entity
@Table(name = "room")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 초기화하는 생성자
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id") // 명시적으로 컬럼 이름 정의
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;


    @ManyToOne(fetch = FetchType.LAZY) // User와 다대일 관계 설정
    @JoinColumn(name = "master_id", nullable = false) // 외래 키 매핑(master_id로 컬럼 생성해야 됨)
    private User master;


    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomMember> roomMembers; // RoomMember와의 관계



    public Room(String name, User master) {
        this.name = name;
        this.master = master;
    }

}