package com.umc.yeogi_gal_lae.api.vote.domain;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="vote_room")
public class VoteRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_plan_id", nullable = false)
    private TripPlan tripPlan;         // 여행 계획 하나 당 투표방 하나


    // 자동 동기화
    public void setTripPlan(TripPlan tripPlan) {
        if (this.tripPlan != tripPlan) {     // 현재 상태를 확인
            this.tripPlan = tripPlan;
            if (tripPlan != null) {
                tripPlan.setVoteRoom(this);    // 순환 호출 방지
            }
        }
    }

}
