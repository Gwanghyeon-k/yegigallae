package com.umc.yeogi_gal_lae.api.home.service;

import com.umc.yeogi_gal_lae.api.aiCourse.repository.AICourseRepository;
import com.umc.yeogi_gal_lae.api.home.converter.HomeConverter;
import com.umc.yeogi_gal_lae.api.home.dto.HomeResponse;
import com.umc.yeogi_gal_lae.api.home.repository.HomeRepository;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.types.Status;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import com.umc.yeogi_gal_lae.api.vote.dto.request.VoteRoomRequest;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRoomRepository;
import com.umc.yeogi_gal_lae.api.vote.service.ValidVoteResultService;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;
    private final TripPlanRepository tripPlanRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;
    private final VoteRoomRepository voteRoomRepository;
    private final ValidVoteResultService validVoteResultService;
    private final NotificationService notificationService;
    private final AICourseRepository aiCourseRepository;

    public Response<HomeResponse.OngoingVoteRoomList> getOngoingVoteRooms(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Long> userRoomIds = roomMemberRepository.findAllByUserId(user.getId())
                .stream()
                .map(roomMember -> roomMember.getRoom().getId())
                .collect(Collectors.toList());

        List<HomeResponse.OngoingVoteRoom> rooms = homeRepository.findAllOngoingAndCompletedCourseTripPlans().stream()
                .filter(tripPlan -> userRoomIds.contains(tripPlan.getRoom().getId()))
                .filter(tripPlan ->
                        (tripPlan.getTripPlanType() == TripPlanType.COURSE) ||
                                (tripPlan.getTripPlanType() == TripPlanType.SCHEDULE && !isVoteTimeExpired(tripPlan.getVoteRoom()))
                )
                .map(HomeConverter::toOngoingVoteRoom)
                .collect(Collectors.toList());

        return Response.of(SuccessCode.ONGOING_VOTE_ROOMS_FETCH_OK, new HomeResponse.OngoingVoteRoomList(rooms.size(), rooms));
    }


    // 완료된 투표방과 연관된 종료 날짜가 현재 또는 미래인 여행 계획 조회
    public Response<HomeResponse.CompletedVoteRoomList> getFutureVoteBasedTrips(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Long> userRoomIds = roomMemberRepository.findAllByUserId(user.getId())
                .stream()
                .map(roomMember -> roomMember.getRoom().getId())
                .collect(Collectors.toList());

        List<TripPlan> allOngoingTripPlans = homeRepository.findAllOngoingTripPlans();

        allOngoingTripPlans.forEach(tripPlan -> {
            try {
                if (tripPlan.getTripPlanType() == TripPlanType.COURSE && isCourseTimeExpired(tripPlan)) {
                    tripPlan.setStatus(Status.COMPLETED);
                    tripPlanRepository.save(tripPlan);
                } else if (tripPlan.getTripPlanType() == TripPlanType.SCHEDULE) {
                    VoteRoom voteRoom = tripPlan.getVoteRoom();
                    if (voteRoom != null) {
                        VoteRoomRequest voteRoomRequest = VoteRoomRequest.builder()
                                .tripId(tripPlan.getId())
                                .roomId(tripPlan.getRoom().getId())
                                .voteRoomId(voteRoom.getId())
                                .build();

                        boolean isVoteFailed = validVoteResultService.validResult(voteRoomRequest);

                        if (isVoteFailed) {
                            voteRoomRepository.delete(voteRoom);
                        }
                    }
                }
                } catch(BusinessException e){
                    // VOTE_NOT_COMPLETED_YET 예외 발생 시, 해당 여행을 건너뛰고 나머지 여행들은 계속 조회
                    if (!e.getErrorCode().equals(ErrorCode.VOTE_NOT_COMPLETED_YET)) {
                        throw e;
                    }
                }
        });

        List<HomeResponse.CompletedVoteRoom> rooms = homeRepository.findByStatus(Status.COMPLETED).stream()
                .filter(tripPlan -> userRoomIds.contains(tripPlan.getRoom().getId()))
                .filter(tripPlan -> !tripPlan.getEndDate().isBefore(LocalDate.now()))
                .map(tripPlan -> {
                    Long aiCourseId = aiCourseRepository.findLatestByTripPlanId(tripPlan.getId())
                            .map(aiCourse -> aiCourse.getId())
                            .orElse(null);
                    return HomeConverter.toCompletedVoteRoom(tripPlan, aiCourseId);
                })
                .collect(Collectors.toList());

        return Response.of(SuccessCode.COMPLETED_VOTE_ROOMS_FETCH_OK, new HomeResponse.CompletedVoteRoomList(rooms.size(), rooms));
    }

    public Response<HomeResponse.CompletedTripPlanList> getCompletedTripPlans(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Long> userRoomIds = roomMemberRepository.findAllByUserId(user.getId())
                .stream()
                .map(roomMember -> roomMember.getRoom().getId())
                .collect(Collectors.toList());

        List<HomeResponse.CompletedTripPlan> trips = homeRepository.findByStatus(Status.COMPLETED).stream()
                .filter(tripPlan -> userRoomIds.contains(tripPlan.getRoom().getId()))
                .filter(tripPlan -> tripPlan.getEndDate().isBefore(LocalDate.now()))
                .map(HomeConverter::toCompletedTripPlan)
                .collect(Collectors.toList());
        return Response.of(SuccessCode.COMPLETED_TRIP_PLANS_FETCH_OK, new HomeResponse.CompletedTripPlanList(trips.size(), trips));
    }

    /**
     * 제한 시간이 초과되었는지 확인하는 메서드
     */
    private boolean isVoteTimeExpired(VoteRoom voteRoom) {
        if (voteRoom == null || voteRoom.getTripPlan() == null) {
            return false;
        }

        TripPlan tripPlan = voteRoom.getTripPlan();
        LocalDateTime voteEndTime = voteRoom.getCreatedAt().plusSeconds(tripPlan.getVoteLimitTime().getSeconds());

        return LocalDateTime.now().isAfter(voteEndTime);
    }

    private boolean isCourseTimeExpired(TripPlan tripPlan) {
        if (tripPlan.getTripPlanType() != TripPlanType.COURSE || tripPlan.getVoteLimitTime() == null) {
            return false;
        }

        return LocalDateTime.now().isAfter(
                tripPlan.getCreatedAt().plusSeconds(tripPlan.getVoteLimitTime().getSeconds())
        );
    }

    /**
     * 특정 사용자의 읽지 않은 알림 여부 반환 (이메일 기반)
     */
    public Response<HomeResponse.NotificationStatus> getNotificationStatus(String userEmail) {
        boolean hasUnreadNotifications = notificationService.hasUnreadNotifications(userEmail);
        return Response.of(SuccessCode.NOTIFICATION_FETCH_OK, new HomeResponse.NotificationStatus(hasUnreadNotifications));
    }
}
