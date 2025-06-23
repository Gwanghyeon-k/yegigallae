package com.umc.yeogi_gal_lae.api.room.service;


import com.umc.yeogi_gal_lae.api.room.converter.RoomMemberConverter;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMemberId;
import com.umc.yeogi_gal_lae.api.room.dto.request.CreateRoomRequest;
import com.umc.yeogi_gal_lae.api.room.dto.response.*;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;


    /**
     * 방 생성
     *
     * @param request 방 생성 요청 정보
     */
    public void createRoom(CreateRoomRequest request, String userEmail) {
        // 유저 검증 (방장)
        User master = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("요청하신 이메일과 일치하는 유저가 존재하지 않습니다."));

        // 방 생성 및 저장: 한 명이 방을 여러개 만들어도 되니까 db 찾아보는 과정 필요 x
        Room room = Room.builder()
                .name(request.getRoomName())
                .master(master)
                .build();

        roomRepository.save(room);

        // 방 멤버 추가
        List<User> users = userRepository.findAllById(request.getUserIds());

        // 방장 추가
        if (!users.contains(master)) {  // 중복 추가 방지
            users.add(master);
        }

        List<RoomMember> newRoomMembers = users.stream()
                .map(user -> RoomMemberConverter.fromRequest(room, user))
                .toList();


        roomMemberRepository.saveAll(newRoomMembers);

    }

    /**
     * 방 정보 조회
     *
     * @param roomId 방 ID
     * @return RoomResponse (방 정보 DTO)
     */
    public RoominfoResponse getRoomDetails(Long roomId) {
        // 방 존재 확인
        Room room = roomRepository.findById(roomId)
                .orElseGet(() -> {
                    log.warn("DB에 해당 방이 존재하지 않음, 목업 데이터 반환합니다.");
                    return Room.builder()
                            .id(0L)  // 기본 목업 데이터 ID
                            .name("Default Room")
                            .master(User.builder()
                                    .id(0L)
                                    .username("Mock Master")
                                    .build())
                            .build();
                });

        // 방 정보 응답
        return RoominfoResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .masterName(room.getMaster().getUsername())
                .build();
    }

    /**
     * 방에 속한 사용자 목록 조회
     *
     * @param roomId 방 ID
     * @return 사용자 이름 목록
     */
    public List<RoomMemberResponse> getRoomMembers(Long roomId) {
        // 방 존재 확인
        if (!roomRepository.existsById(roomId)) {
            log.warn("DB에 해당 방이 존재하지 않음, 기본 목업 데이터 반환");
            return List.of(
                    new RoomMemberResponse(new RoomMemberId(roomId, 0L), roomId, 0L, "mock_profile1.jpg"),
                    new RoomMemberResponse(new RoomMemberId(roomId, 1L), roomId, 1L, "mock_profile2.jpg")
            );
        }

        // 방에 속한 사용자 목록 조회
        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoomId(roomId);

        // DB에 데이터가 없을 경우 기본 데이터 반환
        if (roomMembers.isEmpty()) {
            log.warn("방 멤버가 없음, 기본 목업 데이터 반환");
            return List.of(
                    new RoomMemberResponse(new RoomMemberId(roomId, 0L), roomId, 0L, "mock_profile1.jpg"),
                    new RoomMemberResponse(new RoomMemberId(roomId, 1L), roomId, 1L, "mock_profile2.jpg")
            );
        }

        // RoomMember 객체를 RoomMemberResponse로 변환
        return roomMembers.stream()
                .map(RoomMemberConverter::toResponse)
                .toList();
    }

    /**
     * 사용자가 속한 방 리스트 조회
     */
    public RoomListResponse getRoomsByUserId(Long userId) {
        // 사용자가 속한 RoomMember 리스트 조회
        List<RoomMember> roomMembers = roomMemberRepository.findAllByUserId(userId);

        // RoomMember에서 Room만 추출
        List<Room> rooms = roomMembers.stream()
                .map(RoomMember::getRoom)
                .distinct()
                .collect(Collectors.toList());

        // DB에 데이터가 없을 경우 기본 목업 데이터 반환
        if (rooms.isEmpty()) {
            log.warn("사용자가 속한 방이 없음, 기본 목업 데이터 반환");
            return new RoomListResponse(List.of(
                    RoomResponse.builder()
                            .roomId(0L)
                            .roomName("Mock Room 1")
                            .members(List.of(
                                    new SimpleRoomMemberResponse(0L, "mock_profile1.jpg"),
                                    new SimpleRoomMemberResponse(1L, "mock_profile2.jpg")
                            ))
                            .build(),
                    RoomResponse.builder()
                            .roomId(1L)
                            .roomName("Mock Room 2")
                            .members(List.of(
                                    new SimpleRoomMemberResponse(2L, "mock_profile3.jpg")
                            ))
                            .build()
            ));
        }

        // Room을 RoomResponse로 변환하여 리스트 생성
        List<RoomResponse> roomResponses = rooms.stream()
                .map(room -> RoomResponse.builder()
                        .roomId(room.getId())
                        .roomName(room.getName())
                        .members(room.getRoomMembers().stream()
                                .map(member -> new SimpleRoomMemberResponse(
                                        member.getUser().getId(),
                                        member.getUser().getId().equals(userId) ? null : member.getUser().getProfileImage() // 본인 프로필만 null 처리
                                ))
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        // RoomListResponse 형태로 반환
        return new RoomListResponse(roomResponses);
    }



}
