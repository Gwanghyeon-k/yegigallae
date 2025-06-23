package com.umc.yeogi_gal_lae.api.tripPlan.domain;

import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.tripPlan.types.Status;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripType;
import com.umc.yeogi_gal_lae.api.tripPlan.types.VoteLimitTime;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import com.umc.yeogi_gal_lae.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trip_plans")
public class TripPlan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String description;

    @Column
    private String price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripType tripType; // ENUM: 국내/해외

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripPlanType tripPlanType; // COURSE, SCHEDULE (여행 계획 생성, 여행 코스 생성)

    @Enumerated(EnumType.STRING)
    @Column
    private VoteLimitTime voteLimitTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.PLANNED;

    @Column(nullable = false, length = 50)
    private String location;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(name = "group_name")
    private String roomName;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 여행 계획 작성자

    // 방과의 연관 관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false) // 방 ID를 외래 키로 설정
    private Room room; // 여행 계획이 속한 방

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = true)
    @JoinColumn(name = "vote_room_id", nullable = true)
    private VoteRoom voteRoom;

    @OneToMany(mappedBy = "tripPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Place> places = new ArrayList<>();
    
    // 자동 동기
    public void setVoteRoom(VoteRoom voteRoom) {
        if (this.voteRoom != voteRoom) { // 현재 상태를 확인
            this.voteRoom = voteRoom;
            if (voteRoom != null) {
                voteRoom.setTripPlan(this); // 순환 호출 방지
            }
        }
    }


}
