package com.umc.yeogi_gal_lae.api.vote.converter;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoteConverter {

    public static VoteResponse.VoteInfoDTO toResponse(User user, Room room, TripPlan tripPlan, int userCount){


        Integer customDate = tripPlan.getStartDate() != null ? tripPlan.getStartDate().getMonthValue() : null;

        String customLocation = tripPlan.getLocation() != null ? extractCity(tripPlan.getLocation()) : null;

        return VoteResponse.VoteInfoDTO.builder()
                .location(tripPlan.getLocation())
                .description(tripPlan.getDescription())
                .imageUrl(tripPlan.getImageUrl())

                .customLocation(customLocation)            // "경기도 부천시" => "부천시"
                .price(tripPlan.getPrice())
                .month(customDate)        // "2025-02-01"  -> "2월"

                .roomName(room.getName())
                .userCount(userCount)
                .userName(user.getUsername())

                .masterId(room.getMaster().getId())
                .masterName(room.getMaster().getUsername())
                .voteLimitTime(tripPlan.getVoteLimitTime())
                .startDate(tripPlan.getStartDate() != null ? tripPlan.getStartDate().toString() : null)
                .endDate(tripPlan.getEndDate() != null ? tripPlan.getEndDate().toString() : null)
                .build();
    }


    public static VoteResponse.ResultDTO convert( String type, User userVote, Map<String, Long> groupedVotes ) {

        // 현재 사용자가 특정 type('GOOD', 'BAD')에 투표했는지 확인
        Optional<User> filteredUserVote = Optional.ofNullable(userVote).filter(user -> user.getVote().getType().name().equals(type));

        return VoteResponse.ResultDTO.builder()
                .userId(filteredUserVote.map(User::getId).orElse(null))
                .userName(filteredUserVote.map(User::getUsername).orElse(null))
                .type(type)
                .count(groupedVotes.getOrDefault(type, 0L).intValue())
                .build();
    }

    private static String extractCity(String address) {
        Pattern pattern = Pattern.compile("([가-힣]+시)");
        Matcher matcher = pattern.matcher(address);

        if (matcher.find()) {  return matcher.group(1); }   // 부천시 반환

        return null; // 시 정보가 없으면 null 반환
    }
}
