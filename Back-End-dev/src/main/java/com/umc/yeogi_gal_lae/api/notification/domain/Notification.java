package com.umc.yeogi_gal_lae.api.notification.domain;

import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 특정 유저의 알림
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String roomName; // 방 이름
    private String userName; // 사용자 이름 (시작 알림에만 필요)
    private String userEmail; // 사용자 이메일 추가 (중복 문제 해결)
    private Long tripPlanId;
    private TripPlanType tripPlanType;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private boolean isRead = false;

    private String content; // 알림 내용

    // 특정 알림을 읽음 처리
    public void markAsRead() {
        this.isRead = true;
    }
}