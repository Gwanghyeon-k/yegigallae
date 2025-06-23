package com.umc.yeogi_gal_lae.api.place.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceRequest {

    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private String description;
    private Long userId;
}
