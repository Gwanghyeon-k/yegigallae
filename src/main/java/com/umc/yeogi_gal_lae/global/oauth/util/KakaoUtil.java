package com.umc.yeogi_gal_lae.global.oauth.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.yeogi_gal_lae.global.error.AuthHandler;
import com.umc.yeogi_gal_lae.global.error.ErrorStatus;
import com.umc.yeogi_gal_lae.global.oauth.dto.KakaoDTO;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class KakaoUtil {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String client;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    private final ObjectMapper objectMapper;
    private final Set<String> allowedRedirectUris = Set.of(
        "http://localhost:5173/login/kakao",
        "https://yeogi.my/login/kakao"
    );


    public KakaoUtil() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 알 수 없는 필드 무시
    }

    public KakaoDTO.OAuthToken requestToken(String accessCode, String redirectUri) {
        if (!allowedRedirectUris.contains(redirectUri)) {
            log.error("🚨 [ERROR] 허용되지 않은 redirect_uri 요청: {}", redirectUri);
            throw new AuthHandler(ErrorStatus.KAKAO_INVALID_GRANT);
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client);
        params.add("redirect_uri", redirectUri);
        params.add("client_secret", clientSecret);
        params.add("code", accessCode);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        log.info("🔹 [DEBUG] 카카오 토큰 요청 시작");
        log.info("🔹 [DEBUG] 요청한 redirect_uri: {}", redirectUri);
        log.info("🔹 [DEBUG] 요청한 client_id: {}", client);
        log.info("🔹 [DEBUG] 요청한 accessCode: {}", accessCode);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            log.info("🔹 카카오 API 응답: {}", response.getBody());

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new AuthHandler(ErrorStatus.KAKAO_AUTH_FAILED);
            }

            return objectMapper.readValue(response.getBody(), KakaoDTO.OAuthToken.class);

        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("🚨 유효하지 않은 카카오 인증 코드 (401 Unauthorized)");
            throw new AuthHandler(ErrorStatus.KAKAO_INVALID_GRANT);
        } catch (JsonProcessingException e) {
            log.error("🚨 카카오 응답 JSON 파싱 오류: {}", e.getMessage());
            throw new AuthHandler(ErrorStatus.KAKAO_JSON_PARSE_ERROR);
        } catch (Exception e) {
            log.error("🚨 카카오 API 호출 중 오류 발생: {}", e.getMessage());
            throw new AuthHandler(ErrorStatus.KAKAO_API_ERROR);
        }
    }

    public KakaoDTO.KakaoProfile requestProfile(KakaoDTO.OAuthToken oAuthToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + oAuthToken.getAccess_token());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                requestEntity,
                String.class
            );

            log.info("🔹 카카오 프로필 응답: {}", response.getBody());

            return objectMapper.readValue(response.getBody(), KakaoDTO.KakaoProfile.class);

        } catch (JsonProcessingException e) {
            log.error("🚨 카카오 프로필 파싱 오류: {}", e.getMessage());
            throw new AuthHandler(ErrorStatus.KAKAO_JSON_PARSE_ERROR);
        } catch (Exception e) {
            log.error("🚨 카카오 프로필 요청 중 오류 발생: {}", e.getMessage());
            throw new AuthHandler(ErrorStatus.KAKAO_API_ERROR);
        }
    }
}