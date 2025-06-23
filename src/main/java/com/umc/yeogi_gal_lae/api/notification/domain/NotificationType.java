package com.umc.yeogi_gal_lae.api.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    VOTE_START("투표 시작", "투표"),
    VOTE_COMPLETE("투표 종료", "투표결과를"),
    COURSE_START("코스짜기 시작", "코스짜기"),
    COURSE_COMPLETE("코스짜기 완료", "생성된 코스를"),
    BUDGET_START("예산정하기 시작", "예산 정하기"),
    BUDGET_COMPLETE("예산정하기 완료", "예산을");

    private final String title;  // 알림 제목
    private final String description;  // 한글 변환 (캡션에서 사용)

    /**
     * 한글 변환 Getter (기존 typeToKorean() 대체)
     */
    public String getKorean() {
        return description;
    }
}