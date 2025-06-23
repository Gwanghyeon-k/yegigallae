package com.umc.yeogi_gal_lae.api.user.controller;

import com.umc.yeogi_gal_lae.api.user.converter.UserConverter;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.dto.response.UserResponseDTO;
import com.umc.yeogi_gal_lae.api.user.dto.response.UserResponseDTO.JoinResultDTO;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.user.service.AuthService;
import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.global.common.response.BaseResponse;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.oauth.util.CookieUtil;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Operation(summary = "카카오 로그인", description = "사용자에게 직접 카카오 인가코드, redirectUri, isLocal(배포 환경인지 아닌지)를 받아와서 로그인을 처리합니다.")
    @GetMapping("/login/kakao")
    public BaseResponse<JoinResultDTO> kakaoLogin(
        @Parameter(description = "카카오에서 제공한 인가 코드", required = true) @RequestParam("code") String accessCode,
        @Parameter(description = "카카오 OAuth2 인증 후 리다이렉트할 URI (로컬 환경 -> http://localhost:5173/login/kakao / 배포 환경 -> https://yeogi.my/login/kakao)", required = true) String redirectUri,
        HttpServletResponse httpServletResponse,
        @Parameter(description = "directUri가 로컬인지 배포 환경인지에 따라서 true/false 사용") boolean isLocal) {
        User user = authService.oAuthLogin(accessCode, redirectUri, httpServletResponse, isLocal);
        return BaseResponse.onSuccess(UserConverter.toJoinResultDTO(user));
    }

    @Operation(summary = "로그아웃", description = "사용자가 현재 로그인한 계정을 기준으로 리프레시 토큰을 삭제하여 재발급 방지하고 이후 쿠키 삭제를 합니다.")
    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletResponse response, HttpServletRequest request) {
        String email = AuthenticatedUserUtils.getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Refresh Token 삭제하여 재발급 방지
        user.setRefreshToken(null);
        userRepository.save(user);

        // 환경 판별
        boolean isLocal = request.getHeader("Referer") != null && request.getHeader("Referer")
            .contains("localhost:5173");

        // 쿠키 삭제
        CookieUtil.deleteCookie(response, "accessToken", isLocal);
        CookieUtil.deleteCookie(response, "refreshToken", isLocal);

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        return BaseResponse.onSuccess("로그아웃 성공");
    }

    @Operation(summary = "회원탈퇴", description = "회원 정보를 포함한 모든 관련 데이터를 삭제한 후, 계정을 완전히 제거합니다.")
    @DeleteMapping("/delete")
    public BaseResponse<String> deleteUser(HttpServletResponse response, boolean isLocal) {
        return authService.deleteUser(response, isLocal);
    }

    @Operation(summary = "유저 정보 조회")
    @GetMapping("/user")
    public Response<UserResponseDTO.JoinInfoResultDTO> getUserInfo() {

        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        // 이메일로 사용자 조회 & DTO 변환 (서비스에서 처리)
        UserResponseDTO.JoinInfoResultDTO result = authService.getUserInfo(userEmail);

        return Response.of(SuccessCode.USER_FETCH_OK, result);
    }

}