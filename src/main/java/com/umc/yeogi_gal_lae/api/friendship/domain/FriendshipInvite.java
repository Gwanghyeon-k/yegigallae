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
public class FriendshipInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 초대한 User 객체 직접 참조
    @JoinColumn(name = "inviter_id", referencedColumnName = "user_id")
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY) // 초대받을 User 객체 참조 가능 (Optional)
    @JoinColumn(name = "invitee_id", referencedColumnName = "user_id", nullable = true)
    private User invitee;

    private String token; // 초대 토큰
}
