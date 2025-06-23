package com.umc.yeogi_gal_lae.api.friendship.domain;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", referencedColumnName = "user_id") // User의 user_id를 참조
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", referencedColumnName = "user_id") // User의 user_id를 참조
    private User invitee;


    private FriendshipStatus status;

}