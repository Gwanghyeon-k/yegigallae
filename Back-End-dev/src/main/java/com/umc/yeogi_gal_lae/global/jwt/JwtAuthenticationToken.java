package com.umc.yeogi_gal_lae.global.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String email;

    public JwtAuthenticationToken(String email) {
        super(null);
        this.email = email;
        setAuthenticated(true); // 인증된 상태로 설정
    }

    @Override
    public Object getCredentials() {
        return null; // 비밀번호는 필요 없음
    }

    @Override
    public Object getPrincipal() {
        return email; // 인증된 사용자의 이메일 반환
    }
}
