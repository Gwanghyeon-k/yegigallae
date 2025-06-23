package com.umc.yeogi_gal_lae.api.vote.repository;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface VoteRepository extends JpaRepository<Vote,Long> {

    @Query("SELECT v FROM Vote v WHERE v.tripPlan.id = :tripPlanId")
    Optional<Vote> findByTripPlanId(@Param("tripPlanId") Long tripPlanId);

    @Query("SELECT v FROM Vote v WHERE v.tripPlan.id = :tripPlanId")
    List<Vote> findAllVotesByTripPlanId(@Param("tripPlanId") Long tripPlanId);

    @Modifying
    @Query("DELETE FROM Vote v WHERE v.voteRoom IN (SELECT vr FROM VoteRoom vr WHERE vr.tripPlan.user = :user)")
    void deleteByVoteRoomUser(@Param("user") User user);

    @Query("SELECT v FROM Vote v WHERE v.tripPlan.id = :tripPlanId AND v.id = (SELECT u.vote.id FROM User u WHERE u.id = :userId)")
    Optional<Vote> findByUserIdAndTripPlanId(@Param("userId") Long userId, @Param("tripPlanId") Long tripPlanId);

}
