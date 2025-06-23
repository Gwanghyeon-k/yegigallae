package com.umc.yeogi_gal_lae.api.vote.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Builder  // 추가
@AllArgsConstructor  // 필요 시 추가
public class VoteRoomRequest {

    @NotNull
    private Long tripId;

    @NotNull
    private Long roomId;

    @NotNull
    private Long voteRoomId;
}