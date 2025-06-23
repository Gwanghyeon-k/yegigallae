package com.umc.yeogi_gal_lae.global.jwt;

import com.umc.yeogi_gal_lae.global.oauth.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // 예외 처리할 API 리스트 (스웨거 및 로그인 관련 요청 제외)
    private static final List<String> EXCLUDED_URLS = List.of(
        "/api/auth/login/kakao", "/swagger-ui", "/v3/api-docs", "/swagger-resources"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 요청이면 필터를 통과시킴
        if (isExcluded(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // JWT 토큰 확인
        String token = resolveToken(request);

        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);

            // 현재 로그인한 사용자 정보 SecurityContext에 저장
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(email);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Authorization 헤더가 없으면 자동으로 추가
            if (request.getHeader("Authorization") == null) {
                request.setAttribute("Authorization", "Bearer " + token);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcluded(String requestURI) {
        return EXCLUDED_URLS.stream().anyMatch(requestURI::startsWith);
    }

    private String resolveToken(HttpServletRequest request) {
        // 우선적으로 Authorization 헤더에서 가져옴
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // Authorization 헤더에 없으면 쿠키에서 accessToken 가져옴
        return CookieUtil.getCookieValue(request, "accessToken");
    }
}
