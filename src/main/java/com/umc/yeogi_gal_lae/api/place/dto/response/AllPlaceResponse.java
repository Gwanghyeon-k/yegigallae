package com.umc.yeogi_gal_lae.api.place.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class AllPlaceResponse {
    private final List<PlaceResponse> places;
}
