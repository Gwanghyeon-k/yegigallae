package com.umc.yeogi_gal_lae.api.room.service;

import com.umc.yeogi_gal_lae.api.room.converter.RoomMemberConverter;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.dto.request.AddRoomMemberRequest;
import com.umc.yeogi_gal_lae.api.room.dto.response.RoomMemberResponse;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomMemberService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;

    /**
     * 방 멤버 추가: 방에 사람이 없을 때(최초 방생성 시) + 추후 인원 추가
     *
     * @param request 멤버 추가 요청 정보
     */
    @Transactional
    public void addRoomMembers(AddRoomMemberRequest request, String userEmail ) {
        // 방 존재 확인: 인원 추가를 한다면 있는 방 중에 선택을 해서 요청할 것같아서 주석
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new BusinessException.RoomNotFoundException("해당 방이 존재하지 않습니다."));

        // 요청된 사용자 ID를 통해 유저 확인
        List<User> users = userRepository.findAllById(request.getUserIds());

//        // 요청된 사용자 중 존재하지 않는 사용자 ID 필터링: 존재하지 않는 사용자 -> friendship으로 이동
//        Set<Long> foundUserIds = users.stream()
//                .map(User::getId)
//                .collect(Collectors.toSet());
//
//        List<Long> notFoundUserIds = request.getUserIds().stream()
//                .filter(userId -> !foundUserIds.contains(userId))
//                .toList();
//
//        if (!notFoundUserIds.isEmpty()) {
//            throw new BusinessException.UserNotFoundException("다음 사용자 ID가 존재하지 않습니다: " + notFoundUserIds);
//        }

        // 이미 추가된 멤버 필터링 : 이미 필터링 된 유저 정보를 기반으로 선택하기 때문에 주석
//        List<Long> existingUserIds = roomMemberRepository.findAllByIdRoomId(room.getId()).stream()
//                .map(roomMember -> roomMember.getUser().getId())
//                .collect(Collectors.toList());
        // 새로 추가할 맴버만 필터링
        List<RoomMember> newRoomMembers = users.stream()
//                .filter(user -> !existingUserIds.contains(user.getId()))
                .map(user -> RoomMemberConverter.fromRequest(room, user))
                .toList();

//        if (newRoomMembers.isEmpty()) {
//            log.warn("모든 사용자는 이미 방에 추가되어 있습니다. roomId: {}", room.getId());
//            return;
//        }

        // 새 멤버 일괄 저장
        roomMemberRepository.saveAll(newRoomMembers);
        log.info("새로운 멤버들이 추가되었습니다. roomId: {}, newMembers: {}", room.getId(), newRoomMembers);
    }

    /**
     * 특정 방의 멤버 조회
     *
     * @param roomId 방 ID
     * @return 방 멤버 목록
     */
    public List<RoomMemberResponse> getRoomMembers(Long roomId) {
        // 방 존재 확인: 방 조회 api먼저 호출하기 때문에 필요 없을 것 같음.
//        if (!roomRepository.existsById(roomId)) {
//            throw new EntityNotFoundException("방을 찾을 수 없습니다: " + roomId);
//        }

        // 방에 속한 멤버 목록 조회
        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoomId(roomId);

        // RoomMember -> RoomMemberResponse 변환
        return roomMembers.stream()
                .map(RoomMemberConverter::toResponse)
                .toList();
    }

    /**
     * 특정 사용자가 속한 방 목록 조회
     *
     * @param userId 사용자 ID
     * @return 사용자가 속한 방 목록
     */
    public List<RoomMemberResponse> getRoomsByUserId(Long userId) {
        // 사용자 존재 확인: 있는 사용자 중에
//        if (!userRepository.existsById(userId)) {
//            throw new BusinessException.UserNotFoundException("사용자를 찾을 수 없습니다: " + userId);
//        }

        // 사용자가 속한 방 목록 조회
        List<RoomMember> roomMembers = roomMemberRepository.findAllByUserId(userId);

        // RoomMember -> RoomMemberResponse 변환
        return roomMembers.stream()
                .map(RoomMemberConverter::toResponse)
                .toList();
    }
}