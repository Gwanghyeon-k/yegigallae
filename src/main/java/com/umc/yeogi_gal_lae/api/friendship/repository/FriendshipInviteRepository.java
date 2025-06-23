package com.umc.yeogi_gal_lae.api.friendship.repository;

import com.umc.yeogi_gal_lae.api.friendship.domain.FriendshipInvite;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface FriendshipInviteRepository extends JpaRepository<FriendshipInvite, Long> {
    Optional<FriendshipInvite> findByToken(String token);
    void deleteByInviterOrInvitee(User inviter, User invitee);
}
