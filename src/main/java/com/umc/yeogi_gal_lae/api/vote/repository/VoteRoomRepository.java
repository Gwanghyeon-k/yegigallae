package com.umc.yeogi_gal_lae.api.vote.repository;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoteRoomRepository extends JpaRepository<VoteRoom,Long> {

    Optional<VoteRoom> findVoteRoomByTripPlanId(Long tripId);

//    Optional<VoteRoom> findByTripPlanId(Long id);

//    Optional<VoteRoom> findByTripPlanId(Long tripPlanId);

    @Query("SELECT vr FROM VoteRoom vr WHERE vr.tripPlan.id = :tripPlanId")
    Optional<VoteRoom> findByTripPlanId(@Param("tripPlanId") Long tripPlanId);

    @Modifying
    @Query("DELETE FROM VoteRoom vr WHERE vr.tripPlan.user = :user")
    void deleteByTripPlanUser(@Param("user") User user);
}
