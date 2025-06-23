package com.umc.yeogi_gal_lae.api.vote.dto;

import com.umc.yeogi_gal_lae.api.tripPlan.types.VoteLimitTime;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class VoteResponse {

    @Builder
    @Getter
    public static class ResultDTO{
        private Long userId;      // 현재 접속 중인 유저가 투표한 type 에만 Id와 Name 포함하여 반환
        private String userName;
        private String type;      // GOOD or BAD
        private Integer count;
    }

    @Builder
    @Getter
    public static class VoteInfoDTO{

        private String location;         // 여행 장소
        private String description;
        private String imageUrl;

        private String customLocation;   // 서울시, 부천시, ..
        private String price;
        private Integer minDays;      // 최소 숙박일 ex) 3박
        private Integer maxDays;      // 최대 숙박일 ex) 5박
        private Integer month;        // 12월 ~ 3월

        private String roomName;
        private Integer userCount;  // 투표방 인원
        private String userName;    // 현재 사용자의 이름

        private Long masterId;
        private String masterName;
        private VoteLimitTime voteLimitTime;
        private String startDate;     // "YYYY-MM-DD"
        private String endDate;
    }
}
