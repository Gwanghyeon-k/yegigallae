package com.umc.yeogi_gal_lae.api.user.repository;

import com.umc.yeogi_gal_lae.api.user.domain.User;

import java.util.List;
import java.util.Optional;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // 투표 이력에 상관 없이 현재 여행 계획에 해당하는 user 가져오기
    @Query("SELECT u FROM User u WHERE u.vote IS NULL or u.vote.tripPlan.id = :tripId")
    List<User> findUsersByVoteTripPlanId(@Param("tripId") Long tripId);

    Optional<User> findByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.vote = null WHERE u = :user")
    void detachVoteFromUser(@Param("user") User user);
}
