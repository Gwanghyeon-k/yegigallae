package com.umc.yeogi_gal_lae.api.vote.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;


@NoArgsConstructor
public class VoteRequest {


    @Getter
    @Setter
    public static class createVoteReq{

        @JsonIgnore      // 클라이언트에서 받지 않음.
        @Schema(hidden = true)
        private String userEmail;

        @NotNull(message = "투표에 해당하는 여행 계획 Id")
        private Long tripId;

        @NotNull(message = "투표 타입")
        private  String type;

        @NotNull(message = "현재 투표 중인 방의 Id")
        private Long voteRoomId;
    }
}
