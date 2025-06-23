package com.umc.yeogi_gal_lae.api.user.service;

import com.umc.yeogi_gal_lae.api.friendship.repository.FriendshipInviteRepository;
import com.umc.yeogi_gal_lae.api.friendship.repository.FriendshipRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.user.converter.AuthConverter;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.dto.response.UserResponseDTO;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRoomRepository;
import com.umc.yeogi_gal_lae.global.common.response.BaseResponse;
import com.umc.yeogi_gal_lae.global.error.AuthHandler;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.error.ErrorStatus;
import com.umc.yeogi_gal_lae.global.jwt.JwtUtil;
import com.umc.yeogi_gal_lae.global.oauth.dto.KakaoDTO;
import com.umc.yeogi_gal_lae.global.oauth.util.CookieUtil;
import com.umc.yeogi_gal_lae.global.oauth.util.KakaoUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipInviteRepository friendshipInviteRepository;
    private final TripPlanRepository tripPlanRepository;
    private final VoteRoomRepository voteRoomRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public User oAuthLogin(String accessCode, String redirectUri, HttpServletResponse httpServletResponse, boolean isLocal) {
        try {
            KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode, redirectUri);
            KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);

            String email = kakaoProfile.getKakao_account().getEmail();
            String nickname = kakaoProfile.getKakao_account().getProfile().getNickname();
            String profileImage = kakaoProfile.getKakao_account().getProfile().getProfile_image_url();

            User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, nickname, profileImage));

            // JWT 토큰 생성
            String accessToken = jwtUtil.createAccessToken(user.getEmail());
            String refreshToken = jwtUtil.createRefreshToken(user.getEmail());

            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            user.setProfileImage(profileImage);
            userRepository.save(user);

            // 쿠키 저장
            CookieUtil.addCookie(httpServletResponse, "accessToken", accessToken, (int) jwtUtil.getAccessTokenValidity(), isLocal);
            CookieUtil.addCookie(httpServletResponse, "refreshToken", refreshToken, (int) jwtUtil.getRefreshTokenValidity(), isLocal);

            httpServletResponse.setHeader("Authorization", accessToken);

            return user;

        } catch (AuthHandler e) {
            throw e;
        } catch (Exception e) {
            throw new AuthHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User createNewUser(String email, String nickname, String profileImage) {
        try {
            return userRepository.save(AuthConverter.toUser(email, nickname, profileImage));
        } catch (Exception e) {
            throw new AuthHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public BaseResponse<String> deleteUser(HttpServletResponse response, boolean isLocal) {
        String email = AuthenticatedUserUtils.getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // User 엔티티에서 vote 참조를 제거 (vote_id = null)
        userRepository.detachVoteFromUser(user);

        // TripPlan의 VoteRoom 참조 해제
        tripPlanRepository.detachVoteRoomByUser(user);

        // Vote 삭제 (VoteRoom -> Vote 순으로)
        voteRepository.deleteByVoteRoomUser(user);
        voteRoomRepository.deleteByTripPlanUser(user);

        // TripPlan 삭제
        tripPlanRepository.deleteByUser(user);

        // 방 멤버 및 방 삭제
        roomMemberRepository.deleteByUser(user);
        roomRepository.deleteByMaster(user);

        // 친구 관련 데이터 삭제
        friendshipInviteRepository.deleteByInviterOrInvitee(user, user);
        friendshipRepository.deleteByInviterOrInvitee(user, user);

        // 최종적으로 User 삭제
        userRepository.delete(user);

        // 최종적으로 User 삭제
        userRepository.delete(user);

        // JWT 쿠키 삭제
        CookieUtil.deleteCookie(response, "accessToken", isLocal);
        CookieUtil.deleteCookie(response, "refreshToken", isLocal);

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        return BaseResponse.onSuccess("회원 탈퇴 성공");
    }


    @Transactional(readOnly = true)
    public UserResponseDTO.JoinInfoResultDTO getUserInfo(String email) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.warn("사용자를 찾을 수 없음, 기본 목업 데이터 반환");
                    return createMockUser();
                });

        // User 엔티티 -> DTO 변환
        return new UserResponseDTO.JoinInfoResultDTO(user.getId(), user.getEmail(), user.getUsername(), user.getProfileImage());
    }

    /**
     * 기본 목업 사용자 데이터 생성
     */
    private User createMockUser() {
        return User.builder()
                .id(0L)
                .email("mockuser@example.com")
                .username("Mock User")
                .profileImage("mock_profile.jpg")
                .build();
    }

}