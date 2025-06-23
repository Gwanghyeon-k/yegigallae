package com.umc.yeogi_gal_lae.api.notification.repository;

import com.umc.yeogi_gal_lae.api.notification.domain.Notification;
import java.util.Optional;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 유저의 모든 알림 조회 (최신순)
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    Optional<Notification> findById(Long id);

    // 특정 유저의 알림만 가져오기
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isRead = false AND n.userEmail = :userEmail")
    long countUnreadNotificationsByUser(@Param("userEmail") String userEmail);
}