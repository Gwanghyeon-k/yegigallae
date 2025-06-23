package com.umc.yeogi_gal_lae.global.oauth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean isLocal) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(!isLocal);  // 로컬에서는 false, 배포에서는 true
        cookie.setMaxAge(maxAge);

        // 배포 환경에서는 크로스사이트 요청 가능하도록 설정
        if (!isLocal) {
            cookie.setAttribute("SameSite", "None");
        } else {
            cookie.setAttribute("SameSite", "Lax"); // 로컬에서는 기본값 설정
        }

        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response, String name, boolean isLocal) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setSecure(!isLocal);

        if (!isLocal) {
            cookie.setAttribute("SameSite", "None");
        } else {
            cookie.setAttribute("SameSite", "Lax");
        }

        response.addCookie(cookie);
    }


    public static String getCookieValue(HttpServletRequest request, String name) {
        return Optional.ofNullable(request.getCookies())
            .flatMap(cookies -> Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst())
            .orElse(null);
    }
}

