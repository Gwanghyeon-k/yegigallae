package com.umc.yeogi_gal_lae.api.tripPlan.dto;


import com.umc.yeogi_gal_lae.api.place.dto.PlaceDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripPlanDTO {
    private Long id;
    private String location;
    // 필요한 다른 필드들...
    private List<PlaceDTO> places;  // Place 엔티티의 필요한 데이터만 담은 DTO 리스트
}
