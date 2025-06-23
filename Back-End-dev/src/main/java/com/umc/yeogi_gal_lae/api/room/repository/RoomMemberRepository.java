package com.umc.yeogi_gal_lae.api.room.repository;

import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMemberId;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, RoomMemberId> {
    List<RoomMember> findAllByRoomId(Long roomId);//?
    List<RoomMember> findAllByUserId(Long userId);

    @Query("SELECT COUNT(rm) FROM RoomMember rm " +
            "JOIN rm.room r " +
            "JOIN TripPlan tp ON tp.room.id = r.id " +
            "WHERE r.id = :roomId AND tp.id = :tripId")
    int countByRoomIdAndTripId(@Param("roomId") Long roomId, @Param("tripId") Long tripId);

    void deleteByUser(User user);

}