package com.umc.yeogi_gal_lae.api.vote.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter  @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="vote")
@Entity
public class Vote extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_plan_id", nullable = false)
    @JsonIgnore
    private TripPlan tripPlan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vote_room_id", nullable = false)
    @JsonIgnore
    private VoteRoom voteRoom;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Override
    public LocalDateTime getCreatedAt() { return super.getCreatedAt(); }
}
