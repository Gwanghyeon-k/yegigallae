package com.umc.yeogi_gal_lae.api.vote.service;

import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.types.Status;
import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteType;
import com.umc.yeogi_gal_lae.api.vote.dto.request.VoteRoomRequest;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRoomRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;


import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class ValidVoteResultService {

    private final VoteRepository voteRepository;
    private final TripPlanRepository tripPlanRepository;
    private final RoomRepository roomRepository;
    private final VoteRoomRepository voteRoomRepository;
    private final NotificationService notificationService;

    @Transactional
    public boolean validResult(VoteRoomRequest voteRoomRequest) {

        // 투표 완료 여부 확인
        if (!checkVoteCompleted(voteRoomRequest)) {  throw new BusinessException(ErrorCode.VOTE_NOT_COMPLETED_YET);}

        VoteRoom voteRoom = findVoteRoomById(voteRoomRequest.getVoteRoomId());
        TripPlan tripPlan = voteRoom.getTripPlan();
        VoteCounts voteCounts = countVotes(tripPlan.getId());

        if (voteCounts.goodVotes > voteCounts.badVotes) {
            tripPlan.setStatus(Status.COMPLETED);
            tripPlanRepository.save(tripPlan);
        } else {
            voteRoomRepository.delete(voteRoom);
        }

        Room room = findRoomById(voteRoomRequest.getRoomId());
        notificationService.createEndNotification(
                room.getName(), tripPlan.getUser().getEmail(), NotificationType.VOTE_COMPLETE, tripPlan.getId(), tripPlan.getTripPlanType()
        );

        return voteCounts.goodVotes <= voteCounts.badVotes;
    }

    @Transactional(readOnly = true)
    public boolean checkVoteCompleted(VoteRoomRequest voteRoomRequest) {
        try {
            VoteRoom voteRoom = findVoteRoomById(voteRoomRequest.getVoteRoomId());
            TripPlan tripPlan = findTripPlanById(voteRoomRequest.getTripId());
            VoteCounts voteCounts = countVotes(tripPlan.getId());

            // 조건 1. 모든 멤버가 투표했는지 확인
            boolean allMembersVoted = isAllMembersVoted(voteRoomRequest.getRoomId(), voteCounts.totalVotes);

            // 조건 2. 투표 제한 시간 초과 확인
            boolean isTimeExpired = isVoteTimeExpired(voteRoom, tripPlan);

            return (isTimeExpired || allMembersVoted) && (voteCounts.goodVotes != voteCounts.badVotes);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private VoteCounts countVotes(Long tripPlanId) {
        List<Vote> votes = voteRepository.findAllVotesByTripPlanId(tripPlanId);
        long goodVotes = votes.stream().filter(v -> v.getType() == VoteType.GOOD).count();
        long badVotes = votes.stream().filter(v -> v.getType() == VoteType.BAD).count();

        return new VoteCounts(goodVotes, badVotes, votes.size());
    }

    private TripPlan findTripPlanById(Long tripId) {
        return tripPlanRepository.findById(tripId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRIP_PLAN_NOT_FOUND));
    }

    private Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
    }

    private VoteRoom findVoteRoomById(Long voteRoomId) {
        return voteRoomRepository.findById(voteRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VOTE_ROOM_NOT_FOUND));
    }

    private boolean isAllMembersVoted(Long roomId, long totalVotes) {
        Room room = findRoomById(roomId);
        return room.getRoomMembers().size() == totalVotes;
    }

    private boolean isVoteTimeExpired(VoteRoom voteRoom, TripPlan tripPlan) {
        // 현재 시간이 투표 만료 시간보다 이후인지 확인 (투표방 생성 시간 = 투표 시작 시간)
        return LocalDateTime.now().isAfter(
                voteRoom.getCreatedAt().plusSeconds(tripPlan.getVoteLimitTime().getSeconds())
        );
    }

    private static class VoteCounts {
        final long goodVotes;
        final long badVotes;
        final long totalVotes;

        public VoteCounts(long goodVotes, long badVotes, long totalVotes) {
            this.goodVotes = goodVotes;
            this.badVotes = badVotes;
            this.totalVotes = totalVotes;
        }
    }
}
