package com.umc.yeogi_gal_lae.api.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDTO {
    private Long id;
    private String placeName;
    private String address;
    // 필요한 다른 필드들...
}
